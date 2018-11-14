package mpay.my.ecpos_manager_v2.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
/*import org.springframework.web.client.RestTemplate;*/

import mpay.my.ecpos_manager_v2.constant.Constant;
import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;

@PropertySource(ignoreResourceNotFound = true, value = "classpath:otherprops.properties")
@RestController
@RequestMapping("/memberapi/show_items")
public class show_items_RestController {

	@Value("${SYSTEM_IMAGE_LOCAL_PATH}")
	String SYSTEM_IMAGE_LOCAL_PATH;

	@Value("${SYSTEM_IMAGE_FILE_PATH}")
	String SYSTEM_IMAGE_FILE_PATH;

	@Value("${ECPOS_BASE_URL}")
	String ECPOS_BASE_URL;

	@Autowired
	DataSource dataSource;
	
	private static String ECPOS_ACT_FILENAME = Property.getECPOS_ACT_FILENAME();
	private static String ECPOS_ERR_FILENAME = Property.getECPOS_ERR_FILENAME();
	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();

/*	@Autowired
	private RestTemplate restTemplate;*/

/*	@Autowired
	private LicenseService licenseService;*/

	// private String apiAuthorizationToken =
	// licenseService.retrieveStoreAuthToken();

	//Success
	@RequestMapping(value = { "/manager_create_menugroup" }, method = {
			RequestMethod.POST }, produces = "application/json")
	@ResponseBody
	public String createMenuGroup(HttpServletRequest request, @RequestBody String data) {

		JSONObject jsonObj = null;
		JSONObject jsonResult = null;
		Connection connection = null;

		try {
			jsonResult = new JSONObject();
			jsonObj = new JSONObject(data);
			if (jsonObj.has(Constant.STAFF_NAME) && jsonObj.has(Constant.GROUP_NAME)) {
				String staff_name = jsonObj.getString(Constant.STAFF_NAME);
				String group_name = jsonObj.getString(Constant.GROUP_NAME);

				connection = dataSource.getConnection();
				PreparedStatement stmt = connection.prepareStatement("SELECT id FROM empldef WHERE username = ?;");
				stmt.setString(1, staff_name);
				ResultSet resultSet = (ResultSet) stmt.executeQuery();

				if (resultSet.next()) {

					stmt = connection.prepareStatement("INSERT INTO itemgroup " + "(groupname) VALUE (?)");
					stmt.setString(1, group_name);
					stmt.executeUpdate();

					jsonResult.put(Constant.RESPONSE_CODE, "00");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
					Logger.writeActivity("Create Item Group "+ jsonObj.getString(Constant.GROUP_NAME) + " Success.", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "STAFF NOT FOUND");
					Logger.writeActivity("Staff Not Found While Creating Item Group", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
				}
			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID REQUEST");
				Logger.writeActivity("Invalid Request While Creating Item Group", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
					Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
				}
			}
		}
		return jsonResult.toString();

	}

	//Success
	@RequestMapping(value = { "/update_menugroup" }, method = { RequestMethod.POST }, produces = "application/json")
	@ResponseBody
	public String updateMenuGroup(HttpServletRequest request, @RequestBody String data) {

		JSONObject jsonObj = null;
		JSONObject jsonResult = null;
		Connection connection = null;

		try {
			jsonResult = new JSONObject();
			jsonObj = new JSONObject(data);

			System.out.println(data);

			if (jsonObj.has("group_id") && jsonObj.has("group_name")) {
				String group_id = jsonObj.getString("group_id");
				String group_name = jsonObj.getString("group_name");

				connection = dataSource.getConnection();

				PreparedStatement stmt = connection.prepareStatement("UPDATE itemgroup SET groupname = ? WHERE id = ?");
				stmt.setString(1, group_name);
				stmt.setString(2, group_id);
				stmt.executeUpdate();
				connection.close();

				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
				Logger.writeActivity("Update Item Group "+ group_name + " Success.", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID REQUEST");
				Logger.writeActivity("Invalid Request While Updating Item Group "+ jsonObj.getString("group_name"), ECPOS_ACT_FILENAME, ECPOS_FOLDER);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
					Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
				}
			}
		}
		return jsonResult.toString();
	}

	//Success
	@PostMapping("/remove_menugroup")
	public String removeMenuGroup(HttpServletRequest request, @RequestBody String groupid) {

		JSONObject jsonResult = null;
		Connection connection = null;

		try {
			jsonResult = new JSONObject();

			connection = dataSource.getConnection();

			// PreparedStatement stmt = connection.prepareStatement("DELETE from menudef
			// where itemgroup_id=(SELECT grouptype from itemgroup where id=?)");
			PreparedStatement stmt = connection
					.prepareStatement("UPDATE menudef SET itemstatus = 1 WHERE itemgroup_id = ?");
			stmt.setString(1, groupid);
			stmt.executeUpdate();

			stmt = connection.prepareStatement("DELETE from itemgroup where id=?");
			stmt.setString(1, groupid);
			stmt.executeUpdate();
			connection.close();

			jsonResult.put(Constant.RESPONSE_CODE, "00");
			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
			Logger.writeActivity("Remove Item Group Success", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
					Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
				}
			}
		}
		return jsonResult.toString();
	}

	
	@GetMapping("/manager_get_group_items/{groupId}")
	public String getGroupItem(HttpServletRequest request, @PathVariable("groupId") String groupId) {

		System.out.println("Item Group Id: " + groupId);

		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		JSONArray item_list = null;

		try {
			connection = dataSource.getConnection();

			PreparedStatement stmt = connection
					.prepareStatement("SELECT * FROM menudef WHERE itemgroup_id = ?  ORDER BY name");
			stmt.setString(1, groupId);
			ResultSet resultSet = (ResultSet) stmt.executeQuery();

			item_list = new JSONArray();

			while (resultSet.next()) {
				JSONObject item = new JSONObject();

				String item_id = resultSet.getString("id");
				String item_code = resultSet.getString("itemcode");
				String name = resultSet.getString("name");
				String item_price = resultSet.getString("price");
				String itemgroup_id = resultSet.getString("itemgroup_id");
				String gstgroup_id = resultSet.getString("gstgroup_id");
				String item_type = resultSet.getString("itemtype");
				String sst_status = resultSet.getString("sststatus");
				String image_path = "";

				String image_path_holder = resultSet.getString("image_path");
				if (image_path_holder != null) {
					image_path = image_path_holder;
				} else {
					image_path = "";
				}

				String html_button_ctrls =

						"<td><button class=\"btn btn-default btn-sm\" onclick=\"update_selected_item(" + item_id + ","
								+ "'" + name + "'" + "," + item_price + "," + "'" + item_code + "'" + "," + gstgroup_id
								+ "," + item_type + "," + sst_status + "," + "'" + image_path
								+ "')\"><i class=\"fa fa-pencil\" aria-hidden=\"true\"></i></button>"
								+ "<button class=\"btn btn-danger btn-sm\" onclick=\"removed_selected_item(" + item_id
								+ "," + itemgroup_id
								+ ")\"><i class=\"fa fa-trash\" aria-hidden=\"true\"></i></button></td>";

				item.put("item_id", item_id);
				item.put("item_code", item_code);
				item.put("name", name);
				item.put("item_price", item_price);
				item.put("itemgroup_id", itemgroup_id);
				item.put("gstgroup_id", gstgroup_id);
				item.put("item_type", item_type);
				item.put("sst_status", sst_status);
				item.put("image_path", image_path);
				item.put("edit_remove_group_item_btns", html_button_ctrls);
				item_list.put(item);
			}

			jsonResult.put(Constant.RESPONSE_CODE, "00");
			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");

			jsonResult.put("item_list", item_list);

		} catch (Exception ex) {
			System.out.println("ERROR at : " + ex);
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		System.out.println("After Group Founded: " + jsonResult.toString());

		return jsonResult.toString();
	}

	//Success
	@RequestMapping(value = { "/manager_create_menuitem" }, method = {
			RequestMethod.POST }, produces = "application/json")
	@ResponseBody
	public String createMenuItem(HttpServletRequest request, @RequestBody String data) {

		JSONObject jsonObj = null;
		JSONObject jsonResult = null;
		Connection connection = null;

		String imagePath = null;

		try {
			jsonResult = new JSONObject();
			jsonObj = new JSONObject(data);

			System.out.println("Incoming new item: " + data);

			if (jsonObj.has(Constant.STAFF_NAME) && jsonObj.has(Constant.ITEM_NAME) && jsonObj.has(Constant.ITEM_PRICE)
					&& jsonObj.has(Constant.GROUP_NAME) && jsonObj.has(Constant.SST_GROUP)
					&& jsonObj.has(Constant.ITEM_TYPE)) {

				String staff_name = jsonObj.getString(Constant.STAFF_NAME);
				String item_name = jsonObj.getString(Constant.ITEM_NAME);
				String item_code = jsonObj.getString(Constant.ITEM_CODE);
				String item_price = jsonObj.getString(Constant.ITEM_PRICE);
				String group_name = jsonObj.getString(Constant.GROUP_NAME);
				String gst_group = jsonObj.getString(Constant.GST_GROUP);
				String item_type = jsonObj.getString(Constant.ITEM_TYPE);
				String sst_group = jsonObj.getString(Constant.SST_GROUP);
				String uploaded_img = jsonObj.getString("upload_img_result");
				connection = dataSource.getConnection();

				PreparedStatement stmt = connection.prepareStatement("SELECT id FROM empldef WHERE username = ?;");
				stmt.setString(1, staff_name);
				ResultSet resultSet = (ResultSet) stmt.executeQuery();
				if (resultSet.next()) {

					stmt = connection.prepareStatement("SELECT id FROM itemgroup WHERE groupname = ?");
					stmt.setString(1, group_name);
					resultSet = (ResultSet) stmt.executeQuery();

					String itemgroup_id = "";
					while (resultSet.next()) {
						itemgroup_id = resultSet.getString("id");
					}

					if (!itemgroup_id.equals("")) {

						if (!gst_group.equals("")) {
							int duplication_code_status = 0;

							stmt = connection.prepareStatement("SELECT itemcode FROM menudef WHERE itemgroup_id = ?");
							stmt.setString(1, itemgroup_id);
							resultSet = (ResultSet) stmt.executeQuery();

							while (resultSet.next()) {
								if (resultSet.getString("itemcode").equals(item_code)) {
									duplication_code_status = 1;
								}
							}

							if (duplication_code_status == 1) {
								jsonResult.put(Constant.RESPONSE_CODE, "02");
								jsonResult.put(Constant.RESPONSE_MESSAGE, "DUPLICATED ITEM CODE DETECTED");
								Logger.writeActivity("DUPLICATED ITEM CODE DETECTED", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
								duplication_code_status = 0;
							} else {

								stmt = connection.prepareStatement("INSERT INTO menudef "
										+ "(itemcode, name, price, itemgroup_id, gstgroup_id, itemtype, sststatus) "
										+ "VALUE (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
								stmt.setString(1, item_code);
								stmt.setString(2, item_name);
								stmt.setString(3, item_price);
								stmt.setString(4, itemgroup_id);
								stmt.setString(5, gst_group);
								stmt.setString(6, item_type);
								stmt.setString(7, sst_group);
								stmt.executeUpdate();

								int item_id;
								ResultSet generatedKeys = (ResultSet) stmt.getGeneratedKeys();
								if (generatedKeys.next()) {
									if (!uploaded_img.equals("")) {
										item_id = generatedKeys.getInt(1);

										imagePath = this.getPhotoPath(uploaded_img, item_code);

										stmt = connection
												.prepareStatement("UPDATE menudef SET image_path = ? WHERE id = ?");
										stmt.setString(1, imagePath);
										stmt.setInt(2, item_id);
										stmt.executeUpdate();
									}
									jsonResult.put(Constant.RESPONSE_CODE, "00");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
									Logger.writeActivity("Menu Item " + item_name + " Successfully Created.", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
								} else {
									jsonResult.put(Constant.RESPONSE_CODE, "01");
									jsonResult.put(Constant.RESPONSE_MESSAGE, "ITEM CREATE FAILED");
									Logger.writeActivity("Menu Item Cannot Created", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
								}

							}
						} else {
							jsonResult.put(Constant.RESPONSE_CODE, "01");
							jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID GST GROUP");
							Logger.writeActivity("Invalid Group Name", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
						}
					} else {
						jsonResult.put(Constant.RESPONSE_CODE, "01");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID GROUP NAME");
						Logger.writeActivity("Invalid Group Name", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
					}
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "STAFF NOT FOUND");
					Logger.writeActivity("Staff Not Found", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
				}
			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID REQUEST");
				Logger.writeActivity("Invalid Request While Creating Menu Item", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
					Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
				}
			}
		}
		System.out.println("After item created: " + jsonResult.toString());
		Logger.writeActivity("After item created: "+jsonResult.toString(), ECPOS_ACT_FILENAME, ECPOS_FOLDER);
		return jsonResult.toString();
	}

	//Success
	@PostMapping("/manager_update_menuitem")
	public String updateMenuItem(HttpServletRequest request, @RequestBody String data) {

		JSONObject jsonObj = null;
		JSONObject jsonResult = null;
		Connection connection = null;

		try {
			jsonResult = new JSONObject();
			jsonObj = new JSONObject(data);

			String imagePath = null;
			String itemCode = null;

			if (jsonObj.has("item_id") && jsonObj.has("item_name") && jsonObj.has("item_price")) {
				String itemId = jsonObj.getString("item_id");
				itemCode = jsonObj.getString(Constant.ITEM_CODE);
				String itemName = jsonObj.getString(Constant.ITEM_NAME);
				String itemPrice = jsonObj.getString(Constant.ITEM_PRICE);
				String gst_group = "1";
				String itemType = jsonObj.getString(Constant.ITEM_TYPE);
				String sstGroup = jsonObj.getString(Constant.SST_GROUP);
				String uploadedImage = jsonObj.getString("upload_img_result");

				connection = dataSource.getConnection();

				PreparedStatement stmt = connection.prepareStatement("SELECT itemcode FROM menudef WHERE id = ?");
				stmt.setString(1, itemId);
				ResultSet resultSet = (ResultSet) stmt.executeQuery();

				resultSet.next();
				
				//itemCode = resultSet.getString("itemcode");
				if (!uploadedImage.equals("")) {

					imagePath = this.getPhotoPath(uploadedImage, itemCode);

					stmt = connection.prepareStatement("UPDATE menudef SET name = ?, itemcode =? ,price = ?,"
							+ "gstgroup_id = ?, image_path =?, itemtype = ?, sststatus = ? " + "WHERE id = ?");
					stmt.setString(1, itemName);
					stmt.setString(2, itemCode);
					stmt.setString(3, itemPrice);
					stmt.setString(4, gst_group);
					stmt.setString(5, imagePath);
					stmt.setString(6, itemType);
					stmt.setString(7, sstGroup);
					stmt.setString(8, itemId);
				} else { // if no image being upload
					stmt = connection.prepareStatement("UPDATE menudef SET name = ?, itemcode =?, price = ?,"
							+ "gstgroup_id = ?, itemtype = ?, sststatus = ? " + "WHERE id = ?");
					stmt.setString(1, itemName);
					stmt.setString(2, itemCode);
					stmt.setString(3, itemPrice);
					stmt.setString(4, gst_group);
					stmt.setString(5, itemType);
					stmt.setString(6, sstGroup);
					stmt.setString(7, itemId);
				}

				stmt.executeUpdate();
				connection.close();

				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
				Logger.writeActivity("Update Menu Item "+itemName, ECPOS_ACT_FILENAME, ECPOS_FOLDER);
			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID REQUEST");
				Logger.writeActivity("Invalid Request While Updating Menu Item", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
					Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
				}
			}
		}
		return jsonResult.toString();
	}

	//Success
	@PostMapping("/manager_remove_menuitem")
	public String removeMenuItem(HttpServletRequest request, @RequestBody String itemid) {

		JSONObject jsonResult = null;
		Connection connection = null;

		try {
			jsonResult = new JSONObject();

			connection = dataSource.getConnection();
			PreparedStatement stmt = connection.prepareStatement("UPDATE menudef SET itemstatus =1 where id=?");
			stmt.setString(1, itemid);
			stmt.executeUpdate();
			connection.close();

			jsonResult.put(Constant.RESPONSE_CODE, "00");
			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
			Logger.writeActivity("Menu Item Successfully Removed/Hide", ECPOS_ACT_FILENAME, ECPOS_FOLDER);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
					Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
				}
			}
		}
		return jsonResult.toString();
	}

	//Extra Useless Function
	@RequestMapping(value = { "/check_duplicate_item_group_name/{group_name}" }, method = {
			RequestMethod.GET }, produces = "application/json")
	@ResponseBody
	public String checkDuplicateItemGroup(@PathVariable("group_name") String group_name, HttpServletRequest request) {

		JSONObject jsonResult = null;
		Connection connection = null;
		String groupname_holder = null;

		try {
			jsonResult = new JSONObject();

			connection = dataSource.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT groupname FROM itemgroup WHERE groupname = ?");
			stmt.setString(1, group_name);

			ResultSet rs = stmt.executeQuery();

			rs.next();
			groupname_holder = rs.getString("groupname");

			if (groupname_holder.equals(group_name)) {
				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "DUPLICATE GROUP DETECTED");
			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "NEW ITEM GROUP");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Constant.EXCEPTION_MESSAGE;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return jsonResult.toString();

	}

	/*
	 * @RequestMapping(value = { "/manager_sync_data" }, method = RequestMethod.GET,
	 * produces = "application/json")
	 * 
	 * @ResponseBody public String syncData(HttpServletRequest request) {
	 * 
	 * JSONObject jsonObj = null; JSONObject jsonResult = new JSONObject();
	 * Connection connection = null;
	 * 
	 * if(apiAuthorizationToken!=null) {
	 * 
	 * try {
	 * 
	 * HttpHeaders headers = new HttpHeaders(); headers.add("Authorization",
	 * apiAuthorizationToken);
	 * 
	 * ResponseEntity<CatalogItemGroupResponse[]> catalogItemGroupResponseEntity=
	 * restTemplate.exchange(ECPOS_BASE_URL+"/items/catalogitemgroup",
	 * HttpMethod.GET,new
	 * HttpEntity<String>(headers),CatalogItemGroupResponse[].class);
	 * CatalogItemGroupResponse[] catalogItemGroupResponse =
	 * catalogItemGroupResponseEntity.getBody();
	 * 
	 * //Batch Insertion into item group for(CatalogItemGroupResponse
	 * group:catalogItemGroupResponse) { PreparedStatement stmt = connection.
	 * prepareStatement("INSERT INTO itemgroup (id, groupname) VALUES (?,?)");
	 * stmt.setLong(1,group.getId()); stmt.setString(2, group.getName());
	 * stmt.executeUpdate(); }
	 * 
	 * ResponseEntity<CatalogItemResponse[]> catalogItemResponseEntity=
	 * restTemplate.exchange(ECPOS_BASE_URL+"/items/catalogitemgroup",
	 * HttpMethod.GET,new HttpEntity<String>(headers),CatalogItemResponse[].class);
	 * CatalogItemResponse[] catalogItemResponse =
	 * catalogItemResponseEntity.getBody();
	 * 
	 * //Batch Insertion into menudef for(CatalogItemResponse
	 * item:catalogItemResponse) { PreparedStatement stmt = connection.
	 * prepareStatement("INSERT INTO menudef (id, groupname) VALUES (?,?)");
	 * stmt.setLong(1,item.getId());
	 * 
	 * stmt.executeUpdate(); }
	 * 
	 * 
	 * } catch(Exception ex) { ex.printStackTrace(); }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * }else {
	 * 
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * return null; }
	 */

	//Extra, Useless Function
	@RequestMapping(value = { "/check_duplicate_item_code/{item_code}" }, method = {
			RequestMethod.GET }, produces = "application/json")
	@ResponseBody
	public String checkDuplicateItemCode(@PathVariable("item_code") String item_code, HttpServletRequest request) {

		JSONObject jsonResult = null;
		Connection connection = null;
		String itemcode_holder = null;

		try {
			jsonResult = new JSONObject();

			connection = dataSource.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT itemcode FROM menudef WHERE itemcode = ?");
			stmt.setString(1, item_code);

			ResultSet rs = stmt.executeQuery();

			rs.next();
			itemcode_holder = rs.getString("itemcode");

			if (itemcode_holder.equals(item_code)) {
				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "DUPLICATE DETECTED");
			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "NEW ITEM CODE");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Constant.EXCEPTION_MESSAGE;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return jsonResult.toString();
	}

	// Support Function
	private String getPhotoPath(String base64_MIME, String item_code) {

		// **MIME/TYPE**
		String MIME_NAME = base64_MIME.split(",")[0].toLowerCase();
		String MIME_TYPE = "";

		if (MIME_NAME.contains("jpg")) {
			MIME_TYPE = ".jpg";
		} else if (MIME_NAME.contains("jpeg")) {
			MIME_TYPE = ".jpeg";
		} else if (MIME_NAME.contains("png")) {
			MIME_TYPE = ".png";
		} else if (MIME_NAME.contains("pdf")) {
			MIME_TYPE = ".pdf";
		} else if (MIME_NAME.contains("gif")) {
			MIME_TYPE = ".gif";
		}

		String filename = "ITM_" + item_code + MIME_TYPE;
		String filepath = SYSTEM_IMAGE_LOCAL_PATH + "/menu";

		String result = "/menu/" + filename;

		File f = new File(filepath);

		if (!f.exists()) {
			f.mkdirs();
		}

		try {
			byte[] image = Base64.decodeBase64(base64_MIME.split(",")[1]);

			try {
				FileOutputStream fileOuputStream = new FileOutputStream(filepath + "/" + filename);
				fileOuputStream.write(image);
				fileOuputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
			return result;
		}
		return result;
	}

}
