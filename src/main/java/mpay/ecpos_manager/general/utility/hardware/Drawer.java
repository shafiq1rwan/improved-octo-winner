package mpay.ecpos_manager.general.utility.hardware;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mpay.ecpos_manager.general.constant.Constant;
import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;

@Service
public class Drawer {

	private static String HARDWARE_FOLDER = Property.getHARDWARE_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	public JSONObject openDrawer(String deviceManufacturerName, String portName) {
		JSONObject jsonResult = new JSONObject();
		
		try {
			String request = "\\\"DeviceType\\\":" + "\\\"" + deviceManufacturerName 
					+ "\\\","+ "\\\"PortName\\\":" +"\\\"" + portName + "\\\"";
			
			//JSONObject response = submitDrawerRequest(requestJson.toString());
			JSONObject response = submitDrawerRequest("{"+ request +"}");
			
			if (response.getString("ResponseCode").equals("00")) {
				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, response.getString("ResponseMessage"));
				Logger.writeActivity("OPEN DRAWER SUCCESS", HARDWARE_FOLDER);
			} 
			else if(response.getString("ResponseCode").equals("02")) {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, response.getString("ResponseMessage"));
			}
			else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, response.getString("ResponseMessage"));
				Logger.writeActivity(response.getString("ResponseMessage"), HARDWARE_FOLDER);
			}
		} catch(Exception e) {
			Logger.writeError(e, "Exception: ", HARDWARE_FOLDER);
			e.printStackTrace();
		}		
		return jsonResult;
	}
	
	private JSONObject submitDrawerRequest(String request) {
		JSONObject response = new JSONObject();
		
		try {
			String drawerPath = getClass().getClassLoader().getResource("mpay/ecpos_manager/general/utility/hardware/UniDrawer_v1.0.exe").getFile();
			drawerPath = URLDecoder.decode(drawerPath, "UTF-8");
			if (drawerPath.startsWith("/")) {
				drawerPath.substring(1);
			}
			
			Process executeDrawer = Runtime.getRuntime().exec(new String[] {drawerPath, request});
			executeDrawer.waitFor();
			
			BufferedReader input = new BufferedReader(new InputStreamReader(executeDrawer.getInputStream()));
			
			StringBuilder responseString = new StringBuilder();
			String line;
			while ((line = input.readLine()) != null) {
				responseString.append(line);
				System.out.println(responseString);
			}
			input.close();
			
			if (responseString.toString().contains("[UNIDRAWER-RESPONSE]")) {
				response = new JSONObject(responseString.toString().substring(responseString.toString().indexOf("{")));
			}	
		} catch(Exception e) {
			Logger.writeError(e, "Exception: ", HARDWARE_FOLDER);
			e.printStackTrace();
			try {
				response.put("ResponseCode", "02");
				response.put("ResponseMessage", "Exception Occured While Retrieving UniDrawer.exe");
			} catch (JSONException e1) {
			}
		}
		return response;
	}

}
