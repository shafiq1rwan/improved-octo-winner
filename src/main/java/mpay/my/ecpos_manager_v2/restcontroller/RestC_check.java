package mpay.my.ecpos_manager_v2.restcontroller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mpay.my.ecpos_manager_v2.constant.Constant;
import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;
import mpay.my.ecpos_manager_v2.webutil.UserAuthenticationModel;
import mpay.my.ecpos_manager_v2.webutil.UtilWebComponents;

@RestController
@RequestMapping("/rc/check")
public class RestC_check {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@RequestMapping(value = { "/get_check_list" }, method = { RequestMethod.POST }, produces = "application/json")
	public String getChecklist(@RequestBody String data) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			JSONObject jsonObj = new JSONObject(data);

			if (jsonObj.has(Constant.TABLE_NO)) {
				String table_no = jsonObj.getString(Constant.TABLE_NO);
				
				connection = dataSource.getConnection();

				stmt = connection.prepareStatement("SELECT * FROM `check` WHERE table_number = ? AND check_status IN (1,2);");
				stmt.setString(1, table_no);
				rs = stmt.executeQuery();

				JSONArray check_list = new JSONArray();
				while (rs.next()) {
					check_list.put(rs.getString("check_number"));
				}
				jsonResult.put("check_list", check_list);
				Logger.writeActivity("Table Check List: " + check_list.toString(), ECPOS_FOLDER);
				
				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
			} else {
				Logger.writeActivity("Invalid Request for Table Check List", ECPOS_FOLDER);
				
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Request");
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
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_check_detail/{orderType}/{checkNo}/{tableNo}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getCheckDetails(@PathVariable("orderType") String orderType, @PathVariable("checkNo") String checkNo, @PathVariable("tableNo") int tableNo) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		
		try {
			connection = dataSource.getConnection();
			
			String tableNoCondition = null;
			if (orderType.equals("table")) {
				tableNoCondition = "table_number = " + tableNo;
			} else if (orderType.equals("take_away")) {
				tableNoCondition = "table_number is null";
			}
			
			stmt = connection.prepareStatement("select * from `check` c "
					+ "inner join check_status cs on cs.id = c.check_status "
					+ "where " + tableNoCondition + " and check_number = ? and device_type = 1 and check_status in (1, 2);");
			stmt.setString(1, checkNo);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				long id = rs.getLong("id");
				
				jsonResult.put("checkNo", rs.getString("check_number"));
				jsonResult.put("tableNo", rs.getString("table_number") == null ? "-" : rs.getString("table_number"));
				jsonResult.put("createdDate", rs.getString("created_date"));
				jsonResult.put("subtotal", rs.getString("subtotal_amount") == null ? "0.00" : rs.getString("subtotal_amount"));
				jsonResult.put("tax", rs.getString("total_tax_amount") == null ? "0.00" : rs.getString("total_tax_amount"));
				jsonResult.put("serviceCharge", rs.getString("total_service_charge_amount") == null ? "0.00" : rs.getString("total_service_charge_amount"));
				jsonResult.put("total", rs.getString("total_amount") == null ? "0.00" : rs.getString("total_amount"));
				jsonResult.put("roundingAdjustment", rs.getString("total_amount_rounding_adjustment") == null ? "0.00" : rs.getString("total_amount_rounding_adjustment"));
				jsonResult.put("grandTotal", rs.getString("grand_total_amount") == null ? "0.00" : rs.getString("grand_total_amount"));
				jsonResult.put("status", rs.getString("name"));
				jsonResult.put("deposit", rs.getString("deposit_amount") == null ? "0.00" : rs.getString("deposit_amount"));
				jsonResult.put("tender", rs.getString("tender_amount") == null ? "0.00" : rs.getString("tender_amount"));
				jsonResult.put("overdue", rs.getString("overdue_amount") == null ? "0.00" : rs.getString("overdue_amount"));
				
				stmt.close();
				stmt = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id is null and check_detail_status in (1, 2) order by id asc;");
				stmt.setLong(1, id);
				stmt.setString(2, checkNo);
				rs2 = stmt.executeQuery();
				
				JSONArray grandParentItemArray = new JSONArray();
				
				while (rs2.next()) {
					long grandParentId = rs2.getLong("id");
					
					JSONObject grandParentItem = new JSONObject();
					grandParentItem.put("checkDetailId", rs2.getString("id"));
					grandParentItem.put("itemId", rs2.getString("menu_item_id"));
					grandParentItem.put("itemCode", rs2.getString("menu_item_code"));
					grandParentItem.put("itemName", rs2.getString("menu_item_name"));
					grandParentItem.put("itemQuantity", rs2.getString("quantity"));
					grandParentItem.put("subtotal", rs2.getString("subtotal_amount"));
					
					stmt = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id = ? and check_detail_status in (1, 2) order by id asc;");
					stmt.setLong(1, id);
					stmt.setString(2, checkNo);
					stmt.setLong(3, grandParentId);
					rs3 = stmt.executeQuery();
					
					JSONArray parentItemArray = new JSONArray();
					
					while (rs3.next()) {
						long parentId = rs3.getLong("id");
						
						JSONObject parentItem = new JSONObject();
						parentItem.put("itemId", rs3.getString("menu_item_id"));
						parentItem.put("itemCode", rs3.getString("menu_item_code"));
						parentItem.put("itemName", rs3.getString("menu_item_name"));
						parentItem.put("itemQuantity", rs3.getString("quantity"));
						parentItem.put("subtotal", rs3.getString("subtotal_amount"));
						
						stmt = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id = ? and check_detail_status in (1, 2) order by id asc;");
						stmt.setLong(1, id);
						stmt.setString(2, checkNo);
						stmt.setLong(3, parentId);
						rs4 = stmt.executeQuery();
						
						JSONArray childItemArray = new JSONArray();
						
						while (rs4.next()) {
							JSONObject childItem = new JSONObject();
							childItem.put("itemId", rs4.getString("menu_item_id"));
							childItem.put("itemCode", rs4.getString("menu_item_code"));
							childItem.put("itemName", rs4.getString("menu_item_name"));
							childItem.put("itemQuantity", rs4.getString("quantity"));
							childItem.put("subtotal", rs4.getString("subtotal_amount"));
							
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
	
	@RequestMapping(value = { "/create" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String createCheck(@RequestBody String data, HttpServletRequest request) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		int rs3 = -1;
		int rs4 = -1;
		
		UtilWebComponents webComponent = new UtilWebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			JSONObject jsonObj = new JSONObject(data);

			String username = user.getUsername();
			
			int orderType = -1;
			String tableNo = null;
			boolean proceed = false;
			
			if (jsonObj.has("order_type")) {
				if (jsonObj.getString("order_type").equals("table")) {
					if (jsonObj.has("table_no")) {
						orderType = 1;
						tableNo = jsonObj.getString("table_no");
						proceed = true;
					} else {
						Logger.writeActivity("Table Number Not Found", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Table Number Not Found");
					}
				} else if (jsonObj.getString("order_type").equals("take away")) {
					orderType = 2;
					proceed = true;
				}
			} else {
				Logger.writeActivity("Order Type Not Found", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Order Type Not Found");
			}
			
			if (proceed) {
				connection = dataSource.getConnection();
	
				stmt = connection.prepareStatement("select * from staff where staff_username = ?;");
				stmt.setString(1, username);
				rs = stmt.executeQuery();
	
				if (rs.next()) {
					long staffId = rs.getLong("id");
					
					stmt.close();
					stmt = connection.prepareStatement("select * from master where type = 'check';");
					rs2 = stmt.executeQuery();
	
					if (rs2.next()) {
						int currentCheckNo = rs2.getInt("count");
						int newCheckNo = currentCheckNo + 1;
						
						stmt.close();
						stmt = connection.prepareStatement("update master set count = ? where type = 'check';");
						stmt.setString(1, Integer.toString(newCheckNo));
						rs3 = stmt.executeUpdate();
	
						if (rs3 > 0) {
							stmt.close();
							stmt = connection.prepareStatement("insert into `check` (check_number,device_type,staff_id,order_type,table_number,total_item_quantity,subtotal_amount,total_tax_amount,total_service_charge_amount,total_amount,total_amount_rounding_adjustment,grand_total_amount,deposit_amount,tender_amount,overdue_amount,check_status,created_date) " + 
									"values (?,1,?,?,?,0,0,0,0,0,0,0,0,0,0,1,now());");
							stmt.setString(1, Integer.toString(newCheckNo));
							stmt.setLong(2, staffId);
							stmt.setInt(3, orderType);
							stmt.setString(4, tableNo);
							rs4 = stmt.executeUpdate();
	
							if (rs4 > 0) {
								jsonResult.put(Constant.CHECK_NO, newCheckNo);
								jsonResult.put(Constant.RESPONSE_CODE, "00");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Success");
							} else {
								Logger.writeActivity("Check Master Failed To Insert", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "01");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Master Failed To Insert");
							}
						} else {
							Logger.writeActivity("Check Count Failed To Update", ECPOS_FOLDER);
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Count Failed To Update");
						}
					} else {
						Logger.writeActivity("Check Count Not Found", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Count Not Found");
					}
				} else {
					Logger.writeActivity("Staff Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Staff Not Found");
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
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/order" }, method = { RequestMethod.POST }, produces = "application/json")
	public String saveOrder(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;

		try {
			JSONObject order = new JSONObject(data);
			JSONObject item = (JSONObject) order.get("item");
			int orderQuantity = item.getInt("orderQuantity");
			
			connection = dataSource.getConnection();

			String tableNoCondition = null;
			if (order.getInt("orderType") == 1) {
				tableNoCondition = "table_number = " + order.getInt("tableNo");
			} else if (order.getInt("orderType") == 2) {
				tableNoCondition = "table_number is null";
			}
			
			stmt = connection.prepareStatement("select * from `check` where check_number = ? and " + tableNoCondition + " and order_type = ? and device_type = ? and check_status in (1, 2);");
			stmt.setString(1, order.getString("checkNo"));
			stmt.setInt(2, order.getInt("orderType"));
			stmt.setInt(3, order.getInt("deviceType"));
			rs = stmt.executeQuery();

			if (rs.next()) {
				long checkId = rs.getLong("id");
				String checkNo = rs.getString("check_number");
				
				stmt.close();
				stmt = connection.prepareStatement("select * from menu_item where id = ? and backend_id = ?;");
				stmt.setLong(1, item.getLong("id"));
				stmt.setString(2, item.getString("backendId"));
				rs2 = stmt.executeQuery();
				
				if (rs2.next()) {
					boolean isItemTaxable = rs2.getBoolean("is_taxable");
					JSONArray taxCharges = new JSONArray();
					boolean updateTaxChargeResult = false;
					
					if (isItemTaxable) {
						stmt = connection.prepareStatement("select tc.* from menu_item_tax_charge mitc " + 
								"inner join tax_charge tc on tc.id = mitc.tax_charge_id " + 
								"where mitc.menu_item_id = ? and tc.is_active = 1;");
						stmt.setLong(1, rs2.getLong("id"));
						rs3 = stmt.executeQuery();
						
						while (rs3.next()) {
							JSONObject itemTax = new JSONObject();
							itemTax.put("taxChargeType", rs3.getString("charge_type"));
							itemTax.put("rate", rs3.getString("rate"));
							
							taxCharges.put(itemTax);
						}
					}
					
					stmt = connection.prepareStatement("insert into check_detail (check_id,check_number,menu_item_id,menu_item_code,menu_item_name,menu_item_price,quantity,subtotal_amount,total_amount,check_detail_status,created_date) " + 
							"values (?,?,?,?,?,?,?,?,?,1,now());", Statement.RETURN_GENERATED_KEYS);
					stmt.setLong(1, checkId);
					stmt.setString(2, checkNo);
					stmt.setLong(3, rs2.getLong("id"));
					stmt.setString(4, rs2.getString("backend_id"));
					stmt.setString(5, rs2.getString("menu_item_name"));
					stmt.setString(6, rs2.getString("menu_item_base_price"));
					stmt.setInt(7, orderQuantity);
					stmt.setBigDecimal(8, rs2.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(orderQuantity)));
					stmt.setBigDecimal(9, rs2.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(orderQuantity)));
					int insert1stCheckDetail = stmt.executeUpdate();
					
					if (insert1stCheckDetail > 0) {
						rs4 = stmt.getGeneratedKeys();
						
						if (rs4.next()) {
							long parentCheckDetailId = rs4.getLong(1);
							
							if (isItemTaxable) {
								updateTaxChargeResult = updateTaxCharge(parentCheckDetailId, orderQuantity, taxCharges);
							}
							
							if (!isItemTaxable || updateTaxChargeResult) {
								if (item.getString("type").equals("0")) {
									if (item.getJSONArray("modifiers").length() != 0) {
										for (int i = 0; i < item.getJSONArray("modifiers").length(); i++) {
											JSONObject modifier = item.getJSONArray("modifiers").getJSONObject(i);
											
											long modifierCheckDetailId = insertChildCheckDetail(checkId, checkNo, parentCheckDetailId, modifier, orderQuantity);
											
											if (modifierCheckDetailId > 0) {
												if (isItemTaxable) {
													updateTaxChargeResult = updateTaxCharge(modifierCheckDetailId, orderQuantity, taxCharges);
													
													if (updateTaxChargeResult) {
														Logger.writeActivity("A La Carte Successfully Insert", ECPOS_FOLDER);
														jsonResult.put(Constant.RESPONSE_CODE, "00");
														jsonResult.put(Constant.RESPONSE_MESSAGE, "A La Carte Successfully Insert");
													} else {
														Logger.writeActivity("Tax/Charge Failed To Insert", ECPOS_FOLDER);
														jsonResult.put(Constant.RESPONSE_CODE, "01");
														jsonResult.put(Constant.RESPONSE_MESSAGE, "Tax/Charge Failed To Insert");
													}
												}
											} else {
												Logger.writeActivity("Modifier Failed To Insert", ECPOS_FOLDER);
												jsonResult.put(Constant.RESPONSE_CODE, "01");
												jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Failed To Insert");
											}
										}
									} else {
										Logger.writeActivity("A La Carte With No Modifier Successfully Insert", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "00");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "A La Carte With No Modifier Successfully Insert");
									}
								} else if (item.getString("type").equals("1")) {
									if (item.getJSONArray("tiers").length() != 0) {
										for (int i = 0; i < item.getJSONArray("tiers").length(); i++) {
											JSONObject tier = item.getJSONArray("tiers").getJSONObject(i);
											
											if (tier.getJSONArray("items").length() != 0) {
												for (int j = 0; j < tier.getJSONArray("items").length(); j++) {
													JSONObject tierItem = tier.getJSONArray("items").getJSONObject(j);
													
													long childCheckDetailId = insertChildCheckDetail(checkId, checkNo, parentCheckDetailId, tierItem, orderQuantity);
													
													if (childCheckDetailId > 0) {
														if (isItemTaxable) {
															updateTaxChargeResult = updateTaxCharge(childCheckDetailId, orderQuantity, taxCharges);
														}
														
														if (!isItemTaxable || updateTaxChargeResult) {
															if (tierItem.getJSONArray("modifiers").length() != 0) {
																for (int k = 0; k < tierItem.getJSONArray("modifiers").length(); k++) {
																	JSONObject modifier = tierItem.getJSONArray("modifiers").getJSONObject(k);
																	
																	long modifierCheckDetailId = insertChildCheckDetail(checkId, checkNo, childCheckDetailId, modifier, orderQuantity);
																	
																	if (modifierCheckDetailId > 0) {
																		if (isItemTaxable) {
																			updateTaxChargeResult = updateTaxCharge(modifierCheckDetailId, orderQuantity, taxCharges);
																		
																			if (updateTaxChargeResult) {
																				Logger.writeActivity("Combo Successfully Insert", ECPOS_FOLDER);
																				jsonResult.put(Constant.RESPONSE_CODE, "00");
																				jsonResult.put(Constant.RESPONSE_MESSAGE, "Combo Successfully Insert");
																			} else {
																				Logger.writeActivity("Tax/Charge Failed To Insert", ECPOS_FOLDER);
																				jsonResult.put(Constant.RESPONSE_CODE, "01");
																				jsonResult.put(Constant.RESPONSE_MESSAGE, "Tax/Charge Failed To Insert");
																			}
																		}
																	} else {
																		Logger.writeActivity("Modifier Failed To Insert", ECPOS_FOLDER);
																		jsonResult.put(Constant.RESPONSE_CODE, "01");
																		jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Failed To Insert");
																	}
																}
															} else {
																Logger.writeActivity("Combo With No Modifier Successfully Insert", ECPOS_FOLDER);
																jsonResult.put(Constant.RESPONSE_CODE, "00");
																jsonResult.put(Constant.RESPONSE_MESSAGE, "Combo With No Modifier Successfully Insert");
															}
														} else {
															Logger.writeActivity("Tax/Charge Failed To Insert", ECPOS_FOLDER);
															jsonResult.put(Constant.RESPONSE_CODE, "01");
															jsonResult.put(Constant.RESPONSE_MESSAGE, "Tax/Charge Failed To Insert");
														}
													} else {
														Logger.writeActivity("Child Item Failed To Insert", ECPOS_FOLDER);
														jsonResult.put(Constant.RESPONSE_CODE, "01");
														jsonResult.put(Constant.RESPONSE_MESSAGE, "Child Item Failed To Insert");
													}
												}
											} else {
												Logger.writeActivity("Tier Item Not Found", ECPOS_FOLDER);
												jsonResult.put(Constant.RESPONSE_CODE, "01");
												jsonResult.put(Constant.RESPONSE_MESSAGE, "Tier Item Not Found");
											}
										}
									} else {
										Logger.writeActivity("Tier Not Found", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Tier Not Found");
									}
								}
								
								//UPDATE CHECK
								boolean updateCheck = updateCheck(checkId, checkNo);
								
								if (updateCheck) {
									Logger.writeActivity("Check Successfully Updated", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "00");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Successfully Updated");
								} else {
									Logger.writeActivity("Check Failed To Update", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Failed To Update");
								}
							} else {
								Logger.writeActivity("Tax/Charge Failed To Insert", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "01");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Tax/Charge Failed To Insert");
							}
						}
					} else {
						Logger.writeActivity("Check Detail Failed To Insert", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Failed To Insert");
					}
				} else {
					Logger.writeActivity("Item Not Found", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Not Found");
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
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/cancel_item" }, method = { RequestMethod.POST }, produces = "application/json")
	public String cancelItem(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("data: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs3 = null;

		try {
			connection = dataSource.getConnection();
			
			JSONArray checkDetailIdArray = new JSONObject(data).getJSONArray("checkDetailIdArray");
			
			long checkId = -1;
			String checkNo = null;
			
			for (int i = 0; i < checkDetailIdArray.length(); i++) {
				long checkDetailId = checkDetailIdArray.getLong(i);
				
				stmt = connection.prepareStatement("select * from check_detail where id = ?;");
				stmt.setLong(1, checkDetailId);
				rs = stmt.executeQuery();
				
				if (rs.next()) {
					checkId = rs.getLong("check_id");
					checkNo = rs.getString("check_number");
					
					stmt.close();
					stmt = connection.prepareStatement("update check_detail set check_detail_status = 4, updated_date = now() where id = ? or parent_check_detail_id = ?;");
					stmt.setLong(1, checkDetailId);
					stmt.setLong(2, checkDetailId);
					int rs2 = stmt.executeUpdate();
					
					if (rs2 > 0) {
						stmt.close();
						stmt = connection.prepareStatement("select * from check_detail where parent_check_detail_id = ?");
						stmt.setLong(1, checkDetailId);
						rs3 = stmt.executeQuery();
						
						boolean empty = true;
						
						while (rs3.next()) {
							empty = false;
							
							stmt = connection.prepareStatement("update check_detail set check_detail_status = 4, updated_date = now() where id = ? or parent_check_detail_id = ?;");
							stmt.setLong(1, rs3.getLong("id"));
							stmt.setLong(2, rs3.getLong("id"));
							int rs4 = stmt.executeUpdate();
							
							if (rs4 > 0) {
								Logger.writeActivity("Check Detail Successfully Updated", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "00");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Successfully Updated");
							} else {
								Logger.writeActivity("Check Detail Failed To Update", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "01");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Failed To Update");
							}
						}
						
						if (empty) {
							Logger.writeActivity("Check Detail Successfully Updated", ECPOS_FOLDER);
							jsonResult.put(Constant.RESPONSE_CODE, "00");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Successfully Updated");
						}
					} else {
						Logger.writeActivity("Check Detail Failed To Update", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Failed To Update");
					}
				}
			}
			
			//UPDATE CHECK
			boolean updateCheck = updateCheck(checkId, checkNo);
			
			if (updateCheck) {
				Logger.writeActivity("Check Successfully Updated", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Successfully Updated");
			} else {
				Logger.writeActivity("Check Failed To Update", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Failed To Update");
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	public boolean updateTaxCharge (long checkDetailId, int orderQuantity, JSONArray taxCharges) {
		Connection connection = null;
		PreparedStatement stmt = null;
		boolean result = false;
		
		try {
			connection = dataSource.getConnection();
			
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
	
	public long insertChildCheckDetail (long checkId, String checkNo, long parentCheckDetailId, JSONObject childItem, int orderQuantity) {
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		long checkDetailId = 0;
		
		try {
			connection = dataSource.getConnection();
			
			stmt = connection.prepareStatement("select * from menu_item where id = ? and backend_id = ?;");
			stmt.setLong(1, childItem.getLong("id"));
			stmt.setString(2, childItem.getString("backendId"));
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				stmt1 = connection.prepareStatement("insert into check_detail (check_id,check_number,parent_check_detail_id,menu_item_id,menu_item_code,menu_item_name,menu_item_price,quantity,subtotal_amount,total_amount,check_detail_status,created_date) " + 
						"values (?,?,?,?,?,?,?,?,?,?,1,now());", Statement.RETURN_GENERATED_KEYS);
				stmt1.setLong(1, checkId);
				stmt1.setString(2, checkNo);
				stmt1.setLong(3, parentCheckDetailId);
				stmt1.setString(4, rs.getString("id"));
				stmt1.setString(5, rs.getString("backend_id"));
				stmt1.setString(6, rs.getString("menu_item_name"));
				stmt1.setString(7, rs.getString("menu_item_base_price"));
				stmt1.setInt(8, orderQuantity);
				stmt1.setBigDecimal(9, rs.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(orderQuantity)));
				stmt1.setBigDecimal(10, rs.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(orderQuantity)));
				int insertCheckDetail = stmt1.executeUpdate();
				
				if (insertCheckDetail > 0) {
					rs2 = stmt1.getGeneratedKeys();
					
					if (rs2.next()) {
						checkDetailId = rs2.getLong(1);
					}
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt1 != null) stmt1.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return checkDetailId;
	}
	
	public boolean updateCheck (long checkId, String checkNo) {
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		boolean result = false;
		
		try {
			connection = dataSource.getConnection();
			
			stmt = connection.prepareStatement("select sum(subtotal_amount) as 'subtotal_amount',sum(total_tax_amount) as 'total_tax_amount',sum(total_service_charge_amount) as 'total_service_charge_amount',sum(total_amount) as 'total_amount', " + 
					"(select sum(quantity) as 'quantity' from check_detail where check_id = ? and check_number = ? and parent_check_detail_id is null and check_detail_status in (1, 2, 3)) as 'quantity', " + 
					"(select sum(total_amount) from check_detail where check_id = ? and check_number = ? and check_detail_status = 3) as 'amount_paid' " + 
					"from check_detail where check_id = ? and check_number = ? and check_detail_status in (1, 2, 3);");
			stmt.setLong(1, checkId);
			stmt.setString(2, checkNo);
			stmt.setLong(3, checkId);
			stmt.setString(4, checkNo);
			stmt.setLong(5, checkId);
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
				stmt1.setLong(10, checkId);
				stmt1.setString(11, checkNo);
				int rs2 = stmt1.executeUpdate();
				
				if (rs2 > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt1 != null) stmt1.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return result;
	}
}
