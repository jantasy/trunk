package com.telecompp.engine;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.telecom.pp.extend.R;
import com.telecompp.engine.PaymentEngine.PayListener;
import com.telecompp.ui.MyDialog;

public class MyPayListener implements PayListener{
	
	private Activity activity;
	private MyDialog mdyalog;
	public MyPayListener(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onError(final int errorCode, final String msg) {
		
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mdyalog = new MyDialog(activity, R.style.MyDialog,
						new MyDialog.MyDialogListener() {
							
							@Override
							public void onPositiveClick(Dialog dialog, View view) {
								activity.finish();
							}
							
							@Override
							public void onNegativeClick(Dialog dialog, View view) {
								activity.finish();
							}
						}, MyDialog.ButtonConfirm);
//				if(errorCode == 0x02) {					
//					mdyalog.MyDialogSetMsg(msg);
//				} else {					
					mdyalog.MyDialogSetMsg(msg);
//				}
				mdyalog.show();
			}
		});
	}

	@Override
	public void onProcess(int process, String msg) {
		
	}

	@Override
	public void onSuccess(int resultCode, String msg) {
		activity.finish();
	}

}
