package com.corpize.sdk.ivoice.admanager;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;

import com.corpize.sdk.ivoice.bean.AdResponseBean;
import com.corpize.sdk.ivoice.bean.AdidBean;
import com.corpize.sdk.ivoice.common.ErrorUtil;
import com.corpize.sdk.ivoice.listener.RewardVideoQcAdListener;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;

/**
 * author: yh
 * date: 2020-02-11 23:35
 * description: 激励视频广告
 */
public class RewardVideoManager {

    private static RewardVideoManager      sQcRewardAd;
    private        CountDownTimer          mPostDownTime;      //同时发送的三个请求的倒计时
    private        AdidBean                mAdidBean;
    private        String                  mAdId;
    private        RewardVideoQcAdListener mListener;

    //企创
    private AdResponseBean mAdBack;

    private boolean mHaveQC;
    private boolean mHaveCSJ;
    private boolean mHaveTX;
    private boolean mHaveBQT;
    private int     mShowType = 0;//要展示的当前的广告渠道,1=企创 2=穿山甲 3=广点通

    /**
     * 单例模式
     */
    public static RewardVideoManager get () {
        if (sQcRewardAd == null) {
            sQcRewardAd = new RewardVideoManager();
        }
        return sQcRewardAd;
    }

    /**
     * 初始化数据
     */
    private void initData () {
        mHaveQC = false;
        mHaveCSJ = false;
        mHaveTX = false;
        mHaveBQT = false;
        mShowType = 0;
    }

    /**
     * 清理
     */
    public void destroy () {
        mListener = null;
        sQcRewardAd = null;
    }

    /**
     * 获取控件回调
     */
    public RewardVideoQcAdListener getListener () {
        return mListener;
    }

    /**
     * 获取企创的ad
     */
    private void getQcAd (final Activity activity, AdidBean adsSdk, String adId, final boolean isShow) {
        QcHttpUtil.getAd(activity, adsSdk, adId, new QcHttpUtil.QcHttpOnListener<AdResponseBean>() {

            @Override
            public void OnQcCompletionListener (AdResponseBean response) {
                if (response != null && response.getAdm() != null) {
                    if (response.getAdm().getNormal() != null) {
                        mHaveQC = true;
                        mAdBack = response;
                    }

                    if (isShow) {
                        showQcAd(activity, false);
                    }

                } else {
                    if (isShow) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run () {
                                if (mListener != null) {
                                    mListener.onAdError(ErrorUtil.QC, ErrorUtil.NOAD);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void OnQcErrorListener (final String erro, final int code) {
                if (isShow) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run () {
                            if (mListener != null) {
                                mListener.onAdError(ErrorUtil.QC, erro + code);
                            }
                        }
                    });
                }
            }
        });
    }


    /**
     * 展示企创的广告
     */
    private void showQcAd (final Activity activity, boolean isRealyShow) {
        if (!isRealyShow) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run () {
                    mShowType = 1;
                    if (mListener != null) {
                        mListener.onADManager(sQcRewardAd);
                        mListener.onADReceive(sQcRewardAd, ErrorUtil.QC);
                    }

                }
            });
            return;
        }

        if (mAdBack.getAdm() != null && mAdBack.getAdm().getNormal() != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run () {
                    Intent intent = new Intent(activity, QcAdVideoActivity.class);
                    intent.putExtra("dataBean", mAdBack.getAdm().getNormal());
                    activity.startActivity(intent);
                }
            });
        } else {
            if (mListener != null) {
                mListener.onAdError(ErrorUtil.QC, ErrorUtil.NOAD);
            }
        }
    }
}
