package cn.yjt.oa.app.meeting.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.base.adapter.YjtBaseAdapter;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.MeetingSignInInfo;
import cn.yjt.oa.app.meeting.holder.MeetingSigninItemHolder;

/**
 * 会议出席人员的适配器
 * @author 熊岳岳
 * @since 20150827
 */
public class MeetingSigninAdapter extends YjtBaseAdapter<MeetingSignInInfo> {

	private final String TAG = "MeetingSigninAdapter";

	public MeetingSigninAdapter(Context context, List<MeetingSignInInfo> list) {
		super(context, list);
	}

	@Override
	public int getCount() {
		return mDatas.size() == 0 ? mDatas.size() : mDatas.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		return position == 0 ? null : mDatas.get(position - 1);
	}

	@Override
	public long getItemId(int position) {
		return mDatas.size() == 0 ? position : position - 1;
	}

	@Override
	public void onInnerItemClick(View view, int position) {
		if(position == 0){
			return;
		}
		LinearLayout llSigninDetail = (LinearLayout) view.findViewById(R.id.ll_signin_detail);
		llSigninDetail.setVisibility(llSigninDetail.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
	}

	@Override
	public YjtBaseHolder<MeetingSignInInfo> getHolder() {
		return new MeetingSigninItemHolder(MainApplication.getAppContext());
	}
}
