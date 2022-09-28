package com.corpize.sdk.ivoice.utils;

import android.app.Activity;

import com.corpize.sdk.ivoice.common.CommonUtils;

import java.lang.ref.WeakReference;

/**
 * author : xpSun
 * date : 2022/1/19
 * description :摇一摇互动
 */
public class CommonShakeInteractiveUtil {

    private static CommonShakeInteractiveUtil instance;

    private ShakeUtils                 shakeUtils;
    private WeakReference<Activity>    activityWeakReference;
    private ShakeUtils.OnShakeListener onShakeListener;

    public CommonShakeInteractiveUtil setOnShakeListener (ShakeUtils.OnShakeListener onShakeListener) {
        this.onShakeListener = onShakeListener;
        return getInstance();
    }

    private CommonShakeInteractiveUtil () {
    }

    public CommonShakeInteractiveUtil Builder (Activity activity) {
        activityWeakReference = new WeakReference<Activity>(activity);
        return getInstance();
    }

    public static CommonShakeInteractiveUtil getInstance () {
        if (null == instance) {
            instance = new CommonShakeInteractiveUtil();
        }
        return instance;
    }

    public boolean isInitShake () {
        return null != shakeUtils;
    }

    public CommonShakeInteractiveUtil initShake () {
        clearShake();

        Activity activity = activityWeakReference.get();

        if (null == activity) {
            return getInstance();
        }

        if (null == shakeUtils) {
            shakeUtils = new ShakeUtils(activity);
        } else {
            shakeUtils.clear();
        }

        shakeUtils.onResume();
        shakeUtils.setOnShakeListener(new ShakeUtils.OnShakeListener() {
            @Override
            public void onShake () {
                LogUtils.e("返回的摇一摇");

                Activity activity = activityWeakReference.get();
                if (null == activity) {
                    return;
                }

                //播放声音和震动
                CommonUtils.playShakeSound(activity);
                CommonUtils.vibrate(activity, 300);
                DialogUtils.closeNoInteractiveCountDownTime();//无互动的倒计时要清理

                if (onShakeListener != null) {
                    onShakeListener.onShake();
                }
            }
        });
        return getInstance();
    }

    public CommonShakeInteractiveUtil clearShake () {
        if (shakeUtils != null) {
            shakeUtils.onPause();
            shakeUtils.clear();
            shakeUtils = null;
        }
        return getInstance();
    }
}
