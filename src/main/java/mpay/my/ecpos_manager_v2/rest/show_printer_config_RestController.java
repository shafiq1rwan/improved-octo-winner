package mpay.my.ecpos_manager_v2.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.xmlbeans.impl.util.Base64;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import mpay.my.ecpos_manager_v2.constant.Constant;
import mpay.my.ecpos_manager_v2.entity.PrinterDetailInfo;
import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;
import mpay.my.ecpos_manager_v2.webutil.UserAuthenticationModel;
import mpay.my.ecpos_manager_v2.webutil.UtilWebComponents;

@RestController
@RequestMapping("/printerapi")
public class show_printer_config_RestController {
	
	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String MPOP_PRINTER_PATH = "C:\\Users\\nicholas.foo\\source\\repos\\ECPOS_Printer_MPOP\\ECPOS_Printer_MPOP\\bin\\Release\\ECPOS_Printer_MPOP.exe ";
	private static final String PORT_INFO_LIST = "PortInfoList";
	private static final String PAPER_SIZE_LIST = "PaperSizeList";

	private static final String SELECTED_PORT_MODEL_NAME = "selectedPortModelName";
	private static final String SELECTED_PAPER_SIZE = "selectedPaperSize";
	private static final String SELECTED_PORT_NAME = "selectedPortName";

	@GetMapping(path = "/config_printer")
	public String getPrinterList(HttpServletRequest request) {
		JSONObject jsonResult = null;
		JSONObject jsonObj = null;
		try {
			jsonResult = new JSONObject();

			String getPrinterListParams[] = { MPOP_PRINTER_PATH, "1", "2", "2", "2", "2" };
			Process p1 = Runtime.getRuntime().exec(getPrinterListParams);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p1.getInputStream()));

