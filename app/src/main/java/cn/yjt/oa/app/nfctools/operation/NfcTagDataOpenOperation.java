package cn.yjt.oa.app.nfctools.operation;

import cn.yjt.oa.app.nfctools.APNTools;

public class NfcTagDataOpenOperation extends NfcTagOperation {

	@Override
	public void excuteOperation() {
		APNTools.openDataConnection(getContext());

	}

}
