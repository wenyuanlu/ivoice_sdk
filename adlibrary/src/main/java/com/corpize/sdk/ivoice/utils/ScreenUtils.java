package com.corpize.sdk.ivoice.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.PowerManager;
import android.view.WindowManager;

import com.corpize.sdk.ivoice.QCiVoiceSdk;

/**
 * author : xpSun
 * date : 2021/3/25
 * description :
 */
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
    public static int getScreenHeight (Context context) {
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

    //获取顶部状态栏高度
    public int getStatusBarHeight (Context context) {
        Resources resources  = context.getResources();
        int       resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int       height     = resources.getDimensionPixelSize(resourceId);
        return height;
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

    //如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
    //屏幕“亮”，表示有两种状态：a、未锁屏 b、目前正处于解锁状态 。这两种状态屏幕都是亮的
    //屏幕“暗”，表示目前屏幕是黑的 。
    public static boolean isScreenOn (Context context) {
        PowerManager pm         = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean      isScreenOn = pm.isScreenOn();
        return isScreenOn;
    }

    //如果flag为true，表示有两种状态：a、屏幕是黑的 b、目前正处于解锁状态 。
    //如果flag为false，表示目前未锁屏
    public static boolean inKeyguardRestrictedInputMode (Context context) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean         flag             = mKeyguardManager.inKeyguardRestrictedInputMode();
        return flag;
    }

    public static boolean isScreenOnAndUnlock () {
        Context context = QCiVoiceSdk.get().context;
        return isScreenOn(context) && !inKeyguardRestrictedInputMode(context);
    }
}
