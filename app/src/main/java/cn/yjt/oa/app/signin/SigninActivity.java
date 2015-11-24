package cn.yjt.oa.app.signin;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.CardCheckinInfo;
import cn.yjt.oa.app.beans.ClockInInfo;
import cn.yjt.oa.app.beans.DutyInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.signin.adapter.AttendanceRecordsAdapter;
import cn.yjt.oa.app.utils.Config;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.ViewUtil;
import cn.yjt.oa.app.widget.SlidingDrawer;
import cn.yjt.oa.app.widget.SlidingDrawer.OnDrawerCloseListener;
import cn.yjt.oa.app.widget.SlidingDrawer.OnDrawerOpenListener;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshExpandableListView;

/**
 * 查看考勤记录的界面
 */
public class SigninActivity extends TitleFragmentActivity implements OnChildClickListener{

    private final static String TAG = "SigninActivity";

    /** 下拉刷新多层级listview */
    private PullToRefreshExpandableListView listView;
    /** 一个滑动的抽屉控件 */
    private SlidingDrawer slidingDrawer;
    /** 抽屉控件下方那个拉出或者推回抽屉的控件 */
    private ImageView handle;
    /** 查看他人考勤的选择时间对话框 */
    private AlertDialog mAlertDialog;
    /** 选择他人考勤时间段对话框中添加的布局 */
    private View mDialogView;
    /** 对话框的radioGroup组 */
    private RadioGroup mRgAttendanceTime;

    /** 考勤数据监听者 */
    private AttendanceListener listener;
    /** 考勤记录适配器 */
    private AttendanceRecordsAdapter adapter;

    /** 用户的id */
    private long userId;

