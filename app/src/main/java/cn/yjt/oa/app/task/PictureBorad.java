package cn.yjt.oa.app.task;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import cn.yjt.oa.app.R;

public class PictureBorad extends FrameLayout {

	public PictureBorad(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public PictureBorad(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PictureBorad(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater.from(context)
				.inflate(R.layout.picture_borad, this, true);
		
	}

}
