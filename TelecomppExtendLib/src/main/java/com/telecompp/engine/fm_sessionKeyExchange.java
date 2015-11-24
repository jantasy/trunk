package com.telecompp.engine;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.telecompp.activity.ConfirmActivity;
import com.telecompp.util.AlgoManager;
import com.telecompp.util.ConversionTools;
import com.telecompp.util.LoggerHelper;
import com.telecompp.util.RandomMaker;
import com.telecompp.util.SumaConstants;
import com.telecompp.util.SumaPostHandler;
import com.telecompp.util.TerminalDataManager;
import com.telecompp.xml.XmlHandler;
import com.telecompp.xml.XmlMgr_BusTradeBase;
import com.telecompp.xml.XmlMgr_CardSessionExchange;
import com.telecompp.xml.XmlMgr_SessionKeyExchange;
import com.transport.ConvertUtil;
import com.transport.cypher.KEYSUtilProxy;
import com.transport.db.dao.PConfirmDao;

/**
 * 会话密钥功能 
 * 
 * @author xiongdazhuang
 * 
 */
public class fm_sessionKeyExchange implements SumaConstants {
    SumaPostHandler HttpPostHandler = new SumaPostHandler();
    private static String hexString = "0123456789ABCDEF";
    
    private static LoggerHelper logger = new LoggerHelper(ConfirmActivity.class);
    
    /**
     * 终端会话密钥更新
     * 
     * @return 成功："00" 失败：null
     */
    public String terminal_SessionKey_Start() {
        Map<String, Object> responseDataMap = new HashMap<String, Object>();
        Map<String, Object> parsedData = new HashMap<String, Object>();

        // 生成16字节随机数
        byte[] randomData = RandomMaker.getRandomByte(16);
        if (randomData == null) {
            return null;
        }
        // 用预置的公钥加密
        String sessionKeyEncString = AlgoManager.RSACrypto(
                SumaConstants.TERMINAL_PUBKEY, randomData, true);

        XmlMgr_SessionKeyExchange sessionKeyExchangeHandler = new XmlMgr_SessionKeyExchange();
        String xmlDataString = sessionKeyExchangeHandler
                .get(sessionKeyEncString);
        TerminalDataManager.setCookieId("");

        logger.printLogOnSDCard("终端会话密钥交换发送报文开始");
        
        // 发送给前置平台
        responseDataMap = HttpPostHandler.httpPostAndGetResponse(
                IpCons.SERVER_ADDRESS_IP, xmlDataString,
                ENCRYPT_LEVEL_PLAINTEXT_DATA);
        
        logger.printLogOnSDCard("终端会话密钥交换发送报文结束");
        
        if (responseDataMap == null) {
        	logger.printLogOnSDCard("终端会话密钥交换发送报文结束--连接服务器异常");
            TerminalDataManager.printLog(DEBUG_LOGCAT_FILTER, "连接服务器异常");
            return null;
        } else {
            TerminalDataManager.printLog(DEBUG_LOGCAT_FILTER, "response:"
                    + (String) responseDataMap.get(MAP_HTTP_RESPONSE_RESPONSE));
            TerminalDataManager.printLog(DEBUG_LOGCAT_FILTER, "code:"
                    + (String) responseDataMap.get(MAP_HTTP_RESPONSE_CODE));
            TerminalDataManager.printLog(
                    DEBUG_LOGCAT_FILTER,
                    MAP_HTTP_RESPONSE_COOKIE_ID
                            + (String) responseDataMap
                                    .get(MAP_HTTP_RESPONSE_COOKIE_ID));

            String cookieIDString = (String) responseDataMap
                    .get(MAP_HTTP_RESPONSE_COOKIE_ID);
            if (cookieIDString == null) {
                return null;
            }

            TerminalDataManager.setCookieId(cookieIDString);
        }
        parsedData = sessionKeyExchangeHandler.parse((String) responseDataMap
                .get(MAP_HTTP_RESPONSE_RESPONSE));
        if (parsedData == null) {
            return null;
        }

        String respCodeString = (String) parsedData.get("RespCode");

        if (respCodeString == null) {
            return null;
        }
        if (respCodeString.equals("0000") == false) {
            return null;
        }

        String SessionkeyDataString = ConversionTools.ByteArrayToString(
                randomData, randomData.length);
        // 保存SessionKey

        TerminalDataManager.setTerminalSessionKeyData(SessionkeyDataString);

        return "00";
    }

