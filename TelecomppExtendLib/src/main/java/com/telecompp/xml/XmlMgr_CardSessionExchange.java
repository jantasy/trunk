package com.telecompp.xml;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.telecompp.util.SumaConstants;
import com.telecompp.util.SumaPostHandler;

/**
 * 卡片会话密钥XML报文管理
 * 
 * @author xiongdazhuang
 * 
 */
public class XmlMgr_CardSessionExchange implements SumaConstants
{
	/**
	 * 获取报文
	 * 
	 * @return 成功：卡片会话密钥报文。 失败：返回null
	 */
	public String get(String yjtMerchantId)
	{
		XmlHandler objMgr = new XmlHandler(1024 * 4);
		objMgr.initHeader(XML_MSGID_CARD_SESSIONKEY_EXCHANGE, XML_MSGTYPE_REQUEST);
		
		objMgr.addText("YJTMerchantId", yjtMerchantId);

		objMgr.close();
		String xml = objMgr.getXMLString();
		if (xml == null)
		{
			return null;
		}
		return xml;
	}

	/**
	 * 解析报文
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
