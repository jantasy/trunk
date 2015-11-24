package cn.yjt.oa.app.sim;

public class Constant {
	
	//------------ 基本信息 ------------
	
	//插件启动模式
	public static final int START_MODE_NORMAL = 0;//0－正常使用模式
	public static final int START_MODE_DOWNLOAD_AUTHENTICATION = 1;//1－下载应用鉴权模式
	public static final int START_MODE_DELETE_AUTHENTICATION = 2;//2－删除应用鉴权模式
	
	//主界面是否包含消费
	public static final int DISPLAY_WITH_CONSUMPTION = 4;//4-含消费
	public static final int DISPLAY_WITHOUT_CONSUMPTION = 3;//3-不含消费
	
//	//是否打印Log
//	public final static boolean IS_LOG_ENABLED = true;
	
	//Log默认标签
	public final static String TAG_STRING = "YJT";
	public final static String TMP_DIRECTORY = "Android/data/cn.com.ctbri.yijitong/tmp";
	public final static String LOG_DIRECTORY = "Android/data/cn.com.ctbri.yijitong";
	
	//每次从卡内读取的交易记录数
	public final static int RECORDS_MAX = 10;//根据卡应用规范，最多存10个交易记录
	//每次从平台读取的交易记录数
	public final static int QUERY_CONTENT_MAX = 20;
	
	//翼机通相关数据
//	public final static String AID_STRING = "D156000040100030000000";
	public final static String AID_STRING = "D1560000401000300000000200000000";
	
	//------------ 设置 ------------
	
	//联网设置
	public static final int ALLOWED_3G = 0;
	public static final int WIFI_ONLY = 1;
	
	//字体设置
	public final static int FONTSIZE_EXTRA_SMALL = -2;
	public final static int FONTSIZE_SMALL = -1;
	public final static int FONTSIZE_MEDIUM = 0;
	public final static int FONTSIZE_LARGE = 1;
	public final static int FONTSIZE_EXTRA_LARGE = 2;
	
	//补贴钱包设置
	public final static int UNIT_TOSET = 0;
	public final static int UNIT_YUAN_DEFAULT = 1;
	public final static int UNIT_TIMES_DEFAULT = 2;
	public final static int UNIT_YUAN = 3;
	public final static int UNIT_TIMES = 4;
	//01为补贴计金额，02为补贴计次
	public final static String UNIT_YUAN_DEFAULT_STRING = "01";
	public final static String UNIT_TIMES_DEFAULT_STRING = "02";
	
	//------------ 读卡及联网数据 ------------
	
	//使用服务
	public final static String SERVICE_STRING = "chinatelecom.mwallet.appservice.CTWalletService";
	
	//数据格式
	public final static int RES_PACKAGE_TYPE_JSON = 0;
	public final static int RES_PACKAGE_TYPE_XML = 1;
	
	//sendAuthenticateRequest操作类型
//	0x01圈存 
//	0x02圈存结果通知 
//	0x03获取公钥 
//	0x04冲正 
//	0x05获取账户信息 
//	0x06其他
	public final static String OPR_TYPE_DOWNLOAD_AUTH = "01";
	public final static String OPR_TYPE_DELETE_AUTH = "04";
	
	//transmitNetwork的操作类型（DEAL_TYPE）
//	0x01圈存 
//	0x02圈存结果通知 
//	0x03获取公钥 
//	0x04冲正 
//	0x05获取账户信息 
//	0x06其他
	public final static String OPR_TYPE_LOAD = "01";
	public final static String OPR_TYPE_LOAD_RESULT = "02";
	public final static String OPR_TYPE_GET_PK = "03";
	public final static String OPR_TYPE_FLUSH = "04";
	public final static String OPR_TYPE_GET_ACCOUNT = "05";
	public final static String OPR_TYPE_OTHERS = "06";
	
	//transmitNetwork的Params中的操作类型（DEAL_TYPE）：
//	0x01：圈存请求
//	0x02：圈存结果通知
//	0x03：获取公钥
//	0x04：冲正
//	0x05：账户信息查询
//	0x06：其他
//	0x07：获取用户电话号码
//	0x08：个性化信息修改操作
//	0x09：申请业务
	public final static String DEAL_TYPE_QUERY = "05";
	public final static String DEAL_TYPE_GET_PHONENUM = "07";
	
	//查询内容	(QUERY_CONTENT)		Hex String	1	0x01：查询消费清单
//												0x02：查询充值清单
//												0x03：查询考勤打卡清单
//												0x04：查询考勤结果
//												0x05：查询考勤汇总记录
//												0x06：查询考勤班次
//												0x07：查询请假
//												0x08：查询会议信息
	public final static String QUERY_CONTENT_COMSUMPTION = "1";
	public final static String QUERY_CONTENT_DEPOSIT = "2";
	public final static String QUERY_CONTENT_CLOCK_RECORD = "3";
	public final static String QUERY_ATTENDANCE_RECORD = "4";
	public final static String QUERY_ATTENDANCE_RECORD_SUMMARY = "5";
	public final static String QUERY_CONTENT_DUTY = "6";
	public final static String QUERY_LEAVE = "7";
	public final static String QUERY_CONFERENCE = "8";
	
