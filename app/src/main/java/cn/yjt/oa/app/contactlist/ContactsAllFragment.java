package cn.yjt.oa.app.contactlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.CommonContactInfo;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.contactlist.adpter.ContactAllAdapter;
import cn.yjt.oa.app.contactlist.adpter.ContactsPublicServiceAdapter;
import cn.yjt.oa.app.contactlist.adpter.SearchResultAdapter;
import cn.yjt.oa.app.contactlist.data.ContactlistContactInfo;
import cn.yjt.oa.app.contactlist.data.SearchResultInfo;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.contactlist.server.ContactsServerLoader;
import cn.yjt.oa.app.contactlist.server.IServerLoader;
import cn.yjt.oa.app.contactlist.server.IServerLoader.ServerLoaderListener;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.contactlist.view.IndexView;
import cn.yjt.oa.app.contactlist.view.IndexView.OnIndexChoseListener;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class ContactsAllFragment extends Fragment implements OnClickListener, OnIndexChoseListener, OnRefreshListener {

	private final String TAG = "ContactsAllFragment";
	private static final String INDEX_DEFAULT_TOP = "↑";
	private static final String INDEX_DEFAULT_REGISTER = "#";
	private static final String INDEX_DEFAULT_UNREGISTER = "*";
	private static final String[] INDEXSTR = { INDEX_DEFAULT_TOP, INDEX_DEFAULT_REGISTER, "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
			INDEX_DEFAULT_UNREGISTER };

	private View root;
	private EditText contactSearchInput;
	private ImageView serachIcon;
	private LinearLayout textClear;
	private TextView indexShow;
	private IndexView indexLayout;
	private ImageView contactSearchClick;
	private ListView searchResultListView;
	private TextView noResult;
	private LinearLayout resultViewParent;
	private View searchParent;
	private View topDivider;
	private FrameLayout flContent;
	private ProgressBar pbLoading;
	private PullToRefreshListView allContactList;

	private static Handler mHandler = new Handler();
	private boolean isResultViewParentVisible = false;

	private boolean isCancleSearchButtonVisible = false;
	private Button contactSearchCancle;

	private HashMap<String, Integer> allContactIndexPosInList = new HashMap<String, Integer>();
	private List<SearchResultInfo> pageAllResult = new ArrayList<SearchResultInfo>();
	private SearchResultAdapter resultAdapter;

	private List<SearchResultInfo> searchResult = new ArrayList<SearchResultInfo>();
	private List<ContactlistContactInfo> allContacts = new ArrayList<ContactlistContactInfo>();

	private String currentText;

	private ContactManager manager;

	private Set<String> allContactIndexs = new TreeSet<String>();
	private boolean onContactsLoadFinish = true;

	public static final int CONTACTLIST_STRAT_MODEL_NORMAL = 0;
	public static final int CONTACTLIST_STRAT_MODEL_MULITCHOICE = 1;
	private int model = CONTACTLIST_STRAT_MODEL_NORMAL;

	private ContactAllAdapter contactAllAdapter;

	private List<UserSimpleInfo> existList;

	private ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		System.out.println("---onCreateView");
		if (root == null) {

			root = inflater.inflate(R.layout.fragment_contactlist_all, container, false);

			allContactList = (PullToRefreshListView) root.findViewById(R.id.contact_list);
			allContactList.enableFooterView(false);
			allContactList.setOnRefreshListener(this);
			allContactList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					HeaderViewListAdapter adapter = (HeaderViewListAdapter) parent.getAdapter();
					ContactlistContactInfo item = (ContactlistContactInfo) adapter.getItem(position);
					if (item.getViewType() == ContactlistContactInfo.VIEW_TYPE_PUBLIC_SERVICE) {
						ContactsServiceActivity.launch(getActivity());

						/*记录操作 0608*/
						OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_WATCH_SERVICE);
					} else {
						intentToContactDetailActivity(item.getContactInfo());

						/*记录操作 0605*/
						OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_PERSONINFO);
					}

				}
			});

			contactSearchCancle = (Button) root.findViewById(R.id.contact_search_cancle);
			contactSearchCancle.setOnClickListener(this);
			contactSearchInput = (EditText) root.findViewById(R.id.address_search_input);
			contactSearchInput.setOnClickListener(this);
			contactSearchInput.setCursorVisible(false);
			contactSearchInput.setText("");

			//Xiong:
			flContent = (FrameLayout) root.findViewById(R.id.fl_content);
			pbLoading = (ProgressBar) root.findViewById(R.id.pb_loading);
			//END
			serachIcon = (ImageView) root.findViewById(R.id.contact_search_icon);
			textClear = (LinearLayout) root.findViewById(R.id.contact_search_clear_img);
			textClear.setOnClickListener(this);
			indexShow = (TextView) root.findViewById(R.id.contact_index_show_text);
			indexLayout = (IndexView) root.findViewById(R.id.contact_index_layout);
			indexLayout.setIndexs(INDEXSTR, this);
			indexLayout.setIndexShowTextView(indexShow);
			contactSearchClick = (ImageView) root.findViewById(R.id.contact_search_click);
			contactSearchClick.setOnClickListener(this);
			searchResultListView = (ListView) root.findViewById(R.id.contact_search_result_list);
			noResult = (TextView) root.findViewById(R.id.contact_search_result_text);
			resultViewParent = (LinearLayout) root.findViewById(R.id.address_search_result_list_parent);
			setResultViewParent();
			searchParent = root.findViewById(R.id.contact_search_parent);
			topDivider = root.findViewById(R.id.top_divider);
			manager = ContactManager.getContactManager(getActivity());
			contactAllAdapter = new ContactAllAdapter(getActivity(), isAdded(), model);
			contactAllAdapter.setContactsServiceAdapter(new ContactsPublicServiceAdapter(getActivity()));
			allContactList.setAdapter(contactAllAdapter);
			setupView();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (!MainApplication.isLoadingALLContacts()) {
						loadAllContactsOnLocal();
					}
				}
			}, 1000);

		}
		registerContactsUpdatedReceiver();
