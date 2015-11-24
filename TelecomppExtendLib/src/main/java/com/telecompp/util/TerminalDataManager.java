package com.telecompp.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.telecom.pp.extend.BuildConfig;
import com.telecompp.ContextUtil;
import com.telecompp.util.SumaConstants;

/**
 * 终端数据管理
 * @author xiongdazhuang 
 *
 */
public class TerminalDataManager implements SumaConstants
{
	private static Boolean bContinuousTradeFlag = false;//是否“连续交易标识”
	
	private static String signInTime = null;
	private static String generalCodeFromService = null;//来自服务器的错误状态码 如 F001 网络异常
	
	private static int unsettledRecordQueryNumber = 0;//终端保存的未结算的脱机交易记录数，登陆进入主界面后同步。
	
	private static Boolean bAlreadyCardSessionKeyExchange = false;//已经进行卡片会话密钥交换标识
	private static Boolean bAlreadyAccountLogin = false;//已经登录标识
	
	private static String terminalSessionKeyData = null;
	private static String cardSessionKeyData = null;

	private static Boolean masterKeyNeedUpdate = null;//UIM卡未进行过主控密钥更新
	private static String downloadParameterStatus = null;//UIM卡返回的参数下载标识，“07”代表不需要更新
	private static String unCompleteTradeType = null;//未完成交易类型
	private static String paramVerStatusFlag = null;//终端参数版本状态，来自服务器下行报文，报文头
	
	private static String IMEI = null;
	private static String Tel = null;

	private static String CSN = null;
	private static String terminalID = null;
	private static String ICCID = null;
	private static String terminalModel = null;
	private static String verNo = null;
	private static String versionCode = null;
	private static String verStatus = null;
	private static String paramVerStatus = null;

	private static String sessionKey = null;
	private static String cookieId = null;


	private static String interfaceType = "2"; // 请求类型
												// 值为“1”EDEP；值为“2”POSP
	private static String exceptionCode = "";

	public static final int  APP_BEST_PAY = 1;
	public static final int  APP_TRANSPORT = 2;
	public static final int  APP_ALL = 0;
	/**
	 * 支付方式
	 * "App":正常使用碰碰支付
	 * "payEnterFlag":支付插件方式使用
	 */
	
	/*********************************************************
	 * 支付插件 数据
	 **********************************************************/
	private static String plug_merchantName = null;
	private static String plug_merchantNumber = null;
	private static String plug_merchantSerialNB = null;
	private static String plug_paySerialNB = null;
	private static String plug_amount = null;
	private static String plug_tradeType = null;
	private static String plug_signature = null;
	private static int plug_isSecure; //是否有密码、扣款
	private static String plug_cypher = null;//加密后交费易密文
	private static String plug_payAccount = null;//支付账号,交费易登录账号
	
	private static String plug_phoneNum = null;	//	翼机通手机号
	private static String plug_orderseq = null;	//	订单号(退款的时候使用)
	
	//	翼机通信息
	private static String plug_yjtMerchantId = null;	//	翼机通商户Id
	private static String plug_yjtMerchantName = null;	//	翼机通商户名称
	private static String plug_yjtMerchantRechargeType = null;	//	翼机通商户类型 目前有1 2 3中  不同类型对swp卡和大白卡支持不同
	private static boolean isSwpCard = false;	// 标记是否是swp卡
	
	// 商户充值方式
	public static final String YJT_MERCHANT_TYPE_1 = "1"; 
	public static final String YJT_MERCHANT_TYPE_2 = "2";
	public static final String YJT_MERCHANT_TYPE_3 = "3"; 
	

	public static boolean getIsSwpCard() {
		return isSwpCard;
	}

	public static void setIsSwpCard(boolean isSwpCard) {
		TerminalDataManager.isSwpCard = isSwpCard;
	}

	public static String getPlug_yjtMerchantRechargeType() {
		return plug_yjtMerchantRechargeType;
	}

	public static void setPlug_yjtMerchantRechargeType(String plug_yjtMerchantRechargeType) {
		TerminalDataManager.plug_yjtMerchantRechargeType = plug_yjtMerchantRechargeType;
	}

