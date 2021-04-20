package mpay.ecpos_manager.web.restcontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/rc/menu")
public class RestC_menu {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@Value("${menu_image_path}")
	private String menuImagePath;
	
	@RequestMapping(value = { "/get_categories" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getCategories(HttpServletRequest request, HttpServletResponse response) {
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
	
				stmt = connection.prepareStatement("select * from category where is_active = 1 order by category_sequence asc;");
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					JSONObject category = new JSONObject();
					category.put("id", rs.getString("id"));
					category.put("name", rs.getString("category_name"));
					if(menuImagePath.length() == 12) {
						category.put("imagePath", menuImagePath + rs.getString("category_image_path"));
					}else {
						category.put("imagePath", menuImagePath.substring(55) + rs.getString("category_image_path"));
					}
					
					jary.put(category);
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
	
	@RequestMapping(value = { "/get_menu_items/{categoryId}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getMenuItems(@PathVariable("categoryId") long categoryId, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
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
					menuItems.put("alternativeName", rs.getString("menu_item_alt_name"));
					menuItems.put("barcode", rs.getString("menu_item_barcode"));
					menuItems.put("type", rs.getString("menu_item_type"));
					menuItems.put("description", rs.getString("menu_item_description"));
					menuItems.put("price", String.format("%.2f", rs.getBigDecimal("menu_item_base_price")));
					menuItems.put("taxable", rs.getBoolean("is_taxable"));
					menuItems.put("discountable", rs.getBoolean("is_discountable"));
//					menuItems.put("imagePath", menuImagePath + rs.getString("menu_item_image_path"));
					if(menuImagePath.length() == 12) {
						menuItems.put("imagePath", menuImagePath + rs.getString("menu_item_image_path"));
					}else {
						menuItems.put("imagePath", menuImagePath.substring(55) + rs.getString("menu_item_image_path"));
					}
					
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
	public String getTiers(@PathVariable("menuItemId") long menuItemId, @PathVariable("menuItemCode") String menuItemCode, HttpServletRequest request, HttpServletResponse response) {
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
					tiers.put("sequence", rs.getString("combo_detail_sequence"));	
					
					jary.put(tiers);
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

	@RequestMapping(value = { "/get_tier_item_details/{tierId}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getComboDetails(@PathVariable("tierId") long tierId, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
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
						menuItems.put("alternativeName", rs2.getString("menu_item_alt_name"));
						menuItems.put("barcode", rs2.getString("menu_item_barcode"));
						menuItems.put("description", rs2.getString("menu_item_description"));
						menuItems.put("price", String.format("%.2f", rs2.getBigDecimal("menu_item_base_price")));
						menuItems.put("taxable", rs2.getBoolean("is_taxable"));
						menuItems.put("discountable", rs2.getBoolean("is_discountable"));
//						menuItems.put("imagePath", menuImagePath + rs2.getString("menu_item_image_path"));
						if(menuImagePath.length() == 12) {
							menuItems.put("imagePath", menuImagePath + rs2.getString("menu_item_image_path"));
						}else {
							menuItems.put("imagePath", menuImagePath.substring(55) + rs2.getString("menu_item_image_path"));
						}
						
						menuItems.put("sequence", rs.getString("combo_item_detail_sequence"));
						
						jary.put(menuItems);
					}
					jsonResult.put("data", jary);
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
	public String getModifiers(@PathVariable("menuItemId") long menuItemId, @PathVariable("menuItemCode") String menuItemCode, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
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
						modifierDetails.put("alternativeName", rs2.getString("menu_item_alt_name"));
						modifierDetails.put("barcode", rs2.getString("menu_item_barcode"));
						modifierDetails.put("description", rs2.getString("menu_item_description"));
						modifierDetails.put("price", String.format("%.2f", rs2.getBigDecimal("menu_item_base_price")));
						modifierDetails.put("taxable", rs2.getBoolean("is_taxable"));
//						modifierDetails.put("imagePath", menuImagePath + rs2.getString("menu_item_image_path"));
						if(menuImagePath.length() == 12) {
							modifierDetails.put("imagePath", menuImagePath + rs2.getString("menu_item_image_path"));
						}else {
							modifierDetails.put("imagePath", menuImagePath.substring(55) + rs2.getString("menu_item_image_path"));
						}
						
						jary2.put(modifierDetails);
					}
					modifiers.put("data", jary2);
					jary.put(modifiers);
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
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_menu_items_by_item_type/{itemType}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getMenuItemsByItemType(@PathVariable("itemType") String itemType, HttpServletRequest request, HttpServletResponse response) {
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
				
				stmt = connection.prepareStatement("select * from menu_item where is_active = 1 and menu_item_type = ? order by id asc;");
				stmt.setString(1, itemType);
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					JSONObject menuItems = new JSONObject();
					menuItems.put("id", rs.getString("id"));
					menuItems.put("backendId", rs.getString("backend_id"));
					menuItems.put("name", rs.getString("menu_item_name"));
					menuItems.put("alternativeName", rs.getString("menu_item_alt_name"));
					menuItems.put("barcode", rs.getString("menu_item_barcode"));
					menuItems.put("description", rs.getString("menu_item_description"));
					menuItems.put("price", String.format("%.2f", rs.getBigDecimal("menu_item_base_price")));
					menuItems.put("taxable", rs.getBoolean("is_taxable"));
					menuItems.put("discountable", rs.getBoolean("is_discountable"));
//					menuItems.put("imagePath", menuImagePath + rs.getString("menu_item_image_path"));
					if(menuImagePath.length() == 12) {
						menuItems.put("imagePath", menuImagePath + rs.getString("menu_item_image_path"));
					}else {
						menuItems.put("imagePath", menuImagePath.substring(55) + rs.getString("menu_item_image_path"));
					}
					
					jary.put(menuItems);
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
	
	@RequestMapping(value = { "/get_menu_items/{itemId}/{itemBackendId}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getMenuItem(@PathVariable("itemId") String itemId, @PathVariable("itemBackendId") String itemBackendId, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				
				stmt = connection.prepareStatement("select * from menu_item where is_active = 1 and id = ? and backend_id = ? order by id asc;");
				stmt.setString(1, itemId);
				stmt.setString(2, itemBackendId);
				rs = stmt.executeQuery();
	
				if (rs.next()) {
					jsonResult.put("id", rs.getString("id"));
					jsonResult.put("backendId", rs.getString("backend_id"));
					jsonResult.put("name", rs.getString("menu_item_name"));
					jsonResult.put("alternativeName", rs.getString("menu_item_alt_name"));
					jsonResult.put("barcode", rs.getString("menu_item_barcode"));
					jsonResult.put("description", rs.getString("menu_item_description"));
					jsonResult.put("price", String.format("%.2f", rs.getBigDecimal("menu_item_base_price")));
					jsonResult.put("taxable", rs.getBoolean("is_taxable"));
					jsonResult.put("discountable", rs.getBoolean("is_discountable"));
//					jsonResult.put("imagePath", menuImagePath + rs.getString("menu_item_image_path"));
					if(menuImagePath.length() == 12) {
						jsonResult.put("imagePath", menuImagePath + rs.getString("menu_item_image_path"));
					}else {
						jsonResult.put("imagePath", menuImagePath.substring(55) + rs.getString("menu_item_image_path"));
					}
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
	
	@RequestMapping(value = { "/get_modifier_groups" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getModifierGroup(HttpServletRequest request, HttpServletResponse response) {
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
				
				stmt = connection.prepareStatement("select * from modifier_group where is_active = 1;");
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					JSONObject modifierGroup = new JSONObject();
					modifierGroup.put("id", rs.getString("id"));
					modifierGroup.put("name", rs.getString("modifier_group_name"));
					
					jary.put(modifierGroup);
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
	
	@RequestMapping(value = { "/get_modifier_items_list/{modifierGroupId}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getModifierItemList(@PathVariable("modifierGroupId") String modifierGroupId, HttpServletRequest request, HttpServletResponse response) {
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
				
				stmt = connection.prepareStatement("select mi.*, mis.modifier_item_sequence from modifier_group mg " + 
						"inner join modifier_item_sequence mis on mis.modifier_group_id = mg.id " + 
						"inner join menu_item mi on mi.id = mis.menu_item_id " + 
						"where mg.id = ? and mg.is_active = 1 order by mis.modifier_item_sequence asc;");
				stmt.setString(1, modifierGroupId);
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					JSONObject modifierItem = new JSONObject();
					modifierItem.put("id", rs.getString("id"));
					modifierItem.put("backendId", rs.getString("backend_id"));
					modifierItem.put("name", rs.getString("menu_item_name"));
					modifierItem.put("alternativeName", rs.getString("menu_item_alt_name"));
					modifierItem.put("barcode", rs.getString("menu_item_barcode"));
					modifierItem.put("description", rs.getString("menu_item_description"));
//					modifierItem.put("imagePath", menuImagePath + rs.getString("menu_item_image_path"));
					if(menuImagePath.length() == 12) {
						modifierItem.put("imagePath", menuImagePath + rs.getString("menu_item_image_path"));
					}else {
						modifierItem.put("imagePath", menuImagePath.substring(55) + rs.getString("menu_item_image_path"));
					}
					modifierItem.put("price", String.format("%.2f", rs.getBigDecimal("menu_item_base_price")));
					modifierItem.put("taxable", rs.getBoolean("is_taxable"));
					modifierItem.put("sequence", rs.getString("modifier_item_sequence"));
					
					jary.put(modifierItem);
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
	
	@RequestMapping(value = { "/get_modifier_groups_by_menu_item/{menuItemId}/{menuItemCode}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getModifierGroupsByMenuItem(@PathVariable("menuItemId") long menuItemId, @PathVariable("menuItemCode") String menuItemCode, HttpServletRequest request, HttpServletResponse response) {
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
	
				stmt = connection.prepareStatement("select mg.*, mimg.menu_item_modifier_group_sequence from menu_item mi " + 
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
					modifiers.put("sequence", rs.getString("menu_item_modifier_group_sequence"));
					jary.put(modifiers);
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
	
	@RequestMapping(value = { "/get_item_stock_list" }, method = { RequestMethod.POST }, headers = "Accept=application/json")
	public String getItemStockList(@RequestBody String dataObj, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				JSONObject jsonObj = new JSONObject(dataObj);
				
				connection = dataSource.getConnection();
				
				boolean isDailySalesUpdated = false;
				stmt = connection.prepareStatement("select count(*) as cnt from menu_item_stock_update "
						+ "where created_date > ? and created_date < DATE_ADD(?, INTERVAL 1 DAY)");
				stmt.setString(1, jsonObj.get("startDate").toString().substring(0, 10));
				stmt.setString(2, jsonObj.get("startDate").toString().substring(0, 10));
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					isDailySalesUpdated = rs.getInt("cnt") > 0;
					
					if (!isDailySalesUpdated) {
						int staffId = user.getUserLoginId();
						
						stmt1 = connection.prepareStatement("INSERT INTO menu_item_stock_update "
								+ "(new_value,menu_item_id,created_date) "
								+ "SELECT ?,id,? FROM menu_item;");
						stmt1.setString(1, "0");
						stmt1.setString(2, jsonObj.getString("startDate").substring(0, 10) + " " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
						
						stmt1.executeUpdate();
						
						if (stmt1!=null) {stmt1.close();}
						stmt1 = connection.prepareStatement("insert into menu_item_update_log "
								+ "(event,staff_id,event_date,created_date) "
								+ "values (?,?,?,now())");
						stmt1.setString(1, "("+jsonObj.getString("startDate").substring(0, 10)+") Initiate all items unit with 0");
						stmt1.setInt(2, staffId);
						stmt1.setString(3, jsonObj.getString("startDate").substring(0, 10) + " " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
						
						stmt1.executeUpdate();
					}
				}
				
				if(rs != null) {rs.close();}
				if(stmt != null) {stmt.close();}
				stmt= connection.prepareStatement("select b.id,b.menu_item_name,a.new_value,a.id as sale_id "
						+ "from menu_item_stock_update a left join menu_item b on a.menu_item_id = b.id "
						+ "where a.created_date > ? and a.created_date < DATE_ADD(?, INTERVAL 1 DAY) order by b.id;");
				stmt.setString(1, jsonObj.get("startDate").toString().substring(0, 10));
				stmt.setString(2, jsonObj.get("startDate").toString().substring(0, 10));
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					JSONObject item = new JSONObject();
					item.put("id", rs.getLong("id"));
					item.put("menu_item_name", rs.getString("menu_item_name"));
					item.put("new_value", rs.getString("new_value"));
					item.put("saleItem_id", rs.getString("sale_id"));
					
					jary.put(item);
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
				if (stmt1 != null) stmt1.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_menu_item_list" }, method = { RequestMethod.POST }, produces = "application/json")
	public String getMenuItemList(@RequestBody String dataObj, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				JSONArray arr = new JSONArray();
				JSONObject jsonObj = new JSONObject(dataObj);
				JSONObject obj = null;
				
				stmt = connection.prepareStatement("SELECT CASE WHEN "
				+ "(select count(*) from menu_item)=(select count(*) from menu_item_stock_update where created_date > ? and created_date < DATE_ADD(?, INTERVAL 1 DAY)) "
				+ "THEN 1 ELSE 0 END AS RowCountResult;");
				stmt.setString(1, jsonObj.get("startDate").toString().substring(0, 10));
				stmt.setString(2, jsonObj.get("startDate").toString().substring(0, 10));
				rs = stmt.executeQuery();
				
				boolean isRecordUpdated = true;
				while (rs.next()) {
					isRecordUpdated = rs.getInt("RowCountResult") == 1;
				}
				
				if (!isRecordUpdated) {
					stmt1 = connection.prepareStatement("select * from menu_item where is_active = 1 "
							+ "and id not in (select menu_item_id from menu_item_stock_update "
							+ "where created_date > ? and created_date < DATE_ADD(?, INTERVAL 1 DAY)) "
							+ "order by menu_item_name asc;");
					stmt1.setString(1, jsonObj.get("startDate").toString().substring(0, 10));
					stmt1.setString(2, jsonObj.get("startDate").toString().substring(0, 10));
					rs1 = stmt1.executeQuery();
		
					while (rs1.next()) {
						obj = new JSONObject();
						obj.put("id", rs1.getString("id"));
						obj.put("name", rs1.getString("menu_item_name"));
						obj.put("backend_id", rs1.getString("backend_id"));
						arr.put(obj);
					}
					jsonResult.put("menu_item_list", arr);
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
	
	@RequestMapping(value = { "/get_menu_item_detail" }, method = { RequestMethod.POST }, produces = "application/json")
	public String getMenuItem(@RequestBody String dataObj, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				JSONObject jsonObj = new JSONObject(dataObj);
				
				stmt = connection.prepareStatement("select a.id,b.menu_item_name,a.new_value "
						+ "from menu_item_stock_update a left join menu_item b on a.menu_item_id = b.id "
						+ "where a.id = ?;");
				stmt.setString(1, jsonObj.get("saleItem_id").toString());
				rs = stmt.executeQuery();
	
				if (rs.next()) {
					jsonResult.put("saleItem_id", rs.getString("id"));
					jsonResult.put("new_value", rs.getString("new_value"));
					jsonResult.put("name", rs.getString("menu_item_name"));
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
	
	@RequestMapping(value = { "/update_item_stock" }, method = { RequestMethod.POST }, produces = "application/json")
	public String updateItemStock(@RequestBody String jsonData, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				JSONObject jsonObj = new JSONObject(jsonData);
				int staffId = user.getUserLoginId();
				
				stmt = connection.prepareStatement("update menu_item_stock_update "
						+ "set new_value = ? where id = ? and created_date > ? and created_date < DATE_ADD(?, INTERVAL 1 DAY);");
				stmt.setInt(1, Integer.parseInt(jsonObj.getString("newValue")));
				stmt.setInt(2, Integer.parseInt(jsonObj.getString("saleItem_id")));
				stmt.setString(3, jsonObj.get("startDate").toString().substring(0, 10));
				stmt.setString(4, jsonObj.get("startDate").toString().substring(0, 10));
				int trxnUpdate = stmt.executeUpdate();
	
				if (trxnUpdate > 0) {
					stmt2 = connection.prepareStatement("select b.menu_item_name,a.created_date from menu_item_stock_update a left join menu_item b on a.menu_item_id = b.id "
							+ "where a.id = ?");
					stmt2.setString(1, jsonObj.getString("saleItem_id"));
					rs = stmt2.executeQuery();
					
					String itemName = null;
					String saleDate = null;
					while(rs.next()) {
						itemName = rs.getString("menu_item_name");
						saleDate = rs.getString("created_date");
					}
					
					stmt1 = connection.prepareStatement("insert into menu_item_update_log "
							+ "(event,staff_id,event_date,created_date) "
							+ "values (?,?,?,now())");
					stmt1.setString(1, "("+saleDate.substring(0, 10)+") Update item unit "+jsonObj.getString("newValue")+" for "+itemName);
					stmt1.setInt(2, staffId);
					stmt1.setString(3, saleDate.substring(0, 10) + " " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
					
					stmt1.executeUpdate();
					
					jsonResult.put(Constant.RESPONSE_CODE, "00");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
					System.out.println("Item Sales Update Success");
					Logger.writeActivity("Item Sales Update Success", ECPOS_FOLDER);
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Item Sales Update Failed");
					System.out.println("Item Sales Update Failed");
					Logger.writeActivity("Item Sales Update Failed", ECPOS_FOLDER);
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
				if (stmt1 != null) stmt1.close();
				if (stmt2 != null) stmt2.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/add_new_item" }, method = { RequestMethod.POST }, produces = "application/json")
	public String addNewItem(@RequestBody String jsonData, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				int staffId = user.getUserLoginId();
				JSONObject jsonObj = new JSONObject(jsonData);
				
				stmt = connection.prepareStatement("insert into menu_item_stock_update "
						+ "(new_value,menu_item_id,created_date) "
						+ "values (?,?,?);", Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(1, 0);
				stmt.setInt(2, Integer.parseInt(jsonObj.getString("id")));
				stmt.setString(3, jsonObj.getString("startDate").substring(0, 10) + " " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
				int trxnUpdate = stmt.executeUpdate();
				
				if (trxnUpdate > 0) {
					rs1 = stmt.getGeneratedKeys();
					if (rs1.next()) {
						long id = rs1.getLong(1);
						stmt2 = connection.prepareStatement("select b.menu_item_name,a.created_date from menu_item_stock_update a left join menu_item b on a.menu_item_id = b.id "
								+ "where a.id = ?");
						stmt2.setLong(1, id);
						rs = stmt2.executeQuery();
						
						String itemName = null;
						String saleDate = null;
						while(rs.next()) {
							itemName = rs.getString("menu_item_name");
							saleDate = rs.getString("created_date");
						}
						
						stmt1 = connection.prepareStatement("insert into menu_item_update_log "
								+ "(event,staff_id,event_date,created_date) "
								+ "values (?,?,?,now())");
						stmt1.setString(1, "("+saleDate.substring(0, 10)+") Add new item "+itemName);
						stmt1.setInt(2, staffId);
						stmt1.setString(3, saleDate.substring(0, 10) + " " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
						
						stmt1.executeUpdate();
						
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
						System.out.println("Add Menu Item Success");
						Logger.writeActivity("Add Menu Item Success", ECPOS_FOLDER);
					}
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Add Menu Item Failed");
					System.out.println("Add Menu Item Failed");
					Logger.writeActivity("Add Menu Item Failed", ECPOS_FOLDER);
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
				if (stmt1 != null) stmt1.close();
				if (stmt2 != null) stmt2.close();
				if (rs != null) {rs.close();rs = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_sales_update_log" }, method = { RequestMethod.POST }, produces = "application/json")
	public String getSalesUpdateLog(@RequestBody String jsonData, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				JSONObject jsonObj = new JSONObject(jsonData);
				
				stmt = connection.prepareStatement("select a.id,a.event,b.staff_name,a.event_date,a.created_date "
						+ "from menu_item_update_log a left join staff b on a.staff_id = b.id "
						+ "where a.created_date > ? and a.created_date < DATE_ADD(?, INTERVAL 1 DAY);");
				stmt.setString(1, jsonObj.getString("startDate").substring(0, 10));
				stmt.setString(2, jsonObj.getString("startDate").substring(0, 10));
				rs = stmt.executeQuery();
				
				JSONArray arr = new JSONArray();
				JSONObject obj = null;
				while (rs.next()) {
					
					obj = new JSONObject();
					obj.put("id", rs.getString("id"));
					obj.put("event", rs.getString("event"));
					obj.put("staff_name", rs.getString("staff_name"));
					obj.put("event_date", rs.getString("event_date"));
					obj.put("created_date", rs.getString("created_date"));
					arr.put(obj);
				}
				jsonResult.put("logs", arr);
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
	

	@RequestMapping(value = { "/get_menu_openitems/{categoryId}" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getMenuOpenItems(@PathVariable("categoryId") long categoryId, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jary = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				connection = dataSource.getConnection();
				
				stmt = connection.prepareStatement("select cmi.category_menu_item_sequence, mi.* from category c " + 
						"inner join category_menu_item cmi on cmi.category_id = c.id " + 
						"inner join menu_item mi on mi.id = cmi.menu_item_id " + 
						"where c.id = ? and c.is_active = 1 and mi.menu_item_type in (0, 1) and mi.is_active = 1 " + 
						"and mi.is_weighable = 1 " +
						"order by cmi.category_menu_item_sequence asc;");
				stmt.setLong(1, categoryId);
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					JSONObject menuItems = new JSONObject();
					menuItems.put("id", rs.getString("id"));
					menuItems.put("backendId", rs.getString("backend_id"));
					menuItems.put("name", rs.getString("menu_item_name"));
					menuItems.put("alternativeName", rs.getString("menu_item_alt_name"));
					menuItems.put("barcode", rs.getString("menu_item_barcode"));
					menuItems.put("type", rs.getString("menu_item_type"));
					menuItems.put("description", rs.getString("menu_item_description"));
					menuItems.put("price", String.format("%.2f", rs.getBigDecimal("menu_item_base_price")));
					menuItems.put("taxable", rs.getBoolean("is_taxable"));
					menuItems.put("discountable", rs.getBoolean("is_discountable"));
//					menuItems.put("imagePath", menuImagePath + rs.getString("menu_item_image_path"));
					if(menuImagePath.length() == 12) {
						menuItems.put("imagePath", menuImagePath + rs.getString("menu_item_image_path"));
					}else {
						menuItems.put("imagePath", menuImagePath.substring(55) + rs.getString("menu_item_image_path"));
					}
					
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
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/get_opencategories" }, method = { RequestMethod.GET }, produces = "application/json")
	public String getOpenCategories(HttpServletRequest request, HttpServletResponse response) {
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
	
				stmt = connection.prepareStatement("select c.* from category c "
						+ "inner join category_menu_item cmi on cmi.category_id = c.id "
						+ "inner join menu_item mi on mi.id = cmi.menu_item_id "
						+ "where c.is_active = 1 and mi.menu_item_type in (0, 1) and mi.is_active = 1 and mi.is_weighable = 1 "
						+ "group by c.id order by cmi.category_menu_item_sequence asc");
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					JSONObject category = new JSONObject();
					category.put("id", rs.getString("id"));
					category.put("name", rs.getString("category_name"));
					if(menuImagePath.length() == 12) {
						category.put("imagePath", menuImagePath + rs.getString("category_image_path"));
					}else {
						category.put("imagePath", menuImagePath.substring(55) + rs.getString("category_image_path"));
					}
					
					jary.put(category);
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
}
