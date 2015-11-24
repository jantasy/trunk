package com.chinatelecom.nfc.Abstractor;

import android.content.ContentValues;
import android.content.Intent;

import com.chinatelecom.nfc.Interface.IWifiSender;

public abstract class WifiSender implements IWifiSender {
	public ContentValues contentvalues;
	public Intent intent;
	//public String fileUri;	
}
