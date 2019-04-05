package mpay.ecpos_manager.web.restcontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;

@RestController
@RequestMapping("/rc/report")
public class RestC_report {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@RequestMapping(value =  "/get_sales_summary" , method = { RequestMethod.POST }, headers = "Accept=application/json")
	private String getSalesSummary(@RequestBody String dataObj) {
		JSONObject jObjectResult = new JSONObject();
		JSONArray JARY = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			JSONObject jsonObj = new JSONObject(dataObj);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date startDate = dateFormat.parse(jsonObj.getString("startDate").replaceAll("T", " ").replaceAll("Z", ""));
			Date endDate = dateFormat.parse(jsonObj.getString("endDate").replaceAll("T", " ").replaceAll("Z", ""));

			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("select dt.name as dt,pm,trans.count,trans.amount from device_type dt " + 
					"left join ( " + 
					"select trans.device_type,pm.name as pm,trans.count,trans.amount from payment_method pm " + 
					"left join ( " + 
					"select cd.device_type,t.payment_method,count(t.id) as count,sum(t.transaction_amount) as amount " + 
					"from transaction t " + 
					"inner join `check` c on c.id = t.check_id and c.check_number = t.check_number " + 
					"inner join check_detail cd on cd.check_id = c.id and cd.check_number = c.check_number " + 
					"where t.transaction_type = 1 and t.transaction_status = 3 and t.created_date >= ? and t.created_date <= ? " + 
					"group by cd.device_type,t.payment_method) trans on trans.payment_method = pm.id) trans on trans.device_type = dt.id " + 
					"order by dt.id;");
			stmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
			stmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject jObject = new JSONObject();
				jObject.put("deviceType", rs.getString("dt"));
				jObject.put("paymentMethod", rs.getString("pm") == null ? "-" : rs.getString("pm"));
				jObject.put("totalCount", rs.getInt("count") == 0 ? "-" : rs.getInt("count"));
				jObject.put("totalAmount", rs.getBigDecimal("amount") == null ? "-" : String.format("%.2f", rs.getBigDecimal("amount")));
				
				JARY.put(jObject);
			}
			jObjectResult.put("data", JARY);
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
}
