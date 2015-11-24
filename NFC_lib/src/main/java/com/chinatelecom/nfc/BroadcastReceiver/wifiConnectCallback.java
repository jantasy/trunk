package com.chinatelecom.nfc.BroadcastReceiver;

import java.util.List;

import com.chinatelecom.nfc.Const.Const;
import com.chinatelecom.nfc.Utils.MyUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Handler;
import android.os.Parcelable;


public class wifiConnectCallback extends BroadcastReceiver {
	 @Override
	 public void onReceive(final Context context, Intent intent) {
	        String action = intent.getAction();
	        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {  
	            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);    
	            if (null != parcelableExtra) {    
	                WifiManager mWifiManager = (WifiManager) context 
	                        .getSystemService(Context.WIFI_SERVICE); 
	                int wID = mWifiManager.getConnectionInfo().getNetworkId();
	            	
	                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;    
	                State state = networkInfo.getState();  
	                boolean isConnected = state==State.CONNECTED;//当然，这边可以更精确的确定状态  
//	                LogTag.showTAG_e(this.getClass().getSimpleName(), "isConnected"+isConnected);  
	                if(isConnected){  
//	                	MyUtil.ltoast(context, "WLAN已连接");
	                	Const.WifiConnectionStateOfReturn = true;
	                }else{  
	                	Const.WifiConnectionStateOfReturn = false;
	                }  
	            }    
	        }  
	 }
}
