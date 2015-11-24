package cn.yjt.oa.app.contactlist;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;
import io.luobo.widget.OnNodeClickListener;
import io.luobo.widget.XNode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.BaseFragment;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.OnBackPressedInterface;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.CommonContactInfo;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.CustLastUpdateTimesInfo;
import cn.yjt.oa.app.beans.DeptDetailInfo;
import cn.yjt.oa.app.beans.DeptDetailUserInfo;
import cn.yjt.oa.app.beans.GroupInfo;
import cn.yjt.oa.app.beans.InviteUserInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.adpter.CommonServiceAdapter;
import cn.yjt.oa.app.contactlist.adpter.ContactAllAdapter;
import cn.yjt.oa.app.contactlist.adpter.ContactDeptAdapter;
import cn.yjt.oa.app.contactlist.adpter.GroupAdapter;
import cn.yjt.oa.app.contactlist.adpter.SearchResultAdapter;
import cn.yjt.oa.app.contactlist.data.ContactlistContactInfo;
import cn.yjt.oa.app.contactlist.data.ContactlistGroupInfo;
import cn.yjt.oa.app.contactlist.data.SearchResultInfo;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.contactlist.view.CustomViewPager;
import cn.yjt.oa.app.contactlist.view.IndexView;
import cn.yjt.oa.app.contactlist.view.IndexView.OnIndexChoseListener;
import cn.yjt.oa.app.enterprise.contact.MachineContactInfo;
import cn.yjt.oa.app.enterprise.contact.PhoneUtils;
import cn.yjt.oa.app.enterprise.operation.AddMemberActivity;
import cn.yjt.oa.app.enterprise.operation.SelectPopupMenu;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.utils.Config;
import cn.yjt.oa.app.utils.SharedUtils;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshExpandableListView;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class ContactlistFragment extends BaseFragment implements OnClickListener, OnIndexChoseListener,
		OnBackPressedInterface {
	static final String TAG = ContactlistFragment.class.getSimpleName();
	private Button contactSearchCancle;
	private TextView contactAll, contactGroups, contactPublicService, noResult, indexShow, organizeName;
	private EditText contactSearchInput;
	private ImageView serachIcon, contactSearchClick;
	private CustomViewPager contactPager;
	private LinearLayout textClear, resultViewParent, organizeParent;
	private RelativeLayout organizeLayout;
	private PullToRefreshListView allContactList, publicServiceContactList;
	private PullToRefreshListView organizeContactList;
	private ListView searchResultListView;
	private PullToRefreshExpandableListView groupContactList;

	private View searchParent, topDivider;

	private IndexView indexLayout;

	private List<View> pagers = new ArrayList<View>();

	private static final String INDEX_DEFAULT_REGISTER = "#";
	private static final String INDEX_DEFAULT_UNREGISTER = "*";
	private static final String[] INDEXSTR = { INDEX_DEFAULT_REGISTER, "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
			INDEX_DEFAULT_UNREGISTER };

	public static final int UPDATE_GROUP_NEED_REFRESH = 1;
	private static final int REQUEST_CODE_SELECT_CONTACTS = 0;
	private static final int REQUEST_CODE_INPUT_MANUAL = 2;

	private List<ContactlistContactInfo> allContacts = new ArrayList<ContactlistContactInfo>();
	private List<XNode> deptList = new ArrayList<XNode>();
	private Set<String> allContactIndexs = new TreeSet<String>();
	private HashMap<String, Integer> allContactIndexPosInList = new HashMap<String, Integer>();

	private List<ContactlistGroupInfo> groupInfos = new ArrayList<ContactlistGroupInfo>();

	private List<CommonContactInfo> publicServices = new ArrayList<CommonContactInfo>();

	public static final int PAGER_ALL = 0;
	public static final int PAGER_GROUPS = 1;
	public static final int PAGER_PUANLIC_SERVICE = 2;

	private int currentPage;

	private ContactAllAdapter contactAllAdapter;
	private ContactDeptAdapter deptAdapter;
	private GroupAdapter groupAdapter;
	private CommonServiceAdapter commonServiceAdapter;
	private SearchResultAdapter resultAdapter;

	private ContactManager manager;

	private boolean isRegisteredContactsLoadFinish, isUnregisteredContactsLoadFinish;

	private boolean onContactsLoadFinish = true;
	private boolean registeredContactLoadSuccess;
	private boolean unRegisteredContactLoadSuccess;
	private boolean onGrouosLoadFinish = true;
	private boolean onPublicsLoadFinish = true;
	private boolean onReloadGroupchildFinish = true;

	public static final String CONTACTLIST_STRAT_MODEL = "contact_list_model";
	public static final int CONTACTLIST_STRAT_MODEL_NORMAL = 0;
	public static final int CONTACTLIST_STRAT_MODEL_MULITCHOICE = 1;
	private int model = CONTACTLIST_STRAT_MODEL_NORMAL;

	private List<UserSimpleInfo> existList;

	private String currentText = "", pageAllText, pageGroupText, pageServiceText;
	private List<SearchResultInfo> pageAllResult = new ArrayList<SearchResultInfo>();
	private List<SearchResultInfo> pageGroupResult = new ArrayList<SearchResultInfo>();
	private List<SearchResultInfo> pagePublicServiceResult = new ArrayList<SearchResultInfo>();
	private List<SearchResultInfo> searchResult = new ArrayList<SearchResultInfo>();

	public static final int CONTACTS_LOAD_DG_ID = 0x00000001;

	public static boolean isIntentToGroupDetail = false;

	private Activity runActivity;

	private boolean isDialogShow;

	private View rightButton;

	private boolean isViewDestoryed = false;

	private boolean isResultViewParentVisible = false;

	private boolean isCancleSearchButtonVisible = false;

	private boolean isAttached;

	private boolean isShouldShowDialog = false;

	private boolean isContactPullToRefresh;
	private boolean isGroupPullToRefresh;
	private boolean isServicePullToRefresh;
	private String currentOrganize = "组织架构";
	private View root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(MainApplication.TAG, "ContactlistFragment.onCreateView");
		if (getActivity() instanceof MainActivity) {
			MainActivity activity = (MainActivity) getActivity();
			activity.addToFragments(2, this);
		}
		manager = ContactManager.getContactManager(getActivity());
		isViewDestoryed = false;
		getExtra();
		if (root == null) {
			root = inflater.inflate(R.layout.contactlist_list_layout, null);
			initView(root);
			setupView();
		}
		if (isMulitChoice()) {
			loadAllDatas();
			checkDataUpdate();
		}
		return root;
	}

	@Override
	public void onFragmentSelected() {
		super.onFragmentSelected();
		//		 showDialog();
		loadAllDatas();
		checkDataUpdate();

	}

	private void showDialog() {
		if (isViewDestoryed) {
			return;
		}
		if (runActivity instanceof ContactlistActivity) {
			runActivity.showDialog(CONTACTS_LOAD_DG_ID);
			isDialogShow = true;
		} else if (runActivity instanceof MainActivity) {
			MainActivity activity = (MainActivity) runActivity;
			if (activity.getCurrentItem() == 2) {
				runActivity.showDialog(CONTACTS_LOAD_DG_ID);
				isDialogShow = true;
			} else {
				isDialogShow = false;
			}

		} else {
			isDialogShow = false;
		}

		DialogState state = (DialogState) runActivity;
		state.setDialogShow(isDialogShow);
	}

	public void activityShowDialog() {
		if (isShouldShowDialog && !isContactPullToRefresh) {
			runActivity.showDialog(CONTACTS_LOAD_DG_ID);
			isDialogShow = true;
			DialogState state = (DialogState) runActivity;
			state.setDialogShow(isDialogShow);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d(MainApplication.TAG, "ContactlistFragment.onAttach");
		super.onAttach(activity);
		runActivity = activity;
		isAttached = true;
	}

	@Override
	public void onDetach() {
		Log.d(MainApplication.TAG, "ContactlistFragment.onDetach");
		super.onDetach();
		isAttached = false;
	}

	private void getExtra() {
		Intent intent = runActivity.getIntent();
		model = intent.getIntExtra(CONTACTLIST_STRAT_MODEL, CONTACTLIST_STRAT_MODEL_NORMAL);
		existList = intent.getParcelableArrayListExtra(ContactlistActivity.EXTRA_EXIST_CONTACTS);
	}

	public boolean isMulitChoice() {
		return model == CONTACTLIST_STRAT_MODEL_MULITCHOICE;
	}

	private void setupView() {
		if (isMulitChoice()) {
			// View viewTitle = (View) contactAll.getParent();
			// viewTitle.setVisibility(View.GONE);
			// searchParent.setVisibility(View.GONE);
			// topDivider.setVisibility(View.GONE);
			contactPublicService.setVisibility(View.GONE);
			// contactGroups.setVisibility(View.GONE);
		}
		setCurrentPageSelected();
		editextAddWatcher();
		setContactsViewPager();
		setSearchResultListView();
	}

	private void setCurrentPageSelected() {
		if (currentPage == PAGER_ALL) {
			setTitleTabSelect(true, false, false);
		} else if (currentPage == PAGER_GROUPS) {
			setTitleTabSelect(false, true, false);
		} else if (currentPage == PAGER_PUANLIC_SERVICE) {
			setTitleTabSelect(false, false, true);
		}
	}

	private void initView(View view) {
		contactSearchCancle = (Button) view.findViewById(R.id.contact_search_cancle);
		contactSearchCancle.setOnClickListener(this);
		contactAll = (TextView) view.findViewById(R.id.contact_all);
		contactAll.setOnClickListener(this);
		contactGroups = (TextView) view.findViewById(R.id.contact_groups);
		contactGroups.setOnClickListener(this);
		contactPublicService = (TextView) view.findViewById(R.id.contact_public_service);
		contactPublicService.setOnClickListener(this);
		organizeParent = (LinearLayout) view.findViewById(R.id.organize_parent);
		organizeLayout = (RelativeLayout) view.findViewById(R.id.organize_layout);
		organizeLayout.setOnClickListener(this);
		organizeName = (TextView) view.findViewById(R.id.organize_name);
		contactSearchInput = (EditText) view.findViewById(R.id.address_search_input);
		contactSearchInput.setOnClickListener(this);
		contactSearchInput.setCursorVisible(false);
		contactSearchInput.setText("");
		serachIcon = (ImageView) view.findViewById(R.id.contact_search_icon);
		textClear = (LinearLayout) view.findViewById(R.id.contact_search_clear_img);
		textClear.setOnClickListener(this);
		indexShow = (TextView) view.findViewById(R.id.contact_index_show_text);
		indexLayout = (IndexView) view.findViewById(R.id.contact_index_layout);
		indexLayout.setIndexs(INDEXSTR, this);
		indexLayout.setIndexShowTextView(indexShow);
		contactPager = (CustomViewPager) view.findViewById(R.id.contact_list_viewpage);
		contactSearchClick = (ImageView) view.findViewById(R.id.contact_search_click);
		contactSearchClick.setOnClickListener(this);
		searchResultListView = (ListView) view.findViewById(R.id.contact_search_result_list);
		noResult = (TextView) view.findViewById(R.id.contact_search_result_text);
		resultViewParent = (LinearLayout) view.findViewById(R.id.address_search_result_list_parent);
		setResultViewParent();
		searchParent = view.findViewById(R.id.contact_search_parent);
		topDivider = view.findViewById(R.id.top_divider);
	}

	private void setSearchResultListView() {
		if (searchResultListView.getAdapter() == null) {
			if (resultAdapter == null) {
				resultAdapter = new SearchResultAdapter(runActivity, isAttached);
			}
			searchResultListView.setAdapter(resultAdapter);
		}

		searchResultListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SearchResultInfo info = searchResult.get(position);
				int page = info.currPage;
				if (page == PAGER_ALL) {
					allContactList.setSelection(info.infoPos);
				} else if (page == PAGER_GROUPS) {
					groupContactList.setSelectedGroup(info.infoPos);
					groupContactList.expandGroup(info.infoPos);
				} else if (page == PAGER_PUANLIC_SERVICE) {
					publicServiceContactList.setSelection(info.infoPos);
				}
				contactSearchInput.setText("");
				doSearchCancle();
			}
		});

	}

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

	private void setContactsViewPager() {
		pagers.clear();
		LayoutInflater inflater = LayoutInflater.from(runActivity);
		initPagerContactAll(inflater);
		initPagerContactGroups(inflater);
		initPagerContactPublisService(inflater);
		contactPager.setScrollable(true);
		contactPager.setAdapter(new ContactPagerAdapter());
		contactPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				currentPage = position;
				if (position == PAGER_ALL) {
					onContactAllSelected();
				} else if (position == PAGER_GROUPS) {
					onContactGroupsSelected();
				} else if (position == PAGER_PUANLIC_SERVICE) {
					onContactPublicServiceSelected();
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	private void initPagerContactAll(LayoutInflater inflater) {
		if (isMulitChoice()) {
			organizeParent.setVisibility(View.GONE);
		}
		View pagerAll = inflater.inflate(R.layout.contactlist_contact, null);
		organizeContactList = (PullToRefreshListView) pagerAll.findViewById(R.id.contact_organize_listView);

		organizeContactList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				isContactPullToRefresh = true;
				loadContactsOnServer(true);

			}
		});
		organizeContactList.enableFooterView(false);
		setDepteListAdapter();
		organizeContactList.setVisibility(View.GONE);
		allContactList = (PullToRefreshListView) pagerAll.findViewById(R.id.contact_all_listView);
		allContactList.setDividerHeight(0);
		allContactList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int headerCount = allContactList.getHeaderViewsCount();
				ContactlistContactInfo info = allContacts.get(position - headerCount);
				if (isMulitChoice()) {
					CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_check);
					checkBox.setChecked(!info.isChecked());
				} else {
					intentToContactDetailActivity(info.getContactInfo());
				}

			}
		});
		allContactList.enableFooterView(false);
		allContactList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (onContactsLoadFinish) {
					isContactPullToRefresh = true;
					loadContactsOnServer(false);
				} else {
					allContactList.onRefreshComplete();
				}

			}
		});

		setContactListAdapter();
		pagers.add(pagerAll);
	}

	private void loadContactDeptOnServer() {
		Type responseType = new TypeToken<Response<List<DeptDetailInfo>>>() {
		}.getType();
		new AsyncRequest.Builder()
				.setModule(
						String.format(AsyncRequest.MODULE_CUSTS_DEPTS,
								String.valueOf(AccountManager.getCurrent(getActivity()).getCustId())))
				.setResponseType(responseType).setResponseListener(new Listener<Response<List<DeptDetailInfo>>>() {

					@Override
					public void onErrorResponse(InvocationError arg0) {
						organizeContactList.onRefreshComplete();
					}

					@Override
					public void onResponse(Response<List<DeptDetailInfo>> response) {
						organizeContactList.onRefreshComplete();
						if (response.getCode() == 0) {
							List<DeptDetailInfo> payload = response.getPayload();
							manager.deleteDept();
							manager.addDepts(payload);
							Log.e("abc", payload.toString() + "@@@@@@@@@@@@@@@");
							loadContactDept(false);
						} else {
							Toast.makeText(runActivity, response.getDescription(), Toast.LENGTH_LONG).show();
						}
					}
				}).build().get();
	}

	private void loadContactDept(boolean isRequest) {
		List<DeptDetailInfo> deptDetailInfos = manager.getDeptDetailInfos();
		System.out.println("deptDetailInfos:" + deptDetailInfos);

		if (deptDetailInfos == null || deptDetailInfos.isEmpty()) {
			if (isRequest) {
				loadContactDeptOnServer();
			}
			return;
		}
		for (DeptDetailInfo deptDetailInfo : deptDetailInfos) {
			List<DeptDetailInfo> children = deptDetailInfo.getChildren();
			if (children != null && children.size() > 0) {
				for (DeptDetailInfo deptDetailInfo2 : children) {
					deptDetailInfo2.setChildren(null);
				}
			}
		}
		deptList.clear();
		deptList.addAll(deptDetailInfos);

		setDepteListAdapter();

	}

	private void setContactListAdapter() {
		if (allContactList.getAdapter() == null) {
			if (contactAllAdapter == null) {
				contactAllAdapter = new ContactAllAdapter(runActivity, isAttached, model);
			}
			allContactList.setAdapter(contactAllAdapter);
		} else {
			contactAllAdapter.notifyDataSetChanged();
		}
	}

	private String getDeptName(List<XNode> deptList, DeptDetailUserInfo detailUserInfo) {
		String deptName = "";
		for (XNode xNode : deptList) {
			if (xNode instanceof DeptDetailInfo) {
				DeptDetailInfo info = (DeptDetailInfo) xNode;
				if (info.getId() == detailUserInfo.getParentId()) {
					deptName = info.getName();
					return deptName;

				} else if (info.getChildren() != null) {
					for (DeptDetailInfo info2 : info.getChildren()) {
						if (info2.getId() == detailUserInfo.getParentId()) {
							deptName = info.getName() + "/" + info2.getName();
							break;
						}
					}
				}
			}
		}
		return deptName;
	}

	private void setDepteListAdapter() {
		if (organizeContactList.getAdapter() == null) {
			if (deptAdapter == null) {
				deptAdapter = new ContactDeptAdapter(runActivity, deptList, new OnNodeClickListener() {

					@Override
					public void onNodeClick(XNode xNode, View arg1) {
						if (xNode instanceof DeptDetailUserInfo) {
							DeptDetailUserInfo info = (DeptDetailUserInfo) xNode;
							String dept = getDeptName(deptList, info);
							ContactInfo contactInfo = ContactManager.getContactManager(runActivity).getContactInfoById(
									info.getUserId());
							contactInfo.setDepartment(dept);
							contactInfo.setPosition(info.getPosition());
							intentToContactDetailActivityFromStruct(contactInfo);
						}
					}
				});
			}
			organizeContactList.setAdapter(deptAdapter);
		} else {
			deptAdapter.clear();
			deptAdapter.notifyDataSetChanged();
		}
	}

	public void initPagerContactGroups(LayoutInflater inflater) {
		// if (isMulitChoice()) {
		// return;
		// }
		View pagerGroup = inflater.inflate(R.layout.contactlist_groups, null);
		groupContactList = (PullToRefreshExpandableListView) pagerGroup.findViewById(R.id.contact_groups_listView);

		groupContactList.enableFooterView(false);

		groupContactList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				ContactInfo info = groupInfos.get(groupPosition).getUsers()[childPosition];
				intentToContactDetailActivity(info);
				return false;
			}
		});

		groupContactList.setOnRefreshListener(groupContactRefreshListener);
		setGroupsAdapter();
		pagers.add(pagerGroup);
	}

	public OnRefreshListener groupContactRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			if (onGrouosLoadFinish) {
				isGroupPullToRefresh = true;
				loadAllGroupsFromServer();
			} else {
				groupContactList.onRefreshComplete();
			}

		}
	};

	private void setGroupsAdapter() {

		if (groupContactList.getAdapter() == null) {
			if (groupAdapter == null) {
				groupAdapter = new GroupAdapter(runActivity, isAttached, model, clickListener);
			}
			groupContactList.setAdapter(groupAdapter);
		} else {
			groupAdapter.notifyDataSetChanged();
		}
	}

	public void initPagerContactPublisService(LayoutInflater inflater) {
		if (isMulitChoice()) {
			return;
		}
		View pagerPublicService = inflater.inflate(R.layout.contactlist_public_service, null);
		pagers.add(pagerPublicService);
		publicServiceContactList = (PullToRefreshListView) pagerPublicService
				.findViewById(R.id.contact_service_listView);
		publicServiceContactList.enableFooterView(false);
		publicServiceContactList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				isServicePullToRefresh = true;
				loadPublicServiceFromServer();
			}
		});
		publicServiceContactList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int headerCount = publicServiceContactList.getHeaderViewsCount();
				CommonContactInfo info = publicServices.get(position - headerCount);
				startPhone(info.getPhone1());
			}
		});
		setPublicServiceAdapter();
	}

	private void setPublicServiceAdapter() {
		if (publicServiceContactList.getAdapter() == null) {
			if (commonServiceAdapter == null) {
				commonServiceAdapter = new CommonServiceAdapter(runActivity, isAttached);
			}
			publicServiceContactList.setAdapter(commonServiceAdapter);
		} else {
			commonServiceAdapter.notifyDataSetChanged();
		}
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

	private void getSerchResult(final String text) {
		new LoadTask() {
			public void run() {
				if (currentPage == PAGER_ALL) {
					getPageAllResult(text);
				} else if (currentPage == PAGER_GROUPS) {
					getPageGroupResult(text);
				} else if (currentPage == PAGER_PUANLIC_SERVICE) {
					getPagePublicServiceResult(text);
				}
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
					resultInfo.currPage = PAGER_ALL;
					resultInfo.infoPos = i;
					if (!pageAllResult.contains(resultInfo)) {
						pageAllResult.add(resultInfo);
					}
				}
			}
		}
		onFilteFinish(text, pageAllResult);
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

	private void onFilteFinish(final String text, final List<SearchResultInfo> resultInfos) {
		if (currentText.equals(text)) {
			runActivity.runOnUiThread(new Runnable() {
				public void run() {
					setPageText(text);
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

	private void setPageText(String text) {
		if (currentPage == PAGER_ALL) {
			pageAllText = text;
		} else if (currentPage == PAGER_GROUPS) {
			pageGroupText = text;
		} else if (currentPage == PAGER_PUANLIC_SERVICE) {
			pageServiceText = text;
		}
	}

	private void getPageGroupResult(final String text) {
		pageGroupResult.clear();
		if (!ContactlistUtils.isEmptyList(groupInfos) && !TextUtils.isEmpty(text)) {
			List<ContactlistGroupInfo> temps = new ArrayList<ContactlistGroupInfo>(groupInfos);
			for (int i = 0; i < temps.size(); i++) {
				ContactlistGroupInfo groupInfo = temps.get(i);
				if (groupInfo.getInfo() == null) {
					continue;
				}
				String name = groupInfo.getInfo().getName();
				if (TextUtils.isEmpty(name)) {
					continue;
				}
				if (name.contains(text) || isPinYinContains(name, text)) {
					SearchResultInfo info = new SearchResultInfo();
					info.cGroupInfo = groupInfo;
					info.currPage = PAGER_GROUPS;
					info.infoPos = i;
					if (!pageGroupResult.contains(info)) {
						pageGroupResult.add(info);
					}
				}
			}
		}
		onFilteFinish(text, pageGroupResult);
	}

	private void getPagePublicServiceResult(String text) {
		pagePublicServiceResult.clear();
		if (!ContactlistUtils.isEmptyList(publicServices) && !TextUtils.isEmpty(text)) {
			List<CommonContactInfo> temps = new ArrayList<CommonContactInfo>(publicServices);
			for (int i = 0; i < temps.size(); i++) {
				CommonContactInfo info = temps.get(i);
				if (info == null) {
					continue;
				}
				String name = info.getName();
				if (TextUtils.isEmpty(name))
					continue;
				if (name.contains(text) || isPinYinContains(name, text)) {
					SearchResultInfo resultInfo = new SearchResultInfo();
					resultInfo.commonContactInfo = info;
					resultInfo.currPage = PAGER_PUANLIC_SERVICE;
					resultInfo.infoPos = i;
					if (!pagePublicServiceResult.contains(resultInfo)) {
						pagePublicServiceResult.add(resultInfo);
					}
				}
			}
		}
		onFilteFinish(text, pagePublicServiceResult);
	}

	@Override
	public void onIndexChose(int index, String indexStr) {
		if (currentPage == PAGER_ALL) {
			allContactListIndexChose(indexStr);
		} else if (currentPage == PAGER_PUANLIC_SERVICE) {

		}
	}

	private void allContactListIndexChose(String index) {
		int listHeaderCount = allContactList.getHeaderViewsCount();
		if (allContactIndexPosInList.containsKey(index)) {
			int indexPos = allContactIndexPosInList.get(index);
			int scrollPos = listHeaderCount + indexPos;
			allContactList.setSelectionFromTop(scrollPos, 0);
		}

	}

	@Override
	public void onClick(View v) {
		int key = v.getId();
		switch (key) {
		case R.id.contact_search_cancle:
			doSearchCancle();
			break;
		case R.id.contact_all:
			onContactAllSelected();
			break;
		case R.id.contact_groups:
			onContactGroupsSelected();
			break;
		case R.id.contact_public_service:
			onContactPublicServiceSelected();
			break;
		case R.id.address_search_input:
			onContactSearchClick();
			break;
		case R.id.contact_search_clear_img:
			onSearchClearImgClick();
			break;
		case R.id.organize_layout:
			if ("全员".equals(currentOrganize)) {
				currentOrganize = "组织架构";
				organizeName.setText(currentOrganize);
				searchParent.setVisibility(View.VISIBLE);
				indexLayout.setVisibility(View.VISIBLE);
				organizeContactList.setVisibility(View.GONE);
				allContactList.setVisibility(View.VISIBLE);
				setRightButton(View.VISIBLE);

			} else if ("组织架构".equals(currentOrganize)) {
				currentOrganize = "全员";
				organizeName.setText(currentOrganize);
				loadContactDept(true);
				indexLayout.setVisibility(View.GONE);
				searchParent.setVisibility(View.GONE);
				organizeContactList.setVisibility(View.VISIBLE);
				allContactList.setVisibility(View.GONE);
				setRightButton(View.GONE);
			}
			break;
		case R.id.btn_select_local:
			startActivityForContacts();
			break;
		case R.id.btn_select_manual:
			startActivityForManual();
			break;

		}
	}

	private void startActivityForContacts() {
		Intent intent = new Intent(runActivity, InviteContactActivity.class);
		// memberContact=new ArrayList<MachineContactInfo>();
		// for (int i = 0; i < adapter.getMembers().size(); i++) {
		// MachineContactInfo info=new MachineContactInfo();
		// info.setDisplayName(adapter.getMembers().get(i).getName());
		// info.setNumber(adapter.getMembers().get(i).getPhone());
		// memberContact.add(info);
		// }
		//
		// intent.putParcelableArrayListExtra(MachineContactActivity.MEMBER_LIST,
		// (ArrayList<? extends Parcelable>) memberContact);
		startActivityForResult(intent, REQUEST_CODE_SELECT_CONTACTS);
	}

	private void startActivityForManual() {
		Intent intent = new Intent(runActivity, AddMemberActivity.class);
		startActivityForResult(intent, REQUEST_CODE_INPUT_MANUAL);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case UPDATE_GROUP_NEED_REFRESH:
				manager.deleteAllGroups();
				groupContactList.setRefreshingState();
				groupContactRefreshListener.onRefresh();
				break;
			case REQUEST_CODE_SELECT_CONTACTS:
				getContactsFromLocal(data);
				break;
			case REQUEST_CODE_INPUT_MANUAL:
				getContactsWithManual(data);
				break;

			default:
				break;
			}

		}
	}

	private void getContactsFromLocal(Intent data) {
		MachineContactInfo info = data.getParcelableExtra(InviteContactActivity.INVITE_CONTACT);
		setInviteUser(info.getDisplayName(), info.getNumber());
	}

	private void getContactsWithManual(Intent data) {
		String name = data.getStringExtra(AddMemberActivity.EXTRA_MEMBER_NAME);
		String phone = data.getStringExtra(AddMemberActivity.EXTRA_MEMBER_PHONE);
		setInviteUser(name, phone);
	}

	private void setInviteUser(String username, String usernumber) {
		UserInfo userInfo = new UserInfo();
		userInfo.setName(username);
		userInfo.setPhone(PhoneUtils.formatPhoneNumber(usernumber));
		inviteUser(userInfo);
	}

	private void inviteUser(final UserInfo member) {
		if (PhoneUtils.isMobileNum(member.getPhone())) {
			AsyncRequest.Builder builder = new AsyncRequest.Builder();
			builder.setModule(AsyncRequest.MODULE_INVITEUSER);
			builder.setRequestBody(member);
			TypeToken<Response<InviteUserInfo>> typeToken = new TypeToken<Response<InviteUserInfo>>() {
			};
			builder.setResponseType(typeToken.getType());
			builder.setResponseListener(new Listener<Response<InviteUserInfo>>() {

				@Override
				public void onErrorResponse(InvocationError arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onResponse(Response<InviteUserInfo> response) {
					if (response.getCode() == 0) {

						Toast.makeText(runActivity,
								String.format(getResources().getString(R.string.invite_user), member.getName()),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(runActivity, response.getDescription(), Toast.LENGTH_SHORT).show();
					}

				}
			});
			builder.build().post();
		} else {
			Toast.makeText(runActivity, member.getName() + "添加失败,号码格式错误或者号码已存在", Toast.LENGTH_SHORT).show();
		}
	}

	void setResult() {
		if (model == CONTACTLIST_STRAT_MODEL_MULITCHOICE) {
			Intent intent = new Intent();
			intent.setClass(runActivity, ContactlistActivity.activity.getClass());
			intent.putParcelableArrayListExtra(ContactlistActivity.CONTACTLIST_MULITCHOICE_RESULT,
					(ArrayList<UserSimpleInfo>) contactAllAdapter.getCheckedList());
			intent.putParcelableArrayListExtra(ContactlistActivity.CONTACTLIST_MULITCHOICE_GROUP_RESULT,
					(ArrayList<GroupInfo>) groupAdapter.getCheckedGroupList());
			runActivity.setResult(Activity.RESULT_OK, intent);
		}
	}

	private void doSearchCancle() {
		contactSearchInput.setCursorVisible(false);
		contactSearchCancle.setVisibility(View.GONE);
		isCancleSearchButtonVisible = false;
		contactSearchInput.setText("");
		// serachIcon.setVisibility(View.GONE);
		contactSearchClick.setVisibility(View.GONE);
		resultViewParent.setVisibility(View.GONE);
		isResultViewParentVisible = false;
		hidenSoftInput();
	}

	private void onContactAllSelected() {

		contactPager.setCurrentItem(PAGER_ALL);
		if (currentText.equals(pageAllText)) {
			notifyAdapter(pageAllResult);
		} else {
			contactSearchInput.setText(currentText);
		}
		if (isMulitChoice()) {
			organizeParent.setVisibility(View.GONE);
			indexLayout.setVisibility(View.VISIBLE);
			searchParent.setVisibility(View.VISIBLE);
			setRightButton(View.GONE);
		} else {
			organizeParent.setVisibility(View.VISIBLE);
			if ("全员".equals(currentOrganize)) {
				indexLayout.setVisibility(View.GONE);
				searchParent.setVisibility(View.GONE);
				setRightButton(View.GONE);

			} else if ("组织架构".equals(currentOrganize)) {
				indexLayout.setVisibility(View.VISIBLE);
				searchParent.setVisibility(View.VISIBLE);
				setRightButton(View.VISIBLE);

			}
		}

		setTitleTabSelect(true, false, false);
	}

	private void notifyAdapter(List<SearchResultInfo> list) {
		searchResult.clear();
		searchResult.addAll(list);
		resultAdapter.setSearchResult(searchResult);
		resultAdapter.notifyDataSetChanged();
	}

	private void setTitleTabSelect(boolean... selects) {
		contactAll.setSelected(selects[0]);
		contactGroups.setSelected(selects[1]);
		contactPublicService.setSelected(selects[2]);

	}

	private void onContactGroupsSelected() {
		searchParent.setVisibility(View.VISIBLE);
		organizeParent.setVisibility(View.GONE);
		indexLayout.setVisibility(View.GONE);
		contactPager.setCurrentItem(PAGER_GROUPS);
		if (currentText.equals(pageGroupText)) {
			notifyAdapter(pageGroupResult);
		} else {
			contactSearchInput.setText(currentText);
		}
		setRightButton(View.VISIBLE);
		setTitleTabSelect(false, true, false);
	}

	private void setRightButton(int visible) {
		if (rightButton != null) {
			rightButton.setVisibility(visible);
		}
	}

	private void onContactPublicServiceSelected() {
		searchParent.setVisibility(View.VISIBLE);
		organizeParent.setVisibility(View.GONE);
		indexLayout.setVisibility(View.GONE);
		contactPager.setCurrentItem(PAGER_PUANLIC_SERVICE);
		if (currentText.equals(pageServiceText)) {
			notifyAdapter(pagePublicServiceResult);
		} else {
			contactSearchInput.setText(currentText);
		}
		setRightButton(View.GONE);
		setTitleTabSelect(false, false, true);
	}

	private void onContactSearchClick() {
		contactSearchInput.setCursorVisible(true);
		contactSearchCancle.setVisibility(View.VISIBLE);
		isCancleSearchButtonVisible = true;
		// serachIcon.setVisibility(View.VISIBLE);
		contactSearchClick.setVisibility(View.VISIBLE);
	}

	private void onSearchClearImgClick() {
		contactSearchInput.setText("");
	}

	private void hidenSoftInput() {
		ContactlistUtils.hidenSoftInput(runActivity, contactSearchInput);
	}

	public void activityHidenSoftInput() {
		if (contactSearchInput != null) {
			hidenSoftInput();
		}
	}

	class ContactPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pagers.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(pagers.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = pagers.get(position);
			container.addView(view);
			return view;
		}
	}

	private void loadAllContactsOnLocal() {
		if (!onContactsLoadFinish) {
			allContactList.onRefreshComplete();
			return;
		}
		isShouldShowDialog = false;
		new LoadTask() {
			@Override
			public void run() {
				List<ContactInfo> allContact = manager.loadAllLocalContacts(isMulitChoice());
				if (allContact != null && allContact.size() > 0) {
					if (isMulitChoice()) {
						filterExistContacts(allContact);
					}
					List<ContactlistContactInfo> temp_Contactlist = getContactlistContactInfos(allContact);
					getUseIndexs(temp_Contactlist);
					final List<ContactlistContactInfo> temp = getuseContacts(temp_Contactlist);
					sortAllContact(temp);
					getAllContactIndexPos(temp);
					onContactsLoadFinish = true;
					runActivity.runOnUiThread(new Runnable() {
						public void run() {
							dismissDialog();
							allContactList.onRefreshComplete();
							isContactPullToRefresh = false;
							allContacts.clear();
							allContacts.addAll(temp);
							contactAllAdapter.setAllContacts(allContacts);
							setContactListAdapter();
							isShouldShowDialog = false;
						}
					});
				}

			}
		}.start();
	}

	private void loadContactsOnServer(final boolean isLoadDept) {
		System.out.println("isDialogShow:" + isDialogShow + "isContactPullToRefresh:" + isContactPullToRefresh);
		if (!isDialogShow && !isContactPullToRefresh) {
			showDialog();
		}
		final List<ContactInfo> temp_list = new ArrayList<ContactInfo>();
		onContactsLoadFinish = false;
		isRegisteredContactsLoadFinish = false;
		isUnregisteredContactsLoadFinish = isMulitChoice();
		manager.getallRegisterContactsFromServerAsync(new Listener<Response<List<ContactInfo>>>() {

			@Override
			public void onResponse(Response<List<ContactInfo>> response) {
				Log.d(TAG, "contacts/local");
				int code = response.getCode();
				if (code == AsyncRequest.ERROR_CODE_OK) {
					List<ContactInfo> temp = response.getPayload();
					if (temp == null) {
						temp = new ArrayList<ContactInfo>();
					}
					for (ContactInfo contactInfo : temp) {
						if (contactInfo != null) {
							contactInfo.setRegister(true);
						}
					}
					temp_list.addAll(temp);
				}
				isRegisteredContactsLoadFinish = true;
				registeredContactLoadSuccess = code == AsyncRequest.ERROR_CODE_OK;
				if (isRegisteredContactsLoadFinish && isUnregisteredContactsLoadFinish) {
					onContactsLoadFinish(temp_list, registeredContactLoadSuccess, unRegisteredContactLoadSuccess);
				}
				if (isLoadDept) {
					loadContactDeptOnServer();
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				isRegisteredContactsLoadFinish = true;
				if (isRegisteredContactsLoadFinish && isUnregisteredContactsLoadFinish) {
					onContactsLoadFinish(temp_list, false, unRegisteredContactLoadSuccess);
				}

				if (isLoadDept) {
					loadContactDeptOnServer();
				}
			}
		});

		if (isUnregisteredContactsLoadFinish) {
			return;
		}
		manager.getallUnregisterContactsFromServerAsync(new Listener<Response<List<ContactInfo>>>() {

			@Override
			public void onResponse(Response<List<ContactInfo>> response) {
				if (response.getCode() == AsyncRequest.ERROR_CODE_OK) {
					List<ContactInfo> temp = response.getPayload();
					if (temp == null) {
						temp = new ArrayList<ContactInfo>();
					}
					for (ContactInfo contactInfo : temp) {
						contactInfo.setRegister(false);
					}
					temp_list.addAll(temp);
				}
				isUnregisteredContactsLoadFinish = true;
				unRegisteredContactLoadSuccess = response.getCode() == AsyncRequest.ERROR_CODE_OK;
				if (isRegisteredContactsLoadFinish && isUnregisteredContactsLoadFinish) {
					onContactsLoadFinish(temp_list, registeredContactLoadSuccess, unRegisteredContactLoadSuccess);
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				isUnregisteredContactsLoadFinish = true;
				if (isRegisteredContactsLoadFinish && isUnregisteredContactsLoadFinish) {
					onContactsLoadFinish(temp_list, registeredContactLoadSuccess, false);
				}

			}
		});
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

	private void dismissDialog() {
		DialogState ds = (DialogState) runActivity;
		isDialogShow = ds.isDialogShow() && isDialogShow;
		System.out.println("dismissDialog:" + isDialogShow);
		if (isDialogShow && checkDialogCanDismiss()) {
			runActivity.dismissDialog(CONTACTS_LOAD_DG_ID);
			isDialogShow = false;
			isShouldShowDialog = false;
		}
	}

	private boolean checkDialogCanDismiss() {
		return onGrouosLoadFinish && onContactsLoadFinish && onPublicsLoadFinish && onReloadGroupchildFinish;
	}

	private void onContactsLoadFinish(final List<ContactInfo> list, final boolean isRegisterSuccess,
			final boolean isUnregisterSuccess) {
		boolean isSuccess = isRegisterSuccess || isUnregisterSuccess;
		if (!isSuccess) {
			onContactsLoadFinish = true;
			allContactList.onRefreshComplete();
			isContactPullToRefresh = false;
			dismissDialog();
			return;
		}
		new LoadTask() {
			@Override
			public void run() {
				if (isRegisterSuccess) {
					manager.deleteAllRegisteredContacts();
				}
				if (isUnregisterSuccess) {
					manager.deleteAllUnregisteredContacts();
				}
				manager.addContacts(list);
				reloadGroupChild();
				runActivity.runOnUiThread(new Runnable() {
					public void run() {
						onContactsLoadFinish = true;
						if (!ContactlistUtils.isEmptyList(list)) {
							loadAllContactsOnLocal();
						} else {
							if (allContactList != null) {
								allContactList.onRefreshComplete();
								isContactPullToRefresh = false;
							}
							dismissDialog();
							checkHasContacts();
							contactAllAdapter.setAllContacts(allContacts);
							setContactListAdapter();
						}
					}
				});
			}
		}.start();
	}

	private void checkHasContacts() {
		if (ContactlistUtils.isEmptyList(allContacts)) {
			Toast.makeText(runActivity, R.string.contacts_loading_failed, Toast.LENGTH_SHORT).show();
		}
	}

	private List<ContactlistContactInfo> getContactlistContactInfos(List<ContactInfo> list) {
		List<ContactlistContactInfo> infos = new ArrayList<ContactlistContactInfo>();
		for (ContactInfo info : list) {
			infos.add(new ContactlistContactInfo(info, ContactlistContactInfo.VIEW_TYPE_CONTACT));
		}

		return infos;
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

	private void sortAllContact(List<ContactlistContactInfo> list) {
		Collections.sort(list, new Comparator<ContactlistContactInfo>() {

			@Override
			public int compare(ContactlistContactInfo lhs, ContactlistContactInfo rhs) {
				int result = new Boolean(rhs.getContactInfo().isRegister()).compareTo(new Boolean(lhs.getContactInfo()
						.isRegister()));
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
	}

	private void getAllContactIndexPos(List<ContactlistContactInfo> list) {
		for (int i = 0; i < list.size(); i++) {
			ContactlistContactInfo info = list.get(i);
			if (info.getViewType() == ContactlistContactInfo.VIEW_TYPE_INDEX) {
				allContactIndexPosInList.put(info.getContactInfo().getName(), i);
			}
		}
	}

	private void loadAllGroupsOnLocal() {
		if (!onGrouosLoadFinish) {
			groupContactList.onRefreshComplete();
			return;
		}
		new LoadTask() {
			@Override
			public void run() {
				List<GroupInfo> groups_temp = manager.loadAllLocalGroups();
				if (!ContactlistUtils.isEmptyList(groups_temp)) {
					final List<ContactlistGroupInfo> temp = getContactlistGroupInfo(groups_temp);
					sortGroups(temp);
					onGrouosLoadFinish = true;
					runActivity.runOnUiThread(new Runnable() {
						public void run() {
							isGroupPullToRefresh = false;
							dismissDialog();
							groupContactList.onRefreshComplete();
							groupInfos.clear();
							groupInfos.addAll(temp);
							groupAdapter.setGroupInfos(groupInfos);
							setGroupsAdapter();
							if (isIntentToGroupDetail) {
								isIntentToGroupDetail = false;
							}
						}
					});
				}
			}
		}.start();

	}

	private List<ContactlistGroupInfo> getContactlistGroupInfo(List<GroupInfo> groups) {
		List<ContactlistGroupInfo> temp = new ArrayList<ContactlistGroupInfo>();
		for (GroupInfo info : groups) {
			ContactlistGroupInfo groupInfo = new ContactlistGroupInfo(info);
			groupInfo.setUsers(getGroupChildsInfo(info.getUsers()));
			temp.add(groupInfo);
		}
		return temp;
	}

	private ContactInfo[] getGroupChildsInfo(UserSimpleInfo[] infos) {
		List<ContactInfo> list = manager.getGroupMembersInfo(infos);
		ContactlistUtils.sortGroupChild(list, runActivity);
		return list.toArray(new ContactInfo[list.size()]);
	}

	private void reloadGroupChild() {
		if (isContactPullToRefresh) {
			return;
		}
		onReloadGroupchildFinish = false;
		final List<ContactlistGroupInfo> temp = new ArrayList<ContactlistGroupInfo>(groupInfos);
		for (ContactlistGroupInfo info : temp) {
			UserSimpleInfo[] users = info.getInfo().getUsers();
			info.setUsers(getGroupChildsInfo(users));
		}
		runActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				onReloadGroupchildFinish = true;
				dismissDialog();
				groupInfos.clear();
				groupInfos.addAll(temp);
				groupAdapter.setGroupInfos(groupInfos);
				setGroupsAdapter();
			}
		});
	}

	private void sortGroups(List<ContactlistGroupInfo> list) {
		Collections.sort(list, new Comparator<ContactlistGroupInfo>() {

			@Override
			public int compare(ContactlistGroupInfo lhs, ContactlistGroupInfo rhs) {
				int reuslt = new Long(lhs.getInfo().getId()).compareTo(new Long(rhs.getInfo().getId()));
				return reuslt;
			}
		});
	}

	private void loadAllGroupsFromServer() {
		System.out.println("isDialogShow:" + isDialogShow + "isGroupPullToRefresh:" + isGroupPullToRefresh);
		if (!isDialogShow && !isGroupPullToRefresh) {
			showDialog();
		}
		onGrouosLoadFinish = false;

		final List<GroupInfo> temp_list = new ArrayList<GroupInfo>();
		manager.getAllGroupsFromServerAsync(new Listener<Response<List<GroupInfo>>>() {

			@Override
			public void onResponse(Response<List<GroupInfo>> response) {
				if (response.getCode() == AsyncRequest.ERROR_CODE_OK) {
					List<GroupInfo> temp = response.getPayload();
					if (temp == null) {
						temp = new ArrayList<GroupInfo>();
					}
					temp_list.addAll(temp);
				}
				boolean success = response.getCode() == AsyncRequest.ERROR_CODE_OK;
				onGroupsLoadFinish(temp_list, success);
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				onGroupsLoadFinish(temp_list, false);
			}
		});
	}

	private void onGroupsLoadFinish(final List<GroupInfo> list, boolean isSuccess) {
		if (!isSuccess) {
			groupContactList.onRefreshComplete();
			onGrouosLoadFinish = true;
			isGroupPullToRefresh = false;
			dismissDialog();
			return;
		}
		new LoadTask() {
			public void run() {
				manager.deleteAllGroups();
				manager.addGrouops(list);
				runActivity.runOnUiThread(new Runnable() {
					public void run() {
						onGrouosLoadFinish = true;
						isGroupPullToRefresh = false;
						if (ContactlistUtils.isEmptyList(list)) {
							if (groupContactList != null) {
								groupContactList.onRefreshComplete();
							}
							if (isIntentToGroupDetail) {
								isIntentToGroupDetail = false;
							}
							dismissDialog();
							groupInfos.clear();
							groupAdapter.setGroupInfos(groupInfos);
							setGroupsAdapter();
						} else {
							loadAllGroupsOnLocal();
						}

					}
				});
			};
		}.start();
	}

	private void loadAllPublicServiceOnLocal() {
		if (!onPublicsLoadFinish) {
			publicServiceContactList.onRefreshComplete();
			return;
		}

		new LoadTask() {
			public void run() {
				final List<CommonContactInfo> temp_list = manager.getAllPublicService();
				if (!ContactlistUtils.isEmptyList(temp_list)) {
					sortPublicServices(temp_list);
					onPublicsLoadFinish = true;
					runActivity.runOnUiThread(new Runnable() {
						public void run() {
							isServicePullToRefresh = false;
							dismissDialog();
							publicServiceContactList.onRefreshComplete();
							publicServices.clear();
							publicServices.addAll(temp_list);
							commonServiceAdapter.setPublicServices(publicServices);
							setPublicServiceAdapter();
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
				int result = new Long(lhs.getId()).compareTo(new Long(rhs.getId()));
				return result;
			}
		});
	}

	private void loadPublicServiceFromServer() {
		System.out.println("isDialogShow:" + isDialogShow + "isServicePullToRefresh:" + isServicePullToRefresh);
		if (!isDialogShow && !isServicePullToRefresh) {
			showDialog();
		}
		onPublicsLoadFinish = false;
		final List<CommonContactInfo> temp_list = new ArrayList<CommonContactInfo>();
		manager.getAllPublicServiceFromServerAsyn(new Listener<Response<List<CommonContactInfo>>>() {

			@Override
			public void onErrorResponse(InvocationError error) {
				onPublicServicesLoadFinish(temp_list, false);
			}

			@Override
			public void onResponse(Response<List<CommonContactInfo>> response) {
				Log.d(TAG, "contacts/common");
				isServicePullToRefresh = false;
				if (response.getCode() == AsyncRequest.ERROR_CODE_OK) {
					List<CommonContactInfo> temp = response.getPayload();
					if (temp == null) {
						temp = new ArrayList<CommonContactInfo>();
					}
					temp_list.addAll(temp);
				}
				boolean success = response.getCode() == AsyncRequest.ERROR_CODE_OK;
				onPublicServicesLoadFinish(temp_list, success);
			}
		});
	}

	private void onPublicServicesLoadFinish(final List<CommonContactInfo> list, boolean isSuccess) {
		if (!isSuccess) {
			publicServiceContactList.onRefreshComplete();
			onPublicsLoadFinish = true;
			isServicePullToRefresh = false;
			dismissDialog();
			return;
		}
		new LoadTask() {
			public void run() {
				manager.deleteAllPublicService();
				manager.addPublicService(list);
				runActivity.runOnUiThread(new Runnable() {
					public void run() {
						isServicePullToRefresh = false;
						onPublicsLoadFinish = true;
						if (ContactlistUtils.isEmptyList(list)) {
							if (publicServiceContactList != null) {
								publicServiceContactList.onRefreshComplete();
							}
							publicServices.clear();
							commonServiceAdapter.setPublicServices(publicServices);
							setPublicServiceAdapter();
							dismissDialog();
						} else {
							loadAllPublicServiceOnLocal();
						}

					}
				});
			};
		}.start();
	}

	@Override
	public boolean onBackPressed() {
		if (contactSearchClick == null) {
			return false;
		}
		if (contactSearchClick.getVisibility() == View.VISIBLE) {
			doSearchCancle();
			return true;
		}
		return false;
	}

	private void checkDataUpdate() {
		if (isViewDestoryed) {
			return;
		}
		getCustUpdatetime();
	}

	private void loadAllDatas() {
		if (isViewDestoryed) {
			return;
		}
		loadAllGroupsOnLocal();
		loadAllContactsOnLocal();
		if (!isMulitChoice()) {
			loadContactDept(false);
			loadAllPublicServiceOnLocal();

		}
	}

	private void getCustUpdatetime() {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_CUSTS_UPDATETIMES);
		TypeToken<Response<CustLastUpdateTimesInfo>> typeToken = new TypeToken<Response<CustLastUpdateTimesInfo>>() {
		};
		builder.setResponseType(typeToken.getType());
		builder.setResponseListener(new Listener<Response<CustLastUpdateTimesInfo>>() {

			@Override
			public void onErrorResponse(InvocationError arg0) {
				loadAllDatas();
			}

			@Override
			public void onResponse(Response<CustLastUpdateTimesInfo> response) {
				if (response.getCode() == 0) {
					CustLastUpdateTimesInfo info = response.getPayload();
					if (getActivity() == null) {
						return;
					}
					SharedPreferences sp = SharedUtils.getContactLastUpdateTime(runActivity);
					String userLastUpdateTime = sp.getString(Config.USER_LAST_UPDATETIME, null);
					String groupLastUpdateTime = sp.getString(Config.GROUP_LAST_UPDATETIME, null);
					String deptLastUpdateTime = sp.getString(Config.DEPT_LAST_UPDATETIME, null);
					String commonContactLastUpdateTime = sp.getString(Config.COMMON_CONTACT_LAST_UPDATETIME, null);
					Editor editor = sp.edit();
					if (!TextUtils.isEmpty(info.getUserLastUpdateTime())) {
						if (TextUtils.isEmpty(userLastUpdateTime)
								|| !userLastUpdateTime.equals(info.getUserLastUpdateTime())) {
							editor.putString(Config.USER_LAST_UPDATETIME, info.getUserLastUpdateTime());
							manager.deleteAllRegisteredContacts();
							manager.deleteAllUnregisteredContacts();
							loadContactsOnServer(false);
						}
					}

					loadAllContactsOnLocal();
					if (!TextUtils.isEmpty(info.getGroupLastUpdateTime())) {
						if (TextUtils.isEmpty(groupLastUpdateTime)
								|| !groupLastUpdateTime.equals(info.getGroupLastUpdateTime())) {
							editor.putString(Config.GROUP_LAST_UPDATETIME, info.getGroupLastUpdateTime());
							manager.deleteAllGroups();
							loadAllGroupsFromServer();
						}
					}

					//					loadAllGroupsOnLocal();
					if (!isMulitChoice()) {
						if (!TextUtils.isEmpty(info.getDeptLastUpdateTime())) {
							if (TextUtils.isEmpty(deptLastUpdateTime)
									|| !deptLastUpdateTime.equals(info.getDeptLastUpdateTime())) {
								editor.putString(Config.DEPT_LAST_UPDATETIME, info.getDeptLastUpdateTime());
								manager.deleteAllDept();
								manager.deleteAllDeptUser();
								loadContactDeptOnServer();
							}
						}
						//						loadContactDept(false);
					}

					if (!isMulitChoice()) {
						if (!TextUtils.isEmpty(info.getCommonContactLastUpdateTime())) {
							if (TextUtils.isEmpty(commonContactLastUpdateTime)
									|| !commonContactLastUpdateTime.equals(info.getCommonContactLastUpdateTime())) {
								editor.putString(Config.COMMON_CONTACT_LAST_UPDATETIME,
										info.getCommonContactLastUpdateTime());
								manager.deleteAllPublicService();
								loadPublicServiceFromServer();
							}
						}

						//						loadAllPublicServiceOnLocal();
					}
					editor.commit();

				} else {
					loadAllDatas();
				}

			}

		});

		builder.build().get();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isIntentToGroupDetail) {
			loadAllGroupsOnLocal();
		}
		hidenSoftInput();
	}

	private void intentToContactDetailActivity(ContactInfo info) {
		System.out.println("info.userid:" + info.getUserId());
		Intent intent = new Intent();
		intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, info);
		intent.setClass(runActivity, ContactDetailActivity.class);
		startActivity(intent);
	}

	private void intentToContactDetailActivityFromStruct(ContactInfo info) {
		Intent intent = new Intent();
		intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, info);
		intent.putExtra(ContactDetailActivity.EXTRA_FROM_STRUCT, true);
		intent.setClass(runActivity, ContactDetailActivity.class);
		startActivity(intent);
	}

	private void intentToGroupDetailActivity(ContactlistGroupInfo info) {
		isIntentToGroupDetail = true;
		Intent intent = new Intent();
		if (info != null) {
			intent.putExtra(GroupDetailActivity.EXTRA_GROUP_ID, info.getInfo().getId());
		}
		intent.setClass(runActivity, GroupDetailActivity.class);
		startActivityForResult(intent, UPDATE_GROUP_NEED_REFRESH);
	}

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

	private void startPhone(String phoneNum) {
		ContactlistUtils.startCall(runActivity, phoneNum);
	}

	@Override
	public CharSequence getPageTitle(Context context) {
		return context.getResources().getString(R.string.contact);
	}

	@Override
	public boolean onRightButtonClick() {
		int current = contactPager.getCurrentItem();
		if (current == PAGER_GROUPS) {
			intentToGroupDetailActivity(null);
			return true;
		} else if (current == PAGER_ALL) {
			showMenu();
			return true;
		}
		return false;
	}

	private void showMenu() {
		SelectPopupMenu menu = new SelectPopupMenu(runActivity);
		menu.setTitle("邀请成员加入");
		menu.setOnItemClickListener(this);
		menu.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				if (runActivity instanceof ContactlistActivity || runActivity instanceof MainActivity) {
					((TitleFragmentActivity) runActivity).lightOn();
				}

			}
		});
		menu.show();
		if (runActivity instanceof ContactlistActivity || runActivity instanceof MainActivity) {
			((TitleFragmentActivity) runActivity).lightOff();
		}
	}

	@Override
	public void configRightButton(ImageView imgView) {
		Log.d(TAG, "configRightButton:" + contactPager);
		int current = 0;
		if (contactPager != null) {
			current = contactPager.getCurrentItem();
		}
		imgView.setImageResource(R.drawable.contact_add_group);
		if (current == PAGER_GROUPS) {
			imgView.setVisibility(View.VISIBLE);
		} else if (current == PAGER_ALL) {
			if ("组织架构".equals(currentOrganize)) {
				setRightButton(View.VISIBLE);
			} else {
				setRightButton(View.GONE);
			}
		} else {
			imgView.setVisibility(View.GONE);
		}
		rightButton = imgView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		isViewDestoryed = true;
		ViewGroup parent = (ViewGroup) root.getParent();
		if (parent != null) {
			parent.removeView(root);
		}
	}

	public interface DialogState {
		public boolean isDialogShow();

		public void setDialogShow(boolean show);
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
}
