package com.corpize.sdk.ivoice.utils;

import android.app.Activity;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.common.CommonUtils;
import com.corpize.sdk.ivoice.dialog.CustomRecorderProgressDialog;

/**
 * @author Created by SXF on 2021/7/8 3:40 PM.
 * @description 摇一摇互动工具类抽取
 */
@Deprecated
public class ShakeInteractiveUtil {

    /**
     * 初始化摇一摇监听
     * 1.外界无需再重新处理摇一摇声音，摇一摇互动和语音互动互斥。
     * 2.回调摇一摇回调,可在回调中做其他操作。
     *
     * @param mActivity
     * @param mShakeUtils
     * @param customMonitorVolumeUtils
     * @param onShakeListener
     */
    public static void setOnShakeListener (final Activity mActivity,
                                           ShakeUtils mShakeUtils,
                                           final CustomMonitorVolumeUtils customMonitorVolumeUtils,
                                           final ShakeUtils.OnShakeListener onShakeListener) {
        try {
//            //处于后台所有互动都进行屏蔽
//            if (QCiVoiceSdk.get().isBackground()) {
//                return;
//            }

            mShakeUtils.onResume();
            mShakeUtils.setOnShakeListener(new ShakeUtils.OnShakeListener() {
                @Override
                public void onShake () {
                    try {
                        // 监测到摇一摇，关闭录音
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run () {
                                try {
                                    //监测到摇一摇，取消录音，此次交互为摇一摇交互
                                    if (customMonitorVolumeUtils != null) {
                                        customMonitorVolumeUtils.stopNoiseLevel();
                                    }
                                    CustomRecorderUtils.getInstance().cancelRecorder();//取消录音
                                    CustomRecorderProgressDialog.getInstance().builder(mActivity).dismissProgressDialog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        LogUtils.e("返回的摇一摇");
                        //播放声音和震动
                        CommonUtils.playShakeSound(mActivity);
                        CommonUtils.vibrate(mActivity, 300);
                        DialogUtils.closeNoInteractiveCountDownTime();//无互动的倒计时要清理
                        //有互动的时候
                        if (onShakeListener != null) {
                            onShakeListener.onShake();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 纯音频互动的初始化摇一摇监听
     *
     * @param mActivity
     * @param mShakeUtils
     * @param onShakeListener
     */
    public static void setOnShakeListener (final Activity mActivity, ShakeUtils mShakeUtils,
                                           final ShakeUtils.OnShakeListener onShakeListener) {
        setOnShakeListener(mActivity, mShakeUtils, null, onShakeListener);
    }
}
