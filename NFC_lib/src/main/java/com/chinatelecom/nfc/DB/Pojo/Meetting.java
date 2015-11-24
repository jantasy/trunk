package com.chinatelecom.nfc.DB.Pojo;

public class Meetting {
	/**
	 * ID
	 */
	public Integer id;
	/**
	 * 名称
	 */
	public String n;
	/**
	 * 参与人员
	 */
	public String p;
	/**
	 * 会议地点
	 */
	public String pl;
	/**
	 * 会议内容
	 */
	public String c;

	/**
	 * 会议开始时间
	 */
	public Long st;
	/**
	 * 手机模式
	 */
	public String mm;
	public String ss;
	public String pw;

	public Meetting(Integer id, String name, String partner, String place,
			String content, Long startTime, String mode,
			String wifiSSID,String wifiPassword) {
		super();
		this.id = id;
		this.n = name;
		this.p = partner;
		this.pl = place;
		this.c = content;
		this.st = startTime;
		this.mm = mode;
		this.ss = wifiSSID;
		this.pw = wifiPassword;
	}

}
