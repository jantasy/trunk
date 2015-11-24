package cn.yjt.oa.app.nfctools.operationview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.nfctools.NfcTagOperationView;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperation;

public class NfcTagOperationSelectedView extends FrameLayout implements
		NfcTagOperationView {

	private NfcTagOperation nfcTagOperation;
	private boolean isChecked;
	private View view;
	private RadioButton operationBtn;
	private OnCheckedChangeListener mListener;
	private ExtraView extraView;

	public NfcTagOperationSelectedView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public NfcTagOperationSelectedView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NfcTagOperationSelectedView(Context context) {
		this(context, null);
	}
	
	private void init(Context context){
		view = LayoutInflater.from(context).inflate(R.layout.operation_selected_view, this);
		operationBtn = (RadioButton) view.findViewById(R.id.operation_btn);
		operationBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(mListener != null){
					mListener.onCheckedChanged(NfcTagOperationSelectedView.this, isChecked);
				}
				
				if(extraView != null){
					extraView.getView().setVisibility(isChecked ? View.VISIBLE : View.GONE);
				}
			}
		});
		
	}

	@Override
	public void setChecked(boolean checked) {
		isChecked = checked;
		operationBtn.setChecked(isChecked);
	}

	@Override
	public boolean isChecked() {
		return isChecked;
	}

	@Override
	public void toggle() {
		isChecked = !isChecked;
		operationBtn.setChecked(isChecked);
	}

	@Override
	public int getOperationId() {
		return nfcTagOperation.getOperationId();
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public void setOnCheckedChangedListener(OnCheckedChangeListener listener) {
		mListener=listener;
	}

	@Override
	public void setNfcTagOperation(NfcTagOperation operation) {
		nfcTagOperation = operation;
		operationBtn.setText(nfcTagOperation.getOperationName());
	}

	@Override
	public NfcTagOperation getNfcTagOperation() {
		if(extraView != null){
			nfcTagOperation.setExtraData(extraView.getExtraData());
		}
		return nfcTagOperation;
	}

	@Override
	public ExtraView getExtraView() {
		return extraView;
	}

	@Override
	public void setExtraView(ExtraView extraView) {
		this.extraView = extraView;
	}

}
