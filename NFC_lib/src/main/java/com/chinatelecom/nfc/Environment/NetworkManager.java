package com.chinatelecom.nfc.Environment;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

public class NetworkManager {
	public final static int E_WIFI = 1;
	public final static int E_GPRS = 2;
	public final static int E_AIRPLANEMODE = 3;
	
	private Context context;
	private ConnectivityManager connManager;
	private WifiManager mWifiManager;
	private AudioManager mAudioManager;

	public NetworkManager(Context context) {
		this.context = context;
		connManager = (ConnectivityManager) this.context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}

	/**
	 * @return 网络是否连接可用
	 */
	public boolean isNetworkConnected() {

		NetworkInfo networkinfo = connManager.getActiveNetworkInfo();

		if (networkinfo != null) {
			return networkinfo.isConnected();
		}

		return false;
	}

	/**
	 * @return wifi是否连接可用
	 */
	public boolean isWifiConnected() {

		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi != null) {
			return mWifi.isConnected();
		}

		return false;
	}

	/**
	 * 当wifi不能访问网络时，mobile才会起作用
	 * @return GPRS是否连接可用
	 */
	public boolean isMobileConnected() {

		NetworkInfo mMobile = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mMobile != null) {
			return mMobile.isConnected();
		}
		return false;
	}

	/**
	 * GPRS网络开关 反射ConnectivityManager中hide的方法setMobileDataEnabled 可以开启和关闭GPRS网络
	 * 
	 * @param isEnable
	 * @throws Exception
	 */
	public void toggleGprs(boolean isEnable) throws Exception {
		Class<?> cmClass = connManager.getClass();
		Class<?>[] argClasses = new Class[1];
		argClasses[0] = boolean.class;

		// 反射ConnectivityManager中hide的方法setMobileDataEnabled，可以开启和关闭GPRS网络
		Method method = cmClass.getMethod("setMobileDataEnabled", argClasses);
		method.invoke(connManager, isEnable);
	}

	/**
	 * WIFI网络开关
	 * 
	 * @param enabled
	 * @return 设置是否success
	 */
	public boolean toggleWiFi(boolean enabled) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wm.setWifiEnabled(enabled);

	}
	
    /**
     * 
     * @return 是否处于飞行模式
     */
    public boolean isAirplaneModeOn() {  
        // 返回值是1时表示处于飞行模式  
        int modeIdx = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);  
        boolean isEnabled = (modeIdx == 1);
        return isEnabled;
    }  
    /**
     * 飞行模式开关
     * @param setAirPlane
     */
    public void toggleAirplaneMode(boolean setAirPlane) {  
        Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, setAirPlane ? 1 : 0);  
        // 广播飞行模式信号的改变，让相应的程序可以处理。  
        // 不发送广播时，在非飞行模式下，Android 2.2.1上测试关闭了Wifi,不关闭正常的通话网络(如GMS/GPRS等)。  
        // 不发送广播时，在飞行模式下，Android 2.2.1上测试无法关闭飞行模式。  
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);  
        // intent.putExtra("Sponsor", "Sodino");  
        // 2.3及以后，需设置此状态，否则会一直处于与运营商断连的情况  
        intent.putExtra("state", setAirPlane);  
        context.sendBroadcast(intent);  
    }
    
