package mpay.my.ecpos_manager_v2.webutil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import mpay.my.ecpos_manager_v2.logger.Logger;
import mpay.my.ecpos_manager_v2.property.Property;

public class UtilWebComponents {

	private static String ECPOS_FOLDER = Property.getECPOS_FOLDER_NAME();
	
	public UserAuthenticationModel performEcposAuthentication(String username, String password, DataSource dataSource) {
		UserAuthenticationModel domainContainer = new UserAuthenticationModel();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();

			String query = "SELECT * FROM staff WHERE staff_username = ? and is_active = 1";
			stmt = connection.prepareStatement(query);
			stmt.setString(1, username);
			rs = (ResultSet) stmt.executeQuery();

			if (rs.next()) {
				BCryptPasswordEncoder pass_encode = new BCryptPasswordEncoder();
				String public_password = password;
				String private_password = pass_encode.encode(rs.getString("staff_password"));

				if (pass_encode.matches(public_password, private_password)) {
					domainContainer.setUserLoginId(Integer.parseInt(rs.getString("id")));
					domainContainer.setName(rs.getString("staff_name"));
					domainContainer.setUsername(rs.getString("staff_username"));
					domainContainer.setRoleType(Integer.parseInt(rs.getString("staff_role")));
				}
			}
		} catch (Exception e) {
			Logger.writeError(e, "Exception: ", ECPOS_FOLDER);
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (rs != null) {rs.close();rs = null;}
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
}
