package cn.yjt.oa.app.app.utils;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageUtils {
	public static final String PREFS_NAME_STATE = "AppDownloadState";
	public static final String PREFS_NAME_POSITION = "AppDownloadPosition";
	public static final String SYSTEM_SETTINGS = "SystemSettings";


	
	public static void saveAppStateToFile(Context context,String pkg,int state){
		SharedPreferences sp = context.getSharedPreferences(PREFS_NAME_STATE,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(pkg, state);
		editor.commit();
	}
	
	public static void saveAppPositionToFile(Context context,String pkg,int position){
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREFS_NAME_POSITION,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(pkg, position);
		editor.commit();
	}
	
	public static int getAppStateFromFile(Context context,String pkg){
		SharedPreferences sp = context.getSharedPreferences(PREFS_NAME_STATE,Context.MODE_PRIVATE);
		return sp.getInt(pkg, 0);
	}
	
	public static int getAppPositionFromFile(Context context,String pkg){
		SharedPreferences sp = context.getSharedPreferences(PREFS_NAME_POSITION,Context.MODE_PRIVATE);
		return sp.getInt(pkg, 0);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Integer> getAppInfos(Context context){
		LogUtils.i(context);
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREFS_NAME_POSITION, Context.MODE_PRIVATE);
		LogUtils.i(sp);
		return (Map<String, Integer>) sp.getAll();
	}
	
	public static void clear(Context context){
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREFS_NAME_POSITION, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}
	
	public static void remove(Context context,String pkg){
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREFS_NAME_POSITION, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(pkg);
		editor.commit();
	}
	
	public static SharedPreferences getSystemSettings(Context context){
		SharedPreferences sp = context.getSharedPreferences(SYSTEM_SETTINGS, Context.MODE_PRIVATE);
		return sp;
	}
	
	public static void resetSystemSettings(Context context){
		getSystemSettings(context).edit().clear().commit();
	}
	
	private static final String DASHBOARD_PROMT_STATE = "dashboard_promt_state";
	public static void saveDashboardPromtState(Context context){
		SharedPreferences sp = context.getSharedPreferences(DASHBOARD_PROMT_STATE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("isFirstForPromt", false);
		editor.commit();
	}
	public static boolean isFirstForPromt(Context context){
		SharedPreferences sp = context.getSharedPreferences(DASHBOARD_PROMT_STATE, Context.MODE_PRIVATE);
		return sp.getBoolean("isFirstForPromt", true);
	}
	
	public static void saveUserLastUpdateTime(Context context,String pkg,int state){
		SharedPreferences sp = context.getSharedPreferences(PREFS_NAME_STATE,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(pkg, state);
		editor.commit();
	}
}