	//查询结果中的常量
	//1.卡内交易记录类型
//	交易类型标识（TTI）	用于标识持卡人选择的交易类型（例如：圈存、圈提、消费等等）而分配的一个值。
//	0x01 - ED圈存
//	0x02 - EP圈存
//	0x05 - ED消费
//	0x06 - EP消费
//	0x03 - 圈提
//	0x04 - ED取款
//	0x07 - ED修改透支限额
//	0x08 - 信用消费
	public final static String TTI_ED_LOAD = "01";
	public final static String TTI_EP_LOAD = "02";
	public final static String TTI_ED_PURCHASE = "05";
	public final static String TTI_EP_PURCHASE = "06";
	
	//2.钱包类型：WALLET_TYPE
//	1:主钱包；
//  2:补贴钱包金额；
//  3:补贴钱包计次   
	public final static String WALLET_TYPE_1 = "1";
	public final static String WALLET_TYPE_2 = "2";
	public final static String WALLET_TYPE_3 = "3";
	
	//3.考勤明细结果：STATUS
//	1：正常（上班正常下班正常）；
//  2： 迟到（上班迟到下班正常）；
//  3：早退（上班正常下班早退）；
//  4：缺勤（上班未刷下班未刷）；
//  5：请假；
//  6：上班迟到下班早退
//  7：上班迟到下班未刷
//  8：上班正常下班未刷
//  9：上班未刷下班正常
//  10：上班未刷下班早退
//  11：其他
	public final static String ATT_STATUS_1 = "1";
	public final static String ATT_STATUS_2 = "2";
	public final static String ATT_STATUS_3 = "3";
	public final static String ATT_STATUS_4 = "4";
	public final static String ATT_STATUS_5 = "5";
	public final static String ATT_STATUS_6 = "6";
	public final static String ATT_STATUS_7 = "7";
	public final static String ATT_STATUS_8 = "8";
	public final static String ATT_STATUS_9 = "9";
	public final static String ATT_STATUS_10 = "10";
	public final static String ATT_STATUS_11 = "11";
	
	//4.考勤汇总结果：STATUS
//	1：正常（上班正常下班正常）；
//  2： 迟到（上班迟到下班正常）；
//  3：早退（上班正常下班早退）；
//  4：缺勤（上班未刷下班未刷）；
//  5：请假；
//  6：上班迟到下班早退
//  7：上班迟到下班未刷
//  8：上班正常下班未刷
//  9：上班未刷下班正常
//  10：上班未刷下班早退
//  11：其他
	public final static String ATT_SUM_STATUS_1 = "1";
	public final static String ATT_SUM_STATUS_2 = "2";
	public final static String ATT_SUM_STATUS_3 = "3";
	public final static String ATT_SUM_STATUS_4 = "4";
	public final static String ATT_SUM_STATUS_5 = "5";
	public final static String ATT_SUM_STATUS_6 = "6";
	public final static String ATT_SUM_STATUS_7 = "7";
	public final static String ATT_SUM_STATUS_8 = "8";
	public final static String ATT_SUM_STATUS_9 = "9";
	public final static String ATT_SUM_STATUS_10 = "10";
	public final static String ATT_SUM_STATUS_11 = "11";

	//5.会议状态：MEETING_STATUS
//  0：未请假，
//  1：已请假待批准；
//  2:已批准请假，
//  3：批准结果为不允许请假
	public final static String MEETING_STATUS_0 = "0";
	public final static String MEETING_STATUS_1 = "1";
	public final static String MEETING_STATUS_2 = "2";
	public final static String MEETING_STATUS_3 = "3";
	
	//6.请假类型
//	1.事假
//  2.病假
//  3.补休
//  4.调休
//  5.补卡
//  6.出差
//  7.其他
//  8.年假
	public final static String LEAVE_TYPE_1 = "1";
	public final static String LEAVE_TYPE_2 = "2";
	public final static String LEAVE_TYPE_3 = "3";
	public final static String LEAVE_TYPE_4 = "4";
	public final static String LEAVE_TYPE_5 = "5";
	public final static String LEAVE_TYPE_6 = "6";
	public final static String LEAVE_TYPE_7 = "7";
	public final static String LEAVE_TYPE_8 = "8";
	
	//7.考勤请假状态：HOLIDAY_FLAG
//	0:待批；
//	1:批准假期；
//	2:批准不允许请假
	public final static String HOLIDAY_FLAG_0 = "0";
	public final static String HOLIDAY_FLAG_1 = "1";
	public final static String HOLIDAY_FLAG_2 = "2";

	//------------ 刷新 ------------
	
	//刷新结果
	public static final int REFRESH_SUCCEED = 0;
	public static final int REFRESH_ERR_NET = 1;	
	public static final int REFRESH_ERR_INFO = 2;	
	public static final int REFRESH_ERR_UNKNOWN = 3;	
	
