package cn.yjt.oa.app.patrol.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.PatrolPointAttendanceInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.patrol.adapter.PatrolRecordDetailAdapter;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;

/**
 * 巡检记录界面
 * @author 熊岳岳
 * @since 20150731
 */
public class PatrolDetailRecordActivity extends TitleFragmentActivity {

	private final String TAG = "PatrolDetailRecordActivity";

	/** 自定义的下拉刷新和上拉加载的listview，用来展示巡检点信息 */
	private ListView mLvShowList;
	/** 适配巡检点listview的适配器 */
	private PatrolRecordDetailAdapter mAdapter;

    private final static String ROUTE_ID = "route_id";
    private final static String ROUTE_DATE = "route_date";
    private final static String ROUTE_TIME = "route_time";

    private long mRouteId;
    private String mDutyDate;
    private String mScheduleTime;
	/*-----生命周期方法START-----*/
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_patrol_detail_record);
		initParams();
		initView();
		fillData();
		setListener();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	/*-----生命周期方法END-----*/

	/*-----onCreate中执行的方法START-----*/
	/** 向控件中填充数据 */
	private void initParams() {
        mAdapter = new PatrolRecordDetailAdapter(this);
        mRouteId = getIntent().getLongExtra(ROUTE_ID,0);
        mDutyDate = getIntent().getStringExtra(ROUTE_DATE);
        mScheduleTime = getIntent().getStringExtra(ROUTE_TIME);

        PatrolApiHelper.getPatrolDetailRecord(new ProgressDialogResponseListener<List<PatrolPointAttendanceInfo>>(this,"正在加载该条巡检的详细信息") {
            @Override
            public void onSuccess(List<PatrolPointAttendanceInfo> payload) {
                mAdapter.setDataListsAndRefresh(payload);
            }
        },String.valueOf(mRouteId),String.valueOf(mDutyDate),String.valueOf(mScheduleTime));

	}

	/** 初始化控件 */
	private void initView() {
		mLvShowList = (ListView) findViewById(R.id.prlv_show_list);
	}

	/** 往控件中填充数据 */
	private void fillData() {
		mLvShowList.setAdapter(mAdapter);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}

	/** 设置监听 */
	private void setListener() {
        mAdapter.setListViewAndListener(mLvShowList);
	}

	/*-----onCreate中执行的方法END-----*/

	/** 开启PatrolManageActivity界面 */
	public static void launch(Context context,long id,String dutyDate,String scheduleTime) {
		Intent intent = new Intent(context, PatrolDetailRecordActivity.class);
        intent.putExtra(ROUTE_ID,id);
        intent.putExtra(ROUTE_DATE,dutyDate);
        intent.putExtra(ROUTE_TIME,scheduleTime);
		context.startActivity(intent);
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

}
