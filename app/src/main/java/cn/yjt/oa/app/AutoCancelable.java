package cn.yjt.oa.app;

import io.luobo.common.Cancelable;


public interface AutoCancelable {
	
	public void addCancelable(Cancelable cancelable);
	
	public void removeCancelable(Cancelable cancelable);
	
}
