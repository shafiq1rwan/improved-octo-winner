package mpay.ecpos_manager.api.devicecall;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.SecureHash;

@RestController
@RequestMapping("/device")
public class RestC_devicecall {
	
	private static String DEVICECALL_FOLDER = Property.getDEVICECALL_FOLDER_NAME();
	
	@Autowired
	DeviceCall deviceCall;

	@RequestMapping(value = { "/order/checking" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String orderCheck(@RequestBody String data) {
		Logger.writeActivity("----------- DEVICE CALLING ORDER CHECKING START ---------", DEVICECALL_FOLDER);
		Logger.writeActivity("request: " + data, DEVICECALL_FOLDER);
		JSONObject jsonResult = new JSONObject();
		
		String responseCode = "E01";
		String responseMessage = "Server error. Please try again later.";
		
		try {
			JSONObject jsonData = new JSONObject(data);
			
			if (jsonData.has("checkNumber") && jsonData.has("hashData") && jsonData.has("order")) {
				String newHashData = SecureHash.generateSecureHash("SHA-256", "CheckOrder".concat(jsonData.getJSONArray("order").toString().concat(jsonData.getString("checkNumber"))));
				
				if (newHashData.equals(jsonData.getString("hashData"))) {
					JSONObject getCheck = deviceCall.getCheck(jsonData.getString("checkNumber"));
					
					if (getCheck.getString("resultCode").equals("00")) {
						jsonResult = deviceCall.checkOrderItem(jsonData.getJSONArray("order"));
						Logger.writeActivity(jsonResult.getString("resultCode") + ": " + jsonResult.getString("resultMessage"), DEVICECALL_FOLDER);
					} else {
						jsonResult = new JSONObject(getCheck.toString());
						Logger.writeActivity(jsonResult.getString("resultCode") + ": " + jsonResult.getString("resultMessage"), DEVICECALL_FOLDER);
					}
				} else {
					responseCode = "EA2";
					responseMessage = "SecureHash Not Match";
					Logger.writeActivity(responseCode + ": " + responseMessage, DEVICECALL_FOLDER);
					
					jsonResult.put("resultCode", responseCode);
					jsonResult.put("resultMessage", responseMessage);
				}
			} else {
				responseCode = "EA1";
				responseMessage = "Request Received Not Complete";
				Logger.writeActivity(responseCode + ": " + responseMessage, DEVICECALL_FOLDER);
				
				jsonResult.put("resultCode", responseCode);
				jsonResult.put("resultMessage", responseMessage);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		}
		Logger.writeActivity("----------- DEVICE CALLING ORDER CHECKING END ---------", DEVICECALL_FOLDER);
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/order/submit" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String orderOrder(@RequestBody String data) {
		Logger.writeActivity("----------- DEVICE CALLING ORDER SUBMIT START ---------", DEVICECALL_FOLDER);
		Logger.writeActivity("request: " + data, DEVICECALL_FOLDER);
		JSONObject jsonResult = new JSONObject();
		
		String responseCode = "E01";
		String responseMessage = "Server error. Please try again later.";
		
		try {
			JSONArray jsonData = new JSONArray(data);
			
			jsonResult = deviceCall.checkOrderItem(jsonData);
			
			if (jsonResult.getString("resultCode").equals("00")) {
//To Do				
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
		Logger.writeActivity("----------- DEVICE CALLING ORDER SUBMIT END ---------", DEVICECALL_FOLDER);
		return jsonResult.toString();
	}
}
