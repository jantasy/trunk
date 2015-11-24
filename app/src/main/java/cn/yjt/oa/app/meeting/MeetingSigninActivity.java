package cn.yjt.oa.app.meeting;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.MeetingSignInInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.meeting.utils.DateUtils;
import cn.yjt.oa.app.utils.CheckNetUtils;
import cn.yjt.oa.app.utils.ErrorUtils;

/**会议签到的Activity*/
@SuppressLint("SimpleDateFormat")
public class MeetingSigninActivity extends Activity implements OnClickListener, Listener<Response<MeetingSignInInfo>> {

	private ImageView signinImage;
	private TextView signinText;
	private TextView signinTime;
	private View progressBar;
	private View confirmButton;

	private String mMeetingId;
	private String mToken;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		initParams();
		prepareView();
		resetView();
		requestMeetingSignin();
	}


	private void requestMeetingSignin() {
		ApiHelper.requestMeetingSignin(this,mMeetingId,mToken);
	}

	private void resetView() {
		progressBar.setVisibility(View.VISIBLE);
		signinImage.setVisibility(View.INVISIBLE);
		signinText.setText("正在进行会议签到...");
		signinTime.setText("");
	}

	private void prepareView() {
		initView();

		//如果网络不可用的话，直接给出提示，然后结束该方法
		if (!CheckNetUtils.hasNetWork(this)) {
			signinText.setText(R.string.connect_network_fail);
			signinImage.setImageResource(R.drawable.signin_error_image);
			return;
		}

	}

	private void initParams() {
		Intent intent = getIntent();
		mMeetingId = intent.getStringExtra("meetingId");
		mToken = intent.getStringExtra("token");
	}

	private void setupColor(MeetingSignInInfo payload) {
		//		if (payload.getResultColor() != 0) {
		//			signinText.setTextColor(payload.getResultColor());
		//		}
		//		if (payload.getDescColor() != 0) {
		//			signinTime.setTextColor(payload.getDescColor());
		//		}
	}

	private void onSigninSuccess(MeetingSignInInfo payload) {
		signinText.setText("签到成功");
		signinTime.setText(String.format(
				"您在%s成功签到",DateUtils.requestToDateTime(payload.getSignInTime())));
		signinImage.setImageResource(R.drawable.signin_success_image);
	}

	/**
	 * 初始化控件，并为控件设置监听
	 */
	private void initView() {
		setContentView(R.layout.activity_attendance);
		//初始化控件
		signinImage = (ImageView) findViewById(R.id.tv_check_in);
		signinText = (TextView) findViewById(R.id.tv_check_in_status);
		signinTime = (TextView) findViewById(R.id.tv_check_in_time);
		progressBar = findViewById(R.id.pb_updating);
		//为控件设置监听
		confirmButton = findViewById(R.id.btn_comfirm);
		confirmButton.setOnClickListener(this);
	}

	private void onSiginFailure(String msg) {
		signinText.setText("签到无效");
		signinTime.setText(msg);
		progressBar.setVisibility(View.GONE);
		signinImage.setVisibility(View.VISIBLE);
		signinImage.setImageResource(R.drawable.signin_error_image);
		confirmButton.setClickable(true);
	}

	public static void launch(Context context,String meetingId,String token) {
		Intent intent = new Intent(context, MeetingSigninActivity.class);
		intent.putExtra("meetingId", meetingId);
		intent.putExtra("token", token);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		finish();
	}

	@Override
	public void onErrorResponse(InvocationError arg0) {
		onSiginFailure(ErrorUtils.getErrorDescription(arg0.getErrorType()));
	}

	@Override
	public void onResponse(Response<MeetingSignInInfo> response) {
		if (response.getCode() == 0) {
			//TODO 获取数据，显示数据
			MeetingSignInInfo payload = response.getPayload();
			//			payload.setInspectResult(1);
			//			payload.setInspectInTime("11");
			//			payload.setInspectResultDesc("会议签到成功");
			progressBar.setVisibility(View.GONE);
			signinImage.setVisibility(View.VISIBLE);
			setupColor(payload);
			//			if (payload.getInspectResult() == 1) {
			onSigninSuccess(payload);
			//			} else {
			//				onSiginFailure(payload.getInspectResultDesc() == null ? "您不在可范围内"
			//						: payload.getInspectResultDesc());
			//			}

		} else {
			onSiginFailure(response.getDescription());
		}
	}

}
