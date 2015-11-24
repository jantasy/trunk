package com.telecompp.util;

import com.telecom.pp.extend.R;
import com.telecompp.ui.MyDialog;
import com.telecompp.ui.MyDialog.MyDialogListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 提供各种dialog的使用
 * @author poet
 *
 */
public class DialogUtil {
	private DialogUtil(){};
	public static final int DIALOG_PROGRESS = 0; 
	public static final int DIALOG_ALERT = 1;
	public static Dialog getDialog(Activity context,int type){
		switch (type) {
		case DIALOG_PROGRESS:
			//返回进度条对话框
			Dialog dialog =new WaitProgressDialog(context,R.style.MyDialog);
			dialog.setCancelable(false);
			return dialog;
		default:
			break;
		}
		return null;
	}
	
	public static Dialog getDialog(Context context,int type, String msg){
		switch (type) {
		case DIALOG_PROGRESS:
			//返回进度条对话框
			Dialog dialog =new WaitProgressDialog(context,R.style.MyDialog, msg);
			dialog.setCancelable(false);
			return dialog;
		default:
			break;
		}
		return null;
	}
	
	public static MyDialog getMyDialog(String title,String msg,Activity activity, MyDialogListener listener,int type){
		MyDialog mdialog = new MyDialog(activity, R.style.MyDialog, listener, type);
		mdialog.setTitleTxt(title);
		mdialog.MyDialogSetMsg(msg);
		return mdialog;
	}
}
class WaitProgressDialog extends Dialog{
	
	private String msg = null;

	public WaitProgressDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public WaitProgressDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public WaitProgressDialog(Context context, int theme, String msg) {
		super(context, theme);
		this.msg = msg;
	}

	public WaitProgressDialog(Context context) {
		super(context);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_progress_dialog);
		if(msg != null && !"".equals(msg)) {
			TextView tv = (TextView)findViewById(R.id.tv_progress);
			tv.setText(msg);
		}
		
	}
}
