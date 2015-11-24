package com.telecompp.engine;

import android.app.Activity;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.telecompp.ContextUtil;
import com.telecompp.util.LoggerHelper;
import com.telecompp.util.ResponseExceptionInfo;
import com.telecompp.util.SumaConstants;
import com.telecompp.util.TerminalDataManager;
import com.transport.cypher.KEYSUtil;
import com.transport.cypher.KEYSUtilProxy;

/**
 * 初始化网络环境
 * 
 * @author poet
 * 
 */
public class InitEngine {
	private static boolean cardExchange = false;
	private static String TerminalModelNum = null;
	private static String IMEI = null;
	
	private static LoggerHelper logger = new LoggerHelper(InitEngine.class);
	
	/**
	 * 0x01终端会话密钥交换失败
	 * 0x02上传密钥出错
	 * 0x03注册失败
	 * 0x04卡会话密钥错误
	 * 
	 * @return
	 */
	public static int initEnv(String phoneNum) {
		try {
			TerminalDataManager.setVerNo("0.0.4");// 使用一个固定版本
			logger.printLogOnSDCard("==setCSN==");
			// TODO 如何处理截取20个长度是否合理
			TerminalDataManager.setCSN(KEYSUtilProxy.getCardICCID());

			// 898603
			String iccid = getCardICCID();
			if (iccid == null || iccid.equals("")) {
				// 如果iccid为空的话, 将iccid设置成csn
				iccid = KEYSUtilProxy.getCardICCID();
			}
			logger.printLogOnSDCard("获取到的iccid==");
			TerminalDataManager.setICCID(iccid);// 后台ICCID的限制
			logger.printLogOnSDCard("==setVersionCode==");
			TerminalDataManager.setVersionCode(SumaConstants.VERSION_CODE);// 固定数据
			logger.printLogOnSDCard("==setIMEI==");
			TerminalDataManager.setIMEI(getIMEI());
			logger.printLogOnSDCard("==setTel==");
			TerminalDataManager.setTel(getTel());
			// 手机型号
			logger.printLogOnSDCard("==setTerminalModel==");
			TerminalDataManager.setTerminalModel(getDeviceInfo());
			fm_sessionKeyExchange sessionKeyExchangeHandler = new fm_sessionKeyExchange();
			
			logger.printLogOnSDCard("开始终端会话密钥交换");
			
			// 终端会话密钥交换
			if( sessionKeyExchangeHandler
					.terminal_SessionKey_Start() == null) {
				logger.printLogOnSDCard("终端会话密钥交换异常" + SumaConstants.ERROR_TERMINAL_SECRET_EXCHANGE_FAILD_MSG);
				ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_TERMINAL_SECRET_EXCHANGE_FAILD_MSG);
				return Integer.parseInt(SumaConstants.ERROR_TERMINAL_SECRET_EXCHANGE_FAILD);
			}
			else{
				TerminalDataManager.setbAlreadyCardSessionKeyExchange(false);
				if (cardExchange) {
					logger.printLogOnSDCard("开始卡片会话密钥交换");
					return cardKeyExchange();
				} else {
					// wb===2C版本 注册
					IMEI = TerminalDataManager.getIMEI();// IMEI
					TerminalModelNum = TerminalDataManager.getTerminalModel();// 终端型号

//					String terminalRegisterResult = terminalRegister2C();
					logger.printLogOnSDCard("开始终端注册");
					String terminalRegisterResult = terminalRegister2YJT(phoneNum);
					logger.printLogOnSDCard("终端注册结束");
					if (terminalRegisterResult == null) {
						logger.printLogOnSDCard("终端注册结束--异常--" + ResponseExceptionInfo.getErrorMsgFromHttpResponseCode(ResponseExceptionInfo.getHttpResponseCode()));
						ResponseExceptionInfo.setErrorMsg(ResponseExceptionInfo.getErrorMsgFromHttpResponseCode(ResponseExceptionInfo.getHttpResponseCode()));
						return Integer.parseInt(SumaConstants.ERROR_TERMINAL_REGISTER_FAILD);//终端注册失败
					} else {
						cardExchange = true;
						logger.printLogOnSDCard("开始卡片会话密钥交换");
						return cardKeyExchange();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.printLogOnSDCard("支付环境初始化异常:" + e.toString());
			return 0xff;// 未知错误
		}
	}

	public static String getCardICCID() {
		String ICCID = null;
		try {
			TelephonyManager tm = (TelephonyManager) ContextUtil.getInstance()
					.getSystemService(Activity.TELEPHONY_SERVICE);

			ICCID = tm.getSimSerialNumber().toUpperCase(); // 取出ICCID
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ICCID == null) {
			return "";
		}

		return ICCID;
	}

	public static String getIMEI() {
		String IMEI = null;
		try {
			TelephonyManager tm = (TelephonyManager) ContextUtil.getInstance()
					.getSystemService(Activity.TELEPHONY_SERVICE);

			IMEI = tm.getDeviceId().toUpperCase(); // 取出IMEI
			logger.printLogOnSDCard("==IMEI==" + IMEI);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (IMEI == null) {
			return "";
		}

		return IMEI;
	}

	public static String getTel() {
		String Tel = null;
		try {
			TelephonyManager tm = (TelephonyManager) ContextUtil.getInstance()
					.getSystemService(Activity.TELEPHONY_SERVICE);

			Tel = tm.getLine1Number().toUpperCase(); // 取出Tel
			//
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Tel == null) {
			return "";
		}

		return Tel;
	}

	public static String getDeviceInfo() {
		String model = Build.MODEL;
		return model;
	}
	
	
	/**
	 * 发起注册请求 2YJT版本
	 * 
	 * @return 注册结果
	 */
	public static String terminalRegister2YJT(String phoneNum) {
		fm_terminalInfoManager terminalInfoManagerHandler = new fm_terminalInfoManager();
		String result = terminalInfoManagerHandler.terminalRegister2YJT(IMEI,
				TerminalModelNum, phoneNum);
		if (result == null) {
			if (TerminalDataManager.getGeneralCodeFromService().equals("F304")) {
				return "01";
			}
			return null;
		}

		// 注册成功后，存储账号和密码
		return "00";
	}
	
	
	/**
	 * 发起注册请求 2C版本
	 * 
	 * @return 注册结果
	 */
	public static String terminalRegister2C() {
		fm_terminalInfoManager terminalInfoManagerHandler = new fm_terminalInfoManager();
		String result = terminalInfoManagerHandler.terminalRegister2C(IMEI,
				TerminalModelNum);
		if (result == null) {
			if (TerminalDataManager.getGeneralCodeFromService().equals("F304")) {
				return "01";
			}
			return null;
		}

		// 注册成功后，存储账号和密码
		return "00";
	}
	
	
	private static int cardKeyExchange(){
		// 如果是, 则说明已经注册过了该进行卡片回话密钥交换了,
		if (KEYSUtil.isNeedGenKeys()) {
			if (0 != fm_sessionKeyExchange
					.commitKeys(KEYSUtil.genKeys())) {
				ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_CARD_SECRET_EXCHANGE_FAILD_MSG);
				return Integer.parseInt(SumaConstants.ERROR_CARD_SECRET_EXCHANGE_FAILD);//上传密钥出错
			}
		} 
		int i = cardExchange();
		if(i != 0)
			return i;
		if (TerminalDataManager
				.getbAlreadyCardSessionKeyExchange()) {
			// 省去登录的步骤直接跳到主页面
			return 0;
		}
		ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_CARD_SECRET_EXCHANGE_FAILD_MSG);
		return Integer.parseInt(SumaConstants.ERROR_CARD_SECRET_EXCHANGE_FAILD);
	}
	private static int cardExchange() {
		fm_sessionKeyExchange sessionKeyExchangeHandler = new fm_sessionKeyExchange();
		String c_sessionKeyResultString = sessionKeyExchangeHandler
				.card_SessionKey_Start();
		if (c_sessionKeyResultString == null) {
			ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_CARD_SECRET_EXCHANGE_FAILD_MSG);
			return Integer.parseInt(SumaConstants.ERROR_CARD_SECRET_EXCHANGE_FAILD);
		} else {
			TerminalDataManager.setbAlreadyCardSessionKeyExchange(true);
			return 0;
		}
	}
}
