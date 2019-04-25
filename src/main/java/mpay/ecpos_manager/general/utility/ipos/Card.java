package mpay.ecpos_manager.general.utility.ipos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import mpay.ecpos_manager.general.constant.Constant;
import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;

@Service
public class Card {

	private static String IPOS_FOLDER = Property.getIPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@Value("${ipos_exe}")
	private String iposExe;
	
	public JSONObject pingTest(String tranType, JSONObject terminalWifiIPPort) {
		JSONObject jsonResult = new JSONObject();

		try {
			String pingRequest = "\\\"tranType\\\":" + "\\\"" + tranType + "\\\"";
			
			String terminalWifiIP = "";
			String terminalWifiPort = "";
			
			if (terminalWifiIPPort.length() != 0) {
				terminalWifiIP = terminalWifiIPPort.getString("wifi_IP");
				terminalWifiPort = terminalWifiIPPort.getString("wifi_Port");
				
				pingRequest = pingRequest + "," + "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIP + "\\\"," + "\\\"wifiPort\\\":" + "\\\"" + terminalWifiPort + "\\\"";
			}
			
			JSONObject response = submitIPOS("{"+pingRequest+"}");
			
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
	
	public JSONObject cardSalePayment(String storeId, String tranType, BigDecimal amount, String tips, String uniqueTranNumber, JSONObject terminalWifiIPPort, WebSocketSession session) {
		JSONObject jsonResult = new JSONObject();

		try {
			String saleRequest = "\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType
					+ "\\\"," + "\\\"amount\\\":" + "\\\"" + amount + "\\\"," + "\\\"tips\\\":" + "\\\"" + tips
					+ "\\\"," + "\\\"uniqueTranNumber\\\":" + "\\\"" + uniqueTranNumber + "\\\"";
			
			String terminalWifiIP = "";
			String terminalWifiPort = "";
			
			if (terminalWifiIPPort.length() != 0) {
				terminalWifiIP = terminalWifiIPPort.getString("wifi_IP");
				terminalWifiPort = terminalWifiIPPort.getString("wifi_Port");
				
				saleRequest = saleRequest + "," + "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIP + "\\\"," + "\\\"wifiPort\\\":" + "\\\"" + terminalWifiPort + "\\\"";
			}

			if(session!=null) {
				jsonResult = submitIPOS("{"+saleRequest+"}", session);
			} else {
				//jsonResult = submitIPOS("{"+saleRequest+"}");
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	public JSONObject cardVoidPayment(String storeId, String tranType, String invoiceNo, JSONObject terminalWifiIPPort) {
		JSONObject jsonResult = new JSONObject();

		try {
			String voidRequest = "\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType + "\\\"," 
					+ "\\\"invoiceNumber\\\":" + "\\\"" + invoiceNo + "\\\"";
			
			String terminalWifiIP = "";
			String terminalWifiPort = "";
			
			if (terminalWifiIPPort.length() != 0) {
				terminalWifiIP = terminalWifiIPPort.getString("wifi_IP");
				terminalWifiPort = terminalWifiIPPort.getString("wifi_Port");
				
				voidRequest = voidRequest + "," + "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIP + "\\\"," + "\\\"wifiPort\\\":" + "\\\"" + terminalWifiPort + "\\\"";
			}

			jsonResult = submitIPOS("{"+voidRequest+"}");
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	public JSONObject cardSettlement(long settlementId, String storeId, String tranType, String niiName, JSONObject terminalWifiIPPort) {
		JSONObject jsonResult = new JSONObject();
		
		try {
			String settlementRequest = "\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType + "\\\"," 
					+ "\\\"niiName\\\":" + "\\\"" + niiName + "\\\"";
			
			String terminalWifiIP = "";
			String terminalWifiPort = "";
			
			if (terminalWifiIPPort.length() != 0) {
				terminalWifiIP = terminalWifiIPPort.getString("wifi_IP");
				terminalWifiPort = terminalWifiIPPort.getString("wifi_Port");
				
				settlementRequest = settlementRequest + "," + "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIP + "\\\"," + "\\\"wifiPort\\\":" + "\\\"" + terminalWifiPort + "\\\"";
			}

			jsonResult = submitIPOS("{"+settlementRequest+"}");
			jsonResult.put("settlementId", settlementId);
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	public JSONObject submitIPOS(String request){
		JSONObject response = new JSONObject();
		
		try {
			Process executeIPOS = Runtime.getRuntime().exec(iposExe + " " + request);
			//executeIPOS.waitFor();
			if(!executeIPOS.waitFor(2, TimeUnit.MINUTES)) {
				//destroy the process if exceed  timeout
				executeIPOS.destroyForcibly();
				System.out.println("terminate ipos process");
			}
			
			BufferedReader input = new BufferedReader(new InputStreamReader(executeIPOS.getInputStream()));
			
			StringBuilder responseString = new StringBuilder();

			String line;
			while ((line = input.readLine()) != null) {
				responseString.append(line);
				System.out.println(responseString);
			}
			
			input.close();
	
			if (responseString.toString().contains("[IPOS-RESPONSE]")) {
				response = new JSONObject(responseString.toString().substring(responseString.toString().indexOf("[IPOS-RESPONSE]")+15));
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		return response;
	}
	
	//websocket variant
	public JSONObject submitIPOS(String request, WebSocketSession session){
		JSONObject response = new JSONObject();
		
		try {
			Process executeIPOS = Runtime.getRuntime().exec(iposExe + " " + request);

			BufferedReader input = new BufferedReader(new InputStreamReader(executeIPOS.getInputStream()));

			String line;
			String iposResponseMessage = "";
			while ((line = input.readLine()) != null) {
				 System.out.println(line);
				 session.sendMessage(new TextMessage(line));
				 
				 if(line.contains("[IPOS-RESPONSE]")) {
					 iposResponseMessage = line;
				 }
			}
			
			//executeIPOS.waitFor();
			if(!executeIPOS.waitFor(2, TimeUnit.MINUTES)) {
				//destroy the process if exceed  timeout
				executeIPOS.destroyForcibly();
				System.out.println("terminate ipos process");
			}
			input.close();

			System.out.println("last line :" + iposResponseMessage);
			
			JSONObject jsonData = new JSONObject(iposResponseMessage.replace("[IPOS-RESPONSE]", ""));
			if(!jsonData.isNull("cardResponse")) {
				response = jsonData;
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		
		System.out.println("IPOS result: " + response.toString());
		return response;
	}
}