//		Intent intent = new Intent(IServerLoader.ACTION_CONTACTS_UPDATED);
//		if(getActivity()!=null){
//			getActivity().sendBroadcast(intent);
//		}
		return root;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	private void intentToContactDetailActivity(ContactInfo info) {
		Intent intent = new Intent();
		intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, info);
		intent.setClass(getActivity(), ContactDetailActivity.class);
		startActivity(intent);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		System.out.println("---onDestroyView");
		ViewGroup parent = (ViewGroup) root.getParent();
		if (parent != null) {
			parent.removeView(root);
		}
		unregisterContactsUpdatedReceiver();
	}

	/**注册联系人更新广播接收者*/
	private void registerContactsUpdatedReceiver() {
		IntentFilter filter = new IntentFilter(IServerLoader.ACTION_CONTACTS_UPDATED);
		getActivity().registerReceiver(contactsUpdatedReceiver, filter);
	}

	/**解除注册联系人更新广播接受者*/
	private void unregisterContactsUpdatedReceiver() {
		getActivity().unregisterReceiver(contactsUpdatedReceiver);
	}

	/**联系人更新广播接受者*/
	private BroadcastReceiver contactsUpdatedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
//			List<ContactInfo> result = intent.getParcelableArrayListExtra(IServerLoader.EXTRA_CONTACTS);
			LogUtils.d(TAG, "update");
