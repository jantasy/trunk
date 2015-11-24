package cn.yjt.oa.app.lifecircle.view;

import cn.yjt.oa.app.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SpinerDialog extends Dialog {
    public TypeAdatper adapter;
    private Context mContext;
    RoundedRectListView list;
    public int selectItem;
    View view;

    public class TypeAdatper extends BaseAdapter {
        String[] array;
        Context contex;

        public TypeAdatper(Context context, String[] strArr) {
            contex = context;
            array = strArr;
            Log.d("SpinerDialog", "array.length: " + array.length);
        }

        public int getCount() {
            return array.length;
        }

        public Object getItem(int i) {
            return array[i];
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.spinner_item2, null);
            LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.item_ll);
            if (i == 0) {
                linearLayout.setBackgroundResource(R.drawable.order_selector_top);
            } else if (i == adapter.array.length - 1) {
                linearLayout.setBackgroundResource(R.drawable.order_selector_bottom);
            } else {
                linearLayout.setBackgroundResource(R.drawable.order_selector);
            }
            ((TextView) inflate.findViewById(R.id.spinner_text)).setText(array[i]);
            return inflate;
        }
    }

    public SpinerDialog(Context context, int i) {
        super(context, R.style.dialogNoTitle);        
        selectItem = i;
        mContext = context;
        setCancelable(true);
        initView();
    }

    protected SpinerDialog(Context context, boolean z, OnCancelListener onCancelListener) {
        super(context, z, onCancelListener);
        selectItem = 0;
        setCancelable(true);
        mContext = context;
        initView();
    }

    public void initView() {
        view = LayoutInflater.from(mContext).inflate(R.layout.show_dialog, null);
        list = (RoundedRectListView) view.findViewById(R.id.spinner_list);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        list.setOnItemClickListener(onItemClickListener);
    }

    public void setView(String[] strArr) {
        adapter = new TypeAdatper(mContext, strArr);
        list.setAdapter(adapter);
        setContentView(view);
    }

    public void show() {
        super.show();
        Display defaultDisplay = getWindow().getWindowManager().getDefaultDisplay();
        LayoutParams attributes = getWindow().getAttributes();
        attributes.height = (int) (0.7d * ((double) defaultDisplay.getHeight()));
        attributes.width = (int) (0.9d * ((double) defaultDisplay.getWidth()));
        getWindow().setAttributes(attributes);
        setCanceledOnTouchOutside(true);
    }
}
