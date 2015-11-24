package cn.yjt.oa.app.dashboard.config;

import java.util.List;

import cn.yjt.oa.app.beans.DashBoardItem;

public class DashBoardHideDefaultFilter implements DashBoardFilter {
	
	DashBoardFilter deprecatedFilter = new DashBoardDeprecatedFilter();
	DashBoardFilter permissionFilter = new DashBoardPermissionFilter();
	DashBoardFilter repeatingFilter  = new DashBoardRepeatingFilter();
	
	public DashBoardHideDefaultFilter() {
		
	}

	@Override
	public void filt(List<DashBoardItem> boardItems) {
		deprecatedFilter.filt(boardItems);
		permissionFilter.filt(boardItems);
		repeatingFilter.filt(boardItems);
		
	}

}
