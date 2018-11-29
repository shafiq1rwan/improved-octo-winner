package mpay.my.ecpos_manager_v2.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import mpay.my.ecpos_manager_v2.webutil.UserAuthenticationModel;

@Repository
public class EmployeeRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public EmployeeRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<UserAuthenticationModel> rowMapper = (rs, rowNum) -> {
		UserAuthenticationModel authenticatedUser = new UserAuthenticationModel();
		authenticatedUser.setUserLoginId(rs.getInt("id"));
		authenticatedUser.setUsername(rs.getString("username"));
		authenticatedUser.setUserPassword(rs.getString("password"));
		authenticatedUser.setName(rs.getString("name"));
		authenticatedUser.setRoleType(rs.getInt("type"));
		authenticatedUser.setStoreId(rs.getInt("storeid"));
		return authenticatedUser;
	};

	public UserAuthenticationModel getAuthenticatedUser(String username) {
		return jdbcTemplate.queryForObject("SELECT * FROM empldef WHERE username = ?", new Object[] { username }, rowMapper);
	}
}
