package com.telecompp.xml;

import com.telecompp.util.TerminalDataManager;

/**
 * 公交卡交易请求报文基类
 * 
 * @author poet
 * 
 */
public abstract class XmlMgr_BusTradeBase extends XmlMgr_BaseMgr {
	private String city;

	public XmlMgr_BusTradeBase(String city) {
		this.city = city;
	}

	public String get() {
		String InterfaceType = null;
		XmlHandler objMgr = new XmlHandler(1024 * 4);
		objMgr.initHeader(getMsgId(), XML_MSGTYPE_REQUEST, city);

		InterfaceType = TerminalDataManager.getInterfaceType();

		objMgr.addText("InterfaceType", InterfaceType);
		objMgr.addText("MFlag", "2");
//		objMgr.addText("TransNO", TerminalDataManager.getPlug_paySerialNB()); // 碰碰订单号
		objMgr.addText("MerchantID",
				TerminalDataManager.getPlug_merchantNumber());// 商户ID
		initContent(objMgr);
		objMgr.close();
		String xml = objMgr.getXMLString();
		if (xml == null) {
			return null;
		}
		// TODO 打印请求报文
		return xml;
	}

	/**
	 * 初始化请求报文的内容部分
	 * 
	 * @param objMgr
	 */
	protected abstract void initContent(XmlHandler objMgr);

	protected abstract String getMsgId();
}
