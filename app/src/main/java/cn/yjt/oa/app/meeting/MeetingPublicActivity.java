package cn.yjt.oa.app.meeting;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ImageUtils;
import cn.yjt.oa.app.beans.MeetingInfo;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.meeting.bean.MeetingInfoForm;
import cn.yjt.oa.app.builder.DateTimePickDialogBuilder;
import cn.yjt.oa.app.meeting.http.MeetingApiHelper;
import cn.yjt.oa.app.meeting.utils.DateUtils;
import cn.yjt.oa.app.utils.FileUtils;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;

/**
 * 新建会议的界面
 * 
 * <pre>
 * 该界面有两种类型，“新建会议”和“更新会议”。
 * 当创建会议成功后，当前界面就会变为“更新会议”，再点提交只是对之前提交的会议的更新。
 * 如果携带MeetingInfo对象打开此页面，也会自动变为“更新会议的状态”。
 * 其余情况界面属于“新建会议状态”。
 * 新创建会议，需录入： 会议名称，会议主题，会议时间，会议地点,生成签到二维码，并提供二维码的下载
 * </pre>
 * @author 熊岳岳
 * @since 20150820
 */
public class MeetingPublicActivity extends TitleFragmentActivity implements OnClickListener {

	private final String TAG = "MeetingPublicActivity";

	/** activity关闭的resultCode */
	private final static int REQUEST_CODE = 10;

	/** 创建类型 */
	private final int TYPE_PUBLIC = 0;
	/** 更新类型 */
	private final int TYPE_UPDATE = 1;
	/** 当前页面的类型 */
	private int mType = TYPE_PUBLIC;//默认为发布会议状态

	/** 进度对话框 */
	private ProgressDialog mProgressDialog;
	/** 日历对象 */
	private Calendar mMeetingStartCalendar;
	/** 会议的日期 */
	private Date mMeetingDate;
	/** 会议开始的时间 */
	private Date mMeetingStartTime;
	/** 会议结束的时间 */
	private Date mMeetingStopTime;
	/** 会议二维码url(拼接上时间戳) */
	private String mQrcodeUrl;

	/** 当前界面的MeetingInfo对象 */
	private MeetingInfo mMeetingInfo;
	/** 作为界面表达填充的对象 */
	private MeetingInfoForm mFormInfo;

