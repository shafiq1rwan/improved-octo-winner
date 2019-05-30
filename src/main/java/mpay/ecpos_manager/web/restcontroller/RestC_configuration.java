package mpay.ecpos_manager.web.restcontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mpay.ecpos_manager.general.constant.Constant;
import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.AesEncryption;
import mpay.ecpos_manager.general.utility.QRGenerate;
import mpay.ecpos_manager.general.utility.hardware.Drawer;
import mpay.ecpos_manager.general.utility.hardware.ReceiptPrinter;
import mpay.ecpos_manager.general.utility.ipos.Card;
import mpay.ecpos_manager.general.utility.UserAuthenticationModel;
import mpay.ecpos_manager.general.utility.WebComponents;

@RestController
@RequestMapping("/rc/configuration")
public class RestC_configuration {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	Drawer drawer;
	
	@Autowired
	ReceiptPrinter receiptPrinter;
	
	@Autowired
	Card iposCard;
	
/*	@Value("${printer_exe}")
	private String printerExe;*/
	
	@Value("${CLOUD_BASE_URL}")
	private String cloudUrl;
	
	@RequestMapping(value = {"/session_checking"}, method = { RequestMethod.GET, RequestMethod.POST })
	public String ecposSessionChecking(HttpServletRequest request) {
		JSONObject jsonResult = new JSONObject();
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				jsonResult.put("responseCode", "00");
			} else {
				jsonResult.put("responseCode", "01");
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_cash_flow_list" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getTransactionList(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				
				stmt = connection.prepareStatement("SELECT a.id, a.cash_amount, a.new_amount, a.reference, b.staff_name, a.created_date from cash_drawer_log a, staff b"
						+ " WHERE a.performed_by = b.id");
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					JSONObject transaction = new JSONObject();
					transaction.put("id", rs.getString("id"));
					transaction.put("cashAmount", rs.getString("cash_amount"));
					transaction.put("newAmount", rs.getString("new_amount"));
					transaction.put("reference", rs.getString("reference"));
					transaction.put("staffName", rs.getString("staff_name"));
					transaction.put("datetime", rs.getString("created_date"));
					
					jary.put(transaction);
				}
				jsonResult.put("data", jary);
			} else {
				response.setStatus(408);
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
	
	@RequestMapping(value = { "/get_table_list" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String getTablelist(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
	
				stmt = connection.prepareStatement("SELECT * FROM table_setting where status_lookup_id = 2;");
				rs = stmt.executeQuery();
				
				boolean isEmpty = true;
				JSONArray tableList = new JSONArray();
				while (rs.next()) {
					isEmpty = false;
					
					stmt2 = connection.prepareStatement("SELECT COUNT(*) AS count FROM `check` WHERE table_number = ? AND check_status IN (1,2)");
					stmt2.setLong(1, rs.getLong("id"));
					rs2 = stmt2.executeQuery();
					
					if (rs2.next()) {
						String data = Long.toString(rs.getLong("id")) + "," + rs.getString("table_name") + "," + rs2.getString("count");
						tableList.put(data);
					}
				}
				
				if (isEmpty) {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Table info not found");
				} else {
					jsonResult.put(Constant.TABLE_LIST, tableList);
					jsonResult.put(Constant.RESPONSE_CODE, "00");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
				}
			} else {
				response.setStatus(408);
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
	public String getTerminallist(@PathVariable("terminalId") String terminalId, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray JARY = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
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
			} else {
				response.setStatus(408);
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
	
	@RequestMapping(value = { "/get_receipt_printer_manufacturers" }, method = { RequestMethod.GET}, produces = "application/json")
	public String getReceiptPrinterManufacturers(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray receiptPrinterManufacturerJArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if(user!=null) {
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("select * from receipt_printer_manufacturer_lookup;");
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					JSONObject receiptPrinterManufacturerObj = new JSONObject();
					receiptPrinterManufacturerObj.put("id", rs.getInt("id"));
					receiptPrinterManufacturerObj.put("name", rs.getString("name"));
					receiptPrinterManufacturerJArray.put(receiptPrinterManufacturerObj);
				}
				jsonResult.put("device_manufacturers", receiptPrinterManufacturerJArray);
				
				//get selected receipt printer if available
				JSONObject selectedReceiptPrinterObj = selectedReceiptPrinter();
				if(selectedReceiptPrinterObj.has("receipt_printer_manufacturer")) {
					jsonResult.put("selectedReceiptPrinter", selectedReceiptPrinterObj.getInt("receipt_printer_manufacturer"));
				}
			} else {
				response.setStatus(408);
			}	
		} catch(Exception e) {
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
	
	private JSONObject selectedReceiptPrinter() {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("select * from receipt_printer;");
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				jsonResult.put("receipt_printer_manufacturer", rs.getInt("receipt_printer_manufacturer"));
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
	
	@RequestMapping(value = { "/save_receipt_printer" }, method = { RequestMethod.POST }, produces = "application/json")
	public void saveReceiptPrinter(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				JSONObject receiptPrinter = new JSONObject(data);
					
				stmt = connection.prepareStatement("select * from receipt_printer;");
				rs = stmt.executeQuery();
				
				if (rs.next()) {
					stmt2 = connection.prepareStatement("update receipt_printer set receipt_printer_manufacturer = ?;");
					stmt2.setInt(1, receiptPrinter.getInt("receipt_printer_manufacturer"));
					stmt2.executeUpdate();
				} else {
					stmt2 = connection.prepareStatement("insert into receipt_printer (receipt_printer_manufacturer) values (?)");
					stmt2.setInt(1, receiptPrinter.getInt("receipt_printer_manufacturer"));
					stmt2.executeUpdate();
				}
			}
			else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
	}
	
/*	@RequestMapping(value = { "/get_printer_detail" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String getPrinterList(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
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
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult.toString();
	}*/
/*	
	@RequestMapping(value = { "/open_cash_drawer" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String openCashDrawer(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
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
			} else {
				response.setStatus(408);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jsonResult.toString();
	}
*/	
	@RequestMapping(value = { "/save_printer" }, method = { RequestMethod.POST }, produces = "application/json")
	public void savePrinter(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
			connection = dataSource.getConnection();
			
			JSONObject printer = new JSONObject(data);
				
			stmt = connection.prepareStatement("select * from printer;");
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				stmt2 = connection.prepareStatement("update printer set model_name = ?, port_name = ?, paper_size = ?;");
				stmt2.setString(1, printer.getString("modelName"));
				stmt2.setString(2, printer.getString("portName"));
				stmt2.setString(3, printer.getString("paperSize"));
				stmt2.executeUpdate();
			} else {
				stmt2 = connection.prepareStatement("insert into printer (model_name, port_name, paper_size) values (?,?,?)");
				stmt2.setString(1, printer.getString("modelName"));
				stmt2.setString(2, printer.getString("portName"));
				stmt2.setString(3, printer.getString("paperSize"));
				stmt2.executeUpdate();
			}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value = { "/save_terminal" }, method = { RequestMethod.POST }, produces = "application/json")
	public String saveTerminal(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			
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
				if (action.equals("create")) {
					stmt2 = connection.prepareStatement("insert into terminal (name,serial_number,wifi_IP,wifi_Port) values (?,?,?,?);");
					stmt2.setString(1, terminalName);
					stmt2.setString(2, terminalSerialNo);
					stmt2.setString(3, terminalWifiIP);
					stmt2.setString(4, terminalWifiPort);
				} else if (action.equals("update")) {
					stmt2 = connection.prepareStatement("update terminal set name = ?,serial_number = ?,wifi_IP = ?,wifi_Port = ? where id = ?;");
					stmt2.setString(1, terminalName);
					stmt2.setString(2, terminalSerialNo);
					stmt2.setString(3, terminalWifiIP);
					stmt2.setString(4, terminalWifiPort);
					stmt2.setString(5, terminalId);
				}
				int rs2 = stmt2.executeUpdate();
				
				if (rs2 > 0) {
					connection.commit();
					jsonResult.put("response_code", "00");
					jsonResult.put("response_message", "Terminal Information has been saved");
					Logger.writeActivity("Terminal Information has been saved", ECPOS_FOLDER);
				} else {
					connection.rollback();
					jsonResult.put("response_code", "01");
					jsonResult.put("response_message", "Terminal Information failed to save");
					Logger.writeActivity("Terminal Information failed to save", ECPOS_FOLDER);
				}
			}
			connection.setAutoCommit(true);
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
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
	public String removeTerminal(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			
			String terminalId = new JSONObject(data).getString("id");
			
			stmt = connection.prepareStatement("select * from terminal where id = ?;");
			stmt.setString(1, terminalId);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				stmt2 = connection.prepareStatement("delete from terminal where id = ?;");
				stmt2.setString(1, terminalId);
				int rs2 = stmt2.executeUpdate();
				
				if (rs2 > 0) {
					connection.commit();
					jsonResult.put("response_code", "00");
					jsonResult.put("response_message", "Terminal has been removed");
					Logger.writeActivity("Terminal has been removed", ECPOS_FOLDER);
				} else {
					connection.rollback();
					jsonResult.put("response_code", "01");
					jsonResult.put("response_message", "Terminal failed to remove");
					Logger.writeActivity("Terminal failed to remove", ECPOS_FOLDER);
				}
			} else {
				connection.rollback();
				jsonResult.put("response_code", "01");
				jsonResult.put("response_message", "Terminal Not Found");
				Logger.writeActivity("Terminal NOT Found", ECPOS_FOLDER);
			}
			connection.setAutoCommit(true);
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/ping_terminal" }, method = { RequestMethod.POST }, produces = "application/json")
	public String pingTerminal(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
			connection = dataSource.getConnection();
			String terminalId = new JSONObject(data).getString("id");
			
			stmt = connection.prepareStatement("select * from terminal where id = ?;");
			stmt.setString(1, terminalId);
			rs = stmt.executeQuery();
			
				if (rs.next()) {
					JSONObject terminalWifiIPPort = new JSONObject();
					if(rs.getString("wifi_IP") == null || rs.getString("wifi_Port") == null) {
						terminalWifiIPPort.put("wifi_IP", rs.getString("wifi_IP"));
						terminalWifiIPPort.put("wifi_Port", rs.getString("wifi_Port"));
					}
					JSONObject pingResponse = iposCard.pingTest("ping-test", terminalWifiIPPort);

					jsonResult.put(Constant.RESPONSE_CODE, pingResponse.getString(Constant.RESPONSE_CODE));
					jsonResult.put(Constant.RESPONSE_MESSAGE, pingResponse.getString(Constant.RESPONSE_MESSAGE));
					Logger.writeActivity(Constant.RESPONSE_MESSAGE, ECPOS_FOLDER);
					
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Terminal Not Found");
					Logger.writeActivity("Terminal NOT Found", ECPOS_FOLDER);
				}
			} else {
				response.setStatus(408);
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
	public String generateQR(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
			connection = dataSource.getConnection();
			
			JSONObject jsonRequest = new JSONObject(data); 
			
			if (jsonRequest.has("tableNo") && jsonRequest.has("checkNo")) {
				String tableNo = jsonRequest.getString("tableNo");
				String checkNo = jsonRequest.getString("checkNo");
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
							
							if (!token.equals(null)) {
								String QRUrl = cloudUrl + "order/" + token;
								byte[] QRImageByte = QRGenerate.generateQRImage(QRUrl, 200, 200);
								byte[] encoded = new Base64().encode(QRImageByte);
								String QRImage = "data:image/jpg;base64," + new String(encoded);
								jsonResult.put("QRImage", QRImage);
								
								jsonResult.put("response_code", "00");
								jsonResult.put("response_message", "QR image is generated");
								Logger.writeActivity("QR image is generated", ECPOS_FOLDER);
								Logger.writeActivity(QRImage, ECPOS_FOLDER);
							} else {
								jsonResult.put("response_code", "01");
								jsonResult.put("response_message", "Token Failed To Generate");
								Logger.writeActivity("Token Failed To Generate", ECPOS_FOLDER);
							}
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
			} else {
				jsonResult.put("response_code", "01");
				jsonResult.put("response_message", "Table No or Check No Not Found");
				Logger.writeActivity("Table No or Check No Not Found", ECPOS_FOLDER);
			}
			} else {
				response.setStatus(408);
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
	
	@RequestMapping(value = { "/print_qr" }, method = { RequestMethod.POST }, produces = "application/json")
	public String printQR(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
			connection = dataSource.getConnection();
			
			JSONObject jsonRequest = new JSONObject(data); 
			
			if (jsonRequest.has("tableNo") && jsonRequest.has("checkNo") && jsonRequest.has("qrImage")) {
				JSONObject printableJson = receiptPrinter.printQR(jsonRequest, user.getName());
			
				if(printableJson.getString("response_code").equals("00")) {
					jsonResult.put(Constant.RESPONSE_CODE, "00");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "PRINTER PROBLEM");
				}
			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Table No or Check No or QR Content Not Found");
				Logger.writeActivity("Table No or Check No or QR Content Not Found", ECPOS_FOLDER);
			}
			} else {
				response.setStatus(408);
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
	
	//Cash Drawer APIs
	@RequestMapping(value = { "/get_cash_drawer_setup_info" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getCashDrawerSetupInfo(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray deviceManufacturerJArray = new JSONArray();
		JSONArray portNameJArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("select * from device_manufacturer_lookup;");
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					JSONObject manufacturerObj = new JSONObject();
					manufacturerObj.put("id", rs.getInt("id"));
					manufacturerObj.put("name", rs.getString("name"));
					deviceManufacturerJArray.put(manufacturerObj);
				}
				jsonResult.put("device_manufacturers", deviceManufacturerJArray);
				
				stmt2 = connection.prepareStatement("select * from port_name_lookup;");
				rs2 = stmt2.executeQuery();
				
				while (rs2.next()) {
					JSONObject portNameObj = new JSONObject();
					portNameObj.put("id", rs2.getInt("id"));
					portNameObj.put("name", rs2.getString("name"));
					portNameJArray.put(portNameObj);
				}
				jsonResult.put("port_names", portNameJArray);
				
				stmt3 = connection.prepareStatement("select * from cash_drawer;");
				rs3 = stmt3.executeQuery();
				
				if (rs3.next()) {
					jsonResult.put("cash_amount", rs3.getDouble("cash_amount"));
					jsonResult.put("cash_alert", rs3.getLong("cash_alert"));
				} else {
					jsonResult.put("cash_amount", 0);
					jsonResult.put("cash_alert", 0);
				}
				
				//get selected cash drawer if available
				JSONObject selectedCashDrawerObj = selectedCashDrawer();
				if(selectedCashDrawerObj.has("device_manufacturer") && selectedCashDrawerObj.has("port_name")) {
					jsonResult.put("selectedCashDrawer", selectedCashDrawerObj);
				}
			}
			else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
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
	
	@RequestMapping(value = { "/get_cash_drawer_data" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getCashDrawerData(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("select * from cash_drawer;");
				rs = stmt.executeQuery();
				
				if(rs.next()) {
					jsonResult.put("device_manufacturer", rs.getString("device_manufacturer"));
					jsonResult.put("port_name", rs.getString("port_name"));
				}
			}
			else {
				response.setStatus(408);
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
	
	@RequestMapping(value = { "/save_cash_drawer" }, method = { RequestMethod.POST }, produces = "application/json")
	public void saveCashDrawer(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				JSONObject cashDrawer = new JSONObject(data);
					
				stmt = connection.prepareStatement("select * from cash_drawer;");
				rs = stmt.executeQuery();
				
				if (rs.next()) {
					stmt2 = connection.prepareStatement("update cash_drawer set device_manufacturer = ?, port_name = ?, cash_alert = ?;");
					stmt2.setInt(1, cashDrawer.getInt("device_manufacturer"));
					stmt2.setInt(2, cashDrawer.getInt("port_name"));
					stmt2.setLong(3, cashDrawer.getLong("cash_alert"));
					stmt2.executeUpdate();
				} else {
					stmt2 = connection.prepareStatement("insert into cash_drawer (device_manufacturer, port_name, cash_alert) values (?,?,?)");
					stmt2.setInt(1, cashDrawer.getInt("device_manufacturer"));
					stmt2.setInt(2, cashDrawer.getInt("port_name"));
					stmt2.setLong(3, cashDrawer.getLong("cash_alert"));
					stmt2.executeUpdate();
				}
			}
			else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value = { "/updateCashFlow" }, method = { RequestMethod.POST }, produces = "application/json")
	public String updateCashFlow(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "type", required = true) String type, 
			@RequestParam(value = "amount", required = true) double amount) {
		JSONObject result = new JSONObject();
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";
		
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				JSONObject staffDetail = getStaffDetail(user.getUsername());
				long staffId = -1;
				if (staffDetail.length() <= 0) {
					Logger.writeActivity("Staff Detail Not Found", ECPOS_FOLDER);
					resultCode = "E04";
					resultMessage = "Staff Detail Not Found.";
				} else {
					staffId = staffDetail.getLong("id");
					
					connection = dataSource.getConnection();
					
					stmt = connection.prepareStatement("select cash_amount from cash_drawer;");
					rs = stmt.executeQuery();
					
					if (rs.next()) {
						String cashType = "";
						double currentCash = rs.getDouble("cash_amount");
						double ammendCash = 0.00;
						double newCash = 0.00;
						
						if (type.equals("cashIn")) {
							cashType = "Manual Cash In";
							ammendCash = amount;
						} else {
							cashType = "Manual Cash Out";
							ammendCash = amount * -1.00;
						}
						newCash = currentCash + ammendCash;
						
						if (newCash < 0) {
							resultCode = "E03";
							resultMessage = "Not enough cash to cash out.";
						} else {
							stmt2 = connection.prepareStatement("update cash_drawer set cash_amount = ?;");
							stmt2.setDouble(1, newCash);
							stmt2.executeUpdate();
							
							stmt3 = connection.prepareStatement("insert into cash_drawer_log(cash_amount,new_amount,reference,performed_by) VALUES (?,?,?,?);");
							stmt3.setDouble(1, ammendCash);
							stmt3.setDouble(2, newCash);
							stmt3.setString(3, cashType);
							stmt3.setLong(4, staffId);
							stmt3.executeUpdate();
							
							result.put("amount", newCash);
							
							resultCode = "00";
							resultMessage = "Success";
						}
					} else {
						resultCode = "E02";
						resultMessage = "System Data Corrupted.";
					}
				}
			}
			else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
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
	
	@RequestMapping(value = { "/open_cash_drawer" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String openCashDrawer(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();		
				stmt = connection.prepareStatement("select * from cash_drawer;");
				rs = stmt.executeQuery();
				
				if (rs.next()) {
					stmt2 = connection.prepareStatement("select name from device_manufacturer_lookup where id = ?");
					stmt2.setInt(1,rs.getInt("device_manufacturer"));
					rs2 = stmt2.executeQuery();
					
					if(rs2.next()) {
						if(rs2.getString("name").equals("No Cash Drawer")) {
							jsonResult.put(Constant.RESPONSE_CODE, "00");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
							
						} else {
							stmt3 = connection.prepareStatement("select name from port_name_lookup where id = ?");
							stmt3.setInt(1,rs.getInt("port_name"));
							rs3 = stmt3.executeQuery();
							
							if(rs3.next()) {
								jsonResult = drawer.openDrawer(rs2.getString("name"), rs3.getString("name"));
							} else {
								jsonResult.put(Constant.RESPONSE_CODE, "01");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Port Name Not Found");
							}
						}
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Device Manufacturer Name Not Found");	
					}
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Cash Drawer Info Not Found");
				}
			}
			else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt.close();
				if (stmt3 != null) stmt.close();
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
	
	private JSONObject selectedCashDrawer() {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("select * from cash_drawer;");
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				jsonResult.put("device_manufacturer", rs.getInt("device_manufacturer"));
				jsonResult.put("port_name", rs.getInt("port_name"));
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
	
	//Printer APIs
	@RequestMapping(value = { "/print_receipt" }, method = RequestMethod.POST, produces = "application/json")
	public String printReceipt(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) { 
				JSONObject jsonData = new JSONObject(data);
				
				if(jsonData.has("checkNo")) {
					JSONObject printableJson = receiptPrinter.printReceipt(user.getName(), jsonData.getString("checkNo"));
					
					if(printableJson.getString("response_code").equals("00")) {
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
					} else {

					}
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Check No Not Found");
				}
			} else {
				response.setStatus(408);
			}
		} catch(Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {

		}
		
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/print_transaction_receipt" }, method = RequestMethod.POST, produces = "application/json")
	public String printTransactionReceipt(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) { 
				JSONObject jsonData = new JSONObject(data);
				
				if(jsonData.has("transactionId")) {
					connection = dataSource.getConnection();
					stmt = connection.prepareStatement("select check_number from transaction where id = ?;");
					stmt.setString(1, jsonData.getString("transactionId"));
					rs = stmt.executeQuery();
					
					if(rs.next()) {
						JSONObject printableJson = receiptPrinter.printReceipt(user.getName(), rs.getString("check_number"));
						if(printableJson.getString("response_code").equals("00")) {
							jsonResult.put(Constant.RESPONSE_CODE, "00");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
						} else {

						}
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Not Found");
					}
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Id Not Found");
				}
			} else {
				response.setStatus(408);
			}
		} catch(Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
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
	
	public JSONObject getStaffDetail(String username) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject staffDetail = new JSONObject();
		
		try {
			connection = dataSource.getConnection();
			
			stmt = connection.prepareStatement("select * from staff where staff_username = ?;");
			stmt.setString(1, username);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				staffDetail.put("id",rs.getString("id"));
				staffDetail.put("name",rs.getString("staff_name"));
				staffDetail.put("role",rs.getString("staff_role"));
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
		return staffDetail;
	}
}
