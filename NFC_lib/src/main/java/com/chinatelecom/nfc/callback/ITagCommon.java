package com.chinatelecom.nfc.callback;

public interface ITagCommon {
	/**
	 * 初始化控制方法
	 */
	public void init();
	
	/**
	 * 向xml的控件赋值，显示数据
	 */
	public void showView();
	
	public void edit(boolean isEdit);
	
	/**
	 * 检验
	 */
	public boolean check();
}
