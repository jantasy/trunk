package cn.yjt.oa.app.signin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.CardCheckinInfo;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.widget.TimeLineAdapter;

public class CardCheckinAdapter extends TimeLineAdapter {

	private Context context;
	private LayoutInflater inflater;

	public CardCheckinAdapter(Context context) {
		super();
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getSectionView(int section, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.clockin_signin_date_item,
					parent, false);
		}

		((TextView) convertView).setText(BusinessConstants
				.getDate(getSectionDate(section)));
		((TextView) convertView).setTextColor(context.getResources().getColor(
				R.color.context_text));
		return convertView;
	}

	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.card_checkin_list_item,
					parent, false);
		}
		TextView machineName = (TextView) convertView
				.findViewById(R.id.machine_name);
		TextView checkinTime = (TextView) convertView
				.findViewById(R.id.checkin_time);
		CardCheckinInfo item = (CardCheckinInfo) getItem(section, position);
		machineName.setText(item.getMachineName());
		checkinTime.setText(BusinessConstants.getTimeWithSS(item.getDate()));
		return convertView;
	}

}
