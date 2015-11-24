package cn.yjt.oa.app.cardpack;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.component.TitleFragmentActivity;

/**
 * Created by 熊岳岳 on 2015/9/24.
 */
public class CardPackActivity extends TitleFragmentActivity implements View.OnKeyListener {

    public static String KEY = "mvKkBjbZos3i6Sy4";

    private WebView mWvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardpack);
        initView();
        fillData();
        setListener();


    }

    private void initView() {
        mWvShow = (WebView) findViewById(R.id.wv_show);
    }

    private void fillData() {

        getLeftbutton().setImageResource(R.drawable.navigation_back);

        mWvShow.setHorizontalScrollBarEnabled(false);
        mWvShow.setVerticalScrollBarEnabled(false);
        mWvShow.getSettings().setJavaScriptEnabled(true);
        mWvShow.getSettings().setUseWideViewPort(true);
        mWvShow.getSettings().setLoadWithOverviewMode(true);
        mWvShow.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWvShow.loadUrl(setUrl());
        mWvShow.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void setListener() {
        mWvShow.setOnKeyListener(this);
    }


    private String setUrl() {
        Map<String, String> param = new HashMap();
        param.put("channel", "A01152634730");
        param.put("userId", AccountManager.getCurrent(this).getPhone()+"");
        param.put("type", "cardpack");
        param.put("time", Long.toString(System.currentTimeMillis() / 1000));
//        param.put("time",Long.toString(1442977215));
        List<String> sortedKeys = new ArrayList(param.keySet());
        Collections.sort(sortedKeys);
        StringBuffer sb = new StringBuffer();
        for (String str : sortedKeys) {
            sb.append(param.get(str));
        }
        String sign = MD5Util.hex(MD5Util.hex(sb.toString().toLowerCase()) + KEY);
        param.put("sign", sign);
        String url = HttpRequest.append("http://y.buslive.cn/wcard/index.php/site/from", param);
        Log.d("KEY", url);
        return url;
    }

    @Override
    public void onLeftButtonClick() {
        if(mWvShow.canGoBack()) {
            mWvShow.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK && mWvShow.canGoBack()) {  //表示按返回键 时的操作
                mWvShow.goBack();   //后退
                return true;    //已处理
            }
        }
        return false;
    }
}
