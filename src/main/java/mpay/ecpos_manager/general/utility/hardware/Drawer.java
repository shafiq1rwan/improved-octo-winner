package mpay.ecpos_manager.general.utility.hardware;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import mpay.ecpos_manager.general.constant.Constant;
import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;

@Service
public class Drawer {

	private static String HARDWARE_FOLDER = Property.getHARDWARE_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@Value("${drawer_exe}")
	private String drawerExe;
	
	public JSONObject openDrawer(String deviceManufacturerName, String portName) {
		JSONObject jsonResult = new JSONObject();
		
		try {
	/*		JSONObject requestJson = new JSONObject();
			requestJson.put("DeviceType", deviceManufacturerName);
			requestJson.put("PortName", portName);*/

			//String request = "{\"DeviceType\":" + deviceManufacturerName + ",\"PortName\": "+ portName +"}";
			
			String request = "\\\"DeviceType\\\":" + "\\\"" + deviceManufacturerName 
					+ "\\\","+ "\\\"PortName\\\":" +"\\\"" + portName + "\\\"";
			
			//JSONObject response = submitDrawerRequest(requestJson.toString());
			JSONObject response = submitDrawerRequest("{"+ request +"}");
			
			if (response.getString("ResponseCode").equals("00")) {
				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, response.getString("ResponseMessage"));
				Logger.writeActivity("OPEN DRAWER SUCCESS", HARDWARE_FOLDER);
			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, response.getString("ResponseMessage"));
				Logger.writeActivity("CLOSE DRAWER FAIL", HARDWARE_FOLDER);
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
			System.out.println(new ClassPathResource("/UniDrawer_v1.0.exe", this.getClass().getClassLoader()).getFile().getAbsolutePath());
			Path path = Paths.get(System.getProperty("user.dir"), drawerExe, "UniDrawer_v1.0.exe");
			Process executeDrawer = Runtime.getRuntime().exec(new String[] {path.toAbsolutePath().toString(), request});
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
		}
		return response;
	}
	
	
	
	
	
	
}
