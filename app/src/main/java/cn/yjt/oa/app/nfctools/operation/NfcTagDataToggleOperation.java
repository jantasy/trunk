package cn.yjt.oa.app.nfctools.operation;

import cn.yjt.oa.app.nfctools.APNTools;


public class NfcTagDataToggleOperation extends NfcTagOperation {

	@Override
	public void excuteOperation() {
		APNTools.toggleState(getContext());

	}

}
