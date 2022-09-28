package com.corpize.sdk.ivoice.recorder;

import java.io.File;

public interface OnRecorderListener {
    //开始录音
    void onStartRecorder ();

    //取消录音
    void onCancelRecorder ();

    //停止录音
    void onStopRecorder (File file);

    //录音失败
    void onFailureRecorder ();
}