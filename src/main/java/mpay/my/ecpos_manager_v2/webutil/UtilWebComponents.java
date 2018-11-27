package mpay.my.ecpos_manager_v2.webutil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

public class UtilWebComponents {

	public UserAuthenticationModel getUserSession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		UserAuthenticationModel domainContainer = (UserAuthenticationModel) session.getAttribute("session_user");
		return domainContainer;
	}

	public UserAuthenticationModel performUserAuthentication(String username, String password, DataSource dataSource) {
		UserAuthenticationModel domainContainer = new UserAuthenticationModel();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			connection = dataSource.getConnection();
			String query = "SELECT * FROM empldef WHERE username = ?";
			stmt = connection.prepareStatement(query);
			stmt.setString(1, username);
			rs = (ResultSet) stmt.executeQuery();
			if (rs.next()) {

				if (password.matches(rs.getString("password"))) {
					domainContainer.setUserLoginId(Integer.parseInt(rs.getString("id")));
					domainContainer.setUsername(rs.getString("username"));
					domainContainer.setName(rs.getString("name"));
					domainContainer.setRoleType(Integer.parseInt(rs.getString("type")));
					domainContainer.setStoreId(Integer.parseInt(rs.getString("storeid")));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return domainContainer;
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return domainContainer;
	}

	public void clearEcposSession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
	}

	/*
	 * public CheckDetailItemList getStoreTempCheckData(HttpServletRequest request)
	 * { HttpSession session = request.getSession(); CheckDetailItemList
	 * domain_container = (CheckDetailItemList)
	 * session.getAttribute("session_temp_check_data"); return domain_container; }
	 */

}
