package mpay.ecpos_manager.web.restcontroller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.comm.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SystemUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	
	@Autowired
	private Environment env;
	/*
	 * @Value("${printer_exe}") private String printerExe;
	 */

	@Value("${CLOUD_BASE_URL}")
	private String cloudUrl;

	@Value("${receipt-path}")
	private String receiptPath;
	
	@Value("${menu_image_path}")
	private String menuImagePath;

	@RequestMapping(value = { "/session_checking" }, method = { RequestMethod.GET, RequestMethod.POST })
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

				stmt = connection.prepareStatement(
						"SELECT a.id, a.cash_amount, a.new_amount, a.reference, b.staff_name, a.created_date from cash_drawer_log a, staff b"
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
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}

	@RequestMapping(value = { "/get_table_list" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json")
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

					stmt2 = connection.prepareStatement(
							"SELECT COUNT(*) AS count FROM `check` WHERE table_number = ? AND check_status IN (1,2)");
					stmt2.setLong(1, rs.getLong("id"));
					rs2 = stmt2.executeQuery();

					if (rs2.next()) {
						String data = Long.toString(rs.getLong("id")) + "," + rs.getString("table_name") + ","
								+ rs2.getString("count");
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
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}

	@RequestMapping(value = { "/get_trans_interval_list" }, method = {
			RequestMethod.GET }, produces = "application/json")
	public String getTransIntervalList(HttpServletRequest request, HttpServletResponse response) {
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

				stmt = connection
						.prepareStatement("SELECT id, interval_sync_name FROM interval_sync_lookup ORDER BY id;");
				rs = stmt.executeQuery();

				while (rs.next()) {
					JSONObject transaction = new JSONObject();
					transaction.put("id", rs.getString("id"));
					transaction.put("intervalSyncName", rs.getString("interval_sync_name"));

					jary.put(transaction);
				}
				jsonResult.put("transConfigIntervalList", jary);
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}

	@RequestMapping(value = { "/get_trans_config" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getTransConfig(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				String staffSyncFlagStr = webComponent.getGeneralConfig(connection, "STAFF TRX SYNC");
				String transSyncFlagStr = webComponent.getGeneralConfig(connection, "TRX SYNC");
				String selectedIntervalStr = webComponent.getGeneralConfig(connection, "INTERVAL TRX SYNC");

				if (!(staffSyncFlagStr.equals("") && transSyncFlagStr.equals("") && selectedIntervalStr.equals(""))) {
					boolean staffSyncFlag = Boolean.parseBoolean(staffSyncFlagStr);
					boolean transSyncFlag = Boolean.parseBoolean(transSyncFlagStr);
					jsonResult.put("staffSyncFlag", staffSyncFlag);
					jsonResult.put("transSyncFlag", transSyncFlag);
					jsonResult.put("selectedInterval", selectedIntervalStr);
				} else {
					throw new Exception("Error getting transaction configurations");
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}

	@RequestMapping(value = { "/get_terminal_list/{terminalId}" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json")
	public String getTerminallist(@PathVariable("terminalId") String terminalId, HttpServletRequest request,
			HttpServletResponse response) {
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
				
				// get selected qr payment if available
				JSONObject selectedQRPaymentObj = selectedQRPayment();
				if (selectedQRPaymentObj.has("qr_payment_method")) {
					jsonResult.put("selectedQRPayment",
							selectedQRPaymentObj.getInt("qr_payment_method"));
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}

	@RequestMapping(value = { "/get_receipt_printer_manufacturers" }, method = {
			RequestMethod.GET }, produces = "application/json")
	public String getReceiptPrinterManufacturers(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray receiptPrinterManufacturerJArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
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

				// get selected receipt printer if available
				JSONObject selectedReceiptPrinterObj = selectedReceiptPrinter();
				if (selectedReceiptPrinterObj.has("receipt_printer_manufacturer")) {
					jsonResult.put("selectedReceiptPrinter",
							selectedReceiptPrinterObj.getInt("receipt_printer_manufacturer"));
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}

		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_qr_payment_list/{qrPaymentId}" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json")
	public String getQRPaymentList(@PathVariable("qrPaymentId") String qrPaymentId, HttpServletRequest request,
			HttpServletResponse response) {
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
				
				if (qrPaymentId.equals("all")) {
					stmt = connection.prepareStatement("select * from qr_payment_method_lookup;");
				} else {
					stmt = connection.prepareStatement("select * from qr_payment_method_lookup where id = ?;");
					stmt.setString(1, qrPaymentId);
				}
				rs = stmt.executeQuery();

				while (rs.next()) {
					JSONObject jObject = new JSONObject();
					jObject.put("id", rs.getInt("id"));
					jObject.put("name", rs.getString("name"));
					jObject.put("product_desc", rs.getString("product_desc"));
					jObject.put("tid", rs.getString("tid"));
					jObject.put("url", rs.getString("url"));
					jObject.put("project_key", rs.getString("project_key"));
					jObject.put("uuid", rs.getString("uuid"));
					JARY.put(jObject);
				}
				jsonResult.put("qrPayments", JARY);
				
				// get selected qr payment if available
				JSONObject selectedQRPaymentObj = selectedQRPayment();
				if (selectedQRPaymentObj.has("qr_payment_method")) {
					jsonResult.put("selectedQRPayment",
							selectedQRPaymentObj.getInt("qr_payment_method"));
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	/*@RequestMapping(value = { "/get_qr_payment_method" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json")
	public String getQRPaymentMethod(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				// get selected qr payment if available
				JSONObject selectedQRPaymentObj = selectedQRPayment();
				if (selectedQRPaymentObj.has("qr_payment_method")) {
					jsonResult.put("selectedQRPayment",
							selectedQRPaymentObj.getInt("qr_payment_method"));
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}*/

	private JSONObject selectedReceiptPrinter() {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("select * from receipt_printer;");
			rs = stmt.executeQuery();

			if (rs.next()) {
				jsonResult.put("receipt_printer_manufacturer", rs.getInt("receipt_printer_manufacturer"));
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}
	
	private JSONObject selectedQRPayment() {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("select * from qr_payment_method;");
			rs = stmt.executeQuery();

			if (rs.next()) {
				jsonResult.put("qr_payment_method", rs.getInt("qr_payment_method_used"));
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
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
					stmt2 = connection
							.prepareStatement("insert into receipt_printer (receipt_printer_manufacturer) values (?)");
					stmt2.setInt(1, receiptPrinter.getInt("receipt_printer_manufacturer"));
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
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value = { "/save_qr_payment_method" }, method = { RequestMethod.POST }, produces = "application/json")
	public void saveQRPaymentMethod(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
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
				JSONObject qrPaymentMethod = new JSONObject(data);

				stmt = connection.prepareStatement("select * from qr_payment_method;");
				rs = stmt.executeQuery();

				if (rs.next()) {
					stmt2 = connection.prepareStatement("update qr_payment_method set qr_payment_method_used = ?;");
					stmt2.setInt(1, qrPaymentMethod.getInt("qr_payment_method"));
					stmt2.executeUpdate();
				} else {
					stmt2 = connection
							.prepareStatement("insert into qr_payment_method (qr_payment_method_used) values (?)");
					stmt2.setInt(1, qrPaymentMethod.getInt("qr_payment_method"));
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
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = { "/save_trans_config" }, method = { RequestMethod.POST }, produces = "application/json")
	public String saveTransConfig(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		JSONObject result = new JSONObject();
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				JSONObject transConfig = new JSONObject(data);
				System.out.println(transConfig);
				if (transConfig.has("staffSyncFlag") && transConfig.has("transSyncFlag")
						&& transConfig.has("selectedInterval")) {
					boolean staffSyncFlag = transConfig.getBoolean("staffSyncFlag");
					boolean transSyncFlag = transConfig.getBoolean("transSyncFlag");
					int selectedInterval = transConfig.getInt("selectedInterval");

					webComponent.updateGeneralConfig(connection, "STAFF TRX SYNC", String.valueOf(staffSyncFlag));
					webComponent.updateGeneralConfig(connection, "TRX SYNC", String.valueOf(transSyncFlag));
					webComponent.updateGeneralConfig(connection, "INTERVAL TRX SYNC", String.valueOf(selectedInterval));
					resultCode = "00";
					resultMessage = "Success";
				} else {
					resultCode = "E02";
					resultMessage = "System Data Corrupted.";
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
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

	/*
	 * @RequestMapping(value = { "/get_printer_detail" }, method = {
	 * RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	 * public String getPrinterList(HttpServletRequest request, HttpServletResponse
	 * response) { JSONObject jsonResult = new JSONObject();
	 * 
	 * WebComponents webComponent = new WebComponents(); UserAuthenticationModel
	 * user = webComponent.getEcposSession(request);
	 * 
	 * try { if (user != null) { String getPrinterListParams[] = { printerExe, "1",
	 * "2", "2", "2", "2" }; Process executePrinter =
	 * Runtime.getRuntime().exec(getPrinterListParams); BufferedReader input = new
	 * BufferedReader(new InputStreamReader(executePrinter.getInputStream()));
	 * 
	 * StringBuilder responseString = new StringBuilder();
	 * 
	 * while (input.readLine() != null) { responseString.append(input.readLine()); }
	 * 
	 * JSONObject jsonObj = new JSONObject(responseString.toString());
	 * 
	 * if (jsonObj.has("PortInfoList")) { JSONArray portInfoList =
	 * jsonObj.getJSONArray("PortInfoList"); // JSONArray paperSizeList =
	 * jsonObj.getJSONArray("PaperSizeList");
	 * 
	 * jsonResult.put("portInfoList", portInfoList); //
	 * jsonResult.put("PaperSizeList", paperSizeList);
	 * 
	 * JSONObject selectedPrinter = getSelectedPrinter(); if
	 * (selectedPrinter.length() > 0) { jsonResult.put("selectedPrinter",
	 * selectedPrinter.getString("portName")); } } } else { response.setStatus(408);
	 * } } catch (Exception e) { Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
	 * e.printStackTrace(); } return jsonResult.toString(); }
	 */
	/*
	 * @RequestMapping(value = { "/open_cash_drawer" }, method = {
	 * RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	 * public String openCashDrawer(HttpServletRequest request, HttpServletResponse
	 * response) { JSONObject jsonResult = new JSONObject(); Connection connection =
	 * null; PreparedStatement stmt = null; ResultSet rs = null;
	 * 
	 * WebComponents webComponent = new WebComponents(); UserAuthenticationModel
	 * user = webComponent.getEcposSession(request);
	 * 
	 * try { if (user != null) { connection = dataSource.getConnection();
	 * 
	 * stmt = connection.prepareStatement("select * from printer;"); rs =
	 * stmt.executeQuery();
	 * 
	 * if (rs.next()) { String openDrawerParams[] = { printerExe, "2",
	 * rs.getString("model_name"), rs.getString("paper_size"),
	 * rs.getString("port_name"), "2" }; Process openDrawer =
	 * Runtime.getRuntime().exec(openDrawerParams); BufferedReader input = new
	 * BufferedReader(new InputStreamReader(openDrawer.getInputStream()));
	 * 
	 * StringBuilder responseString = new StringBuilder();
	 * 
	 * while (input.readLine() != null) { responseString.append(input.readLine()); }
	 * 
	 * JSONObject jsonObj = new JSONObject(responseString.toString());
	 * 
	 * if (jsonObj.has("ResponseCode") && jsonObj.has("ResponseMessage")) { if
	 * (jsonObj.getInt("ResponseCode") == 1) {
	 * jsonResult.put(Constant.RESPONSE_CODE, "00");
	 * jsonResult.put(Constant.RESPONSE_MESSAGE, "Success"); } else {
	 * jsonResult.put(Constant.RESPONSE_CODE, "01");
	 * jsonResult.put(Constant.RESPONSE_MESSAGE, "Error Occur While Open Drawer"); }
	 * } else { jsonResult.put(Constant.RESPONSE_CODE, "01");
	 * jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Request"); } } else {
	 * jsonResult.put(Constant.RESPONSE_CODE, "01");
	 * jsonResult.put(Constant.RESPONSE_MESSAGE, "No Connected Printer Found"); } }
	 * else { response.setStatus(408); } } catch (Exception ex) {
	 * ex.printStackTrace(); } return jsonResult.toString(); }
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
					stmt2 = connection
							.prepareStatement("update printer set model_name = ?, port_name = ?, paper_size = ?;");
					stmt2.setString(1, printer.getString("modelName"));
					stmt2.setString(2, printer.getString("portName"));
					stmt2.setString(3, printer.getString("paperSize"));
					stmt2.executeUpdate();
				} else {
					stmt2 = connection
							.prepareStatement("insert into printer (model_name, port_name, paper_size) values (?,?,?)");
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
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
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

					stmt = connection.prepareStatement(
							"select * from terminal where (name = ? or serial_number = ?) and id != ?;");
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
						stmt2 = connection.prepareStatement(
								"insert into terminal (name,serial_number,wifi_IP,wifi_Port) values (?,?,?,?);");
						stmt2.setString(1, terminalName);
						stmt2.setString(2, terminalSerialNo);
						stmt2.setString(3, terminalWifiIP);
						stmt2.setString(4, terminalWifiPort);
					} else if (action.equals("update")) {
						stmt2 = connection.prepareStatement(
								"update terminal set name = ?,serial_number = ?,wifi_IP = ?,wifi_Port = ? where id = ?;");
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
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/save_qrPayment" }, method = { RequestMethod.POST }, produces = "application/json")
	public String saveQRPayment(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
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

				JSONObject qrPayment = new JSONObject(data);
				String action = qrPayment.getString("action");
				String name = qrPayment.getString("name");
				String tid = qrPayment.getString("tid");
				String url = qrPayment.getString("url");
				String project_key = qrPayment.getString("project_key");
				String uuid = qrPayment.getString("uuid");

				String product_desc = null;
				if (qrPayment.has("product_desc")) {
					product_desc = qrPayment.getString("product_desc");
				}

				String id = null;
				if (action.equals("create")) {
					stmt = connection.prepareStatement("select * from qr_payment_method_lookup where name = ?");
					stmt.setString(1, name);
				} else if (action.equals("update")) {
					id = qrPayment.getString("id");

					stmt = connection.prepareStatement(
							"select * from qr_payment_method_lookup where name = ? and id != ?;");
					stmt.setString(1, name);
					stmt.setString(2, id);
				}
				rs = stmt.executeQuery();

				if (rs.next()) {
					jsonResult.put("response_code", "01");
					jsonResult.put("response_message", "Duplicate QR Payment Information");
					Logger.writeActivity("Duplicate QR Payment Information", ECPOS_FOLDER);
				} else {
					if (action.equals("create")) {
						stmt2 = connection.prepareStatement(
								"insert into qr_payment_method_lookup (name,tid,product_desc,url,project_key,uuid) values (?,?,?,?,?,?);");
						stmt2.setString(1, name);
						stmt2.setString(2, tid);
						stmt2.setString(3, product_desc);
						stmt2.setString(4, url);
						stmt2.setString(5, project_key);
						stmt2.setString(6, uuid);
					} else if (action.equals("update")) {
						stmt2 = connection.prepareStatement(
								"update qr_payment_method_lookup set name = ?,tid = ?,product_desc = ?,url = ?,project_key = ?,uuid = ? where id = ?;");
						stmt2.setString(1, name);
						stmt2.setString(2, tid);
						stmt2.setString(3, product_desc);
						stmt2.setString(4, url);
						stmt2.setString(5, project_key);
						stmt2.setString(6, uuid);
						stmt2.setString(7, id);
					}
					int rs2 = stmt2.executeUpdate();

					if (rs2 > 0) {
						connection.commit();
						jsonResult.put("response_code", "00");
						jsonResult.put("response_message", "QR Payment Information has been saved");
						Logger.writeActivity("QR Payment Information has been saved", ECPOS_FOLDER);
					} else {
						connection.rollback();
						jsonResult.put("response_code", "01");
						jsonResult.put("response_message", "QR Payment Information failed to save");
						Logger.writeActivity("QR Payment Information failed to save", ECPOS_FOLDER);
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
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
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
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/remove_qrPayment" }, method = { RequestMethod.POST }, produces = "application/json")
	public String removeQRPayment(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
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

				String id = new JSONObject(data).getString("id");

				stmt = connection.prepareStatement("select * from qr_payment_method_lookup where id = ?;");
				stmt.setString(1, id);
				rs = stmt.executeQuery();

				if (rs.next()) {
					stmt2 = connection.prepareStatement("delete from qr_payment_method_lookup where id = ?;");
					stmt2.setString(1, id);
					int rs2 = stmt2.executeUpdate();

					if (rs2 > 0) {
						connection.commit();
						jsonResult.put("response_code", "00");
						jsonResult.put("response_message", "QR Payment has been removed");
						Logger.writeActivity("QR Payment has been removed", ECPOS_FOLDER);
					} else {
						connection.rollback();
						jsonResult.put("response_code", "01");
						jsonResult.put("response_message", "QR Payment failed to remove");
						Logger.writeActivity("QR Payment failed to remove", ECPOS_FOLDER);
					}
				} else {
					connection.rollback();
					jsonResult.put("response_code", "01");
					jsonResult.put("response_message", "QR Payment Not Found");
					Logger.writeActivity("QR Payment Not Found", ECPOS_FOLDER);
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
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
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
					if (rs.getString("wifi_IP") != null || rs.getString("wifi_Port") != null) {
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
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
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

				if (jsonRequest.has("tableNo") && jsonRequest.has("checkNo") && jsonRequest.has("tableName")) {
					String tableNo = jsonRequest.getString("tableNo");
					String checkNo = jsonRequest.getString("checkNo");
					String tableName = jsonRequest.getString("tableName");
					String brandId = null;
					String storeId = null;

					stmt = connection
							.prepareStatement("select * from general_configuration where parameter = 'BRAND_ID';");
					rs = stmt.executeQuery();

					if (rs.next()) {
						brandId = rs.getString("value");

						stmt.close();
						stmt = connection.prepareStatement("select * from store order by id desc;");
						rs2 = stmt.executeQuery();

						if (rs2.next()) {
							storeId = rs2.getString("id");

							stmt.close();
							stmt = connection.prepareStatement(
									"select * from general_configuration where parameter = 'BYOD QR ENCRYPT KEY';");
							rs3 = stmt.executeQuery();

							if (rs3.next()) {
								String encryptKey = rs3.getString("value");

								String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
										.format(Calendar.getInstance().getTime());

								String delimiter = "|;";
								String tokenValue = brandId + delimiter + storeId + delimiter + tableNo + delimiter
										+ checkNo + delimiter + timeStamp + delimiter + tableName;

								String token = AesEncryption.encrypt(encryptKey, tokenValue);

								if (!token.equals(null)) {

									String isMpayEnv = "";
									String QRUrl = "";

									PreparedStatement stmtC = connection.prepareStatement(
											"select * from general_configuration where parameter = 'IS EXTERNAL IP REQUIERED';");
									ResultSet rsC = stmtC.executeQuery();

									if (rsC.next()) {
										isMpayEnv = rsC.getString("value");
									}

									if (isMpayEnv.equals("1")) {
										PreparedStatement stmtC2 = connection.prepareStatement(
												"select * from general_configuration where parameter = 'BYOD PUBLIC URL';");
										ResultSet rsC2 = stmtC2.executeQuery();
										if (rsC2.next()) {
											QRUrl = rsC2.getString("value") + "order/" + token;
										} else {
											QRUrl = cloudUrl + "order/" + token;
										}
									} else {
										QRUrl = cloudUrl + "order/" + token;
									}

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
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
				}
				if (connection != null) {
					connection.close();
				}
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
					JSONObject printableJson = receiptPrinter.printQR(jsonRequest, user.getName(), false);

					if (printableJson.getString("response_code").equals("00")) {
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
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}

	// Display table QR pdf
	@RequestMapping(value = { "/display_qr_pdf" }, method = { RequestMethod.POST }, produces = "application/json")
	public byte[] displayQRPdf(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		byte[] outputPdf = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				JSONObject jsonRequest = new JSONObject(data);
				Logger.writeActivity("Display QR Pdf [START]", ECPOS_FOLDER);

				if (jsonRequest.has("tableNo") && jsonRequest.has("checkNo") && jsonRequest.has("qrImage")) {
					JSONObject printableJson = receiptPrinter.printQR(jsonRequest, user.getName(), true);
					if (printableJson.getString("response_code").equals("00")) {
						outputPdf = Files.readAllBytes(Paths.get(receiptPath, "qrReciept.pdf"));
						Logger.writeActivity("Pdf Content Length: " + outputPdf.length, ECPOS_FOLDER);
					}
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("Display QR Pdf [END]", ECPOS_FOLDER);
		return outputPdf;
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
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}

	// Cash Drawer APIs
	@RequestMapping(value = { "/get_cash_drawer_setup_info" }, method = {
			RequestMethod.GET }, produces = "application/json")
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

				// get selected cash drawer if available
				JSONObject selectedCashDrawerObj = selectedCashDrawer();
				if (selectedCashDrawerObj.has("device_manufacturer") && selectedCashDrawerObj.has("port_name")) {
					jsonResult.put("selectedCashDrawer", selectedCashDrawerObj);
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (stmt3 != null)
					stmt3.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
				}
				if (connection != null) {
					connection.close();
				}
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

				if (rs.next()) {
					jsonResult.put("device_manufacturer", rs.getString("device_manufacturer"));
					jsonResult.put("port_name", rs.getString("port_name"));
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}

	@RequestMapping(value = { "/get_store_data" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getStoreData(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				String brandId = webComponent.getGeneralConfig(connection, "BRAND_ID");
				stmt = connection.prepareStatement("SELECT id from store;");
				rs = stmt.executeQuery();

				if (rs.next()) {
					jsonResult.put("storeId", rs.getString("id"));
					jsonResult.put("brandId", brandId);
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
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
					stmt2 = connection.prepareStatement(
							"update cash_drawer set device_manufacturer = ?, port_name = ?, cash_alert = ?;");
					stmt2.setInt(1, cashDrawer.getInt("device_manufacturer"));
					stmt2.setInt(2, cashDrawer.getInt("port_name"));
					stmt2.setLong(3, cashDrawer.getLong("cash_alert"));
					stmt2.executeUpdate();
				} else {
					stmt2 = connection.prepareStatement(
							"insert into cash_drawer (device_manufacturer, port_name, cash_alert) values (?,?,?)");
					stmt2.setInt(1, cashDrawer.getInt("device_manufacturer"));
					stmt2.setInt(2, cashDrawer.getInt("port_name"));
					stmt2.setLong(3, cashDrawer.getLong("cash_alert"));
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
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
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

							stmt3 = connection.prepareStatement(
									"insert into cash_drawer_log(cash_amount,new_amount,reference,performed_by) VALUES (?,?,?,?);");
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
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (stmt3 != null)
					stmt3.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
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

	@RequestMapping(value = { "/open_cash_drawer" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json")
	public String openCashDrawer(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("select * from cash_drawer;");
				rs = stmt.executeQuery();

				if (rs.next()) {
					stmt2 = connection.prepareStatement("select name from device_manufacturer_lookup where id = ?");
					stmt2.setInt(1, rs.getInt("device_manufacturer"));
					rs2 = stmt2.executeQuery();

					if (rs2.next()) {
						if (rs2.getString("name").equals("No Cash Drawer")) {
							jsonResult.put(Constant.RESPONSE_CODE, "00");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");

						} else {
							stmt3 = connection.prepareStatement("select name from port_name_lookup where id = ?");
							stmt3.setInt(1, rs.getInt("port_name"));
							rs3 = stmt3.executeQuery();

							if (rs3.next()) {
								//for cash drawer connected to HRPT Printer 
								stmt4 = connection.prepareStatement("select rp.receipt_printer_manufacturer from receipt_printer rp\r\n" + 
										"inner join receipt_printer_manufacturer_lookup rpl on rpl.id = rp.receipt_printer_manufacturer\r\n" + 
										"where rpl.name like '%POS%'"); // Change the printer to POS80
								rs4 = stmt4	.executeQuery();
								if (rs4.next()) {
									// For model MK410 - 23/04/2021
									// Set to any model in the setting
									// Cash drawer cable must be connected to Thermal Printer
									cashDrawerMK410();
//									jsonResult = cashdrawerOpen();
								}
								else
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
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt.close();
				if (stmt3 != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}

		return jsonResult.toString();
	}
	
	/*public void cashDrawerOpen2 () {
		Logger.writeActivity("ENTER cashDrawerOpen2 ", Property.getHARDWARE_FOLDER_NAME());
		
		Enumeration port_list = CommPortIdentifier.getPortIdentifiers ();

		while (port_list.hasMoreElements ()) { //This part in the diving, why not you?
		// Get the list of ports
		CommPortIdentifier port_id =
		(CommPortIdentifier) port_list.nextElement ();

		// Find each ports type and name
		if (port_id.getPortType () == CommPortIdentifier.PORT_SERIAL)
		{
			Logger.writeActivity("Serial port: " + port_id.getName (), Property.getHARDWARE_FOLDER_NAME());
		}
		else if (port_id.getPortType () == CommPortIdentifier.PORT_PARALLEL)
		{
			Logger.writeActivity("Parallel port: " + port_id.getName (), Property.getHARDWARE_FOLDER_NAME());
		} else
			Logger.writeActivity("Other port: " + port_id.getName (), Property.getHARDWARE_FOLDER_NAME());

		// Attempt to open it
		try {
		CommPort port = port_id.open ("PortListOpen",20);
		Logger.writeActivity(" Opened successfully", Property.getHARDWARE_FOLDER_NAME());
		port.close ();
		}
		catch (PortInUseException pe)
		{
			Logger.writeActivity(" Open failed", Property.getHARDWARE_FOLDER_NAME());
		String owner_name = port_id.getCurrentOwner ();
		if (owner_name == null)
			Logger.writeActivity(" Port Owned by unidentified app", Property.getHARDWARE_FOLDER_NAME());
		else
		// The owner name not returned correctly unless it is
		// a Java program.
			Logger.writeActivity(" " + owner_name, Property.getHARDWARE_FOLDER_NAME());
		}
		}
	}*/
	
	public JSONObject cashdrawerOpen() {
		JSONObject jsonResult = new JSONObject();
        byte[] open = {27, 112, 48, 55, 121};
//        byte[] cutter = {29, 86,49};
        String printer = "POS80";
        
        PrintServiceAttributeSet printserviceattributeset = new HashPrintServiceAttributeSet();
        printserviceattributeset.add(new PrinterName(printer,null));
        PrintService[] printservice = PrintServiceLookup.lookupPrintServices(null, printserviceattributeset);
        
        if(printservice.length!=1){
            try {
            	System.out.println("Printer not found");
				jsonResult.put(Constant.RESPONSE_CODE, "02");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "PRINTER NOT FOUND");
				Logger.writeActivity("FAILED DUE TO PRINTER NOT FOUND", Property.getHARDWARE_FOLDER_NAME());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        PrintService pservice = printservice[0];
        
        DocPrintJob job = pservice.createPrintJob();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(open,flavor,null);
        
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        try {
            job.print(doc, aset);
            job.wait();
            job.notifyAll();
            try {
				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Logger.writeActivity("OPEN DRAWER SUCCESS", Property.getHARDWARE_FOLDER_NAME());
        } catch (PrintException ex) {
        	try {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "FAILED");
				Logger.writeActivity("OPEN DRAWER FAILED", Property.getHARDWARE_FOLDER_NAME());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println(ex.getMessage());
        } catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        return jsonResult;
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

			if (rs.next()) {
				jsonResult.put("device_manufacturer", rs.getInt("device_manufacturer"));
				jsonResult.put("port_name", rs.getInt("port_name"));
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}

	// Printer APIs
	@RequestMapping(value = { "/print_receipt" }, method = RequestMethod.POST, produces = "application/json")
	public String printReceipt(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				JSONObject jsonData = new JSONObject(data);

				if (jsonData.has("checkNo")) {
					JSONObject printableJson = receiptPrinter.printReceipt(user.getName(), user.getStoreType(),
							jsonData.getString("checkNo"), false);

					if (printableJson.getString("response_code").equals("00")) {
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE,
								"Printing Failed. Please check your printer configuration.");
					}
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Check No Not Found");
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {

		}

		return jsonResult.toString();
	}

	@RequestMapping(value = {
			"/print_transaction_receipt" }, method = RequestMethod.POST, produces = "application/json")
	public String printTransactionReceipt(@RequestBody String data, HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				JSONObject jsonData = new JSONObject(data);

				if (jsonData.has("transactionId")) {
					connection = dataSource.getConnection();
					stmt = connection.prepareStatement("select check_number from transaction where id = ?;");
					stmt.setString(1, jsonData.getString("transactionId"));
					rs = stmt.executeQuery();

					if (rs.next()) {
						JSONObject printableJson = receiptPrinter.printReceipt(user.getName(), user.getStoreType(),
								rs.getString("check_number"), false);
						if (printableJson.getString("response_code").equals("00")) {
							jsonResult.put(Constant.RESPONSE_CODE, "00");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
						} else {
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE,
									"Printing Failed. Please check your printer configuration.");
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
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}

		return jsonResult.toString();
	}

	@RequestMapping(value = { "/display_receipt" }, method = RequestMethod.POST, produces = "application/json")
	public byte[] displayReceiptPdf(@RequestBody String data, HttpServletRequest request,
			HttpServletResponse response) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		byte[] outputPdf = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				Logger.writeActivity("Display Receipt Pdf [START]", ECPOS_FOLDER);
				JSONObject jsonData = new JSONObject(data);

				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("select check_number from transaction where id = ?;");
				stmt.setString(1, jsonData.getString("transactionId"));
				rs = stmt.executeQuery();

				if (rs.next()) {
					// generate the pdf
					JSONObject printableJson = receiptPrinter.printReceipt(user.getName(), user.getStoreType(),
							rs.getString("check_number"), true);
					if (printableJson.getString("response_code").equals("00")) {
						outputPdf = Files.readAllBytes(Paths.get(receiptPath, "receipt.pdf"));
						Logger.writeActivity("Receipt Pdf Content Length: " + outputPdf.length, ECPOS_FOLDER);
					}
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("Display Receipt Pdf [END]", ECPOS_FOLDER);
		return outputPdf;
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
				staffDetail.put("id", rs.getString("id"));
				staffDetail.put("name", rs.getString("staff_name"));
				staffDetail.put("role", rs.getString("staff_role"));
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return staffDetail;
	}

	@RequestMapping(value = { "/get_kds_config" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getKdsConfig(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonObj = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("SELECT * from kds;");
				rs = stmt.executeQuery();

				if (rs.next()) {
					jsonObj.put("id", rs.getString("id"));
					jsonObj.put("time_warning", rs.getString("time_warning"));
					jsonObj.put("time_late", rs.getString("time_late"));
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonObj.toString();
	}

	@RequestMapping(value = { "/save_kds_setting" }, method = { RequestMethod.POST }, produces = "application/json")
	public String saveKdsSetting(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				connection.setAutoCommit(false);

				JSONObject kdsSetting = new JSONObject(data);
				String id = kdsSetting.getString("id");
				String timeWarning = kdsSetting.getString("timeWarning");
				String timeLate = kdsSetting.getString("timeLate");

				stmt = connection.prepareStatement(
						"update kds set time_warning = ?,time_late = ? where id = ?;");				
				stmt.setString(1, timeWarning);
				stmt.setString(2, timeLate);
				stmt.setString(3, id);

				int rs = stmt.executeUpdate();

				if (rs > 0) {
					connection.commit();
					jsonResult.put("response_code", "00");
					jsonResult.put("response_message", "KDS setting has been saved");
					Logger.writeActivity("KDS setting has been saved", ECPOS_FOLDER);
				} else {
					connection.rollback();
					jsonResult.put("response_code", "01");
					jsonResult.put("response_message", "KDS setting failed to save");
					Logger.writeActivity("KDS setting failed to save", ECPOS_FOLDER);
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
				if (stmt != null)
					stmt.close();
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/shutdown_device" }, method = { RequestMethod.POST }, produces = "application/json")
	public String shutdownDevice(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		try {
			jsonResult.put("result", shutdown(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonResult.toString();
	}
	
	public static boolean shutdown(int time) throws IOException {
	    String shutdownCommand = null, t = time == 0 ? "now" : String.valueOf(time);

	    if(SystemUtils.IS_OS_AIX)
	        shutdownCommand = "shutdown -Fh " + t;
	    else if(SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC|| SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_UNIX)
	        shutdownCommand = "shutdown -h " + t;
	    else if(SystemUtils.IS_OS_HP_UX)
	        shutdownCommand = "shutdown -hy " + t;
	    else if(SystemUtils.IS_OS_IRIX)
	        shutdownCommand = "shutdown -y -g " + t;
	    else if(SystemUtils.IS_OS_SOLARIS || SystemUtils.IS_OS_SUN_OS)
	        shutdownCommand = "shutdown -y -i5 -g" + t;
	    else if(SystemUtils.IS_OS_WINDOWS)
	        shutdownCommand = "shutdown.exe /s /t " + t;
	    else
	        return false;

	    Runtime.getRuntime().exec(shutdownCommand);
	    return true;
	}
	
	// Printer APIs
	@RequestMapping(value = { "/print_kitchen_receipt" }, method = RequestMethod.POST, produces = "application/json")
	public String printKitchenReceipt(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				JSONObject jsonData = new JSONObject(data);

				if (jsonData.has("checkNo")) {
					JSONObject printableJson = receiptPrinter.printKitchenReceipt(user.getName(), user.getStoreType(),
							jsonData.getString("checkNo"), false);

					if (printableJson.getString("response_code").equals("00")) {
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE,
								"Printing Failed. Please check your printer configuration.");
					}
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Check No Not Found");
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {

		}

		return jsonResult.toString();
	}
	
	// Printer APIs
	@RequestMapping(value = { "/print_receipt_before_pay" }, method = RequestMethod.POST, produces = "application/json")
	public String printReceiptBeforePay(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				JSONObject jsonData = new JSONObject(data);

				if (jsonData.has("checkNo")) {
					JSONObject printableJson = receiptPrinter.printReceiptBeforePay(user.getName(), user.getStoreType(),
							jsonData.getString("checkNo"), false);

					if (printableJson.getString("response_code").equals("00")) {
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE,
								"Printing Failed. Please check your printer configuration.");
					}
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Check No Not Found");
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {

		}

		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/receiptKitchenSet" }, method = { RequestMethod.POST }, produces = "application/json")
	public String receiptKitchenSet(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
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

//				JSONObject terminal = new JSONObject(data);
//				String receiptKitchen = terminal.getString("receiptKitchen");
				String receiptKitchen = "receiptKitchen";
				
				stmt = connection
						.prepareStatement("select value from general_configuration where parameter = ?");
				stmt.setString(1, receiptKitchen);
				rs = stmt.executeQuery();

				if (rs.next()) {
					
					String value = rs.getString("value");
					String changeParam = "";
					
					if(value.equals("1")) {
						changeParam = "0";
					}else {
						changeParam = "1";
					}
					stmt2 = connection.prepareStatement(
							"update general_configuration set value = ? where parameter = ?");
					stmt2.setString(1, receiptKitchen);
					stmt2.setString(2, changeParam);
					
					int rs2 = stmt2.executeUpdate();
					
					if (rs2 > 0) {
						connection.commit();
						jsonResult.put("response_code", "00");
						jsonResult.put("response_message", "Kitchen Settings has been saved");
						Logger.writeActivity("Terminal Information has been saved", ECPOS_FOLDER);
					} else {
						connection.rollback();
						jsonResult.put("response_code", "01");
						jsonResult.put("response_message", "Kitchen Settings failed to save");
						Logger.writeActivity("Terminal Information failed to save", ECPOS_FOLDER);
					}
				} else {
					stmt2 = connection.prepareStatement(
							"insert into general_configuration (description,parameter,value) values (?,?,?);");
					stmt2.setString(1, "Kitchen Setting");
					stmt2.setString(2, "receiptKitchen");
					stmt2.setString(3, "1");
					
					int rs2 = stmt2.executeUpdate();

					if (rs2 > 0) {
						connection.commit();
						jsonResult.put("response_code", "00");
						jsonResult.put("response_message", "Kitchen Settings has been saved");
						Logger.writeActivity("Terminal Information has been saved", ECPOS_FOLDER);
					} else {
						connection.rollback();
						jsonResult.put("response_code", "01");
						jsonResult.put("response_message", "Kitchen Settings failed to save");
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
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/getPaymentMethod" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getPaymentMethod(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		String cash = "";
		String card = "";
		String ewallet = "";
		String staticqr = "";

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				String queryPaymentMethod = "select id, name, enable from payment_method";
				stmt = connection.prepareStatement(queryPaymentMethod);
				rs = stmt.executeQuery();
				
				while(rs.next()) {
					if(rs.getString("id").equalsIgnoreCase("1")) {
						cash = rs.getString("enable").equalsIgnoreCase("") ? "" : rs.getString("enable");
						jsonResult.put("cash", cash);
					}else if(rs.getString("id").equalsIgnoreCase("2")) {
						card = rs.getString("enable").equalsIgnoreCase("") ? "" : rs.getString("enable");
						jsonResult.put("card", card);
					}else if(rs.getString("id").equalsIgnoreCase("3")) {
						ewallet = rs.getString("enable").equalsIgnoreCase("") ? "" : rs.getString("enable");
						jsonResult.put("ewallet", ewallet);
					}else if(rs.getString("id").equalsIgnoreCase("4")) {
						staticqr = rs.getString("enable").equalsIgnoreCase("") ? "" : rs.getString("enable");
						jsonResult.put("staticqr", staticqr);
					}
				}

			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		System.out.println("result: "+jsonResult);
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/savePaymentMethod" }, method = { RequestMethod.POST }, produces = "application/json")
	public String savePaymentMethod(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		JSONObject result = new JSONObject();
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				JSONObject paymentMethod = new JSONObject(data);
				
				if(paymentMethod.has("cash") && paymentMethod.has("card") && paymentMethod.has("ewallet") && paymentMethod.has("staticqr")) {
					
					webComponent.updatePaymentMethod(connection, paymentMethod.getString("cash"), "1");
					webComponent.updatePaymentMethod(connection, paymentMethod.getString("card"), "2");
					webComponent.updatePaymentMethod(connection, paymentMethod.getString("ewallet"), "3");
					webComponent.updatePaymentMethod(connection, paymentMethod.getString("staticqr"), "4");

					resultCode = "00";
					resultMessage = "Success";

				}else {
					resultCode = "E02";
					resultMessage = "System Data Corrupted.";
				}
				
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
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
	
	@RequestMapping(value = { "/checkVoidPassword" }, method = { RequestMethod.POST }, produces = "application/json")
	public String checkVoidPassword(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) { 
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		String password = "";
		
		try {
			JSONObject jsonObj = new JSONObject(data);
			
			if (user != null) {
				connection = dataSource.getConnection();
				String queryPaymentMethod = "select value from general_configuration where parameter = 'VOID_PASSWORD'";
				stmt = connection.prepareStatement(queryPaymentMethod);
				rs = stmt.executeQuery();
				
				while(rs.next()) {
					password = rs.getString("value");
				}
				
				if(jsonObj.getString("data").equalsIgnoreCase(password)) {
					jsonResult.put(Constant.RESPONSE_CODE, "00");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Password Match");
				}else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Password Not Match");
				}

			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/checkStockPassword" }, method = { RequestMethod.POST }, produces = "application/json")
	public String checkStockPassword(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) { 
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		String password = "";
		
		try {
			JSONObject jsonObj = new JSONObject(data);
			
			if (user != null) {
				connection = dataSource.getConnection();
				String queryPaymentMethod = "select value from general_configuration where parameter = 'STOCK_PASSWORD'";
				stmt = connection.prepareStatement(queryPaymentMethod);
				rs = stmt.executeQuery();
				
				while(rs.next()) {
					password = rs.getString("value");
				}
				
				if(jsonObj.getString("data").equalsIgnoreCase(password)) {
					jsonResult.put(Constant.RESPONSE_CODE, "00");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Password Match");
				}else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Password Not Match");
				}

			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	//VernPOS Hotel part
	@RequestMapping(value = { "/get_roomtype_list" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json")
	public String getRoomTypelist(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				
				stmt = connection.prepareStatement("SELECT * FROM hotel_room_type order by id;");
				rs = stmt.executeQuery();

				boolean isEmpty = true;
				JSONArray roomTypeList = new JSONArray();
				JSONObject obj = null;
				while (rs.next()) {
					isEmpty = false;
					obj = new JSONObject();
					if(menuImagePath.length() == 12) {
						obj.put("image_path", menuImagePath + rs.getString("image_path"));
					}else {
						obj.put("image_path", menuImagePath.substring(55) + rs.getString("image_path"));
					}
					obj.put("id", rs.getLong("id"));
					obj.put("name", rs.getString("name"));
					
					roomTypeList.put(obj);
				}

				if (isEmpty) {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Room type info not found");
				} else {
					jsonResult.put(Constant.ROOMTYPE_LIST, roomTypeList);
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
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_room_list" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json")
	public String getRoomlist(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				
				boolean noStatus = true;
				String status_id = null;
				String type_id = null;
				StringBuilder sb = new StringBuilder();
				
				sb.append("SELECT a.*,b.name as 'room_status',b.bg_color as 'status_bg_color', "
						+ "c.name as 'room_category_name',d.check_number "
						+ "FROM table_setting a "
						+ "left join hotel_status_lookup b on a.status_lookup_id = b.id "
						+ "left join hotel_room_category_lookup c on a.hotel_room_category = c.id "
						+ "left join `check` d on a.id = d.table_number and d.check_status = 2 "
						+ "where store_id = 3 ");
				if (data.contains(",")) {
					String[] params = data.split(",");
					type_id = params[0];
					status_id = params[1];
					if (status_id != null && !status_id.equals("")) {
						noStatus = false;
						sb.append("and a.status_lookup_id = ? and a.hotel_room_type = ? ");
					} else {
						sb.append("and a.hotel_room_type = ? ");
					}
				} else {
					type_id = data;
					sb.append("and a.hotel_room_type = ? ");
				}
				sb.append("order by a.hotel_floor_no;");
				
				stmt = connection.prepareStatement(sb.toString());
				if (noStatus) {
					stmt.setString(1, type_id);
				} else {
					stmt.setString(1, status_id);
					stmt.setString(2, type_id);
				}
				rs = stmt.executeQuery();

				boolean isEmpty = true;
				String firstFloor = null;
				JSONArray roomList = new JSONArray();
				JSONObject obj = null;
				ArrayList<String> floorList = new ArrayList<String>();
				while (rs.next()) {
					if (isEmpty) {
						isEmpty = false;
						firstFloor = rs.getString("hotel_floor_no");
					}
					
					obj = new JSONObject();
					obj.put("id", Long.toString(rs.getLong("id")));
					obj.put("room_name", rs.getString("table_name"));
					obj.put("floor_no", rs.getString("hotel_floor_no"));
					obj.put("room_status_id", rs.getString("status_lookup_id"));
					obj.put("room_status", rs.getString("room_status"));
					obj.put("room_category_name", rs.getString("room_category_name"));
					obj.put("status_bg_color", rs.getString("status_bg_color"));
					obj.put("check_no", rs.getString("check_number"));

					floorList.add(rs.getString("hotel_floor_no"));
					roomList.put(obj);
				}
				Set<String> uniqueFloor = new HashSet<String>(floorList);

				if (isEmpty) {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Room info not found");
				} else {
					jsonResult.put(Constant.FLOOR_LIST, uniqueFloor);
					jsonResult.put(Constant.ROOM_LIST, roomList);
					jsonResult.put("first_floor_no", firstFloor);
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
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_room_status" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json")
	public String getRoomStatus(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
	
				stmt = connection.prepareStatement("select * from hotel_status_lookup "
						+ "order by id;");
				rs = stmt.executeQuery();
				
				JSONArray roomStatusList = new JSONArray();
				JSONObject obj = null;
				while (rs.next()) {
					obj = new JSONObject();
					obj.put("id", rs.getInt("id"));
					obj.put("room_status", rs.getString("name"));
					
					roomStatusList.put(obj);
				}
				jsonResult.put(Constant.ROOM_STATUS_LIST, roomStatusList);
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/update_room_status" }, method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json")
	public String updateRoomStatus(@RequestBody String jsonData, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				JSONObject jsonObj = new JSONObject(jsonData);
				connection = dataSource.getConnection();
				connection.setAutoCommit(false);
				
				String room_id = jsonObj.getString("roomId");
				//String action = jsonObj.getString("action");
	
				stmt = connection.prepareStatement("select * from table_setting where id = ?;");
				stmt.setString(1, room_id);
				rs = stmt.executeQuery();
				
				int roomNewStatus = 0;
				//if (action.equalsIgnoreCase("in")) {
					roomNewStatus = 1;
				//} else {
				//	roomNewStatus = 4;
				//}
				if (rs.next()) {
					stmt2 = connection.prepareStatement("update table_setting set status_lookup_id = ? where id = ?;");
					stmt2.setInt(1, roomNewStatus);
					stmt2.setString(2, room_id);
					int rs2 = stmt2.executeUpdate();
					
					if (rs2 > 0) {
						connection.commit();
						Logger.writeActivity("Room Status Successfully Updated", ECPOS_FOLDER);
						jsonResult.put(Constant.ROOM_STATUS_ID, roomNewStatus);
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Room Status Successfully Updated");
					} else {
						connection.rollback();
						Logger.writeActivity("Room Status Failed To Update", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Room Status Failed To Update");
					}
				} else {
					connection.rollback();
					Logger.writeActivity("Room ID Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Room ID Not Found");
				}
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)stmt.close();
				if (stmt2 != null)stmt2.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	public void cashDrawerMK410() {
		System.out.println("Keluar lah wahai MK410!!");
		byte[] open = {27,112,0,25,(byte) 250};
//      byte[] cutter = {29, 86,49};
        PrintService pservice = 
        PrintServiceLookup.lookupDefaultPrintService(); 
        DocPrintJob job = pservice.createPrintJob();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(open,flavor,null);
        PrintRequestAttributeSet aset = new 
        HashPrintRequestAttributeSet();
        try {
            job.print(doc, aset);
        } catch (PrintException ex) {
            System.out.println(ex.getMessage());
        }
	}
}
