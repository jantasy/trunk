package cn.yjt.oa.app.dashboard.config;

import java.util.Iterator;
import java.util.List;

import cn.yjt.oa.app.beans.DashBoardItem;

public class DashBoardHideFilter implements DashBoardFilter {

	@Override
	public void filt(List<DashBoardItem> boardItems) {
		List<DashBoardItem> userHideDashBoard = DashBoardHelper.getUserHideDashBoard();
		if(userHideDashBoard == null){
			return;
		}
		Iterator<DashBoardItem> iterator = boardItems.iterator();
		while(iterator.hasNext()){
			DashBoardItem item = iterator.next();
			if(userHideDashBoard.contains(item)){
				iterator.remove();
			}
		}
	}

}
