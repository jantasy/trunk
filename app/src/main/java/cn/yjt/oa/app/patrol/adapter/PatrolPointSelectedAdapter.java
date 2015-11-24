package cn.yjt.oa.app.patrol.adapter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import java.util.List;

import cn.yjt.oa.app.base.adapter.YjtBaseAdapter;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.PatrolPoint;
import cn.yjt.oa.app.patrol.holder.PatrolPointSelectedHolder;

/**
 * Created by 熊岳岳 on 2015/9/14.
 */
public class PatrolPointSelectedAdapter extends YjtBaseAdapter<PatrolPoint> {

    public PatrolPointSelectedAdapter(Context context) {
        super(context);
    }

    public PatrolPointSelectedAdapter(Context context, List<PatrolPoint> list) {
        super(context, list);
    }

    public void setAllCheck(boolean isCheck) {
        for (PatrolPoint info : mDatas) {
            info.setSelected(isCheck);
        }
        notifyDataSetChanged();
    }
//
//    @Override
//    public int getCount() {
//        return 0;
//    }

    @Override
    public void setInnerViewListener(YjtBaseHolder<PatrolPoint> holder, final int position, PatrolPoint info) {
        super.setInnerViewListener(holder, position, info);
        if (holder != null && holder instanceof PatrolPointSelectedHolder) {
            //checkbox选择事件
            ((PatrolPointSelectedHolder) holder).mCbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (position < mDatas.size() && position >= 0) {
                        mDatas.get(position).setSelected(isChecked);
                    }
                }
            });

            //上移按钮点击事件
            ((PatrolPointSelectedHolder) holder).mBtnMoveUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position > 0) {
                        PatrolPoint info = mDatas.remove(position);
                        mDatas.add(position-1, info);
                        notifyDataSetChanged();
                    }
                }
            });

            //下移按钮点击事件
            ((PatrolPointSelectedHolder) holder).mBtnMoveDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position < mDatas.size()-1) {
                        PatrolPoint info = mDatas.remove(position);
                        mDatas.add(position+1, info);
                        notifyDataSetChanged();
                    }
                }
            });

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

    }

    @Override
    public YjtBaseHolder<PatrolPoint> getHolder() {
        return new PatrolPointSelectedHolder(mContext);
    }
}
