package cn.yjt.oa.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.yjt.oa.app.R;

/**
 * 自定义路径导航控件
 * （组合控件 ：   aaa>bbb>ccc）
 *
 */
public class NavigationView extends LinearLayout implements View.OnClickListener {

	//回调接口
	private OnPopListener onPopListener;
	//布局填充器
	private LayoutInflater inflater;
	//条目数量
	private int itemCount;

	//可点击
	private boolean isCanClick = true;

	//------------------3个构造方法-----------------------
	public NavigationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public NavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public NavigationView(Context context) {
		super(context);
		init(context);
	}

	//--------------------------------------------------

	/**初始化*/
	private void init(Context context) {
		//设置布局内部控件水平排列
		setOrientation(HORIZONTAL);
		//加载布局填充器
		inflater = LayoutInflater.from(context);
	}

	/**添加控件到布局中（控件布局：    >XXXX )*/
	public void push(String itemTitle) {
		//填充布局文件获取item
		View item = inflater.inflate(R.layout.view_navigation_item, this, false);
		TextView titleView = (TextView) item.findViewById(R.id.item_title);

		//第一个条目没有左边箭头（如   111>222>333,其中111是没有左边箭头的）
		item.findViewById(R.id.arrow).setVisibility(itemCount == 0 ? View.INVISIBLE : View.VISIBLE);

		//给显示文字部分的textview控件设置id、
		titleView.setId(itemCount);
		titleView.setText(itemTitle);
		titleView.setOnClickListener(this);

		//将item添加到控件中
		addView(item);

		//item的数量加一
		itemCount++;
	}

	//----------------回调接口注册监听----------------
	public void setOnPopListener(OnPopListener listener) {
		this.onPopListener = listener;
	}

	public static interface OnPopListener {
		public void onPop(int index);
	}

	//--------------------------------------------

	/**获取内部控件的点击事件*/
	@Override
	public void onClick(View v) {
		if (isCanClick) {
			//获取点击的控件的id
			int id = v.getId();

			//移除比该控件id要大的控件
			//（如：aaa<bbb<ccc  点了bbb后，将ccc移除掉）
			for (int i = itemCount - 1; i > id; i--) {
				removeViewAt(i);
			}

			//同时当前内部控件的数量为当前的id加一
			itemCount = id + 1;

			//调用回调接口中的方法
			if (onPopListener != null) {
				onPopListener.onPop(id);
			}
		}
	}

	/**移除所有的内部控件*/
	public void clear() {
		removeAllViews();
		itemCount = 0;
		if (onPopListener != null) {
			onPopListener.onPop(0);
		}
	}
	
	public boolean isCanClick() {
		return isCanClick;
	}

	public void setCanClick(boolean isCanClick) {
		this.isCanClick = isCanClick;
	}
	
}
