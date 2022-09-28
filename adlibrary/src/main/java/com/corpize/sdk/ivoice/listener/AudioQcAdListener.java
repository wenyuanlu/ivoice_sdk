package com.corpize.sdk.ivoice.listener;

import com.corpize.sdk.ivoice.admanager.QcAdManager;

/**
 * author: yh
 * date: 2020-02-21 21:21
 * description: Banner广告的回调
 */
public interface AudioQcAdListener extends QCADListener {

    void onAdReceive (QcAdManager manager);//返回管理类

    void onAdClose ();

}
