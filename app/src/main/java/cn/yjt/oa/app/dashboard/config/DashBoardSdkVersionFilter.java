package cn.yjt.oa.app.dashboard.config;

import java.util.Iterator;
import java.util.List;

import android.content.pm.PackageManager;
import android.os.Build.VERSION;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.DashBoardItem;
import cn.yjt.oa.app.permisson.PermissionManager;

public class DashBoardSdkVersionFilter implements DashBoardFilter {

	@Override
	public void filt(List<DashBoardItem> boardItems) {
		Iterator<DashBoardItem> iterator = boardItems.iterator();
				
		while (iterator.hasNext()) {
			DashBoardItem boardItem = iterator.next();
			//System.out.println("sdkversion"+boardItem.getSdkVersion());
			if(boardItem.getSdkVersion()!=0){
				Boolean isVerify = boardItem.getSdkVersion()<=VERSION.SDK_INT?true:false;
				if (!isVerify) {
					iterator.remove();
				}
			}
		}
	}
}
