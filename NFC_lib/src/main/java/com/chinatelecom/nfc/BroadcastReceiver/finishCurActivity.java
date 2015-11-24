package com.chinatelecom.nfc.BroadcastReceiver;

import java.util.List;

import com.chinatelecom.nfc.Const.Const;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;


public class finishCurActivity extends BroadcastReceiver {
	 @Override
	 public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (action.equals(Const.finishCurActivity)) {
	        }
	 }
}
