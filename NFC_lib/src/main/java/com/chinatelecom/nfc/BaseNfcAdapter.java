package com.chinatelecom.nfc;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts.Data;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.chinatelecom.nfc.Card.card.CardManager;
import com.chinatelecom.nfc.Const.Const;
import com.chinatelecom.nfc.DB.Dao.MyDataDao;
import com.chinatelecom.nfc.DB.Dao.NameCardDao;
import com.chinatelecom.nfc.DB.Pojo.MyData;
import com.chinatelecom.nfc.DB.Pojo.MyMode;
import com.chinatelecom.nfc.DB.Pojo.NameCard;
import com.chinatelecom.nfc.DB.Pojo.ProtocolTitle;
import com.chinatelecom.nfc.DB.Provider.MyDataTable;
import com.chinatelecom.nfc.Debug.MyDebug;
import com.chinatelecom.nfc.Interface.IGetPhoneDrviceIDForLottery;
import com.chinatelecom.nfc.Record.NdefMessageParser;
import com.chinatelecom.nfc.Record.ParsedNdefRecord;
import com.chinatelecom.nfc.Record.UriRecord;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MyUtil;
import com.google.gson.Gson;

public class BaseNfcAdapter extends Activity {
	private final String TAG = "BaseNfcAdapter";
	
	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private String jsonData;
	private Gson gson;
	private Resources res;
	protected ProgressDialog progress;

	private boolean isWriteTag = false;
	
	private IGetPhoneDrviceIDForLottery mIGetPhoneDrviceIDForLottery;
//	private SoundUtil sound;
//	private final int soundIndex = 1;
	
	// public NfcAdapter nfcAdapter(Context context) {
	// if (_nfcAdapter != null) {
	// return _nfcAdapter;
	// }
	// _nfcAdapter = NfcAdapter.getDefaultAdapter(context);
	// if (_nfcAdapter == null) {
	// MyUtil.showMessage("此设备不支持NFC！", context);
	// }
	// return _nfcAdapter;
	// }

	
	public IGetPhoneDrviceIDForLottery getmIGetPhoneDrviceIDForLottery() {
		return mIGetPhoneDrviceIDForLottery;
	}
	public void setmIGetPhoneDrviceIDForLottery(
			IGetPhoneDrviceIDForLottery mIGetPhoneDrviceIDForLottery) {
		this.mIGetPhoneDrviceIDForLottery = mIGetPhoneDrviceIDForLottery;
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(MyDebug.LIFTCYCLE)
			Log.e(TAG, "onCreate");
		
		res = getResources();
//		sound = new SoundUtil(BaseNfcAdapter.this);
//		sound.loadSfx(R.raw.sound, soundIndex);
		
		gson = new Gson();
		initNFCInfo();
	}

//	private Integer mydataId;
//	private Integer dataType;
//	private Integer tableID;
	protected MyData mMyData;
	protected Boolean isLotteryActivity = false;

