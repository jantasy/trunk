package io.luobo.launcher.floatwidget;

import android.content.Context;
import android.content.SharedPreferences;

public class FloatWidgetPreference {

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences("float_widget", Context.MODE_PRIVATE);
	}
	
	public static int getVerticalMagneticOffset(Context context) {
		return getPreferences(context).getInt("vertical_magnetic_offset", (int) (100*context.getResources().getDisplayMetrics().density));
	}
}
