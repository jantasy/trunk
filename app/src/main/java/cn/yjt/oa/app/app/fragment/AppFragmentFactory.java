package cn.yjt.oa.app.app.fragment;

import cn.yjt.oa.app.app.controller.AppType;

public class AppFragmentFactory {
	public static final int PAGER_COUNT=2;
	
	public static BaseFragment getFragmentWithType(String type){
		BaseFragment fragment = null;
		switch (AppType.getAppType(type)) {
		case AppType.APPS_CARDAPP:
			fragment = new ActivityAppFragment();
			break;
		case AppType.APPS_BUSINESS:
			fragment = new DashBoardAppFragment();
			break;
		default:
			break;
		}
		return fragment;
	}
	
	public static BaseFragment getFragmentForPager(int index){
		BaseFragment fragment = null;
		switch (index) {
		case 1:
			fragment = new AppLocalFragment();
			break;
		case 0:
			fragment = new ActivityAppFragment();
			break;
		default:
			break;
		}
		return fragment;
	}
}
