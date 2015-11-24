package cn.yjt.oa.app.contactlist.view;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.yjt.oa.app.R;

public class IndexView extends LinearLayout {

	private int childHeight;
	private String[] indexs;

	private OnIndexChoseListener cl;

	private TextView indexShow;

	private float density;
	
	private int selectedIndex = -1;
	
	public IndexView(Context context) {
		super(context);
		getDensity(context);
	}

	public IndexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getDensity(context);
	}

	public IndexView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getDensity(context);
	}

	private void getDensity(Context context){
		this.density = context.getResources().getDisplayMetrics().density;
	}
	
	public void setIndexs(String[] indexs, OnIndexChoseListener l) {
		this.indexs = indexs;
		this.cl = l;
		setUpView();
	}

	public void setIndexShowTextView(TextView tv) {
		indexShow = tv;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int acion = event.getAction();
		switch (acion) {
		case MotionEvent.ACTION_DOWN:
			countSelectIndex(event);
			if (indexShow != null) {
				indexShow.setVisibility(VISIBLE);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			countSelectIndex(event);
			break;
		case MotionEvent.ACTION_UP:
			setActionCancle();
			break;
		case MotionEvent.ACTION_CANCEL :
			setActionCancle();
			break;
		}
		return true;
	}
	
	private void countSelectIndex(MotionEvent event){
		int index = (int) (event.getY() / childHeight);
		if (index > -1 && index < indexs.length) {
			if (cl != null) {
				cl.onIndexChose(index, indexs[index]);
			}
			if (indexShow != null) {
				indexShow.setText(indexs[index]);
			}
			setSelectedIndexBg(index);
		}
	}
	
	private void setActionCancle(){
		if (indexShow != null) {
			indexShow.setVisibility(GONE);
		}
		if (selectedIndex != -1) {
			TextView view = (TextView) ((LinearLayout) getChildAt(selectedIndex)).getChildAt(0); 
			view.setBackgroundColor(unSelectedColorBg());
			view.setTextColor(unSelectedTextColor());
			selectedIndex = -1;
		}
	}
	
	private int unSelectedColorBg(){
		return Color.parseColor("#00ffffff");
	}
	
	private int selectedResBg(){
		return R.drawable.contactlist_index_select_bg;
	}
	
	private int unSelectedTextColor(){
		return Color.parseColor("#33000000");
	}
	private int selectedTextColor(){
		return Color.WHITE;
	}
	
	private void setSelectedIndexBg(int index){
		if (selectedIndex != -1) {
			TextView view = (TextView) ((LinearLayout) getChildAt(selectedIndex)).getChildAt(0);
			view.setBackgroundColor(unSelectedColorBg());
			view.setTextColor(unSelectedTextColor());
		}
		TextView view = (TextView) ((LinearLayout) getChildAt(index)).getChildAt(0); 
		view.setBackgroundResource(selectedResBg());
		view.setTextColor(selectedTextColor());
		selectedIndex = index;
	}
	
	private void setUpView() {
		if (indexs == null) {
			return;
		}
		for (String str : indexs) {
			TextView tv = getTextView();
			tv.setText(str);
			LinearLayout layout = getView();
			layout.addView(tv);
			addView(layout);
		}

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}
	
	private TextView getTextView() {
		TextView textView = new TextView(getContext());
		int textViewSize =  Math.round(11 * density);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				textViewSize, textViewSize);
		textView.setLayoutParams(params);
		textView.setGravity(Gravity.CENTER);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
		textView.setTextColor(unSelectedTextColor());
		return textView;
	}

	private LinearLayout getView(){
		LinearLayout layout = new LinearLayout(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, 0);
		params.weight = 1;
		params.gravity = Gravity.CENTER;
		layout.setGravity(Gravity.CENTER);
		layout.setLayoutParams(params);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		return layout;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (indexs != null && indexs.length > 0) {
			int indexLayoutHeight = MeasureSpec.getSize(heightMeasureSpec);
			int indexHeight = indexLayoutHeight / indexs.length;
			childHeight = indexHeight;
		}
	}
	
	public interface OnIndexChoseListener {
		public void onIndexChose(int index, String indexStr);
	}
	
}
