package cn.yjt.oa.app.nfctools;

import android.view.View;
import android.widget.Checkable;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperation;

public interface NfcTagOperationView extends Checkable{
	
    public static interface OnCheckedChangeListener {
        void onCheckedChanged(NfcTagOperationView operationView, boolean isChecked);
    }
    
    public static interface ExtraView{
    	
    	public View getView();
    	public byte[] getExtraData();

    	/**
    	 * @see NfcTagOperationViewGroup#EXTRA_VIEW_GRAVITY_BOTTOM
    	 * @see NfcTagOperationViewGroup#EXTRA_VIEW_GRAVITY_BELLOW
    	 * @return
    	 */
    	public int getExtraViewGravity();
    	
    	/**
    	 * @see NfcTagOperationViewGroup#EXTRA_VIEW_GRAVITY_BOTTOM
    	 * @see NfcTagOperationViewGroup#EXTRA_VIEW_GRAVITY_BELLOW
    	 */
    	public void setExtraViewGravity(int gravity);
    	
    }
	
	public int getOperationId();
	
	public View getView();
	
	public void setOnCheckedChangedListener(OnCheckedChangeListener listener);
	
	public void setNfcTagOperation(NfcTagOperation operation);
	
	public NfcTagOperation getNfcTagOperation();
	
	public ExtraView getExtraView();
	
	public void setExtraView(ExtraView extraView);
	
	
	
}
