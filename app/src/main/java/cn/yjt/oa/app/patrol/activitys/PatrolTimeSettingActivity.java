package cn.yjt.oa.app.patrol.activitys;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.AttendanceTime;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;

public class PatrolTimeSettingActivity extends BackTitleFragmentActivity
		implements OnClickListener, OnCheckedChangeListener,
		CompoundButton.OnCheckedChangeListener {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"HH:mm");

	private TextView onDutyTime;
	private TextView offDutyTime;
	private AttendanceTime attendanceTime;
	private AttendanceTime multiAttendanceTime;

	private TextView tips;

	private RadioGroup attendanceMode;

	private CheckBox multiSegmentSetting;
	private TextView tvMultiSegmentSetting;
	private TextView multiOndutyTime;
	private TextView multiOffdutyTime;

	private LinearLayout multiTimeLayout;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_patrol_time_setting);

		multiTimeLayout = (LinearLayout) findViewById(R.id.multi_time_layout);
		multiSegmentSetting = (CheckBox) findViewById(R.id.cb_multi_segment_setting);
		tvMultiSegmentSetting = (TextView) findViewById(R.id.tv_multi_segment_setting);
		onDutyTime = (TextView) findViewById(R.id.onduty_time);
		offDutyTime = (TextView) findViewById(R.id.offduty_time);
		multiOndutyTime = (TextView) findViewById(R.id.multi_onduty_time);
		multiOffdutyTime = (TextView) findViewById(R.id.multi_offduty_time);

		attendanceMode = (RadioGroup) findViewById(R.id.attendance_mode_group);

		tips = (TextView) findViewById(R.id.tips);

		onDutyTime.setOnClickListener(this);
		offDutyTime.setOnClickListener(this);
		multiOndutyTime.setOnClickListener(this);
		multiOffdutyTime.setOnClickListener(this);

		// multiSegmentSetting.setChecked(StorageUtils.getSystemSettings(this)
		// .getBoolean(Config.IS_OPEN_SHAKING_SOUND, true));
		multiSegmentSetting.setOnCheckedChangeListener(this);

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		ListSlice<AttendanceTime> listSlice = (ListSlice<AttendanceTime>) bundle
				.getSerializable(PatrolTimeActivity.PATROL_TIME);

		Boolean payload = intent.getBooleanExtra(PatrolTimeActivity.PATROL_STATUS, true);

		if (listSlice.getTotal() != 0) {
			attendanceTime = listSlice.getContent().get(0);
			setupView(attendanceTime);

			if (listSlice.getContent().size() > 1) {
				multiAttendanceTime = listSlice.getContent().get(1);
				setupMultiView(attendanceTime);
				multiSegmentSetting.setChecked(true);
			} else {
				multiSegmentSetting.setChecked(false);
			}
		}
		checkMode(payload);
		changeViewState(payload);
		attendanceMode
				.setOnCheckedChangeListener(PatrolTimeSettingActivity.this);
		// useSimpleAttendance.setOnCheckedChangeListener(this);
		// requestAttendanceTimes();
		getRightButton().setImageResource(R.drawable.contact_list_save);
	}

	private void setupMultiView(AttendanceTime attendanceTime2) {
		multiOndutyTime.setText(multiAttendanceTime.getInStartTime());
		multiOffdutyTime.setText(multiAttendanceTime.getOutStartTime());
	}

	private void setupView(AttendanceTime attendanceTime) {
		onDutyTime.setText(attendanceTime.getInStartTime());
		offDutyTime.setText(attendanceTime.getOutStartTime());
	}

	@Override
	public void onRightButtonClick() {
		Long inStartTime1 = AttendanceTime.parseTime(
				onDutyTime.getText().toString()).getTime();
		Long inStartTime2 = AttendanceTime.parseTime(
				multiOndutyTime.getText().toString()).getTime();
		Long outEndTime1 = AttendanceTime.parseTime(
				offDutyTime.getText().toString()).getTime();
		Long outEndTime2 = AttendanceTime.parseTime(
				multiOffdutyTime.getText().toString()).getTime();

		if (multiSegmentSetting.isChecked()) {
			if ((inStartTime1 >= inStartTime2 && inStartTime1 <= outEndTime2)
					|| (outEndTime1 >= inStartTime2 && outEndTime1 <= outEndTime2)
					|| (inStartTime2 >= inStartTime1 && inStartTime2 <= outEndTime1)
					|| (outEndTime2 >= inStartTime1 && outEndTime2 <= outEndTime1)) {

				Toast.makeText(getApplicationContext(),
						"任意两个班段的时间都不能重合，请修改后重试。", Toast.LENGTH_LONG).show();
			} else {
                modifyPatrolTime();
			}
		}else{
            modifyPatrolTime();
		}

	}

    /**修改巡检时间*/
    private void modifyPatrolTime() {
        PatrolApiHelper.setPatrolTime(new ProgressDialogResponseListener<Object>(
                this, "正在修改巡检时间") {

            @Override
            public void onSuccess(Object payload) {
                Toast.makeText(getApplicationContext(), "修改成功",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }, AccountManager.getCurrent(getApplicationContext()).getCustId(),buildAttendanceTime());
    }

    private List<AttendanceTime> buildAttendanceTime() {
		if (attendanceTime == null) {
			attendanceTime = new AttendanceTime();
		}
		List<AttendanceTime> attendanceTimes = new ArrayList<AttendanceTime>();

		attendanceTime.setInEndTime(onDutyTime.getText().toString());
		attendanceTime.setInStartTime(onDutyTime.getText().toString());
		attendanceTime.setOutEndTime(offDutyTime.getText().toString());
		attendanceTime.setOutStartTime(offDutyTime.getText().toString());

		attendanceTimes.add(attendanceTime);

		if (multiSegmentSetting.isChecked()) {
			if (multiAttendanceTime == null) {
				multiAttendanceTime = new AttendanceTime();
			}
			multiAttendanceTime.setInEndTime(multiOndutyTime.getText()
					.toString());
			multiAttendanceTime.setInStartTime(multiOndutyTime.getText()
					.toString());
			multiAttendanceTime.setOutEndTime(multiOffdutyTime.getText()
					.toString());
			multiAttendanceTime.setOutStartTime(multiOffdutyTime.getText()
					.toString());
			attendanceTimes.add(multiAttendanceTime);
		}
		return attendanceTimes;
	}

	private void requestAttendanceTimeStatus() {
		ApiHelper.getTimeAttendanceStatus(new ResponseListener<Boolean>() {

			@Override
			public void onSuccess(Boolean payload) {
				checkMode(payload);
				changeViewState(payload);
			}

			@Override
			public void afterFinish() {
				super.afterFinish();
				attendanceMode
						.setOnCheckedChangeListener(PatrolTimeSettingActivity.this);
			}
		});
	}

	private void checkMode(boolean isChecked) {
		attendanceMode.setOnCheckedChangeListener(null);
		attendanceMode.check(isChecked ? R.id.attendance_mode_simple
				: R.id.attendance_mode_complex);
		attendanceMode.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.onduty_time:
			setOnDutyTime(onDutyTime);
			break;
		case R.id.offduty_time:
			setOffDutyTime(offDutyTime);
			break;

		case R.id.multi_onduty_time:
			setOnDutyTime(multiOndutyTime);
			break;
		case R.id.multi_offduty_time:
			setOffDutyTime(multiOffdutyTime);
			break;
		default:
			break;
		}
	}

	private void setOnDutyTime(final TextView duty_time) {
		int hourOfDay = 9;
		int minute = 0;

		String time = duty_time.getText().toString();
		try {
			Date date = DATE_FORMAT.parse(time);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
			minute = calendar.get(Calendar.MINUTE);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		TimePickerDialog dialog = new TimePickerDialog(this,
				getTimePickerDialogTheme(), new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						duty_time.setText(buildTime(hourOfDay, minute));
					}
				}, hourOfDay, minute, true);
		dialog.show();
	}

	private void setOffDutyTime(final TextView duty_time) {
		int hourOfDay = 18;
		int minute = 0;

		String time = duty_time.getText().toString();
		try {
			Date date = DATE_FORMAT.parse(time);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
			minute = calendar.get(Calendar.MINUTE);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		TimePickerDialog dialog = new TimePickerDialog(this,
				getTimePickerDialogTheme(), new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						duty_time.setText(buildTime(hourOfDay, minute));
					}
				}, hourOfDay, minute, true);
		dialog.show();
	}

	@TargetApi(VERSION_CODES.ICE_CREAM_SANDWICH)
	private int getTimePickerDialogTheme() {
		if (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
			return 0;
		} else if (VERSION.SDK_INT < VERSION_CODES.ICE_CREAM_SANDWICH) {
			return DatePickerDialog.THEME_HOLO_LIGHT;
		} else {
			return DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;
		}
	}

	public static void launch(Context context) {
		Intent intent = new Intent(context, PatrolTimeSettingActivity.class);
		context.startActivity(intent);
	}

	public static void launch(Context context, Intent intent) {
		context.startActivity(intent);
	}

	private String buildTime(int hourOfDay, int minute) {
		String hour = String.valueOf(hourOfDay);
		String min = String.valueOf(minute);

		if (hour.length() == 1) {
			hour = "0" + hour;
		}
		if (min.length() == 1) {
			min = "0" + min;
		}

		return hour + ":" + min;
	}

	private void toggle() {
		attendanceMode.setOnCheckedChangeListener(null);
		int checkedRadioButtonId = attendanceMode.getCheckedRadioButtonId();
		attendanceMode
				.check(checkedRadioButtonId == R.id.attendance_mode_simple ? R.id.attendance_mode_complex
						: R.id.attendance_mode_simple);
		changeViewState(attendanceMode.getCheckedRadioButtonId() == R.id.attendance_mode_simple);
		attendanceMode.setOnCheckedChangeListener(this);
	}

	private void changeTip(boolean isChecked) {
		String tips = "您需要使用对应的管理网站设置精细型巡检规则。如未设置，请在PC端访问http://yjt.189.cn选择管理员入口登录后进行配置。";
		if (isChecked) {
			tips = "本企业所有人员将依据您在本规则模式下设置的时间进行巡检，周六日为休息日。如您在PC端设置了精细型巡检规则，则那些规则将会失效，但您可以切换选择，重新启用精细型巡检规则。";
		}
		this.tips.setText(tips);
	}

	private void requestChangedMode(boolean isChecked) {
		requestEnableTimeAttendance(isChecked);
	}

	private void requestEnableTimeAttendance(final boolean isChecked) {
        PatrolApiHelper.setPatrolTimeStatus(new ProgressDialogResponseListener<Object>(this, "正在提交...") {

            @Override
            public void onSuccess(Object payload) {

            }

            @Override
            public void onErrorResponse(Response<Object> response) {
                super.onErrorResponse(response);
                toggle();
            }

            @Override
            public void onErrorResponse(InvocationError error) {
                super.onErrorResponse(error);
                toggle();
            }

        },AccountManager.getCurrent(getApplicationContext()).getCustId(),isChecked);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		boolean isChecked = checkedId == R.id.attendance_mode_simple;

        if(!isChecked){
            /*记录操作 1721*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_SETTING_COMPLEX_PATROLTIME);
        }else{
            if(multiSegmentSetting.isChecked()){
                /*记录操作 1723*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_SETTING_DOUBLE_PATROLTIME);
            }else{
                /*记录操作 1722*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_SETTING_SIGNLE_PATROLTIME);
            }
        }

		requestChangedMode(isChecked);
		changeViewState(isChecked);
	}

	private void changeViewState(boolean isChecked) {
		changeTip(isChecked);
		findViewById(R.id.time_layout).setVisibility(
				isChecked ? View.VISIBLE : View.GONE);
		findViewById(R.id.attendance_complex_intr).setVisibility(
				!isChecked ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.cb_multi_segment_setting:
			if (isChecked) {
				tvMultiSegmentSetting.setText("双班段开启");
				multiTimeLayout.setVisibility(View.VISIBLE);

                /*记录操作 1723*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_SETTING_DOUBLE_PATROLTIME);

			} else {
				tvMultiSegmentSetting.setText("双班段关闭");
				multiTimeLayout.setVisibility(View.INVISIBLE);

                /*记录操作 1722*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_SETTING_SIGNLE_PATROLTIME);
			}
			break;
		}
	}
}
