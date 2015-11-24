package cn.yjt.oa.app.teleconferencenew;

import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.utils.ToastUtils;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.utils.MD5Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.GroupInfo;
import cn.yjt.oa.app.beans.TeleconferenceInfo;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.ContactlistActivity;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.contactlist.db.GlobalContactManager.GlobalContact;
import cn.yjt.oa.app.enterprise.contact.MachineContactActivity;
import cn.yjt.oa.app.enterprise.contact.MachineContactInfo;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.teleconference.ManualAddActivity;
import cn.yjt.oa.app.utils.BitmapUtils;
import cn.yjt.oa.app.utils.ViewUtil;

public class TeleconferenceActivity extends TitleFragmentActivity implements OnClickListener {
	private static final String tag = "TeleconferenceActivity";
	private List<TeleconferenceInfo> mItems;
	private LinearLayout mLLYjt, mLLPhone, mLLInput;
	private ListView mList;
	private Button mButtonStart;
	private ProgressWheel progressWheelLinear;
	private TextView mTVtime;
	
	private static final int REQUEST_CODE_SELECT_CONTACTS = 0;
	private static final int REQUEST_CODE_INPUT_MANUAL = 2;
	private List<MachineContactInfo> memberContact;
	
	private ListViewAdapter adapter;
	private ContentResolver resolver;
	private Handler mHandler;
	
    private final int MENU_ITEM_ID_CALL_LIST = 3;
    private final int MENU_ITEM_ID_REWARD_LIST = 4;
    private final int MENU_ITEM_ID_MEETING_CONTROL = 5;
    
    private static final int REQUEST_EDIT_MEETING = 100;    
    
