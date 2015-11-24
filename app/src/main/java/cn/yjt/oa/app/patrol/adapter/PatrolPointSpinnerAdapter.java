package cn.yjt.oa.app.patrol.adapter;

import android.content.Context;

import cn.yjt.oa.app.base.adapter.YjtBaseAdapter;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.PatrolPoint;
import cn.yjt.oa.app.patrol.holder.PtrolPointSpinnerHolder;

/**
 * Created by xiong on 2015/9/18.
 */
public class PatrolPointSpinnerAdapter extends YjtBaseAdapter<PatrolPoint>{

    public PatrolPointSpinnerAdapter(Context context) {
        super(context);
    }

    @Override
    public YjtBaseHolder<PatrolPoint> getHolder() {
        return new PtrolPointSpinnerHolder(mContext);
    }
}
