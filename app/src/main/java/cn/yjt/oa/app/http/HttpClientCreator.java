package cn.yjt.oa.app.http;

import io.luobo.common.http.Converter;
import io.luobo.common.http.ListenerClient;
import io.luobo.common.http.volley.GsonConverter;
import io.luobo.common.http.volley.VolleyClients;

import java.lang.reflect.Type;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.format.DateUtils;
import cn.yjt.oa.app.BuildConfig;
import cn.yjt.oa.app.app.utils.LogUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

public class HttpClientCreator extends VolleyClients {
	private static final HttpClientCreator instance = new HttpClientCreator();
	private DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(
			(int) DateUtils.MINUTE_IN_MILLIS,
			DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
			DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
	private final RetryPolicy retryPolicyWithNoRetry = new RetryPolicy() {

		@Override
		public void retry(VolleyError error) throws VolleyError {
			throw error;
		}

		@Override
		public int getCurrentRetryCount() {
			return 0;
		}

		@Override
		public int getCurrentTimeout() {
			return (int) (DateUtils.MINUTE_IN_MILLIS);
		}
		
	};

	public static HttpClientCreator getClientCreator() {
		return instance;
	}

	private HttpClientCreator() {
	}

	@Override
	protected RequestQueue getRequestQueue() {
		return RequestQueueHolder.getInstance().getRequestQueue();
	}

	@Override
	protected LruCache<String, Bitmap> getBitmapCache() {
		return BitmapCache.globalBitmapCache();
	}

	@Override
	protected Converter getDataConverter() {
		// new GsonConverter(new Gson());
		//return new JacksonConverter(ObjectMapperHolder.getInstance().getMapper());
		return new GsonConverterDelegate(GsonHolder.getInstance().getGson());
	}
	
	
	@Override
	public ListenerClient newListenerClient() {
		ListenerClient client = super.newListenerClient();
		client.setRetryPolicy(retryPolicy);
		return client;
	}
	
	public ListenerClient newListenerClientNoRetry() {
		ListenerClient client = super.newListenerClient();
		client.setRetryPolicy(retryPolicyWithNoRetry);
		return client;
	}
	
	public static class GsonConverterDelegate extends GsonConverter{

		public GsonConverterDelegate(Gson gson) {
			super(gson);
		}

		@Override
		public <T> T convertToObject(String str, Type type) {
			if(BuildConfig.DEBUG){
				LogUtils.i(getClass().getSimpleName(), str);
			}
			return super.convertToObject(str, type);
		}
	}
}
