package mpay.my.ecpos_manager_v2.restcontroller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mpay.my.ecpos_manager_v2.constant.Constant;
import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;
import mpay.my.ecpos_manager_v2.utility.ipos.Card;
import mpay.my.ecpos_manager_v2.webutil.UserAuthenticationModel;
import mpay.my.ecpos_manager_v2.webutil.UtilWebComponents;

@RestController
@RequestMapping("/rc/transaction")
public class RestC_transaction {
	
	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	Card iposCard;
	
	@RequestMapping(value = { "/get_accumulated_amount" }, method = { RequestMethod.POST }, produces = "application/json")
	public String getAccumulatedAmount(@RequestBody String data) {
		JSONObject jsonResult = new JSONObject();
		BigDecimal accumulatedAmount = new BigDecimal("0.00");
		BigDecimal itemAmount = new BigDecimal("0.00");
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		
		try {
			connection = dataSource.getConnection();
			
			JSONArray checkDetailIdArray = new JSONObject(data).getJSONArray("checkDetailIdArray");
			
			for (int i = 0; i < checkDetailIdArray.length(); i++) {
				long checkDetailId = checkDetailIdArray.getLong(i);
				
				stmt = connection.prepareStatement("select * from check_detail where id = ?;");
				stmt.setLong(1, checkDetailId);
				rs = stmt.executeQuery();
				
				if (rs.next()) {
					itemAmount = rs.getBigDecimal("total_amount");
					
					stmt.close();
					stmt = connection.prepareStatement("select * from check_detail where parent_check_detail_id = ?;");
					stmt.setLong(1, checkDetailId);
					rs2 = stmt.executeQuery();
					
					while (rs2.next()) {
						itemAmount = itemAmount.add(rs2.getBigDecimal("total_amount"));
						
						stmt = connection.prepareStatement("select * from check_detail where parent_check_detail_id = ?;");
						stmt.setLong(1, rs2.getLong("id"));
						rs3 = stmt.executeQuery();
						
						while (rs3.next()) {
							itemAmount = itemAmount.add(rs3.getBigDecimal("total_amount"));
						}
					}
				}
				accumulatedAmount = accumulatedAmount.add(itemAmount);
			}
			
			jsonResult.put("accumulatedAmount", accumulatedAmount);
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
	
	@RequestMapping(value = { "/card_sale" }, method = { RequestMethod.POST }, produces = "application/json")
	public String submitPayment(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;

		UtilWebComponents webComponent = new UtilWebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			connection = dataSource.getConnection();
			
			JSONObject jsonObj = new JSONObject(data);
			
			int paymentType = -1;
			if (jsonObj.getString("paymentType").equals("full")) {
				paymentType = 1;
			} else if (jsonObj.getString("paymentType").equals("partial")) {
				paymentType = 2;
			} else if (jsonObj.getString("paymentType").equals("deposit")) {
				paymentType = 3;
			} else {
				Logger.writeActivity("Payment Type Not Found", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Payment Type Not Found");
				
				return jsonResult.toString();
			}
			
			int paymentMethod = -1;
			int transactionStatus = 1;
			String terminalSerialNumber = null;
			if (jsonObj.getString("paymentMethod").equals("Cash")) {
				paymentMethod = 1;
				transactionStatus = 3;
			} else if (jsonObj.getString("paymentMethod").equals("Card")) {
				paymentMethod = 2;
				
				terminalSerialNumber = jsonObj.getString("terminalSerialNo");
				if (terminalSerialNumber.equals(null)) {
					Logger.writeActivity("Terminal Serial NUmber Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Terminal Serial Number Not Found");
					
					return jsonResult.toString();
				}
			} else if (jsonObj.getString("paymentMethod").equals("QR")) {
				paymentMethod = 3;
			} else {
				Logger.writeActivity("Payment Method Not Found", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Payment Method Not Found");
				
				return jsonResult.toString();
			}
			
			JSONArray checkDetailIdArray = new JSONArray();
			if (paymentType == 2) {
				checkDetailIdArray = jsonObj.getJSONArray("checkDetailIdArray");
			
				if (checkDetailIdArray.length() < 0) {
					Logger.writeActivity("Item Not Found For Partial Payment", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Not Found For Partial Payment");
					
					return jsonResult.toString();
				}
			}
			
			BigDecimal paymentAmount = new BigDecimal(jsonObj.getString("paymentAmount"));
			if (paymentAmount.equals(null)) {
				Logger.writeActivity("Payment Amount Not Found", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Payment Amount Not Found");
				
				return jsonResult.toString();
			}
			
			String username = user.getUsername();
			
			stmt = connection.prepareStatement("select * from store;");
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				long storeId = rs.getLong("id");
				
				stmt.close();
				stmt = connection.prepareStatement("select * from staff where staff_username = ?;");
				stmt.setString(1, username);
				rs2 = stmt.executeQuery();
	
				if (rs2.next()) {
					long staffId = rs2.getLong("id");
					
					stmt.close();
					stmt = connection.prepareStatement("select * from `check` where table_number = ? and check_number = ? and device_type = 2 and check_status in (1, 2);");
					stmt.setInt(1, jsonObj.getInt("tableNo"));
					stmt.setString(2, jsonObj.getString("checkNo"));
					rs3 = stmt.executeQuery();
					
					if (rs3.next()) {
						long checkId = rs3.getLong("id");
						String checkNo = rs3.getString("check_number");
						
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
							rs4 = stmt.getGeneratedKeys();
							
							if (rs4.next()) {
								long transactionId = rs4.getLong(1);

								JSONObject terminalWifiIPPort = new JSONObject();
								JSONObject transactionResult = new JSONObject();
								JSONObject updateTransactionResult = new JSONObject();
								
								if (paymentMethod == 1) {
									updateTransactionResult.put(Constant.RESPONSE_CODE, "00");
									updateTransactionResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
								} else if (paymentMethod == 2) {
									terminalWifiIPPort = getTerminalWifiIPPort(terminalSerialNumber);
									String uniqueTranNumber = generateUniqueTranNumber(storeId, transactionId);
									
									if (terminalWifiIPPort.length() > 0 && !uniqueTranNumber.equals(null)) {
										transactionResult = iposCard.cardSalePayment(String.format("%04d", storeId), "card-sale", paymentAmount, "0.00", uniqueTranNumber, terminalWifiIPPort);
										
										if (transactionResult.getString("responseCode").equals("00")) {
											updateTransactionResult = updateTransactionResult(transactionResult);
										} else {
											Logger.writeActivity("Transaction Failed To Perform", ECPOS_FOLDER);
											jsonResult.put(Constant.RESPONSE_CODE, "01");
											jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Failed To Perform");
										}
									} else {
										Logger.writeActivity("Transaction Data Failed To Gather", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Data Failed To Gather");
									}
								} else if (paymentMethod == 3) {
//TO DO									
								}
								
								if (updateTransactionResult.getString(Constant.RESPONSE_CODE).equals("00")) {
									boolean updateCheckResult = false;
									
									if (paymentType == 1) {
										updateCheckResult = updateCheck1(paymentType, paymentAmount, checkNo, jsonObj.getInt("tableNo"), transactionId);
									} else if (paymentType == 2) {
										updateCheckResult = updateCheck2(checkNo, checkDetailIdArray, transactionId, paymentType, paymentAmount);
									}
									
									if (updateCheckResult) {
										Logger.writeActivity("Transaction has been successfully performed", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "00");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction has been successfully performed.");
									} else {
										Logger.writeActivity("Check Failed To Update", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Failed To Update");
									}
								} else {
									Logger.writeActivity(updateTransactionResult.getString(Constant.RESPONSE_MESSAGE), ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, updateTransactionResult.getString(Constant.RESPONSE_MESSAGE));
								}
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
				} else {
					Logger.writeActivity("Staff Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Staff Not Found");
				}
			} else {
				Logger.writeActivity("Store Not Found", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Store Not Found");
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
				if (rs4 != null) {rs4.close();rs4 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	public JSONObject getTerminalWifiIPPort(String terminalSerialNumber) {
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
	
	public String generateUniqueTranNumber(long storeId, long transactionId) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String uniqueTranNumber = null;
		
		try {
			connection = dataSource.getConnection();
			
			Calendar calendar = Calendar.getInstance();
			String year = Integer.toString(calendar.get(Calendar.YEAR));
			String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
			String date = Integer.toString(calendar.get(Calendar.DATE));

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
	
	public boolean updateCheck1(int paymentType, BigDecimal paymentAmount, String checkNo, int tableNo, long transactionId) {
		Connection connection = null;
		PreparedStatement stmt = null;
		boolean response = false;
		
		try {
			connection = dataSource.getConnection();
			
			String amountType = null;
			if (paymentType == 3) {
				amountType = "deposit_amount";
			} else {
				amountType = "tender_amount";
			}
			
			stmt = connection.prepareStatement("update `check` set " + amountType + " = ?, check_status = 3, updated_date = now() where check_number = ? and table_number = ? and check_status in (1, 2);");
			stmt.setBigDecimal(1, paymentAmount);
			stmt.setString(2, checkNo);
			stmt.setInt(3, tableNo);
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
	
	public boolean updateCheck2(String checkNo, JSONArray checkDetailIdArray, long transactionId, int paymentType, BigDecimal paymentAmount) {
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
				stmt = connection.prepareStatement("select * from check_detail where id in ("+ checkDetailIds +") and check_number = ?;");
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
					
					if (empty) {
						stmt = connection.prepareStatement("update `check` set tender_amount = ?, check_status = 3, updated_date = now() where id = ? and check_number = ? and check_status in (1, 2);");
						stmt.setBigDecimal(1, paymentAmount);
						stmt.setLong(2, rs.getLong("check_id"));
						stmt.setString(3, checkNo);
						int updateCheck = stmt.executeUpdate();
						
						if (updateCheck> 0) {
							response = true;
						}
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
	
	public JSONObject updateTransactionResult(JSONObject transactionResult) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		JSONObject jsonResult = new JSONObject();
		
		try {
			connection = dataSource.getConnection();
			
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
}