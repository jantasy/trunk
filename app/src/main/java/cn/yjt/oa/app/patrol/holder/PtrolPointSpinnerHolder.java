package cn.yjt.oa.app.patrol.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.PatrolPoint;

/**
 * Created by xiong on 2015/9/18.
 */
public class PtrolPointSpinnerHolder extends YjtBaseHolder<PatrolPoint> {

    private TextView mTvPointName;

    public PtrolPointSpinnerHolder(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        mConvertView = View.inflate(mContext, R.layout.item_createtag_attendancearea, null);
        mTvPointName = (TextView) mConvertView.findViewById(R.id.attendance_area);
        return mConvertView;
    }

    @Override
    public void refreshView(int position, PatrolPoint info) {
        if (info != null) {
            mTvPointName.setText(info.getName());
        }
    }
}
