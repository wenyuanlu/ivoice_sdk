package com.corpize.sdk.ivoice.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.bean.response.DownLoadBean;
import com.corpize.sdk.ivoice.bean.response.EventtrackersBean;
import com.corpize.sdk.ivoice.common.CommonShakeEnum;
import com.corpize.sdk.ivoice.common.CommonUtils;
import com.corpize.sdk.ivoice.dialog.CustomDownloadConfirmDialog;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DonwloadSaveImg;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DownloadInstaller;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DownloadProgressCallBack;
import com.corpize.sdk.ivoice.video.ThirdAppUtils;

import java.util.List;

/**
 * author : xpSun
 * date : 2022/1/20
 * description :
 */
public class CommonInteractiveEventUtils {

    private boolean mHaveSnake        = false;       //是否摇一摇曝光
    private boolean mHaveSnakeClick   = false;       //是否摇一摇曝光的点击事件
    private boolean mHaveDownStart    = false;       //是否发送开始下载曝光请求
    private boolean mHaveDownComplete = false;       //是否发送完成下载曝光请求
    private boolean mHaveDownInstall  = false;       //是否发送开始安装曝光请求

    private int provider;

    public interface OnInteractiveEventListener {
        void onPlayerStatusChanger (boolean pause);

        void onAdClick ();
    }

    private OnInteractiveEventListener onInteractiveEventListener;

    private CommonInteractiveEventUtils () {
    }

    private static CommonInteractiveEventUtils instance;

    public static CommonInteractiveEventUtils getInstance () {
        if (null == instance) {
            instance = new CommonInteractiveEventUtils();
        }
        return instance;
    }

    public CommonInteractiveEventUtils setProvider (int provider) {
        this.provider = provider;
        return getInstance();
    }

    public void onShakeEvent (
            final Activity activity,
            final AdAudioBean bean,
            OnInteractiveEventListener onInteractiveEventListener) {
        onShakeEvent(activity, bean, 0, onInteractiveEventListener);
    }

