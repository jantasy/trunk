package cn.yjt.oa.app.patrol.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.PatrolPoint;

/**
 * 巡检点的holder
 * @author 熊岳岳
 * @since 20150906
 */
public class PatrolPointHolder extends YjtBaseHolder<PatrolPoint> {

	private final String TAG = "PatrolPointHolder";

	public TextView mTvPatrolName;
	public TextView mTvCheck;
	public ImageView mIvDelete;

	public PatrolPointHolder(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		mConvertView = View.inflate(mContext, R.layout.item_can_delete, null);
		mTvPatrolName = (TextView) mConvertView.findViewById(R.id.tv_name);
		mTvCheck = (TextView) mConvertView.findViewById(R.id.tv_check);
		mIvDelete = (ImageView) mConvertView.findViewById(R.id.iv_delete);
		return mConvertView;
	}

	@Override
	public void refreshView(int position, PatrolPoint info) {
		mTvPatrolName.setText(info.getName());
        setCheckString(info);
	}

    /**设置校验字符串的显示*/
    private void setCheckString(PatrolPoint info){
        StringBuilder check = new StringBuilder();

        //如果是位置校验
        if(info.getCheckPosition()==1){
            check.append("位置");
        }
        //如果是标签校验
        if(info.getCheckTag()==1){
            if (!TextUtils.isEmpty(check.toString())){
                check.append("和");
            }
            check.append("标签");
        }
        //如果既不是标签也不是位置校验 -_-|不太可能
        if (TextUtils.isEmpty(check.toString())){
            check.append("无");
        }

        check.append("校验");
        mTvCheck.setText(check.toString());
    }

}
