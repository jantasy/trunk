
package com.telecompp.util;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 基本数据存取功能接口
 * @author xiongdazhuang
 *
 */ 
public class DataSaveManager implements SumaConstants
{
	private static final String SHARE_PREFERENCES_NAME = "TelecomPOS";

	public static final String SHARE_PREFERENCES_NAME_IMEI = "SharePreferenceIMEI";
	public static final String SHARE_PREFERENCES_NAME_CSN = "SharePreferenceCSN";
	public static final String SHARE_PREFERENCES_CACHE_FILES = "SharePreferenceCacheFiles";
	
	public static final String SHARE_REGISTER_INFO = "share_register_info";
	public static final String SHARE_LOGIN_INFO = "share_login_info";

	/**
	 * 实现应用参数保存，一组String值
	 * 
	 * @param context
	 * @param name 保存的name
	 * @param value 保存的值 
	 * @throws Exception
	 */
	public static void setPrefenceStringSet(Context context, String name, Set<String> value) throws Exception
	{
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putStringSet(name, value);
		editor.commit();
	}

	/**
	 * 实现应用参数提取，一组String值
	 * 
	 * @param context
	 * @param name 输入的name值
	 * @return 获取name对应的保存的值
	 * @throws Exception
	 */
	public static String[] getPreferenceStringSet(Context context, String name) throws Exception
	{
		Set<String> valueSet = null;
		String[] valueString = null;

		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
		valueSet = preferences.getStringSet(name, null);
		if (valueSet != null)
		{
			valueString = (String[]) valueSet.toArray(new String[0]);
		}

		return valueString;
	}

	/**
	 * 实现应用参数保存，String值
	 * 
	 * @param context 
	 * @param name 输入的name值
	 * @param value
	 * @throws Exception
	 */
	public static void setPrefenceString(Context context, String name, String value) throws Exception
	{
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(name, value);
		editor.commit();
	}

	/**
	 * 实现应用参数提取，String值
	 * 
	 * @param context
	 * @param name 输入的name值
	 * @return
	 * @throws Exception
	 */
	public static String getPreferenceString(Context context, String name) throws Exception
	{
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
		String valueString = preferences.getString(name, "");
		return valueString;
	}

	/**
	 * 实现应用参数保存，int值
	 * 
	 * @param context
	 * @param name 输入的name值
	 * @param value
	 * @throws Exception
	 */
	public static void setPrefenceInt(Context context, String name, int value) throws Exception
	{
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt(name, value);
		editor.commit();
	}

	/**
	 * 实现应用参数提取,int值
	 * 
	 * @param context
	 * @param name 输入的name值
	 * @return
	 * @throws Exception
	 */
	public static int getPreferenceInt(Context context, String name) throws Exception
	{
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
		int value = preferences.getInt(name, 0);
		return value;
	}

	/**
	 * 实现应用参数保存，boolean值
	 * 
	 * @param context
	 * @param name 输入的name值
	 * @param value
	 * @throws Exception
	 */
	public static void setPrefenceBoolean(Context context, String name, Boolean value) throws Exception
	{
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(name, value);
		editor.commit();
	}

	/**
	 * 实现应用参数提取,boolean值
	 * 
	 * @param context
	 * @param name 输入的name值
	 * @return
	 * @throws Exception
	 */
	public static boolean getPreferenceBoolean(Context context, String name) throws Exception
	{
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
		boolean value = preferences.getBoolean(name, false);
		return value;
	}
}