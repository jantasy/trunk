package cn.yjt.oa.app.meeting.adapter;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.base.adapter.YjtBaseAdapter;
import cn.yjt.oa.app.base.holder.YjtBaseHolder;
import cn.yjt.oa.app.beans.MeetingInfo;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.meeting.MeetingInfoActivity;
import cn.yjt.oa.app.meeting.holder.MeetingItemHolder;
import cn.yjt.oa.app.meeting.http.MeetingApiHelper;

/**
 * 会议列表的适配器
 * @author 熊岳岳
 * @since 20150827
 */
public class MeetingAdapter extends YjtBaseAdapter<MeetingInfo> {

	private final String TAG = "MeetingAdapter";

	public MeetingAdapter(Context context, List<MeetingInfo> list) {
		super(context, list);
	}

	@Override
	public YjtBaseHolder<MeetingInfo> getHolder() {
		return new MeetingItemHolder(MainApplication.getAppContext());
	}

	@Override
	public void onInnerItemClick(View view,int position) {
		MeetingInfo info = (MeetingInfo) getItem(position);
		MeetingInfoActivity.launch(MainApplication.getAppContext(), info);
	}

	@Override
	public void setInnerViewListener(YjtBaseHolder<MeetingInfo> holder, final int position, final MeetingInfo info) {
		if (holder instanceof MeetingItemHolder) {
			((MeetingItemHolder) holder).mIvDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteItem(position, info.getId());
				}
			});
		}
	}

	/** 删除条目 */
	private void deleteItem(final int position, final long id) {
		if(!(mContext instanceof Activity)){
			return ;
		}
		Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("确定要删除会议:" + mDatas.get(position).getName())
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MeetingApiHelper.deletePublicMeeting(new ProgressDialogResponseListener<Object>(mContext, "正在删除") {
						@Override
						public void onSuccess(Object payload) {
							mDatas.remove(position);
							MeetingAdapter.this.notifyDataSetChanged();
						}
					}, id);
				}

			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).show();
	}
}
