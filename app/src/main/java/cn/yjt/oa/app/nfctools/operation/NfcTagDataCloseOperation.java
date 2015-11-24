package cn.yjt.oa.app.nfctools.operation;

import cn.yjt.oa.app.nfctools.APNTools;

public class NfcTagDataCloseOperation extends NfcTagOperation {

	@Override
	public void excuteOperation() {
		APNTools.closeDataConnection(getContext());
	}

}
