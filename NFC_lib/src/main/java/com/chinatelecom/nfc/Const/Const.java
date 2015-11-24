package com.chinatelecom.nfc.Const;

import java.util.ArrayList;
import java.util.Iterator;

import com.chinatelecom.nfc.BroadcastReceiver.finishCurActivity;
import com.chinatelecom.nfc.Service.WifiSenderService;
import com.chinatelecom.nfc.Utils.Constant;

import android.R.integer;
import android.app.Service;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

public class Const {
	public static enum sendType   //单（多）文件发送方式
	{
		single,
		multiple
	}
	public static enum transmitMode{  //蓝牙（wifi）发送方式
		bluetooth,
		wifi
	}
	
	
	public static final int bluetoothConnectionTimeout = 5000;    //蓝牙连接超市
	public static final int SeekP2PTimeout = 5000;    //wifi搜索超时
	public static final int listenSharedPort = 8888;   //wifi传输端口
//	public static final int showRateLT = 500000;    //传输文件显示百分比条件
	public static final int showRateLT = 3000000;    //传输文件显示百分比条件
	public static final int intervalSendTime = 10000;    //再次强行发送文件时间
	public static final int judgeWifiConnectionStateTime = 21000;
	public static final String BlueUUID = "00001101-0000-1000-8000-00805F9B34FB";
	public static final String ShareDIR = Constant.NFCFORCHINATELECOM_DIR + "/myshares";
	public static Boolean WifiConnectionStateOfReturn = false;
	
	/* broadcast */
	public static String transmitFinished = "com.chinatelecom.nfc.sendFinished";
	public static final String finishCurActivity = "com.chinatelecom.nfc.finishCurActivity";
//	public static final String wifiConnectCallback = 
	
	public static Boolean NameCardActivityState = false;	
	
	public static final String mobileVersion = getVersion();
	public static final int mobileSDKVersion = getSDK();
	
	
    private static String getVersion()
    {
    	String version_release = Build.VERSION.RELEASE;
    	return version_release;
    }
    
    private static int getSDK()
    {
    	int sdk = Build.VERSION.SDK_INT;
    	return sdk;
    }
    
    public static final int TypeMyData = 1;
    public static Iterator<Double> RateIterator = getRateIterator(); 
    
    private static Iterator<Double> getRateIterator()
    {
    	ArrayList<Double> AL = new ArrayList<Double>();
    	AL.add(0.1);
    	AL.add(0.2);
    	AL.add(0.3);
    	AL.add(0.4);
    	AL.add(0.5);
    	AL.add(0.6);
    	AL.add(0.7);
    	AL.add(0.8);
    	AL.add(0.9);
    	AL.add(1.0);
    	return AL.iterator();
    }
    
    public static WifiSenderService runningServiceBox = null;
    
    public static Handler UIhandler = new Handler(Looper.getMainLooper());
    
}
