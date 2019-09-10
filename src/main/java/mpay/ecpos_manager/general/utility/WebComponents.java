package mpay.ecpos_manager.general.utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import mpay.ecpos_manager.general.logger.Logger;
import mpay.ecpos_manager.general.property.Property;

@Component
public class WebComponents {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	public UserAuthenticationModel performEcposAuthentication(String username, String password, DataSource dataSource, String key) {
		UserAuthenticationModel domainContainer = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			connection = dataSource.getConnection();

			String query = "SELECT * FROM staff WHERE staff_username = ? and is_active = 1";
			stmt = connection.prepareStatement(query);
			stmt.setString(1, username);
			rs = stmt.executeQuery();

			if (rs.next()) {
				BCryptPasswordEncoder pass_encode = new BCryptPasswordEncoder();
				String public_password = password;
				String private_password = pass_encode.encode(AesEncryption.decrypt(key, rs.getString("staff_password")));

				if (pass_encode.matches(public_password, private_password)) {
					domainContainer = new UserAuthenticationModel();
					domainContainer.setUserLoginId(Integer.parseInt(rs.getString("id")));
					domainContainer.setName(rs.getString("staff_name"));
					domainContainer.setUsername(rs.getString("staff_username"));
					domainContainer.setRoleType(Integer.parseInt(rs.getString("staff_role")));
					
					stmt2 = connection.prepareStatement("select * from store;");
					rs2 = stmt2.executeQuery();

					if (rs2.next()) {
						domainContainer.setStoreType(rs2.getInt("store_type_id"));
						domainContainer.setTakeAwayFlag(rs2.getBoolean("ecpos_takeaway_detail_flag"));
					}
					
					domainContainer.setDeviceId(Long.parseLong(getGeneralConfig(connection, "DEVICE_ID")));
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return domainContainer;
	}
	
	public UserAuthenticationModel performEcposQRAuthentication(String qrContent, DataSource dataSource, String key) {
		UserAuthenticationModel domainContainer = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			String decryptedStr = AesEncryption.decrypt(key, qrContent);
			String[] decryptedDataPart = decryptedStr.split(String.valueOf((char)28));
			if (decryptedDataPart.length == 3) {
				connection = dataSource.getConnection();

				String query = "SELECT id, store_type_id FROM store;";
				stmt = connection.prepareStatement(query);
				rs = stmt.executeQuery();

				if (rs.next()) {
					if (rs.getLong("id") == Long.parseLong(decryptedDataPart[0])) {
						stmt2 = connection.prepareStatement("SELECT * FROM staff WHERE staff_username = ? and is_active = 1;");
						stmt2.setString(1, decryptedDataPart[1]);
						rs2 = stmt2.executeQuery();

						if (rs2.next()) {
							if (rs2.getString("staff_password").equals(decryptedDataPart[2])) {
								domainContainer = new UserAuthenticationModel();
								domainContainer.setStoreType(rs.getInt("store_type_id"));
								domainContainer.setUserLoginId(Integer.parseInt(rs2.getString("id")));
								domainContainer.setName(rs2.getString("staff_name"));
								domainContainer.setUsername(rs2.getString("staff_username"));
								domainContainer.setRoleType(Integer.parseInt(rs2.getString("staff_role")));
							} else {
								domainContainer = null;
							}
						}
					} else {
						System.out.println("Invalid Store");
						domainContainer = null;
					}
				}
			}
		} catch (Exception e) {
			domainContainer = null;
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (stmt2 != null) stmt2.close();
				if (rs != null) {rs.close();rs = null;}
				if (rs2 != null) {rs2.close();rs2 = null;}
				if (connection != null) {connection.close();}
			} catch (SQLException e) {
				Logger.writeError(e, "SQLException :", ECPOS_FOLDER);
				e.printStackTrace();
			}
		}
		return domainContainer;
	}

	public void clearEcposSession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
	}

	public UserAuthenticationModel getEcposSession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		UserAuthenticationModel domainContainer = (UserAuthenticationModel) session.getAttribute("session_user");
		return domainContainer;
	}
	
	public String getGeneralConfig(Connection connection, String parameter) throws Exception {
		String value = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {	
			stmt = connection.prepareStatement("SELECT * FROM general_configuration WHERE parameter = ?");
			stmt.setString(1, parameter);
			rs = stmt.executeQuery();

			if(rs.next()) {
				value = rs.getString("value");		
			}
		}catch(Exception e) {
			Logger.writeError(e, "SQLException: ", ECPOS_FOLDER);
			e.printStackTrace();
			throw e;
		} 
		return value;
	}
	
	public boolean updateGeneralConfig(Connection connection, String parameter, String value) throws Exception {
		boolean flag = false;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement("UPDATE general_configuration SET value = ? WHERE parameter = ?");
			stmt.setString(1, value==null?"":value);
			stmt.setString(2, parameter);
			int rowAffected = stmt.executeUpdate();
	
			if(rowAffected!=0) {
				flag = true;	
			}
		} catch (Exception ex) {
			Logger.writeError(ex, "SQLException :", ECPOS_FOLDER);
			ex.printStackTrace();
			throw ex;
		} 
		return flag;
	}
	
	public JSONObject getActivationInfo(Connection connection){
		JSONObject result = new JSONObject();
		
		try {
			result.put("activationId", getGeneralConfig(connection, "ACTIVATION_ID"));
			result.put("activationKey", getGeneralConfig(connection, "ACTIVATION_KEY"));
			result.put("macAddress", getGeneralConfig(connection, "MAC_ADDRESS"));
			result.put("brandId", getGeneralConfig(connection, "BRAND_ID"));
			result.put("versionNumber", getGeneralConfig(connection, "VERSION_NUMBER"));
		}catch(Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		}
		return result;
	}
	
	public static int trimCheckRef(String checkRefNo) {
		int val = checkRefNo == null || checkRefNo.equals("") ? 0 : Integer.parseInt(checkRefNo.substring(6));
		return val;
	}
}
