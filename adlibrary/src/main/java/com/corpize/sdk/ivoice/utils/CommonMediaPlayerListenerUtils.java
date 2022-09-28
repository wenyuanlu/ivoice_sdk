package com.corpize.sdk.ivoice.utils;

import android.app.Activity;
import android.media.AudioManager;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.bean.UpVoiceResultBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.bean.response.InteractiveBean;
import com.corpize.sdk.ivoice.bean.response.RemindBean;
import com.corpize.sdk.ivoice.common.CommonShakeEnum;
import com.corpize.sdk.ivoice.listener.OnPlayerAdListener;

import java.lang.ref.WeakReference;

import static com.corpize.sdk.ivoice.utils.StartMusicUtils.getReStartMusic;

/**
 * author : xpSun
 * date : 2022/1/20
 * description :音频回调
 */
public class CommonMediaPlayerListenerUtils {

    private WeakReference<Activity> activityWeakReference;
    private AdAudioBean             mResponse;
    private int                     allTimer = 0;
    private int                     mIntervalTime;
    private int                     remindsTime;//重试次数

    private OnPlayerAdListener onPlayerAdListener;

    public CommonMediaPlayerListenerUtils (Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
        init();
    }

    public void setOnPlayerAdListener (OnPlayerAdListener onPlayerAdListener) {
        this.onPlayerAdListener = onPlayerAdListener;
    }

    public void setResponse (AdAudioBean mResponse) {
        this.mResponse = mResponse;
        mIntervalTime = null == mResponse.getInteractive() ? 0 : mResponse.getInteractive().getWait();
    }

    private void init () {
        CommonSendShowExposureUtils.getInstance()
                .setOnAdExposureListener(
                        new CommonSendShowExposureUtils.OnSendShowExposureListener() {
            @Override
            public void onAdExposure () {
                if (onPlayerAdListener != null) {
                    onPlayerAdListener.onAdExposure();
                }
            }
        });
    }

    //单播放的监听
    public MediaPlayerUtil.MediaOnListener mMediaOnListener = new MediaPlayerUtil.MediaOnListener() {
        @Override
        public void onPlayStartListener (final int allTime) {
            remindsTime = mResponse.getInteractive().getReminds();

            allTimer = allTime;
            //开始曝光监听
            CommonSendShowExposureUtils.getInstance().initDefaultValue();
            CommonSendShowExposureUtils.getInstance().musicPlayExposure(mResponse, 0, 0);
        }

        @Override
        public void onPlayCurrentTimeListener (int currentTime) {
            //播放时间的返回
            CommonSendShowExposureUtils.getInstance().musicPlayExposure(mResponse, currentTime, 1);
        }

        @Override
        public void onPlayCompletionListener () {
            if (onPlayerAdListener != null) {
                onPlayerAdListener.onAdCompleteCallBack();
            }

            //播放完成曝光监听
            CommonSendShowExposureUtils.getInstance().musicPlayExposure(mResponse, 0, 2);
        }

        @Override
        public void onPlayErrorListener (int code, String msg) {
            if (onPlayerAdListener != null) {
                onPlayerAdListener.onAdError("广告获取成功,播放失败," + msg);
            }
        }

        @Override
        public void onAudioFocusChange (int focusChange) {
            onAudioFocusChangeEvent(focusChange);
        }
    };

    //多播放的监听
    public MediaPlayerUtil.MediaMoreOnListener mediaMoreOnListener = new MediaPlayerUtil.MediaMoreOnListener() {
        @Override
        public void onPlayStartListener (int position, int allTime) {
            if (position == 0) {
                remindsTime = mResponse.getInteractive().getReminds();
                allTimer = allTime;
                CommonSendShowExposureUtils.getInstance().initDefaultValue();
                CommonSendShowExposureUtils.getInstance().musicPlayExposure(mResponse, 0, 0);
            }

            if (onPlayerAdListener != null) {
                onPlayerAdListener.onPlayStartListener(position, allTime);
            }
        }

        @Override
        public void onPlayStatusChangeListener (int position, int status) {
        }

        @Override
        public void onPlayCurrentTimeListener (int position, int currentTime) {
            if (position == 0) {
                CommonSendShowExposureUtils.getInstance().musicPlayExposure(mResponse, currentTime, 1);
            }
        }

        @Override
        public void onPlayCompletionListener () {
            int     lock_interaction_switch = SpUtils.getInt("lock_interaction_switch");
            boolean interactionFlag         = QCiVoiceSdk.get().isInteractionFlag();

            if(CommonShakeEnum.COMMON_ENUM_BRAND.getAction() == mResponse.getAction()){
                CommonCountDownUtils.getInstance()
                        .init(mIntervalTime,
                                new CommonCountDownUtils.OnCustomCountDownListener() {
                            @Override
                            public void onCustomCountDown (int timer) {
                                if (0 == timer) {
                                    QCiVoiceSdk.get().setInteractionFlag(false);

                                    if (onPlayerAdListener != null) {
                                        onPlayerAdListener.onAdCompleteCallBack();
                                    }
                                }
                            }
                        });
            } else if (!interactionFlag) {
                initCommonAdOperation();
            } else if (1 == lock_interaction_switch && interactionFlag) {
                CommonCountDownUtils.getInstance()
                        .init(mIntervalTime, new CommonCountDownUtils.OnCustomCountDownListener() {
                            @Override
                            public void onCustomCountDown (int timer) {
                                if (0 == timer) {
                                    QCiVoiceSdk.get().setInteractionFlag(false);
                                }
                            }
                        });
            } else {
                if (onPlayerAdListener != null) {
                    onPlayerAdListener.onAdCompleteCallBack();
                }
            }
        }

        @Override
        public void onPlayCenterPositionListener (int position) {
            if (position == 0) {
                //播放完成曝光监听
                CommonSendShowExposureUtils.getInstance().musicPlayExposure(mResponse, 0, 2);
            }

            if (onPlayerAdListener != null) {
                onPlayerAdListener.onPlayCenterPositionListener(position);
            }
        }

        @Override
        public void onPlayErrorListener (int code, String msg) {
            if (onPlayerAdListener != null) {
                onPlayerAdListener.onAdError("广告获取成功,播放失败," + msg);
            }
        }

        @Override
        public void onAudioFocusChange (int focusChange) {
            onAudioFocusChangeEvent(focusChange);
        }
    };

