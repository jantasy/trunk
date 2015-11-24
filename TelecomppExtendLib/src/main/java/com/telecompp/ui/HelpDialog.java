package com.telecompp.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.telecom.pp.extend.R;
import com.telecompp.util.SharedHelp;
import com.telecompp.util.SumaConstants;

public class HelpDialog extends Dialog implements
		View.OnClickListener, SumaConstants {
	public static final int ButtonCancle = 0;// 显示取消按钮
	public static final int ButtonConfirm = 1;// 显示确定按钮
	public static final int ButtonBoth = 2;// 显示全部按钮
	public static final int ButtonNone = 3;// 不显示任何的按钮
	View view = null;
	Button btn_confirm; 
	Button btn_cancel;
	int buttonNums = 2;
	TextView theView;
	TextView tx_title;
	Activity activity;
	boolean visiable = true;
	MyDialogListener listener;
	String g_Msg = "";
	String title = "";
	String posiBtnMsg = "";
	String negaBtnMsg = "";
	ViewGroup container;
	CheckBox cb_helpdialog_nomore;

	public HelpDialog(Activity activity) {
		super(activity);
		this.activity = activity;
	}

	public HelpDialog(Activity activity, int theme, MyDialogListener listener,
			int btnNums) {
		super(activity, theme);
		this.activity = activity;
		this.listener = listener;
		buttonNums = btnNums;
	}

	public void setTitleTxt(String msg) {
		title = msg;
	}

	public void MyDialogSetMsg(String msg) {
		g_Msg = msg;
	}

	public void setPosiBtnMsg(String posiMsg) {
		posiBtnMsg = posiMsg;
	}

	public void setNegaBtnMsg(String negaMsg) {
		negaBtnMsg = negaMsg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helpdialog);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		container = (ViewGroup) findViewById(R.id.rl_container);
		
		cb_helpdialog_nomore = (CheckBox)findViewById(R.id.cb_helpdialog_nomore);
		
		cb_helpdialog_nomore.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked) {
					SharedHelp.setIsHelpNeed(activity, false); 
				} else {
					SharedHelp.setIsHelpNeed(activity, true); 					
				}
			}
			
		});
		
		if (buttonNums == ButtonConfirm) {
			btn_cancel.setVisibility(View.GONE);
			btn_confirm
					.setBackgroundResource(R.drawable.common_res_alertdialog_full_btn_selector_yjt);
		} else if (buttonNums == ButtonCancle) {
			btn_confirm.setVisibility(View.GONE);
			btn_cancel
					.setBackgroundResource(R.drawable.common_res_alertdialog_full_btn_selector_yjt);
		} else if (buttonNums == ButtonNone) {
			btn_confirm.setVisibility(View.GONE);
			btn_cancel
					.setBackgroundResource(R.drawable.common_res_base_alertdialog_full_btn_bg_norrmal_yjt);
		}
		if (view != null) {
			container.removeAllViews();
			android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
					android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
					android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
			container.addView(view, params);
		}
		theView = (TextView) findViewById(R.id.textViewDialog);
		tx_title = (TextView) findViewById(R.id.txt_str_prompt);
	}

	@Override
	protected void onStart() {
		if (!visiable) {
			theView.setVisibility(View.GONE);
		}
		if (g_Msg != null && !g_Msg.equals("")) {
			theView.setText(g_Msg);
		}
		if (title != null && !title.equals("")) {
			tx_title.setText(title);
		}
		if (!posiBtnMsg.equals("")) {
			btn_confirm.setText(posiBtnMsg);
		}
		if (!negaBtnMsg.equals("")) {
			btn_cancel.setText(negaBtnMsg);
		}
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		if (listener != null) {
			if (v.getId() == R.id.btn_confirm) {
				listener.onPositiveClick(this, v);
			} else if (v.getId() == R.id.btn_cancel) {
				listener.onNegativeClick(this, v);
			}
		}
		if (isShowing()) {
			dismiss();
		}
	}

	public void setMsgViewVisiable(boolean visiable) {
		this.visiable = visiable;
	}

	public interface MyDialogListener {
		public void onPositiveClick(Dialog dialog, View view);

		public void onNegativeClick(Dialog dialog, View view);
	}

	@Override
	public void show() {
		if (activity != null && !activity.isFinishing()) {
			super.show();
		}
	}

	public void setView(View view) {
		this.view = view;
	}
}
