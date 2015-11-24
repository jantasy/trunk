package cn.yjt.oa.app.notifications;

import cn.yjt.oa.app.ImageBrowser;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.ClipboardUtils;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.widget.CopyPopupWindow;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.NoticeInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest.Builder;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderProgressListener;
import cn.yjt.oa.app.widget.ProgressView;

public class NotificationDetailActivity extends TitleFragmentActivity implements
		ImageLoaderProgressListener{
	
	static final String TAG = "NotificationDetailActivity";
	private NoticeInfo info;
	private ImageView iconIv;
	private int imageWidth;
	private int imageHeight;
	private ProgressView progressView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*记录操作 0902*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_NOTIFICATION);

		setContentView(R.layout.notification_detail_layout);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		imageWidth = outMetrics.widthPixels;
		imageHeight = outMetrics.heightPixels;
		double scale =(double)imageHeight/imageWidth;
		imageWidth -= getResources().getDimensionPixelSize(
				R.dimen.notification_detail_padding);
		imageHeight = (int) (imageWidth*scale);
		initView();
	}
	
	
	
	

	private void initView() {
		getTitleFragment();

	    info = getIntent().getParcelableExtra("notice_info");
		if(info!=null){
			if (info.getIsRead() == 0) {
				info.setIsRead(1);
				uploadReadStatue(info);
			}
		}
		TextView timeTv = (TextView) findViewById(R.id.time);
		final TextView titleTv = (TextView) findViewById(R.id.title);
		final TextView contentTv = (TextView) findViewById(R.id.detail_content);
		TextView fromUserTv = (TextView) findViewById(R.id.from_user);
		iconIv = (ImageView) findViewById(R.id.icon);
		progressView = (ProgressView) findViewById(R.id.progressView);
		progressView.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(info.getImage())) {
			// AsyncRequest.getBitmap(info.getImage(), new Listener<Bitmap>() {
			//
			// @Override
			// public void onResponse(Bitmap arg0) {
			// int oldWidth = arg0.getWidth();
			// int oldHeight = arg0.getHeight();
			// float f = ((float)width)/oldWidth;
			// int dheight = (int)(f*oldHeight);
			// Bitmap bm = Bitmap.createScaledBitmap(arg0, width, dheight,
			// false);
			// iconIv.setImageBitmap(bm);
			// }
			//
			// @Override
			// public void onErrorResponse(InvocationError arg0) {
			//
			// }
			// });

			MainApplication.getImageLoader().get(info.getImage(),imageWidth,1, this,false);
		} else {
			iconIv.setVisibility(View.GONE);
		}
		String time = info.getCreateTime();
		try {
			Date date = BusinessConstants.parseTime(info.getCreateTime());
			SimpleDateFormat formart = new SimpleDateFormat("yy-MM-dd HH:mm");
			time = formart.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		timeTv.setText(time);
		titleTv.setText(info.getTitle());
		contentTv.setText(info.getContent());
		fromUserTv.setText(info.getFromUser());
        iconIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(info.getImage())) {
					String[] url = info.getImage().split(",");
					ImageBrowser.launch(NotificationDetailActivity.this, url, 0);

					/*记录操作 0903*/
					OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_NOTIFICATION_BIGIMAGE);
				}
			}
		});
		titleTv.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				CopyPopupWindow copyWindow = new CopyPopupWindow(NotificationDetailActivity.this);
				copyWindow.showAsDropDown(v, 0, dp2px(-5));
				copyWindow.setOnCopyListener(new CopyPopupWindow.OnCopyClickListener() {
					@Override
					public void onCopyClick() {
						ClipboardUtils.copyToClipboard(titleTv.getText().toString());
					}
				});
				return true;
			}
		});
        contentTv.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				CopyPopupWindow copyWindow = new CopyPopupWindow(NotificationDetailActivity.this);
				copyWindow.showAsDropDown(v, 0, dp2px(-20));
				copyWindow.setOnCopyListener(new CopyPopupWindow.OnCopyClickListener() {
					@Override
					public void onCopyClick() {
						ClipboardUtils.copyToClipboard(contentTv.getText().toString());
					}
				});
				return true;
			}
		});
	}
	
	

	private void getTitleFragment() {
		// TODO Auto-generated method stub
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setVisibility(View.GONE);
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
	public void uploadReadStatue(NoticeInfo info) {
		Builder builder = new Builder();
		String uploadUrl = "/notices/" + info.getId() + "/isread";
		builder.addQueryParameter("noticeId", String.valueOf(info.getId()));
		builder.setModule(uploadUrl);
		builder.setResponseType(new TypeToken<Response<String>>() {}.getType());
		builder.setResponseListener(new Listener<Response<String>>() {
			@Override
			public void onErrorResponse(InvocationError arg0) {
				
			}

			@Override
			public void onResponse(Response<String> arg0) {
				
			}
		});
		builder.build().put();
	}

	public static void launch(Context context, NoticeInfo ti) {
		Intent noticeIntent = new Intent(context,
				NotificationDetailActivity.class);
		noticeIntent.putExtra("notice_info", ti);
		noticeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(noticeIntent);
	}

	@Override
	public void onSuccess(final ImageContainer container) {
		LogUtils.d(TAG, "onSuccess");
		progressView.post(new Runnable() {

			@Override
			public void run() {
				progressView.setVisibility(View.GONE);
				Bitmap bitmap = container.getBitmap();
				int oldWidth = bitmap.getWidth();
				int oldHeight = bitmap.getHeight();
				float f = ((float) imageWidth) / oldWidth;
				int dheight = (int) (f * oldHeight);
				Bitmap bm = null;
				try {
					bm = Bitmap.createScaledBitmap(bitmap, imageWidth, dheight, false);
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					System.gc();
					MainApplication.getImageLoader().getImageCache().trim();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					bm = Bitmap.createScaledBitmap(bitmap, imageWidth, dheight, false);
				}

				iconIv.setImageBitmap(bm);

			}
		});
	}

	@Override
	public void onError(ImageContainer container) {
		LogUtils.d(TAG, "onError");
		progressView.post(new Runnable() {
			
			@Override
			public void run() {
				progressView.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onWait(ImageContainer container) {
		LogUtils.d(TAG, "onWait");
		progressView.post(new Runnable() {
			
			@Override
			public void run() {
				progressView.setWaiting(true);
				progressView.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void onStarted(final ImageContainer container) {
		LogUtils.d(TAG, "onStarted");
		progressView.post(new Runnable() {
			
			@Override
			public void run() {
				progressView.setVisibility(View.VISIBLE);
				progressView.setMax((int) container.getMax());
				progressView.setProgress((int) container.getProgress());
			}
		});
	}

	@Override
	public void onStart(ImageContainer container) {
		LogUtils.d(TAG, "onStart");
		progressView.setVisibility(View.VISIBLE);
		progressView.setWaiting(false);
	}

	@Override
	public void onProgress(final ImageContainer container) {
		LogUtils.d(TAG, "onProgress");
		progressView.post(new Runnable() {

			@Override
			public void run() {
				progressView.setMax((int) container.getMax());
				progressView.setProgress((int) container.getProgress());
			}
		});
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}


}
