package cn.yjt.oa.app.app.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.activity.AdditionalIcon;
import cn.yjt.oa.app.app.activity.AppDetailsActivity;
import cn.yjt.oa.app.app.adapter.AppListViewAdapter;
import cn.yjt.oa.app.app.controller.AppRequest;
import cn.yjt.oa.app.app.controller.AppRequest.AppInfosCallback;
import cn.yjt.oa.app.app.controller.AppType;
import cn.yjt.oa.app.app.utils.AdditionalIconUtils;
import cn.yjt.oa.app.beans.AppInfo;
import cn.yjt.oa.app.utils.CheckNetUtils;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class ActivityAppFragment extends BaseFragment implements
		AppInfosCallback, OnRefreshListener, OnLoadMoreListner,OnItemClickListener {
	private AppListViewAdapter adapter;
	private PullToRefreshListView listView;
	private ProgressBar loadProgress;
	private List<AppInfo> listAppInfo;
	private List<AdditionalIcon> listAdditional;
	private List<String> listFilter;
	private int appType;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listAdditional = getActivity().getIntent().getParcelableArrayListExtra(AdditionalIconUtils.EXTRA_REQUEST_ADDICONS);
		if(listAdditional == null){
			listAdditional = new ArrayList<AdditionalIcon>();
		}
		listFilter = getActivity().getIntent().getStringArrayListExtra(AdditionalIconUtils.EXTRA_REQUEST_ADDITIONAL);
		appType = AppType.getAppType(getActivity().getIntent().getAction());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_fragment_app, container, false);
		initView(view);
		initData();
		return view;
	}
	
	public void initView(View view){
		loadProgress = (ProgressBar) view.findViewById(R.id.app_progress);
		loadProgress.setVisibility(View.VISIBLE);
		if(!CheckNetUtils.hasNetWork(getActivity())){
			loadProgress.setVisibility(View.GONE);
		}
		listView = (PullToRefreshListView) view.findViewById(R.id.lv_app_list);
		listView.setOnRefreshListener(this);
		listView.setOnLoadMoreListner(this);
		adapter = new AppListViewAdapter(getActivity(),appType);
	}
	public void initData(){
		new AppRequest(appType).getApps(0, 20,this);
		listView.setOnItemClickListener(this);
		if(appType == AppType.APPS_BUSINESS){
			adapter.bindAdditionalData(listAdditional);
		}
		listView.setAdapter(adapter);
	}
	
	@Override
	public void onAppInfosResponse(List<AppInfo> appInfos) {
		loadProgress.setVisibility(View.GONE);
		if(appInfos != null){
			listAppInfo = appInfos;
			filter(listAppInfo);
			adapter.bindData(appInfos);
			adapter.notifyDataSetChanged();
		}
	}
	
	private void filter(List<AppInfo> list){
		List<AppInfo> temp = new ArrayList<AppInfo>();
		if(listFilter != null){
			for(String filter:listFilter){
				for(AppInfo appInfo:list){
					if(appInfo!=null && filter != null){
						
					if(filter.equals(appInfo.getPackageName())){
						temp.add(appInfo);
					}
					}
				}
			}
		}
		list.removeAll(temp);
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		// TODO 未作类型选择处理
		if(appType == AppType.APPS_BUSINESS){
			int installedSize = listAdditional.size();
			if( id >= installedSize ){
				id = id - installedSize;
				goActivity(id,listAppInfo.get((int)id));
			}
		}else{
			goActivity(id,listAppInfo.get((int)id));
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		adapter.notifyDataSetChanged();
	}
	
	public void goActivity(long id,AppInfo appInfo){
		Intent intent = new Intent(getActivity(), AppDetailsActivity.class);
		intent.putExtra("appInfo", appInfo);
		intent.setAction(AppRequest.ACTION_APP_DETAILS);
		startActivity(intent);
	}

	@Override
	public void onRefresh() {
		new AppRequest(appType).getApps(0, 20, new AppInfosCallback() {
			
			@Override
			public void onAppInfosResponse(final List<AppInfo> appInfos) {
				if(appInfos != null){
					listAppInfo = appInfos;
					adapter = new AppListViewAdapter(getActivity(),appType);
					adapter.bindData(appInfos);
					adapter.notifyDataSetChanged();
					listView.onRefreshComplete();
				}
			}
		});
	}
	
	private static int from = 0;
	private static int max = 20;
	@Override
	public void onLoadMore() {
		new AppRequest(appType).getApps(from, max, new AppInfosCallback() {
			
			@Override
			public void onAppInfosResponse(final List<AppInfo> appInfos) {
				if(appInfos != null && from != 0 ){
					if(listAppInfo.addAll(appInfos)){
						from = from + max;
						adapter = new AppListViewAdapter(getActivity(),appType);
						adapter.bindData(appInfos);
						adapter.notifyDataSetChanged();
					}
				}
				listView.onLoadMoreComplete();
			}
		});
	}
}
