package cn.yjt.oa.app.nfctools.fragment;

import org.ndeftools.Message;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.nfctools.NfcReader;
import cn.yjt.oa.app.nfctools.NfcReader.NfcReaderInterface;
import cn.yjt.oa.app.nfctools.NfcTagInfo;
import cn.yjt.oa.app.nfctools.NfcTagWriter;
import cn.yjt.oa.app.nfctools.NfcTagWriter.NfcTagWriterInterface;

public class NFCOtherFragment extends NFCBaseFragment implements
		NfcReaderInterface, NfcTagWriterInterface, OnClickListener {
	private RelativeLayout copyTag;
	private ImageView copyIcon;
	private TextView copyPrompt;
	private NfcReader nfcReader;
	private NfcTagWriter nfcTagWriter;

	private AlertDialog dialog;
	private NfcTagInfo tagInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.nfc_fragment_other, null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		copyTag = (RelativeLayout) view.findViewById(R.id.copy_tag);
		copyTag.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.copy_tag:
			showDialog();
			if (nfcReader != null) {
				if (!nfcReader.isDetecting()) {
					nfcReader.startDetecting();
				}
			}
			if (nfcTagWriter != null) {
				if (!nfcTagWriter.isDetecting()) {
					nfcReader.stopDetecting();
				}
			}
			break;

		default:
			break;
		}

	}

	private void showDialog() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.nfc_copytag_dialog, null);
		copyIcon = (ImageView) view.findViewById(R.id.copy_icon);
		copyPrompt = (TextView) view.findViewById(R.id.copy_prompt);
		copyIcon.setImageResource(R.drawable.copy_tag_read);
		copyPrompt.setText(R.string.nfc_copytag_read);
		Builder builder = AlertDialogBuilder.newBuilder(getActivity())
				.setTitle(R.string.nfc_copytag_title_one).setView(view)
				.setPositiveButton("取消", null);
		dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onNfcStateEnabled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNfcStateDisabled() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNfcStateChange(boolean enabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNfcFeatureNotFound() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTagLost() {
		// TODO Auto-generated method stub

	}

	@Override
	public void readNdefMessage(NfcTagInfo info) {
		tagInfo = info;
		dialog.setTitle(R.string.nfc_copytag_title_tow);
		copyIcon.setImageResource(R.drawable.copy_tag_write);
		copyPrompt.setText(R.string.nfc_copytag_write);
		Toast.makeText(getActivity(), R.string.nfc_copytag_title_tow,
				Toast.LENGTH_SHORT).show();
		if (nfcReader != null) {
			if (!nfcReader.isDetecting()) {
				nfcReader.stopDetecting();
			}
		}
		if (nfcTagWriter != null) {
			if (!nfcTagWriter.isDetecting()) {
				nfcTagWriter.startDetecting();
			}
		}
	}

	@Override
	public void readEmptyNdefMessage(NfcTagInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readNonNdefMessage(NfcTagInfo info) {
		// TODO Auto-generated method stub

	}

	public void setNfcReader(NfcReader nfcOtherReader) {
		this.nfcReader = nfcOtherReader;
	}

	@Override
	public NdefMessage createNdefMessage() {
		if (tagInfo != null) {
			Message message = tagInfo.getMessage();
			if (message != null) {
				return message.getNdefMessage();
			}
		}

		return null;
	}

	@Override
	public void writeNdefFailed(Exception e) {
		if (nfcTagWriter != null) {
			if (!nfcTagWriter.isDetecting()) {
				nfcTagWriter.stopDetecting();
			}
		}
		if (dialog != null) {
			dialog.setTitle(R.string.nfc_copytag_error);
			copyIcon.setImageResource(R.drawable.copy_tag_error);
			copyPrompt.setText(R.string.nfc_copytag_write_error);
		}
		Toast.makeText(getActivity(), "写入失败", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void writeNdefNotWritable() {
		if (nfcTagWriter != null) {
			if (!nfcTagWriter.isDetecting()) {
				nfcTagWriter.stopDetecting();
			}
		}
		if (dialog != null) {
			dialog.setTitle(R.string.nfc_copytag_error);
			copyIcon.setImageResource(R.drawable.copy_tag_error);
			copyPrompt.setText("标签不可写");
		}

		Toast.makeText(getActivity(), "标签不可写", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void writeNdefTooSmall(int required, int capacity) {
		if (nfcTagWriter != null) {
			if (!nfcTagWriter.isDetecting()) {
				nfcTagWriter.stopDetecting();
			}
		}
		if (dialog != null) {
			dialog.setTitle(R.string.nfc_copytag_error);
			copyIcon.setImageResource(R.drawable.copy_tag_error);
			copyPrompt.setText("标签容量太小");
		}
		Toast.makeText(getActivity(), "标签容量太小", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void writeNdefCannotWriteTech() {
		if (nfcTagWriter != null) {
			if (!nfcTagWriter.isDetecting()) {
				nfcTagWriter.stopDetecting();
			}
		}
		if (dialog != null) {
			dialog.setTitle(R.string.nfc_copytag_error);
			copyIcon.setImageResource(R.drawable.copy_tag_error);
			copyPrompt.setText("标签不支持该技术");
		}
		Toast.makeText(getActivity(), "标签不支持该技术", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void writeNdefSuccess(String sn) {
		if (nfcTagWriter != null) {
			if (!nfcTagWriter.isDetecting()) {
				nfcTagWriter.stopDetecting();
			}
		}
		if (dialog != null) {
			dialog.setTitle(R.string.nfc_copytag_write_sucess);
			copyIcon.setImageResource(R.drawable.copy_tag_sucess);
			copyPrompt.setText(R.string.nfc_copytag_write_sucess);
		}
		Toast.makeText(getActivity(), "写入成功", Toast.LENGTH_SHORT).show();

	}

	public void setNfcTagWriter(NfcTagWriter nfcTagWriter) {
		this.nfcTagWriter = nfcTagWriter;
	}

	@Override
	public void connectError(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readTag216(NfcTagInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] createNtag216() {
		//TODO
		return null;
	}

}
