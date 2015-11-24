package com.chinatelecom.nfc.View.Pen;

import android.graphics.Color;
import android.graphics.Paint;

public class RedPen extends PaintFactory {

	public static Paint getPaint() {
		// return create(Color.RED);
		return create(Color.rgb(230, 70, 70));
	}

}
