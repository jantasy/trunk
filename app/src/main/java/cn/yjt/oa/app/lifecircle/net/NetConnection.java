package cn.yjt.oa.app.lifecircle.net;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MIME;
import org.apache.http.message.BasicNameValuePair;

import cn.yjt.oa.app.lifecircle.model.Netable;
import cn.yjt.oa.app.lifecircle.model.Word;
import cn.yjt.oa.app.lifecircle.net.MyException.JsonException;
import cn.yjt.oa.app.lifecircle.net.MyException.TimeOutException;
import cn.yjt.oa.app.lifecircle.utils.Constants;
import cn.yjt.oa.app.lifecircle.utils.PreferfenceUtils;

public class NetConnection {
    private static final String TAG = "NetConnection";
    private final String WEB_HTTP, WEB_HTTPS;
    private Context mContext;
    private HttpGetAndSent mHttpGetAndSent;
    private HttpsGetAndSent mHttps;
    private NetAnalytic mInfoAnalysis;
    private String mobileType;
    private String parameters;
    private final boolean isUserHttps = false;

    public NetConnection(Context context) {        
//        WEB_IP = "http://112.124.45.47:9902/";
//    	WEB_HTTP = "http://219.142.69.153:9098/living-circle/";
//    	WEB_HTTP = "http://42.123.77.69:9090/living-circle/";
    	WEB_HTTP = "http://www.yijitongoa.com:9090/yjtoa/passthrough/";
    	WEB_HTTPS = "https://219.142.69.153:9098/living-circle/";
        mContext = context;
        mInfoAnalysis = new NetAnalytic();
        mHttpGetAndSent = new HttpGetAndSent(mContext);
        mHttps = new HttpsGetAndSent();
    }

    public static String post(String str, Map<String, String> map, Map<String, File> map2) throws IOException {
        String uuid = UUID.randomUUID().toString();
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setReadTimeout(5 * 1000);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("connection", "keep-alive");
        httpURLConnection.setRequestProperty("Charsert", "UTF-8");
        httpURLConnection.setRequestProperty(MIME.CONTENT_TYPE, "multipart/form-data" + ";boundary=" + uuid);
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry entry : map.entrySet()) {
            stringBuilder.append("--");
            stringBuilder.append(uuid);
            stringBuilder.append("\r\n");
            stringBuilder.append("Content-Disposition: form-data; name=\"" + ((String) entry.getKey()) + "\"" + "\r\n");
            stringBuilder.append("Content-Type: text/plain; charset=" + "UTF-8" + "\r\n");
            stringBuilder.append("Content-Transfer-Encoding: 8bit" + "\r\n");
            stringBuilder.append("\r\n");
            stringBuilder.append((String) entry.getValue());
            stringBuilder.append("\r\n");
        }
        DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
        dataOutputStream.write(stringBuilder.toString().getBytes());
        if (map2 != null) {
            for (Entry entry2 : map2.entrySet()) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("--");
                stringBuilder2.append(uuid);
                stringBuilder2.append("\r\n");
                stringBuilder2.append("Content-Disposition: form-data; name=\"pic\"; filename=\"" + ((File) entry2.getValue()).getName() + "\"" + "\r\n");
                stringBuilder2.append("Content-Type: application/octet-stream; charset=" + "UTF-8" + "\r\n");
                stringBuilder2.append("\r\n");
                dataOutputStream.write(stringBuilder2.toString().getBytes());
                InputStream fileInputStream = new FileInputStream((File) entry2.getValue());
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    dataOutputStream.write(bArr, 0, read);
                }
                fileInputStream.close();
                dataOutputStream.write("\r\n".getBytes());
            }
        }
        dataOutputStream.write(("--" + uuid + "--" + "\r\n").getBytes());
        dataOutputStream.flush();
        int responseCode = httpURLConnection.getResponseCode();
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Object obj = "OK";
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                break;
            }
            obj = new StringBuilder(String.valueOf(obj)).append(readLine).toString();
        }
        if (responseCode == 200) {
            Log.d("NetConnection", "200");
            StringBuilder stringBuilder3 = new StringBuilder();
            while (true) {
                responseCode = inputStream.read();
                if (responseCode == -1) {
                    break;
                }
                stringBuilder3.append((char) responseCode);
                Log.d("NetConnection","sb2:" + stringBuilder3.toString());
            }
        }
        dataOutputStream.close();
        httpURLConnection.disconnect();
        return inputStream.toString();
    }

    public Netable cancel_first_order(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("firstOrderId", str));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/cancel_first_order");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/cancel_first_order");
            } 
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/cancel_first_order");
            Log.d("NetConnection", "网络返回:" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("11.3 单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("11.3 放弃订单:", "ok");
        return netable;
    }

    public Netable collect(String str, String str2) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            Log.d("NetConnection", "id:" + str + ",op:" + str2);
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("operate", str2));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/collect");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/collect");
            } 
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/collect");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("5.2单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("5.2 用户收藏店铺:", "ok");
        return netable;
    }

    public Netable generate_first_order(String str, String str2, String str3) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchandiseId", str));
            arrayList.add(new BasicNameValuePair("amount", str2));
            arrayList.add(new BasicNameValuePair("recommendPhoneNo", str3));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/generate_first_order");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/generate_first_order");
            } 
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/generate_first_order");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("11.1 单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("11.1 订单提交:", "ok");
        return netable;
    }

    public Netable getAreaInfo(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            parameters = "api/area_info?city=" + URLEncoder.encode(str);
            parameters += "&serverIP=" + Constants.PRO_IP;
//            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            Log.d("参数:", parameters);
            String requestHttp = mHttpGetAndSent.requestHttp(WEB_HTTP + parameters);
            Log.d("网络返回:", requestHttp);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttp);
                netable.setName(str);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("2.3单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("2.3获取商圈:", "ok");
        return netable;
    }

    public Netable getCityAllInfo(String str) {
        Netable netable = new Netable();
        int i = 1;
//        while (i != 0) {
            parameters = "api/city_all_info?city=" + str;
            parameters += "&serverIP=" + Constants.PRO_IP;
            Log.d("Parameter:", parameters);
            String requestHttpsGet;
            if(isUserHttps) {
            	requestHttpsGet = mHttps.requestHttpsGet(WEB_HTTPS + parameters);
            } else {
            	requestHttpsGet = mHttpGetAndSent.requestHttp(WEB_HTTP + parameters);
            } 
            Log.d("Result:", requestHttpsGet);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsGet);
                if(netable.getResult() != 0)
                	return null;
                i = 0;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("9.1 is miss:", "..");
                i = 1;
                return null;
            }
