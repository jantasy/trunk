package cn.yjt.oa.app.teleconference;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import cn.yjt.oa.app.component.TitleFragmentActivity;

public class WebActivity extends TitleFragmentActivity {
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		WebView webView = new WebView(this);
		webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		
		Uri data = getIntent().getData();
		String url = data.getQueryParameter("url");
		if(url != null){
			webView.loadUrl(url);
		}
		setContentView(webView);
	}
}
