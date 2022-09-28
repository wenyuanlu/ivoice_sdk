package com.corpize.sdk.ivoice.listener;

/**
 * author: yh
 * date: 2020-02-21 21:21
 * description: 回调的基类
 */
public interface QCADListener {

    void onAdClick ();

    void onAdCompletion ();//播放完成

    void onAdError (String fail);//出错,无广告时,其他错误的时候

    void onAdExposure();

}
