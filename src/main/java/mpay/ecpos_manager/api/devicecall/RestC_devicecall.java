package mpay.ecpos_manager.api.devicecall;

import java.net.InetAddress;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.DeviceClientWs;
import mpay.ecpos_manager.general.utility.SecureHash;

@RestController
@RequestMapping("/device")
public class RestC_devicecall {
	
	private static String DEVICECALL_FOLDER = Property.getDEVICECALL_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	DeviceCall deviceCall;
	
	@Value("${websocket-kds-byod}")
	private String websocketKdsByod;
	
	@RequestMapping(value = { "/order/checking" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String orderCheck(@RequestBody String data) {
		Logger.writeActivity("----------- DEVICE CALLING ORDER CHECKING START ---------", DEVICECALL_FOLDER);
		Logger.writeActivity("request: " + data, DEVICECALL_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		
		String responseCode = "E01";
		String responseMessage = "Server error. Please try again later.";
		
		try {
			JSONObject jsonData = new JSONObject(data);
			
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			
			if ((jsonData.has("checkNumber") && !jsonData.isNull("checkNumber"))
					&& (jsonData.has("hashData") && !jsonData.isNull("hashData") && !jsonData.getString("hashData").isEmpty()) 
					&& (jsonData.has("order") && !jsonData.isNull("order") && jsonData.getJSONArray("order").length() > 0)) {
				String newHashData = SecureHash.generateSecureHash("SHA-256", "CheckOrder".concat(jsonData.getJSONArray("order").toString().concat(jsonData.getString("checkNumber"))));

				if (newHashData.equals(jsonData.getString("hashData"))) {
					JSONObject getCheck = new JSONObject();
					
					if (jsonData.getString("checkNumber").isEmpty()) {
						getCheck.put("resultCode", "00");
						getCheck.put("resultMessage", "Empty check number received");
					} else {
						getCheck = deviceCall.getCheck(connection, jsonData.getString("checkNumber"), "", 0);	
					}
					
					if (getCheck.getString("resultCode").equals("00")) {
						jsonResult = deviceCall.checkOrderItem(connection, jsonData.getJSONArray("order"));
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
			connection.setAutoCommit(true);
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("----------- DEVICE CALLING ORDER CHECKING END ---------", DEVICECALL_FOLDER);
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/order/submit" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String orderOrder(@RequestBody String data) {
		Logger.writeActivity("----------- DEVICE CALLING ORDER SUBMIT START ---------", DEVICECALL_FOLDER);
		Logger.writeActivity("request: " + data, DEVICECALL_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		
		String responseCode = "E01";
		String responseMessage = "Server error. Please try again later.";
		
		try {
			JSONObject jsonData = new JSONObject(data);
			
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			
			if ((jsonData.has("deviceType") && !jsonData.isNull("deviceType") && !jsonData.getString("deviceType").isEmpty())
					&& (jsonData.has("orderType") && !jsonData.isNull("orderType") && jsonData.getInt("orderType") > 0)
					&& (jsonData.has("checkNumber") && !jsonData.isNull("checkNumber"))
					&& (jsonData.has("hashData") && !jsonData.isNull("hashData") && !jsonData.getString("hashData").isEmpty()) 
					&& (jsonData.has("tableNumber") && !jsonData.isNull("tableNumber"))
					&& (jsonData.has("order") && !jsonData.isNull("order") && jsonData.getJSONArray("order").length() > 0)) {
				if (!(jsonData.getString("deviceType").equals("byod") && !jsonData.getString("checkNumber").isEmpty() && !jsonData.getString("tableNumber").isEmpty())
						&& !(jsonData.getString("deviceType").equals("kiosk") && jsonData.getString("checkNumber").isEmpty() && jsonData.getString("tableNumber").isEmpty())) {
					responseCode = "EA6";
					responseMessage = "Device Type Not Match (Check No & Table No Not Empty)";
					Logger.writeActivity(responseCode + ": " + responseMessage, DEVICECALL_FOLDER);
					
					jsonResult.put("resultCode", responseCode);
					jsonResult.put("resultMessage", responseMessage);
					return jsonResult.toString();
				}
				
				String newHashData = SecureHash.generateSecureHash("SHA-256", "SendOrder".concat(jsonData.getJSONArray("order").toString().concat(jsonData.getString("checkNumber").concat(jsonData.getString("tableNumber")))));

				if (newHashData.equals(jsonData.getString("hashData"))) {
					JSONObject getCheck = new JSONObject();
					
					if (jsonData.getString("checkNumber").isEmpty()) {
						getCheck = deviceCall.createCheck(connection, jsonData.getInt("orderType"));
					} else {
						getCheck = deviceCall.getCheck(connection, jsonData.getString("checkNumber"), jsonData.getString("tableNumber"), jsonData.getInt("orderType"));
					}
					
					if (getCheck.getString("resultCode").equals("00")) {
						JSONObject checkOrder = deviceCall.checkOrderItem(connection, jsonData.getJSONArray("order"));
						Logger.writeActivity(checkOrder.getString("resultCode") + ": " + checkOrder.getString("resultMessage"), DEVICECALL_FOLDER);
						
						if (checkOrder.getString("resultCode").equals("00")) {
							jsonResult = deviceCall.submitOrderItem(connection, getCheck.getLong("checkId"), getCheck.getString("checkNo"), jsonData.getString("deviceType"), jsonData.getJSONArray("order"));
							Logger.writeActivity(jsonResult.getString("resultCode") + ": " + jsonResult.getString("resultMessage"), DEVICECALL_FOLDER);
							
							if (jsonResult.getString("resultCode").equals("00")) {
								connection.commit();
								
								//start send order to kitchen
								JSONObject jsonObj = deviceCall.sendOrderToKitchen(connection, jsonData);
								System.out.println("websocketKdsByod = " + websocketKdsByod);
								DeviceClientWs d = new DeviceClientWs( new URI(websocketKdsByod));
								d.setJsonOrder(jsonObj);
								d.connect();
								d.close();
								//end send order to kitchen
								
								jsonResult.put("checkNo", getCheck.getString("checkNo"));
							}
						} else {
							jsonResult = new JSONObject(checkOrder.toString());
							Logger.writeActivity(jsonResult.getString("resultCode") + ": " + jsonResult.getString("resultMessage"), DEVICECALL_FOLDER);
						}
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
			connection.setAutoCommit(true);
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("----------- DEVICE CALLING ORDER SUBMIT END ---------", DEVICECALL_FOLDER);
		return jsonResult.toString();
	}
	
	@RequestMapping(value = { "/order/info" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String orderInfo(@RequestBody String data) {
		Logger.writeActivity("----------- DEVICE CALLING ORDER INFO START ---------", DEVICECALL_FOLDER);
		Logger.writeActivity("request: " + data, DEVICECALL_FOLDER);
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		
		String responseCode = "E01";
		String responseMessage = "Server error. Please try again later.";
		
		try {
			JSONObject jsonData = new JSONObject(data);
			
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			
			if ((jsonData.has("checkNumber") && !jsonData.isNull("checkNumber"))
					&& (jsonData.has("hashData") && !jsonData.isNull("hashData") && !jsonData.getString("hashData").isEmpty())) {
				String newHashData = SecureHash.generateSecureHash("SHA-256", "OrderInfo".concat(jsonData.getString("checkNumber")));

				if (newHashData.equals(jsonData.getString("hashData"))) {
					if (jsonData.getString("checkNumber").isEmpty()) {
						jsonResult.put("resultCode", "00");
						jsonResult.put("resultMessage", "Empty check number received");
					} else {
						jsonResult = deviceCall.getOrderInfo(connection, jsonData.getString("checkNumber"));
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
			connection.setAutoCommit(true);
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", DEVICECALL_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", DEVICECALL_FOLDER);
				e.printStackTrace();
			}
		}
		Logger.writeActivity("----------- DEVICE CALLING ORDER INFO END ---------", DEVICECALL_FOLDER);
		return jsonResult.toString();
	}
}