			StringBuilder response = new StringBuilder();

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				response.append(s);
			}

			System.out.println("Printer Information: " + response.toString());

			jsonObj = new JSONObject(response.toString());

			if (jsonObj.has(PORT_INFO_LIST) && jsonObj.has(PAPER_SIZE_LIST)) {

				Map<String, Object> printerResultMap = jdbcTemplate.queryForMap("SELECT * FROM printer");
				String printerModel = (String) printerResultMap.get("printer_model");
				String portName = (String) printerResultMap.get("port_name");
				int index = 0;
				String portNameSend = "";

				JSONArray portInfoList = jsonObj.getJSONArray(PORT_INFO_LIST);
				JSONArray paperSizeList = jsonObj.getJSONArray(PAPER_SIZE_LIST);

				for (int i = 0; i < portInfoList.length(); i++) {
					JSONObject portItem = portInfoList.getJSONObject(i);
					System.out.println(portItem.toString());
					if (portItem.getJSONObject("PortInfo").getString("PortName").equals(portName)
							&& portItem.getString("ModelName").equals(printerModel)) {
						index = i;
					}
				}

				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
				jsonResult.put("portInfoIndex", index);
				jsonResult.put(PORT_INFO_LIST, portInfoList);
				jsonResult.put(PAPER_SIZE_LIST, paperSizeList);
				Logger.writeActivity("FIND PRINTERS SUCCESS", ECPOS_FOLDER);
			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "FAILURE WHEN RETRIEVING PRINTER DATA");
				Logger.writeActivity("FIND PRINTERS FAIL", ECPOS_FOLDER);
			}

		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		System.out.println(jsonResult.toString());
		return jsonResult.toString();
	}

	//Used in ecpos_manager_setting 
	@GetMapping("/retrieve_printer_data")
	public ResponseEntity<?> retrievePrinterData() {

		JSONObject jsonResult = null;
		JSONObject jsonObj = null;
		String html = "";
		JSONArray portInfoList = null;
		JSONArray paperSizeList = null;
		//boolean firstTimeSetup = false;
		Map<String, Object> selectedPrinterResultMap;

		try {
			jsonResult = new JSONObject();

			String getPrinterListParams[] = { MPOP_PRINTER_PATH, "1", "2", "2", "2", "2" };
			Process p1 = Runtime.getRuntime().exec(getPrinterListParams);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p1.getInputStream()));

			StringBuilder response = new StringBuilder();

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				response.append(s);
			}

			jsonObj = new JSONObject(response.toString());

			if (jsonObj.has(PORT_INFO_LIST)) {
				portInfoList = jsonObj.getJSONArray(PORT_INFO_LIST);
				paperSizeList = jsonObj.getJSONArray(PAPER_SIZE_LIST);
				// If data ady exist
				if (ifPrinterDataExist()) {
					// Remove button go here
					html = "<div class='form-group col-md-4 printerName'>"
							+ "<input id='selectedPrinterName' type='text' disabled class='form-control'/>"
							+ "<button class='col-md-2 form-control' ng-click='removePrinter()' type='button'>Remove</button>"
							+ "<div>";

					//firstTimeSetup = true;
					selectedPrinterResultMap = jdbcTemplate.queryForMap("SELECT * FROM printer");
					jsonResult.put("selectedPrinter", (String) selectedPrinterResultMap.get("port_name"));
				} else {
					// if data not appear, choose dropdown
					html = "<div class=\"form-group col-md-4 printerName\">"
							+ "				<label for=\"drop_down_list_printer_model\">Printer Model</label>"
							+ "		 		<select"
							+ "					class=\"form-control\" id=\"drop_down_list_printer_model\" ng-model=\"selectedPort\" required>"
							+ "					<option value=\"\" selected>-- Select --</option>"
							+ "					<option ng-repeat=\"info in printerConfigurationData.PortInfoList\" value=\"{{$index}}\">{{info.PortInfo.PortName}}</option>"
							+ "				</select>" + "			</div>";
					//firstTimeSetup = false;
				}

				jsonResult.put(PORT_INFO_LIST, portInfoList);
				jsonResult.put(PAPER_SIZE_LIST, paperSizeList);
				jsonResult.put("htmlElementString", html);
				//jsonResult.put("firstTimeSetup", firstTimeSetup);
				Logger.writeActivity("Available Printer Ports: " + portInfoList.toString(), ECPOS_FOLDER);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		return new ResponseEntity<>(jsonResult.toString(), HttpStatus.OK);
	}

	@PostMapping(path = "/save_printer_config")
	public String savePrinterConfig(@RequestBody String data) {

		JSONObject jsonResult = null;
		JSONObject jsonObj = null;

		try {

			jsonResult = new JSONObject();
			jsonObj = new JSONObject(data);
			if (jsonObj.has(SELECTED_PORT_MODEL_NAME) && jsonObj.has(SELECTED_PAPER_SIZE)
					&& jsonObj.has(SELECTED_PORT_NAME)) {

				String selectPortModelName = jsonObj.getString(SELECTED_PORT_MODEL_NAME);
				String selectPortName = jsonObj.getString(SELECTED_PORT_NAME);
				int selectPaperSizeIndex = jsonObj.getInt(SELECTED_PAPER_SIZE);

				System.out.println(selectPortModelName + "haha " + selectPaperSizeIndex);

				// Handle First time
				if (ifPrinterDataExist()) {

					int updatePrinterResult = jdbcTemplate.update(
							"UPDATE printer SET printer_model = ?, paper_size = ?, port_name = ?",
							new Object[] { selectPortModelName, selectPaperSizeIndex, selectPortName });

					if (updatePrinterResult == 1) {

						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "CANNOT UPDATE PRINTER DATA");
					}

				} else {

					int insertPrinterResult = jdbcTemplate.update(
							"INSERT INTO printer (printer_model, paper_size, port_name) VALUES (?, ?, ?)",
							new Object[] { selectPortModelName, selectPaperSizeIndex, selectPortName });

					System.out.println("Insertion Row Affected: " + insertPrinterResult);
					if (insertPrinterResult == 1) {
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
						Logger.writeActivity("SUCCESSFULLY SET ACTIVE PRINTER", ECPOS_FOLDER);
						
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "CANNOT SAVE PRINTER DATA");
						Logger.writeActivity("CANNOT SAVE ACTIVE PRINTER", ECPOS_FOLDER);	
					}
				}

			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "FAILURE WHEN RETRIEVING REQUEST DATA");
				Logger.writeActivity("FAILURE WHEN RETRIEVING REQUEST DATA", ECPOS_FOLDER);
			}

		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult.toString();
	}

	private boolean ifPrinterDataExist() {
		String printerInfo = "SELECT * FROM printer";
		try {
			Map<String, Object> printerResultMap = jdbcTemplate.queryForMap(printerInfo);
			if (printerResultMap.get("printer_model") != null) {
				return true; // Data Exist
			} else {
				return false; // Not Exist
			}
		} catch (DataAccessException ex) {
			return false; // Not Exist
		}
	}

	@PostMapping(path = "/open_cash_drawer", produces = "application/json")
	@ResponseBody
	public String openCashDrawer(HttpServletRequest request) {

		JSONObject jsonResult = null;
		JSONObject jsonObj = null;

		try {

			jsonResult = new JSONObject();

			PrinterDetailInfo printerResult;

			try {
				printerResult = (PrinterDetailInfo) jdbcTemplate.queryForObject(
						"SELECT printer_model, paper_size, port_name FROM printer",
						new BeanPropertyRowMapper(PrinterDetailInfo.class));
			} catch (EmptyResultDataAccessException ex) {
				printerResult = null;
			}

			if (printerResult != null) {
				String openDrawerParams[] = { MPOP_PRINTER_PATH, "2", printerResult.getPrinterModel(),
						Integer.toString(printerResult.getPaperSize()), printerResult.getPortName(), "2" };

				System.out.println("My Printer: " + printerResult.getPrinterModel());
				System.out.println("My Printer: " + Integer.toString(printerResult.getPaperSize()));
				System.out.println("My Printer: " + printerResult.getPortName());

				Process p = Runtime.getRuntime().exec(openDrawerParams);

				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

				StringBuilder response = new StringBuilder();

				String s = null;
				while ((s = stdInput.readLine()) != null) {
					response.append(s);
				}

				System.out.println("My Printer: " + response.toString());
				jsonObj = new JSONObject(response.toString());

				if (jsonObj.has("ResponseCode") && jsonObj.has("ResponseMessage")) {

					if (jsonObj.getInt("ResponseCode") == 1) {
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "ERROR WHILE OPEN CASH DRAWER");
					}

				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID REQUEST");
				}

			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "PRINTER CONFIGURATION DATA CANNOT BE FOUND!");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return jsonResult.toString();
	}

	@PostMapping(path = "/print_receipt")
	public ResponseEntity<String> printReceipt(HttpServletRequest request, @RequestBody String data) {

		JSONObject jsonResult = null;
		JSONObject jsonObj = null;
		JSONArray receiptItemResults = null;

		try {
			jsonResult = new JSONObject();

			int chk_seq = Integer.parseInt(data);

			UtilWebComponents webcomponent = new UtilWebComponents();
			UserAuthenticationModel session_container_user = webcomponent.getUserSession(request);

			String staff_name = null;

			if (session_container_user != null) {
				staff_name = session_container_user.getName();
			}

			Map<String, Object> resultSet = jdbcTemplate.queryForMap("SELECT * FROM checks WHERE chk_seq =?",
					new Object[] { chk_seq });

			String chk_no = (String) resultSet.get("chk_num");
			int tbl_no = (int) resultSet.get("tblno");
			int chk_open = (int) resultSet.get("chk_open");

			String recieptInfoQuery = "SELECT sum(amount) as total ,payment_type FROM transaction WHERE check_no = ? GROUP BY payment_type";
			Map<String, Object> receiptInfoResultSet = jdbcTemplate.queryForMap(recieptInfoQuery,
					new Object[] { chk_no });

			double total_amount = (double) receiptInfoResultSet.get("total");
			String payment_type = (String) receiptInfoResultSet.get("payment_type");

			String recieptItemQuery = "SELECT c.name, COUNT(c.name) as qty, (c.chk_ttl*count(c.name)) as ttl "
					+ "FROM checks b JOIN details c " + "ON b.chk_seq = c.chk_seq "
					+ "WHERE b.chk_seq = ? AND c.detail_item_status = 0 " + "AND c.detail_type = 'S' "
					+ "GROUP BY c.name";

			List<Map<String, Object>> recieptItems = jdbcTemplate.queryForList(recieptItemQuery,
					new Object[] { chk_seq });

			receiptItemResults = new JSONArray();

			for (Map<String, Object> recieptItem : recieptItems) {
				JSONObject jsonReceiptItem = new JSONObject();

				jsonReceiptItem.put("name", (String) recieptItem.get("name"));
				jsonReceiptItem.put("quantity", (long) recieptItem.get("qty"));
				BigDecimal receiptItemTotal = (BigDecimal) recieptItem.get("ttl");

				jsonReceiptItem.put("total", (double) receiptItemTotal.doubleValue());

				receiptItemResults.put(jsonReceiptItem);
			}

			jsonResult.put("invoice_no", chk_no);
			jsonResult.put("table_no", tbl_no);
			jsonResult.put("staff_name", staff_name);
			jsonResult.put("payment_type", payment_type);
			jsonResult.put("total_amt", total_amount);
			jsonResult.put("receipt_item_list", receiptItemResults);

			// Print
			PrinterDetailInfo printerResult;

			try {
				printerResult = (PrinterDetailInfo) jdbcTemplate.queryForObject(
						"SELECT printer_model, port_name FROM printer",
						new BeanPropertyRowMapper<PrinterDetailInfo>(PrinterDetailInfo.class));
			} catch (EmptyResultDataAccessException ex) {
				printerResult = null;
			}

			System.out.println("Print Printer " + printerResult.getPortName());

			String jsonData = new String(Base64.encode(jsonResult.toString().getBytes()));

			String printReceiptParams[] = { MPOP_PRINTER_PATH, "3", printerResult.getPrinterModel(), "0",
					printerResult.getPortName(), jsonData };

			Process p = Runtime.getRuntime().exec(printReceiptParams);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

			StringBuilder response = new StringBuilder();

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				response.append(s);
			}
			System.out.println("Print Receipt: " + jsonResult.toString());
			System.out.println("Print Receipt Status: " + response.toString());
			jsonObj = new JSONObject(response.toString());

			if (jsonObj.has("ResponseCode") && jsonObj.has("ResponseMessage")) {

				if (jsonObj.getInt("ResponseCode") == 1) {

				} else {

				}

			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

		} catch (JSONException | DataAccessException ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
	}

	//Used at payment RestController
	@PostMapping("/print_tranx_receipt")
	public ResponseEntity<String> printTransactionReceipt(HttpServletRequest request, @RequestBody String data) {

		JSONObject jsonResult = null;
		JSONObject jsonObj = null;
		JSONArray receiptItemResults = null;

		
		String address = "Star Clothing Boutique\n" + "123 Star Road\n" + "City, State 12345\n";

		try {
			jsonResult = new JSONObject();
			jsonObj = new JSONObject(data);

			UtilWebComponents webcomponent = new UtilWebComponents();
			UserAuthenticationModel session_container_user = webcomponent.getUserSession(request);

			String staff_name = null;

			if (session_container_user != null) {
				staff_name = session_container_user.getName();
			}

			Map<String, Object> resultSet = jdbcTemplate.queryForMap("SELECT * FROM checks WHERE chk_num =?",
					new Object[] { jsonObj.getString("checkNumber") });

			int tbl_no = (int) resultSet.get("tblno");
			int chk_open = (int) resultSet.get("chk_open");

			String recieptInfoQuery = "SELECT sum(amount) as total FROM transaction WHERE check_no = ? GROUP BY payment_type";
			Map<String, Object> receiptInfoResultSet = jdbcTemplate.queryForMap(recieptInfoQuery,
					new Object[] { jsonObj.getString("checkNumber") });

			BigDecimal total_amount = new BigDecimal((double) receiptInfoResultSet.get("total"));

			String recieptItemQuery = "SELECT c.name, COUNT(c.name) as qty, (c.detail_item_price *count(c.name)) as ttl "
					+ "FROM checks b JOIN details c " + "ON b.chk_seq = c.chk_seq "
					+ "WHERE b.chk_seq = ? AND c.detail_item_status = 0 " + "AND c.detail_type = 'S' "
					+ "GROUP BY c.name";

			List<Map<String, Object>> recieptItems = jdbcTemplate.queryForList(recieptItemQuery,
					new Object[] { (long) resultSet.get("chk_seq") });

			receiptItemResults = new JSONArray();

			for (Map<String, Object> recieptItem : recieptItems) {
				JSONObject jsonReceiptItem = new JSONObject();

				jsonReceiptItem.put("name", (String) recieptItem.get("name"));
				jsonReceiptItem.put("quantity", (long) recieptItem.get("qty"));
				BigDecimal receiptItemTotal = (BigDecimal) recieptItem.get("ttl");

				jsonReceiptItem.put("total", (double) receiptItemTotal.doubleValue());

				receiptItemResults.put(jsonReceiptItem);
			}

			BigDecimal sst = BigDecimal.ZERO;
			BigDecimal serviceTax = (BigDecimal) resultSet.get("service_tax");
			BigDecimal salesTax = (BigDecimal) resultSet.get("sales_tax");
			sst = serviceTax.add(salesTax);

			jsonResult.put("address", address);
			jsonResult.put("invoice_no", jsonObj.getString("checkNumber"));
			jsonResult.put("table_no", tbl_no);
			jsonResult.put("staff_name", staff_name);
			jsonResult.put("total_amt", total_amount);
			jsonResult.put("payment_type", "");
			jsonResult.put("receipt_item_list", receiptItemResults);
			jsonResult.put("sst", sst);

			System.out.println(jsonResult.toString());

			// Perform Printing
			PrinterDetailInfo printerResult;

			try {
				printerResult = (PrinterDetailInfo) jdbcTemplate.queryForObject(
						"SELECT printer_model, port_name FROM printer",
						new BeanPropertyRowMapper<PrinterDetailInfo>(PrinterDetailInfo.class));
			} catch (EmptyResultDataAccessException ex) {
				printerResult = null;
			}

			String jsonData = new String(Base64.encode(jsonResult.toString().getBytes()));
			System.out.println(jsonData.toString());

			String printReceiptParams[] = { MPOP_PRINTER_PATH, "3", printerResult.getPrinterModel(), "0",
					printerResult.getPortName(), jsonData };

			Process p = Runtime.getRuntime().exec(printReceiptParams);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

			StringBuilder response = new StringBuilder();

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				response.append(s);
			}

			System.out.println(response.toString());
			jsonObj = new JSONObject(response.toString());

			if (jsonObj.has("ResponseCode") && jsonObj.has("ResponseMessage")) {
				if (jsonObj.getInt("ResponseCode") == 1) {
					System.out.println("Success Printing Transaction Receipt");
					Logger.writeActivity("Success Printing", ECPOS_FOLDER);
				} else {
					System.out.println("Please Check Your Printer Setting!");
					Logger.writeActivity("Fail Printing", ECPOS_FOLDER);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
	}

	@PostMapping("/printReceipt")
	public ResponseEntity<String> printDisplayReceipt(HttpServletRequest request, @RequestBody String data) {

		JSONObject jsonResult = new JSONObject();
		JSONObject jsonPrinterResponse = null;
		String paymentTypeName = "";
		JSONArray receiptItemResults = null;

		try {
			JSONObject jsonData = new JSONObject(data);
			// get check result
			String checkNum = jsonData.getString("checkNumber");

			// Retrieve user name
			UtilWebComponents webcomponent = new UtilWebComponents();
			UserAuthenticationModel session_container_user = webcomponent.getUserSession(request);

			String staff_name = null;

			if (session_container_user != null) {
				staff_name = session_container_user.getName();
			}

			// Query for check info
			Map<String, Object> checkResultMap = jdbcTemplate.queryForMap("SELECT * FROM checks WHERE chk_num = ?",
					new Object[] { checkNum });

			int tbl_no = (int) checkResultMap.get("tblno");
			int chk_open = (int) checkResultMap.get("chk_open");

			String recieptInfoQuery = "SELECT payment_type FROM transaction WHERE check_no = ?";
			List<Map<String, Object>> receiptInfoResultMaps = jdbcTemplate.queryForList(recieptInfoQuery,
					new Object[] { checkNum });

			// If more than 2
			if (receiptInfoResultMaps.size() > 1) {
				paymentTypeName = "Split Payment";
			}

			String recieptItemQuery = "SELECT c.name, COUNT(c.name) as qty, (c.chk_ttl*count(c.name)) as ttl "
					+ "FROM checks b JOIN details c " + "ON b.chk_seq = c.chk_seq "
					+ "WHERE b.chk_seq = ? AND c.detail_item_status = 0 " + "AND c.detail_type = 'S' "
					+ "GROUP BY c.name";

			List<Map<String, Object>> receiptItemResultItems = jdbcTemplate.queryForList(recieptItemQuery,
					new Object[] { (int) checkResultMap.get("chk_seq") });

			// Split into multiple payment division;
			receiptItemResults = new JSONArray();

			for (Map<String, Object> recieptItem : receiptItemResultItems) {
				JSONObject jsonReceiptItem = new JSONObject();

				jsonReceiptItem.put("name", (String) recieptItem.get("name"));
				jsonReceiptItem.put("quantity", (long) recieptItem.get("qty"));
				BigDecimal receiptItemTotal = (BigDecimal) recieptItem.get("ttl");

				jsonReceiptItem.put("total", (double) receiptItemTotal.doubleValue());

				receiptItemResults.put(jsonReceiptItem);
			}

			jsonResult.put("invoice_no", checkNum);
			jsonResult.put("table_no", tbl_no);
			jsonResult.put("staff_name", staff_name);
			jsonResult.put("payment_type", paymentTypeName);
			jsonResult.put("total_amt", (BigDecimal) checkResultMap.get("sub_ttl"));
			jsonResult.put("payment_ttl", (BigDecimal) checkResultMap.get("pymnt_ttl"));
			jsonResult.put("sales_tax", (BigDecimal) checkResultMap.get("sales_tax"));
			jsonResult.put("service_tax", (BigDecimal) checkResultMap.get("service_tax"));
			jsonResult.put("service_tax", (BigDecimal) checkResultMap.get("due_ttl"));

			jsonResult.put("receipt_item_list", receiptItemResults);
			
			if(jsonData.has("cardResponse")) {
				JSONObject jsonCardResponse = new JSONObject(jsonData.getString("cardResponse"));
				jsonResult.put("card_trax_data", jsonCardResponse);
			}

			// Get the printer working
			PrinterDetailInfo printerResult;

			try {
				printerResult = (PrinterDetailInfo) jdbcTemplate.queryForObject(
						"SELECT printer_model, port_name FROM printer",
						new BeanPropertyRowMapper<PrinterDetailInfo>(PrinterDetailInfo.class));
			} catch (EmptyResultDataAccessException ex) {
				printerResult = null;
			}

			System.out.println("Print Printer " + printerResult.getPortName());

			String encodedData = new String(Base64.encode(jsonResult.toString().getBytes()));

			String printReceiptParams[] = { MPOP_PRINTER_PATH, "3", printerResult.getPrinterModel(), "0",
					printerResult.getPortName(), encodedData };

			Process p = Runtime.getRuntime().exec(printReceiptParams);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

			StringBuilder response = new StringBuilder();

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				response.append(s);
			}

			jsonPrinterResponse = new JSONObject(response.toString());

			if (jsonPrinterResponse.has("ResponseCode") && jsonPrinterResponse.has("ResponseMessage")) {
				if (jsonPrinterResponse.getInt("ResponseCode") != 1) {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
	}

	@PostMapping("printkitchenreceipt")
	public ResponseEntity<String> printKitchenReceipt(@RequestBody String data, HttpServletRequest request) {

		String newReceiptItemQuerySql = "SELECT name, count(name) AS 'qty' FROM details WHERE chk_seq = :chkSeq AND detail_item_status = 0 "
				+ " AND id IN (:itemIds) GROUP BY name";

		try {
			JSONObject jsonResult = new JSONObject();
			JSONObject jsonObj = new JSONObject(data);
			String checkNo = jsonObj.getString("checkNo");

			Map<String, Object> checkResultMap = jdbcTemplate.queryForMap("SELECT * FROM checks WHERE chk_num =?",
					new Object[] { checkNo });

			JSONArray kitchenItemsList = jsonObj.getJSONArray("kitchenReceipt");

			if (kitchenItemsList == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			List<Map<String, Object>> receiptItemResultMaps = new ArrayList<Map<String, Object>>();

			if (kitchenItemsList.length() > 0) {

				long[] itemIds = new long[kitchenItemsList.length()];
				for (int i = 0; i < kitchenItemsList.length(); ++i) {
					itemIds[i] = kitchenItemsList.optInt(i);
					System.out.println("PrinterItem: " + itemIds[i]);
				}

				List<Long> itemIdsList = Arrays.stream(itemIds).boxed().collect(Collectors.toList());
				System.out.println(itemIdsList.toString());

				MapSqlParameterSource parameters = new MapSqlParameterSource();
				parameters.addValue("chkSeq", (long) checkResultMap.get("chk_seq"));
				parameters.addValue("itemIds", itemIdsList);

				try {
					receiptItemResultMaps = namedParameterJdbcTemplate.queryForList(newReceiptItemQuerySql, parameters);
				} catch (DataAccessException ex) {
					receiptItemResultMaps = Collections.emptyList();
				}

				// for (long item : itemIds) {
				// try {
				// Map<String, Object> itemResultMap =
				// jdbcTemplate.queryForMap(newReceiptItemQuerySql,
				// new Object[] { item });
				//
				// receiptItemResultMaps.add(itemResultMap);
				// } catch (DataAccessException ex) {
				// receiptItemResultMaps = Collections.emptyList();
				// }
				// }
			}

			// List<Map<String, Object>> receiptItemResultMaps;
			// try{
			// receiptItemResultMaps = jdbcTemplate.queryForList(recieptItemQuerySql,
			// new Object[] {(long)checkResultMap.get("chk_seq")});
			// } catch(DataAccessException ex) {
			// receiptItemResultMaps = Collections.emptyList();
			// }

			JSONArray jsonArray = new JSONArray();

			for (Map<String, Object> map : receiptItemResultMaps) {
				JSONObject jsonItem = new JSONObject();
				jsonItem.put("name", (String) map.get("name"));
				jsonItem.put("quantity", (long) map.get("qty"));

				System.out.println((String) map.get("name"));

				jsonArray.put(jsonItem);
			}

			jsonResult.put("invoice_no", checkNo);
			jsonResult.put("table_no", (int) checkResultMap.get("tblno"));
			jsonResult.put("receipt_item_list", jsonArray);

			System.out.println(jsonResult.toString());

			PrinterDetailInfo printerResult;
			try {
				printerResult = (PrinterDetailInfo) jdbcTemplate.queryForObject(
						"SELECT printer_model, port_name FROM printer",
						new BeanPropertyRowMapper<PrinterDetailInfo>(PrinterDetailInfo.class));
			} catch (EmptyResultDataAccessException ex) {
				printerResult = null;
			}

			if (!receiptItemResultMaps.isEmpty()) {

				String jsonData = new String(Base64.encode(jsonResult.toString().getBytes()));

				String printReceiptParams[] = { MPOP_PRINTER_PATH, "4", printerResult.getPrinterModel(), "0",
						printerResult.getPortName(), jsonData };

				Process p = Runtime.getRuntime().exec(printReceiptParams);

				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	//Important and real
	@PostMapping("/printKitchenReceipt")
	public String printKitchenDisplayReceipt(@RequestBody String data, HttpServletRequest request) {

		JSONObject jsonResult = new JSONObject();
		JSONObject requestData = null;
		String SELECT_RECEIPT_SQL = "SELECT name, count(name) AS 'qty' FROM details WHERE chk_seq = ? AND detail_item_status = 0 AND detail_type = 'S' GROUP BY name";

		try {
			JSONObject jsonData = new JSONObject(data);
			if (jsonData.has("checkNumber")) {
				
				Map<String, Object> checkResultMap = jdbcTemplate.queryForMap("SELECT * FROM checks WHERE chk_num =?",
						new Object[] { jsonData.getString("checkNumber") });

				List<Map<String, Object>> detailtemResultMaps = jdbcTemplate.queryForList(SELECT_RECEIPT_SQL,
						new Object[] { (long)checkResultMap.get("chk_seq") });
				
				JSONArray jsonArray = new JSONArray();
				
				for(Map<String,Object> detailItemMap: detailtemResultMaps) {
					JSONObject detailItem = new JSONObject();
					detailItem.put("name", (String) detailItemMap.get("name"));
					detailItem.put("quantity", (long) detailItemMap.get("qty"));
					jsonArray.put(detailItem);
				}
				
				//data to be printed
				requestData = new JSONObject();
				requestData.put("invoice_no", jsonData.getString("checkNumber"));
				requestData.put("table_no", (int) checkResultMap.get("tblno"));
				requestData.put("receipt_item_list", jsonArray);
				
				//Retrieve Printer Data
				PrinterDetailInfo printerResult;
				try {
					printerResult = (PrinterDetailInfo) jdbcTemplate.queryForObject(
							"SELECT printer_model, port_name FROM printer",
							new BeanPropertyRowMapper<PrinterDetailInfo>(PrinterDetailInfo.class));
				} catch (EmptyResultDataAccessException ex) {
					printerResult = null;
				}
				
				if(printerResult!= null) {
					String printerJsonData = new String(Base64.encode(requestData.toString().getBytes()));
					String printReceiptParams[] = { MPOP_PRINTER_PATH, "4", printerResult.getPrinterModel(), "0",
								printerResult.getPortName(), printerJsonData };
					Process p = Runtime.getRuntime().exec(printReceiptParams);
								
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
					StringBuilder response = new StringBuilder();
					String s = null;
					while ((s = stdInput.readLine()) != null) {
						response.append(s);
					}

					JSONObject jsonPrinterResponse = new JSONObject(response.toString());

					if (jsonPrinterResponse.has("ResponseCode") && jsonPrinterResponse.has("ResponseMessage")) {
						if (jsonPrinterResponse.getInt("ResponseCode") != 1) {
							jsonResult.put(Constant.RESPONSE_CODE, "00");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
							Logger.writeActivity("Receipt :"+ jsonData.getString("checkNumber") +" had been printed", ECPOS_FOLDER);
						}
						else {
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "FAILED");	
						}
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "RESPONSE NOT FOUND");
					}
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "PRINTER NOT FOUND");
				}
			}

		}
		catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}

		return jsonResult.toString();
	}

	//used in ecpos_manager_setting_ctrl
	@PostMapping("/remove_printer_config")
	public ResponseEntity<?> removePrinterConfig(@RequestBody String data) {

		// JSONObject jsonResult = null;
		String removePrinterModelSql = "DELETE FROM printer WHERE port_name = ?";

		try {
			System.out.println(data);

			JSONObject jsonData = new JSONObject(data);
			if (!jsonData.has("printerName"))
			{
				Logger.writeActivity("Printer Not Found", ECPOS_FOLDER);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			jdbcTemplate.update(removePrinterModelSql, new Object[] { jsonData.getString("printerName") });
			Logger.writeActivity("Printer Deselected", ECPOS_FOLDER);
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
