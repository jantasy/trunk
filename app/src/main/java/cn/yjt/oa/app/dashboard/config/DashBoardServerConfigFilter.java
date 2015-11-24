package cn.yjt.oa.app.dashboard.config;

import java.util.Iterator;
import java.util.List;

import cn.yjt.oa.app.beans.DashBoardItem;

public class DashBoardServerConfigFilter implements DashBoardFilter{
	
	private List<DashBoardItem> serverConfig;
	
	public DashBoardServerConfigFilter(List<DashBoardItem> serverConfig) {
		this.serverConfig = serverConfig;
	}
	
	@Override
	public void filt(List<DashBoardItem> boardItems) {
		if(serverConfig == null){
			return;
		}
		Iterator<DashBoardItem> iterator = boardItems.iterator();
		while(iterator.hasNext()){
			DashBoardItem item = iterator.next();
			if(DashBoardHelper.isYjtDashBoardItem(item)&&isDeprecated(item)){
				iterator.remove();
			}
		}
		System.out.println("DashBoardServerConfigFilter"+boardItems);
	}
	
	private boolean isDeprecated(DashBoardItem item){
		for (DashBoardItem dashBoardItem : serverConfig) {
			if(dashBoardItem.getId() == item.getId() && dashBoardItem.getStatus() == DashBoardItem.STATUS_DEPRECATED){
				return true;
			}
		}
		return false;
	}

	
}
