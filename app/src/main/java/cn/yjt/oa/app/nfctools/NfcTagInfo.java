package cn.yjt.oa.app.nfctools;

import org.ndeftools.Message;

public class NfcTagInfo {
	
	private String serialNumber;
	private String type;
	private String dataFormat;
	private int maxSize;
	private int usedSize;
	private boolean canMakeReadOnly;
	private boolean isWriteable;
	private Message message;
	private byte[] tag216Data;
	
	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getUsedSize() {
		return usedSize;
	}

	public void setUsedSize(int usedSize) {
		this.usedSize = usedSize;
	}

	public boolean isCanMakeReadOnly() {
		return canMakeReadOnly;
	}

	public void setCanMakeReadOnly(boolean canMakeReadOnly) {
		this.canMakeReadOnly = canMakeReadOnly;
	}

	public boolean isWriteable() {
		return isWriteable;
	}

	public void setWriteable(boolean isWriteable) {
		this.isWriteable = isWriteable;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	
	public byte[] getTag216Data() {
		return tag216Data;
	}

	public void setTag216Data(byte[] tag216Data) {
		this.tag216Data = tag216Data;
	}

	@Override
	public String toString() {
		return "NfcTagInfo [serialNumber=" + serialNumber + ", type=" + type
				+ ", dataFormat=" + dataFormat + ", maxSize=" + maxSize
				+ ", usedSize=" + usedSize + ", canMakeReadOnly="
				+ canMakeReadOnly + ", isWriteable=" + isWriteable
				+ ", message=" + message + "]";
	}
	
	

}
