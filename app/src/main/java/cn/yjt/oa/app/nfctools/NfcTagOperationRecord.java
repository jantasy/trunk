package cn.yjt.oa.app.nfctools;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.nfc.NdefRecord;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperation;
import cn.yjt.oa.app.nfctools.operation.NfcTagWellkownOperation;

public class NfcTagOperationRecord implements Parcelable {

	static final String TAG = NfcTagOperationRecord.class.getSimpleName();

	private static final byte[] OPERATION_SEPARATOR = { -0x01, 0x1E, 0x1F };

	private String tagName;
	private String content;
	private List<NfcTagOperation> operations = new ArrayList<NfcTagOperation>();
	private byte[] data;
	private long id;

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<NfcTagOperation> getOperations() {
		return operations;
	}

	public void setOperations(List<NfcTagOperation> operations) {
		this.operations = operations;
	}

	public byte[] getData() {
		data = generateRecordData(operations,tagName);
		return data;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private static byte[] generateRecordData(List<NfcTagOperation> operations,String tagName) {
		if (tagName == null || operations == null) {
			return null;
		} else {
			byte[][] datas = new byte[operations.size() + 1][];
			datas[0] = tagName.getBytes();
			int totleLength = datas[0].length;
			for (int i = 0; i < operations.size(); i++) {
				datas[i + 1] = operations.get(i).generateOperationData();
				totleLength += datas[i + 1].length;
			}
			byte[] recordData = new byte[totleLength + (operations.size())
					* OPERATION_SEPARATOR.length];
			int writeLength = 0;
			for (int i = 0; i < datas.length; i++) {
				// write data.
				System.arraycopy(datas[i], 0, recordData, writeLength,
						datas[i].length);
				writeLength += datas[i].length;
				if (i < datas.length - 1) {
					// write a separate.
					System.arraycopy(OPERATION_SEPARATOR, 0, recordData,
							writeLength, OPERATION_SEPARATOR.length);
					writeLength += OPERATION_SEPARATOR.length;
				}
			}
			return recordData;

		}
	}

	public List<NdefRecord> generateNdefRecord() {
		NdefRecord.createExternal("yjt", "opt", getData());
		List<NfcTagOperation> operationBundle = new ArrayList<NfcTagOperation>();
		List<NfcTagWellkownOperation> wellkownOperations = new ArrayList<NfcTagWellkownOperation>();
		for (NfcTagOperation nfcTagOperation : operations) {
			if(nfcTagOperation instanceof NfcTagWellkownOperation){
				wellkownOperations.add((NfcTagWellkownOperation) nfcTagOperation);
			}else{
				operationBundle.add(nfcTagOperation);
			}
		}
		
		List<NdefRecord> ndefRecords = new ArrayList<NdefRecord>();
		if(!operationBundle.isEmpty()){
			NdefRecord ndefRecord = NdefRecord.createExternal("yjt", "opt", generateRecordData(operationBundle, getTagName()));
			ndefRecords.add(ndefRecord);
		}
		for (NfcTagWellkownOperation wellkownOperation : wellkownOperations) {
			ndefRecords.add(wellkownOperation.generateNdefRecord());
		}
		
		return ndefRecords;
		
	}

	public static NfcTagOperationRecord parseRecord(byte[] data) {
		Log.d(TAG, "parseRecord:" + Arrays.toString(data));

		List<byte[]> bytes = new ArrayList<byte[]>();

		int len = 0;
		byte[] b = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			// TagName and Operations are splited by a line feed.
			if (data[i] == OPERATION_SEPARATOR[0] && data.length > i + 3
					&& data[i + 1] == OPERATION_SEPARATOR[1]
					&& data[i + 2] == OPERATION_SEPARATOR[2]) {
				byte[] dst = new byte[len];
				System.arraycopy(b, 0, dst, 0, len);
				bytes.add(dst);
				len = 0;
				i += 2;
			} else {
				b[len++] = data[i];
				if (i == data.length - 1) {
					byte[] dst = new byte[len];
					System.arraycopy(b, 0, dst, 0, len);
					bytes.add(dst);
				}
			}
		}

		NfcTagOperationRecord content = new NfcTagOperationRecord();
		if (!bytes.isEmpty()) {
			content.data = data;
			try {
				// The first part in the data must be tag name.
				content.setTagName(new String(bytes.get(0), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// Other parts are operations.
			StringBuilder builder = new StringBuilder();
			List<NfcTagOperation> nfcTagOperations = new ArrayList<NfcTagOperation>();
			for (int i = 1; i < bytes.size(); i++) {
				NfcTagOperation operation = NfcTagOperation
						.parseOperation(bytes.get(i));
				if (operation != null) {

					if (operation != null) {
						builder.append(operation.getOperatorName()).append(":")
								.append(operation.getOperationText());
						nfcTagOperations.add(operation);
					}
				}
			}
			content.setContent(builder.toString());
			content.setOperations(nfcTagOperations);
		}

		return content;
	}

	public static final Creator<NfcTagOperationRecord> CREATOR = new Creator<NfcTagOperationRecord>() {

		@Override
		public NfcTagOperationRecord[] newArray(int size) {
			return new NfcTagOperationRecord[size];
		}

		@Override
		public NfcTagOperationRecord createFromParcel(Parcel source) {
			NfcTagOperationRecord record = new NfcTagOperationRecord();
			record.setTagName(source.readString());
			record.setContent(source.readString());
			source.readTypedList(record.operations, NfcTagOperation.CREATOR);
			// int len = source.readInt();
			// if(len != -1){
			// record.data = new byte[len];
			// source.readByteArray(record.data);
			// }
			record.setId(source.readLong());
			return record;
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(tagName);
		dest.writeString(content);
		dest.writeTypedList(operations);
		// dest.writeByteArray(getData());
		dest.writeLong(id);
	}

	@Override
	public String toString() {
		return "NfcTagOperationRecord [tagName=" + tagName + ", content="
				+ content + ", operations=" + operations + ", data="
				+ Arrays.toString(generateRecordData(operations,tagName)) + ", id=" + id + "]";
	}

}
