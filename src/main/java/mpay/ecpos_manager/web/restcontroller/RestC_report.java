package mpay.ecpos_manager.web.restcontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
}
