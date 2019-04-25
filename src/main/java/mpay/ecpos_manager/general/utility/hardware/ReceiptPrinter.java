package mpay.ecpos_manager.general.utility.hardware;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.BreakClear;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import mpay.ecpos_manager.general.constant.Constant;
import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.UserAuthenticationModel;
import mpay.ecpos_manager.general.utility.WebComponents;

@Service
public class ReceiptPrinter {

	private static String HARDWARE_FOLDER = Property.getHARDWARE_FOLDER_NAME();
	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();

	@Autowired
	DataSource dataSource;

	private static final String RECEIPT_FONT_FAMILY = "Arial";

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
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		ResultSet rsA = null;

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

	private JSONObject printReceiptData(String staffName, JSONObject receiptHeaderJson, JSONObject receiptContentJson,
			JSONObject receiptFooterJson) {
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
					new File("C:/receipt").mkdirs();

					try (XWPFDocument doc = new XWPFDocument()) {
						if (doc.getStyles() == null) {
							XWPFStyles styles = doc.createStyles();

							//set default font
							CTFonts fonts = CTFonts.Factory.newInstance();
							fonts.setAscii(RECEIPT_FONT_FAMILY);
							styles.setDefaultFonts(fonts);
						}

						CTDocument1 ctdoc = doc.getDocument();
						CTBody ctbody = ctdoc.getBody();
						if (!ctbody.isSetSectPr()) {
							ctbody.addNewSectPr();
						}
						CTSectPr section = ctbody.getSectPr();

						if (!section.isSetPgSz()) {
							section.addNewPgSz();
						}
						CTPageSz pageSize = section.getPgSz();
						pageSize.setOrient(STPageOrientation.PORTRAIT);
						// 226 point x 20
						pageSize.setW(BigInteger.valueOf(4520));
						// 641
						pageSize.setH(BigInteger.valueOf(16820));

						//System.out.println(section.getPgSz());

						// Set Margin
						CTPageMar pageMar = section.addNewPgMar();
						pageMar.setGutter(BigInteger.valueOf(0));
						pageMar.setHeader(BigInteger.valueOf(720L));
						pageMar.setFooter(BigInteger.valueOf(720L));
						pageMar.setLeft(BigInteger.valueOf(280L));
						pageMar.setTop(BigInteger.valueOf(280L));
						pageMar.setRight(BigInteger.valueOf(280L));
						pageMar.setBottom(BigInteger.valueOf(560L));

						// Header Store Name
						XWPFParagraph headerStoreNameParagraph = doc.createParagraph();
						headerStoreNameParagraph.setAlignment(ParagraphAlignment.CENTER);
						headerStoreNameParagraph.setVerticalAlignment(TextAlignment.TOP);
						headerStoreNameParagraph.setSpacingAfter(0);

						XWPFRun runHeaderStoreNameParagraph = headerStoreNameParagraph.createRun();
						runHeaderStoreNameParagraph.setBold(true);
						runHeaderStoreNameParagraph.setFontSize(12);
						runHeaderStoreNameParagraph.setText(receiptHeaderJson.getString("storeName"));

						// Header Store Address and other info					
						XWPFParagraph headerStoreAddressParagraph = doc.createParagraph();
						headerStoreAddressParagraph.setAlignment(ParagraphAlignment.CENTER);
						headerStoreAddressParagraph.setSpacingAfter(0);

						XWPFRun runHeaderStoreAddressParagraph = headerStoreAddressParagraph.createRun();
						runHeaderStoreAddressParagraph.setFontSize(9);
						runHeaderStoreAddressParagraph.setText(receiptHeaderJson.getString("storeAddress"));
						runHeaderStoreAddressParagraph.addBreak();
						runHeaderStoreAddressParagraph.setText("Contact No: " + receiptHeaderJson.getString("storeContactHpNumber"));
						runHeaderStoreAddressParagraph.addBreak();
						
						// Receipt Info Table
						XWPFTable receiptInfoTable = doc.createTable(4, 2);
						CTTblLayoutType receiptInfoTableType = receiptInfoTable.getCTTbl().getTblPr().addNewTblLayout(); // set
																															// Layout
						receiptInfoTableType.setType(STTblLayoutType.FIXED);
						receiptInfoTable.getCTTbl().getTblPr().unsetTblBorders(); // set table no border

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						
						List<String> receiptInfoLabels = Arrays.asList("Check No", "Order At", "Printed At","Staff");
						List<String> receiptInfoContents = Arrays.asList(receiptContentJson.getString("checkNo"),
								receiptContentJson.getString("createdDate"), sdf.format(new Date()),staffName);

						for (int i = 0; i < receiptInfoLabels.size(); i++) {
							XWPFTableRow receiptInfoRow = receiptInfoTable.getRow(i);
							createCellText(receiptInfoRow.getCell(0), receiptInfoLabels.get(i), false,
									ParagraphAlignment.LEFT);
							createCellText(receiptInfoRow.getCell(1), receiptInfoContents.get(i), false,
									ParagraphAlignment.LEFT);
						}

						long receiptInfoTableWidths[] = { 1400, 2000 };

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
						receiptInfoBreak.createRun().addBreak();
						receiptInfoBreak.removeRun(0);

						// Receipt Content
						XWPFTable table = doc.createTable();
						CTTblLayoutType type = table.getCTTbl().getTblPr().addNewTblLayout(); // set Layout
						type.setType(STTblLayoutType.FIXED);

						table.getCTTbl().getTblPr().unsetTblBorders(); // set table no border

						// First row header
						XWPFTableRow tableRowOne = table.getRow(0);

						XWPFParagraph tableHeaderOne = tableRowOne.getCell(0).getParagraphs().get(0);
						tableHeaderOne.setSpacingAfter(0);
						XWPFRun tableHeaderOneRun = tableHeaderOne.createRun();
						tableHeaderOneRun.setFontSize(9);
						//tableHeaderOneRun.setBold(true);
						tableHeaderOneRun.setText("Qty");

						XWPFParagraph tableHeaderTwo = tableRowOne.addNewTableCell().getParagraphs().get(0);
						tableHeaderTwo.setSpacingAfter(0);
						XWPFRun tableHeaderTwoRun = tableHeaderTwo.createRun();
						tableHeaderTwoRun.setFontSize(9);
						//tableHeaderTwoRun.setBold(true);
						tableHeaderTwoRun.setText("Name");

						XWPFParagraph tableHeaderThree = tableRowOne.addNewTableCell().getParagraphs().get(0);
						tableHeaderThree.setSpacingAfter(0);
						tableHeaderThree.setAlignment(ParagraphAlignment.RIGHT);
						XWPFRun tableHeaderThreeRun = tableHeaderThree.createRun();
						tableHeaderThreeRun.setFontSize(9);
						//tableHeaderThreeRun.setBold(true);
						tableHeaderThreeRun.setText("Amt(" + receiptHeaderJson.getString("storeCurrency") + ")");

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

									String parentItemPrice = (formatDecimalString(
											parentItem.getString("totalAmount")).equals("0.00")) ? ""
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

						// 128x20, 28x20, 43x20
						long columnWidths[] = {560, 2560 ,860};

						for (int x = 0; x < table.getNumberOfRows(); x++) {
							XWPFTableRow row = table.getRow(x);
							int numberOfCell = row.getTableCells().size();
							for (int y = 0; y < numberOfCell; y++) {
								XWPFTableCell cell = row.getCell(y);
								cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(columnWidths[y]));
							}
						}
						
						setTableTopBottomBorder(table.getRow(0).getCell(0).getCTTc());
						setTableTopBottomBorder(table.getRow(0).getCell(1).getCTTc());
						setTableTopBottomBorder(table.getRow(0).getCell(2).getCTTc());

						XWPFParagraph receiptContentBreak = doc.createParagraph();
						receiptContentBreak.createRun().addBreak();
						receiptContentBreak.removeRun(0);

						// Receipt Result
						  XWPFTable receiptResultTable = doc.createTable(5,2);
						  receiptResultTable.getCTTbl().getTblPr().addNewTblLayout().setType(STTblLayoutType.FIXED);
						  receiptResultTable.getCTTbl().getTblPr().unsetTblBorders(); // set table no
						  
						  List<String> receiptResultLabels = Arrays.asList("Subtotal","Service Charge","Tax Charge","Rounding Adjustment","Net Total"); 
						  List<String> receiptResultContents = new ArrayList<String>();
						  
						  receiptResultContents.add(formatDecimalString(receiptContentJson.getString("totalAmount")));
						  receiptResultContents.add(formatDecimalString("0.00"));
						  receiptResultContents.add(formatDecimalString("0.00"));
						  receiptResultContents.add(formatDecimalString(receiptContentJson.getString("totalAmountWithTaxRoundingAdjustment")));
						  receiptResultContents.add(formatDecimalString(receiptContentJson.getString("grandTotalAmount")));
						  
							for (int i = 0; i < receiptResultLabels.size(); i++) {
								XWPFTableRow receiptResultRow = receiptResultTable.getRow(i);
								
								if(receiptResultLabels.get(i).equals("Net Total")) {
									createCellText(receiptResultRow.getCell(0), receiptResultLabels.get(i), true,
											ParagraphAlignment.LEFT);

									createCellText(receiptResultRow.getCell(1), receiptResultContents.get(i), true,
											ParagraphAlignment.RIGHT);
									
									CTTc cell = receiptResultRow.getCell(1).getCTTc();
									CTTcPr tcPr = cell.addNewTcPr(); 
									CTTcBorders border = tcPr.addNewTcBorders();
							
									border.addNewBottom().setVal(STBorder.DASHED);
									border.addNewTop().setVal(STBorder.DASHED);
								} else {
									createCellText(receiptResultRow.getCell(0), receiptResultLabels.get(i), false,
											ParagraphAlignment.LEFT);
									
									createCellText(receiptResultRow.getCell(1), receiptResultContents.get(i), false,
											ParagraphAlignment.RIGHT);
								}
							}

						  //long receiptResultTableWidths[] = { 1990, 1990 };
						  long receiptResultTableWidths[] = { 3120, 860 };
						  
						  for (int x = 0; x < receiptResultTable.getNumberOfRows(); x++) { 
							  XWPFTableRow row = receiptResultTable.getRow(x); 
							  int numberOfCell = row.getTableCells().size(); 
							  for (int y = 0; y < numberOfCell; y++) {
								  XWPFTableCell cell = row.getCell(y);
								  cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(
										  receiptResultTableWidths[y])); 
							  } 
						  }
						  
				 
						// Cashless Payment (Coming Soon)

