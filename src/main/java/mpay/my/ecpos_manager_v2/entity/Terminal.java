package mpay.my.ecpos_manager_v2.entity;

public class Terminal {

	private Long id;
	private String terminalName;
	private String wifiIP;
	private String wifiPort;

	public Terminal() {
	}

	public Terminal(Long id, String terminalName, String wifiIP, String wifiPort) {
		this.id = id;
		this.terminalName = terminalName;
		this.wifiIP = wifiIP;
		this.wifiPort = wifiPort;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTerminalName() {
		return terminalName;
	}

	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}

	public String getWifiIP() {
		return wifiIP;
	}

	public void setWifiIP(String wifiIP) {
		this.wifiIP = wifiIP;
	}

	public String getWifiPort() {
		return wifiPort;
	}

	public void setWifiPort(String wifiPort) {
		this.wifiPort = wifiPort;
	}

}
