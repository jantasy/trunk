package cn.yjt.oa.app.signin;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.AttendanceSummaryInfo;
import cn.yjt.oa.app.beans.DeptAttendanceSummaryInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;
import cn.yjt.oa.app.utils.CalendarUtils;
import cn.yjt.oa.app.utils.ColorUtils;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.widget.NavigationView;
import cn.yjt.oa.app.widget.NavigationView.OnPopListener;

/**
 * 查看成员考勤汇总界面
 * <pre>
 * 以组织架构的形式显示当前用户有权查看的其他成员的考勤汇总情况，
 * 显示成员的 正常，打卡异常，计划外出勤，未计算，无打卡记录，当日无排班的信息汇总，
 * 如果查看日期选择的为’今日’，每一个人后面不显示汇总数据了,显示和考勤记录里一样的文字提示
 * <pre>
 * 
 * @author 熊岳岳
 */
public class SigninStructActivity extends TitleFragmentActivity implements OnPopListener, OnItemClickListener {

	private final static String TAG = "SigninStructActivity";

	/**表示考勤时间段为今日的常量*/
	private final static String TIME_TODAY = "今日";
	private final static String TIME_THIS_WEEK = "本周";
	private final static String TIME_LAST_WEEK = "上周";
	private final static String TIME_THIS_MONTH = "本月";
	private final static String TIME_LAST_MONTH = "上月";

	/**表示显示的条目种类的常量*/
	private final static int TYPE_COUNT = 2;
	/**表示成员类型的常量*/
	private final static int TYPE_MEMBER = 0;
	/**表示部门类型的常量*/
	private final static int TYPE_DEPT = 1;

	/**标题导航控件*/
	private NavigationView mNvStruct;
	/**放置标题导航的控件*/
	private HorizontalScrollView mHsNavigationview;
	/**考勤时间段为其他时间段时,成员列表的标题布局*/
	private LinearLayout mLlMemberTitle;
	/**显示部门和该部门成员的listView*/
	private ListView mLvStructMember;
	/**加载数据时显示的progressbar*/
	private ProgressBar mPbLoading;
	/**没有记录可查看时显示的提示语句*/
	private TextView mTvNoRight;

	/**总数据List*/
	private List<DeptAttendanceSummaryInfo> mDeptSummaryLists;
	/**适配部门的List*/
	private List<DeptAttendanceSummaryInfo> mDeptAdapterList;
	/**适配成员的List*/
	private List<AttendanceSummaryInfo> mMemberAdapterList;
	/**将每次展现的两个适配list封装成DeptAttendanceSummaryInfo对象，添加到该list中，用于导航控件*/
	private List<DeptAttendanceSummaryInfo> mOnceAdapterList;

	/**考勤信息汇总适配器适配器*/
	private AttendanceSummaryAdapter mAdapter;

	/**所要查看的考勤的时间段*/
	private String mAttendaceTime;
	/**所要查看的考勤记录的时间周期起始值*/
	private Date mBeginDate;
	/**所要查看的考勤记录的时间周期结束值*/
	private Date mEndDate;

