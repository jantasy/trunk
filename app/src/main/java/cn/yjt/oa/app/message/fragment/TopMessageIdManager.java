package cn.yjt.oa.app.message.fragment;

import java.util.ArrayList;


import android.content.Context;
import android.content.SharedPreferences;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.UserInfo;

/**
 *	置顶消息的管理类 
 *
 */
public class TopMessageIdManager {

	//是否初始化完成
	private static boolean inited = false;
	//置顶消息的个数
	private static final int TOP_ID_LIMIT = 5;
	//置顶消息id的集合
	private static final ArrayList<String> topIds = new ArrayList<String>();
	//监听置顶消息发生改变的接口的集合
	private static final ArrayList<OnTopChangedDataListener> topChangeListeners = new ArrayList<OnTopChangedDataListener>();
	
	//-------监听置顶消息改变的接口----------
	public interface OnTopChangedDataListener {
		void onAddTopMessage(MessageInfo msg);
		void onRemoveTopMessage(MessageInfo msg);
		void onRemoveTopMsgId(long msgId);
	}
	
	public static void registTopChangeListener(OnTopChangedDataListener listener) {
		synchronized (topChangeListeners) {
			topChangeListeners.add(listener);
		}
	}
	
	public static void unregistTopChangeListener(OnTopChangedDataListener listener) {
		synchronized (topChangeListeners) {
			topChangeListeners.remove(listener);
		}
	}
	//--------------------------------
	
	//初始化置顶消息的id
	private static void initTopMessageIds() {
		synchronized (topIds) {
			if (!inited) {
				//从sp中获取置顶消息，添加到topids中
				SharedPreferences sp = MainApplication.getAppContext().getSharedPreferences("top_msg", Context.MODE_PRIVATE);
				UserInfo userInfo = AccountManager.getCurrent(MainApplication.getAppContext());
				String key = "top_msg_ids_" + (userInfo != null ? userInfo.getId() : "null");
				String value = sp.getString(key, null);
				if (value != null) {
					String[] idArr = value.split(",");
					if (idArr != null) {
						for (String id:idArr) {
							topIds.add(id);
						}
					}
				}
				inited = true;
			}
		}
	}
	
	/**
	 * 增加置顶消息，将message的id添加到topIds集合的第一位，如果集合中的数量超过界限，就移除集合中的最后一个id
	 * @param msg 所要添加的置顶消息
	 */
	public static void addTopMessage(MessageInfo msg) {
		long removedMsgId = -1;
		long msgId = msg.getId();
		initTopMessageIds();
		synchronized (topIds) {
			topIds.add(0, String.valueOf(msgId));
			if (TOP_ID_LIMIT > 0 && topIds.size() > TOP_ID_LIMIT) {
				String removed = topIds.remove(topIds.size()-1);
				try {
					removedMsgId = Long.valueOf(removed);
				} catch (NumberFormatException e) {
					removedMsgId = -1;
				}
			}
		}
		
		saveTopIds();
		
		//执行置顶消息添加的监听事件
		performAddTopMessage(msg);
		
		if (removedMsgId != -1)
			//执行置顶消息移除的监听事件
			performRemoveTopMessageId(removedMsgId);
	}

	/**
	 * 移除置顶消息
	 * @param msg
	 */
	public static void removeTopMessage(MessageInfo msg) {
		long msgId = msg.getId();
		initTopMessageIds();
		synchronized (topIds) {
			topIds.remove(String.valueOf(msgId));
		}
		
		saveTopIds();
		performRemoveTopMessage(msg);
	}
	
	private static void performAddTopMessage(MessageInfo msg) {
		synchronized (topChangeListeners) {
			for (OnTopChangedDataListener listener:topChangeListeners) {
				listener.onAddTopMessage(msg);
			}
		}
	}
	
	private static void performRemoveTopMessageId(long msgId) {
		synchronized (topChangeListeners) {
			for (OnTopChangedDataListener listener:topChangeListeners) {
				listener.onRemoveTopMsgId(msgId);
			}
		}
	}
	
	private static void performRemoveTopMessage(MessageInfo msg) {
		synchronized (topChangeListeners) {
			for (OnTopChangedDataListener listener:topChangeListeners) {
				listener.onRemoveTopMessage(msg);
			}
		}
	}
	
	/**
	 * 将所有置顶消息的id用逗号隔开拼接成字符串
	 * @return 拼接后的字符串
	 */
	public static String getTopMsgIdsString() {
		StringBuilder ids = new StringBuilder();
		initTopMessageIds();
		synchronized (topIds) {
			final int size = topIds.size();
			for (int i = 0; i < size ; i++) {
				ids.append(topIds.get(i));
				ids.append(",");
			}
		}
		
		return ids.toString();
	}
	
	public static boolean containsTopMsgId(long msgId) {
		initTopMessageIds();
		synchronized (topIds) {
			return topIds.contains(String.valueOf(msgId));
		}
	}
	
	//将置顶消息的id写入sp中
	private static void saveTopIds() {
		SharedPreferences sp = MainApplication.getAppContext().getSharedPreferences("top_msg", Context.MODE_PRIVATE);
		UserInfo userInfo = AccountManager.getCurrent(MainApplication.getAppContext());
		String key = "top_msg_ids_" + (userInfo != null ? userInfo.getId() : "null");
		sp.edit().putString(key, getTopMsgIdsString()).commit();
	}
	//删除sp中置顶消息的id
	public static void deleteTopIds() {
		SharedPreferences sp = MainApplication.getAppContext().getSharedPreferences("top_msg", Context.MODE_PRIVATE);
		UserInfo userInfo = AccountManager.getCurrent(MainApplication.getAppContext());
		String key = "top_msg_ids_" + (userInfo != null ? userInfo.getId() : "null");
		sp.edit().remove(key).commit();
		topIds.clear();
		
	}
}
