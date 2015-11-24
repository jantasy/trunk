package cn.yjt.oa.app.nfctools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.UnsupportedRecord;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import com.nxp.nfclib.exceptions.SmartCardException;
import com.nxp.nfclib.ntag.NTag210.CardDetails;
import com.nxp.nfclib.ntag.NTag213215216;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class NfcReader extends NfcDetector {

	private static final String TAG = NfcReader.class.getName();

	private NfcReaderInterface readerInterface;

	public NfcReader(Activity activity, NfcReaderInterface readerInterface) {
		super(activity, readerInterface);
		this.readerInterface = readerInterface;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	@Override
	public void nfcIntentDetected(Intent intent, String action) {
		Log.d(TAG, "nfcIntentDetected: " + action);
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		Ndef ndef = Ndef.get(tag);
		try {
			ndef.connect();
			int usedSize = 0;
			NfcTagInfo tagInfo = new NfcTagInfo();
			tagInfo.setType(getTechIso(tag.getTechList()));
			tagInfo.setSerialNumber(toHexString(tag.getId()));
			tagInfo.setMaxSize(ndef.getMaxSize());
			tagInfo.setDataFormat(ndef.getType());
			tagInfo.setCanMakeReadOnly(ndef.canMakeReadOnly());
			tagInfo.setWriteable(ndef.isWritable());
			NdefMessage ndefMessage = ndef.getNdefMessage();
			if (ndefMessage != null) {

				if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
					usedSize = ndefMessage.getByteArrayLength();
					tagInfo.setUsedSize(usedSize);
				}

				NdefRecord[] records = ndefMessage.getRecords();
				if (records.length > 0) {
					Message message = new Message();
					for (int j = 0; j < records.length; j++) {
						if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN) {
							usedSize += records[j].toByteArray().length;
						}
						try {
							message.add(Record.parse(records[j]));
						} catch (FormatException e) {
							// if the record is unsupported or corrupted, keep
							// as
							// unsupported record
							message.add(UnsupportedRecord.parse(records[j]));
						}
					}
					tagInfo.setMessage(message);
					tagInfo.setUsedSize(usedSize);
					Log.d(TAG, "tagInfo:" + tagInfo);
					readerInterface.readNdefMessage(tagInfo);
				} else {
					readerInterface.readEmptyNdefMessage(tagInfo);
				}
			} else {
				readerInterface.readNonNdefMessage(tagInfo);
			}

		} catch (IOException e) {
			readerInterface.connectError(e);
			e.printStackTrace();
		} catch (Exception e) {
			readerInterface.connectError(e);
			e.printStackTrace();
		} finally {
			try {
				if (ndef != null) {
					ndef.close();
				}
			} catch (Exception e) {
				readerInterface.connectError(e);
				e.printStackTrace();
			}
		}
	}

	public static String getTechIso(String[] techList) {
		if (techList == null) {
			return "Unknow ISO";
		}
		String str = new String();
		if (Arrays.asList(techList).contains(NfcA.class.getName()))
			str = "ISO 14443-3A";
		if (Arrays.asList(techList).contains(NfcB.class.getName()))
			str = "ISO 14443-3B";
		if (Arrays.asList(techList).contains(NfcF.class.getName()))
			str = "JIS 6319-4";
		if (Arrays.asList(techList).contains(NfcV.class.getName()))
			str = "ISO 15693";
		if (Arrays.asList(techList).contains(IsoDep.class.getName()))
			str = "ISO 14443-4";
		if (str.isEmpty())
			str = "Unknow ISO";
		return str;
	}

	public static interface NfcReaderInterface extends NfcDetectorInterface {
		/**
		 * An NDEF message was read and parsed
		 * 
		 * @param message
		 *            the message
		 */

		public abstract void readNdefMessage(NfcTagInfo info);

		/**
		 * An empty NDEF message was read.
		 * 
		 */

		public abstract void readEmptyNdefMessage(NfcTagInfo info);

		/**
		 * 
		 * Something was read via NFC, but it was not an NDEF message.
		 * 
		 * Handling this situation is out of scope of this project.
		 * 
		 */

		public abstract void readNonNdefMessage(NfcTagInfo info);

		public void connectError(Exception e);

		public abstract void readTag216(NfcTagInfo info);
	}

	@Override
	public void nTag213215216CardDetected(NTag213215216 tag,
			boolean isUsePassword , Intent intent) throws SmartCardException {
		if (isUsePassword) {
			authenticatePwd(tag);
			readNTag216(tag,intent);
		} else {
//			try {
//				tag.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			nfcIntentDetected(intent, intent.getAction());
		}

	}

//	private void readNdef(NTag213215216 tag) throws SmartCardException {
//		try {
//			NdefMessage ndefMessage = tag.readNDEF();
//			if (ndefMessage != null) {
//				NfcTagInfo tagInfo = new NfcTagInfo();
//				tagInfo.setType(tag.getType().name());
//				tagInfo.setSerialNumber(toHexString(tag.getUID()));
//				tagInfo.setMaxSize(tag.getCardDetails().totalMemory);
//				tagInfo.setDataFormat(tag.getType().name());
//
//				NdefRecord[] records = ndefMessage.getRecords();
//				if (records.length > 0) {
//					Message message = new Message();
//					for (int j = 0; j < records.length; j++) {
//						try {
//							message.add(Record.parse(records[j]));
//						} catch (FormatException e) {
//							message.add(UnsupportedRecord.parse(records[j]));
//						}
//					}
//					tagInfo.setMessage(message);
//					tagInfo.setUsedSize(tag.getCardDetails().totalMemory
//							- tag.getCardDetails().freeMemory);
//					Log.d(TAG, "tagInfo:" + tagInfo);
//				}
//				readerInterface.readNdefMessage(tagInfo);
//			}
//
//		} catch (SmartCardException e) {
//			throw e;
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			readerInterface.connectError(e1);
//		}
//	}

	private void readNTag216(NTag213215216 tag, Intent intent) throws SmartCardException {
		Tag extraTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			byte[] data = readNTag(tag);
			NfcTagInfo tagInfo = new NfcTagInfo();
			tagInfo.setType(tag.getType().name());
			tagInfo.setSerialNumber(toHexString(extraTag.getId()));
			CardDetails cardDetails = tag.getCardDetails();
			tagInfo.setMaxSize(cardDetails.totalMemory);
			tagInfo.setUsedSize(cardDetails.userMemory);
			tagInfo.setDataFormat("YJT NFC format");
			tagInfo.setTag216Data(data);
			//TODO:
			readerInterface.readTag216(tagInfo);
	}

	public static byte[] readNTag(NTag213215216 tag) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int lastUserPage = tag.getLastUserPage();
		int dataLength = 0;
		for (int i = 100; i <= lastUserPage; i++) {
			
			try {
				byte[] read = tag.read(i);
				if (i == 100) {
					buffer.write(read, 1, 3);
				} else {
					buffer.write(read,0,4);
				}
				if (i == 100) {
					dataLength = read[0];
					lastUserPage = read[0] /4 + 100  +1;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		byte[] byteArray = buffer.toByteArray();
		byte[] data = new byte[dataLength];
		System.arraycopy(byteArray, 0, data, 0, dataLength);
		return data;
	}

}
