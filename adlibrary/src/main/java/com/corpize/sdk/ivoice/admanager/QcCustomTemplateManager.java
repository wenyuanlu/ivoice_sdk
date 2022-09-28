package com.corpize.sdk.ivoice.admanager;

import android.app.Activity;
import android.content.Intent;

import com.corpize.sdk.ivoice.QcCustomTemplateAttr;
import com.corpize.sdk.ivoice.bean.AdResponseBean;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.common.ErrorUtil;
import com.corpize.sdk.ivoice.listener.QcCustomTemplateListener;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;
import com.corpize.sdk.ivoice.view.QcCustomTemplateView;

import java.util.List;

/**
 * author : xpSun
 * date : 12/8/21
 * description :
 */
public class QcCustomTemplateManager extends QcAdManager {

    private static QcCustomTemplateManager instance;
    private        QcCustomTemplateView    qcCustomTemplateView;

    private QcCustomTemplateManager () {

    }

    public static QcCustomTemplateManager getInstance () {
        if (null == instance) {
            instance = new QcCustomTemplateManager();
        }
        return instance;
    }

    public void getOfflineAudio (
            final Activity activity,
            final QcCustomTemplateAttr attr,
            final String adId,
            final List<UserPlayInfoBean> labels,
            final AdAudioBean adAudioBean,
            final QcCustomTemplateListener templateListener
    ) {
        getOfflineAudio(
                activity,
                attr,
                adId,
                labels,
                adAudioBean,
                null,
                0,
                templateListener
        );
    }

    public void getOfflineAudio (
            final Activity activity,
            final QcCustomTemplateAttr attr,
            final String adId,
            final List<UserPlayInfoBean> labels,
            final AdAudioBean adAudioBean,
            final Integer provider,
            final int progress,
            final QcCustomTemplateListener templateListener
    ) {
        if (null == activity) {
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                if (null == adAudioBean) {
                    if (templateListener != null) {
                        templateListener.onAdError(ErrorUtil.NOAD);
                    }
                    return;
                }

                try {
                    if (templateListener != null) {
                        qcCustomTemplateView = new QcCustomTemplateView(activity);
                        qcCustomTemplateView.setListener(templateListener);
                        qcCustomTemplateView.setAttr(attr);
                        qcCustomTemplateView.setProvider(provider);
                        qcCustomTemplateView.setCurrentProgress(progress);
                        qcCustomTemplateView.setAdAudioBean(adAudioBean);
                        templateListener.onAdReceive(QcCustomTemplateManager.this, qcCustomTemplateView);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (templateListener != null) {
                        templateListener.onAdError(ErrorUtil.NOAD);
                    }
                }
            }
        });
    }

    public void getAudio (
            final Activity activity,
            final QcCustomTemplateAttr attr,
            final String adId,
            final List<UserPlayInfoBean> labels,
            final QcCustomTemplateListener templateListener
    ) {
        getAudio(activity, attr, adId, labels, templateListener, null,0);
    }

    public void getAudio (
            final Activity activity,
            final QcCustomTemplateAttr attr,
            final String adId,
            final List<UserPlayInfoBean> labels,
            final QcCustomTemplateListener templateListener,
            final Integer provider,
            final int progress
    ) {
        if (null == activity) {
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                QcHttpUtil.getAudioAd(
                        activity,
                        adId,
                        1,
                        labels,
                        provider,
                        new QcHttpUtil.QcHttpOnListener<AdResponseBean>() {
                            @Override
                            public void OnQcCompletionListener (final AdResponseBean response) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run () {
                                        try {
                                            if (response != null && response.getAdm() != null && response.getAdm().getNormal() != null) {
                                                if (templateListener != null) {
                                                    qcCustomTemplateView = new QcCustomTemplateView(activity);
                                                    qcCustomTemplateView.setListener(templateListener);
                                                    qcCustomTemplateView.setAttr(attr);
                                                    qcCustomTemplateView.setProvider(provider);
                                                    qcCustomTemplateView.setCurrentProgress(progress);

                                                    AdAudioBean adAudioBean = response.getAdm().getNormal();
                                                    qcCustomTemplateView.setAdAudioBean(adAudioBean);
                                                    qcCustomTemplateView.setResponseListener(adAudioBean);
                                                    templateListener.onAdReceive(QcCustomTemplateManager.this, qcCustomTemplateView);
                                                }
                                            } else {
                                                if (templateListener != null) {
                                                    templateListener.onAdError(ErrorUtil.NOAD);
                                                }
                                            }
                                        } catch (Exception e) {
                                            if (templateListener != null) {
                                                templateListener.onAdError(e.getMessage());
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
                                        if (templateListener != null) {
                                            templateListener.onAdError(error + code);
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

        if (qcCustomTemplateView != null) {
            qcCustomTemplateView.resume();
        }
    }

    @Override
    public void onPause () {
        super.onPause();
        if (qcCustomTemplateView != null) {
            qcCustomTemplateView.pause();
        }
    }

    @Override
    public void destroy () {
        super.destroy();

        if (qcCustomTemplateView != null) {
            qcCustomTemplateView.skipAd();
        }
    }

    @Override
    public void startPlayAd () {
        super.startPlayAd();

        if (qcCustomTemplateView != null) {
            qcCustomTemplateView.playAd();
        }
    }

    @Override
    public void skipPlayAd () {
        super.skipPlayAd();

        if (qcCustomTemplateView != null) {
            qcCustomTemplateView.skipAd();
        }
    }

    @Override
    public void resumePlayAd () {
        super.resumePlayAd();
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (qcCustomTemplateView != null) {
            qcCustomTemplateView.onActivityResult(requestCode, resultCode, data);
        }
    }
}
