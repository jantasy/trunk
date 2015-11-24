package cn.yjt.oa.app.patrol.holder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.PatrolPoint;

/**
 * 巡检路线绑定巡检点界面中，未选中的巡检点的holder
 * Created by 熊岳岳 on 2015/9/14.
 */
public class PatrolPointSelectedHolder extends YjtBaseHolder<PatrolPoint> {

    public TextView mTvPatrolPointName;
    public CheckBox mCbSelected;
    public Button mBtnMoveUp;
    public Button mBtnMoveDown;

    public PatrolPointSelectedHolder(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        mConvertView = View.inflate(mContext, R.layout.patrol_point_selected_item, null);
        mTvPatrolPointName = (TextView) mConvertView.findViewById(R.id.tv_patrol_point_name);
        mCbSelected = (CheckBox) mConvertView.findViewById(R.id.cb_selected);
        mBtnMoveUp = (Button) mConvertView.findViewById(R.id.btn_move_up);
        mBtnMoveDown = (Button) mConvertView.findViewById(R.id.btn_move_down);
        return mConvertView;
    }

    @Override
    public void refreshView(int position, PatrolPoint info) {
        mTvPatrolPointName.setText(info.getName());
        if (info.isSelected()) {
            mCbSelected.setChecked(true);
        }else{
            mCbSelected.setChecked(false);
        }
    }
}
