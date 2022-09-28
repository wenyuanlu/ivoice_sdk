package com.corpize.sdk.ivoice.admanager;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.corpize.sdk.ivoice.bean.AdResponseBean;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.common.ErrorUtil;
import com.corpize.sdk.ivoice.listener.QcVoiceAdListener;
import com.corpize.sdk.ivoice.utils.GsonUtil;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;

import java.util.List;

/**
 * author : xpSun
 * date : 7/15/21
 * description :
 */
public class QcVoiceAdManager extends QcAdManager {

    private static QcVoiceAdManager     instance;
    private        CommonVoiceAdManager voiceAdManager;

    public static QcVoiceAdManager getInstance () {
        if (null == instance) {
            instance = new QcVoiceAdManager();
        }
        return instance;
    }

    public void getAudioAd (
            final Activity activity,
            final String adId,
            final int delayPlayInteractionTimer,
            final List<UserPlayInfoBean> labels,
            final QcVoiceAdListener commonVoiceAdListener) {
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
                                            if (response != null && response.getAdm()!=null && response.getAdm().getNormal()!=null) {
                                                if (commonVoiceAdListener != null) {
                                                    voiceAdManager = new CommonVoiceAdManager(activity);
                                                    voiceAdManager.setResponse(response.getAdm().getNormal());
                                                    voiceAdManager.setListener(commonVoiceAdListener);
                                                    voiceAdManager.setDelayPlayInteractionTimer(delayPlayInteractionTimer);
                                                    commonVoiceAdListener.onAdReceive(QcVoiceAdManager.this);
                                                }
                                            } else {
                                                if (commonVoiceAdListener != null) {
                                                    commonVoiceAdListener.onAdError(ErrorUtil.NOAD);
                                                }
                                            }
                                        } catch (Exception e) {
                                            if (commonVoiceAdListener != null) {
                                                commonVoiceAdListener.onAdError(e.getMessage());
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
                                        if (commonVoiceAdListener != null) {
                                            commonVoiceAdListener.onAdError(error + code);
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
        if (voiceAdManager != null) {
            voiceAdManager.onResume();
        }
    }

    @Override
    public void onPause () {
        super.onPause();
        if (voiceAdManager != null) {
            voiceAdManager.onPause();
        }
    }

    @Override
    public void destroy () {
        super.destroy();
        if (voiceAdManager != null) {
            voiceAdManager.destroy();
        }
    }

    @Override
    public void startPlayAd () {
        super.startPlayAd();
        if (voiceAdManager != null) {
            voiceAdManager.startPlayAd();
        }
    }

    @Override
    public void skipPlayAd () {
        super.skipPlayAd();
        if (voiceAdManager != null) {
            voiceAdManager.skipPlayAd();
        }
    }

    @Override
    public void resumePlayAd () {
        super.resumePlayAd();
        if (voiceAdManager != null) {
            voiceAdManager.resumePlayAd();
        }
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (voiceAdManager != null) {
            voiceAdManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
