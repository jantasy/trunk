package com.chinatelecom.nfc.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.nfc.NdefRecord;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.chinatelecom.nfc.R;
import com.chinatelecom.nfc.AsyncTask.Wait4Handler;
import com.chinatelecom.nfc.Const.Const;
import com.chinatelecom.nfc.DB.Dao.MyModeDao;
import com.chinatelecom.nfc.DB.Pojo.MyMode;
import com.chinatelecom.nfc.DB.Provider.SettingData;
import com.chinatelecom.nfc.Environment.NetworkManager;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Bytes;

public class MyUtil {

	public static void showMessage(int resID, Context context) {
		Toast.makeText(context, resID, Toast.LENGTH_SHORT).show();
	}

	public static void showMessage(int resID, Context context, boolean l) {
		Toast.makeText(context, resID, Toast.LENGTH_LONG).show();
	}

	public static void showMessage(String str, Context context) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

	public static void showMessage(String str, Context context, boolean l) {
		Toast.makeText(context, str, Toast.LENGTH_LONG).show();
	}

	/**
	 * 转换dip为px
	 * 
	 * @param context
	 * @param dip
	 * @return
	 */
	public static int dipToPx(Context context, int dip) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
	}

	/**
	 * 转换px为dip
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int pxToDip(Context context, int px) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));
	}
	
	
	public static Integer longToInteger(long l){
		return Integer.parseInt(String.valueOf(l));
	}
	
	public static long calendarToDay(long time){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),0,0,0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	/**
	 * long to yyyy/MM/dd HH:mm
	 * @param time long
	 * @return yyyy/MM/dd HH:mm
	 */
	public static String dateFormat(long time){
		
		if(time > 100){
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(time);
			SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			return sf.format(cal.getTime());
		}
		return "";
	}
	
	
	public static boolean noNullException(String string) {
		// TODO Auto-generated method stub
		if(string != null && string.length() > 0){
			return true;
		}
		return false;
	}

	public static int dipToPx(int dip) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		displayMetrics.setToDefaults();
		int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
		return pixel;
	}
	
	/**
	 * 获取当前应用的版本号
	 * 
	 * @return 版本号
	 * @throws Exception
	 */
	public static String getVersionName(Context context) throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		System.out.println(packInfo.versionCode);
		String version = packInfo.versionName;
		return version;
	}
	/**
	 * 获取当前应用的版本号
	 * 
	 * @return 版本号
	 * @throws Exception
	 */
	public static int getVersionCode(Context context) throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		return packInfo.versionCode;
	}
	
	
	public static NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        Preconditions.checkNotNull(text);
        Preconditions.checkNotNull(locale);
        final byte[] langBytes = locale.getLanguage().getBytes(Charsets.US_ASCII);
        final Charset utfEncoding = encodeInUtf8 ? Charsets.UTF_8 : Charset.forName("UTF-16");
        final byte[] textBytes = text.getBytes(utfEncoding);
        final int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        final char status = (char) (utfBit + langBytes.length);
        final byte[] data = Bytes.concat(new byte[] {(byte) status}, langBytes, textBytes);
