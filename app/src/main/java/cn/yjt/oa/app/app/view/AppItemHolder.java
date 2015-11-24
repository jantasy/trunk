package cn.yjt.oa.app.app.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.yjt.oa.app.app.controller.AppItem;
import cn.yjt.oa.app.app.controller.AppItemFactory;
import cn.yjt.oa.app.beans.AppInfo;

public class AppItemHolder implements ViewCreate{
	
	protected AppInfo appInfo;
	protected View convertView;
	protected int position;
	protected ViewGroup parent;
	protected LayoutInflater inflater;
	protected Context context;
	protected AppItem appItem;
	
	protected Bundle savedInstanceState;

	public AppItemHolder(Context context, AppInfo appInfo) {
		this.context = context;
		this.appInfo = appInfo;
		inflater = LayoutInflater.from(context);
	}
	public AppItemHolder setItemView(int position,View convertView,ViewGroup parent){
		this.position = position;
		this.convertView = convertView;
		this.parent = parent;
		return this;
	}
	
	public void setView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		this.parent = container;
		this.savedInstanceState = savedInstanceState;
	}
	
	public AppItemHolder setType(int viewType,int appType){
		appItem = AppItemFactory.getAppItem(context, appInfo, viewType,appType);
		return this;
	}
	@Override
	public View createView() {
		return null;
	}
}
