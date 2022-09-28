package com.example.qcaudioad.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.WindowManager;

public class ScreenUtils {

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth (Context context) {
        int screenWith = -1;
        try {
            screenWith = context.getResources().getDisplayMetrics().widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenWith;
    }

    /**
     * 获取真实的高度,全面屏水滴屏需要
     *
     * @param context
     * @return
     */
    public static int getRealyScreenHeight (Context context) {
        int screenHeight = -1;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //新的获取屏幕高度的方法
                WindowManager wm    = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Point         point = new Point();
                if (wm != null) {
                    wm.getDefaultDisplay().getRealSize(point);
                    screenHeight = point.y;
                    //LogUtils.e("换一种方法获取的屏幕高度=" + screenHeight);
                } else {
                    //原来获取屏幕高度的方法
                    screenHeight = context.getResources().getDisplayMetrics().heightPixels;
                }

            } else {
                //原来获取屏幕高度的方法
                screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return screenHeight;
    }

    public static int dp2px (Context context, float dipValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        } catch (Exception e) {
            return (int) dipValue;
        }
    }

    public static int px2dp (Context context, float px) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (px / scale + 0.5f);
        } catch (Exception e) {
            return (int) px;
        }
    }
}
