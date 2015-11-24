package cn.yjt.oa.app.patrol.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.PatrolRoute;
import cn.yjt.oa.app.meeting.utils.DateUtils;

/**
 * 巡检点的holder
 *
 * @author 熊岳岳
 * @since 20150906
 */
public class PatrolRouteHolder extends YjtBaseHolder<PatrolRoute> {

    private final String TAG = "PatrolPointHolder";

    public TextView mTvRouteName;
    public TextView mTvRouteDescription;
    public TextView mTvRouteCreatetime;
    public ImageView mIvDelete;

    public PatrolRouteHolder(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        mConvertView = View.inflate(mContext, R.layout.patrol_route_item, null);
        mTvRouteName = (TextView) mConvertView.findViewById(R.id.tv_route_name);
        mTvRouteDescription = (TextView) mConvertView.findViewById(R.id.tv_route_description);
        mTvRouteCreatetime = (TextView) mConvertView.findViewById(R.id.tv_route_createtime);
        mIvDelete = (ImageView) mConvertView.findViewById(R.id.iv_delete);
        return mConvertView;
    }

    @Override
    public void refreshView(int position, PatrolRoute info) {
        mTvRouteName.setText(info.getName());
        mTvRouteDescription.setText(info.getDescription());
        initDate(info.getCreateTime());

    }

    private void initDate(String date) {
        String dateString = DateUtils.requestToDateTime(date);
        mTvRouteCreatetime.setText(dateString);
    }
}
