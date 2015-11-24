package cn.yjt.oa.app.account;

import io.luobo.common.http.volley.GsonConverter;
import io.luobo.common.json.TypeToken;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserLoginInfo;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.contactlist.db.GlobalContactManager;
import cn.yjt.oa.app.http.GsonHolder;

/**
 * 账户管理类:
 * 管理当前登录的账户
 */
public class AccountManager {

	//用户信息
	private static UserInfo currentUserInfo;
	//用户信息锁
	private static Object USERINFO_LOCK = new Object();
	//获取当前的用户信息
	public static UserInfo getCurrent(Context context) {
		if (currentUserInfo == null) {
			synchronized (USERINFO_LOCK) {
				if (currentUserInfo == null) {
					currentUserInfo = loadUserInfo(context);
				}
			}
		}
		return currentUserInfo;
	}
	
	/**
	 * 获取简易的用户信息(只包含姓名，id)
	 */
	public static UserSimpleInfo getCurrentSimpleUser(Context context) {
		final UserInfo userInfo = getCurrent(context);
		UserSimpleInfo simpleInfo = null;
		if(userInfo!=null){
			simpleInfo = new UserSimpleInfo(userInfo.getId(), userInfo.getName());
		}
		return simpleInfo;
	}
	
	/**
	 * 更新当前账户的用户信息
	 * @param context 上下文
	 * @param userInfo 更新后的用户
	 */
	public static void updateUserInfo(Context context, UserInfo userInfo) {
		synchronized (USERINFO_LOCK) {
			currentUserInfo = userInfo;
		}
		if(userInfo.getIsYjtUser() == 1){
			GlobalContactManager.addYjtUserId(context, userInfo.getId());
		}
		
		saveUserInfo(context);
		MainApplication.sendCurrentLoginUserChanged(context);
	}
	
	/**
	 * 更新用户的头像
	 * @param context 上下文
	 * @param avatar 头像的url
	 */
	public static void updateUserAvatar(Context context, String avatar) {
		getCurrent(context);
		synchronized (USERINFO_LOCK) {
			UserInfo userInfo = currentUserInfo;
			if (userInfo != null) userInfo.setAvatar(avatar);
		}
		
		saveUserInfo(context);
	}
	
	/**
	 * 获取SharedPreferences
	 * @param context 上下文
	 * @return
	 */
	private static SharedPreferences getAccountPreferences(Context context) {
		context = MainApplication.getAppContext();
		return context.getSharedPreferences("account", Context.MODE_PRIVATE);
	}
	
	/**
	 * 保存当前帐号的用户，将当前的用户信息，转回json字符串，保存在sp中
	 * @param context 上下文
	 */
	private static void saveUserInfo(Context context) {
		synchronized (USERINFO_LOCK) {
			UserInfo userInfo = currentUserInfo;
			String userInfoText = null;
			if (userInfo != null) {
				GsonConverter converter = new GsonConverter(GsonHolder.getInstance().getGson());
				userInfoText = converter.convertToString(userInfo);
			}
			//TODO: encrypt
			SharedPreferences sp = getAccountPreferences(context);
			Editor edit = sp.edit();
			edit.putString("user_info", userInfoText);
			edit.commit();
		}
	}
	
	/**
	 * 加载用户当前的信息
	 * @param context 上下文
	 * @return 当前用户的实体类
	 */
	private static UserInfo loadUserInfo(Context context) {
		
		//从sp中获取用户信息的字符串（json格式）
		SharedPreferences sp = getAccountPreferences(context);
		String userInfoText = sp.getString("user_info", null);
		
		//TODO: decrypt
		
		if (!TextUtils.isEmpty(userInfoText)) {
			//如果该字符串不为空，将其转换成用户信息（UserInfo）对象
			GsonConverter converter = new GsonConverter(GsonHolder.getInstance().getGson());
			return converter.convertToObject(userInfoText, new TypeToken<UserInfo>(){}.getType());
		}
		
		return null;
	}
	
	/**
	 * 清除当前账户的cookies
	 */
	public static void clearAccountCookie() {
		CookieHandler cookieManager = CookieHandler.getDefault();
		if (cookieManager != null && cookieManager instanceof CookieManager) {
			CookieStore store = ((CookieManager)cookieManager).getCookieStore();
			store.removeAll();
		}
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static UserLoginInfo getCurrentLogiInfo(Context context){
		UserLoginInfo info = new UserLoginInfo();
		SharedPreferences sp = context.getSharedPreferences("account", Context.MODE_PRIVATE);
		long userId = sp.getLong("user_id", 0);
		String passWord = sp.getString("password", "");
		String phone = sp.getString("phone", "");
		info.setUserId(userId);
		info.setPhone(phone);
		info.setPassword(passWord);
		return info;
	}
	
	public static void saveCurrentLogiInfo(Context context,long userId,String phone,String psd){
		SharedPreferences sp = context.getSharedPreferences("account", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putLong("user_id", userId);
		edit.putString("password", psd);
		edit.putString("phone", phone);
		edit.putBoolean("auto_login", true);
		edit.commit();
	}
	public static void updateCurrentLogiInfoId(Context context,long userId){
		SharedPreferences sp = context.getSharedPreferences("account", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putLong("user_id", userId);
		edit.commit();
	}
	
	public static boolean isAutoLogin(Context context){
		SharedPreferences sp = context.getSharedPreferences("account", Context.MODE_PRIVATE);
		return sp.getBoolean("auto_login", false);
	}
	
	public static void exitLogin(Context context){
		
		SharedPreferences sp = context.getSharedPreferences("account", Context.MODE_PRIVATE);
		String userInfoStr = sp.getString("user_info", null);
		if (!TextUtils.isEmpty(userInfoStr)) {
		}
		MainApplication.logoutDelAllTag();
		currentUserInfo = null;
		Editor edit = sp.edit();
//		edit.remove("login_name");
//		edit.remove("password");
		edit.remove("user_info");
		edit.putBoolean("auto_login", false);
		edit.commit();
	}

	public static String getPassword(Context context){
		SharedPreferences sp = context.getSharedPreferences("account", Context.MODE_PRIVATE);
		return sp.getString("password", "");
	}
}
