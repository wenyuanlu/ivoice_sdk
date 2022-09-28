package com.corpize.sdk.ivoice;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.corpize.sdk.ivoice.admanager.AudioAdManager;
import com.corpize.sdk.ivoice.admanager.AudioCustomAdManager;
import com.corpize.sdk.ivoice.admanager.AutoRotationManager;
import com.corpize.sdk.ivoice.admanager.QcAdManager;
import com.corpize.sdk.ivoice.admanager.QcCustomTemplateManager;
import com.corpize.sdk.ivoice.admanager.QcFirstVoiceAdManager;
import com.corpize.sdk.ivoice.admanager.QcRollAdManager;
import com.corpize.sdk.ivoice.admanager.QcVoiceAdManager;
import com.corpize.sdk.ivoice.bean.AppUserBean;
import com.corpize.sdk.ivoice.bean.UserBean;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.common.Constants;
import com.corpize.sdk.ivoice.common.ErrorUtil;
import com.corpize.sdk.ivoice.listener.AudioCustomQcAdListener;
import com.corpize.sdk.ivoice.listener.AudioQcAdListener;
import com.corpize.sdk.ivoice.listener.QCADListener;
import com.corpize.sdk.ivoice.listener.QcAutoRotationListener;
import com.corpize.sdk.ivoice.listener.QcCustomTemplateListener;
import com.corpize.sdk.ivoice.listener.QcFirstVoiceAdViewListener;
import com.corpize.sdk.ivoice.listener.QcRollAdViewListener;
import com.corpize.sdk.ivoice.listener.QcVoiceAdListener;
import com.corpize.sdk.ivoice.utils.DeviceUtil;
import com.corpize.sdk.ivoice.utils.GPSUtils;
import com.corpize.sdk.ivoice.utils.NetUtil;
import com.corpize.sdk.ivoice.utils.QCTransparentWebViewUtils;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;
import com.corpize.sdk.ivoice.utils.SensorUtils;
import com.corpize.sdk.ivoice.utils.SpUtils;
import com.corpize.sdk.ivoice.utils.bluetooth.BluetoothUtils;
import com.corpize.sdk.ivoice.utils.headsetplug.HeadsetPlugUtils;
import com.corpize.sdk.ivoice.utils.lifecycle.ActivityLifecycleCallbacks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: yh
 * date: 2020-11-24 10:34
 * description: 对外的方法
 */
public class QCiVoiceSdk {

    private static QCiVoiceSdk sQcAd;
    private static Activity mAudioActivity;
    private static UserBean mUserBean;
    private static boolean mIsInit = false;
    private static int onCreate = 1;
    private static int onResume = 2;//前台状态
    private static int onPause = 3;//后台状态
    private static int onPageState = onResume;//当前页面的状态
    public Context context;

    private Map<Integer, AudioCustomAdManager> managerMap;

    private AudioAdManager audioAdManager;
    private AudioCustomAdManager customAdManager;
    private QcFirstVoiceAdManager qcFirstVoiceAdManager;
    private QcVoiceAdManager qcVoiceAdManager;
    private QcCustomTemplateManager qcCustomTemplateManager;
    private QcRollAdManager qcRollAdManager;
    private AutoRotationManager autoRotationManager;

    private static String oaid;
    private static final int SDK_VERSION = Build.VERSION_CODES.Q;
    private static String mid;//mid
    private static int dnt;         //是否允许广告追踪,0不允许,1允许
    private HeadsetPlugUtils headsetPlugUtils;
    private static final boolean IS_ENABLE_OFFLINE = false;//是否启用离线,默认不启用

    public String getMid() {
        return mid;
    }

    private boolean interactionFlag;//是否触发互动

    public int width, height, positionX, positionY;
    public double clickDownX, clickDownY, clickUpX, clickUpY;

    //单例模式
    public static QCiVoiceSdk get() {
        if (sQcAd == null) {
            sQcAd = new QCiVoiceSdk();
        }
        return sQcAd;
    }

