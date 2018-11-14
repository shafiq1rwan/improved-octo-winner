package mpay.my.ecpos_manager_v2.rest;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;

@RestController
@RequestMapping("/ecposcontroller")
public class client_RestController {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static String ECPOS_ACT_FILENAME = Property.getECPOS_ACT_FILENAME();
	private static String ECPOS_ERR_FILENAME = Property.getECPOS_ERR_FILENAME();
	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	@PostMapping("/getconnection_QR")
	public String getconnection_QR(){
		JSONObject jsonResult = new JSONObject();
		String QR = "";
		try {
		    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		    while (interfaces.hasMoreElements()) {
		        NetworkInterface iface = interfaces.nextElement();
		        if (iface.isLoopback() || !iface.isUp() || iface.isVirtual() || iface.isPointToPoint())
		            continue;

		        Enumeration<InetAddress> addresses = iface.getInetAddresses();
		        while(addresses.hasMoreElements()) {
		            InetAddress addr = addresses.nextElement();

		            final String ip = addr.getHostAddress();
		            System.out.println(ip);
		            if(Inet4Address.class == addr.getClass() & ip.contains("192.168.0")){
		            	QR = ip + ":8080";
		            }
		        }
		    }
		    jsonResult.put("QR", QR);
		} catch (SocketException e) {
			Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
		    throw new RuntimeException(e);
		} catch (Exception e) {
			Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e.toString(), ECPOS_ERR_FILENAME, ECPOS_FOLDER);
			e.printStackTrace();
		}
		
		System.out.println("************QR************" + QR);	
		Logger.writeActivity("************QR************" + QR, ECPOS_ACT_FILENAME, ECPOS_FOLDER);
		return jsonResult.toString();
		//return QR;
	}
	
	
	
	
	
}