//        }
        Log.d("9.1:", "ok");
        return netable;
    }

    public ArrayList<ArrayList<Netable>> getCityInfo(ArrayList<Netable> arrayList) {
        ArrayList<ArrayList<Netable>> arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            for (Object obj = 1; obj != null; obj = null) {
                ArrayList arrayList3 = new ArrayList();
                parameters = "api/city_info";
                parameters += "&serverIP=" + Constants.PRO_IP;
                Log.d("参数:", parameters);
                Log.d("网络返回:", mHttps.requestHttpsGet(WEB_HTTPS + parameters));
                arrayList2.add(arrayList3);
            }
        }
        Log.d("2.2获取市信息:", "ok");
        return arrayList2;
    }

    public Netable getDistanceOption(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            parameters = "api/distance_option?city=" + URLEncoder.encode(str);
            parameters += "&serverIP=" + Constants.PRO_IP;
            Log.d("参数:", parameters);
            String requestHttp = mHttpGetAndSent.requestHttp(WEB_HTTP + parameters);
            Log.d("网络返回:", requestHttp);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttp);
                netable.setName(str);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("4.2单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("4.2获取按距离检索的距离选项:", "ok");
        return netable;
    }

    public Netable getGoods(String str, String str2, String str3, String str4) {
        Netable netable = new Netable();
        int i = 1;
//        while (i != 0) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("kind", str));
            arrayList.add(new BasicNameValuePair("merchantId", str2));
            arrayList.add(new BasicNameValuePair("city", str3));
            arrayList.add(new BasicNameValuePair("pageAmount", str4));
            arrayList.add(new BasicNameValuePair("page", "1"));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/search_promotion_merchandises");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/search_promotion_merchandises");
            } 
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/search_promotion_merchandises");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                i = 0;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("10.1单次错误:", "..");
                i = 1;
                return null;
            }
