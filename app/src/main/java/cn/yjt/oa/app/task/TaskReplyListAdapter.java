package cn.yjt.oa.app.task;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.yjt.oa.app.ImageBrowser;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.ReplyInfo;
import cn.yjt.oa.app.beans.TaskInfo;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.imageloader.ImageLoader;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.utils.FileLoader;
import cn.yjt.oa.app.utils.FileLoader.FileContainer;
import cn.yjt.oa.app.utils.FileLoader.FileLoaderListener;
import cn.yjt.oa.app.utils.OperaEventUtils;

public class TaskReplyListAdapter extends BaseAdapter {

	static final String TAG = "TaskReplyListAdapter";

	public static final int VIEW_TYPE_TASK_CONTENT = 0;
	public static final int VIEW_TYPE_TASK_REPLY = 1;

	private final int VOICE_PROGRESS = 3;
	private final int VOICE_STOP = 4;

	private Context context;
	private LayoutInflater inflater;
	private List<ReplyInfo> taskList;
	private Spannable content;
	private TaskInfo info;
	private MediaPlayer player;
	private TextView duration;

	private int durationMax;
	
	private AnimationDrawable voiceAnimationDrawable;

	private boolean mPlaying = false;

	private MediaStatus mStatus = MediaStatus.INIT;
//	private Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case VOICE_PROGRESS:
//				duration.setText("" + msg.arg1 / 1000);
//				break;
//			case VOICE_STOP:
//				duration.setText("" + durationMax);
//				break;
//
//			default:
//				break;
//			}
//		};
//	};

	enum MediaStatus {
		INIT, PREPARE, START, PAUSE, STOP, COMPELETE
	}

	private int imageWidth;

	private ImageLoader imageLoader;
	private FileLoader fileLoader;

	public TaskReplyListAdapter(Context context, List<ReplyInfo> taskList,
			TaskInfo info) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.taskList = taskList;

