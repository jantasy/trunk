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
import cn.yjt.oa.app.beans.PatrolRoute;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.patrol.activitys.PatrolRouteActivity;
import cn.yjt.oa.app.patrol.holder.PatrolRouteHolder;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.patrol.protocol.PatrolRouteProtocol;
import cn.yjt.oa.app.patrol.protocol.PatrolRouteProtocol.OnRequestPatrolRouteListener;
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
public class PatrolRouteAdapter extends PatrolBaseAdapter<PatrolRoute> implements OnRefreshListener, OnLoadMoreListner,
	OnRequestPatrolRouteListener {

	private final String TAG = "PatrolRouteAdapter";

	private PatrolRouteProtocol mProtocol = PatrolRouteProtocol.getInstance();

	public PatrolRouteAdapter(Context context) {
		super(context);
	}

	public PatrolRouteAdapter(Context context, List<PatrolRoute> list) {
		super(context, list);
	}

	@Override
	public YjtBaseHolder<PatrolRoute> getHolder() {
		return new PatrolRouteHolder(MainApplication.getAppContext());
	}

	@Override
	public void onInnerItemClick(View view, int position) {
		super.onInnerItemClick(view, position);
		PatrolRouteActivity.launthWithPatrolRoute(mContext, (PatrolRoute) getItem(position));
	}

	@Override
	public void setOtherListener() {
		super.setOtherListener();
		mProtocol.setOnRequestPatrolRouteListener(this);
	}

	@Override
	public void setInnerViewListener(YjtBaseHolder<PatrolRoute> holder, final int position, final PatrolRoute info) {
		super.setInnerViewListener(holder, position, info);
		((PatrolRouteHolder) holder).mIvDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                final String routeId = String.valueOf(mDatas.get(position).getId());
                AlertDialog.Builder builder = AlertDialogBuilder.newBuilder(mContext);
                builder.setTitle("删除考勤路线").setMessage("确认删除此巡检路线吗？").setPositiveButton("删除", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePatrolPoint(routeId);

                        /*记录操作 1715*/
                        OperaEventUtils.recordOperation(OperaEvent.OPERA_DELETE_PATROLROUTE);
                    }
                }).setNegativeButton("取消", null).show();
			}
		});
	}

    /**删除巡更点*/
    private void deletePatrolPoint(final String routeId){
        PatrolApiHelper.deletePatrolRoute(new ProgressDialogResponseListener<Object>(mContext,"正在删除巡检路线") {
            @Override
            public void onSuccess(Object payload) {
                onRefresh();
            }
        }, AccountManager.getCurrent(MainApplication.getAppContext()).getCustId(),routeId);
    }

	@Override
	public void onLoadMore() {
		LogUtils.d(TAG, "上拉加载");
		mProtocol.requestLoadMoreDatas(mContext);
	}

	@Override
	public void onRefresh() {
		LogUtils.d(TAG, "下拉刷新");
		mProtocol.requestRefreshDatas(mContext);
	}

	@Override
	public void refreshPatrolRouteSuccess(List<PatrolRoute> datas) {
		mDatas = datas;
		notifyDataSetChanged();
		if (mListView != null && mListView instanceof PullToRefreshListView) {
			((PullToRefreshListView) mListView).onRefreshComplete();
		}
	}

	@Override
	public void loadMorePatrolRouteSuccess(List<PatrolRoute> datas) {
		mDatas.addAll(datas);
		notifyDataSetChanged();
		if (mListView != null && mListView instanceof PullToRefreshListView) {
			((PullToRefreshListView) mListView).onLoadMoreComplete();
		}
	}

}
