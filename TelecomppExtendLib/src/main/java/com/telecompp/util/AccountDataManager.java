package com.telecompp.util;

import android.content.Context;

/**
 * 客户端账户相关固定存储信息 
 * @author xiongdazhuang
 *
 */
public class AccountDataManager
{
	public static final String SHARE_PREFERENCES_FLAG_MASTER_KEY_UPDATE = "SharePreferenceMasterKeyUpdate";
	public static final String SHARE_PREFERENCES_FLAG_PARAMETER_UPDATE = "SharePreferenceParameterUpdate";
	public static final String SHARE_PREFERENCES_CSN = "SharePreferenceCSN";
	public static final String SHARE_PREFERENCES_RESIGTER_STATUS = "SharePreferenceRegisterStatus";
	public static final String SHARE_PREFERENCES_ACCOUNT = "SharePreferenceAccount";
	public static final String SHARE_PREFERENCES_PWD = "SharePreferencePassword";

	public static final String SHARE_MerchantID = "SharePreferenceMerchantID";
	public static final String SHARE_BranchID = "SharePreferenceBranchID";
	public static final String SHARE_IMEI = "SharePreferenceIMEI";
	public static final String SHARE_TerminalModelNum = "SharePreferenceTerminalModelNum";
	public static final String SHARE_Tel = "SharePreferenceTel";
	public static final String SHARE_TelAttribution = "SharePreferenceTelAttribution";
	public static final String SHARE_RelName = "SharePreferenceRelName";
	public static final String SHARE_RelTel = "SharePreferenceRelTel";
	public static final String SHARE_RelAddr = "SharePreferenceRelAddr";
	public static final String SHARE_LoginName = "SharePreferenceLoginName";
	public static final String SHARE_LoginPwd = "SharePreferenceLoginPwd";
	public static final String SHARE_Email = "SharePreferenceEmail";

	/**
	 * 设置主控密钥标识
	 * 
	 * @param context
	 * @param FlagMasterKeyUpdate
	 */
	public static void setFlagMasterKeyUpdate(Context context, String FlagMasterKeyUpdate)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_PREFERENCES_FLAG_MASTER_KEY_UPDATE, FlagMasterKeyUpdate);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取主控密钥标识
	 * 
	 * @param context
	 * @return
	 */
	public static String getFlagMasterKeyUpdate(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_PREFERENCES_FLAG_MASTER_KEY_UPDATE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	/**
	 * 设置参数更新标识
	 * 
	 * @param context
	 * @param FlagParameterUpdate
	 */
	public static void setFlagParameterUpdate(Context context, String FlagParameterUpdate)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_PREFERENCES_FLAG_PARAMETER_UPDATE, FlagParameterUpdate);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取参数更新标识
	 * 
	 * @param context
	 * @return
	 */
	public static String getFlagParameterUpdate(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_PREFERENCES_FLAG_PARAMETER_UPDATE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	/**
	 * 存储CSN
	 * 
	 * @param context
	 * @param RegisterStatus
	 *            0：没注册过 1：注册过
	 * @return
	 */
	public static void setCSN(Context context, String CSN)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_PREFERENCES_CSN, CSN);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取CSN
	 * 
	 * @param context
	 * @return
	 */
	public static String getCSN(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_PREFERENCES_CSN);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	/**
	 * 存储用户注册状态，用于判断用户是否注册过
	 * 
	 * @param context
	 * @param RegisterStatus
	 *            0：没注册过 1：注册过
	 * @return
	 */
	public static void setUserRegisterStatus(Context context, String RegisterStatus)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_PREFERENCES_RESIGTER_STATUS, RegisterStatus);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取用户注册状态，用于判断用户是否注册过
	 * 
	 * @param context
	 * @return
	 */
	public static String getUserRegisterStatus(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_PREFERENCES_RESIGTER_STATUS);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	/**
	 * 获取password
	 * @param context
	 * @return
	 */
	public static String getPassword(Context context)
	{
		String pwd = null;
		try
		{
			pwd = DataSaveManager.getPreferenceString(context, SHARE_PREFERENCES_PWD);
		}
		catch (Exception e)
		{
			return null;
		}
		return pwd;
	}

	/**
	 * 设置password
	 * @param context
	 * @param keyVersion
	 */
	public static void setPassword(Context context, String keyVersion)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_PREFERENCES_PWD, keyVersion);
		}
		catch (Exception e)
		{

		}
	}

	/**
	 * 设置商户号
	 * @param context
	 * @param MerchantID
	 */
	public static void setMerchantID(Context context, String MerchantID)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_MerchantID, MerchantID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 设置网点号
	 * @param context
	 * @param BranchID
	 */
	public static void setBranchID(Context context, String BranchID)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_BranchID, BranchID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 设置
	 * @param context
	 * @param IMEI
	 */
	public static void setIMEI(Context context, String IMEI)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_IMEI, IMEI);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setTerminalModelNum(Context context, String TerminalModelNum)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_TerminalModelNum, TerminalModelNum);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setTel(Context context, String Tel)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_Tel, Tel);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setTelAttribution(Context context, String TelAttribution)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_TelAttribution, TelAttribution);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setRelName(Context context, String RelName)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_RelName, RelName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setRelTel(Context context, String RelTel)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_RelTel, RelTel);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setRelAddr(Context context, String RelAddr)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_RelAddr, RelAddr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setLoginName(Context context, String LoginName)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_LoginName, LoginName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setLoginPwd(Context context, String LoginPwd)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_LoginPwd, LoginPwd);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setEmail(Context context, String Email)
	{
		try
		{
			DataSaveManager.setPrefenceString(context, SHARE_Email, Email);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String getMerchantID(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_MerchantID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	public static String getBranchID(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_BranchID);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	public static String getIMEI(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_IMEI);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	public static String getTerminalModelNum(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_TerminalModelNum);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	public static String getTel(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_Tel);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	public static String getTelAttribution(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_TelAttribution);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	public static String getRelName(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_RelName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	public static String getRelTel(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_RelTel);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	public static String getRelAddr(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_RelAddr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	public static String getLoginName(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_LoginName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	public static String getLoginPwd(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_LoginPwd);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

	public static String getEmail(Context context)
	{
		String acc = null;
		try
		{
			acc = DataSaveManager.getPreferenceString(context, SHARE_Email);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return acc;
	}

}
