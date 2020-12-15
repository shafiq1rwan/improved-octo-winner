package mpay.ecpos_manager.general.utility.ipos;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.aspectj.util.FileUtil;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.SecureHash;
import mpay.ecpos_manager.general.utility.VirtualMPOSConnection;

@Service
public class QR {

	private static String IPOS_FOLDER = Property.getIPOS_FOLDER_NAME();
	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@Autowired
	DataSource dataSource;
	
	@Value("${ipos-path}")
	private String iposPath;
	
	@Value("${ipos-exe}")
	private String iposEXE;
	
	@Value("${ipos-demo-exe}")
	private String iposDemoEXE;
	
	//Sales
	public JSONObject qrSalePayment(String storeId, String tranType, BigDecimal amount, String tips, 
			String uniqueTranNumber, String qrContent, JSONObject qrPaymentDetails, boolean isIposQR) {
		JSONObject jsonResult = new JSONObject();
		
		//boolean isIPOS = qrPaymentDetails.getString("id").equals("1");
		
		try {
			
			if (isIposQR) {
				String qrSaleRequest = "\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType
						+ "\\\"," + "\\\"amount\\\":" + "\\\"" + amount + "\\\"," + "\\\"tips\\\":" + "\\\"" + tips
						+ "\\\"," + "\\\"uniqueTranNumber\\\":" + "\\\"" + uniqueTranNumber + "\\\"," + "\\\"qrContent\\\":" + "\\\"" + qrContent + "\\\"";
				
				String terminalWifiIP = "";
				String terminalWifiPort = "";
				
				if (qrPaymentDetails.length() != 0) {
					terminalWifiIP = qrPaymentDetails.getString("wifi_IP");
					terminalWifiPort = qrPaymentDetails.getString("wifi_Port");
					
					qrSaleRequest = qrSaleRequest + "," + "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIP + "\\\"," + "\\\"wifiPort\\\":" + "\\\"" + terminalWifiPort + "\\\"";
				}

				jsonResult = submitIPOS("{"+qrSaleRequest+"}");
				System.out.println("My Result :" + jsonResult.toString());
				
			} else {
				
				//production details
				//String mpay_tid = "013678-3-005";
				//String product_desc = "Mpay-Testing";
				//String project_key = "DFMY";
				
				//uat details
				//String mpay_tid = "007889-3-006";
				//String product_desc = "Grab-Testing-50";
				//String project_key = "MBOC";
				
				String mpay_tid = qrPaymentDetails.getString("tid");
				String product_desc = qrPaymentDetails.getString("product_desc");
				String project_key = qrPaymentDetails.getString("project_key");
				
				String reference_code = qrPaymentDetails.getString("uuid")+"-"+new SimpleDateFormat("yyyyMMddHHmm").format(new Date())+"-001";
				amount = amount.setScale(1, BigDecimal.ROUND_UP);
				
				JSONObject toMPos = new JSONObject();
				toMPos.put("mpay_tid", mpay_tid);
				toMPos.put("product_desc", product_desc);
				toMPos.put("total_amount", amount.toString());
				toMPos.put("qr_content", qrContent);
				toMPos.put("transaction_reference_code", reference_code);
				String hashValues = new StringBuffer("VM_"+project_key)
						.append(mpay_tid)
						.append(reference_code)
						.append(amount).toString();
				toMPos.put("secure_hash", SecureHash.vmposGenSecureHash(hashValues));
				toMPos.put("uuid", qrPaymentDetails.getString("uuid"));
				
				System.out.println("json val: "+toMPos.toString());
				
				Logger.writeActivity("doQRSale | mpay_tid:"+toMPos.get("mpay_tid"), ECPOS_FOLDER);
				Logger.writeActivity("doQRSale | product_desc:"+toMPos.get("product_desc"), ECPOS_FOLDER);
				Logger.writeActivity("doQRSale | total_amount:"+toMPos.get("total_amount"), ECPOS_FOLDER);
				Logger.writeActivity("doQRSale | qr_content:"+toMPos.get("qr_content"), ECPOS_FOLDER);
				Logger.writeActivity("doQRSale | reference_code:"+toMPos.get("transaction_reference_code"), ECPOS_FOLDER);
				Logger.writeActivity("doQRSale | secure_hash:"+toMPos.get("secure_hash"), ECPOS_FOLDER);
				Logger.writeActivity("doQRSale | uuid:"+toMPos.get("uuid"), ECPOS_FOLDER);
				
				jsonResult = VirtualMPOSConnection.virtualMPOSConnection(toMPos, 
						qrPaymentDetails.getString("url").replaceAll("////",""), "doQRSale", dataSource);
				jsonResult.put("storeId", storeId);
				jsonResult.put("tranType", tranType);
				jsonResult.put("tips", tips);
				jsonResult.put("uniqueTranNumber", uniqueTranNumber);
				jsonResult.put("transaction_reference_code", toMPos.getString("transaction_reference_code"));
				System.out.println("My Result :" + jsonResult.toString());
			}
	
		} catch (Exception e) {
			if (isIposQR) {
				Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			} else {
				Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			}
			e.printStackTrace();
		}
		return jsonResult;
	}
	
