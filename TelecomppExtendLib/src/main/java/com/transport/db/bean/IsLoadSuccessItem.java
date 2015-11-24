package com.transport.db.bean;

import com.telecompp.util.AlgoManager;

public class IsLoadSuccessItem {

	private	String applicationSN;	//	交易序列号
	private String consumeCount;	//	消费计数器
	private String loadCount;	//	充值计数器
	private String tac;	//	TAC
	private String tranResultFlag;	//	交易状态标识
	private String tradeNum;	//	交易流水号
	private String phone;	//	电话号码
	private String balance;	//	交易前余额
	private String md5;	//	MD5校验吗
	
	/**
	 * 将 xml 交易时间和交易金额计算MD5值，用于校验数据的合法性
	 */
	private String initMd5(){
//		String count = xml.substring(xml.length()/2)+trade_time+trade_money+type;
		String count = applicationSN + consumeCount + loadCount + tac + tranResultFlag
				+ tradeNum + phone + balance;
		return AlgoManager.MD5(count);
	}
	
	public String getApplicationSN() {
		return applicationSN;
	}
	public void setApplicationSN(String applicationSN) {
		this.applicationSN = applicationSN;
	}
	public String getConsumeCount() {
		return consumeCount;
	}
	public void setConsumeCount(String consumeCount) {
		this.consumeCount = consumeCount;
	}
	public String getLoadCount() {
		return loadCount;
	}
	public void setLoadCount(String loadCount) {
		this.loadCount = loadCount;
	}
	public String getTac() {
		return tac;
	}
	public void setTac(String tac) {
		this.tac = tac;
	}
	public String getTranResultFlag() {
		return tranResultFlag;
	}
	public void setTranResultFlag(String tranResultFlag) {
		this.tranResultFlag = tranResultFlag;
	}
	public String getTradeNum() {
		return tradeNum;
	}
	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5() {
		this.md5 = this.initMd5();
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	/**
	 * 返回数据是否合法
	 * true 合法数据false非法数据
	 * @return
	 */
	public boolean isValid(){
		return initMd5().equals(md5);
	}

}