    /**
     * 卡片会话密钥交换
     * 
     * @return 成功："00" 失败：null
     */
    public String card_SessionKey_Start() {
        Map<String, Object> responseDataMap = new HashMap<String, Object>();
        Map<String, Object> parsedData = new HashMap<String, Object>();

        	//上传公钥
        	commitKeys(KEYSUtilProxy.decriptCardKey("11"));
        // 卡片会话密钥交换
        XmlMgr_CardSessionExchange cardSessionExchangeHandler = new XmlMgr_CardSessionExchange();
        //	将provinceCode和 custId传递给服务器端
        String xmlDataString = cardSessionExchangeHandler.get(TerminalDataManager.getPlug_yjtMerchantId());
        // 发送给前置平台
        responseDataMap = HttpPostHandler.httpPostAndGetResponse(
                IpCons.SERVER_ADDRESS_IP, xmlDataString,
                ENCRYPT_LEVEL_TERMINAL_SESSIONKEY_ENC_DATA);
        if (responseDataMap == null) {
            TerminalDataManager.printLog(DEBUG_LOGCAT_FILTER, "连接服务器异常");
            return null;
        } else {
            TerminalDataManager.printLog(DEBUG_LOGCAT_FILTER, "response:"
                    + (String) responseDataMap.get(MAP_HTTP_RESPONSE_RESPONSE));
            TerminalDataManager.printLog(DEBUG_LOGCAT_FILTER, "code:"
                    + (String) responseDataMap.get(MAP_HTTP_RESPONSE_CODE));
            TerminalDataManager.printLog(
                    DEBUG_LOGCAT_FILTER,
                    MAP_HTTP_RESPONSE_COOKIE_ID
                            + (String) responseDataMap
                                    .get(MAP_HTTP_RESPONSE_COOKIE_ID));
        }

        parsedData = cardSessionExchangeHandler.parse((String) responseDataMap
                .get(MAP_HTTP_RESPONSE_RESPONSE));
        if (parsedData == null) {
            return null;
        }

        String respCodeString = (String) parsedData.get("RespCode");
        String cipherSKString = (String) parsedData.get("CipherSK");
        String cipherSKSignString = (String) parsedData.get("CipherSKSign");

        if ((respCodeString == null) || (cipherSKString == null)
                || (cipherSKSignString == null)) {
            return null;
        }
        if (respCodeString.equals("0000") == false) {
            return null;
        }
        String resultDataString = null;
        // TODO 如果是只支持公交的话需要做特殊的解密
//        	if(!KEYSUtilProxy.rsa_pk_verify(cipherSKString , cipherSKSignString)){
//        		return null;
//        	}
        	//使用私钥解密
        	resultDataString = KEYSUtilProxy.decriptCardKey(cipherSKString);
        TerminalDataManager.setCardSessionKeyData(resultDataString);
        return "00";
    }

    /**
     * 使用会话密钥加密
     * 
     * @param indata
     *            需要加密的数据
     * @param encryptLevel
     *            方式
     * @return 成功：非空数据 失败：null
     */
    public String postDataEncWithSessionKey(String indata, int encryptLevel) {
        String transCodeDataString = null;
        String outdataString = null;

        transCodeDataString = string2GBK(indata);
        if (transCodeDataString == null) {
            return null;
        }

        if (encryptLevel == ENCRYPT_LEVEL_PLAINTEXT_DATA) {
            // 明文数据
            return indata;
        } else if (encryptLevel == ENCRYPT_LEVEL_TERMINAL_SESSIONKEY_ENC_DATA) {
            // 使用终端会话密钥加密，不对body加密
            String terminalSessionKey = TerminalDataManager
                    .getTerminalSessionKeyData();
            if (terminalSessionKey == null) {
                return null;
            }
            outdataString = AlgoManager.TDesCryption(terminalSessionKey,
                    transCodeDataString, true, false);
        } else if (encryptLevel == ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA) {
            // 对报文体使用卡片会话密钥加密，对整个报文使用终端会话密钥加密
            String tmpdataString = innerEncBody(indata);
            String terminalSessionKey = TerminalDataManager
                    .getTerminalSessionKeyData();
            if (terminalSessionKey == null) {
                return null;
            }

            tmpdataString = string2GBK(tmpdataString);// 转换
            outdataString = AlgoManager.TDesCryption(terminalSessionKey,
                    tmpdataString, true, false);
        } else {
            return null;// 参数错误
        }

        return outdataString;
    }

