package mpay.my.ecpos_manager_v2.restcontroller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mpay.my.ecpos_manager_v2.constant.Constant;
import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;
import mpay.my.ecpos_manager_v2.webutil.AesEncryption;
import mpay.my.ecpos_manager_v2.webutil.QRGenerate;

@RestController
@RequestMapping("/rc/configuration")
public class RestC_configuration {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@Value("${printer_exe}")
	private String printerExe;
	
	@Value("${CLOUD_BASE_URL}")
	private String cloudUrl;
	
	@RequestMapping(value = { "/get_table_list" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String getTablelist() {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("SELECT store_table_count FROM store;");
			rs = stmt.executeQuery();

			if (rs.next()) {
				int table_count = rs.getInt("store_table_count");
				
				JSONArray tableList = new JSONArray();
				for (int i = 0; i < table_count; i++) {

					stmt = connection.prepareStatement("SELECT COUNT(*) AS count FROM `check` WHERE table_number = ? AND check_status IN (1,2)");
					stmt.setInt(1, i + 1);
					rs2 = stmt.executeQuery();
					
					if (rs2.next()) {
						String data = Integer.toString(i + 1) + "," + rs2.getString("count");
						tableList.put(data);
					}
				}
				jsonResult.put(Constant.TABLE_LIST, tableList);
				Logger.writeActivity("Table List: " + tableList.toString(), ECPOS_FOLDER);
				
				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
			} else {
				Logger.writeActivity("Table List Not Found", ECPOS_FOLDER);
				
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "NO TABLE FOUND, PLEASE TRY AGAIN");
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_terminal_list/{terminalId}" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String getTerminallist(@PathVariable("terminalId") String terminalId) {
		JSONObject jsonResult = new JSONObject();
		JSONArray JARY = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			
			if (terminalId.equals("all")) {
				stmt = connection.prepareStatement("select * from terminal;");
			} else {				
				stmt = connection.prepareStatement("select * from terminal where id = ?;");
				stmt.setString(1, terminalId);
			}
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jObject = new JSONObject();
				jObject.put("id", rs.getString("id"));
				jObject.put("name", rs.getString("name"));
				jObject.put("serialNo", rs.getString("serial_number"));
				jObject.put("wifiIP", rs.getString("wifi_IP"));
				jObject.put("wifiPort", rs.getString("wifi_Port"));
				JARY.put(jObject);
			}
			jsonResult.put("terminals", JARY);
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_printer_detail" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String getPrinterList() {
		JSONObject jsonResult = new JSONObject();

		try {
			String getPrinterListParams[] = { printerExe, "1", "2", "2", "2", "2" };
			Process executePrinter = Runtime.getRuntime().exec(getPrinterListParams);
			BufferedReader input = new BufferedReader(new InputStreamReader(executePrinter.getInputStream()));

			StringBuilder responseString = new StringBuilder();

			while (input.readLine() != null) {
				responseString.append(input.readLine());
			}

			JSONObject jsonObj = new JSONObject(responseString.toString());

			if (jsonObj.has("PortInfoList")) {
				JSONArray portInfoList = jsonObj.getJSONArray("PortInfoList");
//				JSONArray paperSizeList = jsonObj.getJSONArray("PaperSizeList");

				jsonResult.put("portInfoList", portInfoList);
//				jsonResult.put("PaperSizeList", paperSizeList);
				
				JSONObject selectedPrinter = getSelectedPrinter();
				if (selectedPrinter.length() > 0) {
					jsonResult.put("selectedPrinter", selectedPrinter.getString("portName"));
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/open_cash_drawer" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String openCashDrawer() {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
				
			stmt = connection.prepareStatement("select * from printer;");
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				String openDrawerParams[] = { printerExe, "2", rs.getString("model_name"), rs.getString("paper_size"), rs.getString("port_name"), "2" };
				Process openDrawer = Runtime.getRuntime().exec(openDrawerParams);
				BufferedReader input = new BufferedReader(new InputStreamReader(openDrawer.getInputStream()));
			
				StringBuilder responseString = new StringBuilder();

				while (input.readLine() != null) {
					responseString.append(input.readLine());
				}

				JSONObject jsonObj = new JSONObject(responseString.toString());

				if (jsonObj.has("ResponseCode") && jsonObj.has("ResponseMessage")) {
					if (jsonObj.getInt("ResponseCode") == 1) {
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Success");
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Error Occur While Open Drawer");
					}
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Request");
				}
			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "No Connected Printer Found");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/save_printer" }, method = { RequestMethod.POST }, produces = "application/json")
	public void savePrinter(@RequestBody String data) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			
			JSONObject printer = new JSONObject(data);
				
			stmt = connection.prepareStatement("select * from printer;");
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				stmt.close();
				stmt = connection.prepareStatement("update printer set model_name = ?, port_name = ?, paper_size = ?;");
				stmt.setString(1, printer.getString("modelName"));
				stmt.setString(2, printer.getString("portName"));
				stmt.setString(3, printer.getString("paperSize"));
				stmt.executeUpdate();
			} else {
				stmt.close();
				stmt = connection.prepareStatement("insert into printer (model_name, port_name, paper_size) values (?,?,?)");
				stmt.setString(1, printer.getString("modelName"));
				stmt.setString(2, printer.getString("portName"));
				stmt.setString(3, printer.getString("paperSize"));
				stmt.executeUpdate();
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value = { "/save_terminal" }, method = { RequestMethod.POST }, produces = "application/json")
	public String saveTerminal(@RequestBody String data) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			
			JSONObject terminal = new JSONObject(data);
			String action = terminal.getString("action");
			String terminalName = terminal.getString("name");
			String terminalSerialNo = terminal.getString("serialNo");
			
			String terminalWifiIP = null;
			if (terminal.has("wifiIP")) {
				terminalWifiIP = terminal.getString("wifiIP");
			}
			
			String terminalWifiPort = null;
			if (terminal.has("wifiPort")) {
				terminalWifiPort = terminal.getString("wifiPort");
			}
			
			String terminalId = null;
			if (action.equals("create")) {
				stmt = connection.prepareStatement("select * from terminal where name = ? or serial_number = ?;");
				stmt.setString(1, terminalName);
				stmt.setString(2, terminalSerialNo);
			} else if (action.equals("update")) {
				terminalId = terminal.getString("id");
				
				stmt = connection.prepareStatement("select * from terminal where (name = ? or serial_number = ?) and id != ?;");
				stmt.setString(1, terminalName);
				stmt.setString(2, terminalSerialNo);
				stmt.setString(3, terminalId);
			}
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				jsonResult.put("response_code", "01");
				jsonResult.put("response_message", "Duplicate Terminal Information");
				Logger.writeActivity("Duplicate Terminal Information", ECPOS_FOLDER);
			} else {
				stmt.close();
				
				if (action.equals("create")) {
					stmt = connection.prepareStatement("insert into terminal (name,serial_number,wifi_IP,wifi_Port) values (?,?,?,?);");
					stmt.setString(1, terminalName);
					stmt.setString(2, terminalSerialNo);
					stmt.setString(3, terminalWifiIP);
					stmt.setString(4, terminalWifiPort);
				} else if (action.equals("update")) {
					stmt = connection.prepareStatement("update terminal set name = ?,serial_number = ?,wifi_IP = ?,wifi_Port = ? where id = ?;");
					stmt.setString(1, terminalName);
					stmt.setString(2, terminalSerialNo);
					stmt.setString(3, terminalWifiIP);
					stmt.setString(4, terminalWifiPort);
					stmt.setString(5, terminalId);
				}
				int rs2 = stmt.executeUpdate();
				
				if (rs2 > 0) {
					jsonResult.put("response_code", "00");
					jsonResult.put("response_message", "Terminal Information has been saved");
					Logger.writeActivity("Terminal Information has been saved", ECPOS_FOLDER);
				} else {
					jsonResult.put("response_code", "01");
					jsonResult.put("response_message", "Terminal Information failed to save");
					Logger.writeActivity("Terminal Information failed to save", ECPOS_FOLDER);
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/remove_terminal" }, method = { RequestMethod.POST }, produces = "application/json")
	public String removeTerminal(@RequestBody String data) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			
			String terminalId = new JSONObject(data).getString("id");
			
			stmt = connection.prepareStatement("select * from terminal where id = ?;");
			stmt.setString(1, terminalId);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				stmt.close();
				stmt = connection.prepareStatement("delete from terminal where id = ?;");
				stmt.setString(1, terminalId);
				int rs2 = stmt.executeUpdate();
				
				if (rs2 > 0) {
					jsonResult.put("response_code", "00");
					jsonResult.put("response_message", "Terminal has been removed");
					Logger.writeActivity("Terminal has been removed", ECPOS_FOLDER);
				} else {
					jsonResult.put("response_code", "01");
					jsonResult.put("response_message", "Terminal failed to remove");
					Logger.writeActivity("Terminal failed to remove", ECPOS_FOLDER);
				}
			} else {
				jsonResult.put("response_code", "01");
				jsonResult.put("response_message", "Terminal Not Found");
				Logger.writeActivity("Terminal NOT Found", ECPOS_FOLDER);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}

	@RequestMapping(value = { "/generate_qr" }, method = { RequestMethod.POST }, produces = "application/json")
	public String generateQR(@RequestBody String data) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;

		try {
			connection = dataSource.getConnection();
			
			String tableNo = new JSONObject(data).getString("tableNo");
			String checkNo = new JSONObject(data).getString("checkNo");
			String brandId = null;
			String storeId = null;
			
			stmt = connection.prepareStatement("select * from general_configuration where parameter = 'BRAND_ID';");
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				brandId = rs.getString("value");
				
				stmt.close();
				stmt = connection.prepareStatement("select * from store order by id desc;");
				rs2 = stmt.executeQuery();
				
				if (rs2.next()) {
					storeId = rs2.getString("id");
					
					stmt.close();
					stmt = connection.prepareStatement("select * from general_configuration where parameter = 'BYOD QR ENCRYPT KEY';");
					rs3 = stmt.executeQuery();
					
					if (rs3.next()) {
						String encryptKey = rs3.getString("value");
						
						String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
						
						String delimiter = "|;";
						String tokenValue = brandId + delimiter + storeId + delimiter + tableNo + delimiter + checkNo + delimiter + timeStamp;
						
						String token = AesEncryption.encrypt(encryptKey, tokenValue);
						
						String QRUrl = cloudUrl + "order#!/tk/" + token;
						byte[] QRImageByte = QRGenerate.generateQRImage(QRUrl, 200, 200);
						Base64 codec = new Base64();
						byte[] encoded = codec.encode(QRImageByte);
						String QRImage = "data:image/jpg;base64," + new String(encoded);
						jsonResult.put("QRImage", QRImage);
						
						jsonResult.put("response_code", "00");
						jsonResult.put("response_message", "QR image is generated");
						Logger.writeActivity("QR image is generated", ECPOS_FOLDER);
						Logger.writeActivity(QRImage, ECPOS_FOLDER);
					} else {
						jsonResult.put("response_code", "01");
						jsonResult.put("response_message", "BYOD QR Encrypt Key Not Found");
						Logger.writeActivity("BYOD QR Encrypt Key Not Found", ECPOS_FOLDER);
					}
				} else {
					jsonResult.put("response_code", "01");
					jsonResult.put("response_message", "Store Id Not Found");
					Logger.writeActivity("Store Id Not Found", ECPOS_FOLDER);
				}
			} else {
				jsonResult.put("response_code", "01");
				jsonResult.put("response_message", "Brand Id Not Found");
				Logger.writeActivity("Brand Id Not Found", ECPOS_FOLDER);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	public JSONObject getSelectedPrinter() {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("SELECT * from printer;");
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				jsonResult.put("model", rs.getString("model"));
				jsonResult.put("portName", rs.getString("port_name"));
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}
}
