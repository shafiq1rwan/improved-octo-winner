package mpay.ecpos_manager.web.restcontroller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

import mpay.ecpos_manager.general.constant.Constant;
import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.UserAuthenticationModel;
import mpay.ecpos_manager.general.utility.WebComponents;

@RestController
@RequestMapping("/rc/check")
public class RestC_check {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@RequestMapping(value = { "/get_checks" }, method = { RequestMethod.POST }, produces = "application/json")
	public String getChecklist(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				JSONObject jsonObj = new JSONObject(data);
	
				if (jsonObj.has(Constant.TABLE_NO)) {
					String table_no = jsonObj.getString(Constant.TABLE_NO);
					
					connection = dataSource.getConnection();
	
					stmt = connection.prepareStatement("SELECT * FROM `check` WHERE table_number = ? AND check_status IN (1,2);");
					stmt.setString(1, table_no);
					rs = stmt.executeQuery();
	
					JSONArray checks = new JSONArray();
					while (rs.next()) {
						checks.put(rs.getString("check_number"));
					}
					jsonResult.put("checks", checks);
					jsonResult.put(Constant.RESPONSE_CODE, "00");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Request");
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
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_check_list" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getCheckList(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				
				stmt = connection.prepareStatement("select c.id,c.check_number,s.staff_name,ot.name as order_type,c.table_number,c.total_item_quantity, " + 
						"c.grand_total_amount,c.deposit_amount,c.tender_amount,c.overdue_amount,cs.name as check_status,c.created_date " + 
						"from `check` c " + 
						"inner join staff s on s.id = c.staff_id " + 
						"inner join order_type ot on ot.id = c.order_type " + 
						"inner join check_status cs on cs.id = c.check_status " + 
						"order by created_date desc;");
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					JSONObject check = new JSONObject();
					check.put("id", rs.getLong("id"));
					check.put("checkNumber", rs.getString("check_number"));
					check.put("staffName", rs.getString("staff_name"));
					check.put("orderType", rs.getString("order_type"));
					check.put("tableNumber", rs.getInt("table_number") == 0 ? "-" : rs.getInt("table_number"));
					check.put("totalItemQuantity", rs.getInt("total_item_quantity"));
					check.put("grandTotalAmount", String.format("%.2f", rs.getBigDecimal("grand_total_amount")));
					check.put("depositAmount", String.format("%.2f", rs.getBigDecimal("deposit_amount")));
					check.put("tenderAmount", String.format("%.2f", rs.getBigDecimal("tender_amount")));
					check.put("overdueAmount", String.format("%.2f", rs.getBigDecimal("overdue_amount")));
					check.put("checkStatus", rs.getString("check_status"));
					check.put("createdDate", sdf.format(rs.getTimestamp("created_date")));
					
					jary.put(check);
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
	public String getCheckDetails(@PathVariable("orderType") String orderType, @PathVariable("checkNo") String checkNo, @PathVariable("tableNo") String tableNo, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		PreparedStatement stmt5 = null;
		PreparedStatement stmtA = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rsA = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				
				String tableNoCondition = null;
				if (orderType.equals("table")) {
					tableNoCondition = "table_number = " + tableNo;
				} else if (orderType.equals("take_away")) {
					tableNoCondition = "table_number is null";
				}
				
				stmt = connection.prepareStatement("select * from `check` c "
						+ "inner join check_status cs on cs.id = c.check_status "
						+ "where " + tableNoCondition + " and check_number = ? and check_status in (1, 2);");
				stmt.setString(1, checkNo);
				rs = stmt.executeQuery();
				
				if (rs.next()) {
					long id = rs.getLong("id");
					
					jsonResult.put("checkNo", rs.getString("check_number"));
					jsonResult.put("tableNo", rs.getString("table_number") == null ? "-" : rs.getString("table_number"));
					jsonResult.put("createdDate", sdf.format(rs.getTimestamp("created_date")));
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
						taxCharge.put("chargeAmount", new BigDecimal(rs2.getString("grand_total_charge_amount")));
						
						taxCharges.put(taxCharge);
					}
					jsonResult.put("taxCharges", taxCharges);
	
					stmt3 = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id is null and check_detail_status in (1, 2) order by id asc;");
					stmt3.setLong(1, id);
					stmt3.setString(2, checkNo);
					rs3 = stmt3.executeQuery();
					
					JSONArray grandParentItemArray = new JSONArray();
					while (rs3.next()) {
						long grandParentId = rs3.getLong("id");
						
						JSONObject grandParentItem = new JSONObject();
						grandParentItem.put("checkDetailId", rs3.getString("id"));
						grandParentItem.put("itemId", rs3.getString("menu_item_id"));
						grandParentItem.put("itemCode", rs3.getString("menu_item_code"));
						grandParentItem.put("itemName", rs3.getString("menu_item_name"));
						grandParentItem.put("itemPrice", rs3.getString("menu_item_price"));
						grandParentItem.put("itemQuantity", rs3.getInt("quantity"));
						grandParentItem.put("totalAmount", rs3.getString("total_amount"));
						
						stmtA = connection.prepareStatement("select * from menu_item mi " + 
								"left join menu_item_modifier_group mimg on mimg.menu_item_id = mi.id " + 
								"where mi.id = ?;");
						stmtA.setString(1, rs3.getString("menu_item_id"));
						rsA = stmtA.executeQuery();
	
						if (rsA.next()) {
							if (rsA.getInt("menu_item_type") == 0) {
								grandParentItem.put("isAlaCarte", true);
								
								if (rsA.getLong("menu_item_id") > 0) {
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
						
						stmt4 = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id = ? and check_detail_status in (1, 2) order by id asc;");
						stmt4.setLong(1, id);
						stmt4.setString(2, checkNo);
						stmt4.setLong(3, grandParentId);
						rs4 = stmt4.executeQuery();
						
						JSONArray parentItemArray = new JSONArray();
						while (rs4.next()) {
							long parentId = rs4.getLong("id");
							
							JSONObject parentItem = new JSONObject();
							parentItem.put("itemId", rs4.getString("menu_item_id"));
							parentItem.put("itemCode", rs4.getString("menu_item_code"));
							parentItem.put("itemName", rs4.getString("menu_item_name"));
							parentItem.put("itemPrice", rs4.getString("menu_item_price"));
							parentItem.put("itemQuantity", rs4.getString("quantity"));
							parentItem.put("totalAmount", rs4.getString("total_amount"));
							
							stmt5 = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id = ? and check_detail_status in (1, 2) order by id asc;");
							stmt5.setLong(1, id);
							stmt5.setString(2, checkNo);
							stmt5.setLong(3, parentId);
							rs5 = stmt5.executeQuery();
							
							JSONArray childItemArray = new JSONArray();
							while (rs5.next()) {
								JSONObject childItem = new JSONObject();
								childItem.put("itemId", rs5.getString("menu_item_id"));
								childItem.put("itemCode", rs5.getString("menu_item_code"));
								childItem.put("itemName", rs5.getString("menu_item_name"));
								childItem.put("itemPrice", rs5.getString("menu_item_price"));
								childItem.put("itemQuantity", rs5.getString("quantity"));
								childItem.put("totalAmount", rs5.getString("total_amount"));
								
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
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (rs4 != null) {rs4.close();rs4 = null;}
				if (rs5 != null) {rs5.close();rs5 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_all_check_detail/{orderType}/{checkNo}/{tableNo}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getAllCheckDetails(@PathVariable("orderType") String orderType, @PathVariable("checkNo") String checkNo, @PathVariable("tableNo") String tableNo, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		PreparedStatement stmt5 = null;
		PreparedStatement stmtA = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rsA = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				
				String tableNoCondition = null;
				if (orderType.equals("table")) {
					tableNoCondition = "table_number = " + tableNo;
				} else if (orderType.equals("take_away")) {
					tableNoCondition = "table_number is null";
				}
				
				stmt = connection.prepareStatement("select * from `check` c "
						+ "inner join check_status cs on cs.id = c.check_status "
						+ "where " + tableNoCondition + " and check_number = ?;");
				stmt.setString(1, checkNo);
				rs = stmt.executeQuery();
				
				if (rs.next()) {
					long id = rs.getLong("id");
					
					jsonResult.put("checkNo", rs.getString("check_number"));
					jsonResult.put("tableNo", rs.getString("table_number") == null ? "-" : rs.getString("table_number"));
					jsonResult.put("createdDate", sdf.format(rs.getTimestamp("created_date")));
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
						taxCharge.put("chargeAmount", new BigDecimal(rs2.getString("grand_total_charge_amount")));
						
						taxCharges.put(taxCharge);
					}
					jsonResult.put("taxCharges", taxCharges);
	
					stmt3 = connection.prepareStatement("select * from check_detail cd " +
							"inner join check_status cs on cs.id = cd.check_detail_status " +
							"where check_id = ? and check_number = ? and parent_check_detail_id is null order by cd.id asc;");
					stmt3.setLong(1, id);
					stmt3.setString(2, checkNo);
					rs3 = stmt3.executeQuery();
					
					JSONArray grandParentItemArray = new JSONArray();
					while (rs3.next()) {
						long grandParentId = rs3.getLong("id");
						
						JSONObject grandParentItem = new JSONObject();
						grandParentItem.put("checkDetailId", rs3.getString("id"));
						grandParentItem.put("itemId", rs3.getString("menu_item_id"));
						grandParentItem.put("itemCode", rs3.getString("menu_item_code"));
						grandParentItem.put("itemName", rs3.getString("menu_item_name"));
						grandParentItem.put("itemPrice", rs3.getString("menu_item_price"));
						grandParentItem.put("itemQuantity", rs3.getInt("quantity"));
						grandParentItem.put("itemStatus", rs3.getString("name").equals("Closed") ? "Paid" : rs3.getString("name"));
						grandParentItem.put("totalAmount", rs3.getString("total_amount"));
						
						stmtA = connection.prepareStatement("select * from menu_item mi " + 
								"left join menu_item_modifier_group mimg on mimg.menu_item_id = mi.id " + 
								"where mi.id = ?;");
						stmtA.setString(1, rs3.getString("menu_item_id"));
						rsA = stmtA.executeQuery();
	
						if (rsA.next()) {
							if (rsA.getInt("menu_item_type") == 0) {
								grandParentItem.put("isAlaCarte", true);
								
								if (rsA.getLong("menu_item_id") > 0) {
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
						
						stmt4 = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id = ? order by id asc;");
						stmt4.setLong(1, id);
						stmt4.setString(2, checkNo);
						stmt4.setLong(3, grandParentId);
						rs4 = stmt4.executeQuery();
						
						JSONArray parentItemArray = new JSONArray();
						while (rs4.next()) {
							long parentId = rs4.getLong("id");
							
							JSONObject parentItem = new JSONObject();
							parentItem.put("itemId", rs4.getString("menu_item_id"));
							parentItem.put("itemCode", rs4.getString("menu_item_code"));
							parentItem.put("itemName", rs4.getString("menu_item_name"));
							parentItem.put("itemPrice", rs4.getString("menu_item_price"));
							parentItem.put("itemQuantity", rs4.getString("quantity"));
							parentItem.put("totalAmount", rs4.getString("total_amount"));
							
							stmt5 = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id = ? order by id asc;");
							stmt5.setLong(1, id);
							stmt5.setString(2, checkNo);
							stmt5.setLong(3, parentId);
							rs5 = stmt5.executeQuery();
							
							JSONArray childItemArray = new JSONArray();
							while (rs5.next()) {
								JSONObject childItem = new JSONObject();
								childItem.put("itemId", rs5.getString("menu_item_id"));
								childItem.put("itemCode", rs5.getString("menu_item_code"));
								childItem.put("itemName", rs5.getString("menu_item_name"));
								childItem.put("itemPrice", rs5.getString("menu_item_price"));
								childItem.put("itemQuantity", rs5.getString("quantity"));
								childItem.put("totalAmount", rs5.getString("total_amount"));
								
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
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (rs4 != null) {rs4.close();rs4 = null;}
				if (rs5 != null) {rs5.close();rs5 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/create/table" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String createTableCheck(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("----------- CREATE TABLE CHECK START ---------", ECPOS_FOLDER);
		Logger.writeActivity("request: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				JSONObject jsonObj = new JSONObject(data);
	
				String username = user.getUsername();
				
				if (jsonObj.has("table_no")) {
					String tableNo = jsonObj.getString("table_no");
				
					connection = dataSource.getConnection();
					connection.setAutoCommit(false);
		
					stmt = connection.prepareStatement("select * from staff where staff_username = ?;");
					stmt.setString(1, username);
					rs = stmt.executeQuery();
		
					if (rs.next()) {
						long staffId = rs.getLong("id");
						
						stmt2 = connection.prepareStatement("select * from master where type = 'check';");
						rs2 = stmt2.executeQuery();
		
						if (rs2.next()) {
							int currentCheckNo = rs2.getInt("count");
							int newCheckNo = currentCheckNo + 1;
							
							stmt3 = connection.prepareStatement("update master set count = ? where type = 'check';");
							stmt3.setString(1, Integer.toString(newCheckNo));
							int rs3 = stmt3.executeUpdate();
		
							if (rs3 > 0) {
								stmt4 = connection.prepareStatement("insert into `check` (check_number,staff_id,order_type,table_number,total_item_quantity,total_amount,total_amount_with_tax,total_amount_with_tax_rounding_adjustment,grand_total_amount,deposit_amount,tender_amount,overdue_amount,check_status,created_date) " + 
										"values (?,?,1,?,0,0,0,0,0,0,0,0,1,now());");
								stmt4.setString(1, Integer.toString(newCheckNo));
								stmt4.setLong(2, staffId);
								stmt4.setString(3, tableNo);
								int rs4 = stmt4.executeUpdate();
		
								if (rs4 > 0) {
									connection.commit();
									Logger.writeActivity("Check Number: " + newCheckNo, ECPOS_FOLDER);
									jsonResult.put(Constant.CHECK_NO, newCheckNo);
									jsonResult.put(Constant.RESPONSE_CODE, "00");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Success");
								} else {
									connection.rollback();
									Logger.writeActivity("Check Master Failed To Insert", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Master Failed To Insert");
								}
							} else {
								connection.rollback();
								Logger.writeActivity("Check Count Failed To Update", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "01");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Count Failed To Update");
							}
						} else {
							connection.rollback();
							Logger.writeActivity("Check Count Not Found", ECPOS_FOLDER);
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Count Not Found");
						}
					} else {
						connection.rollback();
						Logger.writeActivity("Staff Not Found", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Staff Not Found");
					}
				} else {
					Logger.writeActivity("Request Not Complete", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Request Not Complete");
				}
				connection.setAutoCommit(true);
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
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("----------- CREATE TABLE CHECK END ---------", ECPOS_FOLDER);
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/create/take_away" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String createCheck(HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("----------- CREATE TAKE AWAY CHECK START ---------", ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		PreparedStatement stmtA = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rsA = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				connection.setAutoCommit(false);
				
				boolean proceedCreate = false;
				
				stmtA = connection.prepareStatement("select * from `check` where order_type = 2 order by id desc limit 1;");
				rsA = stmtA.executeQuery();
				
				if (rsA.next()) {
					if (rsA.getInt("check_status") == 1 && rsA.getString("updated_date") == null) {
						proceedCreate = false;
						connection.commit();
						Logger.writeActivity("Check Number: " + rsA.getString("check_number"), ECPOS_FOLDER);
						jsonResult.put(Constant.CHECK_NO, rsA.getString("check_number"));
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Success");
					} else {
						proceedCreate = true;
					}
				} else {
					proceedCreate = true;
				}
				
				if (proceedCreate) {
					String username = user.getUsername();
					
					stmt = connection.prepareStatement("select * from staff where staff_username = ?;");
					stmt.setString(1, username);
					rs = stmt.executeQuery();
		
					if (rs.next()) {
						long staffId = rs.getLong("id");
						
						stmt2 = connection.prepareStatement("select * from master where type = 'check';");
						rs2 = stmt2.executeQuery();
		
						if (rs2.next()) {
							int currentCheckNo = rs2.getInt("count");
							int newCheckNo = currentCheckNo + 1;
							
							stmt3 = connection.prepareStatement("update master set count = ? where type = 'check';");
							stmt3.setString(1, Integer.toString(newCheckNo));
							int rs3 = stmt3.executeUpdate();
		
							if (rs3 > 0) {
								stmt4 = connection.prepareStatement("insert into `check` (check_number,staff_id,order_type,total_item_quantity,total_amount,total_amount_with_tax,total_amount_with_tax_rounding_adjustment,grand_total_amount,deposit_amount,tender_amount,overdue_amount,check_status,created_date) " + 
										"values (?,?,2,0,0,0,0,0,0,0,0,1,now());");
								stmt4.setString(1, Integer.toString(newCheckNo));
								stmt4.setLong(2, staffId);
								int rs4 = stmt4.executeUpdate();
		
								if (rs4 > 0) {
									connection.commit();
									Logger.writeActivity("Check Number: " + newCheckNo, ECPOS_FOLDER);
									jsonResult.put(Constant.CHECK_NO, newCheckNo);
									jsonResult.put(Constant.RESPONSE_CODE, "00");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Success");
								} else {
									connection.rollback();
									Logger.writeActivity("Check Master Failed To Insert", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Master Failed To Insert");
								}
							} else {
								connection.rollback();
								Logger.writeActivity("Check Count Failed To Update", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "01");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Count Failed To Update");
							}
						} else {
							connection.rollback();
							Logger.writeActivity("Check Count Not Found", ECPOS_FOLDER);
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Count Not Found");
						}
					} else {
						connection.rollback();
						Logger.writeActivity("Staff Not Found", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Staff Not Found");
					}
				}
				connection.setAutoCommit(true);
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
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("----------- CREATE TAKE AWAY END ---------", ECPOS_FOLDER);
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/order" }, method = { RequestMethod.POST }, produces = "application/json")
	public String saveOrder(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("----------- CREATE ORDER START ---------", ECPOS_FOLDER);
		Logger.writeActivity("request: " + data, ECPOS_FOLDER);
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
		PreparedStatement stmt9 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rs6 = null;
		ResultSet rs7 = null;
		ResultSet rs8 = null;
		ResultSet rs9 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				JSONObject order = new JSONObject(data);
				
				connection = dataSource.getConnection();
				connection.setAutoCommit(false);
	
				DecimalFormat df = new DecimalFormat("#0.00");
	
				if ((order.has("deviceType") && !order.isNull("deviceType"))
						&& (order.has("orderType") && !order.isNull("orderType"))
						&& (order.has("checkNo") && !order.isNull("checkNo") && !order.getString("checkNo").isEmpty())) {
					if (order.has("item") && !order.isNull("item") && order.getJSONObject("item").length() > 0) {
						JSONObject item = order.getJSONObject("item");
						int orderQuantity = item.getInt("orderQuantity");
	
						String tableNoCondition = "table_number is null";
						if (order.getInt("orderType") == 1) {
							tableNoCondition = "table_number = " + order.getInt("tableNo");
						}
	
						stmt = connection.prepareStatement("select * from `check` where check_number = ? and " + tableNoCondition + " and order_type = ? and check_status in (1, 2);");
						stmt.setString(1, order.getString("checkNo"));
						stmt.setInt(2, order.getInt("orderType"));
						rs = stmt.executeQuery();
	
						if (rs.next()) {
							long checkId = rs.getLong("id");
							String checkNo = rs.getString("check_number");
	
							if ((item.has("id") && !item.isNull("id") && !item.getString("id").isEmpty())
									&& (item.has("backendId") && !item.isNull("backendId") && !item.getString("backendId").isEmpty())) {
								stmt2 = connection.prepareStatement("select * from menu_item where id = ? and backend_id = ?;");
								stmt2.setLong(1, item.getLong("id"));
								stmt2.setString(2, item.getString("backendId"));
								rs2 = stmt2.executeQuery();
	
								if (rs2.next()) {
									if (rs2.getBoolean("is_active") == true) {
										if (item.has("type") && !item.isNull("type") && !item.getString("type").isEmpty() && rs2.getString("menu_item_type").equals(item.getString("type"))) {
											if (item.has("price") && !item.isNull("price") && df.format(rs2.getBigDecimal("menu_item_base_price")).equals(df.format(new BigDecimal(item.getDouble("price"))))) {
												BigDecimal totalAmount = rs2.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(orderQuantity));
												boolean isItemTaxable = rs2.getBoolean("is_taxable");
	
												JSONObject charges = new JSONObject();
												JSONArray totalTaxes = new JSONArray();
												JSONArray overallTaxes = new JSONArray();
	
												if (isItemTaxable) {
													stmt3 = connection.prepareStatement("select tc.* from tax_charge tc " + 
															"inner join charge_type_lookup ctlu on ctlu.charge_type_number = tc.charge_type " + 
															"where tc.is_active = 1;");
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
												Logger.writeActivity("isItemTaxable: " + isItemTaxable, ECPOS_FOLDER);
												Logger.writeActivity("charges: " + charges, ECPOS_FOLDER);
	
												stmt4 = connection.prepareStatement("insert into check_detail (check_id,check_number,device_type,menu_item_id,menu_item_code,menu_item_name,menu_item_price,quantity,total_amount,check_detail_status,created_date) "
																+ "values (?,?,?,?,?,?,?,?,?,1,now());", Statement.RETURN_GENERATED_KEYS);
												stmt4.setLong(1, checkId);
												stmt4.setString(2, checkNo);
												stmt4.setInt(3, order.getInt("deviceType"));  
												stmt4.setLong(4, rs2.getLong("id"));
												stmt4.setString(5, rs2.getString("backend_id"));
												stmt4.setString(6, rs2.getString("menu_item_name"));
												stmt4.setString(7, rs2.getString("menu_item_base_price"));
												stmt4.setInt(8, orderQuantity);
												stmt4.setBigDecimal(9, totalAmount);
												int insert1stCheckDetail = stmt4.executeUpdate();
	
												if (insert1stCheckDetail > 0) {
													rs4 = stmt4.getGeneratedKeys();
	
													if (rs4.next()) {
														long parentCheckDetailId = rs4.getLong(1);
														
														boolean updateCheck = updateCheck(connection, checkId, checkNo, orderQuantity, totalAmount, isItemTaxable, charges);
	
														if (updateCheck) {
															if (item.getString("type").equals("0")) {
																// A La Carte
																stmt5 = connection.prepareStatement("select * from menu_item_modifier_group where menu_item_id = ?;");
																stmt5.setString(1, rs2.getString("id"));
																rs5 = stmt5.executeQuery();
		
																if (rs5.next()) {
																	if (item.has("modifiers") && !item.isNull("modifiers") && item.getJSONArray("modifiers").length() > 0) {
																		for (int i = 0; i < item.getJSONArray("modifiers").length(); i++) {
																			JSONObject modifier = item.getJSONArray("modifiers").getJSONObject(i);
		
																			if (modifier.has("id") && !modifier.isNull("id") && !modifier.getString("id").isEmpty()) {
																				stmt6 = connection.prepareStatement("select mg.is_active as mg_is_active, mi.* from menu_item_modifier_group mimg "
																								+ "inner join modifier_group mg on mimg.modifier_group_id = mg.id "
																								+ "inner join modifier_item_sequence mis on mis.modifier_group_id = mg.id "
																								+ "inner join menu_item mi on mi.id = mis.menu_item_id "
																								+ "where mimg.menu_item_id = ? and mi.id = ? and mi.backend_id = ?;");
																				stmt6.setString(1, rs2.getString("id"));
																				stmt6.setString(2, modifier.getString("id"));
																				stmt6.setString(3, modifier.getString("backendId"));
																				rs6 = stmt6.executeQuery();
		
																				if (rs6.next()) {
																					if (rs6.getBoolean("mg_is_active") == true && rs6.getBoolean("is_active") == true) {
																						if (rs6.getString("menu_item_type").equals("2")) {
																							long modifierCheckDetailId = insertChildCheckDetail(connection, order.getInt("deviceType"), checkId, checkNo, parentCheckDetailId, modifier, orderQuantity, isItemTaxable, charges);
		
																							if (modifierCheckDetailId > 0) {
																								connection.commit();
																								Logger.writeActivity("Item Successfully Ordered", ECPOS_FOLDER);
																								jsonResult.put(Constant.RESPONSE_CODE, "00");
																								jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Successfully Ordered");
																							} else {
																								connection.rollback();
																								Logger.writeActivity("Modifier Item Failed To Insert", ECPOS_FOLDER);
																								jsonResult.put(Constant.RESPONSE_CODE, "01");
																								jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Failed To Insert");
																							}
																						} else {
																							connection.rollback();
																							Logger.writeActivity("Modifier Item Type Not Match", ECPOS_FOLDER);
																							jsonResult.put(Constant.RESPONSE_CODE, "01");
																							jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Type Not Match");
																						}
																					} else {
																						connection.rollback();
																						Logger.writeActivity("Modifier Item Not Active", ECPOS_FOLDER);
																						jsonResult.put(Constant.RESPONSE_CODE, "01");
																						jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Not Active");
																					}
																				} else {
																					connection.rollback();
																					Logger.writeActivity("Modifier Item Not Found", ECPOS_FOLDER);
																					jsonResult.put(Constant.RESPONSE_CODE, "01");
																					jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Not Found");
																				}
																			} else {
																				connection.rollback();
																				Logger.writeActivity("Modifier Item Not Found", ECPOS_FOLDER);
																				jsonResult.put(Constant.RESPONSE_CODE, "01");
																				jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Not Found");
																			}
																		}
																	} else {
																		connection.rollback();
																		Logger.writeActivity("Modifier Item Not Found", ECPOS_FOLDER);
																		jsonResult.put(Constant.RESPONSE_CODE, "01");
																		jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Not Found");
																	}
																} else {
																	if (item.has("modifiers") && !item.isNull("modifiers") && item.getJSONArray("modifiers").length() > 0) {
																		connection.rollback();
																		Logger.writeActivity("Modifier Item Not Found", ECPOS_FOLDER);
																		jsonResult.put(Constant.RESPONSE_CODE, "01");
																		jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Not Found");
																	} else {
																		connection.commit();
																		Logger.writeActivity("Item Successfully Ordered", ECPOS_FOLDER);
																		jsonResult.put(Constant.RESPONSE_CODE, "00");
																		jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Successfully Ordered");
																	}
																}
															} else if (item.getString("type").equals("1")) {
																// Combo
		
																// get tier quantity
																stmt5 = connection.prepareStatement("select * from combo_detail where menu_item_id = ?;");
																stmt5.setString(1, rs2.getString("id"));
																rs5 = stmt5.executeQuery();
		
																JSONArray comboTiers = new JSONArray();
																int totalTier = 0;
																while (rs5.next()) {
																	JSONObject comboTierInfo = new JSONObject();
																	comboTierInfo.put("combo_detail_id", rs5.getString("id"));
																	comboTierInfo.put("combo_detail_quantity", rs5.getInt("combo_detail_quantity"));
																	totalTier++;
																	
																	comboTiers.put(comboTierInfo);
																}
																Logger.writeActivity("Correct comboTiers: " + comboTiers, ECPOS_FOLDER);
		
																// combo items details checking
																boolean isCheckSuccess = false;
		
																if (item.has("tiers") && !item.isNull("tiers") && item.getJSONArray("tiers").length() > 0) {
																	main: for (int i = 0; i < item.getJSONArray("tiers").length(); i++) {
																		JSONObject tier = item.getJSONArray("tiers").getJSONObject(i);
		
																		if (tier.has("id") && !tier.isNull("id") && !tier.getString("id").isEmpty()) {
																			stmt6 = connection.prepareStatement("select cid.* from combo_detail cd "
																							+ "inner join combo_item_detail cid on cid.combo_detail_id = cd.id "
																							+ "where cd.id = ? and cd.menu_item_id = ?;");
																			stmt6.setLong(1, tier.getLong("id"));
																			stmt6.setString(2, rs2.getString("id"));
																			rs6 = stmt6.executeQuery();
		
																			if (rs6.next()) {
																				if (tier.has("items") && !tier.isNull("items") && tier.getJSONArray("items").length() > 0) {
																					for (int j = 0; j < tier.getJSONArray("items").length(); j++) {
																						JSONObject tierItem = tier.getJSONArray("items").getJSONObject(j);
		
																						if ((tierItem.has("id") && !tierItem.isNull("id") && !tierItem.getString("id").isEmpty()) 
																								&& (tierItem.has("backendId") && !tierItem.isNull("backendId") && !tierItem.getString("backendId").isEmpty())) {
																							if (rs6.getString("menu_item_id") != null && !rs6.getString("menu_item_id").isEmpty()) {
																								stmt7 = connection.prepareStatement("select mi.* from combo_item_detail cid "
																														+ "inner join menu_item mi on mi.id = cid.menu_item_id "
																														+ "where cid.id = ? and mi.id = ? and mi.backend_id = ?;");
																							} else if (rs6.getString("menu_item_group_id") != null && !rs6.getString("menu_item_group_id").isEmpty()) {
																								stmt7 = connection.prepareStatement("select mi.* from combo_item_detail cid "
																														+ "inner join menu_item_group mig on mig.id = cid.menu_item_group_id "
																														+ "inner join menu_item_group_sequence migs on migs.menu_item_group_id = mig.id "
																														+ "inner join menu_item mi on mi.id = migs.menu_item_id "
																														+ "where cid.id = ? and mi.id = ? and mi.backend_id = ?");
																							}
																							stmt7.setString(1, rs6.getString("id"));
																							stmt7.setString(2, tierItem.getString("id"));
																							stmt7.setString(3, tierItem.getString("backendId"));
																							rs7 = stmt7.executeQuery();
		
																							if (rs7.next()) {
																								if (rs7.getBoolean("is_active") == true) {
																									long childCheckDetailId = insertChildCheckDetail(connection, order.getInt("deviceType"), checkId, checkNo, parentCheckDetailId, tierItem, orderQuantity, isItemTaxable, charges);
		
																									if (childCheckDetailId > 0) {
																										stmt8 = connection.prepareStatement("select * from menu_item_modifier_group where menu_item_id = ?;");
																										stmt8.setString(1, rs7.getString("id"));
																										rs8 = stmt8.executeQuery();
		
																										if (rs8.next()) {
																											if (tierItem.has("modifiers") && !tierItem.isNull("modifiers") && tierItem.getJSONArray("modifiers").length() > 0) {
																												for (int k = 0; k < tierItem.getJSONArray("modifiers").length(); k++) {
																													JSONObject modifier = tierItem.getJSONArray("modifiers").getJSONObject(k);
		
																													if ((modifier.has("id") && !modifier.isNull("id") && !modifier.getString("id").isEmpty())
																															&& (modifier.has("backendId") && !modifier.isNull("backendId") && !modifier.getString("backendId").isEmpty())) {
																														stmt9 = connection.prepareStatement("select mg.is_active as mg_is_active, mi.* from menu_item_modifier_group mimg "
																																				+ "inner join modifier_group mg on mimg.modifier_group_id = mg.id "
																																				+ "inner join modifier_item_sequence mis on mis.modifier_group_id = mg.id "
																																				+ "inner join menu_item mi on mi.id = mis.menu_item_id "
																																				+ "where mimg.menu_item_id = ? and mi.id = ? and mi.backend_id = ?;");
																														stmt9.setString(1, rs7.getString("id"));
																														stmt9.setString(2, modifier.getString("id"));
																														stmt9.setString(3, modifier.getString("backendId"));
																														rs9 = stmt9.executeQuery();
		
																														if (rs9.next()) {
																															if (rs9.getBoolean("mg_is_active") == true&& rs9.getBoolean("is_active") == true) {
																																if (rs9.getString("menu_item_type").equals("2")) {
																																	long modifierCheckDetailId = insertChildCheckDetail(connection, order.getInt("deviceType"), checkId, checkNo, childCheckDetailId, modifier, orderQuantity, isItemTaxable, charges);
																																	
																																	if (modifierCheckDetailId > 0) {
																																		isCheckSuccess = true;
																																		Logger.writeActivity("Item Successfully Ordered", ECPOS_FOLDER);
																																		jsonResult.put(Constant.RESPONSE_CODE, "00");
																																		jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Successfully Ordered");
																																	} else {
																																		connection.rollback();
																																		Logger.writeActivity("Modifier Item Failed To Insert", ECPOS_FOLDER);
																																		jsonResult.put(Constant.RESPONSE_CODE, "01");
																																		jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Failed To Insert");
																																		break main;
																																	}
																																} else {
																																	connection.rollback();
																																	Logger.writeActivity("Modifier Item Type Not Match", ECPOS_FOLDER);
																																	jsonResult.put(Constant.RESPONSE_CODE, "01");
																																	jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Type Not Match");
																																	break main;
																																}
																															} else {
																																connection.rollback();
																																Logger.writeActivity("Modifier Item Not Active", ECPOS_FOLDER);
																																jsonResult.put(Constant.RESPONSE_CODE, "01");
																																jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Not Active");
																																break main;
																															}
																														} else {
																															connection.rollback();
																															Logger.writeActivity("Modifier Item Not Found", ECPOS_FOLDER);
																															jsonResult.put(Constant.RESPONSE_CODE, "01");
																															jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Not Found");
																															break main;
																														}
																													} else {
																														connection.rollback();
																														Logger.writeActivity("Modifier Item Not Found", ECPOS_FOLDER);
																														jsonResult.put(Constant.RESPONSE_CODE, "01");
																														jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Not Found");
																														break main;
																													}
																												}
																											} else {
																												connection.rollback();
																												Logger.writeActivity("Modifier Item Not Found", ECPOS_FOLDER);
																												jsonResult.put(Constant.RESPONSE_CODE, "01");
																												jsonResult.put(Constant.RESPONSE_MESSAGE, "Modifier Item Not Found");
																												break main;
																											}
																										} else {
																											if (tierItem.has("modifiers") && !tierItem.isNull("modifiers") && tierItem.getJSONArray("modifiers").length() > 0) {
																												connection.rollback();
																												Logger.writeActivity("Tier Item Modifier Not Found", ECPOS_FOLDER);
																												jsonResult.put(Constant.RESPONSE_CODE, "01");
																												jsonResult.put(Constant.RESPONSE_MESSAGE, "Tier Item Modifier Not Found");
																												break main;
																											} else {
																												isCheckSuccess = true;
																												Logger.writeActivity("Item Successfully Ordered", ECPOS_FOLDER);
																												jsonResult.put(Constant.RESPONSE_CODE, "00");
																												jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Successfully Ordered");
																											}
																										}
																									} else {
																										connection.rollback();
																										Logger.writeActivity("Tier Item Failed To Insert", ECPOS_FOLDER);
																										jsonResult.put(Constant.RESPONSE_CODE, "01");
																										jsonResult.put(Constant.RESPONSE_MESSAGE, "Tier Item Failed To Insert");
																										break main;
																									}
																								} else {
																									connection.rollback();
																									Logger.writeActivity("Tier Item Not Active", ECPOS_FOLDER);
																									jsonResult.put(Constant.RESPONSE_CODE, "01");
																									jsonResult.put(Constant.RESPONSE_MESSAGE, "Tier Item Not Active");
																									break main;
																								}
																							} else {
																								connection.rollback();
																								Logger.writeActivity("Tier Item Not Found", ECPOS_FOLDER);
																								jsonResult.put(Constant.RESPONSE_CODE, "01");
																								jsonResult.put(Constant.RESPONSE_MESSAGE, "Tier Item Not Found");
																								break main;
																							}
																						} else {
																							connection.rollback();
																							Logger.writeActivity("Item Tier Not Found", ECPOS_FOLDER);
																							jsonResult.put(Constant.RESPONSE_CODE, "01");
																							jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Tier Not Found");
																							break main;
																						}
																						
																						// deduct tier quantity
																						for (int k = 0; k < comboTiers.length(); k++) {
																							JSONObject comboTierInfo = comboTiers.getJSONObject(k);
		
																							if (comboTierInfo.getLong("combo_detail_id") == (tier.getLong("id"))) {
																								int deductedQuantity = comboTierInfo.getInt("combo_detail_quantity") - 1;
																								comboTierInfo.put("combo_detail_quantity", deductedQuantity);
																							}
																						}
																					}
																				} else {
																					connection.rollback();
																					Logger.writeActivity("Item Tier Not Found", ECPOS_FOLDER);
																					jsonResult.put(Constant.RESPONSE_CODE, "01");
																					jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Tier Not Found");
																					break main;
																				}
																			} else {
																				connection.rollback();
																				Logger.writeActivity("Tier Not Found", ECPOS_FOLDER);
																				jsonResult.put(Constant.RESPONSE_CODE, "01");
																				jsonResult.put(Constant.RESPONSE_MESSAGE, "Tier Not Found");
																				break main;
																			}
																		} else {
																			connection.rollback();
																			Logger.writeActivity("Tier Not Found", ECPOS_FOLDER);
																			jsonResult.put(Constant.RESPONSE_CODE, "01");
																			jsonResult.put(Constant.RESPONSE_MESSAGE, "Tier Not Found");
																			break main;
																		}
																	}
																
																	// check if tier quantity equal to 0, if yes then success else fail
																	if (isCheckSuccess) {
																		for (int j = 0; j < comboTiers.length(); j++) {
																			JSONObject comboTierInfo = comboTiers.getJSONObject(j);
		
																			if (comboTierInfo.getInt("combo_detail_quantity") == 0) {
																				totalTier--;
																			} else {
																				connection.rollback();
																				Logger.writeActivity("Combo Item Quantity Not Match", ECPOS_FOLDER);
																				jsonResult.put(Constant.RESPONSE_CODE, "01");
																				jsonResult.put(Constant.RESPONSE_MESSAGE, "Combo Item Quantity Not Match");
																			}
																		}
																		
																		if (totalTier == 0) {
																			connection.commit();
																			Logger.writeActivity("Item Successfully Ordered", ECPOS_FOLDER);
																			jsonResult.put(Constant.RESPONSE_CODE, "00");
																			jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Successfully Ordered");
																		}
																	}
																} else {
																	connection.rollback();
																	Logger.writeActivity("Item Tier Not Found", ECPOS_FOLDER);
																	jsonResult.put(Constant.RESPONSE_CODE, "01");
																	jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Tier Not Found");
																}
															} else {
																connection.rollback();
																Logger.writeActivity("Item Type Not Match", ECPOS_FOLDER);
																jsonResult.put(Constant.RESPONSE_CODE, "01");
																jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Type Not Match");
															}
														} else {
															connection.rollback();
															Logger.writeActivity("Failed to update check", ECPOS_FOLDER);
															jsonResult.put(Constant.RESPONSE_CODE, "01");
															jsonResult.put(Constant.RESPONSE_MESSAGE, "Failed to update check");
														}
													} else {
														connection.rollback();
														Logger.writeActivity("Check Detail ID Failed To Return", ECPOS_FOLDER);
														jsonResult.put(Constant.RESPONSE_CODE, "01");
														jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail ID Failed To Return");
													}
												} else {
													connection.rollback();
													Logger.writeActivity("Check Detail Failed To Insert", ECPOS_FOLDER);
													jsonResult.put(Constant.RESPONSE_CODE, "01");
													jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Failed To Insert");
												}
											} else {
												connection.rollback();
												Logger.writeActivity("Item Price Not Match", ECPOS_FOLDER);
												jsonResult.put(Constant.RESPONSE_CODE, "01");
												jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Price Not Match");
											}
										} else {
											connection.rollback();
											Logger.writeActivity("Item Type Not Match", ECPOS_FOLDER);
											jsonResult.put(Constant.RESPONSE_CODE, "01");
											jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Type Not Match");
										}
									} else {
										connection.rollback();
										Logger.writeActivity("Item Not Active", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Not Active");
									}
								} else {
									connection.rollback();
									Logger.writeActivity("Item Not Found", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Not Found");
								}
							} else {
								connection.rollback();
								Logger.writeActivity("Item Not Found", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "01");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Not Found");
							}
						} else {
							connection.rollback();
							Logger.writeActivity("Check Not Found", ECPOS_FOLDER);
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Not Found");
						}
					} else {
						Logger.writeActivity("Order Item Not Found", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Order Item Not Found");
					}
				} else {
					Logger.writeActivity("Request Not Complete", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Request Not Complete");
				}
				connection.setAutoCommit(true);
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
				if (stmt9 != null) stmt9.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (rs4 != null) {rs4.close();rs4 = null;}
				if (rs5 != null) {rs5.close();rs5 = null;}
				if (rs6 != null) {rs6.close();rs6 = null;}
				if (rs7 != null) {rs7.close();rs7 = null;}
				if (rs8 != null) {rs8.close();rs8 = null;}
				if (rs9 != null) {rs9.close();rs9 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("----------- CREATE ORDER END ---------", ECPOS_FOLDER);
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/barcode_order" }, method = { RequestMethod.POST }, produces = "application/json")
	public String saveBarcodeOrder(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("----------- CREATE BARCODE ORDER START ---------", ECPOS_FOLDER);
		Logger.writeActivity("request: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
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
				JSONObject order = new JSONObject(data);
				
				connection = dataSource.getConnection();
				connection.setAutoCommit(false);
	
				if ((order.has("deviceType") && !order.isNull("deviceType"))
						&& (order.has("orderType") && !order.isNull("orderType"))
						&& (order.has("checkNo") && !order.isNull("checkNo") && !order.getString("checkNo").isEmpty())) {
					if (order.has("barcode") && !order.isNull("barcode") && !order.getString("barcode").isEmpty()) {
						String itemBarcode = order.getString("barcode");
						int orderQuantity = 1;
	
						String tableNoCondition = "table_number is null";
						if (order.getInt("orderType") == 1) {
							tableNoCondition = "table_number = " + order.getInt("tableNo");
						}
	
						stmt = connection.prepareStatement("select * from `check` where check_number = ? and " + tableNoCondition + " and order_type = ? and check_status in (1, 2);");
						stmt.setString(1, order.getString("checkNo"));
						stmt.setInt(2, order.getInt("orderType"));
						rs = stmt.executeQuery();
	
						if (rs.next()) {
							long checkId = rs.getLong("id");
							String checkNo = rs.getString("check_number");
	
							stmt2 = connection.prepareStatement("select * from menu_item where menu_item_barcode = ?;");
							stmt2.setString(1, itemBarcode);
							rs2 = stmt2.executeQuery();
	
							if (rs2.next()) {
								if (rs2.getBoolean("is_active") == true) {
									if (rs2.getString("menu_item_type").equals("0")) {
										BigDecimal totalAmount = rs2.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(orderQuantity));
										boolean isItemTaxable = rs2.getBoolean("is_taxable");
	
										JSONObject charges = new JSONObject();
										JSONArray totalTaxes = new JSONArray();
										JSONArray overallTaxes = new JSONArray();
	
										if (isItemTaxable) {
											stmt3 = connection.prepareStatement("select tc.* from tax_charge tc " + 
													"inner join charge_type_lookup ctlu on ctlu.charge_type_number = tc.charge_type " + 
													"where tc.is_active = 1;");
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
										Logger.writeActivity("isItemTaxable: " + isItemTaxable, ECPOS_FOLDER);
										Logger.writeActivity("charges: " + charges, ECPOS_FOLDER);
	
										stmt4 = connection.prepareStatement("insert into check_detail (check_id,check_number,device_type,menu_item_id,menu_item_code,menu_item_name,menu_item_price,quantity,total_amount,check_detail_status,created_date) "
														+ "values (?,?,?,?,?,?,?,?,?,1,now());", Statement.RETURN_GENERATED_KEYS);
										stmt4.setLong(1, checkId);
										stmt4.setString(2, checkNo);
										stmt4.setInt(3, order.getInt("deviceType"));  
										stmt4.setLong(4, rs2.getLong("id"));
										stmt4.setString(5, rs2.getString("backend_id"));
										stmt4.setString(6, rs2.getString("menu_item_name"));
										stmt4.setString(7, rs2.getString("menu_item_base_price"));
										stmt4.setInt(8, orderQuantity);
										stmt4.setBigDecimal(9, totalAmount);
										int insert1stCheckDetail = stmt4.executeUpdate();
	
										if (insert1stCheckDetail > 0) {
											rs4 = stmt4.getGeneratedKeys();
	
											if (rs4.next()) {
												long parentCheckDetailId = rs4.getLong(1);
	
												boolean updateCheck = updateCheck(connection, checkId, checkNo, orderQuantity, totalAmount, isItemTaxable, charges);
												
												if (updateCheck) {
													// Only A La Carte
													stmt5 = connection.prepareStatement("select * from menu_item_modifier_group where menu_item_id = ?;");
													stmt5.setString(1, rs2.getString("id"));
													rs5 = stmt5.executeQuery();
	
													if (rs5.next()) {
														connection.rollback();
														Logger.writeActivity("Item Cannot Order Through Barcode, Contain Modifier", ECPOS_FOLDER);
														jsonResult.put(Constant.RESPONSE_CODE, "01");
														jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Cannot Order Through Barcode, Contain Modifier");
													} else {
														connection.commit();
														Logger.writeActivity("Item Successfully Ordered", ECPOS_FOLDER);
														jsonResult.put(Constant.RESPONSE_CODE, "00");
														jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Successfully Ordered");
													}
												} else {
													connection.rollback();
													Logger.writeActivity("Failed to update check", ECPOS_FOLDER);
													jsonResult.put(Constant.RESPONSE_CODE, "01");
													jsonResult.put(Constant.RESPONSE_MESSAGE, "Failed to update check");
												}
											} else {
												connection.rollback();
												Logger.writeActivity("Check Detail ID Failed To Return", ECPOS_FOLDER);
												jsonResult.put(Constant.RESPONSE_CODE, "01");
												jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail ID Failed To Return");
											}
										} else {
											connection.rollback();
											Logger.writeActivity("Check Detail Failed To Insert", ECPOS_FOLDER);
											jsonResult.put(Constant.RESPONSE_CODE, "01");
											jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Failed To Insert");
										}
									} else {
										connection.rollback();
										Logger.writeActivity("Item Type Not Match", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Type Not Match");
									}
								} else {
									connection.rollback();
									Logger.writeActivity("Item Not Active", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Not Active");
								}
							} else {
								connection.rollback();
								Logger.writeActivity("Item Not Found", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "01");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Not Found");
							}
						} else {
							connection.rollback();
							Logger.writeActivity("Check Not Found", ECPOS_FOLDER);
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Not Found");
						}
					} else {
						Logger.writeActivity("Order Item Not Found", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Order Item Not Found");
					}
				} else {
					Logger.writeActivity("Request Not Complete", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Request Not Complete");
				}
				connection.setAutoCommit(true);
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
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (rs4 != null) {rs4.close();rs4 = null;}
				if (rs5 != null) {rs5.close();rs5 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("----------- CREATE BARCODE ORDER END ---------", ECPOS_FOLDER);
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/update_item_quantity" }, method = { RequestMethod.POST }, produces = "application/json")
	public String updateItemQuantity(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("----------- UPDATE ITEM QUANTITY START ---------", ECPOS_FOLDER);
		Logger.writeActivity("request: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
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
				JSONObject detail = new JSONObject(data);
				
				connection = dataSource.getConnection();
				connection.setAutoCommit(false);
	
				if ((detail.has("id") && !detail.isNull("id"))
						&& (detail.has("quantity") && !detail.isNull("quantity"))) {
					long checkDetailId = detail.getLong("id");
					int quantity = detail.getInt("quantity");
	
					stmt = connection.prepareStatement("select * from check_detail where id = ?;");
					stmt.setLong(1, checkDetailId);
					rs = stmt.executeQuery();
	
					if (rs.next()) {
						long checkId = rs.getLong("check_id");
						String checkNo = rs.getString("check_number");
						
						stmt2 = connection.prepareStatement("select * from menu_item where id = ?;");
						stmt2.setLong(1, rs.getLong("menu_item_id"));
						rs2 = stmt2.executeQuery();
	
						if (rs2.next()) {
							if (rs2.getString("menu_item_type").equals("0")) {
								BigDecimal totalAmount = rs2.getBigDecimal("menu_item_base_price").multiply(new BigDecimal(quantity));
								boolean isItemTaxable = rs2.getBoolean("is_taxable");
	
								JSONObject charges = new JSONObject();
								JSONArray totalTaxes = new JSONArray();
								JSONArray overallTaxes = new JSONArray();
	
								if (isItemTaxable) {
									stmt3 = connection.prepareStatement("select tc.* from tax_charge tc " + 
											"inner join charge_type_lookup ctlu on ctlu.charge_type_number = tc.charge_type " + 
											"where tc.is_active = 1;");
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
								Logger.writeActivity("isItemTaxable: " + isItemTaxable, ECPOS_FOLDER);
								Logger.writeActivity("charges: " + charges, ECPOS_FOLDER);
	
								stmt4 = connection.prepareStatement("update check_detail set quantity = ?,total_amount = ? where id = ?;");
								stmt4.setInt(1, quantity);
								stmt4.setBigDecimal(2, totalAmount);
								stmt4.setLong(3, checkDetailId);
								int updateCheckDetail = stmt4.executeUpdate();
	
								if (updateCheckDetail > 0) {
									boolean updateDeductCheck = updateCancelledItemCheck(connection, checkId, checkNo, quantity, rs.getBigDecimal("total_amount"), isItemTaxable, charges);
									
									if (updateDeductCheck) {
										boolean updateCheck = updateCheck(connection, checkId, checkNo, quantity, totalAmount, isItemTaxable, charges);
									
										if (updateCheck) {
											// Only A La Carte
											stmt5 = connection.prepareStatement("select * from menu_item_modifier_group where menu_item_id = ?;");
											stmt5.setString(1, rs2.getString("id"));
											rs5 = stmt5.executeQuery();
		
											if (rs5.next()) {
												connection.rollback();
												Logger.writeActivity("Item Quantity Failed To Update, Contain Modifier", ECPOS_FOLDER);
												jsonResult.put(Constant.RESPONSE_CODE, "01");
												jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Quantity Failed To Update, Contain Modifier");
											} else {
												connection.commit();
												Logger.writeActivity("Item Quantity Successfully Updated", ECPOS_FOLDER);
												jsonResult.put(Constant.RESPONSE_CODE, "00");
												jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Quantity Successfully Updated");
											}
										} else {
											connection.rollback();
											Logger.writeActivity("Failed to update check", ECPOS_FOLDER);
											jsonResult.put(Constant.RESPONSE_CODE, "01");
											jsonResult.put(Constant.RESPONSE_MESSAGE, "Failed to update check");
										}
									} else {
										connection.rollback();
										Logger.writeActivity("Failed to update check", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Failed to update check");
									}
								} else {
									connection.rollback();
									Logger.writeActivity("Check Detail Failed To Update", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Failed To Update");
								}
							} else {
								connection.rollback();
								Logger.writeActivity("Item Type Not Match", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "01");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Type Not Match");
							}
						} else {
							connection.rollback();
							Logger.writeActivity("Item Not Found", ECPOS_FOLDER);
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Not Found");
						}
					} else {
						connection.rollback();
						Logger.writeActivity("Check Detail Not Found", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Not Found");
					}
				} else {
					Logger.writeActivity("Request Not Complete", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Request Not Complete");
				}
				connection.setAutoCommit(true);
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
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (rs4 != null) {rs4.close();rs4 = null;}
				if (rs5 != null) {rs5.close();rs5 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("----------- UPDATE ITEM QUANTITY END ---------", ECPOS_FOLDER);
		return jsonResult.toString();
	}

	@RequestMapping(value = { "/cancel_item" }, method = { RequestMethod.POST }, produces = "application/json")
	public String cancelItem(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("----------- CANCEL ITEM START ---------", ECPOS_FOLDER);
		Logger.writeActivity("request: " + data, ECPOS_FOLDER);
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

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				connection.setAutoCommit(false);
				
				JSONObject jsonData = new JSONObject(data);
				
				if (jsonData.has("checkDetailIdArray") && !jsonData.isNull("checkDetailIdArray") && jsonData.getJSONArray("checkDetailIdArray").length() > 0) {
					JSONArray checkDetailIdArray = jsonData.getJSONArray("checkDetailIdArray");
					
					long checkId = -1;
					String checkNo = null;
					
					boolean proceed = false;
					loop: for (int i = 0; i < checkDetailIdArray.length(); i++) {
						long checkDetailId = checkDetailIdArray.getLong(i);
						
						stmt = connection.prepareStatement("select * from check_detail where id = ?;");
						stmt.setLong(1, checkDetailId);
						rs = stmt.executeQuery();
						
						if (rs.next()) {
							checkId = rs.getLong("check_id");
							checkNo = rs.getString("check_number");
							int orderQuantity = rs.getInt("quantity");
							BigDecimal totalAmount = rs.getBigDecimal("total_amount");
							
							stmt2 = connection.prepareStatement("select * from menu_item where id = ? and backend_id = ?");
							stmt2.setLong(1, rs.getLong("menu_item_id"));
							stmt2.setString(2, rs.getString("menu_item_code"));
							rs2 = stmt2.executeQuery();
							
							if (rs2.next()) {
								boolean isItemTaxable = rs2.getBoolean("is_taxable");
								
								JSONObject charges = new JSONObject();
								JSONArray totalTaxes = new JSONArray();
								JSONArray overallTaxes = new JSONArray();
	
								if (isItemTaxable) {
									stmt3 = connection.prepareStatement("select tc.* from tax_charge tc " + 
											"inner join charge_type_lookup ctlu on ctlu.charge_type_number = tc.charge_type " + 
											"where tc.is_active = 1;");
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
								Logger.writeActivity("isItemTaxable: " + isItemTaxable, ECPOS_FOLDER);
								Logger.writeActivity("charges: " + charges, ECPOS_FOLDER);
								
								stmt4 = connection.prepareStatement("update check_detail set check_detail_status = 4, updated_date = now() where id = ?;");
								stmt4.setLong(1, checkDetailId);
								int updateGrandParentCheckDetail = stmt4.executeUpdate();
								
								if (updateGrandParentCheckDetail > 0) {
									boolean updateCheck = updateCancelledItemCheck(connection, checkId, checkNo, orderQuantity, totalAmount, isItemTaxable, charges);
									
									if (updateCheck) {
										stmt5 = connection.prepareStatement("select * from check_detail where parent_check_detail_id = ?;");
										stmt5.setLong(1, checkDetailId);
										rs4 = stmt5.executeQuery();
										
										boolean noSub = true;
										
										while (rs4.next()) {
											noSub = false;
											orderQuantity = rs4.getInt("quantity");
											totalAmount = rs4.getBigDecimal("total_amount");
											
											stmt6 = connection.prepareStatement("update check_detail set check_detail_status = 4, updated_date = now() where id = ?;");
											stmt6.setLong(1, rs4.getLong("id"));
											int updateParentCheckDetail = stmt6.executeUpdate();
											
											if (updateParentCheckDetail > 0) {
												updateCheck = updateCancelledItemCheck(connection, checkId, checkNo, orderQuantity, totalAmount, isItemTaxable, charges);
											
												if (updateCheck) {
													stmt7 = connection.prepareStatement("select * from check_detail where parent_check_detail_id = ?;");
													stmt7.setLong(1, rs4.getLong("id"));
													rs5 = stmt7.executeQuery();
												
													boolean noSub2 = true;
													
													while (rs5.next()) {
														noSub = false;
														orderQuantity = rs5.getInt("quantity");
														totalAmount = rs5.getBigDecimal("total_amount");
														
														stmt8 = connection.prepareStatement("update check_detail set check_detail_status = 4, updated_date = now() where id = ?;");
														stmt8.setLong(1, rs5.getLong("id"));
														int updateChildCheckDetail = stmt8.executeUpdate();
														
														if (updateChildCheckDetail > 0) {
															updateCheck = updateCancelledItemCheck(connection, checkId, checkNo, orderQuantity, totalAmount, isItemTaxable, charges);
														
															if (updateCheck) {
																proceed = true;
															} else {
																proceed = false;
																connection.rollback();
																Logger.writeActivity("Check Failed To Update", ECPOS_FOLDER);
																jsonResult.put(Constant.RESPONSE_CODE, "01");
																jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Failed To Update");
																break loop;
															}
														} else {
															proceed = false;
															connection.rollback();
															Logger.writeActivity("Check Detail Failed To Update", ECPOS_FOLDER);
															jsonResult.put(Constant.RESPONSE_CODE, "01");
															jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Failed To Update");
															break loop;
														}
													}
													
													if (noSub2) {
														proceed = true;
													}
												} else {
													proceed = false;
													connection.rollback();
													Logger.writeActivity("Check Failed To Update", ECPOS_FOLDER);
													jsonResult.put(Constant.RESPONSE_CODE, "01");
													jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Failed To Update");
													break loop;
												}
											} else {
												proceed = false;
												connection.rollback();
												Logger.writeActivity("Check Detail Failed To Update", ECPOS_FOLDER);
												jsonResult.put(Constant.RESPONSE_CODE, "01");
												jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Failed To Update");
												break loop;
											}
										}
										
										if (noSub) {
											proceed = true;
										}
									} else {
										proceed = false;
										connection.rollback();
										Logger.writeActivity("Check Failed To Update", ECPOS_FOLDER);
										jsonResult.put(Constant.RESPONSE_CODE, "01");
										jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Failed To Update");
										break loop;
									}
								} else {
									proceed = false;
									connection.rollback();
									Logger.writeActivity("Check Detail Failed To Update", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Failed To Update");
									break loop;
								}
							} else {
								proceed = false;
								connection.rollback();
								Logger.writeActivity("Menu Item Not Found", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "01");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Menu Item Not Found");
								break loop;
							}
						} else {
							proceed = false;
							connection.rollback();
							Logger.writeActivity("Check Detail Not Found", ECPOS_FOLDER);
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Not Found");
							break loop;
						}
					}
					
					if (proceed) {
						Logger.writeActivity("Check Detail Successfully Updated", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Successfully Updated");
					}
				} else {
					connection.rollback();
					Logger.writeActivity("Request Not Complete", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Request Not Complete");
				}
				connection.setAutoCommit(true);
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
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (rs4 != null) {rs4.close();rs4 = null;}
				if (rs5 != null) {rs5.close();rs5 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("----------- CANCEL ITEM END ---------", ECPOS_FOLDER);
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/cancel_check" }, method = { RequestMethod.POST }, produces = "application/json")
	public String cancelCheck(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		Logger.writeActivity("----------- CANCEL CHECK START ---------", ECPOS_FOLDER);
		Logger.writeActivity("request: " + data, ECPOS_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		ResultSet rs = null;
		ResultSet rs3 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				connection.setAutoCommit(false);
				
				JSONObject jsonData = new JSONObject(data);
				
				if (jsonData.has("checkNo") && !jsonData.isNull("checkNo") && !jsonData.getString("checkNo").isEmpty()) {
					String checkNo = jsonData.getString("checkNo");
						
					stmt = connection.prepareStatement("select * from `check` where check_number = ?;");
					stmt.setString(1, checkNo);
					rs = stmt.executeQuery();
					
					if (rs.next()) {
						long checkId = rs.getLong("id");
						
						stmt2 = connection.prepareStatement("update `check` set check_status = 4, updated_date = now() where id = ? and check_number = ?;");
						stmt2.setLong(1, checkId);
						stmt2.setString(2, checkNo);
						int rs2 = stmt2.executeUpdate();
						
						if (rs2 > 0) {
							stmt3 = connection.prepareStatement("select * from check_detail where check_id = ? and check_number = ?");
							stmt3.setLong(1, checkId);
							stmt3.setString(2, checkNo);
							rs3 = stmt3.executeQuery();
							
							if (rs3.next()) {
								stmt4 = connection.prepareStatement("update check_detail set check_detail_status = 4, updated_date = now() where check_id = ? and check_number = ?;");
								stmt4.setLong(1, checkId);
								stmt4.setString(2, checkNo);
								int rs4 = stmt4.executeUpdate();
								
								if (rs4 > 0) {
									connection.commit();
									Logger.writeActivity("Check Detail Successfully Updated", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "00");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Successfully Updated");
								} else {
									connection.rollback();
									Logger.writeActivity("Check Detail Failed To Update", ECPOS_FOLDER);
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Failed To Update");
								}
							} else {
								connection.commit();
								Logger.writeActivity("Check Successfully Updated", ECPOS_FOLDER);
								jsonResult.put(Constant.RESPONSE_CODE, "00");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Detail Successfully Updated");
							}
						} else {
							connection.rollback();
							Logger.writeActivity("Check Failed To Update", ECPOS_FOLDER);
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Failed To Update");
						}
					} else {
						connection.rollback();
						Logger.writeActivity("Check Not Found", ECPOS_FOLDER);
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Not Found");
					}
				} else {
					connection.rollback();
					Logger.writeActivity("Request Not Complete", ECPOS_FOLDER);
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Request Not Complete");
				}
				connection.setAutoCommit(true);
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
				if (rs != null) {rs.close();rs = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("----------- CANCEL CHECK END ---------", ECPOS_FOLDER);
		return jsonResult.toString();
	}
	
	public long insertChildCheckDetail(Connection connection, int deviceType, long checkId, String checkNo, long parentCheckDetailId, JSONObject childItem, int orderQuantity, boolean isItemTaxable, JSONObject charges) {
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		long checkDetailId = 0;
		
		try {
			stmt = connection.prepareStatement("select * from menu_item where id = ? and backend_id = ?;");
			stmt.setLong(1, childItem.getLong("id"));
			stmt.setString(2, childItem.getString("backendId"));
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
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return checkDetailId;
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
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (rs3 != null) {rs3.close();rs3 = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public boolean updateCancelledItemCheck(Connection connection, long checkId, String checkNo, int orderQuantity, BigDecimal amount, boolean isItemTaxable, JSONObject charges) {
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
				
				BigDecimal newTotalAmount = totalAmount.subtract(amount);
				
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

							if (rs2.next()) {
								stmt3 = connection.prepareStatement("update check_tax_charge set total_charge_amount = ?,total_charge_amount_rounding_adjustment = ?,grand_total_charge_amount = ? where check_id = ? and check_number = ? and tax_charge_id = ?;");
								stmt3.setBigDecimal(1, totalChargeAmount);
								stmt3.setBigDecimal(2, totalChargeAmountRoundingAdjustment);
								stmt3.setBigDecimal(3, grandTotalChargeAmount);
								stmt3.setLong(4, checkId);
								stmt3.setString(5, checkNo);
								stmt3.setLong(6, totalTax.getLong("id"));
								int updateCharge = stmt3.executeUpdate();
							
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
						
								if (rs3.next()) {
									stmt5 = connection.prepareStatement("update check_tax_charge set total_charge_amount = ?,total_charge_amount_rounding_adjustment = ?,grand_total_charge_amount = ? where check_id = ? and check_number = ? and tax_charge_id = ?;");
									stmt5.setBigDecimal(1, totalChargeAmount);
									stmt5.setBigDecimal(2, totalChargeAmountRoundingAdjustment);
									stmt5.setBigDecimal(3, grandTotalChargeAmount);
									stmt5.setLong(4, checkId);
									stmt5.setString(5, checkNo);
									stmt5.setLong(6, overallTax.getLong("id"));
									int updateCharge = stmt5.executeUpdate();
							
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
					}
				} else {
					proceedUpdateCheck = true;
				}
	
				if (proceedUpdateCheck) {
					BigDecimal newGrandTotalAmount = roundToNearest(newTotalAmountWithTax);
					BigDecimal newTotalAmountWithTaxRoundingAdjustment = newGrandTotalAmount.subtract(newTotalAmountWithTax);
					BigDecimal newOverdueAmount = newGrandTotalAmount.subtract(tenderAmount);
					
					stmt6 = connection.prepareStatement("update `check` set total_item_quantity = ?,total_amount = ?,total_amount_with_tax = ?,total_amount_with_tax_rounding_adjustment = ?,grand_total_amount = ?,overdue_amount = ?,check_status = 2,updated_date = now() where id = ? and check_number = ?;");
					stmt6.setInt(1, totalQuantity - orderQuantity);
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
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (stmt3 != null) stmt3.close();
				if (stmt4 != null) stmt4.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public BigDecimal roundToNearest(BigDecimal value) {
		double d = value.doubleValue();
		double rounded = Math.round(d * 20.0) / 20.0;
		
		return BigDecimal.valueOf(rounded);
	}
}