//			loadAllContactsOnLocal();
			if(MainApplication.sContactInfoLists!=null){
				updateUI(MainApplication.sContactInfoLists);
			}
			
		}
	};

	private void setResultViewParent() {
		if (isCancleSearchButtonVisible) {
			contactSearchCancle.setVisibility(View.VISIBLE);
			contactSearchClick.setVisibility(View.VISIBLE);
		} else {
			contactSearchCancle.setVisibility(View.GONE);
			contactSearchClick.setVisibility(View.GONE);
		}

		if (isResultViewParentVisible) {
			resultViewParent.setVisibility(View.VISIBLE);
		} else {
			resultViewParent.setVisibility(View.GONE);
		}

	}

	//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void allContactListIndexChose(String index) {
		int listHeaderCount = allContactList.getHeaderViewsCount();

		if (index.equals(INDEX_DEFAULT_TOP)) {
			allContactList.setSelectionFromTop(listHeaderCount, 0);
			return;
		}
		if (allContactIndexPosInList.containsKey(index)) {
			int indexPos = allContactIndexPosInList.get(index);
			int scrollPos = listHeaderCount + indexPos;
			allContactList.setSelectionFromTop(scrollPos, 0);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.contact_search_cancle:
			doSearchCancle();
			break;
		case R.id.address_search_input:
			onContactSearchClick();
			break;
		case R.id.contact_search_clear_img:
			onSearchClearImgClick();
			break;
		default:
			break;
		}
	}

	private void onSearchClearImgClick() {
		contactSearchInput.setText("");
	}

	@Override
	public void onIndexChose(int index, String indexStr) {
		allContactListIndexChose(indexStr);
	}

	private void editextAddWatcher() {
		contactSearchInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString().trim();
				if (!TextUtils.isEmpty(text)) {
					textClear.setVisibility(View.VISIBLE);
				} else {
					textClear.setVisibility(View.GONE);
				}
				currentText = text;
				getSerchResult(text);
			}
		});
	}

	private void setupView() {
		editextAddWatcher();
		setSearchResultListView();
	}

	private void setSearchResultListView() {
		if (searchResultListView.getAdapter() == null) {
			if (resultAdapter == null) {
				resultAdapter = new SearchResultAdapter(getActivity(), isAdded());
			}
			searchResultListView.setAdapter(resultAdapter);
		}

		searchResultListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SearchResultInfo info = searchResult.get(position);
				int page = info.currPage;
				allContactList.setSelection(info.infoPos);
				contactSearchInput.setText("");
				doSearchCancle();
			}
		});

	}

	private void doSearchCancle() {
		contactSearchInput.setCursorVisible(false);
		contactSearchCancle.setVisibility(View.GONE);
		isCancleSearchButtonVisible = false;
		contactSearchInput.setText("");
		contactSearchClick.setVisibility(View.GONE);
		resultViewParent.setVisibility(View.GONE);
		isResultViewParentVisible = false;
		ContactlistUtils.hidenSoftInput(getActivity(), contactSearchInput);
	}

	private void getSerchResult(final String text) {
		new LoadTask() {
			public void run() {
				getPageAllResult(text);
			}
		}.start();
	}

	private void getPageAllResult(String text) {
		pageAllResult.clear();
		if (!ContactlistUtils.isEmptyList(allContacts) && !TextUtils.isEmpty(text)) {
			List<ContactlistContactInfo> temps = new ArrayList<ContactlistContactInfo>(allContacts);
			for (int i = 0; i < temps.size(); i++) {
				ContactlistContactInfo info = temps.get(i);
				if (info.getViewType() == ContactlistContactInfo.VIEW_TYPE_INDEX) {
					continue;
				}
				ContactInfo cInfo = info.getContactInfo();
				if (cInfo == null) {
					continue;
				}
				String name = cInfo.getName();
				if (TextUtils.isEmpty(name)) {
					continue;
				}
				if (name.contains(text) || isPinYinContains(name, text)) {
					SearchResultInfo resultInfo = new SearchResultInfo();
					resultInfo.cContactInfo = info;
					resultInfo.infoPos = i;
					if (!pageAllResult.contains(resultInfo)) {
						pageAllResult.add(resultInfo);
					}
				}
			}
		}
		onFilteFinish(text, pageAllResult);
	}

	private void onFilteFinish(final String text, final List<SearchResultInfo> resultInfos) {
		if (currentText.equals(text)) {
			runOnUiThread(new Runnable() {
				public void run() {
					notifyAdapter(resultInfos);
					if (TextUtils.isEmpty(text)) {
						resultViewParent.setVisibility(View.GONE);
						isResultViewParentVisible = false;
					} else {
						resultViewParent.setVisibility(View.VISIBLE);
						isResultViewParentVisible = true;
					}
				}
			});
		}
	}

	private void notifyAdapter(List<SearchResultInfo> list) {
		searchResult.clear();
		searchResult.addAll(list);
		resultAdapter.setSearchResult(searchResult);
		resultAdapter.notifyDataSetChanged();
	}

	private boolean isPinYinContains(String sourceName, String matchText) {
		String pinYin = ContactlistUtils.getPinYin(matchText);
		String namePinYin = ContactlistUtils.getPinYin(sourceName);
		if (namePinYin.contains(pinYin)) {
			return true;
		}
		String target = "";
		for (int i = 0; i < sourceName.length(); i++) {
			String sub = sourceName.substring(i, i + 1);
			target += ContactlistUtils.getFirstLetter(sub);
		}
		if (target.contains(pinYin)) {
			return true;
		}
		return false;
	}

	private void loadAllContactsOnLocal() {
		if (!onContactsLoadFinish) {
			allContactList.onRefreshComplete();
			return;
		}
		threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				List<ContactInfo> allContact = manager.loadAllLocalContacts(false);
				updateUI(allContact);
			}
		});

	}

	private void updateUI(List<ContactInfo> allContact) {

		List<ContactlistContactInfo> temp_Contactlist = getContactlistContactInfos(allContact);
		getUseIndexs(temp_Contactlist);
		final List<ContactlistContactInfo> temp = getuseContacts(temp_Contactlist);
		sortAllContact(temp);
		getAllContactIndexPos(temp);
		onContactsLoadFinish = true;
		runOnUiThread(new Runnable() {
			public void run() {

				allContactList.onRefreshComplete();
				allContacts.clear();
				allContacts.addAll(temp);
				contactAllAdapter.setAllContacts(allContacts);
				setContactListAdapter();
				//xiong:
				pbLoading.setVisibility(View.GONE);
				flContent.setVisibility(View.VISIBLE);
			}
		});
	}

	private void runOnUiThread(Runnable run) {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			activity.runOnUiThread(run);
		}

	}

	private void setContactListAdapter() {
		if (allContactList.getAdapter() == null) {
			if (contactAllAdapter == null) {
				contactAllAdapter = new ContactAllAdapter(getActivity(), isAdded(), model);
				contactAllAdapter.setContactsServiceAdapter(new ContactsPublicServiceAdapter(getActivity()));
			}
			allContactList.setAdapter(contactAllAdapter);
		} else {
			contactAllAdapter.notifyDataSetChanged();
		}
	}

	private void getAllContactIndexPos(List<ContactlistContactInfo> list) {
		for (int i = 0; i < list.size(); i++) {
			ContactlistContactInfo info = list.get(i);
			if (info.getViewType() == ContactlistContactInfo.VIEW_TYPE_INDEX) {
				allContactIndexPosInList.put(info.getContactInfo().getName(), i);
			}
		}
	}

	private void sortAllContact(List<ContactlistContactInfo> list) {
		Collections.sort(list, new Comparator<ContactlistContactInfo>() {

			@Override
			public int compare(ContactlistContactInfo lhs, ContactlistContactInfo rhs) {
				int result = 0;
				try {
					result = new Boolean(rhs.getContactInfo().isRegister()).compareTo(new Boolean(lhs.getContactInfo()
							.isRegister()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (result == 0) {
					result = lhs.getIndex().compareTo(rhs.getIndex());
					if (result == 0) {
						result = lhs.getViewType() - rhs.getViewType();
						if (result == 0) {
							result = lhs.getNamePinYin().compareTo(rhs.getNamePinYin());
							if (result == 0) {
								String lhsName = lhs.getContactInfo().getName();
								String rhsName = rhs.getContactInfo().getName();
								if (TextUtils.isEmpty(lhsName) && TextUtils.isEmpty(rhsName)) {
									result = 0;
								} else if (TextUtils.isEmpty(lhsName) && !TextUtils.isEmpty(rhsName)) {
									result = -1;
								} else if (!TextUtils.isEmpty(lhsName) && TextUtils.isEmpty(rhsName)) {
									result = 1;
								} else {
									result = lhsName.compareTo(rhsName);
								}

							}
						}
					}
				}
				return result;
			}

		});
		List<ContactlistContactInfo> serviceContacts = new ArrayList<ContactlistContactInfo>();
		for (ContactlistContactInfo contactlistContactInfo : list) {
			if (contactlistContactInfo.getCommonContactInfo() != null) {
				serviceContacts.add(contactlistContactInfo);
			}
		}
		list.removeAll(serviceContacts);
		list.addAll(0, serviceContacts);
	}

	private List<ContactlistContactInfo> getuseContacts(List<ContactlistContactInfo> list) {
		List<ContactlistContactInfo> tempAllContacts = new ArrayList<ContactlistContactInfo>();
		tempAllContacts.addAll(list);
		for (String index : allContactIndexs) {
			ContactInfo contactInfo = new ContactInfo();
			contactInfo.setUserId(ContactlistContactInfo.INFO_ID_DEFAULT);
			contactInfo.setRegister(!index.equals(INDEX_DEFAULT_UNREGISTER));
			contactInfo.setName(index);

			ContactlistContactInfo info = new ContactlistContactInfo(contactInfo,
					ContactlistContactInfo.VIEW_TYPE_INDEX);
			info.setIndex(index);
			tempAllContacts.add(info);
		}
		return tempAllContacts;
	}

	private void filterExistContacts(List<ContactInfo> allContact) {
		if (!ContactlistUtils.isEmptyList(existList)) {
			for (UserSimpleInfo info : existList) {
				ContactInfo cInfo = new ContactInfo();
				cInfo.setUserId(info.getId());
				if (allContact.contains(cInfo)) {
					allContact.remove(cInfo);
				}
			}
		}
	}

	private void getUseIndexs(List<ContactlistContactInfo> list) {
		allContactIndexs.clear();
		for (ContactlistContactInfo info : list) {
			boolean isRegistered = info.getContactInfo().isRegister();
			if (isRegistered) {
				String firstLetter = ContactlistUtils.getFirstLetter(info.getContactInfo().getName());
				if (TextUtils.isEmpty(firstLetter)) {
					allContactIndexs.add(INDEX_DEFAULT_REGISTER);
					info.setIndex(INDEX_DEFAULT_REGISTER);
				} else {
					if (ContactlistUtils.isLetter(firstLetter.toCharArray()[0])) {
						allContactIndexs.add(firstLetter);
						info.setIndex(firstLetter);
					} else {
						allContactIndexs.add(INDEX_DEFAULT_REGISTER);
						info.setIndex(INDEX_DEFAULT_REGISTER);
					}

				}
			} else {
				allContactIndexs.add(INDEX_DEFAULT_UNREGISTER);
				info.setIndex(INDEX_DEFAULT_UNREGISTER);
			}
		}
	}

	private List<ContactlistContactInfo> getContactlistCommonContactInfos(List<CommonContactInfo> list) {
		if (list == null) {
			return new ArrayList<ContactlistContactInfo>();
		}
		List<ContactlistContactInfo> infos = new ArrayList<ContactlistContactInfo>();
		for (CommonContactInfo info : list) {
			infos.add(new ContactlistContactInfo(info, ContactlistContactInfo.VIEW_TYPE_CONTACT));
		}

		return infos;
	}

	private List<ContactlistContactInfo> getContactlistContactInfos(List<ContactInfo> list) {
		if (list == null) {
			return new ArrayList<ContactlistContactInfo>();
		}
		List<ContactlistContactInfo> infos = new ArrayList<ContactlistContactInfo>();
		for (ContactInfo info : list) {
			infos.add(new ContactlistContactInfo(info, ContactlistContactInfo.VIEW_TYPE_CONTACT));
		}

		return infos;
	}

	private void onContactSearchClick() {
		contactSearchInput.setCursorVisible(true);
		contactSearchCancle.setVisibility(View.VISIBLE);
		isCancleSearchButtonVisible = true;
		// serachIcon.setVisibility(View.VISIBLE);
		contactSearchClick.setVisibility(View.VISIBLE);
	}

	private class LoadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			run();
			return null;
		}

		public void run() {

		}

		public void start() {
			execute();
		}

	}

	@Override
	public void onRefresh() {
		loadContactsOnServer();
	}

	//	private void updateListView(List<ContactInfo> result) {
	//		updateUI(result);
	//	}

	private void loadContactsOnServer() {

		//TODO LOAD SERVICE CONTACTS
		new ContactsServerLoader(new ServerLoaderListener<List<ContactInfo>>() {

			@Override
			public void onSuccess(final List<ContactInfo> result) {
				allContactList.onRefreshComplete();
				//						updateListView(result);
				updateUI(result);
			}

			@Override
			public void onError() {
				allContactList.onRefreshComplete();
			}
		}).load();

	}
}
