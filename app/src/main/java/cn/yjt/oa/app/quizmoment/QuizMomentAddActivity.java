package cn.yjt.oa.app.quizmoment;

import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.http.ProgressListener;
import io.luobo.common.json.TypeToken;

import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.DocumentInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.http.FileClientFactory;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class QuizMomentAddActivity extends TitleFragmentActivity implements
		View.OnClickListener {

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation arg0) {
			location = arg0.getAddrStr();
			Toast.makeText(QuizMomentAddActivity.this,
					getString(R.string.quiz_moment_location) + location,
					Toast.LENGTH_SHORT).show();
		}
	};
	String location = "";
	private ImageView img_moment;
	private EditText et_moment;
	private ProgressDialog progressDialog;
	private Cancelable cancelable;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		initLocation();// 初始化定位
		initView();
	}

	/**
	 * 初始化百度定位
	 */
	private void initLocation() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption opt = new LocationClientOption();
		opt.setIsNeedAddress(true);
		opt.setOpenGps(true);
		mLocationClient.setLocOption(opt);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 开启定位
		mLocationClient.start();
		// 获取位置信息
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else
			Log.d("LocSDK4", "locClient is null or not started");
	}

	/**
	 * 初始化页面展示
	 */
	private void initView() {
		setContentView(R.layout.quiz_moment_add_activity);
		// 初始化顶部菜单
		initTitleBar();
		img_moment = (ImageView) findViewById(R.id.img_moment);
		findViewById(R.id.btn_add_moment).setOnClickListener(this);
		et_moment = (EditText) findViewById(R.id.et_moment);
		final int width = (int) (MainApplication.getDisplayMetrics().widthPixels - 2*TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 23, getResources()
						.getDisplayMetrics()));
		MainApplication.getImageLoader().get(getIntent().getData().toString(),
				width, width,
				new ImageLoaderListener() {

					@Override
					public void onSuccess(ImageContainer container) {
						final Bitmap bitmap = container.getBitmap();
						if (bitmap != null) {
							img_moment.setScaleType(ScaleType.FIT_XY);
							img_moment.setOnClickListener(null);
							double ratio = (double)bitmap.getWidth()/bitmap.getHeight();
							int height = (int) (width/ratio);
							img_moment
									.setLayoutParams(new RelativeLayout.LayoutParams(
											RelativeLayout.LayoutParams.MATCH_PARENT,
											height));
							img_moment.setImageBitmap(bitmap);
						} else {
							Toast.makeText(getApplicationContext(), "图片获取失败",
									Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onError(ImageContainer container) {
						Toast.makeText(getApplicationContext(), "图片获取失败",
								Toast.LENGTH_SHORT).show();
					}
				}, false);
	}

	private void initTitleBar() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setVisibility(View.INVISIBLE);
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 停止定位
		mLocationClient.stop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add_moment:
			commit();
			break;

		default:
			break;
		}
	}

	private void commit() {
		if (TextUtils.isEmpty(et_moment.getText())) {
			Toast.makeText(this, R.string.quiz_moment_content_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		hideSoftInput();
		uploadFile();
	}

	private void commitMoment(String downUrl) {
		showProgressDialog("正在提交...");
		progressDialog.setCancelable(false);
		DocumentInfo info = new DocumentInfo();
		info.setDownUrl(downUrl);
		info.setCreateTime(BusinessConstants.formatTime(new Date()));
		info.setDescription(et_moment.getText().toString());
		info.setAddress(location);
		info.setType(DocumentInfo.MOMENT);
		// 添加精彩瞬间
		AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
		requestBuilder.setModule(AsyncRequest.MODULE_DOCUMENT);
		requestBuilder.setModuleItem(DocumentInfo.MOMENT);
		requestBuilder.setRequestBody(info);
		requestBuilder.setResponseType(new TypeToken<Response<DocumentInfo>>() {
		}.getType());
		requestBuilder
				.setResponseListener(new Listener<Response<DocumentInfo>>() {
					@Override
					public void onErrorResponse(InvocationError arg0) {
						dismissProgressDialog();
						toast("提交失败");
					}

					@Override
					public void onResponse(Response<DocumentInfo> response) {
						dismissProgressDialog();
						if (response.getCode() == 0) {
							toast("提交成功");
							finish();
						} else {
							toast("提交失败");
						}
					}
				});
		requestBuilder.build().post();
	}

	private String downUrl;

	private void uploadFile() {
		if (downUrl != null) {
			commitMoment(downUrl);
			return;
		}
		cancelable = AsyncRequest.upload(this, getIntent().getData(),
				BusinessConstants.buildUrl(AsyncRequest.MODULE_UPLOAD_IMAGE),
				"image", 720, 1280, true,
				new ProgressListener<Response<String>>() {

					@Override
					public void onError(InvocationError arg0) {
						System.out.println("onError:" + arg0);
						dismissProgressDialog();
						toast("图片上传失败");
					}

					@Override
					public void onFinished(Response<String> response) {
						System.out.println("onFinished");
						if (response.getCode() == 0) {
							downUrl = response.getPayload();
							commitMoment(downUrl);
						} else {
							dismissProgressDialog();
							toast("图片上传失败");
						}
					}

					@Override
					public void onProgress(long arg0, long arg1) {
					}

					@Override
					public void onStarted() {
						System.out.println("onStarted");
						showProgressDialog("正在上传图片...");
					}
				}, FileClientFactory.createSingleThreadFileClient(this));
	}

	private void toast(final String message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void showProgressDialog(final String message) {
		if (isFinishing()) {
			return;
		}
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (progressDialog == null) {
					progressDialog = ProgressDialog.show(
							QuizMomentAddActivity.this, null, message);
					progressDialog.setCancelable(true);
					progressDialog.setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							if (cancelable != null) {
								cancelable.cancel();
							}
						}
					});
					progressDialog.show();
				} else {
					progressDialog.setMessage(message);
				}
			}
		});
	}

	private void dismissProgressDialog() {
		if (isFinishing()) {
			return;
		}
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		});
	}

	public static void launch(Context context, Uri data) {
		Intent intent = new Intent(context, QuizMomentAddActivity.class);
		intent.setData(data);
		context.startActivity(intent);
	}
}
