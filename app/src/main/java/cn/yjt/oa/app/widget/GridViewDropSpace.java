package cn.yjt.oa.app.widget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.utils.ViewUtil;
import cn.yjt.oa.app.widget.dragdrop.DragContainer;
import cn.yjt.oa.app.widget.dragdrop.DragSource;
import cn.yjt.oa.app.widget.dragdrop.DropTarget;

public class GridViewDropSpace extends GridView implements DropTarget, DragSource, AbsListView.OnScrollListener {

	public GridViewDropSpace(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GridViewDropSpace(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GridViewDropSpace(Context context) {
		super(context);
		init();
	}
	
	public interface ReorderListener {
		boolean onReorder(int fromPos, int toPos);
	}
	
	private int dragPosOrg;
	private int dragPosCurrent;
	private int ignorePosition;
	private WeakReference<View> dragView;
	private BitmapDrawable dragViewDrawable;
	private DragContainer container;
	private boolean inDragMode;
	private Rect sharedRect = new Rect();
	private ArrayList<Rect> viewBounds;
	
	private ReorderListener reorderListener;
	
	private int scrollSpace = 100;
	private int smoothScrollDistance;
	private boolean scrollPosted;
	private boolean lockDragMoveForScroll;
	private boolean lockDragMoveForAnimate;
	private boolean postReorder;
	
	/**
	 * scroll state
	 */
	private int scrollFirstVisble;
	private int scrollFirstTop;
	
	private int motionX;
	private int motionY;
	
	private float dragStartX;
	private float dragStartY;
	
	private DragOverListener dragOverListener;
	
	private final static long SCROLL_DELAY = 500L;
	private final static int SCROLL_DURATION = 500;
	private final static long REORDER_ANIMATE_DURATION = 300L;
	
	@SuppressLint("NewApi")
	private Animator.AnimatorListener animatorListener = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? null : new  Animator.AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			lockDragMoveForAnimate = false;
			if (postReorder) {
				if (reorderListener != null)
					reorderListener.onReorder(dragPosOrg, dragPosCurrent);
				endDrop();
				postReorder = false;
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			lockDragMoveForAnimate = false;
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}
	};
	
	private void init() {
		scrollSpace = getContext().getResources().getDimensionPixelSize(R.dimen.gridview_scroll_edge);
		setOnScrollListener(this);
	}
	
	private boolean isLockDragMove() {
		return lockDragMoveForAnimate | lockDragMoveForScroll;
	}
	
	public void setDragOverListener(DragOverListener dragOverListener){
		this.dragOverListener = dragOverListener;
	}
	
	public void setIgnorePosition(int position) {
		ignorePosition = position;
	}
	
	public void setDragContainer(DragContainer container) {
		this.container = container;
		if (container != null) {
			container.setAutoStartDrag(false);
		}
	}
	
	public void setReorderListener(ReorderListener listener) {
		this.reorderListener = listener;
	}
	
	public void startDragMode() {
		startDragMode(-1, null);
	}
	
	public void startDragMode(int position, View dragView) {
		dragStartX = -1;
		dragStartY = -1;
		
		inDragMode = true;
		dragPosOrg = position;
		dragPosCurrent = dragPosOrg;
		initDragState();
		if (dragPosOrg != -1) {
			container.startDrag(this, dragPosOrg);
			Bitmap bmp = ViewUtil.createViewCacheBitmap(dragView, 1f, true);
			dragViewDrawable = new BitmapDrawable(getResources(), bmp);
			dragViewDrawable.setBounds(dragView.getLeft(), dragView.getTop(), dragView.getRight(), dragView.getBottom());
		}
	}
	
	public boolean isInDragMode() {
		return inDragMode;
	}

	@Override
	public boolean accept(Object dragItem, DragSource dragSource) {
		return true;
	}

	@Override
	public void drop(Object dragItem, int x, int y) {
		if (dragPosOrg != dragPosCurrent) {
			if (isLockDragMove()) {
				postReorder = true;
			} else {
				if (reorderListener != null)
					reorderListener.onReorder(dragPosOrg, dragPosCurrent);
			}
			
		}
	}

	@Override
	public void onDragIn(int x, int y) {
		motionX = x;
		motionY = y;
		if(dragStartX == -1){
			dragStartX = x;
			dragStartY = y;
			System.out.println("onDragIn x="+x+" y="+y + " dragStartX="+dragStartX+" dragStartY="+dragStartY);
		}
		
		if (isLockDragMove())
			return;
		processDragMove(x, y);
	}

	private Runnable smoothScrollRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (smoothScrollDistance != 0 && scrollPosted) {
				if (lockDragMoveForAnimate)
					postDelayed(this, SCROLL_DELAY);
				resetAllViewPosition();
				scrollPosted = false;
				smoothScrollBy(smoothScrollDistance, SCROLL_DURATION);
			}
		}
	};
	
	private int getScrollDistanceByPostitionOffset(int offset) {
        int index = -1;
        if (offset < 0) {
            index = getFirstVisiblePosition();
        } else if (offset > 0) {
            index = getLastVisiblePosition();
        }

        if (index > -1) {
            View child = getChildAt(index - getFirstVisiblePosition());
            if (child != null) {
                Rect visibleRect = new Rect();
                if (child.getGlobalVisibleRect(visibleRect)) {
                    // the child is partially visible
                    int childRectArea = child.getWidth() * child.getHeight();
                    int visibleRectArea = visibleRect.width() * visibleRect.height();
                    float visibleArea = (visibleRectArea / (float) childRectArea);
                    final float visibleThreshold = 0.75f;
                    if ((offset < 0) && (visibleArea < visibleThreshold)) {
                        // the top index is not perceivably visible so offset
                        // to account for showing that top index as well
                        ++index;
                    } else if ((offset > 0) && (visibleArea < visibleThreshold)) {
                        // the bottom index is not perceivably visible so offset
                        // to account for showing that bottom index as well
                        --index;
                    }
                }
                int pos = Math.max(0, Math.min(getCount()-1, index + offset));
                int newIndex = pos-getFirstVisiblePosition();
                if (newIndex < getChildCount() && newIndex >= 0) {
                	View scrollToView = getChildAt(newIndex);
                	if (offset > 0) {
                    	if (scrollToView.getBottom() <= getBottom()) {
                    		// no need to scroll
                    		return 0;
                    	} else {
                    		return scrollToView.getBottom() - getBottom();
                    	}
                	} else {
                    	if (scrollToView.getTop() >= 0) {
                    		// no need to scroll
                    		return 0;
                    	} else {
                    		return scrollToView.getTop();
                    	}
                	}
                	
                } else {
                	return offset > 0 ? child.getHeight() : -child.getHeight();
                }
            }
        }
        
        return 0;
    }
	
	private void resetAllViewPosition() {
		int childCount = getChildCount();
		int fistVisibleItem = getFirstVisiblePosition();
		for (int i=0; i<childCount; ++i) {
			View v = getChildAt(i);
			int orgPos = fistVisibleItem+i;
			Rect targetBounds = viewBounds.get(i);
			v.setTag(R.id.item_pos_org, orgPos);
			v.setTag(R.id.item_pos_cur, orgPos);
			v.offsetLeftAndRight(targetBounds.left - v.getLeft());
			v.offsetTopAndBottom(targetBounds.top - v.getTop());
			if (orgPos == dragPosOrg)
				v.setVisibility(View.VISIBLE);
		}
		
		dragPosCurrent = dragPosOrg;
	}
	
	@Override
	public void onDragOver(int x, int y) {
		System.out.println("onDragOver x="+x+" y="+y + " dragStartX="+dragStartX+" dragStartY="+dragStartY);
		
		if(dragOverListener != null){
			dragOverListener.onDragOver(x - dragStartX, y - dragStartY);
		}
		
		motionX = x;
		motionY = y;
		if (isLockDragMove())
			return;
		processDragMove(x, y);
		int currentScroll = 0;
		if (y + scrollSpace > getBottom()) {
			currentScroll = getScrollDistanceByPostitionOffset(getNumColumns());
		} else if (y - scrollSpace < 0) {
			currentScroll = getScrollDistanceByPostitionOffset(-getNumColumns());
		} else {
			currentScroll = 0;
		}
		
		
		if (currentScroll == 0) {
			removeCallbacks(smoothScrollRunnable);
			smoothScrollDistance = 0;
			scrollPosted = false;
		} else {
			if (scrollPosted) {
				if ((smoothScrollDistance > 0) ^ (currentScroll > 0)) {
					// scroll direction changed
					removeCallbacks(smoothScrollRunnable);
				} else {
					return;
				}
			}
			scrollPosted = true;
			smoothScrollDistance = currentScroll;
			postDelayed(smoothScrollRunnable, SCROLL_DELAY);
			Log.d("gridview", "poset scroll to:"+smoothScrollDistance);
		}
	}

	@Override
	public void onDragOut(int x, int y) {
	}
	
	private void processDragMove(int x, int y) {
		View v = getViewAtPosition(x, y);
		if (v == null)
			return;
		int curPosForView = (Integer) v.getTag(R.id.item_pos_cur);
		if (curPosForView == ignorePosition)
			return;
		if (curPosForView < dragPosCurrent) {
			//move all view  from curPosForView to dragPos down
			moveViews(curPosForView, dragPosCurrent, false);
		} else if (curPosForView > dragPosCurrent) {
			//move all view  from dragPos to curPosForView up
			moveViews(dragPosCurrent+1, curPosForView+1, true);
		}
		dragPosCurrent = curPosForView;
	}
	
	/**
	 * [from, to)
	 * @param fromPos
	 * @param toPos
	 * @param isUpOrDown
	 */
	private void moveViews(int fromPos, int toPos, boolean isUpOrDown) {
		int childCount = getChildCount();
		
		Object resultList = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			resultList = new LinkedList<Animator>();
		}
		int firstVisibleItem = getFirstVisiblePosition();
		for (int i=0; i<childCount; ++i) {
			View v = getChildAt(i);
			int curPos = (Integer) v.getTag(R.id.item_pos_cur);
			if (curPos >= fromPos && curPos < toPos) {
				int moveToPos = curPos + (isUpOrDown ? -1 : 1);
				int index = moveToPos-firstVisibleItem;
				v.setTag(R.id.item_pos_cur, moveToPos);
				Rect targetBounds;
				if (index >= 0 && index < childCount) {
					targetBounds = viewBounds.get(index);
				} else {
					targetBounds = sharedRect;
					if (moveToPos > curPos) {
						targetBounds.set(0, v.getBottom(), v.getWidth(), v.getBottom()+v.getHeight());
					} else {
						targetBounds.set(getRight()-v.getWidth(), v.getTop()-v.getHeight(), getRight(), v.getTop());
					}
				}
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					AnimatorSet set = createTranslationAnimations(v, targetBounds.left, targetBounds.right, targetBounds.top, targetBounds.bottom);
					((LinkedList<Animator>) resultList).add(set);
				} else {
					v.offsetLeftAndRight(targetBounds.left - v.getLeft());
					v.offsetTopAndBottom(targetBounds.top - v.getTop());
				}
			}
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			AnimatorSet resultSet = new AnimatorSet();
	        resultSet.playTogether((LinkedList<Animator>) resultList);
	        resultSet.setDuration(REORDER_ANIMATE_DURATION);
	        resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
	        resultSet.addListener(animatorListener);
	        resultSet.start();
	        Log.d("gridview", "moveViews-lockDragMove");
	        lockDragMoveForAnimate = true;
		}
		
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private AnimatorSet createTranslationAnimations(View view, int targetLeft, int targetRight, int targetTop, int targetBottom) {
        ObjectAnimator animL = ObjectAnimator.ofInt(view, "left", view.getLeft(), targetLeft);
        ObjectAnimator animR = ObjectAnimator.ofInt(view, "right", view.getRight(), targetRight);
        ObjectAnimator animT = ObjectAnimator.ofInt(view, "top", view.getTop(), targetTop);
        ObjectAnimator animB = ObjectAnimator.ofInt(view, "bottom", view.getBottom(), targetBottom);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animL, animR, animT, animB);
        return animSetXY;
    }

	@Override
	public void drawShadow(Canvas canvas) {
		dragViewDrawable.draw(canvas);
	}

	@Override
	public Object startDrag() {
		return null;
	}

	@Override
	public void onDragComplete(boolean isCanceled) {
		if (postReorder)
			return;
		endDrop();
	}
	
	private void endDrop() {
		dragStartX = 0;
		dragStartY = 0;
		Adapter adapter = getAdapter();
		if (adapter != null && adapter instanceof BaseAdapter) {
			((BaseAdapter) adapter).notifyDataSetChanged();
		}
		resetDragViewVisibility();
		inDragMode = false;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		lockDragMoveForScroll = scrollState != SCROLL_STATE_IDLE;
		if (isLockDragMove())
			return;
		
		int firstVisibleItemTop = 0;
		if (getChildCount() > 0) {
			firstVisibleItemTop = getChildAt(0).getTop();
		}
		
		int firstVisibleItem = getFirstVisiblePosition();
		if (firstVisibleItem != scrollFirstVisble || firstVisibleItemTop != scrollFirstTop) {
			scrollFirstVisble = firstVisibleItem;
			scrollFirstTop = firstVisibleItemTop;
			if (inDragMode) {
				initDragState();
				processDragMove(motionX, motionY);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (inDragMode) {
			initDragState();
		}
	}
	
	private void initDragState() {
		Log.d("gridview", "initDragState");
		int firstVisibleItem = getFirstVisiblePosition();
		int itemPos = firstVisibleItem;
		int childCount = getChildCount();
		if (viewBounds == null) {
			viewBounds = new ArrayList<Rect>(childCount);
		}
		resetDragViewVisibility();
		int viewBoundsSize = viewBounds.size();
			
		for (int i=0; i<childCount; ++i) {
			View v = getChildAt(i);
			int pos = itemPos + i ;
			if (pos == dragPosOrg) {
				dragView = new WeakReference<View>(v);
				v.setVisibility(View.INVISIBLE);
			} else if (ignorePosition != pos) {
				v.setVisibility(View.VISIBLE);
			}
			v.setTag(R.id.item_pos_org, pos);
			v.setTag(R.id.item_pos_cur, pos);
			Rect bounds;
			if (i < viewBoundsSize) {
				bounds = viewBounds.get(i);
			} else {
				bounds = new Rect();
				viewBounds.add(bounds);
			}
			bounds.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
		}
		
		viewBoundsSize = viewBounds.size();
		if (childCount > viewBoundsSize) {
			int removeCount = childCount-viewBoundsSize;
			while (removeCount-- > 0) {
				viewBounds.remove(viewBounds.size()-1);
			}
		}
	}

	private void resetDragViewVisibility() {
		if (dragView != null && dragView.get() != null) {
			final View v = dragView.get();
			if (v != null)
				v.setVisibility(View.VISIBLE);
		}
	}
	
	private View getViewAtPosition(int x, int y) {
		int childCount = getChildCount();
		for (int i=0; i<childCount; ++i) {
			View v = getChildAt(i);
			v.getHitRect(sharedRect);
			if (sharedRect.contains(x, y)) {
				return v;
			}
		}
		
		return null;
	}
	
	public static interface DragOverListener{
		void onDragOver(float disX,float disY);
	}

}
