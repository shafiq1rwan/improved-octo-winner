package mpay.my.ecpos_manager_v2.webutil;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;

public class AesEncryption {

	public final static String initVector = "RandomInitVector";
	
	public final static String salt = "RandomSalt";
	
	final static String foldername = Property.getECPOS_FOLDER_NAME();

	public static String encrypt(String key, String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 256);
	        SecretKey tmp = factory.generateSecret(spec);
	        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
	         
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
	        return Base64.getUrlEncoder().encodeToString(cipher.doFinal(value.getBytes("UTF-8")));
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", foldername);
			e.printStackTrace();
		}
		return null;
	}

	public static String decrypt(String key, String encrypted) {
		try
	    {
	        IvParameterSpec ivspec = new IvParameterSpec(initVector.getBytes("UTF-8"));
	         
	        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 256);
	        SecretKey tmp = factory.generateSecret(spec);
	        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
	         
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
	        return new String(cipher.doFinal(Base64.getUrlDecoder().decode(encrypted)));
	    }
	    catch (Exception e) {
	    	Logger.writeError(e, "Exception :", foldername);
			e.printStackTrace();
	    }
	    return null;
	}
	
//	public static void main(String[] args) {
//		String value = "Hello World!!!";
//		String key = "5216444430761517";
//		
//		String encrypt = encrypt(key, value);
//		System.out.println(encrypt);
//		String decrypt = decrypt(key, encrypt);
//		System.out.println(decrypt);
//	} 
}