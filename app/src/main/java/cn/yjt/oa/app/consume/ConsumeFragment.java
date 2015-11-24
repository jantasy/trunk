package cn.yjt.oa.app.consume;

import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import com.activeandroid.util.Log;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.TransInfo;
import cn.yjt.oa.app.component.TimeClockFragment;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.sim.SIMInfos;
import cn.yjt.oa.app.sim.SIMRecord;
import cn.yjt.oa.app.sim.SIMUtils;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.widget.SectionAdapter;
import cn.yjt.oa.app.widget.TimeLineAdapter;
import cn.yjt.oa.app.widget.TimeLineView;
import cn.yjt.oa.app.widget.listview.FooterView;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class ConsumeFragment extends TimeClockFragment implements Listener<Response<ListSlice<TransInfo>>>,
		View.OnClickListener {

	private final String TAG = "ConsumeFragment";

	private PullToRefreshListView pullListView;
	private LayoutInflater inflater;

	private int from;
	private static final int MAX = 31;
	private Type responseType = new TypeToken<Response<ListSlice<TransInfo>>>() {
	}.getType();

	private View cardRecordTab;
	private View transRecordTab;
	private TextView balance;
	private TextView edBalance;
	private TextView balanceUnit;
	private View selectedTab;

	// 碰碰翼机通圈存需要打开
	private View sufficientValue;

	private SIMListener simListener;
	private TranListener tranListener;
	private TimeLineView timelineView;
	List<TransInfo> temps;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		setListViewAdapter(simAdapter);

		return inflater.inflate(R.layout.consume, container, false);
	}

	@Override
	protected void setupTimeLine() {
		if (timelineView != null && adapter != null) {
			if (selectedTab == cardRecordTab) {
				if (simAdapter.getCount() > 0)
					timelineView.setVisibility(View.VISIBLE);
				else
					timelineView.setVisibility(View.GONE);
			} else {
				if (adapter.getCount() > 0)
					timelineView.setVisibility(View.VISIBLE);
				else
					timelineView.setVisibility(View.GONE);
			}
			timelineView.clearAllTimeNodes();
			timelineView.setFirstNodeShowing(false);
			int visibleItemCount = pullListView.getChildCount();
			int firstVisibleItem = pullListView.getFirstVisiblePosition();
			try {
				FooterView footerView = pullListView.getFooterView();
				if (footerView != null && footerView.isEnabled()) {
					View view = footerView.getFooterContainer();
					if (view != null && view.isEnabled()) {
						view.setBackgroundResource(R.color.app_content_background);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int i = 0; i < visibleItemCount; ++i) {
				int pos = firstVisibleItem + i - pullListView.getHeaderViewsCount();
				View v = pullListView.getChildAt(i);

				int[] sectionInfo = null;
				if (selectedTab == cardRecordTab) {
					sectionInfo = SectionAdapter.getDetailPosition(simAdapter, pos);
				} else {
					sectionInfo = SectionAdapter.getDetailPosition(adapter, pos);
				}

				if (sectionInfo != null && sectionInfo.length > 1) {
					if (firstVisibleItem != 0 || i != 0) {
						timelineView.addTimeNode(v.getTop() + v.getHeight() / 8);
					}
					if (sectionInfo[0] == 0) {
						timelineView.setFirstNodeShowing(true);
					}
				}
			}
			timelineView.invalidate();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		simListener = new SIMListener();
		tranListener = new TranListener();
		pullListView = (PullToRefreshListView) getListView();
		pullListView.setOnLoadMoreListner(simListener);
		pullListView.setOnRefreshListener(simListener);

		cardRecordTab = view.findViewById(R.id.card_record);
		transRecordTab = view.findViewById(R.id.trans_record);
		sufficientValue = view.findViewById(R.id.consume_sufficient);
		timelineView = (TimeLineView) view.findViewById(R.id.timelineview);

		cardRecordTab.setOnClickListener(this);
		transRecordTab.setOnClickListener(this);
		cardRecordTab.setSelected(true);
		sufficientValue.setOnClickListener(this);

		balance = (TextView) view.findViewById(R.id.record_balance);
		edBalance = (TextView) view.findViewById(R.id.record_edbalance);
		balanceUnit = (TextView) view.findViewById(R.id.record_unit);
		emptyView = (TextView) view.findViewById(R.id.empty_view);
		emptyView.setText("暂无记录");
		pullListView.setEmptyView(emptyView);

		readSIM();
		selectedTab = cardRecordTab;
		tranListener.onRefresh();
		pullListView.setRefreshingState();
	}

	@Override
	public void onDestroyView() {
		if (cancelable != null) {
			cancelable.cancel();
		}
		super.onDestroyView();

	}

	private void readSIM() {
		TitleFragmentActivity activity = (TitleFragmentActivity) getActivity();
		activity.showProgressBar();
		new SIMReaderTask().execute();
	}

	private TimeLineAdapter adapter = new TimeLineAdapter() {

		@Override
		public View getSectionView(int section, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.consume_header, parent, false);

			((TextView) convertView).setText(BusinessConstants.getDate(getSectionDate(section)));
			return convertView;
		}

		@Override
		public int getItemViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getItemViewType(int section, int position) {
			// TODO Auto-generated method stub
			TransInfo transInfo = (TransInfo) getItem(section, position);
			if (transInfo.getType() == 0) {
				position = 1;
			} else {
				position = 2;
			}
			return position;
		}

		@Override
		public View getItemView(int section, int position, View convertView, ViewGroup parent) {
			TextView title;
			TextView content;
			TextView cardTransScan;
			int type = getItemViewType(section, position);

			TransInfo transInfo = (TransInfo) getItem(section, position);
			int transAmout = transInfo.getTransAmount();
			// 分转化成元并保留两位小数
			float transAmoutChangeToYuan = transAmout / 100.00f;
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			String amout = decimalFormat.format(transAmoutChangeToYuan);

			if (convertView == null)
				convertView = inflater.inflate(R.layout.consume_item, parent, false);
			if (type == 1) {
				title = (TextView) convertView.findViewById(R.id.title);
				content = (TextView) convertView.findViewById(R.id.content);
				title.setVisibility(View.VISIBLE);
				content.setVisibility(View.VISIBLE);

				switch (transInfo.getWalletType()) {
				case TransInfo.WALLET_TYPE_MAIN: {
					title.setText(amout);
					content.setText("主钱包消费");
				}
					break;
				case TransInfo.WALLET_TYPE_ALLOWANCE: {
					title.setText(amout);
					content.setText("补贴钱包消费");
				}
					break;
				case TransInfo.WALLET_TYPE_ALLOWANCE_TIMES: {

					title.setText(transAmout);
					content.setText("补贴钱包消费");
				}
					break;
				}

			} else {
				title = (TextView) convertView.findViewById(R.id.title_right);
				content = (TextView) convertView.findViewById(R.id.content_right);
				cardTransScan = (TextView) convertView.findViewById(R.id.card_tran_san);

				title.setVisibility(View.VISIBLE);
				content.setVisibility(View.VISIBLE);
				cardTransScan.setVisibility(View.VISIBLE);

				if (TransInfo.WALLET_TYPE_ALLOWANCE_TIMES == transInfo.getWalletType()) {
					title.setText(transAmout);
				} else {
					title.setText(amout);
				}
				content.setText(transInfo.getRechargeDesc());
				cardTransScan.setText("订单号："+transInfo.getCardTransSan());
			}

			return convertView;
		}
	};

	private TimeLineAdapter simAdapter = new TimeLineAdapter() {

		@Override
		public View getSectionView(int section, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.consume_header, parent, false);

			((TextView) convertView).setText(BusinessConstants.getDate(getSectionDate(section)));
			return convertView;
		}

		@Override
		public View getItemView(int section, int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.consume_item, parent, false);

			TextView title = (TextView) convertView.findViewById(R.id.title);
			TextView content = (TextView) convertView.findViewById(R.id.content);
			TextView titleright = (TextView) convertView.findViewById(R.id.title_right);
			TextView contentright = (TextView) convertView.findViewById(R.id.content_right);
			SIMRecord record = (SIMRecord) getItem(section, position);
			String type = record.GetTradeType();
			String accountString = record.getAccountString();
			// "01" - 补贴钱包充值 "02" - 主钱包充值 "05" - 补贴钱包消费 "06" - 主钱包消费
			if (SIMRecord.WALLET_TYPE_MAIN_EXPANCE.equals(type)) {// 主钱包消费
				title.setVisibility(View.VISIBLE);
				content.setVisibility(View.VISIBLE);
				title.setText(accountString);
				content.setText("主钱包消费");
			} else if (SIMRecord.WALLET_TYPE_MAIN_SUFFICIENT.equals(type)) {// 主钱包充值
				titleright.setVisibility(View.VISIBLE);
				contentright.setVisibility(View.VISIBLE);
				titleright.setText(accountString);
				contentright.setText("主钱包充值");
			} else if (SIMRecord.WALLET_TYPE_PAY_EXPANCE.equals(type)) {// 补贴钱包消费
				title.setVisibility(View.VISIBLE);
				content.setVisibility(View.VISIBLE);
				title.setText(accountString);
				content.setText("补贴钱包消费");
			} else if (SIMRecord.WALLET_TYPE_PAY_SUFFICIENT.equals(type)) {// 补贴钱包充值
				titleright.setVisibility(View.VISIBLE);
				contentright.setVisibility(View.VISIBLE);
				titleright.setText(accountString);
				contentright.setText("补贴钱包充值");
			} else {// 其他

			}
			return convertView;
		}
	};
	private TextView emptyView;
	private Cancelable cancelable;

	private void load(int from, int max) {
		this.from = from;
		Calendar cal = Calendar.getInstance();
		String today = BusinessConstants.formatTime(cal.getTime());
		cal.add(Calendar.MONTH, -3);
		String before = BusinessConstants.formatTime(cal.getTime());
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_CONSUME);
		builder.addPageQueryParameter(from, max);
		builder.addQueryParameter("beginDate", before);
		builder.addQueryParameter("endDate", today);
		builder.setResponseType(responseType);
		builder.setResponseListener(this);
		cancelable = builder.build().get();
	}

	@Override
	public void onErrorResponse(InvocationError arg0) {
		cancelable = null;
		tranListener.finishLoading();
	}

	@Override
	public void onResponse(Response<ListSlice<TransInfo>> response) {
		cancelable = null;
		tranListener.finishLoading();
		if (response.getCode() == AsyncRequest.ERROR_CODE_OK) {
			ListSlice<TransInfo> slice = response.getPayload();
			if (slice != null && slice.getContent() != null) {
				List<TransInfo> items = slice.getContent();
				if (items != null) {
					LogUtils.e(TAG, items.toString());
					if (from == 0) {
						adapter.clear();
					}
					adapter.addEntries(items);
					adapter.notifyDataSetChanged();
					int size = items.size();
					from += size;
					if (size < MAX) {
						// load more finished
						pullListView.enableFooterView(false);
					}
				}
			}
		}

		if (transRecordTab.isSelected()) {

			if (adapter.isEmpty()) {
				emptyView.setVisibility(View.VISIBLE);
				emptyView.setText("您暂没有交易记录");
			} else {
				emptyView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (cardRecordTab == v && selectedTab != v) {
			timelineView.setVisibility(simAdapter.getCount() == 0 ? View.GONE : View.VISIBLE);
			cardRecordTab.setSelected(true);
			transRecordTab.setSelected(false);
			setListViewAdapter(simAdapter);
			pullListView.setOnLoadMoreListner(simListener);
			pullListView.setOnRefreshListener(simListener);
			if (pullListView.getFooterView().isLoading()) {
				pullListView.onLoadMoreComplete();
			}
			if (pullListView.getHeaderView().isRefreshing()) {
				pullListView.onRefreshComplete();
			}
			selectedTab = v;
			//			if (simAdapter.isEmpty()) {
			//				emptyView.setVisibility(View.VISIBLE);
			//				emptyView.setText("您的卡内没有充值或消费记录");
			//			} else {
			//				emptyView.setVisibility(View.GONE);
			//			}
		} else if (transRecordTab == v && selectedTab != v) {
			timelineView.setVisibility(adapter.getCount() == 0 ? View.GONE : View.VISIBLE);
			cardRecordTab.setSelected(false);
			transRecordTab.setSelected(true);
			setListViewAdapter(adapter);
			pullListView.setOnLoadMoreListner(tranListener);
			pullListView.setOnRefreshListener(tranListener);
			selectedTab = v;
			if (pullListView.getFooterView().isLoading()) {
				pullListView.onLoadMoreComplete();
			}
			if (pullListView.getHeaderView().isRefreshing()) {
				pullListView.onRefreshComplete();
			}
			//			if (adapter.isEmpty()) {
			//				emptyView.setVisibility(View.VISIBLE);
			//				emptyView.setText("您暂没有交易记录");
			//			} else {
			//				emptyView.setVisibility(View.GONE);
			//			}
		} else if (sufficientValue == v) {
			// 碰碰充值功能
			//			PaymentEngine.getInstance(getActivity(), null).pay(
			//					AccountManager.getCurrent(getActivity()).getPhone());
			//			Toast.makeText(this.getActivity(),
			//					getResources().getString(R.string.consume_msg),
			//					Toast.LENGTH_SHORT).show();
		}
	}

	private class SIMReaderTask extends AsyncTask<Void, Void, SIMInfos> {

		@Override
		protected SIMInfos doInBackground(Void... params) {
			FragmentActivity activity = getActivity();
			if (activity != null) {
				return SIMUtils.readSIM(activity.getApplicationContext());
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(SIMInfos result) {
			TitleFragmentActivity activity = (TitleFragmentActivity) getActivity();
			if (activity == null) {
				return;
			}
			activity.dismissProgressBar();
			if (selectedTab == cardRecordTab) {
				if (pullListView.getHeaderView().isRefreshing()) {
					pullListView.onRefreshComplete();
				}
			}
			if (result != null) {
				balance.setText(String.valueOf(result.getBalanceString()));
				edBalance.setText("补贴：" + result.getEdBalanceString() + result.getUnitString());
				//				balanceUnit.setText(result.getUnitString());
				simAdapter.clear();
				simAdapter.addEntries(result.getSimRecords());
				try {
					simAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (getActivity() != null) {
					Toast.makeText(getActivity(), "您的卡暂不支持NFC功能", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private class SIMRecordTask extends AsyncTask<Void, Void, List<SIMRecord>> {

		@Override
		protected List<SIMRecord> doInBackground(Void... params) {
			return SIMUtils.getBusinessRecords(getActivity().getApplicationContext());
		}

		@Override
		protected void onPostExecute(List<SIMRecord> result) {
			simListener.finishLoading();
			if (result != null) {
				simAdapter.clear();
				simAdapter.addEntries(result);
				try {
					simAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (getActivity() != null) {

					Toast.makeText(getActivity(), "您的卡暂不支持NFC功能", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private class SIMListener extends ListViewListener {

		@Override
		public void onRefresh() {
			super.onRefresh();
			new SIMRecordTask().execute();
		}

		@Override
		public void onLoadMore() {
			super.onLoadMore();
			// do nothing.
		}

		@Override
		public void finishLoading() {
			if (selectedTab == cardRecordTab) {
				super.finishLoading();
			}
		}

	}

	private class ListViewListener implements OnLoadMoreListner, OnRefreshListener {

		@Override
		public void onRefresh() {
		}

		@Override
		public void onLoadMore() {
		}

		public void finishLoading() {
			if (pullListView.getFooterView().isLoading()) {
				pullListView.onLoadMoreComplete();
			}
			if (pullListView.getHeaderView().isRefreshing()) {
				pullListView.onRefreshComplete();
			}
		}

	}

	private class TranListener extends ListViewListener {

		@Override
		public void onRefresh() {
			super.onRefresh();
			load(0, MAX);
		}

		@Override
		public void onLoadMore() {
			super.onLoadMore();
			load(from, MAX);
		}

		@Override
		public void finishLoading() {
			if (selectedTab == transRecordTab) {
				super.finishLoading();
			}
		}

	}

}
