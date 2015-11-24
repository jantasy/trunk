package cn.yjt.oa.app.patrol.activitys;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.activeandroid.query.Select;

import java.util.List;

import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.OperaStatistics;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.permisson.PermissionManager;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.ViewUtil;


/**
 * 巡检总页面
 * Created by 熊岳岳 on 2015/9/22.
 */
public class PatrolSummaryActivity extends TitleFragmentActivity implements View.OnClickListener {

    private final String TAG = "PatrolSummaryActivity";

    private CardView mCvPatrolManager;
    private CardView mCvReportForm;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_patrol_summary);

        if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
            LaunchActivity.launch(this);
            finish();
        } else {
              /*记录操作 1701*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_PATROL);

            initView();
            fillData();
            setListener();
        }

    }

    @Override
    protected void onDestroy() {
        List<OperaStatistics> list = new Select().from(OperaStatistics.class).execute();
        for(OperaStatistics info :list){
//            Toast.makeText(this, info.getOperaEventNo() + ":" + info.getTime(), Toast.LENGTH_SHORT).show();
            LogUtils.i(TAG,"操作编号："+info.getOperaEventNo() + "---"+"" + info.getTime());
        }
        super.onDestroy();
    }

    private void initView() {
       mCvPatrolManager = (CardView) findViewById(R.id.cv_patrol_manager);
       mCvReportForm = (CardView) findViewById(R.id.cv_report_form);
    }

    private void fillData() {
        //TODO:权限控制
        if (PermissionManager.verifyCode(AccountManager.getCurrent(getApplicationContext()).getPermission(), PermissionManager.PERMISSION_CODE_INSPECT_CONFIG)) {
            mCvPatrolManager.setVisibility(View.VISIBLE);
            mCvReportForm.setVisibility(View.VISIBLE);
        }
        if(PermissionManager.verifyCode(AccountManager.getCurrent(getApplicationContext()).getPermission(), PermissionManager.PERMISSION_CODE_GET_INSPECT_REPORT)) {
            mCvReportForm.setVisibility(View.VISIBLE);
        }
        getLeftbutton().setImageResource(R.drawable.navigation_back);
    }

    private void setListener() {
        findViewById(R.id.cv_patrol_manager).setOnClickListener(this);
        findViewById(R.id.cv_patrol_record).setOnClickListener(this);
        findViewById(R.id.cv_patrol_location).setOnClickListener(this);
        findViewById(R.id.cv_report_form).setOnClickListener(this);
        findViewById(R.id.cv_patrol_remind).setOnClickListener(this);
    }

    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.cv_patrol_manager:
                /*记录操作 1704*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_PATROL_MANAGE);
                PatrolManageActivity.launch(this);
                break;

            case R.id.cv_patrol_record:
               /*记录操作 1705*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_PATROL_RECORD);
                PatrolRecordActivity.launch(this);
                break;

//
            case R.id.cv_patrol_location:
                /*记录操作 1703*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_LOCATING_PATROL);
                PatrolActivity.launchWithInspectInfo(this, null);
                break;

            //点击巡检报表下载
            case R.id.cv_report_form:
                PatrolReportsActivity.launch(this);
                break;

            //点击巡检提醒
            case R.id.cv_patrol_remind:
                PatrolRemindActivity.launch(this);
                break;

            default:
                break;

        }
    }
}
