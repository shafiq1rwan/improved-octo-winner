package mpay.ecpos_manager.api.datasync;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.NetworkAddressTool;
import mpay.ecpos_manager.general.utility.URLTool;
import mpay.ecpos_manager.general.utility.WebComponents;
import mpay.ecpos_manager.general.utility.ZipTool;

@RestController
public class RestC_activation {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Value("${query-path}")
	private String queryPath;

	@Value("${query-file-name}")
	private String queryFileName;
	
	@Value("${menu_image_path}")
	private String imagePath;

	@Value("${image-file-name}")
	private String imageFileName;
	
	@Autowired
	private DataSource dataSource;
	
	@Value("${CLOUD_BASE_URL}")
	private String cloudUrl;
	
	private static final int API_TIMEOUT = 30 * 1000;
	
	// activation
	@RequestMapping(value = "/activation", method = { RequestMethod.POST })
	public String ecposActivation(
			@RequestParam(value = "brandId", required = true) String brandId, 
			@RequestParam(value = "activationId", required = true) String activationId, 
			@RequestParam(value = "activationKey", required = true) String activationKey, 
			HttpServletRequest request) {
		Logger.writeActivity("----------- RECEIVE ECPOS ACTIVATION REQUEST ---------", ECPOS_FOLDER);
		Logger.writeActivity("brandId : " + brandId, ECPOS_FOLDER);
		Logger.writeActivity("activationId : " + activationId, ECPOS_FOLDER);
		Logger.writeActivity("activationKey : " + activationKey, ECPOS_FOLDER);
		
		Connection connection = null;

		JSONObject result = new JSONObject();
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";
		try {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("brandId", brandId);
			params.put("activationId", activationId);
			params.put("activationKey", activationKey);
			params.put("macAddress", NetworkAddressTool.GetAddress("mac"));
			params.put("type", 1);

			Logger.writeActivity("Request: " + params.toString(), ECPOS_FOLDER);

			byte[] sendData = URLTool.BuildStringParam(params).getBytes("UTF-8");

			URL url = new URL(cloudUrl + "api/device/activation");
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

			Logger.writeActivity("Response: " + inputBuffer.toString(), ECPOS_FOLDER);

			JSONObject responseData = new JSONObject(inputBuffer.toString());

			if (responseData.has("resultCode") && responseData.getString("resultCode").equals("00")) {
				// successful
		
				// publish menu before
				if (responseData.has("queryMySqlFilePath") && responseData.has("imageFilePath")
						&& responseData.has("versionCount") && responseData.has("storeInfo") && responseData.has("staffInfo") && responseData.has("staffRole") && responseData.has("tableSetting")) {
					String queryFilePathStr = responseData.getString("queryMySqlFilePath");
					String imageFilePathStr = responseData.getString("imageFilePath");
					
					// query file
					File menuFilePath = new File(queryPath);
					if (!menuFilePath.exists()) {
						menuFilePath.mkdirs();
					}
					File queryFile = new File(queryPath, queryFileName);
					if (queryFile.exists()) {
						queryFile.delete();
					}
					
					// write to local path
					BufferedInputStream in = new BufferedInputStream(new URL(queryFilePathStr).openStream());
					FileOutputStream fileOutputStream = new FileOutputStream(queryFile);
					byte dataBuffer[] = new byte[1024];
					int bytesRead;
					while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
						fileOutputStream.write(dataBuffer, 0, bytesRead);
					}
					fileOutputStream.flush();
					fileOutputStream.close();
					in.close();
					
					// images
					File imageFilePath = new File(imagePath);
					if (!imageFilePath.exists()) {
						imageFilePath.mkdirs();
					} else {
						FileUtils.cleanDirectory(imageFilePath);
					}
					File imageFile = new File(imagePath, imageFileName);
					if (imageFile.exists()) {
						imageFile.delete();
					}
					
					// write to local path
					in = new BufferedInputStream(new URL(imageFilePathStr).openStream());
					fileOutputStream = new FileOutputStream(imageFile);
					while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
						fileOutputStream.write(dataBuffer, 0, bytesRead);
					}
					fileOutputStream.flush();
					fileOutputStream.close();
					in.close();

					ZipTool.unzipFile(imageFile.getAbsolutePath(), imageFilePath.getAbsolutePath());
					imageFile.delete();
					
					connection = dataSource.getConnection();
					connection.setAutoCommit(false);
					DataSync.resetDBStoreData(connection);
					DataSync.resetDBMenuData(connection);
					
					Statement statement = connection.createStatement();
					BufferedReader br2 = new BufferedReader(new FileReader(queryFile));
					String readLine = null;
					while ((readLine = br2.readLine()) != null) {
						statement.execute(readLine);
					}
					br2.close();
					
					// store info
					JSONObject storeInfo = responseData.getJSONObject("storeInfo");
					if(storeInfo!=null) {
						if(DataSync.insertStoreInfo(connection, storeInfo, imagePath)) {					
							JSONArray staffRole = responseData.getJSONArray("staffRole");
							JSONArray tableSetting = responseData.getJSONArray("tableSetting");
							JSONArray staffInfo = responseData.getJSONArray("staffInfo");
							
							if(staffRole.length()!=0) {
								// insert staff role
								DataSync.insertStaffRole(connection, staffRole);
							}
							
							if(tableSetting.length()!=0) {
								// insert table setting
								DataSync.insertTableSetting(connection, tableSetting);
							}
							
							if(staffInfo.length()!=0) {
								// insert staff info
								DataSync.insertStaffInfo(connection, staffInfo);
							}
							
							DataSync.updateSyncDate(connection);
							connection.commit();
						}
						else {
							connection.rollback();
						}
					}
					connection.setAutoCommit(true);	
					
					WebComponents webComponent = new WebComponents();
					webComponent.updateGeneralConfig(dataSource, "BRAND_ID", brandId);
					webComponent.updateGeneralConfig(dataSource, "ACTIVATION_ID", activationId);
					webComponent.updateGeneralConfig(dataSource, "ACTIVATION_KEY", activationKey);
					webComponent.updateGeneralConfig(dataSource, "MAC_ADDRESS", NetworkAddressTool.GetAddress("mac"));
					webComponent.updateGeneralConfig(dataSource, "VERSION_NUMBER", String.valueOf(responseData.getLong("versionCount")));
					
					resultCode = "00";
					resultMessage = "Success";
					
					// clear session
					webComponent.clearEcposSession(request);
				} else {
					resultCode = "E03";
					resultMessage = "Corrupted data. Please try again later.";
				}
				
			} else if (responseData.has("resultCode") && responseData.getString("resultCode").equals("01")) {
				if (responseData.has("versionCount") && responseData.has("storeInfo") && responseData.has("staffInfo") && responseData.has("staffRole") && responseData.has("tableSetting")){
					// successful
					connection = dataSource.getConnection();
					connection.setAutoCommit(false);
					DataSync.resetDBStoreData(connection);
					DataSync.resetDBMenuData(connection);
					
					JSONObject storeInfo = responseData.getJSONObject("storeInfo");
					if(storeInfo!=null) {
						if(DataSync.insertStoreInfo(connection, storeInfo, imagePath)) {					
							JSONArray staffRole = responseData.getJSONArray("staffRole");
							JSONArray tableSetting = responseData.getJSONArray("tableSetting");
							JSONArray staffInfo = responseData.getJSONArray("staffInfo");
							
							if(staffRole.length()!=0) {
								// insert staff role
								DataSync.insertStaffRole(connection, staffRole);
							}
							
							if(tableSetting.length()!=0) {
								// insert table setting
								DataSync.insertTableSetting(connection, tableSetting);
							}
							
							if(staffInfo.length()!=0) {
								// insert staff info
								DataSync.insertStaffInfo(connection, staffInfo);
							}
							
							DataSync.updateSyncDate(connection);
							connection.commit();
						}
						else {
							connection.rollback();
						}
					}
					connection.setAutoCommit(true);
					
					WebComponents webComponent = new WebComponents();
					webComponent.updateGeneralConfig(dataSource, "BRAND_ID", brandId);
					webComponent.updateGeneralConfig(dataSource, "ACTIVATION_ID", activationId);
					webComponent.updateGeneralConfig(dataSource, "ACTIVATION_KEY", activationKey);
					webComponent.updateGeneralConfig(dataSource, "MAC_ADDRESS", NetworkAddressTool.GetAddress("mac"));
					webComponent.updateGeneralConfig(dataSource, "VERSION_NUMBER", String.valueOf(responseData.getLong("versionCount")));			

					resultCode = "00";
					resultMessage = "Success";
					// clear session
					webComponent.clearEcposSession(request);
				} else {
					resultCode = "E03";
					resultMessage = "Corrupted data. Please try again later.";
				}
			} else {
				resultCode = "E02";
				resultMessage = responseData.has("resultMessage") && !responseData.getString("resultMessage").isEmpty()
						? responseData.getString("resultMessage")
						: "Unknown error. Please try again later.";
			}
		} catch (Exception e) {
			Logger.writeActivity("Error occurred. Refer error log.", ECPOS_FOLDER);
			Logger.writeError(e, "Error", ECPOS_FOLDER);
			try {
				if (connection != null && !connection.getAutoCommit()) {
					connection.rollback();
				}
			} catch (Exception ex) {
			}
			e.printStackTrace();
		} finally {
			Logger.writeActivity("----------- END ECPOS ACTIVATION REQUEST ---------", ECPOS_FOLDER);
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
			}

			try {
				Logger.writeActivity("resultCode: " + resultCode, ECPOS_FOLDER);
				Logger.writeActivity("resultMessage: " + resultMessage, ECPOS_FOLDER);
				result.put("resultCode", resultCode);
				result.put("resultMessage", resultMessage);
			} catch (Exception e) {
			}
		}
		return result.toString();
	}
}
