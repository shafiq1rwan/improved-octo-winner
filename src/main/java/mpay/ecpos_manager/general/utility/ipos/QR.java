package mpay.ecpos_manager.general.utility.ipos;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.aspectj.util.FileUtil;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;

@Service
public class QR {

	private static String IPOS_FOLDER = Property.getIPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@Value("${ipos-path}")
	private String iposPath;
	
	@Value("${ipos-exe}")
	private String iposEXE;
	
	@Value("${ipos-demo-exe}")
	private String iposDemoEXE;
	
	//Sales
	public JSONObject qrSalePayment(String storeId, String tranType, BigDecimal amount, String tips, String uniqueTranNumber, String qrContent, JSONObject terminalWifiIPPort) {
		JSONObject jsonResult = new JSONObject();

		try {
			String qrSaleRequest = "\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType
					+ "\\\"," + "\\\"amount\\\":" + "\\\"" + amount + "\\\"," + "\\\"tips\\\":" + "\\\"" + tips
					+ "\\\"," + "\\\"uniqueTranNumber\\\":" + "\\\"" + uniqueTranNumber + "\\\"," + "\\\"qrContent\\\":" + "\\\"" + qrContent + "\\\"";
			
			String terminalWifiIP = "";
			String terminalWifiPort = "";
			
			if (terminalWifiIPPort.length() != 0) {
				terminalWifiIP = terminalWifiIPPort.getString("wifi_IP");
				terminalWifiPort = terminalWifiIPPort.getString("wifi_Port");
				
				qrSaleRequest = qrSaleRequest + "," + "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIP + "\\\"," + "\\\"wifiPort\\\":" + "\\\"" + terminalWifiPort + "\\\"";
			}

			jsonResult = submitIPOS("{"+qrSaleRequest+"}");
			System.out.println("My Result :" + jsonResult.toString());
	
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	//Void
	public JSONObject qrVoid(String storeId, String tranType, String uniqueTranNumber, String traceNumber, JSONObject terminalWifiIPPort) {
		JSONObject jsonResult = new JSONObject();

		try {
			String qrVoidRequest = "\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType
					+ "\\\"," + "\\\"uniqueTranNumber\\\":" + "\\\"" + uniqueTranNumber + "\\\"," + "\\\"traceNumber\\\":" + "\\\"" + traceNumber + "\\\"";
			
			String terminalWifiIP = "";
			String terminalWifiPort = "";
			
			if (terminalWifiIPPort.length() != 0) {
				terminalWifiIP = terminalWifiIPPort.getString("wifi_IP");
				terminalWifiPort = terminalWifiIPPort.getString("wifi_Port");
				
				qrVoidRequest = qrVoidRequest + "," + "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIP + "\\\"," + "\\\"wifiPort\\\":" + "\\\"" + terminalWifiPort + "\\\"";
			}

			jsonResult = submitIPOS("{"+qrVoidRequest+"}");
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	//Refund
	public JSONObject qrRefund(String storeId, String tranType, String amount, String tips, String uniqueTranNumber, String traceNumber, JSONObject terminalWifiIPPort) {
		JSONObject jsonResult = new JSONObject();

		try {
			String qrRefundRequest = "\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType
					+ "\\\"," + "\\\"amount\\\":" + "\\\"" + amount + "\\\"," + "\\\"tips\\\":" + "\\\"" + tips
					+ "\\\"," + "\\\"uniqueTranNumber\\\":" + "\\\"" + uniqueTranNumber + "\\\"," + "\\\"traceNumber\\\":" + "\\\"" + traceNumber + "\\\"";
			
			String terminalWifiIP = "";
			String terminalWifiPort = "";
			
			if (terminalWifiIPPort.length() != 0) {
				terminalWifiIP = terminalWifiIPPort.getString("wifi_IP");
				terminalWifiPort = terminalWifiIPPort.getString("wifi_Port");
				
				qrRefundRequest = qrRefundRequest + "," + "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIP + "\\\"," + "\\\"wifiPort\\\":" + "\\\"" + terminalWifiPort + "\\\"";
			}

			jsonResult = submitIPOS("{"+qrRefundRequest+"}");
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	private JSONObject submitIPOS(String request){
		JSONObject response = new JSONObject();
		try {
			new File(iposPath).mkdirs();
			File iposFile = new File(Paths.get(iposPath, iposEXE).toString());
			File iposDemoFile = new File(Paths.get(iposPath, iposDemoEXE).toString());
			
			String iposOriPath = getClass().getClassLoader().getResource(Paths.get("mpay/ecpos_manager/general/utility/ipos/ipos_application/", iposEXE).toString()).getFile();
			iposOriPath = URLDecoder.decode(iposOriPath, "UTF-8");
			if (iposOriPath.startsWith("file:")) {
				iposOriPath = iposOriPath.substring("file:".length());
			}
			if (iposOriPath.startsWith("/")) {
				iposOriPath = iposOriPath.substring("/".length());
			}
			File iposOriFile = new File(iposOriPath);
			
			String iposDemoOriPath = getClass().getClassLoader().getResource(Paths.get("mpay/ecpos_manager/general/utility/ipos/ipos_application/", iposDemoEXE).toString()).getFile();
			iposDemoOriPath = URLDecoder.decode(iposDemoOriPath, "UTF-8");
			if (iposDemoOriPath.startsWith("file:")) {
				iposDemoOriPath = iposDemoOriPath.substring("file:".length());
			}
			if (iposDemoOriPath.startsWith("/")) {
				iposDemoOriPath = iposDemoOriPath.substring("/".length());
			}
			File iposDemoOriFile = new File(iposDemoOriPath);
			
			if (!iposFile.exists()) {
				FileUtil.copyFile(iposOriFile, iposFile);
			}
			if (!iposDemoFile.exists()) {
				FileUtil.copyFile(iposDemoOriFile, iposDemoFile);
			}
			
			Process executeIPOS = Runtime.getRuntime().exec(new String[] {Paths.get(iposPath, iposEXE).toString(), request});
					
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

}
