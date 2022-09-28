package com.corpize.sdk.ivoice.utils;

import android.Manifest;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.corpize.sdk.ivoice.listener.OnVolumeChangerListener;

public class CustomMonitorVolumeUtils {

    private static final String      TAG               = "CustomRecordUtils";
    private static final int         SAMPLE_RATE_IN_HZ = 8000;
    private static final int         BUFFER_SIZE       = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    private              AudioRecord mAudioRecord;
    private              boolean     isGetVoiceRun;
    private              Object      mLock;
    private              Context     mContext;

    private short[]                 buffer;
    private OnVolumeChangerListener onVolumeChangerListener;
    private Thread                  thread;

    private static final int     SEND_HANDLER_MESSAGE_WHAT = 0x1001;
    private              Handler handler                   = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage (@NonNull Message msg) {
            super.handleMessage(msg);

            if (SEND_HANDLER_MESSAGE_WHAT == msg.what) {
                getVolumeLevel();
            }
        }
    };

    public void setOnVolumeChangerListener (OnVolumeChangerListener onVolumeChangerListener) {
        this.onVolumeChangerListener = onVolumeChangerListener;
    }

    public CustomMonitorVolumeUtils (Context mContext) {
        this.mContext = mContext;
        mLock = new Object();
        buffer = new short[BUFFER_SIZE];
    }

    public void getNoiseLevel () {
        if (isGetVoiceRun) {
            Log.e(TAG, "还在录着呢");
            return;
        }
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        if (mAudioRecord == null) {
            Log.e("sound", "mAudioRecord初始化失败");
        }
        isGetVoiceRun = true;

        thread = new Thread(new Runnable() {
            @Override
            public void run () {
                try {
                    boolean hasRecord = PermissionUtil.checkPermission(mContext, Manifest.permission.RECORD_AUDIO);
                    if (!hasRecord) {
                        Log.e(TAG, "hasRecord:" + hasRecord);
                        return;
                    }

                    mAudioRecord.startRecording();
                    getVolumeLevel();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "录制异常:" + e.getMessage());
                    e.printStackTrace();

                    if (onVolumeChangerListener != null) {
                        onVolumeChangerListener.volumeEnd();
                    }
                }
            }
        });
        thread.start();
    }

    private void getVolumeLevel () {
        if(null == mAudioRecord || null == buffer){
            stopNoiseLevel();

            if (onVolumeChangerListener != null) {
                onVolumeChangerListener.volumeEnd();
            }
            return;
        }

        //r是实际读取的数据长度，一般而言r会小于bufferSize
        int  r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
        long v = 0;
        // 将 buffer 内容取出，进行平方和运算
        for (int i = 0; i < buffer.length; i++) {
            v += buffer[i] * buffer[i];
        }
        // 平方和除以数据总长度，得到音量大小。
        double mean   = v / (double) r;
        double volume = 10 * Math.log10(mean);
        Log.d(TAG, "分贝值:" + volume);

        if (null != onVolumeChangerListener) {
            onVolumeChangerListener.volumeChanger(volume);
        }

        if (isGetVoiceRun) {
            handler.sendEmptyMessageDelayed(SEND_HANDLER_MESSAGE_WHAT, 100);
        }
    }

    public void stopNoiseLevel () {
        try {
            if(null != mAudioRecord){
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }

            isGetVoiceRun = false;

            handler.removeMessages(SEND_HANDLER_MESSAGE_WHAT);

            if (thread != null) {
                thread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}