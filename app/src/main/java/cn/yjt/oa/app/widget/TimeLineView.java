package cn.yjt.oa.app.widget;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import cn.yjt.oa.app.R;

public class TimeLineView extends View {

	public TimeLineView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	public TimeLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public TimeLineView(Context context) {
		super(context);
		init(context, null, 0);
	}
	
	private Drawable nodeDrawable;
	private Drawable lineDrawable;
	private int nodeWidth;
	private int nodeHeight;
	private int lineWidth;
	private boolean firstNodeShowing;
	
	/**
	 * node y position
	 */
	private ArrayList<Integer> nodePositions = new ArrayList<Integer>();
	
	private void init(Context context, AttributeSet attrs, int defStyle) {
		setWillNotDraw(false);
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeLineView, defStyle, 0);
			lineWidth = a.getDimensionPixelSize(R.styleable.TimeLineView_timelineWidth, 0);
			nodeWidth = a.getDimensionPixelSize(R.styleable.TimeLineView_nodeWidth, 0);
			nodeHeight = a.getDimensionPixelSize(R.styleable.TimeLineView_nodeHeight, 0);
			setLineDrawable(a.getDrawable(R.styleable.TimeLineView_lineDrawable));
			setNodeDrawable(a.getDrawable(R.styleable.TimeLineView_nodeDrawable));
			a.recycle();
		}
	}
	
	public void setLineDrawable(Drawable line) {
		lineDrawable = line;
		if (lineWidth == 0 && line != null) {
			lineWidth = line.getIntrinsicWidth();
		}
	}
	
	public void setNodeDrawable(Drawable node) {
		nodeDrawable = node;
		if (node != null) {
			if (nodeWidth == 0)
				nodeWidth = node.getIntrinsicWidth();
			if (nodeHeight == 0)
				nodeHeight = node.getIntrinsicHeight();
		}
	}
	
	public void updateTimeNodePosition(Collection<Integer> positions) {
		nodePositions.clear();
		if (positions != null) {
			nodePositions.addAll(positions);
		}
		invalidate();
	}
	
	public void setFirstNodeShowing(boolean showing) {
		firstNodeShowing = showing;
	}
	
	public void clearAllTimeNodes() {
		nodePositions.clear();
	}
	
	public void addTimeNode(int position) {
		nodePositions.add(position);
	}
	
	@Override
    protected int getSuggestedMinimumWidth() {
		int min = super.getSuggestedMinimumWidth();
		int horizontalPadding = getPaddingLeft() + getPaddingRight();
        return max(min, max(nodeWidth+horizontalPadding, lineWidth+horizontalPadding));
    }
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }
	
	public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
        case MeasureSpec.AT_MOST:
            result = size;
            break;
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result;
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(nodePositions.isEmpty()){
			return;
		}
		int left = getPaddingLeft();
		int top = getPaddingTop();
		int right = getWidth()-getPaddingRight();
		int bottom = getHeight()-getPaddingBottom();
		if (firstNodeShowing) {
			top = Math.max(top, nodePositions.get(0));
		}
		drawLine(canvas, left, top, right-left, bottom-top);
		drawNode(canvas, left, top, right-left, bottom);
	}

	private void drawNode(Canvas canvas, int left, int top, int width, int bottom) {
		int nodeLeft = left + (width-nodeWidth)/2;
		int halfNodeHeight = nodeHeight/2;
		for (int item:nodePositions) {
			//node outside no need to draw
			if (item+halfNodeHeight < top || item-halfNodeHeight > bottom) {
				continue;
			}
			
			nodeDrawable.setBounds(nodeLeft, item-halfNodeHeight, nodeLeft+nodeWidth, item+halfNodeHeight);
			drawDrawable(canvas, nodeDrawable);
		}
		
	}

	private void drawLine(Canvas canvas, int left, int top, int width, int height) {
		int lineLeft = left + (width-lineWidth)/2;
		lineDrawable.setBounds(lineLeft, top, lineLeft+lineWidth, top+height);
		drawDrawable(canvas, lineDrawable);
	}
	
	private void drawDrawable(Canvas canvas, Drawable drawable) {
		int saveCount = -1;
		if (drawable instanceof ColorDrawable) {
			saveCount = canvas.save();
			canvas.clipRect(drawable.getBounds());
		}
		
		drawable.draw(canvas);
		if (saveCount != -1) {
			canvas.restoreToCount(saveCount);
		}
	}

}
