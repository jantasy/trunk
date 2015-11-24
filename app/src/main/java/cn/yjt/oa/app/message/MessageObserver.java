package cn.yjt.oa.app.message;


/**
 * 消息观察者接口
 *
 */
public interface MessageObserver {

	/**
	 * 更新消息的‘已读/未读’状态改变
	 * @param type  消息类型
	 * @param id	消息id	
	 * @param read	‘未读/已读’标记
	 */
	public void onReadStateChanged(String type,long id,int read);
	
	/**获取id*/
	public long getId();
	
	/**设置为已读*/
	public void setIsRead(int read);
}
