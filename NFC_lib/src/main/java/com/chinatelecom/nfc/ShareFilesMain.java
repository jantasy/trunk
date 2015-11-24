package com.chinatelecom.nfc;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.chinatelecom.nfc.Const.Const;
import com.chinatelecom.nfc.Const.Const.transmitMode;
import com.chinatelecom.nfc.Model.AllCommunicationModule;
import com.chinatelecom.nfc.Model.WifiModule;
import com.chinatelecom.nfc.Model.WifiReceiver;
import com.chinatelecom.nfc.Service.BluetoothReceiverService;
import com.chinatelecom.nfc.Service.WifiReceiverService;
import com.chinatelecom.nfc.Service.WifiSenderService;
import com.chinatelecom.nfc.Utils.ClsUtils;
import com.chinatelecom.nfc.Utils.MyUtil;


public class ShareFilesMain extends BaseTag {

	//ashley 0923 删除无用资源
	RelativeLayout btn_sendpic, btn_sendmusic, btn_filemanage, btn_result;
	Button btn_setting;
	RadioGroup rdgroup;
	Uri uri;
	transmitMode transmitmode = transmitMode.wifi;
	final static int sharepicsType = 2;
	final static int sharefilesType = 3;
	final static int sharemusicsType = 4;
	
    int filesSize;
    public static WifiReceiver wifiRecer;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_sharefiles_main);
        initialComponent();
        initial();
    }
    
    private void initialComponent()
    {
    	btn_setting = (Button)findViewById(R.id.setting);
    	btn_sendpic = (RelativeLayout)findViewById(R.id.sendpic);
    	btn_sendmusic = (RelativeLayout)findViewById(R.id.sendmusic);
    	btn_filemanage = (RelativeLayout)findViewById(R.id.filemanage);
//    	btn_receive = (Button)findViewById(R.id.receive);
    	btn_result = (RelativeLayout)findViewById(R.id.result);
    	rdgroup = (RadioGroup)findViewById(R.id.rdgroup);
    	
    }
    private void initial()
    {
    	btn_setting.setOnClickListener(viewocl);
    	btn_sendpic.setOnClickListener(viewocl);
    	btn_sendmusic.setOnClickListener(viewocl);
    	btn_filemanage.setOnClickListener(viewocl);
//    	btn_receive.setOnClickListener(viewocl);
    	btn_result.setOnClickListener(viewocl);
    	rdgroup.setOnCheckedChangeListener(rdocl);
    	
    	if (!hasSDCard()){
    		MyUtil.ltoast(this, "找不到外部存储设备");
    		finish();
    	}
    	
    	if (!Const.mobileVersion.contains("4.")) ((RadioButton)findViewById(R.id.rdbluetooth)).setVisibility(View.GONE);
		MyUtil.openBluetoothIfClosed();
		MyUtil.toggleWIFIP2P(ShareFilesMain.this, true);
		
		IntentFilter filter = new IntentFilter(Const.transmitFinished);
		registerReceiver(new transmitFinishedBroadcastReceiver(), filter);
    }
    
    
 
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	Intent intent;
    	
    	
    	if (requestCode == sharepicsType && data != null) 
    	{
    		Uri uri = data.getData();
    		ArrayList<Uri> uris = new ArrayList<Uri>();uris.add(uri);
    		intent = new Intent(this, ShareFilesSender.class);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(Intent.EXTRA_STREAM, uris);
            intent.putExtra("transmitmode", transmitmode.toString());
            //intent.putExtras(senddata);  
            intent.setType("image/*");
            startActivity(intent);
    	}
    	else if (requestCode == sharefilesType && data != null) 
        {
        	ArrayList<Uri> uri = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            intent = new Intent(this, ShareFilesSender.class);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra("transmitmode", transmitmode.toString());
            //intent.putExtras(senddata);  
            intent.setType("*/*");
            startActivity(intent);
        }
    	else if (requestCode == sharemusicsType && data != null) 
        {
        	ArrayList<Uri> uri = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            intent = new Intent(this, ShareFilesSender.class);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra("transmitmode", transmitmode.toString());
            //intent.putExtras(senddata);  
            intent.setType("audio/*");
            startActivity(intent);
        }
    	
    }

    
    OnCheckedChangeListener rdocl = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == R.id.rdwifi) {
				transmitmode = transmitMode.wifi;
//				WifiP2pManager manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
//				Channel channel = manager.initialize(ShareFilesMain.this, getMainLooper(), null);
//				if (manager != null && channel != null) {
//					MyUtil.showMessage(R.string.nfc_msg_open_wifi_direct, ShareFilesMain.this);
//	                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
//	            } 
			}
			else if (checkedId == R.id.rdbluetooth) {
				transmitmode = transmitMode.bluetooth;
			}
		}
	};
  
    OnClickListener viewocl = new OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		// TODO Auto-generated method stub
