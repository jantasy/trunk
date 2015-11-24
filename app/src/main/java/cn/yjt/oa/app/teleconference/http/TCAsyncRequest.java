package cn.yjt.oa.app.teleconference.http;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import cn.yjt.oa.app.beans.TCAccessTokenResponse;
import cn.yjt.oa.app.beans.TCActiveCodeResponse;
import cn.yjt.oa.app.beans.TCCreateConferenceResponse;
import cn.yjt.oa.app.http.AsyncRequest;

@SuppressWarnings("unused")
public class TCAsyncRequest {
	// 获取access_token
	//private static final String BASE_URL = "http://118.180.5.4";
	private static final String BASE_URL = "http://202.100.92.4";
//	private static final String BASE_URL = "http://test.yijitongoa.com:9090/PhoneMeeting-bussiness";
	private static final String APP_ID = "yijitong";
	private static final String APP_SECRET = "yijitong";
	private static final String GRANT_TYPE = "client_credential";
	
	// 获取access_token
	private static final String ACCESS_TOKEN = "/access_token";
	// 激活帐号 
	private static final String ACTIVATE_ACCOUNT = "/ecp/activate_account";
	// 创建电话会议 
	private static final String CREATE_CONFERENCE = "/ecp/create_conference";
	// 再次邀请参会人员
	private static final String INVITE_PARTICIPANTS = "/ecp/invite_participants";
	// 电话会议开户
	private static final String REGISTER_ACCOUNT = "/ecp/register_account";
	
	public static final int ACTIVATE_OK = 0; // 激活成功
	
	private static final Type TYPE_ACCESS_TOKEN = new TypeToken<TCAccessTokenResponse>(){}.getType();
	private static final Type TYPE_ACTIVATE_ACCOUNT = new TypeToken<TCActiveCodeResponse>(){}.getType();
	private static final Type TYPE_CREATE_CONFERENCE = new TypeToken<TCCreateConferenceResponse>(){}.getType();
	private static Map<String,String> header = new HashMap<String,String>();
	static{
		header.put("luobo-response-charset", "utf-8");
	}
	
	public static final Map<String,String> addAccessTokenParameters(String user_account){
		Map<String,String> map = new HashMap<String,String>();
		map.put("user_account", "13331092266");//13331092266
		map.put("app_id", APP_ID);
		map.put("app_secret", APP_SECRET);
		map.put("grant_type", GRANT_TYPE);
		return map;
	}
	
	public static void getAccessToken(Map<String,String> params,final AccessTokenCallback callback){
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setCustomModule(BASE_URL)
		.setModule(ACCESS_TOKEN)
		.addQueryParameters(params)
		.addRequestHeaders(header)
		.setResponseType(TYPE_ACCESS_TOKEN)
		.setResponseListener(new Listener<TCAccessTokenResponse>() {
			
			@Override
			public void onResponse(TCAccessTokenResponse response) {
				callback.onResult(response.getAccess_token());
			}
			@Override
			public void onErrorResponse(InvocationError error) {
				error.printStackTrace();
			}
		})
		.build()
		.get();
	}
	
	public interface AccessTokenCallback{
		public void onResult(String accessToken);
	}
	
	public static Map<String, String> addActivateAccountParameters(
			String access_token, String mobile, String active_code) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("access_token", access_token);
		map.put("mobile", mobile);
		map.put("active_code", active_code);
		return map;
	}
	public static void getVerificationCode(Map<String,String> params,final ActivateAccountCallback callback){
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setCustomModule(BASE_URL)
		.setModule(ACTIVATE_ACCOUNT)
		.addQueryParameters(params)
		.addRequestHeaders(header)
		.setResponseType(TYPE_ACTIVATE_ACCOUNT)
		.setResponseListener(new Listener<TCActiveCodeResponse>() {
			
			@Override
			public void onResponse(TCActiveCodeResponse response) {
				callback.onResult(response);
			}
			@Override
			public void onErrorResponse(InvocationError error) {
				error.printStackTrace();
			}
		})
		.build()
		.get();
	}
	
	public interface ActivateAccountCallback{
		public void onResult(TCActiveCodeResponse response);
	}
	
	public static Map<String, String> addCreateConferenceParameters(
			String access_token, String mobile, String ecp_token,String participants) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("access_token", access_token);
		map.put("mobile", mobile);
		map.put("ecp_token", ecp_token);
		map.put("participants", participants);
		return map;
	}
	
	public static final void getCreateConference(Map<String,String> params,final CreateConferenceCallback callback){
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setCustomModule(BASE_URL)
		.setModule(CREATE_CONFERENCE)
		.addQueryParameters(params)
		.addRequestHeaders(header)
		.setResponseType(TYPE_CREATE_CONFERENCE)
		.setResponseListener(new Listener<TCCreateConferenceResponse>() {
			
			@Override
			public void onResponse(TCCreateConferenceResponse response) {
				callback.onResult(response);
			}
			@Override
			public void onErrorResponse(InvocationError error) {
				error.printStackTrace();
			}
		})
		.build()
		.get();
	}
	public interface CreateConferenceCallback{
		public void onResult(TCCreateConferenceResponse response);
	}
	
}
