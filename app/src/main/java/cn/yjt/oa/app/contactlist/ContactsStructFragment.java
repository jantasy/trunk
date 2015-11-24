package cn.yjt.oa.app.contactlist;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.DeptDetailInfo;
import cn.yjt.oa.app.beans.DeptDetailUserInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.contactlist.ContactsStructLevelFragment.OnStructItemClickListener;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.contactlist.server.IServerLoader;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.widget.NavigationView;
import cn.yjt.oa.app.widget.NavigationView.OnPopListener;

/**组织架构的Fragment*/
public class ContactsStructFragment extends Fragment implements OnPopListener, OnStructItemClickListener,
		ContactsStructSynchronous {

	/**该Fragment是rootview*/
	private View root;
	/**导航控件*/
	private NavigationView navigationView;
	/**横向scrollview*/
	private HorizontalScrollView horizontalScrollView;
	/**如果加载组织架构失败，显示的提示语句*/
	private TextView textView;
	/**加载组织架构数据时的圆形进度*/
	private ProgressBar progressBar;

	/**定义一个handler*/
	private Handler handler;
	/**封装fragment的集合*/
	private List<Fragment> fragments = new ArrayList<Fragment>();
	/**填充组织架构数据的集合*/
	private List<DeptDetailInfo> data = new ArrayList<DeptDetailInfo>();

	/**防止listview的item被连点多次*/
	private boolean isClicked = false;

	/***/
	public static boolean isRefreshing = false;
	public ContactsStructSynchronous mSynchronous;

	private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (root == null) {
			//当前listview的根布局
			root = inflater.inflate(R.layout.fragment_contacts_struct, container, false);
			//初始化的控件
			progressBar = (ProgressBar) root.findViewById(R.id.pb_load);
			textView = (TextView) root.findViewById(R.id.tv_nostruct);
			navigationView = (NavigationView) root.findViewById(R.id.navigation_view);
			navigationView.setOnPopListener(this);
			horizontalScrollView = (HorizontalScrollView) root.findViewById(R.id.navigation_scrollview);
			//加载组织架构的数据
			if (!MainApplication.isLoadingALLContacts() || !MainApplication.isLoadingDeptContacts) {
				loadDepts();
			}
		}
		registerContactsUpdatedReceiver();
		registerStructsRefeshingReceiver();
		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ViewGroup parent = (ViewGroup) root.getParent();
		if (parent != null) {
			parent.removeView(root);
		}
		unregisterContactsUpdatedReceiver();
		unregisterStructsRefeshingReceiver();
	}

	/**注册通讯录更新的广播监听*/
	private void registerContactsUpdatedReceiver() {
		IntentFilter filter = new IntentFilter(IServerLoader.ACTION_DEPTS_UPDATED);
		filter.addAction(IServerLoader.ACTION_CONTACTS_UPDATED);
		getActivity().registerReceiver(contactsUpdatedReceiver, filter);
	}

	/**取消注册通讯录更新的广播监听*/
	private void unregisterContactsUpdatedReceiver() {
		getActivity().unregisterReceiver(contactsUpdatedReceiver);
	}

	/**通讯录更新的广播*/
	private BroadcastReceiver contactsUpdatedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			loadDepts();
		}
	};

	private void registerStructsRefeshingReceiver() {
		IntentFilter filter = new IntentFilter(IServerLoader.ACTION_DEPTS_REFRESHING);
		getActivity().registerReceiver(structsRefeshingReceiver, filter);
	}

	private void unregisterStructsRefeshingReceiver() {
		getActivity().unregisterReceiver(structsRefeshingReceiver);
	}

	private BroadcastReceiver structsRefeshingReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			navigationView.setCanClick(false);
		}
	};

	/**加载组织架构的数据*/
	private void loadDepts() {
		new Thread() {
			public void run() {
				//从本地数据库获取组织架构相关的数据
				while (MainApplication.isLoadingALLContacts()) {

				}
				final List<DeptDetailInfo> deptDetailInfos = ContactManager.getContactManager(
						MainApplication.getAppContext()).getDeptDetailInfos();
				updateUI(deptDetailInfos);
			}

		}.start();
	}

	private void updateUI(final List<DeptDetailInfo> deptDetailInfos) {
		//如果数据为空，显示提示语句。
		if (deptDetailInfos == null || deptDetailInfos.isEmpty()) {
			handler.post(new Runnable() {
				public void run() {
					textView.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
				}
			});
			return;
		}
		//否则更新数据
		data.clear();
		data.addAll(deptDetailInfos);
		handler.post(new Runnable() {
			@Override
			public void run() {
				DeptDetailInfo deptDetailInfo = new DeptDetailInfo();
				deptDetailInfo.setName(AccountManager.getCurrent(getActivity()).getCustName());
				deptDetailInfo.setChildren(deptDetailInfos);
				progressBar.setVisibility(View.GONE);
				showLevelFragment(deptDetailInfo, true);
			}
		});
	}

	private void reqiestDepts(Listener<Response<List<DeptDetailInfo>>> listener) {
		Type responseType = new TypeToken<Response<List<DeptDetailInfo>>>() {
		}.getType();
		new AsyncRequest.Builder().setModule(buildDeptModule()).setResponseType(responseType)
				.setResponseListener(listener).build().get();
	}

	private String buildDeptModule() {
		return String.format(AsyncRequest.MODULE_CUSTS_DEPTS,
				String.valueOf(AccountManager.getCurrent(MainApplication.getAppContext()).getCustId()));
	}

	/**展示组织架构分层显示的fragment*/
	private void showLevelFragment(DeptDetailInfo deptDetailInfo, boolean isFresh) {
		System.out.println("showLevelFragment:" + deptDetailInfo);
		if (deptDetailInfo != null) {
			root.findViewById(R.id.navigation_scrollview).setVisibility(View.VISIBLE);
			final ContactsStructLevelFragment instantiate = ContactsStructLevelFragment.instantiate(getActivity(),
					deptDetailInfo);
			this.mSynchronous = instantiate;
			if (isFresh) {
				navigationView.clear();
				fragments.clear();
			}
			horizontalScrollView.post(new Runnable() {

				@Override
				public void run() {
					int scrollTo = 0;
					final int count = ((LinearLayout) horizontalScrollView.getChildAt(0)).getChildCount();
					for (int i = 0; i < count; i++) {
						final View child = ((LinearLayout) horizontalScrollView.getChildAt(0)).getChildAt(i);
						scrollTo += child.getWidth();
					}
					horizontalScrollView.scrollTo(scrollTo, 0);
				}
			});

			navigationView.push(deptDetailInfo.getName());
			fragments.add(instantiate);
			getChildFragmentManager().beginTransaction().add(R.id.struct_container, instantiate)
					.commitAllowingStateLoss();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isClicked = false;
					instantiate.setRefreshing(false);
					isRefreshing = false;
					if (mSynchronous != null) {
						mSynchronous.listRefreshed();
					}
					navigationView.setCanClick(true);
				}
			}, 500);

		}
	}

	@Override
	public void onPop(int index) {
		if (!isRefreshing) {
			Iterator<Fragment> iterator = fragments.iterator();
			FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
			while (iterator.hasNext()) {
				Fragment fragment = iterator.next();
				if (fragments.indexOf(fragment) > index) {
					iterator.remove();
					beginTransaction.remove(fragment);
				}
			}
			beginTransaction.commit();
		}
		isClicked = false;
	}

	@Override
	public void onStructItemClick(Object item) {

		if (item instanceof DeptDetailInfo) {
			if (!isClicked) {
				isClicked = true;
				showLevelFragment((DeptDetailInfo) item, false);
			}
		} else if (item instanceof DeptDetailUserInfo) {
			DeptDetailUserInfo info = (DeptDetailUserInfo) item;
			if (info.getStatus() == 3) {
				String dept = ContactlistUtils.getDeptName(data, info);
				ContactInfo contactInfo = ContactManager.getContactManager(getActivity()).getContactInfoById(
						info.getUserId());
				contactInfo.setDepartment(dept);
				contactInfo.setPosition(info.getPosition());
				ContactDetailActivity.launchWithStructContact(getActivity(), contactInfo);

				  /*记录操作 0611*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_PERSONINFO_STRUCT);
			}
		}

	}

	@Override
	public void listRefreshing() {
		isRefreshing = true;
	}

	@Override
	public void listRefreshed() {

	}
}
