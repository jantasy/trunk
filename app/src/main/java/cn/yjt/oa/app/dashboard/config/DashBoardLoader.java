package cn.yjt.oa.app.dashboard.config;

import java.util.ArrayList;
import java.util.List;

import cn.yjt.oa.app.beans.DashBoardItem;

public class DashBoardLoader {
	
	private List<DashBoardFilter> dashBoardFilters = new ArrayList<DashBoardFilter>();
	
	public DashBoardLoader(DashBoardFilter filter, DashBoardFilter... filters) {
		dashBoardFilters.add(filter);
		for (int i = 0; i < filters.length; i++) {
			dashBoardFilters.add(filters[i]);
		}
	}

	public List<DashBoardItem> load(){
		List<DashBoardItem> userDashBoard = DashBoardHelper.getUserDashBoard();
		if(userDashBoard == null || userDashBoard.isEmpty()){
			userDashBoard = DashBoardHelper.getDefaultDashBoard();
		}
		
		for (DashBoardFilter dashBoardFilter : dashBoardFilters) {
			dashBoardFilter.filt(userDashBoard);
		}
		return userDashBoard;
	}
	
	public void addFilter(DashBoardFilter filter){
		dashBoardFilters.add(filter);
	}
	public void removeFilter(DashBoardFilter filter){
		dashBoardFilters.remove(filter);
	}
	
	
	
}
