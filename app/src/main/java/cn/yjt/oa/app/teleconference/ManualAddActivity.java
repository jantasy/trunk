package cn.yjt.oa.app.teleconference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.utils.ToastUtils;

public class ManualAddActivity extends TitleFragmentActivity implements OnClickListener{
	private EditText phonenumber;
	private ImageButton ibtn_clear;
	private Button btnAdd;
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.tc_activity_manual_add);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		initView();
	}
	
	public void initView(){
		phonenumber = (EditText) findViewById(R.id.manual_input_phone_num);
		ibtn_clear = (ImageButton) findViewById(R.id.manual_clear_text);
		ibtn_clear.setOnClickListener(this);
		btnAdd = (Button) findViewById(R.id.btn_manaual_add);
		btnAdd.setOnClickListener(this);
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.manual_clear_text:
			phonenumber.setText("");
			break;
		case R.id.btn_manaual_add:
			if(!TextUtils.isEmpty(getPhoneNumber())){
				if(checkPhoneNumber(getPhoneNumber())){
					addPhoneNumber();
					finish();
				}else{
					ToastUtils.shortToastShow("请输入格式正确的号码");
				}
			}else{
				ToastUtils.shortToastShow("请输入号码");
			}

			break;
		default:
			break;
		}
	}
	
	public String getPhoneNumber(){
		return phonenumber.getText().toString().trim();
	}
	
	public void addPhoneNumber(){
		Intent data = new Intent();
		data.putExtra("phonenumber", getPhoneNumber());
		setResult(Activity.RESULT_OK, data);
	}

	/** 校验号码 */
	private boolean checkPhoneNumber(String text) {
		if (!TextUtils.isEmpty(text)) {
			return text.matches("^(0\\d{2,3})?(\\d{7,8})(\\*(\\d+))?$")
					||text.matches("^1[0-9]{10}$");
		}
		return false;
	}
}
