package mpay.ecpos_manager.web.restcontroller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mpay.ecpos_manager.general.constant.Constant;
import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.QRGenerate;
import mpay.ecpos_manager.general.utility.UserAuthenticationModel;
import mpay.ecpos_manager.general.utility.WebComponents;
import mpay.ecpos_manager.general.utility.ipos.Card;
import mpay.ecpos_manager.general.utility.ipos.QR;

@RestController
@RequestMapping("/rc/transaction")
public class RestC_transaction {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();

	@Autowired
	DataSource dataSource;

	@Autowired
	Card iposCard;

	@Autowired
	QR iposQR;

	@RequestMapping(value = { "/get_transaction_list" }, method = { RequestMethod.POST }, headers = "Accept=application/json", produces = "application/json")
	public String getTransactionList(@RequestBody String dataObj, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				JSONObject jsonObj = new JSONObject(dataObj);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				Date startDate = dateFormat.parse(jsonObj.getString("startDate").replaceAll("T", " ").replaceAll("Z", ""));
				Date endDate = dateFormat.parse(jsonObj.getString("endDate").replaceAll("T", " ").replaceAll("Z", ""));
				String paymentMethod = jsonObj.getString("paymentMethod");
				String tsStatus = jsonObj.getString("tsStatus");
				
				System.out.println("jsonObj = " + jsonObj);
				
				connection = dataSource.getConnection();
				StringBuffer sql = new StringBuffer("select t.id,s.staff_name,t.check_number,c.check_ref_no,tt.name as transaction_type,pm.name as payment_method, "
						+ "pt.name as payment_type,case when pm.id = 1 then '-' else case when t.terminal_serial_number is null then '' else t.terminal_serial_number end end as terminal, "
						+ "t.transaction_amount,tss.name as transaction_status, "
						+ "case when pm.id = 1 then t.created_date else case when t.transaction_date is not null and t.transaction_time is not null then "
						+ "concat('20',SUBSTRING(t.transaction_date, 1, 2),'-',SUBSTRING(t.transaction_date, 3, 2),'-',SUBSTRING(t.transaction_date, 5, 2),' ',SUBSTRING(t.transaction_time, 1, 2),':',SUBSTRING(t.transaction_time, 3, 2),':',SUBSTRING(t.transaction_time, 5, 2)) else '' end end as transaction_date "
						+ ", c.receipt_number "
						+ "from transaction t " + "inner join staff s on s.id = t.staff_id "
						+ "inner join `check` c on c.id = t.check_id and c.check_number = t.check_number "
						+ "inner join transaction_type tt on tt.id = t.transaction_type "
						+ "inner join payment_method pm on pm.id = t.payment_method "
						+ "inner join payment_type pt on pt.id = t.payment_type "
						+ "left join terminal on terminal.serial_number = t.terminal_serial_number "
						+ "inner join transaction_settlement_status tss on tss.id = t.transaction_status "
						+ "where (t.transaction_date >= ? and t.transaction_date <= ?) or (t.created_date >= ? and t.created_date <= ?) ");
				
				if (!paymentMethod.isEmpty())
					sql.append("and t.payment_method = "+paymentMethod+" ");
				
				if (!tsStatus.isEmpty())
					sql.append("and t.transaction_status = "+tsStatus+" ");
				
				sql.append("order by t.transaction_date desc;");
				
				stmt = connection.prepareStatement(sql.toString());
				System.out.println("sql = " + sql);
				stmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
				stmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
				stmt.setTimestamp(3, new java.sql.Timestamp(startDate.getTime()));
				stmt.setTimestamp(4, new java.sql.Timestamp(endDate.getTime()));
				rs = stmt.executeQuery();

				while (rs.next()) {
					JSONObject transaction = new JSONObject();
					transaction.put("id", rs.getString("id"));
					transaction.put("staffName", rs.getString("staff_name"));
					transaction.put("checkNumber", rs.getString("check_number"));
					transaction.put("checkNoByday", WebComponents.trimCheckRef(rs.getString("check_ref_no")));
					transaction.put("transactionType", rs.getString("transaction_type"));
					transaction.put("paymentMethod", rs.getString("payment_method") + " (" + rs.getString("terminal") + ")");
					transaction.put("paymentType", rs.getString("payment_type"));
					transaction.put("terminal", rs.getString("terminal"));
					transaction.put("transactionAmount", String.format("%.2f", rs.getBigDecimal("transaction_amount")));
					transaction.put("transactionStatus", rs.getString("transaction_status"));
					transaction.put("transactionDate", rs.getString("transaction_date"));
					transaction.put("receipt_number", rs.getString("receipt_number") == null ? "-" : rs.getString("receipt_number"));

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
				if (rs != null) {rs.close(); rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_dropdown_filter" }, method = {
			RequestMethod.GET }, produces = "application/json")
	public String getDropDownCheckListingFilter(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray dropdownArray = new JSONArray();
		JSONArray dropdownArray2 = new JSONArray();
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
				stmt = connection.prepareStatement("select * from payment_method;");
				rs = stmt.executeQuery();

				while (rs.next()) {
					JSONObject obj = new JSONObject();
					obj.put("id", rs.getInt("id"));
					obj.put("name", rs.getString("name"));
					dropdownArray.put(obj);
				}
				jsonResult.put("payment_method_drop", dropdownArray);

				stmt2 = connection.prepareStatement("select * from transaction_settlement_status;");
				rs2 = stmt2.executeQuery();

				while (rs2.next()) {
					JSONObject obj2 = new JSONObject();
					obj2.put("id", rs2.getInt("id"));
					obj2.put("name", rs2.getString("name"));
					dropdownArray2.put(obj2);
				}
				jsonResult.put("ts_status_drop", dropdownArray2);
				
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
	
	@RequestMapping(value = { "/get_transaction_details" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getTransactionDetails(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "id") String transactionId) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		PreparedStatement stmt5 = null;
		PreparedStatement stmt6 = null;
		PreparedStatement stmt7 = null;
		PreparedStatement stmt8 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rs6 = null;
		ResultSet rs7 = null;
		ResultSet rs8 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				String staffName = user.getName();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("select t.*, tt.name from transaction t inner join transaction_type tt on t.transaction_type = tt.id where t.id = ?;");
				stmt.setString(1, transactionId);
				rs = stmt.executeQuery();

				if (rs.next()) {
					boolean isVoid = false;

					if (rs.getLong("transaction_type") == 2) {
						isVoid = true;
					}

					jsonResult.put("id", rs.getLong("id"));
					jsonResult.put("isVoid", isVoid);

					// Receipt summary
					// JSONObject receiptSummary = new JSONObject();
					jsonResult.put("paymentMethod", rs.getInt("payment_method"));
					if (rs.getInt("payment_method") == 1) {
						JSONObject cashData = new JSONObject();
						cashData.put("cashReceivedAmount", new BigDecimal(rs.getString("received_amount")));
						cashData.put("cashChangeAmount", new BigDecimal(rs.getString("change_amount")));

						jsonResult.put("cashData", cashData);
					} else if (rs.getInt("payment_method") == 2) {
						JSONObject cardData = new JSONObject();
						cardData.put("uid", rs.getString("unique_trans_number"));
						cardData.put("approvalCode", rs.getString("approval_code"));
						cardData.put("mid", rs.getString("bank_mid"));
						cardData.put("tid", rs.getString("bank_tid"));
						cardData.put("date", rs.getString("transaction_date"));
						cardData.put("time", rs.getString("transaction_time"));
						cardData.put("invoiceNo", rs.getString("invoice_number"));
						cardData.put("cardType", rs.getString("card_issuer_name"));
						cardData.put("app", rs.getString("app_label"));
						cardData.put("aid", rs.getString("aid"));
						cardData.put("maskedCardNo", rs.getString("masked_card_number"));
						cardData.put("cardExpiry", rs.getString("card_expiry_date"));
						cardData.put("batchNo", rs.getString("batch_number"));
						cardData.put("rRefNo", rs.getString("rrn"));
						cardData.put("tc", rs.getString("tc"));
						cardData.put("terminalVerification", rs.getString("terminal_verification_result"));

						jsonResult.put("cardData", cardData);
					} else if (rs.getInt("payment_method") == 3) {
						JSONObject qrData = new JSONObject();
						qrData.put("issuerType", rs.getString("qr_issuer_type"));
						qrData.put("uid", rs.getString("unique_trans_number"));
						qrData.put("mid", rs.getString("bank_mid"));
						qrData.put("tid", rs.getString("bank_tid"));
						qrData.put("date", rs.getString("transaction_date"));
						qrData.put("time", rs.getString("transaction_time"));
						qrData.put("traceNo", rs.getString("trace_number"));
						qrData.put("authNo", rs.getString("auth_number"));
						qrData.put("amountMYR", rs.getString("qr_amount_myr"));
						qrData.put("amountRMB", rs.getString("qr_amount_rmb"));
						qrData.put("userID", rs.getString("qr_user_id"));
						qrData.put("refID", rs.getString("qr_ref_id"));

						jsonResult.put("qrData", qrData);

						// qr Image
						if (rs.getString("qr_ref_id") != null) {
							byte[] qrByteData = QRGenerate.generateQRImage(rs.getString("qr_ref_id"), 300, 300);
							byte[] encoded = new Base64().encode(qrByteData);
							String QRImage = "data:image/jpg;base64," + new String(encoded);
							jsonResult.put("qrImg", QRImage);
						}
					}

					// Receipt header
					stmt2 = connection.prepareStatement("select * from `store` limit 1");
					rs2 = stmt2.executeQuery();

					if (rs2.next()) {
						JSONObject receiptHeader = new JSONObject();

						receiptHeader.put("storeName", rs2.getString("store_name"));
						receiptHeader.put("storeLogoPath", rs2.getString("store_logo_path"));
						receiptHeader.put("storeAddress", rs2.getString("store_address"));
						receiptHeader.put("storeContactHpNumber", rs2.getString("store_contact_hp_number"));
						receiptHeader.put("storeCurrency", rs2.getString("store_currency")); // will change if got
						
						jsonResult.put("receiptHeader", receiptHeader);
					}

					// Receipt data - Info
					stmt3 = connection.prepareStatement("select * from `check` c " 
									+ "inner join check_status cs on cs.id = c.check_status "
									+ "where check_number = ? and check_status in (2,3);");
					stmt3.setString(1, rs.getString("check_number"));
					rs3 = stmt3.executeQuery();

					if (rs3.next()) {
						JSONObject receiptData = new JSONObject();
						long id = rs3.getLong("id");

						receiptData.put("checkNo", rs3.getString("check_number"));
						receiptData.put("checkNoByDay", WebComponents.trimCheckRef(rs3.getString("check_ref_no")));
						receiptData.put("tableNo", rs3.getString("table_number") == null ? "-" : rs3.getString("table_number"));
						receiptData.put("createdDate", sdf.format(rs3.getTimestamp("created_date")));
						receiptData.put("totalAmount", new BigDecimal(rs3.getString("total_amount") == null ? "0.00" : rs3.getString("total_amount")));
						receiptData.put("totalAmountWithTax", new BigDecimal(rs3.getString("total_amount_with_tax") == null ? "0.00" : rs3.getString("total_amount_with_tax")));
						receiptData.put("totalAmountWithTaxRoundingAdjustment", new BigDecimal(rs3.getString("total_amount_with_tax_rounding_adjustment") == null ? "0.00" : rs3.getString("total_amount_with_tax_rounding_adjustment")));
						receiptData.put("grandTotalAmount", new BigDecimal(rs3.getString("grand_total_amount") == null ? "0.00" : rs3.getString("grand_total_amount")));
						receiptData.put("status", rs3.getString("name"));
						receiptData.put("tenderAmount", rs3.getString("tender_amount") == null ? "0.00" : rs3.getString("tender_amount"));
						receiptData.put("overdueAmount", rs3.getString("overdue_amount") == null ? "0.00" : rs3.getString("overdue_amount"));
						receiptData.put("staff", staffName);
						receiptData.put("transType", rs.getString("name"));
						
						//Author: Shafiq Irwan
						//Date: 05/10/2020
						//Purpose: add receipt no
						receiptData.put("receipt_number", rs3.getString("receipt_number") == null ? "-" : rs3.getString("receipt_number"));

						jsonResult.put("receiptData", receiptData);

						// Receipt data - Tax
						stmt4 = connection.prepareStatement("select * from tax_charge tc "
								+ "inner join check_tax_charge ctc on ctc.tax_charge_id = tc.id "
								+ "where ctc.check_id = ? and ctc.check_number = ?" 
								+ "order by tc.charge_type;");
						stmt4.setLong(1, id);
						stmt4.setString(2, rs.getString("check_number"));
						rs4 = stmt4.executeQuery();

						JSONArray taxCharges = new JSONArray();
						while (rs4.next()) {
							JSONObject taxCharge = new JSONObject();
							taxCharge.put("name", rs4.getString("tax_charge_name"));
							taxCharge.put("rate", rs4.getBigDecimal("rate"));
							taxCharge.put("chargeAmount", new BigDecimal(rs4.getString("grand_total_charge_amount")));

							taxCharges.put(taxCharge);
						}
						jsonResult.put("taxCharges", taxCharges);

						// Receipt Content - Items
						stmt5 = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id is null and check_detail_status in (2, 3) order by id asc;");
						stmt5.setLong(1, id);
						stmt5.setString(2, rs.getString("check_number"));
						rs5 = stmt5.executeQuery();

						JSONArray grandParentItemArray = new JSONArray();
						while (rs5.next()) {
							long grandParentId = rs5.getLong("id");

							JSONObject grandParentItem = new JSONObject();
							grandParentItem.put("checkDetailId", rs5.getString("id"));
							grandParentItem.put("itemId", rs5.getString("menu_item_id"));
							grandParentItem.put("itemCode", rs5.getString("menu_item_code"));
							grandParentItem.put("itemName", rs5.getString("menu_item_name"));
							grandParentItem.put("itemPrice", rs5.getString("menu_item_price"));
							grandParentItem.put("itemQuantity", rs5.getInt("quantity"));
							grandParentItem.put("totalAmount", rs5.getString("total_amount"));

							stmt6 = connection.prepareStatement("select * from menu_item mi "
									+ "left join menu_item_modifier_group mimg on mimg.menu_item_id = mi.id "
									+ "where mi.id = ?;");
							stmt6.setString(1, rs5.getString("menu_item_id"));
							rs6 = stmt6.executeQuery();

							if (rs6.next()) {
								if (rs6.getInt("menu_item_type") == 0) {
									grandParentItem.put("isAlaCarte", true);

									if (rs6.getLong("menu_item_id") > 0) {
										grandParentItem.put("hasModified", true);
									} else {
										grandParentItem.put("hasModified", false);
									}
								} else {
									grandParentItem.put("isAlaCarte", false);
								}
							} else {
								grandParentItem.put("isAlaCarte", false);
							}

							stmt7 = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id = ? and check_detail_status in (2,3) order by id asc;");
							stmt7.setLong(1, id);
							stmt7.setString(2, rs.getString("check_number"));
							stmt7.setLong(3, grandParentId);
							rs7 = stmt7.executeQuery();

							JSONArray parentItemArray = new JSONArray();
							while (rs7.next()) {
								long parentId = rs7.getLong("id");

								JSONObject parentItem = new JSONObject();
								parentItem.put("itemId", rs7.getString("menu_item_id"));
								parentItem.put("itemCode", rs7.getString("menu_item_code"));
								parentItem.put("itemName", rs7.getString("menu_item_name"));
								parentItem.put("itemPrice", rs7.getString("menu_item_price"));
								parentItem.put("itemQuantity", rs7.getString("quantity"));
								parentItem.put("totalAmount", rs7.getString("total_amount"));

								stmt8 = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id = ? and check_detail_status in (2, 3) order by id asc;");
								stmt8.setLong(1, id);
								stmt8.setString(2, rs.getString("check_number"));
								stmt8.setLong(3, parentId);
								rs8 = stmt8.executeQuery();

								JSONArray childItemArray = new JSONArray();
								while (rs8.next()) {
									JSONObject childItem = new JSONObject();
									childItem.put("itemId", rs8.getString("menu_item_id"));
									childItem.put("itemCode", rs8.getString("menu_item_code"));
									childItem.put("itemName", rs8.getString("menu_item_name"));
									childItem.put("itemPrice", rs8.getString("menu_item_price"));
									childItem.put("itemQuantity", rs8.getString("quantity"));
									childItem.put("totalAmount", rs8.getString("total_amount"));

									childItemArray.put(childItem);
								}
								parentItem.put("childItemArray", childItemArray);
								parentItemArray.put(parentItem);
							}
							grandParentItem.put("parentItemArray", parentItemArray);
							grandParentItemArray.put(grandParentItem);
						}
						jsonResult.put("grandParentItemArray", grandParentItemArray);

						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
					}
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Not Found");
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
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();
				if (stmt5 != null) stmt5.close();
				if (stmt6 != null) stmt6.close();
				if (stmt7 != null) stmt7.close();
				if (stmt8 != null) stmt8.close();
				if (rs != null) {rs.close(); rs = null;}
				if (rs2 != null) {rs2.close(); rs2 = null;}
				if (rs3 != null) {rs3.close(); rs3 = null;}
				if (rs4 != null) {rs4.close(); rs4 = null;}
				if (rs5 != null) {rs5.close(); rs5 = null;}
				if (rs6 != null) {rs6.close(); rs6 = null;}
				if (rs7 != null) {rs7.close(); rs7 = null;}
				if (rs8 != null) {rs8.close(); rs8 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}

	@RequestMapping(value = { "/get_accumulated_amount" }, method = { RequestMethod.POST }, produces = "application/json")
	public String getAccumulatedAmount(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		BigDecimal accumulatedAmount = new BigDecimal("0.00");
		BigDecimal itemAmount = BigDecimal.ZERO;
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		PreparedStatement stmt5 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();

				JSONArray checkDetailIdArray = new JSONObject(data).getJSONArray("checkDetailIdArray");

				for (int i = 0; i < checkDetailIdArray.length(); i++) {
					long checkDetailId = checkDetailIdArray.getLong(i);

					stmt = connection.prepareStatement("select * from check_detail where id = ?;");
					stmt.setLong(1, checkDetailId);
					rs = stmt.executeQuery();

					if (rs.next()) {
						stmt2 = connection.prepareStatement("select * from menu_item where id = ? and backend_id = ?;");
						stmt2.setLong(1, rs.getLong("menu_item_id"));
						stmt2.setString(2, rs.getString("menu_item_code"));
						rs2 = stmt2.executeQuery();

						if (rs2.next()) {
							boolean isItemTaxable = rs2.getBoolean("is_taxable");

							JSONObject charges = new JSONObject();
							JSONArray totalTaxes = new JSONArray();
							JSONArray overallTaxes = new JSONArray();

							if (isItemTaxable) {
								stmt3 = connection.prepareStatement("select tc.* from tax_charge tc "
										+ "inner join charge_type_lookup ctlu on ctlu.charge_type_number = tc.charge_type "
										+ "where tc.is_active = 1;");
								rs3 = stmt3.executeQuery();

								while (rs3.next()) {
									JSONObject taxInfo = new JSONObject();

									if (rs3.getInt("charge_type") == 1) {
										taxInfo.put("id", rs3.getString("id"));
										taxInfo.put("rate", rs3.getString("rate"));

										totalTaxes.put(taxInfo);
									} else if (rs3.getInt("charge_type") == 2) {
										taxInfo.put("id", rs3.getString("id"));
										taxInfo.put("rate", rs3.getString("rate"));

										overallTaxes.put(taxInfo);
									}
								}
								charges.put("totalTaxes", totalTaxes);
								charges.put("overallTaxes", overallTaxes);
							}

							itemAmount = itemAmount.add(taxesCalculation(rs.getBigDecimal("total_amount"), isItemTaxable, charges));

							stmt4 = connection.prepareStatement("select * from check_detail where parent_check_detail_id = ?;");
							stmt4.setLong(1, checkDetailId);
							rs4 = stmt4.executeQuery();

							while (rs4.next()) {
								itemAmount = itemAmount.add(taxesCalculation(rs4.getBigDecimal("total_amount"), isItemTaxable, charges));

								stmt5 = connection.prepareStatement("select * from check_detail where parent_check_detail_id = ?;");
								stmt5.setLong(1, rs4.getLong("id"));
								rs5 = stmt5.executeQuery();

								while (rs5.next()) {
									itemAmount = itemAmount.add(taxesCalculation(rs5.getBigDecimal("total_amount"), isItemTaxable, charges));
								}
							}
						}
					}
					accumulatedAmount = roundToNearest(accumulatedAmount.add(itemAmount));
				}
				jsonResult.put("accumulatedAmount", accumulatedAmount);
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close(); rs = null;}
				if (rs2 != null) {rs2.close(); rs2 = null;}
				if (rs3 != null) {rs3.close(); rs3 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}

	@RequestMapping(value = { "/submit_payment" }, method = { RequestMethod.POST }, produces = "application/json")
	public String submitPayment(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
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

				JSONObject jsonObj = new JSONObject(data);

				int paymentType = -1;
				if (jsonObj.getString("paymentType").equals("full")) {
					paymentType = 1;
				} else if (jsonObj.getString("paymentType").equals("partial")) {
					paymentType = 2;
				} else {
					Logger.writeActivity("Invalid Payment Type", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Payment Type");

					return jsonResult.toString();
				}

				int paymentMethod = -1;
				int transactionStatus = 1;
				String terminalSerialNumber = null;
				BigDecimal receivedAmount = BigDecimal.ZERO;
				if (jsonObj.getString("paymentMethod").equals("Cash")) {
					paymentMethod = 1;
					transactionStatus = 3;
					receivedAmount = new BigDecimal(jsonObj.getString("receivedAmount"));
				} else if (jsonObj.getString("paymentMethod").equals("Card")) {
					paymentMethod = 2;
					receivedAmount = new BigDecimal(jsonObj.getString("paymentAmount"));

					if (!(jsonObj.has("terminalSerialNo") && !jsonObj.getString("terminalSerialNo").equals(null))) {
						Logger.writeActivity("Terminal Serial Number Not Found", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Terminal Serial Number Not Found");

						return jsonResult.toString();
					} else {
						terminalSerialNumber = jsonObj.getString("terminalSerialNo");
					}
				} else if (jsonObj.getString("paymentMethod").equals("QR")) {
					paymentMethod = 3;
					receivedAmount = new BigDecimal(jsonObj.getString("paymentAmount"));

					if (!(jsonObj.has("terminalSerialNo") && !jsonObj.getString("terminalSerialNo").equals(null))) {
						Logger.writeActivity("Terminal Serial Number Not Found", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Terminal Serial Number Not Found");

						return jsonResult.toString();
					} else {
						terminalSerialNumber = jsonObj.getString("terminalSerialNo");
					}
				} else {
					Logger.writeActivity("Invalid Payment Method", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Payment Method");

					return jsonResult.toString();
				}

				BigDecimal paymentAmount = BigDecimal.ZERO;
				if (!(jsonObj.has("paymentAmount") && !jsonObj.getString("paymentAmount").equals(null))) {
					Logger.writeActivity("Invalid Payment Amount", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Payment Amount");

					return jsonResult.toString();
				} else {
					paymentAmount = new BigDecimal(jsonObj.getString("paymentAmount"));
				}

				BigDecimal changeAmount = receivedAmount.subtract(paymentAmount);

				JSONObject staffDetail = getStaffDetail(user.getUsername());
				long staffId = -1;
				if (staffDetail.length() <= 0) {
					Logger.writeActivity("Staff Detail Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Staff Detail Not Found");

					return jsonResult.toString();
				} else {
					staffId = staffDetail.getLong("id");
				}

				JSONObject storeDetail = getStoreDetail();
				long storeId = -1;
				if (storeDetail.length() <= 0) {
					Logger.writeActivity("Store Detail Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Store Detail Not Found");

					return jsonResult.toString();
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
						stmt = connection.prepareStatement("insert into transaction (staff_id,check_id,check_number,transaction_type,payment_method,payment_type,terminal_serial_number,transaction_currency,transaction_amount,received_amount,change_amount,transaction_status,created_date,device_id) "
										+ "values (?,?,?,?,?,?,?,?,?,?,?,?,now(),?);", Statement.RETURN_GENERATED_KEYS);
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
								boolean isCashAlertTriggered = false;

								if (paymentMethod == 1) {
									stmt = connection.prepareStatement("select cash_amount, cash_alert from cash_drawer;");
									rs3 = stmt.executeQuery();

									if (rs3.next()) {
										long cashAlert = rs3.getLong("cash_alert");
										double currentCash = rs3.getDouble("cash_amount");
										double ammendCash = 0.00;
										double newCash = 0.00;
										if (receivedAmount.compareTo(paymentAmount) <= 0) {
											ammendCash = receivedAmount.doubleValue();
										} else {
											ammendCash = paymentAmount.doubleValue();
										}
										newCash = currentCash + ammendCash;

										if (cashAlert > 0 && newCash > cashAlert) {
											isCashAlertTriggered = true;
										}

										stmt = connection.prepareStatement("update cash_drawer set cash_amount = ?;");
										stmt.setDouble(1, newCash);
										stmt.executeUpdate();

										stmt = connection.prepareStatement("insert into cash_drawer_log(cash_amount,new_amount,reference,performed_by) VALUES (?,?,?,?);");
										stmt.setDouble(1, ammendCash);
										stmt.setDouble(2, newCash);
										stmt.setString(3, "Cash From Sale");
										stmt.setLong(4, staffId);
										stmt.executeUpdate();
									}

									paymentFlag = true;
									updateTransactionResult.put(Constant.RESPONSE_CODE, "00");
									updateTransactionResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
								} else if (paymentMethod == 2) {
									terminalWifiIPPort = getTerminalWifiIPPort(terminalSerialNumber);
									String uniqueTranNumber = generateUniqueTranNumber(storeId, transactionId);

									if (!uniqueTranNumber.equals(null)) {
										transactionResult = iposCard.cardSalePayment(String.format("%04d", storeId), "card-sale", paymentAmount, "0.00", uniqueTranNumber, terminalWifiIPPort, null);

										if (transactionResult.has("responseCode")) {
											if (transactionResult.getString("responseCode").equals("00") || transactionResult.getString("responseCode").equals("09")) {
												paymentFlag = true;
												updateTransactionResult = updateTransactionResult(transactionResult, "card");
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
								} else if (paymentMethod == 3) {
									terminalWifiIPPort = getTerminalWifiIPPort(terminalSerialNumber);
									String uniqueTranNumber = generateUniqueTranNumber(storeId, transactionId);
									String qrContent = jsonObj.getString("qrContent");

									if (!uniqueTranNumber.equals(null)) {
										transactionResult = iposQR.qrSalePayment(String.format("%04d", storeId), "qr-sale", paymentAmount, "0.00", uniqueTranNumber, qrContent, terminalWifiIPPort);

										if (transactionResult.has("responseCode")) {
											if (transactionResult.getString("responseCode").equals("00")) {
												paymentFlag = true;
												updateTransactionResult = updateTransactionResult(transactionResult, "qr");
											} else if (transactionResult.getString("responseCode").equals("01")) {
												Logger.writeActivity(transactionResult.getString("responseMessage"), ECPOS_FOLDER);
												jsonResult.put(Constant.RESPONSE_CODE, "01");
												jsonResult.put(Constant.RESPONSE_MESSAGE, transactionResult.getString("responseMessage"));
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

								if (paymentFlag) {
									if (updateTransactionResult.has(Constant.RESPONSE_CODE)) {
										if (updateTransactionResult.getString(Constant.RESPONSE_CODE).equals("00")) {
											JSONObject updateCheckResult = new JSONObject();

											if (paymentType == 1) {
												updateCheckResult = updateCheck1(orderType, paymentAmount, checkNo, jsonObj.getInt("tableNo"), transactionId, grandTotalAmount, tenderAmount);
											} else {
												updateCheckResult = updateCheck2(orderType, paymentType, paymentAmount, checkNo, jsonObj.getInt("tableNo"), transactionId, grandTotalAmount, tenderAmount);
											}

											if (updateCheckResult.getString("status").equals("success")) {
												Logger.writeActivity("Transaction has been successfully performed", ECPOS_FOLDER);
												jsonResult.put(Constant.RESPONSE_CODE, "00");
												jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction has been successfully performed.");
												jsonResult.put("check_status", updateCheckResult.getString("checkStatus"));
												jsonResult.put("change_amount", changeAmount);
												jsonResult.put("is_cash_alert", isCashAlertTriggered);
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
						Logger.writeActivity("Payment Amount Is Greater Than Amount Need To Be Paid", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Payment Amount Is Greater Than Amount Need To Be Paid");
					}
				} else {
					Logger.writeActivity("Check Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Not Found");
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
				if (rs != null) {rs.close(); rs = null;}
				if (rs2 != null) {rs2.close(); rs2 = null;}
				if (rs3 != null) {rs3.close(); rs3 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}

	@RequestMapping(value = { "/void_transaction" }, method = { RequestMethod.POST }, produces = "application/json")
	public String voidTransaction(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
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
				JSONObject jsonObj = new JSONObject(data);

				stmt = connection.prepareStatement("select * from transaction where id = ?;");
				stmt.setLong(1, jsonObj.getLong("transactionId"));
				rs = stmt.executeQuery();

				if (rs.next()) {
					// ady voided
					if (rs.getInt("transaction_status") == 5) {
						jsonResult.put("isVoid", true);
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Successfully Voided");
					} else {

						JSONObject terminalWifiIPPort = getTerminalWifiIPPort(rs.getString("terminal_serial_number"));
						JSONObject storeDetail = getStoreDetail();

						if (storeDetail.length() <= 0) {
							Logger.writeActivity("Store Detail Not Found", ECPOS_FOLDER);
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "Store Detail Not Found");
						} else {

							// cash void
							if (rs.getLong("payment_method") == 1) {
								stmt2 = connection.prepareStatement("update transaction SET transaction_type = ?, transaction_status = 5, updated_date = now() where id = ?");
								stmt2.setLong(1, 2); // 2 for "void"
								stmt2.setLong(2, jsonObj.getLong("transactionId"));
								stmt2.executeUpdate();

								jsonResult.put(Constant.RESPONSE_CODE, "00");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Successfully Voided");
							}

							// card void
							else if (rs.getLong("payment_method") == 2) {
								if (rs.getString("invoice_number") != null) {
									JSONObject cardVoidResponse = iposCard.cardVoidPayment(storeDetail.getString("id"), "card-void", rs.getString("invoice_number"), terminalWifiIPPort);
									System.out.println("Card Voiding Response: " + cardVoidResponse.toString());

									if (cardVoidResponse.has("responseCode")) {
										if (cardVoidResponse.getString("responseCode").equals("00")) {
											// update transaction status
											stmt2 = connection.prepareStatement("update transaction SET transaction_type = ?, transaction_status = 5, updated_date = now() where id = ?");
											stmt2.setLong(1, 2); // 2 for "void"
											stmt2.setLong(2, jsonObj.getLong("transactionId"));
											stmt2.executeUpdate();

											jsonResult.put(Constant.RESPONSE_CODE, "00");
											jsonResult.put(Constant.RESPONSE_MESSAGE,
													"Transaction Successfully Voided");
										} else {
											Logger.writeActivity(cardVoidResponse.getString("responseMessage"), ECPOS_FOLDER);
											jsonResult.put(Constant.RESPONSE_CODE, "01");
											jsonResult.put(Constant.RESPONSE_MESSAGE, cardVoidResponse.getString("responseMessage"));
										}
									} else {
										Logger.writeActivity(cardVoidResponse.getString("responseMessage"), ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, cardVoidResponse.getString("responseMessage"));
									}
								} else {
									Logger.writeActivity("Invoice Number Not Found", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Invoice Number Not Found");
								}
							}

							// qr void
							else if (rs.getLong("payment_method") == 3) {
								if (rs.getString("unique_trans_number") != null && rs.getString("trace_number") != null) {
									JSONObject qrVoidResponse = iposQR.qrVoid(storeDetail.getString("id"), "qr-void", rs.getString("unique_trans_number"), rs.getString("qr_ref_id"), terminalWifiIPPort);
									System.out.println("QR Voiding Response: " + qrVoidResponse.toString());

									if (qrVoidResponse.has("responseCode")) {
										if (qrVoidResponse.getString("responseCode").equals("00")) {
											// update transaction status
											stmt2 = connection.prepareStatement("update transaction SET transaction_type = ?, transaction_status = 5, updated_date = now() where id = ?");
											stmt2.setLong(1, 2); // 2 for "void"
											stmt2.setLong(2, jsonObj.getLong("transactionId"));
											stmt2.executeUpdate();

											jsonResult.put(Constant.RESPONSE_CODE, "00");
											jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Successfully Voided");
										} else {
											Logger.writeActivity(qrVoidResponse.getString("responseMessage"), ECPOS_FOLDER);
											jsonResult.put(Constant.RESPONSE_CODE, "01");
											jsonResult.put(Constant.RESPONSE_MESSAGE, qrVoidResponse.getString("responseMessage"));
										}
									} else {
										Logger.writeActivity(qrVoidResponse.getString("responseMessage"), ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, qrVoidResponse.getString("responseMessage"));
									}
								} else {
									Logger.writeActivity("Transaction Number or Trace Number Not Found", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Number or Trace Number Not Found");
								}
							}

						}

					}
				} else {
					Logger.writeActivity("Transaction Id Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Transaction Id Not Found");
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
					stmt.close();
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}

		System.out.println("Final Result: " + jsonResult.toString());
		return jsonResult.toString();
	}

	@RequestMapping(value = { "/request_settlement" }, method = { RequestMethod.POST }, produces = "application/json")
	public String requestSettlement(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
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

				JSONObject staffDetail = getStaffDetail(user.getUsername());
				if (staffDetail.length() <= 0) {
					Logger.writeActivity("Staff Detail Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Staff Detail Not Found");

					return jsonResult.toString();
				}
				long staffId = staffDetail.getLong("id");

				JSONObject storeDetail = getStoreDetail();
				if (storeDetail.length() <= 0) {
					Logger.writeActivity("Store Detail Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Store Detail Not Found");

					return jsonResult.toString();
				}
				long storeId = storeDetail.getLong("id");

				JSONObject jsonObj = new JSONObject(data);

				if (jsonObj.has("terminalSerialNo") && jsonObj.has("settlementType")) {
					String terminalSerialNo = jsonObj.getString("terminalSerialNo");
					String settlementType = jsonObj.getString("settlementType");
					String niiType = getSettlementNiiType(settlementType);

					stmt = connection.prepareStatement(
							"insert into settlement (staff_id,nii_type,settlement_status,created_date,device_id) "
									+ "values (?,?,1,now(),?);",
							Statement.RETURN_GENERATED_KEYS);
					stmt.setLong(1, staffId);
					stmt.setString(2, settlementType);
					stmt.setLong(3, user.getDeviceId());
					int insertSettlement = stmt.executeUpdate();

					if (insertSettlement > 0) {
						rs = stmt.getGeneratedKeys();

						if (rs.next()) {
							long settlementId = rs.getLong(1);

							JSONObject terminalWifiIPPort = getTerminalWifiIPPort(terminalSerialNo);
							JSONObject settlementResult = iposCard.cardSettlement(settlementId,
									String.format("%04d", storeId), "card-settlement", niiType, terminalWifiIPPort);

							if (settlementResult.getString("responseCode").equals("00")) {
								if (settlementResult.getJSONObject("settlementResponse").length() > 0) {
									JSONObject settlementResponse = settlementResult
											.getJSONObject("settlementResponse");

									int settlementStatus = 4;
									if (settlementResult.getString("responseCode").equals("00")) {
										settlementStatus = 3;
									}

									stmt.close();
									stmt = connection.prepareStatement(
											"update settlement set settlement_status = ?,response_code = ?,response_message = ?,updated_date = now(),wifi_ip = ?, "
													+ "wifi_port = ?,merchant_info = ?,bank_mid = ?,bank_tid = ?,batch_number = ?,transaction_date = ?,transaction_time = ?, "
													+ "batch_total = ?, nii = ? where id = ?;");
									stmt.setInt(1, settlementStatus);
									stmt.setString(2, settlementResult.getString("responseCode"));
									stmt.setString(3, settlementResult.getString("responseMessage"));
									stmt.setString(4, settlementResult.getString("wifiIP"));
									stmt.setString(5, settlementResult.getString("wifiPort"));
									stmt.setString(6, settlementResponse.getString("merchantInfo"));
									stmt.setString(7, settlementResponse.getString("bankMerchantID"));
									stmt.setString(8, settlementResponse.getString("bankTerminalID"));
									stmt.setString(9, settlementResponse.getString("batchNumber"));
									stmt.setString(10, settlementResponse.getString("transactionDate"));
									stmt.setString(11, settlementResponse.getString("transactionTime"));
									stmt.setString(12, settlementResponse.getString("batchTotals"));
									stmt.setString(13, settlementResponse.getString("nii"));
									stmt.setString(14, settlementResult.getString("settlementId"));
									int updateSettlement = stmt.executeUpdate();

									if (updateSettlement > 0) {
										Logger.writeActivity("Settlement has been successfully performed",
												ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "00");
										jsonResult.put(Constant.RESPONSE_MESSAGE,
												"Settlement has been successfully performed.");
									} else {
										Logger.writeActivity("Settlement Failed To Update", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Settlement Failed To Update");
									}
								} else {
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Card Sale Payment Response Not Found");
									Logger.writeActivity("Card Sale Payment Response Not Found", ECPOS_FOLDER);
								}
							} else if (settlementResult.getString("responseCode").equals("BE")) {
								Logger.writeActivity("BATCH EMPTY", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "BE");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "BATCH EMPTY");
							} else {
								Logger.writeActivity("Settlement Failed To Perform", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "01");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Settlement Failed To Perform");
							}
						} else {
							Logger.writeActivity("Settlement Failed To Insert", ECPOS_FOLDER);
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "Settlement Failed To Insert");
						}
					} else {
						Logger.writeActivity("Settlement Failed To Insert", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Settlement Failed To Insert");
					}
				} else {
					Logger.writeActivity("Settlement Request Info Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Settlement Request Info Not Found");
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
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}

	private String getSettlementNiiType(String settlementType) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String niiTypeName = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("select name from nii_type where id = ?;");
			stmt.setString(1, settlementType);
			rs = stmt.executeQuery();

			if (rs.next()) {
				niiTypeName = rs.getString("name");
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close(); rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return niiTypeName;
	}

	public JSONObject getStoreDetail() {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject storeDetail = new JSONObject();

		try {
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("select * from store;");
			rs = stmt.executeQuery();

			if (rs.next()) {
				storeDetail.put("id", rs.getString("id"));
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close(); rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return storeDetail;
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
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close(); rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return staffDetail;
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
				terminalWifiIPPort.put("wifi_IP", rs.getString("wifi_IP"));
				terminalWifiIPPort.put("wifi_Port", rs.getString("wifi_Port"));
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close(); rs = null;}
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
				if (rs != null) {rs.close(); rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return null;
	}

	public JSONObject updateCheck1(int orderType, BigDecimal paymentAmount, String checkNo, int tableNo, long transactionId, BigDecimal grandTotalAmount, BigDecimal tenderAmount) {
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

			stmt = connection.prepareStatement("update `check` set tender_amount = ?, overdue_amount = ?, " + checkStatusCondition + ", updated_date = now() where check_number = ? and "
							+ tableNoCondition + " and check_status in (1, 2);");
			stmt.setBigDecimal(1, tenderAmount);
			stmt.setBigDecimal(2, grandTotalAmount.subtract(tenderAmount));
			stmt.setString(3, checkNo);
			int updateCheck = stmt.executeUpdate();

			if (updateCheck > 0) {
				stmt.close();
				stmt = connection.prepareStatement("update check_detail set check_detail_status = 3, transaction_id = ?, updated_date = now() "
								+ "where check_id = (select id from `check` where check_number = ? and "
								+ tableNoCondition + ") and check_number = ? and check_detail_status in (1, 2); ");
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
		return result;
	}

	public JSONObject updateCheck2(int orderType, int paymentType, BigDecimal paymentAmount, String checkNo, int tableNo, long transactionId, BigDecimal grandTotalAmount, BigDecimal tenderAmount) {
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

			stmt = connection.prepareStatement("update `check` set " + amountType + " = ?, overdue_amount = ?, "
					+ checkStatusCondition + "updated_date = now() where check_number = ? and " + tableNoCondition
					+ " and check_status in (1, 2);");
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
		return result;
	}

	public JSONObject updateTransactionResult(JSONObject transactionResult, String transactionCategory) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		JSONObject jsonResult = new JSONObject();

		try {
			connection = dataSource.getConnection();

			if (transactionCategory.equals("card")) {
				if (transactionResult.getJSONObject("cardResponse").length() == 0) {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Card Sale Payment Response Not Found");
					Logger.writeActivity("Card Sale Payment Response Not Found", ECPOS_FOLDER);
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

					stmt = connection.prepareStatement("update transaction set response_code = ?,response_message = ?,updated_date = now(),wifi_ip = ?,wifi_port = ?, approval_code = ?, "
									+ "bank_mid = ?,bank_tid = ?,transaction_date = ?,transaction_time = ?,invoice_number = ?,merchant_info = ?,card_issuer_name = ?, "
									+ "masked_card_number = ?,card_expiry_date = ?,batch_number = ?,rrn = ?,card_issuer_id = ?,cardholder_name = ?,aid = ?,app_label = ?, "
									+ "tc = ?,terminal_verification_result = ?,transaction_status = ? where unique_trans_number = ? and transaction_type = ?;");
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
						System.out.println("Card Sales Success and Update Table");
						Logger.writeActivity("Card Sale Payment Response Successfully Update Transaction Table", ECPOS_FOLDER);
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, transactionResult.getString("responseMessage"));
						System.out.println("Card Sales Failed to update table: "+transactionResult.getString("responseMessage"));
						Logger.writeActivity(transactionResult.getString("responseMessage"), ECPOS_FOLDER);
					}
				}
			} else if (transactionCategory.equals("qr")) {
				if (transactionResult.getJSONObject("qrResponse").length() == 0) {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "QR Sale Payment Response Not Found");
					Logger.writeActivity("QR Sale Payment Response Not Found", ECPOS_FOLDER);
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
									+ "qr_amount_myr = ?,qr_amount_rmb = ?, auth_number = ?, transaction_status = ? where unique_trans_number = ? and transaction_type = ?;");
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
					stmt.setString(17, qrResponse.getString("authNo"));
					stmt.setInt(18, transactionStatus);
					stmt.setString(19, transactionResult.getString("uniqueTranNumber"));
					stmt.setInt(20, transactionType);
					int updateTransaction = stmt.executeUpdate();

					if (updateTransaction > 0) {
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
						Logger.writeActivity("QR Sale Payment Response Successfully Update Transaction Table",
								ECPOS_FOLDER);
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
				if (rs != null) {rs.close(); rs = null;}
				if (rs2 != null) {rs2.close(); rs2 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}

	public BigDecimal taxesCalculation(BigDecimal amount, boolean isItemTaxable, JSONObject charges) {
		BigDecimal resultAmount = amount;

		try {
			if (isItemTaxable) {
				boolean proceed = false;
				if (!(charges.has("totalTaxes") && !charges.isNull("totalTaxes") && charges.getJSONArray("totalTaxes").length() > 0)) {
					proceed = true;
				} else {
					JSONArray totalTaxes = charges.getJSONArray("totalTaxes");

					for (int i = 0; i < totalTaxes.length(); i++) {
						JSONObject totalTax = totalTaxes.getJSONObject(i);
						BigDecimal totalTaxAmount = amount.multiply(new BigDecimal(totalTax.getString("rate")).divide(new BigDecimal("100")));
						BigDecimal grandTotalTaxAmount = totalTaxAmount.setScale(2, RoundingMode.HALF_UP);

						proceed = true;
						resultAmount = resultAmount.add(grandTotalTaxAmount);
					}
				}

				if (proceed) {
					if (!(charges.has("overallTaxes") && !charges.isNull("overallTaxes") && charges.getJSONArray("overallTaxes").length() > 0)) {
						// Do nothing
					} else {
						JSONArray overallTaxes = charges.getJSONArray("overallTaxes");

						for (int i = 0; i < overallTaxes.length(); i++) {
							JSONObject overallTax = overallTaxes.getJSONObject(i);
							BigDecimal overallTaxAmount = resultAmount.multiply(new BigDecimal(overallTax.getString("rate")).divide(new BigDecimal("100")));
							BigDecimal grandOverallTaxAmount = overallTaxAmount.setScale(2, RoundingMode.HALF_UP);

							resultAmount = resultAmount.add(grandOverallTaxAmount);
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		return resultAmount;
	}

	public BigDecimal roundToNearest(BigDecimal value) {
		double d = value.doubleValue();
		double rounded = Math.round(d * 20.0) / 20.0;

		return BigDecimal.valueOf(rounded);
	}
}