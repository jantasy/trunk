package cn.yjt.oa.app.attendance;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.AttendanceTime;
import cn.yjt.oa.app.beans.AttendanceUserTime;
import cn.yjt.oa.app.beans.BeaconInfo;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.UserData;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class AutoBeaconSettingFragment extends Fragment implements OnRefreshListener, OnCheckedChangeListener,
		OnSharedPreferenceChangeListener {

	private static final String TAG = "AutoBeaconSettingFragment";

	private View root;
	private PullToRefreshListView listView;
	private List<BeaconInfo> beaconInfos = new ArrayList<BeaconInfo>();
	private LayoutInflater inflater;
	private AutoBeaconAdapter autoBeaconAdapter;
	private BeaconAdapter adapter;
	private DeletedBeaconAdapter deletedBeaconAdapter;
	private CheckBox checkbox;
	private TextView introduction;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (root == null) {
			this.inflater = inflater;
			root = inflater.inflate(R.layout.fragment_auto_beacon_attendance_setting, container, false);
			checkbox = (CheckBox) root.findViewById(R.id.auto_attendance_checkbox);
			listView = (PullToRefreshListView) root.findViewById(R.id.beacon_list);
			introduction = (TextView) root.findViewById(R.id.bottom_text);
			listView.enableFooterView(false);
			listView.setOnRefreshListener(this);
			adapter = new BeaconAdapter();
			deletedBeaconAdapter = new DeletedBeaconAdapter();
			autoBeaconAdapter = new AutoBeaconAdapter();
			listView.setAdapter(autoBeaconAdapter);
			checkbox.setChecked(UserData.getAutoAttendance());
			checkbox.setOnCheckedChangeListener(this);
			setupIntroduction();
			request();
		}
		watchUserData();
		AttendanceTimeCheckReceiver.sendBroadcast(getActivity());
		return root;
	}

	private void setupIntroduction() {
		introduction.post(new Runnable() {

			@Override
			public void run() {
				introduction
						.setText("开启自动考勤后，“翼机通+”会根据您当天排班每个班段的开始前半小时和结束后半小时自动进行蓝牙标签扫描，若您在设置的自动考勤标签周围（约0-30米）会自动进行考勤。\n开启自动考勤可能会稍微增加您的手机耗电呦"
								+ buildAttendanceTimeIntroduction());
			}
		});
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		LogUtils.i(TAG, "onSharedPreferenceChanged:" + key);
		if (UserData.KEY_ATTENDANCE_USER_TIME.equals(key)) {
			setupIntroduction();
		}
	}

	private void watchUserData() {
		UserData.getUserPreferences().registerOnSharedPreferenceChangeListener(this);
		System.out.println("registerOnSharedPreferenceChangeListener");
	}

	private void unwatchUserData() {
		UserData.getUserPreferences().unregisterOnSharedPreferenceChangeListener(this);
		System.out.println("unregisterOnSharedPreferenceChangeListener");
	}

	private String buildAttendanceTimeIntroduction() {
		AttendanceUserTime attendanceUserTime = UserData.getAttendanceUserTime();
		if (attendanceUserTime == null) {
			AttendanceTimeCheckReceiver.sendBroadcast(getActivity());
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append("\n今日考勤时间：");
		List<AttendanceTime> times = attendanceUserTime.getTimes();
		for (AttendanceTime attendanceTime : times) {
			if (TextUtils.equals(attendanceTime.getInStartTime(), attendanceTime.getInEndTime())) {
				builder.append("(").append(attendanceTime.getInStartTime()).append("-");
			} else {
				builder.append("([").append(attendanceTime.getInStartTime()).append("~")
						.append(attendanceTime.getInEndTime()).append("]").append("-");
			}
			if (TextUtils.equals(attendanceTime.getOutStartTime(), attendanceTime.getOutEndTime())) {
				builder.append(attendanceTime.getOutStartTime()).append(")");
			} else {
				builder.append("[").append(attendanceTime.getOutStartTime()).append("~")
						.append(attendanceTime.getOutEndTime()).append("]").append("");
			}
		}
		return builder.toString();
	}

	private void request() {
		ApiHelper.getUserBeaconsInArea(new ResponseListener<List<BeaconInfo>>() {

			@Override
			public void onSuccess(List<BeaconInfo> payload) {
				beaconInfos.clear();
				beaconInfos.addAll(payload);
				List<BeaconInfo> favorateUserAreaBeaconInfos = UserData.getFavorateUserAreaBeaconInfos();
				deletedBeaconAdapter.beaconInfos.clear();
				if (favorateUserAreaBeaconInfos != null) {
					deletedBeaconAdapter.beaconInfos.addAll(favorateUserAreaBeaconInfos);
				}
				deletedBeaconAdapter.beaconInfos.removeAll(beaconInfos);

				autoBeaconAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				listView.onRefreshComplete();
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unwatchUserData();
		ViewGroup parent = (ViewGroup) root.getParent();
		if (parent != null) {
			parent.removeView(root);
		}
	}

	private class AutoBeaconAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return adapter.getCount() + deletedBeaconAdapter.getCount();
		}

		@Override
		public Object getItem(int position) {
			if (position >= adapter.getCount()) {
				return deletedBeaconAdapter.getItem(position);
			}
			return adapter.getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position >= adapter.getCount()) {
				return deletedBeaconAdapter.getView(position - adapter.getCount(), convertView, parent);
			}
			return adapter.getView(position, convertView, parent);
		}

	}

	private class BeaconAdapter extends BaseAdapter {

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
				convertView = inflater.inflate(R.layout.item_auto_beacon_setting, parent, false);
			}
			TextView name = (TextView) convertView.findViewById(R.id.beacon_name);
			TextView uumm = (TextView) convertView.findViewById(R.id.uumm);
			TextView areaName = (TextView) convertView.findViewById(R.id.area_name);
			final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.item_check);
			final BeaconInfo item = (BeaconInfo) getItem(position);
			List<BeaconInfo> favorateUserAreaBeaconInfos = UserData.getFavorateUserAreaBeaconInfos();
			if (favorateUserAreaBeaconInfos != null) {
				checkBox.setChecked(favorateUserAreaBeaconInfos.contains(item));
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checkBox.toggle();
					List<BeaconInfo> favorateUserAreaBeaconInfos = UserData.getFavorateUserAreaBeaconInfos();
					if (favorateUserAreaBeaconInfos == null) {
						favorateUserAreaBeaconInfos = new ArrayList<BeaconInfo>();
					}
					if (checkBox.isChecked()) {
						favorateUserAreaBeaconInfos.add(item);
					} else {
						favorateUserAreaBeaconInfos.remove(item);
					}
					UserData.setFavorateUserAreaBeaconInfos(favorateUserAreaBeaconInfos);
				}
			});
			name.setText(item.getName());
			uumm.setText(item.getUumm());
			areaName.setText("区域：" + item.getAreaName());
			return convertView;
		}

	}

	private class DeletedBeaconAdapter extends BaseAdapter {

		private List<BeaconInfo> beaconInfos = new ArrayList<BeaconInfo>();

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
				convertView = inflater.inflate(R.layout.item_auto_beacon_setting, parent, false);
				convertView.setAlpha(0.5f);
			}
			TextView name = (TextView) convertView.findViewById(R.id.beacon_name);
			TextView uumm = (TextView) convertView.findViewById(R.id.uumm);
			TextView areaName = (TextView) convertView.findViewById(R.id.area_name);
			final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.item_check);
			final BeaconInfo item = (BeaconInfo) getItem(position);
			List<BeaconInfo> favorateUserAreaBeaconInfos = UserData.getFavorateUserAreaBeaconInfos();
			if (favorateUserAreaBeaconInfos != null) {
				checkBox.setChecked(favorateUserAreaBeaconInfos.contains(item));
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checkBox.toggle();
					List<BeaconInfo> favorateUserAreaBeaconInfos = UserData.getFavorateUserAreaBeaconInfos();
					if (favorateUserAreaBeaconInfos == null) {
						favorateUserAreaBeaconInfos = new ArrayList<BeaconInfo>();
					}
					if (checkBox.isChecked()) {
						favorateUserAreaBeaconInfos.add(item);
					} else {
						favorateUserAreaBeaconInfos.remove(item);
					}
					UserData.setFavorateUserAreaBeaconInfos(favorateUserAreaBeaconInfos);
				}
			});
			name.setText(item.getName());
			uumm.setText(item.getUumm());
			areaName.setText("该标签已不是您的企业考勤标签");
			return convertView;
		}

	}

	@Override
	public void onRefresh() {
		request();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			/*记录操作 0808*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_OPEN_AUTO_ATTENDANCE);
		}else{
			/*记录操作 0809*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_CLOSE_AUTO_ATTENDANCE);
		}
		UserData.setAutoAttendance(isChecked);
	}
}
