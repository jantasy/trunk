package cn.yjt.oa.app.app.controller;

import cn.yjt.oa.app.app.AppRequest;
// 应用的入口类型
public class AppType {
	// 卡应用下载
	public static final int APPS_CARDAPP = 1;
	// 工作台应用添加
	public static final int APPS_BUSINESS = 2;
	// 默认
	public static final int APPS_DEFAULT = 0;
	
	public static int getAppType(String type){
		int appType = 0;
		if(type.equals(AppRequest.ACTION_CARD_APP)){
			appType = APPS_CARDAPP;
		}else if(type.equals(AppRequest.ACTION_APP_ADD)){
			appType = APPS_BUSINESS;
		}
		return appType;
	}
}
