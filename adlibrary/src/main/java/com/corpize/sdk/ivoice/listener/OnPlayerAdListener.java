package com.corpize.sdk.ivoice.listener;

public interface OnPlayerAdListener {

    void onAdCompleteCallBack ();

    void onAdExposure ();

    void onAdError (String msg);

    void sendAdExposure (String url);

    void onAdClick();

    default void onPlayCenterPositionListener(int position){};

    default void onPlayStartListener(int position, int allTime){};
}