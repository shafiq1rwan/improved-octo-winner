package mpay.ecpos_manager.api.datasync;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.SecureHash;
import mpay.ecpos_manager.general.utility.URLTool;
import mpay.ecpos_manager.general.utility.WebComponents;

@RestController
public class RestC_synctransaction {
	
	@Autowired
	private DataSource dataSource;
	
	@Value("${CLOUD_BASE_URL}")
	private String cloudUrl;
	
	private static final int API_TIMEOUT = 300 * 1000;

	private static String SYNC_FOLDER = Property.getSYNC_FOLDER_NAME();
	
	@RequestMapping(value = "/syncTransaction", method = { RequestMethod.POST })
	public String syncTransaction(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("----------- SYNC TRANSACTION START ---------", SYNC_FOLDER);
		JSONObject result = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			JSONObject jsonData = new JSONObject(data);
			WebComponents webComponents = new WebComponents();
			String resultCode = "E01";
			String resultMessage = "Server error. Please try again later.";
			String checkBrandId = webComponents.getGeneralConfig(dataSource, "BRAND_ID");
			String checkStoreId = "";
			
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT id FROM store");
			rs = stmt.executeQuery();
			if(rs.next()) {
				checkStoreId = rs.getString("id");
			}
			stmt.close();
			rs.close();
			
			if(!(jsonData.has("brandId") && jsonData.has("storeId") && checkBrandId.equals(jsonData.getString("brandId")) && checkStoreId.equals(jsonData.getString("storeId")))) {
				resultCode = "E02";
				resultMessage = "Server error. Invalid store info.";
			}
			else {
				Date date = new Date();
				Timestamp currentDate = new Timestamp(date.getTime());
				
				Timestamp lastSyncDate = new Timestamp(0);
				
				stmt = connection.prepareStatement("select * from check_transaction_settlement_cloud_sync where response_code = '00' order by sync_date desc limit 1;");
				rs = stmt.executeQuery();
			
				if (rs.next()) {
					lastSyncDate = rs.getTimestamp("sync_date");	
				}
				
				WebComponents webComponent = new WebComponents();
				JSONObject activationInfo = webComponent.getActivationInfo(dataSource);
				Logger.writeActivity("activationInfo: " + activationInfo, SYNC_FOLDER);
				
				Map<String, Object> params = new LinkedHashMap<>();
				params.put("storeId", jsonData.getString("storeId"));
				params.put("activationId", activationInfo.getString("activationId"));
				params.put("timeStamp", date.toString());
				params.put("brandId", activationInfo.getString("brandId"));
				
				JSONObject json = new JSONObject();
				json.put("check", DataSync.getCheckData(connection, currentDate, lastSyncDate));
				json.put("checkDetail", DataSync.getCheckDetailData(connection, currentDate, lastSyncDate));
				json.put("transaction", DataSync.getTransactionData(connection, currentDate, lastSyncDate));
				json.put("settlement", DataSync.getSettlementData(connection, currentDate, lastSyncDate));
				
				params.put("data", json);
				
				params.put("authToken", SecureHash.generateSecureHash("SHA-256", activationInfo.getString("activationId").concat(activationInfo.getString("macAddress")).concat(date.toString()).concat(params.get("data").toString())));

				Logger.writeActivity("requestParams: " + params, SYNC_FOLDER);
				byte[] sendData = URLTool.BuildStringParam(params).getBytes("UTF-8");
				
				URL url = new URL(cloudUrl + "api/device/syncTransaction");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Content-Length", String.valueOf(sendData.length));
				conn.setConnectTimeout(API_TIMEOUT);
				conn.setReadTimeout(API_TIMEOUT);
	 			conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.getOutputStream().write(sendData);
				
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuffer inputBuffer = new StringBuffer();
				
				String nextLine = "";
				while ((nextLine = br.readLine()) != null) {
					inputBuffer.append(nextLine);
				}
				br.close();
				Logger.writeActivity("response: " + inputBuffer, SYNC_FOLDER);
				
				JSONObject responseData = new JSONObject(inputBuffer.toString());
				if (responseData.has("resultCode")) {
					if (responseData.getString("resultCode").equals("E02")) {
						resultCode = "E02";
						resultMessage = "Device has been deactivated.";
						Logger.writeActivity(resultCode + ": " + resultMessage, SYNC_FOLDER);
					} else if (responseData.getString("resultCode").equals("E03")) {
						resultCode = "E02";
						resultMessage = "Invalid access token. Please contact support.";
						Logger.writeActivity(resultCode + ": " + resultMessage, SYNC_FOLDER);
					} else if (responseData.getString("resultCode").equals("E04")) {
						resultCode = "E02";
						resultMessage = "Current store is not published at cloud. Please contact support.";
						Logger.writeActivity(resultCode + ": " + resultMessage, SYNC_FOLDER);
					} else if (responseData.getString("resultCode").equals("00")) {
						resultCode = "00";
						resultMessage = "Check, transaction and settlement data have been sync to cloud.";
						Logger.writeActivity(resultCode + ": " + resultMessage, SYNC_FOLDER);
					}
				} else {
					resultCode = "E03";
					resultMessage = responseData.has("resultMessage") && !responseData.getString("resultMessage").isEmpty() ? responseData.getString("resultMessage") : "Unknown error. Please try again later.";
					Logger.writeActivity(resultCode + ": " + resultMessage, SYNC_FOLDER);
				}
			}
			DataSync.insertTransactionSyncRecord(connection, resultCode, resultMessage);
			
			result.put("resultCode", resultCode);
			result.put("resultMessage", resultMessage);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (connection != null && !connection.getAutoCommit()) {
					connection.rollback();
				}
			} catch (Exception ex) {
				Logger.writeError(ex, "Exception: ", SYNC_FOLDER);
				ex.printStackTrace();
			}
		} finally {
			try {
				if (connection != null) {
					connection.setAutoCommit(true);
					connection.close();
				}
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", SYNC_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("----------- SYNC TRANSACTION END ---------", SYNC_FOLDER);
		return result.toString();
	}
}