	private static String plug_yjtBalance = null;	//	卡内余额
	
	
	public static String getPlug_yjtBalance() {
		return plug_yjtBalance;
	}

	public static void setPlug_yjtBalance(String plug_yjtBalance) {
		TerminalDataManager.plug_yjtBalance = plug_yjtBalance;
	}

	public static String getPlug_yjtMerchantName() {
		return plug_yjtMerchantName;
	}

	public static void setPlug_yjtMerchantName(String plug_yjtMerchantName) {
		TerminalDataManager.plug_yjtMerchantName = plug_yjtMerchantName;
	}

	public static String getPlug_yjtMerchantId() {
		return plug_yjtMerchantId;
	}

	public static void setPlug_yjtMerchantId(String plug_yjtMerchantId) {
		TerminalDataManager.plug_yjtMerchantId = plug_yjtMerchantId;
	}

	public static String getPlug_orderseq() {
		return plug_orderseq;
	}

	public static void setPlug_orderseq(String plug_orderseq) {
		TerminalDataManager.plug_orderseq = plug_orderseq;
	}

	public static String getPlug_phoneNum() {
		return plug_phoneNum;
	}

	public static void setPlug_phoneNum(String plug_phoneNum) {
		TerminalDataManager.plug_phoneNum = plug_phoneNum;
	}

	public static String PayAccount;
	

	public static String getPlug_cypher() {
		return plug_cypher;
	}

	public static void setPlug_cypher(String plug_cypher) {
		TerminalDataManager.plug_cypher = plug_cypher;
	}



	public static String getPlug_payAccount() {
		return plug_payAccount;
	}

	public static void setPlug_payAccount(String plug_payAccount) {
		TerminalDataManager.plug_payAccount = plug_payAccount;
	}

	public static int getPlug_isSecure() {
		return plug_isSecure;
	}

	public static void setPlug_isSecure(int plug_isSecure) {
		TerminalDataManager.plug_isSecure = plug_isSecure;
	}

	//支付操作数据
	private static String plug_flag_showPayInfoView_restart = "";
	private static String plug_flag_enterType = "";
	private static int plug_flag_payResultCode = 0x00;
	private static String plug_flag_resultText = "";
	private static String plug_flag_bPayed = "";
	
	
	
	


	/**
	 * 打印log
	 */
	public static void printLog(String logcatFilter, String log)
	{
		if (BuildConfig.DEBUG)
		{
			Log.d(logcatFilter, log);
		}
	}

	public static String getSessionKey()
	{
		return sessionKey;
	}

	public static void setSessionKey(String sessionKey)
	{
		TerminalDataManager.sessionKey = sessionKey;
	}



	public static String getTerminalModel()
	{
		return terminalModel;
	}

	public static void setTerminalModel(String terminalModel)
	{
		TerminalDataManager.terminalModel = terminalModel;
	}

	public static String getCSN()
	{
		return CSN;
	}

	public static void setCSN(String cSN)
	{
		CSN = cSN;
	}
	public static String getTermianlID()
	{
		return terminalID;
	}

	public static void setTerminalID(String id)
	{
		terminalID = id;
	}

	public static String getICCID()
	{
		return ICCID;
	}

	public static void setICCID(String iCCID)
	{
		ICCID = iCCID;
	}

	public static String getVerNo()
	{
		return verNo;
	}

	public static void setVerNo(String verNo)
	{
		TerminalDataManager.verNo = verNo;
	}

	public static String getVerStatus()
	{
		return verStatus;
	}

	public static void setVerStatus(String verStatus)
	{
		TerminalDataManager.verStatus = verStatus;
	}

	public static String getParamVerStatus()
	{
		return paramVerStatus;
	}

	public static void setParamVerStatus(String paramVerStatus)
	{
		TerminalDataManager.paramVerStatus = paramVerStatus;
	}

	public static String getInterfaceType()
	{
		return interfaceType;
	}

	public static void setInterfaceType(String interfaceType)
	{
		TerminalDataManager.interfaceType = interfaceType;
	}

	public static String getExceptionCode()
	{
		return exceptionCode;
	}

