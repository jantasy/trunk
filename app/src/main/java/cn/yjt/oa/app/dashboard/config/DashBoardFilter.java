package cn.yjt.oa.app.dashboard.config;

import java.util.List;

import cn.yjt.oa.app.beans.DashBoardItem;

public interface DashBoardFilter {
	
	public void filt(List<DashBoardItem> boardItems);
}
