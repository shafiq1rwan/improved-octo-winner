package mpay.ecpos_manager.general.property;

import java.util.ResourceBundle;

public class Property {
	
	private static ResourceBundle res = ResourceBundle.getBundle("mpay.ecpos_manager.general.property/GeneralProperty");

	private static String ECPOS_FOLDER_NAME = res.getString("ECPOS_FOLDER_NAME");
	private static String IPOS_FOLDER_NAME = res.getString("IPOS_FOLDER_NAME");
	private static String SYNC_FOLDER_NAME = res.getString("SYNC_FOLDER_NAME");
	private static String VMPOS_FOLDER_NAME = res.getString("VMPOS_FOLDER_NAME");
	private static String DEVICECALL_FOLDER_NAME = res.getString("DEVICECALL_FOLDER_NAME");
	
	private static String HARDWARE_FOLDER_NAME = res.getString("HARDWARE_FOLDER_NAME");

	public static String getECPOS_FOLDER_NAME() {
		return ECPOS_FOLDER_NAME;
	}
	
	public static String getIPOS_FOLDER_NAME() {
		return IPOS_FOLDER_NAME;
	}

	public static String getSYNC_FOLDER_NAME() {
		return SYNC_FOLDER_NAME;
	}
	
	public static String getVMPOS_FOLDER_NAME() {
		return VMPOS_FOLDER_NAME;
	}
	
	public static String getDEVICECALL_FOLDER_NAME() {
		return DEVICECALL_FOLDER_NAME;
	}
	
	public static String getHARDWARE_FOLDER_NAME() {
		return HARDWARE_FOLDER_NAME;
	}
}
