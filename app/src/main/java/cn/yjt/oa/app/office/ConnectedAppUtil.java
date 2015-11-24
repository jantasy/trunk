package cn.yjt.oa.app.office;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.office.cypher.ThreeDES;

public class ConnectedAppUtil {
	
	private static final Intent MAIN = new Intent(Intent.ACTION_MAIN);
	static {
		MAIN.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}
	
	private static final HashMap<String, ComponentName> CONNCETER_APP = new HashMap<String, ComponentName>();
	static {
		CONNCETER_APP.put("com.gsww.androidnma.activity", new ComponentName("com.gsww.androidnma.activity", "com.gsww.androidnma.activity.LoginActivity"));
	}
	
	public static boolean isConnectorApp(String packageName) {
		return CONNCETER_APP.containsKey(packageName);
	}
	
	public static void launchPackage(Context context, String packageName){
		//在这里进行第三方应用
		final Intent intent = MAIN;
		ComponentName component = CONNCETER_APP.get(packageName);
		if (component == null)
			return;
		intent.setComponent(component);
		UserInfo userInfo = AccountManager.getCurrent(context);
		//计算token
		String userInfoStr = userInfo.getPhone()+"|"+getIMEI(context)+"|"+getTime();
    	//2.生成加密串
    	byte [] encode = ThreeDES.encryptMode("zonghebangongwangluoban3".getBytes(),userInfoStr.getBytes());
    	String encryptedStr  = ThreeDES.byte2hex(encode);
    	//3.生成token
    	String token = userInfoStr+"|"+encryptedStr ;
    	//intent.
		intent.putExtra("token", token);
		context.startActivity(intent);
	}
	
	private static String getIMEI(Context context){
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	}
	
	private static String getTime(){
		Date date = new Date();
		return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
	}
}
