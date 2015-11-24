package cn.yjt.oa.app.personalcenter;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.UmengBaseActivity;

public class LoginOrRegistFail extends UmengBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_regist_fail_activity);
		TextView title = (TextView) findViewById(R.id.title);
		TextView content = (TextView) findViewById(R.id.fail_content);
		String from = getIntent().getStringExtra("from");
		if ("regist".equals(from)){
			title.setText("注册失败");
			content.setText("注册失败，请重新注册");
		}
		findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
