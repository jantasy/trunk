package cn.yjt.oa.app.utils;

import android.util.Log;

/**
 * log工具类
 * <pre>
 * 鉴于打印log的时候发生异常的教训，在此将log类进行一次封装，
 * 过滤掉Tag和msg为空的情况，然后捕获其他可能的异常
 * <pre>
 * @author 熊岳岳
 *
 */
public class LogUtils {

	private final static String TAG = "LogUtils";

	/**封装Log.v*/
	public static void v(String tag, String msg) {
		if (tag != null && msg != null) {
			Log.v(tag, msg);
		} else {
			Log.e(TAG, "log打印发生tag或msg为空的异常");
		}
	}
	/**封装Log.d*/
	public static void d(String tag, String msg) {
		if (tag != null && msg != null) {
			Log.d(tag, msg);
		} else {
			Log.e(TAG, "log打印发生tag或msg为空的异常");
		}
	}
	/**封装Log.i*/
	public static void i(String tag, String msg) {
		if (tag != null && msg != null) {
			Log.i(tag, msg);
		} else {
			Log.e(TAG, "log打印发生tag或msg为空的异常");
		}
	}
	/**封装Log.w*/
	public static void w(String tag, String msg) {
		if (tag != null && msg != null) {
			Log.w(tag, msg);
		} else {
			Log.e(TAG, "log打印发生tag或msg为空的异常");
		}
	}
	/**封装Log.e*/
	public static void e(String tag, String msg) {
		if (tag != null && msg != null) {
			Log.e(tag, msg);
		} else {
			Log.e(TAG, "log打印发生tag或msg为空的异常");
		}
	}
}