    private void onAudioFocusChangeEvent (int focusChange) {
        try {
            if (AudioManager.AUDIOFOCUS_GAIN == focusChange && 1 != MediaPlayerUtil.getInstance().getUserClickStop()) {

            } else {
                if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                    //是播放状态,则暂停播放,显示播放按钮
                    MediaPlayerUtil.getInstance().stopAndRelease();

                    if (onPlayerAdListener != null) {
                        onPlayerAdListener.onAdCompleteCallBack();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //摇一摇的点击事件,区分息屏和亮屏
    private void setShakeClick (Activity activity, AdAudioBean bean) {
        CommonInteractiveEventUtils.getInstance().onShakeEvent(
                activity,
                mResponse,
                new CommonInteractiveEventUtils.OnInteractiveEventListener() {
                    @Override
                    public void onPlayerStatusChanger (boolean pause) {
                        if (pause) {
                            MediaPlayerUtil.getInstance().stopAndRelease();
                        }
                    }

                    @Override
                    public void onAdClick () {
                        if (onPlayerAdListener != null) {
                            onPlayerAdListener.onAdClick();
                        }
                    }
                });
    }

    //播放重试语音
    private void playRestartMusic (AdAudioBean bean) {
        Activity activity = activityWeakReference.get();

        if (null == activity) {
            return;
        }

        remindsTime--;
        LogUtils.e("remindsTime,playRestartMusic======" + remindsTime);
        if (remindsTime < 0) {
            if (onPlayerAdListener != null) {
                onPlayerAdListener.onAdCompleteCallBack();
            }
            return;
        }

        final int             volume       = bean.getVolume();
        final InteractiveBean interactive  = bean.getInteractive();
        int                   perMis       = checkNeedPermissions();
        int                   action       = bean.getAction();
        RemindBean            remind       = interactive.getRemind();
        String                reStartMusic = getReStartMusic(perMis, remind, action);

        MediaPlayerUtil.getInstance().setMinVolume(volume);
        MediaPlayerUtil.getInstance().playVoice(activity, reStartMusic, new MediaPlayerUtil.MediaOnListener() {
            @Override
            public void onPlayStartListener (int allTime) {
                LogUtils.e("onPlayStartListener,onPlayStartListener======");
            }

            @Override
            public void onPlayCurrentTimeListener (int currentTime) {
                LogUtils.e("onPlayCurrentTimeListener,onPlayCurrentTimeListener======");
            }

            @Override
            public void onPlayCompletionListener () {
                LogUtils.e("onPlayCompletionListener,playRestartMusic======");
                //已经到了语音重试，证明是语音交互。并且有录音读写权限
                initCommonAdOperation();
            }

            @Override
            public void onPlayErrorListener (int code, String msg) {
                LogUtils.e("onPlayErrorListener,onPlayErrorListener======");

                if (onPlayerAdListener != null) {
                    onPlayerAdListener.onAdError(msg);
                }
            }

            @Override
            public void onAudioFocusChange (int focusChange) {
                onAudioFocusChangeEvent(focusChange);
            }
        });
    }

    private void initCommonAdOperation () {
        final Activity activity = activityWeakReference.get();

        if (null == activity) {
            return;
        }

        CommonInteractiveUtils
                .getInstance()
                .setIntervalTime(mIntervalTime)
                .setOnVoiceInteractiveResponseListener(
                        new CommonInteractiveUtils.OnVoiceInteractiveResponseListener() {
                            @Override
                            public void onVoiceInteractiveResponse (int response) {
                                //1:肯定，0:否定，999: 无法识别
                                if (UpVoiceResultBean.FAIl == response) {
                                    // 否定，结束交互
                                    if (onPlayerAdListener != null) {
                                        onPlayerAdListener.onAdCompleteCallBack();
                                    }
                                } else if (UpVoiceResultBean.SUCCESS == response) {
                                    //肯定，有互动的时候
                                    InteractiveBean interactive = mResponse.getInteractive();
                                    setShakeClick(activity, mResponse);
                                } else if (UpVoiceResultBean.UN_KNOW == response) {
                                    //无法识别重新播放语音。重新采集
                                    playRestartMusic(mResponse);
                                }
                            }
                        })
                .Builder(activity);
    }

    //判断权限
    private int checkNeedPermissions () {
        Activity activity = activityWeakReference.get();
        if (null == activity) {
            return PermissionUtil.PERMISSION_CODE_NOPERMISSION;
        }
        return PermissionUtil.checkAudioAndWritePermissions(activity);
    }
}
