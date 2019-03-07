package mpay.ecpos_manager.web.restcontrollerbk;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mpay.ecpos_manager.general.constant.Constant;
import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;
import mpay.ecpos_manager.general.utility.UserAuthenticationModel;
import mpay.ecpos_manager.general.utility.UtilWebComponents;

@RestController
@RequestMapping("/ecposmanagerapi/checks")
public class show_checks_RestController {

//	private final static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
//	
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//
//	// SQL Statements
//	private static final String SELECT_MENUDEF_BY_ID_SQL = "SELECT * from menudef WHERE id=?";
//	private static final String SELECT_DETAILS_CHKTTL_BY_CHKSEQ_ORDERBY_TOP1_SQL = "SELECT chk_ttl FROM details WHERE chk_seq = ? ORDER BY dtl_seq DESC LIMIT 1";
//	private static final String SELECT_TENDERTYPE_BY_ID_SQL = "SELECT tender_name FROM tendertype WHERE id =?";
//	private static final String GET_SYS_TABLE_NUMBER_SQL = "SELECT propertyname,table_count,gst_percentage, sales_tax_percentage, service_tax_percentage, other_tax_percentage FROM system";
//	
//	private static final String UPDATE_DETAILS_CHKSEQ_DETAILSEQ_SQL = "UPDATE details SET chk_seq = ?, dtl_seq =? WHERE id = ?";
//	private static final String UPDATE_CHECKS_SQL = "UPDATE checks SET sub_ttl =  ?, tax_ttl =?, sales_tax=?, service_tax=? WHERE chk_num = ?";
//	
//	private static final String INSERT_DETAILS_SQL = "INSERT INTO Details (chk_seq,dtl_seq,number,name,chk_ttl,detail_type,detail_item_price) VALUES (?,?,?,?,?,?,?)";
//	private static final String INSERT_TRANX_SQL = "INSERT INTO transaction (check_no, tran_type ,tran_status, payment_type, amount, performBy) VALUES (?,?,?,?,?,?)";
//	
//	// Half done, not yet handle void
//	@GetMapping("/getcheckdetail/{checkNo}")
//	public ResponseEntity<String> getCheckDetails(@PathVariable("checkNo") String checkNo) {
//		JSONObject jsonResult = new JSONObject();
//		JSONArray item_detail_array = null;
//		String itemDetailsSql = "SELECT *,(SELECT itemcode from menudef where id= number) as 'itemcode'"
//				+ " FROM details where chk_seq=? AND detail_item_status = 0 AND detail_type = 'S' ";
//		String subttlSql = "SELECT ROUND(SUM(detail_item_price), 2) as ttl from details WHERE chk_seq=? AND detail_item_status =0 AND detail_type = 'S' ";
//		try {
//			Map<String, Object> checkInfo = findCheck(checkNo);
//			if (checkInfo.isEmpty())
//				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//
//			jsonResult.put(Constant.RESPONSE_CODE, "00");
//			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//			jsonResult.put("checknumber", (String) checkInfo.get("chk_num"));
//			jsonResult.put("chksequence", (long) checkInfo.get("chk_seq"));
//			jsonResult.put("datetime", (Timestamp) checkInfo.get("createdate"));
//			jsonResult.put("tableno", (int) checkInfo.get("tblno"));
//			jsonResult.put("status", (int) checkInfo.get("chk_open"));
//			jsonResult.put("ttl", (BigDecimal) checkInfo.get("pymnt_ttl"));
//			jsonResult.put("salestax", (BigDecimal) checkInfo.get("tax_ttl"));
//			jsonResult.put("due", (BigDecimal) checkInfo.get("due_ttl"));
//
//			List<Map<String, Object>> itemDetailList = jdbcTemplate.queryForList(itemDetailsSql,
//					new Object[] { (long) checkInfo.get("chk_seq") });
//
//			item_detail_array = new JSONArray();
//
//			if (!itemDetailList.isEmpty()) {
//				for (Map<String, Object> itemDetail : itemDetailList) {
//					JSONObject detail_item = new JSONObject();
//					detail_item.put("itemcode", (String) itemDetail.get("itemcode"));
//					detail_item.put("itemid", (long) itemDetail.get("id"));
//					detail_item.put("itemname", (String) itemDetail.get("name"));
//					detail_item.put("itemprice", (BigDecimal) itemDetail.get("detail_item_price"));
//					detail_item.put("detailtype", (String) itemDetail.get("detail_type"));
//					detail_item.put("itemmenudefid", (int) itemDetail.get("number"));
//
//					item_detail_array.put(detail_item);
//				}
//			}
//
//			jsonResult.put("item_detail_array", item_detail_array);
//
//			Double subtotalHolder = jdbcTemplate.queryForObject(subttlSql,
//					new Object[] { (long) checkInfo.get("chk_seq") }, Double.class);
//			double subtotal = (subtotalHolder == null) ? 0.00 : subtotalHolder.doubleValue();
//			jsonResult.put("subttl", subtotal);
//
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		}
//
//		return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
//	}
//	
//	// @GetMapping("/storebalance/{chkNo}")
//	// public ResponseEntity<String> getStoreBalanceCheck(@PathVariable("chkNo")
//	// String chkNo) {
//	// String selectDetailsSql = "SELECT * FROM details WHERE chk_seq =?";
//	//
//	// JSONObject storeBalanceJsonObject = null;
//	// JSONArray storeBalanceDetailItemsArray = null;
//	// ResponseEntity<String> responseResult = null;
//	// try {
//	// Map<String, Object> check = findCheck(chkNo);
//	//
//	// if (check == null)
//	// return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//	//
//	// List<Map<String, Object>> storeBalanceResultSet =
//	// jdbcTemplate.queryForList(selectDetailsSql,
//	// check.get("chk_num"));
//	//
//	// storeBalanceJsonObject = new JSONObject();
//	// storeBalanceJsonObject.put(Constant.RESPONSE_CODE, "00");
//	// storeBalanceJsonObject.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//	// storeBalanceJsonObject.put("checknumber", (String) check.get("chk_num"));
//	// storeBalanceJsonObject.put("chksequence", (long) check.get("chk_seq"));
//	// storeBalanceJsonObject.put("datetime", (Timestamp) check.get("createdate"));
//	// storeBalanceJsonObject.put("tableno", (int) check.get("tblno"));
//	// storeBalanceJsonObject.put("status", (int) check.get("chk_open"));
//	// storeBalanceJsonObject.put("ttl", (BigDecimal) check.get("pymnt_ttl"));
//	// storeBalanceJsonObject.put("salestax", (BigDecimal) check.get("tax_ttl"));
//	// storeBalanceJsonObject.put("due", (BigDecimal) check.get("due_ttl"));
//	//
//	// storeBalanceDetailItemsArray = new JSONArray();
//	// for (Map<String, Object> storeBalance : storeBalanceResultSet) {
//	// JSONObject detaiItemJsonObject = new JSONObject();
//	// detaiItemJsonObject.put("itemId", (int) storeBalance.get("number"));
//	// detaiItemJsonObject.put("itemName", (String) storeBalance.get("name"));
//	// detaiItemJsonObject.put("itemPrice", (BigDecimal)
//	// storeBalance.get("chk_ttl"));
//	// detaiItemJsonObject.put("detailSequence", (int) storeBalance.get("dtl_seq"));
//	// detaiItemJsonObject.put("detailType", (String)
//	// storeBalance.get("detail_type"));
//	//
//	// storeBalanceDetailItemsArray.put(detaiItemJsonObject);
//	// }
//	//
//	// storeBalanceJsonObject.put("orderedItems", storeBalanceDetailItemsArray);
//	// responseResult = new
//	// ResponseEntity<String>(storeBalanceJsonObject.toString(), HttpStatus.OK);
//	//
//	// } catch (JSONException ex) {
//	// Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() +
//	// ": " + ex.toString(),
//	// ECPOS_ACT_FILENAME, ECPOS_FOLDER);
//	// ex.printStackTrace();
//	// } catch (DataAccessException ex) {
//	// Logger.writeError(Thread.currentThread().getStackTrace()[1].getMethodName() +
//	// ": " + ex.toString(),
//	// ECPOS_ACT_FILENAME, ECPOS_FOLDER);
//	// ex.printStackTrace();
//	// }
//	// return responseResult;
//	// }
//
//	// Done
//	@PostMapping("/create")
//	public ResponseEntity<String> createCheck(@RequestBody String jsonData) {
//		try {
//			Map<String, Object> parsedJsonData = parseJsonStringToMap(jsonData);
//			
//			if (parsedJsonData == null) {
//				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
//			}
//			
//			String staffName = (String) parsedJsonData.get("staff_name");
//			String tableNo = (String) parsedJsonData.get("table_no");
//
//			String staffInfoSql = "SELECT id FROM empldef WHERE username = ?";
//			Map<String, Object> staffResultSet = jdbcTemplate.queryForMap(staffInfoSql, new Object[] { staffName });
//
//			int staffId = (int) staffResultSet.get("id");
//			String masterChecksInfoSql = "SELECT chk_num FROM masterchecks";
//			int masterCheckResultSet = jdbcTemplate.queryForObject(masterChecksInfoSql, Integer.class);
//			int currentChkNo = masterCheckResultSet;
//			int newChkNo = currentChkNo + 1;
//
//			String masterChecksUpdateSql = "UPDATE masterchecks SET chk_num=? WHERE chk_num=?";
//			jdbcTemplate.update(masterChecksUpdateSql, new Object[] { newChkNo, currentChkNo });
//
//			String checksInsertionSql = "INSERT INTO checks(chk_num,empl_id,tblno,storeid,chk_open,sub_ttl,tax_ttl,pymnt_ttl,due_ttl) values (?,?,?,?,2,0,0,0,0)";
//			jdbcTemplate.update(checksInsertionSql, new Object[] { newChkNo, staffId, tableNo, 1 });
//
//			return new ResponseEntity<>(HttpStatus.OK);
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//	}
//
//	@PostMapping("/additem")
//	public ResponseEntity<String> addItemIntoCheck(@RequestBody String jsonData) {
//		try {
//			JSONObject jsonResult = new JSONObject();
//			JSONObject jsonObj = new JSONObject(jsonData);
//			long checkSequence = Long.parseLong(jsonObj.getString("chk_seq"));
//			String checkNumber = jsonObj.getString("check_num");
//			String itemId = jsonObj.getString("item_id");
//			String detailType = jsonObj.getString("detail_type");
//
//			int detailSequence = (getDetailSequence(checkSequence) == 0) ? 1 : getDetailSequence(checkSequence) + 1;
//
//			Map<String, Object> menudefMapResult = jdbcTemplate.queryForMap(SELECT_MENUDEF_BY_ID_SQL, new Object[] { itemId });
//
//			// String sql = "SELECT chk_ttl FROM details WHERE chk_seq = ? ORDER BY dtl_seq
//			// DESC LIMIT 1";
//			//
//			// BigDecimal chkTotal = getChkTtlFromDetail(checkSequence,
//			// SELECT_DETAILS_CHKTTL_BY_CHKSEQ_ORDERBY_TOP1_SQL);
//			// if (chkTotal.equals(BigDecimal.ZERO)) {
//			// chkTotal = (BigDecimal) menudefMapResult.get("price");
//			// } else {
//			// chkTotal = chkTotal.add((BigDecimal) menudefMapResult.get("price"));
//			// }
//
//			KeyHolder keyHolder = new GeneratedKeyHolder();
//
//			// jdbcTemplate.update(INSERT_DETAILS_SQL,
//			// new Object[] { checkSequence, detailSequence++, (int)
//			// menudefMapResult.get("id"),
//			// (String) menudefMapResult.get("name"), menudefMapResult.get("price"),
//			// detailType,
//			// (BigDecimal) menudefMapResult.get("price") });
//
//			jdbcTemplate.update(connection -> {
//				PreparedStatement ps = connection.prepareStatement(INSERT_DETAILS_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
//				ps.setLong(1, checkSequence);
//				ps.setInt(2, detailSequence);
//				ps.setInt(3, (int) menudefMapResult.get("id"));
//				ps.setString(4, (String) menudefMapResult.get("name"));
//				ps.setBigDecimal(5, (BigDecimal) menudefMapResult.get("price"));
//				ps.setString(6, detailType);
//				ps.setBigDecimal(7, (BigDecimal) menudefMapResult.get("price"));
//				return ps;
//			}, keyHolder);
//
//			jsonResult.put(Constant.RESPONSE_CODE, "00");
//			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//			jsonResult.put("generatedItemId", (long) keyHolder.getKey());
//
//			return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//	}
//
//	// private BigDecimal getChkTtlFromDetail(long checkSeq, String sql) {
//	// try {
//	// return jdbcTemplate.queryForObject(sql, new Object[] { checkSeq },
//	// BigDecimal.class);
//	// } catch (EmptyResultDataAccessException ex) {
//	// return BigDecimal.ZERO;
//	// }
//	// }
//
//	// Important
//	@PostMapping("addtransaction")
//	public ResponseEntity<Void> addTransaction(@RequestBody String jsonData) {
//		try {
//			JSONObject jsonObj = new JSONObject(jsonData);
//			String checkNumber = jsonObj.getString("chkNo");
//			long tenderId = jsonObj.getLong("tenderId");
//			BigDecimal amount = BigDecimal.valueOf(jsonObj.getDouble("tranxAmt")).negate();
//			String detailType = jsonObj.getString("detailType");
//
//			long checkSequence = (long) findCheck(checkNumber).get("chk_seq");
//			int detailSequence = (getDetailSequence(checkSequence) == 0) ? 1 : getDetailSequence(checkSequence) + 1;
//
//			String tenderName = jdbcTemplate.queryForObject(SELECT_TENDERTYPE_BY_ID_SQL, new Object[] { tenderId }, String.class);
//
//			jdbcTemplate.update(INSERT_DETAILS_SQL, new Object[] { checkSequence, detailSequence++, (long) tenderId, tenderName, amount, detailType, amount });
//
//			return new ResponseEntity<>(HttpStatus.OK);
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//	}
//
//	// Important
//	@PostMapping("/storebalance")
//	public ResponseEntity<Void> checkStoreBalance(@RequestBody String jsonData) {
//		System.out.println("Store Balance Concern :" + jsonData);
//
//		try {
//			JSONObject jsonObj = new JSONObject(jsonData);
//			String checkNumber = jsonObj.getString("chk_num");
//			long checkSequence = jsonObj.getLong("chk_seq");
//			JSONArray itemList = jsonObj.getJSONArray("store_balance_item_list");
//
//			String detailPriceSql = "SELECT SUM(detail_item_price) AS 'detail_item_price' FROM details WHERE chk_seq =? AND detail_type =? AND detail_item_status = 0";
//
//			// Count service and Sales Tax
//			List<Map<String, Object>> itemDetailMapResultList = jdbcTemplate.queryForList("SELECT * FROM details WHERE chk_seq = ? " + "AND detail_type='S' AND detail_item_status = 0", new Object[] { checkSequence });
//
//			Map<String, Object> settingDataMapResult = jdbcTemplate.queryForMap(GET_SYS_TABLE_NUMBER_SQL);
//			/*
//			 * System.out.println((int) settingDataMapResult.get("table_count"));
//			 * System.out.println((int) settingDataMapResult.get("sales_Tax_Percentage"));
//			 */
//
//			BigDecimal salesTax = BigDecimal.ZERO;
//			BigDecimal serviceTax = BigDecimal.ZERO;
//
//			if (!itemDetailMapResultList.isEmpty() && !settingDataMapResult.isEmpty()) {
//				double salesTaxPercentage = (int) settingDataMapResult.get("sales_Tax_Percentage");
//				double serviceTaxPercentage = (int) settingDataMapResult.get("service_Tax_Percentage");
//
//				salesTaxPercentage = salesTaxPercentage / 100;
//				serviceTaxPercentage = serviceTaxPercentage / 100;
//
//				for (Map<String, Object> itemDetail : itemDetailMapResultList) {
//					Map<String, Object> itemMapResult;
//
//					int itemGroupNumber = (int) itemDetail.get("number");
//
//					if (itemGroupNumber == 0) { // it is an open item
//						itemMapResult = Collections.emptyMap();
//					} else {
//						itemMapResult = jdbcTemplate.queryForMap("SELECT * FROM menudef WHERE id = ?", new Object[] { (int) itemDetail.get("number") });
//					}
//
//					if (!itemMapResult.isEmpty()) {
//						if ((int) itemMapResult.get("sststatus") == 1) {
//							// Goods
//							if ((int) itemMapResult.get("itemtype") == 1) {
//								BigDecimal salesTaxAmt = (BigDecimal) itemDetail.get("detail_item_price");
//								salesTax = salesTax.add(salesTaxAmt.multiply(new BigDecimal(salesTaxPercentage)));
//								// Service
//							} else if ((int) itemMapResult.get("itemtype") == 2) {
//								BigDecimal serviceTaxAmt = (BigDecimal) itemDetail.get("detail_item_price");
//								serviceTax = serviceTax.add(serviceTaxAmt.multiply(new BigDecimal(serviceTaxPercentage)));
//							}
//						}
//					} else {
//						// dealt with open item
//						BigDecimal salesTaxAmt = (BigDecimal) itemDetail.get("detail_item_price");
//						salesTax = salesTax.add(salesTaxAmt.multiply(new BigDecimal(salesTaxPercentage)));
//					}
//				}
//			}
//
//			BigDecimal salesDetailPrices = jdbcTemplate.queryForObject(detailPriceSql, new Object[] { checkSequence, String.valueOf('S') }, BigDecimal.class);
//			BigDecimal transactionDetailPrices = jdbcTemplate.queryForObject(detailPriceSql, new Object[] { checkSequence, String.valueOf('T') }, BigDecimal.class);
//			BigDecimal subTotal = (salesDetailPrices == null) ? BigDecimal.ZERO : salesDetailPrices;
//			// BigDecimal paymentTotal = (transactionDetailPrices == null) ? BigDecimal.ZERO
//			// : transactionDetailPrices;
//			// System.out.println("My Test: " + paymentTotal.toString());
//
//			BigDecimal taxTotal = BigDecimal.ZERO;
//			taxTotal = taxTotal.add(salesTax.add(serviceTax));
//			subTotal = subTotal.add(taxTotal);
//			// BigDecimal dueTotal = BigDecimal.ZERO;
//			// dueTotal = paymentTotal.add(subTotal);
//
//			System.out.println("======");
//			System.out.println(subTotal.toString());
//			// System.out.println(paymentTotal.toString());
//			System.out.println(taxTotal.toString());
//			// System.out.println(dueTotal.toString());
//			System.out.println("======");
//
//			int haha = jdbcTemplate.update(UPDATE_CHECKS_SQL, new Object[] { subTotal, taxTotal, salesTax, serviceTax, checkNumber });
//			// storeBalanceReceiptPrinting(itemList);
//			return new ResponseEntity<>(HttpStatus.OK);
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//	}
//
//	// Important
//	private void storeBalanceReceiptPrinting(JSONArray itemList) {
//		int[] itemIds = new int[itemList.length()];
//		
//		for (int i = 0; i < itemList.length(); ++i) {
//			itemIds[i] = itemList.optInt(i);
//		}
//
//		for (int j : itemIds) {
//			System.out.println("Hyu " + j);
//		}
//	}
//
//	// Important
//	@PostMapping("/addopenitem")
//	public ResponseEntity<String> addOpenItemIntoCheck(@RequestBody String jsonData) {
//		try {
//			JSONObject jsonResult = new JSONObject();
//			JSONObject jsonObj = new JSONObject(jsonData);
//			String chkNo = jsonObj.getString("check_no");
//			Map<String, Object> checkMapResult = findCheck(chkNo);
//			long chkSequence = (long) checkMapResult.get("chk_seq");
//			int detailSequence = (getDetailSequence(chkSequence) == 0) ? 1 : getDetailSequence(chkSequence) + 1;
//			// BigDecimal chkTotal = getChkTtlFromDetail(chkSequence,
//			// SELECT_DETAILS_CHKTTL_BY_CHKSEQ_ORDERBY_TOP1_SQL);
//			BigDecimal price = BigDecimal.valueOf(jsonObj.getDouble("price"));
//
//			// if(chkTotal.equals(BigDecimal.ZERO)) {
//			// chkTotal = price;
//			// }
//			// else {
//			// chkTotal = chkTotal.add(price);
//			// }
//
//			String openItemNameSql = "SELECT name FROM details WHERE chk_seq = ? AND name LIKE ? AND detail_item_status = 0 ORDER BY name DESC LIMIT 1";
//
//			Map<String, Object> detailsMapResult;
//			
//			try {
//				detailsMapResult = jdbcTemplate.queryForMap(openItemNameSql, new Object[] { chkSequence, "OpenItem_%" });
//			} catch (EmptyResultDataAccessException e) {
//				Logger.writeError(e, "EmptyResultDataAccessException: ", ECPOS_FOLDER);
//				detailsMapResult = Collections.emptyMap();
//			}
//
//			String openItemName = "";
//			
//			if (!detailsMapResult.isEmpty()) {
//				openItemName = (String) detailsMapResult.get("name");
//				openItemName = openItemName.substring(openItemName.indexOf("_") + 1, openItemName.length());
//				int seq = Integer.parseInt(openItemName);
//				seq++;
//				openItemName = String.format("OpenItem_%03d", seq);
//			} else {
//				openItemName = "OpenItem_001";
//			}
//			System.out.println(openItemName);
//
//			// jdbcTemplate.update(INSERT_DETAILS_SQL,
//			// new Object[] {chkSequence, detailSequence++, 0, openItemName, price,
//			// String.valueOf('S'), price});
//
//			KeyHolder keyHolder = new GeneratedKeyHolder();
//
//			final String itemName = openItemName;
//			jdbcTemplate.update(connection -> {
//				PreparedStatement ps = connection.prepareStatement(INSERT_DETAILS_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
//				ps.setLong(1, chkSequence);
//				ps.setInt(2, detailSequence);
//				ps.setInt(3, 0);
//				ps.setString(4, itemName);
//				ps.setBigDecimal(5, price);
//				ps.setString(6, String.valueOf('S'));
//				ps.setBigDecimal(7, price);
//				return ps;
//			}, keyHolder);
//
//			jsonResult.put(Constant.RESPONSE_CODE, "00");
//			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//			jsonResult.put("generatedItemId", (long) keyHolder.getKey());
//
//			return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//
//	}
//
//	// Done //Not important
//	@PostMapping("/update")
//	public ResponseEntity<String> updateCheck(@RequestBody String jsonData) {
//		String checkNoSql = "SELECT * FROM checks WHERE chk_num = ?";
//		String insertDetailItemSql = "INSERT INTO Details (chk_seq,dtl_seq,number,name,chk_ttl,detail_type) VALUES (?,?,?,?,?,?)";
//		String updateCheckSql = "UPDATE checks " + "SET sub_ttl =  ?, tax_ttl =?, pymnt_ttl=?, due_ttl = ? WHERE chk_num = ?";
//
//		try {
//			JSONObject jsonCheckData = new JSONObject(jsonData);
//			int updateType = jsonCheckData.getInt("update_type");
//			JSONArray orderedItemJsonArray = jsonCheckData.getJSONArray("ordered_item");
//
//			Map<String, Object> selectedCheck = jdbcTemplate.queryForMap(checkNoSql, new Object[] { jsonCheckData.getString("chk_num") });
//
//			int detailSequenceNo = 1;
//			double subTotal = 0.00;
//			double paymentTotal = 0.00;
//			double taxTotal = 0.00;
//			double dueTotal = 0.00;
//
//			if (updateType == 1) {
//				for (int i = 0; i < orderedItemJsonArray.length(); i++) {
//					int itemId = 0;
//					String itemName = null;
//					double itemPrice = 0.00;
//					String detailType = "";
//
//					JSONObject item = (JSONObject) orderedItemJsonArray.get(i);
//
//					Map<String, Object> selectedMenuDefItem = getMenuDefItem(item.getString("itemname"));
//					if (selectedMenuDefItem.isEmpty()) {
//						itemId = item.getInt("itemid");
//						itemName = item.getString("itemname");
//						itemPrice = item.getDouble("itemprice");
//						detailType = item.getString("detailtype");
//
//						if (detailType.equals("T"))
//							paymentTotal += itemPrice;
//
//					} else {
//						itemId = (int) selectedMenuDefItem.get("id");
//						itemName = (String) selectedMenuDefItem.get("name");
//						BigDecimal itemPriceInDecimal = (BigDecimal) selectedMenuDefItem.get("price");
//						itemPrice = itemPriceInDecimal.doubleValue();
//						detailType = "S";
//					}
//
//					if (detailType.equals("S"))
//						subTotal += itemPrice;
//
//					jdbcTemplate.update(insertDetailItemSql, new Object[] { (long) selectedCheck.get("chk_seq"),
//							detailSequenceNo++, itemId, itemName, itemPrice, detailType });
//				}
//
//				dueTotal = subTotal - paymentTotal;
//
//				jdbcTemplate.update(updateCheckSql, new Object[] { subTotal, taxTotal, paymentTotal, dueTotal,
//						jsonCheckData.getString("chk_num") });
//			}
//			// Add Options
//			else if (updateType == 2) {
//				addItemIntoDetailsList(orderedItemJsonArray, jsonCheckData.getString("chk_num"), updateCheckSql,
//						insertDetailItemSql, (long) selectedCheck.get("chk_seq"), 0, null,
//						(long) selectedCheck.get("chk_seq"));
//			}
//
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//
//		return new ResponseEntity<String>(HttpStatus.OK);
//	}
//
//	private void addItemIntoDetailsList(JSONArray itemArray, String checkNo, String updateCheckSql,
//			String insertDetailItemSql, long checkSeq, int type, String preCheckNo, long preCheckSeq)
//			throws JSONException {
//
//		int detailSequence = (getDetailSequence(preCheckSeq) == 0) ? 1 : getDetailSequence(preCheckSeq) + 1;
//		double subTotal = convertBigDecimalToDouble(getExistingDetailData(preCheckSeq).get("sub_ttl"));
//		double paymentTotal = convertBigDecimalToDouble(getExistingDetailData(preCheckSeq).get("pymnt_ttl"));
//		double taxTotal = convertBigDecimalToDouble(getExistingDetailData(preCheckSeq).get("tax_ttl"));
//		double dueTotal = convertBigDecimalToDouble(getExistingDetailData(preCheckSeq).get("due_ttl"));
//
//		// Special Variable for something
//		double splitSubTotal = 0.00;
//		double splitPaymentTotal = 0.00;
//		double splitTaxTotal = 0.00;
//		double splitDueTotal = 0.00;
//		int splitDetailSequence = (getDetailSequence(checkSeq) == 0) ? 1 : getDetailSequence(checkSeq) + 1;
//		System.out.println("Joker: " + splitDetailSequence);
//
//		int specialUpdateFlag = 0;
//
//		System.out.println("My Sub " + subTotal);
//		System.out.println("My Payment " + paymentTotal);
//
//		for (int i = 0; i < itemArray.length(); i++) {
//
//			// Need to exclude existing one
//
//			/*
//			 * int itemId = 0; String itemName = null; double itemPrice = 0.00; String
//			 * detailType = "";
//			 */
//
//			JSONObject item = (JSONObject) itemArray.get(i);
//
//			if (item.getInt("detailsequence") < 0) {
//				Map<String, Object> selectedMenuDefItem = getMenuDefItem(item.getString("itemname"));
//
//				// int itemId = 0;
//				String itemName = null;
//				double itemPrice = 0.00;
//				String detailType = "";
//				int itemNumber = 0;
//
//				if (selectedMenuDefItem.isEmpty()) {
//					// itemId = item.getInt("itemid");
//					itemName = item.getString("itemname");
//					itemPrice = item.getDouble("itemprice");
//					detailType = item.getString("detailtype");
//					itemNumber = item.getInt("itemmenudefid");
//
//					/*
//					 * if (detailType.equals("T")) paymentTotal += itemPrice;
//					 */
//
//				} else {
//					// itemId = (int) selectedMenuDefItem.get("id");
//					itemNumber = (int) selectedMenuDefItem.get("id");
//					itemName = (String) selectedMenuDefItem.get("name");
//					BigDecimal itemPriceInDecimal = (BigDecimal) selectedMenuDefItem.get("price");
//					itemPrice = itemPriceInDecimal.doubleValue();
//					detailType = "S";
//				}
//
//				if (detailType.equals("T")) {
//					paymentTotal += itemPrice;
//					splitPaymentTotal += itemPrice;
//				}
//
//				if (detailType.equals("S")) {
//					subTotal += itemPrice;
//					splitSubTotal += itemPrice;
//				}
//
//				jdbcTemplate.update(insertDetailItemSql,
//						new Object[] { checkSeq, detailSequence++, itemNumber, itemName, itemPrice, detailType });
//			}
//			// Handling speical number, not itemid
//			// Handling the existing part
//			// Handle Void //Reduce amount
//			if (item.getInt("itemvoidstatus") == 1) {
//				int existingItemId = item.getInt("itemid");
//				voidSelectedDetailItem(existingItemId);
//
//				// if it is of type transcation/sales
//				if (item.getString("detailtype").equals("S")) {
//					System.out.println("B4 Subtract subttl " + subTotal);
//					subTotal -= item.getDouble("itemprice");
//					System.out.println("Subtract " + item.getInt("itemprice"));
//					System.out.println("After " + item.getInt("itemprice"));
//				} else { // of type of transaction
//					paymentTotal -= item.getDouble("itemprice");
//				}
//			}
//
//			if (type == 1) {
//				int existingItemId = item.getInt("itemid");
//				jdbcTemplate.update(UPDATE_DETAILS_CHKSEQ_DETAILSEQ_SQL,
//						new Object[] { checkSeq, splitDetailSequence++, existingItemId });
//
//				// Reduce the amount when transfer
//				if (item.getString("detailtype").equals("S")) {
//					subTotal -= item.getDouble("itemprice");
//					splitSubTotal += item.getDouble("itemprice");
//
//				} else {
//					paymentTotal -= item.getDouble("itemprice");
//					splitPaymentTotal += item.getDouble("itemprice");
//				}
//
//				specialUpdateFlag = 1;
//			}
//
//			// Extra
//			/*
//			 * if (detailType.equals("T")) paymentTotal += itemPrice;
//			 * 
//			 * if (detailType.equals("S")) subTotal += itemPrice;
//			 */
//
//			System.out.println("Advanced subttl" + subTotal);
//			System.out.println("Advanced payment_ttl" + paymentTotal);
//
//		}
//
//		if (specialUpdateFlag == 1) {
//			splitDueTotal = splitSubTotal - splitPaymentTotal;
//			dueTotal = subTotal - paymentTotal;
//
//			// System.out.println("After Split" + splitDueTotal);
//			// System.out.println("Before Split" + dueTotal);
//
//			jdbcTemplate.update(updateCheckSql,
//					new Object[] { splitSubTotal, splitTaxTotal, splitPaymentTotal, splitDueTotal, checkNo });
//
//			// Previous vs current bill
//			jdbcTemplate.update(updateCheckSql,
//					new Object[] { subTotal, taxTotal, paymentTotal, dueTotal, preCheckNo });
//
//		} else {
//			dueTotal = subTotal - paymentTotal;
//			jdbcTemplate.update(updateCheckSql, new Object[] { subTotal, taxTotal, paymentTotal, dueTotal, checkNo });
//		}
//
//	}
//
//	// Done
//	/*
//	 * @PutMapping("/storebalance/update/{chkNo}") public ResponseEntity<String>
//	 * updateStoreBalanceCheck(@PathVariable("chkNo") String chkNo,
//	 * 
//	 * @RequestBody String jsonData) { String insertDetailItemSql =
//	 * "INSERT INTO Details (chk_seq,dtl_seq,number,name,chk_ttl,detail_type) " +
//	 * "VALUES (?,?,?,?,?,?)"; String checkNoSql =
//	 * "SELECT * FROM checks WHERE chk_num = ?"; String updateCheckSql =
//	 * "UPDATE checks " + "SET sub_ttl =  ?, tax_ttl =?, pymnt_ttl=?, due_ttl = ? "
//	 * + "WHERE chk_num = ?";
//	 * 
//	 * ResponseEntity<String> responseResult = null; try {
//	 * 
//	 * Map<String, Object> check = findCheck(chkNo);
//	 * 
//	 * if (check == null) { return new ResponseEntity<>(HttpStatus.NOT_FOUND); }
//	 * 
//	 * JSONObject jsonUpdateCheck = new JSONObject(jsonData); JSONArray
//	 * orderedItemJsonArray = jsonUpdateCheck.getJSONArray("ordered_item");
//	 * 
//	 * // int detailSequenceNo = //
//	 * getDetailSequence(String.valueOf(check.get("dtl_seq")));
//	 * 
//	 * for (int i = 0; i < orderedItemJsonArray.length(); i++) {
//	 * 
//	 * int itemId = 0; String itemName = null; double itemPrice = 0.00;
//	 * 
//	 * JSONObject item = (JSONObject) orderedItemJsonArray.get(i);
//	 * 
//	 * Map<String, Object> selectedMenuDefItem =
//	 * getMenuDefItem(item.getString("item_name")); if (selectedMenuDefItem == null)
//	 * {
//	 * 
//	 * itemName = item.getString("item_name"); itemPrice =
//	 * item.getDouble("item_price"); } else { itemId = (int)
//	 * selectedMenuDefItem.get("id"); itemName = (String)
//	 * selectedMenuDefItem.get("name"); itemPrice = (double)
//	 * selectedMenuDefItem.get("price"); }
//	 * 
//	 * jdbcTemplate.update(insertDetailItemSql, new Object[] { (int)
//	 * check.get("chk_seq"), detailSequenceNo++, itemId, itemName, itemPrice });
//	 * 
//	 * }
//	 * 
//	 * responseResult = new ResponseEntity<>(HttpStatus.OK);
//	 * 
//	 * } catch (Exception ex) { ex.printStackTrace(); }
//	 * 
//	 * return responseResult; }
//	 */
//
//	// Done
//	@PostMapping("/void")
//	public ResponseEntity<Void> voidSelectedCheckItems(@RequestBody String data) {
//		try {
//			JSONArray selectedItemJsonArray = new JSONArray(data);
//			int[] itemIds = new int[selectedItemJsonArray.length()];
//
//			for (int i = 0; i < selectedItemJsonArray.length(); ++i) {
//				itemIds[i] = selectedItemJsonArray.optInt(i);
//			}
//
//			for (int i = 0; i < itemIds.length; i++) {
//				int deletedItemId = itemIds[i];
//				voidSelectedDetailItem(deletedItemId);
//			}
//
//			return new ResponseEntity<>(HttpStatus.OK);
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//	}
//
//	// Done
//	// @PostMapping("/sample")
//	// public ResponseEntity<String> voidSelectedCheckItem(@RequestBody String data)
//	// {
//	//
//	// ResponseEntity<String> responseResult = null;
//	//
//	// try {
//	// Map<String, Object> myData = parseJsonStringToMap(data);
//	// List<Map<String, Object>> arrayX = (List<Map<String, Object>>)
//	// myData.get("MyList");
//	//
//	// for (Map<String, Object> item : arrayX) {
//	// System.out.println((int) item.get("age"));
//	// }
//	// responseResult = new ResponseEntity<String>(HttpStatus.OK);
//	//
//	// } catch (Exception ex) {
//	// ex.printStackTrace();
//	// }
//	//
//	// return responseResult;
//	// }
//
//	@PostMapping("/splitCheck")
//	public ResponseEntity<String> createSplitCheck(@RequestBody String jsonData) {
//
//		String result_chk_num = null;
//		String selectDuplicateCheckNumSql = "SELECT chk_num FROM checks WHERE chk_num LIKE ?";
//		String insertCheckSql = "INSERT INTO checks (chk_num,empl_id,tblno,storeid,chk_open,sub_ttl,tax_ttl,pymnt_ttl,due_ttl) "
//				+ "VALUES (?,?,?,?,2,0,0,0,0)";
//
//		try {
//			JSONObject jsonResult = new JSONObject();
//			JSONObject jsonObj = new JSONObject(jsonData);
//			String chk_num = jsonObj.getString("chk_num");
//			JSONArray selectedItems = jsonObj.getJSONArray("selected_detail_items");
//
//			Map<String, Object> checkMapResult = findCheck(chk_num);
//
//			// Split "123_1" into 123,1 for split check number addition
//			String[] processing_chk_num = chk_num.split("_");
//
//			// For first time, split check
//			if (processing_chk_num.length == 1) {
//				result_chk_num = processing_chk_num[0] + "_" + Integer.toString(1);
//			}
//
//			// Check for duplication
//			List<Map<String, Object>> duplicateChecks = jdbcTemplate.queryForList(selectDuplicateCheckNumSql,
//					new Object[] { processing_chk_num[0] + "%" });
//
//			List<String> list_of_duplicate_chk_num = new ArrayList<String>();
//
//			if (!duplicateChecks.isEmpty()) {
//				for (Map<String, Object> mapItem : duplicateChecks) {
//					String duplicate_chk_num = (String) mapItem.get("chk_num");
//					System.out.println("What my Check num: " + duplicate_chk_num);
//					list_of_duplicate_chk_num.add(duplicate_chk_num);
//				}
//
//				if (list_of_duplicate_chk_num.size() >= 1) {
//					String possibe_duplicate_chk_num = list_of_duplicate_chk_num
//							.get(list_of_duplicate_chk_num.size() - 1);
//					System.out.println("Who are you ? :" + possibe_duplicate_chk_num);
//
//					String[] processing_result_chk_num = possibe_duplicate_chk_num.split("_");
//
//					if (processing_result_chk_num.length > 1) {
//						int chk_counter = Integer.parseInt(processing_result_chk_num[1]) + 1;
//						result_chk_num = processing_result_chk_num[0] + "_" + Integer.toString(chk_counter);
//					}
//				}
//
//			}
//
//			System.out.println("Chk_num me: " + result_chk_num);
//
//			// Insert the newly Created Check no
//			jdbcTemplate.update(insertCheckSql, new Object[] { result_chk_num, (int) checkMapResult.get("empl_id"),
//					(int) checkMapResult.get("tblno"), 1 });
//
//			updateSplitCheckDetailData(selectedItems, result_chk_num);
//
//			// Get the result back to
//			Map<String, Object> splitCheckResult = jdbcTemplate.queryForMap("SELECT * FROM checks WHERE chk_num =?",
//					new Object[] { result_chk_num });
//
//			// addItemIntoDetailsList(selectedItems, result_chk_num, UPDATE_CHECKS_SQL,
//			// INSERT_DETAILS_SQL,
//			// newCheckSequence, 1, jsonObj.getString("chk_num"), (long)
//			// checkMapResult.get("chk_seq"));
//
//			jsonResult.put(Constant.RESPONSE_CODE, "00");
//			jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//			jsonResult.put("check_num", result_chk_num);
//			jsonResult.put("new_check_seq", (long) splitCheckResult.get("chk_seq"));
//			jsonResult.put("pre_check_seq", (long) checkMapResult.get("chk_seq"));
//			return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
//
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//
//	}
//
//	private void updateSplitCheckDetailData(JSONArray jsonArray, String latestChkNo) {
//
//		String updateDetailChkSeqSql = "UPDATE details SET chk_seq = ?, dtl_seq=? WHERE id = ?";
//
//		if (jsonArray.length() > 0) {
//
//			int[] itemIds = new int[jsonArray.length()];
//
//			for (int i = 0; i < jsonArray.length(); i++) {
//				itemIds[i] = jsonArray.optInt(i);
//				System.out.println("Selected ITem Id:" + jsonArray.optInt(i));
//			}
//
//			Map<String, Object> splitCheckMapResult = findCheck(latestChkNo);
//			long newCheckSequence = (long) splitCheckMapResult.get("chk_seq");
//
//			int detailSequence = 1;
//			// double subTotal =
//			// convertBigDecimalToDouble(getExistingDetailData(newCheckSequence).get("sub_ttl"));
//			// double paymentTotal =
//			// convertBigDecimalToDouble(getExistingDetailData(newCheckSequence).get("pymnt_ttl"));
//			// double taxTotal =
//			// convertBigDecimalToDouble(getExistingDetailData(newCheckSequence).get("tax_ttl"));
//			// double dueTotal =
//			// convertBigDecimalToDouble(getExistingDetailData(newCheckSequence).get("due_ttl"));
//
//			for (int itemId : itemIds) {
//				jdbcTemplate.update(updateDetailChkSeqSql, new Object[] { newCheckSequence, detailSequence++, itemId });
//			}
//
//		}
//	}
//
//	@GetMapping("/tender")
//	public ResponseEntity<String> getTenderType() {
//		ResponseEntity<String> responseResult = null;
//
//		try {
//			String getTenderSql = "SELECT * FROM tendertype";
//			List<Map<String, Object>> tenderTypeResult = jdbcTemplate.queryForList(getTenderSql);
//
//			ObjectMapper mapper = new ObjectMapper();
//			ArrayNode arrayNode = mapper.createArrayNode();
//
//			for (Map<String, Object> item : tenderTypeResult) {
//				JsonNode tenderJsonResult = mapper.createObjectNode();
//				((ObjectNode) tenderJsonResult).put("tender_id", (int) item.get("id"));
//				((ObjectNode) tenderJsonResult).put("tender_name", (String) item.get("tender_name"));
//
//				arrayNode.add(tenderJsonResult);
//			}
//
//			String jsonString = arrayNode.toString();
//
//			responseResult = new ResponseEntity<String>(jsonString, HttpStatus.OK);
//
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//		}
//
//		return responseResult;
//	}
//
//	@PostMapping("/payment")
//	public ResponseEntity<String> makePayment(@RequestBody String data, HttpServletRequest request) {
//
//		int userid = 0;
//
//		UtilWebComponents webcomponent = new UtilWebComponents();
//		UserAuthenticationModel session_container_user = webcomponent.getEcposSession(request);
//		if (session_container_user != null) {
//			userid = session_container_user.getUserLoginId();
//		}
//
//		try {
//			JSONObject jsonResult = new JSONObject();
//
//			Map<String, Object> jsonData = parseJsonStringToMap(data);
//			String chkNum = (String) jsonData.get("chk_num");
//			int chkSeq = (int) jsonData.get("chk_seq");
//
//			String detailPriceSql = "SELECT SUM(detail_item_price) AS 'ttl' FROM details WHERE "
//					+ "chk_seq =? AND detail_type =? AND detail_item_status = 0";
//
//			// Query for Sales
//			BigDecimal amtTtl = jdbcTemplate.queryForObject(detailPriceSql,
//					new Object[] { chkSeq, String.valueOf('S') }, BigDecimal.class);
//
//			// Query for Transaction
//			BigDecimal paymentTtl = jdbcTemplate.queryForObject(detailPriceSql,
//					new Object[] { chkSeq, String.valueOf('T') }, BigDecimal.class);
//
//			String tranType = "Sales";
//			String tranStatus = "APPROVED";
//			String paymentType = (String) jsonData.get("payment_type");
//			// BigDecimal amtTtl = (BigDecimal) jsonData.get("selected_items_ttl");
//			// BigDecimal paymentTtl = (BigDecimal) jsonData.get("key_in_amt");
//
//			BigDecimal dueTtl = paymentTtl.add(amtTtl);
//
//			// Queries to calculate the math
//
//			if (!amtTtl.equals(BigDecimal.ZERO) && !paymentTtl.equals(BigDecimal.ZERO)
//					&& !dueTtl.equals(BigDecimal.ZERO)) {
//
//				jdbcTemplate.update(INSERT_TRANX_SQL,
//						new Object[] { chkNum, tranType, tranStatus, paymentType, amtTtl, userid });
//
//				jdbcTemplate.update(UPDATE_CHECKS_SQL,
//						new Object[] { amtTtl, BigDecimal.ZERO, paymentTtl, dueTtl, chkNum });
//
//				// If the balance ady zero or negative, close the ehc
//				if (dueTtl.compareTo(BigDecimal.ZERO) <= 0) {
//					jdbcTemplate.update("UPDATE checks SET chk_open = 3 WHERE chk_num = ?", new Object[] { chkNum });
//				}
//
//				jsonResult.put(Constant.RESPONSE_CODE, "00");
//				jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS");
//			}
//
//			return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
//
//		} catch (Exception e) {
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//
//	}
//
//	@PostMapping("/payment/cash")
//	public ResponseEntity<String> makeCashPayment(@RequestBody String data, HttpServletRequest request) {
//
//		String insertTransactionSql = "INSERT INTO transaction (check_no, tran_type ,tran_status, payment_type, amount, performBy) VALUES (?,?,?,?,?,?)";
//		String updateCheckStatusSql = "UPDATE checks SET chk_open =3 WHERE chk_num = ?";
//		ResponseEntity<String> responseResult = null;
//		UtilWebComponents webcomponent = new UtilWebComponents();
//
//		int userid = 0;
//
//		UserAuthenticationModel session_container_user = webcomponent.getEcposSession(request);
//		if (session_container_user != null) {
//			userid = session_container_user.getUserLoginId();
//		}
//
//		Map<String, Object> jsonData = parseJsonStringToMap(data);
//		int chkNum = Integer.parseInt((String) jsonData.get("chk_num"));
//		String tranType = "Sales";
//		String tranStatus = "APPROVED";
//		String paymentType = (String) jsonData.get("payment_type");
//		double amount = Double.parseDouble(String.valueOf(jsonData.get("selected_items_ttl")));
//
//		// System.out.println("My amount");
//		// System.out.println(amount);
//
//		jdbcTemplate.update(insertTransactionSql,
//				new Object[] { chkNum, tranType, tranStatus, paymentType, amount, userid });
//
//		jdbcTemplate.update(updateCheckStatusSql, new Object[] { chkNum });
//
//		responseResult = new ResponseEntity<String>(HttpStatus.OK);
//
//		return responseResult;
//
//		/*
//		 * int userid = 0; JSONObject jsonResult = new JSONObject(); JSONObject jsonObj
//		 * = null; Connection connection = null; PreparedStatement stmt = null;
//		 * 
//		 * 
//		 * 
//		 * java.sql.Timestamp ourJavaTimestampObject = new
//		 * java.sql.Timestamp(System.currentTimeMillis()); Date date = new
//		 * Date(ourJavaTimestampObject.getTime());
//		 * 
//		 * DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		 * 
//		 * UtilWebComponents webcomponent = new UtilWebComponents();
//		 * 
//		 * try {
//		 * 
//		 * UserAuthenticationModel session_container_user =
//		 * webcomponent.GetUserSession(request); if (session_container_user != null) {
//		 * userid = session_container_user.getUserloginid(); }
//		 * 
//		 * jsonObj = new JSONObject(data); if (jsonObj.has("chk_num") &&
//		 * jsonObj.has("chk_seq") && jsonObj.has("selected_items_ttl") &&
//		 * jsonObj.has("key_in_amt") && jsonObj.has("payment_balance_amt") &&
//		 * jsonObj.has("selected_detail_items") && session_container_user != null) {
//		 * 
//		 * String chk_num = jsonObj.getString("chk_num"); String chk_seq =
//		 * jsonObj.getString("chk_seq"); double amount =
//		 * Double.parseDouble(jsonObj.getString("selected_items_ttl")); double
//		 * key_in_amt = Double.parseDouble(jsonObj.getString("key_in_amt")); double
//		 * balance_amt = Double.parseDouble(jsonObj.getString("payment_balance_amt"));
//		 * JSONArray selected_items = jsonObj.getJSONArray("selected_detail_items");
//		 * 
//		 * int[] itemIds = new int[selected_items.length()];
//		 * 
//		 * for (int i = 0; i < selected_items.length(); i++) { itemIds[i] =
//		 * selected_items.optInt(i); }
//		 * 
//		 * System.out.println("chk_num " + chk_num); System.out.println("chk_seq " +
//		 * chk_seq); System.out.println("amount " + amount);
//		 * System.out.println("key_in_amt " + key_in_amt);
//		 * System.out.println("balance_amt " + balance_amt);
//		 * System.out.println("selected_items " + itemIds);
//		 * 
//		 * connection = dataSource.getConnection();
//		 * 
//		 * for (int j = 0; j < itemIds.length; j++) { stmt =
//		 * connection.prepareStatement("UPDATE details SET payment_status =1 WHERE id=?"
//		 * ); stmt.setInt(1, itemIds[j]); stmt.executeUpdate(); }
//		 * 
//		 * // SELECT the available item for that particular check stmt =
//		 * connection.prepareStatement(
//		 * "SELECT COUNT(id) as detail_count FROM details WHERE chk_seq =? AND detail_item_status = 0 AND payment_status = 0"
//		 * ); stmt.setString(1, chk_seq); ResultSet rs = stmt.executeQuery();
//		 * 
//		 * rs.next(); int detail_counts =
//		 * Integer.parseInt(rs.getString("detail_count"));
//		 * 
//		 * jsonResult.put("detail_counts", detail_counts);
//		 * 
//		 * if (detail_counts == 0) { // No More Item in the check. Close it. stmt =
//		 * connection.prepareStatement("UPDATE checks set chk_open=3 where chk_seq=?");
//		 * stmt.setString(1, chk_seq); stmt.executeUpdate(); }
//		 * 
//		 * stmt =
//		 * connection.prepareStatement("INSERT INTO transaction (check_no, tran_type, "
//		 * +
//		 * "tran_status, payment_type, amount, performBy, tran_datetime) VALUES (?,?,?,?,?,?,?)"
//		 * ); stmt.setString(1, chk_num); stmt.setString(2, "SALES"); stmt.setString(3,
//		 * "APPROVED"); stmt.setString(4, "CASH"); stmt.setDouble(5, amount);
//		 * stmt.setInt(6, userid); stmt.setDate(7, date); stmt.executeUpdate();
//		 * 
//		 * jsonResult.put(Constant.RESPONSE_CODE, "00");
//		 * jsonResult.put(Constant.RESPONSE_MESSAGE, "SUCCESS"); } else {
//		 * jsonResult.put(Constant.RESPONSE_CODE, "01");
//		 * jsonResult.put(Constant.RESPONSE_MESSAGE, "INVALID REQUEST"); } } catch
//		 * (Exception e) { System.out.println("ERROR at : " + e); e.printStackTrace(); }
//		 * finally { if (connection != null) { try { connection.close(); } catch
//		 * (SQLException e) { // TODO Auto-generated catch block e.printStackTrace(); }
//		 * } }
//		 * 
//		 * return jsonResult.toString();
//		 */
//
//	}
//
//	private Map<String, Object> getMenuDefItem(String itemName) {
//		String selectedItemNameSql = "SELECT * FROM menudef WHERE name LIKE ?";
//		try {
//			return jdbcTemplate.queryForMap(selectedItemNameSql, new Object[] { itemName });
//		} catch (EmptyResultDataAccessException e) {
//			Logger.writeError(e, "EmptyResultDataAccessException: ", ECPOS_FOLDER);
//			return Collections.emptyMap();
//		}
//	}
//
//	private Map<String, Object> findCheck(String chkNo) {
//		String findCheckSql = "SELECT * FROM checks WHERE chk_num = ?";
//		try {
//			return jdbcTemplate.queryForMap(findCheckSql, new Object[] { chkNo });
//		} catch (DataAccessException e) {
//			Logger.writeError(e, "DataAccessException: ", ECPOS_FOLDER);
//			return Collections.emptyMap();
//		}
//	}
//
//	private int getDetailSequence(long chkSeq) {
//		String findDetailInfoSql = "SELECT dtl_seq FROM details WHERE chk_seq = ? ORDER BY dtl_seq DESC LIMIT 1";
//		try {
//			return jdbcTemplate.queryForObject(findDetailInfoSql, new Object[] { chkSeq }, Integer.class);
//		} catch (DataAccessException e) {
//			Logger.writeError(e, "DataAccessException: ", ECPOS_FOLDER);
//			return 0;
//		}
//	}
//
//	private Map<String, Object> getExistingDetailData(long chkSeq) {
//		String findDetailInfoSql = "SELECT sub_ttl,tax_ttl,pymnt_ttl,due_ttl FROM checks WHERE chk_seq = ?";
//		try {
//			return jdbcTemplate.queryForMap(findDetailInfoSql, new Object[] { chkSeq });
//		} catch (DataAccessException e) {
//			Logger.writeError(e, "DataAccessException: ", ECPOS_FOLDER);
//			return Collections.emptyMap();
//		}
//	}
//
//	private double convertBigDecimalToDouble(Object targetNumber) {
//		BigDecimal sourceDecimal = (BigDecimal) targetNumber;
//		return sourceDecimal.doubleValue();
//	}
//
//	private void voidSelectedDetailItem(int itemId) {
//		String voidSelectedDetailItemSql = "UPDATE details SET detail_item_status = 1 WHERE id =? AND detail_type = 'S'";
//		jdbcTemplate.update(voidSelectedDetailItemSql, new Object[] { itemId });
//	}
//
//	private Map<String, Object> parseJsonStringToMap(String jsonData) {
//		try {
//			return new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
//			});
//		} catch (IOException e) {
//			Logger.writeError(e, "IOException: ", ECPOS_FOLDER);
//			return Collections.emptyMap();
//		}
//	}

}
