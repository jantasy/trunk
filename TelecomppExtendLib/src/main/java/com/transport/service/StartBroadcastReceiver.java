package com.transport.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartBroadcastReceiver extends BroadcastReceiver{
	
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION)) {
			Intent service = new Intent();
			service.setClass(context, UploadConfirmService.class);
			context.startService(service);
		}
	}

}
