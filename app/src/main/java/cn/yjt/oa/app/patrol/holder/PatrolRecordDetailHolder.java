package cn.yjt.oa.app.patrol.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.PatrolAttendanceStatus;
import cn.yjt.oa.app.beans.PatrolPointAttendanceInfo;
import cn.yjt.oa.app.meeting.utils.DateUtils;
import cn.yjt.oa.app.patrol.bean.PatrolRecord;

/**
 * Created by 熊岳岳 on 2015/9/23.
 */
public class PatrolRecordDetailHolder extends YjtBaseHolder<PatrolPointAttendanceInfo> {

    private TextView mTvPointName;
    private TextView mTvPatrolTime;
    private TextView mTvStatus;
    private ImageView mIvCompleted;

    public PatrolRecordDetailHolder(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        mConvertView = View.inflate(mContext, R.layout.patrol_record_detail_item, null);
        mTvPointName = (TextView) mConvertView.findViewById(R.id.tv_point_name);
        mTvPatrolTime = (TextView) mConvertView.findViewById(R.id.tv_patrol_time);
        mTvStatus = (TextView) mConvertView.findViewById(R.id.tv_status);
        mIvCompleted = (ImageView) mConvertView.findViewById(R.id.iv_completed);
        return mConvertView;
    }

    @Override
    public void refreshView(int position, PatrolPointAttendanceInfo info) {
        mTvPointName.setText(info.getPointName());
        setInTime(info.getInTime());
        setStatus(info.getStatus());
    }

    private void setStatus(int status) {
        String statusStr = new String();
        switch (status) {
            case PatrolAttendanceStatus.FINISH:
                statusStr = PatrolAttendanceStatus.FINISH_STATUS;
                mIvCompleted.setImageResource(R.drawable.completed_icon);
                break;
            case PatrolAttendanceStatus.UNDERWAY:
                statusStr = PatrolAttendanceStatus.UNDERWAY_STATUS;
                break;
            case PatrolAttendanceStatus.NOTFINISH:
                statusStr = PatrolAttendanceStatus.NOTFINISH_STATUS;
                break;
            case PatrolAttendanceStatus.NOSTART:
                statusStr = PatrolAttendanceStatus.NOSTART_STATUS;
                break;
        }
        mTvStatus.setText(statusStr + "");
    }

    private void setInTime(String inTime) {
        if (TextUtils.isEmpty(inTime)) {
            mTvPatrolTime.setText("未巡检");
        } else {
            mTvPatrolTime.setText(DateUtils.timeRequestToTimeSecond(inTime));
        }
    }
}
