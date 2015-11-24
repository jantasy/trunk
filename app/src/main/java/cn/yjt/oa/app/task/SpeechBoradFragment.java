package cn.yjt.oa.app.task;

import java.io.File;
import java.io.IOException;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.task.RecorderButton.Mode;
import cn.yjt.oa.app.task.RecorderButton.OnModeChangedListener;
import cn.yjt.oa.app.task.RecorderButton.OnPressListener;
import cn.yjt.oa.app.task.RecorderButton.ProgressCallback;

public class SpeechBoradFragment extends Fragment implements OnPressListener,
		ProgressCallback, OnClickListener, OnModeChangedListener {

	private ImageView progressLeft;
	private ImageView progressRight;
	private RecorderButton recorderButton;
	private AnimationDrawable animLeft;
	private AnimationDrawable animRight;
	private View root;
	private TextView recordTip;
	private MediaRecorder recorder;

	private MediaPlayer player;
	private TextView recordTime;

	private long recordStart;

	private File recordFile;
	private Mode currentMode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.speech_borad, container, false);
		progressLeft = (ImageView) root.findViewById(R.id.progress_left);
		progressRight = (ImageView) root.findViewById(R.id.progress_right);
		recorderButton = (RecorderButton) root.findViewById(R.id.progress_btn);
		recordTime = (TextView) root.findViewById(R.id.record_time);
		recordTip = (TextView) root.findViewById(R.id.record_tip);
		rerecordBtn = root.findViewById(R.id.rerecord_btn);

		animLeft = (AnimationDrawable) progressLeft.getBackground();
		animRight = (AnimationDrawable) progressRight.getBackground();

		recorderButton.setOnPressListener(this);
		recorderButton.setProgressCallback(this);
		recorderButton.setOnModeChangedListener(this);
		rerecordBtn.setOnClickListener(this);
		if (currentMode != null) {
			setMode(currentMode);
			if (currentMode == Mode.PLAY) {
				onPlayMode();
			}
		}

		return root;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	@Override
	public void onDown(Mode mode) {
		System.out.println("onUp(Mode mode):" + mode);
		if (mode == Mode.RECORD) {

			animLeft.start();
			animRight.start();
			recordTime.setVisibility(View.VISIBLE);
			recordTime.post(recordTimeRun);

			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			if(VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD_MR1){
				recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			}else{
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			}
			recorder.setOutputFile(recordFile.getAbsolutePath());
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			try {
				recorder.prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}
			recorder.start();
			recordStart = System.currentTimeMillis();
			setMode(Mode.RECORDING);
		}
	}

	@Override
	public void onUp(Mode mode) {
		System.out.println("onUp(Mode mode):" + mode);
		if (mode == Mode.RECORDING) {

			recordTime.removeCallbacks(recordTimeRun);
			recordTime.setVisibility(View.GONE);

			stopAnim();
			try {

				recorder.stop();
				recorder.release();
				recorder = null;
			} catch (Exception e) {
				setMode(Mode.RECORD);
				return;
			}

			if (recordFile.exists()) {
				setMode(Mode.PLAY);
				onPlayMode();
				refreshNew();
				long audioLength = System.currentTimeMillis() -recordStart;
				recorderButton.setAudioLength(audioLength);
			}
		} else if (mode == Mode.PLAY) {
			startPlaying();
		} else if (mode == Mode.PLAYING) {
			stopPlaying();
		}
	}

	private void refreshNew() {
		FragmentActivity activity = getActivity();
		if(activity instanceof NewInterface){
			NewInterface n = (NewInterface) activity;
			n.refreshNew();
		}
	}

	private void onPlayMode() {
		rerecordBtn.setVisibility(View.VISIBLE);
		progressLeft.setVisibility(View.GONE);
		progressRight.setVisibility(View.GONE);
	}

	private void setMode(Mode mode) {
		recorderButton.setMode(mode);
		currentMode = mode;
	}

	private void stopAnim() {
		if (animRight != null) {
			animRight.stop();
			animRight.selectDrawable(0);
		}
		if (animLeft != null) {
			animLeft.stop();
			animLeft.selectDrawable(0);
		}
	}

	private Runnable recordTimeRun = new Runnable() {

		@Override
		public void run() {
			if (recordStart != 0) {
				long recordedTime = System.currentTimeMillis() - recordStart;
				recordTime.setText(recordedTime / 1000 + "\"");
				recordTime.postDelayed(recordTimeRun, 25);
			} else {
				recordTime.removeCallbacks(recordTimeRun);
			}
		}
	};
	private View rerecordBtn;

	public void setRecorderFile(File file) {
		this.recordFile = file;
	}

	private void startPlaying() {
		player = new MediaPlayer();
		player.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				recorderButton.setMax(player.getDuration());
				setMode(Mode.PLAYING);
			}
		});
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				setMode(Mode.PLAY);
				player = null;
			}
		});
		try {
			player.setDataSource("file://" + recordFile.getAbsolutePath());
			player.prepare();
//			TrackInfo[] trackInfo = player.getTrackInfo();
//			if(trackInfo!=null&&trackInfo.length > 0){
//				TrackInfo trackInfo2 = trackInfo[0];
//				MediaFormat format = trackInfo2.getFormat();
//				System.out.println("MediaFormat:"+format);
//				
//			}
			player.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 停止播放
	private void stopPlaying() {
		if (player != null) {

			setMode(Mode.PLAY);
			player.stop();
			player.release();
			player = null;
		}
	}

	@Override
	public float getProgress() {
		return player.getCurrentPosition();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rerecord_btn:
			AlertDialogBuilder.newBuilder(getActivity()).setMessage("是否确定重录").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					onRecordMode();
					stopAnim();
					stopPlaying();
					setMode(Mode.RECORD);
					if(recordFile != null&&recordFile.exists()){
						recordFile.delete();
						refreshNew();
					}
					
				}
			}).setNegativeButton("取消", null).show();
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onDestroyView() {
		stopPlaying();
		super.onDestroyView();
	}

	private void onRecordMode() {
		rerecordBtn.setVisibility(View.GONE);
		progressLeft.setVisibility(View.VISIBLE);
		progressRight.setVisibility(View.VISIBLE);
		animLeft.stop();
		animRight.stop();
	}

	@Override
	public void onChanged(Mode mode) {
		if (mode == Mode.RECORD) {
			recordTip.setText("长按开始语音");
		} else if (mode == Mode.RECORDING) {
			recordTip.setText("松开结束录音");
		} else if (mode == Mode.PLAY) {
			recordTip.setText("点击播放");
		} else if (mode == Mode.PLAYING) {
			recordTip.setText("点击停止");
		}
	}

}
