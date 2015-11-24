package cn.yjt.oa.app.patrol.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.PatrolAttendanceInfo;
import cn.yjt.oa.app.beans.PatrolAttendanceStatus;
import cn.yjt.oa.app.meeting.utils.DateUtils;

/**
 * Created by 熊岳岳 on 2015/9/23.
 */
public class PatrolRecordHolder extends YjtBaseHolder<PatrolAttendanceInfo> {

    private TextView mTvRouteName;
    private TextView mTvPlanTime;
    private TextView mTvCompletePoint;
    private TextView mTvAllPoint;
    private TextView mTvStatus;

    public PatrolRecordHolder(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        mConvertView = View.inflate(mContext, R.layout.patrol_record_item, null);
        mTvRouteName = (TextView) mConvertView.findViewById(R.id.tv_route_name);
        mTvPlanTime = (TextView) mConvertView.findViewById(R.id.tv_plan_time);
        mTvCompletePoint = (TextView) mConvertView.findViewById(R.id.tv_complete_point);
        mTvAllPoint = (TextView) mConvertView.findViewById(R.id.tv_all_point);
        mTvStatus = (TextView) mConvertView.findViewById(R.id.tv_status);
        return mConvertView;
    }

    @Override
    public void refreshView(int position, PatrolAttendanceInfo info) {
        mTvRouteName.setText(info.getLineName());
        setPlanTime(info);
        mTvCompletePoint.setText(info.getFinishPoints() + "");
//        mTvAllPoint.setText(info.getAllPoints()+info.getFinishPoints() + "");
        mTvAllPoint.setText(info.getAllPoints() + "");
        setStatus(info.getStatus());
    }

    private void setStatus(int status) {
        String statusStr = new String();
        switch(status){
            case PatrolAttendanceStatus.FINISH:
                statusStr = PatrolAttendanceStatus.FINISH_STATUS;
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
        mTvStatus.setText(statusStr+"");
    }

    private void setPlanTime(PatrolAttendanceInfo info) {
        String date = null;
        if(info.getDutyDate()!=null){
            date = DateUtils.dateRequestToDateTime(info.getDutyDate());
        }
        mTvPlanTime.setText(date+" "+info.getInSTime()+"~"+info.getOutTime());
    }
}