//        }
        Log.d("10.1 获得商家商品:", "ok");
        return netable;
    }

    public Netable getGoodsDetails(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchandiseId", str));
            arrayList.add(new BasicNameValuePair("filterMerchantId", "-1"));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchandise_detail");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchandise_detail");
            } 
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchandise_detail");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("10.2单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("10.2商品详情:", "ok");
        return netable;
    }

    public Netable getMerchantIndustry(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            parameters = "api/merchant_industry?city=" + URLEncoder.encode(str);
            parameters += "&serverIP=" + Constants.PRO_IP;
            Log.d("参数:", parameters);
            String requestHttp = mHttpGetAndSent.requestHttp(WEB_HTTP + parameters);
            Log.d("网络返回:", requestHttp);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttp);
                netable.setName(str);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("4.1单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("4.1获取商户行业二级分类:", "ok");
        return netable;
    }

    public Netable getMyGoods(String str, String str2) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("pageAmount", "50"));
            arrayList.add(new BasicNameValuePair("page", str2));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_search_merchandise");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchant_search_merchandise");
            } 
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_search_merchandise");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("6.17 单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("6.17 检索我的商品:", "ok");
        return netable;
    }

    public Netable getNearByInfo(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            parameters = "api/local_industry?city=" + URLEncoder.encode(str);
            parameters += "&serverIP=" + Constants.PRO_IP;
            Log.d("参数:", parameters);
            String requestHttp = mHttpGetAndSent.requestHttp(WEB_HTTP + parameters);
            Log.d("网络返回:", requestHttp);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttp);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("2.4单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("2.4获取附近六个行业:", "ok");
        return netable;
    }

    public ArrayList<Netable> getProvince() {
        ArrayList arrayList = new ArrayList();
        Object obj = 1;
//        while (obj != null) {
            parameters = "public_data/province_cities?bussiness=1";
            Log.d("Parameter:", parameters);
            String requestHttp = mHttpGetAndSent.requestHttp(WEB_HTTP + parameters);
            Log.d("Result:", requestHttp);
            try {
                arrayList = mInfoAnalysis.jsonObj(requestHttp).getProvinces();                
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("2.5 is miss:", "..");
                obj = 1;
                return null;
            }
//        }
        Log.d("2.5:", "ok");
        return arrayList;
    }

    public Netable getSaveNum(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_get_collect_count");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchant_get_collect_count");
            } 
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_get_collect_count");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("6.11单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("6.11获取收藏数:", "ok");
        return netable;
    }

    public Netable getStartAD(String str) throws TimeOutException, JsonException {
        Netable netable = new Netable();
        parameters = "api/start_ad_pic?city=" + URLEncoder.encode(str);
        parameters += "&serverIP=" + Constants.PRO_IP;
        Log.d("参数:", parameters);
        String requestHttp = mHttpGetAndSent.requestHttp(WEB_HTTP + parameters);
        Log.d("网络返回:", requestHttp);
        return mInfoAnalysis.jsonObj(requestHttp);
    }

    public Netable getStoreDetail(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_detail");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchant_detail");
            } 
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_detail");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("4.5单次错误:", "..");
                obj = 1;
                return null;
            }
//        }
        Log.d("4.5 获得商户详细描述:", "ok");
        return netable;
    }

    public Netable getStoreUserMerchants() {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_user_type");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/get_user_type");
            } 
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_user_type");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("6.1单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("6.1 是否商户认证用户:", "ok");
        return netable;
    }

    public Netable getUserCollect(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("pageAmount", "50"));
            arrayList.add(new BasicNameValuePair("page", str));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/list_user_collect");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/list_user_collect");
            } 
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/list_user_collect");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("5.7单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("5.7 获得收藏收听列表:", "ok");
        return netable;
    }

    public Netable getWordAD(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            parameters = "api/main_ad_word?city=" + URLEncoder.encode(str);
            parameters += "&serverIP=" + Constants.PRO_IP;
            Log.d("参数:", parameters);
            String requestHttp = mHttpGetAndSent.requestHttp(WEB_HTTP + parameters);
            Log.d("网络返回:", requestHttp);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttp);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("3.2单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("3.2获取文字类广告:", "ok");
        return netable;
    }

    public Netable get_back_money(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("firstOrderId", str));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_back_money");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/get_back_money");
            } 
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_back_money");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("11.5单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d(" 11.5 退款申请:", "ok");
        return netable;
    }

    public Netable get_contact() {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("city", PreferfenceUtils.getCityPreferences(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_contact");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/get_contact");
            } 
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_contact");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("5.9单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("5.9 获得联系信息:", "ok");
        return netable;
    }

    public Netable get_coupon(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("couponId", str));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_coupon");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/get_coupon");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_coupon");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("7.2单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("7.2 优惠券领取:", "ok");
        return netable;
    }

    public Netable get_survey() {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "token"));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_survey");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/get_survey");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_survey");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("5.8单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("5.8 获得调查问卷:", "ok");
        return netable;
    }

    public Netable get_user_score() {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_user_score");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/get_user_score");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_user_score");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("5.4单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("5.4 获得用户积分:", "ok");
        return netable;
    }

    public Netable getusertype() {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_user_type");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/get_user_type");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/get_user_type");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("4.5单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("4.5 获得商户详细描述:", "ok");
        return netable;
    }

    public Netable guessLikeMerchant(String str, String str2, String str3) {
        Log.d("NetConnection", "city:" + str + ",x:" + str2 + ",y:" + str3);
        Netable netable = new Netable();
        int i = 1;
//        while (i != 0) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("city", str));
            arrayList.add(new BasicNameValuePair("x", str2));
            arrayList.add(new BasicNameValuePair("y", str3));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/guess_like_merchant");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/guess_like_merchant");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/guess_like_merchant");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                i = 0;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("4.10 单次错误:", "..");
                i = 1;
            }
