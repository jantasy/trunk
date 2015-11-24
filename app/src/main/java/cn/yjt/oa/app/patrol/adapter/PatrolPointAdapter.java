package cn.yjt.oa.app.patrol.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.PatrolPoint;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.patrol.activitys.PatrolPointActivity;
import cn.yjt.oa.app.patrol.holder.PatrolPointHolder;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.patrol.protocol.PatrolPointProtocol;
import cn.yjt.oa.app.patrol.protocol.PatrolPointProtocol.OnRequestPatrolPointListener;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

/**
 * 巡检点适配器
 * @author 熊岳岳
 * @since 20150907
 */
public class PatrolPointAdapter extends PatrolBaseAdapter<PatrolPoint> implements OnRefreshListener, OnLoadMoreListner,
	OnRequestPatrolPointListener {

	private final String TAG = "PatrolPointAdapter";
	/** 巡检点的获取数据的协议对象 */
	private PatrolPointProtocol mProtocol = PatrolPointProtocol.getInstance();

	/*------构造方法START------*/
	public PatrolPointAdapter(Context context) {
		super(context);

	}

	public PatrolPointAdapter(Context context, List<PatrolPoint> list) {
		super(context, list);
	}

	/*------构造方法END------*/

	@Override
	public YjtBaseHolder<PatrolPoint> getHolder() {
		return new PatrolPointHolder(MainApplication.getAppContext());
	}

	@Override
	public void onInnerItemClick(View view, int position) {
		super.onInnerItemClick(view, position);
		PatrolPointActivity.launthWithPatrolPoint(mContext, (PatrolPoint)getItem(position));
	}

	@Override
	public void setOtherListener() {
		super.setOtherListener();
		mProtocol.setOnRequestPatrolPointListener(this);
	}

	@Override
	public void setInnerViewListener(YjtBaseHolder<PatrolPoint> holder, final int position, final PatrolPoint info) {
		super.setInnerViewListener(holder, position, info);
        //TODO：条目删除操作
		((PatrolPointHolder) holder).mIvDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                final String pointId = String.valueOf(mDatas.get(position).getId());
                AlertDialog.Builder builder = AlertDialogBuilder.newBuilder(mContext);
                builder.setTitle("删除巡检点").setMessage("确认删除此巡检点吗？").setPositiveButton("删除", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      deletePatrolPoint(pointId);

                      /*记录操作 1712*/
                      OperaEventUtils.recordOperation(OperaEvent.OPERA_DELETE_PATROLPOINT);
                    }
                }).setNegativeButton("取消", null).show();


			}
		});
	}

    /**删除巡更点*/
    private void deletePatrolPoint(final String pointId){
        PatrolApiHelper.deletePatrolPoint(new ProgressDialogResponseListener<Object>(mContext,"正在删除巡检点") {
            @Override
            public void onSuccess(Object payload) {
                onRefresh();
            }
        },AccountManager.getCurrent(mContext).getCustId(),pointId);
    }

	//listview下拉刷新时调用
	@Override
	public void onRefresh() {
		LogUtils.d(TAG, "下拉刷新");
		mProtocol.requestRefreshDatas(mContext);
	}

	//listview上拉加载时调用
	@Override
	public void onLoadMore() {
		LogUtils.d(TAG, "上拉加载");
		mProtocol.requestLoadMoreDatas(mContext);
	}

	@Override
	public void refreshPatrolPointSuccess(List<PatrolPoint> datas) {
		mDatas = datas;
		notifyDataSetChanged();
		if (mListView != null && mListView instanceof PullToRefreshListView) {
			((PullToRefreshListView) mListView).onRefreshComplete();
		}
	}

	@Override
	public void loadMorePatrolPointSuccess(List<PatrolPoint> datas) {
		mDatas.addAll(datas);
		notifyDataSetChanged();
		if (mListView != null && mListView instanceof PullToRefreshListView) {
			((PullToRefreshListView) mListView).onLoadMoreComplete();
		}
	}
}
