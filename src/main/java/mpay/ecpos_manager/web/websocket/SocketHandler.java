package mpay.ecpos_manager.web.websocket;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import mpay.ecpos_manager.general.constant.Constant;
import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.UserAuthenticationModel;
import mpay.ecpos_manager.general.utility.ipos.Card;

@Component
public class SocketHandler extends TextWebSocketHandler {

	private static String IPOS_FOLDER = Property.getIPOS_FOLDER_NAME();
	
	private DataSource dataSource;
	
	private Card iposCard;
	
	//private String iposExe;
	
	// onMessage
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		System.out.println("Come on I am here");
		String data = message.getPayload();
		Logger.writeActivity("data: " + data, IPOS_FOLDER);
		
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
	
		UserAuthenticationModel user = (UserAuthenticationModel)session.getAttributes().get("session_user");
		dataSource = (DataSource)session.getAttributes().get("dataSource");
		//iposExe = (String)session.getAttributes().get("ipos_exe");
		iposCard = (Card)session.getAttributes().get("ipos_card");

		try {
			connection = dataSource.getConnection();
			JSONObject jsonObj = new JSONObject(data);
			
			int paymentType = -1;
			if (jsonObj.getString("paymentType").equals("full")) {
				paymentType = 1;
			} else if (jsonObj.getString("paymentType").equals("partial")) {
				paymentType = 2;
			} else {
				Logger.writeActivity("Invalid Payment Type", IPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Payment Type");
				
				session.sendMessage(new TextMessage(jsonResult.toString()));
				session.close();
			}
			
			int paymentMethod = -1;
			int transactionStatus = 1;
			String terminalSerialNumber = null;
			BigDecimal receivedAmount = BigDecimal.ZERO;
			if (jsonObj.getString("paymentMethod").equals("Card")) {
				paymentMethod = 2;
				receivedAmount = new BigDecimal(jsonObj.getString("paymentAmount"));
				
				if (!(jsonObj.has("terminalSerialNo") && !jsonObj.getString("terminalSerialNo").equals(null))) {
					Logger.writeActivity("Terminal Serial Number Not Found", IPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Terminal Serial Number Not Found");
					
					session.sendMessage(new TextMessage(jsonResult.toString()));
					session.close();
				} else {
					terminalSerialNumber = jsonObj.getString("terminalSerialNo");
				}
			} else {
				Logger.writeActivity("Invalid Payment Method", IPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Payment Method");
				
				session.sendMessage(new TextMessage(jsonResult.toString()));
				session.close();
			}
			
			BigDecimal paymentAmount = BigDecimal.ZERO;
			if (!(jsonObj.has("paymentAmount") && !jsonObj.getString("paymentAmount").equals(null))) {
				Logger.writeActivity("Invalid Payment Amount", IPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Payment Amount");
				
				session.sendMessage(new TextMessage(jsonResult.toString()));
				session.close();
			} else {					
				paymentAmount = new BigDecimal(jsonObj.getString("paymentAmount"));
			}
			
			BigDecimal changeAmount = receivedAmount.subtract(paymentAmount);
			
			JSONObject staffDetail = getStaffDetail(user.getUsername());
			long staffId = -1;
			if (staffDetail.length() <= 0) {
				Logger.writeActivity("Staff Detail Not Found", IPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Staff Detail Not Found");
				
				session.sendMessage(new TextMessage(jsonResult.toString()));
				session.close();
			} else {
				staffId = staffDetail.getLong("id");
			}
			
			JSONObject storeDetail = getStoreDetail();
			long storeId = -1;
			if (storeDetail.length() <= 0) {
				Logger.writeActivity("Store Detail Not Found", IPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Store Detail Not Found");
				
				session.sendMessage(new TextMessage(jsonResult.toString()));
				session.close();
			} else {
				storeId = storeDetail.getLong("id");
			}

			String tableNoCondition = "table_number is null";
			if (jsonObj.getInt("tableNo") > 0) {
				tableNoCondition = "table_number = " + jsonObj.getInt("tableNo");
			}
			
			stmt = connection.prepareStatement("select * from `check` where " + tableNoCondition + " and check_number = ? and check_status in (1, 2);");
			stmt.setString(1, jsonObj.getString("checkNo"));
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				if (rs.getBigDecimal("overdue_amount").compareTo(paymentAmount) >= 0) {
					int orderType = rs.getInt("order_type");
					long checkId = rs.getLong("id");
					String checkNo = rs.getString("check_number");
					BigDecimal grandTotalAmount = rs.getBigDecimal("grand_total_amount");
					BigDecimal tenderAmount = rs.getBigDecimal("tender_amount");
					
					stmt.close();
					stmt = connection.prepareStatement("insert into transaction (staff_id,check_id,check_number,transaction_type,payment_method,payment_type,terminal_serial_number,transaction_currency,transaction_amount,received_amount,change_amount,transaction_status,created_date,device_id) " + 
							"values (?,?,?,?,?,?,?,?,?,?,?,?,now(),?);", Statement.RETURN_GENERATED_KEYS);
					stmt.setLong(1, staffId);
					stmt.setLong(2, checkId);
					stmt.setString(3, checkNo);
					stmt.setInt(4, 1);
					stmt.setInt(5, paymentMethod);
					stmt.setInt(6, paymentType);
					stmt.setString(7, terminalSerialNumber);
					stmt.setString(8, "RM");
					stmt.setBigDecimal(9, paymentAmount);
					stmt.setBigDecimal(10, receivedAmount);
					stmt.setBigDecimal(11, changeAmount);
					stmt.setInt(12, transactionStatus);
					stmt.setLong(13, user.getDeviceId());
					int insertTransaction = stmt.executeUpdate();
					
					if (insertTransaction > 0) {
						rs2 = stmt.getGeneratedKeys();
						
						if (rs2.next()) {
							long transactionId = rs2.getLong(1);
	
							JSONObject terminalWifiIPPort = new JSONObject();
							JSONObject transactionResult = new JSONObject();
							JSONObject updateTransactionResult = new JSONObject();
							
							boolean paymentFlag = false;
							
							if(paymentMethod == 2) {
								terminalWifiIPPort = getTerminalWifiIPPort(terminalSerialNumber);
								String uniqueTranNumber = generateUniqueTranNumber(storeId, transactionId);
								
								if (!uniqueTranNumber.equals(null)) {
									//For Testing Purpose Only
									//transactionResult = new JSONObject("{\"tranType\":\"card-sale\",\"uniqueTranNumber\":\""+uniqueTranNumber+"\",\"responseCode\":\"00\",\"responseMessage\":\"APPROVED\",\"cardResponse\":{\"approvalCode\":\"007800\",\"invoiceNumber\":\"000007\",\"merchantInfo\":null,\"bankTerminalID\":\"99990023\",\"bankMerchantID\":\"000027031314099\",\"cardIssuerName\":\"VISA Wave \",\"cardNumber\":\"**** **** **** 4314\",\"expiryDate\":\"**\\/**\",\"batchNumber\":\"000001\",\"transactionDate\":\"140502\",\"transactionTime\":\"114637\",\"retrievalReferenceNumber\":\"000181100012\",\"cardIssuerID\":null,\"cardHolderName\":\" \\/\",\"AID\":\"A0000000031010\",\"APP\":\"MAYBANK VISA\",\"TC\":\"B9F2415D54989E98\",\"terminalVerificationResult\":\"3\"},\"qrResponse\":null,\"settlementResponse\":null,\"wifiIP\":\"\",\"wifiPort\":\"\"}");
									transactionResult = iposCard.cardSalePayment(String.format("%04d", storeId), "card-sale", paymentAmount, "0.00", uniqueTranNumber, terminalWifiIPPort, session);
									
									if(transactionResult.has("responseCode")) { 
										if (transactionResult.getString("responseCode").equals("00") || transactionResult.getString("responseCode").equals("09")) {
											paymentFlag = true;
											updateTransactionResult = updateTransactionResult(transactionResult,"card");
										} else {
											Logger.writeActivity("Transaction Failed To Perform", IPOS_FOLDER);
											jsonResult.put(Constant.RESPONSE_CODE, "01");
											jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Failed To Perform");
										}
									} else {
										Logger.writeActivity("IPOS cannot be detected.", IPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "IPOS cannot be detected. Please try again later.");
									}
								} else {
									Logger.writeActivity("Transaction Data Failed To Gather", IPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Data Failed To Gather");
								}
							}
							
							if(paymentFlag) {
								if(updateTransactionResult.has(Constant.RESPONSE_CODE)) {
									if (updateTransactionResult.getString(Constant.RESPONSE_CODE).equals("00")) {
										JSONObject updateCheckResult = new JSONObject();
										
										if (paymentType == 1) {
											updateCheckResult = updateCheck1(orderType, paymentAmount, checkNo, jsonObj.getInt("tableNo"), transactionId, grandTotalAmount, tenderAmount);
										} else {
											updateCheckResult = updateCheck2(orderType, paymentType, paymentAmount, checkNo, jsonObj.getInt("tableNo"), transactionId, grandTotalAmount, tenderAmount);
										}
										
										if (updateCheckResult.getString("status").equals("success")) {
											Logger.writeActivity("Transaction has been successfully performed", IPOS_FOLDER);
											jsonResult.put(Constant.RESPONSE_CODE, "00");
											jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction has been successfully performed.");
											jsonResult.put("check_status", updateCheckResult.getString("checkStatus"));
											jsonResult.put("change_amount", changeAmount);
										} else {
											Logger.writeActivity("Check Failed To Update", IPOS_FOLDER);
											jsonResult.put(Constant.RESPONSE_CODE, "01");
											jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Failed To Update");
										}
									} else {
										Logger.writeActivity(updateTransactionResult.getString(Constant.RESPONSE_MESSAGE), IPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, updateTransactionResult.getString(Constant.RESPONSE_MESSAGE));
									}
								} else {
										Logger.writeActivity("Transaction Failed To Perform", IPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Failed To Perform");
								}
							}
	
						} else {
							Logger.writeActivity("Transaction Id Not Found", IPOS_FOLDER);
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Id Not Found");
						}
					} else {
						Logger.writeActivity("Transaction Failed To Insert", IPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Failed To Insert");
					}
				} else {
					Logger.writeActivity("Payment Amount Is Greater Than Amount Need To Be Paid", IPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Payment Amount Is Greater Than Amount Need To Be Paid");
				}
			} else {
				Logger.writeActivity("Check Not Found", IPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Not Found");
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", IPOS_FOLDER);
				e.printStackTrace();
			}
		}
				
		if(session.isOpen()) {
			Logger.writeActivity("Card Payment Response: " + jsonResult.toString(), IPOS_FOLDER);
			session.sendMessage(new TextMessage(jsonResult.toString()));
			session.close();
		}
	}

	// onOpen
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Logger.writeActivity("Open WS connection successfully.", IPOS_FOLDER);
	}

	// onError
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		if(session.isOpen()) {
			session.close();
		}
		System.out.println("Error Occured. Connection Closed");
		Logger.writeActivity("WS Connection Failed. Close the connection.", IPOS_FOLDER);
	}

	// onClose
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("Connection Closed");
		Logger.writeActivity("Close WS connection successfully.", IPOS_FOLDER);
	}
	
	private JSONObject getStoreDetail() {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject storeDetail = new JSONObject();
		
		try {
			connection = dataSource.getConnection();
			
			stmt = connection.prepareStatement("select * from store;");
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				storeDetail.put("id",rs.getString("id"));
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", IPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return storeDetail;
	}
	
	private JSONObject getStaffDetail(String username) {
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
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", IPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return staffDetail;
	}
	
	private JSONObject getTerminalWifiIPPort(String terminalSerialNumber) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject terminalWifiIPPort = new JSONObject();
		
		try {
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("select * from terminal where serial_number = ?;");
			stmt.setString(1, terminalSerialNumber);
			rs = stmt.executeQuery();

			if (rs.next()) {
				terminalWifiIPPort.put("wifi_IP",rs.getString("wifi_IP"));
				terminalWifiIPPort.put("wifi_Port",rs.getString("wifi_Port"));
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", IPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return terminalWifiIPPort;
	}
	
	private String generateUniqueTranNumber(long storeId, long transactionId) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String uniqueTranNumber = null;
		
		try {
			connection = dataSource.getConnection();
			
			Calendar calendar = Calendar.getInstance();
			String year = Integer.toString(calendar.get(Calendar.YEAR));
			String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
			String date = String.format("%02d", calendar.get(Calendar.DATE));

			String posId = "01";
			
			uniqueTranNumber = year + month + date + String.format("%04d", storeId) + posId + String.format("%07d", transactionId);
			
			stmt = connection.prepareStatement("update transaction set unique_trans_number = ?, transaction_status = 2 where id = ?;");
			stmt.setString(1, uniqueTranNumber);
			stmt.setLong(2, transactionId);
			int updateTransaction = stmt.executeUpdate();
			
			if (updateTransaction > 0) {
				return uniqueTranNumber;
			} else {
				return null;
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", IPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private JSONObject updateCheck1(int orderType, BigDecimal paymentAmount, String checkNo, int tableNo, long transactionId, BigDecimal grandTotalAmount, BigDecimal tenderAmount) {
		Connection connection = null;
		PreparedStatement stmt = null;
		JSONObject result = new JSONObject();
		
		try {
			connection = dataSource.getConnection();
			
			tenderAmount = tenderAmount.add(paymentAmount);
			
			String tableNoCondition = "table_number is null";
			if (tableNo > 0) {
				tableNoCondition = "table_number = " + tableNo;
			}
			
			String checkStatusCondition = "check_status = check_status";
			if (orderType != 3) {
				checkStatusCondition = "check_status = 3";
			}
			
			stmt = connection.prepareStatement("update `check` set tender_amount = ?, overdue_amount = ?, " + checkStatusCondition + ", updated_date = now() where check_number = ? and " + tableNoCondition + " and check_status in (1, 2);");
			stmt.setBigDecimal(1, tenderAmount);
			stmt.setBigDecimal(2, grandTotalAmount.subtract(tenderAmount));
			stmt.setString(3, checkNo);
			int updateCheck = stmt.executeUpdate();
			
			if (updateCheck > 0) {
				stmt.close();
				stmt = connection.prepareStatement("update check_detail set check_detail_status = 3, transaction_id = ?, updated_date = now() " + 
						"where check_id = (select id from `check` where check_number = ? and " + tableNoCondition + ") and check_number = ? and check_detail_status in (1, 2); ");
				stmt.setLong(1, transactionId);
				stmt.setString(2, checkNo);
				stmt.setString(3, checkNo);
				int updateCheckDetail = stmt.executeUpdate();
				
				if (updateCheckDetail > 0) {
					result.put("status", "success");
					result.put("checkStatus", "closed");
				} else {
					result.put("status", "fail");
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", IPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private JSONObject updateCheck2(int orderType, int paymentType, BigDecimal paymentAmount, String checkNo, int tableNo, long transactionId, BigDecimal grandTotalAmount, BigDecimal tenderAmount) {
		Connection connection = null;
		PreparedStatement stmt = null;
		JSONObject result = new JSONObject();
		
		try {
			connection = dataSource.getConnection();
			
			String amountType = null;
			BigDecimal paidAmount = new BigDecimal("0.00");
			BigDecimal overdueAmount = new BigDecimal("0.00");
			if (paymentType == 2) {
				amountType = "tender_amount";
				paidAmount = tenderAmount.add(paymentAmount);
				overdueAmount = grandTotalAmount.subtract(paidAmount);
			}
			
			String tableNoCondition = "table_number is null";
			if (tableNo > 0) {
				tableNoCondition = "table_number = " + tableNo;
			}
			
			String checkStatusCondition = "";
			String checkStatus = "open";
			if (overdueAmount.compareTo(BigDecimal.ZERO) == 0 && orderType != 3) {
				checkStatusCondition = "check_status = 3, ";
				checkStatus = "closed";
			}
			
			stmt = connection.prepareStatement("update `check` set " + amountType + " = ?, overdue_amount = ?, " + checkStatusCondition + "updated_date = now() where check_number = ? and " + tableNoCondition + " and check_status in (1, 2);");
			stmt.setBigDecimal(1, paidAmount);
			stmt.setBigDecimal(2, overdueAmount);
			stmt.setString(3, checkNo);
			int updateCheck = stmt.executeUpdate();
			
			if (updateCheck > 0) {
				result.put("status", "success");
				result.put("checkStatus", checkStatus);
			} else {
				result.put("status", "fail");
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", IPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private JSONObject updateTransactionResult(JSONObject transactionResult, String transactionCategory) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		JSONObject jsonResult = new JSONObject();
		
		try {
			connection = dataSource.getConnection();
			
			if(transactionCategory.equals("card")) {
				if (transactionResult.getJSONObject("cardResponse").length() == 0) {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Card Sale Payment Response Not Found");
					Logger.writeActivity("Card Sale Payment Response Not Found", IPOS_FOLDER);
				} else {
					JSONObject cardResponse = transactionResult.getJSONObject("cardResponse");
					
					int transactionStatus = 4;
					if (transactionResult.getString("responseCode").equals("00") || transactionResult.getString("responseCode").equals("09")) {
						transactionStatus = 3;
					}

					int transactionType = -1;
					if (transactionResult.getString("tranType").equals("card-sale")) {
						transactionType = 1;
					} else if (transactionResult.getString("tranType").equals("card-void")) {
						transactionType = 2;
					}
					
					stmt = connection.prepareStatement("update transaction set response_code = ?,response_message = ?,updated_date = now(),wifi_ip = ?,wifi_port = ?, approval_code = ?, " + 
							"bank_mid = ?,bank_tid = ?,transaction_date = ?,transaction_time = ?,invoice_number = ?,merchant_info = ?,card_issuer_name = ?, " + 
							"masked_card_number = ?,card_expiry_date = ?,batch_number = ?,rrn = ?,card_issuer_id = ?,cardholder_name = ?,aid = ?,app_label = ?, " + 
							"tc = ?,terminal_verification_result = ?,transaction_status = ? where unique_trans_number = ? and transaction_type = ?;");
					stmt.setString(1, transactionResult.getString("responseCode"));
					stmt.setString(2, transactionResult.getString("responseMessage"));
					stmt.setString(3, transactionResult.getString("wifiIP"));
					stmt.setString(4, transactionResult.getString("wifiPort"));
					stmt.setString(5, cardResponse.getString("approvalCode"));
					stmt.setString(6, cardResponse.getString("bankMerchantID"));
					stmt.setString(7, cardResponse.getString("bankTerminalID"));
					stmt.setString(8, cardResponse.getString("transactionDate"));
					stmt.setString(9, cardResponse.getString("transactionTime"));
					stmt.setString(10, cardResponse.getString("invoiceNumber"));
					stmt.setString(11, cardResponse.getString("merchantInfo"));
					stmt.setString(12, cardResponse.getString("cardIssuerName"));
					stmt.setString(13, cardResponse.getString("cardNumber"));
					stmt.setString(14, cardResponse.getString("expiryDate"));
					stmt.setString(15, cardResponse.getString("batchNumber"));
					stmt.setString(16, cardResponse.getString("retrievalReferenceNumber"));
					stmt.setString(17, cardResponse.getString("cardIssuerID"));
					stmt.setString(18, cardResponse.getString("cardHolderName"));
					stmt.setString(19, cardResponse.getString("AID"));
					stmt.setString(20, cardResponse.getString("APP"));
					stmt.setString(21, cardResponse.getString("TC"));
					stmt.setString(22, cardResponse.getString("terminalVerificationResult"));
					stmt.setInt(23, transactionStatus);
					stmt.setString(24, transactionResult.getString("uniqueTranNumber"));
					stmt.setInt(25, transactionType);
					int updateTransaction = stmt.executeUpdate();
					
					if (updateTransaction > 0) {
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
						Logger.writeActivity("Card Sale Payment Response Successfully Update Transaction Table", IPOS_FOLDER);
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, transactionResult.getString("responseMessage"));
						Logger.writeActivity(transactionResult.getString("responseMessage"), IPOS_FOLDER);
					}
				}
			} else if(transactionCategory.equals("qr")) {
				if(transactionResult.getJSONObject("qrResponse").length() == 0) {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "QR Sale Payment Response Not Found");
					Logger.writeActivity("QR Sale Payment Response Not Found", IPOS_FOLDER);
				} else {
					JSONObject qrResponse = transactionResult.getJSONObject("qrResponse");
					
					int transactionStatus = 4;
					if (transactionResult.getString("responseCode").equals("00") || transactionResult.getString("responseCode").equals("09")) {
						transactionStatus = 3;
					}
					
					int transactionType = -1;
					if (transactionResult.getString("tranType").equals("qr-sale")) {
						transactionType = 1;
					} else if (transactionResult.getString("tranType").equals("qr-void")) {
						transactionType = 2;
					} else if (transactionResult.getString("tranType").equals("qr-refund")) {
						transactionType = 3;
					}
										
					stmt = connection.prepareStatement("update transaction set response_code = ?,response_message = ?,updated_date = now(),wifi_ip = ?,wifi_port = ?, qr_issuer_type = ?, "
							+ "bank_tid = ?,bank_mid = ?,mpay_mid = ?,mpay_tid = ?,transaction_date = ?,transaction_time = ?,trace_number = ?,qr_ref_id = ?,qr_user_id =?, "
							+ "qr_amount_myr = ?,qr_amount_rmb = ?, transaction_status = ? where unique_trans_number = ? and transaction_type = ?;");

					stmt.setString(1, transactionResult.getString("responseCode"));
					stmt.setString(2, transactionResult.getString("responseMessage"));
					stmt.setString(3, transactionResult.getString("wifiIP"));
					stmt.setString(4, transactionResult.getString("wifiPort"));
					stmt.setString(5, qrResponse.getString("qrIssuerType"));
					stmt.setString(6, qrResponse.getString("bankTerminalID"));
					stmt.setString(7, qrResponse.getString("bankMerchantID"));
					stmt.setString(8, qrResponse.getString("mpayMerchantID"));
					stmt.setString(9, qrResponse.getString("mpayTerminalID"));
					stmt.setString(10, qrResponse.getString("transactionDate"));
					stmt.setString(11, qrResponse.getString("transactionTime"));
					stmt.setString(12, qrResponse.getString("traceNumber"));
					stmt.setString(13, qrResponse.getString("qrRefID"));
					stmt.setString(14, qrResponse.getString("qrUserID"));
					stmt.setString(15, qrResponse.getString("amountMYR"));
					stmt.setString(16, qrResponse.getString("amountRMB"));
					stmt.setInt(17, transactionStatus);
					stmt.setString(18, transactionResult.getString("uniqueTranNumber"));
					stmt.setInt(19, transactionType);
					int updateTransaction = stmt.executeUpdate();
					
					if (updateTransaction > 0) {
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
						Logger.writeActivity("QR Sale Payment Response Successfully Update Transaction Table", IPOS_FOLDER);
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, transactionResult.getString("responseMessage"));
						Logger.writeActivity(transactionResult.getString("responseMessage"), IPOS_FOLDER);
					}
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", IPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}
	
/*	private JSONObject cardSalePayment(String storeId, String tranType, BigDecimal amount, String tips, String uniqueTranNumber, JSONObject terminalWifiIPPort, WebSocketSession session) {
		JSONObject jsonResult = new JSONObject();

		try {
			String saleRequest = "\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType
					+ "\\\"," + "\\\"amount\\\":" + "\\\"" + amount + "\\\"," + "\\\"tips\\\":" + "\\\"" + tips
					+ "\\\"," + "\\\"uniqueTranNumber\\\":" + "\\\"" + uniqueTranNumber + "\\\"";
			
			String terminalWifiIP = "";
			String terminalWifiPort = "";
			
			if (terminalWifiIPPort.length() != 0) {
				terminalWifiIP = terminalWifiIPPort.getString("wifi_IP");
				terminalWifiPort = terminalWifiIPPort.getString("wifi_Port");
				
				saleRequest = saleRequest + "," + "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIP + "\\\"," + "\\\"wifiPort\\\":" + "\\\"" + terminalWifiPort + "\\\"";
			}

			jsonResult = submitIPOS("{"+saleRequest+"}", session);
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		
		System.out.println("Card Sales Payment Result :" + jsonResult.toString());
		return jsonResult;
	}
	

	private JSONObject submitIPOS(String request, WebSocketSession session) throws Exception{
		JSONObject response = new JSONObject();
		
		try {
			Process executeIPOS = Runtime.getRuntime().exec(iposExe + " " + request);
			//executeIPOS.waitFor();

			BufferedReader input = new BufferedReader(new InputStreamReader(executeIPOS.getInputStream()));
			
			//StringBuilder responseString = new StringBuilder();

			String line;
			String iposResponseMessage = "";
			while ((line = input.readLine()) != null) {
				 System.out.println(line);
				 session.sendMessage(new TextMessage(line));
				 
				 if(line.contains("[IPOS-RESPONSE]")) {
					 iposResponseMessage = line;
				 }
			}
			
			//executeIPOS.waitFor();
			if(!executeIPOS.waitFor(2, TimeUnit.MINUTES)) {
				//destroy the process if exceed  timeout
				executeIPOS.destroyForcibly();
				System.out.println("terminate ipos process");
			}
			input.close();
		
			//return the json
			System.out.println("last line :" + iposResponseMessage);
			
			JSONObject jsonData = new JSONObject(iposResponseMessage.replace("[IPOS-RESPONSE]", ""));
			if(!jsonData.isNull("cardResponse")) {
				response = jsonData;
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		
		System.out.println("IPOS result: " + response.toString());
		return response;
	}*/

}
