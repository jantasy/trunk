package cn.yjt.oa.app.documentcenter;

import io.luobo.common.http.FileClient;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.ProgressListener;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.FileClientFactory;
import cn.yjt.oa.app.utils.FileUtils;

public class DocumentCenterDownloadActivity extends TitleFragmentActivity implements
		OnClickListener {

	private String url, fileName;
	
	private double fileSize; 

	private TextView tvName, tvSize;

	private Button btnDownload;
	
	private ProgressDialog downloadDialog;
	
	private static final int DOWNLOAD_SUCESS = 1;
	private static final int DOWNLOAD_FAIL = 2;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DOWNLOAD_SUCESS:
				String text=getResources().getString(
						R.string.document_center_download_sucess)+msg.obj;
				Toast.makeText(
						getApplicationContext(),
						text,
						Toast.LENGTH_SHORT).show();
				if(file.exists()){
					btnDownload.setText("打开");
				}
				break;
			case DOWNLOAD_FAIL:
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(
								R.string.document_center_download_fail),
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	private File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.document_center_download);

		initview();

	}

	private void initview() {
		Intent intent = getIntent();
		url = intent.getStringExtra("DocumentDownloadUri");
		fileName = intent.getStringExtra("DocumentDownloadName");
		fileSize = intent.getDoubleExtra("DocumentDownloadSize", 0);
		
		LogUtils.i("===url = " + url);
		LogUtils.i("===fileName = " + fileName);
		LogUtils.i("===fileSize = " + fileSize);
		initTitleBar();
		tvName = (TextView) findViewById(R.id.document_center_download_name);
		tvSize = (TextView) findViewById(R.id.document_center_download_size);
		btnDownload = (Button) findViewById(R.id.document_center_download_btn);
		
		tvName.setText(fileName);
		tvSize.setText(FileUtils.sizeFromBToString(fileSize));

		btnDownload.setOnClickListener(this);
		
		initPro();
		
		file = FileUtils.createFileInUserFolder("DocumentDownload/"+fileName);
		File parentFile = file.getParentFile();
		if(!parentFile.exists()){
			parentFile.mkdirs();
		}
		if(file.exists()){
			btnDownload.setText("打开");
		}

	}

	
	private void initTitleBar() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);

	}
	
	@Override
	public void onLeftButtonClick() {
		super.onLeftButtonClick();
		finish();
	}
	private void initPro() {
		if (downloadDialog == null) {
			downloadDialog = new ProgressDialog(this);
			downloadDialog.setMessage(getResources().getString(
					R.string.document_center_downloading));
			downloadDialog.setCancelable(false);
		}
	}
	
	private void openFile(){
		String path = file.getAbsolutePath();
		String mime = "*/*";
		if(path.contains(".")&&!path.endsWith(".")){
			String extension = path.substring(path.lastIndexOf(".")+1);
			 mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), mime);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.document_center_download_btn:
			if(file.exists()){
				openFile();
			}else{
				startDowning();
			}
			break;

		default:
			break;
		}
	}

	private void startDowning() {
		if (!downloadDialog.isShowing()) {
			downloadDialog.show();
		}
		try {
			FileClient client = FileClientFactory.createSingleThreadFileClient(MainApplication.getAppContext()); 
			client.download(url, file, new ProgressListener<File>() {
				
				@Override
				public void onStarted() {
					LogUtils.i("===onStarted");
				}
				
				@Override
				public void onProgress(long progress, long total) {
					LogUtils.i("===onProgress progress = " + progress);
					LogUtils.i("===onProgress total = " + total);
				}
				
				@Override
				public void onFinished(File file) {
					if (downloadDialog != null) {
						downloadDialog.dismiss();
					}
					Message msg=mHandler.obtainMessage();
					msg.obj=file.getAbsolutePath();
					msg.what=DOWNLOAD_SUCESS;
					mHandler.sendMessage(msg);
					
					LogUtils.i("===onFinished = " + file);
				}
				
				@Override
				public void onError(InvocationError error) {
					if (downloadDialog != null) {
						downloadDialog.dismiss();
					}
					mHandler.sendEmptyMessage(DOWNLOAD_FAIL);
					LogUtils.i("===onError error = " + error.toString());
				}
			}).start();
		} catch (InvocationError e) {
			e.printStackTrace();
			LogUtils.i("===e = " + e.toString());
		}
		
	}
	
}
