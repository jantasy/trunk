package cn.yjt.oa.app.nfctools;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cn.yjt.oa.app.nfctools.NfcTagOperationView.ExtraView;

public class NfcTagOperationViewGroup extends LinearLayout {

	public static interface onCheckedChangeListener {
		void onCheckedChanged(NfcTagOperationViewGroup group, int operationId);
	}
	
	/**
	 * Position extra view in the bottom of the NfcTagOperationViewGroup.
	 */
	public static final int EXTRA_VIEW_GRAVITY_BOTTOM = 0;
	
	/**
	 * Position extra view bellow the NfcTagOperationView witch it belongs to.
	 */
	public static final int EXTRA_VIEW_GRAVITY_BELLOW = 1;
	

	private int NONE_CHECKED = 0xFFFF;
	
	private int checkedOperationId = NONE_CHECKED;
	private boolean protectFromCheckedChange;
	private onCheckedChangeListener onCheckedChangeListener;
	private PassThroughHierarchyChangeListener passThroughListener;
	private NfcTagOperationView.OnCheckedChangeListener childOnCheckedChangeListener;
	private List<ExtraView> extraViews = new ArrayList<NfcTagOperationView.ExtraView>();
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public NfcTagOperationViewGroup(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public NfcTagOperationViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public NfcTagOperationViewGroup(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		childOnCheckedChangeListener = new CheckedStateTracker();
		passThroughListener = new PassThroughHierarchyChangeListener();
		super.setOnHierarchyChangeListener(passThroughListener);
		setOrientation(LinearLayout.VERTICAL);
	}

	public void setOnCheckedChangeListener(
			onCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if (child instanceof NfcTagOperationView) {
			final NfcTagOperationView button = (NfcTagOperationView) child;
			if (button.isChecked()) {
				protectFromCheckedChange = true;
				if (checkedOperationId != NONE_CHECKED) {
					setCheckedStateForView(checkedOperationId, false);
				}
				protectFromCheckedChange = false;
				setCheckedId(button.getOperationId());
			}
			ExtraView extraView = ((NfcTagOperationView) child).getExtraView();
			if(extraView != null){
				if(extraView.getExtraViewGravity() == EXTRA_VIEW_GRAVITY_BELLOW){
					//TODO Position extra view.
				}else{
					extraViews.add(extraView);
				}
			}
		}

		super.addView(child, index, params);
	}

	private void setCheckedStateForView(int viewId, boolean checked) {
		View checkedView = findViewByOperationId(viewId).getView();
		if (checkedView != null && checkedView instanceof NfcTagOperationView) {
			((NfcTagOperationView) checkedView).setChecked(checked);
		}
	}

	private void setCheckedId(int operationId) {
		checkedOperationId = operationId;
		if (onCheckedChangeListener != null) {
			onCheckedChangeListener.onCheckedChanged(this, checkedOperationId);
		}
	}

	public NfcTagOperationView findViewByOperationId(int operationId) {
		System.out.println("findViewByOperationId:"+Integer.valueOf(operationId));
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child instanceof NfcTagOperationView) {
				NfcTagOperationView view = (NfcTagOperationView) child;
				if (view.getOperationId() == operationId) {
					System.out.println("findViewByOperationId:successful:"+view);
					return view;
				}
			}
		}
		return null;
	}

	public void check(int id) {
		// don't even bother
		if (id != NONE_CHECKED && (id == checkedOperationId)) {
			return;
		}

		if (checkedOperationId != NONE_CHECKED) {
			setCheckedStateForView(checkedOperationId, false);
		}

		if (id != NONE_CHECKED) {
			setCheckedStateForView(id, true);
		}

		setCheckedId(id);
	}

	public int getCheckedOperationId() {
		return checkedOperationId;
	}

	public void clearCheck() {
		check(NONE_CHECKED);
	}
	
	public void initFinish(){
		for (ExtraView extraView : extraViews) {
			if(extraView.getExtraViewGravity() == EXTRA_VIEW_GRAVITY_BOTTOM){
				addView(extraView.getView(), getLayoutParams());
			}
		}
	}

	private class CheckedStateTracker implements
			NfcTagOperationView.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(NfcTagOperationView operationView,
				boolean isChecked) {
			if (protectFromCheckedChange) {
				return;
			}

			protectFromCheckedChange = true;
			if (checkedOperationId != NONE_CHECKED) {
				setCheckedStateForView(checkedOperationId, false);
			}
			protectFromCheckedChange = false;

			int id = operationView.getOperationId();
			setCheckedId(id);
		}
	}

	private class PassThroughHierarchyChangeListener implements
			ViewGroup.OnHierarchyChangeListener {
		private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

		/**
		 * {@inheritDoc}
		 */
		public void onChildViewAdded(View parent, View child) {
			if (parent == NfcTagOperationViewGroup.this
					&& child instanceof NfcTagOperationView) {
				((NfcTagOperationView) child)
						.setOnCheckedChangedListener(childOnCheckedChangeListener);
			}

			if (mOnHierarchyChangeListener != null) {
				mOnHierarchyChangeListener.onChildViewAdded(parent, child);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void onChildViewRemoved(View parent, View child) {
			if (parent == NfcTagOperationViewGroup.this
					&& child instanceof NfcTagOperationView) {
				((NfcTagOperationView) child).setOnCheckedChangedListener(null);
			}

			if (mOnHierarchyChangeListener != null) {
				mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
			}
		}
	}
}
