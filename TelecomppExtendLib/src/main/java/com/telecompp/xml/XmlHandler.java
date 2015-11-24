package com.telecompp.xml;

import java.io.StringWriter;

import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.telecompp.ContextUtil;

/**
 * XML报文拼装功能
 * 
 * @author xiongdazhuang
 * 
 */
public class XmlHandler
{
	private StringWriter xmlWriter;
	private XmlPullParserFactory pullFactory;
	private XmlSerializer xmlSerializer;
	private Boolean bSuccess;

	/**
	 * 初始化
	 * 
	 * @param space
	 */
	public XmlHandler(int space)
	{
		try
		{
			bSuccess = true;
			xmlWriter = new StringWriter(space);
			xmlWriter.getBuffer().delete(0, xmlWriter.getBuffer().length());
			pullFactory = XmlPullParserFactory.newInstance();
			xmlSerializer = pullFactory.newSerializer();
			xmlSerializer.setOutput(xmlWriter);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			bSuccess = false;
			return;
		}
		return;
	}

	/**
	 * 拼装报文头
	 * 
	 * @param MsgID
	 *            报文的消息类型
	 * @param MsgType
	 *            报文的请求类型 1:请求 2:响应
	 */
	public void initHeader(String MsgID, String MsgType)
	{
		initHeader(MsgID, MsgType, ContextUtil.CITYCODE);
	}
	
	/**
	 * 拼装报文头
	 * @param MsgID
	 * 				报文的消息类型
	 * @param MsgType
	 *  			报文的请求类型 1:请求 2:响应
	 * @param city
	 * 				公交城市
	 */
	public void initHeader(String MsgID, String MsgType,String city)
	{
		try
		{
			SumaXMLHeaderMgr headerMgrHandler = new SumaXMLHeaderMgr();
			headerMgrHandler.init();

			xmlSerializer.startDocument("GBK", null);
			xmlSerializer.startTag("", "SumaPOS");
			xmlSerializer.startTag("", "Header");

			addText("MsgID", MsgID);
			addText("MsgType", MsgType);
			addText("CSN", headerMgrHandler.CSN);
			addText("ICCID", headerMgrHandler.ICCID);
			// addText("TerminalModelNum", headerMgrHandler.TerminalModelNum);
			addText("VersionCode", headerMgrHandler.VersionCode);
			addText("VerNo", headerMgrHandler.VerNo);
			addText("VerStatus", headerMgrHandler.VerStatus);
			addText("ParamVerstatus", headerMgrHandler.ParamVerstatus);
			//添加城市
			addText("City", city);
			xmlSerializer.endTag("", "Header");
			xmlSerializer.startTag("", "Content");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			bSuccess = false;
			return;
		}
	}

	/**
	 * 添加XML报文tag起始标识
	 * 
	 * @param tag
	 *            标识字段
	 */
	public void addTag(String tag)
	{
		try
		{
			xmlSerializer.startTag("", tag);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			bSuccess = false;
			return;
		}
		return;
	}

	/**
	 * 添加XML报文tag结束标识
	 * 
	 * @param tag
	 *            标识字段
	 */
	public void endTag(String tag)
	{
		try
		{
			xmlSerializer.endTag("", tag);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			bSuccess = false;
			return;
		}
		return;
	}

	/**
	 * 添加一个字段
	 * 
	 * @param tag
	 *            字段标识
	 * @param text
	 *            字段内容
	 */
	public void addText(String tag, String text)
	{
		try
		{
			if(text == null)
				text = "";
			xmlSerializer.startTag("", tag);
			xmlSerializer.text(text);
			xmlSerializer.endTag("", tag);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			bSuccess = false;
			return;
		}
		return;
	}

	/**
	 * 结束该XML报文的封装
	 */
	public void close()
	{
		try
		{
			xmlSerializer.endTag("", "Content");
			xmlSerializer.endTag("", "SumaPOS");
			xmlSerializer.endDocument();
			xmlSerializer.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			bSuccess = false;
			return;
		}
		return;
	}

	/**
	 * 获取拼装完成的XML报文字符串
	 * 
	 * @return 拼装完成的XML报文字符串
	 */
	public String getXMLString()
	{
		if (bSuccess == false)
		{
			return null;
		}
		return xmlWriter.toString();
	}
}