	/**--------------------------Activity生命周期方法------------------------*/
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_singin_struct);
		initParam();
		initView();
		fillData();
		setListener();
		businessLogic();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**--------------------------------END--------------------------------*/

	/**--------------------------onCreate方法中的执行的方法------------------------*/
	/**初始化一些参数*/
	private void initParam() {
		//从开启该界面的意图中获取所要查看的考勤的时间段
		mAttendaceTime = getIntent().getStringExtra("time");
		LogUtils.i(TAG, "我要查看的是’" + mAttendaceTime + "'段时间的考勤");
		//初始化listview的适配器
		mAdapter = new AttendanceSummaryAdapter(mAttendaceTime);
		//初始化list集合
		mDeptSummaryLists = new ArrayList<DeptAttendanceSummaryInfo>();
		mDeptAdapterList = new ArrayList<DeptAttendanceSummaryInfo>();
		mMemberAdapterList = new ArrayList<AttendanceSummaryInfo>();
		mOnceAdapterList = new ArrayList<DeptAttendanceSummaryInfo>();
		//初始化所要查看的考勤汇总的时间段
		initDateTime();
	}

	/**初始化控件*/
	private void initView() {
		mHsNavigationview = (HorizontalScrollView) findViewById(R.id.hs_navigationview);
		mNvStruct = (NavigationView) findViewById(R.id.nv_struct);
		mLvStructMember = (ListView) findViewById(R.id.lv_structMember);
		mLlMemberTitle = (LinearLayout) findViewById(R.id.ll_member_title);
		mPbLoading = (ProgressBar) findViewById(R.id.pb_loading);
		mTvNoRight = (TextView) findViewById(R.id.tv_no_right);
	}

	/**控件中填充数据*/
	private void fillData() {
		//设置当前acitvity的标题显示
		setTitle("成员考勤记录(" + mAttendaceTime + ")");
		//设置标题左边按钮的背景图片
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		//将企业的名称添加到导航控件中
		mNvStruct.push(AccountManager.getCurrent(this).getCustName());
		//listview添加适配器
		mLvStructMember.setAdapter(mAdapter);
	}

	/**设置监听*/
	private void setListener() {
		//标题导航控件设置设置导航点击的监听
		mNvStruct.setOnPopListener(this);
		//listview设置条目点击的监听
		mLvStructMember.setOnItemClickListener(this);
	}

	/**业务逻辑*/
	private void businessLogic() {
		loadAttendanceSummaryData();
		//设置控件的显示
		mHsNavigationview.setVisibility(View.VISIBLE);
		mLlMemberTitle.setVisibility(View.GONE);
	}

	/**---------------------------------END---------------------------------*/

	/**标题左边按钮的，点击后关闭当前activity*/
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	/**标题导航控件中回调接口中方法*/
	@Override
	public void onPop(int index) {
		//如果mOnceAdapterList不为空，取出导航界面对应的适配集合
		if (mOnceAdapterList.size() == 0) {
			return;
		}
		mDeptAdapterList = mOnceAdapterList.get(index).getChildren();
		mMemberAdapterList = mOnceAdapterList.get(index).getMembers();

		testAdapterListIsEmpty();

		//把无法通过导航界面实现的适配集合删除
		for (int i = mOnceAdapterList.size() - 1; i > index; i--) {
			mOnceAdapterList.remove(i);
		}

		//刷新listview
		refreshListView();
	}

	/**listview的条目点击事件*/
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (mAdapter.getItemViewType(position)) {

		//成员条目被点击
		case TYPE_MEMBER:
			AttendanceSummaryInfo info = (AttendanceSummaryInfo) mAdapter.getItem(position);
			LogUtils.i(TAG, "您点击了：" + "成员：" + info.getName());
			//跳转到查看考勤详情的页面
			SigninActivity.launchWithUserIdAndDate(this, info.getUserId(), info.getName(), mBeginDate.getTime(),
					mEndDate.getTime());
			break;

		//部门条目被点击
		case TYPE_DEPT:
			DeptAttendanceSummaryInfo info2 = (DeptAttendanceSummaryInfo) mAdapter.getItem(position);
			LogUtils.i(TAG, "您点击了：" + "部门：" + info2.getName());
			//将点击的部门添加到导航控件中
			mNvStruct.push(mDeptAdapterList.get(position - mMemberAdapterList.size()).getName());
			//获取新的适配集合
			List<DeptAttendanceSummaryInfo> tempDeptAdapterList = mDeptAdapterList.get(
					position - mMemberAdapterList.size()).getChildren();
			mOnceAdapterList.add(mDeptAdapterList.get(position - mMemberAdapterList.size()));
			mMemberAdapterList = mDeptAdapterList.get(position - mMemberAdapterList.size()).getMembers();
			mDeptAdapterList = tempDeptAdapterList;
			testAdapterListIsEmpty();
			//刷新listview
			refreshListView();

		default:
			LogUtils.i(TAG, "部门条目和成员条目都没有被点击，我想应该不会走到这");
			break;
		}
	}

	/**刷新listView*/
	private void refreshListView() {

		//当前汇总的时间段不为‘今日’（因为考前时间段为今日的话，显示和其他时间段不同）
		//或者当前列表没有成员时，不显示成员考勤的标题
		if (mMemberAdapterList.size() > 0 && !TIME_TODAY.equals(mAttendaceTime)) {
			mLlMemberTitle.setVisibility(View.VISIBLE);
		} else {
			mLlMemberTitle.setVisibility(View.GONE);
		}
		mAdapter.notifyDataSetChanged();
	}

	/**启动方法*/
	public static void launchWithTime(String time, Context context) {
		Intent intent = new Intent(context, SigninStructActivity.class);
		intent.putExtra("time", time);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

	/**测试适配的list是否为空,如果为空，让他们不为空*/
	private void testAdapterListIsEmpty() {
		if (mMemberAdapterList == null) {
			mMemberAdapterList = new ArrayList<AttendanceSummaryInfo>();
		}

		if (mDeptAdapterList == null) {
			mDeptAdapterList = new ArrayList<DeptAttendanceSummaryInfo>();
		}
	}

	/**初始化时间段*/
	private void initDateTime() {
		Calendar calendar = Calendar.getInstance();

		//设置每个时间段的起始和结束日期
		if (TIME_TODAY.equals(mAttendaceTime)) {
			//如果是查看今日，那么开始时间和结束时间都是今天
			mBeginDate = calendar.getTime();
			mEndDate = calendar.getTime();

		} else if (TIME_THIS_WEEK.equals(mAttendaceTime)) {
			//如果是查看本周，那么开始时间就为本周周一
			mBeginDate = CalendarUtils.getThisWeekMonday();
			mEndDate = calendar.getTime();

		} else if (TIME_LAST_WEEK.equals(mAttendaceTime)) {
			//如果查看的上周，那么开始时间就为上周一，结束时间为上周日
			mBeginDate = CalendarUtils.getLastWeekMonday();
			mEndDate = CalendarUtils.getLastWeekSunday();

		} else if (TIME_THIS_MONTH.equals(mAttendaceTime)) {
			//如果查看的本月，那么开始时间就为本月第一天
			mBeginDate = CalendarUtils.getThisMonthFirstday();
			mEndDate = calendar.getTime();

		} else if (TIME_LAST_MONTH.equals(mAttendaceTime)) {
			//如果查看的上月，那么开始时间为上个月第一天，结束时间为上个月最后一天
			mBeginDate = CalendarUtils.getLastMonthFirstday();
			mEndDate = CalendarUtils.getLastMonthLastday();

		} else {
			LogUtils.e(TAG, "查看时间段异常");
		}
		//打印日志
		LogUtils.i(
				TAG,
				"起始日期：" + AsyncRequest.DATE_FORMAT.format(mBeginDate) + "\n结束日期："
						+ AsyncRequest.DATE_FORMAT.format(mEndDate));
	}

	/**从服务器获取考勤汇总数据,获取成功后，设置数据在界面上的显示*/
	private void loadAttendanceSummaryData() {
		//请求网络
		Builder builder = new Builder();
		String url = AsyncRequest.MODULE_ATTENDANCE_SUMMARY;
		builder.setModule(url);
		builder.addDateQueryParameter(mBeginDate, mEndDate);
		builder.setResponseType(new TypeToken<Response<List<DeptAttendanceSummaryInfo>>>() {
		}.getType());
		builder.setResponseListener(new Listener<Response<List<DeptAttendanceSummaryInfo>>>() {
			@Override
			public void onErrorResponse(InvocationError arg0) {
				LogUtils.e(TAG, arg0.getMessage() + "  获取失败");
			}

			@Override
			public void onResponse(Response<List<DeptAttendanceSummaryInfo>> response) {
				LogUtils.e(TAG, "响应码：" + response.getCode() + "  获取成功");
				if (response.getCode() == 0) {
					if (response.getPayload() != null && response.getPayload().size() > 0) {
						LogUtils.i(TAG, response.getPayload().get(0).toString());
						//先将获取到的所有数据保存到总数据集合中
						mDeptSummaryLists = response.getPayload();
						//然后设置部门的适配集合
						mDeptAdapterList = mDeptSummaryLists;
						//再设置
						DeptAttendanceSummaryInfo info = new DeptAttendanceSummaryInfo();
						info.setChildren(mDeptAdapterList);
						mOnceAdapterList.add(info);
						//刷新集合，设置控件的显示与否
						refreshListView();
						mLvStructMember.setVisibility(View.VISIBLE);
						mPbLoading.setVisibility(View.GONE);
					} else {
						//如果返回值为空，或者返回的集合中没有内容
						mTvNoRight.setVisibility(View.VISIBLE);
						mPbLoading.setVisibility(View.GONE);
					}
				}
			}
		});
		builder.build().get();
	}

	/**-------------------------------适配器相关-------------------------------*/
	/**考勤信息汇总适配器适配器*/
	private class AttendanceSummaryAdapter extends BaseAdapter {

		private String mAdapterTime;

		/**给适配器创建构造方法*/
		public AttendanceSummaryAdapter(String time) {
			mAdapterTime = time;
		}

		@Override
		public int getCount() {
			return mDeptAdapterList.size() + mMemberAdapterList.size();
		}

		@Override
		public Object getItem(int position) {
			if (position < mMemberAdapterList.size()) {
				return mMemberAdapterList.get(position);
			}
			return mDeptAdapterList.get(position - mMemberAdapterList.size());
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_COUNT;
		}

		@Override
		public int getItemViewType(int position) {
			if (position < mMemberAdapterList.size()) {
				return TYPE_MEMBER;
			}
			return TYPE_DEPT;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//定义一个viewHolder对象
			BaseViewHolder viewHolder;

			LogUtils.i(TAG, "getView——当前条目的position:" + position);
			LogUtils.i(TAG, "getView——当前条目的类型:" + getItemViewType(position));

			//根据不同的条目类型，进行不同的操作
			switch (getItemViewType(position)) {

			//如果是成员条目
			case TYPE_MEMBER:
				if (convertView == null) {
					viewHolder = getViewHolder();
					viewHolder.initMemberView(convertView);
					convertView = viewHolder.getConvertView();
				} else {
					viewHolder = (BaseViewHolder) convertView.getTag();
				}
				viewHolder.fillMemberData(mMemberAdapterList.get(position));
				break;

			//如果是部门条目
			case TYPE_DEPT:
				if (convertView == null) {
					viewHolder = getViewHolder();
					viewHolder.initDeptView(convertView);
					convertView = viewHolder.getConvertView();
				} else {
					viewHolder = (BaseViewHolder) convertView.getTag();
				}
				viewHolder.fillDeptData(mDeptAdapterList.get(position - mMemberAdapterList.size()));
				break;

			default:
				//避免出现convertView为空的情况
				convertView = new View(SigninStructActivity.this);
				break;
			}
			return convertView;
		}

		/**
		 * 获取viewHolder的方法
		 * <pre>
		 * 由于这里的holder类型，是根据构造方法中的time来决定的，
		 * 所以需要根据time的类型创建不同的holder
		 * <pre>
		 */
		public BaseViewHolder getViewHolder() {
			if (TIME_TODAY.equals(mAdapterTime)) {
				return new TodayViewHolder();
			} else {
				return new OtherViewHolder();
			}
		}
	}

	/**-------------------------------END----------------------------------*/

	/**----------------------------ViewHolder相关----------------------------*/
	/**
	 * 抽象的基类viewHolder
	 * <pre>
	 * 由于本Activity需要两种ViewHolder，
	 * 即“考勤时间段为‘今日’时的ViewHolder”和“考勤时间段为其他时间段时的ViewHolder”
	 * 于是将这两个holder中通用的参数和方法抽取出来，
	 * 由于子类holder中部门的条目都是一样的，所以将部门有关的操作也都放到基类中来
	 * <pre>
	 */
	private abstract class BaseViewHolder {

		//部门条目的控件
		TextView tvDeptName;
		TextView tvDeptCount;

		/**适配器中复用的convertView*/
		View mConvertView;

		/**初始化部门条目的控件*/
		public void initDeptView(View convertView) {
			//初始化convertView，并和OtherViewHolder绑定在一起
			convertView = View.inflate(getApplicationContext(), R.layout.item_struct_attendance, null);
			convertView.setTag(this);
			mConvertView = convertView;
			//初始化viewHolder中的控件
			tvDeptName = (TextView) convertView.findViewById(R.id.tv_dept_name);
			tvDeptCount = (TextView) convertView.findViewById(R.id.tv_dept_count);
		}

		/**填充部门条目的*/
		public void fillDeptData(DeptAttendanceSummaryInfo info) {
			tvDeptName.setText(info.getName());
			tvDeptCount.setText(info.getMembers().size() + "");
		}

		/**获取convertView*/
		public View getConvertView() {
			return mConvertView;
		}

		/**初始化成员条目的控件*/
		public abstract void initMemberView(View convertView);

		/**给成员条目的控件添加数据*/
		public abstract void fillMemberData(AttendanceSummaryInfo member);

	}

	/**考勤时间段为‘今日’时的ViewHolder*/
	private class TodayViewHolder extends BaseViewHolder {

		//成员条目的控件
		TextView tvMemberName;
		TextView tvAttendaceStatus;

		@Override
		public void initMemberView(View convertView) {
			//初始化convertView，并和OtherViewHolder绑定在一起
			convertView = View.inflate(getApplicationContext(), R.layout.item_member_attendance_today, null);
			convertView.setTag(this);
			mConvertView = convertView;
			//初始化viewHolder中的控件
			tvMemberName = (TextView) convertView.findViewById(R.id.tv_member_name);
			tvAttendaceStatus = (TextView) convertView.findViewById(R.id.tv_attendace_status);
		}

		@Override
		public void fillMemberData(AttendanceSummaryInfo member) {
			//设置打卡状态的颜色，正常为绿色，其他均为红色
			if (member.getStatus() == 1) {
				tvAttendaceStatus.setTextColor(ColorUtils.getResourcesColor(R.color.attendance_normal));
			} else {
				tvAttendaceStatus.setTextColor(ColorUtils.getResourcesColor(R.color.attendance_abnormal));
			}
			tvMemberName.setText(member.getName());
			tvAttendaceStatus.setText(member.getStatusDesc());
		}

	}

	/**考勤时间段为其他时间段时的ViewHolder*/
	private class OtherViewHolder extends BaseViewHolder {

		//成员条目的控件
		TextView tvMemberName;
		TextView tvAttendanceNormal;
		TextView tvAttendanceAbnormal;
		TextView tvAttendanceOutPlan;
		TextView tvAttendanceNotCount;
		TextView tvAttendanceNotRecord;
		TextView tvAttendanceNotWork;

		@Override
		public void initMemberView(View convertView) {
			//初始化convertView，并和OtherViewHolder绑定在一起
			convertView = View.inflate(getApplicationContext(), R.layout.item_member_attandance_summary, null);
			convertView.setTag(this);
			mConvertView = convertView;
			//初始化viewHolder中的控件
			tvMemberName = (TextView) convertView.findViewById(R.id.tv_member_name);
			tvAttendanceNormal = (TextView) convertView.findViewById(R.id.tv_attendance_normal);
			tvAttendanceAbnormal = (TextView) convertView.findViewById(R.id.tv_attendance_abnormal);
			tvAttendanceOutPlan = (TextView) convertView.findViewById(R.id.tv_attendance_out_plan);
			tvAttendanceNotCount = (TextView) convertView.findViewById(R.id.tv_attendance_not_count);
			tvAttendanceNotRecord = (TextView) convertView.findViewById(R.id.tv_attendance_not_record);
			tvAttendanceNotWork = (TextView) convertView.findViewById(R.id.tv_attendance_not_work);
		}

		@Override
		public void fillMemberData(AttendanceSummaryInfo member) {
			tvMemberName.setText(member.getName());
			tvAttendanceNormal.setText(member.getZhengchangNum() + "");
			tvAttendanceAbnormal.setText(member.getAbnormalNum() + "");
			tvAttendanceOutPlan.setText(member.getNoScheduleNum() + "");
			tvAttendanceNotCount.setText(member.getWeijisuanNum() + "");
			tvAttendanceNotRecord.setText(member.getQueqinNum() + "");
			tvAttendanceNotWork.setText(member.getNoDutyNum() + "");
		}
	}
	/**---------------------------------END-----------------------------------*/
}
