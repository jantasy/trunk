package cn.yjt.oa.app.utils;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.BeaconInfo;

public class SharedUtils {

	public static final String CONTACT_LAST_UPDATETIME = "ContactLastUpdateTime";
	public static final String DATA_MANAGEMENT= "DataManagement";
	
	private static final String TAG_NAME = "tag_name";
    private static final String PATROL_TAG_NAME = "patrol_tag_name";
	private static final String SP_BEACON_NAMES = "beacon_names";
	private static final String ATTENDANCE_EMAIL = "attendance_email";
	private static final String DASH_BOARD_CONFIG = "dash_board_config";
	
	
	public static SharedPreferences getSystemSettings(Context context){
		SharedPreferences sp = context.getSharedPreferences(DATA_MANAGEMENT, Context.MODE_PRIVATE);
		return sp;
	}
	public static SharedPreferences getContactLastUpdateTime(Context context){
		SharedPreferences sp = context.getSharedPreferences(DASH_BOARD_CONFIG, Context.MODE_PRIVATE);
		return sp;
	}
	public static SharedPreferences getDashBoardConfig(Context context){
		String spname =AccountManager.getCurrent(MainApplication.getAppContext()).getId()+CONTACT_LAST_UPDATETIME;
		//TODO context may be null
		SharedPreferences sp = MainApplication.getAppContext().getSharedPreferences(spname, Context.MODE_PRIVATE);
		return sp;
	}
	
	public static SharedPreferences getUserSp(Context context){
		long id = AccountManager.getCurrent(context).getId();
		SharedPreferences sp = context.getSharedPreferences(id+"_sp", Context.MODE_PRIVATE);
		return sp;
	}
	
	public static void setTagName(Context context,String tagName){
		getUserSp(context).edit().putString(TAG_NAME, tagName).commit();
	}
	
	public static String getTagName(Context context){
		return getUserSp(context).getString(TAG_NAME, "");
	}

    public static void setPatrolTagName(Context context,String tagName){
        getUserSp(context).edit().putString(PATROL_TAG_NAME, tagName).commit();
    }

    public static String getPatrolTagName(Context context){
        return getUserSp(context).getString(PATROL_TAG_NAME, "");
    }
	
	public static void setBeaconNames(Context context,String beaconNames){
		getUserSp(context).edit().putString(SP_BEACON_NAMES, beaconNames).commit();
	}
	
	public static String getBeaconNames(Context context){
		return getUserSp(context).getString(SP_BEACON_NAMES, "");
	}
	
	
	public static void setAttendanceEmail(Context context,String email){
		getUserSp(context).edit().putString(ATTENDANCE_EMAIL, email).commit();
	}
	
	public static String getAttendanceEmail(Context context){
		return getUserSp(context).getString(ATTENDANCE_EMAIL, "");
	}
	
	public static void setFavoriteBeacons(List<BeaconInfo> beaconInfos){
		
	}
	
	public static List<BeaconInfo> getFavoritBeacons(){
		return null;
	}
	

}
