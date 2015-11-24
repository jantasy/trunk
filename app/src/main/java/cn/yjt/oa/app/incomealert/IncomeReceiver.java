package cn.yjt.oa.app.incomealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.app.utils.StorageUtils;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.utils.AppSettings;
import cn.yjt.oa.app.utils.Config;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.PhoneUtils;

public class IncomeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(AppSettings.getIncomeAlertMode() == AppSettings.INCOME_MODE_CLOSE){
			return;
		}
		
		UserInfo current = AccountManager.getCurrent(context.getApplicationContext());
		if(current == null){
			return;
		}
		boolean isTelecom = PhoneUtils.isTelecom(current.getPhone());
		if(!isTelecom){
			return;
		}
		String number = intent
				.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

		String stringExtra = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		
		if(TelephonyManager.EXTRA_STATE_RINGING.equals(stringExtra)){
			try{
				IncomeManager.getInstance(context).receive(number);
			}catch(Throwable t){
				t.printStackTrace();
			}
		}else if(TelephonyManager.EXTRA_STATE_IDLE.equals(stringExtra)){
			try{
				IncomeManager.getInstance(context).close();
			}catch(Throwable t){
				t.printStackTrace();
			}
		}

		 /*记录操作 0615*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_INCOMING);
	}

}
