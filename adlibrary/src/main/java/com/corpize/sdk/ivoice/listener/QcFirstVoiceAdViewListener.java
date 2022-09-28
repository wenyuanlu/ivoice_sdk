package com.corpize.sdk.ivoice.listener;

import android.view.View;

import com.corpize.sdk.ivoice.admanager.QcAdManager;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;

import java.util.List;

/**
 * author : xpSun
 * date : 7/9/21
 * description :
 */
public interface QcFirstVoiceAdViewListener extends QCADListener {

    //首听关闭
    void onFirstVoiceAdClose ();

    //首听倒计时结束
    void onFirstVoiceAdCountDownCompletion ();

    //播放首听
    void onAdExposure ();

    //返回管理类
    void onAdReceive (QcAdManager manager, View adView);

    //返回广告的请求实体
    default void onFetchApiResponse (AdAudioBean adAudioBean) {

    }

    //返回广告的曝光
    default void onFetchAdsSendShowExposure (String exposure) {

    }
}
