package com.telecompp.util;

/**
 * 系统全局变量管理
 * @author xiongdazhuang
 *
 */
public interface SumaConstants {

	/** 
	 * 系统log管理
	 */
	public final Boolean DEBUG_LOG_FLAG = true;
	public final String DEBUG_LOGCAT_FILTER = "SumaLog";
	public final String DEBUG_LOGCAT_FILTER_NFC = "SumaLogNFC";
	public final String XML_MSGID_FIND_PWD = "1010"; // 客户端找回密码
	public final String XML_MSGID_TERMINAL_REGISTER2C = "3201";
	
	public final String VERSION_CODE = "4";
	
	//	翼机通终端注册
	public final String XML_MSGID_TERMINAL_REGISTER2YJT = "5009";
	/**
	 * 客户端与前置平台报文交互特殊字段
	 */
	/**
	 * MsgID
	 */
	public final String XML_MSGID_SESSIONKEY_EXCHANGE = "1000"; // 客户端会话密钥交换
	public final String XML_MSGID_CLIENT_UPGRADE = "1002"; // 客户端升级
	public final String XML_MSGID_CARD_SESSIONKEY_EXCHANGE = "1001"; // 卡片会话密钥交换
//	public final String XML_MSGID_TERMINAL_AUTH = "1003";
//	public final String XML_MSGID_TRADE_SEARCH = "1004";
//
//	public final String XML_MSGID_TERMINAL_REGISTER = "1005";
//	public final String XML_MSGID_TERMINAL_SEARCH = "1006";
//	public final String XML_MSGID_TERMINAL_MODIFY = "1007";
//	public final String XML_MSGID_NOTICE = "1008";
//
//	public final String XML_MSGID_DOWNLOAD_PARAMETER = "2001";
//	public final String XML_MSGID_DOWNLOAD_PARAMETER_NOTICE = "2002";
//
//	public final String XML_MSGID_MASTER_KEY_DOWNLOAD = "2003";
//	public final String XML_MSGID_MASTER_KEY_DOWNLOAD_NOTICE = "2004";
//
//	public final String XML_MSGID_AUTH = "2005";
//
//	public final String XML_MSGID_SIGN_IN = "2006";
//	public final String XML_MSGID_SIGN_OUT = "2007";
//
//	public final String XML_MSGID_ONLINE_TRADE = "3001";
//	public final String XML_MSGID_TRADE_REVERSE = "3002";
//
//	public final String XML_MSGID_TRADE_REVOKED = "3003";// 交易撤销
//	public final String XML_MSGID_TRADE_REVOKED_REVERSE = "3004";// 交易撤销冲正
//
//	public final String XML_MSGID_UPLOAD_AND_SETTLEMENT = "3005";// 脱机批上送
//	public final String XML_MSGID_GET_BALANCE = "3006";
//	public final String XML_MSGID_ONLINE_LOAD = "3007";
//	public final String XML_MSGID_ONLINE_LOAD_REVERSE = "3008";
//	public final String XML_MSGID_ONLINE_LOAD_NOTICE = "3009";
//
//	public final String XML_MSGID_TRADE_REFUND = "300A";// 交易退款
//
//	public final String XML_MSGID_OFFLINE_TRADE_RECORD_UPSEND = "3100";
//	public final String XML_MSGID_ONLINE_TRADE_NOTICE = "3101";
	
