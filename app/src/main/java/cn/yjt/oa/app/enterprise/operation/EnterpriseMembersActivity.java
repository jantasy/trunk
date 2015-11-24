package cn.yjt.oa.app.enterprise.operation;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.AreaUser;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserListInfo;
import cn.yjt.oa.app.beans.UserManagerInfo;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.enterprise.contact.MachineContactActivity;
import cn.yjt.oa.app.enterprise.contact.MachineContactInfo;
import cn.yjt.oa.app.enterprise.contact.PhoneUtils;
import cn.yjt.oa.app.enterprise.operation.MemberAdapter.OnSearchMemptyListener;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.signin.SigninActivity;

import com.umeng.analytics.MobclickAgent;

public class EnterpriseMembersActivity extends TitleFragmentActivity implements
		OnClickListener, TextWatcher,
		Listener<Response<List<UserManagerInfo>>>, OnItemClickListener {
	private MemberAdapter adapter;
	private List<UserManagerInfo> members;
	private List<UserManagerInfo> sources;
	private List<UserManagerInfo> modifys;

	private List<MachineContactInfo> memberContact;
	private ListView listView;
	private Button btnAdd;
	private ProgressBar progress;
	private ProgressDialog progressDialog;
	private TextView noMemberPromit;
	private LinearLayout contactSearch;
	private OperationMembersMenu operationMenu;
	private UserInfo userInfo;

	private ImageView serachIcon;
	private EditText searchInput;
	private LinearLayout textClear;
	private Button searchCancel;

	private boolean isResultViewParentVisible = true;
	private OnSearchMemptyListener mListener = new OnSearchMemptyListener() {

		@Override
		public void adapterIsEmpty() {
			doSearchCancle();
		}
	};

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.enterprise_memeber_list);
		initAdapter();
		initView();
		initUserManagerInfo();
	}

	private void initView() {
		userInfo = AccountManager.getCurrent(this);
		getLeftbutton().setImageResource(R.drawable.navigation_back);

		progress = (ProgressBar) findViewById(R.id.enterprise_member_progressbar);
		listView = (ListView) findViewById(R.id.enterprise_member_list);

		contactSearch = (LinearLayout) findViewById(R.id.contact_search_parent);
		serachIcon = (ImageView) findViewById(R.id.contact_search_icon);
		searchInput = (EditText) findViewById(R.id.address_search_input);
		searchInput.setOnClickListener(this);
		searchInput.addTextChangedListener(this);

		// searchInput.setCursorVisible(true);
		// searchInput.setText("");
		textClear = (LinearLayout) findViewById(R.id.contact_search_clear_img);
		textClear.setOnClickListener(this);
		searchCancel = (Button) findViewById(R.id.contact_search_cancle);
		searchCancel.setOnClickListener(this);

		listView.setItemsCanFocus(true);
		listView.setAdapter(adapter);
		btnAdd = (Button) findViewById(R.id.enterprise_member_add);
		getRightButton().setImageResource(R.drawable.contact_list_save);
		if (userInfo.getIsYjtUser() == 1) {
			btnAdd.setVisibility(View.GONE);
		} else {
			btnAdd.setVisibility(View.VISIBLE);
			btnAdd.setOnClickListener(this);
		}
		noMemberPromit = (TextView) findViewById(R.id.enterprise_no_member_promit);
		listView.setEmptyView(noMemberPromit);

		listView.setOnItemClickListener(this);

		initOperationMemberMenu();

	}

	@Override
	public void onBackPressed() {
		if (adapter.isModify()) {
			AlertDialogBuilder
					.newBuilder(this)
					.setMessage("您的成员列表有修改，是否提交？")
					.setPositiveButton("提交",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									onRightButtonClick();
								}
							})
					.setNegativeButton("放弃",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									superOnBackPressed();
								}
							}).show();
		} else {
			super.onBackPressed();
		}
	}

	private void superOnBackPressed() {
		super.onBackPressed();
	}

	private void initAdapter() {
		members = new ArrayList<UserManagerInfo>();
		modifys = new ArrayList<UserManagerInfo>();
		adapter = new MemberAdapter(this);
		adapter.setOnSearchMemptyListener(mListener);
		adapter.bindData(members);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.enterprise_member_add:
			showMenu();
			break;
		case R.id.btn_select_local:
			startActivityForContacts();
			break;
		case R.id.btn_select_manual:
			startActivityForManual();
			break;
		case R.id.contact_search_cancle:
			doSearchCancle();
			break;
		case R.id.address_search_input:
			onSearchClick();
			break;
		case R.id.contact_search_clear_img:
			onSearchClearImgClick();
			break;
		default:
			break;
		}
	}

	private void onSearchClearImgClick() {
		searchInput.setText("");
	}

	// TODO:
	private void onSearchClick() {
		btnAdd.setVisibility(View.GONE);
		searchInput.requestFocus();
		searchInput.setCursorVisible(true);
		searchCancel.setVisibility(View.VISIBLE);
		matchSearchInput("");
	}

	private void matchSearchInput(String input) {
		List<UserManagerInfo> selectedMatched = findMatchUserManagerInfoWithName(
				members, input);
		adapter.bindData(selectedMatched);
		adapter.notifyDataSetChanged();
		if (selectedMatched.size() == 0) {
			noMemberPromit.setVisibility(View.GONE);
		}
	}

	private void doSearchCancle() {
		if (userInfo.getIsYjtUser() == 1) {
			btnAdd.setVisibility(View.GONE);
		} else {
			btnAdd.setVisibility(View.VISIBLE);
			// btnAdd.setOnClickListener(this);
		}
		searchInput.setText("");
		searchInput.clearFocus();
		searchInput.setCursorVisible(false);
		hideSoftInput();
		searchCancel.setVisibility(View.GONE);
		showDefaultList();
	}

	private void showDefaultList() {
		for (UserManagerInfo info : adapter.getDeleteMembers()) {
			if (members.contains(info)) {
				members.remove(info);
			}
		}
		adapter.bindData(members);
		adapter.notifyDataSetChanged();
	}

	private void startActivityForManual() {
		Intent intent = new Intent(this, AddMemberActivity.class);
		startActivityForResult(intent, REQUEST_CODE_INPUT_MANUAL);
	}

	private void showMenu() {
		SelectPopupMenu menu = new SelectPopupMenu(this);
		menu.setOnItemClickListener(this);
		menu.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				lightOn();
			}
		});
		menu.show();
		lightOff();
	}

	private void startActivityForContacts() {
		Intent intent = new Intent(this, MachineContactActivity.class);
		memberContact = new ArrayList<MachineContactInfo>();
		for (int i = 0; i < adapter.getMembers().size(); i++) {
			MachineContactInfo info = new MachineContactInfo();
			info.setDisplayName(adapter.getMembers().get(i).getName());
			info.setNumber(adapter.getMembers().get(i).getPhone());
			memberContact.add(info);
		}

		intent.putParcelableArrayListExtra(MachineContactActivity.MEMBER_LIST,
				(ArrayList<? extends Parcelable>) memberContact);
		startActivityForResult(intent, REQUEST_CODE_SELECT_CONTACTS);
	}

	private static final int REQUEST_CODE_SELECT_CONTACTS = 0;
	private static final int REQUEST_CODE_INPUT_MANUAL = 1;
	private RelativeLayout resultViewParent;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_SELECT_CONTACTS:
				getContactsFromLocal(data);
				break;
			case REQUEST_CODE_INPUT_MANUAL:
				getMemberWithManual(data);
				break;

			default:
				break;
			}
		}
	}

	private void getContactsFromLocal(Intent data) {
		Parcelable[] contactList = data
				.getParcelableArrayExtra(MachineContactActivity.MACHINE_CONTACT_LIST);
		if (contactList != null && contactList.length > 0) {
			for (int i = 0; i < contactList.length; i++) {
				MachineContactInfo info = (MachineContactInfo) contactList[i];
				setMember(info.getDisplayName(), info.getNumber());
			}

		}

	}

	private void setMember(String username, String usernumber) {
		UserManagerInfo member = new UserManagerInfo();
		member.setName(username);
		member.setPhone(PhoneUtils.formatPhoneNumber(usernumber));
		addMember(member);
	}

	private void getMemberWithManual(Intent data) {
		String name = data.getStringExtra(AddMemberActivity.EXTRA_MEMBER_NAME);
		String phone = data
				.getStringExtra(AddMemberActivity.EXTRA_MEMBER_PHONE);
		setMember(name, phone);
	}

	private void addMember(UserManagerInfo member) {
		if (PhoneUtils.isMobileNum(member.getPhone())
				&& !adapter.getMembers().contains(member)) {
			adapter.addMember(member);
		} else {
			toast(member.getName() + "添加失败,号码格式错误或者号码已存在");
		}
	}

	private void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	private void initUserManagerInfo() {
		progress.setVisibility(View.VISIBLE);

		contactSearch.setVisibility(View.GONE);
		getAsyncRequest(
				String.format(AsyncRequest.MODULE_CUSTS_USERS, getCustId()))
				.get();
	}

	private AsyncRequest getAsyncRequest(String module) {
		Type type_enterprise = new TypeToken<Response<List<UserManagerInfo>>>() {
		}.getType();
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(module).setResponseType(type_enterprise)
				.setResponseListener(this);
		return builder.build();
	}

	private void initProgressDialog(String msg) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(msg);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	public void update() {
		final UserListInfo userListInfo = getUserListInfo();
		if (!userListInfo.getAdds().isEmpty()) {
			MobclickAgent.onEventValue(this,
					"enterprise_manage_member_list_add", null, userListInfo
							.getAdds().size());
		}
		if (!userListInfo.getDeletes().isEmpty()) {
			MobclickAgent.onEventValue(this,
					"enterprise_manage_member_list_remove", null, userListInfo
							.getDeletes().size());
		}
		if (!userListInfo.getUpdates().isEmpty()) {
			MobclickAgent.onEventValue(this,
					"enterprise_manage_member_list_update", null, userListInfo
							.getUpdates().size());
		}
		initProgressDialog("开始更新数据");
		Type type_enterprise = new TypeToken<Response<UserListInfo>>() {
		}.getType();
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(
				String.format(AsyncRequest.MODULE_CUSTS_USERS, getCustId()))
				.setRequestBody(userListInfo).setResponseType(type_enterprise)
				.setResponseListener(new Listener<Response<UserListInfo>>() {

					@Override
					public void onErrorResponse(InvocationError error) {
						progressDialog.dismiss();
					}

					@Override
					public void onResponse(Response<UserListInfo> response) {
						progressDialog.dismiss();
						if (response.getCode() == 0) {
							// if(!userListInfo.getDeletes().isEmpty()){
							// MainApplication.clearContacts();
							// MainActivity.launchClearTask(EnterpriseMembersActivity.this);
							// }else{
							finish();
							// }

						} else if (response.getCode() == 11) {
							toast("服务器繁忙");
						} else {
							toast(response.getDescription());
						}

					}

				}).build().put();
	}

	@Override
	public void onErrorResponse(InvocationError error) {
		progress.setVisibility(View.GONE);
		contactSearch.setVisibility(View.VISIBLE);
		searchInput.requestFocus();
	}

	@Override
	public void onResponse(Response<List<UserManagerInfo>> response) {
		progress.setVisibility(View.GONE);
		contactSearch.setVisibility(View.VISIBLE);
		searchInput.requestFocus();
		if (response != null) {
			sources = response.getPayload();
			addMembers(sources);
		}
	}

	private void addMembers(List<UserManagerInfo> list) {
		addAll(members, list);
		adapter.notifyDataSetChanged();
	}

	private List<UserManagerInfo> getAdds() {
		List<UserManagerInfo> dest = new ArrayList<UserManagerInfo>();
		// List<UserManagerInfo> temp = adapter.getMembers();
		addAll(dest, adapter.getMembers());
		dest.removeAll(sources);
		return dest;
	}

	private List<UserManagerInfo> getDeletes() {
		List<UserManagerInfo> src = new ArrayList<UserManagerInfo>();
		List<UserManagerInfo> dest = new ArrayList<UserManagerInfo>();
		addAll(dest, adapter.getDeleteMembers());
		addAll(src, sources);
		// src.removeAll(dest);
		for (UserManagerInfo info : dest) {
			if (sources.contains(info)) {
				sources.remove(info);
			}
		}

		return adapter.getDeleteMembers();
	}

	private List<UserManagerInfo> getUpdates() {
		List<UserManagerInfo> src = new ArrayList<UserManagerInfo>();
		List<UserManagerInfo> dest = new ArrayList<UserManagerInfo>();
		addAll(dest, modifys);
		addAll(src, getAdds());
		dest.removeAll(src);
		return dest;
	}

	private void addAll(List<UserManagerInfo> src, List<UserManagerInfo> dest) {
		if (dest != null) {
			src.addAll(dest);
		}
	}

	private String getCustId() {
		return AccountManager.getCurrent(this).getCustId();
	}

	private UserListInfo getUserListInfo() {
		UserListInfo info = new UserListInfo();
		info.setAdds(getAdds());
		info.setDeletes(getDeletes());
		info.setUpdates(getUpdates());
		return info;
	}

	public static void launch(Activity activity) {
		Intent intent = new Intent(activity, EnterpriseMembersActivity.class);
		activity.startActivity(intent);
	}

	@Override
	public void onRightButtonClick() {
		doSearchCancle();
		if (sources != null) {
			update();
			MobclickAgent.onEvent(this, "enterprise_manage_member_list_save");
		} else {
			Toast.makeText(this, "无数据不能提交", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onLeftButtonClick() {
		doSearchCancle();
		onBackPressed();
	}

	private void initOperationMemberMenu() {
		operationMenu = new OperationMembersMenu(this);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MobclickAgent.onEvent(this, "enterprise_manage_member_list_clickitem");
		operationMenu.setmUserManagerInfo((UserManagerInfo) adapter
				.getItem(position));
		ClickLListener mListener = new ClickLListener();
		operationMenu.setOnItemClickLListener(mListener);
		operationMenu.show();
		lightOff();
		operationMenu
				.setOnDismissListener(new DialogInterface.OnDismissListener() {

					// @Override
					// public void onDismiss() {
					// }

					@Override
					public void onDismiss(DialogInterface dialog) {
						adapter.notifyDataSetChanged();
						lightOn();
					}
				});

	}

	// @Override
	// public boolean dispatchKeyEvent(KeyEvent event) {
	// if(event.getAction()==KeyEvent.KEYCODE_BACK){
	// if(operationMenu!=null && operationMenu.isShowing()){
	// operationMenu.dismissMenu();
	// return false;
	// }
	// }
	// return super.dispatchKeyEvent(event);
	// }

	private class ClickLListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.setting_admin:
				MobclickAgent.onEvent(EnterpriseMembersActivity.this,
						"enterprise_manage_member_list_dialog_setadminbtn");
				if (modifys != null
						&& !modifys.contains(operationMenu
								.getmUserManagerInfo())) {
					modifys.add(operationMenu.getmUserManagerInfo());
				} else {
					modifys.remove(operationMenu.getmUserManagerInfo());
					modifys.add(operationMenu.getmUserManagerInfo());
				}

				break;
			case R.id.check_attendance:
				MobclickAgent.onEvent(EnterpriseMembersActivity.this,
						"enterprise_manage_member_list_dialog_modifyinfo");
				SigninActivity.launchWithUserId(EnterpriseMembersActivity.this,
						operationMenu.getmUserManagerInfo().getId());
				operationMenu.dismiss();
				break;
			case R.id.member_delete:
				MobclickAgent.onEvent(EnterpriseMembersActivity.this,
						"enterprise_manage_member_list_dialog_checkattendance");
				// showDialog(operationMenu.getmUserManagerInfo());
				adapter.removeMember(operationMenu.getmUserManagerInfo());
				if (modifys != null
						&& modifys
								.contains(operationMenu.getmUserManagerInfo())) {
					modifys.remove(operationMenu.getmUserManagerInfo());
				}

				operationMenu.dismiss();
				break;
			case R.id.member_save:
				MobclickAgent.onEvent(EnterpriseMembersActivity.this,
						"enterprise_manage_member_list_dialog_delete");
				if (modifys != null
						&& !modifys.contains(operationMenu
								.getmUserManagerInfo())) {
					modifys.add(operationMenu.getmUserManagerInfo());
				} else {
					modifys.remove(operationMenu.getmUserManagerInfo());
					modifys.add(operationMenu.getmUserManagerInfo());
				}
				break;

			default:
				break;
			}

		}

	}

	private List<UserManagerInfo> findMatchUserManagerInfoWithName(
			List<UserManagerInfo> userManagerInfos, String name) {

		List<UserManagerInfo> matchUserManagerInfo = new ArrayList<UserManagerInfo>();
		if (TextUtils.isEmpty(name)) {
			return matchUserManagerInfo;
		}
		// TODO:
		// if(userManagerInfos!=null){
		for (UserManagerInfo userManagerInfo : userManagerInfos) {
			//防止用户名为空的情况，如果用户名为空，将其改为匿名
			if(userManagerInfo.getName()==null){
				userManagerInfo.setName("匿名");
			}
			//Log.e("UserManagerInfo", "用户名" + userManagerInfo.getName());
			if (userManagerInfo.getName().contains(name)
					|| isPinYinContains(userManagerInfo.getName(), name)) {
				matchUserManagerInfo.add(userManagerInfo);
			}
		}
		return matchUserManagerInfo;
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

	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() > 0) {
			matchSearchInput(s.toString());
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}
}
