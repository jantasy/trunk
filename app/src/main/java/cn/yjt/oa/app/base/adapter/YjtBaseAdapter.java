package cn.yjt.oa.app.base.adapter;

import java.util.ArrayList;
import java.util.List;

import cn.yjt.oa.app.base.holder.YjtBaseHolder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * 翼机通的基类适配器
 * 
 * <pre>
 * 讲listview适配器中常用的方法封装起来，减少不必要的代码量
 * </pre>
 * @author 熊岳岳
 * @since 20150902
 */
public abstract class YjtBaseAdapter<T> extends BaseAdapter implements OnItemClickListener {

	private final String TAG = "YjtBaseAdapter";

	/** 上下文对象 */
	public Context mContext;
	/** 适配器的数据源 */
	public List<T> mDatas;
	/** 适配该适配器的listview对象 */
	public ListView mListView;

	/**
	 * 构造方法
	 * @param context 上下文对象
	 */
	public YjtBaseAdapter(Context context) {
		this.mContext = context;
		this.mDatas = new ArrayList<T>();
	}
	
	/**
	 * 构造方法
	 * @param context 上下文对象
	 * @param list 数据源集合
	 */
	public YjtBaseAdapter(Context context, List<T> list) {
		this.mContext = context;
		this.mDatas = list;
	}

	/**
	 * 传入listview，并给其设置监听
	 * @param listView
	 */
	public final void setListViewAndListener(ListView listView) {
		this.mListView = listView;
		if (mListView != null) {
			mListView.setOnItemClickListener(this);
			setOtherListener();
		}
	}


	/**
	 * 给当前适配器设置数据源，并刷新
	 * @param lists
	 */
	public void setDataListsAndRefresh(List<T> lists) {
		this.mDatas = lists;
		this.notifyDataSetChanged();
	}

	/*---------------实现父类及其实现的接口中所有所要重写的方法START---------------*/
	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		YjtBaseHolder<T> holder = null;
		if (convertView == null) {
			holder = getHolder();
			convertView = holder.getContvertView();
		} else {
			holder = (YjtBaseHolder<T>) convertView.getTag();
		}
		T info = (T) getItem(position);
		if(info!=null){
			holder.refreshView(position, info);
		}
		setInnerViewListener(holder, position, info);
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// 顶部view条目的数量
		int headerViewsCount = mListView.getHeaderViewsCount();
		position = position - headerViewsCount;
		onInnerItemClick(view, position);
	}

	/*---------------实现BaseAdapter中的方法END---------------*/

	
	/**
	 * 设置listview的其他点击事件
	 */
	public void setOtherListener() {
		
	}
	/**
	 * 获取适配holder
	 * @return 返回一个MeetingBaseHolder或者其子类对象
	 */
	public abstract YjtBaseHolder<T> getHolder();

	/**
	 * listview的条目点击事件
	 * @param view	被点击的view
	 * @param position	被点击的view所处的位置
	 */
	public void onInnerItemClick(View view, int position){
		
	}
	/**
	 * listview条目内部控件的点击事件
	 * @param holder
	 * @param position	
	 * @param info	
	 */
	public void setInnerViewListener(YjtBaseHolder<T> holder, int position, T info) {

	}
}
