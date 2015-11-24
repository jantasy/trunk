package cn.yjt.oa.app.teleconferencenew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends TitleFragmentActivity {
	private static final String tag = "WebViewActivity";
	private WebView mWebView;
	private MyWebClient mWebClient;
	private String mUrl;
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
			setContentView(R.layout.activity_webview);
			getLeftbutton().setImageResource(R.drawable.navigation_back);	
			mWebView = (WebView) findViewById(R.id.webview);
			mWebClient = new MyWebClient();
			mWebView.setWebViewClient(mWebClient);
			Intent intent = getIntent();
			int webviewType = intent.getIntExtra(Constants.WEBVIEW_TYPE, Constants.WEBVIEW_TYPE_REWARD);
			if(webviewType == Constants.WEBVIEW_TYPE_REWARD) {
				setTitle("奖励规则");
				mUrl = Constants.REWARD_URL;
			} else {
				setTitle("会控说明");
				mUrl = Constants.CONTROL_URL;
			}
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.loadUrl(mUrl);			
	}
	
	@Override
	public void onLeftButtonClick() {
		if (mWebView.canGoBack()){
			mWebView.goBack();
		}else{
			super.onBackPressed();
		}
	}
	
	private class MyWebClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			mWebView.loadUrl(url);
			return true;
		}
	}
}
