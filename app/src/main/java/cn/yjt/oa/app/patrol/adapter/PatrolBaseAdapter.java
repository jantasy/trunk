package cn.yjt.oa.app.patrol.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;

import cn.yjt.oa.app.base.adapter.YjtBaseAdapter;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

/**
 * 巡更基类适配器
 * @author 熊岳岳
 * @since 20150907
 */
public abstract class PatrolBaseAdapter<T> extends YjtBaseAdapter<T> implements OnRefreshListener,OnLoadMoreListner{
	
	public PatrolBaseAdapter(Context context) {
		super(context);
	}
	
	public PatrolBaseAdapter(Context context, List<T> list) {
		super(context, list);
	}

	@Override
	public void onInnerItemClick(View view, int position) {
		super.onInnerItemClick(view, position);
	}
	
	@Override
	public void setOtherListener() {
		super.setOtherListener();
		if(mListView!=null && mListView instanceof PullToRefreshListView){
			((PullToRefreshListView)mListView).setOnRefreshListener(this);
			((PullToRefreshListView)mListView).setOnLoadMoreListner(this);
		}
	}
}
