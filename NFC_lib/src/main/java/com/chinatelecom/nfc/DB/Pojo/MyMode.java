package com.chinatelecom.nfc.DB.Pojo;

/**
 * 最初的手机模式，用于还原手机原模式。
 * @author ycliuc
 *
 */

public class MyMode{
	public Integer id;
	public String name;
	/**
	 * off:振动
	 * on:响铃
	 * default:默认
	 */
	public  Integer muteMode;
	public  Integer wifiSwitch;
	public  Integer digitalSwitch;
	public  Integer bluetooth;
	public  String wifiSSID;
	public  String wifiPassword;
	public String content;
	/**
	 * <ul>
	 * <li>1我的模式</li>
	 * <li>6自定义模式</li>
	 * </ul>
	 */
	public Integer modeFlag;
	
	public MyMode(Integer id, String name, Integer muteMode,
			Integer wifiSwitch, Integer digitalSwitch,
			Integer bluetooth, String wifiSSID,
			String wifiPassword, String content, Integer modeFlag) {
		super();
		this.id = id;
		this.name = name;
		this.muteMode = muteMode;
		this.wifiSwitch = wifiSwitch;
		this.digitalSwitch = digitalSwitch;
		this.bluetooth = bluetooth;
		this.wifiSSID = wifiSSID;
		this.wifiPassword = wifiPassword;
		this.content = content;
		this.modeFlag = modeFlag;
	}

	


	

}
