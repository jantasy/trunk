package cn.yjt.oa.app.app.controller;

import android.content.Context;
import cn.yjt.oa.app.beans.AppInfo;

public class AppItemFactory {
	public static AppItem getAppItem(Context context, AppInfo appInfo, int viewType,int appType) {
		AppItem retItem = new AppItem(context, appInfo);
		if(appType == AppType.APPS_BUSINESS){
			switch(viewType) {
			case ItemType.ITEM_TYPE_APPS_ADD_INTERNAL:
				retItem = new AppAddInternalItem(context, appInfo);
				break;
			case ItemType.ITEM_TYPE_APPS_ADD_NORMAL:
				retItem = new AppAddNormalItem(context, appInfo);
				break;
			case ItemType.ITEM_TYPE_APPS_ADD_LOCAL:
				retItem = new AppAddLocalItem(context,appInfo);
			default:
				break;
			}
		}else if (appType == AppType.APPS_CARDAPP){
			retItem = new AppCardNormalItem(context, appInfo);
		}
		return retItem;
	}
}
