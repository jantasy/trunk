package cn.yjt.oa.app.signin;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.CustSignCommonInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.enterprise.AttendanceMemberBindActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.utils.POIPicker;
import cn.yjt.oa.app.utils.POIPicker.POIPickerListener;
import cn.yjt.oa.app.widget.ViewContainerStub;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;

public class AttendanceSetActivity extends TitleFragmentActivity implements
		OnClickListener, POIPickerListener {

	private static final int SEEKBAR_CHANGE_UNIT = 1;
	private static final int SEEKBAR_MAX = 100;
	private static final int SEEKBAR_DEFAULT = 100;
	private static final int RANGE_UNIT = 10;
	private static final int RANGE_MAX = SEEKBAR_MAX * RANGE_UNIT;
	private static final int RANGE_DEFAULT = SEEKBAR_DEFAULT * RANGE_UNIT;

	private TextView currentPosition;
	private TextView signinTime;
	private TextView signoutTime;
	private TextView signLocation;
	private EditText signinName;
	private EditText signinRangeEt;
	private SeekBar rangeSeekBar;

	private CustSignCommonInfo mSignCommonInfo;

	private static final int SIGNIN_TIME_DIALOG_ID = 0;
	private static final int SIGNOUT_TIME_DIALOG_ID = 1;
	private static final int REQUEST_CODE_CHOOSE_POSITION = 3;
	private int mSigninHour;
	private int mSigninMinute;
	private int mSignoutHour;
	private int mSignoutMinute;
	private ProgressDialog dialog;
	private boolean isProgressChange = false;
	private MapView mMapView;
	private BaiduMap mBaiduMap;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_attendance_set);
		initViews();
		initMaps();
		watchRangeView();
		mSignCommonInfo = getIntent().getParcelableExtra("CustSignCommonInfo");
		bindData(mSignCommonInfo);
		poiPicker = new POIPicker(this, this);
	}

	private void initViews() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setImageResource(R.drawable.contact_list_save);
		currentPosition = (TextView) findViewById(R.id.current_position);
		signinName = (EditText) findViewById(R.id.sign_name);
		signLocation = (TextView) findViewById(R.id.sign_location);
		signinTime = (TextView) findViewById(R.id.tv_check_in_time);
		signoutTime = (TextView) findViewById(R.id.signout_time);
		if (AccountManager.getCurrent(getApplicationContext()).getIsYjtUser() == 1) {
			findViewById(R.id.time_layout).setVisibility(View.GONE);
			findViewById(R.id.time_title).setVisibility(View.GONE);
		}

		signLocation.setOnClickListener(this);
		signinTime.setOnClickListener(this);
		signoutTime.setOnClickListener(this);

		signinRangeEt = (EditText) findViewById(R.id.et_signin_range);
		findViewById(R.id.range_add).setOnClickListener(this);
		findViewById(R.id.range_subtract).setOnClickListener(this);
		// findViewById(R.id.tag_manage).setOnClickListener(this);
		rangeSeekBar = (SeekBar) findViewById(R.id.signin_range);
		rangeSeekBar.setMax(100);
		findViewById(R.id.member_manage).setOnClickListener(this);
	}

	private void initMaps() {
		BaiduMapOptions options = new BaiduMapOptions();
		options.zoomGesturesEnabled(true);
		options.zoomControlsEnabled(false);
		mMapView = new MapView(this, options);
		((ViewContainerStub) findViewById(R.id.bmapView)).setView(mMapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15f));
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				LocationMode.FOLLOWING, false, BitmapDescriptorFactory
						.fromResource(R.drawable.attendance_icon_location)));
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	private void bindData(CustSignCommonInfo mSignCommonInfo) {

		if (mSignCommonInfo == null) {
			mSigninHour = 8;
			mSigninMinute = 0;
			mSignoutHour = 18;
			mSignoutMinute = 0;
			updateDisplay(signinTime, mSigninHour, mSigninMinute);
			updateDisplay(signoutTime, mSignoutHour, mSignoutMinute);
			rangeSeekBar.setProgress(SEEKBAR_DEFAULT);
			signinRangeEt.setText(String.valueOf(RANGE_DEFAULT));
			currentPosition.setText("未设置");
		} else {
			signinName.setText(mSignCommonInfo.getName());
			currentPosition.setText(mSignCommonInfo.getPositionDescription());

			rangeSeekBar.setProgress(mSignCommonInfo.getSignRange()
					/ RANGE_UNIT);
			signinRangeEt.setText(mSignCommonInfo.getSignRange() + "");

			try {
				String[] split = mSignCommonInfo.getPositionData().split(",");
				animateWithFece(new LatLng(Double.parseDouble(split[0]),
						Double.parseDouble(split[1])));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void animateWithFece(LatLng latLng) {
		MyLocationData locData = new MyLocationData.Builder()
				.latitude(latLng.latitude).longitude(latLng.longitude).build();
		mBaiduMap.setMyLocationData(locData);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(u);
		int range = rangeSeekBar.getProgress() * RANGE_UNIT;
		if (mSignCommonInfo != null) {
			range = mSignCommonInfo.getSignRange();
		}
		addBaiduMapOverlayRange(range, latLng);
	}

	private void addBaiduMapOverlayRange(int rang, LatLng circleCenterOptions) {
		if (overlay != null) {
			overlay.remove();
		}
		if (circleCenterOptions == null) {
			MyLocationData locationData = mBaiduMap.getLocationData();
			if (locationData == null) {
				System.out.println("locationData:" + locationData);
				return;
			}
			circleCenterOptions = new LatLng(locationData.latitude,
					locationData.longitude);
		}
		Stroke stroke = new Stroke(1, 0xAA5096eb);
		CircleOptions circleOptions = new CircleOptions()
				.center(circleCenterOptions).radius(rang).stroke(stroke)
				.fillColor(Color.parseColor("#115096eb"));
		overlay = mBaiduMap.addOverlay(circleOptions);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SIGNIN_TIME_DIALOG_ID:
			return new TimePickerDialog(this, mSigninTimeSetListener,
					mSigninHour, mSigninMinute, false);
		case SIGNOUT_TIME_DIALOG_ID:
			return new TimePickerDialog(this, mSignoutTimeSetListener,
					mSignoutHour, mSignoutMinute, false);
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case SIGNIN_TIME_DIALOG_ID:
			((TimePickerDialog) dialog).updateTime(mSigninHour, mSigninMinute);
			break;
		case SIGNOUT_TIME_DIALOG_ID:
			((TimePickerDialog) dialog)
					.updateTime(mSignoutHour, mSignoutMinute);
			break;
		}
	}

	private void updateDisplay(TextView mDisplay, int mHour, int mMinute) {
		mDisplay.setText(new StringBuilder().append(pad(mHour)).append(":")
				.append(pad(mMinute)));
	}

	private TimePickerDialog.OnTimeSetListener mSigninTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mSigninHour = hourOfDay;
			mSigninMinute = minute;
			updateDisplay(signinTime, mSigninHour, mSigninMinute);
		}
	};

	private TimePickerDialog.OnTimeSetListener mSignoutTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mSignoutHour = hourOfDay;
			mSignoutMinute = minute;
			updateDisplay(signoutTime, mSignoutHour, mSignoutMinute);
		}
	};

	private String latlng;

	private String poiName;

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	private void watchRangeView() {
		rangeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				isProgressChange = false;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				isProgressChange = true;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (isProgressChange) {
					signinRangeEt.setText(getRange());

				}

			}
		});
		signinRangeEt.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					EditText editText = (EditText) v;
					if (Integer.valueOf(editText.getText().toString()) > RANGE_MAX) {
						signinRangeEt.setText(String.valueOf(RANGE_MAX));
					}
				}

			}
		});
		signinRangeEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String rang = s.toString().trim();
				if (!TextUtils.isEmpty(rang)) {
					int range = Integer.valueOf(rang);
					if (Integer.valueOf(rang) > RANGE_MAX) {
						range = RANGE_MAX;
					}
					rangeSeekBar.setProgress(range / RANGE_UNIT);
					addBaiduMapOverlayRange(range, null);
				}

			}
		});
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onRightButtonClick() {
		submitAttendaceAreaSetting(new Runnable() {

			@Override
			public void run() {
				toast("考勤区域设置成功！");
				finish();
			}
		});
	}

	private void submitAttendaceAreaSetting(Runnable run) {
		if (getCustSignCommonInfo()) {
			updateCustSignCommonInfo(run);
		}
	}

	private boolean getCustSignCommonInfo() {
		if (mSignCommonInfo == null) {
			// if (TextUtils.isEmpty(signinName.getText().toString())) {
			// toast("请填写考勤名称");
			// return false;
			// }
			if (TextUtils.isEmpty(latlng)) {
				toast("请先设置考勤中心点");
				return false;
			}
			mSignCommonInfo = new CustSignCommonInfo();
			mSignCommonInfo.setCustId(Long.valueOf(AccountManager.getCurrent(
					this).getCustId()));
		}
		mSignCommonInfo.setName(signinName.getText().toString());
		if (!TextUtils.isEmpty(latlng)) {
			mSignCommonInfo.setPositionDescription(poiName);
			mSignCommonInfo.setPositionData(latlng);
		}
		mSignCommonInfo.setSignRange(rangeSeekBar.getProgress() * RANGE_UNIT);
		return true;
	}

	private void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void updateCustSignCommonInfo(final Runnable runAfterSuccess) {
		Listener<Response<CustSignCommonInfo>> listener = new ProgressDialogResponseListener<CustSignCommonInfo>(
				this, "正在提交...") {

			@Override
			public void onSuccess(CustSignCommonInfo payload) {
				mSignCommonInfo = payload;
				bindData(payload);
				runAfterSuccess.run();
			}
		};
		Type type_enterprise = new TypeToken<Response<CustSignCommonInfo>>() {
		}.getType();
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(
				String.format(AsyncRequest.MODULE_CUSTS_SIGNCOMMON,
						AccountManager.getCurrent(getApplicationContext())
								.getCustId())).setRequestBody(mSignCommonInfo)
				.setResponseType(type_enterprise).setResponseListener(listener);
		if (mSignCommonInfo.getId() > 0) {
			 /*记录操作 0812*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_MODIFY_ATTENDANCE_AREAINFO);

			builder.setModuleItem(String.valueOf(mSignCommonInfo.getId()));
			builder.build().put();
		} else {
			 /*记录操作 0811*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_CREATE_ATTENDANCE_AREA);

			builder.build().post();
		}

	}

	public static void launch(Context context) {
		Intent intent = new Intent(context, AttendanceSetActivity.class);
		context.startActivity(intent);
	}

	public static void launchWithCustSignCommonInfo(Context context,
			CustSignCommonInfo info) {
		Intent intent = new Intent(context, AttendanceSetActivity.class);
		intent.putExtra("CustSignCommonInfo", info);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_check_in_time:
			showDialog(SIGNIN_TIME_DIALOG_ID);
			break;
		case R.id.signout_time:
			showDialog(SIGNOUT_TIME_DIALOG_ID);

			break;
		case R.id.sign_location:
			// POIActivity.startForResult(this, REQUEST_CODE_CHOOSE_POSITION);
			poiPicker.show();
			break;
		case R.id.range_add:
			if (rangeSeekBar.getProgress() < SEEKBAR_MAX - SEEKBAR_CHANGE_UNIT) {
				rangeSeekBar.setProgress(rangeSeekBar.getProgress()
						+ SEEKBAR_CHANGE_UNIT);
			} else {
				rangeSeekBar.setProgress(SEEKBAR_MAX);
			}
			signinRangeEt.setText(getRange());
			break;
		case R.id.range_subtract:
			if (rangeSeekBar.getProgress() > SEEKBAR_CHANGE_UNIT) {
				rangeSeekBar.setProgress(rangeSeekBar.getProgress()
						- SEEKBAR_CHANGE_UNIT);
			} else {
				rangeSeekBar.setProgress(0);
			}
			signinRangeEt.setText(getRange());
			break;
		case R.id.nfc_write_tag:
			// startToWrite();
			break;
		// case R.id.tag_manage:
		// AttendanceTagListActivity.launch(this);
		// break;
		case R.id.member_manage:
			memberManage();
			break;
		default:
			break;
		}
	}

	private void memberManage() {
		if (mSignCommonInfo == null) {
			if (TextUtils.isEmpty(latlng)) {
				toast("请先设置考勤中心点");
				return;
			}
			alertCommitConfirmDialog();
			return;
		}else{
			if(!compareModify()){
				alertCommitConfirmDialog();
				return;
			}
		}
		AttendanceMemberBindActivity.launch(this, mSignCommonInfo.getId());
	}

	private boolean compareModify() {
		String name = mSignCommonInfo.getName();
		String string = signinName.getText().toString();
		if(!name.equals(string)){
			System.out.println("name diff");
			System.out.println("name :"+name);
			System.out.println("string:"+string);
			return false;
		}
		//TODO:
		if(mSignCommonInfo.getPositionDescription()!=null){
			if(!mSignCommonInfo.getPositionDescription().equals(currentPosition.getText().toString())){
				System.out.println("position diff");
				return false;
			}
		}else{
//			return false;
		}
		
		if(mSignCommonInfo.getSignRange() != rangeSeekBar.getProgress() * RANGE_UNIT){
			System.out.println("range diff");
			return false;
		}
		
		return true;
	}

	private void alertCommitConfirmDialog() {
		
		AlertDialogBuilder.newBuilder(this).setMessage("管理本区域人员前需要先提交当前设置，是否提交？")
				.setPositiveButton("提交", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						submitAttendaceAreaSetting(new Runnable() {

							@Override
							public void run() {
								toast("考勤区域设置成功！");
//								memberManage();
								AttendanceMemberBindActivity.launch(AttendanceSetActivity.this, mSignCommonInfo.getId());
							}
						});
					}
				}).setNegativeButton("取消", null).show();
	}

	private String getRange() {
		return String.valueOf(rangeSeekBar.getProgress() * RANGE_UNIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {

			if (requestCode == REQUEST_CODE_CHOOSE_POSITION) {
				poiName = data.getStringExtra("poi_name");
				latlng = data.getStringExtra("latlng");
				currentPosition.setText(poiName);
			}
		}
	}

	private POIPicker poiPicker;
	private Overlay overlay;

	@Override
	public void onPick(PoiInfo info) {
		animateWithFece(info.location);
		if ("当前位置".equals(info.name)) {
			poiName = info.address;
		} else {
			poiName = info.name;
		}
		latlng = info.location.latitude + "," + info.location.longitude;
		currentPosition.setText(poiName);
	}

}
