package cn.yjt.oa.app.dashboard.config;

import java.util.List;

import cn.yjt.oa.app.beans.DashBoardItem;
/**
 * 新item若为0，默认显示，并不受后台控制
 * @author chenshang
 *
 */
public class DashBoardNewItemFilter implements DashBoardFilter {

	@Override
	public void filt(List<DashBoardItem> boardItems) {
		List<DashBoardItem> defaultDashBoard = DashBoardHelper.getDefaultDashBoard();
		
		for (DashBoardItem dashBoardItem : defaultDashBoard) {
			if(!isContained(boardItems, dashBoardItem)){
				boardItems.add(dashBoardItem);
			}
		}
	}
	
	private boolean isContained(List<DashBoardItem> boardItems,DashBoardItem item){
		for (DashBoardItem dashBoardItem : boardItems) {
			if(item.getId() != 0 && item.getId() == dashBoardItem.getId()){
				return true;
			}
		}
		return false;
	}

}
