package mpay.my.ecpos_manager_v2.webutil;

import java.security.MessageDigest;

public class SecureHashTool {
	private static final char[] HEX_TABLE = new char[] {
			'0', '1', '2', '3', '4', '5', '6', '7', 
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};
	
	public static String generateSecureHash(String mdInstance, String originalString) {
		MessageDigest md = null;
		byte[] ba = null;
		try {
			md = MessageDigest.getInstance(mdInstance);
			ba = md.digest(originalString.getBytes("ISO-8859-1"));
		} catch (Exception e) {

			e.printStackTrace();
		}
	    return hex(ba);   
	}
	
	private static String hex(byte[] input) {
		StringBuffer sb = new StringBuffer(input.length * 2);
		for (int i = 0; i < input.length; i++) {
			sb.append(HEX_TABLE[(input[i] >> 4) & 0xf]);
			sb.append(HEX_TABLE[input[i] & 0xf]);
		}
		return sb.toString();
	}
}