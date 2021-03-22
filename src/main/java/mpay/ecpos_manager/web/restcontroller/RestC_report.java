package mpay.ecpos_manager.web.restcontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.UserAuthenticationModel;
import mpay.ecpos_manager.general.utility.WebComponents;

@RestController
@RequestMapping("/rc/report")
public class RestC_report {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@RequestMapping(value =  "/get_sales_summary" , method = { RequestMethod.POST }, headers = "Accept=application/json")
	private String getSalesSummary(@RequestBody String dataObj, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jObjectResult = new JSONObject();
		JSONArray JARY = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				JSONObject jsonObj = new JSONObject(dataObj);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				Date startDate = dateFormat.parse(jsonObj.getString("startDate").replaceAll("T", " ").replaceAll("Z", ""));
				Date endDate = dateFormat.parse(jsonObj.getString("endDate").replaceAll("T", " ").replaceAll("Z", ""));
	
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("select pm.name,t.count,t.amount from payment_method pm " + 
						"left join (select payment_method,count(*) as count,sum(transaction_amount) as amount from transaction " + 
						"where transaction_type = 1 and transaction_status = 3 and created_date >= ? and created_date <= ? " + 
						"group by payment_method) t on t.payment_method = pm.id;");
				stmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
				stmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					JSONObject jObject = new JSONObject();
					jObject.put("paymentMethod", rs.getString("name") == null ? "-" : rs.getString("name"));
					jObject.put("totalCount", rs.getInt("count") == 0 ? "-" : rs.getInt("count"));
					jObject.put("totalAmount", rs.getBigDecimal("amount") == null ? "-" : String.format("%.2f", rs.getBigDecimal("amount")));
					
					JARY.put(jObject);
				}
				jObjectResult.put("data", JARY);
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
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
		return jObjectResult.toString();
	}
	
	@RequestMapping(value =  "/get_item_summary" , method = { RequestMethod.POST }, headers = "Accept=application/json")
	private String getItemSummary(@RequestBody String dataObj, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jObjectResult = new JSONObject();
		JSONArray JARY = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		
		try {
			if (user != null) {
				JSONObject jsonObj = new JSONObject(dataObj);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				Date startDate = dateFormat.parse(jsonObj.getString("startDate").replaceAll("T", " ").replaceAll("Z", ""));
				Date endDate = dateFormat.parse(jsonObj.getString("endDate").replaceAll("T", " ").replaceAll("Z", ""));
	
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("SELECT menu_item_name, SUM(quantity) as totalitem FROM check_detail " + 
						"WHERE check_detail_status = 3 AND created_date >= ? " + 
						"AND created_date <= ? and parent_check_detail_id is null " + 
						"and menu_item_name <> 'Room Charge' " + 
						"GROUP BY menu_item_name;");
				stmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
				stmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					JSONObject jObject = new JSONObject();
					jObject.put("items", rs.getString("menu_item_name") == null ? "-" : rs.getString("menu_item_name"));
					jObject.put("totalItems", rs.getInt("totalitem") == 0 ? "-" : rs.getInt("totalitem"));
					
					JARY.put(jObject);
				}
				jObjectResult.put("data", JARY);
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
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
		return jObjectResult.toString();
	}
	
	@RequestMapping(value =  "/get_sales_summary_chart" , method = { RequestMethod.GET }, headers = "Accept=application/json")
	private String getSalesSummaryChart(@RequestParam String startDate, @RequestParam String endDate, HttpServletRequest request, HttpServletResponse response) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		StringBuffer paymentMethod = new StringBuffer();
		StringBuffer totalCount = new StringBuffer();
		String jsonData = "";
		
		try {
			if (user != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				Date newstartDate = dateFormat.parse(startDate.replaceAll("T", " ").replaceAll("Z", ""));
				Date newendDate = dateFormat.parse(endDate.replaceAll("T", " ").replaceAll("Z", ""));
	
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("select pm.name,t.count,t.amount from payment_method pm " + 
						"left join (select payment_method,count(*) as count,sum(transaction_amount) as amount from transaction " + 
						"where transaction_type = 1 and transaction_status = 3 and created_date >= ? and created_date <= ? " + 
						"group by payment_method) t on t.payment_method = pm.id;");
				stmt.setTimestamp(1, new java.sql.Timestamp(newstartDate.getTime()));
				stmt.setTimestamp(2, new java.sql.Timestamp(newendDate.getTime()));
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					paymentMethod.append(rs.getString("name") == null ? "-" : "\""+rs.getString("name")+"\",");
					totalCount.append(rs.getInt("count") == 0 ? "0," : ""+rs.getInt("count")+",");
				}
				
				String newPaymentMethod = paymentMethod.substring(0, paymentMethod.length() - 1);
				String newTotalCount = totalCount.substring(0, totalCount.length() - 1);
				jsonData = "{\"paymentMethod\":["+newPaymentMethod+"],\"totalCount\":["+newTotalCount+"]}";
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
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
		return jsonData;
	}
	
	@RequestMapping(value =  "/get_item_summary_charts" , method = { RequestMethod.GET }, headers = "Accept=application/json")
	private String getItemSummaryCharts(@RequestParam String startDate, @RequestParam String endDate, HttpServletRequest request, HttpServletResponse response) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		String jsonData = "";
		StringBuffer item_name = new StringBuffer();
		StringBuffer item_total = new StringBuffer();
		
		try {
			if (user != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				Date newstartDate = dateFormat.parse(startDate.replaceAll("T", " ").replaceAll("Z", ""));
				Date newendDate = dateFormat.parse(endDate.replaceAll("T", " ").replaceAll("Z", ""));
	
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("SELECT menu_item_name, SUM(quantity) as totalitem FROM check_detail " + 
						"WHERE check_detail_status = 3 AND created_date >= ? " + 
						"AND created_date <= ? and parent_check_detail_id is null " + 
						"GROUP BY menu_item_name;");
				stmt.setTimestamp(1, new java.sql.Timestamp(newstartDate.getTime()));
				stmt.setTimestamp(2, new java.sql.Timestamp(newendDate.getTime()));
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					item_name.append(rs.getString("menu_item_name") == null ? "-" : "\""+rs.getString("menu_item_name")+"\",");
					item_total.append(rs.getInt("totalitem") == 0 ? "0," : rs.getInt("totalitem") +",");
				}
				
				String itemNameSubStr = item_name.substring(0, item_name.length() - 1);
				String itemTotalSubStr = item_total.substring(0, item_total.length() - 1);
				jsonData = "{\"item_name\":["+itemNameSubStr+"],\"item_total\":["+itemTotalSubStr+"]}";
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
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
		return jsonData;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/get_total_working_hour" , method = { RequestMethod.POST }, headers = "Accept=application/json")
	private String getTotalWorkingHour(@RequestBody String dataObj, HttpServletRequest request, HttpServletResponse response) throws JSONException, ParseException {
		JSONObject jObjectResult = new JSONObject();
		JSONArray JARY = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		JSONObject jsonObj = new JSONObject(dataObj);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date endDate = dateFormat.parse(jsonObj.getString("endDate"));
		String staff = jsonObj.getString("checkStatus");
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		StringBuffer strSelect = new StringBuffer("select st.staff_name, swh.clock_in, swh.clock_out, swh.created_date from staff_workinghour swh ");
		strSelect.append("left join staff st on (swh.staff_id = st.id) ");
		strSelect.append("where swh.created_date like ? ");
		
		if(!staff.equalsIgnoreCase("")) {
			strSelect.append("and swh.staff_id = ?");
		}
		
		strSelect.append("order by swh.id desc");
		
		try {
			if (user != null) {
				String getDate = String.format("%02d",(endDate.getMonth()+1))+"/"+(endDate.getYear()+1900);
				String param = "%"+getDate+"%";
				
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement(strSelect.toString());
				stmt.setString(1, param);
				if(!staff.equalsIgnoreCase("")) {
					stmt.setString(2, staff);
				}
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					JSONObject jObject = new JSONObject();
					String timeClockIn = rs.getString("clock_in");
					String timeClockOut = rs.getString("clock_out");
					
					jObject.put("staff_name", rs.getString("staff_name") == null ? "-" : rs.getString("staff_name"));
					jObject.put("clock_in", timeClockIn);
					jObject.put("clock_out", timeClockOut);
					jObject.put("date_working", rs.getString("created_date"));
					
					JARY.put(jObject);
				}
				jObjectResult.put("data", JARY);
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
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
		return jObjectResult.toString();
	}
	
	@RequestMapping(value =  "/get_total_staff_sales" , method = { RequestMethod.POST }, headers = "Accept=application/json")
	private String getTotalStaffSales(@RequestBody String dataObj, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jObjectResult = new JSONObject();
		JSONArray JARY = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		StringBuffer strSelect = new StringBuffer("SELECT count(staff_id) as total_sales, st.staff_name as staff_name, round(sum(transaction_amount),2) as total_amount FROM transaction ts ");
		strSelect.append("left join staff st on (ts.staff_id = st.id) ");
		strSelect.append("where ts.created_date >= ? and ts.created_date <= ? group by staff_id");
		
		try {
			if (user != null) {
				JSONObject jsonObj = new JSONObject(dataObj);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				Date startDate = dateFormat.parse(jsonObj.getString("startDate").replaceAll("T", " ").replaceAll("Z", ""));
				Date endDate = dateFormat.parse(jsonObj.getString("endDate").replaceAll("T", " ").replaceAll("Z", ""));
				
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement(strSelect.toString());
				stmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
				stmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					JSONObject jObject = new JSONObject();
					jObject.put("total_sales", rs.getString("total_sales"));
					jObject.put("staff_name", rs.getString("staff_name"));
					jObject.put("total_amount", rs.getString("total_amount"));
					
					JARY.put(jObject);
				}
				jObjectResult.put("data", JARY);
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
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
		return jObjectResult.toString();
	}
	
	@RequestMapping(value =  "/get_overall_sales_performance_staff" , method = { RequestMethod.GET }, headers = "Accept=application/json")
	private String getOverallSalesPerformanceByStaff(@RequestParam String startDate, @RequestParam String endDate, HttpServletRequest request, HttpServletResponse response) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);
		String jsonData = "";
		StringBuffer item_name = new StringBuffer();
		StringBuffer item_total = new StringBuffer();
		
		try {
			if (user != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				Date newstartDate = dateFormat.parse(startDate.replaceAll("T", " ").replaceAll("Z", ""));
				Date newendDate = dateFormat.parse(endDate.replaceAll("T", " ").replaceAll("Z", ""));
	
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("SELECT round(sum(transaction_amount),2) as sales_amount, concat(lpad(day(created_date),2,0),'/',lpad(month(created_date),2,0)) as created_date from transaction " + 
						" where created_date >= ? and created_date <= ? group by day(created_date)");
				stmt.setTimestamp(1, new java.sql.Timestamp(newstartDate.getTime()));
				stmt.setTimestamp(2, new java.sql.Timestamp(newendDate.getTime()));
				rs = stmt.executeQuery();
	
				while (rs.next()) {
					item_name.append(rs.getString("created_date") == null ? "-" : "\""+rs.getString("created_date")+"\",");
					item_total.append(rs.getInt("sales_amount") == 0 ? "0," : rs.getInt("sales_amount") +",");
				}
				
				String itemNameSubStr = item_name.substring(0, item_name.length() - 1);
				String itemTotalSubStr = item_total.substring(0, item_total.length() - 1);
				jsonData = "{\"item_name\":["+itemNameSubStr+"],\"item_total\":["+itemTotalSubStr+"]}";
			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", ECPOS_FOLDER);
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
		return jsonData;
	}
	
	@RequestMapping(value = { "/getStaffDropdownList" }, method = {
			RequestMethod.GET }, produces = "application/json")
	public String getStaffDropdownList(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray dropdownArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;

		WebComponents webComponent = new WebComponents();
		UserAuthenticationModel user = webComponent.getEcposSession(request);

		try {
			if (user != null) {
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("select * from staff where staff_role in (1,2)");
				rs = stmt.executeQuery();

				while (rs.next()) {
					JSONObject obj = new JSONObject();
					obj.put("id", rs.getInt("id"));
					obj.put("name", rs.getString("staff_name"));
					dropdownArray.put(obj);
				}
				jsonResult.put("staff_drop", dropdownArray);

			} else {
				response.setStatus(408);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (stmt3 != null)
					stmt3.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult.toString();
	}
}
