package com.chinatelecom.nfc;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

import com.chinatelecom.nfc.Const.Const.transmitMode;
import com.chinatelecom.nfc.Interface.IWifiSender;
import com.chinatelecom.nfc.Model.AllCommunicationModule;
import com.chinatelecom.nfc.Model.ShareFilesSenderMultiple;
import com.chinatelecom.nfc.Model.ShareFilesSenderSingle;
import com.chinatelecom.nfc.Model.WifiReceiver.sharefilesBoradcast;
import com.chinatelecom.nfc.Utils.MyUtil;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;



public class ShareFilesSender extends BaseTag  implements CreateNdefMessageCallback, OnNdefPushCompleteCallback{
	IWifiSender SF;
	ProgressBar OK_btn;
	ListView fileslist_LV;
	Intent comeinIntent;
	transmitMode transmitmode = transmitMode.wifi;
	Set<BluetoothDevice> bDevice = BluetoothAdapter.getDefaultAdapter().getBondedDevices(); 
	Handler handler = new Handler();
	static Boolean hasConnection = false;
	WifiP2pManager manager;
	Channel channel;
	Stack<Boolean> wifiDirect = new Stack<Boolean>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_sharefiles_sender);
        initialComponent();
        initial();
        //setContentView(new CustomGifView(this));  
    }    
    
	
    private void initialComponent()
    {
//    	OK_btn = (ProgressBar)(findViewById(R.id.sharefiles_pBar));
    	fileslist_LV = (ListView)(findViewById(R.id.fileslist));
    	NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    	nfcAdapter.setNdefPushMessageCallback(this, this);
    	nfcAdapter.setOnNdefPushCompleteCallback(this, this);
    }
    


    
    private void initial()
    {
    	final Context curContext = this.getApplicationContext();
    	comeinIntent = (Intent) getIntent().clone();
        String action = comeinIntent.getAction();
        String type = comeinIntent.getType();
        
        String transmitmode_Str = comeinIntent.getStringExtra("transmitmode");
        if (transmitmode_Str.equals(transmitMode.bluetooth.toString()))
        	transmitmode = transmitMode.bluetooth;
        else if (transmitmode_Str.equals(transmitMode.wifi.toString()))
        	transmitmode = transmitMode.wifi;
    	
        if (Intent.ACTION_SEND.equals(action) && type != null) {
        	final ShareFilesSenderSingle mSF = new ShareFilesSenderSingle(comeinIntent);
        	fileslist_LV.setAdapter(mSF.getSimpleAdapter(curContext));
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
        	final ShareFilesSenderMultiple mSF = new ShareFilesSenderMultiple(comeinIntent);
        	fileslist_LV.setAdapter(mSF.getSimpleAdapter(curContext));
        }
        processIntent(comeinIntent);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
    }
    
	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
//            processIntent(intent);
//        }
		Boolean notnull = intent != null || intent.getAction() != null;
//		Boolean b = intent.getType().equals("application/mobileinfo") && (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()));
        if (notnull)
        	super.onNewIntent(intent);
        setIntent(intent);
	}
