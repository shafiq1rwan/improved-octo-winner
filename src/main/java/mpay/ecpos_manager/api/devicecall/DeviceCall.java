package mpay.ecpos_manager.api.devicecall;

import java.math.BigDecimal;
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
					jsonResult.put("checkId", rs.getString("id"));
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
	
	public JSONObject submitOrderItem(Connection connection, String checkId, String checkNo, String deviceType, JSONArray orderData) {
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
									boolean isItemTaxable = rs.getBoolean("is_taxable");

									JSONArray taxCharges = new JSONArray();

									if (isItemTaxable) {
										stmtA = connection.prepareStatement("select tc.* from menu_item_tax_charge mitc "
														+ "inner join tax_charge tc on tc.id = mitc.tax_charge_id "
														+ "where mitc.menu_item_id = ? and tc.is_active = 1;");
										stmtA.setLong(1, rs.getLong("id"));
										rsA = stmt3.executeQuery();

										while (rsA.next()) {
											JSONObject itemTax = new JSONObject();
											itemTax.put("taxChargeType", rsA.getString("charge_type"));
											itemTax.put("rate", rsA.getString("rate"));

											taxCharges.put(itemTax);
										}
									}
									Logger.writeActivity("isItemTaxable: " + isItemTaxable, DEVICECALL_FOLDER);
									Logger.writeActivity("taxCharges: " + taxCharges, DEVICECALL_FOLDER);

									stmtB = connection.prepareStatement("insert into check_detail (check_id,check_number,device_type,menu_item_id,menu_item_code,menu_item_name,menu_item_price,quantity,subtotal_amount,total_amount,check_detail_status,created_date) "
													+ "values (?,?,?,?,?,?,?,?,?,?,1,now());", Statement.RETURN_GENERATED_KEYS);
									stmtB.setString(1, checkId);
									stmtB.setString(2, checkNo);
									stmtB.setInt(3, deviceId);  
									stmtB.setLong(4, rs.getLong("id"));
									stmtB.setString(5, rs.getString("backend_id"));
									stmtB.setString(6, rs.getString("menu_item_name"));
									stmtB.setString(7, rs.getString("menu_item_base_price"));
									stmtB.setInt(8, orderQuantity);
									stmtB.setBigDecimal(9, rs.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(orderQuantity)));
									stmtB.setBigDecimal(10, rs.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(orderQuantity)));
									int insert1stCheckDetail = stmtB.executeUpdate();

									if (insert1stCheckDetail > 0) {
										rsB = stmtB.getGeneratedKeys();

										if (rsB.next()) {
											long parentCheckDetailId = rsB.getLong(1);

											boolean updateTaxChargeResult = false;

											if (isItemTaxable) {
												updateTaxChargeResult = updateTaxCharge(connection, parentCheckDetailId, orderQuantity, taxCharges);
											}

											if (!isItemTaxable || updateTaxChargeResult) {
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
																					long modifierCheckDetailId = insertChildCheckDetail(connection, deviceId, checkId, checkNo, parentCheckDetailId, modifier, orderQuantity);

																					if (modifierCheckDetailId > 0) {
																						if (isItemTaxable) {
																							updateTaxChargeResult = updateTaxCharge(connection, modifierCheckDetailId, orderQuantity, taxCharges);

																							if (updateTaxChargeResult) {
																								responseCode = "00";
																								responseMessage = "Item Successfully Ordered";
																							} else {
																								responseCode = "EB3";
																								responseMessage = "Tax/Charge Failed To Update";
																								break main;
																							}
																						} else {
																							responseCode = "00";
																							responseMessage = "Item Successfully Ordered";
																						}
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
																			long childCheckDetailId = insertChildCheckDetail(connection, deviceId, checkId, checkNo, parentCheckDetailId, comboItem, orderQuantity);

																			if (childCheckDetailId > 0) {
																				if (isItemTaxable) {
																					updateTaxChargeResult = updateTaxCharge(connection, childCheckDetailId, orderQuantity, taxCharges);
																				}

																				if (!isItemTaxable || updateTaxChargeResult) {
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
																													long modifierCheckDetailId = insertChildCheckDetail(connection, deviceId, checkId, checkNo, childCheckDetailId, modifier, orderQuantity);
																													
																													if (modifierCheckDetailId > 0) {
																														if (isItemTaxable) {
																															updateTaxChargeResult = updateTaxCharge(connection, modifierCheckDetailId, orderQuantity, taxCharges);

																															if (updateTaxChargeResult) {
																																responseCode = "00";
																																responseMessage = "Item Successfully Ordered";
																															} else {
																																responseCode = "EB3";
																																responseMessage = "Tax/Charge Failed To Update";
																																break main;
																															}
																														} else {
																															responseCode = "00";
																															responseMessage = "Item Successfully Ordered";
																														}
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
																					responseCode = "EB3";
																					responseMessage = "Tax/Charge Failed To Update";
																					break main;
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
													responseCode = "E04";
													responseMessage = "Item Type Not Match (id = " + jsonObj.getString("id") + ")";
													break main;
												}
											} else {
												responseCode = "EB3";
												responseMessage = "Tax/Charge Failed To Update";
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
			
			if (responseCode.equals("00")) {
				//UPDATE CHECK
				boolean updateCheck = updateCheck(connection, checkId, checkNo);
				
				if (updateCheck) {
					Logger.writeActivity("Check Successfully Updated", DEVICECALL_FOLDER);
					responseCode = "00";
					responseMessage = "Item/s Successfully Ordered";
				} else {
					connection.rollback();
					Logger.writeActivity("Check Failed To Update", DEVICECALL_FOLDER);
					responseCode = "01";
					responseMessage = "Check Failed To Update";
				}
			} else {
				connection.rollback();
			}
			jsonResult.put("resultCode", responseCode);
			jsonResult.put("resultMessage", responseMessage);
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
				
	public boolean updateTaxCharge(Connection connection, long checkDetailId, int orderQuantity, JSONArray taxCharges) {
		PreparedStatement stmt = null;
		boolean result = false;
		
		try {			
			for (int i = 0; i < taxCharges.length(); i++) {
				JSONObject taxCharge = taxCharges.getJSONObject(i);
				
				if (taxCharge.getString("taxChargeType").equals("1")) {
					stmt = connection.prepareStatement("update check_detail set tax_rate = ?,total_tax_amount = ?,total_amount = total_amount + ?, updated_date = now() where id = ?");
					stmt.setString(1, taxCharge.getString("rate"));
					stmt.setBigDecimal(2, new BigDecimal(orderQuantity).multiply(new BigDecimal(taxCharge.getString("rate")).divide(new BigDecimal("100"))));
					stmt.setBigDecimal(3, new BigDecimal(orderQuantity).multiply(new BigDecimal(taxCharge.getString("rate")).divide(new BigDecimal("100"))));
					stmt.setLong(4, checkDetailId);
				} else if (taxCharge.getString("taxChargeType").equals("2")) {
					stmt = connection.prepareStatement("update check_detail set service_charge_rate = ?,total_service_charge_amount = ?,total_amount = total_amount + ?, updated_date = now() where id = ?");
					stmt.setString(1, taxCharge.getString("rate"));
					stmt.setBigDecimal(2, new BigDecimal(orderQuantity).multiply(new BigDecimal(taxCharge.getString("rate")).divide(new BigDecimal("100"))));
					stmt.setBigDecimal(3, new BigDecimal(orderQuantity).multiply(new BigDecimal(taxCharge.getString("rate")).divide(new BigDecimal("100"))));
					stmt.setLong(4, checkDetailId);
				}
				int rs = stmt.executeUpdate();
				
				if (rs > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public long insertChildCheckDetail(Connection connection, int deviceType, String checkId, String checkNo, long parentCheckDetailId, JSONObject childItem, int orderQuantity) {
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		long checkDetailId = 0;
		
		try {
			stmt = connection.prepareStatement("select * from menu_item where backend_id = ?;");
			stmt.setString(1, childItem.getString("id"));
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				stmt1 = connection.prepareStatement("insert into check_detail (check_id,check_number,device_type,parent_check_detail_id,menu_item_id,menu_item_code,menu_item_name,menu_item_price,quantity,subtotal_amount,total_amount,check_detail_status,created_date) " + 
						"values (?,?,?,?,?,?,?,?,?,?,?,1,now());", Statement.RETURN_GENERATED_KEYS);
				stmt1.setString(1, checkId);
				stmt1.setString(2, checkNo);
				stmt1.setInt(3, deviceType);
				stmt1.setLong(4, parentCheckDetailId);
				stmt1.setString(5, rs.getString("id"));
				stmt1.setString(6, rs.getString("backend_id"));
				stmt1.setString(7, rs.getString("menu_item_name"));
				stmt1.setString(8, rs.getString("menu_item_base_price"));
				stmt1.setInt(9, orderQuantity);
				stmt1.setBigDecimal(10, rs.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(orderQuantity)));
				stmt1.setBigDecimal(11, rs.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(orderQuantity)));
				int insertCheckDetail = stmt1.executeUpdate();
				
				if (insertCheckDetail > 0) {
					rs2 = stmt1.getGeneratedKeys();
					
					if (rs2.next()) {
						checkDetailId = rs2.getLong(1);
					}
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt1 != null) stmt1.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		return checkDetailId;
	}

	public boolean updateCheck(Connection connection, String checkId, String checkNo) {
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		boolean result = false;
		
		try {
			stmt = connection.prepareStatement("select sum(subtotal_amount) as 'subtotal_amount',sum(total_tax_amount) as 'total_tax_amount',sum(total_service_charge_amount) as 'total_service_charge_amount',sum(total_amount) as 'total_amount', " + 
					"(select sum(quantity) as 'quantity' from check_detail where check_id = ? and check_number = ? and parent_check_detail_id is null and check_detail_status in (1, 2, 3)) as 'quantity', " + 
					"(select sum(total_amount) from check_detail where check_id = ? and check_number = ? and check_detail_status = 3) as 'amount_paid' " + 
					"from check_detail where check_id = ? and check_number = ? and check_detail_status in (1, 2, 3);");
			stmt.setString(1, checkId);
			stmt.setString(2, checkNo);
			stmt.setString(3, checkId);
			stmt.setString(4, checkNo);
			stmt.setString(5, checkId);
			stmt.setString(6, checkNo);
			rs = stmt.executeQuery();

			if (rs.next()) {
				BigDecimal totalAmount = rs.getBigDecimal("total_amount") == null ? new BigDecimal("0.00") : rs.getBigDecimal("total_amount");
				BigDecimal grandTotalAmount = totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
				BigDecimal totalAmountRoundingAdjustment = grandTotalAmount.subtract(totalAmount);
				BigDecimal amountPaid = rs.getBigDecimal("amount_paid") == null ? new BigDecimal("0.00") : rs.getBigDecimal("amount_paid");

				stmt1 = connection.prepareStatement("update `check` set total_item_quantity = ?,subtotal_amount = ?,total_tax_amount = ?,total_service_charge_amount = ?,total_amount = ?,total_amount_rounding_adjustment = ?,grand_total_amount = ?,tender_amount = ?,overdue_amount = ?,check_status = 2,updated_date = now() where id = ? and check_number = ?;");
				stmt1.setInt(1, rs.getInt("quantity"));
				stmt1.setBigDecimal(2, rs.getBigDecimal("subtotal_amount") == null ? new BigDecimal("0.00") : rs.getBigDecimal("subtotal_amount"));
				stmt1.setBigDecimal(3, rs.getBigDecimal("total_tax_amount"));
				stmt1.setBigDecimal(4, rs.getBigDecimal("total_service_charge_amount"));
				stmt1.setBigDecimal(5, totalAmount);
				stmt1.setBigDecimal(6, totalAmountRoundingAdjustment);
				stmt1.setBigDecimal(7, grandTotalAmount);
				stmt1.setBigDecimal(8, amountPaid);
				stmt1.setBigDecimal(9, grandTotalAmount.subtract(amountPaid));
				stmt1.setString(10, checkId);
				stmt1.setString(11, checkNo);
				int rs2 = stmt1.executeUpdate();
				
				if (rs2 > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt1 != null) stmt1.close();
				if (rs != null) {rs.close();rs = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		return result;
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
					stmt3 = connection.prepareStatement("insert into `check` (check_number,order_type,total_item_quantity,subtotal_amount,total_tax_amount,total_service_charge_amount,total_amount,total_amount_rounding_adjustment,grand_total_amount,deposit_amount,tender_amount,overdue_amount,check_status,created_date) " + 
							"values (?,?,0,0,0,0,0,0,0,0,0,0,1,now());", Statement.RETURN_GENERATED_KEYS);
					stmt3.setString(1, Integer.toString(newCheckNo));
					stmt3.setInt(2, orderType);
					int rs3 = stmt3.executeUpdate();

					if (rs3 > 0) {
						rs4 = stmt3.getGeneratedKeys();

						if (rs4.next()) {
							Logger.writeActivity("checkNo: " + newCheckNo, DEVICECALL_FOLDER);
							jsonResult.put("checkId", rs4.getLong(1));
							jsonResult.put("checkNo", newCheckNo);
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
}
