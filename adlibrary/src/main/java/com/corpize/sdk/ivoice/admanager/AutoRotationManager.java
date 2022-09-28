package com.corpize.sdk.ivoice.admanager;

import android.app.Activity;
import android.content.Intent;

import com.corpize.sdk.ivoice.bean.AdResponseBean;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.listener.QcAutoRotationListener;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;
import com.corpize.sdk.ivoice.view.QcAutoRotationView;

import java.util.List;

/**
 * author : xpSun
 * date : 2022/2/25
 * description :
 */
public class AutoRotationManager extends QcAdManager {

    private static AutoRotationManager instance;
    private        QcAutoRotationView  qcAutoRotationView;

    private AutoRotationManager () {

    }

    public static AutoRotationManager getInstance () {
        if (null == instance) {
            instance = new AutoRotationManager();
        }
        return instance;
    }

    public void getAudio (
            final Activity activity,
            final String adId,
            final List<UserPlayInfoBean> labels,
            final QcAutoRotationListener autoRotationListener,
            final Integer provider
    ) {
        if (null == activity) {
            return;
        }

        activity.runOnUiThread(() -> {
            QcHttpUtil.getAudioAd(
                    activity,
                    adId,
                    1,
                    labels,
                    provider,
                    9,
                    new QcHttpUtil.QcHttpOnListener<AdResponseBean>() {
                        @Override
                        public void OnQcCompletionListener (AdResponseBean response) {
                            activity.runOnUiThread(() -> {
                                if (response != null
                                        && response.getAdm() != null
                                        && response.getAdm().getNormal() != null) {
                                    if (null != autoRotationListener) {
                                        qcAutoRotationView = new QcAutoRotationView(activity);
                                        qcAutoRotationView.setQcAutoRotationListener(autoRotationListener);

                                        AdAudioBean adAudioBean = response.getAdm().getNormal();
                                        qcAutoRotationView.setAdAudioBean(adAudioBean);
                                        autoRotationListener.onAdReceive(AutoRotationManager.this, qcAutoRotationView);
                                    }
                                }
                            });
                        }

                        @Override
                        public void OnQcErrorListener (String error, int code) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run () {
                                    if (autoRotationListener != null) {
                                        autoRotationListener.onAdError(error + code);
                                    }
                                }
                            });
                        }
                    });
        });
    }

    @Override
    public void onResume () {
        super.onResume();
        if (qcAutoRotationView != null) {
            qcAutoRotationView.onResume();
        }
    }

    @Override
    public void onPause () {
        super.onPause();
        if (qcAutoRotationView != null) {
            qcAutoRotationView.onPause();
        }
    }

    @Override
    public void destroy () {
        super.destroy();
        if (qcAutoRotationView != null) {
            qcAutoRotationView.destroy();
        }
    }

    @Override
    public void startPlayAd () {
        super.startPlayAd();
        if (qcAutoRotationView != null) {
            qcAutoRotationView.startPlayAd();
        }
    }

    @Override
    public void skipPlayAd () {
        super.skipPlayAd();
        if (qcAutoRotationView != null) {
            qcAutoRotationView.skipPlayAd();
        }
    }

    @Override
    public void resumePlayAd () {
        super.resumePlayAd();
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (qcAutoRotationView != null) {
            qcAutoRotationView.onActivityResult(requestCode, resultCode, data);
        }
    }
}