//        final byte[] data = Bytes.concat(new byte[] {(byte) status}, textBytes);
        
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
//        NdefRecord extRecord = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, "com.chinatelecom.nfc:externalType".getBytes(utfEncoding), new byte[0], data);
//        return extRecord;
    }

    public static NdefRecord newMimeRecord(String type, byte[] data) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(data);
        final byte[] typeBytes = type.getBytes(Charsets.US_ASCII);
        return new NdefRecord(NdefRecord.TNF_MIME_MEDIA, typeBytes, new byte[0], data);
    }
    
    
    
    public static void openBluetoothIfClosed()
    {
    	BluetoothAdapter BAdapter = BluetoothAdapter.getDefaultAdapter();
    	if (!BAdapter.isEnabled()) BAdapter.enable();
    }
    
    public static void openBluetoothIfClosed(BluetoothAdapter BAdapter)
    {
        if (!BAdapter.isEnabled()) BAdapter.enable();
    }
    
    public static Boolean openBluetoothUntilSuccess()
    {
    	int timeout = 6000;
    	int accumulateTime = 0;
    	openBluetoothIfClosed();
    	BluetoothAdapter BAdapter = BluetoothAdapter.getDefaultAdapter();
    	while(true)
    	{
    		if (BAdapter.getState() == BluetoothAdapter.STATE_ON) return true;
    		else {
    			try {
    				if (accumulateTime > timeout) return false;
					Thread.sleep(1000);
					accumulateTime += 1000;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    }
    
    public static Boolean openBluetoothUntilSuccess(BluetoothAdapter BAdapter)
    {
    	int timeout = 6000;
    	int accumulateTime = 0;
    	openBluetoothIfClosed();
    	while(true)
    	{
    		if (BAdapter.getState() == BluetoothAdapter.STATE_ON) return true;
    		else {
    			try {
    				if (accumulateTime > timeout) return false;
					Thread.sleep(1000);
					accumulateTime += 1000;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    }
    
    
    public static void closeBluetoothIfOpened()
    {
    	BluetoothAdapter BAdapter = BluetoothAdapter.getDefaultAdapter();
    	boolean b = BAdapter.getState()==BluetoothAdapter.STATE_ON || BAdapter.getState()==BluetoothAdapter.STATE_TURNING_ON;
    	if (b) BAdapter.disable();
    }
    
    public static void closeBluetoothIfOpened(BluetoothAdapter BAdapter)
    {
    	boolean b = BAdapter.getState()==BluetoothAdapter.STATE_ON || BAdapter.getState()==BluetoothAdapter.STATE_TURNING_ON;
        if (b) BAdapter.disable();
    }
	
    public static Boolean BluetoothIsOpenedOrOpening()
    {
    	BluetoothAdapter BAdapter = BluetoothAdapter.getDefaultAdapter();
    	return (BAdapter.getState()==BluetoothAdapter.STATE_ON || BAdapter.getState()==BluetoothAdapter.STATE_TURNING_ON);
    }
    public static Boolean BluetoothIsOpenedOrOpening(BluetoothAdapter BAdapter)
    {
    	return (BAdapter.getState()==BluetoothAdapter.STATE_ON || BAdapter.getState()==BluetoothAdapter.STATE_TURNING_ON);
    }
    public static NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }
    public static String intToIp(int ip)
    {
    	return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
    }
    
    /**
     * 
     * @param context
     * @param muteMode
     * @param wifiSSID
     * @param wifiPassword
     */
	public static void setPhoneMode(Context context,List<MyMode> myModes,String muteMode,String wifiSSID,String wifiPassword) {
		// TODO Auto-generated method stub
		NetworkManager mNetworkManager = new NetworkManager(context);
		if(myModes != null && myModes.size() > 0){
			MyMode myMode = myModes.get(0);
			int m = mNetworkManager.getAudioManagerMode();
			if (m == AudioManager.RINGER_MODE_SILENT) {
				myMode.muteMode = SettingData.ON;
			}else if(m == AudioManager.RINGER_MODE_VIBRATE){
				myMode.muteMode = SettingData.OFF;
			}else{
				myMode.muteMode = SettingData.DEFAULT_MODE;
			}
			WifiAdmin wifiAdmin = new WifiAdmin(context);
			myMode.wifiSwitch = wifiAdmin.isWifiOpened()?SettingData.ON:SettingData.OFF;
			myMode.digitalSwitch = mNetworkManager.isMobileConnected()?SettingData.ON:SettingData.OFF;
			myMode.bluetooth = BluetoothIsOpenedOrOpening()?SettingData.ON:SettingData.OFF;
			
			myMode.wifiSSID = "";
			myMode.wifiPassword  = "";
			MyModeDao.update(context, myMode, true);
		}
		String[] strMode = muteMode.split(",");
		int []intMode = new int[strMode.length];
		for (int i = 0; i < strMode.length; i++) {
			intMode[i] = Integer.parseInt(strMode[i]);
		}
		
		setPhoneModeOnly(context,intMode,wifiSSID, wifiPassword);
		
	}

	public static void setPhoneModeOnly(Context context,int []mode,String wifiSSID, String wifiPassword) {
		 NetworkManager mNetworkManager = new NetworkManager(context); 
		if (mode == null)
			return;
		//响铃和静音
		if (mode[0] == SettingData.OFF) {
			mNetworkManager.openModeVibrate();
		} else if (mode[0] == SettingData.ON) {
			mNetworkManager.openSilentMode();
		}else{
			mNetworkManager.closeSilentMode();
		}
		//振动
//		if (mode[1] == SettingData.OFF) {
//			mNetworkManager.closeModeVibrate();
//		} else 
//		if (mode[1] == SettingData.ON) {
//			mNetworkManager.openModeVibrate();
//		}
		//�{牙
		if (mode[1] == SettingData.OFF) {
			closeBluetoothIfOpened();
		} else if (mode[1] == SettingData.ON) {
			openBluetoothIfClosed();
		}
		//gprs
		if (mode[2] == SettingData.OFF) {
			try {
				mNetworkManager.toggleGprs(false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (mode[2] == SettingData.ON) {
			try {
				mNetworkManager.toggleGprs(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//wifi
		if (mode[3] == SettingData.OFF) {
			mNetworkManager.toggleWiFi(false);
		} else if (mode[3] == SettingData.ON) {
			if (!TextUtils.isEmpty(wifiPassword)
					&& !TextUtils.isEmpty(wifiSSID)) {
				///////
				openWifiBySSID(context,wifiSSID,wifiPassword);
//				ProgressDialog progressDialog = ProgressDialog.show(context, "等待信息", "请等待连接", true, false);
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				progressDialog.dismiss();
			} else {
				mNetworkManager.toggleWiFi(true);
			}
		}
			
	}
	
	/**
	 * 打开无线
	 * @param context
	 * @param mySsid
	 * @param password
	 */
	public static void openWifiBySSID(final Context context,final String mySsid, final String password) {
		new Thread() {
			@Override
			public void run() {
				if (mySsid == null || mySsid.length() == 0) {
					return;
				}
				WifiAdmin wifiAdmin = new WifiAdmin(context);
				// 需要花时间方法
				// 打开wifi
				wifiAdmin.OpenWifi();
				// 判断wifi是否开启
				String SSID = wifiAdmin.GetSSID();
				String newSSID = SSID == null ? "NOWIFI" : SSID;
				if (!wifiAdmin.isWifiOpened()) {
					try {
						while (!wifiAdmin.isWifiOpened())
							Thread.sleep(100);
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// 扫描wifi
				wifiAdmin.StartScan();
				try {
					if (!newSSID.equals(mySsid)) {
						final WifiAdmin wifiadmin = wifiAdmin;
						wifiadmin.ConnectConfiguration2(mySsid, password);
//						new Thread() {
//							@Override
//							public void run() {
//								Const.UIhandler.post(new Runnable() {
//									@Override
//									public void run() {
//										try {
//											int i = 0;
//											while(true) {
//												Thread.sleep(1000);
//												i += 1000;
//												if (i > Const.judgeWifiConnectionStateTime) break;
//											}
//										} catch (InterruptedException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
//										String msg = Const.WifiConnectionStateOfReturn ? "" : "WLAN连接失败";
//										if (msg != null && msg != "")
//											MyUtil.ltoast(context, msg);				                
//									}
//								});
//							}
//						}.start();
					} else {
						// 向handler发消息
						return;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// handler.sendEmptyMessage(-1);
					e.printStackTrace();
					return;
				}

				int time = 0;
				while (!newSSID.equals(mySsid)) {
					wifiAdmin = new WifiAdmin(context);
					SSID = wifiAdmin.GetSSID();
					newSSID = SSID == null ? "NOWIFI" : SSID;
					try {
						Thread.sleep(1000);
						time++;
						if (time >= 5000) {
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				while (!isWiFiActive(context)) {

					try {
						Thread.sleep(1000);
						time++;

						if (time >= 5000) {

							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (time >= 5000) {
					// 超时
				}
			}
		}.start();

	}
	
	// 判断WIFI是否打开
	public static boolean isWiFiActive(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getTypeName().equals("WIFI")
							&& info[i].isConnected()) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
	public static void ltoast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	
	//by yuzhao 2013/7/11 start
	public static void lltoast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	//by yuzhao 2013/7/11 end
	
	public static void stoast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	public static Boolean isClassType(String gson, int classType)
	{
		String[] gsonArr = gson.substring(1, gson.length() - 1).split(",");
		String[] kv;
		for (String s : gsonArr) {
			kv = s.split(":");
			if (kv[0].toString().equals("\"classType\"") && kv[1].equals(String.valueOf(classType))) return true;
		} 
		return false; 
	}

	public static boolean checkURL(String url){
		String pattern = "[a-zA-z]+://[^\\s]*";
		if(url.matches(pattern)){
			return true;
		}
		return false;
	}
	public static boolean setSelection(CharSequence text){
		if (text instanceof Spannable) {
		Spannable spanText = (Spannable)text;
		Selection.setSelection(spanText, text.length());
		}
		return false;
	}
	
	
	public static String getStringMode(int[] mode){
		StringBuilder strmode = new StringBuilder();
		if(mode != null){
			for (int i = 0; i < mode.length; i++) {
				strmode.append(mode[i]).append(",");
			}
		}
		String smode = strmode.toString();
		smode = smode.substring(0, smode.length() - 1);
		return smode;
	}
	
	public static String getDeviceId(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String mDeviceId = tm.getDeviceId();
		
		return mDeviceId;
	}
	private final static String diviceidkey = "diviceid";
	private final static String mydatakeyword = "mydata";
	
	public static String enDiviceID(String diviceID){
		
		return diviceidkey+diviceID;
	}
	public static boolean deDiviceID(String enDiviceID){
		if(enDiviceID.length() > diviceidkey.length() 
				&& enDiviceID.substring(0, diviceidkey.length()).equals(diviceidkey)){
			return true;
		}
		return false;
	}
	public static String subDiviceID(String enDiviceID){
		return enDiviceID.substring(diviceidkey.length(),enDiviceID.length());
	}
	
	public static Boolean isMydata(String mydataStr)
	{
		if(mydataStr.length() > mydatakeyword.length() 
				&& mydataStr.substring(0, mydatakeyword.length()).equals(mydatakeyword)){
			return true;
		}
		return false;
	}
	
	public static String desMydata(String mydataStr)
	{
		return mydataStr.substring(mydatakeyword.length(), mydataStr.length());
	}
	
	public static String enMydata(String mydataStr)
	{
		return mydatakeyword + mydataStr;
	}	
	
	
	
    public static String Pad2Solid100Size(StringBuilder sb)
    {
		while (true) {
			if (sb.length() > 100)
				break;
			else
				sb.append(" ");
		}
		return sb.toString();
    }
    
    public static String HexAdd(String s,int i)
	{
		return Integer.toHexString(Integer.parseInt(s,16) + i);
	}
    
    public static String getLocalIpAddress() {  
        try {  
            for (Enumeration<NetworkInterface> en = NetworkInterface  
                    .getNetworkInterfaces(); en.hasMoreElements();) {  
                NetworkInterface intf = en.nextElement();  
                for (Enumeration<InetAddress> enumIpAddr = intf  
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {  
                    InetAddress inetAddress = enumIpAddr.nextElement();  
//                    if (!inetAddress.isLoopbackAddress()) {  
//                        return inetAddress.getHostAddress().toString();  
//                    }
                    if (inetAddress.toString().contains("192.168"))
                    {
                    	return inetAddress.getHostAddress().toString();  
                    }
                }  
            }  
        } catch (SocketException ex) {  
            Log.e("WifiPreference IpAddress", ex.toString());  
        }  
        return null;  
    }
    
    public static ArrayList<Uri> Array2Generic(String[] StrArr)
    {
    	if (StrArr == null) return null;
    	ArrayList<Uri> AL = new ArrayList<Uri>();
    	for (String Str : StrArr) {
    		AL.add(Uri.parse(Str));			
		}
    	return AL;
    }
    
    /**
	 * 是否有sdcard的权限， 判断SD卡是否挂载(nw)
	 */
	public static boolean externalStorageState() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
	
	
	public static boolean makeDir(String file){
		try{
			File testFileFile = new File(file);
			if (!testFileFile.exists()) {
				testFileFile.mkdir();
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	 /**
     * 读取SD卡中文本文件
     * 
     * @param fileName
     * @return
     */ 
	public static String readSDFile(String f) {
		StringBuffer sb = new StringBuffer();
		File file = new File(f);
		if(isTextType(file)){
			try {
				FileInputStream fis = new FileInputStream(file);
				int c;
				while ((c = fis.read()) != -1) {
					sb.append((char) c);
				}
				fis.close();
	
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();

	}
	
	/**
	 * 判断是不是文本文件
	 * @param file
	 * @return
	 */
	private static boolean isTextType(File file) {
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return false;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return false;
		if (end.equals(".txt"))
			return true;
		return false;
	}
	
	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * @param file
	 */
	private static String getMIMEType(File file){
	    String type="*/*";
	    String fName=file.getName();
	    //获取后缀名前的分隔符"."在fName中的位置。
	    int dotIndex = fName.lastIndexOf(".");
	    if(dotIndex < 0){
	        return type;
	    }
	    /* 获取文件的后缀名 */
	    String end=fName.substring(dotIndex,fName.length()).toLowerCase();
	    if(end=="")return type;
	    //在MIME和文件类型的匹配表中找到对应的MIME类型。
	    for(int i=0;i<MIME_MapTable.length;i++){
	        if(end.equals(MIME_MapTable[i][0]))
	            type = MIME_MapTable[i][1];
	    }
	    return type;
	}
	
	//建立一个MIME类型与文件后缀名的匹配表
	private static final String[][] MIME_MapTable={
	    //{后缀名，    MIME类型}
	    {".3gp",    "video/3gpp"},
	    {".apk",    "application/vnd.android.package-archive"},
	    {".asf",    "video/x-ms-asf"},
	    {".avi",    "video/x-msvideo"},
	    {".bin",    "application/octet-stream"},
	    {".bmp",      "image/bmp"},
	    {".c",        "text/plain"},
	    {".class",    "application/octet-stream"},
	    {".conf",    "text/plain"},
	    {".cpp",    "text/plain"},
	    {".doc",    "application/msword"},
	    {".exe",    "application/octet-stream"},
	    {".gif",    "image/gif"},
	    {".gtar",    "application/x-gtar"},
	    {".gz",        "application/x-gzip"},
	    {".h",        "text/plain"},
	    {".htm",    "text/html"},
	    {".html",    "text/html"},
	    {".jar",    "application/java-archive"},
	    {".java",    "text/plain"},
	    {".jpeg",    "image/jpeg"},
	    {".jpg",    "image/jpeg"},
	    {".js",        "application/x-javascript"},
	    {".log",    "text/plain"},
	    {".m3u",    "audio/x-mpegurl"},
	    {".m4a",    "audio/mp4a-latm"},
	    {".m4b",    "audio/mp4a-latm"},
	    {".m4p",    "audio/mp4a-latm"},
	    {".m4u",    "video/vnd.mpegurl"},
	    {".m4v",    "video/x-m4v"},    
	    {".mov",    "video/quicktime"},
	    {".mp2",    "audio/x-mpeg"},
	    {".mp3",    "audio/x-mpeg"},
	    {".mp4",    "video/mp4"},
	    {".mpc",    "application/vnd.mpohun.certificate"},        
	    {".mpe",    "video/mpeg"},    
	    {".mpeg",    "video/mpeg"},    
	    {".mpg",    "video/mpeg"},    
	    {".mpg4",    "video/mp4"},    
	    {".mpga",    "audio/mpeg"},
	    {".msg",    "application/vnd.ms-outlook"},
	    {".ogg",    "audio/ogg"},
	    {".pdf",    "application/pdf"},
	    {".png",    "image/png"},
	    {".pps",    "application/vnd.ms-powerpoint"},
	    {".ppt",    "application/vnd.ms-powerpoint"},
	    {".prop",    "text/plain"},
	    {".rar",    "application/x-rar-compressed"},
	    {".rc",        "text/plain"},
	    {".rmvb",    "audio/x-pn-realaudio"},
	    {".rtf",    "application/rtf"},
	    {".sh",        "text/plain"},
	    {".tar",    "application/x-tar"},    
	    {".tgz",    "application/x-compressed"}, 
	    {".txt",    "text/plain"},
	    {".wav",    "audio/x-wav"},
	    {".wma",    "audio/x-ms-wma"},
	    {".wmv",    "audio/x-ms-wmv"},
	    {".wps",    "application/vnd.ms-works"},
	    //{".xml",    "text/xml"},
	    {".xml",    "text/plain"},
	    {".z",        "application/x-compress"},
	    {".zip",    "application/zip"},
	    {"",        "*/*"}    
	};
	
	public static void initLotteryDeviceID(Context context){
		if(externalStorageState()){
			String fileName = "DeviceIDTestFile.txt";
			writeFileSdcard(Constant.LOTTERY_DIR+File.separator + fileName,
					context.getResources().getString(R.string.nfc_lottery_test_data));
		}
	}
	
	public static void writeFileSdcard(String fileName, String message) {

		try {

			// FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
			
			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] bytes = message.getBytes();

			fout.write(bytes);

			fout.close();

		}

		catch (Exception e) {

			e.printStackTrace();

		}

	}
	
	public static void toggleWIFIP2P(Context context, Boolean b) {
        WifiP2pManager manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        Channel channel = manager.initialize(context, context.getMainLooper(), null);
		try {
			Method localMethod = manager.getClass().getMethod("enableP2p",new Class[]{Channel.class});
			try {
				localMethod.invoke(manager, new Object[]{channel});
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static String int2Ip(int ip)
    {
    	return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
    }
	private static Activity nc = null;
    public static void setActivity(Activity namecardactivity){
    	nc = namecardactivity;
    }
    public static Activity getActivity(){
    	return nc;
    }
}
