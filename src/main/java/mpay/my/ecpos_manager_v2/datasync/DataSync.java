package mpay.my.ecpos_manager_v2.datasync;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataSync {
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
			throw ex;
		} finally {
			if (ps1 != null) {
				ps1.close();
			}
		}
		return flag;
	}
}