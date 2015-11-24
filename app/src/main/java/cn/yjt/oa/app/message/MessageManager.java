package cn.yjt.oa.app.message;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 * 消息管理类
 *
 */
public class MessageManager {
	
	/**单例设计模式，初始化构造函数，并且不允许重新赋值*/
	private static final MessageManager INSTANCE = new MessageManager();
	
	/**封装接口消息观察者的集合*/
	private Set<WeakReference<MessageObserver>> managerObservers = new HashSet<WeakReference<MessageObserver>>();
	
	private MessageManager() {
	}
	
	/**获取Messager对象*/
	public static MessageManager getInstance(){
		return INSTANCE;
	}
	
	/**注册消息中心观察者*/
	public void registerManagerObserver(MessageObserver observer){
		managerObservers.add(new WeakReference<MessageObserver>(observer));
	}
	
	/**取消已注册的消息中心的观察者*/
	public void unregisterManagerObserver(MessageObserver observer){
		managerObservers.remove(observer);
	}
	
	/**更新‘已读/未读’状态的改变*/
	public void notifyReadChanged(String type,long id,int read){
		for (WeakReference<MessageObserver> weakReference : managerObservers) {
			MessageObserver messageObserver = weakReference.get();
			if(messageObserver != null){
				messageObserver.onReadStateChanged(type,id, read);
			}
		}
	}
	
	
}
