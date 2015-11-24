package cn.yjt.oa.app.app.utils;

import android.util.Log;

public class LogUtils {
	private static boolean log = true;
	public static void i(Object msg){
		if(log){
			Log.i("LogUtils", "--"+msg);
		}
	}
	public static void i(Object tag,Object msg){
		if(log){
			Log.i(tag.getClass().getName(), "--"+msg);
		}
	}
	public static void i(String tag,Object msg){
		if(log){
			Log.i(tag, "--"+msg);
		}
	}
}
