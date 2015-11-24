package cn.yjt.oa.app.enterprise;

import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ClockInInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.clockin.ClockingInAdapter;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class AttendanceRecordActivity extends TitleFragmentActivity implements
		OnRefreshListener,OnLoadMoreListner {

	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat searchFormat = new SimpleDateFormat("yyyyMMdd");

	private PullToRefreshListView listView;
	private List<ClockInInfo> listInfo;
	private ClockingInAdapter adapter;
	private long userId;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		userId = getIntent().getLongExtra("userId", 0);
		setContentView(R.layout.activity_attendance_record);
		listView = (PullToRefreshListView) findViewById(R.id.listView);
		listInfo = new ArrayList<ClockInInfo>();
		adapter = new ClockingInAdapter(this);
		adapter.updateData(listInfo);
		listView.setRefreshingState();
		listView.setOnRefreshListener(this);
		listView.setOnLoadMoreListner(this);
		listView.setAdapter(adapter);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		onRefresh();
	}
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onRefresh() {
		load(null,0);
	}

	/**
	 * 
	 * @param fromDate
	 * @param mode 0 refresh, 1 loadmore
	 */
	private void load(Date fromDate,final int mode) {
		Calendar calendar = Calendar.getInstance();
		if(fromDate != null){
			calendar.setTime(fromDate);
			calendar.add(Calendar.DATE, -1);
		}
		String endDate = searchFormat.format(calendar.getTime());
		calendar.add(Calendar.MONTH, -1);// 得到前一个月
		Date date = calendar.getTime();
		String beginDate = searchFormat.format(date);
		Type responseType = new TypeToken<Response<ListSlice<ClockInInfo>>>() {
		}.getType();
		cancelable = new AsyncRequest.Builder()
				.addQueryParameter("beginDate", beginDate)
				.addQueryParameter("endDate", endDate)
				.setModule(AsyncRequest.MODULE_CUSTS_ATTENDACE)
				.setModuleItem(String.valueOf(userId))
				.setResponseType(responseType).setResponseListener(new Listener<Response<ListSlice<ClockInInfo>>>(){

					@Override
					public void onErrorResponse(InvocationError arg0) {
						if(mode == 0){
							listView.onRefreshComplete();
						}else{
							listView.onLoadMoreComplete();
						}
						Toast.makeText(getApplicationContext(), "网络连接失败,请稍后重试",
								Toast.LENGTH_SHORT).show();						
					}

					@Override
					public void onResponse(Response<ListSlice<ClockInInfo>> arg0) {
						if(mode == 0){
							listView.onRefreshComplete();
						}else{
							listView.onLoadMoreComplete();
						}
						if (arg0.getCode() == 0) {
							ListSlice<ClockInInfo> payload = arg0.getPayload();
							if(mode == 0){
								listInfo.clear();
							}
							listInfo.addAll(payload.getContent());
							adapter.notifyDataSetChanged();
						} else {
							Toast.makeText(getApplicationContext(), arg0.getDescription(),
									Toast.LENGTH_SHORT).show();
						}
						
					}})
				.build().get();
	}


	protected void onDestroy() {
		super.onDestroy();
		if (cancelable != null) {
			cancelable.cancel();
		}
	}

	private Cancelable cancelable;

	public static void launchWithUserId(Context context, long userId) {
		Intent intent = new Intent(context, AttendanceRecordActivity.class);
		intent.putExtra("userId", userId);
		context.startActivity(intent);
	}
	@Override
	public void onLoadMore() {
		ClockInInfo item = (ClockInInfo) adapter.getItem(adapter.getCount()-1);
		load(item.getDate(), 1);
		
	}
}
