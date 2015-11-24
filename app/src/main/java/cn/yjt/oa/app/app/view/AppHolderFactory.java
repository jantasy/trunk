package cn.yjt.oa.app.app.view;

import android.content.Context;
import cn.yjt.oa.app.app.controller.AppType;
import cn.yjt.oa.app.app.controller.ItemType;
import cn.yjt.oa.app.beans.AppInfo;

public class AppHolderFactory {
	public static AppItemHolder getAppItemHolder(Context context, AppInfo appInfo,int itemType,int appType){
		AppItemHolder holder = null;
		if (appType == AppType.APPS_BUSINESS) {
			switch (itemType) {
			case ItemType.ITEM_TYPE_APPS_ADD_INTERNAL:
				holder = new AppAddItemInternalHolder(context, appInfo);
				break;
			case ItemType.ITEM_TYPE_APPS_ADD_NORMAL:
				holder = new AppAddItemNormalHolder(context, appInfo);
				break;
			case ItemType.ITEM_TYPE_APPS_ADD_LOCAL:
				// 复用
				holder = new AppAddItemInternalHolder(context,appInfo);
			default:
				break;
			}
		} else if (appType == AppType.APPS_CARDAPP) {
			holder = new AppCardItemNormalHolder(context, appInfo);
		} else if (appType == AppType.APPS_DEFAULT) {
			holder = new AppDetailsHolder(context, appInfo);
		}
		return holder;
	}
}
