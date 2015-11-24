package cn.yjt.oa.app.patrol.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import java.util.List;

import cn.yjt.oa.app.base.adapter.YjtBaseAdapter;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.RouteUser;
import cn.yjt.oa.app.patrol.holder.PatrolPersonBindHolder;

/**
 *
 *
 * Created by 熊岳岳 on 2015/9/14.
 */
public class PatrolPersonBindAdapter extends YjtBaseAdapter<RouteUser> {

    public boolean isScrolling = false;

    public PatrolPersonBindAdapter(Context context) {
        super(context);
    }

    public PatrolPersonBindAdapter(Context context, List<RouteUser> list) {
        super(context, list);
    }


//    @Override
//    public int getCount() {
//        return 0;
//    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PatrolPersonBindHolder holder = null;
        if (convertView == null) {
            holder = new PatrolPersonBindHolder(mContext);
            convertView = holder.getContvertView();
        } else {
            holder = (PatrolPersonBindHolder) convertView.getTag();
        }
        RouteUser info = (RouteUser) getItem(position);
        if(info!=null){
            holder.refreshViewWithScrolling(position, info,isScrolling);
        }
        setInnerViewListener(holder, position, info);
        return convertView;
    }

    @Override
    public void setOtherListener() {
        super.setOtherListener();
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {

                    //空闲状态
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        isScrolling = false;
                        notifyDataSetChanged();
                        break;

                    //滑动状态
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        isScrolling = true;
                        break;

                    //触摸后滚动
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        isScrolling = true;
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    public void setAllCheck(boolean isCheck){
        for(RouteUser info :mDatas){
            info.setSelected(isCheck);
        }
        notifyDataSetChanged();
    }

    @Override
    public void setInnerViewListener(YjtBaseHolder<RouteUser> holder, final int position, RouteUser info) {
        super.setInnerViewListener(holder, position, info);
        if(holder!=null&&holder instanceof PatrolPersonBindHolder){
            ((PatrolPersonBindHolder)holder).mCbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

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
    public YjtBaseHolder<RouteUser> getHolder() {
        return new PatrolPersonBindHolder(mContext);
    }
}
