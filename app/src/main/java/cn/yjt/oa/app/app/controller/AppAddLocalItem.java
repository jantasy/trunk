package cn.yjt.oa.app.app.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.widget.Toast;
import cn.yjt.oa.app.app.utils.AdditionalIconUtils;
import cn.yjt.oa.app.beans.AppInfo;
import cn.yjt.oa.app.beans.DashBoardItem;

public class AppAddLocalItem extends AppItem {

	public AppAddLocalItem(Context context, AppInfo appInfo) {
		super(context, appInfo);
	}

	@Override
	public void doAction(int position) {
		Intent intent = new Intent();
		intent.putExtra(AdditionalIconUtils.EXTRA_RESULT_PKG, appInfo.getPackageName());
		intent.putExtra(AdditionalIconUtils.EXTRA_RESULT_ICON_URL,getResourceName());
		intent.putExtra(AdditionalIconUtils.EXTRA_RESULT_APPNAME, appInfo.getTitle(context));
		((Activity)context).setResult(Activity.RESULT_OK, intent);
		Toast.makeText(context, "已添加", Toast.LENGTH_LONG).show();
		((Activity)context).finish();
	}
	
	public String getResourceName(){
		PackageManager pm = context.getPackageManager();
		String resName = null;
		try {
			ApplicationInfo info = pm.getApplicationInfo(appInfo.getPackageName(), 0);
			Resources res = pm.getResourcesForApplication(appInfo.getPackageName());
			resName = DashBoardItem.SCHEME_RESOURCE + res.getResourceName(info.icon);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return resName;
	}
}
