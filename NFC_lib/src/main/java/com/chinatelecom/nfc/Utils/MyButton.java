/*
 * $Id$
 * Copyright (C) 2012 Sharp Corp. Communication Systems Group.
 */
package com.chinatelecom.nfc.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;
/**
 * TimelineButton
 * @author iss_lyc
 *
 */
public class MyButton extends Button {

    private static final int TOP_SPACE = 0;
    private static final int BOTTOM_SPACE = 18;
    private static float FONT_SIZE = 18;
    private Paint mPaint;
    private String text = getText().toString();
    private int width;
    private int height;
    
    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        FONT_SIZE = getTextSize();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(FONT_SIZE);
        mPaint.setColor(getTextColors().getDefaultColor());
//        TimelineCommon.setFontToMRSWShinGoMedium(context, mPaint);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextColor(int color){
        mPaint.setColor(color);
    }
    
    @Override
    public void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    public void onDraw(Canvas canvas) {
        float fontSpacing = mPaint.getFontSpacing();
        float x = (width - fontSpacing+6)/2;
        float ww = mPaint.measureText(text, 0, text.length());
        float y = (height - ww)/ 2 ;
        if (y < 0) {
            y = 1.0f;
        }
        String[] s = text.split("");
        boolean newLine = false;
        for (int i = 1; i <= text.length(); i++) {
            newLine = false;
            MyButtonCharSetting setting = MyButtonCharSetting.getSetting(s[i]);
            if (setting == null) {
                canvas.rotate(90, x, y);
                canvas.drawText(s[i], x, y, mPaint);
                canvas.rotate(-90, x, y);
            } else {
                canvas.save();
                canvas.rotate(setting.angle, x, y);
                canvas.drawText(s[i], x , y
                        , mPaint);
                canvas.restore();
            }
            if (y + fontSpacing > height - BOTTOM_SPACE) {
                newLine = true;
            } else {
                newLine = false;
            }
            if (newLine) {
                x -= fontSpacing;
                y = TOP_SPACE + fontSpacing;
            } else {
                if(setting == null){
                    y += fontSpacing;
                }else{
                	y = y + mPaint.measureText(setting.getCharcter(), 0, setting.getCharcter().length());
                }
            }
        }
    }
}
