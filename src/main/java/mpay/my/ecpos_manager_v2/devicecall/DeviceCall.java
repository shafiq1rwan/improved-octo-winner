package mpay.my.ecpos_manager_v2.devicecall;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;

@Service
public class DeviceCall {

	private static String DEVICECALL_FOLDER = Property.getDEVICECALL_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	public JSONObject checkOrderItem(JSONArray jsonData) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
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
			
			for (int i = 0; i < jsonData.length(); i++) {
				JSONObject jsonObj = jsonData.getJSONObject(i);
				
				if (jsonObj.has("id") && !jsonObj.isNull("id") && !jsonObj.getString("id").isEmpty()) {
					connection = dataSource.getConnection();
					
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
																		break;
																	}
																} else {
																	responseCode = "E04";
																	responseMessage = "Modifier Item Type Not Match (modifier id = " + rs3.getString("id") + ")";
																	break;
																}
															} else {
																responseCode = "E03";
																responseMessage = "Modifier Item Not Active (modifier id = " + rs3.getString("id") + ")";
																break;
															}
														} else {
															responseCode = "E02";
															responseMessage = "Modifier Item Not Exist (id = " + jsonObj.getString("id") + ")";
															break;
														}
													} else {
														responseCode = "E06";
														responseMessage = "Modifier Item Not Found In Request (id = " + jsonObj.getString("id") + ")";
														break;
													}
												}
											} else {
												responseCode = "E06";
												responseMessage = "Item Modifier Not Found In Request (id = " + jsonObj.getString("id") + ")";
												break;
											}
										} else {
											if (jsonObj.has("sub") && !jsonObj.isNull("sub") && jsonObj.getJSONArray("sub").length() > 0) {
												responseCode = "E06";
												responseMessage = "Item Modifier Not Found In Database (id = " + jsonObj.getString("id") + ")";
												break;
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
																				stmt6.setString(1, comboItem.getString("id"));
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
																								break;
																							}
																						} else {
																							responseCode = "E04";
																							responseMessage = "Combo Modifier Item Type Not Match (modifier id = " + rs6.getString("id") + ")";
																							break;
																						}
																					} else {
																						responseCode = "E03";
																						responseMessage = "Combo Modifier Item Not Active (modifier id = " + rs6.getString("id") + ")";
																						break;
																					}
																				} else {
																					responseCode = "E02";
																					responseMessage = "Combo Modifier Item Not Exist (id = " + jsonObj.getString("id") + ")";
																					break;
																				}
																			} else {
																				responseCode = "E06";
																				responseMessage = "Combo Modifier Item Not Found In Request (id = " + jsonObj.getString("id") + ")";
																				break;
																			}
																		}
																	} else {
																		responseCode = "E06";
																		responseMessage = "Combo Item Modifier Not Found In Request (id = " + jsonObj.getString("id") + ")";
																		break;
																	}
																} else {
																	if (comboItem.has("sub") && !comboItem.isNull("sub") && comboItem.getJSONArray("sub").length() > 0) {
																		responseCode = "E06";
																		responseMessage = "Combo Item Modifier Not Found In Database (id = " + jsonObj.getString("id") + ")";
																		break;
																	} else {
																		responseCode = "00";
																		responseMessage = "Combo Item Details Matched";
																	}
																}
															} else {
																responseCode = "E05";
																responseMessage = "Combo Item Price Not Match (id = " + jsonObj.getString("id") + ")";
																break;
															}
														} else {
															responseCode = "E03";
															responseMessage = "Combo Item Not Active (combo id = " + jsonObj.getString("id") + ")";
															break;
														}
													} else {
														responseCode = "E06";
														responseMessage = "Combo Item Not Found (combo id = " + jsonObj.getString("id") + ")";
														break;
													}
												} else {
													responseCode = "E06";
													responseMessage = "Combo Item Not Found (combo id = " + jsonObj.getString("id") + ")";
													break;
												}
											} else {
												responseCode = "E06";
												responseMessage = "Combo Item Not Found In Request (combo id = " + jsonObj.getString("id") + ")";
												break;
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
													break;
												}
											}
										}
									} else {
										responseCode = "E04";
										responseMessage = "Item Type Not Match (id = " + jsonObj.getString("id") + ")";
										break;
									}
								} else {
									responseCode = "E05";
									responseMessage = "Item Price Not Match (id = " + jsonObj.getString("id") + ")";
									break;
								}
							} else {
								responseCode = "E04";
								responseMessage = "Item Type Not Match (id = " + jsonObj.getString("id") + ")";
								break;
							}
						} else {
							responseCode = "E03";
							responseMessage = "Item Not Active (id = " + jsonObj.getString("id") + ")";
							break;
						}
					} else {
						responseCode = "E02";
						responseMessage = "Item Not Exist (id = " + jsonObj.getString("id") + ")";
						break;
					}
				} else {
					responseCode = "E06";
					responseMessage = "Item Not Found In Request";
					break;
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
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}
}
