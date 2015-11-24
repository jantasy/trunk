package cn.yjt.oa.app.app.activity;

import cn.yjt.oa.app.app.AppRequest;

public class TitleFactory {
	public static String getTitle(String type){
		String title = null;
		if(AppRequest.ACTION_CARD_APP.equals(type)){
			title = "卡应用下载";
		}else if(AppRequest.ACTION_APP_ADD.equals(type)){
			title = "应用添加";
		}
		return title;
	}
}