	//控件参数
	private EditText mEtMeetingName;
	private EditText mEtMeetingTheme;
	private EditText mEtAddress;
	private TextView mTvDate;
	private TextView mTvStartTime;
	private TextView mTvStopTime;
	private ImageView mIvQrCode;
	private Button mBtnCommit;
	private Button mBtnDownload;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_meeting_setting);
		initParam();
		initView();
		fillData();
		SetListener();
	}

	/*-----oncreate中调用的方法START-----*/
	private void initParam() {
		initMeetingInfo();
		initMeetingTime();

	}

	private void initView() {
		mEtMeetingName = (EditText) findViewById(R.id.et_meeting_name);
		mEtMeetingTheme = (EditText) findViewById(R.id.et_meeting_theme);
		mEtAddress = (EditText) findViewById(R.id.et_address);
		mTvDate = (TextView) findViewById(R.id.tv_date);
		mTvStartTime = (TextView) findViewById(R.id.tv_start_time);
		mTvStopTime = (TextView) findViewById(R.id.tv_stop_time);
		mIvQrCode = (ImageView) findViewById(R.id.iv_qr_code);
		mBtnCommit = (Button) findViewById(R.id.btn_commit);
		mBtnDownload = (Button) findViewById(R.id.btn_download);
	}

	private void fillData() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		//填写日期
		if (mMeetingDate != null) {
			mTvDate.setText(DateUtils.sDateFormat.format(mMeetingDate));
		}
		//填写开始时间
		if (mMeetingStartTime != null) {
			mTvStartTime.setText(DateUtils.sTimeFormat.format(mMeetingStartTime));
		}
		//如果会议的类型为“更新会议”时
		if (mType == TYPE_UPDATE) {
			//填写回忆结束时间
			if (mMeetingStopTime != null) {
				mTvStopTime.setText(DateUtils.sTimeFormat.format(mMeetingStopTime));
			}
			//填写会议名称、主题、地点、二维码
			mEtMeetingName.setText(mFormInfo.getName());
			mEtMeetingTheme.setText(mFormInfo.getTheme());
			mEtAddress.setText(mFormInfo.getAddress());
			mBtnCommit.setText("更新会议并生成二维码");
			fillQrCode(mFormInfo.getQrcode());
		}
	}

	private void SetListener() {
		mTvDate.setOnClickListener(this);
		mTvStartTime.setOnClickListener(this);
		mTvStopTime.setOnClickListener(this);
		mBtnCommit.setOnClickListener(this);
		mBtnDownload.setOnClickListener(this);
	}

	/** 初始化会议时间 */
	private void initMeetingTime() {
		mMeetingStartCalendar = Calendar.getInstance();

		switch (mType) {
		/*
		 * 如果当前界面处于‘发布会议’的状态:
		 * 1.则将会议开始的默认时间设为今天上午9点
		 * 2.会议结束时间不做处理
		 */
		case TYPE_PUBLIC:
			Date date = mMeetingStartCalendar.getTime();
			String format = DateUtils.sDateFormat.format(date);
			//默认时间为上午9点
			format += "09:00";
			mMeetingStartCalendar.setTime(DateUtils.parseDateTimeFormat(format));
			break;
		/*
		 * 如果当前界面处于‘更新会议’的状态:
		 * 1.则将会议开始的默认时间设为传递过来的MeetingInfo对象中的时间
		 * 2.如果会议存在结束时间，也同时设置好
		 */
		case TYPE_UPDATE:
			if (!TextUtils.isEmpty(mMeetingInfo.getStartTime())) {
				mMeetingStartCalendar.setTime(DateUtils.parseRequestFormat(mMeetingInfo.getStartTime()));

			}
			if (!TextUtils.isEmpty(mMeetingInfo.getEndTime())) {
				mMeetingStopTime = DateUtils.changeDateToTimeOnly(DateUtils.parseRequestFormat(mMeetingInfo
					.getEndTime()));
			}
			break;

		default:
			LogUtils.e(TAG, "当前界面的类型异常");
			break;
		}
		mMeetingDate = DateUtils.changeDateToDateOnly(mMeetingStartCalendar.getTime());
		mMeetingStartTime = DateUtils.changeDateToTimeOnly(mMeetingStartCalendar.getTime());
	}

	/** 初始化携带过来的MeetingInfo */
	private void initMeetingInfo() {
		if (getIntent().getExtras() != null) {
			//获取intent传递过来的的MeetingInfo对象
			mMeetingInfo = (MeetingInfo) getIntent().getExtras().getSerializable("meetingInfo");
		}
		if (mMeetingInfo != null) {
			//如果intent携带的MeetingInfo对象不为空的话,则将当前页面的类型设置为’更新会议类型‘
			mType = TYPE_UPDATE;
			mFormInfo = new MeetingInfoForm(mMeetingInfo);
		}
	}

	/*-----oncreate中调用的方法END-----*/

	/** 启动该Activity */
	public static void launch(Context context) {
		Intent intent = new Intent(context, MeetingPublicActivity.class);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

	/** 启动该Activity并携带MeetingInfo */
	public static void launchWithMeetingInfo(Activity context, MeetingInfo info) {
		Intent intent = new Intent(context, MeetingPublicActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("meetingInfo", info);
		intent.putExtras(bundle);
		context.startActivityForResult(intent, REQUEST_CODE);
	}

	/** 设置会议日期 */
	private void setMeetingDate() {
		DateTimePickDialogBuilder builder =DateTimePickDialogBuilder.getIntances(this, mMeetingDate);
		builder.setOnButtonClickListener(new DateTimePickDialogBuilder.OnButtonClickListener() {
			@Override
			public void positiveButtonClick(Date dateTime) {
				if (dateTime != null) {
					mMeetingDate = DateUtils.changeDateToDateOnly(dateTime);
					mTvDate.setText(DateUtils.sDateFormat.format(mMeetingDate));
				} else {
					LogUtils.e(TAG, "从日期对话框返回数据出错");
				}
			}

			@Override
			public void negativeButtonClick() {

			}
		});
		builder.buildDatePickerDialog();
	}

	/** 设置会议开始时间 */
	private void setMeetingStartTime() {
		DateTimePickDialogBuilder builder = DateTimePickDialogBuilder.getIntances(this, mMeetingStartTime);
		builder.setIs24HourView(true);
		builder.setOnButtonClickListener(new DateTimePickDialogBuilder.OnButtonClickListener() {
			@Override
			public void positiveButtonClick(Date dateTime) {
				if (dateTime != null) {
					//如果会议开始时间大于会议结束时间，不允许完成设置
					Date tempStartTime = null;
					tempStartTime = DateUtils.changeDateToTimeOnly(dateTime);
					if (mMeetingStopTime != null) {
						if (mMeetingStopTime.compareTo(tempStartTime) < 0) {
							Toast.makeText(MeetingPublicActivity.this, "终止时间必须大于起始时间，请重新设置", Toast.LENGTH_SHORT).show();
							return;
						}
					}
					mMeetingStartTime = tempStartTime;
					mTvStartTime.setText(DateUtils.sTimeFormat.format(mMeetingStartTime));
				} else {
					LogUtils.e(TAG, "从日期对话框返回数据出错");
				}
			}

			@Override
			public void negativeButtonClick() {

			}
		});
		builder.buildTimePickerDialog();
	}

	/** 设置会议结束时间 */
	private void setMeetingStopTime() {
		DateTimePickDialogBuilder builder = null;
		if (mMeetingStopTime != null) {
			builder = DateTimePickDialogBuilder.getIntances(this, mMeetingStopTime);
		} else {
			builder = DateTimePickDialogBuilder.getIntances(this,mMeetingStartTime);
		}
		builder.setIs24HourView(true);
		builder.setOnButtonClickListener(new DateTimePickDialogBuilder.OnButtonClickListener() {
			@Override
			public void positiveButtonClick(Date dateTime) {
				if (mMeetingStartTime != null) {
					if (dateTime != null) {
						mMeetingStopTime = DateUtils.changeDateToTimeOnly(dateTime);
						//如果会议开始时间大于会议结束时间，不允许完成设置
						if (mMeetingStopTime.compareTo(mMeetingStartTime) > 0) {
							mMeetingStopTime = DateUtils.changeDateToTimeOnly(dateTime);
							mTvStopTime.setText(DateUtils.sTimeFormat.format(dateTime));
						} else {
							Toast.makeText(MeetingPublicActivity.this, "终止时间必须大于起始时间，请重新设置", Toast.LENGTH_SHORT).show();
						}
					} else {
						LogUtils.e(TAG, "从日期对话框返回数据出错");
					}
				} else {
					Toast.makeText(MeetingPublicActivity.this, "请先设置开始时间", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void negativeButtonClick() {

			}
		});
		builder.buildTimePickerDialog();
	}

	/** 提交会议并请求二维码 */
	private void commitRequestQrCode() {
		packageInfo();

         /*记录操作 1602*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_COMMIT_MEETING);

		if (isCompleteForm()) {
			switch (mType) {

			case TYPE_PUBLIC:
				publicMeetingInfo();
				break;

			case TYPE_UPDATE:
				updateMeetingInfo();
				break;

			default:
				LogUtils.e(TAG, "会议类型出错，无法提交");
				break;
			}
		} else {
			Toast.makeText(MeetingPublicActivity.this, "请将会议信息填写完整", Toast.LENGTH_SHORT).show();
		}
	}

	/** 下载二维码 */
	private void downloadQrCode() {
		if (mQrcodeUrl == null) {
			Toast.makeText(getApplicationContext(), "请先提交会议", Toast.LENGTH_SHORT).show();
			return;
		}

          /*记录操作 1603*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_DOWNLOAD_QRCODE);

		String cachePath = MainApplication.getImageLoader().getCacheFileByUrl(mQrcodeUrl);
		String filePath = FileUtils.getUserFolder() + "/会议二维码/" + mMeetingInfo.getName() + mMeetingInfo.getId()
			+ ".png";
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

	/** 封装表单中的数据 */
	private void packageInfo() {
		mFormInfo = new MeetingInfoForm();
		mFormInfo.setName(mEtMeetingName.getText().toString());
		mFormInfo.setTheme(mEtMeetingTheme.getText().toString());
		mFormInfo.setDate(mTvDate.getText().toString());
		mFormInfo.setStarttime(mTvStartTime.getText().toString());
		mFormInfo.setStoptime(mTvStopTime.getText().toString());
		mFormInfo.setAddress(mEtAddress.getText().toString());
	}

	/** 封装提交的数据 */
	private MeetingInfo packageUpdateInfo() {
		if (mMeetingInfo == null) {
			mMeetingInfo = new MeetingInfo();
		}

		mMeetingInfo.setCreateUserId(AccountManager.getCurrentSimpleUser(this).getId());
		mMeetingInfo.setName(mFormInfo.getName());
		mMeetingInfo.setContent(mFormInfo.getTheme());
		mMeetingInfo.setAddress(mFormInfo.getAddress());

		if (!TextUtils.isEmpty(mFormInfo.getDate())) {
			if (!TextUtils.isEmpty(mFormInfo.getStarttime())) {
				mMeetingInfo.setStartTime(DateUtils.sRequestFormat.format(DateUtils.parseDateTimeFormat(mFormInfo
					.getDate() + mFormInfo.getStarttime())));
			}
			if (!TextUtils.isEmpty(mFormInfo.getStoptime())) {
				mMeetingInfo.setEndTime(DateUtils.sRequestFormat.format(DateUtils.parseDateTimeFormat(mFormInfo
					.getDate() + mFormInfo.getStoptime())));
			}
		}

		return mMeetingInfo;
	}

	/** 验证表单是否填完 */
	private boolean isCompleteForm() {
		if (isStringNull(mFormInfo.getName())
			|| isStringNull(mFormInfo.getDate()) || isStringNull(mFormInfo.getStarttime())
			|| isStringNull(mFormInfo.getAddress())) {
			return false;
		}
		return true;
	}

	//TODO:应该放在工具类中，待优化
	/** 判断某个字符串是否为空 */
	private boolean isStringNull(String str) {
		if (str != null) {
			return TextUtils.isEmpty(str.trim());
		}
		return true;
	}

	/** 提交会议信息 */
	private void publicMeetingInfo() {
		MeetingApiHelper.publicMeeting(new ProgressDialogResponseListener<MeetingInfo>(this, "正在提交会议") {

			@Override
			public void onSuccess(MeetingInfo payload) {
				mMeetingInfo = payload;
				mBtnCommit.setText("更新会议并生成二维码");
				mType = TYPE_UPDATE;
				fillQrCode(payload.getTwoDimensionCode());
				Toast.makeText(getApplicationContext(), "发布成功", Toast.LENGTH_SHORT).show();
			}
		}, packageUpdateInfo());
	}

	/** 更新会议信息 */
	private void updateMeetingInfo() {
		MeetingApiHelper.updateMeeting(new ProgressDialogResponseListener<MeetingInfo>(this, "正在提交会议") {

			@Override
			public void onSuccess(MeetingInfo payload) {
				mMeetingInfo = payload;
				fillQrCode(payload.getTwoDimensionCode());
				Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
			}
		}, packageUpdateInfo());
	}

	/** 加载二维码 */
	private void fillQrCode(String url) {
		//用变量保存二位码的url
		mQrcodeUrl = getUrlWithTime(url);
		MainApplication.getImageLoader().get(mQrcodeUrl, new ImageLoaderListener() {

			@Override
			public void onSuccess(ImageContainer container) {
				Toast.makeText(getApplicationContext(), "加载二维码成功", Toast.LENGTH_SHORT).show();
				mIvQrCode.setImageBitmap(container.getBitmap());
			}

			@Override
			public void onError(ImageContainer container) {
				Toast.makeText(getApplicationContext(), "加载二维码失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/** 给url拼上一个时间戳 */
	private String getUrlWithTime(String url) {
		if(TextUtils.isEmpty(url)){
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

	/*---------------Override---------------*/
	@Override
	public void onLeftButtonClick() {
		if (mMeetingInfo != null) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("meetingInfo", mMeetingInfo);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
		}
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_date:
			setMeetingDate();
			break;
		case R.id.tv_start_time:
			setMeetingStartTime();
			break;
		case R.id.tv_stop_time:
			setMeetingStopTime();
			break;
		case R.id.btn_commit:
			commitRequestQrCode();
			break;
		case R.id.btn_download:
			downloadQrCode();
			break;

		default:
			break;
		}
	}
}
