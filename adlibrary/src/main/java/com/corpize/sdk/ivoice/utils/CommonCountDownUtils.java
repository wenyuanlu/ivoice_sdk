package com.corpize.sdk.ivoice.utils;

import android.os.CountDownTimer;

/**
 * author : xpSun
 * date : 2022/1/20
 * description :自定义倒计时工具类
 */
public class CommonCountDownUtils {

    private static CommonCountDownUtils instance;
    private        CountDownTimer       customCountDownTimer;

    public static CommonCountDownUtils getInstance () {
        if (null == instance) {
            instance = new CommonCountDownUtils();
        }
        return instance;
    }

    public interface OnCustomCountDownListener {
        void onCustomCountDown (int timer);
    }

    private OnCustomCountDownListener onCustomCountDownListener;

    public CommonCountDownUtils init (int timer,
                                      final OnCustomCountDownListener onCustomCountDownListener
    ) {
        init(timer * 1000, timer * 1000, onCustomCountDownListener);
        return getInstance();
    }

    public CommonCountDownUtils init (
            final long millisInFuture,
            final long countDownInterval,
            final OnCustomCountDownListener onCustomCountDownListener
    ) {
        if (null != customCountDownTimer) {
            customCountDownTimer.cancel();
            customCountDownTimer = null;
        }

        customCountDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick (long millisUntilFinished) {
                if (millisInFuture != countDownInterval) {
                    int millisUntil = Long.valueOf(millisUntilFinished / 1000).intValue();
                    if (onCustomCountDownListener != null) {
                        onCustomCountDownListener.onCustomCountDown(millisUntil);
                    }
                }
            }

            @Override
            public void onFinish () {
                if (onCustomCountDownListener != null) {
                    onCustomCountDownListener.onCustomCountDown(0);
                }
            }
        };
        customCountDownTimer.start();
        return getInstance();
    }

    public CommonCountDownUtils cancel () {
        if (null != customCountDownTimer) {
            customCountDownTimer.cancel();
            customCountDownTimer = null;
        }
        return getInstance();
    }
}
