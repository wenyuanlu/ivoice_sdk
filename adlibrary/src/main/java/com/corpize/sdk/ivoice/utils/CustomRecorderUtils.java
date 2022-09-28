package com.corpize.sdk.ivoice.utils;

import android.content.Context;

import com.corpize.sdk.ivoice.recorder.CustomAudioRecorder;
import com.corpize.sdk.ivoice.recorder.OnRecorderListener;
import com.corpize.sdk.ivoice.video.CustomCountDownTimer;

import java.io.File;
import java.util.Calendar;

/**
 * author : xpSun
 * date : 2021/5/12
 * description :
 */
public class CustomRecorderUtils {

    private static CustomRecorderUtils instance;
    private CustomAudioRecorder mRecorder;
    private String recorderFile;
    private OnRecorderListener onRecorderListener;
    private int timeSeconds = 5 * 1000;//最长录制时长，5秒
    private CustomCountDownTimer customCountDownTimer;


    public void setOnRecorderListener(OnRecorderListener onRecorderListener) {
        this.onRecorderListener = onRecorderListener;
    }

    public String getRecorderFile() {
        return recorderFile;
    }

    private CustomRecorderUtils() {
        mRecorder = CustomAudioRecorder.getInstance();

        mRecorder.setOnRecorderListener(new OnRecorderListener() {
            @Override
            public void onStartRecorder() {
                if (onRecorderListener != null) {
                    onRecorderListener.onStartRecorder();
                }
            }

            @Override
            public void onCancelRecorder() {
                if (onRecorderListener != null) {
                    onRecorderListener.onCancelRecorder();
                }
            }

            @Override
            public void onStopRecorder(File file) {
                if (onRecorderListener != null) {
                    onRecorderListener.onStopRecorder(file);
                }
            }

            @Override
            public void onFailureRecorder() {
                if (onRecorderListener != null) {
                    onRecorderListener.onFailureRecorder();
                }
            }
        });
    }

    public static CustomRecorderUtils getInstance() {
        if (instance == null) {
            instance = new CustomRecorderUtils();
        }
        return instance;
    }

    //开始录音 录音结束后发送
    public void startRecord(Context context) {
        try {
            String saveFile = context.getExternalFilesDir("phonic").getAbsolutePath() + "/";
            File file = new File(saveFile);
            if (!file.exists()) {
                file.mkdir();
            }
            String name = String.format("%s", Calendar.getInstance().getTime().getTime());
            recorderFile = String.format("%s/%s", file.getAbsoluteFile(), name);

            mRecorder.setBasePath(recorderFile);

            if (!mRecorder.isRecording()) {
                mRecorder.startRecord();
                setAudioTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置录音倒计时。倒计时结束停止录音
     * */
    private void setAudioTime() {
        customCountDownTimer = new CustomCountDownTimer(timeSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                stopRecorder();
            }
        };
        customCountDownTimer.start();//开始倒计时

    }

    //停止录音
    public void stopRecorder() {
        try {
            if (mRecorder.isRecording()) {
                mRecorder.stopRecord();
            }
            if (customCountDownTimer != null) {
                customCountDownTimer.cancel();
                customCountDownTimer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消录音
     */
    public void cancelRecorder() {
        try {
            if (mRecorder.isRecording()) {
                mRecorder.cancelRecord();
            }
            if (customCountDownTimer != null) {
                customCountDownTimer.cancel();
                customCountDownTimer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
