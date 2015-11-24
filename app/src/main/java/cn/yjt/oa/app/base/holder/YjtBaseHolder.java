package cn.yjt.oa.app.base.holder;

import android.content.Context;
import android.view.View;
import cn.yjt.oa.app.beans.MeetingInfo;
import cn.yjt.oa.app.utils.BitmapUtils;

public abstract class YjtBaseHolder<T> {

	private final String TAG = "YjtBaseHolder";

	/**上下文对象*/
	public Context mContext;
	/**该holder的所代表的view对象*/
	public View mConvertView;

	public YjtBaseHolder(Context context) {
		this.mContext = context;
		this.mConvertView = initView();
		mConvertView.setTag(this);
	}

	/**获取该holder的所代表的view对象*/
	public View getContvertView() {
		return mConvertView;
	}

	/**创建view对象 初始化控件*/
	public abstract View initView();
	
	/**刷新view对象，填充数据*/
	public abstract void refreshView(int position,T info);
}
