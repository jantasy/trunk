package cn.yjt.oa.app.enterprise.operation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.enterprise.contact.PhoneUtils;

public class AddMemberActivity extends TitleFragmentActivity implements
		OnClickListener {

	public static final String EXTRA_MEMBER_NAME = "name";
	public static final String EXTRA_MEMBER_PHONE = "phone";
	private EditText name;
	private EditText phone;
	private Button btnAdd;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.enterprise_add_member);
		initView();
	}

	private void initView() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		name = (EditText) findViewById(R.id.member_name);
		phone = (EditText) findViewById(R.id.member_phone);
		btnAdd = (Button) findViewById(R.id.btn_member_ok);
		btnAdd.setOnClickListener(this);
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	private String getName() {
		return name.getText().toString().trim();
	}

	private String getPhone() {
		return phone.getText().toString().trim();
	}

	@Override
	public void onClick(View v) {
		if (!TextUtils.isEmpty(getName()) && !TextUtils.isEmpty(getPhone())) {
			if (PhoneUtils.isMobileNum(getPhone())) {
				setResult();
			} else {
				toast("手机号码格式错误");
			}
		} else {
			toast("用户名和手机号不能为空");
		}
	}

	private void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	private void setResult() {
		hideSoftInput();
		Intent data = new Intent();
		data.putExtra(EXTRA_MEMBER_NAME, getName());
		data.putExtra(EXTRA_MEMBER_PHONE, getPhone());
		setResult(Activity.RESULT_OK, data);
		finish();
	}
}