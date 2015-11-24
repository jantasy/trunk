package cn.yjt.oa.app.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import cn.yjt.oa.app.MainApplication;

public class MediaPlayerManager {
	private Context context;
	private MediaPlayer mediaPlayer;
	private static MediaPlayerManager mediaPlayerManager;

	public MediaPlayerManager(Context context) {
		mediaPlayer = new MediaPlayer();
		this.context = context;
	}

	public static MediaPlayerManager getInstance() {
		if (mediaPlayerManager == null) {
			mediaPlayerManager = new MediaPlayerManager(MainApplication.getAppContext());
		}
		return mediaPlayerManager;
	}

	public void play() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.reset();
		Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
				context, RingtoneManager.TYPE_NOTIFICATION);
		try {
			if (defaultRingtoneUri != null) {

				mediaPlayer.setDataSource(context, defaultRingtoneUri);

				AudioManager mAudioManager = (AudioManager) context
						.getSystemService(Context.AUDIO_SERVICE);
				// 铃声音量
				float streamMaxVolume = mAudioManager
						.getStreamMaxVolume(AudioManager.STREAM_RING);
				float streamVolume = mAudioManager
						.getStreamVolume(AudioManager.STREAM_RING);
				float volume = streamVolume / streamMaxVolume;

				mediaPlayer.setVolume(volume, volume);
				mediaPlayer.prepare();

				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						mediaPlayer.release();

					}
				});
				mediaPlayer.start();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			if(mediaPlayer != null){
				try {
					mediaPlayer.release();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				mediaPlayer = null;
			}
		} finally{
			
		}
	}

}
