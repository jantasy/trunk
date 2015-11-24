package com.chinatelecom.nfc.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

	public final static String sp_environment = "environment";
	public final static String sp_app = "application";
	public final static String sp_app_firsttime = "firsttime";
//	public final static String sp_lottery_name = "lottery";
//	public final static String sp_lottery_testdata = "lotterytestdata";
	
	/**
	 *  存环境数据
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveEnvironment(Context context, String key, String value) {
		context.getSharedPreferences(sp_environment,Context.MODE_PRIVATE).edit().putString(key, value).commit();
	}


	/**
	 * 取环境数据
	 * @param context
	 * @return
	 */
	public static SharedPreferences getEnvironment(Context context) {
		return context.getSharedPreferences(sp_environment,Context.MODE_PRIVATE);
	}
	
	public static void saveApp(Context context, boolean value) {
		context.getSharedPreferences(sp_app,Context.MODE_PRIVATE).edit().putBoolean(sp_app_firsttime, value).commit();
	}
	public static SharedPreferences getApp(Context context) {
		return context.getSharedPreferences(sp_app,Context.MODE_PRIVATE);
	}
	
//	public static void saveLotteryTestData(Context context, String value) {
//		context.getSharedPreferences(sp_lottery_name,Context.MODE_PRIVATE).edit().putString(sp_lottery_testdata, value).commit();
//	}
//	public static String getLotteryTestData(Context context) {
//		return context.getSharedPreferences(sp_lottery_name,Context.MODE_PRIVATE).getString(sp_lottery_testdata, "A0000039A091A1");
//	}
}
