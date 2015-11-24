package cn.yjt.oa.app.teleconference.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.beans.TCCreateConferenceResponse;
import cn.yjt.oa.app.beans.TCItem;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.contactlist.ContactlistActivity;
import cn.yjt.oa.app.teleconference.ManualAddActivity;
import cn.yjt.oa.app.teleconference.http.TCAsyncRequest;
import cn.yjt.oa.app.teleconference.http.TCAsyncRequest.CreateConferenceCallback;

public class TCHeldFragment extends TCBaseFragment implements 
OnClickListener,IUserView,OnItemClickListener,CreateConferenceCallback{
	private List<TCItem> items;
	private UserPresenter presenter;
	private PopupMenu popupMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		items = new ArrayList<TCItem>();
		presenter = new UserPresenter(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_tc_held, container,false);
		initView(root);
		return root;
	}
	
	private TextView selectedCount;
	private TextView chargesInstructions;
	private ListView listView;
	private TCHeldListViewAdapter adapter;
	private ImageButton btnAddUser;
	private Button btnStart;
	private void initView(View view){
		selectedCount = (TextView) view.findViewById(R.id.tv_selected_count);
		chargesInstructions = (TextView) view.findViewById(R.id.charges_instructions);
		chargesInstructions.setText(Html.fromHtml("<a href='http://ecp.189.cn/wap/ecpjs.html'> 查看资费标准 </a>"));
		chargesInstructions.setMovementMethod(LinkMovementMethod.getInstance());
		listView = (ListView) view.findViewById(R.id.tc_user_list);
		adapter = new TCHeldListViewAdapter();
		listView.setAdapter(adapter);
		btnAddUser = (ImageButton) view.findViewById(R.id.ibtn_add_user);
		btnAddUser.setOnClickListener(this);
		btnStart = (Button) view.findViewById(R.id.btn_start);
		btnStart.setOnClickListener(this);
	}
	
	class TCHeldListViewAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tc_held_item, parent, false);
			}
			TextView userName = (TextView) convertView.findViewById(R.id.tc_user_name);
			TextView userPhone = (TextView) convertView.findViewById(R.id.tc_user_phone);
			ImageButton deleteUser = (ImageButton) convertView.findViewById(R.id.tc_btn_delete);
			deleteUser.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					items.remove(getItem(position));
					refresh();
				}
			});
			userName.setText(items.get(position).getName());
			userPhone.setText("[ "+items.get(position).getPhone()+" ]");
			return convertView;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_add_user:
			addUser(v);
			break;
		case R.id.btn_start:
			start();
			break;
		default:
			break;
		}
	}
	private static String access_token;
	private static String mobile;
	private static String ecp_token;
	private void start(){
		if(access_token!=null&&mobile!=null&&ecp_token!=null&&getParticipants()!=null){
			Map<String,String> params = TCAsyncRequest.addCreateConferenceParameters(access_token, mobile, ecp_token, getParticipants());
			LogUtils.i(this, "--会议创建人："+mobile);
			LogUtils.i(this, "--会议参与人："+getParticipants());
			TCAsyncRequest.getCreateConference(params, this);
		}else{
			toast("会议创建失败");
		}
		
	}
	
	private void toast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}

	private void addUser(View view){
		popupMenu = new PopupMenu(getActivity());
		popupMenu.setOnItemClickListener(this);
		popupMenu.showAsDropDown(view);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if(popupMenu != null){
			popupMenu.dismiss();
		}
		TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Service.TELEPHONY_SERVICE);   
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	PhoneStateListener listener = new PhoneStateListener() {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// state 当前状态 incomingNumber,貌似没有去电的API
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				System.out.println("挂断");
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				System.out.println("接听");
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("响铃:来电号码" + incomingNumber);
				break;
			}
		}
	};
	
	private static final int REQUEST_CODE_SELECT_CONTACTS = 0;
	private static final int REQUEST_CODE_ADD_PHONE_NUMBER = 2;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if(requestCode == ContactlistActivity.REQUEST_CODE_CLICK){
				getContactsFromYjt(data);
			}
			if(requestCode == REQUEST_CODE_SELECT_CONTACTS){
				getContactsFromLocal(data);
			}
			if(requestCode == REQUEST_CODE_ADD_PHONE_NUMBER){
				getContactByManual(data);
			}
		}
	}
	private void getContactByManual(Intent data){
		String phonenumber = data.getStringExtra("phonenumber");
		addItem("匿名", phonenumber);
	}
	private void getContactsFromYjt(Intent data){
		List<UserSimpleInfo> list = data.getParcelableArrayListExtra(ContactlistActivity.CONTACTLIST_MULITCHOICE_RESULT);
		presenter.addUser(getActivity(), getItemIds(list));
	}
	
	private void getContactsFromLocal(Intent data){
		ContentResolver  resolver = getActivity().getContentResolver();
		Uri contact = data.getData();
		@SuppressWarnings("deprecation")
		Cursor cursor = getActivity().managedQuery(contact, null, null, null, null);
		cursor.moveToFirst();
		String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		Cursor phone = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
				null, 
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+contactId,
				null, 
				null);
		String usernumber = null;
		while(phone.moveToNext()){
			usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		}
		addItem(username,usernumber);
	}
	
	private void addItem(String username,String usernumber){
		TCItem item = new TCItem();
		item.setName(username);
		item.setPhone(usernumber);
		items.add(item);
		refresh();
	}
	
	private Long[] getItemIds(List<UserSimpleInfo> list){
		Long[] ids = new Long[list.size()];
		for(int i=0;i<ids.length;i++){
			ids[i] = list.get(i).getId();
		}
		return ids;
	}
	
	@Override
	public String getTitle() {
		return "电话会议";
	}
	
	private void refresh(){
		selectedCount.setText("已选择的与会人员（"+items.size()+"）");
		adapter.notifyDataSetChanged();
	}

	@Override
	public void setUser(TCItem item) {
		items.add(item);
		refresh();
	}

	@Override
	public void setUsers(List<TCItem> items) {
		this.items.addAll(items);
		refresh();
	}
	
	private void startActivityForContacts(){
		startActivityForResult(new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_SELECT_CONTACTS);
	}
	
	private void startActivityForPhoneNumber(){
		Intent intent = new Intent(getActivity(),ManualAddActivity.class);
		startActivityForResult(intent, REQUEST_CODE_ADD_PHONE_NUMBER);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		switch ((int)id) {
		case 0:
			startActivityForPhoneNumber();
			break;
		case 1:
			ContactlistActivity.startActivityForChoiceContact(getActivity(),ContactlistActivity.REQUEST_CODE_CLICK);
			break;
		case 2:
			startActivityForContacts();
			break;
		default:
			break;
		}
	}

	private String getParticipants(){
		StringBuilder builder = new StringBuilder(mobile);
		for(int i=0;i<items.size();i++){
			builder.append(",");
			builder.append(items.get(i).getPhone());
		}
		return builder.toString();
	}
	
	@Override
	public void setParams(String mobile, String ecp_token, String access_token) {
		System.out.println("---mobile--"+mobile+"---ecp_token---"+ecp_token+"---access_token--"+access_token);
		TCHeldFragment.mobile = mobile;
		TCHeldFragment.ecp_token = ecp_token;
		TCHeldFragment.access_token = access_token;
	}

	@Override
	public void onResult(TCCreateConferenceResponse response) {
		toast(response.getMsg());
	}
}