    public void onShakeEvent (
            final Activity activity,
            final AdAudioBean bean,
            int mPosition,
            OnInteractiveEventListener onInteractiveEventListener) {
        try {
            if (bean == null) {
                return;
            }

            VoiceInteractiveUtil.getInstance().onDismiss();

            List<String> clks   = bean.getClks();
            int          action = bean.getAction();
            String       ldp    = bean.getLdp();

            //摇一摇事件的曝光
            if (!mHaveSnake) {
                mHaveSnake = true;
                sendShowExposure(clks);
            }

            //摇一摇的点击返回
            if (onInteractiveEventListener != null) {
                onInteractiveEventListener.onAdClick();
            }

            //处于前台
            if (CommonShakeEnum.COMMON_ENUM_WEBVIEW.getAction() == action) {   // 1 - App webview 打开链接
                if (!TextUtils.isEmpty(ldp)) {
                    SpUtils.saveBoolean("show", false);//手动设置切到了后台
                    if (onInteractiveEventListener != null) {
                        onInteractiveEventListener.onPlayerStatusChanger(true);
                    }
                    CommonClickWebViewUtils.openWebView(activity, ldp, mPosition);
                }
            } else if (CommonShakeEnum.COMMON_ENUM_SYSTEM_WEB.getAction() == action) {   // 2 - 系统浏览器打开链接
                if (!TextUtils.isEmpty(ldp)) {
                    if (onInteractiveEventListener != null) {
                        onInteractiveEventListener.onPlayerStatusChanger(true);
                    }
                    CommonClickWebViewUtils.openCommonExternal(activity, ldp, mPosition);
                }
            } else if (CommonShakeEnum.COMMON_ENUM_PHONE.getAction() == action) {   // 3 - 拨打电话
                if (onInteractiveEventListener != null) {
                    onInteractiveEventListener.onPlayerStatusChanger(true);
                }
                CommonUtils.callPhone(activity, ldp, mPosition);
            } else if (CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction() == action) {   // 6 - 下载APP
                if (1 == bean.getDirect_download()) {
                    downApk(activity, bean);
                } else {
                    if (onInteractiveEventListener != null) {
                        onInteractiveEventListener.onPlayerStatusChanger(true);
                    }

                    if (null != activity &&
                            !activity.isDestroyed() &&
                            null != bean.getDownload()
                    ) {
                        CustomDownloadConfirmDialog customDownloadConfirmDialog = new CustomDownloadConfirmDialog(activity);
                        customDownloadConfirmDialog.setOnConfirmClick(new View.OnClickListener() {
                            @Override
                            public void onClick (View v) {
                                downApk(activity, bean);
                            }
                        });

                        DownLoadBean downLoadBean = bean.getDownload();

                        String iconUrl         = downLoadBean.getLogo();
                        String appName         = downLoadBean.getName();
                        String appVersion      = downLoadBean.getVersion();
                        String appDeveloper    = downLoadBean.getAuthor();
                        String jurisdictionUrl = downLoadBean.getPermission();
                        String privacyUrl      = downLoadBean.getPolicy();

                        customDownloadConfirmDialog.setContentValue(
                                iconUrl,
                                appName,
                                appVersion,
                                appDeveloper,
                                jurisdictionUrl,
                                privacyUrl
                        );
                        customDownloadConfirmDialog.showDialog();
                    }
                }
            } else if (CommonShakeEnum.COMMON_ENUM_DEEPLINK.getAction() == action) {   // 7 - deeplink 链接
                String deeplink = bean.getDeeplink();
                if (!TextUtils.isEmpty(deeplink) && ThirdAppUtils.openLinkApp(activity, deeplink, mPosition)) {
                    if (onInteractiveEventListener != null) {
                        onInteractiveEventListener.onPlayerStatusChanger(true);
                    }
                } else {
                    if (!TextUtils.isEmpty(ldp)) {
                        SpUtils.saveBoolean("show", false);//手动设置切到了后台
                        if (onInteractiveEventListener != null) {
                            onInteractiveEventListener.onPlayerStatusChanger(true);
                        }
                        CommonClickWebViewUtils.openCommonExternal(activity, ldp, mPosition);
                    }
                }

            } else if (CommonShakeEnum.COMMON_ENUM_COUPON.getAction() == action) {
                if (!TextUtils.isEmpty(ldp)) {
                    if (onInteractiveEventListener != null) {
                        onInteractiveEventListener.onPlayerStatusChanger(true);
                    }
                    DialogUtils.showWebDialog(activity, ldp);
                }

                //下载图文
                String coupon = bean.getLdp();
                if (!TextUtils.isEmpty(coupon)) {
                    DonwloadSaveImg.donwloadImg(activity, coupon);
                }
            } else {
                if (!TextUtils.isEmpty(ldp)) {
                    if (onInteractiveEventListener != null) {
                        onInteractiveEventListener.onPlayerStatusChanger(true);
                    }
                    CommonClickWebViewUtils.openCommonExternal(activity, ldp, mPosition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载的通用方法
     *
     * @param activity
     * @param bean
     */
    private void downApk (Activity activity, AdAudioBean bean) {
        if (null != bean && !TextUtils.isEmpty(bean.getLdp())) {
            String                  downUrl       = bean.getLdp();
            final EventtrackersBean eventtrackers = bean.getEventtrackers();
            new DownloadInstaller(activity, downUrl, new DownloadProgressCallBack() {
                @Override
                public void downloadProgress (int progress) {
                    if (!mHaveDownStart && eventtrackers != null) {
                        mHaveDownStart = true;
                        sendShowExposure(eventtrackers.getStartdownload());
                    }

                    if (progress == 100) {
                        if (!mHaveDownComplete && eventtrackers != null) {
                            mHaveDownComplete = true;
                            sendShowExposure(eventtrackers.getCompletedownload());
                        }
                    }
                }

                @Override
                public void downloadException (Exception e) {
                }

                @Override
                public void onInstallStart () {
                    LogUtils.d("开始安装=");
                    if (!mHaveDownStart && eventtrackers != null) {
                        mHaveDownStart = true;
                        sendShowExposure(eventtrackers.getStartdownload());
                    }
                    if (!mHaveDownComplete && eventtrackers != null) {
                        mHaveDownComplete = true;
                        sendShowExposure(eventtrackers.getCompletedownload());
                    }
                    if (!mHaveDownInstall && eventtrackers != null) {
                        mHaveDownInstall = true;
                        sendShowExposure(eventtrackers.getStartinstall());
                    }
                }
            }).start();
        }
    }

    private void downApk (Activity activity, int mPosition, final EventtrackersBean eventtrackers, String downUrl) {
        new DownloadInstaller(mPosition, activity, downUrl, new DownloadProgressCallBack() {
            @Override
            public void downloadProgress (int progress) {
                if (!mHaveDownStart && eventtrackers != null) {
                    mHaveDownStart = true;
                    sendShowExposure(eventtrackers.getStartdownload());
                }

                if (progress == 100) {
                    if (!mHaveDownComplete && eventtrackers != null) {
                        mHaveDownComplete = true;
                        sendShowExposure(eventtrackers.getCompletedownload());
                    }
                }
            }

            @Override
            public void downloadException (Exception e) {
            }

            @Override
            public void onInstallStart () {
                LogUtils.d("开始安装=");
                if (!mHaveDownStart && eventtrackers != null) {
                    mHaveDownStart = true;
                    sendShowExposure(eventtrackers.getStartdownload());
                }
                if (!mHaveDownComplete && eventtrackers != null) {
                    mHaveDownComplete = true;
                    sendShowExposure(eventtrackers.getCompletedownload());
                }
                if (!mHaveDownInstall && eventtrackers != null) {
                    mHaveDownInstall = true;
                    sendShowExposure(eventtrackers.getStartinstall());
                }
            }
        }).start();
    }

    //发送曝光,计算了宽高及时间戳
    private void sendShowExposure (List<String> imgList) {
        CommonSendShowExposureUtils.getInstance().setProvider(provider).sendShowExposure(imgList);
    }
}
