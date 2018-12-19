package mpay.my.ecpos_manager_v2.restcontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mpay.my.ecpos_manager_v2.constant.Constant;
import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;

@RestController
@RequestMapping("/rc/tableorder")
public class RestC_table_order {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@RequestMapping(value = { "/get_table_list" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public String getTablelist() {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("SELECT store_table_count FROM store;");
			rs = stmt.executeQuery();

			if (rs.next()) {
				int table_count = rs.getInt("store_table_count");
				
				JSONArray tableList = new JSONArray();
				for (int i = 0; i < table_count; i++) {

					stmt = connection.prepareStatement("SELECT COUNT(*) AS count FROM `check` WHERE table_number = ? AND check_status IN (1,2)");
					stmt.setInt(1, i + 1);
					rs2 = stmt.executeQuery();
					
					if (rs2.next()) {
						String data = Integer.toString(i + 1) + "," + rs2.getString("count");
						tableList.put(data);
					}
				}
				jsonResult.put(Constant.TABLE_LIST, tableList);
				Logger.writeActivity("Table List: " + tableList.toString(), ECPOS_FOLDER);
				
				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
			} else {
				Logger.writeActivity("Table List Not Found", ECPOS_FOLDER);
				
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "NO TABLE FOUND, PLEASE TRY AGAIN");
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
				rs = (ResultSet) stmt.executeQuery();

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
}
