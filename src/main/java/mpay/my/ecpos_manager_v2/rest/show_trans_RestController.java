package mpay.my.ecpos_manager_v2.rest;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import mpay.my.ecpos_manager_v2.constant.Constant;
import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;

@RestController
@RequestMapping("/memberapi/show_trans")
public class show_trans_RestController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();

	private static final String SELECT_TRANSACTION_USING_DATE_SQL = "SELECT a.*, DATE_FORMAT(a.tran_datetime,'%r') as tran_time, "
			+ "b.name, CONCAT('Table ',c.tblno,' - Check ',c.chk_num) as description, " + "c.sub_ttl as detail_amount "
			+ "FROM transaction a JOIN empldef b ON a.performBy = b.id " + "JOIN checks c ON c.chk_num = a.check_no "
			+ "JOIN details d ON d.chk_seq = c.chk_seq " + "WHERE a.tran_datetime >= ? " + "AND c.voidable = 0 "
			+ "AND c.chk_open = 3 " + "AND d.detail_item_status = 0 AND d.detail_type != 'T' " + "GROUP BY a.tran_id "
			+ "ORDER BY tran_datetime DESC";

	// Not Ready
/*	@RequestMapping(value = { "/get_tran_history" }, method = { RequestMethod.GET }, produces = "application/json")
	@ResponseBody
	public String getTranHistory(HttpServletRequest request) {

		JSONObject jsonResult = null;
		Connection connection = null;

		try {
			jsonResult = new JSONObject();
			jsonResult.put(Constant.RESPONSE_CODE, "00");
			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
			JSONArray tran_list = new JSONArray();
			connection = dataSource.getConnection();

			PreparedStatement stmt = connection.prepareStatement(
					"SELECT a.*, b.tblno AS table_no FROM transaction a " + "JOIN checks b ON a.check_no = b.chk_num "
							+ "WHERE tran_datetime >  DATE_SUB(CURDATE(), INTERVAL 1 WEEK);");

			ResultSet resultSet = (ResultSet) stmt.executeQuery();
			while (resultSet.next()) {
				JSONObject tran = new JSONObject();
				tran.put(Constant.AMOUNT, resultSet.getString(Constant.AMOUNT));
				tran.put(Constant.TABLE_NO, resultSet.getString(Constant.TABLE_NO));
				tran.put(Constant.CHECK_NO, resultSet.getString(Constant.CHECK_NO));
				tran.put(Constant.TRAN_TYPE, resultSet.getString(Constant.TRAN_TYPE));
				tran.put(Constant.MTRX_ID, resultSet.getString(Constant.MTRX_ID));
				tran.put(Constant.TRAN_STATUS, resultSet.getString(Constant.TRAN_STATUS));
				tran.put(Constant.AUTH_CODE, resultSet.getString(Constant.AUTH_CODE));
				tran.put(Constant.PAYMENT_TYPE, resultSet.getString(Constant.PAYMENT_TYPE));
				tran.put(Constant.TRAN_DATETIME, resultSet.getString(Constant.TRAN_DATETIME));
				tran.put(Constant.TRACE_NO, resultSet.getString(Constant.TRACE_NO));
				tran.put(Constant.BATCH_NO, resultSet.getString(Constant.BATCH_NO));
				tran.put(Constant.BANK_MID, resultSet.getString(Constant.BANK_MID));
				tran.put(Constant.BANK_TID, resultSet.getString(Constant.BANK_MID));
				tran.put(Constant.AID, resultSet.getString(Constant.AID));
				tran.put(Constant.APP_LABEL, resultSet.getString(Constant.APP_LABEL));
				tran.put(Constant.MASKED_CARDNO, resultSet.getString(Constant.MASKED_CARDNO));
				tran.put(Constant.CARDHOLDER_NAME, resultSet.getString(Constant.CARDHOLDER_NAME));

				tran_list.put(tran);
			}
			jsonResult.put(Constant.TRAN_LIST, tran_list);
		} catch (Exception e) {
			e.printStackTrace();
			return Constant.EXCEPTION_MESSAGE;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return jsonResult.toString();

	}*/

	// Not Ready
	/*
	 * @RequestMapping(value = { "/sync_transaction" }, method = {
	 * RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	 * 
	 * @ResponseBody public String syncTransaction(HttpServletRequest
	 * request, @RequestBody String data) {
	 * 
	 * JSONObject jsonObj = null; JSONObject jsonResult = null; Connection
	 * connection = null; PreparedStatement stmt = null;
	 * 
	 * try{ jsonResult = new JSONObject(); jsonObj = new JSONObject(data);
	 * connection = dataSource.getConnection(); if(jsonObj.has(Constant.CHECK_NO) &&
	 * jsonObj.has(Constant.TRAN_TYPE) && jsonObj.has(Constant.TRAN_STATUS) &&
	 * jsonObj.has(Constant.PAYMENT_TYPE) && jsonObj.has(Constant.TRAN_DATETIME) &&
	 * jsonObj.has(Constant.AMOUNT)){ String tran_type =
	 * jsonObj.getString(Constant.TRAN_TYPE); String tran_datetime =
	 * jsonObj.getString(Constant.TRAN_DATETIME); DateFormat dateFormat = new
	 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); tran_datetime =
	 * dateFormat.format(dateFormat.parse(tran_datetime));
	 * if(tran_type.equals("CARD")) { if(jsonObj.has(Constant.MTRX_ID) &&
	 * jsonObj.has(Constant.AUTH_CODE) && jsonObj.has(Constant.TRACE_NO) &&
	 * jsonObj.has(Constant.BATCH_NO) && jsonObj.has(Constant.BANK_MID) &&
	 * jsonObj.has(Constant.BANK_TID) && jsonObj.has(Constant.AID) &&
	 * jsonObj.has(Constant.APP_LABEL) && jsonObj.has(Constant.MASKED_CARDNO) &&
	 * jsonObj.has(Constant.CARDHOLDER_NAME)) { stmt =
	 * connection.prepareStatement("INSERT INTO transaction (check_no, tran_type, "
	 * + "tran_status, tran_datetime, payment_type, " +
	 * "mtrx_id, auth_code, trace_no, batch_no, " +
	 * "bank_mid, bank_tid, aid, app_label, masked_cardno, cardholder_name, amount) "
	 * + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"); stmt.setString(1,
	 * jsonObj.getString(Constant.CHECK_NO)); stmt.setString(2,
	 * jsonObj.getString(Constant.TRAN_TYPE)); stmt.setString(3,
	 * jsonObj.getString(Constant.TRAN_STATUS)); stmt.setString(4, tran_datetime);
	 * stmt.setString(5, jsonObj.getString(Constant.PAYMENT_TYPE));
	 * stmt.setString(6, jsonObj.getString(Constant.MTRX_ID)); stmt.setString(7,
	 * jsonObj.getString(Constant.AUTH_CODE)); stmt.setString(8,
	 * jsonObj.getString(Constant.TRACE_NO)); stmt.setString(9,
	 * jsonObj.getString(Constant.BATCH_NO)); stmt.setString(10,
	 * jsonObj.getString(Constant.BANK_MID)); stmt.setString(11,
	 * jsonObj.getString(Constant.BANK_TID)); stmt.setString(12,
	 * jsonObj.getString(Constant.AID)); stmt.setString(13,
	 * jsonObj.getString(Constant.APP_LABEL)); stmt.setString(14,
	 * jsonObj.getString(Constant.MASKED_CARDNO)); stmt.setString(15,
	 * jsonObj.getString(Constant.CARDHOLDER_NAME)); stmt.setString(16,
	 * jsonObj.getString(Constant.AMOUNT)); stmt.executeUpdate(); PreparedStatement
	 * updateSTMT = connection.
	 * prepareStatement("UPDATE checks SET chk_open = 3 WHERE chk_num = ?");
	 * updateSTMT.setString(1, jsonObj.getString(Constant.CHECK_NO));
	 * updateSTMT.executeUpdate(); jsonResult.put(Constant.RESPONSE_CODE, "00");
	 * jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS"); } else{
	 * jsonResult.put(Constant.RESPONSE_CODE, "01");
	 * jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID REQUEST"); } } else { stmt
	 * =
	 * connection.prepareStatement("INSERT INTO transaction (check_no, tran_type, "
	 * + "tran_status, tran_datetime, payment_type, amount) VALUES (?,?,?,?,?,?)");
	 * stmt.setString(1, jsonObj.getString(Constant.CHECK_NO)); stmt.setString(2,
	 * jsonObj.getString(Constant.TRAN_TYPE)); stmt.setString(3,
	 * jsonObj.getString(Constant.TRAN_STATUS)); stmt.setString(4, tran_datetime);
	 * stmt.setString(5, jsonObj.getString(Constant.PAYMENT_TYPE));
	 * stmt.setString(6, jsonObj.getString(Constant.AMOUNT)); stmt.executeUpdate();
	 * PreparedStatement updateSTMT = connection.
	 * prepareStatement("UPDATE checks SET chk_open = 3 WHERE chk_num = ?");
	 * updateSTMT.setString(1, jsonObj.getString(Constant.CHECK_NO));
	 * updateSTMT.executeUpdate(); jsonResult.put(Constant.RESPONSE_CODE, "00");
	 * jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS"); } } else {
	 * jsonResult.put(Constant.RESPONSE_CODE, "01");
	 * jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID REQUEST"); }
	 * 
	 * } catch(Exception e){ e.printStackTrace(); } finally{ if(connection != null)
	 * { try { connection.close(); }catch (Exception e) { e.printStackTrace(); } } }
	 * 
	 * return jsonResult.toString();
	 * 
	 * }
	 */

	//Success
	@RequestMapping(value = { "/get_transaction_list" }, method = { RequestMethod.GET }, produces = "application/json")
	@ResponseBody
	public String getTransactionList(HttpServletRequest request) {

		JSONObject jsonResult = null;
		JSONArray trans_list = null;
		Connection connection = null;

		try {
			jsonResult = new JSONObject();
			jsonResult.put(Constant.RESPONSE_CODE, "00");
			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");

			connection = dataSource.getConnection();

			PreparedStatement stmt = connection
					.prepareStatement("SELECT a.*, DATE_FORMAT(a.tran_datetime,'%r') as tran_time, "
							+ "b.name, CONCAT('Table ',c.tblno,' - Check ',c.chk_num) as description, "
							+ "c.sub_ttl as detail_amount " + "FROM transaction a JOIN empldef b ON a.performBy = b.id "
							+ "JOIN checks c ON c.chk_num = a.check_no " + "JOIN details d ON d.chk_seq = c.chk_seq "
							+ "WHERE c.voidable = 0 " + "AND c.chk_open = 3 "
							+ "AND d.detail_item_status = 0 AND d.detail_type != 'T' " + "GROUP BY a.tran_id "
							+ "ORDER BY tran_datetime DESC;");
			ResultSet resultSet = (ResultSet) stmt.executeQuery();

			trans_list = new JSONArray();

			while (resultSet.next()) {
				JSONObject jsonObj = new JSONObject();

				jsonObj.put("tran_id", resultSet.getString("tran_id"));
				jsonObj.put("tran_time", resultSet.getString("tran_time"));
				jsonObj.put("name", resultSet.getString("name"));
				jsonObj.put("description", resultSet.getString("description"));
				jsonObj.put("amount", resultSet.getString("detail_amount"));

				trans_list.put(jsonObj);
			}

			jsonResult.put("trans_list", trans_list);
			Logger.writeActivity("Transaction List: " + jsonResult.toString(), ECPOS_FOLDER);

		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
					e.printStackTrace();
				}
			}
		}

		return jsonResult.toString();
	}

	//Success
	@RequestMapping(value = { "/get_transaction_details/{tran_id}" }, method = {
			RequestMethod.GET }, produces = "application/json")
	@ResponseBody
	public String getTransactionDetails(@PathVariable("tran_id") String tran_id, HttpServletRequest request) {

		System.out.println("Transaction Details with Id :" + tran_id);
		Logger.writeActivity("Transaction Details with Id :" + tran_id, ECPOS_FOLDER);
		
		JSONObject jsonResult = null;
		JSONArray detail_list = null;
		Connection connection = null;

		try {

			jsonResult = new JSONObject();
			jsonResult.put(Constant.RESPONSE_CODE, "00");
			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");

			connection = dataSource.getConnection();

			/*
			 * PreparedStatement stmt = connection.
			 * prepareStatement("SELECT a.tran_datetime, a.payment_type, b.chk_num, b.tblno, "
			 * + "SUM(d.chk_ttl) as detail_amount " + "FROM transaction a " +
			 * "JOIN checks b ON a.check_no = b.chk_num " +
			 * "JOIN details d ON d.chk_seq = b.chk_seq " + "WHERE a.tran_id = ? " +
			 * "AND d.detail_type != 'T' AND d.detail_item_status = 0 " +
			 * "GROUP BY a.tran_id;");
			 */

			PreparedStatement stmt = connection
					.prepareStatement("SELECT a.tran_id, a.tran_datetime, a.payment_type, b.chk_num, b.tblno, "
							+ "b.sub_ttl as detail_amount, " + "b.tax_ttl as tax " + "FROM transaction a "
							+ "JOIN checks b ON a.check_no = b.chk_num " + "JOIN details d ON d.chk_seq = b.chk_seq "
							+ "WHERE a.tran_id = ? " + "AND d.detail_type != 'T' AND d.detail_item_status = 0 "
							+ "GROUP BY a.tran_id;");

			stmt.setString(1, tran_id);

			ResultSet resultSet = (ResultSet) stmt.executeQuery();

			while (resultSet.next()) {

				jsonResult.put("tran_datetime", resultSet.getString("tran_datetime"));
				jsonResult.put("payment_type", resultSet.getString("payment_type"));
				jsonResult.put("chk_num", resultSet.getString("chk_num"));
				jsonResult.put("transactionId", resultSet.getString("tran_id"));
				jsonResult.put("tblno", resultSet.getString("tblno"));
				jsonResult.put("amount", resultSet.getString("detail_amount"));
				jsonResult.put("tax", resultSet.getString("tax"));

			}

			PreparedStatement stmt2 = connection
					.prepareStatement("SELECT c.name, count(c.name) as qty, (c.chk_ttl*count(c.name)) as ttl "
							+ "FROM transaction a " + "JOIN checks b ON a.check_no = b.chk_num "
							+ "JOIN details c ON b.chk_seq = c.chk_seq " + "WHERE a.tran_id = ? "
							+ "AND c.detail_type != 'T' " + "AND c.detail_item_status = 0 " + "GROUP BY c.name; ");
			stmt2.setString(1, tran_id);
			ResultSet secondResultSet = (ResultSet) stmt2.executeQuery();
			detail_list = new JSONArray();

			while (secondResultSet.next()) {
				JSONObject itemList = new JSONObject();
				itemList.put("name", secondResultSet.getString("name"));
				itemList.put("qty", secondResultSet.getString("qty"));
				itemList.put("ttl", secondResultSet.getString("ttl"));

				detail_list.put(itemList);
			}
			jsonResult.put("detail_list", detail_list);
			Logger.writeActivity("Transaction Details: " + jsonResult.toString(), ECPOS_FOLDER);

		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
					e.printStackTrace();
				}
			}
		}
		return jsonResult.toString();
	}

	/*
	 * @RequestMapping(value = { "/doom" }, method = { RequestMethod.POST}, produces
	 * = "application/json")
	 * 
	 * @ResponseBody public String getDate(HttpServletRequest request,@RequestBody
	 * String data) {
	 * 
	 * JSONObject jsonResult = null; JSONArray detail_list = null; Connection
	 * connection = null;
	 * 
	 * System.out.println("Hello you"); System.out.println(data);
	 * 
	 * try {
	 * 
	 * jsonResult = new JSONObject(); jsonResult.put(Constant.RESPONSE_CODE, "00");
	 * jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
	 * 
	 * 
	 * }catch(Exception e) { e.printStackTrace(); } finally { if(connection != null)
	 * { try { connection.close(); }catch (Exception e) { e.printStackTrace(); } } }
	 * 
	 * return jsonResult.toString();
	 * 
	 * }
	 */

	/*
	 * @PostMapping("/void") public ResponseEntity<Void> voidReceipt(@RequestBody
	 * String data){
	 * 
	 * try { JSONObject jsonObj = new JSONObject(data);
	 * 
	 * jdbcTemplate.update("UPDATE checks SET voidable = 1 WHERE chk_num = ?", new
	 * Object[] {jsonObj.getString("chkNo")});
	 * 
	 * return new ResponseEntity<>(HttpStatus.OK);
	 * 
	 * }catch(Exception ex) { ex.printStackTrace(); return new
	 * ResponseEntity<>(HttpStatus.BAD_REQUEST); } }
	 */

	// Success
	@PostMapping("/get_transaction_list_date")
	public ResponseEntity<?> getTransactionBasedOnDate(@RequestBody String data) {
		JSONObject jsonResult = null;
		JSONArray transList = null;

		try {
			jsonResult = new JSONObject();
			JSONObject jsonObj = new JSONObject(data);

			if (!jsonObj.has("datetime")) {
				Logger.writeActivity("No Datetime Found", ECPOS_FOLDER);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			String dateString = jsonObj.getString("datetime");
			System.out.println(dateString);

			List<Map<String, Object>> transactionListMaps = jdbcTemplate.queryForList(SELECT_TRANSACTION_USING_DATE_SQL,
					new Object[] { dateString });

			transList = new JSONArray();

			for (Map<String, Object> transactionMap : transactionListMaps) {
				JSONObject transaction = new JSONObject();

				transaction.put("tran_id", (Integer) transactionMap.get("tran_id"));
				transaction.put("tran_time", (String) transactionMap.get("tran_time"));
				transaction.put("name", (String) transactionMap.get("name"));
				transaction.put("description", (String) transactionMap.get("description"));
				transaction.put("amount", (BigDecimal) transactionMap.get("detail_amount"));

				transList.put(transaction);
			}

			jsonResult.put("trans_list", transList);
			Logger.writeActivity("Transaction List Until " + dateString + " " + jsonResult.toString(), ECPOS_FOLDER);

		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		return new ResponseEntity<>(jsonResult.toString(), HttpStatus.OK);
	}

}
