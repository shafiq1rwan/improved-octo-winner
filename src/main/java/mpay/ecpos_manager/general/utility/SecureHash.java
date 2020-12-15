package mpay.ecpos_manager.general.utility;

import java.security.MessageDigest;

public class SecureHash {
	
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
	
	// Array for creating hex chars 
	static final char[] vmpos_HEX_TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' }; 
	public static String vmposGenSecureHash(String originalString) {
	MessageDigest md = null;
	byte[] ba = null;
	// create the md hash and ISO-8859-1 encode it
	try {
	md = MessageDigest.getInstance("SHA-256");
	ba = md.digest(originalString.getBytes("ISO-8859-1"));
	} catch (Exception e) {
	} // wont happen
	return vmpos_hex(ba);
	}
	static String vmpos_hex(byte[] input) {
	// create a StringBuffer 2x the size of the hash array
	StringBuffer sb = new StringBuffer(input.length * 2);
	// retrieve the byte array data, convert it to hex and add it to the
	// StringBuffer
	for (int i = 0; i < input.length; i++) {
	sb.append(vmpos_HEX_TABLE[(input[i] >> 4) & 0xf]);
	sb.append(vmpos_HEX_TABLE[input[i] & 0xf]);
	}
	return sb.toString();
	}
}