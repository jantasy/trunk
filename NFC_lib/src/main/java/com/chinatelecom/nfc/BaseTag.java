package com.chinatelecom.nfc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.chinatelecom.nfc.DB.Dao.MyDataDao;
import com.chinatelecom.nfc.DB.Pojo.MyData;
import com.chinatelecom.nfc.DB.Provider.MyDataTable;
import com.chinatelecom.nfc.Debug.MyDebug;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.callback.ITagCommon;

public class BaseTag extends BaseNfcAdapter implements ITagCommon{
	private final String TAG = "BaseTag";
	
	protected Integer mydataId;
	protected Integer dataType;
	protected Integer tableID;
	protected Integer type;
	protected Integer intentType;
	protected boolean readFromNfc = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(MyDebug.LIFTCYCLE)
		Log.e(TAG, "onCreate");
		
		Intent data = getIntent();
		mydataId = data.getIntExtra(Constant.MYDATA_ID, 1);
		dataType = data.getIntExtra(Constant.MYDATA_DATATYPE, 1);
		tableID = data.getIntExtra(Constant.MYDATA_TABLEID, 1);
		type = data.getIntExtra("type", 1);
		intentType = data.getIntExtra(Constant.INTENT_TYPE, Constant.INTENT_TYPE_MAKE_TAG);
		readFromNfc = data.getBooleanExtra(Constant.INTENT_FORM_NFC, false);
	}
	
	protected MyData getMyData(){
		 
		return MyDataDao.query(this,mydataId, dataType);
	}
	protected MyData getMyData(Integer mydataType){
		
		return MyDataDao.query(this,mydataId, mydataType);
	}

	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void edit(boolean isEdit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected void setImageRes(ImageView view,boolean edit) {
		if (edit) {
			view.setImageResource(R.drawable.nfc_nc_save);
		}else{
			view.setImageResource(R.drawable.nfc_nc_edit);
		}
	}
	
	/**
	 * 是否显示底部按钮
	 */
	protected void needShowBottomBar() {
		if(intentType == Constant.INTENT_TYPE_MAKE_TAG){
			findViewById(R.id.llBottom).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.llBottom).setVisibility(View.GONE);
		}
	}
	private String getTitleStr(){
		String[] menuName ;
		String title = null;
		menuName = getResources().getStringArray(R.array.nfc_tag_title);
		switch(dataType){
		case MyDataTable.NAMECARD:
			title = menuName[0];
			break;
		case MyDataTable.BUS_CARD:
			title = menuName[1];
			break;
		case MyDataTable.MEETTING:
			title = menuName[2];
			break;
		//ashley 0923 删除无用资源
//		case MyDataTable.COUPON:
//			title = menuName[3];
//			break;
//		case MyDataTable.LOTTERY:
//			title = menuName[4];
//			break;
		case MyDataTable.TEXT:
		case MyDataTable.WEB:
		//ashley 0923 删除无用资源
//		case MyDataTable.AD:
			title = menuName[3];
			break;
		//ashley 0923 删除无用资源
//		case MyDataTable.MOVIETICKET:
//		case MyDataTable.PARKTICKET:
//			title = menuName[8];
//			break;
		}
		
		return title;
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
//	     if (keyCode == KeyEvent.KEYCODE_BACK) {
//	    	 if((intentType != Constant.INTENT_TYPE_MAKE_TAG)&&readFromNfc){
//	    		Intent intent = new Intent();
//				intent.setClass(this, MainActivity.class);
//				StringBuilder readOrWrite = new StringBuilder();
//				readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",").append(MyDataTable.TAG_READ_FAVORITE).append(",").append(MyDataTable.TAG_MY_NAMECARD);
//				intent.putExtra(Constant.MYDATA_READORWRITE, readOrWrite.toString());
//				intent.putExtra(Constant.MYDATA_DATATYPE,  String.valueOf(dataType));
//				intent.putExtra("title", getTitleStr());
//				finish();
//				startActivity(intent);
//	    	 }
//	     }  
	     return super.onKeyDown(keyCode, event);
	 } 
}
