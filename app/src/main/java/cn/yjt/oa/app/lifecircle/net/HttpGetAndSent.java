package cn.yjt.oa.app.lifecircle.net;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class HttpGetAndSent {
    private final String NET_MISSING;
    private final String NET_WRONG;
    final int REPONSE_OK;
    String TAG;

    public HttpGetAndSent(Context context) {
        this.REPONSE_OK = 200;
        this.NET_WRONG = "netwrong";
        this.NET_MISSING = "netmissing";
        this.TAG = "HttpGetAndSent";
    }

    public byte[] doGetbytes(String str) {
        Log.d(this.TAG, "doGetbytes>>");
        try {
            HttpParams basicHttpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(basicHttpParams, 10000);
            HttpConnectionParams.setSoTimeout(basicHttpParams, 5 * 1000);
            try {
                HttpResponse execute = new DefaultHttpClient(basicHttpParams).execute(new HttpGet(str));
                if (execute.getStatusLine().getStatusCode() != 200) {
                    return null;
                }
                byte[] toByteArray = EntityUtils.toByteArray(execute.getEntity());
                Log.d(this.TAG, "length>>" + toByteArray.length);
                return toByteArray;
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e2) {
            return null;
        }
    }

    public String requestHttp(String str) {
        Exception e;
        try {
            Log.d("网络链接:", str);
            HttpParams basicHttpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(basicHttpParams, 5 * 1000);
            HttpConnectionParams.setSoTimeout(basicHttpParams, 5 * 1000);
            try {
                HttpResponse execute = new DefaultHttpClient(basicHttpParams).execute(new HttpGet(str));
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

    public String requestPostHttp(List<NameValuePair> list, String str) {
    	DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
    	HttpPost httpPost = new HttpPost(str);        
        try {
            defaultHttpClient.getParams().setParameter("http.connection.timeout", Integer.valueOf(10000));
            defaultHttpClient.getParams().setParameter("http.socket.timeout", Integer.valueOf(10000));
            httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            HttpResponse execute = defaultHttpClient.execute(httpPost);
            Log.d(this.TAG, "网络返回 状态码为200 :" + execute.getStatusLine().getStatusCode());
            return execute.getStatusLine().getStatusCode() == 200 ? new String(EntityUtils.toByteArray(execute.getEntity()), "utf-8") : "netwrong";
        } catch (Exception e) {
            e.printStackTrace();
            return "netwrong";
        }
    }
}
