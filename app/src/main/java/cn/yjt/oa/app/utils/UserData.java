package cn.yjt.oa.app.utils;

import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.AttendanceUserTime;
import cn.yjt.oa.app.beans.BeaconInfo;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.http.GsonHolder;

import com.google.gson.Gson;

public final class UserData {

	private static final Gson gson = GsonHolder.getInstance().getGson();

	public static final String KEY_BEACONS = "beacons";
	public static final String KEY_ARE_BEACONS = "are_beacons";
	public static final String KEY_ATTENDANCE_USER_TIME = "attendance_user_time";

	public static final String KEY_AUTO_ATTENDANCE = "auto_attendance";

	public static final Type TYPE_BEACONS = new TypeToken<List<BeaconInfo>>() {
	}.getType();
	public static final Type TYPE_ATTENDANCE_TIMES = AttendanceUserTime.class;
	// xiong
	// public static final

	private static final Map<String, Object> DATA_CACHE = new HashMap<String, Object>();

	private static Object getValue(String key, Type type) {
		if (DATA_CACHE.containsKey(key)) {
			return DATA_CACHE.get(key);
		}
		String string = getUserPreferences().getString(key, "");
		Object object = gson.fromJson(string, type);
		if (object != null) {
			DATA_CACHE.put(key, object);
		}
		return object;
	}

	private static void putValue(String key, Object value) {
		Editor putString = getUserPreferences().edit().putString(key,
				gson.toJson(value));
		boolean commit = putString.commit();
		System.out.println("commit:" + commit);
		DATA_CACHE.put(key, value);
	}

	public static SharedPreferences getUserPreferences() {
		UserInfo userInfo = AccountManager.getCurrent(MainApplication
				.getAppContext());
		String userId = "none";
		if (userInfo != null) {
			userId = String.valueOf(userInfo.getId());
		}
		SharedPreferences sharedPreferences = MainApplication.getAppContext()
				.getSharedPreferences("userdata_" + userId,
						Context.MODE_PRIVATE);
		System.out.println(sharedPreferences);
		return sharedPreferences;
	}

	@SuppressWarnings("unchecked")
	public static List<BeaconInfo> getFavorateUserAreaBeaconInfos() {
		return (List<BeaconInfo>) getValue(KEY_BEACONS, TYPE_BEACONS);
	}

	public static void setFavorateUserAreaBeaconInfos(
			List<BeaconInfo> beaconInfos) {
		putValue(KEY_BEACONS, beaconInfos);
	}

	@SuppressWarnings("unchecked")
	public static List<BeaconInfo> getUserAreaBeaconInfos() {
		return (List<BeaconInfo>) getValue(KEY_ARE_BEACONS, TYPE_BEACONS);
	}

	public static void setUserAreaBeaconInfos(List<BeaconInfo> beaconInfos) {
		System.out.println("setUserAreaBeaconInfos:" + beaconInfos);
		putValue(KEY_ARE_BEACONS, beaconInfos);
	}

	public static AttendanceUserTime getAttendanceUserTime() {
		AttendanceUserTime value = (AttendanceUserTime) getValue(
				KEY_ATTENDANCE_USER_TIME, TYPE_ATTENDANCE_TIMES);
		System.out.println("getAttendanceUserTime:" + value);
		return value;
	}

	public static void setAttendanceUserTime(
			AttendanceUserTime attendanceUserTime) {
		System.out.println(Thread.currentThread().getName()
				+ " setAttendanceUserTime:" + attendanceUserTime);
		putValue(KEY_ATTENDANCE_USER_TIME, attendanceUserTime);
	}

	public static void setAutoAttendance(boolean enable) {
		getUserPreferences().edit().putBoolean(KEY_AUTO_ATTENDANCE, enable)
				.commit();
	}

	public static boolean getAutoAttendance() {
		return getUserPreferences().getBoolean(KEY_AUTO_ATTENDANCE, false);
	}
	
	public static void putString(String key,String value){
		getUserPreferences().edit().putString(key, value).commit();
	}

	public static String getString(String key,String value){
		return getUserPreferences().getString(key, value);
	}
}
