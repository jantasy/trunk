package cn.yjt.oa.app.meeting;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ImageUtils;
import cn.yjt.oa.app.beans.MeetingInfo;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.meeting.http.MeetingApiHelper;
import cn.yjt.oa.app.meeting.utils.DateUtils;
import cn.yjt.oa.app.utils.ColorUtils;
import cn.yjt.oa.app.utils.FileUtils;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;

/**
 * 显示会议详情的页面
 * @author 熊岳岳
 * @since 20150827
 */
public class MeetingInfoActivity extends TitleFragmentActivity implements OnClickListener {

	private final static String TAG = "MeetingInfoActivity";

	/** 点击查看会议详情的页面 */
	private TextView mTvSigninDetail;

	/** 升级二维码按钮 */
	private Button mBtnUpdateQrcode;
	/** 下载二维码按钮 */
	private Button mBtnDownloadQrcode;
	/** 编辑会议按钮 */
	private Button mBtnEditMeeting;

	/** 当前页面所属会议的实体类 */
	private MeetingInfo mMeetingInfo;

	/** 当前会议二维码的url(拼上一个时间戳) */
	private String mQrcodeUrl;

	//会议基本信息的控件
	private TextView mTvMeetingName;
	private TextView mTvMeetingTheme;
	private TextView mTvMeetingDate;
	private TextView mTvMeetingTime;
	private TextView mTvMeetingAddress;
	private TextView mTvMeetingSigninCount;
	private TextView mTvMeetingSigninTime;
	private TextView mTvMeetinUnstart;
	private ImageView mIvMeetingQrcode;
	private LinearLayout mLlMeetingSigninCount;
	private LinearLayout mLlMeetingSigninTime;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_meeting_manager);

        /*记录操作 1605*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_MEETING_DETAIL);

		initParam();
		initView();
		fillData();
		setListener();
	}

	/*-----oncreate中的方法START-----*/
	private void initParam() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mMeetingInfo = (MeetingInfo) bundle.getSerializable("meetingInfo");
		} else {
			mMeetingInfo = new MeetingInfo();
		}
	}

	private void initView() {
		mTvMeetingName = (TextView) findViewById(R.id.tv_meeting_name);
		mTvMeetingTheme = (TextView) findViewById(R.id.tv_meeting_theme);
		mTvMeetingDate = (TextView) findViewById(R.id.tv_meeting_date);
		mTvMeetingTime = (TextView) findViewById(R.id.tv_meeting_time);
		mTvMeetingAddress = (TextView) findViewById(R.id.tv_meeting_address);
		mTvMeetingSigninCount = (TextView) findViewById(R.id.tv_meeting_signin_count);
		mTvMeetingSigninTime = (TextView) findViewById(R.id.tv_meeting_signin_time);
		mIvMeetingQrcode = (ImageView) findViewById(R.id.iv_meeting_qrcode);
		mTvSigninDetail = (TextView) findViewById(R.id.tv_signin_detail);
		mTvMeetinUnstart = (TextView) findViewById(R.id.tv_meeting_unstart);
		mBtnUpdateQrcode = (Button) findViewById(R.id.btn_update_qrcode);
		mBtnDownloadQrcode = (Button) findViewById(R.id.btn_download_qrcode);
		mBtnEditMeeting = (Button) findViewById(R.id.btn_edit_meeting);
		mLlMeetingSigninCount = (LinearLayout) findViewById(R.id.ll_meeting_signin_count);
		mLlMeetingSigninTime = (LinearLayout) findViewById(R.id.ll_meeting_signin_time);
	}

	private void fillData() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		mTvMeetingName.setText(mMeetingInfo.getName());
		mTvMeetingTheme.setText(mMeetingInfo.getContent());
		fillAboutDate();
		fillAboutMeetingStartData();
		fillAboutMeetingType();
	}

	private void setListener() {
		mTvSigninDetail.setOnClickListener(this);
		mBtnUpdateQrcode.setOnClickListener(this);
		mBtnDownloadQrcode.setOnClickListener(this);
		mBtnEditMeeting.setOnClickListener(this);
	}

	/** 加载二维码 */
	private void fillQrCode() {
		initQrCode(mMeetingInfo.getTwoDimensionCode());
	}

	/** 根据会议是否开始设置不同的显示 */
	private void fillAboutMeetingStartData() {
		//如果会议尚未开启考勤人数显示为：会议尚未开始,隐藏“出席人数"那一栏,显示"会议尚未开始"
		if (isMeetingStart()) {
			mTvMeetingSigninCount.setTextColor(ColorUtils.getResourcesColor(R.color.attendance_normal));
			mTvMeetingSigninCount.setText(mMeetingInfo.getSignInNums() + "");
			//如果参会人数为0的时间，也隐藏“点击查看详细人员名单”
			if (mMeetingInfo.getSignInNums() == 0) {
				mTvSigninDetail.setVisibility(View.INVISIBLE);
			}
		} else {
			mLlMeetingSigninCount.setVisibility(View.GONE);
			mTvMeetinUnstart.setVisibility(View.VISIBLE);
		}
	}

	/** 根据会议的类型设置不同的显示 */
	private void fillAboutMeetingType() {
		//判断当前会议的类型，如果是参加的会议不显示更新“考勤人数”，"更新二维码"，"编辑二维码"，"编辑会议"
		switch (mMeetingInfo.getType()) {
		case MeetingInfo.TYPE_JOIN:
			fillJoinMeetingData();
			break;

		case MeetingInfo.TYPE_PUBLIC:
			fillQrCode();
			break;

		default:
			break;
		}
	}

	/** 显示日期相关的控件 */
	private void fillAboutDate() {
		String date = null;
		StringBuilder time = null;

		//界面显示是分为日期（只包含年月日）时间（起始时间~结束时间）
		//MeetingInfo实体类返回的值为‘起始时间’和‘结束时间’(包括年月日)
		//所以将日期给拆出来，同时用‘~’拼接一下起始时间和结束时间
		if (!TextUtils.isEmpty(mMeetingInfo.getStartTime())) {
			date = DateUtils.sDateFormat.format(DateUtils.parseRequestFormat(mMeetingInfo.getStartTime()));
			time = new StringBuilder(DateUtils.sTimeFormat.format(DateUtils.parseRequestFormat(mMeetingInfo
				.getStartTime())));
			if (!TextUtils.isEmpty(mMeetingInfo.getEndTime())) {
				time.append("~").append(
					DateUtils.sTimeFormat.format(DateUtils.parseRequestFormat(mMeetingInfo.getEndTime())));
			}
		}
		mTvMeetingTime.setText(time != null ? time.toString() : "");
		mTvMeetingDate.setText(date);
		mTvMeetingAddress.setText(mMeetingInfo.getAddress());

	}

	/** 参加会议的数据显示 */
	private void fillJoinMeetingData() {
		setTitle("会议详情");
		mLlMeetingSigninCount.setVisibility(View.GONE);
		mBtnDownloadQrcode.setVisibility(View.GONE);
		mBtnUpdateQrcode.setVisibility(View.GONE);
		mBtnEditMeeting.setVisibility(View.GONE);
		mTvMeetinUnstart.setVisibility(View.GONE);
		mIvMeetingQrcode.setVisibility(View.GONE);
		mLlMeetingSigninTime.setVisibility(View.VISIBLE);
		if (!TextUtils.isEmpty(mMeetingInfo.getSignInTime())) {
			if (isNormalSignin()) {
				mTvMeetingSigninTime.setTextColor(ColorUtils.getResourcesColor(R.color.attendance_normal));
			} else {
				mTvMeetingSigninTime.setTextColor(ColorUtils.getResourcesColor(R.color.attendance_abnormal));
			}
			mTvMeetingSigninTime.setText(DateUtils.requestToDateTime(mMeetingInfo.getSignInTime()));
		}
	}

	/*-----oncreate中的方法END-----*/
	/** 启动该Activity */
	public static void launch(Context context, MeetingInfo info) {
		Intent intent = new Intent(context, MeetingInfoActivity.class);
		if (info != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("meetingInfo", info);
			intent.putExtras(bundle);
		}
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_signin_detail:
			MeetingStaffActivity.launch(this, mMeetingInfo.getId());

            /*记录操作 1607*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_JOINMEETING_PERSON);

			break;
		case R.id.btn_update_qrcode:
			updateQrCode();

            /*记录操作 1606*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_UPDATE_QRCODE);

			break;
		case R.id.btn_download_qrcode:
			downloadQrcode();
			break;
		case R.id.btn_edit_meeting:
			MeetingPublicActivity.launchWithMeetingInfo(this, mMeetingInfo);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			MeetingInfo tempInfo = (MeetingInfo) data.getExtras().getSerializable("meetingInfo");
			if (tempInfo != null) {
				mMeetingInfo = tempInfo;
			}
			fillData();
		}
	}

	/** 更新二维码 */
	private void updateQrCode() {
		MeetingApiHelper.updateMeetingQrcode(new ProgressDialogResponseListener<MeetingInfo>(this, "正在更新二维码") {
			@Override
			public void onSuccess(MeetingInfo payload) {
				mMeetingInfo = payload;
				initQrCode(mMeetingInfo.getTwoDimensionCode());
			}
		}, mMeetingInfo.getId());
	}

	/** 下载二维码 */
	private void downloadQrcode() {
		if (mQrcodeUrl == null) {
			Toast.makeText(getApplicationContext(), "下载失败，请更新二维码", Toast.LENGTH_SHORT).show();
			return;
		}

          /*记录操作 1603*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_DOWNLOAD_QRCODE);

		String fileName = mMeetingInfo.getName() + mMeetingInfo.getId() + ".png";
		String cachePath = MainApplication.getImageLoader().getCacheFileByUrl(mQrcodeUrl);
		String filePath = FileUtils.getUserFolder() + "/" + fileName;
		File src = new File(cachePath);
		File file = new File(filePath);
		if (!src.exists()) {
			Toast.makeText(getApplicationContext(), "下载失败，请更新二维码", Toast.LENGTH_SHORT).show();
			return;
		}
		FileUtils.copy(src, file);
		Toast.makeText(getApplicationContext(), "二维码已下载到：" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
		ImageUtils.openImageFile(this, file);
	}

	/** 判断会议是否开始 */
	private boolean isMeetingStart() {
		if (!TextUtils.isEmpty(mMeetingInfo.getStartTime())) {
			Date startTime = DateUtils.parseRequestFormat(mMeetingInfo.getStartTime());
			return Calendar.getInstance().getTime().compareTo(startTime) > 0;
		}
		return false;
	}

	/** 签到是否迟到 */
	private boolean isNormalSignin() {
		if (!TextUtils.isEmpty(mMeetingInfo.getStartTime()) && !TextUtils.isEmpty(mMeetingInfo.getSignInTime())) {
			Date startTime = DateUtils.parseRequestFormat(mMeetingInfo.getStartTime());
			Date signinTime = DateUtils.parseRequestFormat(mMeetingInfo.getSignInTime());
			return startTime.compareTo(signinTime) > 0;
		}
		return false;
	}

	/** 加载二维码 */
	private void initQrCode(String url) {
		LogUtils.i(TAG, "二维码地址：" + url);
		mQrcodeUrl = getUrlWithTime(url);
		MainApplication.getImageLoader().get(mQrcodeUrl, new ImageLoaderListener() {

			@Override
			public void onSuccess(ImageContainer container) {
				Toast.makeText(getApplicationContext(), "加载二维码成功", Toast.LENGTH_SHORT).show();
				mIvMeetingQrcode.setImageBitmap(container.getBitmap());
			}

			@Override
			public void onError(ImageContainer container) {
				Toast.makeText(getApplicationContext(), "加载二维码失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/** 给url拼上一个时间戳 */
	private String getUrlWithTime(String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		long time = Calendar.getInstance().getTime().getTime();
		if (url.indexOf("?") > -1) {
			url = url + "&timestamp=" + time;
		} else {
			url = url + "?timestamp=" + time;
		}
		return url;
	}
}