//    		if (transmitmode == transmitMode.bluetooth){
//				if (!MyUtil.openBluetoothUntilSuccess()) {
//					MyUtil.ltoast(ShareFilesMain.this, getString(R.string.nfc_failed2OpenBluetooth));
//					return;
//				}
//				else
//					MyUtil.ltoast(ShareFilesMain.this, getString(R.string.nfc_success2OpenBluetooth));
//    		}
//    		else if (transmitmode == transmitMode.wifi){
//				MyUtil.toggleWIFIP2P(ShareFilesMain.this, true);
//				MyUtil.ltoast(ShareFilesMain.this, getString(R.string.nfc_success2OpenWIFIP2P));
//    		}
    		
//			MyUtil.openBluetoothIfClosed();
//			MyUtil.toggleWIFIP2P(ShareFilesMain.this, true);
    		
    		
    		Intent intent;
    		int id = v.getId();
    		if(id== R.id.sendpic){
				// TODO Auto-generated method stub
    			intent = new Intent(Intent.ACTION_GET_CONTENT);
    			intent.setType("image/*");
				//galleryIntent.putExtra("crop", true);
    			intent.putExtra("return-data", true);
				startActivityForResult(intent, sharepicsType);
				}else 
    		if(id== R.id.sendmusic){
    			intent = new Intent(ShareFilesMain.this, MusicsManager.class);
    			//musicIntent.setType("*/*");
    			//galleryIntent.putExtra("crop", true);
    			//musicIntent.putExtra("return-data", true);
    			startActivityForResult(intent, sharemusicsType);
    			}else 
    		if(id== R.id.filemanage){
    			intent = new Intent();
    			intent.setDataAndType(Uri.fromFile(new File("/sdcard")), "*/*");
    			intent.setClass(ShareFilesMain.this, FilesManager.class);
				startActivityForResult(intent, sharefilesType);
    			
//            	intent = new Intent(ShareFilesMain.this, WifiSenderService.class);
//            	intent.putExtra("fileUri", new String[]{"file){///mnt/sdcard/1/ldm-SayYes.mp3"});
//            	intent.putExtra("transmiting", getString(R.string.nfc_transmiting));
//            	intent.putExtra("transmitending", getString(R.string.nfc_transmitending));
//            	intent.putExtra("fileisnotfound", getString(R.string.nfc_fileisnotfound));    	
//            	intent.putExtra("failed2connect", getString(R.string.nfc_failed2connect));
//            	intent.putExtra("failed2transmit", getString(R.string.nfc_failed2transmit));
//            	intent.putExtra("waitforsending", getString(R.string.nfc_waitforsending));
//            	intent.putExtra("sending", getString(R.string.nfc_sending));
//            	intent.setType("*/*");
//            	startService(intent);
				}else   	
			//ashley 0923 删除无用资源