    public static String getOaid() {
        return oaid;
    }

    public static void setOaid(String oaid) {
        QCiVoiceSdk.oaid = oaid;
    }

    private QCiVoiceSdk() {

    }

    public boolean isInteractionFlag() {
        return interactionFlag;
    }

    public void setInteractionFlag(boolean interactionFlag) {
        this.interactionFlag = interactionFlag;
    }

    /**
     * 初始化
     */
    public void init(Application application,
                     String oaid,
                     String mid,
                     int dnt) {
        mIsInit = true;
        QCiVoiceSdk.oaid = oaid;
        QCiVoiceSdk.mid = mid;
        QCiVoiceSdk.dnt = dnt;

        context = application.getApplicationContext();

        try {
            saveSharedPreferences();

            //获取蓝牙信息
            BluetoothUtils.getInstance().init(context);

            headsetPlugUtils = HeadsetPlugUtils.getInstance().Build(context);
            headsetPlugUtils.registerReceiver();

            //初始化GPRS，判断是否关闭定位权限（接口关闭）
            if (SpUtils.getBoolean(Constants.SP_PERMISSION_LOCATION, true)) {
                GPSUtils.getInstance(context).initLngAndLat();
            }

            //获取陀螺仪,并发送请求,开启定时任务
//            getSensorManagerInfo(context);//1.1.8新增上传附近蓝牙信息，扫描蓝牙需要时间，故把上报放在扫描后执行

            //检测前后台切换
            application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks());

            //初始化时获取一次电量
            DeviceUtil.getBattery(context);

            //局域网获取ip
            if (NetUtil.isWifiConnected(context)) {
                QCTransparentWebViewUtils.getInstance().loadWebView(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //保存常规信息数据
    private void saveSharedPreferences() {
        try {
            SpUtils.saveBoolean(Constants.SP_IS_DEBUG_TAG, Constants.IS_TEST);
            SpUtils.saveString(Constants.SP_SDK_VERSION_TAG, Constants.SDK_VERSION);
            SpUtils.saveInt(Constants.SP_SDK_DNT_TAG, dnt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     */
    public void getSensorManagerInfo(Context context) {
        SensorUtils.getSensorManagerInfo(context);
    }

    /**
     * 获取用户数据
     */
    public void setUserInfo(Map<String, String> userMap) {
        String userId = userMap.get("userId");
        String avatar = userMap.get("avatar");
        //本地保存
        mUserBean = new UserBean(userId, avatar);
    }

    /**
     * 获取用户信息
     */
    public UserBean getUserInfo() {
        return mUserBean;
    }

    /**
     * 在Activity的activity中调用
     */
    public void onResume() {
        onPageState = onResume;

        if (qcCustomTemplateManager != null) {
            qcCustomTemplateManager.onResume();
        }
    }

    /**
     * 在Activity的onPause中调用
     */
    public void onPause() {
        onPageState = onPause;
    }

    public void onDestroy() {
        try {
            mAudioActivity = null;

            if (managerMap != null) {
                managerMap.clear();
                managerMap = null;
            }

            GPSUtils.getInstance(context).removeListener();

            if (headsetPlugUtils != null) {
                headsetPlugUtils.unRegisterReceiver();
            }

            //注销蓝牙广播
            BluetoothUtils.getInstance().unRegisterBluetoothBroadcast();

            if (audioAdManager != null) {
                audioAdManager.destroy();
            }

            if (null != customAdManager) {
                customAdManager.destroy();
            }

            if (null != qcFirstVoiceAdManager) {
                qcFirstVoiceAdManager.destroy();
            }

            if (null != qcVoiceAdManager) {
                qcVoiceAdManager.destroy();
            }

            if (null != qcCustomTemplateManager) {
                qcCustomTemplateManager.destroy();
            }

            if (null != qcRollAdManager) {
                qcRollAdManager.destroy();
            }

            if (null != autoRotationManager) {
                autoRotationManager.destroy();
            }

            QCTransparentWebViewUtils.getInstance().remove();

            interactionFlag = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 广告点击后跳转到其它页面返回操作
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (managerMap != null && managerMap.get(requestCode) != null) {
            managerMap.get(requestCode).onActivityResult(requestCode, resultCode, data);
        }

        if (audioAdManager != null) {
            audioAdManager.onActivityResult(requestCode, resultCode, data);
        }

        if (customAdManager != null) {
            customAdManager.onActivityResult(requestCode, resultCode, data);
        }

        if (qcFirstVoiceAdManager != null) {
            qcFirstVoiceAdManager.onActivityResult(requestCode, resultCode, data);
        }

        if (qcVoiceAdManager != null) {
            qcVoiceAdManager.onActivityResult(requestCode, resultCode, data);
        }

        if (qcCustomTemplateManager != null) {
            qcCustomTemplateManager.onActivityResult(requestCode, resultCode, data);
        }

        if (qcRollAdManager != null) {
            qcRollAdManager.onActivityResult(requestCode, resultCode, data);
        }

        if (autoRotationManager != null) {
            autoRotationManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 获取当前页面状态
     */
    public int getPageState() {
        return onPageState;
    }

    /**
     * 是否处于页面后台
     */
    public boolean isBackground() {
        return onPageState == onPause;
    }

    /**
     * 初始化,传递activity
     */
    public QCiVoiceSdk createAdNative(Activity activity) {
        mAudioActivity = activity;
        return sQcAd;
    }

    /**
     * 加载音频广告
     */
    public AudioAdManager addAudioAd(final AdAttr adAttr, final AudioQcAdListener listener) {
        if (mIsInit) {
            if (null == adAttr || !commonManagerCheck(mAudioActivity, adAttr.getAdid(), listener)) {
                return null;
            }

            audioAdManager = AudioAdManager.get();
            audioAdManager.showQcAd(
                    mAudioActivity,
                    adAttr.getAdid(),
                    adAttr.getLabel(),
                    listener
            );
            return audioAdManager;
        } else {
            showErrorListener(mAudioActivity, ErrorUtil.NOINIT, listener);
            return null;
        }
    }

    /**
     * 自定义音频广告素材
     */
    public QcAdManager addCustomAudioAd(final AdAttr adAttr, final AudioCustomQcAdListener listener) {
        return addCustomAudioAd(0, adAttr, listener);
    }

    public QcAdManager addCustomAudioAd(final int position,
                                        final AdAttr adAttr,
                                        final AudioCustomQcAdListener listener) {
        if (mIsInit) {
            if (null == adAttr || !commonManagerCheck(mAudioActivity, adAttr.getAdid(), listener)) {
                return null;
            }

            customAdManager = new AudioCustomAdManager();
            customAdManager.showQcAd(position, mAudioActivity, adAttr, listener);
            if (managerMap == null) {
                managerMap = new HashMap<>();
            }
            managerMap.put(position, customAdManager);
            return customAdManager;
        } else {
            showErrorListener(mAudioActivity, ErrorUtil.NOINIT, listener);
            return null;
        }
    }

    /**
     * 展示错误的回调
     *
     * @param activity
     * @param errorMsg
     */
    private void showErrorListener(Activity activity,
                                   final String errorMsg,
                                   final QCADListener listener) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onAdError(errorMsg);
                }
            }
        });
    }

    /**
     * 加载首听
     */
    public QcFirstVoiceAdManager addFirstVoiceAd(
            Activity activity,
            String adId,
            List<UserPlayInfoBean> labels,
            QcFirstVoiceAdViewListener listener) {

        mAudioActivity = activity;
        if (!commonManagerCheck(mAudioActivity, adId, listener)) {
            return null;
        }

        if (null == qcFirstVoiceAdManager) {
            qcFirstVoiceAdManager = QcFirstVoiceAdManager.getInstance();
        }

        qcFirstVoiceAdManager.getAudioAd(
                activity,
                adId,
                labels,
                listener
        );
        return qcFirstVoiceAdManager;
    }

    public QcFirstVoiceAdManager addFirstVoiceOfflineAd(
            Activity activity,
            String adId,
            List<UserPlayInfoBean> labels,
            AdAudioBean adAudioBean,
            QcFirstVoiceAdViewListener listener) {

        mAudioActivity = activity;
        if (!commonManagerCheck(mAudioActivity, adId, listener)) {
            return null;
        }

        if (!IS_ENABLE_OFFLINE) {
            return null;
        }

        if (null == qcFirstVoiceAdManager) {
            qcFirstVoiceAdManager = QcFirstVoiceAdManager.getInstance();
        }

        qcFirstVoiceAdManager.getOfflineAudioAd(
                activity,
                adId,
                labels,
                adAudioBean,
                listener
        );
        return qcFirstVoiceAdManager;
    }

    /**
     * 添加音频无界面
     */
    public QcVoiceAdManager addVoiceAd(
            Activity activity,
            String adId,
            List<UserPlayInfoBean> labels,
            QcVoiceAdListener listener) {
        return addVoiceAd(
                activity,
                adId,
                0,
                labels,
                listener
        );
    }

    /**
     * 添加音频无界面
     */
    public QcVoiceAdManager addVoiceAd(
            Activity activity,
            String adId,
            int delayTimer,
            List<UserPlayInfoBean> labels,
            QcVoiceAdListener listener) {

        mAudioActivity = activity;
        if (!commonManagerCheck(mAudioActivity, adId, listener)) {
            return null;
        }

        if (null == qcVoiceAdManager) {
            qcVoiceAdManager = QcVoiceAdManager.getInstance();
        }
        qcVoiceAdManager.getAudioAd(
                activity,
                adId,
                delayTimer,
                labels,
                listener);
        return qcVoiceAdManager;
    }

    public QcCustomTemplateManager addCustomTemplateAd(
            Activity activity,
            QcCustomTemplateAttr attr,
            String adId,
            List<UserPlayInfoBean> labels,
            QcCustomTemplateListener listener
    ) {
        mAudioActivity = activity;
        if (!commonManagerCheck(mAudioActivity, adId, listener)) {
            return null;
        }

        if (null == qcCustomTemplateManager) {
            qcCustomTemplateManager = QcCustomTemplateManager.getInstance();
        }
        qcCustomTemplateManager.getAudio(
                activity,
                attr,
                adId,
                labels,
                listener
        );
        return qcCustomTemplateManager;
    }

    public QcCustomTemplateManager addCustomTemplateAd(
            Activity activity,
            QcCustomTemplateAttr attr,
            String adId,
            List<UserPlayInfoBean> labels,
            final Integer provider,
            final int progress,
            QcCustomTemplateListener listener
    ) {
        mAudioActivity = activity;
        if (!commonManagerCheck(mAudioActivity, adId, listener)) {
            return null;
        }

        if (null == qcCustomTemplateManager) {
            qcCustomTemplateManager = QcCustomTemplateManager.getInstance();
        }
        qcCustomTemplateManager.getAudio(
                activity,
                attr,
                adId,
                labels,
                listener,
                provider,
                progress
        );
        return qcCustomTemplateManager;
    }

    public QcCustomTemplateManager addCustomTemplateOfflineAd(
            Activity activity,
            QcCustomTemplateAttr attr,
            String adId,
            List<UserPlayInfoBean> labels,
            AdAudioBean adAudioBean,
            final Integer provider,
            final int progress,
            QcCustomTemplateListener listener
    ) {
        mAudioActivity = activity;
        if (!commonManagerCheck(mAudioActivity, adId, listener)) {
            return null;
        }

        if (!IS_ENABLE_OFFLINE) {
            return null;
        }

        if (null == qcCustomTemplateManager) {
            qcCustomTemplateManager = QcCustomTemplateManager.getInstance();
        }

        qcCustomTemplateManager.getOfflineAudio(
                activity,
                attr,
                adId,
                labels,
                adAudioBean,
                provider,
                progress,
                listener
        );
        return qcCustomTemplateManager;
    }

    public QcCustomTemplateManager addCustomTemplateOfflineAd(
            Activity activity,
            QcCustomTemplateAttr attr,
            String adId,
            List<UserPlayInfoBean> labels,
            AdAudioBean adAudioBean,
            QcCustomTemplateListener listener
    ) {
        mAudioActivity = activity;
        if (!commonManagerCheck(mAudioActivity, adId, listener)) {
            return null;
        }

        if (!IS_ENABLE_OFFLINE) {
            return null;
        }

        if (null == qcCustomTemplateManager) {
            qcCustomTemplateManager = QcCustomTemplateManager.getInstance();
        }

        qcCustomTemplateManager.getOfflineAudio(
                activity,
                attr,
                adId,
                labels,
                adAudioBean,
                listener
        );
        return qcCustomTemplateManager;
    }

    public QcRollAdManager addRollAd(
            Activity activity,
            String adId,
            AdRollAttr adRollAttr,
            QcRollAdViewListener listener) {

        if (!commonManagerCheck(activity, adId, listener)) {
            return null;
        }

        if (null == qcRollAdManager) {
            qcRollAdManager = QcRollAdManager.getInstance();
        }

        qcRollAdManager.getAudioAd(activity, adId, adRollAttr, listener);
        return qcRollAdManager;
    }

    public AutoRotationManager addAutoRotationAd(
            Activity activity,
            String adId,
            final List<UserPlayInfoBean> labels,
            QcAutoRotationListener listener
    ) {
        if (!commonManagerCheck(activity, adId, listener)) {
            return null;
        }

        if (null == audioAdManager) {
            autoRotationManager = AutoRotationManager.getInstance();
        }

        autoRotationManager.getAudio(activity, adId, labels, listener, null);
        return autoRotationManager;
    }

    private boolean commonManagerCheck(Activity activity, String adId, QCADListener listener) {
        if (null == activity) {
            if (listener != null) {
                listener.onAdError(ErrorUtil.NO_ACTIVITY);
            }
            return false;
        }

        if (TextUtils.isEmpty(oaid)
                && Build.VERSION.SDK_INT >= SDK_VERSION) {
            if (listener != null) {
                listener.onAdError(ErrorUtil.NO_OAID);
            }
            return false;
        }

        if (TextUtils.isEmpty(adId)) {
            if (listener != null) {
                listener.onAdError(ErrorUtil.NOADID);
            }
            return false;
        }

        return true;
    }

    //对外暴露曝光接口
    public void sendAdExposure(String url) {
        if (!IS_ENABLE_OFFLINE) {
            return;
        }

        QcHttpUtil.sendAdExposure(url);
    }

    //对外暴露设置定位权限接口
    public void setLocationPermission(boolean isLocationPermission) {
        SpUtils.saveBoolean(Constants.SP_PERMISSION_LOCATION, isLocationPermission);
        //如果进入应用后才开启定位权限，需要初始化定位
        if (isLocationPermission && AppUserBean.getInstance().getLat() == 0) {
            GPSUtils.getInstance(context).initLngAndLat();
        }
    }

    //对外暴露设置麦克风权限接口
    public void setMicrophonePermission(boolean isMicrophonePermission) {
        SpUtils.saveBoolean(Constants.SP_PERMISSION_MICROPHONE, isMicrophonePermission);
    }

    //对外暴露设置设备号权限接口
    public void setDevicePermission(boolean isDevicePermission) {
        SpUtils.saveBoolean(Constants.SP_PERMISSION_DEVICE, isDevicePermission);
    }

    public boolean isDebug() {
        return SpUtils.getBoolean(Constants.SP_IS_DEBUG_TAG);
    }

    public String getSdkVersion() {
        return SpUtils.getString(Constants.SP_SDK_VERSION_TAG);
    }
}
