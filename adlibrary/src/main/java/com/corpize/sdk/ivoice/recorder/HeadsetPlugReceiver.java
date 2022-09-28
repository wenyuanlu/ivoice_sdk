package com.corpize.sdk.ivoice.recorder;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.corpize.sdk.ivoice.common.SystemConstants;
import com.corpize.sdk.ivoice.utils.LogUtils;


/**
 * 耳机状态监听
 * <p>
 */
public class HeadsetPlugReceiver extends BroadcastReceiver {

    private OnHeadsetPlugListener mHeadsetPlugListener;

    public HeadsetPlugReceiver () {

    }

    public void setHeadsetPlugListener (OnHeadsetPlugListener mHeadsetPlugListener) {
        this.mHeadsetPlugListener = mHeadsetPlugListener;
    }

    @Override
    public void onReceive (Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                LogUtils.e(action);
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    // 蓝牙头戴式耳机，支持语音输入输出
                    int state = adapter.getProfileConnectionState(BluetoothProfile.HEADSET);
                    if (BluetoothProfile.STATE_CONNECTED == state) {
                        if (mHeadsetPlugListener != null) {
                            mHeadsetPlugListener.onHeadsetPlug(SystemConstants.PlugType.BLUETOOTH_PLUG_TYPE,true);
                        }
                    } else {
                        if (mHeadsetPlugListener != null) {
                            mHeadsetPlugListener.onHeadsetPlug(SystemConstants.PlugType.BLUETOOTH_PLUG_TYPE,false);
                        }
                    }
                }
            } else if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        //外放
                        if (mHeadsetPlugListener != null) {
                            mHeadsetPlugListener.onHeadsetPlug(SystemConstants.PlugType.DEVICE_EXTERNAL_RELEASE,true);
                        }
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        //耳机
                        if (mHeadsetPlugListener != null) {
                            mHeadsetPlugListener.onHeadsetPlug(SystemConstants.PlugType.DEVICE_LINK_PLUG_TYPE,false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnHeadsetPlugListener {
        //type 为耳机类型
        //true说明没有耳机   false说明有耳机
        void onHeadsetPlug (int type,boolean isPlug);
    }
}