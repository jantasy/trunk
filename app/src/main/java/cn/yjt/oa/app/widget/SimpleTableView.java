package cn.yjt.oa.app.widget;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.yjt.oa.app.R;

public class SimpleTableView extends ViewGroup {

	int mCols;
	int mRows;
	int mDividerHeight;
	int mDividerWidth;
	Drawable mDividerHDrawable;
	Drawable mDividerVDrawable;
	
	int mItemWidth;
	int mItemHeight;
	int mRowHeight;
	int mActualShowingColCount;
	int mActualShowingRowCount;
	LayoutInflater mInflater;
	boolean mAutoFitColsRows = true;
	
	private ArrayList<Rect> mHorizontalDividerRects;
	private ArrayList<Rect> mVerticalDividerRects;
	
	public SimpleTableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	public SimpleTableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public SimpleTableView(Context context) {
		super(context);
		init(context, null, 0);
	}
	
	private void init(Context context, AttributeSet attrs, int defStyle) {
		setWillNotDraw(false);
		mInflater = LayoutInflater.from(context);
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleTableView, defStyle, 0);
			mDividerHDrawable = a.getDrawable(R.styleable.SimpleTableView_dividerH);
			if (mDividerHDrawable != null) {
				mDividerHeight = mDividerHDrawable.getIntrinsicHeight();
				mHorizontalDividerRects = new ArrayList<Rect>();
			}
			mDividerVDrawable = a.getDrawable(R.styleable.SimpleTableView_dividerV);
			if (mDividerVDrawable != null) {
				mDividerWidth = mDividerVDrawable.getIntrinsicWidth();
				mVerticalDividerRects = new ArrayList<Rect>();
			}
			int dividerWidth = a.getDimensionPixelSize(R.styleable.SimpleTableView_dividerWidth, 0);
			if (dividerWidth != 0) {
				mDividerWidth = dividerWidth;
				mDividerHeight = dividerWidth;
			}
			mRowHeight = a.getDimensionPixelSize(R.styleable.SimpleTableView_tableRowHeight, 0);
			mCols = a.getInt(R.styleable.SimpleTableView_tableCols, 1);
			mRows = a.getInt(R.styleable.SimpleTableView_tableRows, 1);
			a.recycle();
		}
	}
	
	public LayoutInflater getLayoutInflater() {
		return mInflater;
	}
	
	public void setColsAndRows(int cols, int rows) {
		mCols = cols;
		mRows = rows;
	}
	
	public void setAutoFitColsRows(boolean auto) {
		mAutoFitColsRows = auto;
	}
	
	public void setHorizontalDividerWidth(int w) {
		mDividerWidth = w;
	}
	
	public void setVerticalDividerHeight(int h) {
		mDividerHeight = h;
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = mDividerHDrawable;
        Integer saveCount;
        if (drawable != null) {
            // If we have a horizontal divider to draw, draw it at the remembered positions
            final ArrayList<Rect> rects = mHorizontalDividerRects;
            for (int i = rects.size() - 1; i >= 0; i--) {
                drawable.setBounds(rects.get(i));
                saveCount = null;
                if (drawable instanceof ColorDrawable) {
        			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
        				saveCount = canvas.save();
        				canvas.clipRect(drawable.getBounds());
        			}
        		}
                drawable.draw(canvas);
                if (saveCount != null) {
                	canvas.restoreToCount(saveCount.intValue());
                }
            }
        }

        drawable = mDividerVDrawable;
        if (drawable != null) {
            // If we have a vertical divider to draw, draw it at the remembered positions
            final ArrayList<Rect> rects = mVerticalDividerRects;
            for (int i = rects.size() - 1; i >= 0; i--) {
                drawable.setBounds(rects.get(i));
                saveCount = null;
                if (drawable instanceof ColorDrawable) {
        			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
        				saveCount = canvas.save();
        				canvas.clipRect(drawable.getBounds());
        			}
        		}
                drawable.draw(canvas);
                if (saveCount != null) {
                	canvas.restoreToCount(saveCount.intValue());
                }
            }
        }
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
		for (int i=0, j=getChildCount(); i<j; ++i) {
			View child = getChildAt(i);
			int col = i%mActualShowingColCount;
			int row = i/mActualShowingColCount;
			int left = paddingLeft+(mItemWidth+mDividerWidth)*col;
			int top = paddingTop+(mItemHeight+mDividerHeight)*row;
			child.layout(left, top, left+mItemWidth, top+mItemHeight);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);
        
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
        
        if (widthSpecMode == MeasureSpec.UNSPECIFIED) {
            throw new RuntimeException("SimpleTableView width cannot have UNSPECIFIED dimensions");
        }
        
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        int childTopExtraPadding = 0;
        int childBottomExtraPadding = 0;
        
        final int childCount = getChildCount();
        if (childCount == 0) {
        	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        	return;
        }
        int actualShowingColCount;
        if (mAutoFitColsRows) {
        	actualShowingColCount = childCount >= mCols ? Math.min((int)Math.ceil((float)childCount/mRows), mCols) : childCount;
        } else {
        	actualShowingColCount = mCols;
        }
        mActualShowingColCount = actualShowingColCount;
        
        int itemWidth = (widthSpecSize-paddingLeft-paddingRight-mDividerWidth*(actualShowingColCount-1))/actualShowingColCount;
        
        int actualShowingRows;
        if (mAutoFitColsRows) {
        	actualShowingRows = Math.min((int)Math.ceil((float)childCount/actualShowingColCount), mRows);
        } else {
        	actualShowingRows = mRows;
        }
    	mActualShowingRowCount = actualShowingRows;
    	
        if (heightSpecMode != MeasureSpec.EXACTLY) {
        	if (childCount > 0) {
        		View child = getChildAt(0);
        		child.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), 
        				getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, LayoutParams.WRAP_CONTENT));
        		int childHeight = child.getMeasuredHeight();
        		heightSpecSize = (mRowHeight > 0 ? mRowHeight : childHeight)*actualShowingRows + (actualShowingRows-1)*mDividerHeight+paddingTop+paddingBottom;
        		if (mRowHeight > 0) {
        			int heightExtra = heightSpecSize-childHeight*actualShowingRows-(actualShowingRows-1)*mDividerHeight;
            		childTopExtraPadding = (int)(heightExtra/(2f*actualShowingRows));
            		childBottomExtraPadding = heightExtra/actualShowingRows-childTopExtraPadding;
            	}
        	}
        } else {
        	if (childCount > 0) {
        		View child = getChildAt(0);
        		if (child instanceof TextView) {
        			child.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), 
            				getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, LayoutParams.WRAP_CONTENT));
            		final int childHeight = child.getMeasuredHeight();
            		int heightExtra = heightSpecSize-childHeight*actualShowingRows-(actualShowingRows-1)*mDividerHeight;
            		childTopExtraPadding = (int)(heightExtra/(2f*actualShowingRows));
            		childBottomExtraPadding = heightExtra/actualShowingRows-childTopExtraPadding;
        		}
        	}
        }
        
        int itemHeight = (heightSpecSize-paddingTop-paddingBottom-(actualShowingRows-1)*mDividerHeight)/actualShowingRows;
        
        for (int i=0, j=getChildCount(); i<j; ++i) {
        	View child = getChildAt(i);
        	child.setPadding(child.getPaddingLeft(), child.getPaddingTop()+childTopExtraPadding, 
        			child.getPaddingRight(), child.getPaddingBottom()+childBottomExtraPadding);
    		child.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), 
    				MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY));
        }
        
        mItemWidth = itemWidth;
        mItemHeight = itemHeight;
        
        final int viewContentWidth = widthSpecSize-paddingLeft-paddingRight;
        final int viewContentHeight = heightSpecSize-paddingTop-paddingBottom;
        if (mHorizontalDividerRects != null) {
        	mHorizontalDividerRects.clear();
            if (mActualShowingRowCount > 1) {
            	for (int i=1; i<mActualShowingRowCount; ++i) {
            		int top = paddingTop+mItemHeight*i+mDividerHeight*(i-1);
            		mHorizontalDividerRects.add(new Rect(paddingLeft, top, paddingLeft+viewContentWidth, top+mDividerHeight));
            	}
            }
        }
        
        if (mVerticalDividerRects != null) {
        	mVerticalDividerRects.clear();
            if (mActualShowingColCount > 1) {
            	for (int i=1; i<mActualShowingColCount; ++i) {
            		int left = paddingLeft+mItemWidth*i+mDividerWidth*(i-1);
            		mVerticalDividerRects.add(new Rect(left, paddingTop, left+mDividerWidth, paddingTop+viewContentHeight));
            	}
            }
        }
        
        setMeasuredDimension(widthSpecSize, heightSpecSize);
	}
	
	public int getActualShowingRowCount() {
		return mActualShowingRowCount;
	}

}
