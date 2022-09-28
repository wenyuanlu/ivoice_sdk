package com.corpize.sdk.ivoice.admanager;

import android.app.Activity;
import android.content.Intent;

import com.corpize.sdk.ivoice.AdRollAttr;
import com.corpize.sdk.ivoice.bean.AdResponseBean;
import com.corpize.sdk.ivoice.common.ErrorUtil;
import com.corpize.sdk.ivoice.listener.QcRollAdViewListener;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;
import com.corpize.sdk.ivoice.view.QcRollAdView;

/**
 * author : xpSun
 * date : 11/22/21
 * description : item 贴片广告
 */
public class QcRollAdManager extends QcAdManager {

    private static QcRollAdManager instance;
    private        QcRollAdView    qcRollAdView;

    public static QcRollAdManager getInstance () {
        if (null == instance) {
            instance = new QcRollAdManager();
        }
        return instance;
    }

    private QcRollAdManager () {
    }

    public void getAudioAd (
            final Activity activity,
            final String adId,
            final AdRollAttr adRollAttr,
            final QcRollAdViewListener qcRollAdViewListener) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                QcHttpUtil.getAudioAd(
                        activity,
                        adId,
                        1,
                        null,
                        new QcHttpUtil.QcHttpOnListener<AdResponseBean>() {
                            @Override
                            public void OnQcCompletionListener (final AdResponseBean response) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run () {
                                        try {
                                            if (response != null && response.getAdm() != null && response.getAdm().getNormal() != null) {
                                                if (qcRollAdViewListener != null) {
                                                    qcRollAdView = new QcRollAdView(activity);
                                                    qcRollAdView.setAdAudioBean(response.getAdm().getNormal());
                                                    qcRollAdView.setQcRollAdViewListener(qcRollAdViewListener);
                                                    qcRollAdView.setAdRollAttr(adRollAttr);
                                                    qcRollAdViewListener.onAdReceive(QcRollAdManager.this, qcRollAdView);
                                                }
                                            } else {
                                                if (qcRollAdViewListener != null) {
                                                    qcRollAdViewListener.onAdError(ErrorUtil.NOAD);
                                                }
                                            }
                                        } catch (Exception e) {
                                            if (qcRollAdViewListener != null) {
                                                qcRollAdViewListener.onAdError(e.getMessage());
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
                                        if (qcRollAdViewListener != null) {
                                            qcRollAdViewListener.onAdError(error + code);
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

        if (qcRollAdView != null) {
            qcRollAdView.pause();
        }
    }

    @Override
    public void destroy () {
        super.destroy();

        if (qcRollAdView != null) {
            qcRollAdView.destroy();
        }
    }

    @Override
    public void startPlayAd () {
        super.startPlayAd();
        if (qcRollAdView != null) {
            qcRollAdView.playAd();
        }
    }

    @Override
    public void skipPlayAd () {
        super.skipPlayAd();

        if (qcRollAdView != null) {
            qcRollAdView.pause();
        }
    }

    @Override
    public void resumePlayAd () {
        super.resumePlayAd();
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