//        }
        Log.d("4.10 猜你喜欢:", "ok");
        return netable;
    }

    public Netable join_merchant(String str, String str2, String str3, String str4) throws TimeOutException, JsonException {
        Netable netable = new Netable();
        List arrayList = new ArrayList();
        arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
        arrayList.add(new BasicNameValuePair("token", "1234567"));
        arrayList.add(new BasicNameValuePair("name", str));
        arrayList.add(new BasicNameValuePair("address", str2));
        arrayList.add(new BasicNameValuePair("tel", str3));
        arrayList.add(new BasicNameValuePair("connectPhone", str4));
        arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
        String requestHttpsPost;
        if(isUserHttps) {
        	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/ask_join_merchant");
        } else {
        	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/ask_join_merchant");
        }
//        String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/ask_join_merchant");
        Log.d("NetConnection", "网络返回::" + requestHttpsPost);
        netable = mInfoAnalysis.jsonObj(requestHttpsPost);
        Log.d("6.15 申请加入周边商户:", "ok");
        return netable;
    }

    public Word keyword_hint(String str, String str2, String str3, String str4, String str5, String str6) {
        Word word = new Word();
        Object obj = 1;
//        while (obj != null) {
            Log.d("NetConnection", "word:" + str6);
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("city", str));
            arrayList.add(new BasicNameValuePair("area", str2));
            arrayList.add(new BasicNameValuePair("zone", str3));
            arrayList.add(new BasicNameValuePair("firstType", str4));
            arrayList.add(new BasicNameValuePair("secondType", str5));
            arrayList.add(new BasicNameValuePair("word", str6));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/keyword_hint");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/keyword_hint");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/keyword_hint");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                word = mInfoAnalysis.jsonObj2(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("4.8单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("4.8 获取关键字提示:", "ok");
        return word;
    }

    public Netable list_comments(String str, String str2) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("pageAmount", "10"));
            arrayList.add(new BasicNameValuePair("page", str2));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/list_comments");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/list_comments");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/list_comments");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("4.6单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("4.6 获得商户评论:", "ok");
        return netable;
    }

    public Netable list_images(String str, String str2, String str3) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("isMerchantUser", str3));
            arrayList.add(new BasicNameValuePair("pageAmount", "10"));
            arrayList.add(new BasicNameValuePair("page", str2));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/list_images");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/list_images");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/list_images");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("4.7单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("4.7 商户相册 图片集:", "ok");
        return netable;
    }

    public Netable main_ad_detail(String str, int i) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            parameters = "api/main_ad_detail?city=" + URLEncoder.encode(str) + "&adId=" + i;
            parameters += "&serverIP=" + Constants.PRO_IP;
            Log.d("参数:", parameters);
            String requestHttpsGet;
            if(isUserHttps) {
            	requestHttpsGet = mHttps.requestHttpsGet(WEB_HTTPS + parameters);
            } else {
            	requestHttpsGet = mHttpGetAndSent.requestHttp(WEB_HTTP + parameters);
            }
