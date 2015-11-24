package com.chinatelecom.nfc.Model;

import java.util.ArrayList;
import java.util.List;

import com.chinatelecom.nfc.R;
import com.chinatelecom.nfc.Service.WifiReceiverService;
import com.chinatelecom.nfc.Utils.MyUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Handler;
import android.widget.Toast;

public class WifiReceiver{
	WifiP2pManager manager;
	sharefilesBoradcast sboradcast;
	IntentFilter mIntentFilter;
	Context context;
	Channel channel;
	List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	WifiModule wModule;
	//String[] fileUri;
	public static Boolean hasConnection = false;
	public static Boolean beReceiving = false;
	int filesSize;
	
	
	
	public class sharefilesBoradcast extends BroadcastReceiver{
		@Override
		public void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			String s = Thread.currentThread().getName();
			if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
				if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED){
					Toast.makeText(context, context.getString(R.string.nfc_wifidirectorisclose), Toast.LENGTH_SHORT).show();
				}
				else if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				}
			}
			else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
				// The peer list has changed! We should probably do something about that.
				if (manager != null) {
					manager.requestPeers(channel, new PeerListListener() {
						@Override
						public void onPeersAvailable(WifiP2pDeviceList peerList) {
							// Out with the old, in with the new.
							if (hasConnection) return;
							peers.clear();   //清空list，刷新
							peers.addAll(peerList.getDeviceList());
							// If an AdapterView is backed by this data, notify it of the change.  For instance, if you have a ListView of available peers, trigger an update.
							if (peers.size() != 0)
								connect(context);
							else {
								//Log.d(WiFiDirectActivity.TAG, "No devices found");
								return;
							}
						}
					});
				}
			} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
				// Connection state changed! We should probably do something about that.
				if (manager != null) {
					NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
					if(networkInfo.isConnected()){
						hasConnection = true;
						manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
							@Override
							public void onConnectionInfoAvailable(WifiP2pInfo info) {
								// TODO Auto-generated method stub
								context.unregisterReceiver(sboradcast);
								//wModule.Ip = info.groupFormed.groupOwnerAddress.getHostAddress();////////
								if (wModule.Ip.length() == 0) return;
								receive(context);
							}
						});
					}
					else
						hasConnection = false;
				}
			} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
			}
		}
		
	}
	
	
	
	public WifiReceiver(Context context, WifiModule wModule, int filesSize)
	{
		this.wModule = wModule;
		this.context = context;
		this.filesSize = filesSize;
		
		sboradcast = new sharefilesBoradcast();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		context.registerReceiver(sboradcast, mIntentFilter);
		
		manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(context, context.getMainLooper(), null);
	}
	
	public void discover()
	{
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (hasConnection) 
				{
//					receive(context);
					return;
				}
				else {
					context.registerReceiver(sboradcast, mIntentFilter);
					dispeers(manager, channel);
				}
			}
		}, 1000);
	}
	
	
	private void connect(final Context context) {
		// Picking the first device found on the network.
		WifiP2pDevice device;
		WifiP2pConfig config = new WifiP2pConfig();
//        Iterator<WifiP2pDevice> peersIterator = peers.iterator();
		for (int i = 0 ; i < peers.size() ; i++) {
			device = peers.get(i);
			if (device.deviceAddress.toLowerCase().equals(this.wModule.Address.toLowerCase()))
			{
				config.deviceAddress = device.deviceAddress;
				config.wps.setup = WpsInfo.PBC;
				break;
			}
			if (i == peers.size() - 1) 
			{
//        		dispeers(manager, channel);
				return;
			}
		}
		manager.connect(channel, config, new ActionListener() {
			int fir = 0;
			@Override
			synchronized public void onSuccess() {
				hasConnection = true;
			}
			@Override
			public void onFailure(int reason) {
				if (fir++ != 0)
					Toast.makeText(context, context.getString(R.string.nfc_failed2connectretry), Toast.LENGTH_SHORT).show();
			};
		});
	}
	
	
	private void receive(final Context context)
	{
		String wModuleIp = wModule.Ip;
		Intent intent = new Intent(context, WifiReceiverService.class);
		intent.putExtra("wModuleIp", wModuleIp);
		intent.putExtra("receive2direction", context.getString(R.string.nfc_receive2direction));
		intent.putExtra("filesSize", filesSize);
		intent.setType("*/*");
		context.startService(intent);
	}
	
	
	private void dispeers(WifiP2pManager manager, Channel channel)
	{
		dispeers(manager, channel, new ActionListener(){
			@Override
			public void onSuccess(){
				//...
			}
			@Override
			public void onFailure(int reasonCode){
				//...
			}});            
	}
	
	private void dispeers(WifiP2pManager manager, Channel channel, ActionListener actionListener)
	{
		manager.discoverPeers(channel, actionListener);
	}
	
	
	
}
