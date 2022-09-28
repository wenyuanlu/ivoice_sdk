package com.corpize.sdk.ivoice.listener;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.corpize.sdk.ivoice.admanager.QcAdManager;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;

import java.util.List;

/**
 * author : xpSun
 * date : 12/8/21
 * description :
 */
public interface QcCustomTemplateListener extends QCADListener {

    //返回管理类
    void onAdReceive (QcAdManager manager, View adView);

    //获取广告主标题
    void fetchMainTitle (String title);

    //手动点击了广告的跳过
    void onAdSkipClick ();

    //获取内容控件
    void onFetchAdContentView (
            TextView adTipView,//左上角 广告 标识
            LinearLayout skipLayout,//右上角跳过布局
            TextView mainTitleView,//下方主标题
            TextView subtitleView,//icon 右侧副标题
            TextView understandDescView//右下角了解详情
    );

    //返回广告的请求实体
    default void onFetchApiResponse(AdAudioBean adAudioBean){

    }

    //返回广告的曝光
    default void onFetchAdsSendShowExposure(String exposure){

    }

    default void onPlayCompletionListener(){

    }

    default void onFetchInteractionTimer(int duration){

    }
}