	//碰碰二期添加内容
//	public final String XML_MSGID_GET_MAC1 = "FF01";//申请MCA1
//	public final String XML_MSGID_AUTHEN_MAC2 = "FF02";//申请MAC2
//	public final String XML_MSGID_BUS_LOAD = "B004";//公交圈存
//	public final String XML_MSGID_NOTIFY_BUS_LOAD = "B005";//圈存结果通知
//	public final String XML_MSGID_LOAD_CONFIRM = "8002";//圈存确认
//	public final String XML_MSGID_GET_LOAD_CIPHERTEXT = "8001";//获取公交充值密文，福州公交
//	public final String XML_MSGID_GET_MTICKET_CIPHTERTEXT = "8003";//获取公交月卡充值密文，福州公交
//	public final String XML_MSGID_PRUCHASE_INIT = "8004";//公交消费初始化
//	public final String XML_MSGID_PRUCHASE_CONFIRM = "8005";//公交消费确认
//	public final String XML_MSGID_QUERY_RECORD = "8006";//公交卡交易记录查询
//	public final String XML_MSGID_GET_MTICKET_INFO = "8007";//获取公交月卡充值密文，福州公交
//	public final String XML_MSGID_MLOAD_CONFIRM = "8008";//公交月票充值确认
//	public final String XML_MSGID_CPU_LOAD_ORDER = "8011";//获取CPU卡充值订单
//	public final String XML_MSGID_MLOAD_RESTORE = "8888";//公交月票充值确认
//	
//	public final String XML_MSGID_PREREAD = "8009";//公交预读卡
//	public final String XML_MSGID_RECOVER = "8010";//公交恢复
	
	//====================================================
	public final String XML_MSGID_COMMIT_KEYS = "A001";//上传密钥
	public final String XML_MSGID_UIM_REG= "A002";//判断指定的ICCID是否已经注册了商户
	public final String XML_MSGID_JFY_PASS = "A005";//交费易密码
	public final String XML_CITY_XIAMEN = "360000";//在xml的头的city节点的可选值代表厦门
//======================厦门部分ID==================
//	public static final String XML_MSGID_XiaMen_M1_Card_Recharge="7001";
//	public static final String XML_MSGID_XiaMen_CPU_Card_RechargeInit="7002";
//	public static final String XML_MSGID_XiaMen_CPU_Card_RechargeConfirm="7003";
//	public static final String XML_MSGID_XiaMen_CPU_Card_RechargeFlush="7004";
//	public static final String XML_MSGID_XiaMen_GET_TerminalNo="7005";
//	
//	public static final String MsgID_PT_XiaMen_CPU_Card_OffPurchase_GetPSAMID="7006";//获取PSAM终端号
//	public static final String MsgID_PT_XiaMen_CPU_Card_OffPurchase_Request="7007";  //(getmac1)
//	public static final String MsgID_PT_XiaMen_CPU_Card_OnPurchase_Request="7009";  //(getmac1)
//	public static final String MsgID_PT_XiaMen_CPU_Card_OffPurchase_Confirm="7008"; //(checkmac2)
//	public static final String XML_MSGID_XiaMen_CPU_Card_PRechargeFlush="7010";//消费冲正
//	
//	public static final String XML_MSGID_XiaMen_CPU_Card_UPDATE_BINARY="7011";//更新有效日期获取改写MAC
	
	
	//--------------------------------翼支付个人支付相关MSGID
		public final String XML_MSGID_YZF_ORDER = "9901";//申请支付
		public final String XML_MSGID_YZF_QUERY = "9902";//公交查询
		public final String XML_MSGID_YZF_REFOUND = "9903";//退款
		
	//--------------------------------对接翼机通相关的MSGID
		public final String XML_MSGID_YJT_MERCHANTINFO = "5001";	// 获取翼机通商户信息
		public final String XML_MSGID_YJT_LOAD = "5002";	// 翼机通卡充值
		public final String XML_MSGID_YJT_LOADCONFIRM = "5003";	// 翼机通卡充值确认
		public final String XML_MSGID_YJT_NOLOADCONFIRM = "5004";	// 大白卡只扣款不充值确认
		
		// ---------------------翼机通空圈--个人化和补贴钱包相关的msgid
		public final String XML_MSGID_YJT_PERSONAL_USERAUC = "5101";	// 用户认证
		public final String XML_MSGID_YJT_PERSONAL_PERSONREQ = "5103";	// 个人化
		public final String XML_MSGID_YJT_PERSONAL_CARDSTATUSSYN = "5102";	// 卡片状态同步
		public final String XML_MSGID_YJT_PERSONAL_APDURESULTREQ = "5105";	// 卡片响应结果通知
		public final String XML_MSGID_YJT_PERSONAL_DELPERSONREQ = "5104";	// 去个人化
		
