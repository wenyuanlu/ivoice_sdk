package com.corpize.sdk.ivoice.listener;

import android.view.View;

import com.corpize.sdk.ivoice.utils.MediaPlayerUtil;

/**
 * author : xpSun
 * date : 7/8/21
 * description :
 */
public interface OnFirstVoicePlayListener extends MediaPlayerUtil.MediaOnListener {

    //网络请求加载失败
    void onQcErrorListener(String error,int code);

    //播放完成
    void onAdCompletion ();

}
