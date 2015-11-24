package cn.yjt.oa.app.patrol.adapter;

import android.content.Context;

import java.util.List;

import cn.yjt.oa.app.base.adapter.YjtBaseAdapter;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.PatrolPointAttendanceInfo;
import cn.yjt.oa.app.patrol.bean.PatrolRecord;
import cn.yjt.oa.app.patrol.holder.PatrolRecordDetailHolder;

/**
 * Created by xiong on 2015/9/24.
 */
public class PatrolRecordDetailAdapter extends YjtBaseAdapter<PatrolPointAttendanceInfo>{

    public PatrolRecordDetailAdapter(Context context) {
        super(context);
    }

    public PatrolRecordDetailAdapter(Context context, List list) {
        super(context, list);
    }

    @Override
    public YjtBaseHolder getHolder() {
        return new PatrolRecordDetailHolder(mContext);
    }
}