		public final String XML_MSGID_YJT_ED_0LOAD = "5106";	// 补助冲零
		public final String XML_MSGID_YJT_ED_0LOADCONFIRM = "5107";	// 补助冲零结果结果通知
		public final String XML_MSGID_YJT_ED_LOAD = "5108";	// 补贴申请
		public final String XML_MSGID_YJT_ED_LOADCONFIRM = "5109";	// 补助结果确认
		
		
		
	/**
	 * MsgType
	 */
	public final String XML_MSGTYPE_REQUEST = "1";
	public final String XML_MSGTYPE_RESPONSE = "2";

	/**
	 * HttpTransResponse map
	 */
	public final String MAP_HTTP_RESPONSE_RESPONSE = "response";
	public final String MAP_HTTP_RESPONSE_CODE = "code";
	public final String MAP_HTTP_RESPONSE_COOKIE_ID = "CookieID";

	/**
	 * HttpResponseCode Information
	 */
	public final int HTTP_RESPONSE_CODE_OK = 200;

	/**
	 * encryptLevel 加密级别
	 */
	public final int ENCRYPT_LEVEL_PLAINTEXT_DATA = 0x0000; // 明文数据
	public final int ENCRYPT_LEVEL_TERMINAL_SESSIONKEY_ENC_DATA = 0x0001;// 使用终端会话密钥加密，不对body加密
	public final int ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA = 0x0002;// 对报文体使用卡片会话密钥加密，对整个报文使用终端会话密钥加密

	/**
	 * Terminal PubKey 预置的客户端与前置平台公钥
	 */
	// public final String TERMINAL_PUBKEY =
	// "82552414255577f7e6a7492377a45ec521c4c6f75b6700612ed0e45ce3f02a595883fe02a31bea01c04d9cb50c2da462c1d6a542011c3ca845e71087d62a478058e264718d5ac47d41f0c70df8aeb50fac3988493e2f2f01f42dfcdba870ee92f67bb76250c10f17c21c4f1699cdde3f070f4e1164fb5a6a2cd08655a1ba724d";
//	public final String TERMINAL_PUBKEY = "a123948438e0a98b3f524de0b7db1486c333db0534cca19a169dbc68271a827bdf66bec013fa2e7c277e6a7a48ad435280326e50ebc31ae0bf91b24438d9ee5a706a1cb8aab4f276a9b11fc384d2544a91780fdcacd8ddaaa0b601ae7182046a3d6319de8f6ec0266892a6e9c0249d9c0b4fd3f3273faba2cc395b71ccd326ad";
	public final String TERMINAL_PUBKEY = "cdcb0ddd7c61999e118d81fe143770c5850d5b69c8391b362e3c42f9963ee2548d5b4a3fda3dce8aa6e71376a6703821104081fc1c630a888b95c959f8468d6063eb83fc3b58850384e6e37a2cb461dfd04c62c2a0ecadff0c483ef906d89de9b1eb24e5d2e8f0ae96c9fe798f2959db09a6a0743e61fc63426d9bb21ce8f721";
	/**
	 * 前置平台服务器IP地址
	 */
	// public final String SERVER_ADDRESS_IP =
	// "http://124.207.144.242:8080/CTCCMobilePos/trans.do";
//	public static String SERVER_ADDRESS_IP = "http://219.142.69.131:8083/CTCCMobilePos/trans.do";// 电信研究院地址
	// public final String SERVER_ADDRESS_IP =
	// "http://192.168.1.106:8080/CTCCMobilePos/trans.do";// 海龙服务地址
	// public final String SERVER_ADDRESS_IP =
	// "http://180.166.69.32:28080/CTCCMobilePos/trans.do";// 电信生产地址

	/**
	 * 测试地址 
	 */
//	public final String SERVER_ADDRESS_IP = "http://192.168.5.232:8080/CTCCMobilePos/trans.do";

