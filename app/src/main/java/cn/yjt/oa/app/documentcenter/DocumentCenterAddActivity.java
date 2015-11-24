package cn.yjt.oa.app.documentcenter;

import io.luobo.common.Cancelable;
import io.luobo.common.http.FileClient;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.http.ProgressListener;
import io.luobo.common.json.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.beans.DocumentInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.http.FileClientFactory;

public class DocumentCenterAddActivity extends TitleFragmentActivity implements
		View.OnClickListener {

	private Bitmap bitmap;
	private EditText et_moment;
	private Uri document_uri;
	private File filePath;//
	private static final int UPLOAD_SUCCESS = 1;
	private static final int UPLOAD_FAIL = 2;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			dismissDialog();
			if (msg.what == UPLOAD_FAIL)
				Toast.makeText(DocumentCenterAddActivity.this,
						R.string.document_center_add_faild, Toast.LENGTH_SHORT)
						.show();
			else {
				// 添加成功,进入文件添加页面
				Toast.makeText(DocumentCenterAddActivity.this,
						R.string.document_center_add_sucess, Toast.LENGTH_SHORT)
						.show();
				finish();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		initView();
	}

	/**
	 * 初始化页面展示
	 */
	private void initView() {
		setContentView(R.layout.document_center_add_activity);
		// 初始化顶部菜单
		initTitleBar();
		document_uri = getIntent().getData();
		findViewById(R.id.btn_add_moment).setOnClickListener(this);
		et_moment = (EditText) findViewById(R.id.et_moment);
	}

	private void initTitleBar() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setVisibility(View.INVISIBLE);
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap != null) {
			bitmap.recycle();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add_moment:
			// 点击提交文档下载
			// 判断填写内容是否齐全
			if (TextUtils.isEmpty(et_moment.getText().toString())
					|| getString(R.string.quiz_moment_add_hint).equals(
							et_moment.getText().toString())) {
				// 填写内容为空
				
				Toast.makeText(this, R.string.document_center_content_empty, Toast.LENGTH_SHORT).show();
			} else {
				// 添加文档
				addDocument();
			}
			break;

		default:
			break;
		}
	}

	private void addDocument() {
		closeSoftInput();
		// 上传文件
		uploadDocument();
	}

	/**
	 * 上传文件
	 */
	private void uploadDocument() {
		checkNetWork();
		final DocumentCenterAddActivity activity = this;
		activity.showDialog(getString(R.string.document_center_uploading));
		if (filePath == null) {
			String path = getPath(this, document_uri); 
			filePath = new File(path);
		}
		LogUtils.i("===filePath = " + filePath);
		FileClient client = FileClientFactory
				.createSingleThreadFileClient(this);
		try {

			Type type = new TypeToken<Response<String>>() {
			}.getType();
			String uri = BusinessConstants
					.buildUrl(AsyncRequest.MODULE_UPLOAD_FILE);
			uploadCancelable = client.upload(uri, "file", filePath, type,
					new ProgressListener<Response<String>>() {


						@Override
						public void onError(InvocationError arg0) {
							handler.sendEmptyMessage(UPLOAD_FAIL);							
						}

						@Override
						public void onFinished(Response<String> response) {
							// 处理返回结果
							if (response.getCode() == 0) {
								// 上传成功了
								// 获取url
								String downUrl = response.getPayload();
								DocumentInfo info = new DocumentInfo();
								info.setDownUrl(downUrl);
								info.setCreateTime(BusinessConstants
										.formatTime(new Date()));
								info.setDescription(et_moment.getText()
										.toString());
								info.setType(DocumentInfo.DOCUMENT);
								info.setName(filePath.getName());
								LogUtils.i("===name = " + filePath.getName());
								long length = filePath.length();
								info.setSize(length);
								// 添加文档
								AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
								requestBuilder
										.setModule(AsyncRequest.MODULE_DOCUMENT);
								requestBuilder
										.setModuleItem(DocumentInfo.DOCUMENT);
								requestBuilder.setRequestBody(info);
								requestBuilder
										.setResponseType(new TypeToken<Response<DocumentInfo>>() {
										}.getType());
								requestBuilder
										.setResponseListener(new Listener<Response<DocumentInfo>>() {
											@Override
											public void onErrorResponse(
													InvocationError arg0) {
												handler.sendEmptyMessage(UPLOAD_FAIL);
												LogUtils.i("===111 err.msg = " + arg0.getMessage());
											}

											@Override
											public void onResponse(
													Response<DocumentInfo> response) {
												LogUtils.i("===111 response.code = " + response.getCode());
												LogUtils.i("===111 response.getDescription = " + response.getDescription());
												if (response.getCode() == 0)
													handler.sendEmptyMessage(UPLOAD_SUCCESS);
												else {
													handler.sendEmptyMessage(UPLOAD_FAIL);
												}
											}
										});
								requestBuilder.build().post();
							} else {// 上传失败
								handler.sendEmptyMessage(UPLOAD_FAIL);
							}							
						}

						@Override
						public void onProgress(long arg0, long arg1) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onStarted() {
							// TODO Auto-generated method stub
							
						}
					});
		} catch (Exception e) {

		}
	}

	// 通过uri获取路径
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {  
		  
	    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;  
	  
	    // DocumentProvider  
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {  
	        // ExternalStorageProvider  
	        if (isExternalStorageDocument(uri)) {  
	            final String docId = DocumentsContract.getDocumentId(uri);  
	            final String[] split = docId.split(":");  
	            final String type = split[0];  
	  
	            if ("primary".equalsIgnoreCase(type)) {  
	                return Environment.getExternalStorageDirectory() + "/" + split[1];  
	            }  
	  
	        }  
	        // DownloadsProvider  
	        else if (isDownloadsDocument(uri)) {  
	  
	            final String id = DocumentsContract.getDocumentId(uri);  
	            final Uri contentUri = ContentUris.withAppendedId(  
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));  
	  
	            return getDataColumn(context, contentUri, null, null);  
	        }  
	        // MediaProvider  
	        else if (isMediaDocument(uri)) {  
	            final String docId = DocumentsContract.getDocumentId(uri);  
	            final String[] split = docId.split(":");  
	            final String type = split[0];  
	  
	            Uri contentUri = null;  
	            if ("image".equals(type)) {  
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("video".equals(type)) {  
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("audio".equals(type)) {  
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  
	            }  
	  
	            final String selection = "_id=?";  
	            final String[] selectionArgs = new String[] {  
	                    split[1]  
	            };  
	  
	            return getDataColumn(context, contentUri, selection, selectionArgs);  
	        }  
	    }  
	    // MediaStore (and general)  
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {  
	  
	        // Return the remote address  
	        if (isGooglePhotosUri(uri))  
	            return uri.getLastPathSegment();  
	  
	        return getDataColumn(context, uri, null, null);  
	    }  
	    // File  
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {  
	        return uri.getPath();  
	    }  
	  
	    return null;  
	}  
	  
	/** 
	 * Get the value of the data column for this Uri. This is useful for 
	 * MediaStore Uris, and other file-based ContentProviders. 
	 * 
	 * @param context The context. 
	 * @param uri The Uri to query. 
	 * @param selection (Optional) Filter used in the query. 
	 * @param selectionArgs (Optional) Selection arguments used in the query. 
	 * @return The value of the _data column, which is typically a file path. 
	 */  
	public static String getDataColumn(Context context, Uri uri, String selection,  
	        String[] selectionArgs) {  
	  
	    Cursor cursor = null;  
	    final String column = "_data";  
	    final String[] projection = {  
	            column  
	    };  
	  
	    try {  
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,  
	                null);  
	        if (cursor != null && cursor.moveToFirst()) {  
	            final int index = cursor.getColumnIndexOrThrow(column);  
	            return cursor.getString(index);  
	        }  
	    } finally {  
	        if (cursor != null)  
	            cursor.close();  
	    }  
	    return null;  
	}  
	  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is ExternalStorageProvider. 
	 */  
	public static boolean isExternalStorageDocument(Uri uri) {  
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is DownloadsProvider. 
	 */  
	public static boolean isDownloadsDocument(Uri uri) {  
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is MediaProvider. 
	 */  
	public static boolean isMediaDocument(Uri uri) {  
	    return "com.android.providers.media.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is Google Photos. 
	 */  
	public static boolean isGooglePhotosUri(Uri uri) {  
	    return "com.google.android.apps.photos.content".equals(uri.getAuthority());  
	}  	
	
	private void closeSoftInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et_moment.getWindowToken(), 0);
	}

	private void checkNetWork() {
		if (!hasNetWork()) {
			Toast.makeText(this, R.string.connect_network_fail,
					Toast.LENGTH_LONG).show();
			return;
		}
	}

	private boolean hasNetWork() {
		ConnectivityManager manager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	ProgressDialog dialog;
	private Cancelable uploadCancelable;

	public void showDialog(String message) {
		dialog = new ProgressDialog(this);
		dialog.setMessage(message);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if(uploadCancelable != null){
					uploadCancelable.cancel();
				}
			}
		});
		dialog.show();
	}

	public void dismissDialog() {
		dialog.setCancelable(true);
		dialog.dismiss();
	}
}
