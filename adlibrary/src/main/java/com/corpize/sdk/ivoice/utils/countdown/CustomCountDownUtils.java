package com.corpize.sdk.ivoice.utils.countdown;

import android.os.CountDownTimer;

/**
 * author : xpSun
 * date : 12/1/21
 * description :
 */
public class CustomCountDownUtils extends CountDownTimer {

    private OnCustomCountDownListener onCustomCountDownListener;

    public void setOnCustomCountDownListener (OnCustomCountDownListener onCustomCountDownListener) {
        this.onCustomCountDownListener = onCustomCountDownListener;
    }

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CustomCountDownUtils (long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick (long millisUntilFinished) {
        if (onCustomCountDownListener != null) {
            onCustomCountDownListener.onTick(millisUntilFinished);
        }
    }

    @Override
    public void onFinish () {
        if (onCustomCountDownListener != null) {
            onCustomCountDownListener.onFinish();
        }
    }
}
