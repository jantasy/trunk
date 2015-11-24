package cn.yjt.oa.app.message.fragment;

import cn.yjt.oa.app.beans.MessageInfo;

public interface OnTopChangedUiListener {
	void onAddToTop(MessageInfo message);
	void onAdapterChange();
	void onRemoveFromTop(MessageInfo message);
}