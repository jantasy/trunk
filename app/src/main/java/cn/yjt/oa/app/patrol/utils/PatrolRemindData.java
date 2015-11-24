package cn.yjt.oa.app.patrol.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.UserInfo;

/**
 * 巡检提醒相关的一些保存到sp数据的存取
 * Created by 熊岳岳 on 2015/10/13.
 */
public class PatrolRemindData {

    private static final String PATROL_REMIND = "patrol_remind";

    public static final String REMIND_TYPE = "remind_type";
    public static final String REMIND_TIME = "remind_time";

    public static final int TYPE_NO_REMIND = 0;
    public static final int TYPE_ONE = 1;
    public static final int TYPE_TWO = 2;
    public static final int TYPE_THREE = 3;
    public static final int TYPE_USER_DEFINDED = 4;

    /**获取巡检提醒的SharedPrefs*/
    public static SharedPreferences getPatrolRemindSP() {
        UserInfo userInfo = AccountManager.getCurrent(MainApplication.getAppContext());
        String userId = "none";
        if (userInfo != null) {
            userId = String.valueOf(userInfo.getId());
        }
        SharedPreferences sp = MainApplication.getAppContext()
              .getSharedPreferences(PATROL_REMIND + "_" + userId,Context.MODE_PRIVATE);
        return sp;
    }

    /**设置 提醒类型*/
    public static void setRemindType(int value){
        putInt(REMIND_TYPE, value);
    }

    /**设置 提醒时间*/
    public static void setRemindTime(String value){
        putString(REMIND_TIME,value);
    }

    /**获取 提醒类型*/
    public static int getRemindType(){
        return getInt(REMIND_TYPE);
    }

    /**获取 提醒时间*/
    public static String getRemindTime(){
        return getString(REMIND_TIME);
    }

    private static String getString(String key) {
        String result = getPatrolRemindSP().getString(key, "");
        return result;
    }

    private static void putString(String key, String value) {
       getPatrolRemindSP().edit().putString(key,value).commit();
    }

    private static int getInt(String key) {
        int result = getPatrolRemindSP().getInt(key, 0);
        return result;
    }

    private static void putInt(String key, int value) {
        getPatrolRemindSP().edit().putInt(key, value).commit();
    }

}
