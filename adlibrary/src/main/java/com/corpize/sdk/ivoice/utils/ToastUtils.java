package com.corpize.sdk.ivoice.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * author: yh
 * date: 2020-02-13 15:22
 * description: TODO:
 */
public class ToastUtils {

    private static boolean isshow = true;

    public static void show (final Activity mActivity, final String content) {
        if (isshow) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run () {
                    Toast.makeText(mActivity, content, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
