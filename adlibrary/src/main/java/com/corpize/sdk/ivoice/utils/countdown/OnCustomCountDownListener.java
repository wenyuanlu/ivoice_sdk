package com.corpize.sdk.ivoice.utils.countdown;

/**
 * author : xpSun
 * date : 12/1/21
 * description :
 */
public interface OnCustomCountDownListener {

    void onTick (long millisUntilFinished);

    void onFinish ();

}