	public static void setExceptionCode(String exceptionCode)
	{
		TerminalDataManager.exceptionCode = exceptionCode;
	}

	public static String getVersionCode()
	{
		return versionCode;
	}

	public static void setVersionCode(String versionCode)
	{
		TerminalDataManager.versionCode = versionCode;
	}

	public static Boolean getMasterKeyNeedUpdate()
	{
		return masterKeyNeedUpdate;
	}

	public static void setMasterKeyNeedUpdate(Boolean masterKeyNeedUpdate)
	{
		TerminalDataManager.masterKeyNeedUpdate = masterKeyNeedUpdate;
	}

	public static String getIMEI()
	{
		return IMEI;
	}

	public static void setIMEI(String iMEI)
	{
		IMEI = iMEI;
	}

	public static String getTel()
	{
		return Tel;
	}

	public static void setTel(String tel)
	{
		Tel = tel;
	}

	public static String getDownloadParameterStatus()
	{
		return downloadParameterStatus;
	}

	public static void setDownloadParameterStatus(String downloadParameterStatus)
	{
		TerminalDataManager.downloadParameterStatus = downloadParameterStatus;
	}

	public static String getTerminalSessionKeyData()
	{
		return terminalSessionKeyData;
	}

	public static void setTerminalSessionKeyData(String terminalSessionKeyData)
	{
		TerminalDataManager.terminalSessionKeyData = terminalSessionKeyData;
	}

	public static String getCardSessionKeyData()
	{
		return cardSessionKeyData;
	}

	public static void setCardSessionKeyData(String cardSessionKeyData)
	{
		TerminalDataManager.cardSessionKeyData = cardSessionKeyData;
	}

	public static String getCookieId()
	{
		return cookieId;
	}

	public static void setCookieId(String cookieId)
	{
		TerminalDataManager.cookieId = cookieId;
	}

	public static String getPlug_merchantName()
	{
		return plug_merchantName;
	}

	public static void setPlug_merchantName(String plug_merchantName)
	{
		TerminalDataManager.plug_merchantName = plug_merchantName;
	}

	public static String getPlug_merchantNumber()
	{
		return plug_merchantNumber;
	}

	public static void setPlug_merchantNumber(String plug_merchantNumber)
	{
		TerminalDataManager.plug_merchantNumber = plug_merchantNumber;
	}

	public static String getPlug_merchantSerialNB()
	{
		return plug_merchantSerialNB;
	}

	public static void setPlug_merchantSerialNB(String plug_merchantSerialNB)
	{
		TerminalDataManager.plug_merchantSerialNB = plug_merchantSerialNB;
	}

	public static String getPlug_paySerialNB()
	{
		return plug_paySerialNB;
	}

	public static void setPlug_paySerialNB(String plug_paySerialNB)
	{
		TerminalDataManager.plug_paySerialNB = plug_paySerialNB;
	}

	public static String getPlug_amount()
	{
		return plug_amount;
	}

	public static void setPlug_amount(String plug_amount)
	{
		TerminalDataManager.plug_amount = plug_amount;
	}

	public static String getPlug_signature()
	{
		return plug_signature;
	}

	public static void setPlug_signature(String plug_signature)
	{
		TerminalDataManager.plug_signature = plug_signature;
	}



	public static String getPlug_tradeType()
	{
		return plug_tradeType;
	}

	public static void setPlug_tradeType(String plug_tradeType)
	{
		TerminalDataManager.plug_tradeType = plug_tradeType;
	}

	public static String getPlug_flag_showPayInfoView_restart()
	{
		return plug_flag_showPayInfoView_restart;
	}

	public static void setPlug_flag_showPayInfoView_restart(String plug_flag_showPayInfoView_restart)
	{
		TerminalDataManager.plug_flag_showPayInfoView_restart = plug_flag_showPayInfoView_restart;
	}

	public static String getPlug_flag_enterType()
	{
		return plug_flag_enterType;
	}

	public static void setPlug_flag_enterType(String plug_flag_enterType)
	{
		TerminalDataManager.plug_flag_enterType = plug_flag_enterType;
	}

	public static String getPlug_flag_resultText()
	{
		return plug_flag_resultText;
	}

