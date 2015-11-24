package cn.yjt.oa.app.notifications;

import android.os.Bundle;
import android.webkit.WebView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.UmengBaseActivity;

public class NewsDetaileActivity extends UmengBaseActivity {
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_detail_activity_layout);
		String uri = getIntent().getStringExtra("content_url");
		webView = (WebView) findViewById(R.id.web_view);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(uri);
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()){
			webView.goBack();
		}else{
			super.onBackPressed();
		}
	}
}