/*	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
    	String action = "";
    	String type = "";
    	if ("ShareSinglePic".equals(data.getAction())) {
        	comeinIntent = (Intent) getIntent().clone();
            action = comeinIntent.getAction();
            type = comeinIntent.getType();
        }
        if (Intent.ACTION_SEND.equals(action) && type != null) {
        	final ShareFilesSingle mSF = new ShareFilesSingle(comeinIntent);
        	fileslist_LV.setAdapter(mSF.getSimpleAdapter(this));
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
        	final ShareFilesMultiple mSF = new ShareFilesMultiple(comeinIntent);
        	fileslist_LV.setAdapter(mSF.getSimpleAdapter(this));
        }
    }
  */  
	private void processIntent(Intent intent) {
//		final AllCommunicationModule comAdpter = new AllCommunicationModule(retStr.split(",")[0], retStr.split(",")[1], retStr.split(",")[2]);

		final AllCommunicationModule comAdpter = new AllCommunicationModule(" ", " ", " ");
		

		
		final Context curContext = this;
        String action = comeinIntent.getAction();
        String type = comeinIntent.getType();
        
        if (Intent.ACTION_SEND.equals(action) && type != null) {
        	final ShareFilesSenderSingle mSF = new ShareFilesSenderSingle(comeinIntent);
        	if (transmitmode == transmitMode.bluetooth)
        		mSF.send(curContext, this, comAdpter._BluetoothModule);
        	else if (transmitmode == transmitMode.wifi)
        		mSF.send(curContext, this, comAdpter._WifiModule);
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
        	final ShareFilesSenderMultiple mSF = new ShareFilesSenderMultiple(comeinIntent);
			if (transmitmode == transmitMode.bluetooth)
				mSF.send(curContext, this, comAdpter._BluetoothModule);
			else if (transmitmode == transmitMode.wifi)
				mSF.send(curContext, this, comAdpter._WifiModule);
        }
	}
    
	public class sharefilesBoradcast extends BroadcastReceiver{
		@Override
		public void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
				if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED){
					MyUtil.ltoast(context, getApplicationContext().getString(R.string.nfc_wifidirectorisclose));
				}
				else if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				}
			}		
			else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(intent.getAction())) {
					// Connection state changed! We should probably do something about that.
				 NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
				 if(networkInfo.isConnected())
					 hasConnection = true;
				 else
					 hasConnection = false;
			 }
		}
	}
	
	
//	sharefilesBoradcast sboradcast = null;
//	IntentFilter mIntentFilter;
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		String[] module = getModule();
		//String[] module = {blueAdrs, wifiAdrs, wifiIP};
		String text = module[0] + "," + module[1] + "," + module[2] + "," + module[3] + "," + module[4];
		NdefMessage msg = new NdefMessage(
				new NdefRecord[] { MyUtil.createMimeRecord("application/mobileinfo", text.getBytes())
				/**
				 * The Android Application Record (AAR) is commented out. When a
				 * device receives a push with an AAR in it, the application
				 * specified in the AAR is guaranteed to run. The AAR overrides
				 * the tag dispatch system. You can add it back in to guarantee
				 * that this activity starts when receiving a beamed message.
				 * For now, this code uses the tag dispatch system.
				 */
				});
		return msg;
	}
	
	@Override
	public void onNdefPushComplete(NfcEvent event) {
		// TODO Auto-generated method stub
		sharefilesBoradcast sboradcast = new sharefilesBoradcast();
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		registerReceiver(sboradcast, mIntentFilter);
		getSystemService(Context.WIFI_P2P_SERVICE);
    	handler.postDelayed(new Runnable() {
    		@Override
    		public void run() {
    			// TODO Auto-generated method stub
    			if (!hasConnection) 
    				discoveryPeers();
    		}
        }, 500);
	}
	
	private void discoveryPeers()
	{
		manager = (WifiP2pManager) this.getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);
		manager.discoverPeers(channel, null);
	}
	
	
	private String[] getModule() {
		String blueAdrs;
		BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
		blueAdrs = blueadapter == null ? " " : blueadapter.getAddress();
		
		String wifiAdrs;
		WifiManager myWiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		wifiAdrs = myWiFiManager.getConnectionInfo().getMacAddress();
		String[] wifiAdrsArr = wifiAdrs.split(":");
		wifiAdrsArr[0] = MyUtil.HexAdd(wifiAdrsArr[0], + 2);
		wifiAdrs = wifiAdrsArr[0].toString() + wifiAdrs.substring(2, wifiAdrs.length());
		
		String wifiIP;
		wifiIP = MyUtil.getLocalIpAddress();
		//wifiIP = "192.168.49.1";
		//wifiIP = "192.168.49.10";
		
		ArrayList<Uri> uris = comeinIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		int filesSize = uris.size();
		
		String[] Adrs = {blueAdrs, wifiAdrs, wifiIP, transmitmode.toString(), String.valueOf(filesSize)};
//		String text = Adrs[0] + "," + Adrs[1] + "," + Adrs[2];
		return Adrs;
	}



}
