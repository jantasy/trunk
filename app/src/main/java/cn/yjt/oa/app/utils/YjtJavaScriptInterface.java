package cn.yjt.oa.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.industry.IndustryDetailActivity;
import cn.yjt.oa.app.industry.SettingIndustryActivity;

public class YjtJavaScriptInterface {
	
	public static final int REQUEST_CODE_INDUSTRY_TAG = 1;

	@SuppressLint("JavascriptInterface")
	public static void addInterface(WebView webView) {
		webView.addJavascriptInterface(new CustAdmin(webView.getContext()), "custAdmin");
	}
	
	public static class CustAdmin{
		
		private Context context;
		
		public CustAdmin(Context context) {
			super();
			this.context = context;
		}
		
		@JavascriptInterface
		public void setIndustryTag(){
			if(context instanceof Activity){
				SettingIndustryActivity.launchForResult((Activity) context, REQUEST_CODE_INDUSTRY_TAG);
			}else{
				SettingIndustryActivity.launch(context);
			}
		}
		
		@JavascriptInterface
		public void setIndustryInfo(String url){
			if(url!=null){
				System.out.println(url);
				IndustryDetailActivity.launch(context, BusinessConstants.buildUrl(url));
			}
			
		}
	}
}
