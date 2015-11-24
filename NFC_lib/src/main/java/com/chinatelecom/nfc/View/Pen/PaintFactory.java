package com.chinatelecom.nfc.View.Pen;

import android.graphics.Color;
import android.graphics.Paint;

public class PaintFactory {
    private static boolean EnableAntiAlias = true;
    private static Paint result;

    private static Paint getDefaultPaint() {

        if (result == null) {
            result = new Paint();
        } else {
            result.reset();
        }
        return result;
    }

    public static Paint create(int color, Paint.Style style, float strokeWidth,
            boolean antiAlias) {
        result = getDefaultPaint();
        result.setColor(color);
        result.setStyle(style);
        result.setStrokeWidth(strokeWidth);
        result.setAntiAlias(antiAlias);
        result.setTextSize(24);
        return result;
    }

    public static Paint create(int color, Paint.Style style, float strokeWidth) {
        return create(color, style, strokeWidth, EnableAntiAlias);
    }

    public static Paint create(int color, Paint.Style style) {
        return create(color, style, 1);
    }

    public static Paint create(int color) {
        return create(color, Paint.Style.FILL, 1);
    }

    public static Paint create() {
        return create(Color.BLACK);
    }

    
}
