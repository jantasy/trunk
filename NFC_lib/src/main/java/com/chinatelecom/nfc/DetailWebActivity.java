package com.chinatelecom.nfc;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinatelecom.nfc.DB.Dao.MyDataDao;
import com.chinatelecom.nfc.DB.Pojo.MyData;
import com.chinatelecom.nfc.DB.Provider.MyDataTable;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MyUtil;

public class DetailWebActivity extends BaseTag{

	
	private EditText title;
	private EditText etUrl;
	private Button btnGo;
	
	/**
	 *  false:查看页面<br>
	 * true:新建页面
	 */
	private boolean isEditOrSelect = true;
	private boolean isEdit = false;
	private LinearLayout llEditOrSelect;
	private LinearLayout llShareTag;
	private ImageView ivEditOrSelect;
	private TextView tvEditOrSelect;
	private Context context;
	private boolean isFromNfcCardBackPage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_detail_web);
		
		mMyData = getMyData();
		init();
		if (mMyData != null  && mMyData.dt == MyDataTable.WEB) {
			showView();
			edit(false);
			//查看页面
			isEditOrSelect = false;
		}
		if(isEditOrSelect){
			//进入的是新建页面
			isEdit = true;
			edit(true);
			isEditOrSelect = true;
		}
		
		if (intentType == Constant.INTENT_TYPE_MAKE_TAG) {
			llShareTag.setVisibility(View.VISIBLE);
			llEditOrSelect.setVisibility(View.VISIBLE);
		}else{
			llShareTag.setVisibility(View.GONE);
			llEditOrSelect.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		title = (EditText) findViewById(R.id.title);
		title.setText(R.string.nfc_web_title);
		etUrl = (EditText) findViewById(R.id.etUrl);
		btnGo = (Button) findViewById(R.id.btnGo);
		btnGo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = etUrl.getText().toString().trim();
				if(!MyUtil.checkURL(url.toLowerCase())){
					String urlHead = getResources().getString(R.string.nfc_web_url_head);
					etUrl.setText(urlHead+url.toLowerCase());
				}
				url = etUrl.getText().toString().trim();
				if(!TextUtils.isEmpty(url)){
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
				}
			}
		});
		ivEditOrSelect = (ImageView) findViewById(R.id.ivEditOrSelect);
		tvEditOrSelect = (TextView) findViewById(R.id.tvEditOrSelect);
		llShareTag = (LinearLayout) findViewById(R.id.llShareTag);
		llEditOrSelect = (LinearLayout) findViewById(R.id.llEditOrSelect);
		llEditOrSelect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isEdit){
					if(check()){
						dialog();
					}
				}else{
					tvEditOrSelect.setText(R.string.nfc_save);
					edit(!isEdit);
					isEdit = !isEdit;
				}
			}
		});
		
		findViewById(R.id.llShareTag).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(title.isEnabled()){
					MyUtil.showMessage(R.string.nfc_msg_save_first, context);
				}else{
					getJsonData(mMyData);
					showPlanDialog();
				}
			}
		});
		
	}

	@Override
	public void showView() {
		// TODO Auto-generated method stub
		etUrl.setText(mMyData.n);
		
		// by WuNan 2013/08/06，如果intentType是1，那么是刷NFC卡片，进入到该页面，直接跳转到该网址
		if(intentType == 1){
//			finish();
			String url = etUrl.getText().toString().trim();
			if(!MyUtil.checkURL(url.toLowerCase())){
				String urlHead = getResources().getString(R.string.nfc_web_url_head);
				etUrl.setText(urlHead+url.toLowerCase());
			}
			url = etUrl.getText().toString().trim();
			if(!TextUtils.isEmpty(url)){
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intent);
			}
			isFromNfcCardBackPage = true;
		}
		
	}

	@Override
	public void edit(boolean isEdit) {
		// TODO Auto-generated method stub
		title.setEnabled(false);
		
		etUrl.setEnabled(isEdit);
		etUrl.setSelected(isEdit);
		setImageRes(ivEditOrSelect,isEdit);
		
		if(isEdit){
			tvEditOrSelect.setText(R.string.nfc_save);
		}else{
			tvEditOrSelect.setText(R.string.nfc_edit);
		}
		
	}
	
	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		String url = etUrl.getText().toString().trim();
		if(TextUtils.isEmpty(url)){
			etUrl.requestFocus();
			MyUtil.showMessage(R.string.nfc_msg_null_web_url, this);
			return false;
		}
		if(!MyUtil.checkURL(url.toLowerCase())){
			String urlHead = getResources().getString(R.string.nfc_web_url_head);
			etUrl.setText(urlHead+url.toLowerCase());
		}
		
		return true;
	}
	
	protected void dialog() {
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
						
							edit(!isEdit);
							isEdit = !isEdit;
							
							if(isEditOrSelect){
								MyData data = new MyData(null, etUrl.getText().toString(), 
										MyDataTable.WEB, -1, 1l, MyDataTable.TAG_WRITETAG);
								//数据库操作：插入，查出
								long id = MyDataDao.insert(context,data,true);
								mMyData = MyDataDao.query(context, MyUtil.longToInteger(id), MyDataTable.MEETTING);
								if(mMyData != null){
									mydataId = mMyData.id;
								}
								isEditOrSelect = false;
							}else{
//								MyData dada = new MyData(mMyData.id, etContent.getText().toString(), 
//								MyDataTable.TEXT, -1, 1l, mMyData.readOrwrite,mMyData.uuid);
								//数据库操作：更新
//								mMyData.id = mydataId;
								mMyData.n = etUrl.getText().toString();
								MyDataDao.update(context, mMyData);
							}
							tvEditOrSelect.setText(R.string.nfc_edit);
						dialog.dismiss();
				}});
		builder.create().show();
	}
	
	
	
}