	//Void
	public JSONObject qrVoid(String storeId, String tranType, String uniqueTranNumber, String traceNumber, 
			JSONObject qrPaymentDetails, boolean isIposQR) {
		JSONObject jsonResult = new JSONObject();
		
		try {
			
			if (isIposQR) {
				String qrVoidRequest = "\\\"storeID\\\":" + "\\\"" + storeId + "\\\"," + "\\\"tranType\\\":" + "\\\"" + tranType
						+ "\\\"," + "\\\"uniqueTranNumber\\\":" + "\\\"" + uniqueTranNumber + "\\\"," + "\\\"traceNumber\\\":" + "\\\"" + traceNumber + "\\\"";
				
				String terminalWifiIP = "";
				String terminalWifiPort = "";
				
				if (qrPaymentDetails.length() != 0) {
					terminalWifiIP = qrPaymentDetails.getString("wifi_IP");
					terminalWifiPort = qrPaymentDetails.getString("wifi_Port");
					
					qrVoidRequest = qrVoidRequest + "," + "\\\"wifiIP\\\":" + "\\\"" + terminalWifiIP + "\\\"," + "\\\"wifiPort\\\":" + "\\\"" + terminalWifiPort + "\\\"";
				}

				jsonResult = submitIPOS("{"+qrVoidRequest+"}");
			} else {
				//production details
				//String mpay_tid = "013678-3-005";
				//String project_key = "DFMY";
				
				//uat details
				//String mpay_tid = "007889-3-006";
				//String project_key = "MBOC";
				
				String mpay_tid = qrPaymentDetails.getString("tid");
				String project_key = qrPaymentDetails.getString("project_key");
				
				String qr_transaction_id = qrPaymentDetails.getString("qr_transaction_id");
				String mpay_transaction_id = qrPaymentDetails.getString("mpay_transaction_id");
				String reference_code = qrPaymentDetails.getString("transaction_reference_code");
				
				JSONObject toMPos = new JSONObject();
				toMPos.put("mpay_tid", mpay_tid);
				toMPos.put("transaction_reference_code", reference_code);
				toMPos.put("total_amount", qrPaymentDetails.getString("amount"));
				toMPos.put("mpay_transaction_id", mpay_transaction_id);
				toMPos.put("qr_transaction_id", qr_transaction_id);
				StringBuffer hashValues = new StringBuffer();
				hashValues.append("VM_"+project_key)
						.append(mpay_tid)
						.append(reference_code)
						.append(qrPaymentDetails.getString("amount"));
				toMPos.put("secure_hash", SecureHash.vmposGenSecureHash(hashValues.toString()));
				toMPos.put("uuid", qrPaymentDetails.getString("uuid"));
				
				Logger.writeActivity("doQRCancel | mpay_tid:"+toMPos.get("mpay_tid"), ECPOS_FOLDER);
				Logger.writeActivity("doQRCancel | total_amount:"+toMPos.get("total_amount"), ECPOS_FOLDER);
				Logger.writeActivity("doQRCancel | mpay_transaction_id:"+toMPos.get("mpay_transaction_id"), ECPOS_FOLDER);
				Logger.writeActivity("doQRCancel | reference_code:"+toMPos.get("transaction_reference_code"), ECPOS_FOLDER);
				Logger.writeActivity("doQRCancel | qr_transaction_id:"+toMPos.get("qr_transaction_id"), ECPOS_FOLDER);
				Logger.writeActivity("doQRCancel | secure_hash:"+toMPos.get("secure_hash"), ECPOS_FOLDER);
				Logger.writeActivity("doQRCancel | uuid:"+toMPos.get("uuid"), ECPOS_FOLDER);
				
				jsonResult = VirtualMPOSConnection.virtualMPOSConnection(toMPos, 
						qrPaymentDetails.getString("url").replaceAll("////",""), "doQRCancel", dataSource);
			}
			
		} catch (Exception e) {
			if (isIposQR) {
				Logger.writeError(e, "Exception: ", IPOS_FOLDER);
			} else {
				Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			}
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
