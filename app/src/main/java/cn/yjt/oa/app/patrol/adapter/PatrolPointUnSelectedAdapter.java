package cn.yjt.oa.app.patrol.adapter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import java.util.List;

import cn.yjt.oa.app.base.adapter.YjtBaseAdapter;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;

import cn.yjt.oa.app.beans.PatrolPoint;
import cn.yjt.oa.app.patrol.holder.PatrolPointUnSelectedHolder;

/**
 *
 *
 * Created by 熊岳岳 on 2015/9/14.
 */
public class PatrolPointUnSelectedAdapter extends YjtBaseAdapter<PatrolPoint> {

    public PatrolPointUnSelectedAdapter(Context context) {
        super(context);
    }

    public PatrolPointUnSelectedAdapter(Context context, List<PatrolPoint> list) {
        super(context, list);
    }

//    @Override
//    public int getCount() {
//        return 0;
//    }

    public void setAllCheck(boolean isCheck){
        for(PatrolPoint info :mDatas){
            info.setSelected(isCheck);
        }
        notifyDataSetChanged();
    }

    @Override
    public void setInnerViewListener(YjtBaseHolder<PatrolPoint> holder, final int position, PatrolPoint info) {
        super.setInnerViewListener(holder, position, info);
        if(holder!=null&&holder instanceof PatrolPointUnSelectedHolder){
            ((PatrolPointUnSelectedHolder)holder).mCbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(position<mDatas.size()&&position>=0){
                             mDatas.get(position).setSelected(isChecked);
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
        return new PatrolPointUnSelectedHolder(mContext);
    }
}
