package cn.yjt.oa.app.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.yjt.oa.app.app.utils.NotificationHolder;

public class InstallBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 接收广播 监听应用是否已经安装
		try {
			if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED")){
				String data = intent.getDataString();
				NotificationHolder.clearNotification(context, data.substring(data.indexOf(":")+1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
