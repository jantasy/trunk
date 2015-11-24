package cn.yjt.oa.app.enterprise.contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.data.ContactlistContactInfo;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.contactlist.view.IndexView;
import cn.yjt.oa.app.contactlist.view.IndexView.OnIndexChoseListener;

public class MachineContactActivity extends TitleFragmentActivity implements
		OnClickListener {
	private EditText contactSearchInput;
	private TextView indexShow, emptyContact;
	private LinearLayout textClear;
	private ListView contactListView;
	private IndexView indexLayout;
	private MachineContactAdapter mContactAdapter;
	private ContentResolver resolver;
	private List<MachineContactInfo> members;

	private Set<String> allContactIndexs = new TreeSet<String>();
	private List<MachineContactInfo> machineContact = new ArrayList<MachineContactInfo>();
	private HashMap<String, Integer> allContactIndexPosInList = new HashMap<String, Integer>();
	private static final String INDEX_DEFAULT_REGISTER = "#";
	private static final String[] INDEXSTR = { INDEX_DEFAULT_REGISTER, "A",
			"B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
			"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	private static final String SORT_KEY="sort_key" ;
	
	public static final String MACHINE_CONTACT_LIST="MachineContactList";
	public static final String MEMBER_LIST="member_list";

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_machine_contact);
		initView();
	}

	private void initView() {
		members= getIntent().getParcelableArrayListExtra(MEMBER_LIST);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setImageResource(R.drawable.contact_list_save);
		mContactAdapter = new MachineContactAdapter(this);
		contactSearchInput = (EditText) findViewById(R.id.contact_search_input);
		contactSearchInput.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					contactSearchInput.setCursorVisible(true);
				}else{
					contactSearchInput.setCursorVisible(false);
				}
			}
		});
		contactSearchInput.setCursorVisible(false);
		contactSearchInput.setText("");
		textClear = (LinearLayout) findViewById(R.id.contact_search_clear);
		textClear.setOnClickListener(this);
		editextAddWatcher();

		indexShow = (TextView) findViewById(R.id.contact_index_show_text);
		indexLayout = (IndexView) findViewById(R.id.contact_index_layout);
		indexLayout.setIndexs(INDEXSTR, new OnIndexChoseListener() {

			@Override
			public void onIndexChose(int index, String indexStr) {
				allContactListIndexChose(indexStr);
			}
		});
		indexLayout.setIndexShowTextView(indexShow);
		emptyContact = (TextView) findViewById(R.id.emty_contact);
		contactListView = (ListView) findViewById(R.id.contact_listView);
		contactListView.setEmptyView(emptyContact);
		contactListView.setAdapter(mContactAdapter);

		getPhoneContacts();

	}

	private void editextAddWatcher() {
		contactSearchInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString().trim();
				if (!TextUtils.isEmpty(text)) {
					textClear.setVisibility(View.VISIBLE);
					 getSearchPhoneContacts(text);
				} else {
					textClear.setVisibility(View.GONE);
					getPhoneContacts();
				}
				
			}
		});
	}
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
	@Override
	public void onRightButtonClick() {
		List<MachineContactInfo> contactList=new ArrayList<MachineContactInfo>();
		List<MachineContactInfo> allContacts = mContactAdapter.getAllContacts();
		for (int i = 0; i < allContacts.size(); i++) {
			if(allContacts.get(i).getIsChecked()==1){
				contactList.add(allContacts.get(i));
			}
		}
		
		Intent data=new Intent();
		MachineContactInfo[] contact=new MachineContactInfo[contactList.size()];
		for (int i = 0; i < contactList.size(); i++) {
			contact[i]=contactList.get(i);
		}
		data.putExtra(MACHINE_CONTACT_LIST, contact);
		setResult(Activity.RESULT_OK, data);
		finish();
	}

	private void getPhoneContacts() {
		if(resolver==null){
			resolver = getContentResolver();
		}
		// 获取手机联系人
		try{
			
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);
		getContactFromCursor(phoneCursor);
		}catch(Throwable t){
			Toast.makeText(getApplicationContext(), "无法获取您的通讯录", Toast.LENGTH_SHORT).show();
		}

	}
	private void getSearchPhoneContacts(String name) {
		if(resolver==null){
			resolver = getContentResolver();
		}
		Cursor phoneCursor = null;
		if(PhoneUtils.isNum(name)){
			phoneCursor = resolver.query(Phone.CONTENT_URI,
					PHONES_PROJECTION,
					Phone.NUMBER + " like '%" + name + "%'", null, null);
		}else if(PhoneUtils.isLetter(name)){
			phoneCursor = resolver.query(Phone.CONTENT_URI,
					PHONES_PROJECTION,
					SORT_KEY + " like '%" + name + "%'", null, null);
		}else{
			phoneCursor = resolver.query(Phone.CONTENT_URI,
					PHONES_PROJECTION,
					Phone.DISPLAY_NAME + " like '%" + name + "%'", null, null);
		}
		
		getContactFromCursor(phoneCursor);
		
	}

	private void getContactFromCursor(Cursor phoneCursor) {
		if (phoneCursor != null) {
			List<MachineContactInfo> tempContact = new ArrayList<MachineContactInfo>();
			while (phoneCursor.moveToNext()) {
				MachineContactInfo info = new MachineContactInfo();
				// 得到手机号码
				
				String phoneNumber = phoneCursor.getString(phoneCursor
						.getColumnIndex(Phone.NUMBER));
				phoneNumber=PhoneUtils.formatPhoneNumber(phoneNumber);
		
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length()!=11 ||!PhoneUtils.isMobileNum(phoneNumber)) {
					continue;
				}
				// 得到联系人名称
				String displayName = phoneCursor.getString(phoneCursor
						.getColumnIndex(Phone.DISPLAY_NAME));
				
				info.setDisplayName(displayName);
				info.setNumber(phoneNumber);
				
				// 得到联系人ID
				Long contactid = phoneCursor.getLong(phoneCursor
						.getColumnIndex(Phone.CONTACT_ID));
				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(phoneCursor
						.getColumnIndex(Photo.PHOTO_ID));
				
			
				info.setViewType(MachineContactInfo.VIEW_TYPE_CONTACT);
				info.setIsChecked(0);
				info.setContactId(contactid);
				info.setPhotoid(photoid);
				info.setNamePinYin(displayName);
				if(!tempContact.contains(info)){
					tempContact.add(info);
				}
				
			}
			
			phoneCursor.close();
			
				
			tempContact.removeAll(members);
			getUseIndexs(tempContact);
			final List<MachineContactInfo> temp = getuseContacts(tempContact);
			sortAllContact(temp);
			getAllContactIndexPos(temp);
			
			machineContact.clear();
			machineContact.addAll(temp);
			mContactAdapter.setAllContacts(machineContact);
			mContactAdapter.notifyDataSetChanged();
			
		}
	}
	
	

	private void getUseIndexs(List<MachineContactInfo> list) {
		allContactIndexs.clear();
		for (MachineContactInfo info : list) {
			String firstLetter = ContactlistUtils.getFirstLetter(info
					.getDisplayName());
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
		}
	}

	private List<MachineContactInfo> getuseContacts(
			List<MachineContactInfo> list) {
		List<MachineContactInfo> tempAllContacts = new ArrayList<MachineContactInfo>();
		tempAllContacts.addAll(list);
		for (String index : allContactIndexs) {
			MachineContactInfo contactInfo = new MachineContactInfo();
			contactInfo.setContactId(MachineContactInfo.INFO_ID_DEFAULT);
			contactInfo.setDisplayName(index);
			contactInfo.setIsChecked(0);
			contactInfo.setViewType(MachineContactInfo.VIEW_TYPE_INDEX);
			contactInfo.setNamePinYin(index);
			contactInfo.setIndex(index);
			tempAllContacts.add(contactInfo);
		}
		return tempAllContacts;
	}

	private void sortAllContact(List<MachineContactInfo> list) {
		Collections.sort(list, new Comparator<MachineContactInfo>() {

			@Override
			public int compare(MachineContactInfo lhs, MachineContactInfo rhs) {
				int result = lhs.getIndex().compareTo(rhs.getIndex());
				if (result == 0) {
					result = lhs.getViewType() - rhs.getViewType();
					if (result == 0) {
						result = lhs.getNamePinYin().compareTo(
								rhs.getNamePinYin());
						if (result == 0) {
							String lhsName = lhs.getDisplayName();
							String rhsName = rhs.getDisplayName();
							if (TextUtils.isEmpty(lhsName)
									&& TextUtils.isEmpty(rhsName)) {
								result = 0;
							} else if (TextUtils.isEmpty(lhsName)
									&& !TextUtils.isEmpty(rhsName)) {
								result = -1;
							} else if (!TextUtils.isEmpty(lhsName)
									&& TextUtils.isEmpty(rhsName)) {
								result = 1;
							} else {
								result = lhsName.compareTo(rhsName);
							}

						}
					}
				}

				return result;

			}
		}

		);

	}

	private void getAllContactIndexPos(List<MachineContactInfo> list) {
		for (int i = 0; i < list.size(); i++) {
			MachineContactInfo info = list.get(i);
			if (info.getViewType() == ContactlistContactInfo.VIEW_TYPE_INDEX) {
				allContactIndexPosInList.put(info.getDisplayName(), i);
			}
		}
	}

	private void allContactListIndexChose(String index) {
		int listHeaderCount = contactListView.getHeaderViewsCount();
		if (allContactIndexPosInList.containsKey(index)) {
			int indexPos = allContactIndexPosInList.get(index);
			int scrollPos = listHeaderCount + indexPos;
			contactListView.setSelectionFromTop(scrollPos, 0);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.contact_search_clear:
			onSearchClearImgClick();
			break;

		default:
			break;
		}
	}

	private void onSearchClearImgClick() {
		contactSearchInput.setText("");
		getPhoneContacts();
	}

}
