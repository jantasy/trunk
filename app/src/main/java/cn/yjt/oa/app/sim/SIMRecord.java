package cn.yjt.oa.app.sim;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.ctbri.yijitong.edep.Method;
import cn.com.ctbri.yijitong.edep.RecordTrade;
import cn.yjt.oa.app.widget.TimeLineAdapter.DateItem;

public class SIMRecord extends RecordTrade implements DateItem {
	//"01" - 补贴钱包充值 "02" - 主钱包充值 "05" - 补贴钱包消费 "06" - 主钱包消费
	public static final String WALLET_TYPE_PAY_SUFFICIENT = "01";// 补贴钱包充值
	public static final String WALLET_TYPE_PAY_EXPANCE = "05";// 补贴钱包消费
	public static final String WALLET_TYPE_MAIN_SUFFICIENT = "02";// 朱钱包充值
	public static final String WALLET_TYPE_MAIN_EXPANCE = "06";// 主钱包消费
	
	private RecordTrade recordTrade;
	private int unit;

	public SIMRecord(RecordTrade recordTrade, int unit) {
		if (recordTrade == null) {
			throw new NullPointerException("RecordTrade must not be null");
		}
		this.recordTrade = recordTrade;
		this.unit = unit;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	@Override
	public String GetAmountTrade() {
		return this.recordTrade.GetAmountTrade();
	}

	@Override
	public String GetExceededofTrade() {
		return this.recordTrade.GetExceededofTrade();
	}

	@Override
	public String GetTradeDate() {
		return this.recordTrade.GetTradeDate();
	}

	@Override
	public String GetTradeSSC() {
		return this.recordTrade.GetTradeSSC();
	}

	@Override
	public String GetTradeTime() {
		return this.recordTrade.GetTradeTime();
	}

	/**
	 * //"01" - 补贴钱包充值 "02" - 主钱包充值 "05" - 补贴钱包消费 "06" - 主钱包消费
	 */
	@Override
	public String GetTradeType() {
		return this.recordTrade.GetTradeType();
	}

	public String getTradeTypeString() {
		if ("01".equals(GetTradeType())) {
			return "补贴钱包充值";
		} else if ("02".equals(GetTradeType())) {
			return "主钱包充值";
		} else if ("05".equals(GetTradeType())) {
			return "补贴钱包消费";
		} else if ("06".equals(GetTradeType())) {
			return "主钱包消费";
		}
		return "";
	}

	public String getAccountString() {
		int account = Method.byteArrayToInt(
				Method.str2bytes(recordTrade.GetAmountTrade()), 0);
		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
		return df.format(account/100.0);
	}

	@Override
	public String GetposId() {
		return this.recordTrade.GetposId();
	}

	@Override
	public Date getDate() {
		System.out.println(GetTradeDate());
		System.out.println(GetTradeTime());
		try {
			return DATE_FORMAT.parse(GetTradeDate()+GetTradeTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return new Date();
	}
	
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	

}
