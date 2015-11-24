package cn.yjt.oa.app.app.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import cn.yjt.oa.app.app.download.AppDownloadManager.AppDownloadTask;
import cn.yjt.oa.app.app.utils.AdditionalIconUtils;
import cn.yjt.oa.app.beans.AppInfo;

public class AppAddNormalItem extends AppItem{

	public AppAddNormalItem(Context context, AppInfo data) {
		super(context, data);
	}
	
	public void doNotInstalledAction() {
		// 启动下载 然后弹出下载框
		super.doNotInstalledAction();
		setOnResult();
	}
	
	@Override
	protected void doInstalledAction() {
		// 直接添加到工作台即可
		setOnResult();
	}
	
	public void setOnResult(){
		Intent intent = new Intent();
		intent.putExtra(AdditionalIconUtils.EXTRA_RESULT_PKG, appInfo.getPackageName());
		intent.putExtra(AdditionalIconUtils.EXTRA_RESULT_ICON_URL, appInfo.getIcon());
		intent.putExtra(AdditionalIconUtils.EXTRA_RESULT_APPNAME, appInfo.getName());
		intent.putExtra(AdditionalIconUtils.EXTRA_RESULT_FILE,AppDownloadTask.getAppDownloadedFile(appInfo).getAbsolutePath());
		((Activity)context).setResult(Activity.RESULT_OK, intent);
		Toast.makeText(context, "已添加", Toast.LENGTH_LONG).show();
		((Activity)context).finish();
	}


	@Override
	public String getStatusText() {
		String text = "安装";
		if (AppStatus.INSTALLED == getStatus()) {
			text = "添加";
		} 
		return text;
	}

}
