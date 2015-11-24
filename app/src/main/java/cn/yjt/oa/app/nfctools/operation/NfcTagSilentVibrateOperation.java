package cn.yjt.oa.app.nfctools.operation;

import android.content.Context;
import android.media.AudioManager;

public class NfcTagSilentVibrateOperation extends NfcTagOperation {

	@Override
	public void excuteOperation() {
		AudioManager audioManager  = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
	}

}
