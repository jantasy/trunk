package com.telecompp.engine;

import java.util.Map;

import com.bestpay.plugin.Plugin;
import com.telecompp.ContextUtil;
import com.telecompp.util.SumaConstants;
import com.telecompp.util.SumaPostHandler;
import com.telecompp.util.TerminalDataManager;
import com.telecompp.xml.XmlHandler;
import com.telecompp.xml.XmlMgr_BusTradeBase;

public class YjtLoadRequestEngine  implements SumaConstants{

	private YjtLoadRequestEngine(){};
	
	
	/**
	 * 补贴圈存请求
	 */
	private static final String DEALTYPE_1 = "1";
	
	/**
	 * 补贴结果通知
	 */
	private static final String DEALTYPE_2 = "2";
	
	/**
	 * 补贴冲零请求
	 */
	private static final String DEALTYPE_15 = "15";
	
	/**
	 * 补助重领结果通知
	 */
	private static final String DEALTYPE_16 = "1６";
	
	
	/**
	 * 补贴申请
	 */
	private static final String APPLYTYPE_2 = "2";
	
	/**
	 * 钱包类型02  标识补贴钱包
	 */
	private static final String PURSETYPE_02 = "02";
	
	
	/**
	 * 5001 获取翼机通商户信息
	 * @param phoneNum: 电话号码
	 * @return
	 */
	public static Map<String, Object> getYjtMerchantInfo(final String phoneNum) {
		//	城市直接写"999888"
		String reqXml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				objMgr.addText("YJTPhone ", phoneNum); // IP地址
				//	手机的ICCID
				objMgr.addText("ICCID", TerminalDataManager.getICCID());
			}
			@Override
			protected String getMsgId() {
				return XML_MSGID_YJT_MERCHANTINFO;
			}
		}.get();
		//	数据首发模块
		SumaPostHandler postHandler = new SumaPostHandler();
		Map<String, Object> resMap = postHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, reqXml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
		if(resMap == null) {
			return null;
		} else {
			String data = (String) resMap.get(MAP_HTTP_RESPONSE_RESPONSE);
			if(data!=null)
				return XmlMgr_BusTradeBase.parse(data);
		}
		return null;
	}
	
	
	/**
	 * 5003 翼机通卡充值结果确认接口
	 * @param applicationSN	卡应用序列号
	 * @param consumeCount	电子钱包消费序号
	 * @param loadCount	电子钱包充值序号
	 * @param TAC	TAC校验码
	 * @param tranResultFlag	交易结果标志(0x01 表示异常交易记录, 0x99表示正常记录)
	 * @param tradeNum	交易流水号
	 * @param phoneNum	翼机通手机号
	 * @param transBeginAmount	交易前余额
	 * @return
	 */
	public static Map<String, Object> loadResultConfirm(final String applicationSN, final String consumeCount, final String loadCount, 
			final String TAC, final String tranResultFlag, final String tradeNum, 
			final String phoneNum, final String transBeginAmount, final String msgId) {
		//	城市直接写"999888"
		String reqXml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				//	手机的ICCID
				objMgr.addText("CARD_SERIAL", applicationSN);
				objMgr.addText("CONSUME_TIMES", consumeCount);
				objMgr.addText("RECHARGE_TIMES", loadCount);
				//	服务器端写死  "0"
//				objMgr.addText("TERMINAL_NO", terminalNo);
				objMgr.addText("TAC", TAC);
				objMgr.addText("TRAN_RESULT_FLAG", tranResultFlag);
				objMgr.addText("TradeNum", tradeNum);
				objMgr.addText("YJTPhone", phoneNum);
				objMgr.addText("TRANS_BEGIN_AMOUNT", transBeginAmount);
			}
			@Override
			protected String getMsgId() {
//				return XML_MSGID_YJT_LOADCONFIRM;
				return msgId;
			}
		}.get();
		//	数据首发模块
		SumaPostHandler postHandler = new SumaPostHandler();
		//	TODO 这里可以new 一个RetryTask类 用于如果服务器没有收到上传的TAC那么就执行 重试3次, 如果失败则存储到数据库中, 等待重试
		
		
		Map<String, Object> resMap = postHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, reqXml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
		if(resMap == null) {
			return null;
		} else {
			String data = (String) resMap.get(MAP_HTTP_RESPONSE_RESPONSE);
			if(data!=null)
				return XmlMgr_BusTradeBase.parse(data);
		}
		return null;
	}
	
	
	/**
	 * 5004   大白卡只扣款不充值确认
	 * @param tranResultFlag	交易结果标志(0x01 表示异常交易记录, 0x99表示正常记录)
	 * @param tradeNum	交易流水号
	 * @param phoneNum	翼机通手机号
	 * @return
	 */
	public static Map<String, Object> noLoadConfirm(final String tranResultFlag, final String tradeNum, 
			final String phoneNum) {
		//	城市直接写"999888"
		String reqXml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				//	手机的ICCID
				objMgr.addText("TRAN_RESULT_FLAG", tranResultFlag);
				objMgr.addText("TradeNum", tradeNum);
				objMgr.addText("YJTPhone", phoneNum);
			}
			@Override
			protected String getMsgId() {
				return XML_MSGID_YJT_NOLOADCONFIRM;
			}
		}.get();
		//	数据首发模块
		SumaPostHandler postHandler = new SumaPostHandler();
		
		Map<String, Object> resMap = postHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, reqXml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
		if(resMap == null) {
			return null;
		} else {
			String data = (String) resMap.get(MAP_HTTP_RESPONSE_RESPONSE);
			if(data!=null)
				return XmlMgr_BusTradeBase.parse(data);
		}
		return null;
	}
	
	/**
	 * 5002 翼机通卡充值申请
	 * @param applyType  申请类型 1:圈存申请, 2:补贴申请
	 * @param cardType	卡种标识 01:CPU 02:M1
	 * @param applicationSN  卡应用序列号
	 * @param purseType  钱包类型(01主钱包 02 补贴钱包)
	 * @param balance  交易前金额
	 * @param tradeMoney  交易金额
	 * @param consumeCount  电子钱包消费计数
	 * @param loadCount  电子钱包充值计数
	 * @param tradeDate  交易日期(YYYYMMDDHHMMSS)
	 * @param seudoRandomNumber  伪随机数
	 * @param mac1  MAC1
	 * @param tradeNum  交易流水号
	 * @param phoneNum 手机号码
	 * @param msgId  XML_MSGID_YJT_ED_LOAD:补贴钱包圈存;  XML_MSGID_YJT_LOAD:翼机通空圈
	 * @return
	 */
	public static Map<String,Object> loadApply(final String applyType, final String cardType, final String applicationSN,final String purseType, 
			final String balance, final String tradeMoney, final String consumeCount, 
			final String loadCount, final String tradeDate, final String seudoRandomNumber, 
			final String mac1, final String tradeNum, final String phoneNum, final String msgId){
		String reqxml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				objMgr.addText(Plugin.PRODUCTAMOUNT, ""+tradeMoney);// 100(以分为单位)
				objMgr.addText("CLIENTIP ", "192..168.1.123"); // IP地址
				//	申请类型
				objMgr.addText("APPLY_TYPE", applyType);
				//	卡种标识
				objMgr.addText("CARD_TYPE", cardType);
				//	卡应用序列号
				objMgr.addText("CARD_SERIAL", applicationSN);
				//	钱包类型
				objMgr.addText("WALLET_TYPE", purseType);
				//	交易前金额
				objMgr.addText("TRANS_BEGIN_AMOUNT", balance);
				//	交易金额
				objMgr.addText("TRANS_AMOUNT", tradeMoney);
				//	电子钱包消费计数
				objMgr.addText("CONSUME_TIMES", consumeCount);
				//	电子钱包充值计数
				objMgr.addText("RECHARGE_TIMES", loadCount);
				//	交易日期
				objMgr.addText("TRANS_DATE", tradeDate);
				//	伪随机数
				objMgr.addText("PRN", seudoRandomNumber);
				//	MAC1
				objMgr.addText("MAC1", mac1);
				//	交易流水号
				objMgr.addText("TradeNum", tradeNum);
				//	电话号码
				objMgr.addText("YJTPhone", phoneNum);
			}
			@Override
			protected String getMsgId() {
//				return XML_MSGID_YJT_LOAD;
				return msgId;
			}
		}.get();
		// 发送请求订单接口
		SumaPostHandler postHandler = new SumaPostHandler();
		Map<String, Object> resMap = postHandler
				.httpPostAndGetResponse(
						IpCons.SERVER_ADDRESS_IP_BUS,
						reqxml,
						ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
		if (resMap != null) {
			String temp = (String) resMap
					.get(MAP_HTTP_RESPONSE_RESPONSE);
			if (temp != null)
				return XmlMgr_BusTradeBase.parse(temp);
		}
		return null;
	}
	
	
	//	定义一个上传TAC的接口
	
	
	/**
	 * 查询订单状态
	 * @param city 下单城市
	 * @return
	 */
	public static Map<String,Object> getOrderStatus(String city){
		String reqXml = new XmlMgr_BusTradeBase(city) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				objMgr.addText("HIHIA", "I DON'T KNOW");
			}
			@Override
			protected String getMsgId() {
				return XML_MSGID_YZF_QUERY;
			}
		}.get();
		SumaPostHandler postHandler = new SumaPostHandler();
		Map<String, Object> resMap = postHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, reqXml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
		if(resMap == null)
			return null;
		else{
			String data = (String) resMap.get(MAP_HTTP_RESPONSE_RESPONSE);
			if(data!=null)
				return XmlMgr_BusTradeBase.parse(data);
		}
		return null;
	}
	
	
	/**
	 * 退款
	 * @param city  退款城市
	 * @param ORDERSEQ  退款订单号
	 * @return
	 */
	public static String  refoundOrder(String city,final String ORDERSEQ,final String orderNo){
		String reqXml = new XmlMgr_BusTradeBase(city) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				objMgr.addText("ORDERSEQ",ORDERSEQ);
				objMgr.addText("MORDERID", orderNo);
			}
			@Override
			protected String getMsgId() {
				return XML_MSGID_YZF_REFOUND;
			}
		}.get();
		SumaPostHandler postHandler = new SumaPostHandler();
		Map<String, Object> resMap = postHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, reqXml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
		if(resMap == null)
			return postHandler.getFailRespCode();
		else{
			resMap = XmlMgr_BusTradeBase.parse((String) resMap
					.get(MAP_HTTP_RESPONSE_RESPONSE));
			return resMap!=null?(String) resMap.get("RespCode"):null;
		}
	}
	
	
	
	
	
	// =========================================补贴钱包============================================
	/**
	 * 补贴钱包的圈存申请
	 * @param cardType: 卡种标识 01 CPU 02 M1
	 * @param applicationSN: 卡应用序列号
	 * @param balance:　交易前余额
	 * @param tradeMoney：交易金额
	 * @param consumeCount:　补贴钱包消费基数
	 * @param loadCount：补贴钱包充值计数
	 * @param tradeDate:　交易日期
	 * @param seudoRandomNumber：　伪随机数
	 * @param mac1：mac1
	 * @param tradeNum: ""
	 * @param phoneNum: 手机号
	 * @param msgId: 请求的msgId  XML_MSGID_YJT_ED_LOAD: 补贴钱包圈存申请;     XML_MSGID_YJT_ED_0LOAD: 补贴钱包的冲零请求
	 * @return
	 */
	public static Map<String,Object> subsidyApply(final String cardType, final String applicationSN, final String balance, 
			final String tradeMoney, final String consumeCount, 
			final String loadCount, final String tradeDate, final String seudoRandomNumber, 
			final String mac1, final String tradeNum, final String phoneNum, final String msgId){
		String reqxml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				objMgr.addText(Plugin.PRODUCTAMOUNT, ""+tradeMoney);// 100(以分为单位)
				objMgr.addText("CLIENTIP ", "192..168.1.123"); // IP地址
				// 圈存请求  客户端不再传递
//				objMgr.addText("DEAL_TYPE", DEALTYPE_1);
				//	申请类型
				objMgr.addText("APPLY_TYPE", APPLYTYPE_2);
				//	卡种标识
				objMgr.addText("CARD_TYPE", cardType);
				//	卡应用序列号
				objMgr.addText("CARD_SERIAL", applicationSN);
				//	钱包类型
				objMgr.addText("WALLET_TYPE", PURSETYPE_02);
				//	交易前金额
				objMgr.addText("TRANS_BEGIN_AMOUNT", balance);
				//	交易金额
				objMgr.addText("TRANS_AMOUNT", tradeMoney);
				//	电子钱包消费计数
				objMgr.addText("CONSUME_TIMES", consumeCount);
				//	电子钱包充值计数
				objMgr.addText("RECHARGE_TIMES", loadCount);
				//	交易日期
				objMgr.addText("TRANS_DATE", tradeDate);
				//	伪随机数
				objMgr.addText("PRN", seudoRandomNumber);
				//	MAC1
				objMgr.addText("MAC1", mac1);
				//	交易流水号
				objMgr.addText("TradeNum", tradeNum);
				//	电话号码
				objMgr.addText("YJTPhone", phoneNum);
				// 补贴的版本号
//				objMgr.addText("SUBSIDES_VERSION_SN", subsideVersionSN);
			}
			@Override
			protected String getMsgId() {
//				return XML_MSGID_YJT_ED_LOAD;
				return msgId;
			}
		}.get();
		// 发送请求订单接口
		SumaPostHandler postHandler = new SumaPostHandler();
		Map<String, Object> resMap = postHandler
				.httpPostAndGetResponse(
						IpCons.SERVER_ADDRESS_IP_BUS,
						reqxml,
						ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
		if (resMap != null) {
			String temp = (String) resMap
					.get(MAP_HTTP_RESPONSE_RESPONSE);
			if (temp != null)
				return XmlMgr_BusTradeBase.parse(temp);
		}
		return null;
	}
	
	/**
	 * 翼机通卡补助结果确认接口
	 * @param applicationSN	卡应用序列号
	 * @param consumeCount	电子钱包消费序号
	 * @param loadCount	电子钱包充值序号
	 * @param TAC	TAC校验码
	 * @param tranResultFlag	交易结果标志(0x01 表示异常交易记录, 0x99表示正常记录)
	 * @param tradeNum	交易流水号
	 * @param phoneNum	翼机通手机号
	 * @param transBeginAmount	交易前余额
	 * @return
	 */
	public static Map<String, Object> subsidyResultConfirm(final String applicationSN, final String consumeCount, final String loadCount, 
			final String TAC, final String tranResultFlag, final String tradeNum, 
			final String phoneNum, final String transBeginAmount) {
		//	城市直接写"999888"
		String reqXml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				//	手机的ICCID
				objMgr.addText("DEAL_TYPE", DEALTYPE_2);
				objMgr.addText("CARD_SERIAL", applicationSN);
				objMgr.addText("CONSUME_TIMES", consumeCount);
				objMgr.addText("RECHARGE_TIMES", loadCount);
				//	服务器端写死  "0"
//				objMgr.addText("TERMINAL_NO", terminalNo);
				objMgr.addText("TAC", TAC);
				objMgr.addText("TRAN_RESULT_FLAG", tranResultFlag);
				objMgr.addText("TradeNum", tradeNum);
				objMgr.addText("YJTPhone", phoneNum);
				objMgr.addText("TRANS_BEGIN_AMOUNT", transBeginAmount);
			}
			@Override
			protected String getMsgId() {
				return XML_MSGID_YJT_ED_LOADCONFIRM;
			}
		}.get();
		//	数据首发模块
		SumaPostHandler postHandler = new SumaPostHandler();
		//	TODO 这里可以new 一个RetryTask类 用于如果服务器没有收到上传的TAC那么就执行 重试3次, 如果失败则存储到数据库中, 等待重试
		
		
		Map<String, Object> resMap = postHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, reqXml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
		if(resMap == null) {
			return null;
		} else {
			String data = (String) resMap.get(MAP_HTTP_RESPONSE_RESPONSE);
			if(data!=null)
				return XmlMgr_BusTradeBase.parse(data);
		}
		return null;
	}
	
	
	/**
	 * 补助冲零请求
	 * @param cardType: 卡种标识 01 CPU 02 M1
	 * @param applicationSN: 卡应用序列号
	 * @param purseType: 钱包类型(01主钱包 02 补贴钱包)
	 * @param balance: 交易前余额
	 * @param tradeMoney: 交易金额
	 * @param consumeCount: 补贴钱包消费计数
	 * @param loadCount: 补贴钱包充值计数
	 * @param tradeDate: 交易日期YYYYMMDDHHMMSS
	 * @param seudoRandomNumber: 伪随机数
	 * @param terminalNo:终端交易序号
	 * @return
	 */
	/*public static Map<String,Object> subsidy0Apply(final String cardType, final String applicationSN,final String purseType, 
			final String balance, final String tradeMoney, final String consumeCount, 
			final String loadCount, final String tradeDate, final String seudoRandomNumber,  final String terminalNo){
		String reqxml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				objMgr.addText(Plugin.PRODUCTAMOUNT, ""+tradeMoney);// 100(以分为单位)
				objMgr.addText("CLIENTIP ", "192..168.1.123"); // IP地址
				// 圈存请求
//				objMgr.addText("DEAL_TYPE", DEALTYPE_15);
				//	申请类型
				objMgr.addText("APPLY_TYPE", "");
				//	卡种标识
				objMgr.addText("CARD_TYPE", cardType);
				//	卡应用序列号
				objMgr.addText("CARD_SERIAL", applicationSN);
				//	钱包类型
				objMgr.addText("WALLET_TYPE", purseType);
				//	交易前金额
				objMgr.addText("TRANS_BEGIN_AMOUNT", balance);
				//	交易金额
				objMgr.addText("TRANS_AMOUNT", tradeMoney);
				//	电子钱包消费计数
				objMgr.addText("CONSUME_TIMES", consumeCount);
				//	电子钱包充值计数
				objMgr.addText("RECHARGE_TIMES", loadCount);
				//	交易日期
				objMgr.addText("TRANS_DATE", tradeDate);
				//	伪随机数
				objMgr.addText("PRN", seudoRandomNumber);
				// 终端交易序号
				objMgr.addText("TERMINAL_NO", terminalNo);
			}
			@Override
			protected String getMsgId() {
				return XML_MSGID_YJT_ED_0LOAD;
			}
		}.get();
		// 发送请求订单接口
		SumaPostHandler postHandler = new SumaPostHandler();
		Map<String, Object> resMap = postHandler
				.httpPostAndGetResponse(
						IpCons.SERVER_ADDRESS_IP_BUS,
						reqxml,
						ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
		if (resMap != null) {
			String temp = (String) resMap
					.get(MAP_HTTP_RESPONSE_RESPONSE);
			if (temp != null)
				return XmlMgr_BusTradeBase.parse(temp);
		}
		return null;
	}*/
	
	
	/**
	 * 翼机通补助冲零结果确认接口
	 * @param applicationSN	卡应用序列号
	 * @param consumeCount	电子钱包消费序号
	 * @param loadCount	电子钱包充值序号
	 * @param TAC	TAC校验码
	 * @param tranResultFlag	交易结果标志(0x01 表示异常交易记录, 0x99表示正常记录)
	 * @param tradeNum	交易流水号
	 * @param phoneNum	翼机通手机号
	 * @param transBeginAmount	交易前余额
	 * @param mac2 mac2
	 * @param terminalNo: 终端交易序号(消费时psam卡返回交易序号，无psam卡可写固定值)
	 * @return
	 */
	/*public static Map<String, Object> subsidy0ResultConfirm(final String applicationSN, final String consumeCount, final String loadCount, 
			final String TAC, final String tranResultFlag, final String tradeNum,
			final String phoneNum, final String transBeginAmount, final String mac2, final String terminalNo) {
		//	城市直接写"999888"
		String reqXml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				//	手机的ICCID
				objMgr.addText("DEAL_TYPE", DEALTYPE_16);
				objMgr.addText("CARD_SERIAL", applicationSN);
				objMgr.addText("CONSUME_TIMES", consumeCount);
				objMgr.addText("RECHARGE_TIMES", loadCount);
				//	服务器端写死  "0"
//				objMgr.addText("TERMINAL_NO", terminalNo);
				objMgr.addText("TAC", TAC);
				objMgr.addText("TRAN_RESULT_FLAG", tranResultFlag);
				objMgr.addText("TradeNum", tradeNum);
				objMgr.addText("YJTPhone", phoneNum);
				objMgr.addText("MAC2", mac2);
				objMgr.addText("TERMINAL_NO", terminalNo);
				objMgr.addText("TRANS_BEGIN_AMOUNT", transBeginAmount);
			}
			@Override
			protected String getMsgId() {
				return XML_MSGID_YJT_ED_0LOADCONFIRM;
			}
		}.get();
		//	数据首发模块
		SumaPostHandler postHandler = new SumaPostHandler();
		//	TODO 这里可以new 一个RetryTask类 用于如果服务器没有收到上传的TAC那么就执行 重试3次, 如果失败则存储到数据库中, 等待重试
		
		
		Map<String, Object> resMap = postHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, reqXml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
		if(resMap == null) {
			return null;
		} else {
			String data = (String) resMap.get(MAP_HTTP_RESPONSE_RESPONSE);
			if(data!=null)
				return XmlMgr_BusTradeBase.parse(data);
		}
		return null;
	}*/
	
	
	
	// =====================================个人化=========================================
	/**
	 * 翼机通用户认证报文
	 * @param misidn: 手机号
	 * @param iccid: 卡片序列号
	 * @param seId: 
	 * @param serviceType: 服务类型(1：应用下载 , 2：应用删除 , 3：应用锁定 , 4：应用解锁)
	 * @return
	 */
	public static Map<String, Object> yjtUserIdentification(final String msisdn, final String iccid, final String seId, final String serviceType) {
//		城市直接写"999888"
		String reqXml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				objMgr.addText("MSISDN", msisdn);
				objMgr.addText("ICCID", iccid);
				objMgr.addText("SEID", seId);
				//	服务器端写死  "0"
//					objMgr.addText("TERMINAL_NO", terminalNo);
				objMgr.addText("ServiceType", serviceType);
			}
			@Override
			protected String getMsgId() {
				return XML_MSGID_YJT_PERSONAL_USERAUC;
			}
		}.get();
		//	数据首发模块
		SumaPostHandler postHandler = new SumaPostHandler();
		
		Map<String, Object> resMap = postHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, reqXml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
		if(resMap == null) {
			return null;
		} else {
			String data = (String) resMap.get(MAP_HTTP_RESPONSE_RESPONSE);
			if(data!=null)
				return XmlMgr_BusTradeBase.parse(data);
		}
		return null;
	}
	
	
	/**
	 * 卡片状态同步
	 * @param msisdn: 手机号
	 * @param iccid: 卡片序列号
	 * @param lcs: (0: 删除, 3: 安装, 7: SELECTABLE, 15: 个人化, 131: LOCKED)
	 * @return
	 */
	public static Map<String, Object> yjtCardStatusSyn(final String msisdn, final String iccid, final String lcs) {
			String reqXml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
				@Override
				protected void initContent(XmlHandler objMgr) {
					//	手机的ICCID
					objMgr.addText("MSISDN", msisdn);
					objMgr.addText("ICCID", iccid);
					//	服务器端写死  "0"
//					objMgr.addText("TERMINAL_NO", terminalNo);
					objMgr.addText("lCS", lcs);
				}
				@Override
				protected String getMsgId() {
					return XML_MSGID_YJT_PERSONAL_CARDSTATUSSYN;
				}
			}.get();
			//	数据首发模块
			SumaPostHandler postHandler = new SumaPostHandler();
			
			Map<String, Object> resMap = postHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, reqXml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
			if(resMap == null) {
				return null;
			} else {
				String data = (String) resMap.get(MAP_HTTP_RESPONSE_RESPONSE);
				if(data!=null)
					return XmlMgr_BusTradeBase.parse(data);
			}
			return null;
	}
	
	/**
	 * 个人化
	 * @param msisdn: 手机号
	 * @param iccid: 卡片序列号
	 * @return
	 */
	public static Map<String, Object> yjtPOSPersonReq(final String msisdn, final String iccid) {
			String reqXml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
				@Override
				protected void initContent(XmlHandler objMgr) {
					objMgr.addText("MSISDN", msisdn);
					objMgr.addText("ICCID", iccid);
					//	服务器端写死  "0"
//					objMgr.addText("TERMINAL_NO", terminalNo);
				}
				@Override
				protected String getMsgId() {
					return XML_MSGID_YJT_PERSONAL_PERSONREQ;
				}
			}.get();
			//	数据首发模块
			SumaPostHandler postHandler = new SumaPostHandler();
			
			Map<String, Object> resMap = postHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, reqXml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
			if(resMap == null) {
				return null;
			} else {
				String data = (String) resMap.get(MAP_HTTP_RESPONSE_RESPONSE);
				if(data!=null)
					return XmlMgr_BusTradeBase.parse(data);
			}
			return null;
	}
	
	
	/**
	 * 卡片响应结果通知
	 * @param msisdn: 手机号
	 * @param iccid: 卡片序列号
	 * @param sequenceId: 个人化过程中的上一条消息流水号
	 * @param apduIndex: 执行的最后一条apdu索引号, 当result为0x01时无效
	 * @param apdu: 执行的最后一条apdu执行的响应数据, 当result为0x01时无效
	 * @param result: 0标识执行成功, 1:系统执行失败
	 * @return
	 */
	public static Map<String, Object> yjtAPDUResultReq(final String msisdn, final String iccid, final String sequenceId,
			final String apduIndex, final String rApdu, final String result) {
			String reqXml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
				@Override
				protected void initContent(XmlHandler objMgr) {
					//	手机的ICCID
					objMgr.addText("MSISDN", msisdn);
					objMgr.addText("ICCID", iccid);
					objMgr.addText("Sequence_Id", sequenceId);
					objMgr.addText("ApduIndex", apduIndex);
					objMgr.addText("RAPDU", rApdu);
					//	服务器端写死  "0"
//					objMgr.addText("TERMINAL_NO", terminalNo);
					objMgr.addText("RESULT", result);
				}
				@Override
				protected String getMsgId() {
					return XML_MSGID_YJT_PERSONAL_APDURESULTREQ;
				}
			}.get();
			//	数据首发模块
			SumaPostHandler postHandler = new SumaPostHandler();
			
			Map<String, Object> resMap = postHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, reqXml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
			if(resMap == null) {
				return null;
			} else {
				String data = (String) resMap.get(MAP_HTTP_RESPONSE_RESPONSE);
				if(data!=null)
					return XmlMgr_BusTradeBase.parse(data);
			}
			return null;
	}
	
	
	/**
	 * 去个人化
	 * @param msisdn: 手机号
	 * @param iccid: 卡片序列号
	 * @param aId: 应用标识
	 * @param appVersion: 应用版本号
	 * @return
	 */
	public static Map<String, Object> yjtDelPersoReq(final String msisdn, final String iccid) {
			String reqXml = new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
				@Override
				protected void initContent(XmlHandler objMgr) {
					objMgr.addText("MSISDN", msisdn);
					objMgr.addText("ICCID", iccid);
					//	服务器端写死  "0"
//					objMgr.addText("TERMINAL_NO", terminalNo);
				}
				@Override
				protected String getMsgId() {
					return XML_MSGID_YJT_PERSONAL_DELPERSONREQ;
				}
			}.get();
			//	数据首发模块
			SumaPostHandler postHandler = new SumaPostHandler();
			
			Map<String, Object> resMap = postHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, reqXml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
			if(resMap == null) {
				return null;
			} else {
				String data = (String) resMap.get(MAP_HTTP_RESPONSE_RESPONSE);
				if(data!=null)
					return XmlMgr_BusTradeBase.parse(data);
			}
			return null;
	}
}
