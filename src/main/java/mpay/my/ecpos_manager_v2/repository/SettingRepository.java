package mpay.my.ecpos_manager_v2.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SettingRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public SettingRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Map<String, Object> getSystemData() {
		return jdbcTemplate.queryForMap("SELECT * FROM system");
	}
	
	public List<Map<String,Object>> getTerminalList(){
		return jdbcTemplate.queryForList("SELECT * FROM terminal");
	}

	public int addTerminal(String wifiIP, String wifiPort) {
		return jdbcTemplate.update("INSERT INTO terminal (wifiIP, wifiPort) VALUES (?,?)",
				new Object[] { wifiIP, wifiPort });
	}
	
	public int removeTerminal(String id) {
		return jdbcTemplate.update("DELETE FROM terminal WHERE id = ?",
				new Object[] {id});
	}
	
	

}
