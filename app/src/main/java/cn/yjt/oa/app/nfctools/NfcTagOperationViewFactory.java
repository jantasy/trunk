package cn.yjt.oa.app.nfctools;

import android.content.Context;
import cn.yjt.oa.app.nfctools.NfcTagOperationView.ExtraView;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperation;
import cn.yjt.oa.app.nfctools.operationview.NfcTagOperationOpenAppView;
import cn.yjt.oa.app.nfctools.operationview.NfcTagOperationSelectedView;
import cn.yjt.oa.app.nfctools.operationview.NfcTagOperationWifiExtraView;

public class NfcTagOperationViewFactory {

	public static NfcTagOperationView create(Context context, int id) {
		NfcTagOperationView nfcTagOperationView;
		switch (id) {
		case NfcTagOperation.NFC_TAG_OPERATION_WIFI_OPEN:
			nfcTagOperationView = new NfcTagOperationSelectedView(context);
			break;
		case NfcTagOperation.NFC_TAG_OPERATION_WIFI_CLOSE:
			nfcTagOperationView = new NfcTagOperationSelectedView(context);
			break;
		case NfcTagOperation.NFC_TAG_OPERATION_WIFI_TOGGLE:
			nfcTagOperationView = new NfcTagOperationSelectedView(context);
			break;
		case NfcTagOperation.NFC_TAG_OPERATION_SILENT_OFF:
			nfcTagOperationView = new NfcTagOperationSelectedView(context);
			break;
		case NfcTagOperation.NFC_TAG_OPERATION_SILENT_MUTE:
			nfcTagOperationView = new NfcTagOperationSelectedView(context);
			break;
		case NfcTagOperation.NFC_TAG_OPERATION_SILENT_VIBRATE:
			nfcTagOperationView = new NfcTagOperationSelectedView(context);
			break;
		case NfcTagOperation.NFC_TAG_OPERATION_OPEN_APP:
			nfcTagOperationView = new NfcTagOperationOpenAppView(context);
			break;
		default:
			nfcTagOperationView = null;
			break;
		}
		if(nfcTagOperationView != null){
			nfcTagOperationView.setExtraView(createExtraView(context, id));
		}
		return nfcTagOperationView;
	}

	public static NfcTagOperationView create(Context context,
			NfcTagOperation operation) {
		NfcTagOperationView operationView = create(context,
				operation.getOperationId());
		operationView.setNfcTagOperation(operation);
		return operationView;
	}

	public static ExtraView createExtraView(Context context, int id) {
		ExtraView extraView;
		switch (id) {
		case NfcTagOperation.NFC_TAG_OPERATION_WIFI_OPEN:
			extraView = new NfcTagOperationWifiExtraView(context);
			extraView.setExtraViewGravity(NfcTagOperationViewGroup.EXTRA_VIEW_GRAVITY_BOTTOM);
			break;
		default:
			extraView = null;
			break;
		}
		return extraView;
	}
}