    /** 查询的开始时间 */
    private Date beginDate;
    /** 查询的开始结束 */
    private Date endDate;
    /** 定一个标识符 */
    private boolean isFirstLoad = true;
    /** 表示从查看成员考勤页面过来的标识 */
    private String launchActivity = " ";

    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
            LaunchActivity.launch(this);
            finish();
        } else {
             /*记录操作 0823*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_ATTENDANCE_RECORD);

            setContentView(R.layout.attendance_record);
            //从意图中获取用户的id
            userId = getIntent().getLongExtra("userId", 0);
            launchActivity = getIntent().getStringExtra("TAG");
            if ("SigninStructActivity".equals(launchActivity)) {
                beginDate = new Date(getIntent().getLongExtra("beginDate", Calendar.getInstance().getTimeInMillis()));
                endDate = new Date(getIntent().getLongExtra("endDate", Calendar.getInstance().getTimeInMillis()));
                String temp = getIntent().getStringExtra("userName");
                String name = temp != null ? temp : "匿名";
                setTitle("(" + name + ")" + "考勤记录");
            }
            //初始化适配器和
            adapter = new AttendanceRecordsAdapter(this);
            listener = new AttendanceListener();

            //初始化控件
            listView = (PullToRefreshExpandableListView) findViewById(R.id.attendance_records_listview);
            slidingDrawer = (SlidingDrawer) findViewById(R.id.duty_sliding);
            handle = (ImageView) findViewById(R.id.handle);

            //控件填充数据
            listView.setAdapter(adapter);
            getLeftbutton().setImageResource(R.drawable.navigation_back);
            getRightButton().setImageResource(R.drawable.attendance_member);

            //控件设置监听
            setListener();

            //控件的状态设置
            //如果是当前用户的就显示右上角，如果是其他用户就不显示
            //TODO:实体企业和互联网企业
            if ((userId == 0 || userId == AccountManager.getCurrentSimpleUser(this).getId())
                    && !"SigninStructActivity".equals(launchActivity)) {
                setTitle("我的考勤记录");
                getRightButton().setVisibility(View.VISIBLE);
            } else {
                getRightButton().setVisibility(View.GONE);
            }
            LogUtils.e(TAG, AccountManager.getCurrentSimpleUser(this).getId() + "");
            slidingDrawer.setVisibility(View.GONE);
            listView.setRefreshingState();
            listener.onRefresh();

            //请求考勤信息
            requestDuty();
        }
    }

    /** 控件设置监听 */
    public void setListener() {
        listView.setOnChildClickListener(this);
        listView.setOnRefreshListener(listener);
        listView.setOnLoadMoreListner(listener);

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                 /*记录操作 0825*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_EXPAND_CARD_RECORD);
            }
        });
        //抽屉控件开打监听
        slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

            @Override
            public void onDrawerOpened() {
                handle.setImageResource(R.drawable.handle_up);

                 /*记录操作 0826*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_EXPAND_TODAY_CLASS);
            }
        });
        //抽屉控件关闭监听
        slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

            @Override
            public void onDrawerClosed() {
                handle.setImageResource(R.drawable.handle_down);
            }
        });
    }

    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onRightButtonClick() {
        super.onRightButtonClick();
        initDailog();
    }

    /** 初始化，查询他人考勤时，选择考勤日期的对话框 */
    public void initDailog() {
        mDialogView = View.inflate(getApplicationContext(), R.layout.dialog_change_attendance_date, null);
        new AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("查看成员考勤，请选择要查看的时间段")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RadioButton checkedButton = (RadioButton) mDialogView.findViewById(mRgAttendanceTime
                                .getCheckedRadioButtonId());
                        SigninStructActivity.launchWithTime(checkedButton.getText().toString(), SigninActivity.this);

                         /*记录操作 0824*/
                        OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_ATTENDANCE_RECORD);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
        mRgAttendanceTime = (RadioGroup) mDialogView.findViewById(R.id.rg_attendance_time);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @param fromDate
     * @param mode     0:refresh , 1:loadmore
     */
    private void searchCardCheckin(Date fromDate, final int mode) {
        if (!"SigninStructActivity".equals(launchActivity)) {
            Calendar calendar = Calendar.getInstance();
            if (fromDate != null) {
                calendar.setTime(fromDate);
                calendar.add(Calendar.DATE, -1);
                fromDate = calendar.getTime();
                calendar.add(Calendar.DATE, 1);
            } else {
                //			calendar.add(Calendar.DATE, -1);
                fromDate = calendar.getTime();
            }
            calendar.add(Calendar.MONTH, -1);// 得到前一个月

            beginDate = calendar.getTime();
        }
        AsyncRequest.Builder builder = new AsyncRequest.Builder();
        builder.setModule(AsyncRequest.MODULE_ATTENDANCE_CARDLIST);
        builder.addDateQueryParameter(beginDate, fromDate);
        if (userId > 0) {
            builder.addQueryParameter("userId", String.valueOf(userId));
        }
        Type type = new TypeToken<Response<ListSlice<CardCheckinInfo>>>() {
        }.getType();
        builder.setResponseType(type);
        builder.setResponseListener(new Listener<Response<ListSlice<CardCheckinInfo>>>() {

            @Override
            public void onErrorResponse(InvocationError error) {

            }

            @Override
            public void onResponse(Response<ListSlice<CardCheckinInfo>> response) {
                if (response.getCode() == 0) {
                    List<CardCheckinInfo> content = response.getPayload().getContent();
                    if (mode == 0) {
                        adapter.setCardCheckinInfos(content);
                    } else {
                        adapter.addCardCheckinInfos(content);
                    }
                    adapter.notifyDataSetChangedWithCheckData();
                } else {
                    Toast.makeText(getApplicationContext(), response.getDescription(), Toast.LENGTH_SHORT).show();
                }
            }
        }).build().get();
    }

    /** 设置个人详细考勤信息 */
    private void searchClockin(Date fromDate, final int mode) {
        if (!"SigninStructActivity".equals(launchActivity)) {
            Calendar calendar = Calendar.getInstance();
            if (fromDate != null) {
                calendar.setTime(fromDate);
                calendar.add(Calendar.DATE, -1);
                fromDate = calendar.getTime();
                calendar.add(Calendar.DATE, 1);
            } else {
                //			calendar.add(Calendar.DATE, -1);
                fromDate = calendar.getTime();
            }
            calendar.add(Calendar.MONTH, -1);// 得到前一个月
            beginDate = calendar.getTime();
        }

        AsyncRequest.Builder builder = new AsyncRequest.Builder();
        builder.setModule(AsyncRequest.MODULE_CLOCKIN);
        builder.addDateQueryParameter(beginDate, fromDate);
        if (userId > 0) {
            builder.addQueryParameter("userId", String.valueOf(userId));
        }

        Type type = new TypeToken<Response<ListSlice<ClockInInfo>>>() {
        }.getType();
        builder.setResponseType(type);
        builder.setResponseListener(new Listener<Response<ListSlice<ClockInInfo>>>() {

            @Override
            public void onResponse(Response<ListSlice<ClockInInfo>> response) {
                completeLoading(mode);
                if (response.getCode() == 0) {
                    List<ClockInInfo> content = response.getPayload().getContent();
                    if (mode == 0) {
                        adapter.setClockInInfos(content);
                    } else {
                        adapter.addClockInInfos(content);
                    }
                    adapter.notifyDataSetChangedWithCheckData();
                } else {
                    Toast.makeText(getApplicationContext(), response.getDescription(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(InvocationError error) {
                completeLoading(mode);

            }

            private void completeLoading(final int mode) {
                if (mode == 0) {
                    listView.onRefreshComplete();
                } else {
                    listView.onLoadMoreComplete();
                }
            }
        });
        builder.build().get();
    }

    protected void showToastFail() {
        Toast.makeText(SigninActivity.this, getResources().getString(R.string.load_fail), Toast.LENGTH_SHORT).show();
    }

    public static void launchWithUserId(Context context, long userId) {
        Intent intent = new Intent(context, SigninActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    public static void launchWithUserIdAndDate(Context context, long userId, String userName, long beginDate, long endDate) {
        Intent intent = new Intent(context, SigninActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        intent.putExtra("beginDate", beginDate);
        intent.putExtra("endDate", endDate);
        intent.putExtra("TAG", "SigninStructActivity");
        context.startActivity(intent);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        CardCheckinInfo cardCheckinInfo = (CardCheckinInfo) parent.getExpandableListAdapter().getChild(groupPosition,
                childPosition);
        if (CardCheckinInfo.SINGIN_TYPE_VISIT.equals(cardCheckinInfo.getType())) {
            if (cardCheckinInfo.getPositionData() == null
                    || "0.005996,0.006494".equals(cardCheckinInfo.getPositionData())) {
                Toast.makeText(this, "没有地理位置", Toast.LENGTH_LONG).show();

            } else {
                Intent intent = new Intent(this, SigninShowPositionActivity.class);
                intent.putExtra(Config.POSITION_DATA, cardCheckinInfo.getPositionData());
                intent.putExtra(Config.POSITION_DES, cardCheckinInfo.getMachineName());
                startActivity(intent);
                return true;
            }
        }
        return false;
    }

    /**
     * 向服务器请求考勤属性信息，
     * 包括考勤时间段和考勤的区域名等
     */
    private void requestDuty() {
        Type responseType = new TypeToken<Response<ListSlice<DutyInfo>>>() {
        }.getType();
        Date date = Calendar.getInstance().getTime();
        new AsyncRequest.Builder().setModule(AsyncRequest.MODULE_ATTENDANCE_DUTY).addDateQueryParameter(date, date)
                .setResponseType(responseType).setResponseListener(new Listener<Response<ListSlice<DutyInfo>>>() {

            @Override
            public void onErrorResponse(InvocationError arg0) {

            }

            @Override
            public void onResponse(Response<ListSlice<DutyInfo>> response) {
                if (response.getCode() == 0) {
                    try {
                        if (response.getPayload().getContent() != null
                                && response.getPayload().getContent().size() > 0) {
                            buildDutyTimeViews(response.getPayload().getContent().get(0));
                            LogUtils.e(TAG, response.getPayload().getContent().get(0).toString());
                        }
                    } finally {
                    }
                }
            }
        }).build().get();
    }

    /**
     * 设置考勤属性信息的显示
     *
     * @param dutyInfo 考勤属性信息
     */
    private void buildDutyTimeViews(DutyInfo dutyInfo) {

        slidingDrawer.setVisibility(View.VISIBLE);

        //初始化抽屉控件的子空间
        LinearLayout layout = (LinearLayout) findViewById(R.id.duty_times_layout);
        TextView dutyText = (TextView) findViewById(R.id.duty_text);
        View content = findViewById(R.id.content);
        View handle = findViewById(R.id.handle);

        //初始化布局填充器
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        String[] onOffDutyTime = dutyInfo.getOnOffDutyTime();
        int height = getWindow().getWindowManager().getDefaultDisplay().getHeight();

        LogUtils.i(TAG, "layout.getHeight:" + layout.getMeasuredHeight());
        LogUtils.i(TAG, "content.getHeight:" + content.getMeasuredHeight());

        //显示考勤地域和考勤时间段到空间上
        dutyText.setText(dutyInfo.getDutyName());
        if (onOffDutyTime != null) {
            for (int i = 0; i < onOffDutyTime.length; i++) {
                View view = inflater.inflate(R.layout.item_duty_time, null);
                TextView textView = (TextView) view.findViewById(R.id.duty_time);
                textView.setText(onOffDutyTime[i]);
                layout.addView(view);
            }
            int expandedOffset = height - getResources().getDimensionPixelSize(R.dimen.dimen_64dp)
                    - getResources().getDimensionPixelSize(R.dimen.dimen_47dp)
                    - getResources().getDimensionPixelSize(R.dimen.dimen_36dp) - onOffDutyTime.length
                    * getResources().getDimensionPixelSize(R.dimen.dimen_31_4dp)
                    - getResources().getDimensionPixelSize(R.dimen.dimen_18_3dp);
            slidingDrawer.setExpandedOffset(expandedOffset);
        }
        LogUtils.i(TAG, "height:" + height);
        //System.out.println("expandedOffset:"+expandedOffset);
        //		slidingDrawer.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, content.getMeasuredHeight()));

    }

    /** 继承了下拉刷新和加载更多的接口的类 */
    private class AttendanceListener implements OnRefreshListener, OnLoadMoreListner {

        //简单的日期格式
        @SuppressLint("SimpleDateFormat")
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        /**
         * 加载更多
         */
        @Override
        public void onLoadMore() {
            ClockInInfo clockInInfo = (ClockInInfo) adapter.getGroup(adapter.getGroupCount() - 1);
            try {
                if (!"SigninStructActivity".equals(launchActivity)) {
                    searchClockin(dateFormat.parse(clockInInfo.getDutyDate()), 1);
                    searchCardCheckin(dateFormat.parse(clockInInfo.getDutyDate()), 1);
                } else {
                    listView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            listView.onLoadMoreComplete();
                        }
                    }, 2000);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        /**
         * 下拉刷新
         */
        @Override
        public void onRefresh() {
            if (!"SigninStructActivity".equals(launchActivity)) {
                searchClockin(null, 0);
                searchCardCheckin(null, 0);
            } else {
                if (isFirstLoad) {
                    searchClockin(endDate, 0);
                    searchCardCheckin(endDate, 0);
                    isFirstLoad = false;
                } else {
                    listView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            listView.onRefreshComplete();
                        }
                    }, 2000);

                }
            }
        }

    }
}
