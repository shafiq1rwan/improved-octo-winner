package mpay.ecpos_manager.general.utility;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;

public class QRGenerate {

	final static String foldername = Property.getECPOS_FOLDER_NAME();
//	private final static String LOGO = "https://www.managepay.com/managepay/static/img/logo/mpay2016-top.png";

	public static byte[] generateQRImage(String content, int width, int height) {
		byte[] pngData = null;

		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

			// Load QR image
			// BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix,
			// getMatrixConfig());

			// Load logo image
			// BufferedImage overly = getOverly(LOGO);

			// Calculate the delta height and width between QR code and logo
			// int deltaHeight = qrImage.getHeight() - overly.getHeight();
			// int deltaWidth = qrImage.getWidth() - overly.getWidth();

			// Initialize combined image
			// BufferedImage combined = new BufferedImage(qrImage.getHeight(),
			// qrImage.getWidth(),
			// BufferedImage.TYPE_INT_ARGB);
			// Graphics2D g = (Graphics2D) combined.getGraphics();

			// Write QR code to new image at position 0/0
			// g.drawImage(qrImage, 0, 0, null);
			// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

			// Write logo into combine image at position (deltaWidth / 2) and
			// (deltaHeight / 2). Background: Left/Right and Top/Bottom must be
			// the same space for the logo to be centered
			// g.drawImage(overly, (int) Math.round(deltaWidth / 2), (int)
			// Math.round(deltaHeight / 2), null);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			// ImageIO.write(combined, "png", outputStream);
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
			pngData = outputStream.toByteArray();
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", foldername);
			e.printStackTrace();
		}
		return pngData;
	}

	private static BufferedImage getOverly(String LOGO) throws IOException {
		URL url = new URL(LOGO);
		return ImageIO.read(url);
	}

	private static MatrixToImageConfig getMatrixConfig() {
		// ARGB Colors
		// Check Colors ENUM
		return new MatrixToImageConfig(Colors.BLACK.getArgb(), Colors.WHITE.getArgb());
	}

	public enum Colors {

		BLUE(0xFF40BAD0), RED(0xFFE91C43), PURPLE(0xFF8A4F9E), ORANGE(0xFFF4B13D), WHITE(0xFFFFFFFF), BLACK(0xFF000000);

		private final int argb;

		Colors(final int argb) {
			this.argb = argb;
		}

		public int getArgb() {
			return argb;
		}
	}

}
