package cn.yjt.oa.app.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.yjt.oa.app.app.controller.AppType;
import cn.yjt.oa.app.app.controller.ItemType;
import cn.yjt.oa.app.app.view.AppHolderFactory;
import cn.yjt.oa.app.app.view.AppItemHolder;
import cn.yjt.oa.app.beans.AppInfo;

public class ActivityDetailsFragment extends BaseFragment {
	private AppInfo appInfo;
	private AppItemHolder holder;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appInfo = getActivity().getIntent().getParcelableExtra("appInfo");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		holder = AppHolderFactory.getAppItemHolder(getActivity(), appInfo, ItemType.ITEM_TYPE_APPS_DEFAULT, AppType.APPS_DEFAULT);
		holder.setView(inflater, container, savedInstanceState);
		holder.setType(ItemType.ITEM_TYPE_APPS_DEFAULT, AppType.APPS_DEFAULT);
		return holder.createView();
	}
}
