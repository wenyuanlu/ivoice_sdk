package com.corpize.sdk.ivoice.admanager;

import android.app.Activity;
import android.content.Intent;

import com.corpize.sdk.ivoice.bean.AdResponseBean;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.common.ErrorUtil;
import com.corpize.sdk.ivoice.listener.QcFirstVoiceAdViewListener;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;
import com.corpize.sdk.ivoice.view.QcFirstVoiceView;

import java.util.List;

/**
 * author : xpSun
 * date : 7/8/21
 * description :
 */
public class QcFirstVoiceAdManager extends QcAdManager {

    private static QcFirstVoiceAdManager instance;
    private        Activity              activity;
    private        QcFirstVoiceView      mView;

    private QcFirstVoiceAdManager () {

    }

    public static QcFirstVoiceAdManager getInstance () {
        if (null == instance) {
            instance = new QcFirstVoiceAdManager();
        }
        return instance;
    }


    public void getOfflineAudioAd (
            final Activity activity,
            final String adId,
            final List<UserPlayInfoBean> labels,
            final AdAudioBean adAudioBean,
            final QcFirstVoiceAdViewListener qcFirstVoiceAdViewListener) {
        this.activity = activity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                if (null == adAudioBean) {
                    if (qcFirstVoiceAdViewListener != null) {
                        qcFirstVoiceAdViewListener.onAdError(ErrorUtil.NOAD);
                    }
                    return;
                }

                try {
                    if (qcFirstVoiceAdViewListener != null) {
                        mView = new QcFirstVoiceView(activity);
                        mView.setAdAudioBean(adAudioBean);
                        mView.setQcFirstVoiceAdViewListener(qcFirstVoiceAdViewListener);
                        qcFirstVoiceAdViewListener.onAdReceive(QcFirstVoiceAdManager.this, mView);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    if (qcFirstVoiceAdViewListener != null) {
                        qcFirstVoiceAdViewListener.onAdError(ErrorUtil.NOAD);
                    }
                }
            }
        });
    }

    public void getAudioAd (
            final Activity activity,
            final String adId,
            final List<UserPlayInfoBean> labels,
            final QcFirstVoiceAdViewListener qcFirstVoiceAdViewListener) {
        this.activity = activity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                QcHttpUtil.getAudioAd(
                        activity,
                        adId,
                        1,
                        labels,
                        new QcHttpUtil.QcHttpOnListener<AdResponseBean>() {
                            @Override
                            public void OnQcCompletionListener (final AdResponseBean response) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run () {
                                        try {
                                            if (response != null && response.getAdm() != null && response.getAdm().getNormal() != null) {
                                                if (qcFirstVoiceAdViewListener != null) {
                                                    mView = new QcFirstVoiceView(activity);
                                                    mView.setQcFirstVoiceAdViewListener(qcFirstVoiceAdViewListener);
                                                    AdAudioBean adAudioBean = response.getAdm().getNormal();
                                                    mView.setAdAudioBean(adAudioBean);
                                                    mView.setResponseListener(adAudioBean);
                                                    qcFirstVoiceAdViewListener.onAdReceive(QcFirstVoiceAdManager.this, mView);
                                                }
                                            } else {
                                                if (qcFirstVoiceAdViewListener != null) {
                                                    qcFirstVoiceAdViewListener.onAdError(ErrorUtil.NOAD);
                                                }
                                            }
                                        } catch (Exception e) {
                                            if (qcFirstVoiceAdViewListener != null) {
                                                qcFirstVoiceAdViewListener.onAdError(e.getMessage());
                                            }
                                        }
                                    }
                                });
                            }

                            @Override
                            public void OnQcErrorListener (final String error, final int code) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run () {
                                        if (qcFirstVoiceAdViewListener != null) {
                                            qcFirstVoiceAdViewListener.onAdError(error + code);
                                        }
                                    }
                                });
                            }
                        });
            }
        });
    }

    @Override
    public void onResume () {
        super.onResume();
    }

    @Override
    public void onPause () {
        super.onPause();
    }

    @Override
    public void destroy () {
        super.destroy();

        if (mView != null) {
            mView.stopAndReleaseAd();
        }
    }

    @Override
    public void startPlayAd () {
        super.startPlayAd();

        if (mView != null) {
            mView.playAd();
        }
    }

    @Override
    public void skipPlayAd () {
        super.skipPlayAd();

        if (mView != null) {
            mView.skipAd();
        }
    }

    @Override
    public void resumePlayAd () {
        super.resumePlayAd();

        if (mView != null) {
            mView.resumePlayAd();
        }
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mView != null) {
            mView.onActivityResult(requestCode, resultCode, data);
        }
    }
}