	/**
	 * 客户端状态码
	 */
//	public final int TERMINAL_CODE_OK = 0000;// 参数错误
//	public final int TERMINAL_CODE_ARG_ERR = 1001;// 参数错误
//	public final int TERMINAL_CODE_CONNECT_SERVER_ERR = 1002;// 网络问题或服务器异常
//	public final int TERMINAL_CODE_UIM_CARD_ERR = 1003;// 与UIM卡通讯失败
//	public final int TERMINAL_CODE_NFC_CARD_ERR = 1004;// 通过NFC通讯失败
//	public final int TERMINAL_CODE_GENERAL_ERR = 1100;// 一般性错误，未知错误
//
//	public final int MAX_RECORD_NUMBER_PER_PAGE = 10;
	public final int OFFLINE_MAX_VERFY_TIMES = 5;
//	public final int APDU_MAX_BLOCK_LENGTH = 252;
//	public final String KEY_GRID_BUTTON_IMAGE = "GridImage";
//	public final String KEY_GRID_BUTTON_NAME = "GridText";
//
//	public static final byte POPUP_TYPE_HELP = 0;
//	public static final byte POPUP_TYPE_EXCEPTIONS = 1;
//
//	public static final String PURCHASE_MODE_ONCE = "once";
//	public static final String PURCHASE_MODE_CONTINUES = "continues";
//
//	public static final int ADVERT_FLIP_INTERVAL = 3000;
//
//	public final String BUNDLE_KEY_SD_SUPPORT_STATE = "sdSupportState";
//	public final String BUNDLE_KEY_CARD_TYPE = "cardType";
//	public final String BUNDLE_KEY_PURCHASE_MODE = "purchaseMode";
//	public final String BUNDLE_KEY_PURCHASE_IS_ONLINE = "purchaseOnline";
//	public final String BUNDLE_KEY_FORMAT_AMOUNT = "formatAmount";
//	public final String BUNDLE_KEY_ACCOUNT = "account";
//	public final String BUNDLE_KEY_AMOUNT = "amount";
//	public final String BUNDLE_KEY_BALANCE = "balance";
//	public final String BUNDLE_KEY_BALANCE2 = "balance2";
//	public final String BUNDLE_KEY_USER_PIN = "userPin";
//	public final String BUNDLE_KEY_RECORDS_TIME = "recordTime";
//	public final String BUNDLE_KEY_RECORDS_TYPE = "recordType";
//	public final String BUNDLE_KEY_RECORDS_AMOUNT = "recordAmount";
//	public final String BUNDLE_KEY_URL = "URL";
//	public final String BUNDLE_KEY_RESULT_STATUS = "resultStatus";
//	public final String BUNDLE_KEY_RESULT_TEXT = "resultText";
//	public final String BUNDLE_KEY_RESULT_TIPS = "resultTips";
//	public final String BUNDLE_KEY_RESULT_CODE = "resultCode";
//	public final String BUNDLE_KEY_LIST_POSITION = "position";
//
//	// main menu index
//	public final int MENU_INDEX_EWALLET_PURCHASE = 0;
//	public final int MENU_INDEX_ACCOUNT_PURCHASE = 1;
//	public final int MENU_INDEX_CASH_PURCHASE = 2;
//	public final int MENU_INDEX_REFUND = 3;
//	public final int MENU_INDEX_EWALLET_QUERY = 4;
//	public final int MENU_INDEX_ACCOUNT_QUERY = 5;
//	public final int MENU_INDEX_CASH_QUERY = 6;
//	public final int MENU_INDEX_EWALLET_LOAD = 7;
//	public final int MENU_INDEX_CASH_LOAD = 8;
//	public final int MENU_INDEX_SIGN_IN = 9;
//	public final int MENU_INDEX_MERCHANT_INFO = 10;
//	public final int MENU_INDEX_RECORD_QUERY = 11;
//	public final int MENU_INDEX_UPLOAD = 12;
//	public final int MENU_INDEX_SIGN_OUT = 13;
//	public final int MENU_INDEX_NOTICE = 14;
//	public final int MENU_INDEX_SERVICE_TEL = 15;

