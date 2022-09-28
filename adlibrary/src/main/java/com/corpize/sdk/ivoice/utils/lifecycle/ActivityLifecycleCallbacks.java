package com.corpize.sdk.ivoice.utils.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.corpize.sdk.ivoice.utils.ScreenUtils;
import com.corpize.sdk.ivoice.utils.SpUtils;

/**
 * author ：Seven
 * date : 5/11/21
 * description :
 */
public class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private int mFinalCount;
    private Activity activity;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        this.activity = activity;

        mFinalCount++;
        //如果mFinalCount ==1，说明是从后台到前台
        if (mFinalCount >= 1) {
            Log.e(ActivityLifecycleCallbacks.class.getSimpleName(),"ActivityLifecycleCallbacks  in");
            SpUtils.saveBoolean("show",true);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mFinalCount--;
        //如果mFinalCount ==0，说明是前台到后台
        if (this.activity == activity &&
                mFinalCount <= 0 &&
                ScreenUtils.isScreenOnAndUnlock()) {
            mFinalCount = 0;
            Log.e(ActivityLifecycleCallbacks.class.getSimpleName(),"ActivityLifecycleCallbacks  out");
            SpUtils.saveBoolean("show",false);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
