package com.telecompp.util;

import android.content.Context;

import com.telecompp.engine.fm_sessionKeyExchange;

public class ConnSessionUtil {

	/** 
	 * 主控密钥更新,参数下载
	 * 
	 * @return
	 */
	public static boolean masterKeyUpdate(Context context) {
		boolean result = false;
		fm_sessionKeyExchange sessionKeyExchangeHandler = new fm_sessionKeyExchange();
		String t_sessionKeyResultString = sessionKeyExchangeHandler
				.terminal_SessionKey_Start();
		if (t_sessionKeyResultString == null) {
			return result;
		}
		return step2();
	}
	
	
	public static boolean step2()
	{
		boolean result = false;
		fm_sessionKeyExchange sessionKeyExchangeHandler = new fm_sessionKeyExchange();
		String c_sessionKeyResultString = sessionKeyExchangeHandler
				.card_SessionKey_Start();
		if (c_sessionKeyResultString == null) {
			return result;
		} else {
			result = true;
			TerminalDataManager.setbAlreadyCardSessionKeyExchange(true);
		}
		return result;
	}
}
