package cn.yjt.oa.app.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.meeting.fragment.MeetingFragment;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.ViewUtil;

import com.zbar.lib.CaptureActivity;

/**
 * 会议的主界面界面
 * <pre>
 * 进去可查看历史会议信息，可新创建会议，扫二维码进行会议签到
 * </pre>
 * @author 熊岳岳
 * @since 20150819
 *
 */
public class MeetingActivity extends TitleFragmentActivity implements OnClickListener {

	private final String TAG = "MeetingManagerActivity";

	/**参见会议的fragment的标识*/
	public static final int FRAGMENT_JOIN = 0;
	/**发布会议的fragment的标识*/
	public static final int FRAGMENT_PUBLIC = 1;

	private Button mBtnScanQrcode;
	private TextView mTvMeetingJoin;
	private TextView mTvMeetingPublic;

	/**我参与的会议的Fragment*/
	private MeetingFragment mJoinFragment;
	/**我发起的会议的Fragment*/
	private MeetingFragment mPublicFragment;


	/*-----生命周期函数START-----*/
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
			LaunchActivity.launch(this);
			finish();
		} else {
			setContentView(R.layout.activity_meeting);

              /*记录操作 1601*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_MEETING);

			initParam();
			initView();
			fillData();
			setListener();
		}
	}

	/*-----生命周期函数END-----*/

	/*-----oncreate中的方法START-----*/
	private void initParam() {

	}

	private void initView() {

		mBtnScanQrcode = (Button) findViewById(R.id.btn_scan_qrcode);
		mTvMeetingJoin = (TextView) findViewById(R.id.tv_meeting_join);
		mTvMeetingPublic = (TextView) findViewById(R.id.tv_meeting_public);

		initFragment();
	}

	private void fillData() {
		getRightButton().setImageResource(R.drawable.contact_add_group);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}

	private void setListener() {
		mBtnScanQrcode.setOnClickListener(this);
		mTvMeetingJoin.setOnClickListener(this);
		mTvMeetingPublic.setOnClickListener(this);

	}

	/**初始化Fragment*/
	private void initFragment() {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		mJoinFragment = new MeetingFragment();
		//给fragment设置上标签以作区分
		Bundle bundle = new Bundle();
		bundle.putInt("tag", FRAGMENT_JOIN);
		mJoinFragment.setArguments(bundle);
		transaction.replace(R.id.fl_fragment, mJoinFragment, "mJoinFragment");
		transaction.commit();

		//默认选中我参见的会议
		mTvMeetingJoin.setSelected(true);
	}

	/*-----oncreate中的方法END-----*/

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onRightButtonClick() {
		MeetingPublicActivity.launch(this);
		super.onRightButtonClick();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_scan_qrcode:
			scanQrcode();
			break;
		case R.id.tv_meeting_join:
			selectJoinFragment();
			break;
		case R.id.tv_meeting_public:
			selectPublicFragment();
			break;
		default:
			break;
		}

	}

	/**选中我参加的会议*/
	private void selectJoinFragment() {
		mTvMeetingJoin.setSelected(true);
		mTvMeetingPublic.setSelected(false);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		if (mJoinFragment == null) {
			mJoinFragment = new MeetingFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("tag", FRAGMENT_JOIN);
			mJoinFragment.setArguments(bundle);
		}
		transaction.replace(R.id.fl_fragment, mJoinFragment, "mJoinFragment");
		transaction.commit();
	}

	/**选中我发起的会议*/
	private void selectPublicFragment() {
		mTvMeetingJoin.setSelected(false);
		mTvMeetingPublic.setSelected(true);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		if (mPublicFragment == null) {
			mPublicFragment = new MeetingFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("tag", FRAGMENT_PUBLIC);
			mPublicFragment.setArguments(bundle);
		}
		transaction.replace(R.id.fl_fragment, mPublicFragment, "mPublicFragment");
		transaction.commit();
	}

	/**开启扫码界面*/
	private void scanQrcode() {
		startActivity(new Intent(this, CaptureActivity.class));
	}
}
