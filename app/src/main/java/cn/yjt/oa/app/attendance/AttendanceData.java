package cn.yjt.oa.app.attendance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.UserInfo;

public class AttendanceData {

	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	public static SharedPreferences getAttendanceData() {
		UserInfo userInfo = AccountManager.getCurrent(MainApplication
				.getAppContext());
		String userId = "none";
		if (userInfo != null) {
			userId = String.valueOf(userInfo.getId());
		}
		SharedPreferences sharedPreferences = MainApplication.getAppContext()
				.getSharedPreferences("attendancedata_" + userId,
						Context.MODE_PRIVATE);
		return sharedPreferences;
	}

	public static boolean containsAttendanceTime(long timeMills) {
		Set<String> stringSet = getAttendanceData().getStringSet(getToday(), null);
		if(stringSet!=null&&stringSet.contains(String.valueOf(timeMills))){
			return true;
		}
		return false;
	}

	public static void addAttendanceTime(long timeMills) {
		Set<String> stringSet = getAttendanceData().getStringSet(getToday(), new HashSet<String>());
		stringSet.add(String.valueOf(timeMills));
		getAttendanceData().edit().putStringSet(getToday(),stringSet).commit();
	}

	public static void clearNotToday() {
		Set<String> stringSet = getAttendanceData().getStringSet(getToday(), null);
		getAttendanceData().edit().clear().putStringSet(getToday(), stringSet).commit();
	}

	public static String getToday() {
		return DATE_FORMAT.format(Calendar.getInstance().getTime());
	}
}
