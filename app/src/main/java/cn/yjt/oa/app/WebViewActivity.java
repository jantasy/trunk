package cn.yjt.oa.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import cn.yjt.oa.app.component.TitleFragmentActivity;

public class WebViewActivity extends TitleFragmentActivity {

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		WebView webView = new WebView(this);
		webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		webView.getSettings().setJavaScriptEnabled(true);

		webView.loadUrl(getIntent().getStringExtra("url"));
		setContentView(webView);
		setTitle(getIntent().getStringExtra("title"));
		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
	public static void launchWithTitleAndUrl(Context context,String title,String url){
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("url", url);
		context.startActivity(intent );
	}
}
