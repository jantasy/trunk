package cn.yjt.oa.app.http;

import io.luobo.common.http.volley.VolleyHelper;

import java.io.File;

import org.apache.http.client.HttpClient;

import android.os.Environment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;

public class RequestQueueHolder {
	private static final RequestQueueHolder instance = new RequestQueueHolder();
	private RequestQueue queue;
	private HttpStack httpStack;
	
	public static RequestQueueHolder getInstance() {
		return instance;
	}
	
	private RequestQueueHolder() {
		initRequestQueue();
	}
	
	private void initRequestQueue() {
		//queue = VolleyHelper.newRequestQueue(new File(Environment.getExternalStorageDirectory(), "volley"));
		HttpClient httpclient = YjtHttpClient.newInstance("yjt-oa");
//		DefaultHttpClient httpclient = new DefaultHttpClient();
//
//		CookieStore cookieStore = new BasicCookieStore();
//		httpclient.setCookieStore(cookieStore);

		httpStack = new HttpClientStack(httpclient);		
		queue = VolleyHelper.newRequestQueue(new File(Environment.getExternalStorageDirectory(), "volley"), httpStack);
		
	}
	
	public HttpStack getHttpStack(){
		return httpStack;
	}
	
	public RequestQueue getRequestQueue() {
		return queue;
	}
}
