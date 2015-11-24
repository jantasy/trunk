package cn.yjt.oa.app.task;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.beans.UserSimpleInfo.RecipientSpan;

public class TaskEditText extends EditText {

    public TaskEditText(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public TaskEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public TaskEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        
        if (selStart == selEnd) {
            Editable buffer = getText();
            RecipientSpan[] infos = buffer.getSpans(selStart, selEnd, UserSimpleInfo.RecipientSpan.class);
            if (infos != null && infos.length > 0) {
                int spanStart = buffer.getSpanStart(infos[0]);
                int spanEnd = buffer.getSpanEnd(infos[0]);
                if (selStart - spanStart < spanEnd - selStart)
                    setSelection(spanStart);
                else
                    setSelection(spanEnd);
            }
        } else {
            Editable buffer = getText();
            RecipientSpan[] infos = buffer.getSpans(selStart, selEnd, UserSimpleInfo.RecipientSpan.class);
            int bestStart = selStart, bestEnd = selEnd;
            if (infos != null && infos.length > 0) {
                for (RecipientSpan info : infos) {
                    int spanStart = buffer.getSpanStart(info);
                    int spanEnd = buffer.getSpanEnd(info);
                    if (bestStart > spanStart)
                        bestStart = spanStart;
                    if (bestEnd < spanEnd) {
                        bestEnd = spanEnd;
                    }
                }
                setSelection(bestStart, bestEnd);
            }
        }
    }
    
    

}
