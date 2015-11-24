package cn.yjt.oa.app.app.fragment;

import java.util.List;

import android.os.Bundle;
import cn.yjt.oa.app.app.adapter.AppListViewAdapter;
import cn.yjt.oa.app.app.controller.AppRequest;
import cn.yjt.oa.app.app.controller.AppRequest.AppInfosCallback;
import cn.yjt.oa.app.beans.AppInfo;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class LoadDataFragment extends BaseFragment implements OnLoadMoreListner,OnRefreshListener{
	protected List<AppInfo> listAppInfo;
	protected AppListViewAdapter adapter;
	protected PullToRefreshListView listView;
	protected AppRequest request;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onRefresh() {
		
	}
	
	protected void refresh(final int appType,final RequestCallback callback){
		AppRequest request = new AppRequest(appType);
		request.getApps(0, 20, new AppInfosCallback() {
			
			@Override
			public void onAppInfosResponse(final List<AppInfo> appInfos) {
				if(appInfos != null){
					listAppInfo = appInfos;
					adapter = new AppListViewAdapter(getActivity(),appType);
					adapter.bindData(appInfos);
					adapter.notifyDataSetChanged();
					callback.onFinished();
				}
			}
		});
	}
	
	private static int from = 0;
	private static int max = 20;
	protected void loadMore(final int appType,final RequestCallback callback){
		AppRequest request = new AppRequest(appType);
		request.getApps(from, max, new AppInfosCallback() {
			
			@Override
			public void onAppInfosResponse(final List<AppInfo> appInfos) {
				if(appInfos != null && from != 0 ){
					if(listAppInfo.addAll(appInfos)){
						from = from + max;
						adapter = new AppListViewAdapter(getActivity(),appType);
						adapter.bindData(appInfos);
						adapter.notifyDataSetChanged();
						callback.onFinished();
					}
				}
			}
		});
	}
	
	@Override
	public void onLoadMore() {
		
	}
	
	public interface RequestCallback{
		void onFinished();
	}
	
}
