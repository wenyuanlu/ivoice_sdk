package com.corpize.sdk.ivoice.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.corpize.sdk.ivoice.bean.AdMusicBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.bean.response.InteractiveBean;
import com.corpize.sdk.ivoice.listener.OnPlayerAdListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * author : xpSun
 * date : 2022/1/19
 * description :播放广告逻辑
 */
public class CommonPlayerAdUtils {

    private AdAudioBean             mResponse;
    private WeakReference<Activity> activityWeakReference;

    private int                            mAllTime;
    private int                            mIntervalTime;//互动结束后摇一摇时间
    private int                            remindsTime;//重试次数
    private CommonMediaPlayerListenerUtils commonMediaPlayerListenerUtils;//音频回调

    private OnPlayerAdListener onPlayerAdListener;

    public CommonPlayerAdUtils (Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
        commonMediaPlayerListenerUtils = new CommonMediaPlayerListenerUtils(activity);
    }

    public void setResponse (AdAudioBean mResponse) {
        stopAndReleaseAd();

        this.mResponse = mResponse;
        if (commonMediaPlayerListenerUtils != null) {
            commonMediaPlayerListenerUtils.setResponse(mResponse);
        }
    }

    public void setOnPlayerAdListener (OnPlayerAdListener onPlayerAdListener) {
        this.onPlayerAdListener = onPlayerAdListener;

        if (commonMediaPlayerListenerUtils != null) {
            commonMediaPlayerListenerUtils.setOnPlayerAdListener(onPlayerAdListener);
        }
    }

    public void playerAd () {
        if (null == mResponse) {
            return;
        }

        Activity activity = activityWeakReference.get();
        if (null != activity) {
            CommonInteractiveUtils.getInstance().Builder(activity).cancel();
        }

        mIntervalTime = null == mResponse.getInteractive() ? 0 : mResponse.getInteractive().getWait();
        remindsTime = mResponse.getInteractive().getReminds();

        if (!TextUtils.isEmpty(mResponse.getAudiourl())) {
            managerAdMusic();
        } else {
            managerAdWithOutMusic();
        }
    }


    /**
     * 停止播放音频
     */
    public void stopAndReleaseAd () {
        MediaPlayerUtil.getInstance().stopAndRelease();
        CommonSendShowExposureUtils.getInstance().musicPlayExposure(mResponse,0, 3);

        Activity activity = activityWeakReference.get();
        if (null != activity) {
            CommonInteractiveUtils.getInstance().Builder(activity).cancel();
        }
    }

    /**
     * 处理并加载音频广告
     */
    private void managerAdMusic () {
        if (null == mResponse) {
            return;
        }

        Activity activity = activityWeakReference.get();

        if (null == activity) {
            return;
        }

        mIntervalTime = null == mResponse.getInteractive() ? 0 : mResponse.getInteractive().getWait();
        final int volume = mResponse.getVolume();
        if (mResponse.getInteractive() != null) {
            remindsTime = mResponse.getInteractive().getReminds();

            List<AdMusicBean> voiceList = new ArrayList<>();
            voiceList.add(new AdMusicBean(mResponse.getAudiourl()));

            //添加广告结束后的提示音
            //判断权限
            int perMis = checkNeedPermissions();
            switch (perMis) {
                //都没有权限
                case PermissionUtil.PERMISSION_CODE_NOPERMISSION:
                    //无互动的时候
                    MediaPlayerUtil.getInstance().setMinVolume(volume);
                    if (commonMediaPlayerListenerUtils != null) {
                        MediaPlayerUtil.getInstance().playVoice(
                                activity,
                                mResponse.getAudiourl(),
                                commonMediaPlayerListenerUtils.mMediaOnListener
                        );
                    }
                    break;
                //只有摇一摇的权限
                case PermissionUtil.PERMISSION_CODE_SHAKE:
                    //摇一摇和录音的权限
                case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD:
                    //摇一摇和录音还有读写的权限
                case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD_WRITE:
                    //有录音和读写权限
                case PermissionUtil.PERMISSION_CODE_RECORD_WRITE:
                    AdMusicBean adMusicBean = CommonSplicingResourceUtils.getInstance().checkAdActionForMusicPathCheckScene(mResponse, perMis);
                    if (null != adMusicBean && !TextUtils.isEmpty(adMusicBean.getMusic())) {
                        voiceList.add(adMusicBean);
                    }
                    MediaPlayerUtil.getInstance().setMinVolume(volume);
                    if (commonMediaPlayerListenerUtils != null) {
                        MediaPlayerUtil.getInstance()
                                .playVoiceList(
                                        activity,
                                        voiceList,
                                        commonMediaPlayerListenerUtils.mediaMoreOnListener
                                );
                    }
                    break;
                default:
                    break;
            }
        } else {
            //无互动的时候
            MediaPlayerUtil.getInstance().setMinVolume(volume);
            if (commonMediaPlayerListenerUtils != null) {
                MediaPlayerUtil.getInstance().playVoice(
                        activity,
                        mResponse.getAudiourl(),
                        commonMediaPlayerListenerUtils.mMediaOnListener
                );
            }
        }
    }

    /**
     * 处理并加载音频广告
     *
     * @param bean
     */
    private void managerAdWithOutMusic () {
        if (null == mResponse) {
            return;
        }

        Activity activity = activityWeakReference.get();
        if (null == activity) {
            return;
        }

        mIntervalTime = null == mResponse.getInteractive() ? 0 : mResponse.getInteractive().getWait();
        final int volume = mResponse.getVolume();
        if (mResponse.getInteractive() != null) {
            //有互动的时候
            final InteractiveBean interactive = mResponse.getInteractive();
            remindsTime = mResponse.getInteractive().getReminds();
            String startMusic = CommonSplicingResourceUtils.getInstance().managerAdWithOutMusic(mResponse);
            if (!TextUtils.isEmpty(startMusic)) {
                //隐藏弹幕,隐藏弹幕按钮,隐藏播放按钮,开启互动播放,无结束页
                MediaPlayerUtil.getInstance().setMinVolume(volume);
                if (commonMediaPlayerListenerUtils != null) {
                    MediaPlayerUtil.getInstance().playVoice(
                            activity,
                            startMusic,
                            commonMediaPlayerListenerUtils.mMediaOnListener
                    );
                }
            }
        }
    }

    /**
     * 判断权限
     */
    private int checkNeedPermissions () {
        Activity activity = activityWeakReference.get();
        if (null == activity) {
            return PermissionUtil.PERMISSION_CODE_NOPERMISSION;
        }
        return PermissionUtil.checkAudioAndWritePermissions(activity);
    }
}
