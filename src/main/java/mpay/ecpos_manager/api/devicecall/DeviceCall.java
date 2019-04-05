package mpay.ecpos_manager.api.devicecall;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;

@Service
public class DeviceCall {

	private static String DEVICECALL_FOLDER = Property.getDEVICECALL_FOLDER_NAME();
	
	public BigDecimal roundToNearest(BigDecimal value) {
		double d = value.doubleValue();
		double rounded = Math.round(d * 20.0) / 20.0;
		
		return BigDecimal.valueOf(rounded);
	}
	
	public JSONObject getCheck(Connection connection, String checkNo, String tableNo, int orderType) {
		JSONObject jsonResult = new JSONObject();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			String responseCode = "";
			String responseMessage = "";
			
			String tableNoCondition = "";
			if (!tableNo.isEmpty()) {
				tableNoCondition = " and table_number = '" + tableNo + "' ";
			}
			
			String orderTypeCondition = "";
			if (orderType > 0) {
				orderTypeCondition = " and order_type = " + orderType + " ";
			}
			
			stmt = connection.prepareStatement("select * from `check` where check_number = ? " + tableNoCondition + orderTypeCondition + ";");
			stmt.setString(1, checkNo);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				String checkStatus = rs.getString("check_status");
				
				if (checkStatus.equals("1") || checkStatus.equals("2")) {
					jsonResult.put("checkId", rs.getLong("id"));
					jsonResult.put("checkNo", rs.getString("check_number"));
					responseCode = "00";
					responseMessage = "Check is in pending";
				} else if (checkStatus.equals("3")) {
					responseCode = "EA4";
					responseMessage = "Check is Closed";
				} else if (checkStatus.equals("4")) {
					responseCode = "EA5";
					responseMessage = "Check is Cancelled";
				}
			} else {
				responseCode = "EA3";
				responseMessage = "Check Not Found";
			}
			jsonResult.put("resultCode", responseCode);
			jsonResult.put("resultMessage", responseMessage);
		} catch (Exception e) {
			try {
				jsonResult = new JSONObject();
				jsonResult.put("resultCode", "01");
				jsonResult.put("resultMessage", "Host Error");
			} catch (Exception ex) {}
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}
	
	public JSONObject getOrderInfo(Connection connection, String checkNo) {
		JSONObject jsonResult = new JSONObject();
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
		
		try {
			stmt = connection.prepareStatement("select * from `check` c "
					+ "inner join check_status cs on cs.id = c.check_status "
					+ "where check_number = ?;");
			stmt.setString(1, checkNo);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				long id = rs.getLong("id");
				
				jsonResult.put("checkNo", rs.getString("check_number"));
				jsonResult.put("totalItemQuantity", rs.getInt("total_item_quantity"));
				jsonResult.put("totalAmount", new BigDecimal(rs.getString("total_amount") == null ? "0.00" : rs.getString("total_amount")));
				jsonResult.put("totalAmountWithTax", new BigDecimal(rs.getString("total_amount_with_tax") == null ? "0.00" : rs.getString("total_amount_with_tax")));
				jsonResult.put("totalAmountWithTaxRoundingAdjustment", new BigDecimal(rs.getString("total_amount_with_tax_rounding_adjustment") == null ? "0.00" : rs.getString("total_amount_with_tax_rounding_adjustment")));
				jsonResult.put("grandTotalAmount", new BigDecimal(rs.getString("grand_total_amount") == null ? "0.00" : rs.getString("grand_total_amount")));
				jsonResult.put("status", rs.getString("name"));
				jsonResult.put("depositAmount", rs.getString("deposit_amount") == null ? "0.00" : rs.getString("deposit_amount"));
				jsonResult.put("tenderAmount", rs.getString("tender_amount") == null ? "0.00" : rs.getString("tender_amount"));
				jsonResult.put("overdueAmount", rs.getString("overdue_amount") == null ? "0.00" : rs.getString("overdue_amount"));
				
				stmt2 = connection.prepareStatement("select * from tax_charge tc " + 
						"inner join check_tax_charge ctc on ctc.tax_charge_id = tc.id " + 
						"where ctc.check_id = ? and ctc.check_number = ?" + 
						"order by tc.charge_type;");
				stmt2.setLong(1, id);
				stmt2.setString(2, checkNo);
				rs2 = stmt2.executeQuery();
				
				JSONArray taxCharges = new JSONArray();
				while (rs2.next()) {
					JSONObject taxCharge = new JSONObject();
					taxCharge.put("name", rs2.getString("tax_charge_name"));
					taxCharge.put("rate", rs2.getBigDecimal("rate"));
					taxCharge.put("type", rs2.getBigDecimal("charge_type"));
					taxCharge.put("chargeAmount", new BigDecimal(rs2.getString("grand_total_charge_amount")));
					
					taxCharges.put(taxCharge);
				}
				jsonResult.put("taxCharges", taxCharges);

				stmt3 = connection.prepareStatement("select * from check_detail cd "
						+ "inner join check_status cs on cs.id = cd.check_detail_status "
						+ "where check_id = ? and check_number = ? and parent_check_detail_id is null and check_detail_status != 4 order by cd.id asc;");
				stmt3.setLong(1, id);
				stmt3.setString(2, checkNo);
				rs3 = stmt3.executeQuery();
				
				JSONArray grandParentItemArray = new JSONArray();
				while (rs3.next()) {
					long grandParentId = rs3.getLong(1);
					
					JSONObject grandParentItem = new JSONObject();
					grandParentItem.put("id", rs3.getString("menu_item_code"));
					grandParentItem.put("quantity", rs3.getString("quantity"));
					grandParentItem.put("price", rs3.getString("menu_item_price"));
					grandParentItem.put("totalPrice", rs3.getString("total_amount"));
					grandParentItem.put("status", rs3.getString("name"));
					grandParentItem.put("orderDate", rs3.getTimestamp("created_date"));
					
					stmt4 = connection.prepareStatement("select * from check_detail cd "
							+ "inner join check_status cs on cs.id = cd.check_detail_status "
							+ "where check_id = ? and check_number = ? and parent_check_detail_id = ? and check_detail_status != 4 order by cd.id asc;");
					stmt4.setLong(1, id);
					stmt4.setString(2, checkNo);
					stmt4.setLong(3, grandParentId);
					rs4 = stmt4.executeQuery();
					
					JSONArray parentItemArray = new JSONArray();
					while (rs4.next()) {
						long parentId = rs4.getLong(1);
						
						JSONObject parentItem = new JSONObject();
						parentItem.put("id", rs4.getString("menu_item_code"));
						parentItem.put("quantity", rs4.getString("quantity"));
						parentItem.put("price", rs4.getString("menu_item_price"));
						parentItem.put("totalPrice", rs4.getString("total_amount"));
						parentItem.put("status", rs4.getString("name"));
						
						stmt5 = connection.prepareStatement("select * from check_detail cd "
								+ "inner join check_status cs on cs.id = cd.check_detail_status "
								+ "where check_id = ? and check_number = ? and parent_check_detail_id = ? and check_detail_status != 4 order by cd.id asc;");
						stmt5.setLong(1, id);
						stmt5.setString(2, checkNo);
						stmt5.setLong(3, parentId);
						rs5 = stmt5.executeQuery();
						
						JSONArray childItemArray = new JSONArray();
						while (rs5.next()) {
							JSONObject childItem = new JSONObject();
							childItem.put("id", rs5.getString("menu_item_code"));
							childItem.put("quantity", rs5.getString("quantity"));
							childItem.put("price", rs5.getString("menu_item_price"));
							childItem.put("totalPrice", rs5.getString("total_amount"));
							childItem.put("status", rs5.getString("name"));
							
							childItemArray.put(childItem);
						}
						parentItem.put("items", childItemArray);
						parentItemArray.put(parentItem);
					}
					grandParentItem.put("items", parentItemArray);
					grandParentItemArray.put(grandParentItem);
				}
				jsonResult.put("items", grandParentItemArray);
				
				jsonResult.put("resultCode", "00");
				jsonResult.put("resultMessage", "SUCCESS");
			} else {
				Logger.writeActivity("Check Not Found", DEVICECALL_FOLDER);
				
				jsonResult.put("resultCode", "01");
				jsonResult.put("resultMessage", "Check Not Found");
			}
		} catch (Exception e) {
			try {
				jsonResult = new JSONObject();
				jsonResult.put("resultCode", "01");
				jsonResult.put("resultMessage", "Host Error");
			} catch (Exception ex) {}
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();
				if (stmt5 != null) stmt5.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (rs4 != null) {rs4.close();rs4 = null;}
				if (rs5 != null) {rs5.close();rs5 = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}
	
	public JSONObject checkOrderItem(Connection connection, JSONArray orderData) {
		JSONObject jsonResult = new JSONObject();
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		PreparedStatement stmt5 = null;
		PreparedStatement stmt6 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rs6 = null;
		
		try {
			String responseCode = "E01";
			String responseMessage = "Server error. Please try again later.";
			
			DecimalFormat df = new DecimalFormat("#0.00");
			
			main: for (int i = 0; i < orderData.length(); i++) {
				JSONObject jsonObj = orderData.getJSONObject(i);
				
				if (jsonObj.has("id") && !jsonObj.isNull("id") && !jsonObj.getString("id").isEmpty()) {				
					stmt = connection.prepareStatement("select * from menu_item where backend_id = ?;");
					stmt.setString(1, jsonObj.getString("id"));
					rs = stmt.executeQuery();
					
					if (rs.next()) {
						if (rs.getBoolean("is_active") == true) {
							if (jsonObj.has("type") && !jsonObj.isNull("type") && !jsonObj.getString("type").isEmpty() && rs.getString("menu_item_type").equals(jsonObj.getString("type"))) {
								if (jsonObj.has("price") && !jsonObj.isNull("price") && df.format(rs.getBigDecimal("menu_item_base_price")).equals(df.format(new BigDecimal(jsonObj.getDouble("price"))))) {
									if (jsonObj.getString("type").equals("0")) {
										//A La Carte
										stmt2 = connection.prepareStatement("select * from menu_item_modifier_group where menu_item_id = ?;");
										stmt2.setString(1, rs.getString("id"));
										rs2 = stmt2.executeQuery();
										
										if(rs2.next()) {
											if (jsonObj.has("sub") && !jsonObj.isNull("sub") && jsonObj.getJSONArray("sub").length() > 0) {
												for (int j = 0; j < jsonObj.getJSONArray("sub").length(); j++) {
													JSONObject modifier = jsonObj.getJSONArray("sub").getJSONObject(j);
													
													if (modifier.has("id") && !modifier.isNull("id") && !modifier.getString("id").isEmpty()) {
														stmt3 = connection.prepareStatement("select mg.is_active as mg_is_active, mi.* from menu_item_modifier_group mimg " + 
																"inner join modifier_group mg on mimg.modifier_group_id = mg.id " + 
																"inner join modifier_item_sequence mis on mis.modifier_group_id = mg.id " + 
																"inner join menu_item mi on mi.id = mis.menu_item_id " + 
																"where mimg.menu_item_id = ? and mi.backend_id = ?;");
														stmt3.setString(1, rs.getString("id"));
														stmt3.setString(2, modifier.getString("id"));
														rs3 = stmt3.executeQuery();
													
														if (rs3.next()) {
															if (rs3.getBoolean("mg_is_active") == true && rs3.getBoolean("is_active") == true) {
																if (rs3.getString("menu_item_type").equals("2")) {
																	if (modifier.has("price") && !modifier.isNull("price") && df.format(rs3.getBigDecimal("menu_item_base_price")).equals(df.format(new BigDecimal(modifier.getDouble("price"))))) {
																		responseCode = "00";
																		responseMessage = "Item Details Matched";
																	} else {
																		responseCode = "E05";
																		responseMessage = "Modifier Item Price Not Match (modifier id = " + rs3.getString("id") + ")";
																		break main;
																	}
																} else {
																	responseCode = "E04";
																	responseMessage = "Modifier Item Type Not Match (modifier id = " + rs3.getString("id") + ")";
																	break main;
																}
															} else {
																responseCode = "E03";
																responseMessage = "Modifier Item Not Active (modifier id = " + rs3.getString("id") + ")";
																break main;
															}
														} else {
															responseCode = "E02";
															responseMessage = "Modifier Item Not Exist (id = " + jsonObj.getString("id") + ")";
															break main;
														}
													} else {
														responseCode = "E06";
														responseMessage = "Modifier Item Not Found In Request (id = " + jsonObj.getString("id") + ")";
														break main;
													}
												}
											} else {
												responseCode = "E06";
												responseMessage = "Item Modifier Not Found In Request (id = " + jsonObj.getString("id") + ")";
												break main;
											}
										} else {
											if (jsonObj.has("sub") && !jsonObj.isNull("sub") && jsonObj.getJSONArray("sub").length() > 0) {
												responseCode = "E06";
												responseMessage = "Item Modifier Not Found In Database (id = " + jsonObj.getString("id") + ")";
												break main;
											} else {
												responseCode = "00";
												responseMessage = "Item Details Matched";
											}
										}
									} else if (jsonObj.getString("type").equals("1")) {
										//Combo
										
										//get tier quantity
										stmt2 = connection.prepareStatement("select * from combo_detail where menu_item_id = ?;");
										stmt2.setString(1, rs.getString("id"));
										rs2 = stmt2.executeQuery();
										
										JSONArray comboTiers = new JSONArray();
										while (rs2.next()) {
											JSONObject comboTierInfo = new JSONObject();
											comboTierInfo.put("combo_detail_id", rs2.getString("id"));
											comboTierInfo.put("combo_detail_quantity", rs2.getInt("combo_detail_quantity"));
											
											comboTiers.put(comboTierInfo);
										}
										
										//combo items details checking
										if (jsonObj.has("sub") && !jsonObj.isNull("sub") && jsonObj.getJSONArray("sub").length() > 0) {
											for (int j = 0; j < jsonObj.getJSONArray("sub").length(); j++) {
												JSONObject comboItem = jsonObj.getJSONArray("sub").getJSONObject(j);
												
												if (comboItem.has("combo_detail_id") && !comboItem.isNull("combo_detail_id")) {
													stmt3 = connection.prepareStatement("select cid.* from combo_detail cd " + 
															"inner join combo_item_detail cid on cid.combo_detail_id = cd.id " + 
															"where cd.id = ? and cd.menu_item_id = ?;");
													stmt3.setLong(1, comboItem.getLong("combo_detail_id"));
													stmt3.setString(2, rs.getString("id"));
													rs3 = stmt3.executeQuery();
													
													if (rs3.next()) {
														if (rs3.getString("menu_item_id") != null && !rs3.getString("menu_item_id").isEmpty()) {
															stmt4 = connection.prepareStatement("select mi.* from combo_item_detail cid " + 
																	"inner join menu_item mi on mi.id = cid.menu_item_id " + 
																	"where cid.id = ? and mi.backend_id = ?;");
														} else if (rs3.getString("menu_item_group_id") != null && !rs3.getString("menu_item_group_id").isEmpty()) {
															stmt4 = connection.prepareStatement("select mi.* from combo_item_detail cid " + 
																	"inner join menu_item_group mig on mig.id = cid.menu_item_group_id " + 
																	"inner join menu_item_group_sequence migs on migs.menu_item_group_id = mig.id " + 
																	"inner join menu_item mi on mi.id = migs.menu_item_id " + 
																	"where cid.id = ? and mi.backend_id = ?");
														}
														stmt4.setString(1, rs3.getString("id"));
														stmt4.setString(2, comboItem.getString("id"));
														rs4 = stmt4.executeQuery();
														
														if (rs4.next()) {
															if (rs4.getBoolean("is_active") == true) {
																if (jsonObj.has("price") && !jsonObj.isNull("price") && df.format(rs.getBigDecimal("menu_item_base_price")).equals(df.format(new BigDecimal(jsonObj.getDouble("price"))))) {
																	stmt5 = connection.prepareStatement("select * from menu_item_modifier_group where menu_item_id = ?;");
																	stmt5.setString(1, rs4.getString("id"));
																	rs5 = stmt5.executeQuery();
																	
																	if(rs5.next()) {
																		if (comboItem.has("sub") && !comboItem.isNull("sub") && comboItem.getJSONArray("sub").length() > 0) {
																			for (int k = 0; k < comboItem.getJSONArray("sub").length(); k++) {
																				JSONObject modifier = comboItem.getJSONArray("sub").getJSONObject(k);
																				
																				if (modifier.has("id") && !modifier.isNull("id") && !modifier.getString("id").isEmpty()) {
																					stmt6 = connection.prepareStatement("select mg.is_active as mg_is_active, mi.* from menu_item_modifier_group mimg " + 
																							"inner join modifier_group mg on mimg.modifier_group_id = mg.id " + 
																							"inner join modifier_item_sequence mis on mis.modifier_group_id = mg.id " + 
																							"inner join menu_item mi on mi.id = mis.menu_item_id " + 
																							"where mimg.menu_item_id = ? and mi.backend_id = ?;");
																					stmt6.setString(1, rs4.getString("id"));
																					stmt6.setString(2, modifier.getString("id"));
																					rs6 = stmt6.executeQuery();
																				
																					if (rs6.next()) {
																						if (rs6.getBoolean("mg_is_active") == true && rs6.getBoolean("is_active") == true) {
																							if (rs6.getString("menu_item_type").equals("2")) {
																								if (modifier.has("price") && !modifier.isNull("price") && df.format(rs6.getBigDecimal("menu_item_base_price")).equals(df.format(new BigDecimal(modifier.getDouble("price"))))) {
																									responseCode = "00";
																									responseMessage = "Combo Item Details Matched";
																								} else {
																									responseCode = "E05";
																									responseMessage = "Combo Modifier Item Price Not Match (modifier id = " + rs6.getString("id") + ")";
																									break main;
																								}
																							} else {
																								responseCode = "E04";
																								responseMessage = "Combo Modifier Item Type Not Match (modifier id = " + rs6.getString("id") + ")";
																								break main;
																							}
																						} else {
																							responseCode = "E03";
																							responseMessage = "Combo Modifier Item Not Active (modifier id = " + rs6.getString("id") + ")";
																							break main;
																						}
																					} else {
																						responseCode = "E02";
																						responseMessage = "Combo Modifier Item Not Exist (id = " + jsonObj.getString("id") + ")";
																						break main;
																					}
																				} else {
																					responseCode = "E06";
																					responseMessage = "Combo Modifier Item Not Found In Request (id = " + jsonObj.getString("id") + ")";
																					break main;
																				}
																			}
																		} else {
																			responseCode = "E06";
																			responseMessage = "Combo Item Modifier Not Found In Request (id = " + jsonObj.getString("id") + ")";
																			break main;
																		}
																	} else {
																		if (comboItem.has("sub") && !comboItem.isNull("sub") && comboItem.getJSONArray("sub").length() > 0) {
																			responseCode = "E06";
																			responseMessage = "Combo Item Modifier Not Found In Database (id = " + jsonObj.getString("id") + ")";
																			break main;
																		} else {
																			responseCode = "00";
																			responseMessage = "Combo Item Details Matched";
																		}
																	}
																} else {
																	responseCode = "E05";
																	responseMessage = "Combo Item Price Not Match (id = " + jsonObj.getString("id") + ")";
																	break main;
																}
															} else {
																responseCode = "E03";
																responseMessage = "Combo Item Not Active (combo id = " + jsonObj.getString("id") + ")";
																break main;
															}
														} else {
															responseCode = "E06";
															responseMessage = "Combo Item Not Found (combo id = " + jsonObj.getString("id") + ")";
															break main;
														}
													} else {
														responseCode = "E06";
														responseMessage = "Combo Item Not Found (combo id = " + jsonObj.getString("id") + ")";
														break main;
													}
												} else {
													responseCode = "E06";
													responseMessage = "Combo Item Not Found In Request (combo id = " + jsonObj.getString("id") + ")";
													break main;
												}
												//deduct tier quantity
												for (int k = 0; k < comboTiers.length(); k++) {
													JSONObject comboTierInfo = comboTiers.getJSONObject(k);
													
													if (comboTierInfo.getLong("combo_detail_id") == (comboItem.getLong("combo_detail_id"))) {
														int deductedQuantity = comboTierInfo.getInt("combo_detail_quantity")-1;
														comboTierInfo.put("combo_detail_quantity", deductedQuantity);
													}
												}
											}
										} else {
											responseCode = "E06";
											responseMessage = "Item Tier Not Found In Request (id = " + jsonObj.getString("id") + ")";
											break main;
										}
										
										//check if tier quantity equal to 0, if yes then success else fail
										if (responseCode.equals("00")) {
											for (int j = 0; j < comboTiers.length(); j++) {
												JSONObject comboTierInfo = comboTiers.getJSONObject(j);
												
												if (comboTierInfo.getInt("combo_detail_quantity") != 0) {
													responseCode = "E07";
													responseMessage = "Combo Item Quantity Not Match (combo id = " + jsonObj.getString("id") + ")";
													break main;
												}
											}
										}
									} else {
										responseCode = "E04";
										responseMessage = "Item Type Not Match (id = " + jsonObj.getString("id") + ")";
										break main;
									}
								} else {
									responseCode = "E05";
									responseMessage = "Item Price Not Match (id = " + jsonObj.getString("id") + ")";
									break main;
								}
							} else {
								responseCode = "E04";
								responseMessage = "Item Type Not Match (id = " + jsonObj.getString("id") + ")";
								break main;
							}
						} else {
							responseCode = "E03";
							responseMessage = "Item Not Active (id = " + jsonObj.getString("id") + ")";
							break main;
						}
					} else {
						responseCode = "E02";
						responseMessage = "Item Not Exist (id = " + jsonObj.getString("id") + ")";
						break main;
					}
				} else {
					responseCode = "E06";
					responseMessage = "Item Not Found In Request";
					break main;
				}
			}
			jsonResult.put("resultCode", responseCode);
			jsonResult.put("resultMessage", responseMessage);
		} catch (Exception e) {
			try {
				jsonResult = new JSONObject();
				jsonResult.put("resultCode", "01");
				jsonResult.put("resultMessage", "Host Error");
			} catch (Exception ex) {}
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();
				if (stmt5 != null) stmt5.close();
				if (stmt6 != null) stmt6.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (rs4 != null) {rs4.close();rs4 = null;}
				if (rs5 != null) {rs5.close();rs5 = null;}
				if (rs6 != null) {rs6.close();rs6 = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}
	
	public JSONObject submitOrderItem(Connection connection, long checkId, String checkNo, String deviceType, JSONArray orderData) {
		JSONObject jsonResult = new JSONObject();
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		PreparedStatement stmt5 = null;
		PreparedStatement stmt6 = null;
		PreparedStatement stmtA = null;
		PreparedStatement stmtB = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rs6 = null;
		ResultSet rsA = null;
		ResultSet rsB = null;
		
		try {
			String responseCode = "E01";
			String responseMessage = "Server error. Please try again later.";
			
			DecimalFormat df = new DecimalFormat("#0.00");
			
			int deviceId = -1;
			if (deviceType.equals("byod")) {
				deviceId = 2;
			} else if (deviceType.equals("kiosk")) {
				deviceId = 3;
			}

			main: for (int i = 0; i < orderData.length(); i++) {
				JSONObject jsonObj = orderData.getJSONObject(i);
				
				if (jsonObj.has("id") && !jsonObj.isNull("id") && !jsonObj.getString("id").isEmpty()) {			
					int orderQuantity = jsonObj.getInt("quantity");
					
					stmt = connection.prepareStatement("select * from menu_item where backend_id = ?;");
					stmt.setString(1, jsonObj.getString("id"));
					rs = stmt.executeQuery();
					
					if (rs.next()) {
						if (rs.getBoolean("is_active") == true) {
							if (jsonObj.has("type") && !jsonObj.isNull("type") && !jsonObj.getString("type").isEmpty() && rs.getString("menu_item_type").equals(jsonObj.getString("type"))) {
								if (jsonObj.has("price") && !jsonObj.isNull("price") && df.format(rs.getBigDecimal("menu_item_base_price")).equals(df.format(new BigDecimal(jsonObj.getDouble("price"))))) {
									BigDecimal totalAmount = rs.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(orderQuantity));
									boolean isItemTaxable = rs.getBoolean("is_taxable");

									JSONObject charges = new JSONObject();
									JSONArray totalTaxes = new JSONArray();
									JSONArray overallTaxes = new JSONArray();

									if (isItemTaxable) {
										stmtA = connection.prepareStatement("select tc.* from tax_charge tc " + 
												"inner join charge_type_lookup ctlu on ctlu.charge_type_number = tc.charge_type " + 
												"where tc.is_active = 1;");
										rsA = stmtA.executeQuery();

										while (rsA.next()) {
											JSONObject taxInfo = new JSONObject();
											
											if (rsA.getInt("charge_type") == 1) {
												taxInfo.put("id", rsA.getString("id"));
												taxInfo.put("rate", rsA.getString("rate"));
												
												totalTaxes.put(taxInfo);
											} else if (rsA.getInt("charge_type") == 2) {
												taxInfo.put("id", rsA.getString("id"));
												taxInfo.put("rate", rsA.getString("rate"));
												
												overallTaxes.put(taxInfo);
											}
										}
										charges.put("totalTaxes", totalTaxes);
										charges.put("overallTaxes", overallTaxes);
									}
									Logger.writeActivity("isItemTaxable: " + isItemTaxable, DEVICECALL_FOLDER);
									Logger.writeActivity("charges: " + charges, DEVICECALL_FOLDER);

									stmtB = connection.prepareStatement("insert into check_detail (check_id,check_number,device_type,menu_item_id,menu_item_code,menu_item_name,menu_item_price,quantity,total_amount,check_detail_status,created_date) "
											+ "values (?,?,?,?,?,?,?,?,?,1,now());", Statement.RETURN_GENERATED_KEYS);
									stmtB.setLong(1, checkId);
									stmtB.setString(2, checkNo);
									stmtB.setInt(3, deviceId);  
									stmtB.setLong(4, rs.getLong("id"));
									stmtB.setString(5, rs.getString("backend_id"));
									stmtB.setString(6, rs.getString("menu_item_name"));
									stmtB.setString(7, rs.getString("menu_item_base_price"));
									stmtB.setInt(8, orderQuantity);
									stmtB.setBigDecimal(9, totalAmount);
									int insert1stCheckDetail = stmtB.executeUpdate();

									if (insert1stCheckDetail > 0) {
										rsB = stmtB.getGeneratedKeys();

										if (rsB.next()) {
											long parentCheckDetailId = rsB.getLong(1);

											boolean updateCheck = updateCheck(connection, checkId, checkNo, orderQuantity, totalAmount, isItemTaxable, charges);

											if (updateCheck) {
												if (jsonObj.getString("type").equals("0")) {
													//A La Carte
													stmt2 = connection.prepareStatement("select * from menu_item_modifier_group where menu_item_id = ?;");
													stmt2.setString(1, rs.getString("id"));
													rs2 = stmt2.executeQuery();
													
													if(rs2.next()) {
														if (jsonObj.has("sub") && !jsonObj.isNull("sub") && jsonObj.getJSONArray("sub").length() > 0) {
															for (int j = 0; j < jsonObj.getJSONArray("sub").length(); j++) {
																JSONObject modifier = jsonObj.getJSONArray("sub").getJSONObject(j);
																
																if (modifier.has("id") && !modifier.isNull("id") && !modifier.getString("id").isEmpty()) {
																	stmt3 = connection.prepareStatement("select mg.is_active as mg_is_active, mi.* from menu_item_modifier_group mimg " + 
																			"inner join modifier_group mg on mimg.modifier_group_id = mg.id " + 
																			"inner join modifier_item_sequence mis on mis.modifier_group_id = mg.id " + 
																			"inner join menu_item mi on mi.id = mis.menu_item_id " + 
																			"where mimg.menu_item_id = ? and mi.backend_id = ?;");
																	stmt3.setString(1, rs.getString("id"));
																	stmt3.setString(2, modifier.getString("id"));
																	rs3 = stmt3.executeQuery();
																
																	if (rs3.next()) {
																		if (rs3.getBoolean("mg_is_active") == true && rs3.getBoolean("is_active") == true) {
																			if (rs3.getString("menu_item_type").equals("2")) {
																				if (modifier.has("price") && !modifier.isNull("price") && df.format(rs3.getBigDecimal("menu_item_base_price")).equals(df.format(new BigDecimal(modifier.getDouble("price"))))) {
																					long modifierCheckDetailId = insertChildCheckDetail(connection, deviceId, checkId, checkNo, parentCheckDetailId, modifier, orderQuantity, isItemTaxable, charges);

																					if (modifierCheckDetailId > 0) {
																						responseCode = "00";
																						responseMessage = "Item Successfully Ordered";
																					} else {
																						responseCode = "EB1";
																						responseMessage = "Check Detail Failed To Insert";
																						break main;																				
																					}
																				} else {
																					responseCode = "E05";
																					responseMessage = "Modifier Item Price Not Match (modifier id = " + rs3.getString("id") + ")";
																					break main;
																				}
																			} else {
																				responseCode = "E04";
																				responseMessage = "Modifier Item Type Not Match (modifier id = " + rs3.getString("id") + ")";
																				break main;
																			}
																		} else {
																			responseCode = "E03";
																			responseMessage = "Modifier Item Not Active (modifier id = " + rs3.getString("id") + ")";
																			break main;
																		}
																	} else {
																		responseCode = "E02";
																		responseMessage = "Modifier Item Not Exist (id = " + jsonObj.getString("id") + ")";
																		break main;
																	}
																} else {
																	responseCode = "E06";
																	responseMessage = "Modifier Item Not Found In Request (id = " + jsonObj.getString("id") + ")";
																	break main;
																}
															}
														} else {
															responseCode = "E06";
															responseMessage = "Item Modifier Not Found In Request (id = " + jsonObj.getString("id") + ")";
															break main;
														}
													} else {
														if (jsonObj.has("sub") && !jsonObj.isNull("sub") && jsonObj.getJSONArray("sub").length() > 0) {
															responseCode = "E06";
															responseMessage = "Item Modifier Not Found In Database (id = " + jsonObj.getString("id") + ")";
															break main;
														} else {
															responseCode = "00";
															responseMessage = "Item Successfully Ordered";
														}
													}
												} else if (jsonObj.getString("type").equals("1")) {
													//Combo
													
													//get tier quantity
													stmt2 = connection.prepareStatement("select * from combo_detail where menu_item_id = ?;");
													stmt2.setString(1, rs.getString("id"));
													rs2 = stmt2.executeQuery();
													
													JSONArray comboTiers = new JSONArray();
													while (rs2.next()) {
														JSONObject comboTierInfo = new JSONObject();
														comboTierInfo.put("combo_detail_id", rs2.getString("id"));
														comboTierInfo.put("combo_detail_quantity", rs2.getInt("combo_detail_quantity"));
														
														comboTiers.put(comboTierInfo);
													}
													
													//combo items details checking
													if (jsonObj.has("sub") && !jsonObj.isNull("sub") && jsonObj.getJSONArray("sub").length() > 0) {
														for (int j = 0; j < jsonObj.getJSONArray("sub").length(); j++) {
															JSONObject comboItem = jsonObj.getJSONArray("sub").getJSONObject(j);
															
															if (comboItem.has("combo_detail_id") && !comboItem.isNull("combo_detail_id")) {
																stmt3 = connection.prepareStatement("select cid.* from combo_detail cd " + 
																		"inner join combo_item_detail cid on cid.combo_detail_id = cd.id " + 
																		"where cd.id = ? and cd.menu_item_id = ?;");
																stmt3.setLong(1, comboItem.getLong("combo_detail_id"));
																stmt3.setString(2, rs.getString("id"));
																rs3 = stmt3.executeQuery();
																
																if (rs3.next()) {
																	if (rs3.getString("menu_item_id") != null && !rs3.getString("menu_item_id").isEmpty()) {
																		stmt4 = connection.prepareStatement("select mi.* from combo_item_detail cid " + 
																				"inner join menu_item mi on mi.id = cid.menu_item_id " + 
																				"where cid.id = ? and mi.backend_id = ?;");
																	} else if (rs3.getString("menu_item_group_id") != null && !rs3.getString("menu_item_group_id").isEmpty()) {
																		stmt4 = connection.prepareStatement("select mi.* from combo_item_detail cid " + 
																				"inner join menu_item_group mig on mig.id = cid.menu_item_group_id " + 
																				"inner join menu_item_group_sequence migs on migs.menu_item_group_id = mig.id " + 
																				"inner join menu_item mi on mi.id = migs.menu_item_id " + 
																				"where cid.id = ? and mi.backend_id = ?");
																	}
																	stmt4.setString(1, rs3.getString("id"));
																	stmt4.setString(2, comboItem.getString("id"));
																	rs4 = stmt4.executeQuery();
																	
																	if (rs4.next()) {
																		if (rs4.getBoolean("is_active") == true) {
																			if (jsonObj.has("price") && !jsonObj.isNull("price") && df.format(rs.getBigDecimal("menu_item_base_price")).equals(df.format(new BigDecimal(jsonObj.getDouble("price"))))) {
																				long childCheckDetailId = insertChildCheckDetail(connection, deviceId, checkId, checkNo, parentCheckDetailId, comboItem, orderQuantity, isItemTaxable, charges);
	
																				if (childCheckDetailId > 0) {
																					stmt5 = connection.prepareStatement("select * from menu_item_modifier_group where menu_item_id = ?;");
																					stmt5.setString(1, rs4.getString("id"));
																					rs5 = stmt5.executeQuery();
																					
																					if(rs5.next()) {
																						if (comboItem.has("sub") && !comboItem.isNull("sub") && comboItem.getJSONArray("sub").length() > 0) {
																							for (int k = 0; k < comboItem.getJSONArray("sub").length(); k++) {
																								JSONObject modifier = comboItem.getJSONArray("sub").getJSONObject(k);
																								
																								if (modifier.has("id") && !modifier.isNull("id") && !modifier.getString("id").isEmpty()) {
																									stmt6 = connection.prepareStatement("select mg.is_active as mg_is_active, mi.* from menu_item_modifier_group mimg " + 
																											"inner join modifier_group mg on mimg.modifier_group_id = mg.id " + 
																											"inner join modifier_item_sequence mis on mis.modifier_group_id = mg.id " + 
																											"inner join menu_item mi on mi.id = mis.menu_item_id " + 
																											"where mimg.menu_item_id = ? and mi.backend_id = ?;");
																									stmt6.setString(1, rs4.getString("id"));
																									stmt6.setString(2, modifier.getString("id"));
																									rs6 = stmt6.executeQuery();
																								
																									if (rs6.next()) {
																										if (rs6.getBoolean("mg_is_active") == true && rs6.getBoolean("is_active") == true) {
																											if (rs6.getString("menu_item_type").equals("2")) {
																												if (modifier.has("price") && !modifier.isNull("price") && df.format(rs6.getBigDecimal("menu_item_base_price")).equals(df.format(new BigDecimal(modifier.getDouble("price"))))) {
																													long modifierCheckDetailId = insertChildCheckDetail(connection, deviceId, checkId, checkNo, childCheckDetailId, modifier, orderQuantity, isItemTaxable, charges);
																													
																													if (modifierCheckDetailId > 0) {
																														responseCode = "00";
																														responseMessage = "Item Successfully Ordered";
																													} else {
																														responseCode = "EB1";
																														responseMessage = "Check Detail Failed To Insert";
																														break main;																				
																													}
																												} else {
																													responseCode = "E05";
																													responseMessage = "Combo Modifier Item Price Not Match (modifier id = " + rs6.getString("id") + ")";
																													break main;
																												}
																											} else {
																												responseCode = "E04";
																												responseMessage = "Combo Modifier Item Type Not Match (modifier id = " + rs6.getString("id") + ")";
																												break main;
																											}
																										} else {
																											responseCode = "E03";
																											responseMessage = "Combo Modifier Item Not Active (modifier id = " + rs6.getString("id") + ")";
																											break main;
																										}
																									} else {
																										responseCode = "E02";
																										responseMessage = "Combo Modifier Item Not Exist (id = " + jsonObj.getString("id") + ")";
																										break main;
																									}
																								} else {
																									responseCode = "E06";
																									responseMessage = "Combo Modifier Item Not Found In Request (id = " + jsonObj.getString("id") + ")";
																									break main;
																								}
																							}
																						} else {
																							responseCode = "E06";
																							responseMessage = "Combo Item Modifier Not Found In Request (id = " + jsonObj.getString("id") + ")";
																							break main;
																						}
																					} else {
																						if (comboItem.has("sub") && !comboItem.isNull("sub") && comboItem.getJSONArray("sub").length() > 0) {
																							responseCode = "E06";
																							responseMessage = "Combo Item Modifier Not Found In Database (id = " + jsonObj.getString("id") + ")";
																							break main;
																						} else {
																							responseCode = "00";
																							responseMessage = "Item Successfully Ordered";
																						}
																					}
																				} else {
																					responseCode = "EB1";
																					responseMessage = "Check Detail Failed To Insert";
																					break main;																				
																				}
																			} else {
																				responseCode = "E05";
																				responseMessage = "Combo Item Price Not Match (id = " + jsonObj.getString("id") + ")";
																				break main;
																			}
																		} else {
																			responseCode = "E03";
																			responseMessage = "Combo Item Not Active (combo id = " + jsonObj.getString("id") + ")";
																			break main;
																		}
																	} else {
																		responseCode = "E06";
																		responseMessage = "Combo Item Not Found (combo id = " + jsonObj.getString("id") + ")";
																		break main;
																	}
																} else {
																	responseCode = "E06";
																	responseMessage = "Combo Item Not Found (combo id = " + jsonObj.getString("id") + ")";
																	break main;
																}
															} else {
																responseCode = "E06";
																responseMessage = "Combo Item Not Found In Request (combo id = " + jsonObj.getString("id") + ")";
																break main;
															}
															//deduct tier quantity
															for (int k = 0; k < comboTiers.length(); k++) {
																JSONObject comboTierInfo = comboTiers.getJSONObject(k);
																
																if (comboTierInfo.getLong("combo_detail_id") == (comboItem.getLong("combo_detail_id"))) {
																	int deductedQuantity = comboTierInfo.getInt("combo_detail_quantity")-1;
																	comboTierInfo.put("combo_detail_quantity", deductedQuantity);
																}
															}
														}
														//check if tier quantity equal to 0, if yes then success else fail
														if (responseCode.equals("00")) {
															for (int j = 0; j < comboTiers.length(); j++) {
																JSONObject comboTierInfo = comboTiers.getJSONObject(j);
																
																if (comboTierInfo.getInt("combo_detail_quantity") != 0) {
																	responseCode = "E07";
																	responseMessage = "Combo Item Quantity Not Match (combo id = " + jsonObj.getString("id") + ")";
																	break main;
																}
															}
														}
													} else {
														responseCode = "E06";
														responseMessage = "Combo Item Not Found In Request (combo id = " + jsonObj.getString("id") + ")";
														break main;
													}
												} else {
													responseCode = "E04";
													responseMessage = "Item Type Not Match (id = " + jsonObj.getString("id") + ")";
													break main;
												}
											} else {
												responseCode = "EB3";
												responseMessage = "Check Failed To Update";
												break main;
											}
										} else {
											responseCode = "EB2";
											responseMessage = "Check Detail ID Failed To Return";
											break main;
										}
									} else {
										responseCode = "EB1";
										responseMessage = "Check Detail Failed To Insert";
										break main;
									}
								} else {
									responseCode = "E05";
									responseMessage = "Item Price Not Match (id = " + jsonObj.getString("id") + ")";
									break main;
								}
							} else {
								responseCode = "E04";
								responseMessage = "Item Type Not Match (id = " + jsonObj.getString("id") + ")";
								break main;
							}
						} else {
							responseCode = "E03";
							responseMessage = "Item Not Active (id = " + jsonObj.getString("id") + ")";
							break main;
						}
					} else {
						responseCode = "E02";
						responseMessage = "Item Not Exist (id = " + jsonObj.getString("id") + ")";
						break main;
					}
				} else {
					responseCode = "E06";
					responseMessage = "Item Not Found In Request";
					break main;
				}
			}
			jsonResult.put("resultCode", responseCode);
			jsonResult.put("resultMessage", responseMessage);
		} catch (Exception e) {
			try {
				jsonResult = new JSONObject();
				jsonResult.put("resultCode", "01");
				jsonResult.put("resultMessage", "Host Error");
			} catch (Exception ex) {}
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();
				if (stmt5 != null) stmt5.close();
				if (stmt6 != null) stmt6.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (rs4 != null) {rs4.close();rs4 = null;}
				if (rs5 != null) {rs5.close();rs5 = null;}
				if (rs6 != null) {rs6.close();rs6 = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}
	
	public long insertChildCheckDetail(Connection connection, int deviceType, long checkId, String checkNo, long parentCheckDetailId, JSONObject childItem, int orderQuantity, boolean isItemTaxable, JSONObject charges) {
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		long checkDetailId = 0;
		
		try {
			stmt = connection.prepareStatement("select * from menu_item where backend_id = ?;");
			stmt.setString(1, childItem.getString("id"));
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				BigDecimal totalAmount = rs.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(orderQuantity));
				
				stmt2 = connection.prepareStatement("insert into check_detail (check_id,check_number,device_type,parent_check_detail_id,menu_item_id,menu_item_code,menu_item_name,menu_item_price,quantity,total_amount,check_detail_status,created_date) " + 
						"values (?,?,?,?,?,?,?,?,?,?,1,now());", Statement.RETURN_GENERATED_KEYS);
				stmt2.setLong(1, checkId);
				stmt2.setString(2, checkNo);
				stmt2.setInt(3, deviceType);
				stmt2.setLong(4, parentCheckDetailId);
				stmt2.setString(5, rs.getString("id"));
				stmt2.setString(6, rs.getString("backend_id"));
				stmt2.setString(7, rs.getString("menu_item_name"));
				stmt2.setString(8, rs.getString("menu_item_base_price"));
				stmt2.setInt(9, orderQuantity);
				stmt2.setBigDecimal(10, totalAmount);
				int insertCheckDetail = stmt2.executeUpdate();
				
				if (insertCheckDetail > 0) {
					rs2 = stmt2.getGeneratedKeys();
					
					if (rs2.next()) {
						boolean updateCheck = updateCheck(connection, checkId, checkNo, 0, totalAmount, isItemTaxable, charges);
						
						if (updateCheck) {
							checkDetailId = rs2.getLong(1);
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		return checkDetailId;
	}
	
	public JSONObject createCheck(Connection connection, int orderType) {
		JSONObject jsonResult = new JSONObject();
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs = null;
		ResultSet rs4 = null;
		
		try {
			stmt = connection.prepareStatement("select * from master where type = 'check';");
			rs = stmt.executeQuery();

			if (rs.next()) {
				int currentCheckNo = rs.getInt("count");
				int newCheckNo = currentCheckNo + 1;
				
				stmt2 = connection.prepareStatement("update master set count = ? where type = 'check';");
				stmt2.setString(1, Integer.toString(newCheckNo));
				int rs2 = stmt2.executeUpdate();

				if (rs2 > 0) {
					stmt3 = connection.prepareStatement("insert into `check` (check_number,order_type,total_item_quantity,total_amount,total_amount_with_tax,total_amount_with_tax_rounding_adjustment,grand_total_amount,deposit_amount,tender_amount,overdue_amount,check_status,created_date) " + 
							"values (?,?,0,0,0,0,0,0,0,0,1,now());", Statement.RETURN_GENERATED_KEYS);
					stmt3.setString(1, Integer.toString(newCheckNo));
					stmt3.setInt(2, orderType);
					int rs3 = stmt3.executeUpdate();

					if (rs3 > 0) {
						rs4 = stmt3.getGeneratedKeys();

						if (rs4.next()) {
							Logger.writeActivity("checkNo: " + newCheckNo, DEVICECALL_FOLDER);
							jsonResult.put("checkId", rs4.getLong(1));
							jsonResult.put("checkNo", Integer.toString(newCheckNo));
							jsonResult.put("resultCode", "00");
							jsonResult.put("resultMessage", "Success");
						} else {
							connection.rollback();
							Logger.writeActivity("Check Id Failed To Return", DEVICECALL_FOLDER);
							jsonResult.put("resultCode", "01");
							jsonResult.put("resultMessage", "Check Id Failed To Return");
						}
					} else {
						connection.rollback();
						Logger.writeActivity("Check Master Failed To Insert", DEVICECALL_FOLDER);
						jsonResult.put("resultCode", "01");
						jsonResult.put("resultMessage", "Check Master Failed To Insert");
					}
				} else {
					connection.rollback();
					Logger.writeActivity("Check Count Failed To Update", DEVICECALL_FOLDER);
					jsonResult.put("resultCode", "01");
					jsonResult.put("resultMessage", "Check Count Failed To Update");
				}
			} else {
				connection.rollback();
				Logger.writeActivity("Check Count Not Found", DEVICECALL_FOLDER);
				jsonResult.put("resultCode", "01");
				jsonResult.put("resultMessage", "Check Count Not Found");
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs4 != null) {rs4.close();rs4 = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}
	
	public boolean updateCheck(Connection connection, long checkId, String checkNo, int orderQuantity, BigDecimal amount, boolean isItemTaxable, JSONObject charges) {
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		PreparedStatement stmt5 = null;
		PreparedStatement stmt6 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		boolean result = false;
	
		try {
			stmt = connection.prepareStatement("select * from `check` where id = ? and check_number = ?;");
			stmt.setLong(1, checkId);
			stmt.setString(2, checkNo);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				int totalQuantity = rs.getInt("total_item_quantity");
				BigDecimal totalAmount = rs.getBigDecimal("total_amount");
				BigDecimal tenderAmount = rs.getBigDecimal("tender_amount");
				
				BigDecimal newTotalAmount = totalAmount.add(amount);
				
				BigDecimal newTotalAmountWithTax = newTotalAmount;
				
				boolean proceedUpdateCheck = false;
				if (isItemTaxable) {
					boolean proceed = false;
					BigDecimal amountWithTotalTax = newTotalAmount;
					if (!(charges.has("totalTaxes") && !charges.isNull("totalTaxes") && charges.getJSONArray("totalTaxes").length() > 0)) {
						proceed = true;
					} else {
						JSONArray totalTaxes = charges.getJSONArray("totalTaxes");
						
						loop: for (int i = 0; i < totalTaxes.length(); i++) {
							JSONObject totalTax = totalTaxes.getJSONObject(i);
							BigDecimal totalChargeAmount = newTotalAmount.multiply(new BigDecimal(totalTax.getString("rate")).divide(new BigDecimal("100")));
							BigDecimal grandTotalChargeAmount = totalChargeAmount.setScale(2, RoundingMode.HALF_UP);
							BigDecimal totalChargeAmountRoundingAdjustment = grandTotalChargeAmount.subtract(totalChargeAmount);
							
							stmt2 = connection.prepareStatement("select * from check_tax_charge where check_id = ? and check_number = ? and tax_charge_id = ?;");
							stmt2.setLong(1, checkId);
							stmt2.setString(2, checkNo);
							stmt2.setLong(3, totalTax.getLong("id"));
							rs2 = stmt2.executeQuery();
							
							int updateCharge = -1;
							if (rs2.next()) {
								stmt3 = connection.prepareStatement("update check_tax_charge set total_charge_amount = ?,total_charge_amount_rounding_adjustment = ?,grand_total_charge_amount = ? where check_id = ? and check_number = ? and tax_charge_id = ?;");
								stmt3.setBigDecimal(1, totalChargeAmount);
								stmt3.setBigDecimal(2, totalChargeAmountRoundingAdjustment);
								stmt3.setBigDecimal(3, grandTotalChargeAmount);
								stmt3.setLong(4, checkId);
								stmt3.setString(5, checkNo);
								stmt3.setLong(6, totalTax.getLong("id"));
							} else {
								stmt3 = connection.prepareStatement("insert into check_tax_charge (check_id,check_number,tax_charge_id,total_charge_amount,total_charge_amount_rounding_adjustment,grand_total_charge_amount) values (?,?,?,?,?,?);");
								stmt3.setLong(1, checkId);
								stmt3.setString(2, checkNo);
								stmt3.setLong(3, totalTax.getLong("id"));
								stmt3.setBigDecimal(4, totalChargeAmount);
								stmt3.setBigDecimal(5, totalChargeAmountRoundingAdjustment);
								stmt3.setBigDecimal(6, grandTotalChargeAmount);
							}
							updateCharge = stmt3.executeUpdate();
							
							if (updateCharge > 0) {
								proceed = true;
								amountWithTotalTax = amountWithTotalTax.add(grandTotalChargeAmount);
								newTotalAmountWithTax = newTotalAmountWithTax.add(grandTotalChargeAmount);
							} else {
								proceed = false;
								break loop;
							}
						}
					}
				
					if (proceed) {
						if (!(charges.has("overallTaxes") && !charges.isNull("overallTaxes") && charges.getJSONArray("overallTaxes").length() > 0)) {
							proceedUpdateCheck = true;
						} else {
							JSONArray overallTaxes = charges.getJSONArray("overallTaxes");
							
							loop: for (int i = 0; i < overallTaxes.length(); i++) {
								JSONObject overallTax = overallTaxes.getJSONObject(i);
								BigDecimal totalChargeAmount = amountWithTotalTax.multiply(new BigDecimal(overallTax.getString("rate")).divide(new BigDecimal("100")));
								BigDecimal grandTotalChargeAmount = totalChargeAmount.setScale(2, RoundingMode.HALF_UP);
								BigDecimal totalChargeAmountRoundingAdjustment = grandTotalChargeAmount.subtract(totalChargeAmount);
								
								stmt4 = connection.prepareStatement("select * from check_tax_charge where check_id = ? and check_number = ? and tax_charge_id = ?;");
								stmt4.setLong(1, checkId);
								stmt4.setString(2, checkNo);
								stmt4.setLong(3, overallTax.getLong("id"));
								rs3 = stmt4.executeQuery();
								
								int updateCharge = -1;
								if (rs3.next()) {
									stmt5 = connection.prepareStatement("update check_tax_charge set total_charge_amount = ?,total_charge_amount_rounding_adjustment = ?,grand_total_charge_amount = ? where check_id = ? and check_number = ? and tax_charge_id = ?;");
									stmt5.setBigDecimal(1, totalChargeAmount);
									stmt5.setBigDecimal(2, totalChargeAmountRoundingAdjustment);
									stmt5.setBigDecimal(3, grandTotalChargeAmount);
									stmt5.setLong(4, checkId);
									stmt5.setString(5, checkNo);
									stmt5.setLong(6, overallTax.getLong("id"));
								} else {
									stmt5 = connection.prepareStatement("insert into check_tax_charge (check_id,check_number,tax_charge_id,total_charge_amount,total_charge_amount_rounding_adjustment,grand_total_charge_amount) values (?,?,?,?,?,?);");
									stmt5.setLong(1, checkId);
									stmt5.setString(2, checkNo);
									stmt5.setLong(3, overallTax.getLong("id"));
									stmt5.setBigDecimal(4, totalChargeAmount);
									stmt5.setBigDecimal(5, totalChargeAmountRoundingAdjustment);
									stmt5.setBigDecimal(6, grandTotalChargeAmount);
								}
								updateCharge = stmt5.executeUpdate();
								
								if (updateCharge > 0) {
									proceedUpdateCheck = true;
									newTotalAmountWithTax = newTotalAmountWithTax.add(grandTotalChargeAmount);
								} else {
									proceedUpdateCheck = false;
									break loop;
								}
							}
						}
					}
				} else {
					proceedUpdateCheck = true;
				}
				
				if (proceedUpdateCheck) {
					BigDecimal newGrandTotalAmount = roundToNearest(newTotalAmountWithTax);
					BigDecimal newTotalAmountWithTaxRoundingAdjustment = newGrandTotalAmount.subtract(newTotalAmountWithTax);
					BigDecimal newOverdueAmount = newGrandTotalAmount.subtract(tenderAmount);
					
					stmt6 = connection.prepareStatement("update `check` set total_item_quantity = ?,total_amount = ?,total_amount_with_tax = ?,total_amount_with_tax_rounding_adjustment = ?,grand_total_amount = ?,overdue_amount = ?,check_status = 2,updated_date = now() where id = ? and check_number = ?;");
					stmt6.setInt(1, totalQuantity + orderQuantity);
					stmt6.setBigDecimal(2, newTotalAmount);
					stmt6.setBigDecimal(3, newTotalAmountWithTax);
					stmt6.setBigDecimal(4, newTotalAmountWithTaxRoundingAdjustment);
					stmt6.setBigDecimal(5, newGrandTotalAmount);
					stmt6.setBigDecimal(6, newOverdueAmount);
					stmt6.setLong(7, checkId);
					stmt6.setString(8, checkNo);
					int updateCheck = stmt6.executeUpdate();
					
					if (updateCheck > 0) {
						result = true;
					}
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();
				if (stmt5 != null) stmt5.close();
				if (stmt6 != null) stmt6.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		return result;
	}
}
