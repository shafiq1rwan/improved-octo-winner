package mpay.ecpos_manager.web.restcontrollerbk;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
/*import org.springframework.web.client.RestTemplate;*/

import mpay.ecpos_manager.general.constant.Constant;
import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.UserAuthenticationModel;
import mpay.ecpos_manager.general.utility.WebComponents;

@PropertySource(ignoreResourceNotFound = true, value = "classpath:otherprops.properties")
@RestController
@RequestMapping("/memberapi/show_sales")
public class show_sales_RestController {

//	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
//
//	@Autowired
//	DataSource dataSource;
//
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//
//	private DecimalFormat df2 = new DecimalFormat(".##");
//
//	// Success
//	@RequestMapping(value = { "/get_table_list" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
//	@ResponseBody
//	public String getTablelist() {
//		JSONObject jsonResult = new JSONObject();
//		Connection connection = null;
//
//		try {
//			connection = dataSource.getConnection();
//
//			PreparedStatement stmt = connection.prepareStatement("SELECT table_count FROM system;");
//			ResultSet rs = (ResultSet) stmt.executeQuery();
//
//			if (rs.next()) {
//				int table_count = rs.getInt("table_count");
//				
//				JSONArray tableList = new JSONArray();
//				for (int i = 0; i < table_count; i++) {
//
//					stmt = connection.prepareStatement("SELECT COUNT(*) AS count FROM checks WHERE tblno = ? AND chk_open IN (1,2)");
//					stmt.setInt(1, i + 1);
//					ResultSet rs2 = (ResultSet) stmt.executeQuery();
//					
//					if (rs2.next()) {
//						String data = Integer.toString(i + 1) + "," + rs2.getString("count");
//						tableList.put(data);
//					}
//				}
//				jsonResult.put(Constant.TABLE_LIST, tableList);
//				Logger.writeActivity("Table List: " + tableList.toString(), ECPOS_FOLDER);
//				
//				jsonResult.put(Constant.RESPONSE_CODE, "00");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//			} else {
//				jsonResult.put(Constant.RESPONSE_CODE, "01");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "NO TABLE FOUND, PLEASE TRY AGAIN");
//				Logger.writeActivity("Table List Not Found", ECPOS_FOLDER);
//			}
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					Logger.writeError(e, "SQLException: ", ECPOS_FOLDER);
//					e.printStackTrace();
//				}
//			}
//		}
//		return jsonResult.toString();
//	}
//
//	// Success
//	@RequestMapping(value = { "/get_check_list" }, method = { RequestMethod.POST }, produces = "application/json")
//	@ResponseBody
//	public String getChecklist(@RequestBody String data) {
//		JSONObject jsonResult = new JSONObject();
//		Connection connection = null;
//
//		try {
//			JSONObject jsonObj = new JSONObject(data);
//
//			if (jsonObj.has(Constant.TABLE_NO)) {
//				String table_no = jsonObj.getString(Constant.TABLE_NO);
//				
//				connection = dataSource.getConnection();
//
//				PreparedStatement stmt = connection.prepareStatement("SELECT chk_num FROM checks WHERE tblno = ? AND chk_open IN (1,2);");
//				stmt.setString(1, table_no);
//				ResultSet rs = (ResultSet) stmt.executeQuery();
//
//				JSONArray check_list = new JSONArray();
//				while (rs.next()) {
//					check_list.put(rs.getString("chk_num"));
//				}
//				jsonResult.put("check_list", check_list);
//				Logger.writeActivity("Table Check List: " + check_list.toString(), ECPOS_FOLDER);
//				
//				jsonResult.put(Constant.RESPONSE_CODE, "00");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//			} else {
//				jsonResult.put(Constant.RESPONSE_CODE, "01");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Request");
//				Logger.writeActivity("Invalid Request for Table Check List", ECPOS_FOLDER);
//			}
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					Logger.writeError(e, "SQLException: ", ECPOS_FOLDER);
//					e.printStackTrace();
//				}
//			}
//		}
//		return jsonResult.toString();
//	}
//	
//	// Support API
//	@RequestMapping(value = { "/get_staff_name" }, method = { RequestMethod.GET }, produces = "application/json")
//	@ResponseBody
//	public String getStaffName(HttpServletRequest request) {
//		JSONObject jsonResult = null;
//
//		UtilWebComponents webcomponent = new UtilWebComponents();
//		UserAuthenticationModel session_container_user = webcomponent.getEcposSession(request);
//
//		try {
//			jsonResult = new JSONObject();
//			
//			if (session_container_user != null) {
//				jsonResult.put(Constant.STAFF_NAME, session_container_user.getUsername());
//			} else {
//				jsonResult.put(Constant.RESPONSE_CODE, "01");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "STAFF NOT FOUND");
//			}
//		} catch (JSONException e) {
//			Logger.writeError(e, "JSONException: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		}
//
//		return jsonResult.toString();
//	}
//
//	// Unknown
//	@RequestMapping(value = { "/get_check_detail" }, method = { RequestMethod.POST }, produces = "application/json")
//	@ResponseBody
//	public String getCheckDetail(HttpServletRequest request, @RequestBody String data) {
//
//		JSONObject jsonObj = null;
//		JSONObject jsonResult = new JSONObject();
//		Connection connection = null;
//
//		try {
//			jsonObj = new JSONObject(data);
//			String check_no = jsonObj.getString(Constant.CHECK_NO);
//
//			if (jsonObj.has(Constant.CHECK_NO)) {
//
//				connection = dataSource.getConnection();
//
//				String staff_name = "";
//				PreparedStatement stmt = connection.prepareStatement("SELECT * FROM checks WHERE chk_num = ?;");
//				stmt.setString(1, check_no);
//				ResultSet rs = (ResultSet) stmt.executeQuery();
//
//				if (rs.next()) {
//
//					jsonResult.put(Constant.RESPONSE_CODE, "00");
//					jsonResult.put(Constant.RESPONSE_MESSAGE, "Success");
//
//					stmt = connection.prepareStatement("SELECT b.username, c.name, c.number as qty, "
//							+ " (c.chk_ttl*c.number) as ttl " + " FROM checks a JOIN empldef b ON a.empl_id = b.id "
//							+ " JOIN details c ON a.chk_seq = c.chk_seq " + " WHERE a.chk_num = ? GROUP BY c.name;");
//					stmt.setString(1, check_no);
//					ResultSet rs2 = (ResultSet) stmt.executeQuery();
//					JSONArray ordered_item = new JSONArray();
//					double total_amount = 0.00;
//
//					while (rs2.next()) {
//						JSONObject item = new JSONObject();
//
//						item.put(Constant.ITEM_NAME, rs2.getString("name"));
//						item.put(Constant.ITEM_QTY, rs2.getString("qty"));
//						item.put(Constant.TOTAL_PRICE, rs2.getString("ttl"));
//						total_amount = total_amount + rs2.getDouble("ttl");
//
//						if (staff_name.equals("")) {
//							staff_name = rs2.getString("username");
//						}
//
//						ordered_item.put(item);
//					}
//					jsonResult.put(Constant.CHECK_NO, check_no);
//					jsonResult.put(Constant.STAFF_NAME, staff_name);
//					jsonResult.put(Constant.TOTAL_PRICE, Double.toString(total_amount));
//					jsonResult.put(Constant.ORDERED_LIST, ordered_item);
//
//				} else {
//					jsonResult.put(Constant.RESPONSE_CODE, "01");
//					jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Check Number");
//				}
//			} else {
//				jsonResult.put(Constant.RESPONSE_CODE, "01");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Request");
//			}
//
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (Exception e) {
//					Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return jsonResult.toString();
//	}
//
//	// Success
//	@RequestMapping(value = { "/create_check" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
//	@ResponseBody
//	public String createCheck(@RequestBody String data) {
//		JSONObject jsonResult = new JSONObject();
//		Connection connection = null;
//
//		try {
//			JSONObject jsonObj = new JSONObject(data);
//
//			if (jsonObj.has(Constant.STAFF_NAME) && jsonObj.has(Constant.TABLE_NO)) {
//				String staff_name = jsonObj.getString(Constant.STAFF_NAME);
//				String table_no = jsonObj.getString(Constant.TABLE_NO);
//				
//				connection = dataSource.getConnection();
//
//				PreparedStatement stmt = connection.prepareStatement("SELECT id FROM empldef WHERE username = ?;");
//				stmt.setString(1, staff_name);
//				ResultSet rs = (ResultSet) stmt.executeQuery();
//				
//				if (rs.next()) {
//					String staff_id = rs.getString("id");
//
//					stmt = connection.prepareStatement("select chk_num from masterchecks");
//					ResultSet rs2 = (ResultSet) stmt.executeQuery();
//					
//					if (rs2.next()) {
//						int currentchknum = rs2.getInt("chk_num");
//						int newchecknum = currentchknum + 1;
//	
//						stmt = connection.prepareStatement("UPDATE masterchecks set chk_num=? where chk_num=?");
//						stmt.setInt(1, newchecknum);
//						stmt.setInt(2, currentchknum);
//						int rs3 = stmt.executeUpdate();
//						
//						if (rs3 > 0) {		
//							stmt = connection.prepareStatement("INSERT INTO checks(chk_num,empl_id,tblno,storeid,chk_open,sub_ttl,tax_ttl,pymnt_ttl,due_ttl) values (?,?,?,?,2,0,0,0,0)");
//							stmt.setInt(1, newchecknum);
//							stmt.setString(2, staff_id);
//							stmt.setString(3, table_no);
//							stmt.setInt(4, 1);
//							int rs4 = stmt.executeUpdate();
//							
//							if (rs4 > 0) {
//								stmt = connection.prepareStatement("select * from checks where chk_num=?");
//								stmt.setInt(1, newchecknum);
//								ResultSet rs5 = (ResultSet) stmt.executeQuery();
//								
//								if (rs5.next()) {
//									jsonResult.put(Constant.CHECK_NO, rs5.getString("chk_num"));
//									Logger.writeActivity("Check Number : " + rs5.getString("chk_num") + " Created", ECPOS_FOLDER);
//									
//									jsonResult.put(Constant.RESPONSE_CODE, "00");
//									jsonResult.put(Constant.RESPONSE_MESSAGE, "Success");
//								} else {
//									Logger.writeActivity("Check Not Found", ECPOS_FOLDER);
//									jsonResult.put(Constant.RESPONSE_CODE, "01");
//									jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Not Found");
//								}
//							} else {
//								Logger.writeActivity("Check Failed To Insert", ECPOS_FOLDER);
//								jsonResult.put(Constant.RESPONSE_CODE, "01");
//								jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Failed To Insert");
//							}
//						} else {
//							Logger.writeActivity("MasterCheckNum Failed To Update", ECPOS_FOLDER);
//							jsonResult.put(Constant.RESPONSE_CODE, "01");
//							jsonResult.put(Constant.RESPONSE_MESSAGE, "MasterCheckNum Failed To Update");
//						}
//					} else {
//						Logger.writeActivity("MasterCheckNum Not Found", ECPOS_FOLDER);
//						jsonResult.put(Constant.RESPONSE_CODE, "01");
//						jsonResult.put(Constant.RESPONSE_MESSAGE, "MasterCheckNum Not Found");
//					}
//				} else {
//					Logger.writeActivity("Staff Not Found When Creating Check", ECPOS_FOLDER);
//					jsonResult.put(Constant.RESPONSE_CODE, "01");
//					jsonResult.put(Constant.RESPONSE_MESSAGE, "Staff Not Found");
//				}
//			} else {
//				Logger.writeActivity("Invalid Request While Creating Check", ECPOS_FOLDER);
//				jsonResult.put(Constant.RESPONSE_CODE, "01");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "Invalid Request");
//			}
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					Logger.writeError(e, "SQLException: ", ECPOS_FOLDER);
//					e.printStackTrace();
//				}
//			}
//		}
//		return jsonResult.toString();
//	}
//
//	@RequestMapping(value = { "/create_split_check" }, method = { RequestMethod.POST }, produces = "application/json")
//	@ResponseBody
//	public String createSplitCheck(HttpServletRequest request, @RequestBody String data) {
//
//		JSONObject jsonResult = null;
//		JSONObject jsonObj = null;
//		Connection connection = null;
//
//		try {
//			jsonResult = new JSONObject();
//			jsonObj = new JSONObject(data);
//
//			if (jsonObj.has("chk_num") && jsonObj.has("selected_detail_items")) {
//				String chk_num = jsonObj.getString("chk_num");
//				JSONArray selected_items = jsonObj.getJSONArray("selected_detail_items");
//
//				System.out.println("Selected Split-Check Item List: " + selected_items.length());
//
//				int[] itemIds = new int[selected_items.length()];
//
//				for (int i = 0; i < selected_items.length(); i++) {
//					itemIds[i] = selected_items.optInt(i);
//					// System.out.println("Selected ITem Id:" + selected_items.optInt(i));
//				}
//
//				// Insert new item
//				connection = dataSource.getConnection();
//
//				PreparedStatement stmt = connection.prepareStatement("SELECT * FROM checks WHERE chk_num =?");
//				stmt.setString(1, chk_num);
//				ResultSet rs = (ResultSet) stmt.executeQuery();
//
//				if (rs.next()) {
//
//					String result_chk_num = null;
//
//					// Split "123_1" into 123,1 for split check number addition
//					String[] processing_chk_num = chk_num.split("_");
//
//					// For first time, split check
//					if (processing_chk_num.length == 1) {
//						result_chk_num = processing_chk_num[0] + "_" + Integer.toString(1);
//					}
//
//					/*
//					 * if(processing_chk_num.length>1) { int split_chk_number =
//					 * Integer.parseInt(processing_chk_num[1]) + 1; result_chk_num =
//					 * processing_chk_num[0] + "_" + Integer.toString(split_chk_number); } else {
//					 * result_chk_num = processing_chk_num[0] + "_" + Integer.toString(1); }
//					 */
//
//					// Check for duplication
//					stmt = connection.prepareStatement("SELECT chk_num FROM checks WHERE chk_num LIKE ?");
//					stmt.setString(1, processing_chk_num[0] + "%");
//					ResultSet rs_duplicate = (ResultSet) stmt.executeQuery();
//
//					List<String> list_of_duplicate_chk_num = new ArrayList<String>();
//
//					if (rs_duplicate.next()) {
//						while (rs_duplicate.next()) {
//							String duplicate_chk_num = rs_duplicate.getString("chk_num");
//							System.out.println("What my Check num: " + duplicate_chk_num);
//							list_of_duplicate_chk_num.add(duplicate_chk_num);
//						}
//
//						if (list_of_duplicate_chk_num.size() >= 1) {
//
//							String possibe_duplicate_chk_num = list_of_duplicate_chk_num
//									.get(list_of_duplicate_chk_num.size() - 1);
//							System.out.println("Who are you ? :" + possibe_duplicate_chk_num);
//
//							String[] processing_result_chk_num = possibe_duplicate_chk_num.split("_");
//							// check out-of-bound
//							if (processing_result_chk_num.length > 1) {
//								int chk_counter = Integer.parseInt(processing_result_chk_num[1]) + 1;
//								result_chk_num = processing_result_chk_num[0] + "_" + Integer.toString(chk_counter);
//							}
//						}
//					}
//
//					System.out.println("Chk_num me: " + result_chk_num);
//
//					stmt = connection.prepareStatement(
//							"INSERT INTO checks (chk_num,empl_id,tblno,storeid,chk_open,sub_ttl,tax_ttl,pymnt_ttl,due_ttl) VALUES (?,?,?,?,2,0,0,0,0)");
//					stmt.setString(1, result_chk_num);
//					stmt.setString(2, rs.getString("empl_id"));
//					stmt.setString(3, rs.getString("tblno"));
//					stmt.setInt(4, 1);
//					stmt.executeUpdate();
//
//					if (selected_items.length() > 0) {
//
//						// Retrieve the newly created split check chk_seq
//						stmt = connection.prepareStatement("SELECT chk_seq FROM checks WHERE chk_num = ?");
//						stmt.setString(1, result_chk_num);
//						ResultSet rs2 = (ResultSet) stmt.executeQuery();
//						rs2.next();
//						String latest_chk_seq = rs2.getString("chk_seq");
//
//						// Update the existing details chk_seq that added into new split check
//						for (int j = 0; j < itemIds.length; j++) {
//							stmt = connection.prepareStatement("UPDATE details SET chk_seq = ? WHERE id = ?");
//							stmt.setString(1, latest_chk_seq);
//							stmt.setInt(2, itemIds[j]);
//							stmt.executeUpdate();
//						}
//
//					}
//
//					jsonResult.put(Constant.RESPONSE_CODE, "00");
//					jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//
//				} else {
//					jsonResult.put(Constant.RESPONSE_CODE, "01");
//					jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID REQUEST");
//
//				}
//			} else {
//				jsonResult.put(Constant.RESPONSE_CODE, "01");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID REQUEST");
//
//			}
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (Exception e) {
//					Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//					e.printStackTrace();
//				}
//			}
//		}
//		return jsonResult.toString();
//	}
//
//	@RequestMapping(value = { "/update_check" }, method = { RequestMethod.GET,
//			RequestMethod.POST }, produces = "application/json")
//	@ResponseBody
//	public String updateCheck(HttpServletRequest request, @RequestBody String data) {
//
//		JSONObject jsonObj = null;
//		JSONObject jsonResult = null;
//		Connection connection = null;
//
//		try {
//			jsonResult = new JSONObject();
//			jsonObj = new JSONObject(data);
//
//			if (jsonObj.has("check_no") && jsonObj.has("staff_name") && jsonObj.has("ordered_item")) {
//				String check_no = jsonObj.getString("check_no");
//				String staff_name = jsonObj.getString("staff_name");
//				JSONArray ordered_item = jsonObj.getJSONArray("ordered_item");
//
//				connection = dataSource.getConnection();
//
//				PreparedStatement stmt = connection.prepareStatement("SELECT * FROM checks WHERE chk_num = ?;");
//				stmt.setString(1, check_no);
//				ResultSet resultSet = (ResultSet) stmt.executeQuery();
//				if (resultSet.next()) {
//					stmt = connection.prepareStatement("SELECT id FROM empldef WHERE username = ?;");
//					stmt.setString(1, staff_name);
//					ResultSet resultSet2 = (ResultSet) stmt.executeQuery();
//					if (resultSet2.next()) {
//						for (int i = 0; i < ordered_item.length(); i++) {
//							JSONObject item = (JSONObject) ordered_item.get(i);
//							if (item.has("item_name") && item.has("item_qty") && item.has("total_price")) {
//								String item_name = item.getString("item_name");
//								// double total_price = item.getDouble(Constant.TOTAL_PRICE);
//								// int item_qty = item.getInt(Constant.ITEM_QTY);
//
//								stmt = connection.prepareStatement("SELECT * FROM menudef WHERE name=?");
//								stmt.setString(1, item_name);
//								ResultSet resultSet3 = (ResultSet) stmt.executeQuery();
//								if (resultSet3.next()) {
//									stmt = connection.prepareStatement(
//											"INSERT INTO details(chk_seq,dtl_seq,number,name,chk_ttl) values(?,?,?,?,?)");
//									stmt.setString(1, resultSet.getString("chk_seq"));
//									stmt.setInt(2, 0);
//									stmt.setString(3, item.getString("item_qty"));
//									stmt.setString(4, resultSet3.getString("name"));
//									stmt.setDouble(5, resultSet3.getDouble("price"));
//									stmt.executeUpdate();
//
//									jsonResult.put("respCode", "00");
//									jsonResult.put("responseMsg", "Success");
//								} else {
//									/*
//									 * jsonResult.put(Constant.RESPONSE_CODE, "01");
//									 * jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID ITEM NAME - " +
//									 * item_name); break;
//									 */
//									double price = 0.00;
//									price = Double.parseDouble(item.getString("total_price"))
//											/ Double.parseDouble(item.getString("item_qty"));
//									stmt = connection.prepareStatement(
//											"INSERT INTO details(chk_seq,dtl_seq,number,name,chk_ttl) values(?,?,?,?,?)");
//									stmt.setString(1, resultSet.getString("chk_seq"));
//									stmt.setInt(2, 0);
//									stmt.setString(3, item.getString("item_qty"));
//									stmt.setString(4, item.getString("item_name"));
//									stmt.setDouble(5, price);
//									stmt.executeUpdate();
//
//									jsonResult.put("respCode", "00");
//									jsonResult.put("responseMsg", "Success");
//								}
//							} else {
//								jsonResult.put("respCode", "01");
//								jsonResult.put("responseMsg", "Invalid Request");
//								break;
//							}
//						}
//					} else {
//						jsonResult.put("respCode", "01");
//						jsonResult.put("responseMsg", "Staff Not Found");
//					}
//				} else {
//					jsonResult.put("respCode", "01");
//					jsonResult.put("responseMsg", "Invalid Check Number");
//				}
//			} else {
//				jsonResult.put("respCode", "01");
//				jsonResult.put("responseMsg", "Invalid Request");
//			}
//
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (Exception e) {
//					Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//					e.printStackTrace();
//				}
//			}
//		}
//		return jsonResult.toString();
//
//	}
//
//	// Add customItem into checks ({})
//	@RequestMapping(value = { "/add_openItem" }, method = { RequestMethod.GET,
//			RequestMethod.POST }, produces = "application/json")
//	@ResponseBody
//	public String addOpenItem(HttpServletRequest request, @RequestBody String data) {
//
//		JSONObject jsonResult = new JSONObject();
//		JSONObject jsonObj = null;
//		Connection connection = null;
//
//		String chk_no = "";
//		String chk_sequence = "";
//		double price = 0.00;
//
//		try {
//			jsonObj = new JSONObject(data);
//			chk_no = jsonObj.getString("check_no");
//			price = jsonObj.getDouble("price");
//
//			chk_sequence = getCheckSeq(chk_no);
//
//			connection = dataSource.getConnection();
//
//			PreparedStatement stmt = connection
//					.prepareStatement("select name from details where chk_seq = ? and name like ? order by name DESC");
//
//			/*
//			 * PreparedStatement stmt = connection
//			 * .prepareStatement("select name from details where chk_num = ? and name like ? order by name DESC"
//			 * );
//			 */
//			stmt.setInt(1, Integer.parseInt(chk_sequence));
//			/* stmt.setString(1, chk_no); */
//			stmt.setString(2, "OpenItem_%");
//			ResultSet resultSet = (ResultSet) stmt.executeQuery();
//
//			String openItemName = "";
//			if (resultSet.next()) {
//				openItemName = resultSet.getString("name");
//				openItemName = openItemName.substring(openItemName.indexOf("_") + 1, openItemName.length());
//				int seq = Integer.parseInt(openItemName);
//				seq++;
//				openItemName = String.format("OpenItem_%03d", seq);
//			} else {
//				openItemName = "OpenItem_001";
//			}
//			System.out.println(openItemName);
//			stmt = connection
//					.prepareStatement("INSERT into details(chk_seq,dtl_seq,number,name,chk_ttl) values(?,?,?,?,?)");
//			stmt.setInt(1, Integer.parseInt(chk_sequence));
//			stmt.setInt(2, 0);
//			stmt.setInt(3, 0);
//			stmt.setString(4, openItemName);
//			stmt.setDouble(5, price);
//			stmt.executeUpdate();
//
//			jsonResult.put(Constant.RESPONSE_CODE, "00");
//			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return jsonResult.toString();
//	}
//
//	/*
//	 * @RequestMapping(value = { "/retrieve_latest_check_data" }, method = {
//	 * RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
//	 * 
//	 * @ResponseBody public String retrieveLatestCheckData(HttpServletRequest
//	 * request) {
//	 * 
//	 * JSONObject jsonResult = new JSONObject(); Connection connection = null;
//	 * 
//	 * HttpSession session = request.getSession(); String str_check_sequence =
//	 * (String)session.getAttribute("Session_Check_Sequence");
//	 * 
//	 * try{
//	 * 
//	 * 
//	 * if(check_sequence==null) { jsonResult.put(Constant.RESPONSE_CODE, "01");
//	 * jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Sequence not found"); }else
//	 * { int check_seq = Integer.parseInt(str_check_sequence);
//	 * 
//	 * connection = dataSource.getConnection();
//	 * 
//	 * PreparedStatement stmt =
//	 * connection.prepareStatement("select * from checks where chk_seq=?");
//	 * 
//	 * stmt.setInt(1,check_seq);
//	 * 
//	 * ResultSet resultSet = (ResultSet) stmt.executeQuery(); resultSet.next();
//	 * 
//	 * uo.setChecknumber(resultSet.getInt("chk_num"));
//	 * uo.setChksequence(resultSet.getInt("chk_seq"));
//	 * uo.setDatetime(resultSet.getString("createdate"));
//	 * uo.setTableno(resultSet.getString("tblno"));
//	 * uo.setStatus(resultSet.getInt("chk_open"));
//	 * uo.setSubttl(resultSet.getDouble("sub_ttl"));
//	 * uo.setTtl(resultSet.getDouble("pymnt_ttl"));
//	 * uo.setSalestax(resultSet.getDouble("tax_ttl"));
//	 * uo.setDue(resultSet.getDouble("due_ttl"));
//	 * 
//	 * 
//	 * 
//	 * stmt = connection.
//	 * prepareStatement("select *,(select itemcode from menudef where id= number) as 'itemcode' from details where chk_seq=?"
//	 * ); stmt.setInt(1, uo.getChksequence()); resultSet = (ResultSet)
//	 * stmt.executeQuery();
//	 * 
//	 * ArrayList<Pojo_member> clear = new ArrayList<Pojo_member>();
//	 * uo.setItemlist(clear);
//	 * 
//	 * ArrayList<Pojo_member> itemlist = uo.getItemlist();
//	 * 
//	 * while(resultSet.next()){ Pojo_member currentPojo_member = new Pojo_member();
//	 * currentPojo_member.setItemcode(resultSet.getInt("itemcode"));
//	 * currentPojo_member.setItemid(resultSet.getInt("id"));
//	 * currentPojo_member.setItemname(resultSet.getString("name"));
//	 * currentPojo_member.setItemprice(resultSet.getDouble("chk_ttl"));
//	 * itemlist.add(currentPojo_member); }
//	 * 
//	 * stmt = connection.
//	 * prepareStatement("select ROUND(SUM(chk_ttl), 2) as ttl from details where chk_seq=?"
//	 * ); stmt.setInt(1, uo.getChksequence()); resultSet = (ResultSet)
//	 * stmt.executeQuery(); resultSet.next();
//	 * uo.setSubttl(resultSet.getDouble("ttl"));
//	 * 
//	 * 
//	 * 
//	 * 
//	 * }
//	 * 
//	 * 
//	 * 
//	 * 
//	 * 
//	 * 
//	 * } catch(Exception ex){ System.out.println("ERROR at : " + ex);
//	 * ex.printStackTrace(); } finally{ if(connection!=null){ try {
//	 * connection.close(); } catch (SQLException e) { // TODO Auto-generated catch
//	 * block e.printStackTrace(); } } }
//	 * 
//	 * 
//	 * }
//	 */
//
//	@RequestMapping(value = { "/manager_make_cash_payment" }, method = {
//			RequestMethod.POST }, produces = "application/json")
//	@ResponseBody
//	public String managerMakeCashPayment(HttpServletRequest request, @RequestBody String data) {
//
//		int userid = 0;
//		JSONObject jsonResult = new JSONObject();
//		JSONObject jsonObj = null;
//		Connection connection = null;
//		PreparedStatement stmt = null;
//
//		java.sql.Timestamp ourJavaTimestampObject = new java.sql.Timestamp(System.currentTimeMillis());
//		Date date = new Date(ourJavaTimestampObject.getTime());
//
//		DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//		UtilWebComponents webcomponent = new UtilWebComponents();
//
//		try {
//
//			UserAuthenticationModel session_container_user = webcomponent.getEcposSession(request);
//			if (session_container_user != null) {
//				userid = session_container_user.getUserLoginId();
//			}
//
//			jsonObj = new JSONObject(data);
//			if (jsonObj.has("chk_num") && jsonObj.has("chk_seq") && jsonObj.has("selected_items_ttl")
//					&& jsonObj.has("key_in_amt") && jsonObj.has("payment_balance_amt")
//					&& jsonObj.has("selected_detail_items") && session_container_user != null) {
//
//				String chk_num = jsonObj.getString("chk_num");
//				String chk_seq = jsonObj.getString("chk_seq");
//				double amount = Double.parseDouble(jsonObj.getString("selected_items_ttl"));
//				double key_in_amt = Double.parseDouble(jsonObj.getString("key_in_amt"));
//				double balance_amt = Double.parseDouble(jsonObj.getString("payment_balance_amt"));
//				JSONArray selected_items = jsonObj.getJSONArray("selected_detail_items");
//
//				int[] itemIds = new int[selected_items.length()];
//
//				for (int i = 0; i < selected_items.length(); i++) {
//					itemIds[i] = selected_items.optInt(i);
//				}
//
//				System.out.println("chk_num " + chk_num);
//				System.out.println("chk_seq " + chk_seq);
//				System.out.println("amount " + amount);
//				System.out.println("key_in_amt " + key_in_amt);
//				System.out.println("balance_amt " + balance_amt);
//				System.out.println("selected_items " + itemIds);
//
//				connection = dataSource.getConnection();
//
//				for (int j = 0; j < itemIds.length; j++) {
//					stmt = connection.prepareStatement("UPDATE details SET payment_status =1 WHERE id=?");
//					stmt.setInt(1, itemIds[j]);
//					stmt.executeUpdate();
//				}
//
//				// SELECT the available item for that particular check
//				stmt = connection.prepareStatement(
//						"SELECT COUNT(id) as detail_count FROM details WHERE chk_seq =? AND detail_item_status = 0 AND payment_status = 0");
//				stmt.setString(1, chk_seq);
//				ResultSet rs = stmt.executeQuery();
//
//				rs.next();
//				int detail_counts = Integer.parseInt(rs.getString("detail_count"));
//
//				jsonResult.put("detail_counts", detail_counts);
//
//				if (detail_counts == 0) {
//					// No More Item in the check. Close it.
//					stmt = connection.prepareStatement("UPDATE checks set chk_open=3 where chk_seq=?");
//					stmt.setString(1, chk_seq);
//					stmt.executeUpdate();
//				}
//
//				stmt = connection.prepareStatement("INSERT INTO transaction (check_no, tran_type, "
//						+ "tran_status, payment_type, amount, performBy, tran_datetime) VALUES (?,?,?,?,?,?,?)");
//				stmt.setString(1, chk_num);
//				stmt.setString(2, "SALES");
//				stmt.setString(3, "APPROVED");
//				stmt.setString(4, "CASH");
//				stmt.setDouble(5, amount);
//				stmt.setInt(6, userid);
//				stmt.setDate(7, date);
//				stmt.executeUpdate();
//
//				jsonResult.put(Constant.RESPONSE_CODE, "00");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//			} else {
//				jsonResult.put(Constant.RESPONSE_CODE, "01");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID REQUEST");
//			}
//		} catch (Exception e) {
//			System.out.println("ERROR at : " + e);
//			e.printStackTrace();
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return jsonResult.toString();
//	}
//
//	@RequestMapping(value = { "/get_group_list" }, method = { RequestMethod.GET }, produces = "application/json")
//	@ResponseBody
//	public String get_groupList(HttpServletRequest request) {
//
//		JSONObject jsonResult = new JSONObject();
//		JSONArray group_list = null;
//		Connection connection = null;
//
//		try {
//			connection = dataSource.getConnection();
//
//			PreparedStatement stmt = connection.prepareStatement("select * from itemgroup;");
//			ResultSet resultSet = (ResultSet) stmt.executeQuery();
//
//			group_list = new JSONArray();
//
//			while (resultSet.next()) {
//				JSONObject group_list_item = new JSONObject();
//				group_list_item.put("groupname", resultSet.getString("groupname"));
//				group_list_item.put("grouptype", resultSet.getInt("grouptype"));
//				group_list_item.put("groupid", resultSet.getInt("id"));
//
//				group_list.put(group_list_item);
//
//			}
//
//			jsonResult.put(Constant.RESPONSE_CODE, "00");
//			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//
//			jsonResult.put("group_list", group_list);
//
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return Constant.EXCEPTION_MESSAGE;
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					Logger.writeError(e, "SQLException: ", ECPOS_FOLDER);
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return jsonResult.toString();
//	}
//
//	@RequestMapping(value = { "/get_group_items/{grouptype}" }, method = {
//			RequestMethod.GET }, produces = "application/json")
//	@ResponseBody
//	public String get_groupItems(HttpServletRequest request, @PathVariable("grouptype") String grouptype) {
//
//		JSONObject jsonResult = new JSONObject();
//		JSONArray item_list = null;
//		Connection connection = null;
//
//		try {
//			connection = dataSource.getConnection();
//
//			PreparedStatement stmt = connection
//					.prepareStatement("select * from menudef where itemgroup_id = ? AND itemstatus = 0 order by name");
//			stmt.setString(1, grouptype);
//			ResultSet resultSet = (ResultSet) stmt.executeQuery();
//
//			item_list = new JSONArray();
//
//			while (resultSet.next()) {
//				JSONObject item = new JSONObject();
//				item.put("item_id", resultSet.getString("id"));
//				item.put("item_code", resultSet.getString("itemcode"));
//				item.put("name", resultSet.getString("name"));
//				item.put("item_price", resultSet.getString("price"));
//				item.put("itemgroup_id", resultSet.getString("itemgroup_id"));
//				item.put("gstgroup_id", resultSet.getString("gstgroup_id"));
//
//				if (resultSet.getString("image_path") == null)
//					item.put("image_path", "/member/meta/images/default_image.png");
//				else
//					item.put("image_path", resultSet.getString("image_path"));
//
//				item_list.put(item);
//			}
//
//			jsonResult.put(Constant.RESPONSE_CODE, "00");
//			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//
//			jsonResult.put("item_list", item_list);
//
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return Constant.EXCEPTION_MESSAGE;
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return jsonResult.toString();
//
//	}
//
//	@RequestMapping(value = { "/manager_add_item_to_check" }, method = {
//			RequestMethod.POST }, produces = "application/json")
//	@ResponseBody
//	public String manager_addItemToCheck(HttpServletRequest request, @RequestBody String data) {
//
//		JSONObject jsonResult = new JSONObject();
//		JSONObject jsonObj = null;
//		Connection connection = null;
//
//		try {
//
//			jsonObj = new JSONObject(data);
//			String checksequence = jsonObj.getString("chk_seq");
//			String itemid = jsonObj.getString("item_id");
//
//			connection = dataSource.getConnection();
//
//			PreparedStatement stmt = connection.prepareStatement("select * from menudef where id=?");
//			stmt.setString(1, itemid);
//			ResultSet resultSet = (ResultSet) stmt.executeQuery();
//			resultSet.next();
//
//			stmt = connection
//					.prepareStatement("INSERT into details(chk_seq,dtl_seq,number,name,chk_ttl) values(?,?,?,?,?)");
//			stmt.setString(1, checksequence);
//			stmt.setInt(2, 0);
//			stmt.setInt(3, resultSet.getInt("id"));
//			stmt.setString(4, resultSet.getString("name"));
//			stmt.setDouble(5, resultSet.getDouble("price"));
//			stmt.executeUpdate();
//
//			jsonResult.put(Constant.RESPONSE_CODE, "00");
//			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//
//		} catch (Exception ex) {
//			System.out.println("ERROR at : " + ex);
//			ex.printStackTrace();
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return jsonResult.toString();
//	}
//
//	@RequestMapping(value = { "/manager_get_check_detail/{check_no}" }, method = {
//			RequestMethod.GET }, produces = "application/json")
//	@ResponseBody
//	public String manager_checkDetails(@PathVariable("check_no") String check_no, HttpServletRequest request) {
//
//		JSONObject jsonResult = new JSONObject();
//		Connection connection = null;
//		JSONArray item_detail_array = null;
//
//		try {
//			String chk_seq = getCheckSeq(check_no);
//
//			connection = dataSource.getConnection();
//
//			PreparedStatement stmt = connection.prepareStatement("select * from checks where chk_seq=?");
//			stmt.setInt(1, Integer.parseInt(chk_seq));
//
//			ResultSet resultSet = (ResultSet) stmt.executeQuery();
//			resultSet.next();
//
//			jsonResult.put(Constant.RESPONSE_CODE, "00");
//			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//
//			jsonResult.put("checknumber", resultSet.getInt("chk_num"));
//			jsonResult.put("chksequence", resultSet.getInt("chk_seq"));
//			jsonResult.put("datetime", resultSet.getString("createdate"));
//			jsonResult.put("tableno", resultSet.getString("tblno"));
//			jsonResult.put("status", resultSet.getInt("chk_open"));
//			// jsonResult.put("subttl",resultSet.getDouble("sub_ttl"));
//			jsonResult.put("ttl", resultSet.getDouble("pymnt_ttl"));
//			jsonResult.put("salestax", resultSet.getDouble("tax_ttl"));
//			jsonResult.put("due", resultSet.getDouble("due_ttl"));
//
//			stmt = connection.prepareStatement(
//					"select *,(select itemcode from menudef where id= number) as 'itemcode' from details where chk_seq=? AND detail_item_status = 0 AND payment_status = 0");
//			stmt.setInt(1, Integer.parseInt(chk_seq));
//			resultSet = (ResultSet) stmt.executeQuery();
//
//			item_detail_array = new JSONArray();
//
//			while (resultSet.next()) {
//				JSONObject detail_item = new JSONObject();
//				detail_item.put("itemcode", resultSet.getString("itemcode"));
//				detail_item.put("itemid", resultSet.getInt("id"));
//				detail_item.put("itemname", resultSet.getString("name"));
//				detail_item.put("itemprice", df2.format(resultSet.getDouble("chk_ttl")));
//
//				item_detail_array.put(detail_item);
//			}
//
//			jsonResult.put("item_detail_array", item_detail_array);
//
//			stmt = connection.prepareStatement(
//					"select ROUND(SUM(chk_ttl), 2) as ttl from details where chk_seq=? AND detail_item_status = 0 AND payment_status = 0");
//			stmt.setInt(1, Integer.parseInt(chk_seq));
//			resultSet = (ResultSet) stmt.executeQuery();
//			resultSet.next();
//			jsonResult.put("subttl", resultSet.getDouble("ttl"));
//
//			connection.close();
//
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return Constant.EXCEPTION_MESSAGE;
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return jsonResult.toString();
//
//	}
//
//	private String getCheckSeq(String check_num) {
//		String result = "";
//		Connection connection = null;
//
//		try {
//			connection = dataSource.getConnection();
//			PreparedStatement stmt = connection.prepareStatement("SELECT chk_seq from checks where chk_num = ?;");
//			stmt.setString(1, check_num);
//			ResultSet rs = stmt.executeQuery();
//
//			rs.next();
//			result = rs.getString("chk_seq");
//
//			connection.close();
//		} catch (Exception ex) {
//			System.out.println("ERROR at : " + ex);
//			ex.printStackTrace();
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//		}
//
//		return result;
//	}
//
//	@RequestMapping(value = { "/remove_selected_check_items" }, method = {
//			RequestMethod.POST }, produces = "application/json")
//	@ResponseBody
//	public String removeSelectedCheckItems(HttpServletRequest request, @RequestBody String data) {
//
//		System.out.println(data);
//
//		JSONObject jsonResult = new JSONObject();
//		Connection connection = null;
//		JSONArray check_item_array = null;
//
//		try {
//
//			check_item_array = new JSONArray(data);
//			int[] itemIds = new int[check_item_array.length()];
//
//			for (int i = 0; i < check_item_array.length(); ++i) {
//				itemIds[i] = check_item_array.optInt(i);
//			}
//
//			connection = dataSource.getConnection();
//
//			for (int i = 0; i < itemIds.length; i++) {
//				int deletedItemId = itemIds[i];
//
//				PreparedStatement stmt = connection
//						.prepareStatement("UPDATE details SET detail_item_status = 1 WHERE id =?");
//				stmt.setInt(1, deletedItemId);
//				stmt.executeUpdate();
//			}
//
//			jsonResult.put(Constant.RESPONSE_CODE, "00");
//			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//
//		} catch (Exception ex) {
//			System.out.println("ERROR at : " + ex);
//			ex.printStackTrace();
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//		}
//
//		return jsonResult.toString();
//	}
//
//	@GetMapping(path = "/get_splitted_checklist/{chk_num}")
//	public List<String> getSplittedCheckList(@PathVariable("chk_num") String chk_num) {
//		List<String> splittedCheckList = new ArrayList<String>();
//		try {
//			String sqlStatement = "SELECT chk_num FROM macromacsuite.checks WHERE chk_num LIKE ? "
//					+ "AND chk_num NOT LIKE ? AND chk_open != 3";
//			splittedCheckList = jdbcTemplate.queryForList(sqlStatement,
//					new Object[] { getChkNumWithoutSplitNum(chk_num) + "%", chk_num }, String.class);
//		} catch (DataAccessException e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		}
//		return splittedCheckList;
//	}
//
//	private String getChkNumWithoutSplitNum(String chk_num) {
//		String splitChkNum[] = chk_num.split("_");
//		if (splitChkNum.length >= 1) {
//			return splitChkNum[0];
//		}
//		return null;
//	}
//
//	private static int detailSequence = 1;
//
//	// Used in show_take_away_order_CTRL
//	@PostMapping("/takeaway")
//	public ResponseEntity<?> createTakeAwayOrder(@RequestBody String data) {
//
//		JSONObject jsonResult;
//		JSONObject jsonData;
//		JSONArray takeAwayItems;
//
//		String SELECT_EMPLOYEE_ID_SQL = "SELECT id FROM empldef WHERE username = ?";
//		String SELECT_MASTERCHECK_CHECKNO_SQL = "SELECT chk_num FROM masterchecks";
//		String UPDATE_MASTERCHECK_SQL = "UPDATE masterchecks SET chk_num=? WHERE chk_num=?";
//		String INSERT_CHECK_SQL = "INSERT INTO checks(chk_num,empl_id,tblno,storeid,chk_open) values (?,?,0,?,2)";
//		String SELECT_CHECK_WITH_CHECKNO_SQL = "SELECT * FROM checks WHERE chk_num=?";
//		String INSERT_DETAILS_SQL = "INSERT INTO Details (chk_seq,dtl_seq,number,name,chk_ttl,detail_type,detail_item_price) VALUES (?,?,?,?,?,?,?)";
//		String SELECT_SYS_TAXES_SQL = "SELECT gst_percentage, sales_tax_percentage, service_tax_percentage, other_tax_percentage FROM system";
//
//		try {
//			jsonResult = new JSONObject();
//			jsonData = new JSONObject(data);
//
//			// Send the data to backend
//			if (!jsonData.has("takeAwayItemList") && !jsonData.has("staffName"))
//				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//
//			// Create new Check
//			int staffId = jdbcTemplate.queryForObject(SELECT_EMPLOYEE_ID_SQL,
//					new Object[] { jsonData.getString("staffName") }, Integer.class);
//
//			// Check if the integer is empty
//			int existingCheckNum = jdbcTemplate.queryForObject(SELECT_MASTERCHECK_CHECKNO_SQL, Integer.class);
//			int newchecknum = existingCheckNum + 1;
//
//			jdbcTemplate.update(UPDATE_MASTERCHECK_SQL, new Object[] { newchecknum, existingCheckNum });
//			String checknumber = Integer.toString(newchecknum) + "TA";
//
//			// KeyHolder checkKeyHolder = new GeneratedKeyHolder();
//			//
//			// jdbcTemplate.update(connection-> {PreparedStatement ps =
//			// connection.prepareStatement(INSERT_CHECK_SQL,
//			// PreparedStatement.RETURN_GENERATED_KEYS);
//			// ps.setString(1, checknumber);
//			// ps.setInt(2, staffId);
//			// ps.setInt(3, 1);
//			// return ps;
//			// },checkKeyHolder);
//
//			jdbcTemplate.update(INSERT_CHECK_SQL, new Object[] { checknumber, staffId, 1 }); // Get generated id here
//
//			Map<String, Object> checkResultMap = jdbcTemplate.queryForMap(SELECT_CHECK_WITH_CHECKNO_SQL,
//					new Object[] { checknumber });
//
//			// Retrieve the takeaway item
//			takeAwayItems = jsonData.getJSONArray("takeAwayItemList");
//			List<Long> generatedIdList = new ArrayList<>();
//
//			// Get system tax
//			Map<String, Object> systemTaxResultMap = jdbcTemplate.queryForMap("SELECT * FROM system");
//			double salesTaxPercentage = (int) systemTaxResultMap.get("sales_tax_percentage");
//			double serviceTaxPercentage = (int) systemTaxResultMap.get("service_tax_percentage");
//
//			salesTaxPercentage = salesTaxPercentage / 100;
//			serviceTaxPercentage = serviceTaxPercentage / 100;
//
//			BigDecimal subTtl = new BigDecimal(0.00);
//			BigDecimal taxTtl = new BigDecimal(0.00);
//			BigDecimal dueTtl = new BigDecimal(0.00);
//			BigDecimal salesTax = new BigDecimal(0.00);
//			BigDecimal serviceTax = new BigDecimal(0.00);
//
//			for (int i = 0; i < takeAwayItems.length(); i++) {
//				JSONObject item = takeAwayItems.getJSONObject(i);
//				KeyHolder keyHolder = new GeneratedKeyHolder();
//
//				// jdbcTemplate.update(INSERT_DETAILS_SQL, new Object[]
//				// {(int)checkResultMap.get("chk_seq"),detailSequence,item.getInt("itemid"),item.getDouble("itemPrice"),'S',item.getDouble("itemPrice")});
//
//				// Check the item detail
//				Map<String, Object> menuItemResultMap = jdbcTemplate.queryForMap("SELECT * FROM menudef WHERE id = ?",
//						new Object[] { item.getInt("itemid") });
//
//				if (!menuItemResultMap.isEmpty()) {
//					if ((int) menuItemResultMap.get("sststatus") == 1) {
//						// Goods
//						if ((int) menuItemResultMap.get("itemtype") == 1) {
//							BigDecimal salesTaxAmt = new BigDecimal(item.getDouble("itemprice"));
//							salesTax = salesTax.add(salesTaxAmt.multiply(new BigDecimal(salesTaxPercentage)));
//							// Service
//						} else if ((int) menuItemResultMap.get("itemtype") == 2) {
//							BigDecimal serviceTaxAmt = new BigDecimal(item.getDouble("itemprice"));
//							serviceTax = serviceTax.add(serviceTaxAmt.multiply(new BigDecimal(serviceTaxPercentage)));
//						}
//					}
//				}
//
//				jdbcTemplate.update(connection -> {
//					PreparedStatement ps = connection.prepareStatement(INSERT_DETAILS_SQL,
//							PreparedStatement.RETURN_GENERATED_KEYS);
//
//					try {
//						ps.setLong(1, (long) checkResultMap.get("chk_seq"));
//						ps.setInt(2, detailSequence++);
//						ps.setInt(3, item.getInt("itemid"));
//						ps.setString(4, item.getString("itemname"));
//						ps.setBigDecimal(5, new BigDecimal(item.getDouble("itemprice")));
//						ps.setString(6, "S");
//						ps.setBigDecimal(7, new BigDecimal(item.getDouble("itemprice")));
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					}
//					return ps;
//				}, keyHolder);
//
//				subTtl = subTtl.add(new BigDecimal(item.getDouble("itemprice")));
//				System.out.println("My Bigger Subtotal: " + subTtl.toString());
//				taxTtl = salesTax.add(serviceTax);
//
//				generatedIdList.add((long) keyHolder.getKey());
//			}
//
//			subTtl = subTtl.add(taxTtl);
//			System.out.println("Subtotal: " + subTtl.toString());
//			dueTtl = subTtl.add(BigDecimal.ZERO);
//
//			// Need to add taxes deduction amount into checks
//			jdbcTemplate.update(
//					"UPDATE checks SET sales_tax = ?, service_tax = ?, tax_ttl = ?, sub_ttl = ?, due_ttl = ? WHERE chk_num = ?",
//					new Object[] { salesTax, serviceTax, taxTtl, subTtl, dueTtl, checknumber });
//
//			jsonResult.put("checkNumber", checknumber);
//			jsonResult.put("kitchenReceiptPrinting", generatedIdList);
//			detailSequence = 1; // Reset the value
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//		return new ResponseEntity<>(jsonResult.toString(), HttpStatus.OK);
//	}

}
