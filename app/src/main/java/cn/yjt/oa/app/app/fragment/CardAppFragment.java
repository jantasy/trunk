package cn.yjt.oa.app.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.adapter.AppListViewAdapter;
import cn.yjt.oa.app.app.controller.AppType;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class CardAppFragment extends BaseFragment{
	private PullToRefreshListView listView;
	private AppListViewAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_fragment_app, container, false);
		listView = (PullToRefreshListView) view.findViewById(R.id.lv_app_list);
		adapter = new AppListViewAdapter(getActivity(), AppType.APPS_CARDAPP);
		listView.setAdapter(adapter);
		return view;
	}
}
