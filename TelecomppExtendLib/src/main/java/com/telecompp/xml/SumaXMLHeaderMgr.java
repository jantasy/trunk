package com.telecompp.xml;

import com.telecompp.util.TerminalDataManager;

/**
 * 报文头管理功能
 * 
 * @author xiongdazhuang
 * 
 */
public class SumaXMLHeaderMgr
{
	public String MsgID = null;
	public String MsgType = null;
	public String CSN = null;
	public String ICCID = null;
	// public String TerminalModelNum = null;
	public String VerNo = null;
	public String VersionCode = null;
	public String VerStatus = null;
	public String ParamVerstatus = null;

	/**
	 * 初始化报文头数据
	 * 
	 * @return 执行成功:true
	 */
	public Boolean init()
	{
		// DeviceInfoManager deviceInfo = new DeviceInfoManager();

		this.MsgID = "";
		this.MsgType = "";
		this.VerNo = TerminalDataManager.getVerNo();
		this.VersionCode = TerminalDataManager.getVersionCode();
		this.CSN = TerminalDataManager.getCSN();// 从UIM卡中读取
		this.ICCID = TerminalDataManager.getICCID();// 从UIM卡中读取
		// this.TerminalModelNum = deviceInfo.getDeviceInfo();
		this.VerStatus = "";
		this.ParamVerstatus = "";

		return true;
	}
}
