package com.corpize.sdk.ivoice.listener;

import android.view.View;

import com.corpize.sdk.ivoice.admanager.QcAdManager;

/**
 * author: yh
 * date: 2020-02-21 21:21
 * description: Banner广告的回调
 */
public interface AudioCustomQcAdListener extends QCADListener {

    void onAdReceive (QcAdManager manager, View adView);//返回管理类

    void onAdUserInfo (String userId, String avater);

}
