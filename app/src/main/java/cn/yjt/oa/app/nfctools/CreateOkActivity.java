package cn.yjt.oa.app.nfctools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import cn.yjt.oa.app.nfctools.fragment.CreateOkFragment;
import cn.yjt.oa.app.nfctools.fragment.NFCBaseFragment;

public class CreateOkActivity extends NFCBaseActivity {

	private CreateOkFragment createOkFragment;
	private NfcTagWriter nfcTagWriter;

	@Override
	protected NFCBaseFragment getFragment() {
		if (createOkFragment == null) {
			createOkFragment = new CreateOkFragment();
			createOkFragment.setArguments(getIntent().getExtras());
		}
		return createOkFragment;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			createOkFragment.startAnimation();
		}
	}

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		nfcTagWriter = new NfcTagWriter(this, createOkFragment);
		nfcTagWriter.setFormatReadOnly(getIntent().getBooleanExtra("read_only", false));
		nfcTagWriter.create();
	}

	@Override
	protected void onResume() {
		nfcTagWriter.resume();
		if (!nfcTagWriter.isDetecting()) {
			nfcTagWriter.startDetecting();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		nfcTagWriter.pause();
		if (nfcTagWriter.isDetecting()) {
			nfcTagWriter.stopDetecting();
		}
		super.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		nfcTagWriter.newIntent(intent);

	}

	/**
	 * 
	 * @param activity
	 * @param record
	 * @param saveRecord
	 * @param delayFinishAfterWriteSucess If you gave -1,it won't finish.
	 */
	public static void launchForResult(Activity activity, NfcTagOperationRecord record, boolean saveRecord,
			long delayFinishAfterWriteSucess, int requestCode) {
		Intent intent = new Intent(activity, CreateOkActivity.class);
		intent.putExtra("nfcTagRecord", record);
		intent.putExtra("save_record", saveRecord);
		intent.putExtra("delay_finish", delayFinishAfterWriteSucess);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 
	 * @param activity
	 * @param record
	 * @param saveRecord
	 * @param delayFinishAfterWriteSucess If you gave -1,it won't finish.
	 */
	public static void launchForResult(Activity activity, NfcTagOperationRecord record, boolean saveRecord,
			long delayFinishAfterWriteSucess, int requestCode, boolean readOnly) {
		Intent intent = new Intent(activity, CreateOkActivity.class);
		intent.putExtra("nfcTagRecord", record);
		intent.putExtra("save_record", saveRecord);
		intent.putExtra("delay_finish", delayFinishAfterWriteSucess);
		intent.putExtra("read_only", readOnly);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 
	 * @param fragment
	 * @param record
	 * @param saveRecord
	 * @param delayFinishAfterWriteSucess If you gave -1,it won't finish.
	 */
	public static void launchForResult(Fragment fragment, NfcTagOperationRecord record, boolean saveRecord,
			long delayFinishAfterWriteSucess, int requestCode) {
		Intent intent = new Intent(fragment.getActivity(), CreateOkActivity.class);
		intent.putExtra("nfcTagRecord", record);
		intent.putExtra("save_record", saveRecord);
		intent.putExtra("delay_finish", delayFinishAfterWriteSucess);
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void launchForResult(Activity activity, NfcTagOperationRecord record, boolean saveRecord,
			long delayFinishAfterWriteSucess, int requestCode, boolean readOnly, long areaId) {
		Intent intent = new Intent(activity, CreateOkActivity.class);
		intent.putExtra("nfcTagRecord", record);
		intent.putExtra("save_record", saveRecord);
		intent.putExtra("delay_finish", delayFinishAfterWriteSucess);
		intent.putExtra("read_only", readOnly);
		intent.putExtra("area_id", areaId);
		activity.startActivityForResult(intent, requestCode);

	}

}
