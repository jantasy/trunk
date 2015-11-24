package com.chinatelecom.nfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

import com.chinatelecom.nfc.DB.Dao.InitDBData;
import com.chinatelecom.nfc.DB.Dao.MyDataDao;
import com.chinatelecom.nfc.DB.Pojo.MyData;
import com.chinatelecom.nfc.DB.Provider.MyDataTable;
import com.chinatelecom.nfc.Debug.MyDebug;
import com.chinatelecom.nfc.Main.MainListView;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MySharedPreferences;
import com.chinatelecom.nfc.Utils.MyUtil;
import com.chinatelecom.nfc.View.MenuHorizontalScrollView;
import com.chinatelecom.nfc.View.MySimpleAdpter;

public class MainGridview extends BaseNfcAdapter implements
		CreateNdefMessageCallback {
	// ashley 0922 削减页面功能：名片管理 读公交卡 情景设置（静音啥的） 访问网址 文件分享
	private int[] menuIcon = new int[] { R.drawable.nfc_icon_card,
			R.drawable.nfc_icon_bus, R.drawable.nfc_icon_meeting,
			R.drawable.nfc_web, R.drawable.nfc_icon_share };
	// private int[] menuIcon = new int[]{
	// R.drawable.nfc_icon_card,R.drawable.nfc_icon_bus,R.drawable.nfc_icon_meeting,
	// R.drawable.nfc_icon_sale,R.drawable.nfc_icon_lottery,R.drawable.nfc_icon_read,
	// R.drawable.nfc_icon_share,R.drawable.nfc_icon_write,R.drawable.nfc_icon_tiket_wait
	// };
	/**
	 * 9宫格名称
	 */
	private String[] menuName;
	private String[] menuTel = new String[] { "", "", "", "", "", "", "", "",
			"", };
	private String[] menuPerson = new String[] { "", "", "", "", "", "", "",
			"", "", };
	
	private static final String[] ALIAS_TAG = new String[]{".BusinessCardManager",".ReadBusCard",".Meeting",".FileShare"};
	
	private AlertDialog alertDialog;
	

	@Override
	protected void onResume() {
		super.onResume();
		List<MyData> mydatas = MyDataDao.query(MainGridview.this, null,
				String.valueOf(MyDataTable.NAMECARD), null, "1", null, true);
		if ((mydatas != null) && (mydatas.size() > 0)
				&& mydatas.get(0).getNameCard() != null) {
			menuTel[0] = mydatas.get(0).getNameCard().getTelPhone();
			menuPerson[0] = mydatas.get(0).getNameCard().getName();
			menuIcon[0] = R.drawable.nfc_icon_card_auto;
			saImageItems.setListData(getDatas());
			saImageItems.notifyDataSetChanged();
		} else {
			menuTel[0] = "";
			menuPerson[0] = "";
			menuIcon[0] = R.drawable.nfc_icon_card;
			saImageItems.setListData(getDatas());
			saImageItems.notifyDataSetChanged();
		}

		if (mMainListView != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(MyDataTable.TAG_READ_FAVORITE).append(",")
					.append(MyDataTable.TAG_READFROMNFC);
			mMainListView.update(null, sb.toString());
		}
	}

	private MenuHorizontalScrollView scrollView;
	private Button menuBtn;
	private View mainGridView;
	private LayoutInflater inflater;
	private GridView gridview;
	private View[] children;
	private ListView menuList;
	private MainListView mMainListView;
	private Button btnDelete;

	private MySimpleAdpter saImageItems;
	private int lastMotionX;
	private int current;
	private int scrollToViewPos;
	ViewGroup tabcontent;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_history);
		if (MyUtil.externalStorageState()) {
			MyUtil.makeDir(Constant.NFCFORCHINATELECOM_DIR);

		}
		inflater = LayoutInflater.from(this);
		menuName = getResources().getStringArray(R.array.nfc_tag_title);

		if (MySharedPreferences.getApp(this).getBoolean(
				MySharedPreferences.sp_app_firsttime, true) == true) {
			InitDBData.initDB(this);
			MySharedPreferences.saveApp(this, false);
			MyUtil.initLotteryDeviceID(this);

			// ashley 0922 不显示引导页
			// // this is the guide when customer uses this app
			// Intent guide = new Intent();
			// guide.setClass(MainGridview.this, NfcGuideViewActivity.class);
			// startActivity(guide);
		}

		this.scrollView = (MenuHorizontalScrollView) findViewById(R.id.mScrollView);
		StringBuilder sb = new StringBuilder();
		sb.append(MyDataTable.TAG_READ_FAVORITE).append(",")
				.append(MyDataTable.TAG_READFROMNFC);
		mMainListView = new MainListView(this, null, sb.toString());
		this.menuList = mMainListView.getListViewView();

		mainGridView = inflater.inflate(R.layout.nfc_main_gridview, null);
		gridview = (GridView) mainGridView.findViewById(R.id.gridview);
		btnDelete = (Button) findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogDelete();
			}
		});
		menuBtn = (Button) this.mainGridView.findViewById(R.id.menuBtn);
		menuBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				scrollView.clickMenuBtn();
			}
		});

		// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
		saImageItems = new MySimpleAdpter(this, // 没什么解释
				getDatas(),// 数据来源
				R.layout.nfc_main_gridview_item,// night_item的XML实现

				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemText", "ItemTel", "ItemName" },

				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemText, R.id.ItemTel,
						R.id.ItemName });

		// 添加并且显示
		gridview.setAdapter(saImageItems);
		// 添加消息处理
		gridview.setOnItemClickListener(new ItemClickListener());

		View leftView = new View(this);
		// 设置右边背景透明
		leftView.setBackgroundColor(Color.TRANSPARENT);
		// 实例化一个View数组，放置了一个view和布局
		children = new View[] { leftView, mainGridView };
		// this.scrollView.initViews(children, new SizeCallBackForMenu(
		// this.menuBtn), this.menuList);
		tabcontent = (ViewGroup) scrollView.findViewById(R.id.top);
		tabcontent.removeAllViews();
		tabcontent.addView(mainGridView);
		this.scrollView.setMenuBtn(this.menuBtn);
		if (!registeNFCNdef() && !MyDebug.DEBUG_NO_NFC_PHONE) {
			exitDialog();
		}
		
		for (int i = 0; i < ALIAS_TAG.length; i++) {
			if(ALIAS_TAG[i].equals(getIntent().getComponent().getShortClassName())){
				launchTools(i);
				finish();
			}
		}

	}

	private void launchTools(int position) {

		Intent intent = new Intent();
		StringBuilder readOrWrite = new StringBuilder();
		String tagTitle[] = getResources().getStringArray(
				R.array.nfc_new_tag_title);
		switch (position) {
		case 0:
			// 1名片管理
			// class
			intent.setClass(MainGridview.this, CardManagerActivity.class);
			// dataType
			intent.putExtra(Constant.MYDATA_DATATYPE,
					String.valueOf(MyDataTable.NAMECARD));
			// title
			intent.putExtra("title", menuName[position]);
			// readOrWrite
			readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",")
					.append(MyDataTable.TAG_READ_FAVORITE).append(",")
					.append(MyDataTable.TAG_MY_NAMECARD);
			intent.putExtra(Constant.MYDATA_READORWRITE, readOrWrite.toString());
			startActivity(intent);
			break;
		case 1:
			// 2读公交卡
			// class
			intent.setClass(MainGridview.this, MainActivity.class);
			// dataType
			intent.putExtra(Constant.MYDATA_DATATYPE,
					String.valueOf(MyDataTable.BUS_CARD));
			// title
			intent.putExtra("title", menuName[position]);
			// readOrWrite
			readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",")
					.append(MyDataTable.TAG_READ_FAVORITE).append(",")
					.append(MyDataTable.TAG_MY_NAMECARD);
			intent.putExtra(Constant.MYDATA_READORWRITE, readOrWrite.toString());
			startActivity(intent);
			break;
		case 2:
			// 3情景设置-写
			// class
			intent.setClass(MainGridview.this, MainActivity.class);
			// dataType
			intent.putExtra(Constant.MYDATA_DATATYPE,
					String.valueOf(MyDataTable.MEETTING));
			// title
			intent.putExtra(Constant.TITLE, menuName[position]);
			// readOrWrite
			readOrWrite.append(MyDataTable.TAG_WRITETAG).append(",")
					.append(MyDataTable.TAG_WRITE_FAVORITE);
			intent.putExtra(Constant.MYDATA_READORWRITE, readOrWrite.toString());
			startActivity(intent);
			break;
