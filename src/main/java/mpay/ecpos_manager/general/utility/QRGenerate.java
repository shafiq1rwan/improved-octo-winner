package mpay.ecpos_manager.general.utility;

import java.io.ByteArrayOutputStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;

public class QRGenerate {
	final static String foldername = Property.getECPOS_FOLDER_NAME();
	
	public static byte[] generateQRImage(String content, int width, int height){
		byte[] pngData = null;
		
		try {
		    QRCodeWriter qrCodeWriter = new QRCodeWriter();
		    BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
		    
		    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
		    pngData = outputStream.toByteArray();    
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", foldername);
			e.printStackTrace();
		}
	    return pngData;
	}
}
