package cn.yjt.oa.app.patrol.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.AttendanceTime;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;

public class PatrolTimeActivity extends BackTitleFragmentActivity {

    //private static Handler mHandler = new Handler();
    public static final String PATROL_TIME = "patrol_time";
    public static final String PATROL_STATUS = "patrol_status";
    private boolean isAlive = true;


    @Override
    protected void onCreate(Bundle savedState) {
        // TODO Auto-generated method stub

        super.onCreate(savedState);
        setContentView(R.layout.activity_attendance_time);
        getLeftbutton().setImageResource(R.drawable.navigation_close);
        isAlive = true;
        requestPatrolStatus();
    }

    private void requestPatrolStatus() {

        PatrolApiHelper.getPatrolTimeStatus(new ResponseListener<Boolean>() {
            @Override
            public void onSuccess(Boolean payload) {
                Intent mIntent = new Intent(PatrolTimeActivity.this,
                        PatrolTimeSettingActivity.class);
                mIntent.putExtra(PATROL_STATUS, payload);
                requestData(mIntent);
            }
        }, AccountManager.getCurrent(getApplicationContext()).getCustId());
    }

    private void requestData(final Intent intent) {
        PatrolApiHelper.getPatrolTime(new ResponseListener<ListSlice<AttendanceTime>>() {
            @Override
            public void onSuccess(ListSlice<AttendanceTime> payload) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(PATROL_TIME, payload);
               intent.putExtras(bundle);
                if (isAlive) {
                    PatrolTimeSettingActivity.launch(
                            PatrolTimeActivity.this,
                            intent);
                    finish();
                }
            }
        }, AccountManager.getCurrent(getApplicationContext()).getCustId());
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
        Intent intent = new Intent(context, PatrolTimeActivity.class);
        context.startActivity(intent);
    }
}
