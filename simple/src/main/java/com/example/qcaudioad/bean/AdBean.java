package com.example.qcaudioad.bean;

import android.view.View;

import com.corpize.sdk.ivoice.admanager.QcAdManager;

public class AdBean {
    private View        adView;//音频时长
    private boolean     isAd;//音频地址
    private QcAdManager qcAdManager;//音频广告管理

    public AdBean (QcAdManager qcAdManager) {
        this.qcAdManager = qcAdManager;
        this.isAd = true;
    }

    public AdBean (View view, boolean isAd) {
        this.adView = view;
        this.isAd = isAd;
    }

    public AdBean (View view, QcAdManager qcAdManager, boolean isAd) {
        this.qcAdManager = qcAdManager;
        this.adView = view;
        this.isAd = isAd;
    }

    public View getAdView () {
        return adView;
    }

    public void setAdView (View adView) {
        this.adView = adView;
    }

    public boolean isAd () {
        return isAd;
    }

    public void setAd (boolean ad) {
        isAd = ad;
    }

    public QcAdManager getQcAdManager () {
        return qcAdManager;
    }

    public void setQcAdManager (QcAdManager qcAdManager) {
        this.qcAdManager = qcAdManager;
    }
}
