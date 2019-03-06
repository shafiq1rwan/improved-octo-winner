package mpay.my.ecpos_manager_v2.devicecall;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;

@RestController
@RequestMapping("/device")
public class RestC_devicecall {
	
	private static String DEVICECALL_FOLDER = Property.getDEVICECALL_FOLDER_NAME();
	
	@Autowired
	DeviceCall deviceCall;

	@RequestMapping(value = { "/order/checking" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String orderCheck(@RequestBody String data) {
		JSONObject jsonResult = new JSONObject();
		
		try {
			JSONArray jsonData = new JSONArray(data);
			
			jsonResult = deviceCall.checkOrderItem(jsonData);
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		}
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/order/submit" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String orderOrder(@RequestBody String data) {
		JSONObject jsonResult = new JSONObject();
		
		String responseCode = "E01";
		String responseMessage = "Server error. Please try again later.";
		
		try {
			JSONArray jsonData = new JSONArray(data);
			
			jsonResult = deviceCall.checkOrderItem(jsonData);
			
			if (jsonResult.getString("responseCode").equals("00")) {
				
			} else {
				responseCode = jsonResult.getString("responseCode");
				responseMessage = "Checking failed (" + jsonResult.getString("responseMessage") + ")";
			}
			jsonResult.put("resultCode", responseCode);
			jsonResult.put("resultMessage", responseMessage);
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		}
		return jsonResult.toString();
	}
}
