package mpay.my.ecpos_manager_v2.restcontrollerbk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mpay.my.ecpos_manager_v2.constant.Constant;
import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;
import mpay.my.ecpos_manager_v2.webutil.UserAuthenticationModel;
import mpay.my.ecpos_manager_v2.webutil.UtilWebComponents;

@RequestMapping("/payment")
@RestController
public class show_payment_RestController {

//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//	
//	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
//
//	private static final String GET_CHECK_TTL_SQL = "SELECT chk_seq, sub_ttl, pymnt_ttl FROM checks WHERE chk_num = ?";
//	private static final String SELECT_TAKEAWAY_CHECK_SQL = "SELECT * FROM checks WHERE chk_num LIKE '%TA' AND chk_open =2";
//	private static final String SELECT_TABLE_CHECK_SQL = "SELECT * FROM checks WHERE chk_num NOT LIKE '%TA' AND chk_open =2";
//	private static final String SELECT_TRANSACTIONS_FROM_DETAILS = "SELECT detail_item_price FROM details "
//			+ "WHERE detail_type ='T' " + "AND detail_item_status = 0 " + "AND chk_seq = ? ";
//	private static final String SELECT_TERMINAL_SQL = "SELECT * FROM terminal";
//
//	private static final String INSERT_TRANX_SQL = "INSERT into transaction (amount, check_no, tran_type, mtrx_id, tran_status, auth_code, payment_type, trace_no, batch_no, bank_mid, bank_tid, aid, app_label, masked_cardno, cardholder_name, tc, performBy, invoice_no, auth_code)"
//			+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//
//	private static final String UPDATE_MASTERTRANX_SQL = "UPDATE mastertransaction SET transaction_no = ?";
//	private static final String UPDATE_CLOSE_CHECK_SQL = "UPDATE checks SET chk_open = 3 WHERE chk_num = ?";
//
//	private static final String IPOS_PATH = "C:\\ipos\\ipos.exe ";
//
//	// Success
//	@GetMapping("/checkTotal/{checkNumber}")
//	public ResponseEntity<?> getCheckTotal(@PathVariable String checkNumber) {
//		JSONObject jsonResult = new JSONObject();
//		try {
//			if (!checkNumber.isEmpty() && !checkNumber.equals(null)) {
//
//				Map<String, Object> checkTotalResult = getCheckTotalMap(checkNumber);
//
//				if (!checkTotalResult.isEmpty()) {
//
//					List<Map<String, Object>> transactionResultMaps = jdbcTemplate.queryForList(
//							SELECT_TRANSACTIONS_FROM_DETAILS, new Object[] { (long) checkTotalResult.get("chk_seq") });
//
//					BigDecimal checkTotal = BigDecimal.ZERO;
//
//					if (checkTotalResult.containsKey("sub_ttl")) {
//						checkTotal = (BigDecimal) checkTotalResult.get("sub_ttl");
//					}
//
//					if (!transactionResultMaps.isEmpty()) {
//						for (Map<String, Object> transactionResultMap : transactionResultMaps) {
//							checkTotal = checkTotal
//									.subtract((BigDecimal) transactionResultMap.get("detail_item_price"));
//						}
//					}
//					jsonResult.put("transactionPrice", checkTotal);
//					jsonResult.put("transactionName", "Check Total");
//					Logger.writeActivity("Payment Check Total : " + checkTotal.toString(), ECPOS_FOLDER);
//				}
//			} else {
//				Logger.writeActivity("Payment Check Number Not Found!", ECPOS_FOLDER);
//				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//			}
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		}
//		return new ResponseEntity<>(jsonResult.toString(), HttpStatus.OK);
//	}
//
//	// Success
//	@GetMapping("/unpaidOrderChecks")
//	public ResponseEntity<?> getUnpaidTakeAwayOrderCheck() {
//		JSONObject jsonResult = new JSONObject();
//
//		try {
//			JSONArray takeAwayOrderJsonArray = new JSONArray();
//			JSONArray tableOrderJsonArray = new JSONArray();
//
//			List<Map<String, Object>> takeAwayResultMaps = getTakeAwayOrderList();
//			if (!takeAwayResultMaps.isEmpty()) {
//
//				for (Map<String, Object> takeAwayOrder : takeAwayResultMaps) {
//					JSONObject takeAwayOrderObj = new JSONObject();
//					takeAwayOrderObj.put("checkSequence", (long) takeAwayOrder.get("chk_seq"));
//					takeAwayOrderObj.put("checkNumber", (String) takeAwayOrder.get("chk_num"));
//					takeAwayOrderObj.put("createdDate", (Date) takeAwayOrder.get("createdate"));
//					takeAwayOrderObj.put("subTotal", (BigDecimal) takeAwayOrder.get("sub_ttl"));
//					takeAwayOrderObj.put("taxTotal", (BigDecimal) takeAwayOrder.get("tax_ttl"));
//
//					takeAwayOrderJsonArray.put(takeAwayOrderObj);
//				}
//				jsonResult.put("takeAwayOrderList", takeAwayOrderJsonArray);
//			}
//
//			List<Map<String, Object>> tableResultMaps = getTableOrderList();
//			if (!tableResultMaps.isEmpty()) {
//
//				for (Map<String, Object> tableOrder : tableResultMaps) {
//					JSONObject tableOrderObj = new JSONObject();
//					tableOrderObj.put("checkSequence", (long) tableOrder.get("chk_seq"));
//					tableOrderObj.put("checkNumber", (String) tableOrder.get("chk_num"));
//					tableOrderObj.put("tableNumber", (int) tableOrder.get("tblno"));
//					tableOrderObj.put("createdDate", (Date) tableOrder.get("createdate"));
//					tableOrderObj.put("subTotal", (BigDecimal) tableOrder.get("sub_ttl"));
//					tableOrderObj.put("taxTotal", (BigDecimal) tableOrder.get("tax_ttl"));
//
//					tableOrderJsonArray.put(tableOrderObj);
//				}
//				jsonResult.put("tableOrderList", tableOrderJsonArray);
//
//			}
//			Logger.writeActivity("Unpaid Check List :" + jsonResult.toString(), ECPOS_FOLDER);
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		}
//		return new ResponseEntity<>(jsonResult.toString(), HttpStatus.OK);
//	}
//
//	// Payment CTRL -> cash payment
//	@PostMapping("/cashPayment")
//	public ResponseEntity<String> performCashPayment(@RequestBody String data, HttpServletRequest request) {
//		JSONObject jsonResult = new JSONObject();
//		int userid = 0;
//
//		UtilWebComponents webcomponent = new UtilWebComponents();
//		UserAuthenticationModel session_container_user = webcomponent.getEcposSession(request);
//		if (session_container_user != null) {
//			userid = session_container_user.getUserLoginId();
//		}
//
//		try {
//			// insert two way into detail
//			JSONObject jsonData = new JSONObject(data);
//			JSONArray jsonTransactionList = jsonData.getJSONArray("transactionList");
//
//			// Get check data
//			Map<String, Object> checkMapResult = jdbcTemplate.queryForMap("SELECT * FROM checks WHERE chk_num = ?",
//					new Object[] { jsonData.getString("checkNumber") });
//
//			if (!checkMapResult.isEmpty()) {
//				String chkNum = (String) jsonData.get("checkNumber");
//				long chkSeq = (long) checkMapResult.get("chk_seq");
//
//				int detailSequence = (getDetailSequence(chkSeq) == 0) ? 1 : getDetailSequence(chkSeq) + 1;
//
//				// Insert Transaction detail into details table
//				for (int i = 0; i < jsonTransactionList.length(); i++) {
//
//					JSONObject jsonTransactionData = jsonTransactionList.getJSONObject(i);
//					System.out.println(new BigDecimal(jsonTransactionData.getDouble("transactionPrice")));
//
//					jdbcTemplate.update(
//							"INSERT INTO details (chk_seq,dtl_seq,number,name,chk_ttl,detail_type,detail_item_price) VALUES (?,?,?,?,?,?,?)",
//							new Object[] { (long) checkMapResult.get("chk_seq"), detailSequence++, 0,
//									jsonTransactionData.getString("transactionName"),
//									new BigDecimal(jsonTransactionData.getDouble("transactionPrice")), "T",
//									new BigDecimal(jsonTransactionData.getDouble("transactionPrice")) });
//				}
//
//				// Make the calculation
//				BigDecimal paymentTotal = jdbcTemplate.queryForObject(
//						"SELECT SUM(detail_item_price) AS 'detail_item_price' FROM details WHERE chk_seq = ? AND detail_type = 'T' AND detail_item_status = 0",
//						new Object[] { chkSeq }, BigDecimal.class);
//
//				Map<String, Object> checkResultMap = jdbcTemplate.queryForMap("SELECT * FROM checks WHERE chk_seq =?",
//						new Object[] { chkSeq });
//
//				BigDecimal subTotal = (BigDecimal) checkResultMap.get("sub_ttl");
//				BigDecimal dueTotal = subTotal.subtract(paymentTotal);
//
//				// Update the check , if <= 0 then close the check
//				if (dueTotal.compareTo(BigDecimal.ZERO) <= 0) {
//					jdbcTemplate.update("UPDATE checks SET chk_open =3, pymnt_ttl = ?, due_ttl = ? WHERE chk_seq = ?",
//							new Object[] { paymentTotal, dueTotal, chkSeq });
//					// Trigger Print Event
//					jsonResult.put("printReceipt", true);
//				} else {
//					jdbcTemplate.update("UPDATE checks SET pymnt_ttl = ?, due_ttl = ? WHERE chk_seq = ?",
//							new Object[] { paymentTotal, dueTotal, chkSeq });
//				}
//
//				// Insert transaction
//				String tranType = "cash-sale";
//				String tranStatus = "APPROVED";
//				String paymentType = "Cash";
//
//				String insertTransactionSql = "INSERT INTO transaction (check_no, tran_type ,tran_status, payment_type, amount, performBy) VALUES (?,?,?,?,?,?)";
//				jdbcTemplate.update(insertTransactionSql,
//						new Object[] { chkNum, tranType, tranStatus, paymentType, subTotal, userid });
//
//				jsonResult.put(Constant.RESPONSE_CODE, "00");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//				Logger.writeActivity("=== CASH PAYMENT TRANSACTION SUCCESS ===", ECPOS_FOLDER);
//			} else {
//				jsonResult.put(Constant.RESPONSE_CODE, "01");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "CHECK NOT FOUND");
//				Logger.writeActivity("=== CHECK NOT FOUND ===", ECPOS_FOLDER);
//			}
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		}
//		return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
//	}
//
//	// Success
//	@PostMapping("/pingTest")
//	public ResponseEntity<String> performPingTest(@RequestBody String data, HttpServletRequest request) {
//		JSONObject jsonResult = new JSONObject();
//		String requestData = "";
//		System.out.println("Ping data: " + data);
//
//		try {
//			JSONObject jsonData = new JSONObject(data);
//
//			if (jsonData.has("wifiIP") && jsonData.has("wifiPort")) {
//				requestData = "{\\\"tranType\\\":" + "\\\"" + jsonData.getString("tranType") + "\\\","
//						+ "\\\"wifiIP\\\":" + "\\\"" + jsonData.getString("wifiIP") + "\\\"," + "\\\"wifiPort\\\":"
//						+ "\\\"" + jsonData.getString("wifiPort") + "\\\"}";
//			} else {
//				requestData = "{\\\"tranType\\\":\\\"ping-test\\\"}";
//			}
//			System.out.println("Ping Request: " + requestData);
//
//			Process p1 = Runtime.getRuntime().exec(IPOS_PATH + requestData);
//
//			System.out.println("Waiting for Ping ...");
//			p1.waitFor();
//			System.out.println("Ping Completed.");
//
//			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p1.getInputStream()));
//			StringBuilder response = new StringBuilder();
//
//			String s = null;
//			while ((s = stdInput.readLine()) != null) {
//				response.append(s);
//			}
//			System.out.println("Ping Response: " + response.toString());
//			JSONObject responseData = new JSONObject(response.toString().replace("[IPOS-RESPONSE]", ""));
//
//			if (responseData.getString("responseCode").equals("00")) {
//				jsonResult.put(Constant.RESPONSE_CODE, "00");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//				Logger.writeActivity("PING SUCCESS", ECPOS_FOLDER);
//			} else {
//				jsonResult.put(Constant.RESPONSE_CODE, "01");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "FAIL");
//				Logger.writeActivity("PING FAILURE", ECPOS_FOLDER);
//			}
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		}
//		return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
//	}
//
//	// Success
//	@PostMapping("/cardPayment")
//	public ResponseEntity<String> performCardPayment(@RequestBody String data, HttpServletRequest request) {
//		JSONObject jsonResult = new JSONObject();
//		int userid = 0;
//		String requestData = "";
//		System.out.println("Card Payment data: " + data);
//
//		UtilWebComponents webcomponent = new UtilWebComponents();
//		UserAuthenticationModel session_container_user = webcomponent.getEcposSession(request);
//		if (session_container_user != null) {
//			userid = session_container_user.getUserLoginId();
//		}
//
//		try {
//			JSONObject jsonData = new JSONObject(data);
//
//			if (getSystemData(userid) != 0) {
//
//				String checkNo = jsonData.getString("checkNumber");
//				String storeId = String.format("%04d", getSystemData(userid));
//				String selectedTerminalId = jsonData.getString("selectedTerminal");
//				String tranType = jsonData.getString("tranType");
//				String amount = new BigDecimal(jsonData.getDouble("amount")).setScale(2, BigDecimal.ROUND_HALF_UP)
//						.toString();
//				String tips = new BigDecimal(jsonData.getDouble("tips")).setScale(2, BigDecimal.ROUND_HALF_UP)
//						.toString();
//
//				Calendar calendar = Calendar.getInstance();
//				String year = Integer.toString(calendar.get(Calendar.YEAR));
//				String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
//				String date = Integer.toString(calendar.get(Calendar.DATE));
//
//				String posId = "01";
//				String transactionId = String.format("%07d", resetTransactionOrder());
//
//				String uniqueTranNumber = year + month + date + storeId + posId + transactionId;
//
//				System.out.println("[CardPayment] Transaction Number: " + uniqueTranNumber);
//				Logger.writeActivity("[CardPayment] Transaction Number: " + uniqueTranNumber, ECPOS_FOLDER);
//
//				requestData = "{\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType
//						+ "\\\"," + "\\\"amount\\\":" + "\\\"" + amount + "\\\"," + "\\\"tips\\\":" + "\\\"" + tips
//						+ "\\\"," + "\\\"uniqueTranNumber\\\":" + "\\\"" + uniqueTranNumber;
//
//				requestData = modifiedRequestDataString(requestData, selectedTerminalId);
//				System.out.println("Card Payment Request: " + requestData);
//				JSONObject responseData = sendDataToIPOS(requestData, "Card Payment");
//
//				/*
//				 * Process p1 = Runtime.getRuntime().exec(IPOS_PATH + requestData);
//				 * BufferedReader stdInput = new BufferedReader(new
//				 * InputStreamReader(p1.getInputStream())); StringBuilder response = new
//				 * StringBuilder();
//				 * 
//				 * String s = null; while ((s = stdInput.readLine()) != null) {
//				 * response.append(s); }
//				 * 
//				 * System.out.println(response.toString()); JSONObject responseData = null;
//				 * 
//				 * if (response.toString().indexOf("[IPOS-CONTACT-BANK][IPOS-RESPONSE]") == 0) {
//				 * responseData = new JSONObject(
//				 * response.toString().replace("[IPOS-CONTACT-BANK][IPOS-RESPONSE]", "")); }
//				 * else { responseData = new
//				 * JSONObject(response.toString().replace("[IPOS-RESPONSE]", "")); }
//				 * 
//				 * System.out.println("Card Payment Response: " + responseData.toString());
//				 */
//
//				if (responseData.getString("cardResponse") == null) {
//					jsonResult.put(Constant.RESPONSE_CODE, "01");
//					jsonResult.put(Constant.RESPONSE_MESSAGE, "NO CARD RESPONSE");
//					Logger.writeActivity("[CardPayment] NO CARD RESPONSE", ECPOS_FOLDER);
//				} else {
//					JSONObject terminalResponse = responseData.getJSONObject("cardResponse");
//
//					// Process the result
//					if (responseData.getString("responseCode").equals("00")) {
//						jdbcTemplate.update(INSERT_TRANX_SQL,
//								new Object[] { amount, checkNo, responseData.getString("tranType"), null,
//										responseData.getString("responseMessage"), null, "Card", null,
//										terminalResponse.getString("batchNumber"),
//										terminalResponse.getString("bankMerchantID"),
//										terminalResponse.getString("bankTerminalID"), terminalResponse.getString("AID"),
//										terminalResponse.getString("APP"), terminalResponse.getString("cardNumber"),
//										terminalResponse.getString("cardHolderName"), terminalResponse.getString("TC"),
//										userid, terminalResponse.getString("invoiceNumber"), "" });
//
//						jdbcTemplate.update(UPDATE_CLOSE_CHECK_SQL, new Object[] { checkNo });
//
//						jsonResult.put(Constant.RESPONSE_CODE, "00");
//						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//						jsonResult.put("cardResponse", terminalResponse);
//						Logger.writeActivity("[CardPayment] Response Data: " + terminalResponse.toString(), ECPOS_FOLDER);
//					} else {
//						jsonResult.put(Constant.RESPONSE_CODE, "01");
//						jsonResult.put(Constant.RESPONSE_MESSAGE, "FAIL");
//						Logger.writeActivity("[CardPayment] Response Message: " + responseData.getString("responseMessage"), ECPOS_FOLDER);
//					}
//				}
//			} else {
//				Logger.writeActivity("[CardPayment] USER ID NOT FOUND", ECPOS_FOLDER);
//				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//			}
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		}
//
//		System.out.println("Card Payment END RESULT: " + jsonResult.toString());
//		return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
//	}
//
//	@PostMapping("/voidPayment")
//	public ResponseEntity<String> performVoidCardPayment(@RequestBody String data, HttpServletRequest request) {
//		JSONObject jsonResult = new JSONObject();
//		int userid = 0;
//		String requestData = "";
//		System.out.println("Void Payment data: " + data);
//
//		UtilWebComponents webcomponent = new UtilWebComponents();
//		UserAuthenticationModel session_container_user = webcomponent.getEcposSession(request);
//		if (session_container_user != null) {
//			userid = session_container_user.getUserLoginId();
//		}
//
//		try {
//			JSONObject jsonData = new JSONObject(data);
//
//			String checkNo = jsonData.getString("checkNumber");
//			String storeId = String.format("%04d", getSystemData(userid));
//			String selectedTerminalId = jsonData.getString("selectedTerminal");
//
//			String FIND_TRANX_INVOICE_NO_SQL = "SELECT invoice_no FROM transaction WHERE tran_id = ? AND isSettlement = 0";
//			Map<String, Object> transactionResult = jdbcTemplate.queryForMap(FIND_TRANX_INVOICE_NO_SQL,
//					new Object[] { jsonData.getString("transactionId") });
//
//			requestData = "{\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + "card-void"
//					+ "\\\"," + "\\\"invoiceNumber\\\":" + "\\\"" + (String) transactionResult.get("invoice_no");
//
//			requestData = modifiedRequestDataString(requestData, selectedTerminalId);
//			System.out.println("Void Payment Request: " + requestData);
//			JSONObject responseData = sendDataToIPOS(requestData, "Void Payment");
//
//			if (responseData.getString("cardResponse") == null) {
//				jsonResult.put(Constant.RESPONSE_CODE, "01");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "NO CARD RESPONSE");
//				Logger.writeActivity("[VoidPayment] NO CARD RESPONSE", ECPOS_FOLDER);
//			} else {
//				JSONObject terminalResponse = responseData.getJSONObject("cardResponse");
//				if (responseData.getString("responseCode").equals("00")) {
//					jdbcTemplate.update("UPDATE transaction SET tran_type = 'void' WHERE tran_id = ? AND isSettlement = 0",
//							new Object[] { jsonData.getString("transactionId") });
//
//					jdbcTemplate.update("UPDATE checks SET voidable = 1 WHERE chk_num = ?", new Object[] { checkNo });
//
//					jsonResult.put(Constant.RESPONSE_CODE, "00");
//					jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//					Logger.writeActivity("[VoidPayment] Response Data: " + terminalResponse.toString(), ECPOS_FOLDER);
//				} else {
//					jsonResult.put(Constant.RESPONSE_CODE, "01");
//					jsonResult.put(Constant.RESPONSE_MESSAGE, "FAIL");
//					Logger.writeActivity("[VoidPayment] Response Message: " + responseData.getString("responseMessage"), ECPOS_FOLDER);
//				}
//			}
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		}
//		System.out.println("Void Payment END Result: " + jsonResult.toString());
//		return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
//	}
//
//	// todo: not yet handle
//	@PostMapping("/settlement")
//	public ResponseEntity<String> performSettlement(@RequestBody String data, HttpServletRequest request) {
//		JSONObject jsonResult = new JSONObject();
//		int userid = 0;
//		String requestData = "";
//		System.out.println("Settlement data: " + data);
//
//		UtilWebComponents webcomponent = new UtilWebComponents();
//		UserAuthenticationModel session_container_user = webcomponent.getEcposSession(request);
//		if (session_container_user != null) {
//			userid = session_container_user.getUserLoginId();
//		}
//
//		try {
//			JSONObject jsonData = new JSONObject(data);
//			String storeId = String.format("%04d", getSystemData(userid));
//			String selectedTerminalId = jsonData.getString("selectedTerminal");
//			String tranType = jsonData.getString("tranType");
//			String settlementType = jsonData.getString("settlementType");
//
//			requestData = "{\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\""
//					+ "card-settlement" + "\\\"," + "\\\"niiName\\\":" + "\\\"" + settlementType;
//
//			requestData = modifiedRequestDataString(requestData, selectedTerminalId);
//			System.out.println("Settlement Request: " + requestData);
//			JSONObject responseData = sendDataToIPOS(requestData, "Settlement");
//
//			/*
//			 * Process p1 = Runtime.getRuntime().exec(IPOS_PATH + requestData);
//			 * p1.waitFor(); BufferedReader stdInput = new BufferedReader(new
//			 * InputStreamReader(p1.getInputStream())); StringBuilder response = new
//			 * StringBuilder();
//			 * 
//			 * String s = null; while ((s = stdInput.readLine()) != null) {
//			 * response.append(s); }
//			 * 
//			 * System.out.println("Settlement Response: " + response.toString()); JSONObject
//			 * responseData = null;
//			 * 
//			 * if (response.toString().indexOf("[IPOS-CONTACT-BANK][IPOS-RESPONSE]") == 0) {
//			 * responseData = new
//			 * JSONObject(response.toString().replace("[IPOS-CONTACT-BANK][IPOS-RESPONSE]",
//			 * "")); } else { responseData = new
//			 * JSONObject(response.toString().replace("[IPOS-RESPONSE]", "")); }
//			 */
//
//			if (responseData.getString("settlementResponse") == null) {
//				System.out.println("No Response");
//				jsonResult.put(Constant.RESPONSE_CODE, "01");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "NO SETTLEMENT RESPONSE");
//				Logger.writeActivity("[Settlement] NO SETTLEMENT RESPONSE", ECPOS_FOLDER);
//			} else {
//				System.out.println("Got Response");
//				JSONObject terminalSettlementResponse = responseData.getJSONObject("settlementResponse");
//
//				if (responseData.getString("responseCode").equals("00")) {
//					jdbcTemplate.update(
//							"INSERT settlement(merchant_info,bank_tid,bank_mid,batch_no,tranx_date,tranx_time,batch_ttl,nii) VALUES (?,?,?,?,?,?,?,?)",
//							new Object[] { terminalSettlementResponse.getString("merchantInfo"),
//									terminalSettlementResponse.getString("bankTerminalID"),
//									terminalSettlementResponse.getString("bankMerchantID"),
//									terminalSettlementResponse.getString("batchNumber"),
//									terminalSettlementResponse.getString("transactionDate"),
//									terminalSettlementResponse.getString("transactionTime"),
//									terminalSettlementResponse.getString("batchTotals"),
//									terminalSettlementResponse.getString("nii") });
//
//					// batch update the transaction which have the same batchNo
//					jdbcTemplate.update("UPDATE transaction SET isSettlement = 1 WHERE batch_no = ? AND tran_type != 'void'",  new Object[] {terminalSettlementResponse.getString("batchNumber")});
//
//					jsonResult.put(Constant.RESPONSE_CODE, "00");
//					jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//					Logger.writeActivity("[Settlement] Response Data: " + terminalSettlementResponse.toString(), ECPOS_FOLDER);
//				} else {
//					jsonResult.put(Constant.RESPONSE_CODE, "01");
//					jsonResult.put(Constant.RESPONSE_MESSAGE, "FAIL");
//					Logger.writeActivity("[Settlement] Response Message: " + responseData.getString("responseMessage"), ECPOS_FOLDER);
//				}
//			}
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		}
//
//		System.out.println("Settlement END RESULT: " + jsonResult.toString());
//		return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
//	}
//
//	// Success
//	@GetMapping("/terminalList")
//	public String getTerminalList(HttpServletRequest request) {
//		JSONObject jsonResult = new JSONObject();
//
//		try {
//			List<Map<String, Object>> terminalResultMaps = jdbcTemplate.queryForList(SELECT_TERMINAL_SQL);
//			JSONArray terminalJsonArray = new JSONArray();
//
//			for (Map<String, Object> terminalResultMap : terminalResultMaps) {
//				JSONObject jsonObj = new JSONObject();
//				jsonObj.put("id", (int) terminalResultMap.get("id"));
//				jsonObj.put("terminalName", (String) terminalResultMap.get("terminalName"));
//				jsonObj.put("wifiIP", (String) terminalResultMap.get("wifiIP"));
//				jsonObj.put("wifiPort", (String) terminalResultMap.get("wifiPort"));
//
//				terminalJsonArray.put(jsonObj);
//			}
//
//			jsonResult.put(Constant.RESPONSE_CODE, "00");
//			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//			jsonResult.put("terminalList", terminalJsonArray);
//
//			System.out.println("Available WiFi Terminal: " + jsonResult.toString());
//			Logger.writeActivity("Available Terminal List: " + jsonResult.toString().toString(), ECPOS_FOLDER);
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		}
//		return jsonResult.toString();
//	}
//
//	// Mini Function(s)
//	private Map<String, Object> getCheckTotalMap(String checkNumber) {
//		try {
//			return jdbcTemplate.queryForMap(GET_CHECK_TTL_SQL, new Object[] { checkNumber });
//		} catch (EmptyResultDataAccessException ex) {
//			return Collections.emptyMap();
//		}
//	}
//
//	private Map<String, Object> getSelectedTerminalMap(String selectedTerminalId) {
//		String SELECT_SELECTED_TERMINAL_SQL = "SELECT * FROM terminal WHERE id = ?";
//		try {
//			return jdbcTemplate.queryForMap(SELECT_SELECTED_TERMINAL_SQL, new Object[] { selectedTerminalId });
//		} catch (DataAccessException ex) {
//			return Collections.emptyMap();
//		}
//	}
//
//	private int getDetailSequence(long chkSeq) {
//		String findDetailInfoSql = "SELECT dtl_seq FROM details WHERE chk_seq = ? ORDER BY dtl_seq DESC LIMIT 1";
//		try {
//			return jdbcTemplate.queryForObject(findDetailInfoSql, new Object[] { chkSeq }, Integer.class);
//		} catch (DataAccessException ex) {
//			return 0;
//		}
//	}
//
//	private int getSystemData(int userId) {
//		String findStoreId = "SELECT storeid FROM empldef WHERE id = ?";
//		try {
//			return jdbcTemplate.queryForObject(findStoreId, new Object[] { userId }, Integer.class);
//		} catch (DataAccessException ex) {
//			return 0;
//		}
//	}
//
//	private List<Map<String, Object>> getTakeAwayOrderList() {
//		try {
//			return jdbcTemplate.queryForList(SELECT_TAKEAWAY_CHECK_SQL);
//		} catch (EmptyResultDataAccessException ex) {
//			return Collections.emptyList();
//		}
//	}
//
//	private List<Map<String, Object>> getTableOrderList() {
//		try {
//			return jdbcTemplate.queryForList(SELECT_TABLE_CHECK_SQL);
//		} catch (EmptyResultDataAccessException ex) {
//			return Collections.emptyList();
//		}
//	}
//
//	private int resetTransactionOrder() {
//		boolean isSameDay = false;
//		int paymentTransactionNumber = 0;
//		SimpleDateFormat standardDateFormat = new SimpleDateFormat("dd/MM/yyyy");
//		// SimpleDateFormat standardDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd
//		// HH:mm:ss");
//
//		Map<String, Object> masterTransactionResult = jdbcTemplate.queryForMap("SELECT * FROM mastertransaction");
//		Date recordDate = (Date) masterTransactionResult.get("record_date");
//		Date currentDate = new Date();
//
//		if (standardDateFormat.format(recordDate).compareTo(standardDateFormat.format(currentDate)) == 0)
//			isSameDay = true;
//
//		if (isSameDay)
//			paymentTransactionNumber = (int) masterTransactionResult.get("transaction_no") + 1;
//		else
//			paymentTransactionNumber = 1;
//
//		jdbcTemplate.update(UPDATE_MASTERTRANX_SQL, new Object[] { paymentTransactionNumber });
//		return paymentTransactionNumber;
//	}
//
//	private String modifiedRequestDataString(String requestData, String selectedTerminalId) {
//		Map<String, Object> terminalResult = getSelectedTerminalMap(selectedTerminalId);
//		if (!terminalResult.isEmpty()) {
//			requestData += "\\\"," + "\\\"wifiIP\\\":" + "\\\"" + (String) terminalResult.get("wifiIP") + "\\\","
//					+ "\\\"wifiPort\\\":" + "\\\"" + (String) terminalResult.get("wifiPort") + "\\\"}";
//		} else {
//			requestData += "\\\"}";
//		}
//		return requestData;
//	}
//
//	private JSONObject sendDataToIPOS(String requestData, String transactionMode)
//			throws IOException, InterruptedException, JSONException {
//		Process p1 = Runtime.getRuntime().exec(IPOS_PATH + requestData);
//		p1.waitFor();
//		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p1.getInputStream()));
//		StringBuilder response = new StringBuilder();
//
//		String s = null;
//		while ((s = stdInput.readLine()) != null) {
//			response.append(s);
//		}
//		System.out.println(transactionMode + " Response: " + response.toString());
//		JSONObject responseData = null;
//
//		if (response.toString().indexOf("[IPOS-CONTACT-BANK][IPOS-RESPONSE]") == 0)
//			responseData = new JSONObject(response.toString().replace("[IPOS-CONTACT-BANK][IPOS-RESPONSE]", ""));
//		else
//			responseData = new JSONObject(response.toString().replace("[IPOS-RESPONSE]", ""));
//
//		return responseData;
//	}

}
