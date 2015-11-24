package cn.yjt.oa.app.nfctools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.ndeftools.Message;
import org.ndeftools.wellknown.UriRecord;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.util.Log;

import com.nxp.nfclib.exceptions.SmartCardException;
import com.nxp.nfclib.ntag.NTag213215216;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class NfcTagWriter extends NfcDetector {

	private NfcTagWriterInterface writerInterface;
	private boolean formatReadOnly;

	public NfcTagWriter(Activity activity, NfcTagWriterInterface writerInterface) {
		super(activity, writerInterface);
		this.writerInterface = writerInterface;
	}

	private static final String TAG = NfcTagWriter.class.getName();

	@Override
	public void nfcIntentDetected(Intent intent, String action) {
		// then write
		write(writerInterface.createNdefMessage(), intent);
	}

	public boolean write(Message message, Intent intent) {
		return write(message.getNdefMessage(), intent);
	}
	
	public void setFormatReadOnly(boolean formatReadOnly) {
		this.formatReadOnly = formatReadOnly;
	}

	public boolean write(NdefMessage rawMessage, Intent intent) {
		System.out.println("write:"+rawMessage);
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		NdefFormatable format = NdefFormatable.get(tag);
		if (format != null) {
			Log.d(TAG, "Write unformatted tag");
			try {
				format.connect();
				if(formatReadOnly){
					format.formatReadOnly(rawMessage);
				}else{
					format.format(rawMessage);
				}

				writerInterface.writeNdefSuccess(toHexString(tag.getId()));

				return true;
			} catch (Exception e) {
				writerInterface.writeNdefFailed(e);
			} finally {
				try {
					format.close();
				} catch (IOException e) {
					// ignore
				}
			}
			Log.d(TAG, "Cannot write unformatted tag");
		} else {
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				try {
					Log.d(TAG, "Write formatted tag");

					ndef.connect();
					if (!ndef.isWritable()) {
						Log.d(TAG, "Tag is not writeable");

						writerInterface.writeNdefNotWritable();

						return false;
					}

					if (ndef.getMaxSize() < rawMessage.toByteArray().length) {
						Log.d(TAG,
								"Tag size is too small, have "
										+ ndef.getMaxSize() + ", need "
										+ rawMessage.toByteArray().length);

						writerInterface.writeNdefTooSmall(
								rawMessage.toByteArray().length,
								ndef.getMaxSize());

						return false;
					}
					ndef.writeNdefMessage(rawMessage);
					if(formatReadOnly){
						try {
							boolean makeReadOnly = ndef.makeReadOnly();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					writerInterface.writeNdefSuccess(toHexString(tag.getId()));

					return true;
				} catch (Exception e) {
					writerInterface.writeNdefFailed(e);
				} finally {
					try {
						ndef.close();
					} catch (IOException e) {
						// ignore
					}
				}
			} else {
				writerInterface.writeNdefCannotWriteTech();
			}
			Log.d(TAG, "Cannot write formatted tag");
		}

		return false;
	}

	public int getMaxNdefSize(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		NdefFormatable format = NdefFormatable.get(tag);
		if (format != null) {
			Log.d(TAG, "Format tag with empty message");
			try {
				if (!format.isConnected()) {
					format.connect();
				}
				format.format(new NdefMessage(new NdefRecord[0]));
			} catch (Exception e) {
				Log.d(TAG, "Problem checking tag size", e);

				return -1;
			}
		}

		Ndef ndef = Ndef.get(tag);
		if (ndef != null) {
			try {
				if (!ndef.isConnected()) {
					ndef.connect();
				}

				if (!ndef.isWritable()) {
					Log.d(TAG, "Capacity of non-writeable tag is zero");

					writerInterface.writeNdefNotWritable();

					return 0;
				}

				int maxSize = ndef.getMaxSize();

				ndef.close();

				return maxSize;
			} catch (Exception e) {
				Log.d(TAG, "Problem checking tag size", e);
			}
		} else {
			writerInterface.writeNdefCannotWriteTech();
		}
		Log.d(TAG, "Cannot get size of tag");

		return -1;
	}

	public static interface NfcTagWriterInterface extends NfcDetectorInterface {
		/**
		 * 
		 * Create an NDEF message to be written when a tag is within range.
		 * 
		 * @return the message to be written
		 */

		public abstract NdefMessage createNdefMessage();

		/**
		 * 
		 * Writing NDEF message to tag failed.
		 * 
		 * @param e
		 *            exception
		 */

		public abstract void writeNdefFailed(Exception e);

		/**
		 * 
		 * Tag is not writable or write-public.
		 * 
		 */

		public abstract void writeNdefNotWritable();

		/**
		 * 
		 * Tag capacity is lower than NDEF message size.
		 * 
		 */

		public abstract void writeNdefTooSmall(int required, int capacity);

		/**
		 * 
		 * Unable to write this type of tag.
		 * 
		 */

		public abstract void writeNdefCannotWriteTech();

		/**
		 * 
		 * Successfully wrote NDEF message to tag.
		 * 
		 * @param sn
		 * 
		 */

		public abstract void writeNdefSuccess(String sn);

		public abstract byte[] createNtag216();
	}

	private static void clearBuffer(byte[] buffer) {
		if (buffer != null) {
			for (int i = 0; i < buffer.length; i++) {
				buffer[i] = 0;
			}
		}
	}

	@Override
	public void nTag213215216CardDetected(NTag213215216 tag,
			boolean isUsePassword , Intent intent) throws SmartCardException {
		if(isUsePassword){
			authenticatePwd(tag);
		}
		String id = "none";
		try {
			byte[] uid = tag.getUID();
			id = toHexString(uid);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		writeNtag216(tag,id);
		programPassword(tag);
		if(formatReadOnly){
			try {
				authenticatePwd(tag);
				tag.makeCardReadOnly();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void programPassword(NTag213215216 tag) throws SmartCardException {
		try {
			tag.programPWDPack(PWD, PACK);
			tag.enablePasswordProtection(true, PROTECTED_POSITION);
			tag.write(PASSWORD_MARK_POSITION, PASSWORD_MARK);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeNtag216(NTag213215216 tag, String id) throws SmartCardException {
		byte[] ntag216 = writerInterface.createNtag216();
		writeBytes(tag, ntag216,id);
	}

	private void writeBytes(NTag213215216 tag, byte[] ntag216, String id)
			throws SmartCardException {
		UriRecord uriRecord = new UriRecord("http://yjt.189.cn");
		NdefMessage message = new NdefMessage(uriRecord.getNdefRecord());
		byte[] headerData = message.toByteArray();
		int offset = tag.getFirstUserpage();
		byte[] headerCopy = new byte[headerData.length - 2];
		System.arraycopy(headerData, 2, headerCopy, 0, headerCopy.length);
		ByteArrayInputStream headerBuffer = new ByteArrayInputStream(headerCopy);
		byte[] data = new byte[ntag216.length - 3];
		System.arraycopy(ntag216, 3, data, 0, data.length);
		ByteArrayInputStream dataBuffer = new ByteArrayInputStream(data);
		try {
//			tag.clear();
			byte[] buffer = new byte[4];
			//3 is a ndef type.
			tag.write(offset++, new byte[]{3,(byte) headerData.length,headerData[0],headerData[1]});
			while (headerBuffer.read(buffer) > 0) {
				tag.write(offset++ , buffer);
				clearBuffer(buffer);
			}
			
			offset = 100;
			byte[] bs = new byte[]{(byte) ntag216.length,ntag216[0],ntag216[1],ntag216[2]};
			tag.write( offset++, bs);
			while (dataBuffer.read(buffer) > 0) {
				tag.write(offset++, buffer);
				clearBuffer(buffer);
			}
			writerInterface.writeNdefSuccess(id);
		} catch (SmartCardException e) {
			throw e;
		} catch (IOException e1) {
			e1.printStackTrace();
			writerInterface.writeNdefFailed(e1);
		}
	}

}
