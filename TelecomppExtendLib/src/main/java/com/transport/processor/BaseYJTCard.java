package com.transport.processor;

import android.content.Context;
import cn.com.ctbri.yijitong.edep.EjitongCard;

import com.telecompp.util.ResponseExceptionInfo;
import com.telecompp.util.SumaConstants;
import com.telecompp.util.WriteLog;

public class BaseYJTCard {
	
	EjitongCard edepAdapter;
	String tradeNum;
	Context context;
	
	public BaseYJTCard(String tradeNum, Context context) {
		this.tradeNum = tradeNum;
		this.context = context;
		initEjitongCard();
	}

	public void initEjitongCard() {
		try {
			//	1.每次机卡交互, 必须先获得翼机通卡
			edepAdapter = EjitongCard.getDefaultCard();
			//	2.初始化机卡通道         aid:应用标识符 D1560000401000300000000200000000
			edepAdapter.init("D1560000401000300000000200000000", context);
		} catch (Exception e) {
			//	设置错误信息
			ResponseExceptionInfo.setErrorCode(SumaConstants.ERROR_INITYJTCARD_EXCEPTION);
			ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_INITYJTCARD_EXCEPTION_MSG);
			e.printStackTrace();
			WriteLog.writeLogOnSDCard("机卡通道初始化失败:" + e.toString());
		}
	}
	
	
}
