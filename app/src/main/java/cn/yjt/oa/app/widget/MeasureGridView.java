package cn.yjt.oa.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MeasureGridView extends GridView {

	public MeasureGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
