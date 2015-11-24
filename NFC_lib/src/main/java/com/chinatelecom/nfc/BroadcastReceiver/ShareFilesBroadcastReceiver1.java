package com.chinatelecom.nfc.BroadcastReceiver;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;


public class ShareFilesBroadcastReceiver1 extends BroadcastReceiver {
	WifiP2pManager manager;
	Channel channel;
	List<WifiP2pDevice> peers;
	
	public ShareFilesBroadcastReceiver1(WifiP2pManager manager, Channel channel) {
		// TODO Auto-generated constructor stub
		this.manager = manager;
		this.channel = channel;
	}
	private ShareFilesBroadcastReceiver1 activity;
	 @Override
	 public void onReceive(Context context, Intent intent) {

	        String action = intent.getAction();     //获取接收的intent里的action

	        //以下进行判断，对各种不同的P2P状态改变，执行不同的代码
	     if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
	            // 检查 Wifi Direct模式是否开启
	            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
	     if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
	    	 if (manager != null) {
	             manager.requestPeers(channel, new com.chinatelecom.nfc.Listener.sharefilesPeerListListener1(peers));
	            }
	        }
	     else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
	            // The peer list has changed!  We should probably do something about that.
	          }
	     else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
	            // Connection state changed!  We should probably do something about that.
	          }
	      else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
	      }
	    }
	 }
}
