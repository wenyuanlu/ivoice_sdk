package com.corpize.sdk.ivoice.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import androidx.core.content.ContextCompat;

import com.corpize.sdk.ivoice.common.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * author ：yh
 * date : 2021/1/27 16:46
 * description :
 * date : 2021/5/11 14:00
 * 添加检测多个音频录制和读写权限
 */
public class PermissionUtil {


    private static final String[] PERMISSION_RECORD_WRITE = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";
    private static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final int PERMISSION_CODE_NOPERMISSION = 0;       //都没有权限
    public static final int PERMISSION_CODE_SHAKE = 1;               //只有摇一摇的权限
    public static final int PERMISSION_CODE_SHAKE_RECORD = 2;       //有摇一摇和录音权限没有读写权限
    public static final int PERMISSION_CODE_SHAKE_RECORD_WRITE = 3; //有摇一摇录音读写权限
    public static final int PERMISSION_CODE_RECORD_WRITE = 4;       //没有摇一摇权限有录音和读写权限

    /**
     * 检测权限
     *
     * @return true：已授权； false：未授权；
     */
    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检测多个音频录制和读写权限
     *
     * @return 未授权的权限
     */
    public static int checkAudioAndWritePermissions(Context context) {
        boolean hasMSensorManager;
        hasMSensorManager = isHasMSensorManager(context);
        List<String> permissionList = new ArrayList<>();
        for (String permission : PERMISSION_RECORD_WRITE) {
            if (checkPermission(context, permission))
                permissionList.add(permission);
        }
        if (permissionList.isEmpty()) {
            if (hasMSensorManager) {
                return PERMISSION_CODE_SHAKE;//只有摇一摇权限
            } else {
                return PERMISSION_CODE_NOPERMISSION;//都没权限
            }
        }
        boolean isRecordForApi = SpUtils.getBoolean(Constants.SP_PERMISSION_MICROPHONE, true);
        if (permissionList.contains(RECORD_AUDIO) && isRecordForApi
                && !permissionList.contains(WRITE_EXTERNAL_STORAGE)
                && hasMSensorManager) {
            LogUtils.e("有摇一摇和录音权限没有读写权限====:");
            return PERMISSION_CODE_SHAKE_RECORD;
        }
        if (permissionList.contains(RECORD_AUDIO) && isRecordForApi
                && permissionList.contains(WRITE_EXTERNAL_STORAGE)
                && hasMSensorManager) {
            LogUtils.e("有摇一摇录音读写权限====:");
            return PERMISSION_CODE_SHAKE_RECORD_WRITE;
        }
        if (permissionList.contains(RECORD_AUDIO) && isRecordForApi
                && permissionList.contains(WRITE_EXTERNAL_STORAGE)
                && !hasMSensorManager) {
            LogUtils.e("没有摇一摇权限有录音和读写权限====:");
            return PERMISSION_CODE_RECORD_WRITE;
        }

        if (hasMSensorManager) {
            LogUtils.e("摇一摇和读写权限====:");
            return PERMISSION_CODE_SHAKE;//只有摇一摇权限
        }
        return PERMISSION_CODE_NOPERMISSION;
    }

    /**
     * 判断设备是否有加速传感器
     * * @return true：有加速传感器； false：无加速传感器；
     */
    private static boolean isHasMSensorManager(Context context) {
        boolean hasMSensorManager;
        try {
            SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            if (mSensorManager != null) {
                //设备上没有加速度传感器
                hasMSensorManager = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
            } else {
                hasMSensorManager = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            hasMSensorManager = false;
        }
        return hasMSensorManager;
    }


}