//            String requestHttpsGet = mHttps.requestHttpsGet(WEB_HTTPS + parameters);
            Log.d("网络返回:", requestHttpsGet);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsGet);
                netable.setName(str);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("3.3单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("3.3获取获取广告详情:", "ok");
        return netable;
    }

    public Netable merchant_ad(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8) throws TimeOutException, JsonException {
        Netable netable = new Netable();
        List arrayList = new ArrayList();
        arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
        arrayList.add(new BasicNameValuePair("merchantId", str));
        arrayList.add(new BasicNameValuePair("startTime", str2));
        arrayList.add(new BasicNameValuePair("endTime", str3));
        arrayList.add(new BasicNameValuePair("title", str4));
        arrayList.add(new BasicNameValuePair("content", str5));
        arrayList.add(new BasicNameValuePair("description", str6));
        arrayList.add(new BasicNameValuePair("openTime", str7));
        arrayList.add(new BasicNameValuePair("travel", str8));
        arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
        String requestHttpsPost;
        if(isUserHttps) {
        	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_ad");
        } else {
        	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchant_ad");
        }
//        String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_ad");
        Log.d("NetConnection", "网络返回::" + requestHttpsPost);
        netable = mInfoAnalysis.jsonObj(requestHttpsPost);
        Log.d("4.4 查找附近的商户:", "ok");
        return netable;
    }

    public Netable merchant_list_word(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_list_word");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchant_list_word");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_list_word");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("6.9单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("6.9获取收藏数:", "ok");
        return netable;
    }

    public Netable merchant_modify_word(String str, String str2, String str3) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("operate", str2));
            arrayList.add(new BasicNameValuePair("word", str3));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_modify_word");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchant_modify_word");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_modify_word");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("6.9单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("6.9获取收藏数:", "ok");
        return netable;
    }

    public Netable merchant_modify_xy(String str, String str2, String str3) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("x", str2));
            arrayList.add(new BasicNameValuePair("y", str3));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_modify_xy");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchant_modify_xy");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_modify_xy");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("6.11单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("6.11获取收藏数:", "ok");
        return netable;
    }

    public Netable merchant_notify(String str, String str2) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("message", str2));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_notify");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchant_notify");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_notify");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("6.6单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("6.6申请发起新的通知:", "ok");
        return netable;
    }

    public Netable merchant_of_merchandise(String str, String str2, String str3, String str4, String str5) {
        Log.d("NetConnection", "id:" + str + ",pag:" + str2 + ",x:" + str3 + ",y:" + str4);
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchandiseId", str));
            arrayList.add(new BasicNameValuePair("filterMerchantId", "-1"));
            arrayList.add(new BasicNameValuePair("x", str3));
            arrayList.add(new BasicNameValuePair("y", str4));
            arrayList.add(new BasicNameValuePair("pageAmount", str5));
            arrayList.add(new BasicNameValuePair("page", str2));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_of_merchandise");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchant_of_merchandise");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_of_merchandise");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("10.3 单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("10.3 商品适用商户:", "ok");
        return netable;
    }

    public Netable merchant_operate_img(String str, String str2, String str3, String str4, String str5) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("imageId", str2));
            arrayList.add(new BasicNameValuePair("operate", str5));
            arrayList.add(new BasicNameValuePair("title", str3));
            arrayList.add(new BasicNameValuePair("description", str4));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_operate_img");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchant_operate_img");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_operate_img");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("6.5单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("6.5商户图片操作:", "ok");
        return netable;
    }

    public Netable merchant_report_comment(String str, String str2, String str3) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("commentId", str2));
            arrayList.add(new BasicNameValuePair("reason", str3));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_report_comment");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchant_report_comment");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_report_comment");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("6.12单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("6.12举报评论:", "ok");
        return netable;
    }

    public Netable modify_merchant_info(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8) throws TimeOutException, JsonException {
        Netable netable = new Netable();
        Log.d("NetConnection", "fullname:" + str2 + ",name:" + str3 + ",address:" + str4 + ",tel:" + str5 + ",description:" + str6 + ",openTime:" + str7 + ",travel:" + str8);
        List arrayList = new ArrayList();
        arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
        arrayList.add(new BasicNameValuePair("merchantId", str));
        arrayList.add(new BasicNameValuePair("fullName", str2));
        arrayList.add(new BasicNameValuePair("shortName", str3));
        arrayList.add(new BasicNameValuePair("address", str4));
        arrayList.add(new BasicNameValuePair("tel", str5));
        arrayList.add(new BasicNameValuePair("token", "1234567"));
        arrayList.add(new BasicNameValuePair("description", str6));
        arrayList.add(new BasicNameValuePair("openTime", str7));
        arrayList.add(new BasicNameValuePair("travel", str8));
        arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
        String requestHttpsPost;
        if(isUserHttps) {
        	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/modify_merchant_info");
        } else {
        	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/modify_merchant_info");
        }
//        String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/modify_merchant_info");
        Log.d("NetConnection", "网络返回::" + requestHttpsPost);
        netable = mInfoAnalysis.jsonObj(requestHttpsPost);
        Log.d("4.4 查找附近的商户:", "ok");
        return netable;
    }

    public Netable new_discount(String str, String str2) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("content", str2));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/new_discount");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/new_discount");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/new_discount");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("6.11单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("6.11获取收藏数:", "ok");
        return netable;
    }

    public Netable offBillCode(String str, String str2) throws TimeOutException, JsonException {
        Netable netable = new Netable();
        List arrayList = new ArrayList();
        arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
        arrayList.add(new BasicNameValuePair("token", "1234567"));
        arrayList.add(new BasicNameValuePair("merchantId", str));
        arrayList.add(new BasicNameValuePair("code", str2));
        arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
        String requestHttpsPost;
        if(isUserHttps) {
        	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_use_merchandise_code");
        } else {
        	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/merchant_use_merchandise_code");
        }
//        String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/merchant_use_merchandise_code");
        Log.d("NetConnection", "网络返回::" + requestHttpsPost);
        netable = mInfoAnalysis.jsonObj(requestHttpsPost);
        Log.d("6.18 销码:", "ok");
        return netable;
    }

    public Netable push_count() {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/push_count");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/push_count");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/push_count");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("8.1单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("8.1 查询PUSH数量:", "ok");
        return netable;
    }

    public Netable push_coupon() {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/push_coupon");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/push_coupon");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/push_coupon");
            Log.d("NetConnection", "网络返回: :" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("8.3单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("8.3 获得商户优惠劵通知:", "ok");
        return netable;
    }

    public Netable push_notify() {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/push_notify");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/push_notify");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/push_notify");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("8.2单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("8.2 获得商户通知:", "ok");
        return netable;
    }

    public Netable refreshTokenPrivate() {
        Netable netable = new Netable();
        int i = 1;
//        while (i != 0) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/refresh_token_private");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/refresh_token_private");
            }          
            
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                i = 0;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("9.2单次错误:", "..");
                i = 1;
            }
