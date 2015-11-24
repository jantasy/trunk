package cn.yjt.oa.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cn.yjt.oa.app.R;


/**
 * Created by xiong on 2015/10/14.
 */
public class CopyPopupWindow extends PopupWindow implements View.OnClickListener {

    private View mConvertView;
    private LinearLayout mLlCopy;

    private OnCopyClickListener mCopyListener;

    public CopyPopupWindow() {
        super();
    }

    public CopyPopupWindow(Context context) {
        mConvertView = View.inflate(context, R.layout.popup_copy_menu, null);
        setContentView(mConvertView);
        initView();
        fillDate();
        setListener();
    }

    private void initView() {
        mLlCopy = (LinearLayout) mConvertView.findViewById(R.id.ll_copy);
    }

    private void fillDate() {
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setListener() {
        mConvertView.findViewById(R.id.ll_copy).setOnClickListener(this);
    }


    public void setOnCopyListener(OnCopyClickListener mCopyListener) {
        this.mCopyListener = mCopyListener;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.ll_copy:
                mCopyListener.onCopyClick();
                break;

            default:
                break;
        }
    }

    public interface OnCopyClickListener{
        public void onCopyClick();
    }
}
