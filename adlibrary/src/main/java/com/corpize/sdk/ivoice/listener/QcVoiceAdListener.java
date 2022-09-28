package com.corpize.sdk.ivoice.listener;

import com.corpize.sdk.ivoice.admanager.QcAdManager;

/**
 * author : xpSun
 * date : 7/15/21
 * description :
 */
public interface QcVoiceAdListener extends QCADListener {

    //返回管理类
    void onAdReceive (QcAdManager manager);

    //广告播放结束,待播放音频互动
    void onAdPlayEndListener ();

}