		Spannable spannedText = Spannable.Factory.getInstance().newSpannable(
				Html.fromHtml(info.getContent().replace("\n", "<br>") + ""));
		Spannable processedText = URLSpanNoUnderline
				.removeUnderlines(spannedText);
		this.content = processedText;
		this.info = info;
		imageWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 150, context.getResources()
						.getDisplayMetrics());
		imageLoader = MainApplication.getImageLoader();
		fileLoader = new FileLoader(context);
	}

	@Override
	public int getCount() {
		return 1 + taskList.size();
	}

	@Override
	public Object getItem(int position) {
		if (getItemViewType(position) == VIEW_TYPE_TASK_CONTENT) {
			return content;
		} else {
			return taskList.get(position - 1);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		if (position == 0)
			return VIEW_TYPE_TASK_CONTENT;
		else
			return VIEW_TYPE_TASK_REPLY;
	}

	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			if (VIEW_TYPE_TASK_CONTENT == getItemViewType(position)) {
				view = inflater.inflate(R.layout.task_detail_task_content,
						null, false);
			} else {
				view = inflater.inflate(R.layout.task_reply_item_layout, null,
						false);
			}
		}

		if (VIEW_TYPE_TASK_CONTENT == getItemViewType(position)) {
			TextView contentText = (TextView) view
					.findViewById(R.id.task_content);
			duration = (TextView) view.findViewById(R.id.voice_duration);
			ProgressBar progress = (ProgressBar) view
					.findViewById(R.id.voice_progress);
			Spannable content = (Spannable) getItem(position);
			contentText.setText(content);

			ImageView image1 = (ImageView) view.findViewById(R.id.task_image1);
			ImageView image2 = (ImageView) view.findViewById(R.id.task_image2);
			ImageView image3 = (ImageView) view.findViewById(R.id.task_image3);
			ImageView image4 = (ImageView) view.findViewById(R.id.task_image4);
			final ImageView voice = (ImageView) view
					.findViewById(R.id.task_voice);
			final View voiceLayout = view.findViewById(R.id.task_voice_layout);
			image1.setVisibility(View.GONE);
			image2.setVisibility(View.GONE);
			image3.setVisibility(View.GONE);
			image4.setVisibility(View.GONE);
			voiceLayout.setVisibility(View.GONE);
			progress.setVisibility(View.GONE);

			String imageUrl = info.getImageUrl();
			final String voiceUrl = info.getVoiceUrl();
			if (!TextUtils.isEmpty(imageUrl)) {
				String[] split = imageUrl.split(",");
				if (split != null && split.length > 0) {

					for (int i = 0; i < split.length; i++) {
						switch (i) {
						case 0:
							loadImage(image1, split[i]);
							setOnClickListener(image1, i, split);
							break;
						case 1:
							loadImage(image2, split[i]);
							setOnClickListener(image2, i, split);
							break;
						case 2:
							loadImage(image3, split[i]);
							setOnClickListener(image3, i, split);
							break;
						case 3:
							loadImage(image4, split[i]);
							setOnClickListener(image4, i, split);

						default:
							break;
						}
					}
				}
			}

			if (!TextUtils.isEmpty(voiceUrl)) {
				progress.setVisibility(View.VISIBLE);
				loadFile(voice, voiceLayout, progress, voiceUrl);

			}

		} else {
			ReplyInfo info = (ReplyInfo) getItem(position);

			ImageView colorMark = (ImageView) view
					.findViewById(R.id.reply_color_mark);
			ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
			int color = info.getMark();
			if (color == TaskDetailActivity.COLOR_MARK_RED_TO_SERVER) {
				drawable.getPaint().setColor(
						TaskDetailActivity.COLOR_MARK_RED | 0xFF000000);
				colorMark.setBackgroundDrawable(drawable);
			} else if (color == TaskDetailActivity.COLOR_MARK_YELLOW_TO_SERVER) {
				drawable.getPaint().setColor(
						TaskDetailActivity.COLOR_MARK_YELLOW | 0xFF000000);
				colorMark.setBackgroundDrawable(drawable);
			} else if (color == TaskDetailActivity.COLOR_MARK_GREEN_TO_SERVER) {
				drawable.getPaint().setColor(
						TaskDetailActivity.COLOR_MARK_GREEN | 0xFF000000);
				colorMark.setBackgroundDrawable(drawable);
			} else {
				colorMark.setBackgroundDrawable(null);
			}

			TextView replyFromUser = (TextView) view
					.findViewById(R.id.reply_from_user);
			replyFromUser.setText(info.getFromUser().getName());

			TextView replyDate = (TextView) view
					.findViewById(R.id.reply_creation_date);
			Date date = null;
			if (!TextUtils.isEmpty(info.getReplyTime())) {
				try {
					date = BusinessConstants.parseTime(info.getReplyTime());
				} catch (ParseException e) {
					date = null;
				}
			}
			TaskDateCompareUtils.setComparedDateForView(context, date,
					replyDate);

			TextView replyContent = (TextView) view
					.findViewById(R.id.reply_content);
			replyContent.setText(info.getContent());
		}

		return view;
	}

	private void setOnClickListener(ImageView imageView, final int index,
			final String[] urls) {


		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*记录操作 1012*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_TASK_BIGIMAGE);

				ImageBrowser.launch(context, urls, index);
			}
		});
	}

	private void loadImage(final ImageView imageView, final String url) {
		imageView.setVisibility(View.VISIBLE);
		imageView.setImageResource(R.drawable.default_image);
		imageLoader.get(url, imageWidth, imageWidth, new ImageLoaderListener() {

			@Override
			public void onSuccess(final ImageContainer container) {
				if (url.equals(container.getUrl())) {
					imageView.post(new Runnable() {

						@Override
						public void run() {
							imageView.setImageBitmap(container.getBitmap());
						}
					});
				}

			}

			@Override
			public void onError(ImageContainer container) {
				// TODO Auto-generated method stub

			}

		});
		// imageLoader.get(url, new ImageListener() {
		//
		// @Override
		// public void onErrorResponse(VolleyError arg0) {
		// }
		//
		// @Override
		// public void onResponse(final ImageContainer arg0, boolean arg1) {
		// if (imageUrl.equals(imageView.getTag())) {
		// Bitmap bitmap = arg0.getBitmap();
		// if (bitmap != null) {
		// imageView.setImageBitmap(bitmap);
		// }
		// }
		// }
		// }, imageWidth, imageWidth);
	}

	private void loadFile(final ImageView voice, final View voiceLayout,
			final ProgressBar progress, final String url) {
		voice.setVisibility(View.VISIBLE);
		voice.setImageResource(R.drawable.task_voice_0);
		fileLoader.get(url, new FileLoaderListener() {

			@Override
			public void onWait(FileContainer container) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStarted(FileContainer container) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStart(FileContainer container) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgress(FileContainer container) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(final FileContainer container) {
				if (url.equals(container.getUrl())) {
					Log.d(TAG, "onSuccess");
					progress.setVisibility(View.GONE);
					voiceLayout.setVisibility(View.VISIBLE);

						player = new MediaPlayer();
						try {
							player.setDataSource(container.getFile()
									.getAbsolutePath());
							player.prepare();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						durationMax = player.getDuration() / 1000;
						duration.setText("" + durationMax+"\"");

						if (player != null) {
							player.reset();
						}
						voiceLayout.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								if (mStatus == MediaStatus.START) {
									voice.setImageResource(R.drawable.task_voice_0);
									stopPlaying();
								} else if (mStatus == MediaStatus.INIT
										|| mStatus == MediaStatus.COMPELETE
										|| mStatus == MediaStatus.STOP) {

									voice.setImageResource(R.drawable.task_voice);
									voiceAnimationDrawable=(AnimationDrawable) voice.getDrawable();
									startPlaying(container.getFile()
											.getAbsolutePath(), voice);
								}

							}
						});
					}

			}

			@Override
			public void onError(FileContainer container) {
				// TODO Auto-generated method stub

			}

		});
	}

	public void startPlaying(String path, final ImageView voice) {

		player = new MediaPlayer();
		try {
			player.setDataSource(path);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				mStatus = MediaStatus.COMPELETE;
				mPlaying = false;
//				duration.setText("" + durationMax+"\"");
				if(voiceAnimationDrawable.isRunning()){
					voiceAnimationDrawable.stop();
				}
				voiceAnimationDrawable=null;
				voice.setImageResource(R.drawable.task_voice_0);
			}
		});
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				if(voiceAnimationDrawable!=null){
					voiceAnimationDrawable.start();
				}
				mPlaying = true;
				mStatus = MediaStatus.START;
			}
		});

		mStatus = MediaStatus.PREPARE;
		try {
			player.prepare();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		player.start();

//		handler.post(new Runnable() {
//
//			@Override
//			public void run() {
//				if (mPlaying) {
//					Message msg = handler.obtainMessage();
//					msg.what = VOICE_PROGRESS;
//					msg.arg1 = player.getCurrentPosition();
//					handler.sendMessage(msg);
//					handler.postDelayed(this, 500);
//				} else {
//					handler.sendEmptyMessage(VOICE_STOP);
//				}
//			}
//		});
	}

	public void stopPlaying() {
		if (mPlaying) {
			player.stop();
			player.release();
			mPlaying = false;
			mStatus = MediaStatus.STOP;

		}
	}

	public void release() {
		if (player != null) {
			stopPlaying();

		}
	}

}