//    		if(id== R.id.receive){
//    			MyUtil.openBluetoothIfClosed();
//    			MyUtil.toggleWIFIP2P(ShareFilesMain.this, true);
//    			intent = new Intent();
//    			intent.setClass(ShareFilesMain.this, ShareFilesReceiver.class);
//				startActivity(intent);
//    			
////    	    	String wModuleIp = "192.168.49.10";
////    	    	intent = new Intent(ShareFilesMain.this, WifiReceiverService.class);
////    	    	intent.putExtra("wModuleIp", wModuleIp);
////    	    	intent.putExtra("receive2direction", getString(R.string.nfc_receive2direction));
////    	    	intent.putExtra("filesSize", 1);
////    	    	intent.setType("*/*");
////    	    	startService(intent);
//				}else 
    		if(id== R.id.result){
    			intent = new Intent();
    			intent.setClass(ShareFilesMain.this, FilesResult.class);
				startActivity(intent);
    			}else 
    		if(id== R.id.setting){
    			MyUtil.showMessage(R.string.nfc_msg_open_wifi_direct, ShareFilesMain.this,true);
    			startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
    			} 
    		}
    	
    };
    
    class transmitFinishedBroadcastReceiver extends BroadcastReceiver {
    	@Override
    		public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals(Const.transmitFinished)) {
    			intent = new Intent();
    			intent.setClass(ShareFilesMain.this, FilesResult.class);
				startActivity(intent);
    		}
    	}
    }
    

    
    private Boolean hasSDCard()
    {
    	return MyUtil.externalStorageState();
    }
    
    
	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		Boolean notnull = intent != null && intent.getType() !=null && intent.getAction() != null;
		Boolean notnull2 = intent != null && intent.getAction() != null;
		
        if (notnull) {
        	Boolean b = intent.getType().equals("application/mobileinfo") && (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()));
        	if (b) {
        		processIntent(intent);
        		return;
//            finish();
        	}
        }
        if (notnull2)
        	super.onNewIntent(intent);
        setIntent(intent);////////////////////
	}
	
	private void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		String retStr = new String(msg.getRecords()[0].getPayload());

		String[] retStr_Arr = retStr.split(",");
		AllCommunicationModule comAdpter = new AllCommunicationModule(retStr_Arr[0], retStr_Arr[1], retStr_Arr[2]);
        if (retStr_Arr[3].equals(transmitMode.bluetooth.toString()))
        	transmitmode = transmitMode.bluetooth;
        else if (retStr_Arr[3].equals(transmitMode.wifi.toString()))
        	transmitmode = transmitMode.wifi;
        filesSize = Integer.valueOf(retStr_Arr[4]);
        handleTransmitMode(transmitmode, comAdpter);
	}
	
	
	private void handleTransmitMode(transmitMode transmitmode, AllCommunicationModule comAdpter)
	{
		if (transmitmode == transmitMode.wifi)
		{
//			if (wifiRecer == null)
			wifiRecer = new WifiReceiver(this, comAdpter._WifiModule, filesSize);
			wifiRecer.discover();
		}
		else if (transmitmode == transmitMode.bluetooth)
		{
			Boolean b = judgeBluetooth(comAdpter._BluetoothModule.Address);//////////////////
			if (!b) MyUtil.ltoast(this, "蓝牙配对失败");
			
			Intent intent = new Intent(this, BluetoothReceiverService.class);
//	    	intent.putExtra("fileUri", new String[]{fileUri});
	    	intent.putExtra("wModuleIp", comAdpter._WifiModule.Ip);
	    	intent.putExtra("bModuleAddress", comAdpter._BluetoothModule.Address);
	    	intent.putExtra("filesSize", filesSize);
	    	intent.putExtra("receive2direction", getApplicationContext().getString(R.string.nfc_receive2direction));
	    	startService(intent);
		}
	}
	
	
	private Boolean judgeBluetooth(String Address){
		Boolean Success2openBluetooth = pair(Address, "k");
    	if (!Success2openBluetooth) 
    	{
    		Toast.makeText(this, getString(R.string.nfc_failed2PairBluetooth), Toast.LENGTH_LONG).show();
    		return false;
    	}
    	return true;
	}
	
	
	   
	   public static boolean pair(String strAddr, String strPsw)
	   {
	           boolean result = false;
	           BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	           bluetoothAdapter.cancelDiscovery();
	           if (!bluetoothAdapter.isEnabled())
	           {
	        	   bluetoothAdapter.enable();
	           }
	           if (!BluetoothAdapter.checkBluetoothAddress(strAddr))
	           { // 检查蓝牙地址是否有效
	        	   Log.d("mylog", "devAdd un effient!");
	           }
	           BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddr);
	           if (device.getBondState() != BluetoothDevice.BOND_BONDED)
	           {
	                   try
	                   {
	                           Log.d("mylog", "NOT BOND_BONDED");
	                           ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
	                           ClsUtils.createBond(device.getClass(), device);
	                           ClsUtils.cancelPairingUserInput(device.getClass(), device);
	                           //remoteDevice = device; // 配对完毕就把这个设备对象传给全局的remoteDevice
	                           result = true;
	                           ClsUtils.connect(device);
	                   }
	                   catch (Exception e)
	                   {
	                           // TODO Auto-generated catch block
	                           Log.d("mylog", "setPiN failed!");
	                           e.printStackTrace();
	                   } 
	           }
	           else
	           {
	                   Log.d("mylog", "HAS BOND_BONDED");
	                   try
	                   {
	                           ClsUtils.createBond(device.getClass(), device);
	                           ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
	                           ClsUtils.createBond(device.getClass(), device);
	                           //remoteDevice = device; // 如果绑定成功，就直接把这个设备对象传给全局的remoteDevice
	                           result = true;
	                   }
	                   catch (Exception e)
	                   {
	                           // TODO Auto-generated catch block
	                           Log.d("mylog", "setPiN failed!");
	                           e.printStackTrace();
	                   }
	           }
	           return result;
	   }

}