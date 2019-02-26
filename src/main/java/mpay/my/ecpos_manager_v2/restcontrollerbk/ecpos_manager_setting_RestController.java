package mpay.my.ecpos_manager_v2.restcontrollerbk;

import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;

@RestController
@RequestMapping("/settingapi")
public class ecpos_manager_setting_RestController {

//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//
//	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
//
//	private static final String SELECT_PRINTER_SQL = "SELECT * FROM printer";
//	private static final String SELECT_TERMINAL_SQL = "SELECT * FROM terminal";
//	private static final String GET_SYS_TABLE_NUMBER_SQL = "SELECT propertyname,table_count,gst_percentage, sales_tax_percentage, service_tax_percentage, other_tax_percentage FROM system";
//
//	private static final String UPDATE_SYS_TABLE_NUMBER_SQL = "UPDATE system SET propertyname = ?, table_count = ? , gst_percentage = ?, sales_tax_percentage = ?, "
//															+ "service_tax_percentage = ?, other_tax_percentage =? WHERE version = 'alpha1.0'";
//	private static final String UPDATE_PRINTER_TABLE_SQL = "UPDATE printer SET printer_model=?, paper_size = ?, port_name = ?";
//	private static final String UPDATE_TERMINAL_TABLE_SQL = "UPDATE terminal SET terminalName = ?, wifiIP = ?, wifiPort = ? WHERE id = ?";
//
//	private static final String INSERT_PRINTER_SQL = "INSERT INTO printer (printer_model,paper_size,port_name) VALUES(?,?,?)";
//	private static final String INSERT_TERMINAL_SQL = "INSERT INTO terminal (terminalName, wifiIP, wifiPort) VALUES (?,?,?)";
//
//	private static final String DELECT_TERMINAL_SQL = "DELETE FROM terminal WHERE id = ?";
//
//	// Success
//	@GetMapping("/loadsetting")
//	public ResponseEntity<?> loadInitialSetting() throws DataAccessException, JSONException {
//		JSONObject jsonResult = new JSONObject();
//
//		Map<String, Object> loadedSettingResult = jdbcTemplate.queryForMap(GET_SYS_TABLE_NUMBER_SQL);
//
//		jsonResult.put("tableCount", loadedSettingResult.containsKey("table_count") ? (int) loadedSettingResult.get("table_count") : 1);
//		jsonResult.put("propertyName", loadedSettingResult.containsKey("propertyname") ? (String) loadedSettingResult.get("propertyname") : "");
//		jsonResult.put("gstPercentage", loadedSettingResult.containsKey("gst_percentage") ? (int) loadedSettingResult.get("gst_percentage") : 0);
//		jsonResult.put("salesTaxPercentage", loadedSettingResult.containsKey("sales_tax_percentage") ? (int) loadedSettingResult.get("sales_tax_percentage") : 0);
//		jsonResult.put("serviceTaxPercentage", loadedSettingResult.containsKey("service_tax_percentage") ? (int) loadedSettingResult.get("service_tax_percentage") : 0);
//		jsonResult.put("otherTaxPercentage", loadedSettingResult.containsKey("other_tax_percentage") ? (int) loadedSettingResult.get("other_tax_percentage") : 0);
//
//		Logger.writeActivity("SETTING SUCCESSFULLY LOADED", ECPOS_FOLDER);
//		return new ResponseEntity<>(jsonResult.toString(), HttpStatus.OK);
//	}
//
//	@PostMapping("/savesetting")
//	public ResponseEntity<?> saveSetting(@RequestBody String data) throws DataAccessException, JSONException {
//		JSONObject jsonData = new JSONObject(data);
//		
//		if (!jsonData.has("tableCount") && !jsonData.has("propertyName") && !jsonData.has("gstPercentage") 
//				&& !jsonData.has("salesTaxPercentage") && !jsonData.has("serviceTaxPercentage") && !jsonData.has("otherTaxPercentage"))
//			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//
//		int tableCount = jsonData.getInt("tableCount");
//		String propertyName = jsonData.getString("propertyName");
//		int gstPercentage = jsonData.getInt("gstPercentage");
//		int salesTaxPercentage = jsonData.getInt("salesTaxPercentage");
//		int serviceTaxPercentage = jsonData.getInt("serviceTaxPercentage");
//		int otherTaxPercentage = jsonData.getInt("otherTaxPercentage");
//
//		if (jsonData.has("portName") && jsonData.has("printerModel")) {
//			String portName = jsonData.getString("portName");
//			String printerModel = jsonData.getString("printerModel");
//			int paperSize = jsonData.getInt("paperSize");
//
//			if (printerDataExist()) {
//				jdbcTemplate.update(UPDATE_PRINTER_TABLE_SQL, new Object[] { printerModel, paperSize, portName });
//				Logger.writeActivity(printerModel + " had been selected", ECPOS_FOLDER);
//			} else {
//				jdbcTemplate.update(INSERT_PRINTER_SQL, new Object[] { printerModel, paperSize, portName });
//				Logger.writeActivity(printerModel + " had been selected", ECPOS_FOLDER);
//			}
//		}
//
//		jdbcTemplate.update(UPDATE_SYS_TABLE_NUMBER_SQL, new Object[] { propertyName, tableCount, gstPercentage, salesTaxPercentage, serviceTaxPercentage, otherTaxPercentage });
//		Logger.writeActivity("SETTING SUCCESSFULLY SAVED", ECPOS_FOLDER);
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	private boolean printerDataExist() {
//		try {
//			Map<String, Object> printerResultMap = jdbcTemplate.queryForMap(SELECT_PRINTER_SQL);
//			
//			if (printerResultMap.get("printer_model") != null) {
//				return true; // Data Exist
//			} else {
//				return false; // Not Exist
//			}
//		} catch (DataAccessException e) {
//			e.printStackTrace();
//			Logger.writeError(e, "DataAccessException: ", ECPOS_FOLDER);
//			return false; // Not Exist
//		}
//	}
//
//	// Success
//	@PostMapping("/addTerminal")
//	public ResponseEntity<?> addTerminalInfo(@RequestBody String data) {
//		System.out.println("terminal info: " + data);
//
//		try {
//			JSONObject jsonData = new JSONObject(data);
//			
//			if (jsonData.has("terminalName") && jsonData.has("ipAddress") && jsonData.has("port")) {
//				jdbcTemplate.update(INSERT_TERMINAL_SQL, new Object[] { jsonData.getString("terminalName"), jsonData.getString("ipAddress"), jsonData.getString("port") });
//				Logger.writeActivity("Terminal " + jsonData.getString("terminalName") + " Is Added", ECPOS_FOLDER);
//			} else {
//				Logger.writeActivity("Terminal Data Not Found", ECPOS_FOLDER);
//				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	// Success
//	@GetMapping("/getTerminalInfo")
//	public ResponseEntity<?> getTerminalInfo() {
//		JSONObject jsonResult = new JSONObject();
//
//		try {
//			List<Map<String, Object>> terminalResultMaps = jdbcTemplate.queryForList(SELECT_TERMINAL_SQL);
//			JSONArray terminalJsonArray = new JSONArray();
//
//			for (Map<String, Object> terminalResultMap : terminalResultMaps) {
//				JSONObject jsonObj = new JSONObject();
//				jsonObj.put("id", (int) terminalResultMap.get("id"));
//				jsonObj.put("terminalName", (String) terminalResultMap.get("terminalName"));
//				jsonObj.put("wifiIP", (String) terminalResultMap.get("wifiIP"));
//				jsonObj.put("wifiPort", (String) terminalResultMap.get("wifiPort"));
//
//				terminalJsonArray.put(jsonObj);
//			}
//			jsonResult.put("terminalInfo", terminalJsonArray);
//			Logger.writeActivity("Terminal Info List: " + terminalJsonArray.toString(), ECPOS_FOLDER);
//		} catch (Exception e) {
//			e.printStackTrace();
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//		}
//		return new ResponseEntity<String>(jsonResult.toString(), HttpStatus.OK);
//	}
//
//	// Success
//	@DeleteMapping("/removeTerminalInfo/{id}")
//	public ResponseEntity<?> removeTerminalInfo(@PathVariable String id) {
//		if (id == null) {
//			Logger.writeActivity("Terminal Not Found. Therefore Cannot Remove.", ECPOS_FOLDER);
//			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//		}
//		
//		try {
//			jdbcTemplate.update(DELECT_TERMINAL_SQL, new Object[] { id });
//			Logger.writeActivity("Terminal Successfully Removed", ECPOS_FOLDER);
//		} catch (Exception e) {
//			e.printStackTrace();
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	@PostMapping("/editTerminalInfo")
//	public ResponseEntity<?> editTerminalInfo(@RequestBody String data) {
//		try {
//			JSONObject jsonData = new JSONObject(data);
//			jdbcTemplate.update(UPDATE_TERMINAL_TABLE_SQL, new Object[] { jsonData.getString("terminalName"), jsonData.getString("ipAddress"), jsonData.getString("port"), jsonData.getString("id") });
//		} catch (Exception e) {
//			e.printStackTrace();
//			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
}
