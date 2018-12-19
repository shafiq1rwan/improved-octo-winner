package mpay.my.ecpos_manager_v2.restcontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@GetMapping("/getcheckdetail/{tableNo}/{checkNo}")
	public ResponseEntity<String> getCheckDetails(@PathVariable("tableNo") int tableNo, @PathVariable("checkNo") String checkNo) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		
		try {
			connection = dataSource.getConnection();
			
			stmt = connection.prepareStatement("select * from `check` c "
					+ "inner join check_status cs on c.check_status = cs.id "
					+ "where table_number = ? and check_number = ? and device_type = 2 and check_status in (1, 2);");
			stmt.setInt(1, tableNo);
			stmt.setString(2, checkNo);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				long id = rs.getLong("id");
				
				jsonResult.put("checkNo", rs.getString("check_number"));
				jsonResult.put("tableNo", rs.getString("table_number"));
				jsonResult.put("createdDate", rs.getString("created_date"));
				jsonResult.put("subtotal", rs.getString("subtotal_amount"));
				jsonResult.put("tax", rs.getString("total_tax_amount"));
				jsonResult.put("serviceCharge", rs.getString("total_service_charge_amount"));
				jsonResult.put("total", rs.getString("total_amount"));
				jsonResult.put("roundingAdjustment", rs.getString("total_amount_rounding_adjustment"));
				jsonResult.put("grandTotal", rs.getString("grand_total_amount"));
				jsonResult.put("status", rs.getString("name"));
				jsonResult.put("deposit", rs.getString("deposit_amount"));
				jsonResult.put("overdue", rs.getString("overdue_amount"));
				
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
					grandParentItem.put("itemId", rs2.getString("item_id"));
					grandParentItem.put("itemCode", rs2.getString("item_code"));
					grandParentItem.put("itemName", rs2.getString("item_name"));
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
						parentItem.put("itemId", rs3.getString("item_id"));
						parentItem.put("itemCode", rs3.getString("item_code"));
						parentItem.put("itemName", rs3.getString("item_name"));
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
							childItem.put("itemId", rs4.getString("item_id"));
							childItem.put("itemCode", rs4.getString("item_code"));
							childItem.put("itemName", rs4.getString("item_name"));
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
		System.out.println(jsonResult.toString());
		return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
	}
	
	@RequestMapping(value = { "/create" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String createCheck(@RequestBody String data, HttpServletRequest request) {
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
			
			if (jsonObj.has(Constant.TABLE_NO)) {
				String tableNo = jsonObj.getString(Constant.TABLE_NO);

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
							stmt = connection.prepareStatement("insert into `check` (check_number,device_type,staff_id,table_number,total_item_quantity,subtotal_amount,total_tax_amount,total_service_charge_amount,total_amount,total_amount_rounding_adjustment,grand_total_amount,deposit_amount,overdue_amount,check_status,created_date) " + 
									"values (?,2,?,?,0,0,0,0,0,0,0,0,0,1,now());");
							stmt.setString(1, Integer.toString(newCheckNo));
							stmt.setLong(2, staffId);
							stmt.setString(3, tableNo);
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
			} else {
				Logger.writeActivity("Table Number Not Found", ECPOS_FOLDER);
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Table Number Not Found");
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
}
