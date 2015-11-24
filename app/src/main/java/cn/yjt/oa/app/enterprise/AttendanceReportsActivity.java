package cn.yjt.oa.app.enterprise;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.builder.EmailDialogBuilder;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.patrol.activitys.PatrolReportsActivity;
import cn.yjt.oa.app.personalcenter.FeedTypeAdapter;
import cn.yjt.oa.app.utils.CalendarUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.ToastUtils;
import io.luobo.common.http.InvocationError;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.FileRequest;
import cn.yjt.oa.app.http.RequestQueueHolder;
import cn.yjt.oa.app.utils.FileUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.NoCache;
import com.umeng.analytics.MobclickAgent;

public class AttendanceReportsActivity extends TitleFragmentActivity implements
		OnClickListener, Listener<File>, ErrorListener , OnCancelListener{

    private final String TIME_THIS_WEEK = "本周";
    private final String TIME_LAST_WEEK = "上周";
    private final String TIME_THIS_MONTH = "本月";
    private final String TIME_LAST_MONTH = "上月";
    private final String TIME_UNSELECTED = "请选择时间段";

    private final String REPORT_CHECK = "签到明细";
    private final String REPORT_ATTENDANCE = "考勤明细";
    private final String REPORT_DISCIPLINE = "考勤违纪";
    private final String REPORT_SUMMARY = "考勤汇总";
    private final String REPORT_UNSELECTED = "请选择报表类型";

    private final String[] mTimeType = new String[]{TIME_THIS_WEEK
            , TIME_LAST_WEEK
            , TIME_THIS_MONTH
            , TIME_LAST_MONTH};
    private final String[] mReportType = new String[]{REPORT_CHECK
            , REPORT_ATTENDANCE
            , REPORT_DISCIPLINE
            , REPORT_SUMMARY};

    private FileRequest fileRequest;
    RequestQueue queue;
    private ProgressDialog downloadProgressDialog;
    private ProgressDialog emailProgressDialog;

    private RelativeLayout mRlChoiceTime;
    private TextView mTvTimeType;
    private LinearLayout mLlTimeType;

    private RelativeLayout mRlChoiceReport;
    private TextView mTvReportType;
    private LinearLayout mLlReportType;

    private FeedTypeAdapter mTimeAdapter;
    private FeedTypeAdapter mReportAdapter;


    private Date mBeginDate;
    private Date mEndDate;
    private String mRequestUrl = new String();
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_attendance_reports);
        initQueue();
        initChoiceTimeView();
        initChoiceReportView();
        getLeftbutton().setImageResource(R.drawable.navigation_back);
        findViewById(R.id.btn_download_excel).setOnClickListener(this);
        findViewById(R.id.btn_email_excel).setOnClickListener(this);
    }

    private void initQueue() {
        queue = new RequestQueue(new NoCache()
                , new BasicNetwork(RequestQueueHolder.getInstance().getHttpStack()));
        queue.start();
    }

    private void initChoiceTimeView() {
        mRlChoiceTime = (RelativeLayout) findViewById(R.id.rl_choice_time);
        mTvTimeType = (TextView) findViewById(R.id.tv_time_type);
        mLlTimeType = (LinearLayout) findViewById(R.id.ll_time_type);

        mTvTimeType.setText(TIME_UNSELECTED);

        mTimeAdapter = new FeedTypeAdapter(this, mTimeType);
        mRlChoiceTime.setOnClickListener(this);
    }

    private void initChoiceReportView() {
        mRlChoiceReport = (RelativeLayout) findViewById(R.id.rl_choice_report);
        mTvReportType = (TextView) findViewById(R.id.tv_report_type);
        mLlReportType = (LinearLayout) findViewById(R.id.ll_report_type);

        mTvReportType.setText(REPORT_UNSELECTED);

        mReportAdapter = new FeedTypeAdapter(this, mReportType);

        //实体企业并且使用考勤机，只有一种考勤报表(签到明细)。
        if( AccountManager.getCurrent(MainApplication.getAppContext()).getIsYjtUser() == 1
                && AccountManager.getCurrent(MainApplication.getAppContext()).getUseCardMachine() != -1 ){
            mTvReportType.setText(REPORT_CHECK);
            findViewById(R.id.iv_expandable).setVisibility(View.INVISIBLE);
        }else{
            mRlChoiceReport.setOnClickListener(this);
        }

    }

    @Override
    protected void onDestroy() {
        queue.stop();
        queue = null;
        super.onDestroy();
    }


    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_download_excel:
                download();

            /*记录操作 0828*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_DOWNLOAD_ATTENDANCE_REPORT);

                break;
            case R.id.btn_email_excel:
                emailExcel();

            /*记录操作 0827*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_EMAIL_ATTENDANCE_REPORT);

                break;

            case R.id.rl_choice_time:
                bindTypeLinearLayout(mLlTimeType, mRlChoiceTime, mTvTimeType, mTimeAdapter);
                break;

            case R.id.rl_choice_report:
                bindTypeLinearLayout(mLlReportType, mRlChoiceReport, mTvReportType, mReportAdapter);
                break;


            default:
                break;
        }
    }


    private void bindTypeLinearLayout(final LinearLayout typeLl, final RelativeLayout choiceRl, final TextView typeTv, FeedTypeAdapter adapter) {

        if (typeLl.getChildCount() > 0) {
            typeLl.removeAllViews();
            choiceRl.setBackgroundResource(R.drawable.feed_back_bg);
        } else {
            choiceRl.setBackgroundResource(R.drawable.feed_type_top_nomal);
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                View v = adapter.getView(i, null, null);
                v.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        TextView typeText = (TextView) v.findViewById(R.id.type_text);
                        typeTv.setText(typeText.getText());
                        typeLl.removeAllViews();
                        choiceRl.setBackgroundResource(R.drawable.feed_back_bg);
                    }
                });
                typeLl.addView(v);
            }
        }

    }

    /** 校验邮箱 */
    private boolean checkEmail(String text) {
        if (!TextUtils.isEmpty(text)) {
            return text.matches("^.+@.+\\..+$");
        }
        return false;
    }

    /** 发送excel表格到邮箱 */
    private void emailExcel() {
        if (!checkTimeRange()) {
            return;
        }
        if (!checkReportType()) {
            return;
        }

        MobclickAgent.onEvent(this, "patrol_reports_sendemail");
        EmailDialogBuilder builder = EmailDialogBuilder.getInstance(this);
        builder.setOnButtonClickListener(new EmailDialogBuilder.OnButtonClickListener() {

            @Override
            public void positiveButtonClick(String text) {
                sendEmail(text);
            }

            @Override
            public void negativeButtonClick() {

            }
        });
        builder.buildInputEmailDialog();
    }

    /** 发送邮件 */
    private void sendEmail(final String email) {

        if (!checkEmail(email)) {
            Toast.makeText(getApplicationContext(), "请填写正确的邮箱", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiHelper.emailAttendanceExcel(sendEmailListener,email
                ,getEmailModule()
                ,mBeginDate
                ,mEndDate);
//      MeetingApiHelper.emailSigninExcel(sendEmailListener, email, mMeetingId);
        emailProgressDialog = ProgressDialog.show(this, null, "正在请求...");
        emailProgressDialog.setCancelable(false);
    }

    /** 检测时间段的选择，设置开始和结束时间点的值 */
    private boolean checkTimeRange() {
        Calendar calendar = Calendar.getInstance();
        String timeType = mTvTimeType.getText().toString();
        if (TextUtils.isEmpty(timeType)) {
            return false;
        }
        //设置每个时间段的起始和结束日期
        if (TIME_THIS_WEEK.equals(timeType)) {
            //如果是查看本周，那么开始时间就为本周周一
            mBeginDate = CalendarUtils.getThisWeekMonday();
            mEndDate = calendar.getTime();

        } else if (TIME_LAST_WEEK.equals(timeType)) {
            //如果查看的上周，那么开始时间就为上周一，结束时间为上周日
            mBeginDate = CalendarUtils.getLastWeekMonday();
            mEndDate = CalendarUtils.getLastWeekSunday();

        } else if (TIME_THIS_MONTH.equals(timeType)) {
            //如果查看的本月，那么开始时间就为本月第一天
            mBeginDate = CalendarUtils.getThisMonthFirstday();
            mEndDate = calendar.getTime();

        } else if (TIME_LAST_MONTH.equals(timeType)) {
            //如果查看的上月，那么开始时间为上个月第一天，结束时间为上个月最后一天
            mBeginDate = CalendarUtils.getLastMonthFirstday();
            mEndDate = CalendarUtils.getLastMonthLastday();

        } else if (TIME_UNSELECTED.equals(timeType)) {
            ToastUtils.shortToastShow("请选择时间段");
            return false;
        }
        return true;
    }

    private boolean checkReportType() {
        String reportType = mTvReportType.getText().toString();
        if (TextUtils.isEmpty(reportType)) {
            return false;
        }
        if (REPORT_CHECK.equals(reportType)) {
            mRequestUrl = AsyncRequest.MODULE_CUSTSIGN_CARD;
        } else if (REPORT_ATTENDANCE.equals(reportType)) {
            mRequestUrl = AsyncRequest.MODULE_CUSTSIGN_DETAIL;
        } else if (REPORT_DISCIPLINE.equals(reportType)) {
            mRequestUrl = AsyncRequest.MODULE_CUSTSIGN_DISOBEY;
        } else if (REPORT_SUMMARY.equals(reportType)) {
            mRequestUrl = AsyncRequest.MODULE_CUSTSIGN_SUMMARY;
        } else if (REPORT_UNSELECTED.equals(reportType)) {
            ToastUtils.shortToastShow("请选择报表类型");
            return false;
        }
        return true;
    }

    private void download() {
        if (!checkTimeRange()) {
            return;
        }
        if (!checkReportType()) {
            return;
        }
        MobclickAgent.onEvent(this,
                "enterprise_manage_attendancereports_download");
        try {
            String url = new AsyncRequest.Builder()
                    .setModule(getDownloadModule())
					.addDateQueryParameterStartTime(
                            mBeginDate,
                            mEndDate)
                    .build().getRequest().getUrl();

            fileRequest = new FileRequest(url, AttendanceReportsActivity.this,
                    FileUtils.getUserFolder(), AttendanceReportsActivity.this);
            fileRequest.setRetryPolicy(new RetryPolicy() {

                @Override
                public void retry(VolleyError arg0) throws VolleyError {

                }

                @Override
                public int getCurrentTimeout() {
                    return 30000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 0;
                }
            });
            queue.add(fileRequest);
            downloadProgressDialog = ProgressDialog.show(this, null, "正在下载报表...");
            downloadProgressDialog.setOnCancelListener(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }


    public static void launch(Context context) {
        Intent intent = new Intent(context, AttendanceReportsActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onErrorResponse(VolleyError arg0) {
        if (downloadProgressDialog != null) {
            downloadProgressDialog.dismiss();
        }
        fileRequest = null;
        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onResponse(File arg0) {
        if (downloadProgressDialog != null) {
            downloadProgressDialog.dismiss();
        }
        fileRequest = null;
        Toast.makeText(getApplicationContext(),
                "报表已下载到：" + arg0.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }

    io.luobo.common.http.Listener<Response<String>> sendEmailListener = new io.luobo.common.http.Listener<Response<String>>() {

        @Override
        public void onErrorResponse(InvocationError arg0) {
            if (emailProgressDialog != null) {
                emailProgressDialog.dismiss();
            }
            Toast.makeText(getApplicationContext(), "请求失败，请重试", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(Response<String> arg0) {
            if (emailProgressDialog != null) {
                emailProgressDialog.dismiss();
            }
            if (arg0.getCode() == 0) {
                Toast.makeText(getApplicationContext(), "报表已发送到您的邮箱", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), arg0.getDescription(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCancel(DialogInterface dialog) {
        if (downloadProgressDialog == dialog) {
            fileRequest.cancel();
        }
    }

    /**获取发送邮箱的url*/
    private String getEmailModule(){
        return String.format(mRequestUrl, AccountManager.getCurrent(MainApplication.getAppContext())
                .getCustId())+AsyncRequest.ATTENDANCE_SENDMAIL;
    }

    /**获取下载报表的url*/
    private String getDownloadModule(){
        return String.format(mRequestUrl, AccountManager.getCurrent(MainApplication.getAppContext())
                .getCustId())+AsyncRequest.ATTENDANCE_DOWNLOAD;
    }
}
