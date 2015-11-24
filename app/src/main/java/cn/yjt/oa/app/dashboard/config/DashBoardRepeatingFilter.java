package cn.yjt.oa.app.dashboard.config;

import java.util.ArrayList;
import java.util.List;

import cn.yjt.oa.app.beans.DashBoardItem;

public class DashBoardRepeatingFilter implements DashBoardFilter {

	@Override
	public void filt(List<DashBoardItem> boardItems) {
		List<DashBoardItem> destination = new ArrayList<DashBoardItem>();
		for (DashBoardItem dashBoardItem : boardItems) {
			if(!destination.contains(dashBoardItem)){
				destination.add(dashBoardItem);
			}
		}
		boardItems.clear();
		boardItems.addAll(destination);
		
	}

}
