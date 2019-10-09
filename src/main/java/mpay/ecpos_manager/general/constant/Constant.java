package mpay.ecpos_manager.general.constant;

public class Constant {
	public static String LOGIN_ID = "loginId";
	public static String PASSWORD = "password";
	public static String PRODUCT_LIST = "product_list";
	public static String ITEM_NAME = "item_name";
	public static String ITEM_CODE = "item_code";
	public static String ITEM_PRICE = "item_price";
	public static String ITEM_QTY = "item_qty";
	public static String TOTAL_PRICE = "total_price";
	public static String ITEM_GROUP_LIST = "itemgroup_list";
	public static String GROUP_NAME = "group_name";
	public static String GROUP_TYPE = "group_type";
	public static String GST_GROUP = "gst_group";
	public static String SST_GROUP = "sst_group";
	public static String ITEM_TYPE = "item_type";
	public static String IMAGE_BITMAP = "image_bitmap";
	public static String IMAGE_PATH = "image_path";
	public static String TABLE_LIST = "table_list";
	public static String TABLE_NO = "table_no";
	public static String CHECK_LIST = "check_list";
	public static String CHECK_NO = "check_no";
	public static String CHECK_DETAIL_ID = "check_detail_id";
	public static String CHECK_REF_NO = "check_ref_no";
	public static String ORDERED_LIST = "ordered_list";
	public static String STAFF_NAME = "staff_name";
	public static String ORDERED_ITEM = "ordered_item";
	public static String TRAN_TYPE = "tran_type";
	public static String MTRX_ID = "mtrx_id";
	public static String TRAN_STATUS = "tran_status";
	public static String AUTH_CODE = "auth_code";
	public static String PAYMENT_TYPE = "payment_type";
	public static String TRAN_DATETIME = "tran_datetime";
	public static String TRACE_NO = "trace_no";
	public static String BATCH_NO = "batch_no";
	public static String BANK_MID = "bank_mid";
	public static String BANK_TID = "bank_tid";
	public static String AID = "aid";
	public static String APP_LABEL = "app_label";
	public static String MASKED_CARDNO = "masked_cardno";
	public static String CARDHOLDER_NAME = "cardholder_name";
	public static String TRAN_LIST = "tran_list";
	public static String AMOUNT = "amount";
	public static String RESPONSE_CODE = "response_code";
	public static String RESPONSE_MESSAGE = "response_message";
	public static String KDS_DATE_TIME = "kds_date_time";
	
	public static int REFERENCE_ID = 0;
	public static int RESPONSECODE = 1;
	public static int REPONSEMSG = 2;
	public static int TOTAL_AMOUNT = 3;
	public static int MTRXID = 4;
	public static int AUTHCODE = 5;
	public static int BANKMID = 6;
	public static int BANKTID = 7;
	public static int BATCH = 8;
	public static int TRACENO = 9;
	public static int APPLABEL = 10;
	public static int MASKEDCARDNO = 11;
	public static int CARDNAME = 12;
	public static int TRANDATETIME = 13;
	public static int READMODE = 14;
	public static int REFERENCE_CODE = 15;
	public static int TRANAID = 16;
	public static int TRANTC = 18;
	
	// User Role
	public static int ADMIN_ROLE = 1;
	public static int STORE_MANAGER_ROLE = 2;
	public static int KITCHEN_ROLE = 3;
	public static int WAITER_ROLE = 4;
	public static int CASHIER_ROLE = 5;
	
	// Order type
	public static String TABLE = "table";
	public static String TAKE_AWAY = "take_away";
	public static String DEPOSIT = "deposit";
	
	public static String DATE_TIME_FORMAT_DB = "yyyy-MM-dd HH:mm:ss.S";
	public static String DATE_TIME_FORMAT_1 = "dd-MM-yyyy hh:mm:ss a";
	public static String DATE_TIME_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";
	
	public static String EXCEPTION_MESSAGE = "{\"response_code\":\"01\",\"response_message\":\"SERVER EXCEPTION\"}";
}
