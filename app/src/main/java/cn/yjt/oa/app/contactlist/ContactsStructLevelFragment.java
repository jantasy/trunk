package cn.yjt.oa.app.contactlist;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.DeptDetailInfo;
import cn.yjt.oa.app.contactlist.adpter.ContactsStructAdapter;
import cn.yjt.oa.app.contactlist.server.DeptsServerLoader;
import cn.yjt.oa.app.contactlist.server.IServerLoader;
import cn.yjt.oa.app.contactlist.server.IServerLoader.ServerLoaderListener;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

/**联系人组织架构分层显示的fragment*/
public class ContactsStructLevelFragment extends Fragment implements OnItemClickListener, OnRefreshListener ,ContactsStructSynchronous{

	private final static String TAG = "ContactsStructLevelFragment";

	private View root;
	/**下拉刷新上拉加载的listview*/
	private PullToRefreshListView structListView;
	/**listview的适配器*/
	private ContactsStructAdapter adapter;
	/**listview正在刷新*/
	private boolean isRefreshing = false;
	
	private ContactsStructSynchronous Isynchronous;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (root == null) {
			//该fragment的根布局
			root = inflater.inflate(R.layout.fragment_struct_level, container, false);
			//初始化适配器
			adapter = new ContactsStructAdapter(getActivity());
			//初始化控件
			structListView = (PullToRefreshListView) root.findViewById(R.id.struct_listview);
			//填充数据
			DeptDetailInfo depts = getArguments().getParcelable("structs");
			adapter.setData(depts.getXChildren());
			structListView.setAdapter(adapter);
			//设置监听
			structListView.enableFooterView(false);
			structListView.setOnRefreshListener(this);
			structListView.setOnItemClickListener(this);
		}
		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ViewGroup parent = (ViewGroup) root.getParent();
		if (parent != null) {
			parent.removeView(root);
		}
	}

	@Override
	public void onRefresh() {
		if (!isRefreshing) {
			if (getActivity() != null) {
				getActivity().sendBroadcast(new Intent(IServerLoader.ACTION_DEPTS_REFRESHING));
			}
			isRefreshing = true;
			new DeptsServerLoader(new ServerLoaderListener<List<DeptDetailInfo>>() {

				@Override
				public void onSuccess(List<DeptDetailInfo> result) {
				
					if (getActivity() != null) {
						getActivity().sendBroadcast(new Intent(IServerLoader.ACTION_DEPTS_UPDATED));
					}
				}

				@Override
				public void onError() {
					isRefreshing = false;
					structListView.onRefreshComplete();
				}
			}).load();
		} else {
			structListView.onRefreshComplete();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (!isRefreshing) {
			Fragment parentFragment = getParentFragment();
			if (parentFragment instanceof OnStructItemClickListener) {
				((OnStructItemClickListener) parentFragment).onStructItemClick(parent.getItemAtPosition(position));
			}
		}
	}

	/**初始化fragment*/
	public static ContactsStructLevelFragment instantiate(Context context, DeptDetailInfo deptDetailInfo) {
		ContactsStructLevelFragment contactsStructLevelFragment = new ContactsStructLevelFragment();
		LogUtils.e(TAG, "fragment:" + contactsStructLevelFragment);
		Bundle args = new Bundle();
		args.putParcelable("structs", deptDetailInfo);
		contactsStructLevelFragment.setArguments(args);
		
		return contactsStructLevelFragment;
	}

	/**listview中条目点击事件的回调接口*/
	public static interface OnStructItemClickListener {
		public void onStructItemClick(Object item);
	}

	//set、get 方法
	public boolean isRefreshing() {
		return isRefreshing;
	}

	public void setRefreshing(boolean isRefreshing) {
		this.isRefreshing = isRefreshing;
	}
	
	public ContactsStructSynchronous getIsynchronous() {
		return Isynchronous;
	}

	public void setIsynchronous(ContactsStructSynchronous isynchronous) {
		Isynchronous = isynchronous;
	}
	

	@Override
	public void listRefreshing() {
		
	}

	@Override
	public void listRefreshed() {
		if(structListView!=null){
			structListView.onRefreshComplete();
		}
	}
}
