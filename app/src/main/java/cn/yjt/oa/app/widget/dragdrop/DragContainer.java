package cn.yjt.oa.app.widget.dragdrop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DragContainer extends FrameLayout implements ViewGroup.OnHierarchyChangeListener, View.OnLongClickListener {

	private DropTarget dropTarget;
	private DragSource dragSource;
	private Object dragItem;
	private boolean inDragMode;
	private Rect sharedRect = new Rect();
	
	private boolean autoStartDrag;
	
	private float currentX;
	private float currentY;
	private float dragStartX;
	private float dragStartY;
	
	public DragContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DragContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DragContainer(Context context) {
		super(context);
		init();
	}

	private void init() {
		setAutoStartDrag(true);
	}
	
	public void setAutoStartDrag(boolean autoStartDrag) {
		if (this.autoStartDrag != autoStartDrag) {
			this.autoStartDrag = autoStartDrag;
			traversalChildren(this, this.autoStartDrag);
		}
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (dragSource != null) {
			int saveCount = canvas.save();
			View dragView = (View)dragSource;
			final Rect rc = sharedRect;
			rc.set(0, 0, dragView.getWidth(), dragView.getHeight());
			offsetDescendantRectToMyCoords(dragView, rc);
			canvas.translate(rc.left+currentX-dragStartX, rc.top+currentY-dragStartY);
			dragSource.drawShadow(canvas);
			canvas.restoreToCount(saveCount);
		}
	}

	@Override
	public void onChildViewAdded(View parent, View child) {
		traversalChildren(child, true);
	}
	
	private void traversalChildren(View parent, boolean isAddOrRemove) {
		if (parent instanceof ViewGroup) {
			ViewGroup group = (ViewGroup)parent;
			if (isAddOrRemove)
				group.setOnHierarchyChangeListener(this);
			else
				group.setOnHierarchyChangeListener(null);
			int childCount = group.getChildCount();
			for (int i=0; i<childCount; ++i) {
				traversalChildren(group.getChildAt(i), isAddOrRemove);
			}
		}
		
		if (parent instanceof DragSource) {
			if (isAddOrRemove)
				parent.setOnLongClickListener(this);
			else
				parent.setOnLongClickListener(null);
		}
	}

	@Override
	public void onChildViewRemoved(View parent, View child) {
		traversalChildren(child, false);
	}

	@Override
	public boolean onLongClick(View v) {
		if (v instanceof DragSource && v.getVisibility() == View.VISIBLE) {
			DragSource dragSource = (DragSource)v;
			startDrag(dragSource, dragSource.startDrag());
			return true;
		}
		return false;
	}
	
	public void startDrag(DragSource dragSource, Object dragItem) {
		this.dragSource = dragSource;
		this.dragItem = dragItem;
		dragStartX = currentX;
		dragStartY = currentY;
		inDragMode = true;
		postInvalidate();
		getParent().requestDisallowInterceptTouchEvent(true);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!processTouchEvent(ev))
			return super.onInterceptTouchEvent(ev);
		return inDragMode;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		currentX = ev.getX();
		currentY = ev.getY();
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return processTouchEvent(event);
	}
	
	private boolean processTouchEvent(MotionEvent event) {
		if (!inDragMode)
			return false;
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_CANCEL: {
			stopDrag(true);
			postInvalidate();
		}
		case MotionEvent.ACTION_UP: {
			boolean isCanceled = true;
			DropTarget currentDropTarget = findDropTarget(this, (int)event.getX(), (int)event.getY());
			if (currentDropTarget != null) {
				if (currentDropTarget.accept(dragItem, dragSource)) {
					final Rect rc = sharedRect;
					rc.set((int)event.getX(), (int)event.getY(), 0, 0);
					offsetRectIntoDescendantCoords((View)currentDropTarget, rc);
					currentDropTarget.drop(dragItem, rc.left, rc.top);
					isCanceled = false;
				}
			}
			stopDrag(isCanceled);
			postInvalidate();
		}
		break;
		case MotionEvent.ACTION_MOVE: {
			processMoveEvent((int)event.getX(), (int)event.getY());
		}
		break;
		}
		
		return true;
	}
	
	private void processMoveEvent(int x, int y) {
		DropTarget currentDropTarget = findDropTarget(this, x, y);
		if (currentDropTarget != null) {
			if (!currentDropTarget.accept(dragItem, dragSource)) {
				currentDropTarget = null;
			}
		}
		
		final Rect rc = sharedRect;
		rc.set(x, y, 0, 0);
		if (currentDropTarget != dropTarget) {
			if (dropTarget != null) {
				offsetRectIntoDescendantCoords((View)dropTarget, rc);
				dropTarget.onDragOut(rc.left, rc.top);
			}
			if (currentDropTarget != null) {
				offsetRectIntoDescendantCoords((View)currentDropTarget, rc);
				currentDropTarget.onDragIn(rc.left, rc.top);
			}
			dropTarget = currentDropTarget;
		} else if (dropTarget != null) {
			offsetRectIntoDescendantCoords((View)dropTarget, rc);
			dropTarget.onDragOver(rc.left, rc.top);
		}
		postInvalidate();
	}

	private void stopDrag(boolean isCanceled) {
		if (dragSource != null) {
			dragSource.onDragComplete(isCanceled);
			dragSource = null;
		}
		dropTarget = null;
		dragItem = null;
		inDragMode = false;
		getParent().requestDisallowInterceptTouchEvent(false);
	}
	
	private DropTarget findDropTarget(ViewGroup parent, int eventX, int eventY) {
		int childCount = parent.getChildCount();
		final Rect rc = sharedRect;
		DropTarget dropTarget = null;
		for (int i=0; i<childCount; ++i) {
			View child = parent.getChildAt(i);
			if (child.getVisibility() == View.VISIBLE) {
				child.getHitRect(rc);
				if (parent != this)
					offsetDescendantRectToMyCoords(parent, rc);
//				rc.offset(offsetX, offsetY);
				if (rc.contains(eventX, eventY)) {
					if (child instanceof ViewGroup) {
						dropTarget = findDropTarget((ViewGroup)child, eventX, eventY);
					}
					
					if (dropTarget == null && child instanceof DropTarget)
						dropTarget = (DropTarget) child;
				}
			}
			
			if (dropTarget != null)
				return dropTarget;
		}
		
		
		return null;
	}
}
