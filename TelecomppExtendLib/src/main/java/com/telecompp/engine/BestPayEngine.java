package com.telecompp.engine;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import android.app.Activity;

import com.bestpay.plugin.Plugin;
import com.telecompp.util.LoggerHelper;
import com.telecompp.util.SumaConstants;
import com.telecompp.util.SumaPostHandler;
import com.telecompp.util.TerminalDataManager;
import com.telecompp.xml.XmlHandler;
import com.telecompp.xml.XmlMgr_BusTradeBase;

/**
 * 提供翼支付下单、退款等功能
 * @author poet
 *
 */
public class BestPayEngine implements SumaConstants{

		private BestPayEngine(){};
		
		private static LoggerHelper logger = new LoggerHelper(BestPayEngine.class);
		
		/**
		 * 下单接口
		 * @param city  下单城市
		 * @param CSN  CSN
		 * @param load_money  充值金额
		 * @param YJTMerchantId  翼机通商户id
		 * @param tradeType  "1" 客票充值
		 * @return
		 */
		public static Map<String,Object> getOrder(String city,final String CSN, final int load_money,final String YJTMerchantId, final String tradeType,
				final String phoneNum){
			String reqxml = new XmlMgr_BusTradeBase(city) {
				@Override
				protected void initContent(XmlHandler objMgr) {
					objMgr.addText(Plugin.PRODUCTAMOUNT, ""+load_money);// 100(以分为单位)
					objMgr.addText("CLIENTIP ", "192..168.1.123"); // IP地址
					objMgr.addText("TradeType", tradeType);
					objMgr.addText("YJTMerchantId", YJTMerchantId);
					objMgr.addText("CSN", CSN);
					objMgr.addText("YJTPhone", phoneNum);
				}
				@Override
				protected String getMsgId() {
					return XML_MSGID_YZF_ORDER;
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
		 * 查询订单状态
		 * @param city 下单城市
		 * @return
		 */
		public static Map<String,Object> getOrderStatus(String city, final String yjtMerchantId){
			String reqXml = new XmlMgr_BusTradeBase(city) {
				@Override
				protected void initContent(XmlHandler objMgr) {
					objMgr.addText("HIHIA", "I DON'T KNOW");
					objMgr.addText("YJTMerchantId", yjtMerchantId);
					objMgr.addText("ORDERSEQ", TerminalDataManager.getPlug_orderseq());
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
		 * 调用翼支付插件进行支付
		 * @param context
		 * @param args
		 */
		public static void pay(Activity activity, Map<String, Object> args) {
			Hashtable<String, String> paramsHashtable = new Hashtable<String, String>();
			
			for(Entry<String, Object> entry : args.entrySet()){
				if(entry.getKey().startsWith("YZF")) {
					String key = entry.getKey().substring(3);
					logger.printLogOnSDCard(key + "====" + (String) entry.getValue());
					paramsHashtable.put(key, (String) entry.getValue());
				}
			}  
			Plugin.pay(activity, paramsHashtable);
			
			/*// 增加子商户id
			String all_merchantId = (String) args.get(Plugin.MERCHANTID);
			String[] merchantIds = null;
			merchantIds = all_merchantId.split("@", -1);
			
//			paramsHashtable.put(Plugin.MERCHANTID,
//					(String) args.get(Plugin.MERCHANTID));
			paramsHashtable.put(Plugin.MERCHANTID,
					merchantIds[0]);
			
			if(merchantIds.length > 1) {				
				paramsHashtable.put(Plugin.SUBMERCHANTID, merchantIds[1]);// 子商户ID
				paramsHashtable.put(Plugin.DIVDETAILS, (String) args.get(Plugin.DIVDETAILS));
			} else {				
				paramsHashtable.put(Plugin.SUBMERCHANTID, "");// 子商户ID
				paramsHashtable.put(Plugin.DIVDETAILS, "");
			}
			paramsHashtable
					.put(Plugin.ORDERSEQ, (String) args.get(Plugin.ORDERSEQ));
			paramsHashtable.put(Plugin.ORDERAMOUNT, ConvertUtil.cen2Money(Integer
					.parseInt((String) args.get(Plugin.ORDERAMOUNT))));
			paramsHashtable.put(Plugin.ORDERTIME, (String) args.get("ORDERDATE"));
			paramsHashtable.put(Plugin.ORDERVALIDITYTIME,
					(String) args.get(Plugin.ORDERVALIDITYTIME));
			paramsHashtable.put(Plugin.PRODUCTDESC, "公交充值");
			paramsHashtable.put(Plugin.PRODUCTAMOUNT, ConvertUtil.cen2Money(Integer
					.parseInt((String) args.get(Plugin.ORDERAMOUNT))));// 产品金额
			paramsHashtable.put(Plugin.ATTACHAMOUNT, ConvertUtil.cen2Money(Integer
					.parseInt((String) args.get(Plugin.ATTACHAMOUNT))));// 附加金额
			paramsHashtable.put(Plugin.CURTYPE, "RMB");
			paramsHashtable.put(Plugin.BACKMERCHANTURL,
					(String) args.get(Plugin.BACKMERCHANTURL));// /Users/songtao/Downloads/apache-tomcat-7.0.56/wtpwebapps/CTCCMobilePosTd/bestpay/back.do
			paramsHashtable.put(Plugin.ATTACH, "公交充值");// NULL
			paramsHashtable.put(Plugin.PRODUCTID,
					(String) args.get(Plugin.PRODUCTID));// 01
			paramsHashtable.put(Plugin.USERIP, "192.168.1.123");
			paramsHashtable.put(Plugin.KEY, (String) args.get("MERCHANTKEY"));// 默认为0
			paramsHashtable.put(Plugin.CUSTOMERID, TerminalDataManager.getICCID());// 支付账户在商户中的登录名称
			paramsHashtable.put(Plugin.MERCHANTPWD,
					(String) args.get(Plugin.MERCHANTPWD));
			paramsHashtable.put(Plugin.ORDERREQTRANSEQ,
					(String) args.get(Plugin.ORDERREQTRANSEQ));
			paramsHashtable.put(Plugin.ACCOUNTID, "");
			Plugin.pay(activity, paramsHashtable);*/
		}
		/**
		 * 退款
		 * @param city  退款城市
		 * @param ORDERSEQ  退款订单号
		 * @param yjtMerchantId  翼机通商户Id
		 * @return
		 */
		public static String  refoundOrder(String city,final String ORDERSEQ,final String yjtMerchantId){
			String reqXml = new XmlMgr_BusTradeBase(city) {
				@Override
				protected void initContent(XmlHandler objMgr) {
					objMgr.addText("ORDERSEQ",ORDERSEQ);
					objMgr.addText("YJTMerchantId", yjtMerchantId);
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
}
