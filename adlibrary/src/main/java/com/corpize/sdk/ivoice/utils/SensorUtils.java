package com.corpize.sdk.ivoice.utils;

import android.content.Context;
import android.util.Log;

import com.corpize.sdk.ivoice.bean.AdSensorBean;
import com.corpize.sdk.ivoice.listener.SensorManagerCallback;

import java.util.Timer;
import java.util.TimerTask;

/**
 * author: yh
 * date: 2020-12-10 15:06
 * description: 获取陀螺仪并定时上传
 */
public class SensorUtils {

    /**
     * TimkerTask 方式实现
     */
    private static Timer mTimer;

    public static void getSensorManagerInfo (final Context context) {
        try {
            DeviceUtil.getSensorManagerInfo(context, new SensorManagerCallback() {
                @Override
                public void getInfo (float x, float y, float z) {
                    QcHttpUtil.upSensorInfo(context,x, y, z, new QcHttpUtil.QcHttpOnListener<AdSensorBean>() {
                        @Override
                        public void OnQcCompletionListener (AdSensorBean response) {
                            if (response != null) {
                                int arranged = response.getArranged();
                                downTime(context, arranged);
                            }
                        }

                        @Override
                        public void OnQcErrorListener (String erro, int code) {
                            //请求失败,倒计时300秒后重新发送请求
                            downTime(context, 300);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param time
     */
    public static void downTime (final Context context, int time) {
        try {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }

            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run () {
                    getSensorManagerInfo(context);
                }
            }, time * 1000, time * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

