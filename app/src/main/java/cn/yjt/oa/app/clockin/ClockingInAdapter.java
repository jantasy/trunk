package cn.yjt.oa.app.clockin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ClockInInfo;
import cn.yjt.oa.app.http.BusinessConstants;

@SuppressLint("SimpleDateFormat")
public class ClockingInAdapter extends BaseAdapter {

	private Context mContext;

	private LayoutInflater inflater;

	private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat sdfTimeServer = new SimpleDateFormat("HHmmss");
	private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdfDateServer = new SimpleDateFormat("yyyyMMdd");

	private List<ClockInInfo> mListInfo = new ArrayList<ClockInInfo>();
	


	public ClockingInAdapter(Context context) {
		this.mContext = context;
		inflater = (LayoutInflater) this.mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void updateData(List<ClockInInfo> listInfo) {
		this.mListInfo = listInfo;
		this.notifyDataSetChanged();
	}
	
	public void clear(){
		mListInfo.clear();
	}
	
	public void addAll(List<ClockInInfo> listInfo){
		mListInfo.addAll(listInfo);
	}

	private class ViewHolder {
		private TextView state;
		private TextView dutyDate;
		private TextView checkInTime;
		private TextView checkOutTime;
	}

	@Override
	public int getCount() {
		return mListInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mListInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.clockingin_adapter, parent,
					false);
			holder.state = (TextView) convertView
					.findViewById(R.id.clock_adapter_state);
			holder.dutyDate = (TextView) convertView
					.findViewById(R.id.clock_adapter_duty_date);
			holder.checkInTime = (TextView) convertView
					.findViewById(R.id.clock_adapter_checkin);
			holder.checkOutTime = (TextView) convertView
					.findViewById(R.id.clock_adapter_checkout);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ClockInInfo info = (ClockInInfo) getItem(position);
		holder.state.setText(info.getStatus());
		// holder.state.setText(ClockInState.getState(info.getStatus()));
		try {
			holder.dutyDate.setText(BusinessConstants.getDate(sdfDateServer
					.parse(info.getDutyDate())));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		if ("正常".equals(info.getStatus())) {
			holder.state.setTextColor(mContext.getResources().getColor(
					R.color.black));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				holder.state.setAlpha(0.8f);
			}
		} else {
			holder.state.setTextColor(mContext.getResources().getColor(
					R.color.textff4a4a));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// holder.state.setAlpha(0.8f);
			}
		}

		if (info.getCheckInTime()==null ||"FFFFF".equals(info.getCheckInTime())) {
			holder.checkInTime.setText("");
		} else {
			try {
				holder.checkInTime.setText(sdfTime.format(sdfTimeServer.parse(info.getCheckInTime())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		if (info.getCheckOutTime()==null ||"FFFFF".equals(info.getCheckOutTime())) {
			holder.checkOutTime.setText("");
		} else {
			try {
				holder.checkOutTime.setText(sdfTime.format(sdfTimeServer.parse(info.getCheckOutTime())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return convertView;
	}

	// @Override
	// public View getSectionView(int section, View convertView, ViewGroup
	// parent) {
	// // if (convertView == null)
	// // convertView = inflater.inflate(R.layout.clockin_signin_date_item,
	// parent, false);
	// //
	// // ((TextView)
	// convertView).setText(BusinessConstants.getDate(getSectionDate(section)));
	// //
	// ((TextView)convertView).setTextColor(mContext.getResources().getColor(R.color.text57595b));
	// return convertView;
	// }

	// @SuppressLint("NewApi")
	// @Override
	// public View getItemView(int section, int position, View convertView,
	// ViewGroup parent) {
	// ViewHolder holder = null;
	// if (convertView == null) {
	// holder = new ViewHolder();
	// convertView = inflater.inflate(R.layout.clockingin_adapter, parent,
	// false);
	// holder.state = (TextView) convertView
	// .findViewById(R.id.clock_adapter_state);
	// holder.dutyDate = (TextView) convertView
	// .findViewById(R.id.clock_adapter_duty_date);
	// holder.checkInTime = (TextView) convertView
	// .findViewById(R.id.clock_adapter_checkin);
	// holder.checkOutTime = (TextView) convertView
	// .findViewById(R.id.clock_adapter_checkout);
	//
	// convertView.setTag(holder);
	// } else {
	// holder = (ViewHolder) convertView.getTag();
	// }
	//
	// ClockInInfo info = (ClockInInfo) getItem(section, position);
	// holder.state.setText(info.getStatus());
	// // holder.state.setText(ClockInState.getState(info.getStatus()));
	// try {
	// holder.dutyDate.setText(BusinessConstants.getDate(sdfDateServer.parse(info
	// .getDutyDate())));
	// } catch (ParseException e1) {
	// e1.printStackTrace();
	// }
	//
	// if ("正常".equals(info.getStatus())) {
	// holder.state.setTextColor(mContext.getResources().getColor(
	// R.color.black));
	// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	// holder.state.setAlpha(0.8f);
	// }
	// } else {
	// holder.state.setTextColor(mContext.getResources().getColor(
	// R.color.textff4a4a));
	// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	// holder.state.setAlpha(0.5f);
	// }
	// }
	//
	// if ("FFFFFF".equals(info.getCheckInTime())) {
	// holder.checkInTime.setText("");
	// } else {
	// try {
	// holder.checkInTime.setText(sdfTime.format(sdfTimeServer
	// .parse(info.getCheckInTime())));
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// if ("FFFFFF".equals(info.getCheckOutTime())) {
	// holder.checkOutTime.setText("");
	// } else {
	// try {
	// holder.checkOutTime.setText(sdfTime.format(sdfTimeServer
	// .parse(info.getCheckOutTime())));
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// return convertView;
	// }

}
