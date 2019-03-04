package mpay.my.ecpos_manager_v2.property;

import java.util.ResourceBundle;

public class Property {

	private static ResourceBundle res = ResourceBundle.getBundle("mpay.my.ecpos_manager_v2.property/GeneralProperty");

	private static String PROJECT_LOG_PATH = res.getString("PROJECT_LOG_PATH");

	private static String ECPOS_FOLDER_NAME = res.getString("ECPOS_FOLDER_NAME");
	private static String IPOS_FOLDER_NAME = res.getString("IPOS_FOLDER_NAME");
	private static String SYNC_FOLDER_NAME = res.getString("SYNC_FOLDER_NAME");

	public static String getPROJECT_LOG_PATH() {
		return PROJECT_LOG_PATH;
	}

	public static String getECPOS_FOLDER_NAME() {
		return ECPOS_FOLDER_NAME;
	}
	
	public static String getIPOS_FOLDER_NAME() {
		return IPOS_FOLDER_NAME;
	}

	public static String getSYNC_FOLDER_NAME() {
		return SYNC_FOLDER_NAME;
	}
}