	//刷新模式
	public static final char NORMAL = 0;	//正常刷新
	public static final char LOAD = 1;		//加载更多项
	
	//刷新状态
	public static final char REFRESHING = 0;
	public static final char NOT_REFRESHING = 1;
	
	//------------ 操作 ------------
	
	//操作结果
	public static final int SUCCESS = 0;
	public static final int FAIL = 1;
	
	//操作类别
	public static final int OP_AUTHENTICATE_DOWNLOAD = 1;
	public static final int OP_AUTHENTICATE_DELETE = 2;
	public static final int OP_FROM_FRAGMENT_UPDATE_FONTSIZE = 3;
	public static final int OP_FROM_ACTIVITY_UPDATE_FONTSIZE = 4;
	public static final int OP_FROM_FRAGMENT_GETTRADELIST = 6;
	public static final int OP_FROM_FRAGMENT_CONSUMPTION_SHOW_ERROR_DIALOG = 7;
	public static final int OP_FROM_FRAGMENT_ATTENDANCE_SHOW_ERROR_DIALOG = 8;
	public static final int OP_FROM_FRAGMENT_CONFERENCE_SHOW_ERROR_DIALOG = 9;
	public static final int OP_FROM_FRAGMENT_SETTINGS_SHOW_ERROR_DIALOG = 10;
	public static final int OP_FROM_FRAGMENT_ASKFORLEAVE_SHOW_RESULT_DIALOG = 11;
	public static final int OP_FROM_FRAGMENT_LEAVEQUERY_SHOW_ERROR_DIALOG = 12;
	public static final int OP_FROM_ACTIVITY_RETRY = 13;
	public static final int OP_FROM_FRAGMENT_UPDATE_UNIT = 14;
	public static final int OP_FROM_ACTIVITY_UPDATE_UNIT = 15;
	public static final int OP_FROM_FRAGMENT_CONSUMPTION_SHOW_SETTINGS_DIALOG = 16;
	
	//----------- 错误码 ----------
	public static final String ERR_REF_CONS_0_UNKNOWN = "00";
	public static final String ERR_REF_CONS_0_OM = "01";
	public static final String ERR_REF_CONS_0 = "02";
	public static final String ERR_REF_CONS_1_UNKNOWN = "03";
	public static final String ERR_REF_CONS_1_OM = "04";
	public static final String ERR_REF_CONS_1 = "05";
	public static final String ERR_REF_CONS_2_UNKNOWN = "06";
	public static final String ERR_REF_CONS_2_NET = "07";
	public static final String ERR_REF_CONS_2 = "08";
	public static final String ERR_REF_CONS_3_UNKNOWN = "09";
	public static final String ERR_REF_CONS_3_NET = "10";
	public static final String ERR_REF_CONS_3 = "11";
	public static final String ERR_REF_ATT_0_UNKNOWN = "12";
	public static final String ERR_REF_ATT_0_NET = "13";
	public static final String ERR_REF_ATT_0 = "14";
	public static final String ERR_REF_ATT_1_UNKNOWN = "15";
	public static final String ERR_REF_ATT_1_NET = "16";
	public static final String ERR_REF_ATT_1 = "17";
	public static final String ERR_REF_ATT_2_UNKNOWN = "18";
	public static final String ERR_REF_ATT_2_NET = "19";
	public static final String ERR_REF_ATT_2 = "20";
	public static final String ERR_REF_ATT_3_UNKNOWN = "21";
	public static final String ERR_REF_ATT_3_NET = "22";
	public static final String ERR_REF_ATT_3 = "23";
	public static final String ERR_REF_CONF_0_UNKNOWN = "24";
	public static final String ERR_REF_CONF_0_NET = "25";
	public static final String ERR_REF_CONF_0 = "26";
	public static final String ERR_REF_CONF_1_UNKNOWN = "27";
	public static final String ERR_REF_CONF_1_NET = "28";
	public static final String ERR_REF_CONF_1 = "29";
	public static final String ERR_REF_CONF_2_UNKNOWN = "30";
	public static final String ERR_REF_CONF_2_NET = "31";
	public static final String ERR_REF_CONF_2 = "32";
	public static final String ERR_REF_CONF_3_UNKNOWN = "33";
	public static final String ERR_REF_CONF_3_NET = "34";
	public static final String ERR_REF_CONF_3 = "35";
	public static final String DATE_END_BEFORE_START = "36";
	public static final String TIME_END_BEFORE_START = "37";
	public static final String ERR_REF_LEAVE_UNKNOWN = "38";
	public static final String ERR_REF_LEAVE_NET = "39";
	public static final String ERR_REF_LEAVE = "40";
	public static final String ERR_REF_CONS_0_ACDENIED = "41";
	public static final String GUESS_UNIT_YUAN = "42";
	public static final String GUESS_UNIT_TIMES = "43";
	
}