//    public void toggleEnvironment(Environments environment){
//    			switch (i) {
//    			case E_WIFI:
//    				togg
//    				break;
//    				
//    			case E_GPRS:
//    				
//    				break;
//    				
//    			case E_AIRPLANEMODE:
//    				
//    				break;
//
//    			default:
//    				break;
//    			}
//    }
    
    
    /**
     * WIFI、振动、静音、2G/3G模块、飞行模式、蓝牙。开、关、读取开关状态。
     */
    
	/**
	 * 判断WIFI状态
	 */
	public boolean isWIFI() {
		return mWifiManager.isWifiEnabled();
	}

	/**
	 * 关闭WIFI
	 */
	public void closeWIFI() {
		if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
				|| mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING
				|| mWifiManager.getWifiState() == WifiManager.WIFI_STATE_UNKNOWN) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * 开启WIFI
	 */
	public void openWIFI() {
		if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED
				|| mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING
				|| mWifiManager.getWifiState() == WifiManager.WIFI_STATE_UNKNOWN) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	/********************************************************************/

	/**
	 * 获取当前的AudioManager的模式 ->静音模式：0 震动模式：1 正常模式：2
	 */
	public int getAudioManagerMode() {
		int strRingerMode = 1;
		if (mAudioManager != null) {
			strRingerMode = mAudioManager.getRingerMode();
		}
		return strRingerMode;
	}

	/**
	 * 开启震动模式 需要设置权限 <uses-permission android:name="andorid.permission.VIBRATE"
	 * />
	 */
	public void openModeVibrate() {
		if (mAudioManager != null) {
			mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		}
	}

	/**
	 * 关闭震动模式
	 */
	public void closeModeVibrate() {
		if (mAudioManager != null) {
			mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}
	}

	/**
	 * 开启静音模式
	 */
	public void openSilentMode() {
		if (mAudioManager != null) {
			mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		}
	}

	/**
	 * 关闭静音模式
	 */
	public void closeSilentMode() {
		if (mAudioManager != null) {
			mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}
	}

	/**********************************************************************/

	/**
	 * 检测GPRS是否已经打开
	 */
	public boolean gprsIsOpenMethod(String methodName) {
		ConnectivityManager mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		Class cmClass = mCM.getClass();
		Class[] argClasses = null;
		Object[] argObject = null;
		Boolean isOpen = false;

		try {
			// 获取方法
			Method mMethod = cmClass.getMethod(methodName, argClasses);
			// 执行方法
			isOpen = (Boolean) mMethod.invoke(mCM, argObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOpen;
	}

	/**
	 * 开启/关闭GPRS
	 */
	public void setGprsEnabled(String methodName, boolean isEnable) {
		ConnectivityManager mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		Class cmClass = mCM.getClass();
		Class[] argClasses = new Class[1];
		argClasses[0] = boolean.class;

		try {
			Method method = cmClass.getMethod(methodName, argClasses);
			method.invoke(mCM, isEnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打开/关闭GPRS 假设打开：传入参数true
	 */
	public boolean gprsEnabled(boolean bEnable) {
		// 判断当前的GPRS是否打开，假设为true
		boolean isOpen = gprsIsOpenMethod("getMobileDataEnabled");
		// 只有在状态不相等的时候，再改变设置GPRS
		if (isOpen == !bEnable) {
			setGprsEnabled("setMobileDataEnabled", bEnable);
		}
		return isOpen;
	}

	/*****************************************************************************************************/

	/**
	 * 判断飞行模式
	 */
	private boolean isAirplaneMode() {
		// 返回值是1时表示处于飞行模式
		int modeIdx = Settings.System.getInt(
				context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
		boolean isEnabled = (modeIdx == 1);
		return isEnabled;
	}

	/**
	 * 开启/关闭飞行模式
	 */
	private void setAirplaneMode(boolean setAirPlane) {
		// 设置开启或关闭飞行模式
		Settings.System.putInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, setAirPlane ? 1 : 0);
		// 广播飞行模式信号的改变，让相应的程序可以处理。
		// 不发送广播时，在非飞行模式下，Android 2.2.1上测试关闭了Wifi,不关闭正常的通话网络(如GMS/GPRS等)。
		// 不发送广播时，在飞行模式下，Android 2.2.1上测试无法关闭飞行模式。
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		// intent.putExtra("Sponsor", "Sodino");
		// 2.3及以后，需设置此状态，否则会一直处于与运营商断连的情况
		intent.putExtra("state", setAirPlane);
		context.sendBroadcast(intent);
//		Toast toast = Toast.makeText(this, "飞行模式启动与关闭需要一定的时间，请耐心等待",Toast.LENGTH_LONG);
//		toast.show();
	}
}

