package cn.yjt.oa.app.sim;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import cn.com.ctbri.yijitong.edep.EjitongCard;
import cn.com.ctbri.yijitong.edep.RecordTrade;

public class SIMUtils {

	private static final String TAG = SIMUtils.class.getSimpleName();
	private static final int RECORDS_COUNTS = 100;

	public static List<SIMRecord> getBusinessRecords(Context context) {
		EjitongCard card = EjitongCard.getDefaultCard();
		List<SIMRecord> simRecord = new ArrayList<SIMRecord>();
		if (card != null) {
			try {
				card.init(Constant.AID_STRING, context);
			} catch (Throwable e) {
				return null;
			}
			List<RecordTrade> records = new ArrayList<RecordTrade>();
			// 获取交易记录
			int result = card.getTradeList(RECORDS_COUNTS, records);// -1为失败，0为正常
			if (result == -1) {
				return null;
			}
			// 获取单位
			String modeString = card.GetSubsidiesWalletType();
			int tmpMode;
			if (modeString.equals(Constant.UNIT_YUAN_DEFAULT_STRING)) {
				tmpMode = Constant.UNIT_YUAN_DEFAULT;
			} else if (modeString.equals(Constant.UNIT_TIMES_DEFAULT_STRING)) {
				tmpMode = Constant.UNIT_TIMES_DEFAULT;
			} else {
				tmpMode = Constant.UNIT_TOSET;
			}
			for (RecordTrade recordTrade : records) {
				simRecord.add(new SIMRecord(recordTrade, tmpMode));
			}
			card.release();
		}

		return simRecord;

	}

	public static SIMBalance getBalance(Context context) {
		EjitongCard card = EjitongCard.getDefaultCard();
		if (card != null) {

			try {
				card.init(Constant.AID_STRING, context);
			} catch (Throwable e) {
				return null;
			}
			// 获取余额
			float balance = card.GetBalance();
			int edBalance = card.GetEdBalance();

			// 获取单位
			String modeString = card.GetSubsidiesWalletType();
			int tmpMode;
			if (modeString.equals(Constant.UNIT_YUAN_DEFAULT_STRING)) {
				tmpMode = Constant.UNIT_YUAN_DEFAULT;// 获取到补贴钱包是计金额的
			} else if (modeString.equals(Constant.UNIT_TIMES_DEFAULT_STRING)) {
				tmpMode = Constant.UNIT_TIMES_DEFAULT;// 获取到补贴钱包是计次的
			} else {
				tmpMode = Constant.UNIT_TOSET;// 没有获取到补贴钱包单位，需要对其单位进行猜测
			}
			card.release();
			return new SIMBalance(balance, edBalance, tmpMode);
		}
		return null;
	}

	public static SIMInfos readSIM(Context context) {
		EjitongCard card = EjitongCard.getDefaultCard();
		//Log.e("abc", "到底是不是空的"+card);
		if(card!=null){
			try {
				card.init(Constant.AID_STRING, context);
			} catch (Throwable e) {
				e.printStackTrace();
				return null;
			}

			// 获取余额
			float balance = card.GetBalance();
			int edBalance = card.GetEdBalance();

			List<RecordTrade> records = new ArrayList<RecordTrade>();
			// 获取交易记录
			int result = card.getTradeList(RECORDS_COUNTS, records);// -1为失败，0为正常
			if (result == -1) {
				return null;
			}
			Log.d(TAG, "getTradeList result = " + result);
			// 获取单位
			String modeString = card.GetSubsidiesWalletType();
			int tmpMode;
			if (modeString.equals(Constant.UNIT_YUAN_DEFAULT_STRING)) {
				tmpMode = Constant.UNIT_YUAN_DEFAULT;
			} else if (modeString.equals(Constant.UNIT_TIMES_DEFAULT_STRING)) {
				tmpMode = Constant.UNIT_TIMES_DEFAULT;
			} else {
				tmpMode = Constant.UNIT_TOSET;
			}

			List<SIMRecord> simRecord = new ArrayList<SIMRecord>();
			for (RecordTrade recordTrade : records) {
				simRecord.add(new SIMRecord(recordTrade, tmpMode));
			}
			card.release();
			return new SIMInfos(simRecord, tmpMode, balance, edBalance);
		}
		return null;
	}
}
