package mpay.ecpos_manager.general.utility.hardware;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.Paths;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import mpay.ecpos_manager.general.utility.QRGenerate;

@Component
public class PrinterInitializer {
	@Value("${receipt-path}")
	private String receiptPath;

	private static final String RECEIPT_FONT_FAMILY = "Arial";

	public void initialize() {
		try {
			System.out.println("Intializing Word/PDF/Printer...");
			String templateName = "ReceiptStyleTemplate_EPSON";

			new File(receiptPath).mkdirs();

			try (XWPFDocument doc = new XWPFDocument(new FileInputStream(URLDecoder.decode(
					getClass().getClassLoader().getResource(Paths.get("docx", templateName + ".docx").toString())
							.toString().substring("file:/".length()),
					"UTF-8")))) {
				XWPFParagraph emptyParagraph = null;

				if (doc.getStyles() != null) {
					XWPFStyles styles = doc.getStyles();
					CTFonts fonts = CTFonts.Factory.newInstance();
					fonts.setAscii(RECEIPT_FONT_FAMILY);
					styles.setDefaultFonts(fonts);
				}

				XWPFParagraph headerStoreNameParagraph = doc.createParagraph();
				headerStoreNameParagraph.setAlignment(ParagraphAlignment.CENTER);
				headerStoreNameParagraph.setVerticalAlignment(TextAlignment.TOP);
				headerStoreNameParagraph.setSpacingAfter(0);

				XWPFRun runHeaderStoreNameParagraph = headerStoreNameParagraph.createRun();
				runHeaderStoreNameParagraph.setBold(true);
				runHeaderStoreNameParagraph.setFontSize(12);
				runHeaderStoreNameParagraph.setText("Initialize");

				emptyParagraph = doc.createParagraph();
				emptyParagraph.setSpacingAfter(0);
				emptyParagraph.createRun().addBreak();
				emptyParagraph.removeRun(0);

				byte[] qrByteData = QRGenerate.generateQRImage("Initialize QR", 300, 300);

				XWPFParagraph qrParagraph = doc.createParagraph();
				qrParagraph.setAlignment(ParagraphAlignment.CENTER);
				qrParagraph.setSpacingAfter(0);
				qrParagraph.setSpacingBefore(0);
				XWPFRun runQrParagraph = qrParagraph.createRun();
				runQrParagraph.addPicture(new ByteArrayInputStream(qrByteData), XWPFDocument.PICTURE_TYPE_JPEG,
						"Generated", Units.toEMU(125), Units.toEMU(125));

				try (FileOutputStream out = new FileOutputStream(
						Paths.get(receiptPath, "initialize.docx").toString())) {
					doc.write(out);
				}

				XWPFDocument document = new XWPFDocument(
						new FileInputStream(new File(Paths.get(receiptPath, "initialize.docx").toString())));
				PdfOptions options = PdfOptions.create();
				OutputStream out = new FileOutputStream(new File(Paths.get(receiptPath, "initialize.pdf").toString()));
				PdfConverter.getInstance().convert(document, out, options);
				document.close();
				out.close();
				
				File file = new File(Paths.get(receiptPath, "initialize.docx").toString());
				if (file.exists()) {
					file.delete();
				}
				file = new File(Paths.get(receiptPath, "initialize.pdf").toString());
				if (file.exists()) {
					file.delete();
				}
				
				System.out.println("Intialization Completed...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
