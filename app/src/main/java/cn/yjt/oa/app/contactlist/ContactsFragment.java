package cn.yjt.oa.app.contactlist;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.BaseFragment;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.OnBackPressedInterface;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.InviteUserInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.server.UpdateCheckServerLoader;
import cn.yjt.oa.app.contactlist.view.CustomViewPager;
import cn.yjt.oa.app.enterprise.contact.MachineContactInfo;
import cn.yjt.oa.app.enterprise.contact.PhoneUtils;
import cn.yjt.oa.app.enterprise.operation.AddMemberActivity;
import cn.yjt.oa.app.enterprise.operation.SelectPopupMenu;
import cn.yjt.oa.app.http.AsyncRequest;

public class ContactsFragment extends BaseFragment implements OnClickListener,
		OnBackPressedInterface {

	public static final int PAGER_ALL = 0;
	public static final int PAGER_GROUPS = 1;
	public static final int PAGER_STRUCT = 2;

	private static final int REQUEST_CODE_GROUPS = 1;
	private static final int REQUEST_CODE_SELECT_CONTACTS = 2;
	private static final int REQUEST_CODE_INPUT_MANUAL = 3;

	private View root;
	private TextView contactAll;
	private TextView contactGroups;
	private TextView contactStruct;
	private CustomViewPager pager;
	private List<Fragment> fragments = new ArrayList<Fragment>();

	@Override
	public CharSequence getPageTitle(Context context) {
		return context.getResources().getString(R.string.contact);
	}

	@Override
	public boolean onRightButtonClick() {
		int current = pager.getCurrentItem();
		if (current == PAGER_GROUPS) {
			GroupDetailActivity.startForResult(this, REQUEST_CODE_GROUPS);

			return true;
		} else if (current == PAGER_ALL) {
			showMenu();
			return true;
		}
		return false;
	}

	private void showMenu() {
		SelectPopupMenu menu = new SelectPopupMenu(getActivity());
		menu.setTitle("邀请成员加入");
		menu.setOnItemClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_select_local:
					startActivityForContacts();
					break;
				case R.id.btn_select_manual:
					startActivityForManual();
					break;
				}
			}
		});
		menu.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				if (getActivity() instanceof ContactlistActivity
						|| getActivity() instanceof MainActivity) {
					((TitleFragmentActivity) getActivity()).lightOn();
				}

			}
		});
		menu.show();
		if (getActivity() instanceof ContactlistActivity
				|| getActivity() instanceof MainActivity) {
			((TitleFragmentActivity) getActivity()).lightOff();
		}
	}
	
	@Override
	public void onFragmentSelected() {
		new UpdateCheckServerLoader().load();
	}

	private void startActivityForContacts() {
		Intent intent = new Intent(getActivity(), InviteContactActivity.class);
		startActivityForResult(intent, REQUEST_CODE_SELECT_CONTACTS);
	}

	private void startActivityForManual() {
		Intent intent = new Intent(getActivity(), AddMemberActivity.class);
		startActivityForResult(intent, REQUEST_CODE_INPUT_MANUAL);
	}

	private void getContactsFromLocal(Intent data) {
		MachineContactInfo info = data
				.getParcelableExtra(InviteContactActivity.INVITE_CONTACT);
		setInviteUser(info.getDisplayName(), info.getNumber());
	}

	private void getContactsWithManual(Intent data) {
		String name = data.getStringExtra(AddMemberActivity.EXTRA_MEMBER_NAME);
		String phone = data
				.getStringExtra(AddMemberActivity.EXTRA_MEMBER_PHONE);
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
				}

				@Override
				public void onResponse(Response<InviteUserInfo> response) {
					if (response.getCode() == 0) {
						Toast.makeText(
								getActivity(),
								String.format(
										getResources().getString(
												R.string.invite_user),
										member.getName()), Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(getActivity(), response.getDescription(),
								Toast.LENGTH_SHORT).show();
					}

				}
			});
			builder.build().post();
		} else {
			Toast.makeText(getActivity(),
					member.getName() + "添加失败,号码格式错误或者号码已存在", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void configRightButton(ImageView imgView) {
		int currentItem = 0;
		if(pager != null){
			currentItem = pager.getCurrentItem();
		}
		switch (currentItem) {
		case 0:
		case 1:
			imgView.setVisibility(View.VISIBLE);
			imgView.setImageResource(R.drawable.contact_add_group);
			break;
		case 2:
		default:
			imgView.setVisibility(View.GONE);
			break;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ContactsAllFragment contactsAllFragment = new ContactsAllFragment();
		ContactsGroupFragment contactsGroupFragment = new ContactsGroupFragment();
		ContactsStructFragment contactsStructFragmen = new ContactsStructFragment();
		fragments.add(contactsAllFragment);
		fragments.add(contactsGroupFragment);
		fragments.add(contactsStructFragmen);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (getActivity() instanceof MainActivity) {
			MainActivity activity = (MainActivity) getActivity();
			activity.addToFragments(2, this);
		}
		System.out.println("--c onCreateView");
		if (root == null) {
			root = inflater.inflate(R.layout.fragment_contacts, container,
					false);

			contactAll = (TextView) root.findViewById(R.id.contact_all);
			contactGroups = (TextView) root.findViewById(R.id.contact_groups);
			contactStruct = (TextView) root.findViewById(R.id.contact_struct);
			pager = (CustomViewPager) root.findViewById(R.id.contact_viewpager);

			contactAll.setOnClickListener(this);
			contactGroups.setOnClickListener(this);
			contactStruct.setOnClickListener(this);

			pager.setAdapter(new ContactsPagerAdapter(getFragmentManager()));
			pager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int i) {
					setTitleTabSelect(i);
					configRightButton(((TitleFragmentActivity) getActivity()).getRightButton());
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
			pager.setScrollable(true);
			select(PAGER_ALL);
		}
		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		System.out.println("--c onDestroyView");
		ViewGroup parent = (ViewGroup) root.getParent();
		if (parent != null) {
			parent.removeView(root);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_GROUPS:
				getActivity().sendBroadcast(new Intent(ContactsGroupFragment.ACTION_REFRESH_GROUPS));
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.contact_all:
			select(PAGER_ALL);

			 /*记录操作 0602*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_ALL);
			break;
		case R.id.contact_groups:
			select(PAGER_GROUPS);

			 /*记录操作 0603*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_GROUP);
			break;
		case R.id.contact_struct:
			select(PAGER_STRUCT);

			 /*记录操作 0604*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_STRUCT);
			break;

		default:
			break;
		}
	}

	private void select(int i) {
		pager.setCurrentItem(i);
		setTitleTabSelect(i);
	}

	private void setTitleTabSelect(int i) {
		boolean isContactAll = false;
		boolean isContactGrooup = false;
		boolean isContactStruct = false;
		switch (i) {
		case PAGER_ALL:
			isContactAll = true;
			break;
		case PAGER_GROUPS:
			isContactGrooup = true;
			break;
		case PAGER_STRUCT:
			isContactStruct = true;
			break;
		default:
			break;
		}
		contactAll.setSelected(isContactAll);
		contactGroups.setSelected(isContactGrooup);
		contactStruct.setSelected(isContactStruct);
	}

	private class ContactsPagerAdapter extends FragmentPagerAdapter {

		public ContactsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			return fragments.get(i);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}
}
