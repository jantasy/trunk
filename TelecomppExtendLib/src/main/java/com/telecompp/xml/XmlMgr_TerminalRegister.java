package com.telecompp.xml;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.telecompp.util.SumaConstants;
import com.telecompp.util.SumaPostHandler;


/**
 * 终端信息注册报文管理
 * @author xiongdazhuang
 *
 */
public class XmlMgr_TerminalRegister implements SumaConstants
{
	/**
	 * 获取终端信息注册报文
	 * @param MerchantID 商户号
	 * @param BranchID 网点号
	 * @param IMEI 移动终端设备标识码
	 * @param TerminalModelNum 终端型号
	 * @param RelName 联系人姓名
	 * @param RelTel 联系电话
	 * @param RelAddr 联系人地址
	 * @param LoginName 自服务账号，商户开户生成
	 * @param LoginPwd 自服务密码，商户开户生成
	 * @param Email 电子邮件
	 * @return 成功：终端信息注册报文 失败：null
	 */
	
	
	/**
	 * 2YJT版本获取终端信息注册报文
	 * @param IMEI 移动终端设备标识码
	 * @param TerminalModelNum 终端型号
	 * @return 成功：终端信息注册报文 失败：null
	 */
	public String get2YJT(String IMEI, String TerminalModelNum, String phoneNum)
	{
		XmlHandler objMgr = new XmlHandler(1024 * 4);
		objMgr.initHeader(XML_MSGID_TERMINAL_REGISTER2YJT, XML_MSGTYPE_REQUEST);

		objMgr.addText("IMEI", IMEI);
		objMgr.addText("TerminalModelNum", TerminalModelNum);
		objMgr.addText("YJTPhone", phoneNum);
		
		objMgr.close();
		String xml = objMgr.getXMLString();
		if (xml == null)
		{
			return null;
		}
		return xml;
	}
	
	/**
	 * 2C版本获取终端信息注册报文
	 * @param IMEI 移动终端设备标识码
	 * @param TerminalModelNum 终端型号
	 * @return 成功：终端信息注册报文 失败：null
	 */
	public String get2C(String IMEI, String TerminalModelNum)
	{
		XmlHandler objMgr = new XmlHandler(1024 * 4);
		objMgr.initHeader(XML_MSGID_TERMINAL_REGISTER2C, XML_MSGTYPE_REQUEST);

		objMgr.addText("IMEI", IMEI);
		objMgr.addText("TerminalModelNum", TerminalModelNum);
		
		objMgr.close();
		String xml = objMgr.getXMLString();
		if (xml == null)
		{
			return null;
		}
		return xml;
	}

	/**
	 * 解析XML报文
	 * 
	 * @param xml
	 *            输入的XML报文
	 * @return 成功：解析后的map。 失败：返回null
	 */
	public Map<String, Object> parse(String xml)
	{
		Map<String, Object> parsedData = new HashMap<String, Object>();
		SumaPostHandler postHandler = new SumaPostHandler();

		try
		{
			parsedData = postHandler.parseXML(xml.getBytes("GBK"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}
		return parsedData;
	}
}
