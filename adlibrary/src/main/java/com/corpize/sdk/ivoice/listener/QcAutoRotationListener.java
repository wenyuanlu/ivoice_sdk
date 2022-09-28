package com.corpize.sdk.ivoice.listener;

import android.view.View;

import com.corpize.sdk.ivoice.admanager.QcAdManager;

/**
 * author : xpSun
 * date : 2022/2/25
 * description :
 */
public interface QcAutoRotationListener extends QCADListener {


    //返回管理类
    void onAdReceive (QcAdManager manager, View adView);
}
