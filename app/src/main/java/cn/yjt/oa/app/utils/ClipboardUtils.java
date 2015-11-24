package cn.yjt.oa.app.utils;


import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

import cn.yjt.oa.app.MainApplication;

/**
 * 剪切板工具类
 * @author 熊岳岳
 * @since 20150830
 *
 */
public class ClipboardUtils {
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void copyToClipboard(String content){
        //兼容低版本
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            ClipboardManager cmb = ManagerUtils.getClipboardManager();
            cmb.setText(content);
        }else{
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) MainApplication
                    .getApplication()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(content);
        }
        ToastUtils.shortToastShow("文字已复制到剪贴板");
	}
}