	// main menu id
//	public final String MENU_ID_EWALLET_PURCHASE = "ewllet_purchase";
//	public final String MENU_ID_ACCOUNT_PURCHASE = "account_purchase";
//	public final String MENU_ID_CASH_PURCHASE = "cash_purchase";
//	public final String MENU_ID_REFUND = "refund";
//	public final String MENU_ID_EWALLET_QUERY = "ewallet_query";
//	public final String MENU_ID_ACCOUNT_QUERY = "account_query";
//	public final String MENU_ID_CASH_QUERY = "cash_query";
//	public final String MENU_ID_EWALLET_LOAD = "ewallet_load";
//	public final String MENU_ID_CASH_LOAD = "cash_load";
//	public final String MENU_ID_SIGN_IN = "sign_in";
//	public final String MENU_ID_MERCHANT_INFO = "merchant_info";
//	public final String MENU_ID_RECORD_QUERY = "record_query";
//	public final String MENU_ID_UPLOAD = "upload";
//	public final String MENU_ID_SIGN_OUT = "sign_out";
//	public final String MENU_ID_NOTICE = "notice";
//	public final String MENU_ID_SERVICE_TEL = "service_tel";
	
	/**
	 * 二期修改主菜单，添加menu
	 * 
	 */
//	public static final String MENU_EXTRA = "MENU_NAME";
//	public static final String MENU_ID_BESTPAY = "bestpay";
//	public static final String MENU_ID_BUS = "bus";
//	public static final String MENU_ID_ECASH = "ecash";
//	public static final String MENU_ID_SELECT_BUS = "select_bus";
//	//================================
//	public static final String MENU_ID_XM_BUS = "xm_bus";
//	public static final String MENU_ID_FZ_BUS = "fz_bus";
	

