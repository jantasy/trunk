package cn.yjt.oa.app.patrol.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.PatrolAttendanceInfo;
import cn.yjt.oa.app.builder.DateTimePickDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.meeting.utils.DateUtils;
import cn.yjt.oa.app.patrol.adapter.PatrolRecordAdapter;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.utils.ViewUtil;
import cn.yjt.oa.app.widget.SlidingDrawer;

/**
 * 巡检记录界面
 *
 * @author 熊岳岳
 * @since 20150731
 */
public class PatrolRecordActivity extends TitleFragmentActivity implements View.OnClickListener {

    public final static String COME_NOTIFICATION = "come_notification";
    private final String TAG = "PatrolRecordActivity";

    /** 自定义的下拉刷新和上拉加载的listview，用来展示巡检点信息 */
    private ListView mPrlvShoList;
    /** 适配巡检点listview的适配器 */
    private PatrolRecordAdapter mAdapter;

    /** 一个滑动的抽屉控件 */
    private SlidingDrawer slidingDrawer;
    /** 抽屉控件下方那个拉出或者推回抽屉的控件 */
    private ImageView handle;

    private TextView mTvStartTime;
    private TextView mTvStopTime;

    private List<PatrolAttendanceInfo> mDatas;

    /**是否是从通知页面跳转过来的*/
    private boolean isNotificationCome = false;

    /*-----生命周期方法START-----*/
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        if (ViewUtil.noLoginToLaunch(this)) {
            LaunchActivity.launch(this);
            finish();
        } else {
            setContentView(R.layout.activity_patrol_record);
            initParams();
            initView();
            fillData();
            setListener();
            searchData();
        }
    }

	/*-----生命周期方法END-----*/

	/*-----onCreate中执行的方法START-----*/

    /** 向控件中填充数据 */
    private void initParams() {
        isNotificationCome = getIntent().getBooleanExtra(COME_NOTIFICATION,false);
        mAdapter = new PatrolRecordAdapter(this);
        mDatas = new ArrayList<>();
    }

    /** 初始化控件 */
    private void initView() {
        mPrlvShoList = (ListView) findViewById(R.id.prlv_show_list);
        slidingDrawer = (SlidingDrawer) findViewById(R.id.sd_patrol_time);
        mTvStartTime = (TextView) findViewById(R.id.tv_start_time);
        mTvStopTime = (TextView) findViewById(R.id.tv_stop_time);
        handle = (ImageView) findViewById(R.id.handle);
    }

    /** 往控件中填充数据 */
    private void fillData() {
        mPrlvShoList.setAdapter(mAdapter);
        getLeftbutton().setImageResource(R.drawable.navigation_back);
        slidingDrawer.setVisibility(View.VISIBLE);
        int height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        int expandedOffset = height - getResources().getDimensionPixelSize(R.dimen.dimen_64dp)
                - getResources().getDimensionPixelSize(R.dimen.dimen_47dp) * 2
//                -getResources().getDimensionPixelSize(R.dimen.dimen_64dp)
                - getResources().getDimensionPixelSize(R.dimen.dimen_18_3dp);
        slidingDrawer.setExpandedOffset(expandedOffset);
        initDate();
    }

    /** 初始化时间 */
    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        mTvStopTime.setText(DateUtils.sDateFormat.format(calendar.getTime()) + "");
        if(isNotificationCome){
            mTvStartTime.setText(DateUtils.sDateFormat.format(calendar.getTime()) + "");;
        }else{
            calendar.add(Calendar.WEEK_OF_MONTH, -1);
            mTvStartTime.setText(DateUtils.sDateFormat.format(calendar.getTime()) + "");
        }
    }

    /** 设置监听 */
    private void setListener() {
        mAdapter.setListViewAndListener(mPrlvShoList);
        //抽屉控件开打监听
        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {

            @Override
            public void onDrawerOpened() {
                handle.setImageResource(R.drawable.handle_up);
            }
        });
        //抽屉控件关闭监听
        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {

            @Override
            public void onDrawerClosed() {
                handle.setImageResource(R.drawable.handle_down);
            }
        });
        mTvStartTime.setOnClickListener(this);
        mTvStopTime.setOnClickListener(this);
        findViewById(R.id.tv_search).setOnClickListener(this);
    }

    /** 设置查询时间段开始时间 */
    private void setStartTime() {
        DateTimePickDialogBuilder builder = DateTimePickDialogBuilder.getIntances(this, DateUtils.parseDateFormat(mTvStartTime.getText().toString()));
        builder.setOnButtonClickListener(new DateTimePickDialogBuilder.OnButtonClickListener() {
            @Override
            public void positiveButtonClick(Date dataTime) {
                Calendar calendar = Calendar.getInstance();
                if(dataTime.compareTo(calendar.getTime())>=0){
                    Toast.makeText(getApplicationContext(), "请勿穿越到未来", Toast.LENGTH_SHORT).show();
                }else{
                mTvStartTime.setText(DateUtils.sDateFormat.format(dataTime));

                }
            }

            @Override
            public void negativeButtonClick() {

            }
        });
        builder.buildDatePickerDialog();

    }

    /** 设置查询时间段结束时间 */
    private void setStopTime() {
        DateTimePickDialogBuilder builder = DateTimePickDialogBuilder.getIntances(this, DateUtils.parseDateFormat(mTvStopTime.getText().toString()));
        builder.setOnButtonClickListener(new DateTimePickDialogBuilder.OnButtonClickListener() {
            @Override
            public void positiveButtonClick(Date dataTime) {
                Calendar calendar = Calendar.getInstance();
                if(dataTime.compareTo(calendar.getTime())>=0){
                    Toast.makeText(getApplicationContext(), "请勿穿越到未来", Toast.LENGTH_SHORT).show();
                }else{
                    mTvStopTime.setText(DateUtils.sDateFormat.format(dataTime));
                }

            }

            @Override
            public void negativeButtonClick() {

            }
        });
        builder.buildDatePickerDialog();
    }

    private void searchData() {
        slidingDrawer.close();
        Date startDate = DateUtils.parseDateFormat(mTvStartTime.getText().toString());
        Date stopDate = DateUtils.parseDateFormat(mTvStopTime.getText().toString());
        if (startDate != null && stopDate != null && stopDate.compareTo(startDate) >= 0) {
            mDatas.clear();
            mAdapter.setDataListsAndRefresh(mDatas);
            PatrolApiHelper.getPatrolRecord(new ProgressDialogResponseListener<List<List<PatrolAttendanceInfo>>>(this, "正在查询巡检记录") {
                @Override
                public void onSuccess(List<List<PatrolAttendanceInfo>> payload) {

                    Toast.makeText(getApplicationContext(), "查询成功", Toast.LENGTH_SHORT).show();
                    if (payload != null) {
                        for (List<PatrolAttendanceInfo> list : payload) {
                            mDatas.addAll(list);
                        }
                        mAdapter.setDataListsAndRefresh(mDatas);
                    }
                }
            }, DateUtils.sDateRequestFormat.format(startDate), DateUtils.sDateRequestFormat.format(stopDate));
        } else {
            Toast.makeText(this, "查询时间段的结束时间必须大于起始时间", Toast.LENGTH_SHORT).show();
        }
    }
    /*-----onCreate中执行的方法END-----*/

    /** 开启PatrolManageActivity界面 */
    public static void launch(Context context) {
        Intent intent = new Intent(context, PatrolRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onRightButtonClick() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start_time:
                setStartTime();
                break;

            case R.id.tv_stop_time:
                setStopTime();
                break;

            case R.id.tv_search:
                searchData();
            default:
                break;
        }
    }


}
