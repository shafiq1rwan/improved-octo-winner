package mpay.ecpos_manager.general.utility.hardware;

import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.sql.DataSource;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import mpay.ecpos_manager.general.constant.Constant;
import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.QRGenerate;

@Service
public class ReceiptPrinter {

	private static String HARDWARE_FOLDER = Property.getHARDWARE_FOLDER_NAME();
	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();

	@Autowired
	DataSource dataSource;

	@Value("${receipt-path}")
	private String receiptPath;

	private static final String RECEIPT_FONT_FAMILY = "Arial";

	private static final String RECEIPT_HEADER_STYLE = "Receipt Header Paragraph";
	private static final String RECEIPT_PARAGRAPH_STYLE = "Receipt Paragraph";

	// receipt header
	private JSONObject getReceiptHeader() {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("select * from `store` limit 1");
			rs = stmt.executeQuery();

			if (rs.next()) {
				jsonResult.put("storeName", rs.getString("store_name"));
				jsonResult.put("storeLogoPath", rs.getString("store_logo_path"));
				jsonResult.put("storeAddress", rs.getString("store_address"));
				jsonResult.put("storeContactHpNumber", rs.getString("store_contact_hp_number"));
				jsonResult.put("storeCurrency", rs.getString("store_currency")); // will change if got currency lookup
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}

	public JSONObject printQR(JSONObject receiptContent, String staffName) {
		JSONObject jsonResult = new JSONObject();

		try {
			JSONObject receiptHeader = getReceiptHeader();
			if (receiptHeader.length() == 0 || receiptContent.length() == 0) {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Receipt Data Incomplete");
			} else {
				JSONObject printerResult = getSelectedReceiptPrinter();
				
				if(!printerResult.getString("receipt_printer").equals("No Printing")) {
					new File(receiptPath).mkdirs();

					PrintService myPrintService = null;
					String templateName = "";

					if (printerResult.has("receipt_printer")) {

						myPrintService = findPrintService(printerResult.getString("receipt_printer"));
						Logger.writeActivity("Selected Printer Brand: " + printerResult.getString("receipt_printer"),
								ECPOS_FOLDER);
						Logger.writeActivity("Selected Printer: " + myPrintService.getName(), ECPOS_FOLDER);

						if (printerResult.getString("receipt_printer").equals("EPSON"))
							templateName = "ReceiptStyleTemplate_EPSON";
						else if (printerResult.getString("receipt_printer").equals("Posiflex"))
							templateName = "ReceiptStyleTemplate_Posiflex";
					}

					Logger.writeActivity("Template Name: " + templateName, ECPOS_FOLDER);
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

						// Header Store Name
						XWPFParagraph headerStoreNameParagraph = doc.createParagraph();
						headerStoreNameParagraph.setAlignment(ParagraphAlignment.CENTER);
						headerStoreNameParagraph.setVerticalAlignment(TextAlignment.TOP);
						headerStoreNameParagraph.setSpacingAfter(0);

						XWPFRun runHeaderStoreNameParagraph = headerStoreNameParagraph.createRun();
						runHeaderStoreNameParagraph.setBold(true);
						runHeaderStoreNameParagraph.setFontSize(12);
						runHeaderStoreNameParagraph.setText(receiptHeader.getString("storeName"));

						// Header Store Address
						XWPFParagraph headerStoreAddressParagraph = doc.createParagraph();
						headerStoreAddressParagraph.setAlignment(ParagraphAlignment.CENTER);
						headerStoreAddressParagraph.setSpacingAfter(0);

						XWPFRun runHeaderStoreAddressParagraph = headerStoreAddressParagraph.createRun();
						runHeaderStoreAddressParagraph.setFontSize(9);
						runHeaderStoreAddressParagraph.setText(receiptHeader.getString("storeAddress"));
						runHeaderStoreAddressParagraph.addBreak();

						emptyParagraph = doc.createParagraph();
						emptyParagraph.setSpacingAfter(0);
						emptyParagraph.createRun().addBreak();
						emptyParagraph.removeRun(0);

						// info table
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						List<String> receiptInfoLabels = Arrays.asList("Check No", "Table No", "Date Time", "Staff");
						List<String> receiptInfoContents = Arrays.asList(receiptContent.getString("checkNo"),
								receiptContent.getString("tableNo"), sdf.format(new Date()), staffName);

						XWPFTable receiptInfoTable = doc.createTable(receiptInfoLabels.size(), 2);
						CTTblLayoutType receiptInfoTableType = receiptInfoTable.getCTTbl().getTblPr().addNewTblLayout();
						receiptInfoTableType.setType(STTblLayoutType.FIXED);
						receiptInfoTable.getCTTbl().getTblPr().unsetTblBorders();

						for (int i = 0; i < receiptInfoLabels.size(); i++) {
							XWPFTableRow receiptInfoRow = receiptInfoTable.getRow(i);
							createCellText(receiptInfoRow.getCell(0), receiptInfoLabels.get(i), false,
									ParagraphAlignment.LEFT);
							createCellText(receiptInfoRow.getCell(1), receiptInfoContents.get(i), false,
									ParagraphAlignment.LEFT);
						}

						long receiptInfoTableWidths[] = { 1400, 2000 };

						CTTblGrid cttblgrid = receiptInfoTable.getCTTbl().addNewTblGrid();
						cttblgrid.addNewGridCol().setW(new BigInteger("1400"));
						cttblgrid.addNewGridCol().setW(new BigInteger("2000"));

						for (int x = 0; x < receiptInfoTable.getNumberOfRows(); x++) {
							XWPFTableRow row = receiptInfoTable.getRow(x);
							int numberOfCell = row.getTableCells().size();
							for (int y = 0; y < numberOfCell; y++) {
								XWPFTableCell cell = row.getCell(y);
								cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(receiptInfoTableWidths[y]));
							}
						}

						emptyParagraph = doc.createParagraph();
						emptyParagraph.setSpacingAfter(0);
						emptyParagraph.createRun().addBreak();
						emptyParagraph.removeRun(0);

						// QR image centralized
						XWPFParagraph qrImageParagraph = doc.createParagraph();
						qrImageParagraph.setAlignment(ParagraphAlignment.CENTER);
						XWPFRun runQrImageParagraph = qrImageParagraph.createRun();

						// InputStream is = new ByteArrayInputStream(QRGenerate.generateQRImage(, 125,
						// 125));
						InputStream is = new ByteArrayInputStream(
								Base64.getDecoder().decode(receiptContent.getString("qrImage").split(",")[1].getBytes()));
						runQrImageParagraph.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, "Generated", Units.toEMU(125),
								Units.toEMU(125));
						is.close();

						/*
						 * emptyParagraph = doc.createParagraph(); emptyParagraph.setSpacingAfter(0);
						 * emptyParagraph.createRun().addBreak(); emptyParagraph.removeRun(0);
						 */

						emptyParagraph = doc.createParagraph();
						emptyParagraph.setSpacingAfter(0);
						emptyParagraph.setAlignment(ParagraphAlignment.CENTER);
						emptyParagraph.createRun().setText("Have a nice day");
						emptyParagraph.createRun().addBreak();

						// output the result as doc file
						try (FileOutputStream out = new FileOutputStream(
								Paths.get(receiptPath, "qrReciept.docx").toString())) {
							doc.write(out);
						}

						// convert docx to pdf
						XWPFDocument document = new XWPFDocument(
								new FileInputStream(new File(Paths.get(receiptPath, "qrReciept.docx").toString())));
						PdfOptions options = PdfOptions.create();
						OutputStream out = new FileOutputStream(
								new File(Paths.get(receiptPath, "qrReciept.pdf").toString()));
						PdfConverter.getInstance().convert(document, out, options);
						document.close();
						out.close();

						// print pdf
						if (myPrintService != null) {
							PDDocument printablePdf = PDDocument
									.load(new File(Paths.get(receiptPath, "qrReciept.pdf").toString()));

							PrinterJob job = PrinterJob.getPrinterJob();
							job.setJobName("QR - " + receiptContent.getString("checkNo"));
							job.setPageable(new PDFPageable(printablePdf));
							job.setPrintService(myPrintService);
							job.print();

							printablePdf.close();
						}

						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
					}
				} else {
					jsonResult.put(Constant.RESPONSE_CODE, "00");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}

	public JSONObject printReceipt(String staffName, String checkNo) {
		JSONObject jsonResult = new JSONObject();

		try {
			JSONObject receiptHeader = getReceiptHeader();
			JSONObject receiptContent = getReceiptContent(checkNo);
			// JSONObject receiptFooter
			JSONObject printReceiptResponse = printReceiptData(staffName, receiptHeader, receiptContent, null);

			if (printReceiptResponse.length() > 0) {
				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
			} else {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "PRINTING FAIL");
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}

	// receipt content
	private JSONObject getReceiptContent(String checkNo) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		PreparedStatement stmt5 = null;
		PreparedStatement stmtA = null;
		PreparedStatement stmt6 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rsA = null;
		ResultSet rs6 = null;

		try {
			connection = dataSource.getConnection();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			stmt = connection.prepareStatement(
					"select * from `check` c " + "inner join check_status cs on cs.id = c.check_status "
							+ "where check_number = ? and check_status in (2,3);");
			stmt.setString(1, checkNo);
			rs = stmt.executeQuery();

			if (rs.next()) {
				long id = rs.getLong("id");

				jsonResult.put("checkNo", rs.getString("check_number"));
				jsonResult.put("tableNo", rs.getString("table_number") == null ? "-" : rs.getString("table_number"));
				jsonResult.put("createdDate", sdf.format(rs.getTimestamp("created_date")));
				jsonResult.put("totalAmount",
						new BigDecimal(rs.getString("total_amount") == null ? "0.00" : rs.getString("total_amount")));
				jsonResult.put("totalAmountWithTax",
						new BigDecimal(rs.getString("total_amount_with_tax") == null ? "0.00"
								: rs.getString("total_amount_with_tax")));
				jsonResult.put("totalAmountWithTaxRoundingAdjustment",
						new BigDecimal(rs.getString("total_amount_with_tax_rounding_adjustment") == null ? "0.00"
								: rs.getString("total_amount_with_tax_rounding_adjustment")));
				jsonResult.put("grandTotalAmount", new BigDecimal(
						rs.getString("grand_total_amount") == null ? "0.00" : rs.getString("grand_total_amount")));
				jsonResult.put("status", rs.getString("name"));
				jsonResult.put("depositAmount",
						rs.getString("deposit_amount") == null ? "0.00" : rs.getString("deposit_amount"));
				jsonResult.put("tenderAmount",
						rs.getString("tender_amount") == null ? "0.00" : rs.getString("tender_amount"));
				jsonResult.put("overdueAmount",
						rs.getString("overdue_amount") == null ? "0.00" : rs.getString("overdue_amount"));

				stmt2 = connection.prepareStatement(
						"select * from tax_charge tc " + "inner join check_tax_charge ctc on ctc.tax_charge_id = tc.id "
								+ "where ctc.check_id = ? and ctc.check_number = ?" + "order by tc.charge_type;");
				stmt2.setLong(1, id);
				stmt2.setString(2, checkNo);
				rs2 = stmt2.executeQuery();

				JSONArray taxCharges = new JSONArray();
				while (rs2.next()) {
					JSONObject taxCharge = new JSONObject();
					taxCharge.put("name", rs2.getString("tax_charge_name"));
					taxCharge.put("rate", rs2.getBigDecimal("rate"));
					taxCharge.put("chargeAmount", new BigDecimal(rs2.getString("grand_total_charge_amount")));

					taxCharges.put(taxCharge);
				}
				jsonResult.put("taxCharges", taxCharges);
				
				stmt3 = connection.prepareStatement(
						"select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id is null and check_detail_status in (2, 3) order by id asc;");
				stmt3.setLong(1, id);
				stmt3.setString(2, checkNo);
				rs3 = stmt3.executeQuery();

				JSONArray grandParentItemArray = new JSONArray();
				while (rs3.next()) {
					long grandParentId = rs3.getLong("id");

					JSONObject grandParentItem = new JSONObject();
					grandParentItem.put("checkDetailId", rs3.getString("id"));
					grandParentItem.put("itemId", rs3.getString("menu_item_id"));
					grandParentItem.put("itemCode", rs3.getString("menu_item_code"));
					grandParentItem.put("itemName", rs3.getString("menu_item_name"));
					grandParentItem.put("itemPrice", rs3.getString("menu_item_price"));
					grandParentItem.put("itemQuantity", rs3.getInt("quantity"));
					grandParentItem.put("totalAmount", rs3.getString("total_amount"));

					stmtA = connection.prepareStatement("select * from menu_item mi "
							+ "left join menu_item_modifier_group mimg on mimg.menu_item_id = mi.id "
							+ "where mi.id = ?;");
					stmtA.setString(1, rs3.getString("menu_item_id"));
					rsA = stmtA.executeQuery();

					if (rsA.next()) {
						if (rsA.getInt("menu_item_type") == 0) {
							grandParentItem.put("isAlaCarte", true);

							if (rsA.getLong("menu_item_id") > 0) {
								grandParentItem.put("hasModified", true);
							} else {
								grandParentItem.put("hasModified", false);
							}
						} else {
							grandParentItem.put("isAlaCarte", false);
						}
					} else {
						grandParentItem.put("isAlaCarte", false);
					}

					stmt4 = connection.prepareStatement(
							"select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id = ? and check_detail_status in (2,3) order by id asc;");
					stmt4.setLong(1, id);
					stmt4.setString(2, checkNo);
					stmt4.setLong(3, grandParentId);
					rs4 = stmt4.executeQuery();

					JSONArray parentItemArray = new JSONArray();
					while (rs4.next()) {
						long parentId = rs4.getLong("id");

						JSONObject parentItem = new JSONObject();
						parentItem.put("itemId", rs4.getString("menu_item_id"));
						parentItem.put("itemCode", rs4.getString("menu_item_code"));
						parentItem.put("itemName", rs4.getString("menu_item_name"));
						parentItem.put("itemPrice", rs4.getString("menu_item_price"));
						parentItem.put("itemQuantity", rs4.getString("quantity"));
						parentItem.put("totalAmount", rs4.getString("total_amount"));

						stmt5 = connection.prepareStatement(
								"select * from check_detail where check_id = ? and check_number = ? and parent_check_detail_id = ? and check_detail_status in (2, 3) order by id asc;");
						stmt5.setLong(1, id);
						stmt5.setString(2, checkNo);
						stmt5.setLong(3, parentId);
						rs5 = stmt5.executeQuery();

						JSONArray childItemArray = new JSONArray();
						while (rs5.next()) {
							JSONObject childItem = new JSONObject();
							childItem.put("itemId", rs5.getString("menu_item_id"));
							childItem.put("itemCode", rs5.getString("menu_item_code"));
							childItem.put("itemName", rs5.getString("menu_item_name"));
							childItem.put("itemPrice", rs5.getString("menu_item_price"));
							childItem.put("itemQuantity", rs5.getString("quantity"));
							childItem.put("totalAmount", rs5.getString("total_amount"));

							childItemArray.put(childItem);
						}
						parentItem.put("childItemArray", childItemArray);
						parentItemArray.put(parentItem);
					}
					grandParentItem.put("parentItemArray", parentItemArray);
					grandParentItemArray.put(grandParentItem);
				}
				jsonResult.put("grandParentItemArray", grandParentItemArray);

				stmt6 = connection
						.prepareStatement("SELECT * FROM transaction WHERE check_number = ? ORDER BY id DESC;");
				stmt6.setString(1, checkNo);
				rs6 = stmt6.executeQuery();
				if (rs6.next()) {
					jsonResult.put("paymentMethod", rs6.getInt("payment_method"));
					if(rs6.getInt("payment_method") == 1) {
						JSONObject cashData = new JSONObject();
						cashData.put("cashReceivedAmount", new BigDecimal(rs6.getString("received_amount")));
						cashData.put("cashChangeAmount", new BigDecimal(rs6.getString("change_amount")));
						
						jsonResult.put("cashData", cashData);
					} else if (rs6.getInt("payment_method") == 2) {
						JSONObject cardData = new JSONObject();
						cardData.put("uid", rs6.getString("unique_trans_number"));
						cardData.put("approvalCode", rs6.getString("approval_code"));
						cardData.put("mid", rs6.getString("bank_mid"));
						cardData.put("tid", rs6.getString("bank_tid"));
						cardData.put("date", rs6.getString("transaction_date"));
						cardData.put("time", rs6.getString("transaction_time"));
						cardData.put("invoiceNo", rs6.getString("invoice_number"));
						cardData.put("cardType", rs6.getString("card_issuer_name"));
						cardData.put("app", rs6.getString("app_label"));
						cardData.put("aid", rs6.getString("aid"));
						cardData.put("maskedCardNo", rs6.getString("masked_card_number"));
						cardData.put("cardExpiry", rs6.getString("card_expiry_date"));
						cardData.put("batchNo", rs6.getString("batch_number"));
						cardData.put("rRefNo", rs6.getString("rrn"));
						cardData.put("tc", rs6.getString("tc"));
						cardData.put("terminalVerification", rs6.getString("terminal_verification_result"));

						jsonResult.put("cardData", cardData);
					} else if (rs6.getInt("payment_method") == 3) {
						JSONObject qrData = new JSONObject();
						qrData.put("issuerType", rs6.getString("qr_issuer_type"));
						qrData.put("uid", rs6.getString("unique_trans_number"));
						qrData.put("mid", rs6.getString("bank_mid"));
						qrData.put("tid", rs6.getString("bank_tid"));
						qrData.put("date", rs6.getString("transaction_date"));
						qrData.put("time", rs6.getString("transaction_time"));
						qrData.put("traceNo", rs6.getString("trace_number"));
						qrData.put("authNo", rs6.getString("auth_number"));
						qrData.put("amountMYR", rs6.getString("qr_amount_myr"));
						qrData.put("amountRMB", rs6.getString("qr_amount_rmb"));
						qrData.put("userID", rs6.getString("qr_user_id"));
						qrData.put("refID", rs6.getString("qr_ref_id"));

						jsonResult.put("qrData", qrData);
					}
				}

				jsonResult.put(Constant.RESPONSE_CODE, "00");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
			} else {
				Logger.writeActivity("Check Not Found", ECPOS_FOLDER);

				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Check Not Found");
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (stmt3 != null)
					stmt3.close();
				if (stmt4 != null)
					stmt4.close();
				if (stmt5 != null)
					stmt5.close();
				if (stmtA != null)
					stmtA.close();
				if (stmt6 != null)
					stmt6.close();
				
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
				}
				if (rs4 != null) {
					rs4.close();
					rs4 = null;
				}
				if (rs5 != null) {
					rs5.close();
					rs5 = null;
				}
				if (rsA != null) {
					rsA.close();
					rsA = null;
				}
				if (rs6 != null) {
					rs6.close();
					rs6 = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		System.out.println("Printable Result: " + jsonResult.toString());
		return jsonResult;
	}

	private JSONObject getSelectedReceiptPrinter() {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("select receipt_printer_manufacturer from receipt_printer;");
			rs = stmt.executeQuery();

			if (rs.next()) {
				stmt2 = connection
						.prepareStatement("select name from receipt_printer_manufacturer_lookup where id = ?;");
				stmt2.setLong(1, rs.getLong("receipt_printer_manufacturer"));
				rs2 = stmt2.executeQuery();

				if (rs2.next()) {
					jsonResult.put("receipt_printer", rs2.getString("name"));
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return jsonResult;
	}

	private JSONObject printReceiptData(String staffName, JSONObject receiptHeaderJson, JSONObject receiptContentJson,
			JSONObject receiptFooterJson) {
		XWPFParagraph emptyParagraph = null;
		JSONObject jsonResult = new JSONObject();

		try {
			if (receiptHeaderJson.length() == 0 || receiptContentJson.length() == 0) {
				jsonResult.put(Constant.RESPONSE_CODE, "01");
				jsonResult.put(Constant.RESPONSE_MESSAGE, "Receipt Data Incomplete");
			} else {
				JSONArray jsonGrandParentArray = receiptContentJson.optJSONArray("grandParentItemArray");

				if (jsonGrandParentArray.length() == 0) {
					jsonResult.put(Constant.RESPONSE_CODE, "01");
					jsonResult.put(Constant.RESPONSE_MESSAGE, "Receipt Item Not Available");
				} else {
					JSONObject printerResult = getSelectedReceiptPrinter();
					
					if(printerResult.getString("receipt_printer").equals("No Printing")) {
						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
					} else {
						new File(receiptPath).mkdirs();

						PrintService myPrintService = null;
						String templateName = "";

						if (printerResult.has("receipt_printer")) {

							myPrintService = findPrintService(printerResult.getString("receipt_printer"));
							System.out.println("Selected Printer: " + myPrintService.getName());
							Logger.writeActivity("Selected Printer Brand: " + printerResult.getString("receipt_printer"),
									ECPOS_FOLDER);
							Logger.writeActivity("Selected Printer: " + myPrintService.getName(), ECPOS_FOLDER);

							if (printerResult.getString("receipt_printer").equals("EPSON"))
								templateName = "ReceiptStyleTemplate_EPSON";
							else if (printerResult.getString("receipt_printer").equals("Posiflex"))
								templateName = "ReceiptStyleTemplate_Posiflex";
						}

						System.out.println("Template Name: " + templateName);
						Logger.writeActivity("Template Name: " + templateName, ECPOS_FOLDER);
						;
						try (XWPFDocument doc = new XWPFDocument(new FileInputStream(URLDecoder.decode(getClass()
								.getClassLoader().getResource(Paths.get("docx", templateName + ".docx").toString())
								.toString().substring("file:/".length()), "UTF-8")))) {
							if (doc.getStyles() != null) {

								XWPFStyles styles = doc.getStyles();
								CTFonts fonts = CTFonts.Factory.newInstance();
								fonts.setAscii(RECEIPT_FONT_FAMILY);
								styles.setDefaultFonts(fonts);

								// XWPFStyles styles = doc.createStyles();

								// set default font
								/*
								 * CTFonts fonts = CTFonts.Factory.newInstance();
								 * fonts.setAscii(RECEIPT_FONT_FAMILY); styles.setDefaultFonts(fonts);
								 */

								// addCustomParagraphStyle(doc, styles, RECEIPT_HEADER_STYLE, "24");
								// addCustomParagraphStyle(doc, styles, RECEIPT_PARAGRAPH_STYLE, "18"); */
							}

							/*
							 * CTDocument1 ctdoc = doc.getDocument(); CTBody ctbody = ctdoc.getBody(); if
							 * (!ctbody.isSetSectPr()) { ctbody.addNewSectPr(); } CTSectPr section =
							 * ctbody.getSectPr();
							 * 
							 * if (!section.isSetPgSz()) { section.addNewPgSz(); }
							 */

							// CTPageSz pageSize = section.getPgSz();
							// pageSize.setOrient(STPageOrientation.PORTRAIT);
							// 226 point x 20
							// pageSize.setW(BigInteger.valueOf(4520));
							// 641
							// pageSize.setH(BigInteger.valueOf(16820));

							// Set Margin
							/*
							 * CTPageMar pageMar = section.addNewPgMar();
							 * pageMar.setGutter(BigInteger.valueOf(0));
							 * pageMar.setHeader(BigInteger.valueOf(720L));
							 * pageMar.setFooter(BigInteger.valueOf(720L));
							 * pageMar.setLeft(BigInteger.valueOf(100L));
							 * pageMar.setTop(BigInteger.valueOf(220L));
							 * pageMar.setRight(BigInteger.valueOf(100L));
							 * pageMar.setBottom(BigInteger.valueOf(440L));
							 */

							// Header Store Name
							XWPFParagraph headerStoreNameParagraph = doc.createParagraph();
							headerStoreNameParagraph.setAlignment(ParagraphAlignment.CENTER);
							headerStoreNameParagraph.setVerticalAlignment(TextAlignment.TOP);
							headerStoreNameParagraph.setSpacingAfter(0);

							XWPFRun runHeaderStoreNameParagraph = headerStoreNameParagraph.createRun();
							runHeaderStoreNameParagraph.setBold(true);
							runHeaderStoreNameParagraph.setFontSize(12);
							runHeaderStoreNameParagraph.setText(receiptHeaderJson.getString("storeName"));

							// Header Store Address
							XWPFParagraph headerStoreAddressParagraph = doc.createParagraph();
							headerStoreAddressParagraph.setAlignment(ParagraphAlignment.CENTER);
							headerStoreAddressParagraph.setSpacingAfter(0);

							XWPFRun runHeaderStoreAddressParagraph = headerStoreAddressParagraph.createRun();
							runHeaderStoreAddressParagraph.setFontSize(9);
							runHeaderStoreAddressParagraph.setText(receiptHeaderJson.getString("storeAddress"));
							runHeaderStoreAddressParagraph.addBreak();
							runHeaderStoreAddressParagraph
									.setText("Contact No: " + receiptHeaderJson.getString("storeContactHpNumber"));
							runHeaderStoreAddressParagraph.addBreak();

							emptyParagraph = doc.createParagraph();
							emptyParagraph.setSpacingAfter(0);
							emptyParagraph.createRun().addBreak();
							emptyParagraph.removeRun(0);

							// Receipt Info Table
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
							List<String> receiptInfoLabels = Arrays.asList("Check No", "Table No", "Order At", "Printed At",
									"Staff");
							List<String> receiptInfoContents = Arrays.asList(receiptContentJson.getString("checkNo"),
									receiptContentJson.getString("tableNo"), receiptContentJson.getString("createdDate"),
									sdf.format(new Date()), staffName);

							XWPFTable receiptInfoTable = doc.createTable(receiptInfoLabels.size(), 2);
							CTTblLayoutType receiptInfoTableType = receiptInfoTable.getCTTbl().getTblPr().addNewTblLayout(); // set
																																// //
																																// Layout
							receiptInfoTableType.setType(STTblLayoutType.FIXED);
							receiptInfoTable.getCTTbl().getTblPr().unsetTblBorders();

							for (int i = 0; i < receiptInfoLabels.size(); i++) {
								XWPFTableRow receiptInfoRow = receiptInfoTable.getRow(i);
								createCellText(receiptInfoRow.getCell(0), receiptInfoLabels.get(i), false,
										ParagraphAlignment.LEFT);
								createCellText(receiptInfoRow.getCell(1), receiptInfoContents.get(i), false,
										ParagraphAlignment.LEFT);
							}

							long receiptInfoTableWidths[] = { 1400, 2000 };

							CTTblGrid cttblgrid = receiptInfoTable.getCTTbl().addNewTblGrid();
							cttblgrid.addNewGridCol().setW(new BigInteger("1400"));
							cttblgrid.addNewGridCol().setW(new BigInteger("2000"));

							for (int x = 0; x < receiptInfoTable.getNumberOfRows(); x++) {
								XWPFTableRow row = receiptInfoTable.getRow(x);
								int numberOfCell = row.getTableCells().size();
								for (int y = 0; y < numberOfCell; y++) {
									XWPFTableCell cell = row.getCell(y);
									cell.getCTTc().addNewTcPr().addNewTcW()
											.setW(BigInteger.valueOf(receiptInfoTableWidths[y]));
								}
							}

							XWPFParagraph receiptInfoBreak = doc.createParagraph();
							receiptInfoBreak.setSpacingAfter(0);
							receiptInfoBreak.createRun().addBreak();
							receiptInfoBreak.removeRun(0);

							// Receipt Content
							XWPFTable table = doc.createTable();
							CTTblLayoutType type = table.getCTTbl().getTblPr().addNewTblLayout(); // set Layout
							type.setType(STTblLayoutType.FIXED);

							table.getCTTbl().getTblPr().unsetTblBorders(); // set table no border

							XWPFTableRow tableRowOne = table.getRow(0);
							XWPFTableRow tableRowTwo = table.createRow();
							
							XWPFParagraph tableHeaderOne = tableRowOne.getCell(0).getParagraphs().get(0);
							tableHeaderOne.setSpacingBefore(0);
							tableHeaderOne.setSpacingAfter(0);
							tableHeaderOne.setVerticalAlignment(TextAlignment.CENTER);
							XWPFRun tableHeaderOneRun = tableHeaderOne.createRun();
							tableHeaderOneRun.setFontSize(9);
							tableHeaderOneRun.setBold(true);
							tableHeaderOneRun.setText("Qty");

							XWPFParagraph tableHeaderTwo = tableRowOne.addNewTableCell().getParagraphs().get(0);
							tableHeaderTwo.setSpacingBefore(0);
							tableHeaderTwo.setSpacingAfter(0);
							tableHeaderTwo.setVerticalAlignment(TextAlignment.CENTER);
							XWPFRun tableHeaderTwoRun = tableHeaderTwo.createRun();
							tableHeaderTwoRun.setFontSize(9);
							tableHeaderTwoRun.setBold(true);
							tableHeaderTwoRun.setText("Name");

							XWPFParagraph tableHeaderThree = tableRowOne.addNewTableCell().getParagraphs().get(0);
							tableHeaderThree.setSpacingBefore(0);
							tableHeaderThree.setSpacingAfter(0);
							tableHeaderThree.setVerticalAlignment(TextAlignment.CENTER);
							tableHeaderThree.setAlignment(ParagraphAlignment.RIGHT);
							XWPFRun tableHeaderThreeRun = tableHeaderThree.createRun();
							tableHeaderThreeRun.setFontSize(9);
							tableHeaderThreeRun.setBold(true);
							tableHeaderThreeRun.setText("Amt(" + receiptHeaderJson.getString("storeCurrency") + ")");
							
							CTTc cellOne = table.getRow(0).getCell(0).getCTTc();
							CTTcPr tcPr = cellOne.addNewTcPr();
							CTTcBorders border = tcPr.addNewTcBorders();
							border.addNewTop().setVal(STBorder.SINGLE);

							CTTc cellTwo = table.getRow(0).getCell(1).getCTTc();
							CTTcPr tcPr2 = cellTwo.addNewTcPr();
							CTTcBorders border2 = tcPr2.addNewTcBorders();
							border2.addNewTop().setVal(STBorder.SINGLE);

							CTTc cellThree = table.getRow(0).getCell(2).getCTTc();
							CTTcPr tcPr3 = cellThree.addNewTcPr();
							CTTcBorders border3 = tcPr3.addNewTcBorders();
							border3.addNewTop().setVal(STBorder.SINGLE);

							XWPFParagraph tableBottomOne = tableRowTwo.getCell(0).getParagraphs().get(0);
							tableBottomOne.setSpacingBefore(0);
							tableBottomOne.setSpacingAfter(0);
							tableBottomOne.createRun().setFontSize(1);
							
							XWPFParagraph tableBottomTwo = tableRowTwo.addNewTableCell().getParagraphs().get(0);
							tableBottomTwo.setSpacingBefore(0);
							tableBottomTwo.setSpacingAfter(0);
							tableBottomTwo.createRun().setFontSize(1);
							
							XWPFParagraph tableBottomThree = tableRowTwo.addNewTableCell().getParagraphs().get(0);
							tableBottomThree.setSpacingBefore(0);
							tableBottomThree.setSpacingAfter(0);
							tableBottomThree.createRun().setFontSize(1);
							
							int twipsPerInch =  1000;
							tableRowTwo.setHeight((int)(twipsPerInch*1/10)); //set height 1/10 inch.
							tableRowTwo.getCtRow().getTrPr().getTrHeightArray(0).setHRule(STHeightRule.EXACT); //set w:hRule="exact"

							// Grand parent loop
							for (int k = 0; k < jsonGrandParentArray.length(); k++) {
								JSONObject grandParentItem = jsonGrandParentArray.optJSONObject(k);

								XWPFTableRow grandParentTableRow = table.createRow();

								createCellText(grandParentTableRow.getCell(0), grandParentItem.getString("itemQuantity"),
										false, ParagraphAlignment.LEFT);

								createCellText(grandParentTableRow.getCell(1), grandParentItem.getString("itemName"), false,
										ParagraphAlignment.LEFT);

								createCellText(grandParentTableRow.getCell(2),
										grandParentItem.getString("totalAmount").substring(0,
												grandParentItem.getString("totalAmount").length() - 2),
										false, ParagraphAlignment.RIGHT);

								if (grandParentItem.has("parentItemArray")) {
									JSONArray jsonParentArray = grandParentItem.getJSONArray("parentItemArray");

									// Parent loop
									for (int p = 0; p < jsonParentArray.length(); p++) {
										JSONObject parentItem = jsonParentArray.optJSONObject(p);

										String parentItemPrice = (formatDecimalString(parentItem.getString("totalAmount"))
												.equals("0.00")) ? ""
														: formatDecimalString(parentItem.getString("totalAmount"));

										XWPFTableRow parentTableRow = table.createRow();

										createCellText(parentTableRow.getCell(0), "", false, ParagraphAlignment.LEFT);

										createCellText(parentTableRow.getCell(1), "  " + parentItem.getString("itemName"),
												false, ParagraphAlignment.LEFT);

										createCellText(parentTableRow.getCell(2), parentItemPrice, false,
												ParagraphAlignment.RIGHT);

										// Child loop
										if (parentItem.has("childItemArray")) {
											JSONArray jsonChildArray = parentItem.getJSONArray("childItemArray");

											for (int c = 0; c < jsonChildArray.length(); c++) {
												JSONObject childItem = jsonChildArray.optJSONObject(c);

												String childItemPrice = (formatDecimalString(
														childItem.getString("totalAmount")).equals("0.00")) ? ""
																: formatDecimalString(childItem.getString("totalAmount"));

												XWPFTableRow childTableRow = table.createRow();

												createCellText(childTableRow.getCell(0), "", false,
														ParagraphAlignment.LEFT);

												createCellText(childTableRow.getCell(1),
														"    " + childItem.getString("itemName"), false,
														ParagraphAlignment.LEFT);

												createCellText(childTableRow.getCell(2), childItemPrice, false,
														ParagraphAlignment.RIGHT);
											}
										}

									}
								}
							}
							
							//Set the table content bottom line
							CTTc cellBottomOne = table.getRow(2).getCell(0).getCTTc();
							CTTcPr tcBottomPr = cellBottomOne.addNewTcPr();
							CTTcBorders borderBottom = tcBottomPr.addNewTcBorders();
							borderBottom.addNewTop().setVal(STBorder.SINGLE);

							CTTc cellBottomTwo = table.getRow(2).getCell(1).getCTTc();
							CTTcPr tcBottomPr2 = cellBottomTwo.addNewTcPr();
							CTTcBorders borderBottom2 = tcBottomPr2.addNewTcBorders();
							borderBottom2.addNewTop().setVal(STBorder.SINGLE);

							CTTc cellBottomThree = table.getRow(2).getCell(2).getCTTc();
							CTTcPr tcBottomPr3 = cellBottomThree.addNewTcPr();
							CTTcBorders borderBottom3 = tcBottomPr3.addNewTcBorders();
							borderBottom3.addNewTop().setVal(STBorder.SINGLE);

							// 121x20, 28x20, 50x20
							long columnWidths[] = { 560, 2420, 1000 };

							CTTblGrid cttblgridReceiptContent = table.getCTTbl().addNewTblGrid();
							cttblgridReceiptContent.addNewGridCol().setW(new BigInteger("560"));
							cttblgridReceiptContent.addNewGridCol().setW(new BigInteger("2420"));
							cttblgridReceiptContent.addNewGridCol().setW(new BigInteger("1000"));

							for (int x = 0; x < table.getNumberOfRows(); x++) {
								XWPFTableRow row = table.getRow(x);
								int numberOfCell = row.getTableCells().size();
								for (int y = 0; y < numberOfCell; y++) {
									XWPFTableCell cell = row.getCell(y);
									cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(columnWidths[y]));
								}
							}

							XWPFParagraph receiptContentBreak = doc.createParagraph();
							receiptContentBreak.setSpacingAfter(0);
							receiptContentBreak.createRun().addBreak();
							receiptContentBreak.removeRun(0);

							JSONArray taxCharges = receiptContentJson.getJSONArray("taxCharges");
							// Receipt Result
							XWPFTable receiptResultTable = doc.createTable(3 + taxCharges.length() + 2, 2);
							receiptResultTable.getCTTbl().getTblPr().addNewTblLayout().setType(STTblLayoutType.FIXED);
							receiptResultTable.getCTTbl().getTblPr().unsetTblBorders(); // set table no

							List<String> receiptResultLabels = new ArrayList<>();
							receiptResultLabels.add("Subtotal");
							for (int x = 0; x < taxCharges.length(); x++) {
								receiptResultLabels.add(taxCharges.getJSONObject(x).getString("name") + "("
										+ taxCharges.getJSONObject(x).getString("rate") + "%)");
							}
							receiptResultLabels.add("Rounding Adjustment");
							receiptResultLabels.add("Net Total");
							List<String> receiptResultContents = new ArrayList<String>();

							receiptResultContents.add(formatDecimalString(receiptContentJson.getString("totalAmount")));
							for (int x = 0; x < taxCharges.length(); x++) {
								receiptResultContents
										.add(formatDecimalString(taxCharges.getJSONObject(x).getString("chargeAmount")));
							}
							receiptResultContents.add(formatDecimalString(
									receiptContentJson.getString("totalAmountWithTaxRoundingAdjustment")));
							receiptResultContents
									.add(formatDecimalString(receiptContentJson.getString("grandTotalAmount")));

							for (int i = 0; i < receiptResultLabels.size(); i++) {
								XWPFTableRow receiptResultRow = receiptResultTable.getRow(i);

								if (receiptResultLabels.get(i).equals("Net Total")) {
									
									XWPFParagraph netTotalBlankCellOne = receiptResultRow.getCell(0).getParagraphs().get(0);
									netTotalBlankCellOne.setSpacingBefore(0);
									netTotalBlankCellOne.setSpacingAfter(0);
									netTotalBlankCellOne.createRun().setFontSize(1);
									
									XWPFParagraph netTotalBlankCellTwo = receiptResultRow.getCell(1).getParagraphs().get(0);
									netTotalBlankCellTwo.setSpacingBefore(0);
									netTotalBlankCellTwo.setSpacingAfter(0);
									netTotalBlankCellTwo.createRun().setFontSize(1);
									
									receiptResultRow.setHeight((int)(twipsPerInch*1/10));
									receiptResultRow.getCtRow().getTrPr().getTrHeightArray(0).setHRule(STHeightRule.EXACT);
									
									CTTc cell = receiptResultRow.getCell(1).getCTTc();
									CTTcPr tcPr4 = cell.addNewTcPr();
									CTTcBorders border4 = tcPr4.addNewTcBorders();
									//CTBorder temp = border4.addNewTop();
									//border4.addNewBottom().setVal(STBorder.SINGLE);
									border4.addNewBottom().setVal(STBorder.SINGLE);

									createCellText(receiptResultTable.getRow(i+1).getCell(0), receiptResultLabels.get(i), true,
											ParagraphAlignment.LEFT);

									createCellText(receiptResultTable.getRow(i+1).getCell(1), receiptResultContents.get(i), true,
											ParagraphAlignment.RIGHT);
									
									XWPFParagraph netTotalBottomBlankCellOne = receiptResultTable.getRow(i+2).getCell(0).getParagraphs().get(0);
									netTotalBottomBlankCellOne.setSpacingBefore(0);
									netTotalBottomBlankCellOne.setSpacingAfter(0);
									netTotalBottomBlankCellOne.createRun().setFontSize(1);
									
									XWPFParagraph netTotalBottomBlankCellTwo = receiptResultTable.getRow(i+2).getCell(1).getParagraphs().get(0);
									netTotalBottomBlankCellTwo.setSpacingBefore(0);
									netTotalBottomBlankCellTwo.setSpacingAfter(0);
									netTotalBottomBlankCellTwo.createRun().setFontSize(1);
									
									receiptResultTable.getRow(i+2).setHeight((int)(twipsPerInch*1/10));
									receiptResultTable.getRow(i+2).getCtRow().getTrPr().getTrHeightArray(0).setHRule(STHeightRule.EXACT);
									
									CTTc cell5 = receiptResultTable.getRow(i+2).getCell(1).getCTTc();
									CTTcPr tcPr5 = cell5.addNewTcPr();
									CTTcBorders border5 = tcPr5.addNewTcBorders();
									border5.addNewBottom().setVal(STBorder.DOUBLE_D);
									
/*									XWPFParagraph netTotalBottomlessBlankCellOne = receiptResultTable.getRow(i+3).getCell(0).getParagraphs().get(0);
									netTotalBottomBlankCellOne.setSpacingBefore(0);
									netTotalBottomBlankCellOne.setSpacingAfter(0);
									netTotalBottomBlankCellOne.createRun().setFontSize(1);
									
									XWPFParagraph netTotalBottomlessBlankCellTwo = receiptResultTable.getRow(i+3).getCell(1).getParagraphs().get(0);
									netTotalBottomBlankCellTwo.setSpacingBefore(0);
									netTotalBottomBlankCellTwo.setSpacingAfter(0);
									netTotalBottomBlankCellTwo.createRun().setFontSize(1);
									
									receiptResultTable.getRow(i+3).setHeight((int)(twipsPerInch*1/10));
									receiptResultTable.getRow(i+3).getCtRow().getTrPr().getTrHeightArray(0).setHRule(STHeightRule.EXACT);*/


								} else {
									createCellText(receiptResultRow.getCell(0), receiptResultLabels.get(i), false,
											ParagraphAlignment.LEFT);

									createCellText(receiptResultRow.getCell(1), receiptResultContents.get(i), false,
											ParagraphAlignment.RIGHT);
								}
							}
							
							//this is cash payment
							if(receiptContentJson.getInt("paymentMethod") == 1) {
								//create 2 additional row for Cash and Charge
								XWPFTableRow receiptCashRow = receiptResultTable.createRow();
								JSONObject cashData = receiptContentJson.getJSONObject("cashData");

								createCellText(receiptCashRow.getCell(0), "Cash", false,
										ParagraphAlignment.LEFT);

								createCellText(receiptCashRow.getCell(1), formatDecimalString(cashData.getString("cashReceivedAmount")), false,
										ParagraphAlignment.RIGHT);
					
								XWPFTableRow receiptChangeRow = receiptResultTable.createRow();
								
								createCellText(receiptChangeRow.getCell(0), "Change", false,
										ParagraphAlignment.LEFT);

								createCellText(receiptChangeRow.getCell(1), formatDecimalString(cashData.getString("cashChangeAmount")), false,
										ParagraphAlignment.RIGHT);
							}
							
							long receiptResultTableWidths[] = { 2980, 1000 };

							CTTblGrid cttblgridReceiptResult = receiptResultTable.getCTTbl().addNewTblGrid();
							cttblgridReceiptResult.addNewGridCol().setW(new BigInteger("2980"));
							cttblgridReceiptResult.addNewGridCol().setW(new BigInteger("1000"));

							for (int x = 0; x < receiptResultTable.getNumberOfRows(); x++) {
								XWPFTableRow row = receiptResultTable.getRow(x);
								int numberOfCell = row.getTableCells().size();
								for (int y = 0; y < numberOfCell; y++) {
									XWPFTableCell cell = row.getCell(y);
									cell.getCTTc().addNewTcPr().addNewTcW()
											.setW(BigInteger.valueOf(receiptResultTableWidths[y]));
								}
							}
												
							emptyParagraph = doc.createParagraph();
							emptyParagraph.setSpacingAfter(0);
							emptyParagraph.createRun().addBreak();
							emptyParagraph.removeRun(0);

							// Cashless Payment (Coming Soon)
							if (receiptContentJson.getInt("paymentMethod") == 2) {
								XWPFParagraph cashlessHeaderParagraph = doc.createParagraph();
								cashlessHeaderParagraph.setAlignment(ParagraphAlignment.CENTER);
								cashlessHeaderParagraph.setVerticalAlignment(TextAlignment.TOP);
								cashlessHeaderParagraph.setSpacingAfter(0);
								cashlessHeaderParagraph.setSpacingBefore(0);

								XWPFRun runCashlessHeaderParagraph = cashlessHeaderParagraph.createRun();
								runCashlessHeaderParagraph.setText("***Cashless Transaction Information***");

								XWPFTable receiptResultTable2 = doc.createTable(15, 2);
								receiptResultTable2.getCTTbl().getTblPr().addNewTblLayout().setType(STTblLayoutType.FIXED);
								receiptResultTable2.getCTTbl().getTblPr().unsetTblBorders(); // set table no

								JSONObject cardData = receiptContentJson.getJSONObject("cardData");

								List<String> receiptResultLabels2 = Arrays.asList("CARD TYPE", "TID", "MID", "DATE", "TIME",
										"CARD NUM", "EXPIRY DATE", "APPR CODE", "RREF NUM", "BATCH NUM", "INV NUM", "UID",
										"TC", "AID", "APP");
								List<String> receiptResultContents2 = new ArrayList<String>();
								receiptResultContents2.add(cardData.getString("cardType"));
								receiptResultContents2.add(cardData.getString("tid"));
								receiptResultContents2.add(cardData.getString("mid"));
								receiptResultContents2.add(cardData.getString("date"));
								receiptResultContents2.add(cardData.getString("time"));
								receiptResultContents2.add(cardData.getString("maskedCardNo"));
								receiptResultContents2.add(cardData.getString("cardExpiry"));
								receiptResultContents2.add(cardData.getString("approvalCode"));
								receiptResultContents2.add(cardData.getString("rRefNo"));
								receiptResultContents2.add(cardData.getString("batchNo"));
								receiptResultContents2.add(cardData.getString("invoiceNo"));
								receiptResultContents2.add(cardData.getString("uid"));
								receiptResultContents2.add(cardData.getString("tc"));
								receiptResultContents2.add(cardData.getString("aid"));
								receiptResultContents2.add(cardData.getString("app"));

								for (int i = 0; i < receiptResultLabels2.size(); i++) {
									XWPFTableRow receiptResultRow2 = receiptResultTable2.getRow(i);
									createCellText(receiptResultRow2.getCell(0), receiptResultLabels2.get(i), false,
											ParagraphAlignment.LEFT);

									createCellText(receiptResultRow2.getCell(1), receiptResultContents2.get(i), false,
											ParagraphAlignment.RIGHT);
								}

								CTTblGrid cttblgridReceiptResult2 = receiptResultTable2.getCTTbl().addNewTblGrid();
								cttblgridReceiptResult2.addNewGridCol().setW(new BigInteger("1500"));
								cttblgridReceiptResult2.addNewGridCol().setW(new BigInteger("2480"));

								emptyParagraph = doc.createParagraph();
								emptyParagraph.setSpacingAfter(0);
								emptyParagraph.createRun().addBreak();
								emptyParagraph.removeRun(0);

								XWPFParagraph terminalVerificationParagraph = doc.createParagraph();
								terminalVerificationParagraph.setAlignment(ParagraphAlignment.CENTER);
								terminalVerificationParagraph.setVerticalAlignment(TextAlignment.TOP);
								terminalVerificationParagraph.setSpacingAfter(0);
								terminalVerificationParagraph.setSpacingAfterLines(0);
								XWPFRun runTerminalVerificationParagraph = terminalVerificationParagraph.createRun();
								if (cardData.getString("terminalVerification").equals("1")) {
									runTerminalVerificationParagraph.setText("Pin Verified Success,");
									runTerminalVerificationParagraph.addBreak();
									runTerminalVerificationParagraph.setText("No Signature Required");
								} else if (cardData.getString("terminalVerification").equals("2")) {
									runTerminalVerificationParagraph.setText("Signature Required");
								} else if (cardData.getString("terminalVerification").equals("3")) {
									runTerminalVerificationParagraph.setText("No Signature Required");
								}

								emptyParagraph = doc.createParagraph();
								emptyParagraph.setSpacingAfter(0);
								emptyParagraph.createRun().addBreak();
								emptyParagraph.removeRun(0);
							} else if (receiptContentJson.getInt("paymentMethod") == 3) {
								XWPFParagraph cashlessHeaderParagraph = doc.createParagraph();
								cashlessHeaderParagraph.setAlignment(ParagraphAlignment.CENTER);
								cashlessHeaderParagraph.setVerticalAlignment(TextAlignment.TOP);
								cashlessHeaderParagraph.setSpacingAfter(0);
								cashlessHeaderParagraph.setSpacingAfterLines(0);

								XWPFRun runCashlessHeaderParagraph = cashlessHeaderParagraph.createRun();
								runCashlessHeaderParagraph.setText("***Cashless Transaction Information***");

								XWPFTable receiptResultTable2 = doc.createTable(11, 2);
								receiptResultTable2.getCTTbl().getTblPr().addNewTblLayout().setType(STTblLayoutType.FIXED);
								receiptResultTable2.getCTTbl().getTblPr().unsetTblBorders(); // set table no

								JSONObject qrData = receiptContentJson.getJSONObject("qrData");

								List<String> receiptResultLabels2 = null;
								if (qrData.has("amountRMB")) {
									receiptResultLabels2 = Arrays.asList("QR ISSUER", "UID", "TID", "MID", "DATE", "TIME",
											"TRACE NUM", "AUTH NUM", "QR USER ID", "AMOUNT (MYR)", "AMOUNT (RMB)");
								} else {
									receiptResultLabels2 = Arrays.asList("QR ISSUER", "UID", "TID", "MID", "DATE", "TIME",
											"TRACE NUM", "AUTH NUM", "QR USER ID", "AMOUNT (MYR)");
								}
								List<String> receiptResultContents2 = new ArrayList<String>();
								receiptResultContents2.add(qrData.getString("issuerType"));
								receiptResultContents2.add(qrData.getString("uid"));
								receiptResultContents2.add(qrData.getString("tid"));
								receiptResultContents2.add(qrData.getString("mid"));
								receiptResultContents2.add(qrData.getString("date"));
								receiptResultContents2.add(qrData.getString("time"));
								receiptResultContents2.add(qrData.getString("traceNo"));
								receiptResultContents2.add(qrData.getString("authNo"));
								receiptResultContents2.add(qrData.getString("userID"));
								receiptResultContents2.add(formatDecimalString(
										String.valueOf(new Double(qrData.getString("amountMYR")) / 100.0)));
								if (qrData.has("amountRMB")) {
									receiptResultContents2.add(formatDecimalString(
											String.valueOf(new Double(qrData.getString("amountRMB")) / 100.0)));
								}

								for (int i = 0; i < receiptResultLabels2.size(); i++) {
									XWPFTableRow receiptResultRow2 = receiptResultTable2.getRow(i);
									createCellText(receiptResultRow2.getCell(0), receiptResultLabels2.get(i), false,
											ParagraphAlignment.LEFT);

									createCellText(receiptResultRow2.getCell(1), receiptResultContents2.get(i), false,
											ParagraphAlignment.RIGHT);
								}

								CTTblGrid cttblgridReceiptResult2 = receiptResultTable2.getCTTbl().addNewTblGrid();
								cttblgridReceiptResult2.addNewGridCol().setW(new BigInteger("1650"));
								cttblgridReceiptResult2.addNewGridCol().setW(new BigInteger("2330"));

								emptyParagraph = doc.createParagraph();
								emptyParagraph.setSpacingAfter(0);
								emptyParagraph.createRun().addBreak();
								emptyParagraph.removeRun(0);

								byte[] qrByteData = QRGenerate.generateQRImage(qrData.getString("refID"), 300, 300);

								XWPFParagraph qrParagraph = doc.createParagraph();
								qrParagraph.setAlignment(ParagraphAlignment.CENTER);
								qrParagraph.setSpacingAfter(0);
								qrParagraph.setSpacingBefore(0);
								XWPFRun runQrParagraph = qrParagraph.createRun();
								runQrParagraph.addPicture(new ByteArrayInputStream(qrByteData),
										XWPFDocument.PICTURE_TYPE_JPEG, "Generated", Units.toEMU(125), Units.toEMU(125));
							}

							emptyParagraph = doc.createParagraph();
							emptyParagraph.setAlignment(ParagraphAlignment.CENTER);
							emptyParagraph.setSpacingAfter(0);
							emptyParagraph.createRun().setText("Please Come Again");

							// output the result as doc file
							try (FileOutputStream out = new FileOutputStream(
									Paths.get(receiptPath, "receipt.docx").toString())) {
								doc.write(out);
							}

							/*
							 * ByteArrayOutputStream out = new ByteArrayOutputStream(); doc.write(out);
							 * doc.close();
							 * 
							 * XWPFDocument document = new XWPFDocument(new
							 * ByteArrayInputStream(out.toByteArray())); PdfOptions options =
							 * PdfOptions.create(); PdfConverter converter =
							 * (PdfConverter)PdfConverter.getInstance(); converter.convert(document, new
							 * FileOutputStream(new File("C:\\receipt\\receipt.pdf")), options);
							 * 
							 * document.close(); out.close();
							 */
						}

						XWPFDocument document = new XWPFDocument(
								new FileInputStream(new File(Paths.get(receiptPath, "receipt.docx").toString())));
						PdfOptions options = PdfOptions.create();
						OutputStream out = new FileOutputStream(new File(Paths.get(receiptPath, "receipt.pdf").toString()));
						PdfConverter.getInstance().convert(document, out, options);
						document.close();
						out.close();

						// print pdf
						if (myPrintService != null) {
							PDDocument printablePdf = PDDocument
									.load(new File(Paths.get(receiptPath, "receipt.pdf").toString()));
							// PrintService myPrintService = findPrintService("EPSON TM-T82 Receipt");
							// PrintService myPrintService = findPrintService("Posiflex PP6900 Printer");

							PrinterJob job = PrinterJob.getPrinterJob();
							job.setJobName("Receipt - " + receiptContentJson.getString("checkNo"));
							job.setPageable(new PDFPageable(printablePdf));
							job.setPrintService(myPrintService);
							job.print();

							printablePdf.close();
						}

						jsonResult.put(Constant.RESPONSE_CODE, "00");
						jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");

					}
					
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception :", HARDWARE_FOLDER);
			e.printStackTrace();
		}
		return jsonResult;
	}

	// Print Receipt
	private PrintService findPrintService(String printerName) {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		for (PrintService printService : printServices) {
			Logger.writeActivity("Available Printer: " + printService.getName(), ECPOS_FOLDER);
			System.out.println("Available Printer: " + printService.getName());
			if (printService.getName().trim().contains(printerName)) {
				return printService;
			}
		}
		return null;
	}

	private List<CTSectPr> getAllSectPr(XWPFDocument document) {
		List<CTSectPr> allSectPr = new ArrayList<>();
		for (XWPFParagraph paragraph : document.getParagraphs()) {
			if (paragraph.getCTP().getPPr() != null && paragraph.getCTP().getPPr().getSectPr() != null) {
				allSectPr.add(paragraph.getCTP().getPPr().getSectPr());
			}
		}
		allSectPr.add(document.getDocument().getBody().getSectPr());
		return allSectPr;
	}

	private void createCellText(XWPFTableCell cell, String content, boolean isBold, ParagraphAlignment alignment) {
		XWPFParagraph paragraph;
		// If no header is set, use the cell's default paragraph.
		if (cell.getParagraphs().get(0).getRuns().size() == 0) {
			paragraph = cell.getParagraphs().get(0);
		} else {
			paragraph = cell.addParagraph();
		}

		paragraph.setAlignment(alignment);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		XWPFRun run = paragraph.createRun();
		run.setBold(isBold);
		run.setText(content);
		run.setFontSize(9);
	}

	private String formatDecimalString(String amountText) {
		return String.format("%.2f", new BigDecimal(amountText));
	}

	private String formatDecimalString(BigDecimal amountDecimal) {
		return String.format("%.2f", amountDecimal);
	}

	private void addCustomParagraphStyle(XWPFDocument docxDocument, XWPFStyles styles, String strStyleId,
			String fontSize) {
		CTStyle ctStyle = CTStyle.Factory.newInstance();
		ctStyle.setStyleId(strStyleId);

		CTString styleName = CTString.Factory.newInstance();
		styleName.setVal(strStyleId);
		ctStyle.setName(styleName);

		CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
		indentNumber.setVal(BigInteger.valueOf(1));

		// lower number > style is more prominent in the formats bar
		ctStyle.setUiPriority(indentNumber);

		CTOnOff onoffnull = CTOnOff.Factory.newInstance();
		ctStyle.setUnhideWhenUsed(onoffnull);

		// style shows up in the formats bar
		ctStyle.setQFormat(onoffnull);

		CTSpacing lineSpacing = CTSpacing.Factory.newInstance();
		lineSpacing.setBefore(BigInteger.valueOf(0));
		lineSpacing.setBeforeLines(BigInteger.valueOf(0));
		lineSpacing.setAfter(BigInteger.valueOf(0));
		lineSpacing.setAfterLines(BigInteger.valueOf(0));

		// lineSpacing.setLineRule(STLineSpacingRule.EXACT);

		// style defines a heading of the given level
		CTPPr ppr = CTPPr.Factory.newInstance();
		ppr.setSpacing(lineSpacing);
		ctStyle.setPPr(ppr);

		XWPFStyle style = new XWPFStyle(ctStyle);

		CTHpsMeasure size = CTHpsMeasure.Factory.newInstance();
		size.setVal(new BigInteger(fontSize));

		CTFonts fonts = CTFonts.Factory.newInstance();
		fonts.setAscii(RECEIPT_FONT_FAMILY);
		fonts.setHAnsi(RECEIPT_FONT_FAMILY);

		CTRPr rpr = CTRPr.Factory.newInstance();
		rpr.setRFonts(fonts);
		rpr.setSz(size);

		style.getCTStyle().setRPr(rpr);
		// is a null op if already defined

		style.setType(STStyleType.PARAGRAPH);
		styles.addStyle(style);
	}

}
