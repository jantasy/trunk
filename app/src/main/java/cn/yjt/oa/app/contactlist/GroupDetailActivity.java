package cn.yjt.oa.app.contactlist;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.GroupInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.data.ContactlistGroupInfo;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.utils.BitmapUtils;

public class GroupDetailActivity extends TitleFragmentActivity implements
		OnClickListener {

	private EditText groupName, description;
	private ListView memberListView;
	private ContactlistGroupInfo info;
	private GroupMembersAdapter adapter;

	private static final int DESCRIPTION_LIMIT = 50;

	public static final String EXTRA_GROUP_ID = "group_id";
	private ContactManager mangager;

	public static final long DEFAULT_ID = -1;

	private boolean isGroupUpdateFinish = true;
	private boolean isCreateGroupFinish = true;

	private UserInfo currLogin;
	private List<UserSimpleInfo> groupContacts = new ArrayList<UserSimpleInfo>();
	private Bitmap defaultIcon;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		setSoftInputModes();
		super.onCreate(savedInstanceState);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setImageResource(R.drawable.contact_list_save);
		currLogin = AccountManager.getCurrent(GroupDetailActivity.this);
		setContentView(R.layout.contact_group_detail);
		initView();
		mangager = ContactManager.getContactManager(this);
		initInfo();
		setAdapter();
		defaultIcon = BitmapUtils.getPersonalHeaderIcon(
				GroupDetailActivity.this, getDefaultBitmap());
	}

	private void setSoftInputModes() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	private void initView() {
		groupName = (EditText) findViewById(R.id.group_name);
		description = (EditText) findViewById(R.id.group_description);
		memberListView = (ListView) findViewById(R.id.group_members_list);
		findViewById(R.id.group_add_new_member).setOnClickListener(this);
		String hint = getResources().getString(
				R.string.contactlist_group_detail_groupdescription_hint);
		description.setHint(String.format(hint, DESCRIPTION_LIMIT));
		description
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						DESCRIPTION_LIMIT) });
	}

	private void initInfo() {
		Intent intent = getIntent();
		long id = intent.getLongExtra(EXTRA_GROUP_ID, DEFAULT_ID);
		initGroupinfo(id);
	}

	private void initGroupinfo(final long id) {
		addCurrLogin();
		if (id == DEFAULT_ID) {
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				GroupInfo gInfo = mangager.getGroupInfo(id);
				info = new ContactlistGroupInfo(gInfo);
				List<ContactInfo> list = mangager.getGroupMembersInfo(gInfo
						.getUsers());
				ContactlistUtils.sortGroupChild(list, GroupDetailActivity.this);
				info.setUsers(list.toArray(new ContactInfo[list.size()]));
				runOnUiThread(new Runnable() {
					public void run() {
						setView();
					}
				});
			}
		}).start();
	}

	private void addCurrLogin() {
		UserSimpleInfo info = new UserSimpleInfo();
		info.setId(currLogin.getId());
		info.setName(currLogin.getName());
		info.setIcon(currLogin.getAvatar());
		groupContacts.add(info);
	}

	private void setView() {

		if (info == null) {
			return;
		}
		groupName.setText(info.getInfo().getName());
		description.setText(info.getInfo().getDescription());
		setListView();
	}

	private void setListView() {
		getGroupMembers();
		setAdapter();
	}

	private void setAdapter() {
		if (adapter == null) {
			adapter = new GroupMembersAdapter();
			memberListView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}

	}

	private void getGroupMembers() {
		for (ContactInfo cInfo : info.getUsers()) {
			UserSimpleInfo sInfo = new UserSimpleInfo();
			sInfo.setId(cInfo.getUserId());
			sInfo.setName(cInfo.getName());
			sInfo.setIcon(cInfo.getAvatar());
			if (!groupContacts.contains(sInfo)) {
				groupContacts.add(sInfo);
			}

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ContactlistActivity.REQUEST_CODE_CLICK
				&& resultCode == RESULT_OK) {
			List<UserSimpleInfo> list = data
					.getParcelableArrayListExtra(ContactlistActivity.CONTACTLIST_MULITCHOICE_RESULT);
			List<GroupInfo> groups = data
					.getParcelableArrayListExtra(ContactlistActivity.CONTACTLIST_MULITCHOICE_GROUP_RESULT);
			getResultInfos(list, groups);
		}
	}

	private void getResultInfos(List<UserSimpleInfo> list,
			List<GroupInfo> groups) {
		if (list != null) {
			groupContacts.addAll(list);
			groupContacts.addAll(getUserFromGroups(groups));
			setAdapter();
		}
	}

	private List<UserSimpleInfo> getUserFromGroups(List<GroupInfo> groups) {
		List<UserSimpleInfo> infos = new ArrayList<UserSimpleInfo>();
		for (GroupInfo group : groups) {
			if (group != null && group.getUsers() != null) {
				infos.addAll(Arrays.asList(group.getUsers()));
			}
		}
		Iterator<UserSimpleInfo> iterator = infos.iterator();
		while (iterator.hasNext()) {
			UserSimpleInfo next = iterator.next();
			if (groupContacts.contains(next)) {
				iterator.remove();
			}
		}
		return infos;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.group_add_new_member:
			ContactlistActivity.startActivityForChoiceContact(this,
					ContactlistActivity.REQUEST_CODE_CLICK, groupContacts);
			break;

		}

	}

	private void storeGroup() {
		GroupInfo info = getGroupInfo();
		if (info == null) {
			return;
		}
		showProgressBar();
		if (info.getId() != DEFAULT_ID) {
			updateGroup(info);
		} else {
			creatGroup(info);
		}

	}

	private void creatGroup(GroupInfo info) {
		if (!isCreateGroupFinish) {
			return;
		}
		isCreateGroupFinish = false;
		mangager.createGroup(info, new Listener<Response<GroupInfo>>() {

			@Override
			public void onErrorResponse(InvocationError error) {
				dismissProgressBar();
				toast(R.string.create_group_failed);
				oncreateGroupFinish(null);
			}

			@Override
			public void onResponse(Response<GroupInfo> response) {
				dismissProgressBar();
				if (response.getCode() == 0) {
					oncreateGroupFinish(response.getPayload());
				} else {
					Toast.makeText(getApplicationContext(),
							response.getDescription(), Toast.LENGTH_LONG)
							.show();
				}

			}
		});
	}

	private void toast(int string) {
		android.widget.Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

	private void oncreateGroupFinish(final GroupInfo info) {

		if (info == null) {
			toast(R.string.create_group_failed);
			isCreateGroupFinish = true;
			return;
		}
		toast(R.string.create_group_success);
		new Thread() {
			public void run() {
				mangager.addGroup(info);
				runOnUiThread(new Runnable() {
					public void run() {
						isCreateGroupFinish = true;
						setResult(RESULT_OK);
						GroupDetailActivity.this.finish();
					}
				});
			};
		}.start();

	}

	private void updateGroup(final GroupInfo info) {
		if (!isGroupUpdateFinish) {
			return;
		}
		isCreateGroupFinish = false;
		mangager.updateGroupInfo(info, new Listener<Response<Object>>() {

			@Override
			public void onResponse(Response<Object> response) {
				dismissProgressBar();
				if (response.getCode() == AsyncRequest.ERROR_CODE_OK) {
					toast(R.string.create_group_success);
					onGroupUpdateFinish(info);
				} else {
					toast(R.string.create_group_failed);
					isGroupUpdateFinish = true;
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				dismissProgressBar();
				toast(R.string.create_group_failed);
				isGroupUpdateFinish = true;
			}
		});
	}

	private void onGroupUpdateFinish(final GroupInfo info) {
		new Thread() {
			@Override
			public void run() {
				boolean isDeleteSelf = isDeleteSelf(info);
				if (isDeleteSelf) {
					mangager.deleteGroup(info.getId());
				} else {
					mangager.addGroup(info);
				}
				runOnUiThread(new Runnable() {
					public void run() {
						isGroupUpdateFinish = true;
						setResult(RESULT_OK);
						GroupDetailActivity.this.finish();
						sendBroadcast(new Intent(ContactsGroupFragment.ACTION_RELOAD_GROUPS));
					}
				});
			}
		}.start();
	}

	private boolean isDeleteSelf(GroupInfo info) {

		for (UserSimpleInfo simpleInfo : info.getUsers()) {
			if (simpleInfo.getId() == currLogin.getId()) {
				return false;
			}
		}
		return true;
	}

	private GroupInfo getGroupInfo() {
		String name = groupName.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, R.string.create_group_no_group_name,
					Toast.LENGTH_SHORT).show();
			return null;
		}
		GroupInfo gInfo = null;
		if (info != null) {
			gInfo = info.getInfo();
		}
		if (gInfo == null) {
			gInfo = new GroupInfo();
			gInfo.setId(DEFAULT_ID);
		}
		gInfo.setName(name);
		gInfo.setDescription(description.getText().toString());
		gInfo.setUsers(groupContacts.toArray(new UserSimpleInfo[groupContacts
				.size()]));
		return gInfo;
	}

	class GroupMembersAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return groupContacts.size();
		}

		@Override
		public Object getItem(int position) {
			return groupContacts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.group_detail_item, parent, false);
				Holder holder = new Holder();
				convertView.setTag(holder);
				holder.delete = (ImageView) convertView
						.findViewById(R.id.grouop_detail_item_delete);
				holder.itemName = (TextView) convertView
						.findViewById(R.id.grouop_detail_item_name);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.grouop_detail_item_icon);
			}
			final UserSimpleInfo info = groupContacts.get(position);
			final Holder holder = (Holder) convertView.getTag();
			holder.delete.setOnClickListener(l);
			holder.delete.setTag(info);
			holder.itemName.setText(info.getName());
