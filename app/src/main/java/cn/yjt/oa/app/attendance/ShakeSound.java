package cn.yjt.oa.app.attendance;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.app.utils.StorageUtils;
import cn.yjt.oa.app.utils.Config;

public class ShakeSound {

	private static ShakeSound instance;
	private int shakeId;
	private int matchId;
	private int nomatchId;
	private SoundPool pool;

	public static ShakeSound getInstance() {
		if (instance == null) {
			instance = new ShakeSound();
		}
		return instance;
	}

	public void load() {
		pool = new SoundPool(2, AudioManager.STREAM_NOTIFICATION, 0);
		AssetFileDescriptor shakeOpenFd = null;
		AssetFileDescriptor matchOpenFd = null;
		AssetFileDescriptor nomatchOpenFd = null;
		try {
			shakeOpenFd = MainApplication.getAppContext().getAssets()
					.openFd("shake_sound_male.mp3");
			shakeId = pool.load(shakeOpenFd, 1);
			matchOpenFd = MainApplication.getAppContext().getAssets()
					.openFd("shake_match.mp3");
			matchId = pool.load(matchOpenFd, 1);
			nomatchOpenFd = MainApplication.getAppContext().getAssets()
					.openFd("shake_nomatch.mp3");
			nomatchId = pool.load(nomatchOpenFd, 1);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (shakeOpenFd != null) {
				try {
					shakeOpenFd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (matchOpenFd != null) {
				try {
					matchOpenFd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (nomatchOpenFd != null) {
				try {
					nomatchOpenFd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void release() {
		pool.release();
		pool = null;
	}

	public void playShake() {
		if (StorageUtils.getSystemSettings(MainApplication.getApplication())
				.getBoolean(Config.IS_OPEN_SHAKING_SOUND, true)) {
			play(shakeId);
		}
	}

	public void playMatch() {
		if (StorageUtils.getSystemSettings(MainApplication.getApplication())
				.getBoolean(Config.IS_OPEN_SHAKING_SOUND, true)) {
			play(matchId);
		}
	}

	public void playNomatch() {
		if (StorageUtils.getSystemSettings(MainApplication.getApplication())
				.getBoolean(Config.IS_OPEN_SHAKING_SOUND, true)) {
			play(nomatchId);
		}
	}

	private void play(int id) {
		if (pool != null) {
			pool.play(id, 1, 1, 1, 0, 1);
		}

	}

}
