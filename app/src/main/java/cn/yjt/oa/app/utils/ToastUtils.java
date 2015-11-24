package cn.yjt.oa.app.utils;

import android.widget.Toast;

import cn.yjt.oa.app.MainApplication;

/**
 * Created by xiong on 2015/10/13.
 */
public class ToastUtils {
    public static void shortToastShow(String message){
        Toast.makeText(MainApplication.getApplication(),message,Toast.LENGTH_SHORT).show();
    }

    public static void longToastShow(String message){
        Toast.makeText(MainApplication.getApplication(),message,Toast.LENGTH_LONG).show();
    }
}
