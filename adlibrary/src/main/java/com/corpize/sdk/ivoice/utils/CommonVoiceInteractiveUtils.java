package com.corpize.sdk.ivoice.utils;

import android.Manifest;
import android.app.Activity;

import com.corpize.sdk.ivoice.bean.UpVoiceResultBean;
import com.corpize.sdk.ivoice.dialog.CustomLoadingProgressDialog;
import com.corpize.sdk.ivoice.dialog.CustomRecorderProgressDialog;
import com.corpize.sdk.ivoice.listener.OnVolumeChangerListener;
import com.corpize.sdk.ivoice.listener.OnVolumeEndListener;
import com.corpize.sdk.ivoice.recorder.OnRecorderListener;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * author : xpSun
 * date : 2022/1/19
 * description :语音互动
 */
public class CommonVoiceInteractiveUtils {

    private static CommonVoiceInteractiveUtils instance;

    //小于此分贝则停止录音
    private static final int                      ON_STOP_RECORDER_FLAG       = 60;
    //持续低于flag的音量的停止次数
    private static final int                      ON_STOP_RECORDER_COUNT_FLAG = 20;
    //录音次数
    private static       int                      monitorVolumeCount          = 0;
    private              boolean                  isEnableDialog              = true;
    private              WeakReference<Activity>  activityWeakReference;
    private              CustomMonitorVolumeUtils customMonitorVolumeUtils;
    private              boolean                  isCancel                    = false;//是否已经结束

    private static final String TAG = CommonVoiceInteractiveUtils.class.getSimpleName();

    private CommonVoiceInteractiveUtils () {
    }

    public static CommonVoiceInteractiveUtils getInstance () {
        if (instance == null) {
            instance = new CommonVoiceInteractiveUtils();
        }
        return instance;
    }

    public CommonVoiceInteractiveUtils builder (Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
        return getInstance();
    }

    public CommonVoiceInteractiveUtils setEnableDialog (boolean isEnableDialog) {
        this.isEnableDialog = isEnableDialog;
        return getInstance();
    }

    public CommonVoiceInteractiveUtils initRecorderOperation (final OnVolumeEndListener onVolumeEndListener) {
        monitorVolumeCount = 0;
        final Activity activity = activityWeakReference.get();
        if (null == activity) {
            return getInstance();
        }

        boolean permission = PermissionUtil.checkPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (!permission) {
            if (onVolumeEndListener != null) {
                onVolumeEndListener.volumeEnd();
            }
            return getInstance();
        }

        if (null == customMonitorVolumeUtils) {
            customMonitorVolumeUtils = new CustomMonitorVolumeUtils(activity);
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
                    onVolumeChangerEnd(activity, onVolumeEndListener);
                }
            }

            @Override
            public void volumeEnd () {
                onDismiss();
                onVolumeChangerEnd(activity, onVolumeEndListener);
            }
        });

        return getInstance();
    }

    public void start () {
        Activity activity = activityWeakReference.get();

        if (null == activity) {
            return;
        }
        CustomRecorderUtils.getInstance().startRecord(activity);
        if (customMonitorVolumeUtils != null) {
            customMonitorVolumeUtils.getNoiseLevel();
        }

        if (isEnableDialog) {
            CustomRecorderProgressDialog.getInstance().builder(activity).showProgressDialog();
        }

        isCancel = false;
    }

    private void onVolumeChangerEnd (final Activity mActivity,
                                     final OnVolumeEndListener onVolumeEndListener) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                if (customMonitorVolumeUtils != null) {
                    customMonitorVolumeUtils.stopNoiseLevel();
                }

                if (onVolumeEndListener != null) {
                    onVolumeEndListener.volumeEnd();
                }
            }
        });
    }

    /**
     * 设置录音回调,封装方法，调用方不再关心录音结果。
     * 只回调录音完成后网络上传的成功与失败
     *
     * @param mActivity
     * @param qcHttpOnListener
     */
    public CommonVoiceInteractiveUtils setRecordListener (final QcHttpUtil.QcHttpOnListener qcHttpOnListener) {
        final Activity activity = activityWeakReference.get();
        if (null == activity) {
            return getInstance();
        }

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

                onDismiss();

                if (isEnableDialog) {
                    CustomLoadingProgressDialog.getInstance().builder(activity).showProgressDialog();
                }

                QcHttpUtil.upVoiceFile(file.getPath(), new QcHttpUtil.QcHttpOnListener<UpVoiceResultBean>() {
                    @Override
                    public void OnQcCompletionListener (final UpVoiceResultBean response) {
                        if (isCancel) {
                            return;
                        }

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run () {
                                CustomLoadingProgressDialog.getInstance().dismissProgressDialog();

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
                    public void OnQcErrorListener (final String error, final int code) {
                        LogUtils.e(TAG + "音频识别返回结果,error:" + error + ",code:" + code);

                        if (isCancel) {
                            return;
                        }

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run () {
                                CustomLoadingProgressDialog.getInstance().builder(activity).dismissProgressDialog();

                                if (qcHttpOnListener != null) {
                                    qcHttpOnListener.OnQcErrorListener(error, code);
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
        return getInstance();
    }

    public void onDismiss () {
        CustomRecorderProgressDialog.getInstance().dismissProgressDialog();
        CustomLoadingProgressDialog.getInstance().dismissProgressDialog();
    }

    public void cancel () {
        isCancel = true;
        onDismiss();
        CustomRecorderUtils.getInstance().cancelRecorder();
        if (customMonitorVolumeUtils != null) {
            customMonitorVolumeUtils.stopNoiseLevel();
        }
    }
}
