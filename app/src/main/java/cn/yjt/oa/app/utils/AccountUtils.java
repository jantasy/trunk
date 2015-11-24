package cn.yjt.oa.app.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.Calendar;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserSimpleInfo;

/**
 * Created by xiong on 2015/10/9.
 */
public class AccountUtils {

    /**获取当前登录账户的id*/
    public static long getCurrentUserId(){
        UserSimpleInfo info = AccountManager.getCurrentSimpleUser(MainApplication.getApplication());
        if(info != null){
            return info.getId();
        }
        return 0;
    }

    /**获取当前登录账户的企业id*/
    public static String getCurrentCustId(){
        UserInfo info = AccountManager.getCurrent(MainApplication.getApplication());
        if(info != null){
            info.getCustId();
        }
        return null;
    }

    /**获取当前登录账户的企业id*/
    public static String getCurrentPhone(){
        UserInfo info = AccountManager.getCurrent(MainApplication.getApplication());
        if(info != null){
            info.getPhone();
        }
        return null;
    }

    /**获取当前的iccid*/
    public static String getCurrentIccId(){
        return TelephonyUtil.getICCID(MainApplication.getApplication());
    }

    /**获取当前的imei*/
    public static String getCurrentImei(){
        return TelephonyUtil.getIMEI(MainApplication.getApplication());
    }

    /**获取当前的时间*/
    public static long getCurrentTime(){
        Calendar time = Calendar.getInstance();
        return time.getTime().getTime();
    }

    /**获取当前app的渠道包信息*/
    public static String getChannelName(){
        String channelName = null;
        PackageManager manager = (MainApplication.getAppContext()).getPackageManager();
        if(manager !=null){
            try {
                ApplicationInfo info = manager.getApplicationInfo((MainApplication.getAppContext()).getPackageName(),PackageManager.GET_META_DATA);
                if(info!=null){
                    if(info.metaData!=null){
                        channelName = info.metaData.getString("UMENG_CHANNEL");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return channelName;
    }
}
