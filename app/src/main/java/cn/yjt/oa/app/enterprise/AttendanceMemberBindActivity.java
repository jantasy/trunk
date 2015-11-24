package cn.yjt.oa.app.enterprise;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.AreaUser;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.contactlist.server.ServerLoader;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;

public class AttendanceMemberBindActivity extends BackTitleFragmentActivity implements OnClickListener,
		OnCheckedChangeListener, TextWatcher, OnScrollListener {

	private final String TAG = "AttendanceMemberBindActivity";

	private List<AreaUser> selectedUsers = new ArrayList<AreaUser>();
	private List<AreaUser> unselectedUsers = new ArrayList<AreaUser>();
	private MemberAdapter selectedMemberAdapter;
	private MemberAdapter unselectedMemberAdapter;
	private CheckBox allUnselectedCheckBox;
	private CheckBox allSelectedCheckBox;

	private long areaId;
	private EditText searchInput;
	private ListView selectedListView;
	private ListView unselectedListView;
	private View searchCancel;
    private View clearInput;
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		 /*记录操作 0813*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_MANAGE_ATTENDANCE_AREAPERSON);

		setContentView(R.layout.activity_attendance_member_bind);

		getRightButton().setImageResource(R.drawable.contact_list_save);

		findViewById(R.id.add).setOnClickListener(this);
		findViewById(R.id.remove).setOnClickListener(this);
		allSelectedCheckBox = (CheckBox) findViewById(R.id.all_select);
		allUnselectedCheckBox = (CheckBox) findViewById(R.id.all_unselect);
		allSelectedCheckBox.setOnCheckedChangeListener(this);
		allUnselectedCheckBox.setOnCheckedChangeListener(this);

		selectedListView = (ListView) findViewById(R.id.selected_list);
		unselectedListView = (ListView) findViewById(R.id.unselected_list);
		searchInput = (EditText) findViewById(R.id.address_search_input);
		searchInput.addTextChangedListener(this);
		searchCancel = findViewById(R.id.search_cancel);
		clearInput = findViewById(R.id.contact_search_clear_img);
		clearInput.setVisibility(View.GONE);
		searchCancel.setOnClickListener(this);
		searchInput.setOnClickListener(this);
		clearInput.setOnClickListener(this);

		selectedMemberAdapter = new MemberAdapter(selectedUsers, false);
		unselectedMemberAdapter = new MemberAdapter(unselectedUsers, false);
		selectedListView.setAdapter(selectedMemberAdapter);
		unselectedListView.setAdapter(unselectedMemberAdapter);
		selectedListView.setOnScrollListener(this);
		unselectedListView.setOnScrollListener(this);

		areaId = getIntent().getLongExtra("areaId", 0);

		IntentFilter filter = new IntentFilter(ServerLoader.ACTION_CONTACTS_UPDATED);
		registerReceiver(contactsChangedReceiver, filter);
		requestAreaUsers();
	}

	private BroadcastReceiver contactsChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			List<ContactInfo> localContacts = ContactManager.getContactManager(getApplicationContext())
					.loadAllLocalContacts(true);
			setAvatarForAreaUsers(localContacts, selectedUsers);
			List<AreaUser> unselectedAreaUser = convertToAreaUser(localContacts);
			unselectedAreaUser.removeAll(selectedUsers);
			unselectedUsers.addAll(unselectedAreaUser);
			ContactlistUtils.sortAreaUser(selectedUsers);
			ContactlistUtils.sortAreaUser(unselectedUsers);
			selectedMemberAdapter.notifyDataSetChanged();
			unselectedMemberAdapter.notifyDataSetChanged();
		}
	};


	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(contactsChangedReceiver);
	};

	@Override
	public void onRightButtonClick() {
		ApiHelper.setAttendanceAreaUsers(new ProgressDialogResponseListener<String>(this, "正在提交...") {

			@Override
			public void onSuccess(String payload) {
				Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_SHORT).show();
				finish();
			}
		}, areaId, selectedUsers);
	}

	private void requestAreaUsers() {
		ApiHelper.getAttendanceAreaUsers(new ProgressDialogResponseListener<ListSlice<AreaUser>>(this, "正在获取关联成员...") {

			@Override
			public void onSuccess(ListSlice<AreaUser> payload) {
				List<ContactInfo> localContacts = ContactManager.getContactManager(getApplicationContext())
						.loadAllLocalContacts(true);
				selectedUsers.addAll(payload.getContent());
				setAvatarForAreaUsers(localContacts, selectedUsers);
				List<AreaUser> unselectedAreaUser = convertToAreaUser(localContacts);
				unselectedAreaUser.removeAll(selectedUsers);
				unselectedUsers.addAll(unselectedAreaUser);
				ContactlistUtils.sortAreaUser(selectedUsers);
				ContactlistUtils.sortAreaUser(unselectedUsers);
				selectedMemberAdapter.notifyDataSetChanged();
				unselectedMemberAdapter.notifyDataSetChanged();
			}

		}, areaId);
	}

	private void setAvatarForAreaUsers(List<ContactInfo> localContacts, List<AreaUser> areaUsers) {
		for (AreaUser areaUser : areaUsers) {
			setAvatarForAreaUser(localContacts, areaUser);
		}
	}

	private void setAvatarForAreaUser(List<ContactInfo> localContacts, AreaUser areaUser) {
		for (ContactInfo contactInfo : localContacts) {
			if (contactInfo.getUserId() == areaUser.getUserId()) {
				areaUser.setAvatar(contactInfo.getAvatar());
				return;
			}
		}
	}

	private List<AreaUser> convertToAreaUser(List<ContactInfo> localContacts) {
		List<AreaUser> areaUsers = new ArrayList<AreaUser>();
		for (ContactInfo contactInfo : localContacts) {
			areaUsers.add(AreaUser.fromContactInfo(contactInfo));
		}
		return areaUsers;
	}

	private class MemberAdapter extends BaseAdapter {

		private List<AreaUser> data;
		private LayoutInflater inflater;
		private List<AreaUser> checkedAreaUsers = new ArrayList<AreaUser>();

		/**判断listview是否是正在滑动的标识符*/
		public boolean isScrolling;

		public MemberAdapter(List<AreaUser> data, boolean isScrolling) {
			this.data = data;
			inflater = LayoutInflater.from(getApplicationContext());
			this.isScrolling = isScrolling;
		}

		public void clearCheckedAreaUsers() {
			data.removeAll(checkedAreaUsers);
			checkedAreaUsers.clear();
		}

		public List<AreaUser> getCheckedAreaUsers() {
			return checkedAreaUsers;
		}

		public void setAllChecked(boolean isChecked) {
			if (isChecked) {
				checkedAreaUsers.clear();
				checkedAreaUsers.addAll(data);
			} else {
				if (checkedAreaUsers.equals(data)) {
					checkedAreaUsers.clear();
				}
			}
		}

		public List<AreaUser> getData() {
			return data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_attendance_member, parent, false);
			}
			TextView name = (TextView) convertView.findViewById(R.id.attendance_member_name);

			final ImageView icon = (ImageView) convertView.findViewById(R.id.attendance_member_icon);
			final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.attendance_member_checkbox);
			final AreaUser areaUser = (AreaUser) getItem(position);
			checkBox.setOnCheckedChangeListener(null);
			checkBox.setChecked(checkedAreaUsers.contains(areaUser));
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						checkedAreaUsers.add(areaUser);
					} else {
						checkedAreaUsers.remove(areaUser);
						uncheckAllSelectedState(data);
					}
				}
			});
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checkBox.toggle();
				}
			});
			name.setText(areaUser.getName());
			icon.setTag(areaUser.getAvatar());
			icon.setImageResource(R.drawable.contactlist_contact_icon_default);
			if (!isScrolling) {
//				LogUtils.e(TAG, "位置：" + areaUser.getName());
				MainApplication.getHeadImageLoader().get(areaUser.getAvatar(), new ImageLoaderListener() {

					@Override
					public void onSuccess(ImageContainer container) {
						if (TextUtils.equals(icon.getTag().toString(), container.getUrl())) {
							icon.setImageBitmap(container.getBitmap());
						}
					}

					@Override
					public void onError(ImageContainer container) {
						if (TextUtils.equals(icon.getTag().toString(), container.getUrl())) {
							icon.setImageResource(R.drawable.contactlist_contact_icon_default);
						}
					}
				});
			}
			return convertView;
		}
	}

	private void uncheckAllSelectedState(List<AreaUser> data) {
		if (data == selectedUsers) {
			allSelectedCheckBox.setChecked(false);
		} else {
			allUnselectedCheckBox.setChecked(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add:
			add();
			break;
		case R.id.remove:
			remove();
			break;
		case R.id.contact_search_clear_img:
			clearSearchInput();
			break;
		case R.id.search_cancel:
			searchCancel();
			break;
		case R.id.address_search_input:
			search();
			break;
		default:
			break;
		}
	}

	private void search() {
		searchInput.requestFocus();
		searchInput.setCursorVisible(true);
		searchCancel.setVisibility(View.VISIBLE);
		matchSearchInput("");
	}

	private void searchCancel() {
		searchInput.setText("");
		searchInput.clearFocus();
		searchInput.setCursorVisible(false);
		hideSoftInput();
		searchCancel.setVisibility(View.GONE);
		showDefaultList();
	}

	private void clearSearchInput() {
		searchInput.setText("");
	}

	private void remove() {
		List<AreaUser> checkedAreaUsers = selectedMemberAdapter.getCheckedAreaUsers();
		unselectedUsers.addAll(checkedAreaUsers);
		if (unselectedMemberAdapter.data != unselectedUsers) {
			unselectedMemberAdapter.data.addAll(checkedAreaUsers);
		}
		selectedUsers.removeAll(checkedAreaUsers);
		if (selectedMemberAdapter.data != selectedUsers) {
			selectedMemberAdapter.data.removeAll(checkedAreaUsers);
		}

		if (allSelectedCheckBox.isChecked()) {
			allSelectedCheckBox.setChecked(false);
		}
		selectedMemberAdapter.clearCheckedAreaUsers();
		ContactlistUtils.sortAreaUser(selectedMemberAdapter.getData());
		ContactlistUtils.sortAreaUser(unselectedMemberAdapter.getData());

		selectedMemberAdapter.notifyDataSetChanged();
		unselectedMemberAdapter.notifyDataSetChanged();
	}

	private void add() {
		List<AreaUser> checkedAreaUsers = unselectedMemberAdapter.getCheckedAreaUsers();
		selectedUsers.addAll(checkedAreaUsers);
		if (selectedUsers != selectedMemberAdapter.data) {
			selectedMemberAdapter.data.addAll(checkedAreaUsers);
		}

		unselectedUsers.removeAll(checkedAreaUsers);
		if (unselectedMemberAdapter.data != unselectedUsers) {
			unselectedMemberAdapter.data.removeAll(checkedAreaUsers);
		}

		unselectedMemberAdapter.clearCheckedAreaUsers();
		if (allUnselectedCheckBox.isChecked()) {
			allUnselectedCheckBox.setChecked(false);
		}
		ContactlistUtils.sortAreaUser(selectedMemberAdapter.getData());
		ContactlistUtils.sortAreaUser(unselectedMemberAdapter.getData());
		selectedMemberAdapter.notifyDataSetChanged();
		unselectedMemberAdapter.notifyDataSetChanged();

	}

	private void allSelected(boolean isChecked) {
		selectedMemberAdapter.setAllChecked(isChecked);
		selectedMemberAdapter.notifyDataSetChanged();
	}

	private void allUnselected(boolean isChecked) {
		unselectedMemberAdapter.setAllChecked(isChecked);
		unselectedMemberAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.all_select:
			allSelected(isChecked);
			break;
		case R.id.all_unselect:
			allUnselected(isChecked);
			break;
		default:
			break;
		}
	}

	public static void launch(Context context, long areaId) {
		Intent intent = new Intent(context, AttendanceMemberBindActivity.class);
		intent.putExtra("areaId", areaId);
		context.startActivity(intent);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() > 0) {
			clearInput.setVisibility(View.VISIBLE);
		} else {
			clearInput.setVisibility(View.GONE);
		}
		matchSearchInput(s.toString());
	}

	private void matchSearchInput(String input) {
		List<AreaUser> selectedMatched = findMatchAreaUserWithName(selectedUsers, input);
		List<AreaUser> unselectedMatched = findMatchAreaUserWithName(unselectedUsers, input);
		selectedMemberAdapter.data = selectedMatched;
		unselectedMemberAdapter.data = unselectedMatched;
		selectedMemberAdapter.notifyDataSetChanged();
		unselectedMemberAdapter.notifyDataSetChanged();
	}

	private void showDefaultList() {
		selectedMemberAdapter.data = selectedUsers;
		unselectedMemberAdapter.data = unselectedUsers;
		selectedMemberAdapter.notifyDataSetChanged();
		unselectedMemberAdapter.notifyDataSetChanged();
	}

	private boolean isMatchInput(String name) {
		String input = searchInput.getText().toString();
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(input)) {
			return false;
		}
		return name.startsWith(input);
	}

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    private List<AreaUser> findMatchAreaUserWithName(List<AreaUser> areaUsers, String name) {

		List<AreaUser> matchedAreaUsers = new ArrayList<AreaUser>();
		if (TextUtils.isEmpty(name)) {
			return matchedAreaUsers;
		}
		for (AreaUser areaUser : areaUsers) {
			if (areaUser.getName().contains(name) || isPinYinContains(areaUser.getName(), name)) {
				matchedAreaUsers.add(areaUser);
			}
		}
		return matchedAreaUsers;
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
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		MemberAdapter adapter = (MemberAdapter) view.getAdapter();
		switch (scrollState) {

		//空闲状态
		case OnScrollListener.SCROLL_STATE_IDLE:
			adapter.isScrolling = false;
			adapter.notifyDataSetChanged();
			break;

		//滑动状态
		case OnScrollListener.SCROLL_STATE_FLING:
			adapter.isScrolling = true;
			break;

		//触摸后滚动
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			adapter.isScrolling = true;
			break;

		default:
			break;
		}
	}

}
