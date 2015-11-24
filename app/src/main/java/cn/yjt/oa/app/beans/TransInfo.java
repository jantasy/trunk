package cn.yjt.oa.app.beans;

import java.text.ParseException;
import java.util.Date;

import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.widget.TimeLineAdapter.DateItem;

/**
 * 消费记录的实体类
 *
 */
public class TransInfo implements DateItem {

	public static final int WALLET_TYPE_MAIN = 1;
	public static final int WALLET_TYPE_ALLOWANCE = 2;
	public static final int WALLET_TYPE_ALLOWANCE_TIMES = 3;

	public static int getWalletTypeAllowanceTimes() {
		return WALLET_TYPE_ALLOWANCE_TIMES;
	}

	public String getCardTransSan() {
		return cardTransSan;
	}

	//交易时间TRANS_DATE；格式：YYYYMMDDHHMMSS
	private String transTime;
	//交易金额TRANS_AMOUNT，单位（分）
	private int transAmount;
	//刷卡机具名称（暂时没用）
	private String terminalName;
	private int walletType;
	//卡片交易号CARD_TRAN_SN，圈存或者划账系统流水号
	private String cardTransSan;
	private Date date;
	
	//充值类型；1.充值成功，2.补贴成功，3.充值已付款，待领取，4.补贴待领取，5.已申请退款，6.已退款成功，7.交易失败
	private int rechargeType;
	//与rechargeType对应，用于客户端显示
	private String rechargeDesc;
	
	//记录类型，默认0：消费记录，1：充值记录
	private int type;//0.消费 1.充值
	
	public void setCardTransSan(String cardTransSan) {
		this.cardTransSan = cardTransSan;
	}

	public int getRechargeType() {
		return rechargeType;
	}

	public void setRechargeType(int rechargeType) {
		this.rechargeType = rechargeType;
	}

	public String getRechargeDesc() {
		return rechargeDesc;
	}

	public void setRechargeDesc(String rechargeDesc) {
		this.rechargeDesc = rechargeDesc;
	}
	

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public int getTransAmount() {
		return transAmount;
	}

	public void setTransAmount(int transAmount) {
		this.transAmount = transAmount;
	}

	public String getTerminalName() {
		return terminalName;
	}

	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}

	public int getWalletType() {
		return walletType;
	}

	public void setWalletType(int walletType) {
		this.walletType = walletType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public Date getDate() {
		if (date == null) {
			try {
				date = BusinessConstants.parseTime(transTime);
			} catch (ParseException e) {
				// ignore
			}
		}
		return date;
	}

	@Override
	public String toString() {
		return "TransInfo [transTime=" + transTime + ", transAmount=" + transAmount + ", terminalName=" + terminalName
				+ ", walletType=" + walletType + ", cardTransSan=" + cardTransSan + ", date=" + date
				+ ", rechargeType=" + rechargeType + ", rechargeDesc=" + rechargeDesc + ", type=" + type + "]";
	}

	
}
