package com.corpize.sdk.ivoice.utils;

import android.app.Activity;

import com.corpize.sdk.ivoice.bean.UpVoiceResultBean;
import com.corpize.sdk.ivoice.dialog.CustomLoadingProgressDialog;
import com.corpize.sdk.ivoice.dialog.CustomRecorderProgressDialog;
import com.corpize.sdk.ivoice.listener.OnVolumeChangerListener;
import com.corpize.sdk.ivoice.listener.OnVolumeEndListener;
import com.corpize.sdk.ivoice.recorder.OnRecorderListener;

import java.io.File;

/**
 * @author Created by SXF on 2021/7/7 3:30 PM.
 * @description 语音互动封装
 */
public class VoiceInteractiveUtil {

    private static final String                       TAG                         = VoiceInteractiveUtil.class.getSimpleName();
    //小于此分贝则停止录音
    private static final int                          ON_STOP_RECORDER_FLAG       = 60;
    //持续低于flag的音量的停止次数
    private static final int                          ON_STOP_RECORDER_COUNT_FLAG = 20;
    private static       int                          monitorVolumeCount          = 0;
    private static       CustomRecorderProgressDialog customRecorderProgressDialog;
    //是否启用dialog
    private static       boolean                      isEnableDialog              = true;
    private static       VoiceInteractiveUtil         instance;
    private              CustomLoadingProgressDialog  customLoadingProgressDialog;

    public static VoiceInteractiveUtil getInstance () {
        if (instance == null) {
            instance = new VoiceInteractiveUtil();
        }
        return instance;
    }

    public VoiceInteractiveUtil setEnableDialog (boolean enableDialog) {
        isEnableDialog = enableDialog;
        return this;
    }

    /**
     * 初始化录音操作，调用只需要初始化录音操作。工具返回录音结束回调
     *
     * @param mActivity
     * @param customMonitorVolumeUtils
     * @param onVolumeEndListener
     */
    public void initRecorderOperation (final Activity mActivity,
                                       final CustomMonitorVolumeUtils customMonitorVolumeUtils,
                                       final OnVolumeEndListener onVolumeEndListener) {
        monitorVolumeCount = 0;//重置次数
        CustomRecorderUtils.getInstance().startRecord(mActivity);

        if (isEnableDialog) {
            customRecorderProgressDialog = CustomRecorderProgressDialog.getInstance().builder(mActivity);
            customRecorderProgressDialog.showProgressDialog();
        }

        customMonitorVolumeUtils.setOnVolumeChangerListener(new OnVolumeChangerListener() {
            @Override
            public void volumeChanger (double volume) {
                int value = Double.valueOf(volume).intValue();

                if (ON_STOP_RECORDER_FLAG > value) {
                    monitorVolumeCount += 1;
                } else {
                    monitorVolumeCount = 0;
                }

                if (monitorVolumeCount >= ON_STOP_RECORDER_COUNT_FLAG) {
                    onVolumeChangerEnd(mActivity, onVolumeEndListener);
                }

                LogUtils.e(TAG + "volume" + String.valueOf(monitorVolumeCount));
            }

            @Override
            public void volumeEnd () {
                onVolumeChangerEnd(mActivity, onVolumeEndListener);
            }
        });
    }

    private void onVolumeChangerEnd(final Activity mActivity,
                                    final OnVolumeEndListener onVolumeEndListener){
        if (onVolumeEndListener != null) {
            onVolumeEndListener.volumeEnd();
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                CustomRecorderUtils.getInstance().stopRecorder();
                if (isEnableDialog && null != customRecorderProgressDialog) {
                    customRecorderProgressDialog.dismissProgressDialog();
                }
            }
        });
    }

    public void onDismiss () {
        if (null != customRecorderProgressDialog) {
            customRecorderProgressDialog.dismissProgressDialog();
        }

        if (null != customLoadingProgressDialog) {
            customLoadingProgressDialog.dismissProgressDialog();
        }
    }


    /**
     * 设置录音回调,封装方法，调用方不再关心录音结果。
     * 只回调录音完成后网络上传的成功与失败
     *
     * @param mActivity
     * @param qcHttpOnListener
     */
    public void setRecordListener (final Activity mActivity,
                                   final QcHttpUtil.QcHttpOnListener qcHttpOnListener) {
        CustomRecorderUtils.getInstance().setOnRecorderListener(new OnRecorderListener() {
            @Override
            public void onStartRecorder () {

            }

            @Override
            public void onCancelRecorder () {

            }

            @Override
            public void onStopRecorder (final File file) {
                if (file == null || !file.exists()) {
                    return;
                }

                LogUtils.e(TAG + "qcsdk,=====file大小：" + file.length() + "file名字:" + file.getName());
                LogUtils.e(TAG + "qcsdk,=====file路径：" + file.getPath());

                if (isEnableDialog) {
                    customLoadingProgressDialog = CustomLoadingProgressDialog.getInstance().builder(mActivity);
                    customLoadingProgressDialog.showProgressDialog();
                }

                QcHttpUtil.upVoiceFile(file.getPath(), new QcHttpUtil.QcHttpOnListener<UpVoiceResultBean>() {
                    @Override
                    public void OnQcCompletionListener (final UpVoiceResultBean response) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run () {
                                if (isEnableDialog && null != customLoadingProgressDialog) {
                                    customLoadingProgressDialog.dismissProgressDialog();
                                }

                                if (qcHttpOnListener != null) {
                                    qcHttpOnListener.OnQcCompletionListener(response);
                                }
                            }
                        });
                        LogUtils.e(TAG + "音频识别返回结果,code:" + response.getCode());
                        //上传完删除当前语音文件
                        if (file.exists()) {
                            file.delete();
                        }
                    }

                    @Override
                    public void OnQcErrorListener (final String erro, final int code) {
                        LogUtils.e(TAG + "音频识别返回结果,erro:" + erro + ",code:" + code);

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run () {
                                if (isEnableDialog && null != customLoadingProgressDialog) {
                                    customLoadingProgressDialog.dismissProgressDialog();
                                }

                                if (qcHttpOnListener != null) {
                                    qcHttpOnListener.OnQcErrorListener(erro, code);
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailureRecorder () {

            }
        });
    }
}
