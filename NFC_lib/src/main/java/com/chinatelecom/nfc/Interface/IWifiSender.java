package com.chinatelecom.nfc.Interface;

import android.app.Activity;
import android.content.Context;
import android.widget.SimpleAdapter;

import com.chinatelecom.nfc.Model.CommunicationModule;

public interface IWifiSender {
	public void send(Context context, Activity activity, CommunicationModule mModule);
	public SimpleAdapter getSimpleAdapter(Context context);
}
