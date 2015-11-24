package cn.yjt.oa.app.picturepicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

public interface PicturePicker {
	
	public void pickPicture(Activity context);
	
	public void pickPicture(Fragment fragment);
	
	/**
	 * Invoke this method in onActivityResult method.
	 * @param data
	 * @return
	 */
	public Uri getPicture(int requestCode,int resultCode,Intent data);
	
	/**
	 * Invoke this method in onActivityResult method.
	 * @param requestCode
	 * @return
	 */
	public boolean isPickerResult(int requestCode);
	
}
