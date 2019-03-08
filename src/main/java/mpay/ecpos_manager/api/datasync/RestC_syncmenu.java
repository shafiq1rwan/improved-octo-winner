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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.SecureHash;
import mpay.ecpos_manager.general.utility.URLTool;
import mpay.ecpos_manager.general.utility.WebComponents;
import mpay.ecpos_manager.general.utility.ZipTool;

@RestController
public class RestC_syncmenu {

	private static String SYNC_FOLDER = Property.getSYNC_FOLDER_NAME();
	
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
	
	@RequestMapping(value = "/syncMenu", method = { RequestMethod.POST })
	public String syncMenu(HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("----------- SYNC MENU BEGIN ---------", SYNC_FOLDER);
		Connection connection = null;

		JSONObject result = new JSONObject();
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";

		try {
			connection = dataSource.getConnection();
			WebComponents webComponent = new WebComponents();
			String sqlStatement = "SELECT id FROM store;";
			PreparedStatement ps1 = connection.prepareStatement(sqlStatement);
			ResultSet rs1 = ps1.executeQuery();
			
			if (rs1.next()) {
				Date date = new Date();
				JSONObject activationInfo = webComponent.getActivationInfo(dataSource);
				Map<String, Object> params = new LinkedHashMap<>();
				params.put("storeId", rs1.getInt("id"));
				params.put("versionCount", activationInfo.getString("versionNumber"));
				params.put("activationId", activationInfo.getString("activationId"));
				params.put("timeStamp", date.toString());
				params.put("brandId", activationInfo.getString("brandId"));
				params.put("authToken", SecureHash.generateSecureHash("SHA-256", activationInfo.getString("activationId").concat(activationInfo.getString("macAddress")).concat(date.toString())));
				
				Logger.writeActivity("Request: " + params.toString(), SYNC_FOLDER);
				System.out.println("params:" + params.toString());
				byte[] sendData = URLTool.BuildStringParam(params).getBytes("UTF-8");

				URL url = new URL(cloudUrl + "api/device/syncMenu");
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
				
				System.out.println("Response:" + inputBuffer.toString());
				Logger.writeActivity("Response: " + inputBuffer.toString(), SYNC_FOLDER);

				JSONObject responseData = new JSONObject(inputBuffer.toString());
				if (responseData.has("resultCode") && responseData.getString("resultCode").equals("E02")) {
					resultCode = "E02";
					resultMessage = "Device has been deactivated.";
				} else if (responseData.has("resultCode") && responseData.getString("resultCode").equals("E03")) {
					resultCode = "E02";
					resultMessage = "Invalid access token. Please contact support.";
				} else if (responseData.has("resultCode") && (responseData.getString("resultCode").equals("E06"))) {
					resultCode = "E02";
					resultMessage = "Current store is not published at cloud. Please contact support.";
				} else if (responseData.has("resultCode") && responseData.getString("resultCode").equals("E04")) {
					resultCode = "E02";
					resultMessage = "There is no menu published at cloud. Please contact support.";
				} else if (responseData.has("resultCode") && responseData.getString("resultCode").equals("E05")) {
					resultCode = "00";
					resultMessage = "Version is up-to-date.";
				} else if (responseData.has("resultCode") && responseData.getString("resultCode").equals("00")) {
					// get full menu
					String queryFilePathStr = responseData.getString("queryMySqlFilePath");
					String imageFilePathStr = responseData.getString("imageFilePath");
					int currentVersion = responseData.getInt("versionCount");

					File queryFilePath = new File(queryPath);
					if (!queryFilePath.exists()) {
						queryFilePath.mkdirs();
					}

					File queryFile = new File(queryPath, queryFileName);
					if (queryFile.exists()) {
						queryFile.delete();
					}

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

					connection.setAutoCommit(false);
					DataSync.resetDBMenuData(connection);

					Statement statement = connection.createStatement();		
					try {
						BufferedReader br2 = new BufferedReader(new FileReader(queryFile));
						String readLine = null;
						while ((readLine = br2.readLine()) != null) {
							statement.execute(readLine);
						}
						br2.close();
					} catch (Exception e) {
						Logger.writeActivity("Error: SQL Exception occured.", SYNC_FOLDER);
						throw e;
					}		
					
					connection.commit();
					connection.setAutoCommit(true);

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
					
					webComponent.updateGeneralConfig(dataSource, "VERSION_NUMBER", String.valueOf(currentVersion));
					DataSync.updateSyncDate(connection);
					resultCode = "00";
					resultMessage = "Updated to latest version.";
				} else if (responseData.has("resultCode") && responseData.getString("resultCode").equals("01")) {
					// sync menu
					JSONArray versionSyncList = responseData.getJSONArray("versionSync");
					int currentVersion = 0;
					
					connection.setAutoCommit(false);
					for (int x = 0; x < versionSyncList.length(); x++) {
						JSONObject versionSyncData = versionSyncList.getJSONObject(x);
						
						String queryFilePathStr = versionSyncData.getString("menuQueryFilePath");
						String imageFilePathStr = versionSyncData.getString("menuImageFilePath");
						currentVersion = versionSyncData.getInt("versionCount");
						
						File queryFilePath = new File(queryPath);
						if (!queryFilePath.exists()) {
							queryFilePath.mkdirs();
						}

						File queryFile = new File(queryPath, queryFileName);
						if (queryFile.exists()) {
							queryFile.delete();
						}

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

						Statement statement = connection.createStatement();
						BufferedReader br2 = new BufferedReader(new FileReader(queryFile));
						String readLine = null;
						while ((readLine = br2.readLine()) != null) {
							statement.execute(readLine);
						}
						br2.close();
						
						if (imageFilePathStr != null && !imageFilePathStr.isEmpty()) {
							File imageFilePath = new File(imagePath);
							if (!imageFilePath.exists()) {
								imageFilePath.mkdirs();
							}
							File imageFile = new File(imagePath, imageFileName);
							if (imageFile.exists()) {
								imageFile.delete();
							}
	
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
						}
					}
					connection.commit();
					connection.setAutoCommit(true);
					webComponent.updateGeneralConfig(dataSource, "VERSION_NUMBER", String.valueOf(currentVersion));
					DataSync.updateSyncDate(connection);		
					resultCode = "00";
					resultMessage = "Updated to latest version.";
				} else {
					resultCode = "E03";
					resultMessage = responseData.has("resultMessage") && !responseData.getString("resultMessage").isEmpty()
							? responseData.getString("resultMessage")
							: "Unknown error. Please try again later.";
				}
			} else {
				// perform reactivation or sync store info
			}
		} catch (Exception e) {
			try {
				if (connection != null && !connection.getAutoCommit()) {
					connection.rollback();
				}
			} catch (Exception ex) {
			}
			e.printStackTrace();
			Logger.writeActivity("Error occurred. Refer error log.", SYNC_FOLDER);
			Logger.writeError(e, "Error", SYNC_FOLDER);
		} finally {
			Logger.writeActivity("----------- SYNC MENU END ---------", SYNC_FOLDER);
			
			try {
				if (connection != null) {
					connection.setAutoCommit(true);
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
		System.out.println(result);
		return result.toString();
	}
	
	@RequestMapping(value = "/syncStore", method = { RequestMethod.POST })
	public String syncStore(HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("----------- SYNC STORE BEGIN ---------", SYNC_FOLDER);
		Connection connection = null;

		JSONObject result = new JSONObject();
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";

		try {
			connection = dataSource.getConnection();
			WebComponents webComponent = new WebComponents();
			String sqlStatement = "SELECT id FROM store;";
			PreparedStatement ps1 = connection.prepareStatement(sqlStatement);
			ResultSet rs1 = ps1.executeQuery();
			
			if (rs1.next()) {
				Date date = new Date();
				JSONObject activationInfo = webComponent.getActivationInfo(dataSource);
				Map<String, Object> params = new LinkedHashMap<>();
				params.put("storeId", rs1.getInt("id"));
				params.put("activationId", activationInfo.getString("activationId"));
				params.put("timeStamp", date.toString());
				params.put("brandId", activationInfo.getString("brandId"));
				params.put("authToken", SecureHash.generateSecureHash("SHA-256", activationInfo.getString("activationId").concat(activationInfo.getString("macAddress")).concat(date.toString())));
				
				Logger.writeActivity("Request: " + params.toString(), SYNC_FOLDER);
				System.out.println("params:" + params.toString());
				byte[] sendData = URLTool.BuildStringParam(params).getBytes("UTF-8");

				URL url = new URL(cloudUrl + "api/device/syncStore");
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
				
				System.out.println("Response:" + inputBuffer.toString());
				Logger.writeActivity("Response: " + inputBuffer.toString(), SYNC_FOLDER);

				JSONObject responseData = new JSONObject(inputBuffer.toString());
				if (responseData.has("resultCode") && responseData.getString("resultCode").equals("E02")) {
					resultCode = "E02";
					resultMessage = "Device has been deactivated.";
				} else if (responseData.has("resultCode") && responseData.getString("resultCode").equals("E03")) {
					resultCode = "E02";
					resultMessage = "Invalid access token. Please contact support.";
				} else if (responseData.has("resultCode") && (responseData.getString("resultCode").equals("E04"))) {
					resultCode = "E02";
					resultMessage = "Current store is not published at cloud. Please contact support.";
				} else if (responseData.has("resultCode") && responseData.getString("resultCode").equals("00")) {
					if(responseData.has("storeInfo") && responseData.has("staffRole") && responseData.has("staffInfo")) {
						connection.setAutoCommit(false);
						DataSync.resetDBStoreData(connection);
						// store info
						JSONObject storeInfo = responseData.getJSONObject("storeInfo");
						if(storeInfo!=null) {
							if(DataSync.insertStoreInfo(connection, storeInfo, imagePath)) {					
								JSONArray staffRole = responseData.getJSONArray("staffRole");
								JSONArray staffInfo = responseData.getJSONArray("staffInfo");
								
								if(staffRole.length()!=0) {
									// insert staff role
									DataSync.insertStaffRole(connection, staffRole);
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
						DataSync.updateSyncDate(connection);
						resultCode = "00";
						resultMessage = "Updated to latest version.";
					}
				} else {
					resultCode = "E03";
					resultMessage = responseData.has("resultMessage") && !responseData.getString("resultMessage").isEmpty()
							? responseData.getString("resultMessage")
							: "Unknown error. Please try again later.";
				}
			} else {
				// perform reactivation or sync store info
			}
		} catch (Exception e) {
			try {
				if (connection != null && !connection.getAutoCommit()) {
					connection.rollback();
				}
			} catch (Exception ex) {
			}
			e.printStackTrace();
			Logger.writeActivity("Error occurred. Refer error log.", SYNC_FOLDER);
			Logger.writeError(e, "Error", SYNC_FOLDER);
		} finally {
			Logger.writeActivity("----------- SYNC MENU END ---------", SYNC_FOLDER);
			
			try {
				if (connection != null) {
					connection.setAutoCommit(true);
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
		System.out.println(result);
		return result.toString();
	}
}
