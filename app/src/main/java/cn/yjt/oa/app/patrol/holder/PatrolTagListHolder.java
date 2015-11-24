package cn.yjt.oa.app.patrol.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.NFCTagInfo;
import cn.yjt.oa.app.beans.PatrolPoint;
import cn.yjt.oa.app.beans.PatrolTag;
import cn.yjt.oa.app.meeting.utils.DateUtils;

/**
 * 巡更标签的holder
 * @author 熊岳岳
 * @since 20150906
 */
public class PatrolTagListHolder extends YjtBaseHolder<PatrolTag> {

	private final String TAG = "PatrolPointHolder";

	public TextView mTvTagName;
	public TextView mTvTagSn;
	public TextView mTvTagArea;
	public TextView mTvTagCreateTime;
	public ImageView mIvTagDelete;

	public PatrolTagListHolder(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		mConvertView = View.inflate(mContext, R.layout.attendance_tag_list_item, null);
		mTvTagName = (TextView) mConvertView.findViewById(R.id.tag_name);
		mTvTagSn = (TextView) mConvertView.findViewById(R.id.tag_sn);
		mTvTagArea = (TextView) mConvertView.findViewById(R.id.tag_area);
		mTvTagCreateTime = (TextView) mConvertView.findViewById(R.id.tag_create_time);
		mIvTagDelete = (ImageView) mConvertView.findViewById(R.id.tag_delete);
		return mConvertView;
	}

	@Override
	public void refreshView(int position, PatrolTag info) {
		mTvTagName.setText(info.getName());
		mTvTagSn.setText(info.getSn());
		mTvTagArea.setText(info.getPointName());
        initDate(info.getCreateTime());

	}

    private void initDate(String date) {
        String dateString = DateUtils.requestToDateTime(date);
        mTvTagCreateTime.setText(dateString);
    }

}
