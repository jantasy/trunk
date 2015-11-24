package cn.yjt.oa.app.nfctools;

import java.util.List;

import cn.yjt.oa.app.nfctools.operation.NfcTagOperation;


public interface NfcTagOperationDialog {
	
	public static interface NfcTagOperationDialogInterface{
		public void onSelected(NfcTagOperation operation);
		public void onCanceled();
	}
	
	public void setNfcTagOperations(List<NfcTagOperation> operations);
	
	public List<NfcTagOperation> getNfcTagOperations();
	
	public void show();
	
	public void setNfcTagOperationDialogInterface(NfcTagOperationDialogInterface dialogInterface);
	

}
