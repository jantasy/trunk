package cn.yjt.oa.app.location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.app.utils.LogUtils;


public class BaseLocationServer {
//
//	private static final String SERVICE_ACTION = "com.ct.bri.wifi.service.ICtbriWifiLocService";
//
//	private static CTLocationService locationService;
//	public static final long LOCATION_OVERTIME = 3;
//	public static boolean isFinish;
//	private static List<MULLocationListener> locationListenerList = new ArrayList<MULLocationListener>();
//	private static MULLocationListener locationlistener = new MULLocationListener() {
//
//		@Override
//		public void onResponse(MULLocation arg0) {
//			isFinish = true;
//
//			synchronized (locationListenerList) {
//
//				Iterator<MULLocationListener> iterator = locationListenerList
//						.iterator();
//				while (iterator.hasNext()) {
//					MULLocationListener locationListener = iterator.next();
//					locationListener.onResponse(arg0);
//					iterator.remove();
//				}
//			}
//			Intent intent = new Intent(SERVICE_ACTION);
//			boolean stopService = MainApplication.getAppContext().stopService(
//					intent);
//			System.out.println("stop CTLocationService : " + stopService);
//
//		}
//	};
//
//	private static Handler sHandler;
//
//	public static void init(Context context) {
//		locationService = new CTLocationService(context);
//		isFinish = true;
//		sHandler = new Handler(Looper.getMainLooper());
//	}
//
//	public static void startLocation(MULLocationListener listener) {
//		if (locationService == null) {
//			init(MainApplication.getAppContext());
//		}
//		synchronized (locationListenerList) {
//			locationListenerList.add(listener);
//		}
//
//		if (isFinish) {
//			isFinish = false;
//			locationService.registerLocationListener(locationlistener);
//			if (Looper.getMainLooper().equals(Looper.myLooper())) {
//				LogUtils.i("==main looper");
//				locationService.getLocation(LOCATION_OVERTIME, true,
//						CTLocationService.PROFESSION_PROVIDER);
//			} else {
//				LogUtils.i("not == main looper");
//				sHandler.post(new Runnable() {
//
//					@Override
//					public void run() {
//						locationService.getLocation(LOCATION_OVERTIME, true,
//								CTLocationService.PROFESSION_PROVIDER);
//					}
//				});
//			}
//		}
//
//	}

}
