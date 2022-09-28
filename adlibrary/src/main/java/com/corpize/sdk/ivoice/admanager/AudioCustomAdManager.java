package com.corpize.sdk.ivoice.admanager;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.corpize.sdk.ivoice.AdAttr;
import com.corpize.sdk.ivoice.bean.AdResponseBean;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.common.ErrorUtil;
import com.corpize.sdk.ivoice.listener.AudioCustomQcAdListener;
import com.corpize.sdk.ivoice.utils.GsonUtil;
import com.corpize.sdk.ivoice.utils.LogUtils;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: yh
 * date: 2020-02-11 23:35
 * description: 音频嵌入式广告
 */
public class AudioCustomAdManager extends QcAdManager {

    private AudioCustomQcAdListener mListener;
    private AdAttr mAdAttr;
    private CustomAudioAdView mView;
    private Activity mActivity;
    private Map<Integer, CustomAudioAdView> viewMap;

    public AudioCustomAdManager() {
    }

    /**
     * 清除广告
     */
    @Override
    public void destroy() {
        super.destroy();
        mListener = null;
        mActivity = null;
        if (mView != null) {
            mView.skipAd();
        }
        if (viewMap != null) {
            viewMap.clear();
            viewMap = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e("自定义的返回=onResume");
        if (mView != null) {
            mView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.e("自定义的返回=onPause");
        if (mView != null) {
            mView.pause();
        }
    }

    @Override
    public void startPlayAd() {
        super.startPlayAd();
        if (mView != null) {
            mView.playAd();
        }
    }

    @Override
    public void skipPlayAd() {
        super.skipPlayAd();
        if (mView != null) {
            mView.skipAd();
        }
    }

    @Override
    public void resumePlayAd() {
        super.resumePlayAd();
        if (mView != null) {
            mView.resume();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (viewMap != null && viewMap.get(requestCode) != null) {
            viewMap.get(requestCode).onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 展示企创音频广告
     */
    public AudioCustomAdManager showQcAd(final int position, final Activity activity, AdAttr adAttr, AudioCustomQcAdListener listener) {
        mListener = listener;
        mActivity = activity;
        mAdAttr = adAttr;
        QcHttpUtil.getAudioAd(activity, adAttr.getAdid(), 1, adAttr.getLabel(), new QcHttpUtil.QcHttpOnListener<AdResponseBean>() {
            @Override
            public void OnQcCompletionListener(final AdResponseBean response) {
                if (response != null && response.getAdm() != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.getAdm().getNormal() != null) {
                                mView = new CustomAudioAdView(activity, null, activity, response.getAdm(), mAdAttr, mListener, position);
                                if (viewMap == null) {
                                    viewMap = new HashMap<>();
                                }
                                viewMap.put(position, mView);

                                if (mListener != null) {
                                    mListener.onAdReceive(AudioCustomAdManager.this, mView);
                                }
                            }
                        }
                    });
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.onAdError(ErrorUtil.NOAD);
                            }
                        }
                    });
                }
            }

            @Override
            public void OnQcErrorListener(final String erro, final int code) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            mListener.onAdError(erro + code);
                        }
                    }
                });
            }
        });
        return this;
    }
}
