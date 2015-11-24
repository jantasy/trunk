package cn.yjt.oa.app.meeting.http;

import io.luobo.common.Cancelable;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;

import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.MeetingInfo;
import cn.yjt.oa.app.beans.MeetingSignInInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.http.AsyncRequest;

/**
 * 会议接口
 * @author 熊岳岳
 * @since 20150901
 */
public class MeetingApiHelper {

	/** 请求我参加的会议 */
	public static Cancelable getPublicMeeting(Listener<Response<ListSlice<MeetingInfo>>> listener, String filter,
		int from, int max) {
		Type responseType = new TypeToken<Response<ListSlice<MeetingInfo>>>() {
		}.getType();
		return new AsyncRequest.Builder().setModule(AsyncRequest.MODULE_MEETING_PUBLIC)
			.setResponseType(responseType)
			.setResponseListener(listener)
			.addQueryParameter("filter", filter)
			.addPageQueryParameter(from, max)
			.build().get();
	}

	/** 请求我发起的会议 */
	public static Cancelable getPublicMeeting(Listener<Response<ListSlice<MeetingInfo>>> listener, int from, int max) {
		return getPublicMeeting(listener, " ", from, max);
	}

	/** 请求我参加的会议 */
	public static Cancelable getJoinMeeting(Listener<Response<ListSlice<MeetingInfo>>> listener, String filter,
		int from, int max) {
		Type responseType = new TypeToken<Response<ListSlice<MeetingInfo>>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(AsyncRequest.MODULE_MEETING_JOIN)
			.setResponseType(responseType)
			.setResponseListener(listener)
			.addQueryParameter("filter", filter)
			.addPageQueryParameter(from, max)
			.build()
			.get();
	}

	/** 请求我参加的会议 */
	public static Cancelable getJoinMeeting(Listener<Response<ListSlice<MeetingInfo>>> listener, int from, int max) {
		return getJoinMeeting(listener, " ", from, max);
	}

	/** 删除会议信息 */
	public static Cancelable deletePublicMeeting(Listener<Response<Object>> listener, long id) {
		Type responseType = new TypeToken<Response<ListSlice<MeetingInfo>>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(String.format(AsyncRequest.MODULE_MEETING_DELETE, String.valueOf(id)))
			.setResponseType(responseType)
			.setResponseListener(listener)
			.build()
			.delete();
	}

	/** 获取会议签到用户列表 */
	public static Cancelable getMeetingSigninInfoList(Listener<Response<ListSlice<MeetingSignInInfo>>> listener,
		String filter, int from, int max, long id) {
		Type responseType = new TypeToken<Response<ListSlice<MeetingSignInInfo>>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(String.format(AsyncRequest.MODULE_MEETING_SIGNIN_LIST, String.valueOf(id)))
			.setResponseType(responseType)
			.setResponseListener(listener)
			.addQueryParameter("filter", filter)
			.addPageQueryParameter(from, max)
			.build()
			.get();
	}

	/** 创建会议 */
	public static Cancelable publicMeeting(Listener<Response<MeetingInfo>> listener, MeetingInfo info) {
		Type responseType = new TypeToken<Response<MeetingInfo>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(AsyncRequest.MODULE_MEETING)
			.setRequestBody(info)
			.setResponseType(responseType)
			.setResponseListener(listener)
			.build()
			.post();
	}

	/** 更新会议 */
	public static Cancelable updateMeeting(Listener<Response<MeetingInfo>> listener, MeetingInfo info) {
		Type responseType = new TypeToken<Response<MeetingInfo>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(String.format(AsyncRequest.MODULE_MEETING_UPDATE, String.valueOf(info.getId())))
			.setRequestBody(info)
			.setResponseType(responseType)
			.setResponseListener(listener)
			.build()
			.put();
	}

	/** 更新会议二维码 */
	public static Cancelable updateMeetingQrcode(Listener<Response<MeetingInfo>> listener, long id) {
		Type responseType = new TypeToken<Response<MeetingInfo>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(String.format(AsyncRequest.MODULE_MEETING_QRCODE, String.valueOf(id)))
			.setResponseType(responseType)
			.setResponseListener(listener)
			.build()
			.put();
	}

	/** 发送会议出席人员报表到邮箱 */
	public static Cancelable emailSigninExcel(Listener<Response<String>> listener, String email, long id) {
		Type responseType = new TypeToken<Response<String>>() {
		}.getType();
		return new AsyncRequest.Builder()
			.setModule(String.format(AsyncRequest.MODULE_MEETING_EMAIL, String.valueOf(id)))
			.addQueryParameter("email", email)
			.setResponseType(responseType)
			.setResponseListener(listener)
			.build()
			.get();
	}
}
