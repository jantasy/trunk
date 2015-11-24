package cn.yjt.oa.app.find;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cn.yjt.oa.app.BaseFragment;
import cn.yjt.oa.app.OnBackPressedInterface;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.AppRequest;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.http.BusinessConstants;

public class FinderFragment extends BaseFragment implements DownloadListener,
		OnBackPressedInterface {

	private static final String URL = BusinessConstants.buildPath("/p/yjtzsfind.html");

	private View progressBar;
	private WebView webView;

	private String fileName;

	private DownloadManager downloadManager;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		getActivity().registerReceiver(receiver,
				new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	@Override
	public void onDetach() {
		getActivity().unregisterReceiver(receiver);
		super.onDetach();
		
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		webView = new WebView(getActivity());
		webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new XWebViewClient());
		webView.setWebChromeClient(new XWebChromeClient());
		webView.setDownloadListener(this);
		webView.getSettings().setSupportZoom(true);

		// 设置默认缩放方式尺寸是far
//		webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		// 设置出现缩放工具
//		webView.getSettings().setBuiltInZoomControls(true);
		// 让网页自适应屏幕宽度
		WebSettings webSettings = webView.getSettings(); // webView: 类WebView的实例
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		// webSettings.setLoadWithOverviewMode(true);

		progressBar = inflater.inflate(R.layout.view_progress, null);
		FrameLayout frameLayout = new FrameLayout(getActivity());
		frameLayout.addView(webView);
		frameLayout.addView(progressBar);

		webView.loadUrl(URL);
		

		return frameLayout;
	}

	@Override
	public CharSequence getPageTitle(Context context) {
		return "发现";
	}

	@Override
	public boolean onRightButtonClick() {
		return false;
	}

	@Override
	public void configRightButton(ImageView imgView) {
		imgView.setVisibility(View.GONE);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	public void onDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
		downloadManager = (DownloadManager) getActivity().getSystemService(
				Context.DOWNLOAD_SERVICE);
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1) {
			fileName = url.substring(url.lastIndexOf("/") + 1);
			String filePath = new AppRequest().getDownloadDirectory(fileName)
					.getAbsolutePath();// .getPath();
			Log.e("asdf", filePath + contentLength);

			// 开始下载
			Uri resource = Uri.parse(url);
			DownloadManager.Request request = new DownloadManager.Request(
					resource);
			request.setAllowedNetworkTypes(Request.NETWORK_MOBILE
					| Request.NETWORK_WIFI);
			request.setAllowedOverRoaming(false);
			// 设置文件类型
			// MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
			// String mimeString =
			// mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
			String mimeString = mimetype;
			request.setMimeType(mimeString);
			// 在通知栏中显示
			request.setShowRunningNotification(true);
			request.setVisibleInDownloadsUi(true);
			// sdcard的目录下的download文件夹
			request.setDestinationInExternalPublicDir("/yijitong/app/",
					fileName);
			request.setTitle(fileName);
			downloadManager.enqueue(request);
			// downloadManager.(title, description, isMediaScannerScannable,
			// mimeType, path, length, showNotification)(fileName, "正在开始下载……",
			// false, mimetype, filePath, contentLength, true);
		} else {
			Uri uri = Uri.parse(url);
			Request request = new Request(uri);
			downloadManager.enqueue(request);
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
			long Id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			Log.e("intent", "" + Id);
			queryDownloadStatus(Id);
		}
	};

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	private void queryDownloadStatus(long Id) {
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterById(Id);
		Cursor c = downloadManager.query(query);
		String title = "";
		String description = "";
		String mimeType = "";
		String fileName = "";
		long fileSize = 0L;
		if (c.moveToFirst()) {
			// downloadManager.addCompletedDownload(
			title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
			description = c.getString(c
					.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
			mimeType = c.getString(c
					.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
			fileName = c.getString(c
					.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
			fileSize = c.getLong(c
					.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
			// int status =
			// c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
			// switch(status) {
			// case DownloadManager.STATUS_PAUSED:
			// Log.v("down", "STATUS_PAUSED");
			// case DownloadManager.STATUS_PENDING:
			// Log.v("down", "STATUS_PENDING");
			// case DownloadManager.STATUS_RUNNING:
			// //正在下载，不做任何事情
			// Log.v("down", "STATUS_RUNNING");
			// break;
			// case DownloadManager.STATUS_SUCCESSFUL:
			// //完成
			// Log.v("down", "下载完成");
			// break;
			// case DownloadManager.STATUS_FAILED:
			// //清除已下载的内容，重新下载
			// Log.v("down", "STATUS_FAILED");
			// downloadManager.remove(prefs.getLong(DL_ID, 0));
			// prefs.edit().clear().commit();
			// break;
			// }
		}
		description = "下载完成";
		downloadManager.addCompletedDownload(title, description, false,
				mimeType, fileName, fileSize, true);

	}

	private class XWebViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			progressBar.setVisibility(View.GONE);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			progressBar.setVisibility(View.VISIBLE);
		}

	}

	private class XWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			AlertDialogBuilder.newBuilder(getActivity())
					.setMessage(message)
					.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).setNegativeButton("取消", null).show();
			result.confirm();
			return true;
		}
	}

	@Override
	public boolean onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return false;
	}

}
