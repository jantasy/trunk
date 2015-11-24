package cn.yjt.oa.app.contactlist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.ContactlistFragment.DialogState;

/**选择同事的界面*/
public class ContactlistActivity extends TitleFragmentActivity implements DialogState {
   
	
	private ContactlistFragment fragment ;
    public static Activity activity;
    
	public static final int REQUEST_CODE_CLICK = 1;
	public static final int REQUEST_CODE_INPUT = 2;
	public static final String CONTACTLIST_MULITCHOICE_RESULT = "contact_list_mulitchoice_result";
	public static final String CONTACTLIST_MULITCHOICE_GROUP_RESULT = "contact_list_mulitchoice_group_result";
	public static final String EXTRA_EXIST_CONTACTS = "extra_exist_contacts";
    private boolean isContactLoadDialogShow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setSoftInputModes();
		super.onCreate(savedInstanceState);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setImageResource(R.drawable.contact_list_save);
		setContentView(R.layout.activity_fragment_container);
		if (fragment == null) {
			fragment =  new ContactlistFragment();
		}
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add( R.id.container,fragment);
		transaction.commit();
	}
	
	/**隐藏软键盘*/
	private void setSoftInputModes() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	public static void startActivityForChoiceContact(Activity activity,
			int requestCode) {
		ContactlistActivity.activity = activity;
		Intent intent = new Intent(activity, ContactlistActivity.class);
		intent.putExtra(ContactlistFragment.CONTACTLIST_STRAT_MODEL,
				ContactlistFragment.CONTACTLIST_STRAT_MODEL_MULITCHOICE);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void startActivityForChoiceContact(Activity activity,
			int requestCode, List<UserSimpleInfo> exists) {
		ContactlistActivity.activity = activity;
		Intent intent = new Intent(activity, ContactlistActivity.class);
		intent.putExtra(ContactlistFragment.CONTACTLIST_STRAT_MODEL,
				ContactlistFragment.CONTACTLIST_STRAT_MODEL_MULITCHOICE);
		if (exists != null) {
			ArrayList<UserSimpleInfo> list = (ArrayList<UserSimpleInfo>) exists;
			intent.putParcelableArrayListExtra(EXTRA_EXIST_CONTACTS, list);
		}
		activity.startActivityForResult(intent, requestCode);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ContactlistFragment.CONTACTS_LOAD_DG_ID:
			ProgressDialog dialog = new ProgressDialog(ContactlistActivity.this);
			String msg = getString(R.string.contacts_loading);
			dialog.setMessage(msg);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					isContactLoadDialogShow = false;
				}
			});
			return dialog;
		}
		return super.onCreateDialog(id);
	}
	
	
	@Override
	public void onBackPressed() {
		if (fragment.onBackPressed()) {
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void onLeftButtonClick() {
		onBackPressed();
	}

	@Override
	public void onRightButtonClick() {
		fragment.setResult();
		finish();
	}

	@Override
	public boolean isDialogShow() {
		return isContactLoadDialogShow;
	}

	@Override
	public void setDialogShow(boolean show) {
		isContactLoadDialogShow = show;
	}
	
}
