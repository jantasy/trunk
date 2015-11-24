package cn.yjt.oa.app.app.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.yjt.oa.app.app.controller.AppDetailsItem;
import cn.yjt.oa.app.app.controller.AppItem;
import cn.yjt.oa.app.beans.AppInfo;

public class AppFragmentHolder implements ViewCreate {
	protected LayoutInflater inflater;
	protected ViewGroup container;
	protected Bundle savedInstanceState;
	
	protected Context context;
	protected AppInfo appInfo;
	protected AppItem appItem;
	
	public AppFragmentHolder(Context context, AppInfo appInfo) {
		this.context = context;
		this.appInfo = appInfo;
		appItem = new AppDetailsItem(context,appInfo);
	}

	public void setView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		this.container = container;
		this.savedInstanceState = savedInstanceState;
	}

	@Override
	public View createView() {
		return null;
	}

}
