package cn.yjt.oa.app.http;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.OperaStatistics;
import cn.yjt.oa.app.beans.OperaStatisticsRequest;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.Cancelable;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Application;
import android.os.AsyncTask;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.AreaUser;
import cn.yjt.oa.app.beans.AttendanceTime;
import cn.yjt.oa.app.beans.AttendanceUserTime;
import cn.yjt.oa.app.beans.BeaconInfo;
import cn.yjt.oa.app.beans.ChangePasswordInfo;
import cn.yjt.oa.app.beans.CustSignCommonInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.MeetingInfo;
import cn.yjt.oa.app.beans.MeetingSignInInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserLoginInfo;
import cn.yjt.oa.app.meeting.MeetingSigninActivity;
import cn.yjt.oa.app.utils.TelephonyUtil;

public final class ApiHelper {

	/**
	 * 根据企业的id从服务器中获取企业的Beancon信息
	 * 
	 * @param listener 这次请求的Listener
	 * @return
	 */
	public static Cancelable getBeacons(Listener<Response<ListSlice<BeaconInfo>>> listener) {
		Type responseType = new TypeToken<Response<ListSlice<BeaconInfo>>>() {
		}.getType();
		String custId = getCustId();
		return new AsyncRequest.Builder().setModule(String.format(AsyncRequest.MODULE_CUSTSIGNIN_BEACONS, custId))
			.setResponseType(responseType).setResponseListener(listener).build().get();
	}

	public static Cancelable deleteBeacon(Listener<Response<String>> listener, long beaconId) {
		Type responseType = new TypeToken<Response<String>>() {
		}.getType();
		String custId = getCustId();
		return new AsyncRequest.Builder().setModule(String.format(AsyncRequest.MODULE_CUSTSIGNIN_BEACONS, custId))
			.setModuleItem(String.valueOf(beaconId)).setResponseType(responseType).setResponseListener(listener)
			.build().delete();
	}

	public static Cancelable addBeacon(Listener<Response<BeaconInfo>> listener, BeaconInfo beaconInfo) {
		Type responseType = new TypeToken<Response<BeaconInfo>>() {
		}.getType();
		String custId = getCustId();
		return new AsyncRequest.Builder().setModule(String.format(AsyncRequest.MODULE_CUSTSIGNIN_BEACONS, custId))
			.setRequestBody(beaconInfo).setResponseType(responseType).setResponseListener(listener).build().post();
	}

	public static Cancelable getAttendanceTimes(Listener<Response<ListSlice<AttendanceTime>>> listener) {
		String custId = getCustId();
		Type responseType = new TypeToken<Response<ListSlice<AttendanceTime>>>() {
		}.getType();
		return new AsyncRequest.Builder().setModule(String.format(AsyncRequest.MODULE_CUSTSIGNIN_TIMES, custId))
			.setResponseType(responseType).setResponseListener(listener).build().get();
	}

	public static Cancelable setAttendanceTimes(Listener<Response<String>> listener, List<AttendanceTime> attendanceTime) {
		String custId = getCustId();
		Type responseType = new TypeToken<Response<String>>() {
		}.getType();
		return new AsyncRequest.Builder().setModule(String.format(AsyncRequest.MODULE_CUSTSIGNIN_TIMES, custId))
			.setRequestBody(attendanceTime).setResponseType(responseType).setResponseListener(listener).build()
			.put();

	}

	private static String getCustId() {
		UserInfo current = AccountManager.getCurrent(MainApplication.getAppContext());
		if (current != null) {
			return current.getCustId();
		} else {
			return "0";
		}
	}

