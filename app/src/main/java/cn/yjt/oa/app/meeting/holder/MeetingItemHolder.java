package cn.yjt.oa.app.meeting.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.MeetingInfo;
import cn.yjt.oa.app.meeting.utils.DateUtils;
import cn.yjt.oa.app.utils.ColorUtils;
import cn.yjt.oa.app.utils.LogUtils;

/**
 * 会议条目的适配holder
 * @author 熊岳岳
 * 
 */
public class MeetingItemHolder extends YjtBaseHolder<MeetingInfo>{

	private final String TAG = "MeetingItemHolder";

	public MeetingInfo mInfo;

	public TextView mTvMeetingName;
	public TextView mTvMeetingTime;
	public TextView mTvSigninTime;
	public TextView mTvAddress;
	public ImageView mIvDelete;

	public MeetingItemHolder(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		mConvertView = View.inflate(mContext, R.layout.meeting_list_item, null);
		mTvMeetingName = (TextView) mConvertView.findViewById(R.id.tv_meetingname);
		mTvMeetingTime = (TextView) mConvertView.findViewById(R.id.tv_meetingtime);
		mTvAddress = (TextView) mConvertView.findViewById(R.id.tv_address);
		mIvDelete = (ImageView) mConvertView.findViewById(R.id.iv_delete);
		return mConvertView;
	}

	@Override
	public void refreshView(int position, MeetingInfo info) {
		if (info != null) {
			mInfo = info;

			switch (info.getType()) {
			case MeetingInfo.TYPE_JOIN:
				fillJoinMeetingData(info);
				break;

			case MeetingInfo.TYPE_PUBLIC:
				fillPublicMeetingData();
				break;

			default:
				LogUtils.e(TAG, "会议类型未知");
				break;
			}
			mTvMeetingName.setText(info.getName());
			mTvAddress.setText("会议地点：" + info.getAddress());
			//设置会议开始时间
			if (!TextUtils.isEmpty(info.getStartTime())) {
				mTvMeetingTime.setText("会议日期：" + DateUtils.requestToDate(info.getStartTime()));
			}
		} else {
			LogUtils.e(TAG, "会议信息为空");
		}
	}

	/** 我参加的会议界面显示 */
	private void fillJoinMeetingData(MeetingInfo info) {
		mIvDelete.setVisibility(View.GONE);
	
	}

	/** 我发起的会议界面显示 */
	private void fillPublicMeetingData() {
		mIvDelete.setVisibility(View.VISIBLE);
	}
}
