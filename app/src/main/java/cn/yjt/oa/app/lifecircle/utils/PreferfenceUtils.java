package cn.yjt.oa.app.lifecircle.utils;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class PreferfenceUtils {
    public static String getADPreferences(Context context) {
        return context.getSharedPreferences("ad", Context.MODE_PRIVATE).getString("url", "ss");
    }

    public static String getCityPreferences(Context context) {
        return context.getSharedPreferences("location", Context.MODE_PRIVATE).getString("city", "北京市");
    }

    public static String getPRODUCT(Context context) {
        return context.getSharedPreferences("PRODUCT", Context.MODE_PRIVATE).getString("PRODUCT", "13812345678");
    }

    public static long getSaveTags(Context context) {
        return context.getSharedPreferences("guide", Context.MODE_PRIVATE).getLong("savetimes", 0);
    }

    public static void setADPreferences(Context context, String str) {
        Editor edit = context.getSharedPreferences("ad", Context.MODE_PRIVATE).edit();
        edit.putString("url", str);
        edit.commit();
    }

    public static void setCityPreferences(Context context, String str) {
        Editor edit = context.getSharedPreferences("location", Context.MODE_PRIVATE).edit();
        edit.putString("city", str);
        edit.commit();
    }

    public static void setPRODUCT(Context context, String str) {
        Editor edit = context.getSharedPreferences("PRODUCT", Context.MODE_PRIVATE).edit();
        edit.putString("PRODUCT", str);
        edit.commit();
    }

    public static void setSaveTags(Context context, long j) {
        Editor edit = context.getSharedPreferences("guide", Context.MODE_PRIVATE).edit();
        edit.putLong("savetimes", j);
        edit.commit();
    }
}
