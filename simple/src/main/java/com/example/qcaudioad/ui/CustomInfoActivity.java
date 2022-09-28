package com.example.qcaudioad.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.corpize.sdk.ivoice.AdAttr;
import com.corpize.sdk.ivoice.AdLayout;
import com.corpize.sdk.ivoice.CoverType;
import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.admanager.QcAdManager;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.listener.AudioCustomQcAdListener;
import com.example.qcaudioad.R;
import com.example.qcaudioad.base.BaseActivity;
import com.example.qcaudioad.common.ADIDConstants;
import com.example.qcaudioad.utils.CommonLabelUtils;
import com.example.qcaudioad.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomInfoActivity extends BaseActivity {

    private final String TAG = "CustomInfoActivity";

    private FrameLayout rootView;
    private QcAdManager mQcCustomAdManager;
    private TextView    status;

    private              int    currentAdStatus;
    private static final String CURRENT_AD_STATUS_TAG = "current_ad_status_tag";

    private static final int CURRENT_AD_STATUS_1 = 1;
    private static final int CURRENT_AD_STATUS_2 = 2;
    private static final int CURRENT_AD_STATUS_3 = 3;
    private static final int CURRENT_AD_STATUS_4 = 4;
    private static final int CURRENT_AD_STATUS_5 = 5;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_info_layout);
        setTitle("自定义嵌入式");

        TextView load  = findViewById(R.id.custom_info_ad_load);
        TextView show  = findViewById(R.id.custom_info_ad_show);
        TextView stop  = findViewById(R.id.custom_info_ad_stop);
        TextView clear = findViewById(R.id.custom_info_ad_clear);

        status = findViewById(R.id.custom_info_ad_status);
        rootView = findViewById(R.id.custom_info_root_view);

        load.setOnClickListener(this);
        show.setOnClickListener(this);
        stop.setOnClickListener(this);
        clear.setOnClickListener(this);

        initData();

        if (null != savedInstanceState) {
            Log.e("customInfoActivity", "savedInstanceState");
            if (savedInstanceState.containsKey(CURRENT_AD_STATUS_TAG)) {
                Log.e("customInfoActivity", "CURRENT_AD_STATUS_TAG");
                currentAdStatus = savedInstanceState.getInt(CURRENT_AD_STATUS_TAG);
                Log.e("currentAdStatus", String.valueOf(currentAdStatus));
                load.postDelayed(new Runnable() {
                    @Override
                    public void run () {
                        switch (currentAdStatus) {
                            case CURRENT_AD_STATUS_1:
                            case CURRENT_AD_STATUS_2:
                                initInfoVoice();
                                break;
                            case CURRENT_AD_STATUS_3:
                            case CURRENT_AD_STATUS_4:
                                initInfoVoice();
                                if (mQcCustomAdManager != null) {
                                    mQcCustomAdManager.skipPlayAd();
                                }
                                break;
                            case CURRENT_AD_STATUS_5:
                                break;
                        }
                    }
                }, 1000);
            }
        }
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.custom_info_ad_load:
                initInfoVoice();
                break;
            case R.id.custom_info_ad_show:
                if (mQcCustomAdManager == null) {
                    showLoadAdErrorToast();
                    return;
                }
                mQcCustomAdManager.startPlayAd();
                status.setText(String.format("当前广告状态:%s", "startPlayAd"));
                break;
            case R.id.custom_info_ad_stop:
                if (mQcCustomAdManager == null) {
                    showLoadAdErrorToast();
                    return;
                }
                mQcCustomAdManager.skipPlayAd();
                status.setText(String.format("当前广告状态:%s", "skipPlayAd"));
                break;
            case R.id.custom_info_ad_clear:
                if (mQcCustomAdManager == null) {
                    showLoadAdErrorToast();
                    return;
                }
                mQcCustomAdManager.skipPlayAd();
                mQcCustomAdManager.destroy();
                rootView.removeAllViews();
                mQcCustomAdManager = null;
                status.setText(String.format("当前广告状态:%s", "destroy"));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (0 != currentAdStatus) {
            outState.putInt(CURRENT_AD_STATUS_TAG, currentAdStatus);
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume () {
        super.onResume();
        //必须调用
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onResume();
        }
    }

    @Override
    protected void onPause () {
        super.onPause();
        //必须调用
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onPause();
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        //释放内存
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onDestroy();
        }
    }

    //自定义嵌入式广告
    private void initInfoVoice () {
        //第一步,设置广告的adId和自定义的参数
        AdAttr attr = fetchAdAttr();

        //第二步,创建广告调用,必须调用
        QCiVoiceSdk.get().createAdNative(CustomInfoActivity.this);
        //第三步,获取广告
        QCiVoiceSdk.get().addCustomAudioAd(attr, new AudioCustomQcAdListener() {
            @Override
            public void onAdReceive (QcAdManager manager, View adView) {
                Log.e(TAG, "onAdReceive");
                status.setText(String.format("当前广告状态:%s", "onAdReceive"));

                mQcCustomAdManager = manager;

                //第四步,展示广告
                //把返回的view添加到界面控件中
                rootView.setVisibility(View.VISIBLE);
                rootView.removeAllViews();
                if (adView != null) {
                    rootView.addView(adView);
                }

                currentAdStatus = CURRENT_AD_STATUS_1;
            }

            @Override
            public void onAdExposure () {
                Log.e(TAG, "onAdExposure");
                status.setText(String.format("当前广告状态:%s", "onAdExposure"));
                currentAdStatus = CURRENT_AD_STATUS_2;
            }

            @Override
            public void onAdUserInfo (String userId, String avater) {
                //弹幕头像点击的时候,返回上传的userId的信息
                Log.e(TAG, "onAdUserInfo" + " |userId=" + userId + " |avater=" + avater);
                status.setText(String.format("当前广告状态:%s", "onAdUserInfo" + " |userId=" + userId + " |avater=" + avater));

                if (!TextUtils.isEmpty(userId)) {
                    Toast.makeText(CustomInfoActivity.this, "返回的用户ID=" + userId, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onAdClick () {
                Log.e(TAG, "onAdClick");
                status.setText(String.format("当前广告状态:%s", "onAdClick"));
                currentAdStatus = CURRENT_AD_STATUS_3;
            }

            @Override
            public void onAdCompletion () {
                Log.e(TAG, "onAdCompletion");
                status.setText(String.format("当前广告状态:%s", "onAdCompletion"));
                currentAdStatus = CURRENT_AD_STATUS_4;
            }

            @Override
            public void onAdError (String fail) {
                Log.e(TAG, fail);
                status.setText(String.format("当前广告状态:%s", "onAdError:" + fail));
                currentAdStatus = CURRENT_AD_STATUS_5;
            }
        });
    }

    private AdAttr fetchAdAttr () {
        Drawable     drawable                = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
        Drawable     palyDrawable            = ContextCompat.getDrawable(this, R.mipmap.icon_play);
        Drawable     pauseDrawable           = ContextCompat.getDrawable(this, R.mipmap.icon_pause);
        Drawable     barrageDrawable         = ContextCompat.getDrawable(this, R.mipmap.icon_barrage);
        Drawable     priseChooseDrawable     = ContextCompat.getDrawable(this, R.mipmap.icon_prise_choose);
        Drawable     priseDefaultDrawable    = ContextCompat.getDrawable(this, R.mipmap.icon_prise_default);
        List<String> barrageContentColorList = new ArrayList<>();
        barrageContentColorList.add("#CE608C");
        barrageContentColorList.add("#BC7D2F");
        barrageContentColorList.add("#5C72D5");
        barrageContentColorList.add("#23A69E");

        String customADID = QCiVoiceSdk.get().isDebug() ? ADIDConstants.TestEnum.INFO_ADID : ADIDConstants.ReleaseEnum.INFO_ADID;

        List<UserPlayInfoBean> userPlayInfoBeanDataList = CommonLabelUtils.getCommonLabels();

        return AdAttr.newBuild()
                .setAdid(customADID)//设置广告adid
                .setMid(ADIDConstants.MID)//设置广告的mid
                .setCanLeftTouch(false)//设置是否支持往左滑动跳出落地页等方法,默认false,设置true时,在list中会拦截原有的滑动事件
                //********设置背景图片的方法********//
                .setBackgroundLayout(AdLayout.TOP_LEFT)//设置背景图片的位置,默认是TOP_LEFT
                .setBackgroundMagin(0, 0, 0, 0)//设置背景图片的magin值,单位dp
                .setBackgroundSize(
                        ScreenUtils.getScreenWidth(this),
                        ScreenUtils.getRealyScreenHeight(this)
                )//设置背景图片的宽度和高度,单位px 默认match_parent
                //********设置Title的方法********//
                .setTitleColor(Color.parseColor("#FFFFFFFF"))//设置标题的颜色#FF3D3B3B
                .setTitleSize(18)//设置标题的大小,单位sp 字体范围10-18
                .setTitleLayout(AdLayout.TOP_LEFT)//设置标题的位置,默认是TOP_LEFT
                .setTitleMagin(15, 20, 0, 0)//设置标题的magin值,单位dp
                .setTitleTextMaxSize(5)//设置title单行最大字数
                .setTitleTextMaxLines(2)//设置title最大行数
                //********设置Content的方法********//
                .setContentColor(Color.parseColor("#FFFFFFFF"))//设置展示内容的颜色6D6D6D
                .setContentSize(14)//设置展示内容的大小,单位sp 字体范围10-14
                //.setContentLayout(AdLayout.TOP_LEFT)//设置展示内容的位置,默认是below,标题下方
                .setContentMagin(15, 0, 130, 0)//设置内容的magin值,单位dp
                //********设置左下角的Info窗口的方法********//
                .setInfoTitleColor(Color.parseColor("#FF333333"))//设置信息的标题颜色
                .setInfoContentColor(Color.parseColor("#FF666666"))//设置信息的内容颜色
                .setInfoButtonColor(Color.parseColor("#FFFFFFFF"))//设置信息内部按钮颜色
                .setInfoButtonBackgroundColor(Color.parseColor("#FF5BC0DE"))//设置信息内部按钮背景颜色
                .setInfoLayout(AdLayout.BOTTOM_LEFT)//设置信息的位置,默认是BOTTOM_LEFT
                .setInfoMagin(0, 0, 15, 20)//设置信息的magin值,单位dp
                //********设置右下角头像的方法********//
                .setAdHeadSize(40)//设置logo的大小,宽高一致,默认40
                .setAdHeadType(CoverType.ROUND)//设置logo的显示的样式,默认圆形-CoverType.ROUND圆形,CoverType.OVAL圆角
                .setShowHeadLinkImage(true)//设置是否在logo上方显示logo的图片,默认true显示
                .setAdHeadLayout(AdLayout.BOTTOM_RIGHT)//设置logo的位置,默认是BOTTOM_RIGHT
                .setAdHeadMagin(0, 0, 15, 190)//设置Logo的magin值,单位dp
                //********设置右下角点赞图片的方法********//
                //.setPraiseChooseImage(priseChooseDrawable)//设置点赞图片,不设置显示默认图片
                //.setPraiseDefaultImage(priseDefaultDrawable)//设置未点赞图片,不设置显示默认图片
                .setPraiseImageSize(40)//设置点赞图片的大小,单位dp,默认40
                .setPraiseImageLayout(AdLayout.BOTTOM_RIGHT)//设置点赞图片位置,默认是BOTTOM_RIGHT
                .setPraiseImageMagin(0, 0, 15, 130)//设置点赞图片的magin值,单位dp
                //********设置右下角点赞数量的方法********//
                .setPraiseNumberColor(Color.parseColor("#FFFFFFFF"))//设置点赞数量的颜色#FFFF5555
                .setPraiseNumberSize(12)//设置点赞数量的大小,单位sp 默认12
                .setPraiseNumberWidth(40)//设置点赞数量控件的宽度,单位dp 默认40
                .setPraiseNumberLayout(AdLayout.BOTTOM_RIGHT)//设置点赞数量的位置,默认是BOTTOM_RIGHT
                .setPraiseNumberMagin(0, 0, 15, 110)//设置点赞数量的的magin值,单位dp
                //********设置右下角弹幕图片的方法********//
                //.setBarrageImage(barrageDrawable)//设置弹幕图片,不设置显示默认图片
                .setBarrageImageSize(40)//设置弹幕图片的大小,单位dp,默认40
                .setBarrageImageLayout(AdLayout.BOTTOM_RIGHT)
                .setBarrageImageMagin(0, 0, 15, 60)//设置弹幕图片的magin值,单位dp
                //********设置右下角弹幕数量的方法********//
                .setBarrageNumberColor(Color.parseColor("#FFFFFFFF"))//设置弹幕数量的颜色#FFFF5555
                .setBarrageNumberSize(12)//设置弹幕数量的大小,单位sp,默认12
                .setBarrageNumberWidth(40)//设置弹幕数量控件的宽度,单位dp 默认40
                .setBarrageNumberLayout(AdLayout.BOTTOM_RIGHT)//设置弹幕数量的位置,默认是BOTTOM_RIGHT
                .setBarrageNumberMagin(0, 0, 15, 40)//设置弹幕数量的magin值,单位dp
                //********设置中间封面图片的方法********//
                .setCoverSize(200)//设置封面图片的大小 ,单位dp 默认
                .setCoverType(CoverType.ROUND)//设置封面图片显示的样式,默认圆形-CoverType.ROUND圆形,CoverType.OVAL圆角
                .setCoverLayout(AdLayout.CENTER_HORIZONTAL)//设置封面图片的位置,默认是CENTER_IN_PARENT 整体居中显示
                .setCoverMagin(0, 150, 0, 0)//设置封面图片的magin值,单位dp
                //********设置封面上播放按钮的方法********//
                //.setMusicBtPlayImage(palyDrawable)//设置播放按钮的图片,不设置显示默认图片
                //.setMusicBtPauseImage(pauseDrawable)//设置暂停按钮的图片,不设置显示默认图片
                .setMusicBtSize(50)//设置播放按钮的大小 ,单位dp 默认50
                //.setMusicBtLayout(AdLayout.TOP_LEFT, false)//设置播放按钮的位置,默认是CENTER_IN_PARENT(基于封面布局),也可设置isToCover=false(基于整个广告设置位置)
                .setMusicBtLayout(AdLayout.CENTER_IN_PARENT)//设置播放按钮的位置,默认是CENTER_IN_PARENT(基于封面布局)
                .setMusicBtMagin(10, 60, 30, 10)//设置播放按钮的magin值,单位dp
                //********设置滚动弹幕方法********//
                .setShowBarrage(true)//是否展示弹幕
                .setBarrageContentSize(12)//设置滚动弹幕内容的大小 单位sp 默认14
                .setBarrageContentColor(barrageContentColorList)//设置滚动弹幕内容的颜色,颜色随机
                .setBarrageBackColor("#DDDDDD")//设置滚动弹幕背景颜色
                .setBarrageHeadSize(18)//设置滚动弹幕头像的大小 单位dp 默认ContentSize+6
                .setBarrageMagin(0, 50, 0, 100)//设置滚动弹幕的magin值,单位dp
                .setBarrageSpeed(3)//设置滚动弹幕的滚动速度,默认为3,建议范围3-10
                //********设置跳过********//
                .setSkipIsEnable(true)//是否启用跳过
                .setSkipGravity(RelativeLayout.ALIGN_PARENT_RIGHT)//设置跳过控件位置,具体参见RelativeLayout.LayoutParams.addRule()方法
                .setSkipMargin(0, 15, 15, 0)
                .setSkipAutoClose(false)//设置跳过倒计时结束后是否自动关闭该广告
                //********设置是否启用右下角********//
                .setEnableRightView(false)
                //********填入播放过的音频信息,非必填********//
                .setLabel(userPlayInfoBeanDataList)
                ;
    }

    private void initData () {
        //如果要使用弹幕功能,可以选择传递当前用户的userId和头像地址,sdk会在弹幕点击的时候,展示头像,返回userId,
        //方便后续客户端添加自定的用户界面的跳转操作.
        setAdUserInfo("10000", "http://resource.corpize.com/image/shufujia_202.jpeg");
    }

    /**
     * 传递用户的信息,用于弹幕的点击操作
     *
     * @param userId
     * @param avater
     */
    private void setAdUserInfo (String userId, String avater) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("avatar", avater);
        QCiVoiceSdk.get().setUserInfo(map);
    }
}
