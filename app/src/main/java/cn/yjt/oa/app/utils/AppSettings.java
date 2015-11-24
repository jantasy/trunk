package cn.yjt.oa.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import cn.yjt.oa.app.MainApplication;


public class AppSettings {
	
	public static final int INCOME_MODE_CLOSE = 0;
	public static final int INCOME_MODE_SIMPLE = 1;
	public static final int INCOME_MODE_COMPLEX = 2;
	
	private static final String KEY_INCOME_MODE = "income_mode";
	
	static void set(String key,boolean value){
		getSettings().edit().putBoolean(key, value).commit();
	}
	
	private static void set(String key,int value){
		getSettings().edit().putInt(key, value).commit();
	}
	
	private static SharedPreferences getSettings(){
		return MainApplication.getAppContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE);
	}

	public static void setIncomeAlertMode(int mode){
		set(KEY_INCOME_MODE, mode);
	}
	
	public static int getIncomeAlertMode(){
		return getSettings().getInt(KEY_INCOME_MODE, INCOME_MODE_SIMPLE);
	}
	
}
