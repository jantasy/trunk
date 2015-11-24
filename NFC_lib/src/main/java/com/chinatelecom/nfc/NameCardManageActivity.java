package com.chinatelecom.nfc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.RawContacts.Data;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chinatelecom.nfc.Const.Const;
import com.chinatelecom.nfc.DB.Dao.MyDataDao;
import com.chinatelecom.nfc.DB.Dao.NameCardDao;
import com.chinatelecom.nfc.DB.Pojo.MyData;
import com.chinatelecom.nfc.DB.Pojo.NameCard;
import com.chinatelecom.nfc.DB.Provider.MyDataTable;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MyUtil;
import com.google.gson.Gson;

public class NameCardManageActivity extends BaseTag implements
		View.OnClickListener, OnTouchListener,
		CreateNdefMessageCallback {

	public static final String TAG = "NameCardManageActivity";

	NameCard contactAllInfoCache = null;// 缓存联系人所有信息

	// 各个组件
	private EditText name;// 姓名
	private EditText telphone;// 号码
	private EditText fax;// 传真
	private EditText companyName;// 公司名称
	private EditText companyNetAddress;// 公司网址
	private EditText companyPhone;// 公司电话
	private EditText section;// 部门
	private EditText rank;// 职位

	private EditText address;// 住址
	private EditText email;// 邮箱
	private EditText information;// 好友描述

	private PhotoEditorView mPhoto;// 头像

	private LinearLayout namecard_save;// 确定
	private ImageView nc_img_save;
	private LinearLayout namecard_swap;// 交换名片
	private ImageView nc_img_swap;
	private LinearLayout namecard_send;// 把当前名片发送到桌面
	private ImageView nc_img_send;
	private LinearLayout namecard_edit;// 新增名片，读取通讯录信息
	private ImageView nc_img_edit;
	private LinearLayout namecard_add;// 添加其他项目。
	private ImageView nc_img_add;

	private LinearLayout lay_tel;
	private Button nc_del_tel;
	private Button nc_call_tel;
	private LinearLayout lay_company;
	private Button nc_del_company;
	private Button nc_call_company;
	private LinearLayout lay_fax;
	private Button nc_del_fax;
	private Button nc_call_fax;
	private TextView nc_txt_phone;

	private LinearLayout lay_section;
	private Button nc_del_section;
	private LinearLayout lay_rank;
	private Button nc_del_rank;
	private LinearLayout lay_net;
	private Button nc_del_net;
	private LinearLayout lay_addr;
	private Button nc_del_addr;
	private LinearLayout lay_eamil;
	private Button nc_del_eamil;
	private TextView nc_txt_detail;

	private TextView nc_txt_ok;

	byte[] _img;// 头像数据
	Bitmap tmpImg;// 保存头像图片，当点击确定的时候把tmpImg存到_img
	private short _showflag;// 置为1则在NameCardManageActivity界面隐藏，在AddItemDialog显示。
	private int shortcut;
	// activity处于两种状态，插入状态或者编辑状态
	private static final int STATE_INSERT = 0;// 插入状态
	private static final int STATE_EDIT = 1;// 编辑状态
	private static final int STATE_SHOW = 2;// 显示状态，也是默认状态。
	private int state;// 记录当前的状态

	private static final int CROP_SMALL_PICTURE = 3;// 剪切图片
	private static final int TAKE_SMALL_PICTURE = 4;// 调用摄像头
	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
	private Uri imageUri;// to store the big bitmap
	private final int REQUEST_CONTACT = 5;// 调用通讯录
	private static final int PHOTO_PICKED_WITH_DATA = 3021;// 用来标识请求gallery的activity

	NameCard namecard = null;
	// MyData mMyData = null;
	int destopCount;
	public static final String TAG_CHECKED = String.valueOf('\u221A');
	public static final String TAG_UNCHECKED = String.valueOf('\u25A1');
	private static final int SHORTCUT_ICON_TITLE_MAX_LEN = 10;

	AddItemDialog mDialog;
	private String intentType = null;
	
	private finishCurActivityBroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		setContentView(R.layout.nfc_namecard);
		Const.NameCardActivityState = true;
		init();// 初始化所有组件

		Intent intent = getIntent();
		String action = intent.getAction();
		intentType = intent.getStringExtra(Constant.INTENT_MY_NAMECARD);
		mydataId = intent.getIntExtra(Constant.MYDATA_ID, -1);
		if (mydataId == -1)
			mydataId = (int) intent.getIntExtra(Intent.EXTRA_UID, -2);
		Log.i("NameCardActivity", "id :" + mydataId);
		if (mydataId > 0) {
			if (!queryNFC(mydataId)) {
				MyUtil.showMessage("该名片以删除或丢失", this);
				finish();
				return;
			} else {
				mMyData = getMyData(MyDataTable.NAMECARD);
				namecard = mMyData.getNameCard();
			}
		} else
			mMyData = getMyData();

		if (action.equals(Intent.ACTION_INSERT)) {
			state = STATE_INSERT;
			_showflag = (short) 0x002c;// 默认除了传真，网址，职位其他的在编辑界面是都显示的。
			shortcut = 0;// 初始化，桌面是没有对应的快捷方式的。
			name.requestFocus();
//			nc_img_save.setBackgroundResource(R.drawable.nfc_nc_save_press);
		} else if (action.equals(Intent.ACTION_EDIT)) {
			state = STATE_EDIT;
			// 这个是为了我名片加入的判断，如果是我的名片就给各个部件赋值，如果是是其他的就不调用showView（）
			if (intentType != null
					&& intentType.equals(String
							.valueOf(MyDataTable.TAG_MY_NAMECARD))) {
				showView();
				intentType = null;
			}
			name.requestFocus();
//			nc_img_save.setBackgroundResource(R.drawable.nfc_nc_save_press);
		} else if (action.equals(Intent.ACTION_DEFAULT)) {
			state = STATE_SHOW;
			nc_img_save.setBackgroundResource(R.drawable.nfc_nc_edit);
		} else {
			Log.e(TAG, "Unknown action,program will exit...");
			finish();
			return;
		}
		imageUri = Uri.parse(IMAGE_FILE_LOCATION);
		registeNFCNdef();
		registefinishCurActivityBroadcastReceiver();
	}

	private void registeNFCNdef() {
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter!=null) {
			nfcAdapter.setNdefPushMessageCallback(this, this);
		}
		
	}

	private void registefinishCurActivityBroadcastReceiver() {
		IntentFilter filter = new IntentFilter(Const.finishCurActivity);
		broadcastReceiver = new finishCurActivityBroadcastReceiver();
		registerReceiver(broadcastReceiver, filter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (state == STATE_INSERT) {
			edit(true);
			nc_txt_ok.setText(R.string.nfc_nc_save);
		} else if (state == STATE_EDIT) {
			if (namecard != null) {
				edit(true);
				nc_txt_ok.setText(R.string.nfc_nc_save);
			}
		} else if (state == STATE_SHOW) {
			if (namecard != null) {
				if (readFromNfc) {
					save2Contact();
					// readFromNfc = false;
				}
				showView();
				edit(false);
				nc_txt_ok.setText(R.string.nfc_nc_edit);
			}
		}
	}

	private void save2Contact() {
		Builder save2Contact = new Builder(this);
		save2Contact.setTitle(R.string.nfc_nc_tip);
		save2Contact.setMessage(R.string.nfc_nc_save_nc_read_from_nfc);
		save2Contact.setPositiveButton(R.string.nfc_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		save2Contact.setNegativeButton(R.string.nfc_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!isExist(namecard.getName().toString()))
							Add2Contacts();
						dialog.dismiss();
					}
				});
		save2Contact.create().show();
	}

	/**
	 * 清空所有控件
	 */
	public void clearAll() {
		name.setText("");
		mPhoto.setPhotoBitmap(null);
		telphone.setText("");
		companyName.setText("");
		companyNetAddress.setText("");
		companyPhone.setText("");
		section.setText("");
		rank.setText("");
		address.setText("");
		email.setText("");
		information.setText("");
	}

	@Override
	public void edit(boolean flag) {
		super.edit(flag);
		name.setEnabled(flag);// 设置EditText是否可编辑
		mPhoto.setEnabled(flag);
		telphone.setEnabled(flag);
		fax.setEnabled(flag);
		companyName.setEnabled(flag);
		companyNetAddress.setEnabled(flag);
		companyPhone.setEnabled(flag);
		section.setEnabled(flag);
		rank.setEnabled(flag);
		address.setEnabled(flag);
		email.setEnabled(flag);
		information.setEnabled(flag);

		name.setSelected(flag);
		mPhoto.setSelected(flag);
		telphone.setSelected(flag);
		fax.setSelected(flag);
		companyName.setSelected(flag);
		companyNetAddress.setSelected(flag);
		companyPhone.setSelected(flag);
		section.setSelected(flag);
		rank.setSelected(flag);
		address.setSelected(flag);
		email.setSelected(flag);
		information.setSelected(flag);

		whitchItm2Show();

		if (flag) {
			companyName.setVisibility(View.VISIBLE);
			namecard_add.setVisibility(View.VISIBLE);
			if (lay_tel.getVisibility() == View.VISIBLE) {
				// nc_del_tel.setVisibility(View.VISIBLE);
				nc_call_tel.setVisibility(View.GONE);
			}
			if (lay_company.getVisibility() == View.VISIBLE) {
				// nc_del_company.setVisibility(View.VISIBLE);
				nc_call_company.setVisibility(View.GONE);
			}
			if (lay_fax.getVisibility() == View.VISIBLE) {
				nc_del_fax.setVisibility(View.VISIBLE);
				nc_call_fax.setVisibility(View.GONE);
			}
			nc_del_section.setVisibility(View.VISIBLE);
			nc_del_rank.setVisibility(View.VISIBLE);
			nc_del_net.setVisibility(View.VISIBLE);
			nc_del_addr.setVisibility(View.VISIBLE);
			nc_del_eamil.setVisibility(View.VISIBLE);
		} else {
			if (companyName.getText().toString().equals("")) {
				companyName.setVisibility(View.GONE);
			}
			namecard_add.setVisibility(View.GONE);
			if (lay_tel.getVisibility() == View.VISIBLE) {
				// nc_del_tel.setVisibility(View.GONE);
				nc_call_tel.setVisibility(View.VISIBLE);
			}
			if (lay_company.getVisibility() == View.VISIBLE) {
				// nc_del_company.setVisibility(View.GONE);
				nc_call_company.setVisibility(View.VISIBLE);
			}
			if (lay_fax.getVisibility() == View.VISIBLE) {
				nc_del_fax.setVisibility(View.GONE);
				nc_call_fax.setVisibility(View.VISIBLE);
			}
			nc_del_section.setVisibility(View.GONE);
			nc_del_rank.setVisibility(View.GONE);
			nc_del_net.setVisibility(View.GONE);
			nc_del_addr.setVisibility(View.GONE);
			nc_del_eamil.setVisibility(View.GONE);
		}
		if (flag) {
			nc_img_save.setBackgroundResource(R.drawable.nfc_nc_save);
		} else
			nc_img_save.setBackgroundResource(R.drawable.nfc_nc_edit);
	}

	@Override
	public void showView() {
		mMyData = getMyData(MyDataTable.NAMECARD);
		namecard = mMyData.getNameCard();
		_showflag = namecard.getShowFlag();
		shortcut = namecard.getShortCut();
		_img = namecard.getContactIcon();
		if (intentType != null
				&& intentType.equals(String
						.valueOf(MyDataTable.TAG_MY_NAMECARD))) {
			name.setText("");
		} else
			name.setText(namecard.getName());
		mPhoto.setPhotoBitmap(getBitmapFromByte(_img));
		if (_img != null) {
			BitmapDrawable bd = (BitmapDrawable) mPhoto.getDrawable();
			tmpImg = bd.getBitmap();
		}
		telphone.setText(namecard.getTelPhone());
		fax.setText(namecard.getFax());
		companyName.setText(namecard.getCompanyName());
		companyNetAddress.setText(namecard.getCompanyNetAddress());
		companyPhone.setText(namecard.getCompanyTelPhone());
		section.setText(namecard.getSection());
		rank.setText(namecard.getRank());
		address.setText(namecard.getAddress());
		email.setText(namecard.getEmail());
		if (namecard.getDescription() != null
				&& !namecard.getDescription().equals(""))
			information.setTextKeepState(namecard.getDescription());
	}

	/**
	 * 当从当前的Activity切换到另一个Activity时调用， 切换过去时当前Activity并未结束(Destory) 缓存联系人信息
	 */
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		String tmpname = name.getText().toString();
		String tmptelphone = telphone.getText().toString();
		String tmpfax = fax.getText().toString();
		String tmpcompanyName = companyName.getText().toString();
		String tmpcompanyNetAddress = companyNetAddress.getText().toString();
		String tmpcompanyPhone = companyPhone.getText().toString();
		String tmpsection = section.getText().toString();
		String tmprank = rank.getText().toString();

		String tmpaddress = address.getText().toString();
		String tmpemail = email.getText().toString();
		String tmpinformation = information.getText().toString();
		String tmpIntentType = intentType;
		BitmapDrawable bd = (BitmapDrawable) mPhoto.getDrawable();
		Bitmap bitMap = bd.getBitmap();
		contactAllInfoCache = new NameCard(mydataId, tmpname,
				getBitmapByte(bitMap), tmptelphone, tmpfax, tmpcompanyName,
				tmpcompanyNetAddress, tmpcompanyPhone, tmpsection, tmprank,
				tmpaddress, tmpemail, tmpinformation, _showflag, shortcut);
		// 得到最终用户信息
		outState.putSerializable("originalData", contactAllInfoCache);
	}

	@Override
	public void init() {
		super.init();
		name = (EditText) findViewById(R.id.name);

		mPhoto = (PhotoEditorView) findViewById(R.id.nameCardPhoto);
		mPhoto.setEditorListener(new PhotoListener());

		telphone = (EditText) findViewById(R.id.phoneNumber);
		fax = (EditText) findViewById(R.id.faxNumber);
		companyName = (EditText) findViewById(R.id.companyName);
		companyNetAddress = (EditText) findViewById(R.id.companyNetAddress);
		companyPhone = (EditText) findViewById(R.id.companyPhone);
		section = (EditText) findViewById(R.id.section);
		rank = (EditText) findViewById(R.id.rank);

		address = (EditText) findViewById(R.id.address);
		email = (EditText) findViewById(R.id.email);
		information = (EditText) findViewById(R.id.information);

		nc_img_save = (ImageView) findViewById(R.id.nc_img_ok);
		namecard_save = (LinearLayout) findViewById(R.id.namecard_save);
		// namecard_save.setOnClickListener(this);
		namecard_save.setOnTouchListener(this);
		nc_img_swap = (ImageView) findViewById(R.id.nc_img_swap);
		namecard_swap = (LinearLayout) findViewById(R.id.llShareTag);
		namecard_swap.setOnTouchListener(this);
		nc_img_send = (ImageView) findViewById(R.id.nc_img_send);
		namecard_send = (LinearLayout) findViewById(R.id.namecard_send);
		// namecard_send.setOnClickListener(this);
		namecard_send.setOnTouchListener(this);
		nc_img_edit = (ImageView) findViewById(R.id.nc_img_edit);
		namecard_edit = (LinearLayout) findViewById(R.id.namecard_edit);
		// namecard_edit.setOnClickListener(this);
		namecard_edit.setOnTouchListener(this);
		nc_img_add = (ImageView) findViewById(R.id.nc_img_add);
		namecard_add = (LinearLayout) findViewById(R.id.namecard_add);
		// namecard_add.setOnClickListener(this);
		namecard_add.setOnTouchListener(this);

		lay_tel = (LinearLayout) findViewById(R.id.lay_tel);

		// nc_del_tel = (Button) findViewById(R.id.nc_del_phone);
		// nc_del_tel.setOnClickListener(this);
		nc_call_tel = (Button) findViewById(R.id.nc_call_phone);
		nc_call_tel.setOnClickListener(this);

		lay_company = (LinearLayout) findViewById(R.id.lay_company);
		// nc_del_company = (Button) findViewById(R.id.nc_del_company);
		// nc_del_company.setOnClickListener(this);
		nc_call_company = (Button) findViewById(R.id.nc_call_company);
		nc_call_company.setOnClickListener(this);

		lay_fax = (LinearLayout) findViewById(R.id.lay_fax);
		nc_del_fax = (Button) findViewById(R.id.nc_del_fax);
		nc_del_fax.setOnClickListener(this);
		nc_call_fax = (Button) findViewById(R.id.nc_call_fax);
		nc_call_fax.setOnClickListener(this);
		nc_txt_phone = (TextView) findViewById(R.id.nc_txt_phone);

		lay_section = (LinearLayout) findViewById(R.id.lay_section);
		nc_del_section = (Button) findViewById(R.id.nc_del_section);
		nc_del_section.setOnClickListener(this);
		lay_rank = (LinearLayout) findViewById(R.id.lay_rank);
		nc_del_rank = (Button) findViewById(R.id.nc_del_rank);
		nc_del_rank.setOnClickListener(this);
		lay_net = (LinearLayout) findViewById(R.id.lay_net);
		nc_del_net = (Button) findViewById(R.id.nc_del_net);
		nc_del_net.setOnClickListener(this);
		lay_addr = (LinearLayout) findViewById(R.id.lay_addr);
		nc_del_addr = (Button) findViewById(R.id.nc_del_adress);
		nc_del_addr.setOnClickListener(this);
		lay_eamil = (LinearLayout) findViewById(R.id.lay_email);
		nc_del_eamil = (Button) findViewById(R.id.nc_del_email);
		nc_del_eamil.setOnClickListener(this);
		nc_txt_detail = (TextView) findViewById(R.id.nc_txt_detail);

		nc_txt_ok = (TextView) findViewById(R.id.nc_txt_ok);
		mDialog = new AddItemDialog(this);
		mDialog.setIsWhitchClick(new AddItemDialog.IsWhitchClick() {
			@Override
			public void whichIsClick(int ClickId) {
				// TODO Auto-generated method stub
				showItem(ClickId);
				showOrHideTip();
				edit(true);
			}
		});

		needShowBottomBar();

		if (MyUtil.getActivity() == null)
			MyUtil.setActivity(this);
	}

	/**
	 * 判断哪些是要显示的。
	 * 
	 * @param id
	 */
	private void showItem(int id) {
		switch (id) {
		// case 0:
		// lay_tel.setVisibility(View.VISIBLE);
		// _showflag = (short) (_showflag & 0x007f);
		// break;
		// case 1:
		// lay_company.setVisibility(View.VISIBLE);
		// _showflag = (short) (_showflag & 0x00bf);
		// break;
		case 2:
			lay_fax.setVisibility(View.VISIBLE);
			_showflag = (short) (_showflag & 0x00df);
			break;
		case 3:
			lay_section.setVisibility(View.VISIBLE);
			_showflag = (short) (_showflag & 0x00ef);
			break;
		case 4:
			lay_rank.setVisibility(View.VISIBLE);
			_showflag = (short) (_showflag & 0x00f7);
			break;
		case 5:
			lay_net.setVisibility(View.VISIBLE);
			_showflag = (short) (_showflag & 0x00fb);
			break;
		case 6:
			lay_addr.setVisibility(View.VISIBLE);
			_showflag = (short) (_showflag & 0x00fd);
			break;
		case 7:
			lay_eamil.setVisibility(View.VISIBLE);
			_showflag = (short) (_showflag & 0x00fe);
			break;
		}
	}

	/**
	 * 在state=STATE_SHOW时判断哪些需要显示。
	 */
	void whitchItm2Show() {
		// if (((_showflag & 0x0080)>>7) != 1) {
		// lay_tel.setVisibility(View.VISIBLE);
		// }
		// if(((_showflag & 0x0040)>>6) != 1 ){
		// lay_company.setVisibility(View.VISIBLE);
		// }
		if (((_showflag & 0x0020) >> 5) != 1)
			lay_fax.setVisibility(View.VISIBLE);
		else
			lay_fax.setVisibility(View.GONE);
		if (((_showflag & 0x0010) >> 4) != 1)
			lay_section.setVisibility(View.VISIBLE);
		else
			lay_section.setVisibility(View.GONE);
		if (((_showflag & 0x0008) >> 3) != 1)
			lay_rank.setVisibility(View.VISIBLE);
		else
			lay_rank.setVisibility(View.GONE);
		if (((_showflag & 0x0004) >> 2) != 1)
			lay_net.setVisibility(View.VISIBLE);
		else
			lay_net.setVisibility(View.GONE);
		if (((_showflag & 0x0002) >> 1) != 1)
			lay_addr.setVisibility(View.VISIBLE);
		else
			lay_addr.setVisibility(View.GONE);
		if (((_showflag & 0x0001)) != 1)
			lay_eamil.setVisibility(View.VISIBLE);
		else
			lay_eamil.setVisibility(View.GONE);
	}

	/**
	 * 调用系统通讯录activity，返回点击的联系人contactID
	 */
	private void getNameCard() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setData(ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, REQUEST_CONTACT);
	}

	/**
	 * 提示保存对话框
	 */
	protected void saveDialog() {
		Builder builder = new Builder(this);
		builder.setMessage(R.string.nfc_confirm_save);
		builder.setTitle(R.string.nfc_title_prompt);
		builder.setPositiveButton(R.string.nfc_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(R.string.nfc_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (verifyAllData()) {
							nc_txt_ok.setText(R.string.nfc_nc_edit);
							state = STATE_SHOW;
						}
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private boolean queryNFC(int id) {
		MyData tmp = MyDataDao.query(this, id, MyDataTable.NAMECARD);
		
		if (tmp != null) {
			return true;
		}
		return false;
	}

	/**
	 * @param name
	 * @return 查询namecard表，若有相同名字就返回true，没有返回false
	 */
	private boolean queryNCTabel(String name) {
		List<NameCard> tmp = NameCardDao.query(this, name, true);
		if (!tmp.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean check() {
		if (TextUtils.isEmpty(name.getText().toString().trim())) {
			MyUtil.showMessage(R.string.nfc_nc_no_name,
					NameCardManageActivity.this);
			return false;
		}
		if (TextUtils.isEmpty(telphone.getText().toString().trim())) {
			MyUtil.showMessage(R.string.nfc_nc_no_phone,
					NameCardManageActivity.this);
			return false;
		}
		if (!TextUtils.isEmpty(email.getText().toString().trim())) {
			if (!isEmail(email.getText().toString().trim())) {
				MyUtil.showMessage(R.string.nfc_nc_email_error,
						NameCardManageActivity.this);
				return false;
			}
		}
		return true;
	}

	/**
	 * 保存并得到所有信息
	 */
	protected boolean verifyAllData() {
		if (tmpImg != null)
			_img = getBitmapByte(tmpImg);
		if (state == STATE_INSERT) {
			if (queryNCTabel(name.getText().toString().trim())) {
				MyUtil.showMessage(R.string.nfc_nc_exist,
						NameCardManageActivity.this);
				return false;
			}
			contactAllInfoCache = new NameCard(null, name.getText().toString()
					.trim(), _img, telphone.getText().toString().trim(), fax
					.getText().toString().trim(), companyName.getText()
					.toString(), companyNetAddress.getText().toString(),
					companyPhone.getText().toString(), section.getText()
							.toString(), rank.getText().toString(), address
							.getText().toString().trim(), email.getText()
							.toString().trim(), information.getText()
							.toString().trim(), _showflag, shortcut);
			destopCount = (int) MyDataDao.insert(this, contactAllInfoCache,
					MyDataTable.TAG_WRITETAG);
			mMyData = MyDataDao.query(this, destopCount, MyDataTable.NAMECARD);
			mydataId = mMyData.id;
		} else if (state == STATE_EDIT) {
			updateNameCard();
		}
		edit(false);
		return true;
	}

	void updateNameCard() {
		if (mMyData != null) {
			contactAllInfoCache = new NameCard(mMyData.getNameCard().getId(),
					name.getText().toString().trim(), _img, telphone.getText()
							.toString().trim(),
					fax.getText().toString().trim(), companyName.getText()
							.toString(),
					companyNetAddress.getText().toString(), companyPhone
							.getText().toString(),
					section.getText().toString(), rank.getText().toString(),
					address.getText().toString().trim(), email.getText()
							.toString().trim(), information.getText()
							.toString().trim(), _showflag, shortcut);
			// mMyData.id = mydataId;
			mMyData.n = name.getText().toString().trim();
			mMyData.setNameCard(contactAllInfoCache);
			MyDataDao.update(this, mMyData);
		}
	}

	public Boolean isEmail(String str) {
		String regex = "[a-zA-Z0-9]{1,}[0-9]{0,}@(([a-zA-Z0-9]-*){1,}\\.){1,3}[a-zA-Z\\-]{1,}";
		return match(regex, str);
	}

	public Boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(broadcastReceiver);
		super.onDestroy();
		Const.NameCardActivityState = false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	// 将头像转换成byte[]以便能将图片存到数据库
	public byte[] getBitmapByte(Bitmap bitmap) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "transform byte exception");
		}
		return out.toByteArray();
	}

	// 得到存储在数据库中的头像
	public Bitmap getBitmapFromByte(byte[] temp) {
		if (temp != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			return bitmap;
		} else {
			// Bitmap bitmap=BitmapFactory.decodeResource(getResources(),
			// R.drawable.nfc_contact_add_icon);
			return null;
		}
	}

	// 弹出提示信息
	public void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 拍照获取图片
	 */
	protected void doTakePhoto() {
		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// action
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, TAKE_SMALL_PICTURE);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.nfc_photoPickerNotFoundText,
					Toast.LENGTH_LONG).show();
		}
	}

	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	/**
	 * 用当前时间给取得的图片命名
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date) + ".jpg";
	}

	/**
	 * 请求Gallery程序
	 */
	protected void doPickPhotoFromGallery() {
		try {
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.nfc_photoPickerNotFoundText1,
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 封装请求Gallery的intent
	 */
	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		return intent;
	}

	/**
	 * 因为调用了Camera和Gally所以要判断他们各自的返回情况, 他们启动时是这样的startActivityForResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的
			tmpImg = data.getParcelableExtra("data");
			_img = getBitmapByte(tmpImg);
			mPhoto.setPhotoBitmap(tmpImg);
			break;
		}
		case REQUEST_CONTACT:
			if (resultCode == RESULT_OK) {
				if (data == null) {
					return;
				}
				Uri result = data.getData();
				String contactId = result.getLastPathSegment();
				ReadContactInfo(contactId);// 通过联系人contactId获得联系人的资料。
			}
			break;
		case TAKE_SMALL_PICTURE:
			Log.i(TAG, "TAKE_SMALL_PICTURE: data = ");
			cropImageUri(imageUri, 80, 80, CROP_SMALL_PICTURE);
			break;
		case CROP_SMALL_PICTURE:
			if (imageUri != null) {
				tmpImg = decodeUriAsBitmap(imageUri);
				mPhoto.setPhotoBitmap(tmpImg);
				intentType = null;// 我的名片的时候，这个是因为黑色手机在点击拍照之后，图片设不上去
			} else {
				Log.e(TAG, "CROP_SMALL_PICTURE: data = " + data);
			}
			break;
		}
	}

	/**
	 * 判断读出的名片标签在通讯录中是否存在。
	 * 
	 * @return 存在返回true ，不存在返回false
	 */
	public boolean isExist(String name) {
		// 根据姓名求id
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(uri, new String[] { Data._ID },
				"display_name=?", new String[] { name }, null);
		if (cursor.getCount() != 0)
			return true;
		else
			return false;
	}

	/**
	 * 一步一步添加数据
	 */
	public void Add2Contacts() {
		// 插入raw_contacts表，并获取_id属性
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentResolver resolver = getContentResolver();
		ContentValues values = new ContentValues();
		long contact_id = ContentUris.parseId(resolver.insert(uri, values));
		// 插入data表
		uri = Uri.parse("content://com.android.contacts/data");
		// add Name
		values.put("raw_contact_id", contact_id);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/name");
		// values.put("data2", "zdong");
		values.put("data1", namecard.getName().toString());
		resolver.insert(uri, values);
		values.clear();
		// add Phone
		values.put("raw_contact_id", contact_id);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
		values.put("data2", "2"); // 手机
		values.put("data1", namecard.getTelPhone().toString());
		resolver.insert(uri, values);
		values.clear();
		// add email
		values.put("raw_contact_id", contact_id);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/email_v2");
		values.put("data2", "2"); // 单位
		values.put("data1", namecard.getEmail().toString());
		resolver.insert(uri, values);
	}

	/**
	 * 读取通讯录的全部的联系人 需要先在raw_contact表中遍历id，并根据id到data表中获取数据
	 */
	public void ReadContactInfo(String contactId) {
		Uri uri = Uri.parse("content://com.android.contacts/contacts"); // 访问raw_contacts表
		ContentResolver resolver = getContentResolver();
		StringBuilder buf = new StringBuilder();
		buf.append("id=" + contactId);
		// 如果要获得data表中某个id对应的数据，则URI为content://com.android.contacts/contacts/#/data
		uri = Uri.parse("content://com.android.contacts/contacts/" + contactId
				+ "/data");
		Cursor cursor2 = resolver.query(uri, new String[] { Data.DATA1,
				Data.MIMETYPE }, null, null, null); // data1存储各个记录的总数据，mimetype存放记录的类型，如电话、email等
		while (cursor2.moveToNext()) {
			String data = cursor2.getString(cursor2.getColumnIndex("data1"));

			if (cursor2.getString(cursor2.getColumnIndex("mimetype")).equals(
					StructuredName.CONTENT_ITEM_TYPE)) { // 名字
				name.setText(data);
			} else if (cursor2.getString(cursor2.getColumnIndex("mimetype"))
					.equals(Phone.CONTENT_ITEM_TYPE)) { // 电话
				// int phoneType =
				// cursor2.getInt(cursor2.getColumnIndex(Phone.CONTENT_TYPE));
				// if (phoneType == Phone.TYPE_MOBILE) //电话
				telphone.setText(data);
				// if (phoneType == Phone.TYPE_WORK)//公司电话
				// setEditText(companyPhone,_companyPhone,data);
			} else if (cursor2.getString(cursor2.getColumnIndex("mimetype"))
					.equals(Email.CONTENT_ITEM_TYPE)) { // email
				email.setText(data);
			} else if (cursor2.getString(cursor2.getColumnIndex("mimetype"))
					.equals(StructuredPostal.CONTENT_ITEM_TYPE)) { // 地址
				address.setText(data);
			} else if (cursor2.getString(cursor2.getColumnIndex("mimetype"))
					.equals(Note.CONTENT_ITEM_TYPE)) {// 描述
				information.setText(data);
			}

			else if (cursor2.getString(cursor2.getColumnIndex("mimetype"))
					.equals(Organization.COMPANY)) { // 公司名称
				companyName.setText(data);
			} else if (cursor2.getString(cursor2.getColumnIndex("mimetype"))
					.equals(Website.URL)) {// 公司网址
				companyNetAddress.setText(data);
			}

			else if (cursor2.getString(cursor2.getColumnIndex("mimetype"))
					.equals(Organization.DEPARTMENT)) {// 公司部门
				section.setText(data);
			} else if (cursor2.getString(cursor2.getColumnIndex("mimetype"))
					.equals(Organization.TITLE)) {// 公司职位
				rank.setText(data);
			}

		}
		Cursor cursor3 = resolver.query(uri, new String[] { Data.DATA15,
				Data.MIMETYPE }, null, null, null); // data1存储各个记录的总数据，mimetype存放记录的类型，如电话、email等

		while (cursor3.moveToNext()) {
			byte[] data2 = cursor3.getBlob(cursor3.getColumnIndex("data15"));
			if (cursor3.getString(cursor3.getColumnIndex("mimetype")).equals(
					"vnd.android.cursor.item/photo")) { // 头像
				if (data2 == null || data2.length == 0) {
					tmpImg = BitmapFactory.decodeResource(this.getResources(),
							R.drawable.nfc_contact_add_icon);
				} else {
					tmpImg = BitmapFactory.decodeByteArray(data2, 0,
							data2.length);
				}
				mPhoto.setImageBitmap(tmpImg);
			}
		}
		String str = buf.toString();
		Log.i("Contacts", str);
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	/**
	 * Constructs an intent for image cropping. 调用图片剪辑程序
	 */
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 把快捷方式的数目写到文件中
	 */
	public void writeFile(String fileName, String writestr) throws IOException {
		try {
			FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
			byte[] bytes = writestr.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从文件中读数据
	 */
	public String readFile(String fileName) throws IOException {
		String res = "";
		try {
			FileInputStream fin = openFileInput(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * EXTRA_SHORTCUT_NAME对应的String需要转换下，不然系统不认
	 */
	private String makeShortcutIconTitle(String content) {
		content = content.replace(TAG_CHECKED, "");
		content = content.replace(TAG_UNCHECKED, "");
		return content.length() > SHORTCUT_ICON_TITLE_MAX_LEN ? content
				.substring(0, SHORTCUT_ICON_TITLE_MAX_LEN) : content;
	}

	/**
	 * 删除快捷方式
	 * */
	public void deleteShortCut(Activity activity, String shortcutName) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");
		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
		/** 改成以下方式能够成功删除，估计是删除和创建需要对应才能找到快捷方式并成功删除 **/
		Intent intent = new Intent();
		intent.setClass(activity, activity.getClass());
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		activity.sendBroadcast(shortcut);
	}

	/**
	 * 判断桌面是否已添加快捷方式
	 * 
	 * @param titleName
	 *            快捷方式名称
	 * @return
	 */
	public boolean IfaddShortCut(String name) {
		boolean isInstallShortcut = false;
		final ContentResolver cr = this.getContentResolver();
		final String AUTHORITY = "com.sec.android.app.launcher.settings";
		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/favorites?notify=true");
		Cursor c = cr.query(CONTENT_URI,
				new String[] { "title", "iconResource" }, "title=?",
				new String[] { name }, null);
		if (c != null && c.getCount() > 0) {
			isInstallShortcut = true;
		}
		return isInstallShortcut;
	}

	/**
	 * 发送namecard快捷方式到桌面
	 */
	public void sendToDesktop(Context paramContext, String paramString,
			int paramInt1, int paramInt2, int paramInt3) {
		Log.i("NameCardActivity", String.valueOf(paramInt2));
		Intent localIntent1 = new Intent();
		Intent localIntent2 = new Intent(this, NameCardManageActivity.class);
		localIntent2.setAction(Intent.ACTION_VIEW);
		localIntent2.putExtra(Intent.EXTRA_UID, paramInt2);
		localIntent1.putExtra(Intent.EXTRA_SHORTCUT_INTENT, localIntent2);
		localIntent1.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				makeShortcutIconTitle(paramString));
		if (tmpImg != null)
			localIntent1.putExtra(Intent.EXTRA_SHORTCUT_ICON, tmpImg);
		else
			localIntent1.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					Intent.ShortcutIconResource.fromContext(this,
							R.drawable.nfc_contact_add_icon));
		localIntent1.putExtra("duplicate", true);

		localIntent1.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		paramContext.sendBroadcast(localIntent1);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int id = v.getId();
		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN: {
//			if(id== R.id.namecard_add){
//				nc_img_add.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.nfc_nc_add_press));
//				}else 
			// if(id== R.id.namecard_save){
			// if (namecard_add.getVisibility() == View.VISIBLE)
			// nc_img_save.setBackgroundDrawable(getResources()
			// .getDrawable(R.drawable.nfc_nc_edit_press));
			// else
			// nc_img_save.setBackgroundDrawable(getResources()
			// .getDrawable(R.drawable.nfc_nc_save_press));
			// }else 
//			if(id== R.id.llShareTag){
//				nc_img_swap.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.nfc_nc_swap_press));
//				}else 
//			if(id== R.id.namecard_edit){
//				nc_img_edit.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.nfc_nc_contact_press));
//				}else 
//			if(id== R.id.namecard_send){
//				nc_img_send.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.nfc_nc_send_press));
//				} 
//		}
//			break;
		case MotionEvent.ACTION_UP: {
			if(id== R.id.namecard_add){
//				nc_img_add.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.nfc_nc_add_normal));
				if( _showflag!= 0 )
					mDialog.init(_showflag);
				else
					MyUtil.showMessage(R.string.nfc_nc_no_other,
							NameCardManageActivity.this);
				}else 
			if(id== R.id.namecard_save){
				if (namecard_add.getVisibility() == View.VISIBLE) {
					if (check()) {
						saveDialog();
					}
				} else {
					state = STATE_EDIT;
					nc_txt_ok.setText(R.string.nfc_nc_save);
					edit(true);
				}
				}else 
			if(id== R.id.llShareTag){
//				nc_img_swap.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.nfc_nc_swap_normal));
				if (check()) {
					if (mMyData != null && mMyData.getNameCard() != null) {
						updateNameCard();
						MyData mydata = mMyData;
						mydata.getNameCard().setContactIcon(null);
						getJsonData(mydata);
						showPlanDialog();
					} else
						MyUtil.showMessage(R.string.nfc_nc_need_save,
								NameCardManageActivity.this);
				}
				}else 
			if(id== R.id.namecard_edit){
				if (state == STATE_SHOW) {
					state = STATE_INSERT;
					namecard = null;
					_showflag = (short) 0x002c;// 默认除了传真，网址，职位其他的在编辑界面是都显示的。
					shortcut = 0;// 初始化，桌面是没有对应的快捷方式的。
					edit(true);
				}
//				nc_img_edit.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.nfc_nc_contact_normal));
				getNameCard();
				clearAll();
				}else 
			if(id== R.id.namecard_send){
//				nc_img_send.setBackgroundDrawable(getResources().getDrawable(
//						R.drawable.nfc_nc_send_normal));
				if (check()) {
					boolean flag = IfaddShortCut(name.getText().toString());
					if(flag)
						shortcut = 1;
					else 
						shortcut = 0;
					if (shortcut == 0) {
						shortcut = 1;
						if (state != STATE_SHOW) {
							if (verifyAllData()) {
								nc_txt_ok.setText(R.string.nfc_nc_edit);
								destopCount = mMyData.id;
							}
						} else
							destopCount = mMyData.id;
						sendToDesktop(NameCardManageActivity.this, name
								.getText().toString(), 2, destopCount,
								R.drawable.nfc_app_logo);
						MyUtil.showMessage(R.string.nfc_nc_send_succeed,
								NameCardManageActivity.this);
						updateNameCard();
					} else
						MyUtil.showMessage(R.string.nfc_nc_has_shortcut,
								NameCardManageActivity.this);
				}
				state = STATE_SHOW;
				// else
				// MyUtil.showMessage("发送失败", NameCardManageActivity.this);
				} 
		}
			break;
		}
		return true;
	}

	/**
	 * 打电话功能
	 */
	public void call(String phoneNumber) {
		if (!phoneNumber.equals("")) {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_CALL);
			i.setData(Uri.parse("tel:" + phoneNumber));
			startActivity(i);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id== R.id.nc_call_company){
			call(companyPhone.getText().toString());
			}else 
		if(id== R.id.nc_call_phone){
			call(telphone.getText().toString());
			}else 
		if(id== R.id.nc_call_fax){
			call(fax.getText().toString());
			}else 
		// if(id== R.id.nc_del_phone){
		// lay_tel.setVisibility(View.GONE);
		// _showflag = (short) (_showflag | 0x0080);
		// showOrHideTip();
		// }else 
		// if(id== R.id.nc_del_company){
		// lay_company.setVisibility(View.GONE);
		// _showflag = (short) (_showflag | 0x0040);
		// showOrHideTip();
		// }else 
		if(id== R.id.nc_del_fax){
			lay_fax.setVisibility(View.GONE);
			_showflag = (short) (_showflag | 0x0020);
			showOrHideTip();
			}else 
		if(id== R.id.nc_del_section){
			lay_section.setVisibility(View.GONE);
			_showflag = (short) (_showflag | 0x0010);
			showOrHideTip();
			}else 
		if(id== R.id.nc_del_rank){
			lay_rank.setVisibility(View.GONE);
			_showflag = (short) (_showflag | 0x0008);
			showOrHideTip();
			}else 
		if(id== R.id.nc_del_net){
			lay_net.setVisibility(View.GONE);
			_showflag = (short) (_showflag | 0x0004);
			showOrHideTip();
			}else 
		if(id== R.id.nc_del_adress){
			lay_addr.setVisibility(View.GONE);
			_showflag = (short) (_showflag | 0x0002);
			showOrHideTip();
			}else 
		if(id== R.id.nc_del_email){
			lay_eamil.setVisibility(View.GONE);
			_showflag = (short) (_showflag | 0x0001);
			showOrHideTip();
			} 
	}

	private void showOrHideTip() {
		// 如果电话里面的项都被删除，隐藏提示”电话“
		// if ((((_showflag & 0x0080) >> 7) != 1)
		// | (((_showflag & 0x0040) >> 6) != 1)
		// | (((_showflag & 0x0020) >> 5) != 1)) {
		// nc_txt_phone.setVisibility(View.VISIBLE);
		// } else
		// nc_txt_phone.setVisibility(View.GONE);
		// 如果详细里面的项都被删除了，隐藏提示”详细信息“
		if ((((_showflag & 0x0010) >> 4) != 1)
				| (((_showflag & 0x0008) >> 3) != 1)
				| (((_showflag & 0x0004) >> 2) != 1)
				| (((_showflag & 0x0002) >> 1) != 1)
				| (((_showflag & 0x0001)) != 1)) {
			nc_txt_detail.setVisibility(View.VISIBLE);
		} else
			nc_txt_detail.setVisibility(View.GONE);
	}

	/**
	 * 自定义类，继承制View，加入点击
	 */
	public class PhotoListener implements PhotoEditorView.EditorListener,
			DialogInterface.OnClickListener {
		@Override
		public void onRequest(int request) {
			if (request == PhotoEditorView.REQUEST_PICK_PHOTO) {
				if (mPhoto.hasSetPhoto()) {
					createPhotoDialog().show();
				} else {
					doPickPhotoAction();
				}
			}
		}

		private Dialog createPhotoDialog() {
			Context context = NameCardManageActivity.this;
			final Context dialogContext = new ContextThemeWrapper(context,
					android.R.style.Theme_Light);
			String cancel = "返回";
			String[] choices;
			choices = new String[3];
			choices[0] = getString(R.string.nfc_use_photo_as_primary);
			choices[1] = getString(R.string.nfc_removePicture);
			choices[2] = getString(R.string.nfc_changePicture);
			final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
					android.R.layout.simple_list_item_1, choices);

			final Builder builder = new Builder(
					dialogContext);
			builder.setTitle(R.string.nfc_attachToContact);
			builder.setSingleChoiceItems(adapter, -1, this);
			builder.setNegativeButton(cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}

					});
			return builder.create();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			switch (which) {
			case 0:
				break;// 什么也不做
			case 1:
				// 删除图像
				mPhoto.setPhotoBitmap(null);
				break;
			case 2:
				// 替换图像
				doPickPhotoAction();
				break;
			}
		}

		private void doPickPhotoAction() {
			Context context = NameCardManageActivity.this;
			final Context dialogContext = new ContextThemeWrapper(context,
					android.R.style.Theme_Light);
			String cancel = "返回";
			String[] choices;
			choices = new String[2];
			choices[0] = getString(R.string.nfc_take_photo);
			choices[1] = getString(R.string.nfc_pick_photo);
			final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
					android.R.layout.simple_list_item_1, choices);

			final Builder builder = new Builder(
					dialogContext);
			builder.setTitle(R.string.nfc_attachToContact);
			builder.setSingleChoiceItems(adapter, -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							switch (which) {
							case 0: {
								String status = Environment
										.getExternalStorageState();
								if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
									doTakePhoto();// 用户点击了从照相机获取
								} else {
									showToast("没有SD卡");
								}
								break;

							}
							case 1:
								doPickPhotoFromGallery();// 从相册中去获取
								break;
							}
						}
					});
			builder.setNegativeButton(cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}

					});
			builder.create().show();
		}
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {// 共享文件发送端
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		MyData mydata = mMyData;
		mydata.getNameCard().setContactIcon(null);
		String text = MyUtil.enMydata(gson.toJson(mydata));
		NdefMessage msg = new NdefMessage(
				new NdefRecord[] { MyUtil.createMimeRecord(
						"application/vnd.com.example.android.beam",
						text.getBytes())
				/**
				 * The Android Application Record (AAR) is commented out. When a
				 * device receives a push with an AAR in it, the application
				 * specified in the AAR is guaranteed to run. The AAR overrides
				 * the tag dispatch system. You can add it back in to guarantee
				 * that this activity starts when receiving a beamed message.
				 * For now, this code uses the tag dispatch system.
				 */
				});
		return msg;
	}

	// by yuzhao 2013/6/20 start
	public class finishCurActivityBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Const.finishCurActivity)) {
				intent.setClass(NameCardManageActivity.this,
						NameCardManageActivity.class);
				intent.setAction(Intent.ACTION_DEFAULT);
				finish();
				startActivity(intent);
			}
		}
	}
	// by yuzhao 2013/6/20 end
}