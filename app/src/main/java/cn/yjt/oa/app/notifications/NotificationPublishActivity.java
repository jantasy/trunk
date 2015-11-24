package cn.yjt.oa.app.notifications;

import io.luobo.common.http.FileClient;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.http.ProgressListener;
import io.luobo.common.json.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.NoticeCreateInfo;
import cn.yjt.oa.app.beans.NoticeInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.http.FileClientFactory;
import cn.yjt.oa.app.imageloader.ImageLoader;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.picturepicker.DefaultPicturePicker;
import cn.yjt.oa.app.picturepicker.PicturePicker;
import cn.yjt.oa.app.utils.BitmapUtils;
import cn.yjt.oa.app.utils.FileUtils;

public class NotificationPublishActivity extends TitleFragmentActivity
		implements OnClickListener {
	static final String TAG = "NotificationPublishActivity";
	
	public static final int REQUEST_CAMERA = 11;
	public static final int REQUEST_IMAGE_GALLERY = 12;
	private PicturePicker picker;
	private File tempFile;
	private Uri uri;
	private EditText title;
	private ImageView imageView;
	private EditText content;
	private DisplayMetrics outMetrics;
	private ProgressDialog progressDialog;
	private FileClient client;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_notifaction_publish);
		outMetrics = new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay()
				.getMetrics(outMetrics);
		getLeftbutton().setImageResource(R.drawable.navigation_back);

		title = (EditText) findViewById(R.id.noti_title);
		imageView = (ImageView) findViewById(R.id.noti_image);
		content = (EditText) findViewById(R.id.noti_content);

		findViewById(R.id.noti_publish_btn).setOnClickListener(this);
		imageView.setOnClickListener(this);
		picker= new DefaultPicturePicker();
		client = FileClientFactory.createSingleThreadFileClient(this);
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	public static void launch(Context context,int requestCode) {
		Intent intent = new Intent(context, NotificationPublishActivity.class);
		((Activity)context).startActivityForResult(intent, requestCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.noti_publish_btn:
			hideSoftInput();
			publish();
			break;
		case R.id.noti_image:
			hideSoftInput();
			addImage();
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
//			if (requestCode == REQUEST_IMAGE_GALLERY) {
//				this.uri = data.getData();
//				updateImage();
//			} else if (requestCode == REQUEST_CAMERA) {
//				this.uri = Uri.fromFile(tempFile);
//				updateImage();
//			}
			if (picker.isPickerResult(requestCode)) {
				this.uri = picker.getPicture(requestCode, resultCode, data);
				if(uri != null){
					updateImage();
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		deleteTempFile();
		super.onDestroy();
	}

	private void deleteTempFile() {
		if (this.uri != null) {
			if ("file".equals(uri.getScheme())) {
				new File(uri.getPath()).delete();
			}
		}
	}

	private void addImage() {
		picker.pickPicture(this);
	}

	private boolean hasUploadFile() {
		if (this.uri != null) {
			if ("file".equals(uri.getScheme()) || "content".equals(uri.getScheme())) {
				return true;
			}
		}
		return false;
	}

	private void publish() {
		/*
		 * uploadFile(
		 * BusinessConstants.buildUrl(AsyncRequest.MODULE_UPLOAD_FILE), "file",
		 * recordFile, VOICE_INDEX)
		 */
		if (hasUploadFile()) {
			uploadFile();
		}else{
			putRequest(null);
		}
	}

	private void uploadFile() {
		progressDialog = ProgressDialog.show(this, null, "正在上传图片...");
		AsyncRequest.upload(this, uri, BusinessConstants.buildUrl(AsyncRequest.MODULE_UPLOAD_IMAGE), "image", 720, 1280, true, new ProgressListener<Response<String>>() {

			@Override
			public void onError(InvocationError arg0) {
				Log.d(TAG, "onErrorResponse arg0:"+arg0.getMessage(),arg0);
				runOnUiThread(new Runnable() {
					public void run() {
						dismissProgressDialog();
						Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
					}
				});
				
			}

			@Override
			public void onFinished(final Response<String> arg0) {
				runOnUiThread(new Runnable() {
					public void run() {
						String url = arg0.getPayload();
						putRequest(url);
						Log.d(TAG, "onResponse url:"+url);
					}
				});
			}

			@Override
			public void onProgress(long arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStarted() {
				// TODO Auto-generated method stub
				
			}
			
		},client);
		
	}
	
	private void putRequest(String imageUrl){ 
		if(progressDialog == null){
			progressDialog = ProgressDialog.show(this, null, "正在发布公告...");
		}else{
			progressDialog.setMessage("正在发布公告...");
		}
		AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
		requestBuilder.setModule(AsyncRequest.MODULE_NOTICE);
		NoticeCreateInfo info = new NoticeCreateInfo();
		info.setContent(content.getText().toString());
		info.setTitle(title.getText().toString());
		info.setImage(imageUrl);
		requestBuilder.setRequestBody(info);
		requestBuilder.setResponseType(new TypeToken<Response<NoticeInfo>>() {
		}.getType());
		requestBuilder.setResponseListener(new Listener<Response<NoticeInfo>>() {

			@Override
			public void onErrorResponse(InvocationError arg0) {
				Log.d(TAG, "onErrorResponse:"+arg0.toString());
				Toast.makeText(getApplicationContext(), "发布失败", Toast.LENGTH_SHORT).show();
				dismissProgressDialog();
			}

			@Override
			public void onResponse(Response<NoticeInfo> arg0) {
				dismissProgressDialog();
				if(arg0.getCode()==0){
					Toast.makeText(getApplicationContext(), "发布成功", Toast.LENGTH_SHORT).show();
					setResult(RESULT_OK);
					finish();
				}else{
					Toast.makeText(getApplicationContext(), "发布失败", Toast.LENGTH_SHORT).show();
				}
				
				
			}
		});
		requestBuilder.build().post();
	}

	private File createTempFile() {
		return FileUtils.createFileInUserFolder(UUID.randomUUID() + ".jpg");
	}
	
	private void dismissProgressDialog(){
		if(progressDialog!=null&&progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}


	private void updateImage() {
		loadBitmap(imageView);
	}

	private void loadBitmap(final ImageView imageView) {
//		new AsyncTask<Void, Void, Bitmap>() {
//
//			protected void onPreExecute() {
//				imageView.setImageBitmap(null);
//			}
//
//			@Override
//			protected Bitmap doInBackground(Void... params) {
//				return getBitmap();
//			}
//
//			@Override
//			protected void onPostExecute(Bitmap result) {
//				if (result != null) {
//					imageView.setScaleType(ScaleType.FIT_XY);
//					imageView.setOnClickListener(null);
//					imageView.setLayoutParams(new LayoutParams(
//							LayoutParams.MATCH_PARENT,
//							(int) ((double) imageView.getWidth()
//									/ result.getWidth() * result.getHeight())));
//					imageView.setImageBitmap(result);
//				}else{
//					Toast.makeText(getApplicationContext(), "图片获取失败", Toast.LENGTH_SHORT).show();
//				}
//
//			}
//
//		}.execute();
		if(uri == null){
			return;
		}
		MainApplication.getImageLoader().get(uri.toString(), MainApplication.getDisplayMetrics().widthPixels, 1, new ImageLoader.ImageLoaderListener() {
			
			@Override
			public void onSuccess(ImageContainer container) {
				Bitmap bitmap = container.getBitmap();
				if (bitmap != null) {
					imageView.setScaleType(ScaleType.FIT_XY);
					imageView.setOnClickListener(null);
					imageView.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT,
							(int) ((double) imageView.getWidth()
									/ bitmap.getWidth() * bitmap.getHeight())));
					imageView.setImageBitmap(bitmap);
				}else{
					Toast.makeText(getApplicationContext(), "图片获取失败", Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void onError(ImageContainer container) {
				Toast.makeText(getApplicationContext(), "图片获取失败", Toast.LENGTH_SHORT).show();				
			}
		}, false);
	}

	private Bitmap getBitmap() {

		if ("file".equals(uri.getScheme())) {

			Bitmap image = BitmapUtils
					.decodeFile(
							uri.getPath(),
							imageView.getWidth(),
							(int) ((double) imageView.getWidth()
									/ outMetrics.widthPixels * outMetrics.heightPixels),
							Config.ARGB_8888);
			return image;
		} else if ("content".equals(uri.getScheme())) {
			try {
				File tempFile = createTempFile();
				InputStream inputStream = this.getContentResolver()
						.openInputStream(uri);
				if(inputStream == null){
					return null;
				}
				
				boolean copy = FileUtils.copy(inputStream, tempFile);
				if (copy) {
					Bitmap image = BitmapUtils
							.decodeFile(
									tempFile.getAbsolutePath(),
									imageView.getWidth(),
									(int) ((double) imageView.getWidth()
											/ outMetrics.widthPixels * outMetrics.heightPixels),
									Config.ARGB_4444);
					this.uri = Uri.fromFile(tempFile);
					return image;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