	private static String getUserId() {
		try {
			return String.valueOf(AccountManager.getCurrent(MainApplication.getAppContext()).getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}

	public static Cancelable getAttendanceAreas(Listener<Response<List<CustSignCommonInfo>>> listener) {
		Type responseType = new TypeToken<Response<List<CustSignCommonInfo>>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(String.format(AsyncRequest.MODULE_CUSTS_SIGNCOMMON_LISTS, getCustId()))
			.setResponseType(responseType).setResponseListener(listener).build().get();
	}

	public static Cancelable getAttendanceAreaUsers(Listener<Response<ListSlice<AreaUser>>> listener, long areaId) {
		String custId = getCustId();
		Type responseType = new TypeToken<Response<ListSlice<AreaUser>>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(String.format(AsyncRequest.MODULE_CUSTSIGNIN_AREA_USER, custId, areaId))
			.setResponseType(responseType).setResponseListener(listener).build().get();
	}

	public static Cancelable setAttendanceAreaUsers(Listener<Response<String>> listener, long areaId,
		List<AreaUser> areaUsers) {
		Type responseType = new TypeToken<Response<String>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(String.format(AsyncRequest.MODULE_CUSTSIGNIN_AREA_USER, getCustId(), areaId))
			.setRequestBody(areaUsers).setResponseType(responseType).setResponseListener(listener).build().put();
	}

	public static Cancelable enableTimeAttendance(Listener<Response<String>> listener, boolean enable) {
		Type responseType = new TypeToken<Response<String>>() {
		}.getType();
		String module = String.format(AsyncRequest.MODULE_CUSTSIGNIN_TIMES, getCustId()) + "/"
			+ (enable ? "enable" : "disable");
		return new AsyncRequest.Builder().setModule(module).setResponseType(responseType).setResponseListener(listener)
			.build().put();
	}

	public static Cancelable getTimeAttendanceStatus(Listener<Response<Boolean>> listener) {
		Type responseType = new TypeToken<Response<Boolean>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(String.format(AsyncRequest.MODULE_CUSTSIGNIN_TIME_STATUS, getCustId()))
			.setResponseType(responseType).setResponseListener(listener).build().get();
	}

	public static Cancelable getUserBeaconsInArea(Listener<Response<List<BeaconInfo>>> listener) {
		Type responseType = new TypeToken<Response<List<BeaconInfo>>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(String.format(AsyncRequest.MODULE_CUSTSIGNIN_USER_BEACONS, getCustId(), getUserId()))
			.setResponseType(responseType).setResponseListener(listener).build().get();
	}

	public static Cancelable getAttendanceUserTimes(Listener<Response<List<AttendanceUserTime>>> listener) {
		Type responseType = new TypeToken<Response<List<AttendanceUserTime>>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(String.format(AsyncRequest.MODULE_CUSTSIGNIN_USER_TIMES, getCustId(), getUserId()))
			.setResponseType(responseType).setResponseListener(listener)
			.addDateQueryParameter(Calendar.getInstance().getTime(), Calendar.getInstance().getTime()).build()
			.get();

	}

	public static Cancelable checkWhiteList(Listener<Response<Boolean>> listener) {
		UserInfo info = AccountManager.getCurrent(MainApplication.getAppContext());
		if (info == null) {
			return null;
		}
		Type responseType = new TypeToken<Response<Boolean>>() {
		}.getType();
		return new AsyncRequest.Builder().setModule(AsyncRequest.MODULE_WHITELIST).setResponseType(responseType)
			.setResponseListener(listener).addQueryParameter("phone", info.getPhone()).build().get();

	}

	public static Cancelable updatePassword(String oldPassword, String newPassword, Listener<Response<String>> listener) {
		UserInfo info = AccountManager.getCurrent(MainApplication.getAppContext());
		if (info == null) {
			return null;
		}
		Type responseType = new TypeToken<Response<String>>() {
		}.getType();
		ChangePasswordInfo passwordInfo = new ChangePasswordInfo();
		passwordInfo.setNewPassword(newPassword);
		passwordInfo.setOldPassword(oldPassword);
		return new AsyncRequest.Builder().setModule(AsyncRequest.MODULE_UPDATEPASSWORD).setResponseType(responseType)
			.setResponseListener(listener).setRequestBody(passwordInfo).build().post();
	}

	public static Cancelable requestMeetingSignin(final Listener<Response<MeetingSignInInfo>> listener,
		String meetingId, String token) {
		Type responseType = new TypeToken<Response<MeetingSignInInfo>>() {
		}.getType();
		MeetingSignInInfo info = new MeetingSignInInfo();
		info.setToken(token);
		info.setMeetingId(Long.valueOf(meetingId));
		return new AsyncRequest.Builder()
			.setModule(String.format(AsyncRequest.MODULE_MEETING_SIGNIN, String.valueOf(meetingId)))
			.setRequestBody(info).setResponseType(responseType).setResponseListener(listener).build().post();
	}

	/** 隐式登录 */
	public static Cancelable realLogin(final Listener<Response<UserInfo>> listener,UserLoginInfo info) {

            /*记录操作 0101*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_LOGIN);

        Type responseType = new TypeToken<Response<UserInfo>>() {
		}.getType();
		info.setIccid(TelephonyUtil.getICCID(MainApplication.getAppContext()));
		return new AsyncRequest.Builder()
			.setModule(AsyncRequest.MODULE_LOGIN)
			.setRequestBody(info)
			.setResponseType(responseType)
			.setResponseListener(listener)
			.build()
			.post();
	}

    /** 提交操作统计 */
    public static Cancelable commitOpera(final Listener<Response<Object>> listener,List<OperaStatisticsRequest> infos) {
        Type responseType = new TypeToken<Response<Object>>() {
        }.getType();
        return new AsyncRequest.Builder()
                .setModule(AsyncRequest.MODULE_OPERASTATISTICS)
                .setRequestBody(infos)
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build()
                .post();
    }

    /** 发送考勤报表到邮箱 */
    public static Cancelable emailAttendanceExcel(Listener<Response<String>> listener, String email, String url,Date beginDate,Date endDate) {
        Type responseType = new TypeToken<Response<String>>() {
        }.getType();
        return new AsyncRequest.Builder()
                .setModule(url)
                .addQueryParameter("email", email)
                .addDateQueryParameterStartTime(beginDate,endDate)
                .setResponseType(responseType)
                .setResponseListener(listener)
                .build()
                .get();
    }
}
