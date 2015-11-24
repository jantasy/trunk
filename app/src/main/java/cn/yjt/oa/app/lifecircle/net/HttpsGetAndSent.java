package cn.yjt.oa.app.lifecircle.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

public class HttpsGetAndSent {
    private static final int SET_CONNECTION_TIMEOUT = 5000;
    private static final int SET_SOCKET_TIMEOUT = 20000;
    private static final String TAG = "HttpsGetAndSent";
    private final String NET_MISSING;
    private final String NET_WRONG;
    private final int REPONSE_OK;

    public HttpsGetAndSent() {
        this.REPONSE_OK = 200;
        this.NET_WRONG = "netwrong";
        this.NET_MISSING = "netmissing";
    }

    public static Bitmap getHttpsBitmap(String str) {
        try {
            HttpResponse execute = getNewHttpClient().execute(new HttpGet(str));
            if (execute.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            InputStream content = execute.getEntity().getContent();
            Bitmap decodeStream = BitmapFactory.decodeStream(content);
            content.close();
            return decodeStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HttpClient getNewHttpClient() {
        try {
            KeyStore instance = KeyStore.getInstance(KeyStore.getDefaultType());
            instance.load(null, null);
            MySSLSocketFactory mySSLSocketFactory = new MySSLSocketFactory(instance);
            mySSLSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams basicHttpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(basicHttpParams, 10000);
            HttpConnectionParams.setSoTimeout(basicHttpParams, 10000);
            HttpProtocolParams.setVersion(basicHttpParams, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(basicHttpParams, "UTF-8");
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https", mySSLSocketFactory, 443));
            ClientConnectionManager threadSafeClientConnManager = new ThreadSafeClientConnManager(basicHttpParams, schemeRegistry);
            HttpConnectionParams.setConnectionTimeout(basicHttpParams, SET_CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(basicHttpParams, SET_SOCKET_TIMEOUT);
            return new DefaultHttpClient(threadSafeClientConnManager, basicHttpParams);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public String requestHttpsGet(String str) {
        HttpGet httpGet;
        Exception e;
        Throwable th;
        try {
            Log.d("网络链接:", str);
            HttpClient newHttpClient = getNewHttpClient();
            newHttpClient.getParams().setParameter("http.protocol.allow-circular-redirects", Boolean.valueOf(true));
            httpGet = new HttpGet(str);
            try {
                HttpResponse execute = newHttpClient.execute(httpGet);
                Log.d("网络状态:", execute.getStatusLine().getStatusCode()+"");
                String str2 = execute.getStatusLine().getStatusCode() == 200 ? new String(EntityUtils.toByteArray(execute.getEntity()), "utf-8") : "netmissing";
                httpGet.abort();
                return str2;
            } catch (Exception e2) {
                e = e2;
                try {
                    e.printStackTrace();
                    httpGet.abort();
                    return "netwrong";
                } catch (Throwable th2) {
                    th = th2;
                    httpGet.abort();
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                httpGet.abort();
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            httpGet = null;
            e.printStackTrace();
            httpGet.abort();
            return "netwrong";
        } catch (Throwable th4) {
            th = th4;
            httpGet = null;
            httpGet.abort();
//            throw th;
        }
		return str;
    }

    public String requestHttpsGet2(String str) {
        Exception e;
        try {
            Log.d("网络链接:", str);
            try {
                HttpResponse execute = getNewHttpClient().execute(new HttpGet(str));
                Log.d("网络状态:", execute.getStatusLine().getStatusCode()+"");
                return execute.getStatusLine().getStatusCode() == 200 ? new String(EntityUtils.toByteArray(execute.getEntity()), "utf-8") : "netmissing";
            } catch (Exception e2) {
                e = e2;
                e.printStackTrace();
                return "netwrong";
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            return "netwrong";
        }
    }

    public String requestHttpsPost(List<NameValuePair> list, String str) {
        HttpClient newHttpClient = getNewHttpClient();
        HttpPost httpPost = new HttpPost(str);        
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            HttpResponse execute = newHttpClient.execute(httpPost);
            Log.d(TAG, "网络返回 状态码为200 :" + execute.getStatusLine().getStatusCode());
            return execute.getStatusLine().getStatusCode() == 200 ? new String(EntityUtils.toByteArray(execute.getEntity()), "utf-8") : "netwrong";
        } catch (Exception e) {
            e.printStackTrace();
            return "netwrong";
        }
    }
}