	private Timer mTimer ;
	private TimerTask mTimerTask ;

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		if(MyDebug.LIFTCYCLE)Log.e(TAG, "onNewIntent");
		setIntent(intent);
		if (intent.getType() != null && intent.getType().equals("application/mobileinfo")) return;
		if(MyDebug.LIFTCYCLE)Log.e(TAG, "isWriteTag:" + isWriteTag);
		if (isWriteTag) {
			if(MyDebug.LIFTCYCLE)Log.e(TAG, "writeToNFC");
			writeToNFC(jsonData);
		} else {
			if(MyDebug.LIFTCYCLE)Log.e(TAG, "ReadFromNFC");
			boolean isFromTag = true;
			if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
				if(MyDebug.LIFTCYCLE)Log.e(TAG, "NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())");
				Intent NameCardIntent = intent;
				Parcelable[] rawMsgs = NameCardIntent
						.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
				if (rawMsgs != null && rawMsgs.length > 0) {
					NdefMessage msg = (NdefMessage) rawMsgs[0];
					String retStr = new String(msg.getRecords()[0].getPayload());
					
					if(!TextUtils.isEmpty(retStr) && MyUtil.deDiviceID(retStr)){
						mIGetPhoneDrviceIDForLottery.getDrviceID(MyUtil.subDiviceID(retStr));
						return ;
					}
					
					if (!TextUtils.isEmpty(retStr)
							&& MyUtil.isMydata(retStr)) {
						retStr = MyUtil.desMydata(retStr);
						isFromTag = false;
						MyData mydata = gson.fromJson(retStr, MyData.class);
						
						final NameCard nameCard = mydata.getNameCard();
						boolean flag  = false;
						int num = -1;
						StringBuilder sb = new StringBuilder();
						sb.append(MyDataTable.TAG_READ_FAVORITE).append(",")
						.append(MyDataTable.TAG_READFROMNFC).append(",")
						.append(MyDataTable.TAG_MY_NAMECARD);
						MyDataDao.getDataBase(this).open();
						List<MyData> list1 = MyDataDao.query(this, null, String.valueOf(MyDataTable.NAMECARD), sb.toString(),
								null, null, true);
						for(int i = 0 ; i < list1.size();i++){
							if(list1.get(i).dt == MyDataTable.NAMECARD){
								String str = list1.get(i).getNameCard().getName();
								if(nameCard.getName().equals(str)){
									flag = true;
									num = i;
								}
							}
						}
						if(!flag){
							//by yuzhao 2013/6/20 start
							long id_myData = MyDataDao.insert(this, nameCard,MyDataTable.TAG_READFROMNFC);
							intent = new Intent(Const.finishCurActivity);
							intent.putExtra(Constant.MYDATA_ID, MyUtil.longToInteger(id_myData));
							intent.putExtra(Constant.MYDATA_DATATYPE, MyDataTable.NAMECARD);
							intent.putExtra(Constant.INTENT_FORM_NFC, Constant.TAG_FORMNFC);
							intent.putExtra(Constant.INTENT_TYPE, Constant.INTENT_TYPE_READ_TAG);
							if (Const.NameCardActivityState == true)
								sendBroadcast(intent);							
							else if (Const.NameCardActivityState == false) {
								intent.setClass(this,NameCardManageActivity.class);
								intent.setAction(Intent.ACTION_DEFAULT);
								startActivity(intent);								
							}
							//by yuzhao 2013/6/20 end
						}else{
							if(list1.get(num).getNameCard().equals(nameCard)){
								MyUtil.showMessage(R.string.nfc_nc_exist, this,true);
								updateNameCardTime(nameCard);
							}else{
								Builder builder = new Builder(this);
								builder.setMessage(R.string.nfc_nc_not_same);
								builder.setTitle(R.string.nfc_nc_tip);
								builder.setPositiveButton(R.string.nfc_cancel,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												updateNameCardTime(nameCard);
												dialog.dismiss();
											}
										});
								builder.setNegativeButton(R.string.nfc_ok,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												updateNameCard(nameCard);
												dialog.dismiss();
											}
										});
								builder.create().show();
							}
						}
					} else {
						isFromTag = true;
					}
				}
			}

			if (isFromTag) {
				if(MyDebug.LIFTCYCLE)Log.e(TAG, "whichPage");
				whichPage(intent);
			}
		}
		 
	}
	/***
	 * 更新下数据库的createTime
	 */
	private void updateNameCardTime(NameCard nameCard ){
		StringBuilder readOrWrite = new StringBuilder();
		readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",").append(MyDataTable.TAG_READ_FAVORITE).append(",").append(MyDataTable.TAG_MY_NAMECARD);
		List<MyData> list = MyDataDao.query(this,null,String.valueOf(MyDataTable.NAMECARD),readOrWrite.toString(), null, null,false);
		for(int i = 0 ; i <list.size();i++){
			if(list.get(i).getNameCard().getName().equals(nameCard.getName())){
				MyDataDao.update(this, list.get(i));
				break;
			}
		}
	}
	/**
	 * 更新内容
	 */
	private void updateNameCard(NameCard nameCard ){
		MyData mydata;
		StringBuilder readOrWrite = new StringBuilder();
		readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",").append(MyDataTable.TAG_READ_FAVORITE).append(",").append(MyDataTable.TAG_MY_NAMECARD);
		List<MyData> list = MyDataDao.query(this,null,String.valueOf(MyDataTable.NAMECARD),readOrWrite.toString(), null, null,true);
		for(int i = 0 ; i <list.size();i++){
			if(list.get(i).getNameCard().getName().equals(nameCard.getName())){
				NameCard contactAllInfoCache = new NameCard(list.get(i).getNameCard().getId(), 
						nameCard.getName(), 
						nameCard.getContactIcon(), 
						nameCard.getTelPhone(),
						nameCard.getFax(),
						nameCard.getCompanyName(),
						nameCard.getCompanyNetAddress(), 
						nameCard.getCompanyTelPhone(), 
						nameCard.getSection(), 
						nameCard.getRank(), 
						nameCard.getAddress(), 
						nameCard.getEmail(), 
						nameCard.getDescription(), 
						nameCard.getShowFlag(), 
						nameCard.getShortCut());
				// mMyData.id = mydataId;
				mydata = MyDataDao.query(this, list.get(i).id, MyDataTable.NAMECARD);
				mydata.n = contactAllInfoCache.getName();
				mydata.setNameCard(contactAllInfoCache);
				MyDataDao.update(this,mydata);
				updateContact(this,contactAllInfoCache);
				break;
			}
		}
	}
	public void updateContact(Context context, NameCard tmp) {
		// 插入raw_contacts表，并获取_id属性
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentResolver resolver = context.getContentResolver();
		context.getContentResolver().delete(uri, ContactsContract.Contacts.DISPLAY_NAME + "=?", new String[] { tmp.getName() });
		ContentValues values = new ContentValues();
		long contact_id = ContentUris.parseId(resolver.insert(uri, values));
		// 插入data表
		uri = Uri.parse("content://com.android.contacts/data");
		// add Name
		values.put("raw_contact_id", contact_id);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/name");
		// values.put("data2", "zdong");
		values.put("data1", tmp.getName().toString());
		resolver.insert(uri, values);
		values.clear();
		// add Phone
		values.put("raw_contact_id", contact_id);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
		values.put("data2", "2"); // 手机
		values.put("data1", tmp.getTelPhone().toString());
		resolver.insert(uri, values);
		values.clear();
		// add email
		values.put("raw_contact_id", contact_id);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/email_v2");
		values.put("data2", "2"); // 单位
		values.put("data1", tmp.getEmail().toString());
		resolver.insert(uri, values);
	}
	
	

	private void whichPage(Intent intent) {
//			sound.play(soundIndex, 1);
		readFromNFC(intent);
	}

	public void getJsonData(MyData myData) {
		ProtocolTitle mProtocolTitle = new ProtocolTitle(this);
		mProtocolTitle.setData(myData);
		try {
			jsonData = gson.toJson(mProtocolTitle);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	void buildTagViews(NdefMessage[] msgs) {
		if (msgs == null || msgs.length == 0) {
			return;
		}
		List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
		//msgs[0].getRecords()
		final int size = records.size();
		StringBuffer sb = new StringBuffer();
		StringBuffer sbUri = new StringBuffer();
		for (int i = 0; i < size; i++) {
			ParsedNdefRecord record = records.get(i);
			if (record instanceof UriRecord) {
				sbUri.append(record.getContent());
			} else {
				sb.append(record.getContent());
			}
		}

		if (!TextUtils.isEmpty(sbUri.toString())) {
			// 网址标签
			MyData d = new MyData(null, sbUri.toString(), MyDataTable.WEB, -1,
					1l, MyDataTable.TAG_READFROMNFC);
			MyDataDao.insert(this, d, true);

			
			//intent.putExtra(Constant.INTENT_TYPE, Constant.INTENT_TYPE_READ_TAG);
			
			return;
		}
		MyDataDao.readData(this, sb.toString(), true);
	}

	private void writeToNFC(String str) {

		Tag m_tag =  ((Tag) getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG));
		
		if(m_tag == null){
			MyUtil.showMessage(R.string.nfc_msg_no_tag, this);
			return;
		}
		NdefRecord mNdefRecord = MyUtil.newTextRecord(str, Locale.CHINA, true);
		final Intent intent_to = new Intent(NfcAdapter.ACTION_TAG_DISCOVERED);
		intent_to.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES,
				mNdefRecord.toByteArray());
		
		try {
			NdefRecord[] arrayOfNdefRecord = new NdefRecord[] { mNdefRecord };
			NdefMessage localNdefMessage = new NdefMessage(arrayOfNdefRecord);
			Ndef localNdef = Ndef.get(m_tag);
			if (localNdef != null) {
				localNdef.connect();
				localNdef.writeNdefMessage(localNdefMessage);
			}
			NdefFormatable localNdefFormatable = NdefFormatable.get(m_tag);
			if (localNdefFormatable != null) {
				localNdefFormatable.connect();
				localNdefFormatable.format(localNdefMessage);
			}
			
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		} catch (FormatException localFormatException) {
			localFormatException.printStackTrace();
		}
		if(progress != null)
			progress.dismiss();
	}

	void readFromNFC(Intent intent) {
		// Parse the intent
		String action = intent.getAction();

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			final Parcelable p = intent
					.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NdefMessage[] msgs;
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
				// Setup the views
				setTitle(R.string.nfc_title_scanned_tag);
				buildTagViews(msgs);
			} else if (p != null) {
				String card = CardManager.load(p, res);
				if(!TextUtils.isEmpty(card)){
					MyData d = new MyData(null, card, MyDataTable.BUS_CARD, -1, 1l,
							MyDataTable.TAG_READFROMNFC);
					getJsonData(d);
					
					long id_bus = MyDataDao.insert(this, d, true);//如果是公交则补存储记录
					
					
					Intent mIntent = new Intent(this,DetailBusActivity.class);
					mIntent.putExtra(Constant.MYDATA_ID, MyUtil.longToInteger(id_bus));//如果是公交则补存储记录
					mIntent.putExtra(Constant.MYDATA_DATATYPE, MyDataTable.BUS_CARD);
					mIntent.putExtra(Constant.INTENT_TYPE, Constant.INTENT_TYPE_READ_TAG);
					mIntent.putExtra(Constant.INTENT_FORM_NFC, Constant.TAG_FORMNFC);
					startActivity(mIntent);
					//结束刷卡页面
					finish();
				}else {
					MyUtil.showMessage(R.string.nfc_bus_null, this);
				}
				// onlyContentView("公交卡",card);
			} else {

				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };

				// Setup the views
				setTitle(R.string.nfc_title_scanned_tag);
				buildTagViews(msgs);
			}
		}
	}
	private boolean isSupportNFCDevice = true;

	private void initNFCInfo() {
		if (nfcAdapter == null) {
			this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		}
		if (nfcAdapter == null) {
			isSupportNFCDevice = false;
			return ;
		}
		Intent localIntent = new Intent(this, this.getClass());
		localIntent.setAction(NfcAdapter.ACTION_TECH_DISCOVERED);
		localIntent.putExtra("ifread", true);
		this.pendingIntent = PendingIntent.getActivity(this, 0,
				localIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter localIntentFilter1 = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			localIntentFilter1.addDataType("*/*");
			IntentFilter localIntentFilter2 = new IntentFilter(
					NfcAdapter.ACTION_TAG_DISCOVERED);
			IntentFilter localIntentFilter3 = new IntentFilter(
					NfcAdapter.ACTION_TECH_DISCOVERED);
			IntentFilter[] arrayOfIntentFilter = new IntentFilter[3];
			arrayOfIntentFilter[0] = localIntentFilter1;
			arrayOfIntentFilter[1] = localIntentFilter2;
			arrayOfIntentFilter[2] = localIntentFilter3;
			this.mFilters = arrayOfIntentFilter;
			String[][] arrayOfStrings = new String[1][];
			String[] arrayOfString = new String[] { IsoDep.class.getName(),
					NfcA.class.getName(), NfcB.class.getName(),
					NfcF.class.getName(), NfcV.class.getName(),
					Ndef.class.getName(), NdefFormatable.class.getName(),
					MifareClassic.class.getName(),
					MifareUltralight.class.getName(), };
			arrayOfStrings[0] = arrayOfString;
			this.mTechLists = arrayOfStrings;
		} catch (IntentFilter.MalformedMimeTypeException localMalformedMimeTypeException) {
			throw new RuntimeException("fail", localMalformedMimeTypeException);
		}
	}

	protected void enableForegroundDispatch() {
		this.nfcAdapter.enableForegroundDispatch(this, this.pendingIntent,
				this.mFilters, this.mTechLists);
	}
	protected void disableForegroundDispatch() {
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(this);
			// nfcAdapter.disableForegroundNdefPush(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(MyDebug.LIFTCYCLE)Log.e(TAG, "onResume");
		
		if (nfcAdapter != null) {
			enableForegroundDispatch();
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if(MyDebug.DEBUG)Log.e(TAG, "onPause");
		disableForegroundDispatch();
	}

	/**
	 * 显示一个进度条，显示十秒。
	 **/
	protected void showPlanDialog() {
		progress = new ProgressDialog(this);
		progress.setMessage("搜索中，请稍后...");
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setCanceledOnTouchOutside(false);
		progress.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				mTimer = new Timer();
				mTimerTask = new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						progress.dismiss();
					}
				};
				mTimer.schedule(mTimerTask, Constant.TIME_OUT);
				isWriteTag = true;
			}
		});
		progress.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				isWriteTag = false;
				mTimer.cancel();
				mTimerTask.cancel();
			}
		});
		progress.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(MyDebug.LIFTCYCLE)Log.e(TAG, "onDestroy");
		if (mTimerTask != null)
			mTimerTask.cancel();
		if (mTimer != null)
			mTimer.cancel();
	}

	/**
	 * 写标签点击监听
	 **/
	private LinearLayout llShareTag;

	protected void initShareTag() {
		llShareTag = (LinearLayout) findViewById(R.id.llShareTag);
		llShareTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				getJsonData(mMyData);
				showPlanDialog();
			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!isSupportNFCDevice){
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
