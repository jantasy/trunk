package cn.yjt.oa.app.industry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.utils.YjtJavaScriptInterface;

@SuppressLint("SetJavaScriptEnabled")
public class IndustryDetailActivity extends TitleFragmentActivity {
	private final static String INDUSTRY_DETAIL_URL="IndustryDetailUrl";
	

	private View progressBar;
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		webView = new WebView(this);
		webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.setWebViewClient(new XWebViewClient());
		webView.setWebChromeClient(new XWebChromeClient());
		// 设置默认缩放方式尺寸是far
		// webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		// 设置出现缩放工具
		// webView.getSettings().setBuiltInZoomControls(true);
		// 让网页自适应屏幕宽度
		WebSettings webSettings = webView.getSettings(); // webView: 类WebView的实例
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		// webSettings.setLoadWithOverviewMode(true);

		progressBar = LayoutInflater.from(this).inflate(R.layout.view_progress,
				null);
		FrameLayout frameLayout = new FrameLayout(this);
		frameLayout.addView(webView);
		frameLayout.addView(progressBar);
		
		webView.loadUrl(getIntent().getStringExtra(INDUSTRY_DETAIL_URL));
		YjtJavaScriptInterface.addInterface(webView);
		setContentView(frameLayout);

		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		}else{
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if(resultCode == Activity.RESULT_OK){
			webView.reload();
		}
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
			AlertDialogBuilder.newBuilder(IndustryDetailActivity.this)
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
	
	public static void launch(Context context,String url) {
		Intent intent=new Intent(context,IndustryDetailActivity.class);
		intent.putExtra(INDUSTRY_DETAIL_URL, url);
		context.startActivity(intent);
	}
}
