package cn.yjt.oa.app.nfctools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.TelephonyManager;

public class APNTools {

	private static void setDataConnection(boolean isEnable, Context context)
			throws ClassNotFoundException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method dataConnSwitchmethod;
		Object ITelephonyStub;

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		Method getITelephonyMethod = TelephonyManager.class
				.getDeclaredMethod("getITelephony");
		getITelephonyMethod.setAccessible(true);
		ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);

		if (isEnable) {
			dataConnSwitchmethod = ITelephonyStub.getClass().getDeclaredMethod(
					"enableDataConnectivity");
		} else {
			dataConnSwitchmethod = ITelephonyStub.getClass().getDeclaredMethod(
					"disableDataConnectivity");
		}
		dataConnSwitchmethod.setAccessible(true);
		dataConnSwitchmethod.invoke(ITelephonyStub);
	}

	protected static boolean isEnabled(Context context) {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			int state = telephonyManager.getDataState();
			if (state == TelephonyManager.DATA_CONNECTED
					|| state == TelephonyManager.DATA_CONNECTING
					|| state == TelephonyManager.DATA_SUSPENDED) {
				return true;
			} else {
				return false;
			}
		} else {
			//20150215
//			ConnectivityManager connMan = (ConnectivityManager) context
//					.getSystemService(Context.CONNECTIVITY_SERVICE);
//			return connMan.getMobileDataEnabled();
			return getMobileDataStatus(context);
		}

	}

	public static void openDataConnection(Context context) {
		if (!isEnabled(context)) {
			toggleState(context);
		}

	}

	public static void closeDataConnection(Context context) {
		if (isEnabled(context)) {
			toggleState(context);
		}
	}

	public static void toggleState(Context context) {
		try {
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
				if (isEnabled(context)) {
					setDataConnection(false, context);
				} else {
					setDataConnection(true, context);
				}
			} else {
				//20150215
//				ConnectivityManager connMan = (ConnectivityManager) context
//						.getSystemService(Context.CONNECTIVITY_SERVICE);
//				boolean isEnabled = connMan.getMobileDataEnabled();
//				if (isEnabled) {
//					connMan.setMobileDataEnabled(false);
//				} else {
//					connMan.setMobileDataEnabled(true);
//				}
				boolean isEnabled = getMobileDataStatus(context);
				if (isEnabled) {
					toggleMobileData(context, false);
				} else {
					toggleMobileData(context, true);
				}

			}
			
		} catch (Exception e) {
		}
		
	}
	
	
	
	
	
	//20150215
	private static boolean getMobileDataStatus(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		Class<?> conMgrClass = null; // ConnectivityManager类
		Field iConMgrField = null; // ConnectivityManager类中的字段
		Object iConMgr = null; // IConnectivityManager类的引用
		Class<?> iConMgrClass = null; // IConnectivityManager类
		Method getMobileDataEnabledMethod = null; // setMobileDataEnabled方法

		try {
			// 取得ConnectivityManager类
			conMgrClass = Class.forName(conMgr.getClass().getName());
			// 取得ConnectivityManager类中的对象mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			iConMgr = iConMgrField.get(conMgr);
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			// 取得IConnectivityManager类中的getMobileDataEnabled(boolean)方法
			getMobileDataEnabledMethod = iConMgrClass
					.getDeclaredMethod("getMobileDataEnabled");
			// 设置getMobileDataEnabled方法可访问
			getMobileDataEnabledMethod.setAccessible(true);
			// 调用getMobileDataEnabled方法
			return (Boolean) getMobileDataEnabledMethod.invoke(iConMgr);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 移动网络开关
	 */
	private static void toggleMobileData(Context context, boolean enabled) {
		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		Class<?> conMgrClass = null; // ConnectivityManager类
		Field iConMgrField = null; // ConnectivityManager类中的字段
		Object iConMgr = null; // IConnectivityManager类的引用
		Class<?> iConMgrClass = null; // IConnectivityManager类
		Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法

		try {
			// 取得ConnectivityManager类
			conMgrClass = Class.forName(conMgr.getClass().getName());
			// 取得ConnectivityManager类中的对象mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			iConMgr = iConMgrField.get(conMgr);
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
					"setMobileDataEnabled", Boolean.TYPE);
			// 设置setMobileDataEnabled方法可访问
			setMobileDataEnabledMethod.setAccessible(true);
			// 调用setMobileDataEnabled方法
			setMobileDataEnabledMethod.invoke(iConMgr, enabled);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
