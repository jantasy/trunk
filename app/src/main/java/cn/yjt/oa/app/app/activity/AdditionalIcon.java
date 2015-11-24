package cn.yjt.oa.app.app.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;

public interface AdditionalIcon extends Parcelable {

	Drawable getIcon(Context context);
	String getTitle(Context context);
	String getDescription();
}
