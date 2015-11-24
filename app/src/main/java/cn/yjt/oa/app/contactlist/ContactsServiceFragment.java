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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.CommonContactInfo;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.contactlist.adpter.CommonServiceAdapter;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.contactlist.server.IServerLoader;
import cn.yjt.oa.app.contactlist.server.IServerLoader.ServerLoaderListener;
import cn.yjt.oa.app.contactlist.server.ServicesServerLoader;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class ContactsServiceFragment extends Fragment implements
		OnRefreshListener {

	private PullToRefreshListView publicServiceContactList;
	private View root;
	private CommonServiceAdapter commonServiceAdapter;
	private List<CommonContactInfo> publicServices = new ArrayList<CommonContactInfo>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (root == null) {

			root = inflater.inflate(R.layout.contactlist_public_service,
					container, false);
			publicServiceContactList = (PullToRefreshListView) root
					.findViewById(R.id.contact_service_listView);
			publicServiceContactList.enableFooterView(false);
			publicServiceContactList.setOnRefreshListener(this);
			publicServiceContactList
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							int headerCount = publicServiceContactList
									.getHeaderViewsCount();
							CommonContactInfo info = publicServices
									.get(position - headerCount);
							startPhone(info.getPhone1());
						}
					});
			setPublicServiceAdapter();
		}
		loadAllPublicServiceOnLocal();
		registerServicesUpdatedReceiver();
		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unregisterServicesUpdatedReceiver();
		ViewGroup parent = (ViewGroup) root.getParent();
		if (parent != null) {
			parent.removeView(root);
		}
	}
	
	private void registerServicesUpdatedReceiver() {
		IntentFilter filter = new IntentFilter(
				IServerLoader.ACTION_SERVICES_UPDATED);
		getActivity().registerReceiver(contactsServicesReceiver, filter);
	}

	private void unregisterServicesUpdatedReceiver() {
		getActivity().unregisterReceiver(contactsServicesReceiver);
	}

	private BroadcastReceiver contactsServicesReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			List<CommonContactInfo> result = intent
					.getParcelableArrayListExtra(IServerLoader.EXTRA_SERVICES);
			regreshUi(result);
			
		}
	};

	private void setPublicServiceAdapter() {
		if (publicServiceContactList.getAdapter() == null) {
			if (commonServiceAdapter == null) {
				commonServiceAdapter = new CommonServiceAdapter(getActivity(),
						isAdded());
			}
			publicServiceContactList.setAdapter(commonServiceAdapter);
		} else {
			commonServiceAdapter.notifyDataSetChanged();
		}
	}

	private void startPhone(String phoneNum) {
		ContactlistUtils.startCall(getActivity(), phoneNum);
		/*记录操作 0609*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_DAIL_SERVICE);
	}

	@Override
	public void onRefresh() {
		new ServicesServerLoader(
				new ServerLoaderListener<List<CommonContactInfo>>() {

					@Override
					public void onSuccess(List<CommonContactInfo> result) {
						publicServiceContactList.onRefreshComplete();
						regreshUi(result);
					}

					@Override
					public void onError() {
						publicServiceContactList.onRefreshComplete();
					}
				}).load();
	}

	private void regreshUi(final List<CommonContactInfo> contactInfos) {
		publicServices.clear();
		publicServices.addAll(contactInfos);
		commonServiceAdapter.setPublicServices(publicServices);
		setPublicServiceAdapter();
	}

	private void loadAllPublicServiceOnLocal() {

		new Thread() {
			public void run() {
				final List<CommonContactInfo> temp_list = ContactManager
						.getContactManager(MainApplication.getAppContext())
						.getAllPublicService();
				if (!ContactlistUtils.isEmptyList(temp_list)) {
					sortPublicServices(temp_list);
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							regreshUi(temp_list);
						}

					});
				}
			};
		}.start();
	}

	private void sortPublicServices(List<CommonContactInfo> list) {
		Collections.sort(list, new Comparator<CommonContactInfo>() {

			@Override
			public int compare(CommonContactInfo lhs, CommonContactInfo rhs) {
				int result = new Long(lhs.getId()).compareTo(new Long(rhs
						.getId()));
				return result;
			}
		});
	}
}
