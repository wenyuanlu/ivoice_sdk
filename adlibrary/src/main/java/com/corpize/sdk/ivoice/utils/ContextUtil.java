package com.corpize.sdk.ivoice.utils;

import android.app.Activity;
import android.os.Build;

public class ContextUtil {

    /**
     * 判断Activity是否Destroy
     * @param activity
     * @return
     */
    public static boolean isDestroy(Activity activity) {
        if (activity== null || activity.isFinishing() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }
}
