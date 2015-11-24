package com.chinatelecom.nfc.Utils;

import android.os.Environment;

public class Constant {
	public static final int LOAD_PAGE = 0x01;
	
	
	public static final int REQUESTCODE_ADD_ENVIROMENT = 0x66;
	public static final int REQUESTCODE_TIME_MANAGE = 0x67;
	public static final int REQUESTCODE_MODEMUTE = 0x68;
	public static final int REQUESTCODE_VIBRATION = 0x69;
	public static final int REQUESTCODE_WIFI = 0x70;
	public static final int REQUESTCODE_BLUETOOTH = 0x71;
	public static final int REQUESTCODE_DIGITALSWITCH = 0x72;
	public static final int REQUESTCODE_AIRPLANEMODE = 0x73;
	
	public static final int REQUESTCODE_ADD_LOTTERY = 0x74;
	public static final int REQUESTCODE_AWARD1 = 0x75;
	public static final int REQUESTCODE_AWARD2 = 0x76;
	public static final int REQUESTCODE_AWARD3 = 0x77;
	public static final int REQUESTCODE_AWARD4 = 0x78;
	public static final int REQUESTCODE_AWARD5 = 0x79;
	public static final int REQUESTCODE_AWARD6 = 0x7A;
	
	public static final int REQUESTCODE_MODE = 0x80;
	public static final int REQUESTCODE_TIME = 1000;
	
	public static final int REQUESTCODE_LOTTERY_FILE = 0x82;
	
	
	public static final String TITLE_ADD_ENVIROMENT = "title_add_enviroment";
	public static final String DATA_ADD_ENVIROMENT = "data_add_enviroment";
	
	public static final String TAG_INTENT = "tagIntent";
	/**
	 * 10s
	 */
	public static final int TIME_OUT = 10000;
	
	public static final String MYDATA_ID = "mydataId";
	public static final String MYDATA_DATATYPE = "dataType";
	public static final String MYDATA_TABLEID = "tableID";
	public static final String MYDATA_READORWRITE = "readOrWrite";
	public static final String TITLE = "title";
	
	public static final int MSG_WHAT_SHOW_PROGRESSBAR = 1;
	
	
	public static final String INTENT_TYPE = "intentType";
	public static final int INTENT_TYPE_READ_TAG= 		1;
	public static final int INTENT_TYPE_MAKE_TAG= 		2;
	public static final String INTENT_MY_NAMECARD = "myNameCard";
	public static final int TAG_MYNAMECARD= 			1;
	public static final String INTENT_FORM_NFC= 	"formNfc";
	public static final boolean TAG_FORMNFC		=		 true; 
	
	public static final String fromNFC = "fromNFC";
	
	public final static String  NFCFORCHINATELECOM_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/NFCForChinaTelecom";
	public final static String  LOTTERY_DIR = NFCFORCHINATELECOM_DIR + "/lottery";
	public final static String  LOTTERY_DIR_DATAANDTYPE = "/sdcard/NFCForChinaTelecom/lottery";
	
}
