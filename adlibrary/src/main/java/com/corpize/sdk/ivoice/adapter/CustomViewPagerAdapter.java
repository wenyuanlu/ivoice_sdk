package com.corpize.sdk.ivoice.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * author : xpSun
 * date : 2022/2/22
 * description :
 */
public class CustomViewPagerAdapter extends PagerAdapter {
    private List<String> images = new ArrayList<>();

    public void setImages (List<String> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    @Override
    public int getCount () {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject (@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem (@NonNull ViewGroup container, int position) {
        WebView webView = new WebView(container.getContext());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setBlockNetworkImage(true);
        webSettings.setSupportZoom(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webSettings.setTextZoom(100);

        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        webView.setLayoutParams(layoutParams);
        String item = images.get(position % images.size());
        webView.loadUrl(item);
        container.addView(webView);
        return webView;
    }

    @Override
    public void destroyItem (@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof View) {
            container.removeView((View) object);
        }
    }
}
