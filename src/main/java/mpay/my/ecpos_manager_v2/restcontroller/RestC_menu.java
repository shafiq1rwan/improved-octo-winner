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

import org.json.JSONObject;
import org.json.JSONArray;
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
@RequestMapping("/rc/menu")
public class RestC_menu {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@RequestMapping(value = { "/get_categories" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getCategories() {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("select * from category where is_active = 1 order by category_sequence asc;");
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				JSONObject category = new JSONObject();
				category.put("id", rs.getString("id"));
				category.put("name", rs.getString("category_name"));
//				category.put("imagePath", rs.getString("category_image_path"));
				category.put("imagePath", "/jakarta-tomcat/webapps/ecposmanager/menuimage/2pc-combo.png");
				
				jary.put(category);
			}
			jsonResult.put("jary", jary);
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
	
	@RequestMapping(value = { "/get_menu_items/{categoryId}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getMenuItems(@PathVariable("categoryId") long categoryId) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("select cmi.category_menu_item_sequence, mi.* from category c " + 
					"inner join category_menu_item cmi on cmi.category_id = c.id " + 
					"inner join menu_item mi on mi.id = cmi.menu_item_id " + 
					"where c.id = ? and c.is_active = 1 and mi.menu_item_type in (0, 1) and mi.is_active = 1 " + 
					"order by cmi.category_menu_item_sequence asc;");
			stmt.setLong(1, categoryId);
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject menuItems = new JSONObject();
				menuItems.put("id", rs.getString("id"));
				menuItems.put("backendId", rs.getString("backend_id"));
				menuItems.put("name", rs.getString("menu_item_name"));
				menuItems.put("type", rs.getString("menu_item_type"));
				menuItems.put("description", rs.getString("menu_item_description"));
//				menuItems.put("imagePath", rs.getString("menu_item_image_path"));
				menuItems.put("imagePath", "/jakarta-tomcat/webapps/ecposmanager/menuimage/2pc-combo.png");
				
				if (menuItems.getString("type").equals("0")) {
					stmt = connection.prepareStatement("select count(mg.id) as count from menu_item mi " + 
							"inner join menu_item_modifier_group mimg on mimg.menu_item_id = mi.id " + 
							"inner join modifier_group mg on mg.id = mimg.modifier_group_id " + 
							"where mi.id = ? and mi.backend_id = ? and mg.is_active = 1;");
					stmt.setString(1, menuItems.getString("id"));
					stmt.setString(2, menuItems.getString("backendId"));
					rs2 = stmt.executeQuery();
					
					if (rs2.next()) {
						if (rs2.getInt("count") > 0) {
							menuItems.put("hasModifier", true);
						} else {
							menuItems.put("hasModifier", false);
						}
					}
				}
				
				jary.put(menuItems);
			}
			jsonResult.put("jary", jary);
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
	
	@RequestMapping(value = { "/get_tiers/{menuItemId}/{menuItemCode}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getTiers(@PathVariable("menuItemId") long menuItemId, @PathVariable("menuItemCode") String menuItemCode) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {			
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("select cd.* from menu_item mi " + 
					"inner join combo_detail cd on cd.menu_item_id = mi.id " + 
					"where mi.id = ? and mi.backend_id = ? and mi.is_active = 1 and mi.menu_item_type = 1 " + 
					"order by cd.combo_detail_sequence asc;");
			stmt.setLong(1, menuItemId);
			stmt.setString(2, menuItemCode);
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject tiers = new JSONObject();
				tiers.put("id", rs.getString("id"));
				tiers.put("name", rs.getString("combo_detail_name"));
				tiers.put("quantity", rs.getString("combo_detail_quantity"));				
				
				jary.put(tiers);
			}
			jsonResult.put("jary", jary);
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

	@RequestMapping(value = { "/get_tier_item_details/{tierId}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getComboDetails(@PathVariable("tierId") long tierId) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			connection = dataSource.getConnection();
			
			stmt = connection.prepareStatement("select cid.* from combo_detail cd " + 
					"inner join combo_item_detail cid on cid.combo_detail_id = cd.id " + 
					"where cd.id = ? " + 
					"order by cid.combo_item_detail_sequence asc;");
			stmt.setLong(1, tierId);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				if (rs.getString("menu_item_id") != null && !rs.getString("menu_item_id").isEmpty()) {
					stmt = connection.prepareStatement("select mi.* from combo_item_detail cid " + 
							"inner join menu_item mi on mi.id = cid.menu_item_id " + 
							"where cid.id = ? and mi.is_active = 1;");
				} else if (rs.getString("menu_item_group_id") != null && !rs.getString("menu_item_group_id").isEmpty()) {
					stmt = connection.prepareStatement("select mi.* from combo_item_detail cid " + 
							"inner join menu_item_group mig on mig.id = cid.menu_item_group_id " + 
							"inner join menu_item_group_sequence migs on migs.menu_item_group_id = mig.id " + 
							"inner join menu_item mi on mi.id = migs.menu_item_id " + 
							"where cid.id = ? and mi.is_active = 1 " + 
							"order by migs.menu_item_group_sequence asc;");
				}
				stmt.setLong(1, rs.getLong("id"));
				rs2 = stmt.executeQuery();
				
				while (rs2.next()) {
					JSONObject menuItems = new JSONObject();
					menuItems.put("id", rs2.getString("id"));
					menuItems.put("backendId", rs2.getString("backend_id"));
					menuItems.put("name", rs2.getString("menu_item_name"));
//					menuItems.put("image_path", rs2.getString("menu_item_image_path"));
					menuItems.put("imagePath", "/jakarta-tomcat/webapps/ecposmanager/menuimage/2pc-combo.png");
					
					jary.put(menuItems);
				}
				jsonResult.put("jary", jary);
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
	
	@RequestMapping(value = { "/get_modifiers/{menuItemId}/{menuItemCode}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getModifiers(@PathVariable("menuItemId") long menuItemId, @PathVariable("menuItemCode") String menuItemCode) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("select mg.* from menu_item mi " + 
					"inner join menu_item_modifier_group mimg on mimg.menu_item_id = mi.id " + 
					"inner join modifier_group mg on mg.id = mimg.modifier_group_id " + 
					"where mi.id = ? and mi.backend_id = ? and mg.is_active = 1 " + 
					"order by mimg.menu_item_modifier_group_sequence asc;");
			stmt.setLong(1, menuItemId);
			stmt.setString(2, menuItemCode);
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject modifiers = new JSONObject();
				modifiers.put("id", rs.getString("id"));
				modifiers.put("name", rs.getString("modifier_group_name"));
				
				stmt = connection.prepareStatement("select mi.* from modifier_group mg " + 
						"inner join modifier_item_sequence mis on mis.modifier_group_id = mg.id " + 
						"inner join menu_item mi on mi.id = mis.menu_item_id " + 
						"where mg.id = ? and mi.menu_item_type = 2 and mi.is_active = 1 " + 
						"order by mis.modifier_item_sequence asc;");
				stmt.setLong(1, rs.getLong("id"));
				rs2 = stmt.executeQuery();
				
				JSONArray jary2 = new JSONArray();
				
				while (rs2.next()) {
					JSONObject modifierDetails = new JSONObject();
					modifierDetails.put("id", rs2.getString("id"));
					modifierDetails.put("backendId", rs2.getString("backend_id"));
					modifierDetails.put("name", rs2.getString("menu_item_name"));
//					modifierDetails.put("image_path", rs2.getString("menu_item_image_path"));
					modifierDetails.put("imagePath", "/jakarta-tomcat/webapps/ecposmanager/menuimage/2pc-combo.png");
					
					jary2.put(modifierDetails);
				}
				modifiers.put("jary", jary2);
				jary.put(modifiers);
			}
			jsonResult.put("jary", jary);
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

			stmt = connection.prepareStatement("select * from `check` where check_number = ? and table_number = ? and order_type = ? and device_type = ? and check_status in (1, 2);");
			stmt.setInt(1, order.getInt("checkNo"));
			stmt.setInt(2, order.getInt("tableNo"));
			stmt.setInt(3, order.getInt("orderType"));
			stmt.setInt(4, order.getInt("deviceType"));
			rs = stmt.executeQuery();

			if (rs.next()) {
				long checkId = rs.getLong("id");
				int checkNo = rs.getInt("check_number");
				
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
					stmt.setInt(2, checkNo);
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
	
	public long insertChildCheckDetail (long checkId, int checkNo, long parentCheckDetailId, JSONObject childItem, int orderQuantity) {
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
				stmt1.setInt(2, checkNo);
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
	
	public boolean updateCheck (long checkId, int checkNo) {
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
			stmt.setInt(2, checkNo);
			stmt.setLong(3, checkId);
			stmt.setInt(4, checkNo);
			stmt.setLong(5, checkId);
			stmt.setInt(6, checkNo);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				BigDecimal totalAmount = rs.getBigDecimal("total_amount");
				BigDecimal grandTotalAmount = totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
				BigDecimal totalAmountRoundingAdjustment = grandTotalAmount.subtract(totalAmount);
				BigDecimal amountPaid = rs.getBigDecimal("amount_paid") == null ? new BigDecimal("0.00") : rs.getBigDecimal("amount_paid");
				
				stmt1 = connection.prepareStatement("update `check` set total_item_quantity = ?,subtotal_amount = ?,total_tax_amount = ?,total_service_charge_amount = ?,total_amount = ?,total_amount_rounding_adjustment = ?,grand_total_amount = ?,overdue_amount = ?,check_status = 2,updated_date = now() where id = ? and check_number = ?;");
				stmt1.setInt(1, rs.getInt("quantity"));
				stmt1.setBigDecimal(2, rs.getBigDecimal("subtotal_amount"));
				stmt1.setBigDecimal(3, rs.getBigDecimal("total_tax_amount"));
				stmt1.setBigDecimal(4, rs.getBigDecimal("total_service_charge_amount"));
				stmt1.setBigDecimal(5, totalAmount);
				stmt1.setBigDecimal(6, totalAmountRoundingAdjustment);
				stmt1.setBigDecimal(7, grandTotalAmount);
				stmt1.setBigDecimal(8, grandTotalAmount.subtract(amountPaid));
				stmt1.setLong(9, checkId);
				stmt1.setInt(10, checkNo);
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
