package mpay.ecpos_manager.general.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.sql.Connection;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;

public class VirtualMPOSConnection {
	
	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	private static String VMPOS_FOLDER = Property.getVMPOS_FOLDER_NAME();
	
	public static JSONObject virtualMPOSConnection(JSONObject toMPOS, String mpos_url, String action, DataSource dataSource) {
		JSONObject response = new JSONObject();			
		HttpURLConnection conn = null;
		Connection dbconn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			/*String mpos_url = null;
			String query = "SELECT * FROM general_configuration WHERE parameter = ?;";
			dbconn = dataSource.getConnection();
			pstmt = dbconn.prepareStatement(query);
			pstmt.setString(1, "Terminal");
			rs = pstmt.executeQuery();
			if(rs.next()){
				mpos_url = rs.getString("value");
			}*/
			//mpos_url = "http://uatmdex.mpay.my/virtualmpos/api/qrcontroller/";
			//mpos_url = "https://mpaypayment.mpay.my/virtualmpos/api/qrcontroller/";
			Logger.writeActivity("Starting connection.....", ECPOS_FOLDER);
			Logger.writeActivity("Starting connection.....", VMPOS_FOLDER);
			mpos_url = mpos_url+action;		
			URL url = new URL(mpos_url);
			Logger.writeActivity(mpos_url, ECPOS_FOLDER);
			Logger.writeActivity(mpos_url, VMPOS_FOLDER);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			conn.connect();
			
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(toMPOS.toString());
			out.close();
			
			if(conn.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR)
			{
				Logger.writeActivity("Connection failed", ECPOS_FOLDER);
				Logger.writeActivity("Connection failed", VMPOS_FOLDER);
			}

			else if(conn.getResponseCode() != HttpURLConnection.HTTP_OK)
			{	
				Logger.writeActivity("Connection error", ECPOS_FOLDER);	
				Logger.writeActivity("Connection error", VMPOS_FOLDER);	
				Logger.writeActivity(String.valueOf(conn.getResponseCode()), ECPOS_FOLDER);
				Logger.writeActivity(String.valueOf(conn.getResponseCode()), VMPOS_FOLDER);	
			}
			else
			{
				Logger.writeActivity("Connection successful", ECPOS_FOLDER);
				Logger.writeActivity("Connection successful", VMPOS_FOLDER);
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			String MPOS_respond_msg = "";
			
			while ((output = br.readLine()) != null)
			{			
				MPOS_respond_msg = MPOS_respond_msg + output;
			}
			response = new JSONObject(MPOS_respond_msg);
			
		} catch (MalformedURLException e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			Logger.writeError(e, "Exception: ", VMPOS_FOLDER);
	        e.printStackTrace();  
		} catch (IOException e) {  
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			Logger.writeError(e, "Exception: ", VMPOS_FOLDER);
			e.printStackTrace();  
		} catch (JSONException e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			Logger.writeError(e, "Exception: ", VMPOS_FOLDER);
		    e.printStackTrace();
		}catch(Exception e){
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			Logger.writeError(e, "Exception: ", VMPOS_FOLDER);
		    e.printStackTrace();
		}finally{  
		    if(conn!=null)  
		    conn.disconnect();  
		    try { if (pstmt != null) pstmt.close(); } catch (Exception e) { e.printStackTrace(); };
		    try { if (dbconn != null) dbconn.close(); } catch (Exception e) { e.printStackTrace(); };
		    try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); };
		}  
		return response;
	}
}


