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
	
	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	private DataSource dataSource;
	
	private Card iposCard;
	
	//private String iposExe;
	
	// onMessage
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		System.out.println("Come on I am here");
		String data = message.getPayload();
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		
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
			} else if (jsonObj.getString("paymentType").equals("split")) {
				paymentType = 3;
			} else if (jsonObj.getString("paymentType").equals("deposit")) {
				paymentType = 4;
			} else {
				Logger.writeActivity("Invalid Payment Type", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Payment Type");
				
				session.sendMessage(new TextMessage(jsonResult.toString()));
				session.close();
			}
			
			int paymentMethod = -1;
			int transactionStatus = 1;
			String terminalSerialNumber = null;
			if (jsonObj.getString("paymentMethod").equals("Card")) {
				paymentMethod = 2;
				
				if (!(jsonObj.has("terminalSerialNo") && !jsonObj.getString("terminalSerialNo").equals(null))) {
					Logger.writeActivity("Terminal Serial Number Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Terminal Serial Number Not Found");
					
					session.sendMessage(new TextMessage(jsonResult.toString()));
					session.close();
				}
				terminalSerialNumber = jsonObj.getString("terminalSerialNo");
			} else {
				Logger.writeActivity("Invalid Payment Method", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Payment Method");
				
				session.sendMessage(new TextMessage(jsonResult.toString()));
				session.close();
			}
			
			JSONArray checkDetailIdArray = new JSONArray();
			if (paymentType == 3) {
			
				if (!(jsonObj.has("checkDetailIdArray") && jsonObj.getJSONArray("checkDetailIdArray").length() > 0)) {
					Logger.writeActivity("Item Not Found For Split Payment", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Not Found For Split Payment");
					
					session.sendMessage(new TextMessage(jsonResult.toString()));
					session.close();
				}
				checkDetailIdArray = jsonObj.getJSONArray("checkDetailIdArray");
			}
			
			if (!(jsonObj.has("paymentAmount") && !jsonObj.getString("paymentAmount").equals(null))) {
				Logger.writeActivity("Invalid Payment Amount", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Payment Amount");
				
				session.sendMessage(new TextMessage(jsonResult.toString()));
				session.close();
			}
			BigDecimal paymentAmount = new BigDecimal(jsonObj.getString("paymentAmount"));
			
			JSONObject staffDetail = getStaffDetail(user.getUsername());
			if (staffDetail.length() <= 0) {
				Logger.writeActivity("Staff Detail Not Found", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Staff Detail Not Found");
				
				session.sendMessage(new TextMessage(jsonResult.toString()));
				session.close();
			}
			long staffId = staffDetail.getLong("id");
			
			JSONObject storeDetail = getStoreDetail();
			if (storeDetail.length() <= 0) {
				Logger.writeActivity("Store Detail Not Found", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Store Detail Not Found");
				
				session.sendMessage(new TextMessage(jsonResult.toString()));
				session.close();
			}
			long storeId = storeDetail.getLong("id");

			stmt = connection.prepareStatement("select * from `check` where table_number = ? and check_number = ? and check_status in (1, 2);");
			stmt.setInt(1, jsonObj.getInt("tableNo"));
			stmt.setString(2, jsonObj.getString("checkNo"));
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				long checkId = rs.getLong("id");
				String checkNo = rs.getString("check_number");
				BigDecimal grandTotalAmount = rs.getBigDecimal("grand_total_amount");
				BigDecimal depositAmount = rs.getBigDecimal("deposit_amount");
				BigDecimal tenderAmount = rs.getBigDecimal("tender_amount");
				
				stmt.close();
				stmt = connection.prepareStatement("insert into transaction (staff_id,check_id,check_number,transaction_type,payment_method,payment_type,terminal_serial_number,transaction_currency,transaction_amount,transaction_status,created_date) " + 
						"values (?,?,?,?,?,?,?,?,?,?,now());", Statement.RETURN_GENERATED_KEYS);
				stmt.setLong(1, staffId);
				stmt.setLong(2, checkId);
				stmt.setString(3, checkNo);
				stmt.setInt(4, 1);
				stmt.setInt(5, paymentMethod);
				stmt.setInt(6, paymentType);
				stmt.setString(7, terminalSerialNumber);
				stmt.setString(8, "RM");
				stmt.setBigDecimal(9, paymentAmount);
				stmt.setInt(10, transactionStatus);
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
								//transactionResult = iposCard.cardSalePayment(String.format("%04d", storeId), "card-sale", paymentAmount, "0.00", uniqueTranNumber, terminalWifiIPPort, session);
								transactionResult = new JSONObject("{\"tranType\":\"card-sale\",\"uniqueTranNumber\":\"201904300001010000231\",\"responseCode\":\"00\",\"responseMessage\":\"APPROVED\",\"cardResponse\":{\"approvalCode\":\"001784\",\"invoiceNumber\":\"000022\",\"merchantInfo\":null,\"bankTerminalID\":\"99990023\",\"bankMerchantID\":\"000027031314099\",\"cardIssuerName\":\"VISA Wave \",\"cardNumber\":\"************2267\",\"expiryDate\":\"**\\/**\",\"batchNumber\":\"000001\",\"transactionDate\":\"190430\",\"transactionTime\":\"091849\",\"retrievalReferenceNumber\":\"000011100036\",\"cardIssuerID\":null,\"cardHolderName\":\" \\/\",\"AID\":\"A0000000031010\",\"APP\":\"MAYBANK VISA\",\"TC\":\"5B6AD4C0A136DCE2\",\"terminalVerificationResult\":\"3\"},\"qrResponse\":null,\"settlementResponse\":null,\"wifiIP\":\"\",\"wifiPort\":\"\"}");
								transactionResult.put("uniqueTranNumber", uniqueTranNumber);
								System.out.println("Card Transaction Result: "+ transactionResult.toString());
								
								
								if(transactionResult.has("responseCode")) {
									if (transactionResult.getString("responseCode").equals("00")) {
										paymentFlag = true;
										updateTransactionResult = updateTransactionResult(transactionResult,"card");
									} else {
										Logger.writeActivity("Transaction Failed To Perform", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Failed To Perform");
									}
								} else {
									Logger.writeActivity("IPOS cannot be detected.", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "IPOS cannot be detected. Please try again later.");
								}
							} else {
								Logger.writeActivity("Transaction Data Failed To Gather", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "01");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Data Failed To Gather");
							}
						}
						
						if(paymentFlag) {
							if(updateTransactionResult.has(Constant.RESPONSE_CODE)) {
								if (updateTransactionResult.getString(Constant.RESPONSE_CODE).equals("00")) {
									boolean updateCheckResult = false;
									
									if (paymentType == 1) {
										updateCheckResult = updateCheck1(paymentAmount, checkNo, jsonObj.getInt("tableNo"), transactionId, grandTotalAmount, depositAmount, tenderAmount);
									} else if (paymentType == 3) {
										updateCheckResult = updateCheck3(checkDetailIdArray, transactionId, checkNo, paymentAmount, grandTotalAmount, depositAmount, tenderAmount);
									} else {
										updateCheckResult = updateCheck2(paymentType, paymentAmount, checkNo, jsonObj.getInt("tableNo"), transactionId, grandTotalAmount, depositAmount, tenderAmount);
									}
									
									if (updateCheckResult) {
										Logger.writeActivity("Transaction has been successfully performed", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "00");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction has been successfully performed.");
									} else {
										Logger.writeActivity("Check Failed To Close", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Failed To Close");
									}
								} else {
									Logger.writeActivity(updateTransactionResult.getString(Constant.RESPONSE_MESSAGE), ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, updateTransactionResult.getString(Constant.RESPONSE_MESSAGE));
								}
							} else {
									Logger.writeActivity("Transaction Failed To Perform", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Failed To Perform");
							}
						}

					} else {
						Logger.writeActivity("Transaction Id Not Found", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Id Not Found");
					}
				} else {
					Logger.writeActivity("Transaction Failed To Insert", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Failed To Insert");
				}
			} else {
				Logger.writeActivity("Check Not Found", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Not Found");
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
				
		if(session.isOpen()) {
			Logger.writeActivity("Card Payment Response: " + jsonResult.toString(), ECPOS_FOLDER);
			session.sendMessage(new TextMessage(jsonResult.toString()));
			session.close();
		}
	}

	// onOpen
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Logger.writeActivity("Open WS connection successfully.", ECPOS_FOLDER);
	}

	// onError
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		if(session.isOpen()) {
			session.close();
		}
		System.out.println("Error Occured. Connection Closed");
		Logger.writeActivity("WS Connection Failed. Close the connection.", ECPOS_FOLDER);
	}

	// onClose
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("Connection Closed");
		Logger.writeActivity("Close WS connection successfully.", ECPOS_FOLDER);
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
		return null;
	}
	
	private boolean updateCheck1(BigDecimal paymentAmount, String checkNo, int tableNo, long transactionId, BigDecimal grandTotalAmount, BigDecimal depositAmount, BigDecimal tenderAmount) {
		Connection connection = null;
		PreparedStatement stmt = null;
		boolean response = false;
		
		try {
			connection = dataSource.getConnection();
			
			tenderAmount = tenderAmount.add(paymentAmount);
			
			stmt = connection.prepareStatement("update `check` set tender_amount = ?, overdue_amount = ?, check_status = 3, updated_date = now() where check_number = ? and table_number = ? and check_status in (1, 2);");
			stmt.setBigDecimal(1, tenderAmount);
			stmt.setBigDecimal(2, grandTotalAmount.subtract(tenderAmount).subtract(depositAmount));
			stmt.setString(3, checkNo);
			stmt.setInt(4, tableNo);
			int updateCheck = stmt.executeUpdate();
			
			if (updateCheck > 0) {
				stmt.close();
				stmt = connection.prepareStatement("update check_detail set check_detail_status = 3, transaction_id = ?, updated_date = now() " + 
						"where check_id = (select id from `check` where check_number = ? and table_number = ?) and check_number = ? and check_detail_status in (1, 2); ");
				stmt.setLong(1, transactionId);
				stmt.setString(2, checkNo);
				stmt.setInt(3, tableNo);
				stmt.setString(4, checkNo);
				int updateCheckDetail = stmt.executeUpdate();
				
				if (updateCheckDetail > 0) {
					response = true;
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return response;
	}
	
	private boolean updateCheck2(int paymentType, BigDecimal paymentAmount, String checkNo, int tableNo, long transactionId, BigDecimal grandTotalAmount, BigDecimal depositAmount, BigDecimal tenderAmount) {
		Connection connection = null;
		PreparedStatement stmt = null;
		boolean response = false;
		
		try {
			connection = dataSource.getConnection();
			
			String amountType = null;
			BigDecimal paidAmount = new BigDecimal("0.00");
			BigDecimal overdueAmount = new BigDecimal("0.00");
			if (paymentType == 2) {
				amountType = "tender_amount";
				paidAmount = tenderAmount.add(paymentAmount);
				overdueAmount = grandTotalAmount.subtract(paidAmount).subtract(depositAmount);
			} else if (paymentType == 4) {
				amountType = "deposit_amount";
				paidAmount = depositAmount.add(paymentAmount);
				overdueAmount = grandTotalAmount.subtract(paidAmount).subtract(tenderAmount);
			}
			
			stmt = connection.prepareStatement("update `check` set " + amountType + " = ?, overdue_amount = ?, updated_date = now() where check_number = ? and table_number = ? and check_status in (1, 2);");
			stmt.setBigDecimal(1, paidAmount);
			stmt.setBigDecimal(2, overdueAmount);
			stmt.setString(3, checkNo);
			stmt.setInt(4, tableNo);
			int updateCheck = stmt.executeUpdate();
			
			if (updateCheck > 0) {
				response = true;
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return response;
	}
	
	private boolean updateCheck3(JSONArray checkDetailIdArray, long transactionId, String checkNo, BigDecimal paymentAmount, BigDecimal grandTotalAmount, BigDecimal depositAmount, BigDecimal tenderAmount) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		boolean response = false;
		
		try {
			connection = dataSource.getConnection();
			
			String checkDetailIds = "";
			for (int i = 0; i < checkDetailIdArray.length(); i++) {
				checkDetailIds += checkDetailIdArray.getString(i);
				
				if (i != checkDetailIdArray.length()-1) {
					checkDetailIds += " ,";
				}
			}
			
			stmt = connection.prepareStatement("update check_detail set check_detail_status = 3, transaction_id = ?, updated_date = now() where id in ("+ checkDetailIds +") and check_number = ?;");
			stmt.setLong(1, transactionId);
			stmt.setString(2, checkNo);
			int updateCheckDetail = stmt.executeUpdate();
			
			if (updateCheckDetail > 0) {
				stmt.close();
				stmt = connection.prepareStatement("select check_id from check_detail where id in ("+ checkDetailIds +") and check_number = ? group by check_id;");
				stmt.setString(1, checkNo);
				rs = stmt.executeQuery();
				
				if (rs.next()) {
					stmt = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ? and check_detail_status in (1, 2);");
					stmt.setLong(1, rs.getLong("check_id"));
					stmt.setString(2, checkNo);
					rs2 = stmt.executeQuery();
					
					boolean empty = true;
					
					if (rs2.next()) {
						empty = false; 
					}
					
					String checkStatusCondition = "";
					if (empty) {
						checkStatusCondition = "check_status = 3, ";
					}
					
					tenderAmount = tenderAmount.add(paymentAmount);
					
					stmt = connection.prepareStatement("update `check` set tender_amount = ?, overdue_amount = ?, " + checkStatusCondition + "updated_date = now() where id = ? and check_number = ? and check_status in (1, 2);");
					stmt.setBigDecimal(1, tenderAmount);
					stmt.setBigDecimal(2, grandTotalAmount.subtract(tenderAmount).subtract(depositAmount));
					stmt.setLong(3, rs.getLong("check_id"));
					stmt.setString(4, checkNo);
					int updateCheck = stmt.executeUpdate();
					
					if (updateCheck> 0) {
						response = true;
					}
				}
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
		return response;
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
					Logger.writeActivity("Card Sale Payment Response Not Found", ECPOS_FOLDER);
				} else {
					JSONObject cardResponse = transactionResult.getJSONObject("cardResponse");
					
					int transactionStatus = 4;
					if (transactionResult.getString("responseCode").equals("00")) {
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
						Logger.writeActivity("Card Sale Payment Response Successfully Update Transaction Table", ECPOS_FOLDER);
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, transactionResult.getString("responseMessage"));
						Logger.writeActivity(transactionResult.getString("responseMessage"), ECPOS_FOLDER);
					}
				}
			} else if(transactionCategory.equals("qr")) {
				if(transactionResult.getJSONObject("qrResponse").length() == 0) {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "QR Sale Payment Response Not Found");
					Logger.writeActivity("QR Sale Payment Response Not Found", ECPOS_FOLDER);
				} else {
					JSONObject qrResponse = transactionResult.getJSONObject("qrResponse");
					
					int transactionStatus = 4;
					if (transactionResult.getString("responseCode").equals("00")) {
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
						Logger.writeActivity("QR Sale Payment Response Successfully Update Transaction Table", ECPOS_FOLDER);
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, transactionResult.getString("responseMessage"));
						Logger.writeActivity(transactionResult.getString("responseMessage"), ECPOS_FOLDER);
					}
				}
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
