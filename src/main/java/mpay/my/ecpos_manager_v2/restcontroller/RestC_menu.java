package mpay.my.ecpos_manager_v2.restcontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;

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
}
