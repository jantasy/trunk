package cn.yjt.oa.app.http;

import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Toast;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.utils.ErrorUtils;

/**
 * Created by chenshang on 15-4-16.
 */
public abstract class ProgressDialogResponseListener<T> extends ResponseListener<T> {

    private Context context;
    private CharSequence message;
    private ProgressDialog progressDialog;

    public ProgressDialogResponseListener(Context context) {
        this.context = context;
    }

    @Override
    public void onFinish() {
        if(progressDialog != null){
            progressDialog.cancel();
            progressDialog = null;
        }
    }

    public ProgressDialogResponseListener(Context context, CharSequence message) {
        this(context);
        this.message = message;
    }

    public void onRequest(Cancelable cancelable) {
        if (TextUtils.isEmpty(message)) {
            showProgressDialog(cancelable, "正在请求...");
        } else {
            showProgressDialog(cancelable, message);
        }
    }
    
    @Override
    public void onErrorResponse(InvocationError error) {
    	super.onErrorResponse(error);
    	Toast.makeText(context, ErrorUtils.getErrorDescription(error.getErrorType()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorResponse(Response<T> response) {
    	super.onErrorResponse(response);
    	Toast.makeText(context, response.getDescription(), Toast.LENGTH_SHORT).show();
    }
    
    public void showProgressDialog(final Cancelable cancelable, CharSequence message) {
        progressDialog = ProgressDialog.show(context, null, message);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelable.cancel();
            }
        });
    }

}

