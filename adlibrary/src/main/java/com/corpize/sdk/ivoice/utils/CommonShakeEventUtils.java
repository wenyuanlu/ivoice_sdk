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
import com.corpize.sdk.ivoice.listener.QCADListener;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DonwloadSaveImg;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DownloadInstaller;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DownloadProgressCallBack;
import com.corpize.sdk.ivoice.video.ThirdAppUtils;

import java.util.List;

/**
 * author : xpSun
 * date : 12/24/21
 * description :摇一摇及点击互动操作
 */
@Deprecated
public class CommonShakeEventUtils {

    private int mWidth  = 0;
    private int mHeight = 0;

    private boolean mHaveSnake        = false;       //是否摇一摇曝光
    private boolean mHaveSnakeClick   = false;       //是否摇一摇曝光的点击事件
    private boolean mHaveDownStart    = false;       //是否发送开始下载曝光请求
    private boolean mHaveDownComplete = false;       //是否发送完成下载曝光请求
    private boolean mHaveDownInstall  = false;       //是否发送开始安装曝光请求

    private Integer provider;

    public interface onPlayerStatusChangerListener {
        void onPlayerStatusChanger (boolean pause);
    }

    public CommonShakeEventUtils (
            int mWidth,
            int mHeight,
            boolean mHaveSnake,
            boolean mHaveSnakeClick,
            boolean mHaveDownStart,
            boolean mHaveDownComplete,
            boolean mHaveDownInstall) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.mHaveSnake = mHaveSnake;
        this.mHaveSnakeClick = mHaveSnakeClick;
        this.mHaveDownStart = mHaveDownStart;
        this.mHaveDownComplete = mHaveDownComplete;
        this.mHaveDownInstall = mHaveDownInstall;
    }

    public void setProvider (Integer provider) {
        this.provider = provider;
    }

    public void onShakeEvent (
            final Activity activity,
            final AdAudioBean bean,
            int mPosition,
            QCADListener qcadListener,
            onPlayerStatusChangerListener onPlayerStatusChangerListener) {
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
            if (qcadListener != null) {
                qcadListener.onAdClick();
            }

            //处于前台
            if (CommonShakeEnum.COMMON_ENUM_WEBVIEW.getAction() == action) {   // 1 - App webview 打开链接
                if (!TextUtils.isEmpty(ldp)) {
                    SpUtils.saveBoolean("show", false);//手动设置切到了后台
                    if (onPlayerStatusChangerListener != null) {
                        onPlayerStatusChangerListener.onPlayerStatusChanger(true);
                    }
                    CommonClickWebViewUtils.openWebView(activity, ldp, mPosition);
                }
            } else if (CommonShakeEnum.COMMON_ENUM_SYSTEM_WEB.getAction() == action) {   // 2 - 系统浏览器打开链接
                if (!TextUtils.isEmpty(ldp)) {
                    if (onPlayerStatusChangerListener != null) {
                        onPlayerStatusChangerListener.onPlayerStatusChanger(true);
                    }
                    CommonClickWebViewUtils.openCommonExternal(activity, ldp, mPosition);
                }
            } else if (CommonShakeEnum.COMMON_ENUM_PHONE.getAction() == action) {   // 3 - 拨打电话
                if (onPlayerStatusChangerListener != null) {
                    onPlayerStatusChangerListener.onPlayerStatusChanger(true);
                }
                CommonUtils.callPhone(activity, ldp, mPosition);
            } else if (CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction() == action) {   // 6 - 下载APP
                if (1 == bean.getDirect_download()) {
                    downApk(activity, bean);
                } else {
                    if (onPlayerStatusChangerListener != null) {
                        onPlayerStatusChangerListener.onPlayerStatusChanger(true);
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
                    if (onPlayerStatusChangerListener != null) {
                        onPlayerStatusChangerListener.onPlayerStatusChanger(true);
                    }
                } else {
                    if (!TextUtils.isEmpty(ldp)) {
                        SpUtils.saveBoolean("show", false);//手动设置切到了后台
                        if (onPlayerStatusChangerListener != null) {
                            onPlayerStatusChangerListener.onPlayerStatusChanger(true);
                        }
                        CommonClickWebViewUtils.openCommonExternal(activity, ldp, mPosition);
                    }
                }

            } else if (CommonShakeEnum.COMMON_ENUM_COUPON.getAction() == action) {
                if (!TextUtils.isEmpty(ldp)) {
                    if (onPlayerStatusChangerListener != null) {
                        onPlayerStatusChangerListener.onPlayerStatusChanger(true);
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
                    if (onPlayerStatusChangerListener != null) {
                        onPlayerStatusChangerListener.onPlayerStatusChanger(true);
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

    /**
     * 发送曝光,计算了宽高及时间戳
     */
    private void sendShowExposure (List<String> imgList) {
        long time = System.currentTimeMillis();

        if (imgList != null && imgList.size() > 0) {
            for (int i = 0; i < imgList.size(); i++) {
                String urlOld = imgList.get(i);
                String url    = urlOld;
                if (url.contains("__WIDTH__")) {//宽度替换
                    url = url.replace("__WIDTH__", mWidth + "");
                }
                if (url.contains("__HEIGHT__")) {//高度替换
                    url = url.replace("__HEIGHT__", mHeight + "");
                }
                if (url.contains("__POSITION_X__")) {//抬起X轴的替换
                    url = url.replace("__POSITION_X__", 0 + "");
                }
                if (url.contains("__POSITION_Y__")) {//抬起Y轴的替换
                    url = url.replace("__POSITION_Y__", 0 + "");
                }
                if (url.contains("__TIME_STAMP__")) {//时间戳的替换
                    url = url.replace("__TIME_STAMP__", time + "");
                }

                url = String.format("%s&provider=%s", url, provider);
                QcHttpUtil.sendAdExposure(url);
            }
        }
    }
}
