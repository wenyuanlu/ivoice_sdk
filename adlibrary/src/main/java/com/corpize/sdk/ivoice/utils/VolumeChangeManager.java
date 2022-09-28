package com.corpize.sdk.ivoice.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import java.lang.ref.WeakReference;

/**
 * author ：yh
 * date : 2020-12-11 14:08
 * description : 音量的监听
 */
public class VolumeChangeManager {

    private static final String VOLUME_CHANGED_ACTION    = "android.media.VOLUME_CHANGED_ACTION";
    private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

    private        VolumeChangeListener    mVolumeChangeListener;
    private        VolumeBroadcastReceiver mVolumeBroadcastReceiver;
    private        Context                 mContext;
    private        AudioManager            mAudioManager;
    private        boolean                 mRegistered = false;
    private static int                     mMaxVolum   = 0;
    private static int                     mLastVolum  = 0;//上一次的音量

    public VolumeChangeManager (Context context) {
        mContext = context;
        mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = getCurrentMusicVolume();
        int maxVolume     = getMaxMusicVolume();
        int volum         = currentVolume * 100 / maxVolume;
        mLastVolum = volum;
    }

    /**
     * 系统媒体音量变化
     */
    public interface VolumeChangeListener {
        void onVolumeChanged (int volume);
    }

    /**
     * 获取当前媒体音量
     * STREAM_MUSIC 媒体音量,有时候
     */
    public int getCurrentMusicVolume () {
        return mAudioManager != null ? mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) : -1;
    }

    /**
     * 获取当前媒体音量
     * STREAM_MUSIC 媒体音量,有时候
     */
    public int getLastVolum () {
        return mLastVolum;
    }

    /**
     * 获取系统最大媒体音量
     */
    public int getMaxMusicVolume () {
        if (mAudioManager != null) {
            return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        } else {
            mAudioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            return mAudioManager != null ? mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) : 15;
        }
    }

    /**
     * 设置当前媒体音量
     */
    public void setMusicVolume (int volume) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    /**
     * 调整音量，自定义
     *
     * @param num 0-100
     * @return 改完后的音量值
     */
    public void setMusicVolume100 (int num) {
        if (num == 0) {
            setMusicVolume(0);
        } else {
            /*int maxVolum = getMaxMusicVolume();
            int a        = num * maxVolum / 100;
            //int a = (int) Math.ceil((num) * getMaxMusicVolume() * 0.01);
            a = a <= 0 ? 1 : a;
            a = a >= maxVolum ? maxVolum : a;
            setMusicVolume(a);*/
            int a = (int) Math.ceil((num) * getMaxMusicVolume() * 0.01);
            a = a <= 0 ? 0 : a;
            a = a >= 100 ? 100 : a;
            setMusicVolume(a);
        }

        //return get100CurrentVolume();
    }

    public VolumeChangeListener getVolumeChangeListener () {
        return mVolumeChangeListener;
    }

    public void setVolumeChangeListener (VolumeChangeListener volumeChangeListener) {
        this.mVolumeChangeListener = volumeChangeListener;
    }

    /**
     * 注册音量广播接收器
     */
    public void registerReceiver () {
        mVolumeBroadcastReceiver = new VolumeBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(VOLUME_CHANGED_ACTION);
        mContext.registerReceiver(mVolumeBroadcastReceiver, filter);
        mRegistered = true;
    }

    /**
     * 解注册音量广播监听器，需要与 registerReceiver 成对使用
     */
    public void unregisterReceiver () {
        if (mRegistered) {
            try {
                mContext.unregisterReceiver(mVolumeBroadcastReceiver);
                mVolumeChangeListener = null;
                mRegistered = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * STREAM_ALARM 警报
     * STREAM_MUSIC 音乐回放即媒体音量
     * STREAM_NOTIFICATION 窗口顶部状态栏Notification,
     * STREAM_RING 铃声
     * STREAM_SYSTEM 系统
     * STREAM_VOICE_CALL 通话
     */
    private static class VolumeBroadcastReceiver extends BroadcastReceiver {
        private WeakReference<VolumeChangeManager> mObserverWeakReference;

        public VolumeBroadcastReceiver (VolumeChangeManager volumeChangeObserver) {
            mObserverWeakReference = new WeakReference<>(volumeChangeObserver);
        }

        @Override
        public void onReceive (Context context, Intent intent) {
            if (VOLUME_CHANGED_ACTION.equals(intent.getAction())) {
                //系统音量改变才通知 有的手机媒体音量获取不准确
                VolumeChangeManager observer = mObserverWeakReference.get();
                if (observer != null) {
                    VolumeChangeListener listener = observer.getVolumeChangeListener();
                    if (listener != null) {
                        int currentVolume = observer.getCurrentMusicVolume();
                        int maxVolume     = observer.getMaxMusicVolume();
                        int volum         = currentVolume * 100 / maxVolume;
                        if (volum == 0 || volum == 100) {
                            mLastVolum = volum;
                            listener.onVolumeChanged(volum);
                        } else {
                            if (volum != mLastVolum) {
                                mLastVolum = volum;
                                listener.onVolumeChanged(volum);
                            }
                        }
                    }
                }
            }
        }
    }
}
