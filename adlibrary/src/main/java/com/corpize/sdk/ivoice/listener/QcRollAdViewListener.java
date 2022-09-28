package com.corpize.sdk.ivoice.listener;

import android.view.View;

import com.corpize.sdk.ivoice.admanager.QcAdManager;

/**
 * author : xpSun
 * date : 11/22/21
 * description :贴片广告
 */
public interface QcRollAdViewListener extends QCADListener {

    //返回管理类
    void onAdReceive (QcAdManager manager, View view);

    //贴片广告曝光
    void onAdExposure ();

    //点击了关闭广告
    void onRollAdClickClose ();

    //展示dialog
    void onRollAdDialogShow ();

    //dialog dismiss
    void onRollAdDialogDismiss ();

    //音频点击修改
    void onRollVolumeChanger (int status);

}
