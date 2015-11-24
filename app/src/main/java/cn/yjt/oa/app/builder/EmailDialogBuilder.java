package cn.yjt.oa.app.builder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.platform.comapi.map.D;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.utils.SharedUtils;

/**
 * 输入邮箱的弹窗
 *
 * @author 熊岳岳
 * @since 20150831
 */
public class EmailDialogBuilder {

    private static EmailDialogBuilder mBuilder;
    /** 上下文对象，弹窗的上下文只能用activity */
    private Activity mActivity;
    /** 对话框对象 */
    private AlertDialog mDialog;

    /** 对话框按钮点击监听 */
    private OnButtonClickListener mListener;

    /** 对话框按钮点击监听 */
    public interface OnButtonClickListener {
        /**点击对话框确定按钮*/
        public void positiveButtonClick(String text);
        /**点击对话框取消按钮*/
        public void negativeButtonClick();
    }

    /*----------------单例的方式创建CustomDialogBuilder对象----------------------*/
    private EmailDialogBuilder() {

    }

    public static EmailDialogBuilder getInstance(Activity activity) {
        if (mBuilder == null) {
            synchronized (EmailDialogBuilder.class) {
                mBuilder = new EmailDialogBuilder();
            }
        }
        mBuilder.mActivity = activity;
        return mBuilder;
    }

    /** 设置监听 */
    public void setOnButtonClickListener(OnButtonClickListener mListener) {
        this.mListener = mListener;
    }

    /** 弹出一个输入邮箱的对话框 */
    public AlertDialog buildInputEmailDialog() {
        View view = View.inflate(mActivity, R.layout.oneedittextdialog_item, null);
        final EditText etInputEmail = (EditText) view.findViewById(R.id.et_input_email);
        String attendanceEmail = SharedUtils.getAttendanceEmail(mActivity);
        //从sp中读出上一次输入的邮箱，填入其中
        if (TextUtils.isEmpty(attendanceEmail)) {
            etInputEmail.setText(AccountManager.getCurrent(mActivity).getEmail());
        } else {
            etInputEmail.setText(attendanceEmail);
        }
        //创建对话框
        mDialog = new AlertDialog.Builder(mActivity)
                .setView(view)
                .setTitle("请输入邮箱")
                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            SharedUtils.setAttendanceEmail(mActivity, etInputEmail.getText().toString());
                            mListener.positiveButtonClick(etInputEmail.getText().toString());
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.negativeButtonClick();
                        }
                    }
                })
                .show();
        return mDialog;
    }


}
