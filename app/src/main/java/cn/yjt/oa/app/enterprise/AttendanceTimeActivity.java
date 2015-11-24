package cn.yjt.oa.app.enterprise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.AttendanceTime;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.ResponseListener;

public class AttendanceTimeActivity extends BackTitleFragmentActivity {

	//private static Handler mHandler = new Handler();
	private boolean isAlive = true;
	@Override
	protected void onCreate(Bundle savedState) {
		// TODO Auto-generated method stub
		super.onCreate(savedState);
		setContentView(R.layout.activity_attendance_time);
		getLeftbutton().setImageResource(R.drawable.navigation_close);
		isAlive = true;
		ApiHelper
				.getAttendanceTimes(new ResponseListener<ListSlice<AttendanceTime>>() {

					@Override
					public void onSuccess(ListSlice<AttendanceTime> payload) {
						Intent intent = new Intent(AttendanceTimeActivity.this,
								AttendanceTimeSettingActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("payload", payload);
						intent.putExtras(bundle);

						final Intent tempintent = intent;
						ApiHelper
								.getTimeAttendanceStatus(new ResponseListener<Boolean>() {
									Intent intent = tempintent;

									@Override
									public void onSuccess(Boolean payload) {
										intent.putExtra("payload2", payload);
										if(isAlive){
											AttendanceTimeSettingActivity.launch(
													AttendanceTimeActivity.this,
													intent);
											finish();
										}
									}

									@Override
									public void afterFinish() {
										super.afterFinish();
									}
								});
					}
				});

	}

	@Override
	public void onLeftButtonClick() {
		super.onLeftButtonClick();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isAlive = false;
	}
	
	
	public static void launch(Context context) {
		Intent intent = new Intent(context, AttendanceTimeActivity.class);
		context.startActivity(intent);
	}
}
