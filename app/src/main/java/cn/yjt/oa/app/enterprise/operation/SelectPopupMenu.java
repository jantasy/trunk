package cn.yjt.oa.app.enterprise.operation;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;

public class SelectPopupMenu extends PopupWindow implements OnClickListener,
		OnTouchListener {
	private TextView titleView;
	private Button btnLocal, btnManual, btnCancel;
	private OnClickListener mListener;
	private View mMenuView;
	private Activity activity;

	public SelectPopupMenu(Context context) {
		super(context);
		activity = (Activity) context;
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		
		mMenuView = inflater.inflate(R.layout.select_popup_menu, null);
		titleView = (TextView) mMenuView.findViewById(R.id.select_pop_title);
		btnLocal = (Button) mMenuView.findViewById(R.id.btn_select_local);
		btnLocal.setOnClickListener(this);
		btnManual = (Button) mMenuView.findViewById(R.id.btn_select_manual);
		btnManual.setOnClickListener(this);
		btnCancel = (Button) mMenuView.findViewById(R.id.btn_select_cancel);
		btnCancel.setOnClickListener(this);
		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
		mMenuView.setOnTouchListener(this);
	}
	
	public void setTitle(CharSequence title){
		titleView.setText(title);
	}

	public void setOnItemClickListener(OnClickListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_select_cancel:
			dismiss();
			break;
		case R.id.btn_select_manual:
			bindListener(v);
            /*记录操作 0208*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_WRITE_CONTACTS_TO_JION);
			break;
		case R.id.btn_select_local:
			bindListener(v);
            /*记录操作 0209*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_INVITE_CONTACTS_TO_JION);
			break;
		default:
			break;
		}
	}

	private void bindListener(View v) {
		dismiss();
		mListener.onClick(v);
	}

	public void show() {
		showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int height = mMenuView.findViewById(R.id.select_popup_menu).getTop();
		int y = (int) event.getY();
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (y < height) {
				//dismiss();
			}
		}
		return true;
	}
}
