package com.corpize.sdk.ivoice.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.corpize.sdk.ivoice.bean.AppUserBean;
import com.corpize.sdk.ivoice.common.Constants;

/**
 * author : xpSun
 * date : 12/23/21
 * description :
 */
public class QCTransparentWebViewUtils {

    private static final String COMMON_SPLIT_TAG = ",";

    private Handler handler = new Handler(Looper.getMainLooper());

    private static QCTransparentWebViewUtils instance;
    private        Runnable                  runnable;

    public static QCTransparentWebViewUtils getInstance () {
        if (null == instance) {
            instance = new QCTransparentWebViewUtils();
        }
        return instance;
    }

    public void remove () {
        try {
            if(null == handler){
                return;
            }

            handler.removeCallbacks(runnable);
            handler.removeCallbacksAndMessages(null);
            handler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadWebView (final Context context) {
        try {
            runnable = new Runnable() {
                @Override
                public void run () {
                    WebView webView = new WebView(context);
                    initWidgets(webView);
                }
            };
            handler.post(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initWidgets (WebView webView) {
        try {
            // 设置支持JavaScript
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);

            webView.addJavascriptInterface(new CustomJavascriptInterface(webView.getContext()), "twv");

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading (WebView view, String url) {
                    return false;
                }
            });

            // 加载网页
            webView.loadUrl(Constants.WIFI_LOCATION_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CustomJavascriptInterface {
        public CustomJavascriptInterface (Context context) {

        }

        @JavascriptInterface
        public void postMessage (String message) {
            try {
                if (!TextUtils.isEmpty(message)
                        && message.contains(COMMON_SPLIT_TAG)) {
                    String[] strs = message.split(COMMON_SPLIT_TAG);

                    if (strs.length > 1) {
                        //经度
                        AppUserBean.getInstance().setLon(Double.valueOf(strs[0]));
                        //纬度
                        AppUserBean.getInstance().setLat(Double.valueOf(strs[1]));

                        remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
