package mpay.ecpos_manager.api.datasync;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.json.JSONArray;
import org.json.JSONObject;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;

public class DataSync {
	
	private static String SYNC_FOLDER = Property.getSYNC_FOLDER_NAME();
	
	public static void resetDBMenuData(Connection connection) throws Exception {
		String sqlStatement = "DELETE FROM category;";
		PreparedStatement ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM category_menu_item;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM combo_detail;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM combo_item_detail;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM menu_item;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM menu_item_group;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM menu_item_group_sequence;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM menu_item_modifier_group;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM modifier_group;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();

		sqlStatement = "DELETE FROM modifier_item_sequence;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();
	}
	
	public static void resetDBStoreData(Connection connection) throws Exception {
		String sqlStatement = "DELETE FROM store;";
		PreparedStatement ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();
		
		sqlStatement = "DELETE FROM role_lookup;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();
		
		sqlStatement = "DELETE FROM staff;";
		ps1 = connection.prepareStatement(sqlStatement);
		ps1.executeUpdate();
		ps1.close();
	}
	
	public static boolean insertStoreInfo(Connection connection, JSONObject storeInfo, String imagePath) throws Exception {
		boolean flag = false;
		PreparedStatement ps1 = null;
		try {
			// write to local path for store logo
			String logoPath = storeInfo.getString("logoPath");
			String imageName = logoPath.substring(logoPath.lastIndexOf('/') + 1);
			File imageFile = new File(imagePath, imageName);
			System.out.println("test1:" + logoPath);
			System.out.println("test2:" + imageFile);
			if(imageFile.exists()) {
				System.out.println("in here");
				BufferedInputStream in = new BufferedInputStream(new URL(logoPath).openStream());
				FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
				byte dataBuffer[] = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
					fileOutputStream.write(dataBuffer, 0, bytesRead);
				}
				fileOutputStream.flush();
				fileOutputStream.close();
				in.close();
			}
			
			String sqlStatement = "INSERT INTO store (id, tax_charge_id, backend_id, store_name, store_logo_path, store_address, store_longitude, store_latitude, "
					+ "store_country, store_currency, store_table_count, store_start_operating_time, store_end_operating_time, last_update_date, is_publish, created_date) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";
			ps1 = connection.prepareStatement(sqlStatement);
			int count = 1;
			ps1.setLong(count++, storeInfo.getLong("storeId"));
			ps1.setLong(count++, storeInfo.getLong("taxChargeId"));
			ps1.setString(count++, storeInfo.getString("backEndId"));
			ps1.setString(count++, storeInfo.getString("name"));
			ps1.setString(count++, imageName);
			ps1.setString(count++, storeInfo.getString("address"));
			ps1.setString(count++, storeInfo.getString("longitude"));
			ps1.setString(count++, storeInfo.getString("latitude"));
			ps1.setString(count++, storeInfo.getString("country"));
			ps1.setString(count++, storeInfo.getString("currency"));
			ps1.setString(count++, storeInfo.getString("tableCount"));
			ps1.setString(count++, storeInfo.getString("startOperatingTime"));
			ps1.setString(count++, storeInfo.getString("endOperatingTime"));
			ps1.setString(count++, storeInfo.has("lastUpdateDate")?storeInfo.getString("lastUpdateDate"):null);
			ps1.setLong(count++, storeInfo.getLong("isPublish"));
			ps1.setString(count++, storeInfo.getString("createdDate"));
			int rowAffected = ps1.executeUpdate();
			if(rowAffected != 0) {
				flag = true;
			}
			
		} catch (Exception ex) {
			Logger.writeError(ex, "Exception: ", SYNC_FOLDER);
			throw ex;
		} finally {
			if (ps1 != null) {
				ps1.close();
			}
		}
		return flag;
	}
	
	public static boolean updateSyncDate(Connection connection) throws Exception {
		boolean flag = false;
		PreparedStatement ps1 = null;
		try {		
			String sqlStatement = "INSERT INTO store_db_sync (sync_date) VALUES (NOW()) ";
			ps1 = connection.prepareStatement(sqlStatement);
			int rowAffected = ps1.executeUpdate();
			if(rowAffected != 0) {
				flag = true;
			}
			
		} catch (Exception ex) {
			Logger.writeError(ex, "Exception: ", SYNC_FOLDER);
			throw ex;
		} finally {
			if (ps1 != null) {
				ps1.close();
			}
		}
		return flag;
	}
	
	public static boolean insertStaffRole(Connection connection, JSONArray staffRole) throws Exception {
		boolean flag = false;
		PreparedStatement ps1 = null;
		try {
			String sqlStatement = "INSERT INTO role_lookup (id, role_name) VALUES ";
			for(int a=0; a < staffRole.length(); a++) {
				if(a!=0)
					sqlStatement += ", ";
				sqlStatement += "(?, ?)";	
			}
			
			ps1 = connection.prepareStatement(sqlStatement);		
			int count = 1;
			for(int a=0; a < staffRole.length(); a++) {
				JSONObject obj = staffRole.getJSONObject(a);
				ps1.setLong(count++, obj.getLong("id"));
				ps1.setString(count++, obj.getString("roleName"));
			}
			
			int rowAffected = ps1.executeUpdate();
			if(rowAffected != 0) {
				flag = true;
			}	
		} catch (Exception ex) {
			Logger.writeError(ex, "Exception: ", SYNC_FOLDER);
			throw ex;
		} finally {
			if (ps1 != null) {
				ps1.close();
			}
		}
		return flag;
	}
	
	public static boolean insertStaffInfo(Connection connection, JSONArray staffInfo) throws Exception {
		boolean flag = false;
		PreparedStatement ps1 = null;
		try {
			String sqlStatement = "INSERT INTO staff (id, staff_name, staff_username, staff_password, staff_role, staff_contact_hp_number, staff_contact_email, is_active, created_date, last_update_date) VALUES ";
			for(int a=0; a < staffInfo.length(); a++) {
				if(a!=0)
					sqlStatement += ", ";
				sqlStatement += "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";	
			}
			
			ps1 = connection.prepareStatement(sqlStatement);		
			int count = 1;
			for(int a=0; a < staffInfo.length(); a++) {
				JSONObject obj = staffInfo.getJSONObject(a);
				ps1.setLong(count++, obj.getLong("id"));
				ps1.setString(count++, obj.getString("name"));
				ps1.setString(count++, obj.getString("username"));
				ps1.setString(count++, obj.getString("password"));
				ps1.setLong(count++, obj.getLong("role"));
				ps1.setString(count++, obj.getString("phoneNumber"));
				ps1.setString(count++, obj.getString("email"));
				ps1.setLong(count++, obj.getLong("isActive"));
				ps1.setString(count++, obj.getString("createdDate"));
				ps1.setString(count++, obj.getString("lastUpdateDate").equals("")?null:obj.getString("lastUpdateDate"));
			}
			
			int rowAffected = ps1.executeUpdate();
			if(rowAffected != 0) {
				flag = true;
			}	
		} catch (Exception ex) {
			Logger.writeError(ex, "Exception: ", SYNC_FOLDER);
			throw ex;
		} finally {
			if (ps1 != null) {
				ps1.close();
			}
		}
		return flag;
	}
	
	public static String getCheckData(Connection connection, Timestamp currentDate, Timestamp lastSyncDate) {
		JSONArray jary = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.prepareStatement("select * from `check` where created_date >= ? and created_date < ?;");
			stmt.setTimestamp(1, lastSyncDate);
			stmt.setTimestamp(2, currentDate);
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject check = new JSONObject();
				check.put("check_id", rs.getString("id"));
				check.put("check_number", rs.getString("check_number"));
				check.put("device_type", rs.getString("device_type"));
				check.put("staff_id", rs.getString("staff_id"));
				check.put("order_type", rs.getString("order_type"));
				check.put("table_number", rs.getString("table_number"));
				check.put("total_item_quantity", rs.getString("total_item_quantity"));
				check.put("subtotal_amount", rs.getString("subtotal_amount"));
				check.put("total_tax_amount", rs.getString("total_tax_amount"));
				check.put("total_service_charge_amount", rs.getString("total_service_charge_amount"));
				check.put("total_amount", rs.getString("total_amount"));
				check.put("total_amount_rounding_adjustment", rs.getString("total_amount_rounding_adjustment"));
				check.put("grand_total_amount", rs.getString("grand_total_amount"));
				check.put("deposit_amount", rs.getString("deposit_amount"));
				check.put("tender_amount", rs.getString("tender_amount"));
				check.put("overdue_amount", rs.getString("overdue_amount"));
				check.put("check_status", rs.getString("check_status"));
				check.put("created_date", rs.getString("created_date"));
				check.put("updated_date", rs.getString("updated_date"));
				
				jary.put(check);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", SYNC_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "Exception: ", SYNC_FOLDER);
				e.printStackTrace();
			}
		}
		return jary.toString();
	}
	
	public static String getCheckDetailData(Connection connection, Timestamp currentDate, Timestamp lastSyncDate) {
		JSONArray jary = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.prepareStatement("select * from check_detail where created_date >= ? and created_date < ?;");
			stmt.setTimestamp(1, lastSyncDate);
			stmt.setTimestamp(2, currentDate);
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject checkDetail = new JSONObject();
				checkDetail.put("check_id", rs.getString("check_id"));
				checkDetail.put("check_number", rs.getString("check_number"));
				checkDetail.put("parent_check_detail_id", rs.getString("parent_check_detail_id"));
				checkDetail.put("menu_item_id", rs.getString("menu_item_id"));
				checkDetail.put("menu_item_code", rs.getString("menu_item_code"));
				checkDetail.put("menu_item_name", rs.getString("menu_item_name"));
				checkDetail.put("menu_item_price", rs.getString("menu_item_price"));
				checkDetail.put("tax_rate", rs.getString("tax_rate"));
				checkDetail.put("service_charge_rate", rs.getString("service_charge_rate"));
				checkDetail.put("quantity", rs.getString("quantity"));
				checkDetail.put("subtotal_amount", rs.getString("subtotal_amount"));
				checkDetail.put("total_tax_amount", rs.getString("total_tax_amount"));
				checkDetail.put("total_service_charge_amount", rs.getString("total_service_charge_amount"));
				checkDetail.put("total_amount", rs.getString("total_amount"));
				checkDetail.put("check_detail_status", rs.getString("check_detail_status"));
				checkDetail.put("transaction_id", rs.getString("transaction_id"));
				checkDetail.put("created_date", rs.getString("created_date"));
				checkDetail.put("updated_date", rs.getString("updated_date"));
				
				jary.put(checkDetail);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", SYNC_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "Exception: ", SYNC_FOLDER);
				e.printStackTrace();
			}
		}
		return jary.toString();
	}
	
	public static String getTransactionData(Connection connection, Timestamp currentDate, Timestamp lastSyncDate) {
		JSONArray jary = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.prepareStatement("select * from transaction where created_date >= ? and created_date < ?;");
			stmt.setTimestamp(1, lastSyncDate);
			stmt.setTimestamp(2, currentDate);
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject transaction = new JSONObject();
				transaction.put("staff_id", rs.getString("staff_id"));
				transaction.put("check_id", rs.getString("check_id"));
				transaction.put("check_number", rs.getString("check_number"));
				transaction.put("transaction_type", rs.getString("transaction_type"));
				transaction.put("payment_method", rs.getString("payment_method"));
				transaction.put("payment_type", rs.getString("payment_type"));
				transaction.put("terminal_serial_number", rs.getString("terminal_serial_number"));
				transaction.put("transaction_currency", rs.getString("transaction_currency"));
				transaction.put("transaction_amount", rs.getString("transaction_amount"));
				transaction.put("transaction_tips", rs.getString("transaction_tips"));
				transaction.put("transaction_status", rs.getString("transaction_status"));
				transaction.put("unique_trans_number", rs.getString("unique_trans_number"));
				transaction.put("qr_content", rs.getString("qr_content"));
				transaction.put("created_date", rs.getString("created_date"));
				transaction.put("response_code", rs.getString("response_code"));
				transaction.put("response_message", rs.getString("response_message"));
				transaction.put("updated_date", rs.getString("updated_date"));
				transaction.put("wifi_ip", rs.getString("wifi_ip"));
				transaction.put("wifi_port", rs.getString("wifi_port"));
				transaction.put("approval_code", rs.getString("approval_code"));
				transaction.put("bank_mid", rs.getString("bank_mid"));
				transaction.put("bank_tid", rs.getString("bank_tid"));
				transaction.put("transaction_date", rs.getString("transaction_date"));
				transaction.put("transaction_time", rs.getString("transaction_time"));
				transaction.put("original_invoice_number", rs.getString("original_invoice_number"));
				transaction.put("invoice_number", rs.getString("invoice_number"));
				transaction.put("merchant_info", rs.getString("merchant_info"));
				transaction.put("card_issuer_name", rs.getString("card_issuer_name"));
				transaction.put("masked_card_number", rs.getString("masked_card_number"));
				transaction.put("card_expiry_date", rs.getString("card_expiry_date"));
				transaction.put("batch_number", rs.getString("batch_number"));
				transaction.put("rrn", rs.getString("rrn"));
				transaction.put("card_issuer_id", rs.getString("card_issuer_id"));
				transaction.put("cardholder_name", rs.getString("cardholder_name"));
				transaction.put("aid", rs.getString("aid"));
				transaction.put("app_label", rs.getString("app_label"));
				transaction.put("tc", rs.getString("tc"));
				transaction.put("terminal_verification_result", rs.getString("terminal_verification_result"));
				transaction.put("original_trace_number", rs.getString("original_trace_number"));
				transaction.put("trace_number", rs.getString("trace_number"));
				transaction.put("qr_issuer_type", rs.getString("qr_issuer_type"));
				transaction.put("mpay_mid", rs.getString("mpay_mid"));
				transaction.put("mpay_tid", rs.getString("mpay_tid"));
				transaction.put("qr_ref_id", rs.getString("qr_ref_id"));
				transaction.put("qr_user_id", rs.getString("qr_user_id"));
				transaction.put("qr_amount_myr", rs.getString("qr_amount_myr"));
				transaction.put("qr_amount_rmb", rs.getString("qr_amount_rmb"));
				
				jary.put(transaction);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", SYNC_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "Exception: ", SYNC_FOLDER);
				e.printStackTrace();
			}
		}
		return jary.toString();
	}
	
	public static String getSettlementData(Connection connection, Timestamp currentDate, Timestamp lastSyncDate) {
		JSONArray jary = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.prepareStatement("select * from settlement where created_date >= ? and created_date < ?;");
			stmt.setTimestamp(1, lastSyncDate);
			stmt.setTimestamp(2, currentDate);
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject settlement = new JSONObject();
				settlement.put("staff_id", rs.getString("staff_id"));
				settlement.put("nii_type", rs.getString("nii_type"));
				settlement.put("settlement_status", rs.getString("settlement_status"));
				settlement.put("created_date", rs.getString("created_date"));
				settlement.put("response_code", rs.getString("response_code"));
				settlement.put("response_message", rs.getString("response_message"));
				settlement.put("updated_date", rs.getString("updated_date"));
				settlement.put("wifi_ip", rs.getString("wifi_ip"));
				settlement.put("wifi_port", rs.getString("wifi_port"));
				settlement.put("merchant_info", rs.getString("merchant_info"));
				settlement.put("bank_mid", rs.getString("bank_mid"));
				settlement.put("bank_tid", rs.getString("bank_tid"));
				settlement.put("batch_number", rs.getString("batch_number"));
				settlement.put("transaction_date", rs.getString("transaction_date"));
				settlement.put("transaction_time", rs.getString("transaction_time"));
				settlement.put("batch_total", rs.getString("batch_total"));
				settlement.put("nii", rs.getString("nii"));
				
				jary.put(settlement);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", SYNC_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "Exception: ", SYNC_FOLDER);
				e.printStackTrace();
			}
		}
		return jary.toString();
	}
	
	public static String getUpdatedCheckData(Connection connection, Timestamp currentDate, Timestamp lastSyncDate) {
		JSONArray jary = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.prepareStatement("select * from `check` where updated_date >= ? and updated_date < ?;");
			stmt.setTimestamp(1, lastSyncDate);
			stmt.setTimestamp(2, currentDate);
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject check = new JSONObject();
				check.put("check_id", rs.getString("id"));
				check.put("check_number", rs.getString("check_number"));
				check.put("device_type", rs.getString("device_type"));
				check.put("staff_id", rs.getString("staff_id"));
				check.put("order_type", rs.getString("order_type"));
				check.put("table_number", rs.getString("table_number"));
				check.put("total_item_quantity", rs.getString("total_item_quantity"));
				check.put("subtotal_amount", rs.getString("subtotal_amount"));
				check.put("total_tax_amount", rs.getString("total_tax_amount"));
				check.put("total_service_charge_amount", rs.getString("total_service_charge_amount"));
				check.put("total_amount", rs.getString("total_amount"));
				check.put("total_amount_rounding_adjustment", rs.getString("total_amount_rounding_adjustment"));
				check.put("grand_total_amount", rs.getString("grand_total_amount"));
				check.put("deposit_amount", rs.getString("deposit_amount"));
				check.put("tender_amount", rs.getString("tender_amount"));
				check.put("overdue_amount", rs.getString("overdue_amount"));
				check.put("check_status", rs.getString("check_status"));
				check.put("created_date", rs.getString("created_date"));
				check.put("updated_date", rs.getString("updated_date"));
				
				jary.put(check);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", SYNC_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "Exception: ", SYNC_FOLDER);
				e.printStackTrace();
			}
		}
		return jary.toString();
	}
	
	public static String getUpdatedCheckDetailData(Connection connection, Timestamp currentDate, Timestamp lastSyncDate) {
		JSONArray jary = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.prepareStatement("select * from check_detail where updated_date >= ? and updated_date < ?;");
			stmt.setTimestamp(1, lastSyncDate);
			stmt.setTimestamp(2, currentDate);
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject checkDetail = new JSONObject();
				checkDetail.put("check_id", rs.getString("check_id"));
				checkDetail.put("check_number", rs.getString("check_number"));
				checkDetail.put("parent_check_detail_id", rs.getString("parent_check_detail_id"));
				checkDetail.put("menu_item_id", rs.getString("menu_item_id"));
				checkDetail.put("menu_item_code", rs.getString("menu_item_code"));
				checkDetail.put("menu_item_name", rs.getString("menu_item_name"));
				checkDetail.put("menu_item_price", rs.getString("menu_item_price"));
				checkDetail.put("tax_rate", rs.getString("tax_rate"));
				checkDetail.put("service_charge_rate", rs.getString("service_charge_rate"));
				checkDetail.put("quantity", rs.getString("quantity"));
				checkDetail.put("subtotal_amount", rs.getString("subtotal_amount"));
				checkDetail.put("total_tax_amount", rs.getString("total_tax_amount"));
				checkDetail.put("total_service_charge_amount", rs.getString("total_service_charge_amount"));
				checkDetail.put("total_amount", rs.getString("total_amount"));
				checkDetail.put("check_detail_status", rs.getString("check_detail_status"));
				checkDetail.put("transaction_id", rs.getString("transaction_id"));
				checkDetail.put("created_date", rs.getString("created_date"));
				checkDetail.put("updated_date", rs.getString("updated_date"));
				
				jary.put(checkDetail);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", SYNC_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "Exception: ", SYNC_FOLDER);
				e.printStackTrace();
			}
		}
		return jary.toString();
	}
	
	public static String getUpdatedTransactionData(Connection connection, Timestamp currentDate, Timestamp lastSyncDate) {
		JSONArray jary = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.prepareStatement("select * from transaction where updated_date >= ? and updated_date < ?;");
			stmt.setTimestamp(1, lastSyncDate);
			stmt.setTimestamp(2, currentDate);
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject transaction = new JSONObject();
				transaction.put("staff_id", rs.getString("staff_id"));
				transaction.put("check_id", rs.getString("check_id"));
				transaction.put("check_number", rs.getString("check_number"));
				transaction.put("transaction_type", rs.getString("transaction_type"));
				transaction.put("payment_method", rs.getString("payment_method"));
				transaction.put("payment_type", rs.getString("payment_type"));
				transaction.put("terminal_serial_number", rs.getString("terminal_serial_number"));
				transaction.put("transaction_currency", rs.getString("transaction_currency"));
				transaction.put("transaction_amount", rs.getString("transaction_amount"));
				transaction.put("transaction_tips", rs.getString("transaction_tips"));
				transaction.put("transaction_status", rs.getString("transaction_status"));
				transaction.put("unique_trans_number", rs.getString("unique_trans_number"));
				transaction.put("qr_content", rs.getString("qr_content"));
				transaction.put("created_date", rs.getString("created_date"));
				transaction.put("response_code", rs.getString("response_code"));
				transaction.put("response_message", rs.getString("response_message"));
				transaction.put("updated_date", rs.getString("updated_date"));
				transaction.put("wifi_ip", rs.getString("wifi_ip"));
				transaction.put("wifi_port", rs.getString("wifi_port"));
				transaction.put("approval_code", rs.getString("approval_code"));
				transaction.put("bank_mid", rs.getString("bank_mid"));
				transaction.put("bank_tid", rs.getString("bank_tid"));
				transaction.put("transaction_date", rs.getString("transaction_date"));
				transaction.put("transaction_time", rs.getString("transaction_time"));
				transaction.put("original_invoice_number", rs.getString("original_invoice_number"));
				transaction.put("invoice_number", rs.getString("invoice_number"));
				transaction.put("merchant_info", rs.getString("merchant_info"));
				transaction.put("card_issuer_name", rs.getString("card_issuer_name"));
				transaction.put("masked_card_number", rs.getString("masked_card_number"));
				transaction.put("card_expiry_date", rs.getString("card_expiry_date"));
				transaction.put("batch_number", rs.getString("batch_number"));
				transaction.put("rrn", rs.getString("rrn"));
				transaction.put("card_issuer_id", rs.getString("card_issuer_id"));
				transaction.put("cardholder_name", rs.getString("cardholder_name"));
				transaction.put("aid", rs.getString("aid"));
				transaction.put("app_label", rs.getString("app_label"));
				transaction.put("tc", rs.getString("tc"));
				transaction.put("terminal_verification_result", rs.getString("terminal_verification_result"));
				transaction.put("original_trace_number", rs.getString("original_trace_number"));
				transaction.put("trace_number", rs.getString("trace_number"));
				transaction.put("qr_issuer_type", rs.getString("qr_issuer_type"));
				transaction.put("mpay_mid", rs.getString("mpay_mid"));
				transaction.put("mpay_tid", rs.getString("mpay_tid"));
				transaction.put("qr_ref_id", rs.getString("qr_ref_id"));
				transaction.put("qr_user_id", rs.getString("qr_user_id"));
				transaction.put("qr_amount_myr", rs.getString("qr_amount_myr"));
				transaction.put("qr_amount_rmb", rs.getString("qr_amount_rmb"));
				
				jary.put(transaction);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", SYNC_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "Exception: ", SYNC_FOLDER);
				e.printStackTrace();
			}
		}
		return jary.toString();
	}
	
	public static String getUpdatedSettlementData(Connection connection, Timestamp currentDate, Timestamp lastSyncDate) {
		JSONArray jary = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.prepareStatement("select * from settlement where updated_date >= ? and updated_date < ?;");
			stmt.setTimestamp(1, lastSyncDate);
			stmt.setTimestamp(2, currentDate);
			rs = stmt.executeQuery();

			while (rs.next()) {
				JSONObject settlement = new JSONObject();
				settlement.put("staff_id", rs.getString("staff_id"));
				settlement.put("nii_type", rs.getString("nii_type"));
				settlement.put("settlement_status", rs.getString("settlement_status"));
				settlement.put("created_date", rs.getString("created_date"));
				settlement.put("response_code", rs.getString("response_code"));
				settlement.put("response_message", rs.getString("response_message"));
				settlement.put("updated_date", rs.getString("updated_date"));
				settlement.put("wifi_ip", rs.getString("wifi_ip"));
				settlement.put("wifi_port", rs.getString("wifi_port"));
				settlement.put("merchant_info", rs.getString("merchant_info"));
				settlement.put("bank_mid", rs.getString("bank_mid"));
				settlement.put("bank_tid", rs.getString("bank_tid"));
				settlement.put("batch_number", rs.getString("batch_number"));
				settlement.put("transaction_date", rs.getString("transaction_date"));
				settlement.put("transaction_time", rs.getString("transaction_time"));
				settlement.put("batch_total", rs.getString("batch_total"));
				settlement.put("nii", rs.getString("nii"));
				
				jary.put(settlement);
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", SYNC_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
			} catch (SQLException e) {
				Logger.writeError(e, "Exception: ", SYNC_FOLDER);
				e.printStackTrace();
			}
		}
		return jary.toString();
	}
	
	public static boolean insertTransactionSyncRecord(Connection connection, String resultCode, String resultMessage) {
		boolean flag = false;
		PreparedStatement stmt = null;
		
		try {
			stmt = connection.prepareStatement("insert into check_transaction_settlement_cloud_sync (sync_date,response_code,response_message) values (now(),?,?);");
			stmt.setString(1, resultCode);
			stmt.setString(2, resultMessage);
			int rs = stmt.executeUpdate();
			
			if(rs > 0) {
				flag = true;
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", SYNC_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				Logger.writeError(e, "Exception: ", SYNC_FOLDER);
				e.printStackTrace();
			}
		}
		return flag;
	}
}