//        }
        Log.d(" 9.2TOKEN刷新:", "ok");
        return netable;
    }

    public Netable repay_first_order(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("firstOrderId", str));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/repay_first_order");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/repay_first_order");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/repay_first_order");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("11.3 单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("11.3 放弃订单:", "ok");
        return netable;
    }

    public Netable report_error(String str, String str2, String str3) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("merchantId", str));
            arrayList.add(new BasicNameValuePair("kind", str2));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("content", str3));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/report_error");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/report_error");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/report_error");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("5.3单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("5.3用户纠错:", "ok");
        return netable;
    }

    public Netable report_software_error(String str) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("content", str));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/report_software_error");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/report_software_error");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/report_software_error");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("5.5单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("5.5 用户反馈:", "ok");
        return netable;
    }

    public Netable search(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("city", str));
            arrayList.add(new BasicNameValuePair("area", str2));
            arrayList.add(new BasicNameValuePair("zone", str3));
            arrayList.add(new BasicNameValuePair("firstType", str4));
            arrayList.add(new BasicNameValuePair("secondType", str5));
            arrayList.add(new BasicNameValuePair("x", str6));
            arrayList.add(new BasicNameValuePair("sales", "0"));
            arrayList.add(new BasicNameValuePair("y", str7));
            arrayList.add(new BasicNameValuePair("word", str8));
            arrayList.add(new BasicNameValuePair("sort", str9));
            arrayList.add(new BasicNameValuePair("inc", str11));
            arrayList.add(new BasicNameValuePair("pageAmount", "10"));
            arrayList.add(new BasicNameValuePair("page", str10));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            Log.d("NetConnection", PreferfenceUtils.getPRODUCT(mContext) + "," + str + "," + str2 + "," + str3 + "," + str4 + "," + str5 + "," + str8 + "," + str9 + ",x" + str6 + ",y" + str7);
            Log.d("NetConnection", Constants.PRO_IP + "api/search_merchant_keyword");
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/search_merchant_keyword");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/search_merchant_keyword");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/search_merchant_keyword");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                obj = 1;
                Log.d("4.3单次错误:", "..");
                return null;
            }