    /**
     * 使用会话密钥解密
     * 
     * @param indata
     *            需要解密的数据
     * @param encryptLevel
     *            方式
     * @return 成功：非空数据 失败：null
     */
    public String postDataDecWithSessionKey(String indata, int encryptLevel) {
        String finalDataString = null;

        if (encryptLevel == ENCRYPT_LEVEL_PLAINTEXT_DATA) {
            return indata;
        } else if (encryptLevel == ENCRYPT_LEVEL_TERMINAL_SESSIONKEY_ENC_DATA) {
            // 使用终端会话密钥对报文进行解密
            String terminalSessionKey = TerminalDataManager
                    .getTerminalSessionKeyData();
            if (terminalSessionKey == null) {
                TerminalDataManager.printLog(DEBUG_LOGCAT_FILTER, "Z1");
                return null;
            }
            String encIndataString = AlgoManager.TDesCryption(
                    terminalSessionKey, indata, false, false);
            // GBK 2 String
            encIndataString = GBK2String(encIndataString);
            if (encIndataString == null) {
                TerminalDataManager.printLog(DEBUG_LOGCAT_FILTER, "Z2");
                return null;
            }
            finalDataString = encIndataString;
            return finalDataString;
        } else if (encryptLevel == ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA) {
            // 使用终端会话密钥对报文进行解密
            String terminalSessionKey = TerminalDataManager
                    .getTerminalSessionKeyData();
            if (terminalSessionKey == null) {
                TerminalDataManager.printLog(DEBUG_LOGCAT_FILTER, "Z3");
                return null;
            }
            String encIndataString = AlgoManager.TDesCryption(
                    terminalSessionKey, indata, false, false);
            // GBK 2 String
            encIndataString = GBK2String(encIndataString);
            if (encIndataString == null) {
                TerminalDataManager.printLog(DEBUG_LOGCAT_FILTER, "Z4");
                return null;
            }
            // 对报文体进行解密
            finalDataString = innerDecBody(encIndataString);
        } else {
            TerminalDataManager.printLog(DEBUG_LOGCAT_FILTER, "Z5");
            return null;
        }

        return finalDataString;
    }

