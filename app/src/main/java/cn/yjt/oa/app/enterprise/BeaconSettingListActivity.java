package cn.yjt.oa.app.enterprise;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.BeaconInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class BeaconSettingListActivity extends BackTitleFragmentActivity
		implements OnRefreshListener {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private List<BeaconInfo> beaconInfos = new ArrayList<BeaconInfo>();
	private BeaconListAdapter adapter;
	private PullToRefreshListView listView;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_beacon_setting_list);
		listView = (PullToRefreshListView) findViewById(R.id.beacon_setting_listview);
		listView.enableFooterView(false);
		listView.setOnRefreshListener(this);
		adapter = new BeaconListAdapter();
		listView.setAdapter(adapter);
		getRightButton().setImageResource(R.drawable.contact_add_group);
	}

	@Override
	public void onRightButtonClick() {
		addBeacon();
		super.onRightButtonClick();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		listView.setRefreshingState();
		System.out.println("listView.setRefreshingState()");
		requestBeaconList();
	}

	private void addBeacon() {
		BeaconSettingActivity.launch(this);
	}

	private void requestBeaconList() {
		ApiHelper.getBeacons(new ResponseListener<ListSlice<BeaconInfo>>() {

			@Override
			public void onSuccess(ListSlice<BeaconInfo> payload) {
				beaconInfos.clear();
				beaconInfos.addAll(payload.getContent());
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				listView.onRefreshComplete();
				System.out.println("listView.onRefreshComplete()"
						+ Thread.currentThread().getName());
			}
		});
	}

	private void postDelete(final BeaconInfo beaconInfo) {
		ApiHelper.deleteBeacon(new ProgressDialogResponseListener<String>(
				BeaconSettingListActivity.this, "正在删除...") {

			@Override
			public void onSuccess(String payload) {
				beaconInfos.remove(beaconInfo);
				adapter.notifyDataSetChanged();
			}
		}, beaconInfo.getId());

	}

	private void showDeleteConfirmDialog(final BeaconInfo beaconInfo) {
		AlertDialogBuilder.newBuilder(this).setMessage("确认删除该蓝牙标签吗？")
				.setPositiveButton("删除", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						postDelete(beaconInfo);
					}
				}).setNegativeButton("取消", null).show();
	}

	public static void launch(Context context) {
		Intent intent = new Intent(context, BeaconSettingListActivity.class);
		context.startActivity(intent);
	}

	private class BeaconListAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public BeaconListAdapter() {
			inflater = LayoutInflater.from(getApplicationContext());
		}

		@Override
		public int getCount() {
			return beaconInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return beaconInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_beacon_list,
						parent, false);
			}
			TextView beaconName = (TextView) convertView
					.findViewById(R.id.beacon_name);
			TextView beaconUumm = (TextView) convertView
					.findViewById(R.id.beacon_uumm);
			View beaconDelete = convertView.findViewById(R.id.beacon_delete);
			TextView beaconCreateTime = (TextView) convertView
					.findViewById(R.id.beacon_create_time);
			TextView areaName = (TextView) convertView
					.findViewById(R.id.beacon_area);

			final BeaconInfo beaconInfo = (BeaconInfo) getItem(position);
			beaconName.setText(beaconInfo.getName());
			beaconUumm.setText("UUMM：" + beaconInfo.getUumm());
			try {
				beaconCreateTime.setText(DATE_FORMAT.format(BusinessConstants
						.parseTime(beaconInfo.getCreateTime())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			areaName.setText("区域：" + beaconInfo.getAreaName());
			beaconDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDeleteConfirmDialog(beaconInfo);
				}
			});

			return convertView;
		}

	}

	@Override
	public void onRefresh() {
		requestBeaconList();
	}
}