//			if (info.getId() == AccountManager.getCurrent(
//					getApplicationContext()).getId()) {
//				holder.delete.setVisibility(View.GONE);
//			} else {
//				holder.delete.setVisibility(View.VISIBLE);
//			}
			holder.icon.setImageBitmap(defaultIcon);
			holder.icon.setTag(info.getIcon());
			MainApplication.getHeadImageLoader().get(info.getIcon(),
					new ImageLoaderListener() {

						@Override
						public void onSuccess(ImageContainer container) {
							if (holder.icon.getTag() != null
									&& holder.icon.getTag().equals(
											container.getUrl())) {
								holder.icon.setImageBitmap(container
										.getBitmap());
							}
						}

						@Override
						public void onError(ImageContainer container) {

						}
					});
			return convertView;
		}

		class Holder {
			TextView itemName;
			ImageView delete, icon;
		}

		OnClickListener l = new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserSimpleInfo info = (UserSimpleInfo) v.getTag();
				groupContacts.remove(info);
				adapter.notifyDataSetChanged();
			}
		};
	}

	private Bitmap getDefaultBitmap() {
		return BitmapFactory.decodeResource(getResources(),
				R.drawable.contactlist_contact_icon_default);
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onRightButtonClick() {
		storeGroup();

		/*记录操作 0610*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_ADD_GROUP);
	}

	public static void startForResult(Fragment fragment,int requestCode) {
		Intent intent = new Intent(fragment.getActivity(), GroupDetailActivity.class);
		fragment.startActivityForResult(intent, requestCode);
	}
}