	// handler message id
//	public final int HANDLE_MESSAGE_NEXT_INTENT = 0x0000;
//	public final int HANDLE_MESSAGE_GET_ACCOUNT = 0x0001;
//	public final int HANDLE_MESSAGE_CONNECT_SERVER = 0x0002;
//	public final int HANDLE_MESSAGE_REFRESH_VIEW = 0x0003;
//	public final int HANDLE_MESSAGE_START_PROGRESS = 0x0004;
//
//	public final int HANDLE_MESSAGE_GESTURE_PASSWORD = 0x000F;
//
//	public final int HANDLE_MESSAGE_REFUND = 0x0010;
//
//	public final int HANDLE_MESSAGE_TOAST_LAST_PAGE = 0x0051;
//	
//	public final int HANDLE_MESSAGE_NFC_HAS_SENT = 0x00F1;
//
//	public final int HANDLE_MESSAGE_RESTART_NFC = 0x00FF;
//
//	public final int HANDLE_MESSAGE_ERROR_NO_INTERNET = 0xFF00;
//
//	public final int HANDLE_MESSAGE_ERROR_SERVER_ERROR = 0xFF03;
//
//	public final int HANDLE_MESSAGE_ERROR_RESTART_APP = 0xFF10;
//
//	public final int HANDLE_MESSAGE_ERROR_PROCESS_SUMA_ERROR = 0xFF20;
//	
//	public final int HANDLE_CARD_TRADE_NEED_CONFIRM = 0xFF21;//再次刷卡
//
//	public final String KEY_HISTORY_ACCOUNT = "account";// 交易账号
//	public final String KEY_HISTORY_SERIAL = "serial";// 交易流水号
//	public final String KEY_HISTORY_DATE = "data";// 交易日期时间
//	public final String KEY_HISTORY_AMOUNT = "amount";// 交易金额
//
//	public final String KEY_HISTORY_BATCHNO = "batchNo";
//
//	public final String KEY_MERCHANT_INFO_TITLE = "title";
//	public final String KEY_MERCHANT_INFO_CONTENT = "content";
//	public final String KEY_MERCHANT_INFO_CONTROL = "control";
//	public final String KEY_MERCHANT_INFO_EDITABLE = "editable";
//
//	public final String KEY_HISTORY_STATE = "state";// 交易状态
//	public final String KEY_HISTORY_TYPE = "type";// 交易类型
//	public final String KEY_HISTORY_REFNO = "refNo";// 检索参考号

//	public final static int SIMMGR_MSG_SERVICE_CONNECTED = 0x00CA;
//	public final int MSG_CONNECT_CARD_FAIL = 0xFFA1;
//	public final int MSG_LOGIN_FAIL = 0xFFA2;
//	public final int MSG_SIGN_IN_FAIL = 0xFFA3;
//	public final int MSG_EXIT = 0xFFA4;
//	public final int MSG_DO_NOTHING = 0xFFA5;
//	public final int MSG_ERR_RETRY = 0xFFA6;
//	public final int MSG_JUMP_TO_UPSEND_RECORD = 0xFFA7;
//	public final int MSG_JUMP_TO_UPSEND_RECORD_DO = 0xFFA8;
//	public final int MSG_GO_TO_NEXT_INTENT = 0xFFA9;
//
//	public final int MSG_ACTION_RE_CONNECT_CARD = 0xFF01;
//	public final int MSG_ACTION_RE_LOGIN = 0xFF02;
//	public final int MSG_ACTION_EXIT = 0xFF03;
	
	
	//	===翼支付碰碰===
	public final String ERROR_TYPE_NO_PAYNOTICE = "6007";	//	没有收到服务器的支付结果确认报文
	public final String ERROR_GET_MAC2_FAILD = "6001";	//	(ACER)获取mac不成功, 充值失败
	public final String ERROR_GET_MAC2_SUCCED_LOAD_FAILD = "6002";	//	获取MAC成功, 充值失败
	public final String ERROR_CARD_SECRET_EXCHANGE_FAILD = "6004";	//	卡片会话密钥交换失败
	public final String ERROR_TERMINAL_SECRET_EXCHANGE_FAILD = "6003";	//	终端会话密钥交换失败
	public final String ERROR_TERMINAL_REGISTER_FAILD = "6005";	//	终端注册失败
	public final String ERROR_GET_MERCHANTINFO_FAILD = "6006";	//	获取商户信息失败
	public final String ERROR_GET_MERCHANTINFO_NEEDUPDATE = "6010";	//	需要升级客户端
	public final String ERROR_INITYJTCARD_EXCEPTION = "6008";	//	机卡通道初始化异常
	public final String ERROR_INITLOAD_EXCEPTION = "6009";	//	圈存初始化异常
	//	对应的错误信息
	public final String ERROR_TYPE_NO_PAYNOTICE_MSG = "没有收到服务器的支付结果确认报文, 已申请退款";	//	没有收到服务器的支付结果确认报文
	public final String ERROR_GET_MAC2_FAILD_MSG = "获取mac不成功, 充值失败, 已申请退款";	//	(ACER)获取mac不成功, 充值失败
	public final String ERROR_GET_MAC2_SUCCED_LOAD_FAILD_MSG = "获取MAC成功, 充值失败, 已申请退款";	//	获取MAC成功, 充值失败
	public final String ERROR_CARD_SECRET_EXCHANGE_FAILD_MSG = "卡片会话密钥交换失败";	//	卡片会话密钥交换失败
	public final String ERROR_TERMINAL_SECRET_EXCHANGE_FAILD_MSG = "终端会话密钥交换失败";	//	终端会话密钥交换失败
	public final String ERROR_TERMINAL_REGISTER_FAILD_MSG = "终端注册失败";	//	终端注册失败
	public final String ERROR_GET_MERCHANTINFO_FAILD_MSG = "获取商户信息失败";	//	获取商户信息失败
	public final String ERROR_GET_MERCHANTINFO_NEEDUPDATE_MSG = "客户端版本过低, 不能使用充值功能, 需要升级客户端";	//	获取商户信息失败
	public final String ERROR_INITYJTCARD_EXCEPTION_MSG = "机卡通道初始化异常";	//	机卡通道初始化异常
	public final String ERROR_INITLOAD_EXCEPTION_MSG = "圈存初始化异常";	//	圈存初始化异常
	
	
	public static final String MERCHANT_RECHARGE_TYPE_1 = "1";	//	企业内swp卡扣款充值，白卡用户提示不能充值，需要现金充值
	public static final String MERCHANT_RECHARGE_TYPE_2 = "2";	//	企业内swp卡扣款充值，白卡用户划账，到充值机直接充值
	public static final String MERCHANT_RECHARGE_TYPE_3 = "3";	//	企业内swp卡，白卡用户都划账，到充值机直接充值
	
	

