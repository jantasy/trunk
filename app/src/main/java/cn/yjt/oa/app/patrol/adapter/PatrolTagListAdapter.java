package cn.yjt.oa.app.patrol.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.NFCTagInfo;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.PatrolPoint;
import cn.yjt.oa.app.beans.PatrolTag;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.patrol.holder.PatrolPointHolder;
import cn.yjt.oa.app.patrol.holder.PatrolTagListHolder;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.patrol.protocol.PatrolTagListProtocol;
import cn.yjt.oa.app.patrol.protocol.PatrolTagListProtocol.OnRequestPatrolTagListListener;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

/**
 * 巡检标签适配器
 * @author 熊岳岳
 * @since 20150907
 */
public class PatrolTagListAdapter extends PatrolBaseAdapter<PatrolTag> implements OnRefreshListener,
	OnLoadMoreListner,
	OnRequestPatrolTagListListener {


    private final String TAG = "PatrolTagListAdapter";
    private String mFilterString = null;

	private PatrolTagListProtocol mProtocol = PatrolTagListProtocol.getInstance();

	public PatrolTagListAdapter(Context context) {
		super(context);
	}

	public PatrolTagListAdapter(Context context, List<PatrolTag> list) {
		super(context, list);
	}


    public void setmFilterString(String mFilterString) {
        this.mFilterString = mFilterString;
    }


	@Override
	public void setOtherListener() {
		super.setOtherListener();
		mProtocol.setOnRequestPatrolTagListListener(this);
	}

	@Override
	public void setInnerViewListener(YjtBaseHolder<PatrolTag> holder, final int position, final PatrolTag info) {
		super.setInnerViewListener(holder, position, info);
        //TODO:条目删除事件
		((PatrolTagListHolder) holder).mIvTagDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                final String tagId = String.valueOf(mDatas.get(position).getId());
                AlertDialog.Builder builder = AlertDialogBuilder.newBuilder(mContext);
                builder.setTitle("删除巡检标签").setMessage("确认删除此巡检标签吗？").setPositiveButton("删除", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePatrolPoint(tagId);

                        /*记录操作 1720*/
                        OperaEventUtils.recordOperation(OperaEvent.OPERA_DELETE_PATROLTAG);
                    }
                }).setNegativeButton("取消", null).show();
			}
		});
	}

    /**删除巡更点*/
    private void deletePatrolPoint(final String tagId){
        PatrolApiHelper.deletePatrolTag(new ProgressDialogResponseListener<Object>(mContext,"正在删除标签") {
            @Override
            public void onSuccess(Object payload) {
                Toast.makeText(mContext,"删除成功",Toast.LENGTH_SHORT).show();
                onRefresh();
            }
        }, AccountManager.getCurrent(MainApplication.getApplication()).getCustId(),tagId);
    }

    public void onRefreshWithFilter(String filter){
        mFilterString = filter;
        onRefresh();
    }

	@Override
	public YjtBaseHolder<PatrolTag> getHolder() {
		return new PatrolTagListHolder(mContext);
	}

	@Override
	public void onInnerItemClick(View view, int position) {
		super.onInnerItemClick(view, position);
		LogUtils.d(TAG, "您点击的条目" + position);
	}

	@Override
	public void onLoadMore() {
		LogUtils.d(TAG, "上拉加载");
		mProtocol.filterLoadMoreDatas(mFilterString);
	}

	@Override
	public void onRefresh() {
		LogUtils.d(TAG, "下拉刷新");
		mProtocol.filterRefreshDatas(mFilterString);
	}

	@Override
	public void refreshPatrolPatrolTagListSuccess(List<PatrolTag> datas) {
		mDatas = datas;
		notifyDataSetChanged();
		if (mListView != null && mListView instanceof PullToRefreshListView) {
			((PullToRefreshListView) mListView).onRefreshComplete();
		}
	}

	@Override
	public void loadMorePatrolPatrolTagListSuccess(List<PatrolTag> datas) {
		mDatas.addAll(datas);
		notifyDataSetChanged();
		if (mListView != null && mListView instanceof PullToRefreshListView) {
			((PullToRefreshListView) mListView).onLoadMoreComplete();
		}
	}

}
