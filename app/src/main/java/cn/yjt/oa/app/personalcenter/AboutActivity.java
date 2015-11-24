package cn.yjt.oa.app.personalcenter;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.utils.IPSettings;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class AboutActivity extends TitleFragmentActivity implements
		View.OnClickListener, OnTouchListener {
	private static final Intent INTENT_WEICHAT;
	private static final Intent INTENT_WEIBO;

	private TextView phoneTv;
	private TextView weixinTv;
	private TextView sinaTv;
	private TextView version;
	private TextView checkVersion;

	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity_layout);
		version = (TextView) findViewById(R.id.about_version);
		checkVersion = (TextView) findViewById(R.id.check_version);
		findViewById(R.id.icon).setOnTouchListener(this);

		checkVersion.setOnClickListener(this);
		PackageInfo info;
		try {
			version.setVisibility(View.VISIBLE);
			info = this.getPackageManager().getPackageInfo(
					this.getPackageName(),
					PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
			version.setText("版本：" + info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			version.setVisibility(View.GONE);
		}
		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.check_version:
			checkVersion();
			break;
		}
	}

	private void launchWeiChat() {
		startActivity(INTENT_WEICHAT);
		copy(weixinTv.getText().toString(), this);
		Toast.makeText(this, "微信公众号已复制到粘贴板", Toast.LENGTH_LONG).show();
	}

	private void launchWeibo() {
		startActivity(INTENT_WEIBO);
		copy(sinaTv.getText().toString(), this);
		Toast.makeText(this, "微博账号已复制到粘贴板", Toast.LENGTH_LONG).show();
	}

	private void setUnderline(TextView textView) {
		textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	}


	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void copy(String content, Context context) {
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			ClipboardManager cmb = (ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setPrimaryClip(ClipData.newPlainText(null, content));
		} else {
			android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(content);
		}
	}

	/** 版本检测 */
	private void checkVersion() {
		mProgressDialog = ProgressDialog.show(this, null,
				this.getString(R.string.check_updating));
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				mProgressDialog.dismiss();
				if (updateStatus == 0 && updateInfo != null) {
					UmengUpdateAgent.showUpdateDialog(AboutActivity.this,
							updateInfo);
				}

				Log.d("AboutActivity", "updateStatus" + updateStatus);
				// case 0: // has update
				// case 1: // has no update
				// case 2: // none wifi
				// case 3: // time out
				switch (updateStatus) {
				case 0:
					break;
				case 1:
					Toast.makeText(AboutActivity.this,
							AboutActivity.this.getString(R.string.no_update),
							Toast.LENGTH_SHORT).show();
				case 2:
					break;
				case 3:
					Toast.makeText(AboutActivity.this, "请求超时",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});

		// UmengUpdateAgent.update(this);
		UmengUpdateAgent.forceUpdate(this);
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	static {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		ComponentName component = new ComponentName("com.tencent.mm",
				"com.tencent.mm.ui.LauncherUI");
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setComponent(component);
		INTENT_WEICHAT = intent;

		intent = new Intent(intent);
		component = new ComponentName("com.sina.weibo",
				"com.sina.weibo.SplashActivity");
		intent.setComponent(component);
		INTENT_WEIBO = intent;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.icon) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				touchChangedIPButton(v);
			}
		}
		return false;
	}

	private Handler handler = new Handler();
	private int counts;
	private Runnable clearTouchCounts = new Runnable() {

		@Override
		public void run() {
			System.out.println("clear tounts");
			counts = 0;
		}
	};

	private void touchChangedIPButton(View v) {
		handler.removeCallbacks(clearTouchCounts);
		handler.postDelayed(clearTouchCounts, 500);
		if (++counts >= 5) {
			IPSettings.launch(this);
		}
	}
}