	public final static class IpCons {
//		public static String SERVER_ADDRESS_IP =  "http://192.168.1.141:8083/CTCCMobilePosTd/ptbusiness.do";       // "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// 电信研究院地址
//		public static String SERVER_ADDRESS_IP_BUS =  "http://192.168.1.141:8083/CTCCMobilePosTd/ptbusiness.do";// "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// 电信研究院地址
//		public static String SERVER_ADDRESS_IP =  "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";       // "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// 电信研究院地址
//		public static String SERVER_ADDRESS_IP_BUS =  "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";// "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// 电信研究院地址
		
		//	
//		public static String SERVER_ADDRESS_IP =  "http://219.141.189.24:8083/CTCCMobilePosTd/ptbusiness.do";       // "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// 电信研究院地址
//		public static String SERVER_ADDRESS_IP_BUS =  "http://219.141.189.24:8083/CTCCMobilePosTd/ptbusiness.do";// "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// 电信研究院地址
		
//		public static String SERVER_ADDRESS_IP =  "http://192.168.199.200:9090/CTCCMobilePosTd/ptbusiness.do";       // "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// �����о�Ժ��ַ
//		public static String SERVER_ADDRESS_IP_BUS =  "http://192.168.199.200:9090/CTCCMobilePosTd/ptbusiness.do";// "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// �����о�Ժ��ַ
		
//		public static String SERVER_ADDRESS_IP =  "http://192.168.199.224:8080/CTCCMobilePosTd/ptbusiness.do";       // "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// 电信研究院地址
//		public static String SERVER_ADDRESS_IP_BUS =  "http://192.168.199.224:8080/CTCCMobilePosTd/ptbusiness.do";// "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// 电信研究院地址
		
//		public static String SERVER_ADDRESS_IP =  "http://219.142.69.131:8093/CTCCMobilePosTd/ptbusiness.do";       // "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// 电信研究院地址
//		public static String SERVER_ADDRESS_IP_BUS =  "http://219.142.69.131:8093/CTCCMobilePosTd/ptbusiness.do";// "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// 电信研究院地址
		
		//	广东生产平台的IP地址
//		public static String SERVER_ADDRESS_IP =  "http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";       // "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// 电信研究院地址
//		public static String SERVER_ADDRESS_IP_BUS =  "http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// 电信研究院地址

		//	内蒙正式平台
		public static String SERVER_ADDRESS_IP =  "http://42.123.77.82:90/CTCCMobilePosTd/ptbusiness.do";       // "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// �����о�Ժ��ַ
		public static String SERVER_ADDRESS_IP_BUS =  "http://42.123.77.82:90/CTCCMobilePosTd/ptbusiness.do";// "http://219.142.69.131:8083/CTCCMobilePosTd/ptbusiness.do";//"http://183.63.191.7:8680/CTCCMobilePosTd/ptbusiness.do";// �����о�Ժ��ַ
		

		public static String PAY_URL_TAIL = "/trans.do";// 翼支付请求后缀
		public static String BUS_URL_TAIL = "/ptbusiness.do";// 公交请求后缀
		public static void setServerAddressIP(String trueIP)
		{
			if(trueIP.substring(trueIP.length()-1).equals("/"))
			{
				SERVER_ADDRESS_IP = trueIP+PAY_URL_TAIL.substring(1);
				SERVER_ADDRESS_IP_BUS = trueIP+BUS_URL_TAIL.substring(1);
			}
			else
			{
				SERVER_ADDRESS_IP = trueIP+PAY_URL_TAIL;
				SERVER_ADDRESS_IP_BUS = trueIP+BUS_URL_TAIL;
			}
			if(trueIP.endsWith("do")){
				SERVER_ADDRESS_IP = trueIP;
				SERVER_ADDRESS_IP_BUS = trueIP;
			}
		}
		// public final String SERVER_ADDRESS_IP =
		// "http://192.168.1.106:8080/CTCCMobilePos/trans.do";// 海龙服务地址
		// public final String SERVER_ADDRESS_IP =
		// "http://180.166.69.32:28080/CTCCMobilePos/trans.do";// 电信生产地址
	}
	//================配置信息名称
	public static final String SP_PLAT_FORM = "QUERY_PLAT_FORM";
}