//		case 3:
//			// 4访问网址-写
//			// class
//			intent.setClass(MainGridview.this, MainActivity.class);
//			// dataType
//			intent.putExtra(Constant.MYDATA_DATATYPE,
//					String.valueOf(MyDataTable.WEB));
//			// title
//			intent.putExtra(Constant.TITLE, tagTitle[6]);
//			// readOrWrite
//			readOrWrite.append(MyDataTable.TAG_WRITETAG).append(",")
//					.append(MyDataTable.TAG_WRITE_FAVORITE);
//			intent.putExtra(Constant.MYDATA_READORWRITE, readOrWrite.toString());
//			startActivity(intent);
//			break;
		case 3:
			// 5文件共享
			// class
			intent.setClass(MainGridview.this, ShareFilesMain.class);
			// dataType(none)
			// title
			intent.putExtra("title", menuName[position]);
			// readOrWrite
			readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",")
					.append(MyDataTable.TAG_READ_FAVORITE).append(",")
					.append(MyDataTable.TAG_MY_NAMECARD);
			intent.putExtra(Constant.MYDATA_READORWRITE, readOrWrite.toString());
			startActivity(intent);
			break;
		}
	}

	// 当AdapterView被单击(触摸屏或者键盘)，则返回的Item单击事件
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View view,// The view within the AdapterView that was clicked
				int position,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			// 在本例中arg2=arg3
			HashMap<String, Object> item = (HashMap<String, Object>) arg0
					.getItemAtPosition(position);
			// ashley 0922 削减页面功能：名片管理 读公交卡 情景设置（静音啥的） 访问网址 文件分享
			// 显示所选Item的ItemText
			Intent intent = new Intent();
			StringBuilder readOrWrite = new StringBuilder();
			// intent.putExtra("title", menuName[position]);
			// readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",").append(MyDataTable.TAG_READ_FAVORITE).append(",").append(MyDataTable.TAG_MY_NAMECARD);
			// intent.putExtra(Constant.MYDATA_READORWRITE,
			// readOrWrite.toString());
			// StringBuilder sb = new StringBuilder();
			String tagTitle[] = getResources().getStringArray(
					R.array.nfc_new_tag_title);
			switch (position) {
			case 0:
				// 1名片管理
				// class
				intent.setClass(MainGridview.this, MainActivity.class);
				// dataType
				intent.putExtra(Constant.MYDATA_DATATYPE,
						String.valueOf(MyDataTable.NAMECARD));
				// title
				intent.putExtra("title", menuName[position]);
				// readOrWrite
				readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",")
						.append(MyDataTable.TAG_READ_FAVORITE).append(",")
						.append(MyDataTable.TAG_MY_NAMECARD);
				intent.putExtra(Constant.MYDATA_READORWRITE,
						readOrWrite.toString());
				startActivity(intent);
				break;
			case 1:
				// 2读公交卡
				// class
				intent.setClass(MainGridview.this, MainActivity.class);
				// dataType
				intent.putExtra(Constant.MYDATA_DATATYPE,
						String.valueOf(MyDataTable.BUS_CARD));
				// title
				intent.putExtra("title", menuName[position]);
				// readOrWrite
				readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",")
						.append(MyDataTable.TAG_READ_FAVORITE).append(",")
						.append(MyDataTable.TAG_MY_NAMECARD);
				intent.putExtra(Constant.MYDATA_READORWRITE,
						readOrWrite.toString());
				startActivity(intent);
				break;
			case 2:
				// 3情景设置-写
				// class
				intent.setClass(MainGridview.this, MainActivity.class);
				// dataType
				intent.putExtra(Constant.MYDATA_DATATYPE,
						String.valueOf(MyDataTable.MEETTING));
				// title
				intent.putExtra(Constant.TITLE, menuName[position]);
				// readOrWrite
				readOrWrite.append(MyDataTable.TAG_WRITETAG).append(",")
						.append(MyDataTable.TAG_WRITE_FAVORITE);
				intent.putExtra(Constant.MYDATA_READORWRITE,
						readOrWrite.toString());
				startActivity(intent);
				break;
			case 3:
				// 4访问网址-写
				// class
				intent.setClass(MainGridview.this, MainActivity.class);
				// dataType
				intent.putExtra(Constant.MYDATA_DATATYPE,
						String.valueOf(MyDataTable.WEB));
				// title
				intent.putExtra(Constant.TITLE, menuName[position]);
				// readOrWrite
				readOrWrite.append(MyDataTable.TAG_WRITETAG).append(",")
						.append(MyDataTable.TAG_WRITE_FAVORITE);
				intent.putExtra(Constant.MYDATA_READORWRITE,
						readOrWrite.toString());
				startActivity(intent);
				break;
			case 4:
				// 5文件共享
				// class
				intent.setClass(MainGridview.this, ShareFilesMain.class);
				// dataType(none)
				// title
				intent.putExtra("title", menuName[position]);
				// readOrWrite
				readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",")
						.append(MyDataTable.TAG_READ_FAVORITE).append(",")
						.append(MyDataTable.TAG_MY_NAMECARD);
				intent.putExtra(Constant.MYDATA_READORWRITE,
						readOrWrite.toString());
				startActivity(intent);
				break;
			default:
				break;
			}
			// switch (position) {
			// case 0:
			// intent.setClass(MainGridview.this, MainActivity.class);
			// intent.putExtra(Constant.MYDATA_DATATYPE,
			// String.valueOf(MyDataTable.NAMECARD));
			//
			// startActivity(intent);
			// break;
			// case 1:
			//
			// intent.setClass(MainGridview.this, MainActivity.class);
			// intent.putExtra(Constant.MYDATA_DATATYPE,
			// String.valueOf(MyDataTable.BUS_CARD));
			// startActivity(intent);
			//
			// break;
			//
			// case 2:
			// intent.setClass(MainGridview.this, MainActivity.class);
			// intent.putExtra(Constant.MYDATA_DATATYPE,
			// String.valueOf(MyDataTable.MEETTING));
			// startActivity(intent);
			//
			// break;
			// case 3:
			//
			// intent.setClass(MainGridview.this, MainActivity.class);
			// intent.putExtra(Constant.MYDATA_DATATYPE,
			// String.valueOf(MyDataTable.COUPON));
			// startActivity(intent);
			//
			// break;
			// case 4:
			// intent.setClass(MainGridview.this, MainActivity.class);
			// intent.putExtra(Constant.MYDATA_DATATYPE,
			// String.valueOf(MyDataTable.LOTTERY));
			// startActivity(intent);
			//
			// break;
			// case 5:
			// intent.setClass(MainGridview.this, MainActivity.class);
			// sb.append(MyDataTable.WEB).append(",").append(MyDataTable.TEXT).append(",").append(MyDataTable.AD);
			// intent.putExtra(Constant.MYDATA_DATATYPE,sb.toString());
			// startActivity(intent);
			//
			// break;
			// case 6:
			// intent.setClass(MainGridview.this, ShareFilesMain.class);
			// startActivity(intent);
			// break;
			// case 7:
			// intent.setClass(MainGridview.this, SettingActivity.class);
			// startActivity(intent);
			//
			// break;
			//
			// case 8:
			// intent.setClass(MainGridview.this, MainActivity.class);
			// //
			// sb.append(MyDataTable.MOVIETICKET).append(",").append(MyDataTable.PARKTICKET);
			// sb.append(MyDataTable.MOVIETICKET);
			// intent.putExtra(Constant.MYDATA_DATATYPE, sb.toString());
			// intent.putExtra("fromMainGridview", "fromMainGridview");
			// startActivity(intent);
			//
			// break;
			// default:
			// break;
			// }

		}

	}

	private ArrayList<HashMap<String, Object>> getDatas() {
		// 生成动态数组，并且转入数据
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < menuName.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			// map.put("ItemImage", R.drawable.nfc_ic_launcher);//添加图像资源的ID
			map.put("ItemImage", menuIcon[i]);// 添加图像资源的ID
			map.put("ItemText", menuName[i]);// 按序号做ItemText
			if (i == 0) {
				map.put("ItemTel", menuTel[i]);// 按序号做ItemText
				map.put("ItemName", menuPerson[i]);// 按序号做ItemText
			}
			lstImageItem.add(map);
		}

		return lstImageItem;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (MenuHorizontalScrollView.menuOut == true)
				// 滑动出抽屉
				this.scrollView.clickMenuBtn();
			else
				dialog();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	protected void dialog() {
		Builder builder = new Builder(this);
		builder.setMessage(R.string.nfc_confirm_exit);
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
						dialog.dismiss();
						MainGridview.this.finish();
					}
				});
		builder.create().show();
	}

	protected void dialogDelete() {
		Builder builder = new Builder(this);
		builder.setMessage(R.string.nfc_confirm_delete_all);
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
						mMainListView.delete();
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		System.exit(0);
		if(alertDialog!=null && alertDialog.isShowing()){
			alertDialog.dismiss();
		}
		super.onDestroy();
		
	}

	public MenuHorizontalScrollView getScrollView() {
		return scrollView;
	}

	public void setScrollView(MenuHorizontalScrollView scrollView) {
		this.scrollView = scrollView;
	}

	private boolean registeNFCNdef() {
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter != null) {
			nfcAdapter.setNdefPushMessageCallback(this, this);
			return true;
		}
		return false;
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		// TODO Auto-generated method stub
		String mDeviceId = MyUtil.getDeviceId(MainGridview.this);

		NdefMessage msg = new NdefMessage(
				new NdefRecord[] { MyUtil.createMimeRecord(getResources()
						.getString(R.string.nfc_lottery_beam), MyUtil
						.enDiviceID(mDeviceId).getBytes()) });
		return msg;
	}

	private void exitDialog() {

		alertDialog=new Builder(this)
		.setMessage(R.string.nfc_msg_no_support_device)
		.setTitle(R.string.nfc_title_prompt)
		.setPositiveButton(R.string.nfc_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						MainGridview.this.finish();
					}
		})
		.create();
		 

		alertDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				MainGridview.this.finish();
			}
		});
		alertDialog.show();
	}
}