	public static void setPlug_flag_resultText(String plug_flag_resultText)
	{
		TerminalDataManager.plug_flag_resultText = plug_flag_resultText;
	}

	public static String getPlug_flag_bPayed()
	{
		return plug_flag_bPayed;
	}

	public static void setPlug_flag_bPayed(String plug_flag_bPayed)
	{
		TerminalDataManager.plug_flag_bPayed = plug_flag_bPayed;
	}

	public static int getPlug_flag_payResultCode()
	{
		return plug_flag_payResultCode;
	}

	public static void setPlug_flag_payResultCode(int plug_flag_payResultCode)
	{
		TerminalDataManager.plug_flag_payResultCode = plug_flag_payResultCode;
	}


	public static Boolean getbAlreadyCardSessionKeyExchange()
	{
		return bAlreadyCardSessionKeyExchange;
	}

	public static void setbAlreadyCardSessionKeyExchange(Boolean bAlreadyCardSessionKeyExchange)
	{
		TerminalDataManager.bAlreadyCardSessionKeyExchange = bAlreadyCardSessionKeyExchange;
	}

	public static Boolean getbAlreadyAccountLogin()
	{
		return bAlreadyAccountLogin;
	}

	public static void setbAlreadyAccountLogin(Boolean bAlreadyAccountLogin)
	{
		TerminalDataManager.bAlreadyAccountLogin = bAlreadyAccountLogin;
	}

	public static int getUnsettledRecordQueryNumber()
	{
		return unsettledRecordQueryNumber;
	}

	public static void setUnsettledRecordQueryNumber(int unsettledRecordQueryNumber)
	{
		TerminalDataManager.unsettledRecordQueryNumber = unsettledRecordQueryNumber;
	}

	public static String getUnCompleteTradeType()
	{
		return unCompleteTradeType;
	}

	public static void setUnCompleteTradeType(String unCompleteTradeType)
	{
		TerminalDataManager.unCompleteTradeType = unCompleteTradeType;
	}

	public static String getParamVerStatusFlag()
	{
		return paramVerStatusFlag;
	}

	public static void setParamVerStatusFlag(String paramVerStatusFlag)
	{
		TerminalDataManager.paramVerStatusFlag = paramVerStatusFlag;
	}

	public static String getGeneralCodeFromService()
	{
		if(generalCodeFromService == null)
		{
			generalCodeFromService = "";
		}
		return generalCodeFromService;
	}

	public static void setGeneralCodeFromService(String generalCodeFromService)
	{
		TerminalDataManager.generalCodeFromService = generalCodeFromService;
	}

	public static Boolean getSignInStatus()
	{
	    if (signInTime == null) {
            return false;
	}

	    String dateString = SumaTerminalAids
                .getSystemTime("yyyyMMdd");
	    
	    if (signInTime.equals(dateString)) {
	        return true;
        }else {
            return false;
        }
		
	}

	public static void setSignInTime(String signInTime)
	{
		TerminalDataManager.signInTime = signInTime;
	}

	public static Boolean getbContinuousTradeFlag()
	{
		return bContinuousTradeFlag;
	}

	public static void setbContinuousTradeFlag(Boolean bContinuousTradeFlag)
	{
		TerminalDataManager.bContinuousTradeFlag = bContinuousTradeFlag;
	}
	
