package com.corpize.sdk.ivoice.utils.headsetplug;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.corpize.sdk.ivoice.common.SystemConstants;
import com.corpize.sdk.ivoice.recorder.HeadsetPlugReceiver;
import com.corpize.sdk.ivoice.utils.LogUtils;
import com.corpize.sdk.ivoice.utils.SpUtils;

/**
 * author : xpSun
 * date : 8/31/21
 * description :
 */
public class HeadsetPlugUtils {

    private static       HeadsetPlugUtils    instance;
    private              Context             context;
    private              HeadsetPlugReceiver headsetPlugReceiver;
    private static final String              COMMON_HEADSET_PLUG_TAG = "common_headset_plug_tag";
    private              boolean             isRegisterReceiver      = false;

    private HeadsetPlugUtils () {
        try {
            headsetPlugReceiver = new HeadsetPlugReceiver();
            headsetPlugReceiver.setHeadsetPlugListener(
                    new HeadsetPlugReceiver.OnHeadsetPlugListener() {
                        @Override
                        public void onHeadsetPlug (int type, boolean isPlug) {
                            LogUtils.e("type:" + type + ";isPlug:" + isPlug);

                            savePlugType(type);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HeadsetPlugUtils getInstance () {
        if (null == instance) {
            instance = new HeadsetPlugUtils();
        }
        return instance;
    }

    public HeadsetPlugUtils Build (Context context) {
        this.context = context;
        init();
        return this;
    }

    private void init () {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(null == bluetoothAdapter){
                return;
            }

            if (BluetoothProfile.STATE_CONNECTED == bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                //蓝牙设备已连接，声音内放，从蓝牙设备输出
                savePlugType(SystemConstants.PlugType.BLUETOOTH_PLUG_TYPE);
            } else if (BluetoothProfile.STATE_DISCONNECTED == bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                //蓝牙设备未连接，声音外放
                checkDeviceExternalRelease();
            } else {
                //蓝牙设备未连接，声音外放
                checkDeviceExternalRelease();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkDeviceExternalRelease () {
        try {
            if (null != context) {
                AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                //获取当前使用的麦克风，设置媒体播放麦克风
                if (mAudioManager.isWiredHeadsetOn()) {
                    //有线耳机已连接，声音内放，从耳机输出
                    savePlugType(SystemConstants.PlugType.DEVICE_LINK_PLUG_TYPE);
                } else {
                    //有线耳机未连接，声音外放，
                    savePlugType(SystemConstants.PlugType.DEVICE_EXTERNAL_RELEASE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void savePlugType (int type) {
        LogUtils.e("type:" + type);
        SpUtils.saveInt(COMMON_HEADSET_PLUG_TAG, type);
    }

    public int getPlugType () {
        int plugType = SpUtils.getInt(COMMON_HEADSET_PLUG_TAG, -1);
        return plugType;
    }

    public void registerReceiver () {
        try {
            if (null != context) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
                intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
                context.registerReceiver(headsetPlugReceiver, intentFilter);
                isRegisterReceiver = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unRegisterReceiver () {
        try {
            if (null != context && isRegisterReceiver) {
                isRegisterReceiver = false;
                context.unregisterReceiver(headsetPlugReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