    private UserInfo mCurrent;
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		if(ViewUtil.noLoginToLaunch(this)){
			LaunchActivity.launch(this);
			finish();
		}else{
			setContentView(R.layout.activity_teleconference);
			getLeftbutton().setImageResource(R.drawable.navigation_back);
			getRightButton().setImageResource(R.drawable.navigation_menu);
			setTitle("多方通话");
			mLLYjt = (LinearLayout) findViewById(R.id.ll_yjt_contacts);
			mLLPhone = (LinearLayout) findViewById(R.id.ll_phone_contacts);
			mLLInput = (LinearLayout) findViewById(R.id.ll_manul_input);
			mList = (ListView) findViewById(R.id.list_member);
			mButtonStart = (Button) findViewById(R.id.button_start);
			progressWheelLinear = (ProgressWheel) findViewById(R.id.progresswheel);
			mTVtime = (TextView) findViewById(R.id.tv_remain_time);
			
			mLLYjt.setOnClickListener(this);
			mLLPhone.setOnClickListener(this);
			mLLInput.setOnClickListener(this);
			mButtonStart.setOnClickListener(this);
			
			resolver = getContentResolver();
			
			mItems = new ArrayList<TeleconferenceInfo>();
			mCurrent = AccountManager.getCurrent(this);
			Log.d(tag, "current UserInfo icon: " + mCurrent.getAvatar() + "name:" + mCurrent.getName()
					+ "number:" + mCurrent.getPhone() + "custId:" + mCurrent.getCustId());
			TeleconferenceInfo item = new TeleconferenceInfo();

			item.setPhotoId(0);
			item.setName(mCurrent.getName());
			item.setPhone(mCurrent.getPhone());
			item.setIcon(mCurrent.getAvatar());
			mItems.add(item);
			adapter = new ListViewAdapter();
			mList.setAdapter(adapter);
			
//			progressWheelLinear.setProgress(0.5f);
			initView();
			
			mHandler = new Handler();
			
		}
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Resources res = getResources();
        menu.add(0, MENU_ITEM_ID_CALL_LIST, 1, "通话明细");
        menu.add(0, MENU_ITEM_ID_REWARD_LIST, 2, "奖励规则");
        menu.add(0, MENU_ITEM_ID_MEETING_CONTROL, 3, "会控说明");
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {        
        switch (item.getItemId()) {
        case MENU_ITEM_ID_CALL_LIST:
        	Intent intent1 = new Intent();
        	intent1.setClass(this, CallListActivity.class);
        	startActivityForResult(intent1, REQUEST_EDIT_MEETING);
//        	startActivity(intent1);
            break;
        case MENU_ITEM_ID_REWARD_LIST:
        	Intent intent2 = new Intent();
        	intent2.setClass(this, WebViewActivity.class);
        	intent2.putExtra(Constants.WEBVIEW_TYPE, Constants.WEBVIEW_TYPE_REWARD);
        	startActivity(intent2);
            break;
        case MENU_ITEM_ID_MEETING_CONTROL:
        	Intent intent3 = new Intent();
        	intent3.setClass(this, WebViewActivity.class);
        	intent3.putExtra(Constants.WEBVIEW_TYPE, Constants.WEBVIEW_TYPE_CONTROL);
        	startActivity(intent3);
            break;
        default:
            break;
        }
        
        return true;
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_yjt_contacts:
			ContactlistActivity.startActivityForChoiceContact(this, ContactlistActivity.REQUEST_CODE_CLICK);
			break;
		case R.id.ll_phone_contacts:
//			startActivityForContacts();
        	Intent intent2 = new Intent();
        	intent2.setClass(this, WebViewActivity.class);
        	intent2.putExtra(Constants.WEBVIEW_TYPE, Constants.WEBVIEW_TYPE_REWARD);
        	startActivity(intent2);
			break;
		case R.id.ll_manul_input:
			startActivityForManual();
			break;
		case R.id.button_start:
			if(mItems.size() > Constants.JOIN_NUMBER_MAX) {
				Toast.makeText(TeleconferenceActivity.this, "参会方人数最多为" + Constants.JOIN_NUMBER_MAX, Toast.LENGTH_SHORT).show();
			} else {
				startMeeting();	
			}			
			break;
		default:
			break;
		}	
	}
	
	private void initView() {
		new Thread() {
			@Override
            public void run() {
                super.run();
    			String date = String.valueOf(System.currentTimeMillis());
//    			String date = "20150506110523423";
    			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
    			params.add(new BasicNameValuePair("command", "auth_check"));
    			params.add(new BasicNameValuePair("admintel", mCurrent.getPhone()));    			
    			params.add(new BasicNameValuePair("userid", getCustId()));
    			//test
//    			params.add(new BasicNameValuePair("userid", "01000000054002"));
    			params.add(new BasicNameValuePair("date", date));
    	    	DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
//    	    	HttpPost httpPost = new HttpPost("http://118.85.207.187:9080/hytconfgw/servlet/HttpServer");  
    	    	HttpPost httpPost = new HttpPost(Constants.MEETING_URL + "auth_check");  
    	    	
    	        try {
    	            defaultHttpClient.getParams().setParameter("http.connection.timeout", Integer.valueOf(10000));
    	            defaultHttpClient.getParams().setParameter("http.socket.timeout", Integer.valueOf(10000));
    	            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
    	            HttpResponse execute = defaultHttpClient.execute(httpPost);
    	            Log.d(tag, "网络返回 状态码为200 :" + execute.getStatusLine().getStatusCode());
    	            final String result = execute.getStatusLine().getStatusCode() == 200 ? new String(EntityUtils.toByteArray(execute.getEntity()), "utf-8") : "netwrong";
    	            Log.d(tag, "result:" + result);
                    mHandler.post(new Runnable() {
                        public void run() {
                        	//ui
                        	try {
								JSONObject jOResult = new JSONObject(result);
								if(TextUtils.equals(jOResult.getString("result"), "YES")) {
									String reason = jOResult.getString("reason");
									if(TextUtils.isEmpty(reason)) {
										//有权限创建电话会议
										Constants.JOIN_NUMBER_MAX = jOResult.getInt("joinnum");
										double totalTime = jOResult.getDouble("sumtime");
										long remainTime = jOResult.getLong("conftime");
										progressWheelLinear.setProgress((float) (remainTime/totalTime));
										mTVtime.setText(String.valueOf(remainTime));
									} else {
										//无权限创建电话会议
										String reasonDetail;
										if(TextUtils.equals(reason, "1")) {
											reasonDetail = "不属于电信号码段";
										} else if(TextUtils.equals(reason, "2")) {
											reasonDetail = "企业ID为空";
										}else if(TextUtils.equals(reason, "3")) {
											reasonDetail = "企业尚未开通电话会议权限";
										}else {
											reasonDetail = "其他情况";
										}
										Toast.makeText(TeleconferenceActivity.this, "失败 (" + reasonDetail + ")", Toast.LENGTH_SHORT).show();
										finish();										
									}
								} else {
									Toast.makeText(TeleconferenceActivity.this, "不属于电信号码段", Toast.LENGTH_SHORT).show();
									finish();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                        	
                        }
                    });

    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }            
            }

		}.start();
	}
	
	private void startMeeting() {
		new Thread() {
			@Override
            public void run() {
                super.run();
    			String jointel = "";
    			for(int i = 1; i < mItems.size() - 1; i++) {
    				TeleconferenceInfo item = mItems.get(i);
    				jointel = jointel + item.getPhone() + "|";
 
    			}
    				jointel += mItems.get(mItems.size() - 1).getPhone();
    			String data = String.valueOf(System.currentTimeMillis());
//    			String data = "20150506110523423";
    			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
    			params.add(new BasicNameValuePair("command", "Command_SUNTEK_CreateConference"));
    			params.add(new BasicNameValuePair("admintel", mItems.get(0).getPhone()));
    			params.add(new BasicNameValuePair("confid", "0"));
    			params.add(new BasicNameValuePair("userid", getCustId()));
    			//test
//    			params.add(new BasicNameValuePair("userid", "01000000054002"));
    			params.add(new BasicNameValuePair("endtype", "1"));
    			params.add(new BasicNameValuePair("jointype", "1"));
    			params.add(new BasicNameValuePair("isrecord", "0"));
    			params.add(new BasicNameValuePair("jointel", jointel));
    			params.add(new BasicNameValuePair("url", "0"));
    			params.add(new BasicNameValuePair("date", data));
//    			String authenticatorsource = MD5Utils.md5("000000000" + "!Eq\\[k(E#0b*SJOG" + data);
//    			params.add(new BasicNameValuePair("authenticatorsource", authenticatorsource));
    			params.add(new BasicNameValuePair("authenticatorsource", "0"));
    			params.add(new BasicNameValuePair("sender", "0"));		
//    			params.add(new BasicNameValuePair("displayadmintel", mItems.get(0).getPhone()));
    			Log.d(tag, "params jointel: " + jointel + "date:" + data);
    	    	DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
    	    	HttpPost httpPost = new HttpPost(Constants.MEETING_URL + "create_teleconference");        
    	        try {
    	            defaultHttpClient.getParams().setParameter("http.connection.timeout", Integer.valueOf(10000));
    	            defaultHttpClient.getParams().setParameter("http.socket.timeout", Integer.valueOf(10000));
    	            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
    	            HttpResponse execute = defaultHttpClient.execute(httpPost);
    	            Log.d(tag, "网络返回 状态码为200 :" + execute.getStatusLine().getStatusCode());
    	            final String result = execute.getStatusLine().getStatusCode() == 200 ? new String(EntityUtils.toByteArray(execute.getEntity()), "utf-8") : "netwrong";
    	            Log.d(tag, "result:" + result);
                    mHandler.post(new Runnable() {
                        public void run() {
                           	try {
    								JSONObject jOResult = new JSONObject(result);
    								if(TextUtils.equals(jOResult.getString("result"), "success")) {
    	            	            	Toast.makeText(TeleconferenceActivity.this, "发起会议成功,请稍等...", Toast.LENGTH_SHORT).show();
    	            	            	mButtonStart.setText("发起会议中...");
    	            	            	mButtonStart.setBackgroundResource(R.drawable.call_bg_disable);            	            	
    	            	            	mButtonStart.setClickable(false);
    								} else if(TextUtils.equals(jOResult.getString("result"), "failed")) {
    									String reason = jOResult.getString("reason");
										String reasonDetail;
										if(TextUtils.equals(reason, "1")) {
											reasonDetail = "无人接";
										} else if(TextUtils.equals(reason, "2")) {
											reasonDetail = "被叫市话忙";
										}else if(TextUtils.equals(reason, "3")) {
											reasonDetail = "被叫长话忙";
										}else if(TextUtils.equals(reason, "4")) {
											reasonDetail = "拥塞";
										}else if(TextUtils.equals(reason, "5")) {
											reasonDetail = "空号";
										}else if(TextUtils.equals(reason, "6")) {
											reasonDetail = "被叫电话在呼入黑名单中";
										}else if(TextUtils.equals(reason, "7")) {
											reasonDetail = "被叫电话在呼出黑名单中";
										}else if(TextUtils.equals(reason, "8")) {
											reasonDetail = "用户接听未按正确的确认键";
										}else if(TextUtils.equals(reason, "9")) {
											reasonDetail = "被叫电话不在白名单中";
										}else if(TextUtils.equals(reason, "11")) {
											reasonDetail = "会议资源不足";
										}else if(TextUtils.equals(reason, "12")) {
											reasonDetail = "该企业会议时间不足";
										}else if(TextUtils.equals(reason, "13")) {
											reasonDetail = "没有可用的会议号";
										}else {
											reasonDetail = "其他情况";
										}
    	            	            	Toast.makeText(TeleconferenceActivity.this, "发起会议失败 (" + reasonDetail + ")", Toast.LENGTH_SHORT).show();
    	            	            	mButtonStart.setText("发起会议失败,请重试!");
    	            	            	mButtonStart.setClickable(true);
    								}
    					
    							} catch (JSONException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							}
                        }
                    });

    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }            
            }

		}.start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case ContactlistActivity.REQUEST_CODE_CLICK:
			List<UserSimpleInfo> infoList = data
					.getParcelableArrayListExtra(ContactlistActivity.CONTACTLIST_MULITCHOICE_RESULT);
			List<GroupInfo> groupInfos = data
					.getParcelableArrayListExtra(ContactlistActivity.CONTACTLIST_MULITCHOICE_GROUP_RESULT);

			if ((infoList == null || infoList.size() == 0)
					&& (groupInfos == null || groupInfos.isEmpty()))
				break;
			
			ContactManager manager = ContactManager.getContactManager(this);
			for (UserSimpleInfo info : infoList) {
				Log.d(tag, "info: " + info.toString());
				UserInfo userInfo = manager.getContactInfoById(info.getId());
				if(userInfo != null) {
					if(!isDuplicateNumber(userInfo.getPhone())) {
						TeleconferenceInfo item = new TeleconferenceInfo();
						item.setPhotoId(0);
						item.setName(info.getName());
						item.setPhone(userInfo.getPhone());
						item.setIcon(info.getIcon());
						mItems.add(item);
					}
				}
			}

			for (GroupInfo groupInfo : groupInfos) {
				Log.d(tag, "groupInfo: " + groupInfo.toString());
				UserSimpleInfo[] infos = groupInfo.getUsers();
				for (UserSimpleInfo info : infos) {
					Log.d(tag, "group info: " + info.toString());
					UserInfo userInfo = manager.getContactInfoById(info.getId());
					if(userInfo != null) {
						if(!isDuplicateNumber(userInfo.getPhone())) {
							TeleconferenceInfo item = new TeleconferenceInfo();
							item.setPhotoId(0);
							item.setName(info.getName());
							item.setPhone(userInfo.getPhone());
							item.setIcon(info.getIcon());
							mItems.add(item);		
						}
					}
				}
			}
			break;
		case REQUEST_CODE_SELECT_CONTACTS:
			getContactsFromLocal(data);
			break;
		case REQUEST_CODE_INPUT_MANUAL:
			getContactByManual(data);
			break;
		case REQUEST_EDIT_MEETING:
			String adminTel = data.getStringExtra(Intent.EXTRA_SHORTCUT_INTENT);
			ArrayList<String> joinList = data.getStringArrayListExtra(Intent.EXTRA_INTENT);			
			mItems.clear();
			
			ContactManager manager1 = ContactManager.getContactManager(this);
			GlobalContact globalContact = manager1.getContactByPhone(adminTel);
			TeleconferenceInfo item = new TeleconferenceInfo();
			item.setPhotoId(0);
			item.setIcon("");
			if(globalContact != null) {
				item.setName(globalContact.name);
			} else {
				item.setName("");
			}
			
			item.setPhone(adminTel);
			mItems.add(item);
			for(int i = 0; i < joinList.size(); i++) {
				GlobalContact globalContact1 = manager1.getContactByPhone(joinList.get(i));
				TeleconferenceInfo item1 = new TeleconferenceInfo();
				item1.setPhotoId(0);
				item1.setIcon("");
				if(globalContact1 != null) {
					item1.setName(globalContact1.name);
				} else {
					item1.setName("");
				}
				item1.setPhone(joinList.get(i));
				mItems.add(item1);
			}
			break;
		default:
			break;
		}
		
		refresh();
	}
	
	private void startActivityForContacts() {
		Intent intent = new Intent(this, MachineContactActivity.class);
		memberContact = new ArrayList<MachineContactInfo>();
		for (int i = 0; i < mItems.size(); i++) {
			MachineContactInfo info = new MachineContactInfo();
			info.setDisplayName(mItems.get(i).getName());
			info.setNumber(mItems.get(i).getPhone());
			memberContact.add(info);
		}

		intent.putParcelableArrayListExtra(MachineContactActivity.MEMBER_LIST, (ArrayList<? extends Parcelable>) memberContact);
		startActivityForResult(intent, REQUEST_CODE_SELECT_CONTACTS);
	}
	
	private void startActivityForManual(){
		Intent intent = new Intent(this,ManualAddActivity.class);
		startActivityForResult(intent, REQUEST_CODE_INPUT_MANUAL);
	}
	
	private void getContactsFromLocal(Intent data) {
		Parcelable[] contactList = data.getParcelableArrayExtra(MachineContactActivity.MACHINE_CONTACT_LIST);
		if(contactList!=null && contactList.length>0){
			for (int i = 0; i < contactList.length; i++) {
				MachineContactInfo info = (MachineContactInfo)contactList[i];
				Log.d(tag, "photoId: " + info.getPhotoid() + "name: " + info.getDisplayName() + "number: " + info.getNumber());
				if(!isDuplicateNumber(info.getNumber())) {
					TeleconferenceInfo item = new TeleconferenceInfo();
					item.setPhotoId(info.getPhotoid());
					item.setName(info.getDisplayName());
					item.setPhone(info.getNumber());
					item.setIcon("");
					mItems.add(item);
				}
			}			
		}	
	}
	
	private void getContactByManual(Intent data){
		String phonenumber = data.getStringExtra("phonenumber");
		Log.d(tag, "phonenumber: " + phonenumber);
		if(!isDuplicateNumber(phonenumber)) {
			TeleconferenceInfo item = new TeleconferenceInfo();
			item.setPhotoId(0);
			item.setIcon("");
			item.setName("");
			item.setPhone(phonenumber);
			mItems.add(item);
		}
	}
	
	class ListViewAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = LayoutInflater.from(TeleconferenceActivity.this).inflate(R.layout.activity_teleconference_item, parent, false);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
			TextView userName = (TextView) convertView.findViewById(R.id.tv_user_name);
			TextView userPhone = (TextView) convertView.findViewById(R.id.tv_user_phone);
			TextView deleteUser = (TextView) convertView.findViewById(R.id.tv_delete);
			if(position == 0) {
				deleteUser.setText("主持人");
			}
			deleteUser.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(position == 0)
						return;
					mItems.remove(getItem(position));
					refresh();
				}
			});
			if(!TextUtils.isEmpty(mItems.get(position).getIcon())) {
				loadImage(mItems.get(position).getIcon(), icon, R.drawable.contactlist_contact_icon_default);
			} else if(mItems.get(position).getPhotoId() > 0){
				loadImage(mItems.get(position).getPhotoId(), icon, R.drawable.contactlist_contact_icon_default);
			} else {
				icon.setImageResource(R.drawable.contactlist_contact_icon_default);
			}
			
			if(TextUtils.isEmpty(mItems.get(position).getName())) {
				userName.setVisibility(View.GONE);
			} else {
				userName.setText(mItems.get(position).getName());	
			}
			
			userPhone.setText(mItems.get(position).getPhone());
			return convertView;
		}
	}
	
	private void refresh(){
		if(mItems.size() > Constants.JOIN_NUMBER_MAX) {
			Toast.makeText(TeleconferenceActivity.this, "参会方人数最多为" + Constants.JOIN_NUMBER_MAX, Toast.LENGTH_SHORT).show();
		}
		mButtonStart.setText("发起会议("+mItems.size()+")");
		adapter.notifyDataSetChanged();
	}
	
	private boolean isDuplicateNumber(String number) {
		for(int i = 0; i < mItems.size(); i++) {
			if(TextUtils.equals(number, mItems.get(i).getPhone())) {
				Toast.makeText(TeleconferenceActivity.this, "您已添加过(" + number + "),不再为您重复添加.", Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		return false;
	}
	
	private void loadImage(long photo_id, ImageView imageView, final int dfResId) {
		imageView.setImageResource(dfResId);
		String selection = ContactsContract.Data._ID + " = " + photo_id;

		String[] projection = new String[] { ContactsContract.Data.DATA15 };
		Cursor cur = resolver.query(ContactsContract.Data.CONTENT_URI,
				projection, selection, null, null);
		cur.moveToFirst();
		byte[] contactIcon = cur.getBlob(0);
		if (contactIcon != null) {
			Bitmap contactPhoto = BitmapFactory.decodeByteArray(contactIcon, 0,
					contactIcon.length);
			getHeaderIcon(imageView, contactPhoto);
		} else {
			getHeaderIcon(imageView, getDefaultBitmap(dfResId));
		}

	}
	
	private void loadImage(String url, final ImageView imageView,
			final int dfResId) {
		// setDefaultImage(imageView, dfResId);
		imageView.setImageResource(dfResId);
		if (TextUtils.isEmpty(url)) {
			return;
		}
		final String tag = MD5Utils.md5(url);
		imageView.setTag(tag);
		AsyncRequest.getBitmap(url, new Listener<Bitmap>() {

			@Override
			public void onErrorResponse(InvocationError invocationerror) {
				onBitmapLoadFinish(imageView, getDefaultBitmap(dfResId), tag);
			}

			@Override
			public void onResponse(Bitmap bitmap) {
				if (bitmap != null) {
					onBitmapLoadFinish(imageView, bitmap, tag);
				} else {
					onBitmapLoadFinish(imageView, getDefaultBitmap(dfResId),
							tag);
				}
			}
		});
	}

	private void onBitmapLoadFinish(ImageView imageView, Bitmap bmp, String tag) {
		if (bmp != null) {
			String imageTag = (String) imageView.getTag();
			if (!tag.equals(imageTag)) {
				return;
			}
			bmp = BitmapUtils.getPersonalHeaderIcon(this, bmp);
			imageView.setImageBitmap(bmp);
		}

	}


	private Bitmap getDefaultBitmap(int resId) {
		Resources res = getResources();
		if (res == null) {
			return null;
		}
		return BitmapFactory.decodeResource(res, resId);
	}
	
	private void getHeaderIcon(ImageView imageView, Bitmap bmp) {
		if (bmp != null) {
			bmp = BitmapUtils.getMachineContactHeaderIcon(this, bmp);
			imageView.setImageBitmap(bmp);			
		}
	}

	private String getCustId(){

		if(mCurrent == null){
			return null;
		}

		if(mCurrent.getExternalCustId()!=null){
			return mCurrent.getExternalCustId();
		}

		String  custId = mCurrent.getCustId();
		while(custId.length()<8){
			custId = "0"+custId;
		}
		custId="01"+custId+"4002";

//		ToastUtils.shortToastShow(custId);
		return custId;
	}
}
