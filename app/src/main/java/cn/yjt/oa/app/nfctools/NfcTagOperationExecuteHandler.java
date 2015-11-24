package cn.yjt.oa.app.nfctools;

import android.content.Context;
import cn.yjt.oa.app.nfctools.executehandler.NfcTagCommonOperationExecuteHandler;
import cn.yjt.oa.app.nfctools.executehandler.NfcTagSigninOperationExecuteHandler;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperation;

public abstract class NfcTagOperationExecuteHandler {
	private NfcTagOperation nfcTagOperation;
	
	public NfcTagOperation getNfcTagOperation() {
		return nfcTagOperation;
	}

	public void setNfcTagOperation(NfcTagOperation nfcTagOperation) {
		this.nfcTagOperation = nfcTagOperation;
	}

	public abstract void handle(Context context,String tagName);

	public static NfcTagOperationExecuteHandler create(
			NfcTagOperation nfcTagOperation) {
		NfcTagOperationExecuteHandler executeHandler;
		if (nfcTagOperation == null) {
			executeHandler = new NfcTagCommonOperationExecuteHandler();
		} else {
			switch (nfcTagOperation.getOperationId()) {
			case NfcTagOperation.NFC_TAG_OPERATION_SIGNIN:
				executeHandler = new NfcTagSigninOperationExecuteHandler();
				break;
			default:
				executeHandler = new NfcTagCommonOperationExecuteHandler();
				break;
			}
		}
		executeHandler.nfcTagOperation = nfcTagOperation;
		return executeHandler;
	}

}
