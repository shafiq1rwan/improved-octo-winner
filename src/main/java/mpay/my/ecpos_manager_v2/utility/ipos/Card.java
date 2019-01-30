package mpay.my.ecpos_manager_v2.utility.ipos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mpay.my.ecpos_manager_v2.constant.Constant;
import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;

@Service
public class Card {

	private static String IPOS_FOLDER = Property.getIPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	private static final String IPOS_PATH = "C:\\IPOS\\ipos.exe ";
	
	public JSONObject pingTest(String tranType, JSONObject terminalWifiIPPort) {
		JSONObject jsonResult = new JSONObject();

		try {
			String pingRequest = "{\\\"tranType\\\":" + "\\\"" + tranType + "\\\"," 
					+ "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIPPort.getString("wifi_IP") + "\\\"," 
					+ "\\\"wifiPort\\\":" + "\\\"" + terminalWifiIPPort.getString("wifi_Port") + "\\\"}";

			JSONObject response = submitIPOS(pingRequest);

			if (response.getString("responseCode").equals("00")) {
				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
				Logger.writeActivity("PING SUCCESS", IPOS_FOLDER);
			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "FAIL");
				Logger.writeActivity("PING FAIL", IPOS_FOLDER);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	public JSONObject cardSalePayment(String storeId, String tranType, BigDecimal amount, String tips, String uniqueTranNumber, JSONObject terminalWifiIPPort) {
		JSONObject jsonResult = new JSONObject();

		try {
			String saleRequest = "{\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType
					+ "\\\"," + "\\\"amount\\\":" + "\\\"" + amount + "\\\"," + "\\\"tips\\\":" + "\\\"" + tips
					+ "\\\"," + "\\\"uniqueTranNumber\\\":" + "\\\"" + uniqueTranNumber + "\\\"," + "\\\"wifiIP\\\":" + "\\\"" 
					+ terminalWifiIPPort.getString("wifi_IP") + "\\\"," + "\\\"wifiPort\\\":" + "\\\"" + terminalWifiIPPort.getString("wifi_Port") + "\\\"}";

			jsonResult = submitIPOS(saleRequest);
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	public JSONObject cardVoidPayment(String storeId, String tranType, String invoiceNo, JSONObject terminalWifiIPPort) {
		JSONObject jsonResult = new JSONObject();

		try {
			String voidRequest = "{\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType + "\\\"," 
					+ "\\\"invoiceNumber\\\":" + "\\\"" + invoiceNo + "\\\"," + "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIPPort.getString("wifi_IP") + "\\\"," 
					+ "\\\"wifiPort\\\":" + "\\\"" + terminalWifiIPPort.getString("wifi_Port") + "\\\"}";

			jsonResult = submitIPOS(voidRequest);
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	public JSONObject cardSettlement(String storeId, String tranType, String niiName, JSONObject terminalWifiIPPort) {
		JSONObject jsonResult = new JSONObject();

		try {
			String settlementRequest = "{\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType + "\\\"," 
					+ "\\\"niiName\\\":" + "\\\"" + niiName + "\\\"," + "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIPPort.getString("wifi_IP") + "\\\"," 
					+ "\\\"wifiPort\\\":" + "\\\"" + terminalWifiIPPort.getString("wifi_Port") + "\\\"}";

			jsonResult = submitIPOS(settlementRequest);
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	public JSONObject submitIPOS(String request){
		JSONObject response = new JSONObject();
		
		try {
			Process executeIPOS = Runtime.getRuntime().exec(IPOS_PATH + request);
			executeIPOS.wait(150000);
			BufferedReader input = new BufferedReader(new InputStreamReader(executeIPOS.getInputStream()));
			
			StringBuilder responseString = new StringBuilder();

			while (input.readLine() != null) {
				responseString.append(input.readLine());
			}
	
			if (responseString.toString().contains("[IPOS-RESPONSE]")) {
				response = new JSONObject(responseString.toString().substring(responseString.toString().indexOf("[IPOS-RESPONSE]")+1));
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		return response;
	}
}