    /**
     * GBK 2 String
     * 
     * @param indata
     * @return
     */
    private String GBK2String(String indata) {
        if (indata == null) {
            return null;
        }

        String finalDataString = null;

        byte[] indataByte = ConversionTools.stringToByteArr(indata);
        try {
            finalDataString = new String(indataByte, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        return finalDataString;
    }

    /**
     * String 2 GBK
     * 
     * @param indata
     * @return
     */
    private static String string2GBK(String indata) {
        if (indata == null) {
            return null;
        }

        byte[] GBKDataBytes;

        try {
            GBKDataBytes = indata.getBytes("GBK");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        String GBKDataString = ConversionTools.ByteArrayToString(GBKDataBytes,
                GBKDataBytes.length);
        return GBKDataString;
    }

    /**
     * 使用卡片会话密钥对报文体进行加密
     * 
     * @param xml
     * @param encryptLevel
     * @return
     */
    private String innerEncBody(String xml) {
        String R1 = null;
        String R2 = null;
        String R3 = null;

        int index1 = xml.indexOf("<Content>") + ("<Content>").length();
        int index2 = xml.indexOf("</Content>");

        if (index1 == -1) {
            return xml;
        }
        if (index2 == -1) {
            return xml;
        }

        R1 = xml.substring(0, index1);
        R2 = xml.substring(index1, index2);
        R3 = xml.substring(index2);

        R2 = string2GBK(R2);

        String cardSessionKeyData = TerminalDataManager.getCardSessionKeyData();
        if (cardSessionKeyData == null) {
            return xml;
        }
        // 对R2加密
        R2 = AlgoManager.TDesCryption(cardSessionKeyData, R2, true, false);

        String finalDataString = R1 + R2 + R3;

        return finalDataString;
    }

    /**
     * 使用卡片会话密钥对报文体进行解密
     * 
     * @param xml
     * @return
     */
    private String innerDecBody(String xml) {
        String R1 = null;
        String R2 = null;
        String R3 = null;

        int index1 = xml.indexOf("<Content>") + ("<Content>").length();
        int index2 = xml.indexOf("</Content>");

        if (index1 == -1) {
            return xml;
        }
        if (index2 == -1) {
            return xml;
        }

        R1 = xml.substring(0, index1);
        R2 = xml.substring(index1, index2);
        R3 = xml.substring(index2);

        String cardSessionKeyData = TerminalDataManager.getCardSessionKeyData();
        if (cardSessionKeyData == null) {
            return xml;
        }
        // 对R2解密
        R2 = AlgoManager.TDesCryption(cardSessionKeyData, R2, false, false);
        R2 = GBK2String(R2);

        String finalDataString = R1 + R2 + R3;

        return finalDataString;
    }
	 public static int commitKeys(final String keys){
		 	SumaPostHandler poster = new SumaPostHandler();
		 	String xml = new XmlMgr_BusTradeBase("110") {
				@Override 
				protected void initContent(XmlHandler objMgr) {
					logger.printLogOnSDCard("卡片会话密钥交换==key==" + keys);
					
					//将密钥上传
					int len = keys.length()/4;
					String p1 = keys.substring(0, len);
					String p2 = keys.substring(len,2* len);
					String p3 = keys.substring(2*len,3*len);
					String p4 = keys.substring(3*len);
					//Modulus|Exponent|P|Q|DP|DQ|InverseQ
					objMgr.addText("CardPubKey1", AlgoManager.RSACrypto(
			                SumaConstants.TERMINAL_PUBKEY, ConvertUtil.decode(p1), true));
					objMgr.addText("CardPubKey2", AlgoManager.RSACrypto(
			                SumaConstants.TERMINAL_PUBKEY,  ConvertUtil.decode(p2), true));
					objMgr.addText("CardPubKey3", AlgoManager.RSACrypto(
			                SumaConstants.TERMINAL_PUBKEY,  ConvertUtil.decode(p3), true));
					objMgr.addText("CardPubKey4", AlgoManager.RSACrypto(
			                SumaConstants.TERMINAL_PUBKEY,  ConvertUtil.decode(p4), true));
				}
				@Override
				protected String getMsgId() {
					return XML_MSGID_COMMIT_KEYS;//进行卡片密钥交换
				}
			}.get();
			//对BODY部分使用平台公钥加密
			//xml = encBody(xml);
			Map<String,Object>resMap = poster.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS, xml, SumaConstants.ENCRYPT_LEVEL_TERMINAL_SESSIONKEY_ENC_DATA);
			if(resMap == null){
				logger.printLogOnSDCard("卡片会话密钥交换, 服务器返回null");
				return -1;
			}else{
				resMap = XmlMgr_BusTradeBase.parse((String)resMap.get(MAP_HTTP_RESPONSE_RESPONSE));
				if("0000".equals(resMap.get("RespCode"))){
					//将密钥设置为已提交
					PConfirmDao dao = new PConfirmDao();
					String pkey = (String) resMap.get("PubKey");
					//存储如数据库
					dao.setCommitted();
					KEYSUtilProxy.savePKey(pkey);
					//获取平台公钥
					return 0;
				}else{
					return -1;
				}
			}
	 }
}
