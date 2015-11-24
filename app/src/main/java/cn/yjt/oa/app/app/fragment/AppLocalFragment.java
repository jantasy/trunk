package cn.yjt.oa.app.app.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.activity.AdditionalPackage;
import cn.yjt.oa.app.app.adapter.AppListViewAdapter;
import cn.yjt.oa.app.app.controller.AppType;
import cn.yjt.oa.app.app.utils.AdditionalIconUtils;
import cn.yjt.oa.app.app.utils.AppUtils;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class AppLocalFragment extends BaseFragment{
	private PullToRefreshListView listView;
	private AppListViewAdapter adapter;
	private List<String> listFilter;
	private List<LocalAppInfo> localAppInfos;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listFilter = getActivity().getIntent().getStringArrayListExtra(AdditionalIconUtils.EXTRA_REQUEST_ADDITIONAL);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_fragment_app, container, false);
		listView = (PullToRefreshListView) view.findViewById(R.id.lv_app_list);
		listView.enableFooterView(false);
		listView.enableHeaderView(false);
		adapter = new AppListViewAdapter(getActivity(), AppType.APPS_BUSINESS);
		loadLocalAppInfos();
		listView.setAdapter(adapter);
		return view;
	}
	private void loadLocalAppInfos() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				localAppInfos = getLocalAppInfos();
				localAppInfoFilter(localAppInfos);
				return null;
			}
			protected void onPostExecute(Void result) {
				adapter.bindLocalData(localAppInfos);
				adapter.notifyDataSetChanged();
			};
		}.execute();
	}
	
	private void localAppInfoFilter(List<LocalAppInfo> list){
		List<LocalAppInfo> temp = new ArrayList<LocalAppInfo>();
		if(listFilter != null){
			for(String filter:listFilter){
				for(LocalAppInfo appInfo:list){
					if(filter != null && appInfo != null){
						
					if(filter.equals(appInfo.getPackageName())){
						temp.add(appInfo);
					}
					}
				}
			}
		}
		list.removeAll(temp);
	}
	
	public List<LocalAppInfo> getLocalAppInfos(){
		List<ResolveInfo> resolveInfos = AppUtils.getResolveInfos(getActivity());
		List<LocalAppInfo> localAppInfos = new ArrayList<LocalAppInfo>();
		PackageManager pm = getActivity().getPackageManager();
		for(ResolveInfo info:resolveInfos){
			LocalAppInfo localAppInfo = new LocalAppInfo();
			localAppInfo.setTitle(info.loadLabel(pm).toString());
			localAppInfo.setIcon(info.loadIcon(pm));
			localAppInfo.setDescription("系统已安装应用");
			localAppInfo.setPackageName(info.activityInfo.packageName);
			localAppInfos.add(localAppInfo);
		}
		return localAppInfos;
	}
	
	public class LocalAppInfo implements AdditionalPackage{
		private Drawable icon;
		private String title;
		private String description;
		private String packageName;
		
		public void setPackageName(String packageName){
			this.packageName = packageName;
		}
		
		public void setIcon(Drawable icon) {
			this.icon = icon;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			
		}

		@Override
		public Drawable getIcon(Context context) {
			return icon;
		}

		@Override
		public String getTitle(Context context) {
			return title;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getPackageName() {
			return packageName;
		}
		
	}
}
