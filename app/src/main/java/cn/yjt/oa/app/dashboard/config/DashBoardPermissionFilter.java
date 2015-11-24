package cn.yjt.oa.app.dashboard.config;

import java.util.Iterator;
import java.util.List;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.DashBoardItem;
import cn.yjt.oa.app.permisson.PermissionManager;

public class DashBoardPermissionFilter implements DashBoardFilter {
	@Override
	public void filt(List<DashBoardItem> boardItems) {
		if(boardItems!=null){
			Iterator<DashBoardItem> iterator = boardItems.iterator();
			String userPermission = AccountManager.getCurrent(
					MainApplication.getAppContext()).getPermission();
			if(iterator!=null){
				while (iterator.hasNext()) {
					DashBoardItem boardItem = iterator.next();
						boolean isVerify = PermissionManager.verify(userPermission,
								boardItem.getRequirePermission());
						if (!isVerify) {
							iterator.remove();
					}
				}
			}
		}
	}
}
