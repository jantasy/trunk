package cn.yjt.oa.app.dashboard.config;

import java.util.Iterator;
import java.util.List;

import cn.yjt.oa.app.beans.DashBoardItem;

public class DashBoardDeprecatedFilter implements DashBoardFilter {

	private List<DashBoardItem> defaultDashBoard;

	public DashBoardDeprecatedFilter() {
		defaultDashBoard = DashBoardHelper.getDefaultDashBoard();
	}

	@Override
	public void filt(List<DashBoardItem> boardItems) {
		if(boardItems!=null){
			Iterator<DashBoardItem> iterator = boardItems.iterator();
			if(iterator!=null){
				while (iterator.hasNext()) {
					DashBoardItem next = iterator.next();
					if (DashBoardHelper.isYjtDashBoardItem(next)) {
						if (!defaultDashBoard.contains(next)) {
							iterator.remove();
						} else {
							checkIconAndTitle(next);
						}
					}
				}
			}
		}
	}
	
	private void checkIconAndTitle(DashBoardItem item){
		DashBoardItem defaultDashBoardItem = defaultDashBoard.get(defaultDashBoard.indexOf(item));
		item.setIconResUri(defaultDashBoardItem.getIconResUri());
		item.setTitle(defaultDashBoardItem.getTitle());
	}

}
