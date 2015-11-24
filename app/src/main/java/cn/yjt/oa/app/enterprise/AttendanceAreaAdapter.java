package cn.yjt.oa.app.enterprise;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.CustSignCommonInfo;

public class AttendanceAreaAdapter extends BaseAdapter {
	private LayoutInflater inflater;

	public AttendanceAreaAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	List<CustSignCommonInfo> commonInfos = Collections.emptyList();

	
	public void setData(List<CustSignCommonInfo> infos){
		this.commonInfos  = infos;
	}
	@Override
	public int getCount() {
		return commonInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return commonInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.item_createtag_attendancearea, parent, false);
		}
		TextView attendanceArea = (TextView) convertView
				.findViewById(R.id.attendance_area);
		CustSignCommonInfo commonInfo = (CustSignCommonInfo) getItem(position);
		attendanceArea.setText(commonInfo.getName());
		return convertView;
	}

}