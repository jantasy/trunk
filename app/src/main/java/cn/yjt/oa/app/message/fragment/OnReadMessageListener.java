package cn.yjt.oa.app.message.fragment;

import cn.yjt.oa.app.beans.MessageInfo;

interface OnReadMessageListener {
	void uploadReadStatue(MessageInfo info); 
	boolean needRefrash(boolean needRefrash);
}