						XWPFRun finalParagraph = doc.createParagraph().createRun();
						finalParagraph.addCarriageReturn();

						// output the result as doc file
						try (FileOutputStream out = new FileOutputStream("C:\\receipt\\receipt.docx")) {
							doc.write(out);
						}

					}

				}

			}

			/*
			 * XWPFDocument document = new XWPFDocument(new FileInputStream(new
			 * File("C:\\XX\\document.docx"))); File outFile = new File(
			 * "C:\\XX\\document.pdf" ); OutputStream out = new FileOutputStream( outFile );
			 * PdfOptions options = PdfOptions.create().fontEncoding( "windows-1250" );
			 * PdfConverter.getInstance().convert(document, out, options );
			 * 
			 * File file = new File("C:\\XX\\document.docx");
			 */

		} catch (Exception e) {
			Logger.writeError(e, "Exception :", HARDWARE_FOLDER);
			e.printStackTrace();
		}

		return jsonResult;
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
	
	private void setTableTopBottomBorder(CTTc cell) {
		  CTTcPr tcPr = cell.addNewTcPr(); 
		  CTTcBorders border = tcPr.addNewTcBorders();
		  
		  border.addNewBottom().setVal(STBorder.DASH_SMALL_GAP);
		  //border.addNewRight().setVal(STBorder.SINGLE);
		  //border.addNewLeft().setVal(STBorder.SINGLE);
		  border.addNewTop().setVal(STBorder.DASH_SMALL_GAP);
	}

	private String formatDecimalString(String amountText) {
		return String.format("%.2f", new BigDecimal(amountText));
	}
	
	private String formatDecimalString(BigDecimal amountDecimal) {
		return String.format("%.2f", amountDecimal);
	}

}
