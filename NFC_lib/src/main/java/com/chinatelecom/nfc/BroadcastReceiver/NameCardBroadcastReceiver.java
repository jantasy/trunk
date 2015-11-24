package com.chinatelecom.nfc.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;

import com.chinatelecom.nfc.MainActivity;
import com.chinatelecom.nfc.DB.Provider.MyDataTable;

public class NameCardBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
			intent.setClass(context, MainActivity.class);
			intent.putExtra("dataType", String.valueOf(MyDataTable.NAMECARD));
			intent.putExtra("title", "名片夹");
			context.startActivity(intent);
		}
	}
}
