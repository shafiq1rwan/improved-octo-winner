package mpay.my.ecpos_manager_v2.datasync;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import org.springframework.web.servlet.ModelAndView;

import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;
import mpay.my.ecpos_manager_v2.webutil.NetworkAddressTool;
import mpay.my.ecpos_manager_v2.webutil.URLTool;
import mpay.my.ecpos_manager_v2.webutil.UtilWebComponents;
import mpay.my.ecpos_manager_v2.webutil.ZipTool;

@RestController
public class RestC_activation {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Value("${query-path}")
	private String queryPath;

	@Value("${query-file-name}")
	private String queryFileName;
	
	@Value("${image-path}")
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
			System.out.println(responseData);

			if (responseData.has("resultCode") && responseData.getString("resultCode").equals("00")) {
				// successful
		
				// publish menu before
				if (responseData.has("queryMySqlFilePath") && responseData.has("imageFilePath")
						&& responseData.has("versionCount") && responseData.has("storeInfo") && responseData.has("staffInfo") && responseData.has("staffRole")) {
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
					resetDBData(connection);
					
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
						if(insertStoreInfo(connection, storeInfo)) {					
							JSONArray staffRole = responseData.getJSONArray("staffRole");
							JSONArray staffInfo = responseData.getJSONArray("staffInfo");
							
							if(staffRole.length()!=0) {
								// insert staff role
								insertStaffRole(connection, staffRole);
							}
							
							if(staffInfo.length()!=0) {
								// insert staff info
								insertStaffInfo(connection, staffInfo);
							}
							
							updateSyncDate(connection);
							connection.commit();
						}
						else {
							connection.rollback();
						}
					}
					connection.setAutoCommit(true);	
					
					UtilWebComponents webComponent = new UtilWebComponents();
					webComponent.updateGeneralConfig(connection, "BRAND_ID", brandId);
					webComponent.updateGeneralConfig(connection, "ACTIVATION_ID", activationId);
					webComponent.updateGeneralConfig(connection, "ACTIVATION_KEY", activationKey);
					webComponent.updateGeneralConfig(connection, "MAC_ADDRESS", NetworkAddressTool.GetAddress("mac"));
					webComponent.updateGeneralConfig(connection, "VERSION_NUMBER", String.valueOf(responseData.getLong("versionCount")));
					
					resultCode = "00";
					resultMessage = "Success";
				} else {
					resultCode = "E03";
					resultMessage = "Corrupted data. Please try again later.";
				}
				
			} else if (responseData.has("resultCode") && responseData.getString("resultCode").equals("01")) {
				if (responseData.has("versionCount") && responseData.has("storeInfo") && responseData.has("staffInfo")) {
					// successful
					connection = dataSource.getConnection();
					resetDBData(connection);
					JSONObject storeInfo = responseData.getJSONObject("storeInfo");
					if(storeInfo!=null) {
						insertStoreInfo(connection, storeInfo);
						updateSyncDate(connection);
					}
					
					UtilWebComponents webComponent = new UtilWebComponents();
					webComponent.updateGeneralConfig(connection, "BRAND_ID", brandId);
					webComponent.updateGeneralConfig(connection, "ACTIVATION_ID", activationId);
					webComponent.updateGeneralConfig(connection, "ACTIVATION_KEY", activationKey);
					webComponent.updateGeneralConfig(connection, "MAC_ADDRESS", NetworkAddressTool.GetAddress("mac"));
					webComponent.updateGeneralConfig(connection, "VERSION_NUMBER", String.valueOf(responseData.getLong("versionCount")));			

					resultCode = "00";
					resultMessage = "Success";
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
				result.put("resultCode", resultCode);
				result.put("resultMessage", resultMessage);
			} catch (Exception e) {
			}
		}
		return result.toString();
	}
	
	public static void resetDBData(Connection connection) throws Exception {
		String sqlStatement = "DELETE FROM category;";
		PreparedStatement ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM category_menu_item;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM combo_detail;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM combo_item_detail;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM menu_item;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM menu_item_group;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM menu_item_group_sequence;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM menu_item_modifier_group;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM modifier_group;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM modifier_item_sequence;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();
		
		sqlStatement = "DELETE FROM store;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();
		
		sqlStatement = "DELETE FROM role_lookup;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();
		
		sqlStatement = "DELETE FROM staff;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();
	}
	
	public boolean insertStoreInfo(Connection connection, JSONObject storeInfo) throws Exception {
		boolean flag = false;
		PreparedStatement ps1 = null;
		try {
			// write to local path for store logo
			String logoPath = storeInfo.getString("logoPath");
			String imageName = logoPath.substring(logoPath.lastIndexOf('/') + 1);
			File imageFile = new File(imagePath, imageName);
			BufferedInputStream in = new BufferedInputStream(new URL(logoPath).openStream());
			FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
			fileOutputStream.flush();
			fileOutputStream.close();
			in.close();
			
			String sqlStatement = "INSERT INTO store (id, tax_charge_id, backend_id, store_name, store_logo_path, store_address, store_longitude, store_latitude, "
					+ "store_country, store_currency, store_table_count, store_start_operating_time, store_end_operating_time, last_update_date, is_publish, created_date) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";
			ps1 = connection.prepareStatement(sqlStatement);
			int count = 1;
			ps1.setLong(count++, storeInfo.getLong("storeId"));
			ps1.setLong(count++, storeInfo.getLong("taxChargeId"));
			ps1.setString(count++, storeInfo.getString("backEndId"));
			ps1.setString(count++, storeInfo.getString("name"));
			ps1.setString(count++, imageName);
			ps1.setString(count++, storeInfo.getString("address"));
			ps1.setString(count++, storeInfo.getString("longitude"));
			ps1.setString(count++, storeInfo.getString("latitude"));
			ps1.setString(count++, storeInfo.getString("country"));
			ps1.setString(count++, storeInfo.getString("currency"));
			ps1.setString(count++, storeInfo.getString("tableCount"));
			ps1.setString(count++, storeInfo.getString("startOperatingTime"));
			ps1.setString(count++, storeInfo.getString("endOperatingTime"));
			ps1.setString(count++, storeInfo.getString("lastUpdateDate"));
			ps1.setLong(count++, storeInfo.getLong("isPublish"));
			ps1.setString(count++, storeInfo.getString("createdDate"));
			int rowAffected = ps1.executeUpdate();
			if(rowAffected != 0) {
				flag = true;
			}
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (ps1 != null) {
				ps1.close();
			}
		}
		return flag;
	}
	
	public static boolean updateSyncDate(Connection connection) throws Exception {
		boolean flag = false;
		PreparedStatement ps1 = null;
		try {		
			String sqlStatement = "INSERT INTO store_db_sync (sync_date) VALUES (NOW()) ";
			ps1 = connection.prepareStatement(sqlStatement);
			int rowAffected = ps1.executeUpdate();
			if(rowAffected != 0) {
				flag = true;
			}
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (ps1 != null) {
				ps1.close();
			}
		}
		return flag;
	}
	
	public static boolean insertStaffRole(Connection connection, JSONArray staffRole) throws Exception {
		boolean flag = false;
		PreparedStatement ps1 = null;
		try {
			String sqlStatement = "INSERT INTO role_lookup (id, role_name) VALUES ";
			for(int a=0; a < staffRole.length(); a++) {
				if(a!=0)
					sqlStatement += ", ";
				sqlStatement += "(?, ?)";	
			}
			
			ps1 = connection.prepareStatement(sqlStatement);		
			int count = 1;
			for(int a=0; a < staffRole.length(); a++) {
				JSONObject obj = staffRole.getJSONObject(a);
				ps1.setLong(count++, obj.getLong("id"));
				ps1.setString(count++, obj.getString("roleName"));
			}
			
			int rowAffected = ps1.executeUpdate();
			if(rowAffected != 0) {
				flag = true;
			}	
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (ps1 != null) {
				ps1.close();
			}
		}
		return flag;
	}
	
	public static boolean insertStaffInfo(Connection connection, JSONArray staffInfo) throws Exception {
		boolean flag = false;
		PreparedStatement ps1 = null;
		try {
			String sqlStatement = "INSERT INTO staff (id, staff_name, staff_username, staff_password, staff_role, staff_contact_hp_number, staff_contact_email, is_active, created_date, last_update_date) VALUES ";
			for(int a=0; a < staffInfo.length(); a++) {
				if(a!=0)
					sqlStatement += ", ";
				sqlStatement += "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";	
			}
			
			ps1 = connection.prepareStatement(sqlStatement);		
			int count = 1;
			for(int a=0; a < staffInfo.length(); a++) {
				JSONObject obj = staffInfo.getJSONObject(a);
				ps1.setLong(count++, obj.getLong("id"));
				ps1.setString(count++, obj.getString("name"));
				ps1.setString(count++, obj.getString("username"));
				ps1.setString(count++, obj.getString("password"));
				ps1.setLong(count++, obj.getLong("role"));
				ps1.setString(count++, obj.getString("phoneNumber"));
				ps1.setString(count++, obj.getString("email"));
				ps1.setLong(count++, obj.getLong("isActive"));
				ps1.setString(count++, obj.getString("createdDate"));
				ps1.setString(count++, obj.getString("lastUpdateDate").equals("")?null:obj.getString("lastUpdateDate"));
			}
			
			int rowAffected = ps1.executeUpdate();
			if(rowAffected != 0) {
				flag = true;
			}	
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (ps1 != null) {
				ps1.close();
			}
		}
		return flag;
	}
}
