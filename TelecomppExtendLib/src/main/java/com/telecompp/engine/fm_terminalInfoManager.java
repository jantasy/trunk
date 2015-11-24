package com.telecompp.engine;

import java.util.HashMap;
import java.util.Map;

import com.telecompp.util.SumaConstants;
import com.telecompp.util.SumaPostHandler;
import com.telecompp.util.TerminalDataManager;
import com.telecompp.xml.XmlMgr_TerminalRegister;


/**
 * 终端信息管理功能
 *  
 * @author xiongdazhuang
 * 
 */
public class fm_terminalInfoManager implements SumaConstants {
    SumaPostHandler HttpPostHandler = new SumaPostHandler();
    
	/**
     * 2YJT版本的终端注册 只有IMEI和TerminalModelNum
     * 
     * @param IMEI
     *            移动终端设备标识码
     * @param TerminalModelNum
     *            终端型号
     * @return 成功："00" 失败：null
     */
    public String terminalRegister2YJT(String IMEI, String TerminalModelNum, String phoneNum) {
        Map<String, Object> responseDataMap = new HashMap<String, Object>();
        Map<String, Object> parsedData = new HashMap<String, Object>();

        // 发送终端注册报文
        XmlMgr_TerminalRegister TerminalRegisterHandler = new XmlMgr_TerminalRegister();
        String terminalRegisterXMLString = TerminalRegisterHandler.get2YJT(IMEI, TerminalModelNum, phoneNum);
        if (terminalRegisterXMLString == null) {
            return null;
        }

        // 将XML报文发送给前置平台
        responseDataMap = HttpPostHandler.httpPostAndGetResponse(
                IpCons.SERVER_ADDRESS_IP, terminalRegisterXMLString,
                ENCRYPT_LEVEL_TERMINAL_SESSIONKEY_ENC_DATA);
        if (responseDataMap == null) {
            return null;
        } 
        // 得到来自前置平台的联机消费响应
        // 解析前置平台响应数据
        parsedData = TerminalRegisterHandler.parse((String) responseDataMap
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
        //	记录merchantId
        TerminalDataManager.setPlug_yjtMerchantId((String)parsedData.get("YJTMerchantId"));

        return "00";
    }
    

    /**
     * 2C版本的终端注册 只有IMEI和TerminalModelNum
     * 
     * @param IMEI
     *            移动终端设备标识码
     * @param TerminalModelNum
     *            终端型号
     * @return 成功："00" 失败：null
     */
    public String terminalRegister2C(String IMEI, String TerminalModelNum) {
        Map<String, Object> responseDataMap = new HashMap<String, Object>();
        Map<String, Object> parsedData = new HashMap<String, Object>();

        // 发送终端注册报文
        XmlMgr_TerminalRegister TerminalRegisterHandler = new XmlMgr_TerminalRegister();
        String terminalRegisterXMLString = TerminalRegisterHandler.get2C(IMEI, TerminalModelNum);
        if (terminalRegisterXMLString == null) {
            return null;
        }

        // 将XML报文发送给前置平台
        responseDataMap = HttpPostHandler.httpPostAndGetResponse(
                IpCons.SERVER_ADDRESS_IP, terminalRegisterXMLString,
                ENCRYPT_LEVEL_TERMINAL_SESSIONKEY_ENC_DATA);
        if (responseDataMap == null) {
            return null;
        } 
        // 得到来自前置平台的联机消费响应
        // 解析前置平台响应数据
        parsedData = TerminalRegisterHandler.parse((String) responseDataMap
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

        return "00";
    }
}
