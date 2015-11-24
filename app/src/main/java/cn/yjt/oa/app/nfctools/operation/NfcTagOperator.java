package cn.yjt.oa.app.nfctools.operation;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class NfcTagOperator implements Parcelable{

	private static final String SCHEME_RESOURCE = "res://";

	public static enum Type {
		/**
		 * Normal operation is just a action.
		 */
		OPERATION_TYPE_NORMAL,
		/**
		 * Data operation need some data,so it needs a special UI to set data.
		 */
		OPERATION_TYPE_DATA
	}

	private String operatorName;
	private List<NfcTagOperation> nfcTagOperations = new ArrayList<NfcTagOperation>();
	private NfcTagOperation selectedOperation;
	private Type type;
	private String icon;

	void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public List<NfcTagOperation> getNfcTagOperations() {
		return nfcTagOperations;
	}

	void addNfcTagOperation(NfcTagOperation operation) {
		nfcTagOperations.add(operation);
	}

	public boolean contains(NfcTagOperation nfcTagOperation) {
		return nfcTagOperations.contains(nfcTagOperation);
	}

	public NfcTagOperation getSelectedOperation() {
		return selectedOperation;
	}

	public void setSelectedOperation(NfcTagOperation selectedOperation) {
		this.selectedOperation = selectedOperation;
	}

	public String getIcon() {
		return icon;
	}

	void setIcon(String icon) {
		this.icon = icon;
	}

	public Drawable getIconDrawable(Context context) {
		if (icon != null) {
			if (icon.startsWith(SCHEME_RESOURCE)) {
				String resourcePath = icon.substring(SCHEME_RESOURCE.length());
				int identifier = context.getResources().getIdentifier(
						resourcePath, null, context.getPackageName());
				return context.getResources().getDrawable(identifier);
			}
		}
		return null;
	}

	/**
	 * @see Type
	 * @return
	 */
	public Type getType() {
		return type;
	}

	void setType(String type) {
		Type t = Type.valueOf(type);
		if (t == null) {
			t = Type.OPERATION_TYPE_NORMAL;
		}
		this.type = t;
	}

	
	
	@Override
	public String toString() {
		return "NfcTagOperator [operatorName=" + operatorName
				+ ", nfcTagOperations=" + nfcTagOperations
				+ ", selectedOperation=" + selectedOperation + ", type=" + type
				+ ", icon=" + icon + "]";
	}

	public static final Creator<NfcTagOperator> CREATOR = new Creator<NfcTagOperator>() {
		
		@Override
		public NfcTagOperator[] newArray(int size) {
			return new NfcTagOperator[size];
		}
		
		@Override
		public NfcTagOperator createFromParcel(Parcel source) {
			NfcTagOperator operator = new NfcTagOperator();
			operator.setOperatorName(source.readString());
			source.readTypedList(operator.nfcTagOperations , NfcTagOperation.CREATOR);
			operator.selectedOperation = source.readParcelable(NfcTagOperation.class.getClassLoader());
			operator.setType(source.readString());
			operator.setIcon(source.readString());
			return operator;
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(operatorName);
		dest.writeTypedList(nfcTagOperations);
		dest.writeParcelable(selectedOperation, flags);
		if(type == null){
			dest.writeString(Type.OPERATION_TYPE_NORMAL.toString());
		}else{
			dest.writeString(type.toString());
		}
		dest.writeString(icon);
	}
}
