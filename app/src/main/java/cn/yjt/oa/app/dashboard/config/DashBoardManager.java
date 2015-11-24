package cn.yjt.oa.app.dashboard.config;

import java.util.List;

import android.content.Context;
import cn.yjt.oa.app.beans.DashBoardItem;

public class DashBoardManager {

	public static boolean isAllowed(Context context){
		List<DashBoardItem> dashBoardItems = new DashBoardLoader(new DashBoardDefaultFilter(),new DashBoardServerConfigFilter(DashBoardHelper.getServerDashBoard())).load();
		for (DashBoardItem dashBoardItem : dashBoardItems) {
			if(context.getClass().getName().equals(dashBoardItem.getClassName())){
				return true;
			}
		}
		return false;
		
	}
}
