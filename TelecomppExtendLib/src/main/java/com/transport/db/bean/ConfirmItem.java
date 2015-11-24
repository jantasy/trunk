package com.transport.db.bean;

import java.security.MessageDigest;

import com.telecompp.util.AlgoManager;
import com.transport.ConvertUtil;
import com.transport.cypher.KEYSUtil;


public class ConfirmItem {

	private String _id;//订单唯一标识
	private String xml;//进行确认的XML数据
	/**
	 * 0 表示获取MAC2失败
	 * 1 表示获取MAC2 成功 但是写卡失败
	 * 2 表示写卡成功
	 */ 
	private String tradeNum;
	private String type;
	private String stat = "0";//确认状态,0表示未确认的订单，1表示确认订单
	private String trade_time;//交易时间
	private String trade_money;//交易金额
	private String city;//公交城市
	private String md5;//用于验证数据的合法性
	
	
	
	
	public String getTradeNum() {
		return tradeNum;
	}
	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 将 xml 交易时间和交易金额计算MD5值，用于校验数据的合法性
	 */
	private String initMd5(){
		String count = xml.substring(xml.length()/2)+trade_time+trade_money+type;
		return AlgoManager.MD5(count);
	}
	/**
	 * 计算记录的MD5值
	 */
	public void setMd5(){
		this.md5 = initMd5();
	}
	public void setMd5(String md5){
		this.md5 = md5;
	}
	public String getMd5(){
		return this.md5;
	}
	/**
	 * 返回数据是否合法
	 * true 合法数据false非法数据
	 * @return
	 */
	public boolean isValid(){
		return initMd5().equals(md5);
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	public String getTrade_time() {
		return trade_time;
	}
	public void setTrade_time(String trade_time) {
		this.trade_time = trade_time;
	}
	public String getTrade_money() {
		return trade_money;
	}
	public void setTrade_money(String trade_money) {
		this.trade_money = trade_money;
	}
	@Override
	public String toString() {
		return "ConfirmItem [_id=" + _id + ", xml=" + xml + ", stat=" + stat
				+ ", trade_time=" + trade_time + ", trade_money=" + trade_money
				+ ", city=" + city + "]";
	}
}
