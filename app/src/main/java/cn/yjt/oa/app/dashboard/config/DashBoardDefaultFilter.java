package cn.yjt.oa.app.dashboard.config;

import java.util.List;

import cn.yjt.oa.app.beans.DashBoardItem;

public class DashBoardDefaultFilter implements DashBoardFilter {
	
	DashBoardFilter deprecatedFilter = new DashBoardDeprecatedFilter();
	DashBoardFilter newItemFilter 	 = new DashBoardNewItemFilter();
	DashBoardFilter permissionFilter = new DashBoardPermissionFilter();
	DashBoardFilter hideFilter 		 = new DashBoardHideFilter();
	DashBoardFilter repeatingFilter  = new DashBoardRepeatingFilter();
	DashBoardFilter sdkVersionFilter = new DashBoardSdkVersionFilter();
	
	public DashBoardDefaultFilter() {
		
	}

	@Override
	public void filt(List<DashBoardItem> boardItems) {
		deprecatedFilter.filt(boardItems);
		newItemFilter.filt(boardItems);
		permissionFilter.filt(boardItems);
		hideFilter.filt(boardItems);
		repeatingFilter.filt(boardItems);
		sdkVersionFilter.filt(boardItems);
	}

}
