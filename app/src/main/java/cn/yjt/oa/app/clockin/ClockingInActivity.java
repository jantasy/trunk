package cn.yjt.oa.app.clockin;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.beans.ClockInInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class ClockingInActivity extends TitleFragmentActivity {

	private TextView tvSum, tvSome;

	private PullToRefreshListView listView;

	private ClockingInAdapter adapter;

	private List<ClockInInfo> mListInfo = new ArrayList<ClockInInfo>();

	private LinearLayout progressbar;

	private SimpleDateFormat searchFormat = new SimpleDateFormat("yyyyMMdd");
	
	private static final int GET_DATA_SUCESS = 1;
	
	private static final String REFRESH = "刷新";
	private static final String NOT_REFRESH = "非刷新";

	// private static final String TAG_START_DATE = "TAG_START_DATE";
	// private static final String TAG_END_DATE = "TAG_END_DATE";
	// private boolean isStartDate = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clocking_in);

		initview();
	}

	private void initview() {
		tvSum = (TextView) findViewById(R.id.clockin_tv_sum);
		tvSome = (TextView) findViewById(R.id.clockin_tv_some);
		listView = (PullToRefreshListView) findViewById(R.id.clockin_record_list);
		progressbar = (LinearLayout) findViewById(R.id.clockin_progress);

		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setImageResource(R.drawable.icon_search);
		
		searchClockin(NOT_REFRESH);

		adapter = new ClockingInAdapter(this);
		adapter.updateData(mListInfo);
		listView.setAdapter(adapter);
		listView.enableFooterView(false);
		listView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				searchClockin(REFRESH);
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
		if (progressbar.getVisibility() == View.GONE) {
			progressbar.setVisibility(View.VISIBLE);
		}
		searchClockin(NOT_REFRESH);
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_DATA_SUCESS:
				tvSum.setText("考勤记录" + mListInfo.size() + "条");
				int normalNount = 0;
				for (ClockInInfo info : mListInfo) {
					if (info.getStatus().equals("正常")) {
						normalNount++;
					}
				}
				int errCount = mListInfo.size() - normalNount;
				tvSome.setText("正常" + normalNount + "条" + "  异常" + errCount
						+ "条");
				break;

			default:
				break;
			}
		};
	};

	private void searchClockin(final String tag) {
		Calendar calendar = Calendar.getInstance();
		String endDate = searchFormat.format(calendar.getTime());
		calendar.add(Calendar.MONTH, -1);// 得到前一个月
		Date date = calendar.getTime();
		String beginDate = searchFormat.format(date);

		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_CLOCKIN);
		builder.addQueryParameter("beginDate", beginDate);
		builder.addQueryParameter("endDate", endDate);
		Type type = new TypeToken<Response<ListSlice<ClockInInfo>>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new Listener<Response<ListSlice<ClockInInfo>>>() {

			@Override
			public void onResponse(Response<ListSlice<ClockInInfo>> response) {
				progressbar.setVisibility(View.GONE);
				LogUtils.i("==response = " + response);
				LogUtils.i("==response getCode = " + response.getCode());
				if (response.getCode() == 0) {// 成功
					ListSlice<ClockInInfo> tempList = response.getPayload();
					if (tempList != null) {
						mListInfo = tempList.getContent();
						handler.sendEmptyMessage(GET_DATA_SUCESS);
						updateAdapter();
					} else {
						showToastFail();
					}
				} else {
					showToastFail();
				}
				
				if (REFRESH.equals(tag)) {
					listView.onRefreshComplete();
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				LogUtils.i("===err = " + error.getMessage());
				progressbar.setVisibility(View.GONE);
				if (REFRESH.equals(tag)) {
					listView.onRefreshComplete();
				}
				Toast.makeText(ClockingInActivity.this,
						getResources().getString(R.string.load_fail),
						Toast.LENGTH_SHORT).show();
			}
		});
		builder.build().get();
	}
	
	private void updateAdapter() {
		if (mListInfo != null) {
//			adapter.clear();
//			adapter.addEntries(mListInfo);
//			adapter.notifyDataSetChanged();
			adapter.updateData(mListInfo);
		}
	}

	protected void showToastFail() {
		Toast.makeText(ClockingInActivity.this,
				getResources().getString(R.string.load_fail),
				Toast.LENGTH_SHORT).show();
	}

	// 启动考勤界面
	public static void startClockinActivity(Context context) {
		Intent intent = new Intent(context, ClockingInActivity.class);
		context.startActivity(intent);
	}
	
}