//        }
        Log.d("4.3根据关键字搜索商户:", "ok");
        return netable;
    }

    public Netable search(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, int i) {
        Netable netable = new Netable();
        netable = search(str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11);
        if(netable == null)
        	return null;
        netable.setFLAG(i);
        return netable;
    }

    public Netable searchNearBy(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            Log.d("NetConnection", "city:" + str + ",firstType:" + str2 + ",secondType:" + str3 + ",distance:" + str6 + ",sort:" + str7);
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("city", str));
            arrayList.add(new BasicNameValuePair("firstType", str2));
            arrayList.add(new BasicNameValuePair("secondType", str3));
            arrayList.add(new BasicNameValuePair("x", str4));
            arrayList.add(new BasicNameValuePair("y", str5));
            arrayList.add(new BasicNameValuePair("distance", str6));
            arrayList.add(new BasicNameValuePair("sort", str7));
            arrayList.add(new BasicNameValuePair("token", "1234567"));
            arrayList.add(new BasicNameValuePair("inc", str9));
            arrayList.add(new BasicNameValuePair("filterMerchantId", "-1"));
            arrayList.add(new BasicNameValuePair("pageAmount", "10"));
            arrayList.add(new BasicNameValuePair("page", str8));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/search_merchant_distant");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/search_merchant_distant");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/search_merchant_distant");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("4.4单次错误:", "..");
                obj = 1;
                return null;
            }
//        }
        Log.d("4.4 查找附近的商户:", "ok");
        return netable;
    }

    public Netable searchOtherbranch(String str, String str2, String str3, String str4, String str5) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("merchantId", str2));
            arrayList.add(new BasicNameValuePair("x", str3));
            arrayList.add(new BasicNameValuePair("y", str4));
            arrayList.add(new BasicNameValuePair("pageAmount", "10"));
            arrayList.add(new BasicNameValuePair("page", str5));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/search_merchant_other_branch");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/search_merchant_other_branch");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/search_merchant_other_branch");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("4.9单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("4.9 检索总店下的其他分店:", "ok");
        return netable;
    }

    public Netable search_first_order(ArrayList<NameValuePair> arrayList, String str, String str2, String str3) {
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList2 = new ArrayList();
            arrayList2.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList2.add(new BasicNameValuePair("token", "1234567"));
            arrayList2.addAll(arrayList);
            arrayList2.add(new BasicNameValuePair("pageAmount", "10"));
            arrayList2.add(new BasicNameValuePair("page", str));
            arrayList2.add(new BasicNameValuePair("sort1", str2));
            arrayList2.add(new BasicNameValuePair("sort2", str3));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/search_first_order");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/search_first_order");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList2, WEB_HTTPS + "api/search_first_order");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("11.4 单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("11.4 浏览订单:", "ok");
        return netable;
    }

    public Netable search_local_coupon(String str, String str2, String str3) {
        Log.d("NetConnection", "x:" + str + ",y" + str2);
        Netable netable = new Netable();
        Object obj = 1;
//        while (obj != null) {
            List arrayList = new ArrayList();
            arrayList.add(new BasicNameValuePair("phoneNo", PreferfenceUtils.getPRODUCT(mContext)));
            arrayList.add(new BasicNameValuePair("x", str));
            arrayList.add(new BasicNameValuePair("y", str2));
            arrayList.add(new BasicNameValuePair("pageAmount", "10"));
            arrayList.add(new BasicNameValuePair("page", str3));
            arrayList.add(new BasicNameValuePair("serverIP", Constants.PRO_IP));
            String requestHttpsPost;
            if(isUserHttps) {
            	requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/search_local_coupon");
            } else {
            	requestHttpsPost = mHttpGetAndSent.requestPostHttp(arrayList, WEB_HTTP + "api/search_local_coupon");
            }
//            String requestHttpsPost = mHttps.requestHttpsPost(arrayList, WEB_HTTPS + "api/search_local_coupon");
            Log.d("NetConnection", "网络返回::" + requestHttpsPost);
            try {
                netable = mInfoAnalysis.jsonObj(requestHttpsPost);
                obj = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("7.1单次错误:", "..");
                obj = 1;
            }
//        }
        Log.d("7.1 在附近查找优惠券:", "ok");
        return netable;
    }
}
