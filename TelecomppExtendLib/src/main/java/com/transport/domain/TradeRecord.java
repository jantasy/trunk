package com.transport.domain;
/**
 * 该类用于表示公交卡内的交易记录
 * @author poet 
 *
 */
public class TradeRecord {
	private String tradeNum;//交易流水号
	private String tradeType;//交易类型
	private String state;//交易状态
	private String tradeMoney;//交易金额
	private String tradeTime;//交易时间
	public String getTradeNum() {
		return tradeNum;
	}
	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getTradeMoney() {
		return tradeMoney;
	}
	public void setTradeMoney(String tradeMoney) {
		this.tradeMoney = tradeMoney;
	}
	public String getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}
	
	
}
