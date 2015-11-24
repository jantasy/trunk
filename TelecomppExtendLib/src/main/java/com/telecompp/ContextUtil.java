package com.telecompp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 自定义应用程序的Application对象 添加全局异常捕获功能,和acitivty跳转的时候提供临时存储
 * 
 * @author poet
 * 
 */
public class ContextUtil{
	private static Context instance;
	
	//	城市代码
	public static final String CITYCODE = "999888";
	
	public static void setInstance(Context instance) {
		ContextUtil.instance = instance.getApplicationContext();
	}
	
	private Object obj;
	public static final String CRetry = "CRetry";
	private Map<String, Object> map = new HashMap<String, Object>();
	public static boolean needCheck = false;// 是否要进行登录
	public static boolean isFirstStart = true;
	
	
	// 获取电话号码的接口
	private static GetYjtPhoneNum _getYjtPhoneNum;
	
	public static void setGetYjtPhoneNum(GetYjtPhoneNum getYjtPhoneNum) {
		_getYjtPhoneNum = getYjtPhoneNum;
	}
	
	public static String getPhoneNum() {
		if(_getYjtPhoneNum != null) {
			return _getYjtPhoneNum.getYjtPhoneNum();
		} else {
			return "";
		}
	}

	public void setValue(String key, Object value) {
		this.map.put(key, value);
	}

	public Object getValue(String key) {
		return map.get(key);
	}

	public Object removeValue(String key) {
		return map.remove(key);
	}

	// wwyue
	public static ArrayList<Map<String, String>> tradeRecords;

	public static Context getInstance() {
		return instance;
	}
	
	public void setObj(Object obj) {
		this.obj = obj;
	}

	public Object getObj() {
		Object temp = this.obj;
		// this.obj = null;
		return temp;
	}
	
	/**
	 * 生成手机的唯一标识 生成算法：2B和2C+优先使用IMEI如果IMEI为空则使用BLUE+WLAN
	 * 
	 * @return
	 */
	public static String getCSN() {
		String temp = null;
		StringBuilder sb = new StringBuilder();
		sb.append("Y");//扩展支付
		temp = getCardICCID();
		if(!TextUtils.isEmpty(temp)) {
			return sb.append(temp.subSequence(1, 20).toString()).toString();
		}
		temp = getIMEI(instance);
		if (temp != null)
			sb.append(temp);
		temp = getBluetoothMACAddress(instance);
		if (temp != null)
			sb.append(temp);
		temp = getWLANMACAddress(instance);
		if (temp != null)
			sb.append(temp);
		temp = getAndroidID(instance);
		if (temp != null)
			sb.append(temp);
		if (sb.length() < 21) {
			return null;
		} else {
			return sb.subSequence(0, 20).toString().replace(":", "Q");
		}
	}
	
	
	public static String getCardICCID() {
		String ICCID = null;
		try {
			TelephonyManager tm = (TelephonyManager) instance
					.getSystemService(Activity.TELEPHONY_SERVICE);
			
			ICCID = tm.getSimSerialNumber().toUpperCase(); // 取出ICCID
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ICCID == null)
		{
			return "";
		}

		return ICCID;
	}

	public static String getAndroidID(Context context) {
		String androidID = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		return androidID;
	}

	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public static String getSimSerialNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimSerialNumber();
	}

	/**
	 * 052. 得到设备唯一识别码 053.
	 * 
	 * 054.
	 * 
	 * @param context
	 *            055.
	 * @return 056.
	 */
	public static String getUniqueNumber(Context context) {
		try {
			String androidID = getAndroidID(context);
			String imei = getIMEI(context);
			String simSerialNumber = getSimSerialNumber(context);
			UUID uuid = new UUID(androidID.hashCode(),
					((long) imei.hashCode() << 32) | simSerialNumber.hashCode());
			return uuid.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 067. 获取设备本身网卡的MAC地址 068.
	 * 
	 * 069.
	 * 
	 * @param context
	 *            070.
	 * @return 071.
	 */
	public static String getWLANMACAddress(Context context) {
		String macAddress = "";
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wm.getConnectionInfo();
		if (info != null) {
			macAddress = info.getMacAddress();
		} else {
			macAddress = "No Wifi Device";
		}
		return macAddress;
	}

	/**
	 * 086. 获取蓝牙MAC地址 087.
	 * 
	 * 088.
	 * 
	 * @param context
	 *            089.
	 * @return 090.
	 */
	public static String getBluetoothMACAddress(Context context) {
		String btMacAddress = "";
		BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
		if (ba != null) {
				btMacAddress = ba.getAddress();
		} else {
			btMacAddress = null;
		}
		return btMacAddress;
	}
	
	/*public void post2UiThread(Runnable r) {
		Handler handler = new Handler(getMainLooper());
		handler.post(r);
		handler = null;
	}*/
}