    public static String getErrorMsg(String msgCode) {
        String errorMSG = "未知错误！";
        if (msgCode.equals("03")) {
            errorMSG = "无效商户";
        } else if (msgCode.equals("05")) {
            errorMSG = "无效终端";
        } else if (msgCode.equals("08")) {
            errorMSG = "终端未签到";
        } else if (msgCode.equals("12")) {
            errorMSG = "无效交易";
        } else if (msgCode.equals("13")) {
            errorMSG = "无效金额";
        } else if (msgCode.equals("14")) {
            errorMSG = "无效卡号";
        } else if (msgCode.equals("15")) {
            errorMSG = "卡状态不正确";
        } else if (msgCode.equals("16")) {
            errorMSG = "卡帐户不存在";
        } else if (msgCode.equals("17")) {
            errorMSG = "卡帐户不存在";
        } else if (msgCode.equals("18")) {
            errorMSG = "账户未启用";
        } else if (msgCode.equals("20")) {
            errorMSG = "无效应答";
        } else if (msgCode.equals("22")) {
            errorMSG = "原交易已被冲正或被撤销";
        } else if (msgCode.equals("25")) {
            errorMSG = "未能找到文件上记录";
        } else if (msgCode.equals("30")) {
            errorMSG = "格式错误";
        } else if (msgCode.equals("31")) {
            errorMSG = "当日消费金额超限";
        } else if (msgCode.equals("32")) {
            errorMSG = "单笔交易金额超限";
        } else if (msgCode.equals("36")) {
            errorMSG = "帐户类型不存在";
        } else if (msgCode.equals("37")) {
            errorMSG = "帐户余额密文校验错";
        } else if (msgCode.equals("38")) {
            errorMSG = "超过允许的PIN试输入";
        } else if (msgCode.equals("39")) {
            errorMSG = "当天交易不允许退货";
        } else if (msgCode.equals("41")) {
            errorMSG = "参数未设置或不允许下载";
        } else if (msgCode.equals("51")) {
            errorMSG = "无足够的存款";
        } else if (msgCode.equals("52")) {
            errorMSG = "预授权完成金额大于预授权金额";
        } else if (msgCode.equals("54")) {
            errorMSG = "已过有效期";
        } else if (msgCode.equals("55")) {
            errorMSG = "不正确的PIN";
        } else if (msgCode.equals("61")) {
            errorMSG = "超出消费金额限制";
        } else if (msgCode.equals("63")) {
            errorMSG = "无效的金额";
        } else if (msgCode.equals("64")) {
            errorMSG = "原始金额不正确";
        } else if (msgCode.equals("68")) {
            errorMSG = "收到的回答太迟";
        } else if (msgCode.equals("75")) {
            errorMSG = "允许的输入PIN次数超限";
        } else if (msgCode.equals("96")) {
            errorMSG = "系统异常、失效";
        } else if (msgCode.equals("97")) {
            errorMSG = "POS终端号找不到";
        } else if (msgCode.equals("99")) {
            errorMSG = "PIN格式错";
        } else if (msgCode.equals("A0")) {
            errorMSG = "MAC校验错";
        } else if (msgCode.equals("F00B")) {
            errorMSG = "不支持该业务功能";
        } else if (msgCode.equals("F200")) {
            errorMSG = "商户未开户";
        } else if (msgCode.equals("F201")) {
            errorMSG = "商户已销户，不允许交易";
        } else if (msgCode.equals("F230")) {
            errorMSG = "商户风控信息不存在";
        } else if (msgCode.equals("F231")) {
            errorMSG = "商户单笔交易超限";
        } else if (msgCode.equals("F232")) {
            errorMSG = "商户日交易超额";
        } else if (msgCode.equals("F233")) {
            errorMSG = "商户周交易超额";
        } else if (msgCode.equals("F234")) {
            errorMSG = "商户月交易超额";
        } else if (msgCode.equals("F250")) {
            errorMSG = "网点风控信息不存在";
        } else if (msgCode.equals("F251")) {
            errorMSG = "网点单笔交易超限";
        } else if (msgCode.equals("F252")) {
            errorMSG = "网点日交易超额";
        } else if (msgCode.equals("F253")) {
            errorMSG = "网点周交易超额";
        } else if (msgCode.equals("F254")) {
            errorMSG = "网点月交易超额";
        } else if (msgCode.equals("F300")) {
            errorMSG = "终端信息未注册";
        } else if (msgCode.equals("F301")) {
            errorMSG = "终端已禁用";
        } else if (msgCode.equals("F302")) {
            errorMSG = "终端信息错误";
        } else if (msgCode.equals("F303")) {
            errorMSG = "平台数据异常";
        } else if (msgCode.equals("F304")) {
            errorMSG = "平台数据异常";
        } else if (msgCode.equals("F305")) {
            errorMSG = "平台数据异常";
        } else if (msgCode.equals("F306")) {
            errorMSG = "平台数据异常";
        } else if (msgCode.equals("F307")) {
            errorMSG = "平台数据异常";
        } else if (msgCode.equals("F308")) {
            errorMSG = "平台数据异常";
        } else if (msgCode.equals("F310")) {
            errorMSG = "终端参数信息找不到";
        } else if (msgCode.equals("F311")) {
            errorMSG = "终端参数数据错误";
        } else if (msgCode.equals("F312")) {
            errorMSG = "终端参数数据错误";
        } else if (msgCode.equals("F313")) {
            errorMSG = "终端参数数据错误";
        } else if (msgCode.equals("F320")) {
            errorMSG = "终端风控未设置";
        } else if (msgCode.equals("F321")) {
            errorMSG = "终端单笔交易超限";
        } else if (msgCode.equals("F322")) {
            errorMSG = "终端日交易超额";
        } else if (msgCode.equals("F323")) {
            errorMSG = "终端周交易超额";
        } else if (msgCode.equals("F324")) {
            errorMSG = "终端月交易超额";
        } else if (msgCode.equals("F330")) {
            errorMSG = "终端数据异常（F330）";
        } else if (msgCode.equals("F331")) {
            errorMSG = "终端数据异常(F331)";
        } else if (msgCode.equals("F332")) {
            errorMSG = "终端数据异常(F332)";
        } else if (msgCode.equals("F333")) {
            errorMSG = "终端月交易超额(F333)";
        } else if (msgCode.equals("F334")) {
            errorMSG = "终端月交易超额(F334)";
        } else if (msgCode.equals("A0001")) {
            errorMSG = "暂不支持的卡片";
        } else if (msgCode.equals("A0002")) {
            errorMSG = "服务器通讯异常";
        } else if (msgCode.equals("A0003")) {
            errorMSG = "该卡不支持此业务";
        } else if (msgCode.equals("FFFFFFFF")) {
            errorMSG = "NFC通讯失败";
        }
        return errorMSG;
    }
    
//    public static String getCardICCID() {
//		try {
//			TelephonyManager service = 	(TelephonyManager) ContextUtil.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
//			String imei=(service).getDeviceId();//IME
//			String iccid = service.getSimSerialNumber();
//			String imsi = service.getSubscriberId();
//			//对上面的3个值做一个组合
//			imei = imei==null||"".equals(imei)?"":imei;
//			imei += iccid==null||"".equals(iccid)?"":iccid;
//			imei += imsi==null||"".equals(imsi)?"":imsi;
//			 String uniqueIuniqueId= AlgoManager.MD5(imei);
//			// uniqueIuniqueId= AlgoManager.MD5(uniqueIuniqueId);
//			 if(uniqueIuniqueId==null||uniqueIuniqueId.equals("")||uniqueIuniqueId.length()<20){
//				 return null;
//			 }
//			 uniqueIuniqueId = uniqueIuniqueId.substring(0, 20);
//			 return uniqueIuniqueId;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
    
//    public static String getCertificateSHA1Fingerprint() {
//    	Context mContext = ContextUtil.getInstance();
//        PackageManager pm = mContext.getPackageManager();
//        String packageName = mContext.getPackageName();
//        int flags = PackageManager.GET_SIGNATURES;
//        PackageInfo packageInfo = null;
//        try {
//            packageInfo = pm.getPackageInfo(packageName, flags);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        Signature[] signatures = packageInfo.signatures;
//        byte[] cert = signatures[0].toByteArray();
//        InputStream input = new ByteArrayInputStream(cert);
//        CertificateFactory cf = null;
//        try {
//            cf = CertificateFactory.getInstance("X509");
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//        X509Certificate c = null;
//        try {
//            c = (X509Certificate) cf.generateCertificate(input);
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//        String hexString = null;
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA1");
//            byte[] publicKey = md.digest(c.getEncoded());
//            hexString = byte2HexFormatted(publicKey);
//        } catch (NoSuchAlgorithmException e1) {
//            e1.printStackTrace();
//        } catch (CertificateEncodingException e) {
//            e.printStackTrace();
//        }
//       return AlgoManager.MD5(hexString);
//    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }
}
