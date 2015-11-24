package com.telecompp.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;


/**
 * 终端设备信息管理
 * 
 * @author xiongdazhuang
 *  
 */
public class DeviceInfoManager
{
	/**
	 * 获取终端型号
	 * 
	 * @return 终端型号
	 */
	static public String getDeviceInfo()
	{
		// Build bd = new Build();
		String model = Build.MODEL;
		return model;
	}

	/**
	 * 获取当前应用的版本名称
	 * 
	 * @param context
	 * @return 版本名称
	 */
	static public String getVersionName(Context context)
	{
		try
		{
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			String version = info.versionName;
			return version;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return 当前应用的版本号
	 */
	static public String getVersionCode(Context context)
	{
		try
		{
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			String version = (info.versionCode + "");
			return version;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取ICCID
	 * 
	 * @param context
	 * @return 获取到的ICCID
	 */
	static public String getCardICCID(Context context)
	{
		String ICCID = null;
		try
		{
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
			ICCID = tm.getSimSerialNumber().toUpperCase(); // 取出ICCID
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (ICCID == null)
		{
			return "";
		}

		return ICCID;
	}

	/**
	 * 获取IMEI
	 * 
	 * @param context
	 * @return 获取到的IMEI
	 */
	static public String getIMEI(Context context)
	{
		String IMEI = null;
		try
		{
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
			IMEI = tm.getDeviceId().toUpperCase(); // 取出IMEI
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (IMEI == null)
		{
			return "";
		}

		return IMEI;
	}

	/**
	 * 获取电话号码
	 * 
	 * @param context
	 * @return 获取到的电话号码
	 */
	static public String getTel(Context context)
	{
		String Tel = null;
		try
		{
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);

			Tel = tm.getLine1Number().toUpperCase(); // 取出Tel
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (Tel == null)
		{
			return "";
		}

		return Tel;
	}
}
