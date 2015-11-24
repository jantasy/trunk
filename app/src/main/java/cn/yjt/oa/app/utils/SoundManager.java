package cn.yjt.oa.app.utils;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import cn.yjt.oa.app.R;

public class SoundManager {
	private SoundPool soundPool;
	private Context context;
	private static SoundManager soundManager;
	private HashMap<Integer, Integer> soundPoolMap; 
	public static final int NOTICE_SOUND=1;  
	

	public SoundManager(Context context) {
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap=new HashMap<Integer, Integer>();
		soundPoolMap.put(NOTICE_SOUND, soundPool.load(context,R.raw.umeng_push_notification_default_sound, 1));
		this.context = context;
	}

	public static SoundManager getInstance(Context context) {
		if (soundManager == null) {
			soundManager = new SoundManager(context);
		}

		return soundManager;

	}

	
	public void playSound(){
		AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_RING);
		float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_RING);
		float volume = streamVolumeCurrent / streamVolumeMax;
		

		/* 使用正确音量播放声音 */
		soundPool.play(soundPoolMap.get(NOTICE_SOUND), volume, volume, 1, 0, 1f);
		
	}
	


}
