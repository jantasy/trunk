package com.telecompp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

/**
 * 终端配置管理
 * 
 * @author xiongdazhuang 
 * 
 */
public class TerminalConfigMgr implements SumaConstants
{
	public static boolean isWifiP2P = false;
	public static boolean isBlueTooth;
	public static int needPWDAmount = 1000;
	public static int verifyTimes = OFFLINE_MAX_VERFY_TIMES;
	public static boolean offlineAvailable = false;

	public static final String SHARE_PREFERENCES_ONLINE_LOGIN_VERFY_FLAG = "SharePreferenceOnlineLoginVerifyFlag";
	public static final String SHARE_PREFERENCES_UPLOAD_THRESHOLD = "SharePreferenceUploadThreshold";// 批上送
	public static final String SHARE_PREFERENCES_BLUETOOTH_WAIT_TIME = "SharePreferenceBluetoothWaitTime";
	public static final String SHARE_PREFERENCES_BLUETOOTH = "SharePreferenceBlueToothReader";// 蓝牙功能
	public static final String SHARE_PREFERENCES_FIRST_TIME_USE = "SharePreferenceFirstTimeUse";
	public static final String SHARE_PREFERENCES_P2P_FLAG = "SharePreferenceP2PFlag";
	public static final String SHARE_PREFERENCES_BLUETOOTH_ADDRESS = "SharePreferenceBlueToothAddress";// 蓝牙连接地址
	public static final List<String> privilege = new ArrayList<String>();
	/**
	 * 默认公交的配置
	 */
	public static final String SHARE_PREFERENCES_BUS_ID = "SharePreferenceBusID";
	public static final String SHARE_PREFERENCES_BUS_NAME = "SharePreferenceBusNAME";
	
	/**
	 * 交费易是否需要密码<IsSecure></IsSecure>   IsSecure = 0 (无密)   IsSecure = 1(有密) 
	 */
	public static String JFYIsSecure;
	public static String JFYCompanyAccount;
	public static String MechantID;
	private static int port = 30000;

	// 默认采用脱机手势校验
	/**
	 * 存储登陆校验方式
	 * 
	 * @param context
	 * @param flag
	 *            0：脱机手势 1：联机校验
	 * @return
	 */
	public static void setOnlineLoginVerfyFlag(Context context, String flag)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_PREFERENCES_ONLINE_LOGIN_VERFY_FLAG, flag);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取登陆校验方式
	 * 
	 * @param context
	 * @return
	 */
	public static String getOnlineLoginVerfyFlag(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_PREFERENCES_ONLINE_LOGIN_VERFY_FLAG);
			if (acc == null)
			{
				acc = "0";
				DataSaveManager.setPrefenceString(context, SHARE_PREFERENCES_ONLINE_LOGIN_VERFY_FLAG, "0");
			}
			else
			{
				if (acc.equals(""))
				{
					acc = "0";
					DataSaveManager.setPrefenceString(context, SHARE_PREFERENCES_ONLINE_LOGIN_VERFY_FLAG, "0");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	/**
	 * 存储批上送阀值
	 * 
	 * @param context
	 * @param number
	 *            阈值
	 * @return
	 */
	public static void setUploadThreshold(Context context, int number)
	{
		try
		{
			DataSaveManager.setPrefenceInt(context, SHARE_PREFERENCES_UPLOAD_THRESHOLD, number);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取批上送阀值
	 * 
	 * @param context
	 * @return
	 */
	public static int getUploadThreshold(Context context)
	{
		int number = 0;
		try
		{
			number = DataSaveManager.getPreferenceInt(context, SHARE_PREFERENCES_UPLOAD_THRESHOLD);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
		return number;
	}

	/**
	 * 存储是否第一次使用状态
	 * 
	 * @param context
	 * @param status
	 * @return
	 */
	public static void setFirstTimeUse(Context context, Boolean status)
	{
		try
		{
			DataSaveManager.setPrefenceBoolean(context, SHARE_PREFERENCES_FIRST_TIME_USE, status);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取是否第一次使用状态
	 * 
	 * @param context
	 * @return flase:初次使用 true:非初次使用
	 */
	public static boolean getFirstTimeUse(Context context)
	{
		boolean status = false;
		try
		{
			status = DataSaveManager.getPreferenceBoolean(context, SHARE_PREFERENCES_FIRST_TIME_USE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return true;
		}
		return status;
	}

	public static int getServerPort() {
        if (port < 60000) {
            port++;
        }else {
            port = 30000;
        }
        return port;
    }
	
	   /**
     * 存储登陆校验方式
     * 
     * @param context
     * @param flag
     *            true：P2P false：card
     * @return
     */
    public static void setP2PFlag(Context context, Boolean flag)
    {
        try
        {
            DataSaveManager.setPrefenceBoolean(context, SHARE_PREFERENCES_P2P_FLAG, flag);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取登陆校验方式
     * 
     * @param context
     * @return true：P2P false：card
     */
    public static boolean getP2PFlag(Context context)
    {
        boolean p2p = false;
        try
        {
            p2p = DataSaveManager.getPreferenceBoolean(context, SHARE_PREFERENCES_P2P_FLAG);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return p2p;
    }
    
    public static void setString(Context context,String flag,String value){
    	try {
			DataSaveManager.setPrefenceString(context, flag, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static String getString(Context context,String flag){
    	try {
			return DataSaveManager.getPreferenceString(context, flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
}
