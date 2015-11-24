package cn.yjt.oa.app.signin;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.beans.CardCheckinInfo;
import cn.yjt.oa.app.beans.ClockInInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.clockin.ClockingInAdapter;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.signin.adapter.SigninRecordAdapter;
import cn.yjt.oa.app.utils.TelephonyUtil;
import cn.yjt.oa.app.utils.ViewUtil;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class AttendanceRecordsActivity extends TitleFragmentActivity implements
		OnClickListener {

	private int REQUESTCODE = 1;
	private int RESULTCODE = 2;
	private String poiName;
	private double latitude;
	private double longitude;
	private String attachment;
	private String signType;

	private PullToRefreshListView signinListView;
	private PullToRefreshListView attendanceListView;

	private View signinRecordTab;
	private View clockinRecordTab;
	private View cardcheckinRecordTab;
	private SigninListener signinListener;
	private ClockinListener clockinListener;
	private CardCheckinListener cardCheckinListener;
	private static final Type TYPE_SIGNIN_RECORD = new TypeToken<Response<ListSlice<SigninInfo>>>() {
	}.getType();

	private SigninRecordAdapter signinAdapter;
	private ClockingInAdapter clockingAdapter;
	private SimpleDateFormat searchFormat = new SimpleDateFormat("yyyyMMdd");
	private static final String REFRESH = "刷新";
	private static final String NOT_REFRESH = "非刷新";
	private List<ClockInInfo> mListInfo = new ArrayList<ClockInInfo>();
	private Boolean isCardCheckinRequest=true;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
			LaunchActivity.launch(this);
			finish();
		} else {
			setContentView(R.layout.signin);
			Init();
			initTitleBar();
		}

	}

	private void Init() {
		// findViewById(R.id.signin_clock).setOnClickListener(this);
		signinListener = new SigninListener();
		clockinListener = new ClockinListener();
		cardCheckinListener = new CardCheckinListener();

		signinAdapter = new SigninRecordAdapter(this);
		signinListView = (PullToRefreshListView) findViewById(R.id.record_list);
		signinListView.setOnRefreshListener(signinListener);
		signinListView.setOnLoadMoreListner(signinListener);
		signinListView.setRefreshingState();
		signinListView.setVisibility(View.VISIBLE);
		signinListView.setAdapter(signinAdapter);

		clockingAdapter = new ClockingInAdapter(this);
		attendanceListView = (PullToRefreshListView) findViewById(R.id.attendance_list);
		attendanceListView.setOnRefreshListener(clockinListener);
		attendanceListView.setOnLoadMoreListner(clockinListener);
		attendanceListView.setRefreshingState();
		attendanceListView.setVisibility(View.GONE);
		attendanceListView.setAdapter(clockingAdapter);

		cardCheckinAdapter = new CardCheckinAdapter(this);
		cardCheckinListView = (PullToRefreshListView) findViewById(R.id.card_checkin_list);
		cardCheckinListView.setOnRefreshListener(cardCheckinListener);
		cardCheckinListView.setOnLoadMoreListner(cardCheckinListener);
		cardCheckinListView.setRefreshingState();
		cardCheckinListView.setAdapter(cardCheckinAdapter);
		TextView cardCheckinEmptyView = (TextView) View.inflate(this,
				R.layout.view_empty_view, null);
		cardCheckinEmptyView.setText("暂时无法获得您的刷卡记录");
		cardCheckinListView.setEmptyView(cardCheckinEmptyView);
		cardCheckinListView.setVisibility(View.GONE);

		signinRecordTab = findViewById(R.id.signin_record);
		clockinRecordTab = findViewById(R.id.clockin_record);
		cardcheckinRecordTab = findViewById(R.id.cardcheckin_record);
		int isYjtUser = AccountManager.getCurrent(this).getIsYjtUser();
		if (isYjtUser == 0) {
			cardcheckinRecordTab.setVisibility(View.GONE);
		}

		signinRecordTab.setOnClickListener(this);
		clockinRecordTab.setOnClickListener(this);
		cardcheckinRecordTab.setOnClickListener(this);
		signinRecordTab.setSelected(true);

		signinListener.onRefresh();
		searchClockin(null, 0);
	}

	private void initTitleBar() {
		setTitleBackground(getResources().getDrawable(
				R.drawable.consume_title_bg));
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		setTitle(R.string.signinleave_title);
		setTitleColor(Color.BLACK);
	}

	private class SigninListener implements OnRefreshListener,
			OnLoadMoreListner {

		@Override
		public void onRefresh() {
			getSigninData(0, 10,
					new Listener<Response<ListSlice<SigninInfo>>>() {

						@Override
						public void onErrorResponse(InvocationError error) {
							signinListView.onRefreshComplete();
							Toast.makeText(AttendanceRecordsActivity.this,
									R.string.load_fail, Toast.LENGTH_SHORT)
									.show();
						}

						@Override
						public void onResponse(
								Response<ListSlice<SigninInfo>> response) {
							signinListView.onRefreshComplete();
							if (response.getCode() == 0) {
								signinAdapter.clear();
								signinAdapter.addEntries(response.getPayload()
										.getContent());
								signinAdapter.notifyDataSetChanged();
							} else {
								Toast.makeText(getApplicationContext(),
										response.getDescription(),
										Toast.LENGTH_SHORT).show();
							}
						}
					});
		}

		@Override
		public void onLoadMore() {
			getSigninData(signinAdapter.getCount(), 10,
					new Listener<Response<ListSlice<SigninInfo>>>() {

						@Override
						public void onErrorResponse(InvocationError error) {
							signinListView.onLoadMoreComplete();
							Toast.makeText(AttendanceRecordsActivity.this,
									R.string.load_fail, Toast.LENGTH_SHORT)
									.show();
						}

						@Override
						public void onResponse(
								Response<ListSlice<SigninInfo>> response) {
							signinListView.onLoadMoreComplete();
							if (response.getCode() == 0) {
								signinAdapter.addEntries(response.getPayload()
										.getContent());
								signinAdapter.notifyDataSetChanged();
							} else {
								Toast.makeText(getApplicationContext(),
										response.getDescription(),
										Toast.LENGTH_SHORT).show();
							}
						}
					});
		}

	}

	public void getSigninData(int from, int max,
			Listener<Response<ListSlice<SigninInfo>>> listener) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_SINGNINS)
				.setResponseType(TYPE_SIGNIN_RECORD)
				.addPageQueryParameter(from, max).setResponseListener(listener)
				.build().get();

	}

	private void updateClockingAdapter(List<ClockInInfo> clocklist) {
		if (clocklist != null) {
			clockingAdapter.updateData(clocklist);
		}
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		// Intent intent;
		switch (v.getId()) {
		// case R.id.signin_clock:
		// signType = SigninInfo.SINGIN_TYPE_VISIT;
		// intent = new Intent(getApplicationContext(),
		// SigninPOISearchActivity.class);
		// intent.putExtra("signtype", SigninInfo.SINGIN_TYPE_VISIT);
		// startActivityForResult(intent, REQUESTCODE);
		// break;

		case R.id.signin_record:
			signinRecordTab.setSelected(true);
			clockinRecordTab.setSelected(false);
			cardcheckinRecordTab.setSelected(false);
			signinListView.setVisibility(View.VISIBLE);
			attendanceListView.setVisibility(View.GONE);
			cardCheckinListView.setVisibility(View.GONE);
			break;
		case R.id.clockin_record:
			signinRecordTab.setSelected(false);
			clockinRecordTab.setSelected(true);
			cardcheckinRecordTab.setSelected(false);
			signinListView.setVisibility(View.GONE);
			attendanceListView.setVisibility(View.VISIBLE);
			cardCheckinListView.setVisibility(View.GONE);
			break;
		case R.id.cardcheckin_record:
			signinRecordTab.setSelected(false);
			clockinRecordTab.setSelected(false);
			cardcheckinRecordTab.setSelected(true);
			signinListView.setVisibility(View.GONE);
			attendanceListView.setVisibility(View.GONE);
			cardCheckinListView.setVisibility(View.VISIBLE);
			if(isCardCheckinRequest){
				searchCardCheckin(null, 0);
			}
				
				
			break;
		default:
			break;
		}
	}

	private class ClockinListener implements OnRefreshListener,
			OnLoadMoreListner {

		@Override
		public void onRefresh() {
			searchClockin(null, 0);
		}

		@Override
		public void onLoadMore() {
			ClockInInfo item = (ClockInInfo) clockingAdapter
					.getItem(clockingAdapter.getCount() - 1);
			searchClockin(item.getDate(), 1);
		}
	}

	private class CardCheckinListener implements OnRefreshListener,
			OnLoadMoreListner {

		@Override
		public void onRefresh() {
			isCardCheckinRequest=false;
			searchCardCheckin(null, 0);
		}

		@Override
		public void onLoadMore() {
			CardCheckinInfo cardCheckinInfo = (CardCheckinInfo) cardCheckinAdapter
					.getItem(cardCheckinAdapter.getCount()
							- cardCheckinAdapter.getSectionCount() - 1);
			searchCardCheckin(cardCheckinInfo.getDate(), 1);
		}
	}

	/**
	 * 
	 * @param fromDate
	 * @param mode
	 *            0:refresh , 1:loadmore
	 */
	private void searchCardCheckin(Date fromDate, final int mode) {
		Calendar calendar = Calendar.getInstance();
		if (fromDate != null) {
			calendar.setTime(fromDate);
			calendar.add(Calendar.DATE, -1);
		}
		String endDate = searchFormat.format(calendar.getTime());
		calendar.add(Calendar.MONTH, -1);// 得到前一个月
		Date date = calendar.getTime();
		String beginDate = searchFormat.format(date);

		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_ATTENDANCE_CARDLIST);
		builder.addQueryParameter("beginDate", beginDate);
		builder.addQueryParameter("endDate", endDate);
		Type type = new TypeToken<Response<ListSlice<CardCheckinInfo>>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(
				new Listener<Response<ListSlice<CardCheckinInfo>>>() {

					@Override
					public void onErrorResponse(InvocationError error) {
						if (mode == 0) {
							cardCheckinListView.onRefreshComplete();
						} else {
							cardCheckinListView.onLoadMoreComplete();
						}
						LogUtils.i("===err = " + error.getMessage());
						if(!cardcheckinRecordTab.isSelected()){
							cardCheckinListView.setVisibility(View.GONE);
						}
					}

					@Override
					public void onResponse(
							Response<ListSlice<CardCheckinInfo>> response) {
						if (mode == 0) {
							cardCheckinListView.onRefreshComplete();
						} else {
							cardCheckinListView.onLoadMoreComplete();
						}
						if (response.getCode() == 0) {// 成功
							ListSlice<CardCheckinInfo> tempList = response
									.getPayload();
							if (tempList != null) {
								if (mode == 0) {
									cardCheckinAdapter.clear();
								}
								cardCheckinAdapter.addEntries(tempList
										.getContent());
								cardCheckinAdapter.notifyDataSetChanged();
							} else {
								showToastFail();
							}
						} else {
							showToastFail();
						}
						if(!cardcheckinRecordTab.isSelected()){
							cardCheckinListView.setVisibility(View.GONE);
						}
					}
				}).build().get();
	}

	private void searchClockin(Date fromDate, final int mode) {
		Calendar calendar = Calendar.getInstance();
		if (fromDate != null) {
			calendar.setTime(fromDate);
			calendar.add(Calendar.DATE, -1);
		}
		calendar.add(Calendar.DATE, -1);
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
				if (mode == 0) {
					attendanceListView.onRefreshComplete();
				} else {
					attendanceListView.onLoadMoreComplete();
				}
				if (response.getCode() == 0) {// 成功
					ListSlice<ClockInInfo> tempList = response.getPayload();
					if (tempList != null) {
						mListInfo = tempList.getContent();
						// updateClockingAdapter(mListInfo);
						if (mode == 0) {
							clockingAdapter.clear();
						}
						clockingAdapter.addAll(mListInfo);
						clockingAdapter.notifyDataSetChanged();
					} else {
						showToastFail();
					}
				} else {
					showToastFail();
				}

				if(!clockinRecordTab.isSelected()){
					attendanceListView.setVisibility(View.GONE);
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				LogUtils.i("===err = " + error.getMessage());
				if (mode == 0) {
					attendanceListView.onRefreshComplete();
				} else {
					attendanceListView.onLoadMoreComplete();
				}
				Toast.makeText(AttendanceRecordsActivity.this,
						getResources().getString(R.string.load_fail),
						Toast.LENGTH_SHORT).show();
				if(!clockinRecordTab.isSelected()){
					attendanceListView.setVisibility(View.GONE);
				}
			}
		});
		builder.build().get();
	}

	protected void showToastFail() {
		Toast.makeText(AttendanceRecordsActivity.this,
				getResources().getString(R.string.load_fail),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && requestCode == REQUESTCODE
				&& resultCode == RESULTCODE) {
			poiName = data.getStringExtra("poiName");
			latitude = data.getDoubleExtra("latitude", 0);
			longitude = data.getDoubleExtra("longitude", 0);
			attachment = data.getStringExtra("attachment");
			signin();
		}
	}

	ProgressDialog dialog;
	private PullToRefreshListView cardCheckinListView;
	private CardCheckinAdapter cardCheckinAdapter;

	private void signin() {
		UserInfo userInfo = AccountManager.getCurrent(this);
		SigninInfo signinInfo = new SigninInfo();
		signinInfo.setUserId(userInfo.getId());
		signinInfo.setType(signType);
		signinInfo.setContent("check_test");
		signinInfo.setPositionDescription(poiName);
		signinInfo.setPositionData(latitude + "," + longitude);
		signinInfo.setAttachment(attachment);
		signinInfo.setIccId(TelephonyUtil.getICCID(this));

		if (dialog == null) {
			dialog = new ProgressDialog(this);
			dialog.setMessage("签到中...");
		}
		dialog.show();

		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_SINGNINS);
		builder.setRequestBody(signinInfo);
		Type type = new TypeToken<Response<SigninInfo>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new Listener<Response<SigninInfo>>() {

			@Override
			public void onResponse(Response<SigninInfo> response) {
				dialog.dismiss();
				if (response.getCode() == 0) {
					final Dialog dialog = new Dialog(AttendanceRecordsActivity.this,
							R.style.dialogNoTitle);
					View dialogView = View.inflate(getApplicationContext(),
							R.layout.signin_success_dialog, null);
					dialogView.findViewById(R.id.btn_comfirm)
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									dialog.dismiss();
									signinListView.setRefreshingState();
									signinListener.onRefresh();

								}
							});
					dialog.setContentView(dialogView);
					dialog.show();
				} else {
					Toast.makeText(AttendanceRecordsActivity.this,
							response.getDescription(), Toast.LENGTH_SHORT)
							.show();
				}
				if(!signinRecordTab.isSelected()){
					signinListView.setVisibility(View.GONE);
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				dialog.dismiss();
				Toast.makeText(getApplicationContext(),
						"签到失败！InvocationError" + error.getMessage(),
						Toast.LENGTH_SHORT).show();
				if(!signinRecordTab.isSelected()){
					signinListView.setVisibility(View.GONE);
				}
			}
		});
		builder.build().post();
	}

	public static void launch(Context context) {
		Intent intent = new Intent(context, SigninActivity.class);
		context.startActivity(intent);
	}
}
