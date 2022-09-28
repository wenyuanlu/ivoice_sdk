package com.corpize.sdk.ivoice.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * author: yh
 * date: 2020-12-10 15:06
 * description: 摇一摇
 */
public class ShakeUtils implements SensorEventListener {
    private              SensorManager   mSensorManager       = null;
    private              Sensor          mSenAccelerometer;
    private              OnShakeListener mOnShakeListener     = null;
    private static final long            UPDATE_INTERVAL_TIME = 50;
    private static final int             SPEED_SHAESHOLD      = 20;//调节灵敏度
    private              boolean         mIsHaveSnake         = false;//是否已经摇一摇过
    private              long            lastUpateTime;
    private              float           lastX, lastY, lastZ;

    public ShakeUtils (Context context) {
        if (null == context) {
            return;
        }

        try {
            mIsHaveSnake = false;
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

            if (mSensorManager != null) {
                if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                    mSenAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                } else {
                    //设备上没有加速度传感器
                    Log.w("TAG", "设备上没有加速度传感器");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnShakeListener (OnShakeListener onShakeListener) {
        mOnShakeListener = onShakeListener;
    }

    public void onResume () {
        if (mSensorManager != null && mSenAccelerometer != null) {
            mSensorManager.registerListener(this, mSenAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onPause () {
        if (mSensorManager != null && mSenAccelerometer != null) {
            mSensorManager.unregisterListener(this, mSenAccelerometer);
        }
    }

    public void clear () {
        mSenAccelerometer = null;
        mOnShakeListener = null;
        mSensorManager = null;
    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged (SensorEvent event) {
        try {
            //传感器值发生改变，加速度传感器返回3个值
            int type = event.sensor.getType();
            if (type == Sensor.TYPE_ACCELEROMETER) {
                //摇一摇算法加强`
                long currentTimeMillis = System.currentTimeMillis();
                long timeInterval      = currentTimeMillis - lastUpateTime;
                if (timeInterval < UPDATE_INTERVAL_TIME) {
                    return;
                }
                lastUpateTime = currentTimeMillis;
                float aux    = event.values[0];
                float auy    = event.values[1];
                float auz    = event.values[2];
                float deltaX = aux - lastX;
                float deltaY = auy - lastY;
                float deltaZ = auz - lastZ;
                // Log.d(TAG, "aux=" + aux + " auy=" + auy + " auz=" + auz);
                lastX = aux;
                lastY = auy;
                lastZ = auz;
                double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                if (speed >= SPEED_SHAESHOLD && !mIsHaveSnake) {
                    mIsHaveSnake = true;
                    if (null != mOnShakeListener) {
                        mOnShakeListener.onShake();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnShakeListener {
        void onShake ();
    }

}

