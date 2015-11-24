package cn.yjt.oa.app.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import cn.yjt.oa.app.app.activity.AdditionalIcon;
import cn.yjt.oa.app.app.controller.AppType;
import cn.yjt.oa.app.app.controller.ItemType;
import cn.yjt.oa.app.app.fragment.AppLocalFragment.LocalAppInfo;
import cn.yjt.oa.app.app.view.AppHolderFactory;
import cn.yjt.oa.app.app.view.AppItemHolder;
import cn.yjt.oa.app.beans.AppInfo;

public class AppListViewAdapter extends BaseAdapter{
	
	private List<AppInfo> listNormal;
	private List<AdditionalIcon> listAddition;
	private List<LocalAppInfo> listLocal;
	
	private Context context;
	private int appType;
	
	public AppListViewAdapter(Context context,int appType){
		this.context = context;
		this.appType = appType;
		initData();
	}
	
	public void initData(){
		listNormal = new ArrayList<AppInfo>();
		listAddition = new ArrayList<AdditionalIcon>();
		listLocal = new ArrayList<LocalAppInfo>();
	}
	
	public void bindData(List<AppInfo> listNormal){
		this.listNormal = listNormal;
	}

	public void bindAdditionalData(List<AdditionalIcon> listAddition){
		this.listAddition = listAddition;
	}
	
	public void bindLocalData(List<LocalAppInfo> listLocal){
		this.listLocal = listLocal;
	}
	
	@Override
	public int getCount() {
		
		if(appType == AppType.APPS_BUSINESS){
			if(listLocal.size() != 0){
				return listLocal.size();
			}
			return listAddition.size()+listNormal.size();
		}else{
			return listNormal.size();
		}
	}
	
	@Override
	public int getViewTypeCount() {
		if(appType == AppType.APPS_BUSINESS){
			return ItemType.ITEM_TYPE_APPS_ADD_COUNT;
		}else{
			return ItemType.ITEM_TYPE_APPS_CARD_COUNT;
		}
	}
	
	@Override
	public int getItemViewType(int position) {
		if (appType == AppType.APPS_BUSINESS) {
			if (position < listAddition.size()) {
				return ItemType.ITEM_TYPE_APPS_ADD_INTERNAL;
			} else if (position >= listAddition.size() && position < listLocal.size() + listAddition.size()) {
				return ItemType.ITEM_TYPE_APPS_ADD_LOCAL;
			}
			return ItemType.ITEM_TYPE_APPS_ADD_NORMAL;
		} else {
			return ItemType.ITEM_TYPE_APPS_CARD_NORMAL;
		}
	}
	
	@Override
	public Object getItem(int position) {
		if (position < listAddition.size()) {
			return new AppInfo(listAddition.get(position));
		} else if(position >= listAddition.size()&&position<listLocal.size()+listAddition.size()){
			return new AppInfo(listLocal.get(position-listAddition.size()));
		}
		return listNormal.get(position-listAddition.size()-listLocal.size());
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AppItemHolder holder = AppHolderFactory.getAppItemHolder(context, (AppInfo)getItem(position), getItemViewType(position),appType);
		holder.setItemView(position, convertView, parent);
		holder.setType(getItemViewType(position),appType);
		return holder.createView();
	}
	
}
