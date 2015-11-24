package com.telecompp.xml;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.telecompp.util.SumaConstants;
import com.telecompp.util.SumaPostHandler;

public abstract class XmlMgr_BaseMgr implements SumaConstants{
	/**
	 * 解析XML报文
	 * 
	 * @param xml
	 *            输入的XML报文
	 * @return 成功：解析后的map。 失败：返回null
	 */
	public static Map<String, Object> parse(String xml)
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
