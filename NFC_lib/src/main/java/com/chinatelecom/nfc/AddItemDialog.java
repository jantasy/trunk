package com.chinatelecom.nfc;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

class AddItemDialog extends AlertDialog {
	String[] itemName = { "手机", "公司", "传真", "部门", "职位", "网址", "地址","邮箱"};
//	String[] itemName = { "传真", "部门", "职位", "网址", "地址","邮箱"};
	List<String> showItem = new ArrayList<String>();
//	String[] showItem = new String[]{};
	short showFlag;
	private IsWhitchClick mListener;
	static Builder addItemDialog = null;
	Context mcontext;
	protected AddItemDialog(Context context) {
		super(context);
		mcontext = context;
	}
	
	/*
	 * 	初始化对话框。
	 */
	public void init( short flag ){	
		showFlag = flag;
		initShowItem();
		addItemDialog = new Builder(mcontext);
		addItemDialog.setTitle("添加其他项目");
//		if(addItemDialog.equals(null))
		ArrayAdapter<String> adaptet = new ArrayAdapter<String>(mcontext, android.R.layout.select_dialog_item,android.R.id.text1,showItem);
		addItemDialog.setAdapter(adaptet, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mListener != null) {
					mListener.whichIsClick(getWhitchClick(which));
					dismiss();
				}
			}
		});
		addItemDialog.show();
	}
	/*
	 * 获得对话框点击的Item在itemName中的位置。
	 */
	private int getWhitchClick(int whitch){
		int num = -1;
		for(int i=0; i < itemName.length; i++){
			if(itemName[i].equals(showItem.get(whitch)))
				num = i;
		}
		return num;
	}
	/*
	 *声明一个借口
	 */
	public interface IsWhitchClick {
		public void whichIsClick(int ClickId);
	}
	/*
	 * 初始化借口。
	 */
	public void setIsWhitchClick(IsWhitchClick listener) {
		mListener = listener;
	}
	/*
	 *初始化显示list 
	 */
	List<String> initShowItem() {
		showItem.clear();
//		if (((showFlag & 0x0080)>>7) == 1) {
//			showItem.add(itemName[0]);
//		}
//		if(((showFlag & 0x0040)>>6) == 1 ){
//			showItem.add(itemName[1]);
//		}
		if(((showFlag & 0x0020)>>5) == 1 ){
			showItem.add(itemName[2]);
		}
		if(((showFlag & 0x0010)>>4) == 1 ){
			showItem.add(itemName[3]);
		}
		if(((showFlag & 0x0008)>>3) == 1 ){
			showItem.add(itemName[4]);
		}
		if(((showFlag & 0x0004)>>2) == 1 ){
			showItem.add(itemName[5]);
		}
		if(((showFlag & 0x0002) >>1)== 1 ){
			showItem.add(itemName[6]);
		}
		if(((showFlag & 0x0001))== 1 ){
			showItem.add(itemName[7]);
		}
		return showItem;
	}
}