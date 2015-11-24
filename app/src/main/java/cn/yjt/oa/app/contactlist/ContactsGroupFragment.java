package cn.yjt.oa.app.contactlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.GroupInfo;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.contactlist.adpter.GroupAdapter;
import cn.yjt.oa.app.contactlist.data.ContactlistGroupInfo;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.contactlist.server.GroupsServerLoader;
import cn.yjt.oa.app.contactlist.server.IServerLoader;
import cn.yjt.oa.app.contactlist.server.IServerLoader.ServerLoaderListener;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshExpandableListView;

public class ContactsGroupFragment extends Fragment implements
		OnRefreshListener {
	
	public static final String ACTION_REFRESH_GROUPS = "cn.yjt.oa.app.contactlist.ACTION_REFRESH_GROUPS";
	public static final String ACTION_RELOAD_GROUPS = "cn.yjt.oa.app.contactlist.ACTION_RELOAD_GROUPS";
	
	private View root;
	private PullToRefreshExpandableListView groupContactList;
	private GroupAdapter groupAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(root == null){
			
		root = inflater.inflate(R.layout.contactlist_groups, container, false);
		groupContactList = (PullToRefreshExpandableListView) root
				.findViewById(R.id.contact_groups_listView);

		groupContactList.setOnRefreshListener(this);
		groupContactList.enableFooterView(false);

		groupContactList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				return false;
			}
		});
		groupAdapter = new GroupAdapter(getActivity(), isAdded(), 0,
				clickListener);
		groupContactList.setAdapter(groupAdapter);
		loadAllGroupsOnLocal();
		}
		getActivity().registerReceiver(groupRefreshReceiver, new IntentFilter(ACTION_REFRESH_GROUPS) );
		getActivity().registerReceiver(groupReloadReceiver, new IntentFilter(ACTION_RELOAD_GROUPS) );
		registerContactsUpdatedReceiver();
		return root;
	}
	
	private void registerContactsUpdatedReceiver() {
		IntentFilter filter = new IntentFilter(
				IServerLoader.ACTION_GROUPS_UPDATED);
		getActivity().registerReceiver(contactsUpdatedReceiver, filter);
	}

	private void unregisterContactsUpdatedReceiver() {
		getActivity().unregisterReceiver(contactsUpdatedReceiver);
	}

	private BroadcastReceiver contactsUpdatedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			List<GroupInfo> result = intent
					.getParcelableArrayListExtra(IServerLoader.EXTRA_GROUPS);
			updateListView(result);
		}
	};
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ViewGroup parent = (ViewGroup) root.getParent();
		if (parent != null) {
			parent.removeView(root);
		}
		getActivity().unregisterReceiver(groupRefreshReceiver);
		getActivity().unregisterReceiver(groupReloadReceiver);
		unregisterContactsUpdatedReceiver();
	}
	
	private BroadcastReceiver groupRefreshReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			groupContactList.setRefreshingState();
			onRefresh();
		}
	};
	
	private BroadcastReceiver groupReloadReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			loadAllGroupsOnLocal();
		}
	};

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int key = v.getId();
			switch (key) {
			/*
			 * case R.id.group_indicator: setGroupExpand(v); break;
			 */
			case R.id.group_item_expandable_show_parent:
				setGroupExpand((RelativeLayout) v);
				break;

			}

		}
	};

	private void setGroupExpand(RelativeLayout view) {
		int groupPosition = (Integer) view.getTag();
		boolean isExpand = groupContactList.isGroupExpanded(groupPosition);
		ImageView imageView = (ImageView) view.getChildAt(0);
		if (isExpand) {
			groupContactList.collapseGroup(groupPosition);
			imageView.setImageResource(R.drawable.contact_list_expandable_show);
		} else {
			groupContactList.expandGroup(groupPosition);
			imageView.setImageResource(R.drawable.contact_list_expandable_hide);
		}
	}
	private void updateListView(List<GroupInfo> groups_temp) {
		if (groups_temp != null) {
			final List<ContactlistGroupInfo> temp = getContactlistGroupInfo(groups_temp);
			sortGroups(temp);
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					groupAdapter.setGroupInfos(temp);
					groupAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	private void loadAllGroupsOnLocal() {
		new Thread() {
			@Override
			public void run() {
				List<GroupInfo> groups_temp = ContactManager.getContactManager(
						MainApplication.getAppContext()).loadAllLocalGroups();
				updateListView(groups_temp);
			}

			
		}.start();

	}

	private List<ContactlistGroupInfo> getContactlistGroupInfo(
			List<GroupInfo> groups) {
		List<ContactlistGroupInfo> temp = new ArrayList<ContactlistGroupInfo>();
		for (GroupInfo info : groups) {
			ContactlistGroupInfo groupInfo = new ContactlistGroupInfo(info);
			groupInfo.setUsers(getGroupChildsInfo(info.getUsers()));
			temp.add(groupInfo);
		}
		return temp;
	}

	private ContactInfo[] getGroupChildsInfo(UserSimpleInfo[] infos) {
		List<ContactInfo> list = ContactManager.getContactManager(
				MainApplication.getAppContext()).getGroupMembersInfo(infos);
		ContactlistUtils.sortGroupChild(list, MainApplication.getAppContext());
		return list.toArray(new ContactInfo[list.size()]);
	}

	private void sortGroups(List<ContactlistGroupInfo> list) {
		Collections.sort(list, new Comparator<ContactlistGroupInfo>() {

			@Override
			public int compare(ContactlistGroupInfo lhs,
					ContactlistGroupInfo rhs) {
				int reuslt = new Long(lhs.getInfo().getId())
						.compareTo(new Long(rhs.getInfo().getId()));
				return reuslt;
			}
		});
	}

	@Override
	public void onRefresh() {
		new GroupsServerLoader(new ServerLoaderListener<List<GroupInfo>>() {

			@Override
			public void onSuccess(List<GroupInfo> result) {
				groupContactList.onRefreshComplete();
				if (!ContactlistUtils.isEmptyList(result)) {
					final List<ContactlistGroupInfo> temp = getContactlistGroupInfo(result);
					sortGroups(temp);
					groupAdapter.setGroupInfos(temp);
					groupAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onError() {
				groupContactList.onRefreshComplete();
			}
		}).load();
		;
	}
}
