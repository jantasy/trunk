package cn.yjt.oa.app.patrol.adapter;

import android.content.Context;
import android.view.View;

import cn.yjt.oa.app.base.adapter.YjtBaseAdapter;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.PatrolAttendanceInfo;
import cn.yjt.oa.app.patrol.activitys.PatrolDetailRecordActivity;
import cn.yjt.oa.app.patrol.holder.PatrolRecordHolder;

/**
 * Created by xiong on 2015/9/23.
 */
public class PatrolRecordAdapter extends YjtBaseAdapter<PatrolAttendanceInfo>{

    private final String TAG = "PatrolRecordAdapter";

    public PatrolRecordAdapter(Context context) {
        super(context);
    }

    @Override
    public YjtBaseHolder<PatrolAttendanceInfo> getHolder() {
        return new PatrolRecordHolder(mContext);
    }

    @Override
    public void onInnerItemClick(View view, int position) {
        super.onInnerItemClick(view, position);
        PatrolAttendanceInfo info = mDatas.get(position);
        PatrolDetailRecordActivity.launch(mContext,info.getLineId(),info.getDutyDate(),info.getInSTime());
    }

    @Override
    public void setOtherListener() {
        super.setOtherListener();
    }
}
