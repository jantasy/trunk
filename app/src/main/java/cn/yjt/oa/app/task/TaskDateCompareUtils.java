package cn.yjt.oa.app.task;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.http.BusinessConstants;

public class TaskDateCompareUtils {
    public static void setComparedDateForView(Context context, Date date, TextView textView) {
        Resources resouces = context.getResources();
        
        Calendar targetTime = Calendar.getInstance();
        targetTime.setTimeInMillis(date.getTime());
        
        Calendar now = Calendar.getInstance();
        
        String timeString = getSimpleTimeString(date);
        String result = null;
        if (now.get(Calendar.DATE) == targetTime.get(Calendar.DATE)) {
            result = timeString;
        } else if (now.get(Calendar.DATE) == targetTime.get(Calendar.DATE) + 1) {
            result = resouces.getString(R.string.task_date_yesterday);
        } else if (now.get(Calendar.DATE) == targetTime.get(Calendar.DATE) + 2) {
            result = resouces.getString(R.string.task_date_before_yesterday);
        } else {
            result = getSimpleDateString(date);
        }
        textView.setText(result);
    }
    
    public static String getSimpleDateString(Date date) {
        if (date == null)
            return null;
        
        String simpleString = null;
        simpleString = BusinessConstants.formatSimpleDate(date);
        simpleString = removeRedundantChar(simpleString);
        return simpleString;
    }
    
    public static String getSimpleTimeString(Date date) {
        if (date == null)
            return null;
        
        String simpleString = null;
        simpleString = BusinessConstants.getTime(date);
        simpleString = removeRedundantChar(simpleString);
        return simpleString;
    }
    
    public static String removeRedundantChar(String date) {
        String simpleString = date;
        if (date != null && date.length() >= 1 &&
                "0".equals(date.subSequence(0, 1))) {
            simpleString = date.substring(1);
        }
        return simpleString;
    }
}
