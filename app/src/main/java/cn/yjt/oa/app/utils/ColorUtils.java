package cn.yjt.oa.app.utils;

import cn.yjt.oa.app.MainApplication;
import android.content.res.ColorStateList;
import android.content.res.Resources;

/**
 * 对颜色一些操作的工具类 
 * @author 熊岳岳
 * @since 20150724
 */
public class ColorUtils {
	
	public static ColorStateList getResourcesColor(int res){
		Resources resource = (Resources) MainApplication.getAppContext().getResources();  
		ColorStateList csl = (ColorStateList) resource.getColorStateList(res); 
		return csl;
	}
}
