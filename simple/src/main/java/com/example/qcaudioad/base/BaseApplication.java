package com.example.qcaudioad.base;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.example.qcaudioad.common.ADIDConstants;
import com.example.qcaudioad.helper.OAIDHelper;

/**
 * author: yh
 * date: 2019-08-08 13:43
 * description: TODO:
 */
public class BaseApplication extends Application {

    private static BaseApplication instance;

    public static BaseApplication getInstance () {
        return instance;
    }

    private static final int COMMON_DNT_VALUE = 1;//是否允许广告追踪,0不允许,1允许

    @Override
    public void onCreate () {
        super.onCreate();
        instance = this;
        Log.e("application","onCreate");

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(instance);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //首先获取oaid
            OAIDHelper.getInstance()
                    .setOnFetchOAIDListener(new OAIDHelper.OnFetchOAIDListener() {
                        @Override
                        public void onOAIDSuccess (String oaid) {
                            Log.e("IVOICE OAID:", oaid);
                            //企创聚合音频sdk初始化,需传入oaid,
                            //如果设备系统版本大于等于android10,则oaid 必传,不可为空
                            QCiVoiceSdk.get().init(getInstance(), oaid, ADIDConstants.MID, COMMON_DNT_VALUE);
                        }

                        @Override
                        public void onOAIDFail (int code) {
                            QCiVoiceSdk.get().init(getInstance(), null, ADIDConstants.MID, COMMON_DNT_VALUE);
                        }
                    })
                    .getDeviceIds(this);
        } else {
            QCiVoiceSdk.get().init(getInstance(), null, ADIDConstants.MID, COMMON_DNT_VALUE);
        }
    }
}
