package cn.yjt.oa.app.personalcenter;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.FeedbackInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;

public class FeedBackActivity extends TitleFragmentActivity implements
		OnClickListener {
	private PopupWindow pop;
//	private EditText phoneNumberEt;
//	private EditText emailEt;
//	private EditText qqEt;
	private EditText contentEt;
	private TextView typeTv;
	private RelativeLayout choiceType;
	private LinearLayout typell;

	
	
	private FeedTypeAdapter mAdapter;
	
	private String[] typeData=new String[]{"功能","视觉","BUG"};

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_activity_layout);
		findViewById(R.id.commit).setOnClickListener(this);
		choiceType=(RelativeLayout) findViewById(R.id.choice_type);
		typeTv=(TextView) findViewById(R.id.type);
		typell=(LinearLayout) findViewById(R.id.type_ll);
		

		mAdapter=new FeedTypeAdapter(this, typeData);
		choiceType.setOnClickListener(this);


		
		
//		phoneNumberEt = (EditText) findViewById(R.id.phone_munber);
//		emailEt = (EditText) findViewById(R.id.email);
//		qqEt = (EditText) findViewById(R.id.qq);
		contentEt = (EditText) findViewById(R.id.feed_content);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.commit:
			commit();

            /*记录操作 0312*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_COMMIT_FEEDBACK);
			break;
		case R.id.choice_type:
			bindTypeLinearLayout();
			break;
		
		}
	}
	
	
	
	private void bindTypeLinearLayout() {
		
		if(typell.getChildCount()>0){
			typell.removeAllViews();
			choiceType.setBackgroundResource(R.drawable.feed_back_bg);
		}else{
			choiceType.setBackgroundResource(R.drawable.feed_type_top_nomal);
			int count = mAdapter.getCount();
			for (int i = 0; i < count; i++) {
				View v=mAdapter.getView(i, null, null);
				v.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						TextView typeText = (TextView) v.findViewById(R.id.type_text);
						typeTv.setText(typeText.getText());
						typell.removeAllViews();
						choiceType.setBackgroundResource(R.drawable.feed_back_bg);
					}
				});
				
				typell.addView(v);
			}
		}
		
	}
	

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	private void commit() {
		String feedType = typeTv.getText().toString();
//		String phoneMunber = phoneNumberEt.getText().toString();
//		String email = emailEt.getText().toString();
//		String qq = qqEt.getText().toString();
		String content = contentEt.getText().toString();
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(this, R.string.feedback_empty, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (feedType.equals("请选择类型")) {
			Toast.makeText(this, R.string.feedback_type_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		FeedbackInfo info = new FeedbackInfo();
		info.setContent(content);
//		info.setPhone(phoneMunber);
//		info.setEmail(email);
//		info.setQq(qq);
		info.setType(feedType);
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.commit_feedback));
		dialog.show();
		Builder builder = new Builder();
		builder.setModule(AsyncRequest.MODULE_FEEDBACK);
		builder.setRequestBody(info);
		Type type = new TypeToken<Response<Object>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new Listener<Response<Object>>() {

			@Override
			public void onResponse(Response<Object> response) {
				dialog.dismiss();
				if (response != null && response.getCode() == 0) {
					Toast.makeText(FeedBackActivity.this,
							R.string.feedback_commit_success,
							Toast.LENGTH_SHORT).show();
					hideSoftInput();
					finish();
				} else {
					Toast.makeText(FeedBackActivity.this,
							R.string.feedback_commit_fail, Toast.LENGTH_SHORT)
							.show();
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				dialog.dismiss();
				Toast.makeText(FeedBackActivity.this,
						R.string.feedback_commit_fail, Toast.LENGTH_SHORT).show();
			}
		});
		builder.build().post();
	}


	@Override
	public void onBackPressed() {
		if (pop != null && pop.isShowing()) {
			pop.dismiss();
		} else {
			super.onBackPressed();
		}
	}
	@SuppressLint("NewApi") @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(typell.getChildCount()>0){
				typell.removeAllViews();
				choiceType.setBackgroundResource(R.drawable.feed_back_bg);
				return true;
			}else{
				return super.onKeyDown(keyCode, event);
			}
		}else{
			
		return super.onKeyDown(keyCode, event);
		}
	}
	
	
}
