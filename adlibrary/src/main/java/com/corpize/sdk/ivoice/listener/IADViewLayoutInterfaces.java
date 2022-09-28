package com.corpize.sdk.ivoice.listener;

import android.content.Intent;

/**
 * author : xpSun
 * date : 2022/2/25
 * description :
 */
public interface IADViewLayoutInterfaces {

    default void onResume(){};

    default void onPause(){};

    default void destroy(){};

    default void startPlayAd(){};

    default void skipPlayAd(){};

    default void resumePlayAd(){};

    default void onActivityResult(int requestCode, int resultCode, Intent data){};
}
