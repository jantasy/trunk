package cn.yjt.oa.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/** 
 * 测试ConnectivityManager 
 * ConnectivityManager主要管理和网络连接相关的操作 
 * 相关的TelephonyManager则管理和手机、运营商等的相关信息；WifiManager则管理和wifi相关的信息。 
 * 想访问网络状态，首先得添加权限<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>  
 * NetworkInfo类包含了对wifi和mobile两种网络模式连接的详细描述,通过其getState()方法获取的State对象则代表着 
 * 连接成功与否等状态。 
 *  
 */
public class CheckNetUtils {
	
	/**
	 * 检查网络是否有链接
	 * @param context 上下文
	 * @return 网络链接返回true，否则返回false
	 */
	public static boolean hasNetWork(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取代表联网状态的NetWorkInfo对象  
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		} else {
			return false;
		}
	}
}
