package com.corpize.sdk.ivoice.utils;

import android.app.Activity;

import com.corpize.sdk.ivoice.bean.UpVoiceResultBean;
import com.corpize.sdk.ivoice.listener.OnVolumeEndListener;

import java.lang.ref.WeakReference;

/**
 * author : xpSun
 * date : 2022/1/19
 * description :音频播放结束后的互动工具类
 */
public class CommonInteractiveUtils {

    private static CommonInteractiveUtils             instance;
    private        WeakReference<Activity>            activityWeakReference;
    private        int                                intervalTime;
    private        OnVoiceInteractiveResponseListener onVoiceInteractiveResponseListener;

    public CommonInteractiveUtils setOnVoiceInteractiveResponseListener (OnVoiceInteractiveResponseListener onVoiceInteractiveResponseListener) {
        this.onVoiceInteractiveResponseListener = onVoiceInteractiveResponseListener;
        return getInstance();
    }

    public interface OnVoiceInteractiveResponseListener {
        void onVoiceInteractiveResponse (int response);
    }

    private CommonInteractiveUtils () {
    }

    public static CommonInteractiveUtils getInstance () {
        if (null == instance) {
            instance = new CommonInteractiveUtils();
        }
        return instance;
    }

    public CommonInteractiveUtils Builder (Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
        initInteractive();
        return getInstance();
    }

    public CommonInteractiveUtils setIntervalTime (int intervalTime) {
        this.intervalTime = intervalTime;
        return getInstance();
    }

    private CommonInteractiveUtils initInteractive () {
        int      permission = checkNeedPermissions();
        Activity activity   = activityWeakReference.get();

        if (null == activity) {
            return getInstance();
        }

        switch (permission) {
            //没有任何权限
            case PermissionUtil.PERMISSION_CODE_NOPERMISSION:
                break;
            //只有摇一摇
            case PermissionUtil.PERMISSION_CODE_SHAKE:
                //摇一摇和录音的权限
            case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD:
                initShake(activity);
                initCountDown(activity);
                break;
            //摇一摇和录音还有读写的权限
            case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD_WRITE:
                initShake(activity);
                initVoice(activity);
                initCountDown(activity);
                break;
            //有录音和读写权限
            case PermissionUtil.PERMISSION_CODE_RECORD_WRITE:
                initVoice(activity);
                initCountDown(activity);
                break;
            default:
                break;
        }
        return getInstance();
    }

    //摇一摇
    private void initShake (Activity activity) {
        final CommonShakeInteractiveUtil  commonShakeInteractiveUtil  = CommonShakeInteractiveUtil.getInstance().Builder(activity);
        final CommonVoiceInteractiveUtils commonVoiceInteractiveUtils = CommonVoiceInteractiveUtils.getInstance().builder(activity);

        commonShakeInteractiveUtil.setOnShakeListener(new ShakeUtils.OnShakeListener() {
            @Override
            public void onShake () {
                if (onVoiceInteractiveResponseListener != null) {
                    onVoiceInteractiveResponseListener.onVoiceInteractiveResponse(UpVoiceResultBean.SUCCESS);
                }

                if (commonShakeInteractiveUtil != null) {
                    commonShakeInteractiveUtil.clearShake();
                }

                if (commonVoiceInteractiveUtils != null) {
                    commonVoiceInteractiveUtils.cancel();
                }
            }
        }).initShake();
    }

    //语音互动
    private void initVoice (Activity activity) {
        final CommonShakeInteractiveUtil  commonShakeInteractiveUtil  = CommonShakeInteractiveUtil.getInstance().Builder(activity);
        final CommonVoiceInteractiveUtils commonVoiceInteractiveUtils = CommonVoiceInteractiveUtils.getInstance().builder(activity);

        commonVoiceInteractiveUtils.initRecorderOperation(new OnVolumeEndListener() {
            @Override
            public void volumeEnd () {
                if (commonShakeInteractiveUtil != null) {
                    commonShakeInteractiveUtil.clearShake();
                }
            }
        }).setRecordListener(new QcHttpUtil.QcHttpOnListener<UpVoiceResultBean>() {
            @Override
            public void OnQcCompletionListener (UpVoiceResultBean response) {
                if (onVoiceInteractiveResponseListener != null) {
                    onVoiceInteractiveResponseListener.onVoiceInteractiveResponse(response.getCode());
                }
            }

            @Override
            public void OnQcErrorListener (String error, int code) {
                // 否定，结束交互
                if (onVoiceInteractiveResponseListener != null) {
                    onVoiceInteractiveResponseListener.onVoiceInteractiveResponse(UpVoiceResultBean.FAIl);
                }
            }
        }).start();
    }

    //倒计时
    private void initCountDown (Activity activity) {
        final CommonShakeInteractiveUtil  commonShakeInteractiveUtil  = CommonShakeInteractiveUtil.getInstance().Builder(activity);
        final CommonVoiceInteractiveUtils commonVoiceInteractiveUtils = CommonVoiceInteractiveUtils.getInstance().builder(activity);

        CommonCountDownUtils.getInstance()
                .init(intervalTime, new CommonCountDownUtils.OnCustomCountDownListener() {
                    @Override
                    public void onCustomCountDown (int timer) {
                        if (commonShakeInteractiveUtil != null) {
                            commonShakeInteractiveUtil.clearShake();
                        }

                        if (commonVoiceInteractiveUtils != null) {
                            commonVoiceInteractiveUtils.cancel();
                        }

                        // 否定，结束交互
                        if (onVoiceInteractiveResponseListener != null) {
                            onVoiceInteractiveResponseListener.onVoiceInteractiveResponse(UpVoiceResultBean.FAIl);
                        }
                    }
                });
    }

    public void cancel(){
        Activity activity = activityWeakReference.get();

        if(null == activity){
            return;
        }

        final CommonShakeInteractiveUtil  commonShakeInteractiveUtil  = CommonShakeInteractiveUtil.getInstance().Builder(activity);
        final CommonVoiceInteractiveUtils commonVoiceInteractiveUtils = CommonVoiceInteractiveUtils.getInstance().builder(activity);

        //摇一摇
        if (commonShakeInteractiveUtil != null) {
            commonShakeInteractiveUtil.clearShake();
        }

        //语音互动
        if (commonVoiceInteractiveUtils != null) {
            commonVoiceInteractiveUtils.cancel();
        }

        //倒计时
        CommonCountDownUtils.getInstance().cancel();
    }

    // 判断权限
    private int checkNeedPermissions () {
        Activity activity = activityWeakReference.get();
        if (null == activity) {
            return PermissionUtil.PERMISSION_CODE_NOPERMISSION;
        }
        return PermissionUtil.checkAudioAndWritePermissions(activity);
    }
}
