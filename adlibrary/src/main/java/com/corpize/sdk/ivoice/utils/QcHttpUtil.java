package com.corpize.sdk.ivoice.utils;

import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.bean.AdBaseBean;
import com.corpize.sdk.ivoice.bean.AdCommentBean;
import com.corpize.sdk.ivoice.bean.AdResponseBean;
import com.corpize.sdk.ivoice.bean.AdSensorBean;
import com.corpize.sdk.ivoice.bean.AdidBean;
import com.corpize.sdk.ivoice.bean.AppUserBean;
import com.corpize.sdk.ivoice.bean.UpVoiceResultBean;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.common.Constants;
import com.corpize.sdk.ivoice.http.MyHttpUtils;
import com.corpize.sdk.ivoice.http.callback.JsonCallback;
import com.corpize.sdk.ivoice.http.callback.JsonSerializator;
import com.corpize.sdk.ivoice.http.callback.StringCallback;
import com.corpize.sdk.ivoice.utils.bluetooth.BluetoothUtils;
import com.corpize.sdk.ivoice.utils.headsetplug.HeadsetPlugUtils;
import com.corpize.sdk.ivoice.utils.wifi.WifiUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * author ：yh
 * date : 2019-08-02 14:44
 * description : 网络编辑类
 */
public class QcHttpUtil {

    private static String mBaseUrl         = Constants.getBaseUrl();           //基础的url
    private static String mSensorBaseUrl   = Constants.getSensorBaseUrl();     //基础的url
    private static String mComentBaseUrl   = Constants.getComentBaseUrl();     //基础的url
    private static String mAdidUrl         = mBaseUrl + "sdk";                 //通过adid获取广告配置
    private static String mGetAdUrl        = mBaseUrl + "ssp/corpize";         //获取广告详情
    private static String mGetAudioAdUrl   = mBaseUrl + "ssp/ivoice/sdk";      //获取音频广告
    private static String mUpComentUrl     = mComentBaseUrl + "comment/up";    //上传评论
    private static String mDownComentUrl   = mComentBaseUrl + "comment/down";  //下载评论
    private static String mUpSensorInfoUrl = mSensorBaseUrl + "inspect";       //上传陀螺仪地址
    private static String mUpVoiceFileUrl  = mBaseUrl + "asr/judge";           //上传音频文件

    //**************************************  下方是网络请求  **************************************//
    //**************************************  下方是网络请求  **************************************//
    //**************************************  下方是网络请求  **************************************//

    /**
     * 获取广告位信息
     */
    public static void getAdid (String adId, final QcHttpOnListener qcHttpOnListener) {
        if (TextUtils.isEmpty(adId)) {
            Log.e("IVOICE", "ADID == null");
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("adid", adId);
        String bean = GsonUtil.GsonString(map);
        MyHttpUtils.postAsyn(mAdidUrl)
                .content(bean)
                .execute(new JsonCallback<AdidBean>(new JsonSerializator()) {
                    @Override
                    public void onResponse (AdidBean response) {
                        if (qcHttpOnListener != null) {
                            qcHttpOnListener.OnQcCompletionListener(response);
                        }
                    }

                    @Override
                    public void onError (int code, Exception e) {
                        if (qcHttpOnListener != null) {
                            qcHttpOnListener.OnQcErrorListener(e.getMessage(), code);
                        }
                    }
                });
    }

    /**
     * 获取正式的广告
     */
    public static void getAd (Context context, AdidBean adsSdk, String adId, final QcHttpOnListener qcHttpOnListener) {
        if (TextUtils.isEmpty(adId)) {
            Log.e("IVOICE", "ADID == null");
            return;
        }

        if (adsSdk != null) {
            String bean = setBaseBean(context, adsSdk, 1, adId, false);
            MyHttpUtils.postAsyn(mGetAdUrl)
                    .content(bean)
                    .execute(new JsonCallback<AdResponseBean>(new JsonSerializator()) {
                        @Override
                        public void onResponse (AdResponseBean response) {
                            if (qcHttpOnListener != null) {
                                qcHttpOnListener.OnQcCompletionListener(response);
                            }
                        }

                        @Override
                        public void onError (int code, Exception e) {
                            if (qcHttpOnListener != null) {
                                qcHttpOnListener.OnQcErrorListener(e.getMessage(), code);
                            }
                        }
                    });
        }
    }

    public static void getAudioAd (
            Context context,
            String adId,
            int style,
            List<UserPlayInfoBean> label,
            final QcHttpOnListener qcHttpOnListener) {
        getAudioAd(context, adId, style, label, null, qcHttpOnListener);
    }

    /**
     * 获取正式的音频广告
     * label:有就带上传最近播放音频信息
     */
    public static void getAudioAd (
            Context context,
            String adId,
            int style,
            List<UserPlayInfoBean> label,
            Integer provider,
            final QcHttpOnListener qcHttpOnListener) {
        getAudioAd(context, adId, style, label, provider,4, qcHttpOnListener);
    }

    public static void getAudioAd (
            Context context,
            String adId,
            int style,
            List<UserPlayInfoBean> label,
            Integer provider,
            int deviceType,
            final QcHttpOnListener qcHttpOnListener) {
        if (TextUtils.isEmpty(adId)) {
            Log.e("IVOICE", "ADID == null");
            return;
        }

        String bean = setBaseBean(context, null, style, adId, label, true, provider,deviceType);
        MyHttpUtils.postAsyn(mGetAudioAdUrl)
                .content(bean)
                .encrypts()
                .execute(new JsonCallback<AdResponseBean>(new JsonSerializator()) {
                    @Override
                    public void onResponse (AdResponseBean response) {
                        if (qcHttpOnListener != null) {
                            qcHttpOnListener.OnQcCompletionListener(response);
                        }
                    }

                    @Override
                    public void onError (int code, Exception e) {
                        if (qcHttpOnListener != null) {
                            qcHttpOnListener.OnQcErrorListener(e.getMessage(), code);
                        }
                    }
                });
    }

    /**
     * 上传点赞的接口
     *
     * @param mid
     * @param creativeId
     * @param qcHttpOnListener
     */
    public static void upPraise (String mid, String creativeId, final QcHttpOnListener qcHttpOnListener) {
        upCommentOrPraise(mid, creativeId, 0, "", "", "", true, qcHttpOnListener);
    }

    /**
     * 上传评论的接口
     *
     * @param mid
     * @param creativeId
     * @param qcHttpOnListener
     */
    public static void upComment (String mid, String creativeId, int time, String content, String avatar, String userid,
                                  final QcHttpOnListener qcHttpOnListener) {
        upCommentOrPraise(mid, creativeId, time, content, avatar, userid, false, qcHttpOnListener);
    }

    /**
     * 上传评论的接口,点赞的接口
     */
    public static void upCommentOrPraise (String mid, String creativeId, int time,
                                          String content, String avatar, String userid,
                                          boolean upvote,
                                          final QcHttpOnListener qcHttpOnListener) {
        Map<String, Object> map = new HashMap<>();
        map.put("ostype", "Android");//必传
        map.put("mid", mid);//必传
        map.put("creative_id", creativeId);//必传
        map.put("deviceid", DeviceUtil.getAndroidId());//必传
        map.put("imei", DeviceUtil.getIMEI());//必传
        map.put("oaid", QCiVoiceSdk.getOaid());//必传
        if (!upvote) {
            //发送评论
            map.put("time", time);
            map.put("content", Base64.getBase64(content));
            map.put("avatar", avatar);
            map.put("userid", userid);
        } else {
            //点赞
            map.put("upvote", upvote);//是否是点赞,点赞传true
        }

        String bean = GsonUtil.GsonString(map);
        MyHttpUtils.postAsyn(mUpComentUrl)
                .content(bean)
                .encrypts()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse (String response) {
                        if (qcHttpOnListener != null) {
                            qcHttpOnListener.OnQcCompletionListener(response);
                        }
                    }

                    @Override
                    public void onError (int code, Exception e) {
                        if (qcHttpOnListener != null) {
                            qcHttpOnListener.OnQcErrorListener(e.getMessage(), code);
                        }
                    }
                });
    }

    /**
     * 获取评论的接口
     */
    public static void getComment (String mid, String creativeId, int numbers, int timestart, int timeend,
                                   final QcHttpOnListener qcHttpOnListener) {
        Map<String, Object> map = new HashMap<>();
        map.put("ostype", "Android");//必传
        map.put("mid", mid);//必传
        map.put("creative_id", creativeId);//必传
        map.put("deviceid", DeviceUtil.getAndroidId());//必传
        map.put("imei", DeviceUtil.getIMEI());//必传
        map.put("oaid", QCiVoiceSdk.getOaid());//必传
        map.put("numbers", numbers);//弹幕的总行数
        map.put("timestart", timestart);//获取弹幕的开始时间
        map.put("timeend", timeend);//获取弹幕的结束时间

        String bean = GsonUtil.GsonString(map);
        MyHttpUtils.postAsyn(mDownComentUrl)
                .content(bean)
                .encrypts()
                .execute(new JsonCallback<AdCommentBean>(new JsonSerializator()) {
                    @Override
                    public void onResponse (AdCommentBean response) {
                        if (qcHttpOnListener != null) {
                            qcHttpOnListener.OnQcCompletionListener(response);
                        }
                    }

                    @Override
                    public void onError (int code, Exception e) {
                        if (qcHttpOnListener != null) {
                            qcHttpOnListener.OnQcErrorListener(e.getMessage(), code);
                        }
                    }
                });
    }

    /**
     * 上传海拔,陀螺仪信息接口
     */
    public static void upSensorInfo (Context context, float x, float y, float z,
                                     final QcHttpOnListener qcHttpOnListener) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("x", x);// x
            map.put("y", y);// y
            map.put("z", z);// z

            //判断是否关闭设备号权限（接口关闭）
            if (SpUtils.getBoolean(Constants.SP_PERMISSION_DEVICE, true)) {
                map.put("androidID", DeviceUtil.getAndroidId());//必传
                map.put("imei", DeviceUtil.getIMEI());//必传
                map.put("oaid", QCiVoiceSdk.getOaid());//必传
            }
            //判断是否关闭定位权限（接口关闭）
            if (SpUtils.getBoolean(Constants.SP_PERMISSION_LOCATION, true)) {
                AppUserBean instance = AppUserBean.getInstance();
                double lat = instance.getLat();
                double lon = instance.getLon();
                double altitude = instance.getAltitude();
                map.put("altitude", altitude);// 海拔 }
                map.put("lat", lat == 0 ? null : lat);// 纬度
                map.put("lon", lon == 0 ? null : lon);// 经度
            }
            try {
                map.put("connectiontype", NetUtil.getNetworkState(context));// 网络连接方式
            } catch (Exception e) {
                e.printStackTrace();
                map.put("connectiontype", NetUtil.NETWORK_MOBILE);// 网络连接方式
            }
            map.put("carrier", DeviceUtil.getOperatorName());//运营商代码
            map.put("make", DeviceUtil.getManufacturer());//制造商
            map.put("model", DeviceUtil.getPhoneModel());//型号
            map.put("os", Constants.ANDROID_OS_TYPE);                                   //系统类型
            map.put("osv", DeviceUtil.getSdkVersion());                                 //系统版本
            map.put("bundle", DeviceUtil.getPackageName());                             //包名
            map.put("mid", QCiVoiceSdk.get().getMid());//媒体id
            map.put("battery", DeviceUtil.fetchBattery());                              //电量
            map.put("cpuModel", TextUtils.isEmpty(DeviceUtil.getCpuName()) || "0".equals(DeviceUtil.getCpuName())
                    ? null : DeviceUtil.getCpuName());//cpu型号,0、空、""，时不传该字段
            map.put("architecture", DeviceUtil.getSupportFramework());                  //cpu架构
            map.put("coreFrequency", TextUtils.isEmpty(DeviceUtil.getMaxCpuFreq()) ? null : DeviceUtil.getMaxCpuFreq());//核心频率coreFrequency
            map.put("ram", DeviceUtil.getTotalRam());                                   //ram
            map.put("rom", String.valueOf(DeviceUtil.getRomMemroy()[0]));               //rom
            map.put("hostname", DeviceUtil.getDeviceName(context));   //设备名称
//            map.put("wifiConn", TextUtils.isEmpty(DeviceUtil.getMyWifiName(QCiVoiceSdk.get().context))
//                    ? null : DeviceUtil.getMyWifiName(QCiVoiceSdk.get().context));   //当前连接热点
//            map.put("wifiAround", TextUtils.isEmpty(DeviceUtil.getMyWifiList(QCiVoiceSdk.get().context))
//                    ? null : DeviceUtil.getMyWifiList(QCiVoiceSdk.get().context));   //附近热点
//            map.put("bluetoothPair", TextUtils.isEmpty(BluetoothUtils.getInstance().fetchBluetoothDevices())
//                    ? null : BluetoothUtils.getInstance().fetchBluetoothDevices()); //配对的蓝牙设备名称
//            map.put("localBluetoothName", TextUtils.isEmpty(BluetoothUtils.getInstance().getLocalBluetoothName())
//                    ? null : BluetoothUtils.getInstance().getLocalBluetoothName()); //本机蓝牙名字
//            map.put("localBluetoothAddress", TextUtils.isEmpty(BluetoothUtils.getInstance().getLocalBluetoothAddress())
//                    ? null : BluetoothUtils.getInstance().getLocalBluetoothAddress());//本机蓝牙地址
            map.put("bluetooth", BluetoothUtils.getInstance().getBluetoothInfo());   //蓝牙信息
            map.put("wifi", WifiUtil.getInstance().getWifiInfo(context));   //wifi信息

            String bean = GsonUtil.GsonString(map);
            MyHttpUtils.postAsyn(mUpSensorInfoUrl)
                    .content(bean)
                    .encrypts()
                    .execute(new JsonCallback<AdSensorBean>(new JsonSerializator()) {
                        @Override
                        public void onResponse (AdSensorBean response) {
                            if (qcHttpOnListener != null) {
                                qcHttpOnListener.OnQcCompletionListener(response);
                            }
                        }

                        @Override
                        public void onError (int code, Exception e) {
                            if (qcHttpOnListener != null) {
                                qcHttpOnListener.OnQcErrorListener(e.getMessage(), code);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传音频文件
     */
    public static void upVoiceFile (String filePath, final QcHttpOnListener qcHttpOnListener) {
        Map<String, Object> map = new HashMap<>();
        //文件的byte数组
        byte[] dataByte = FileUtils.fileToByte(new File(filePath));
        //安卓原生base64方法
        String dataBase64Str = android.util.Base64.encodeToString(dataByte, android.util.Base64.DEFAULT);
        // 音频文件
        map.put("audio", dataBase64Str);
        map.put("id", UUID.randomUUID().toString().replace("-", ""));//32位uuid
        map.put("ts", System.currentTimeMillis());//时间戳，毫秒
        map.put("deviceid", DeviceUtil.getAndroidId());//必传
        map.put("imei", DeviceUtil.getIMEI());//必传
        map.put("oaid", QCiVoiceSdk.getOaid());//必传

        String bean = GsonUtil.GsonString(map);
        MyHttpUtils.postAsyn(mUpVoiceFileUrl)
                .content(bean)
                .encrypts()
                .execute(new JsonCallback<UpVoiceResultBean>(new JsonSerializator()) {
                    @Override
                    public void onResponse (UpVoiceResultBean response) {
                        if (qcHttpOnListener != null) {
                            qcHttpOnListener.OnQcCompletionListener(response);
                        }
                    }

                    @Override
                    public void onError (int code, Exception e) {
                        if (qcHttpOnListener != null) {
                            qcHttpOnListener.OnQcErrorListener(e.getMessage(), code);
                        }
                    }
                });
    }

    /**
     * 广告曝光请求 (企创广告) 检测曝光
     */
    public static void sendAdExposure (List<String> exposureUrlList) {
        if (exposureUrlList != null && exposureUrlList.size() > 0) {
            for (String url : exposureUrlList) {
                MyHttpUtils.getAsyn(url).execute(null);
            }
        }
    }

    /**
     * 广告曝光请求 (企创广告) 检测曝光
     */
    public static void sendAdExposure (String exposureUrl) {
        MyHttpUtils.getAsyn(exposureUrl).execute(null);
    }

    /**
     * 广告点击请求 (企创广告) 检测点击广告
     */
    public static void sendAdClick (List<String> clickUrlList) {
        if (clickUrlList != null) {
            for (String url : clickUrlList) {
                MyHttpUtils.getAsyn(url).execute(null);
            }
        }
    }

    private static String setBaseBean (
            Context context,
            AdidBean adsSdk,
            int style,
            String adId,
            List<UserPlayInfoBean> label,
            boolean isAudio) {
        return setBaseBean(context, adsSdk, style, adId, label, isAudio, null, 4);
    }

    /**
     * 组装广告基础参数
     */
    private static String setBaseBean (
            Context context,
            AdidBean adsSdk,
            int style,
            String adId,
            List<UserPlayInfoBean> label,
            boolean isAudio,
            Integer provider,
            int deviceType) {

        //初始化数据
        AdBaseBean adUpBean = new AdBaseBean();

        adUpBean.setAdid(adId);                                 //adid
        adUpBean.setLabel(label);                               //label
        adUpBean.setBundle(DeviceUtil.getPackageName());        //开发包名
        adUpBean.setAppname(DeviceUtil.getAppName());           //App名称
        adUpBean.setStyle(style);                               //设置类型
        adUpBean.setProvider(provider);
        adUpBean.setDevicetype(deviceType);
        return getAdUpBeanString(context, adUpBean);
    }

    private static String getAdUpBeanString(Context context, AdBaseBean adUpBean) {
        try {
            if (SpUtils.getBoolean(Constants.SP_PERMISSION_DEVICE, true)) {
                adUpBean.setOaid(QCiVoiceSdk.getOaid());          //正对于AndroidQ需要获取的oaid
                adUpBean.setAndroidid(DeviceUtil.getAndroidId());     //手机的ID明文
                adUpBean.setMac(DeviceUtil.getMAC());                  //设备mac地址
                adUpBean.setImei(DeviceUtil.getIMEI());//手机的imei
            }
            //判断是否关闭定位权限（接口关闭）
            if (SpUtils.getBoolean(Constants.SP_PERMISSION_LOCATION, true)) {
                AppUserBean instance = AppUserBean.getInstance();
                double lat = instance.getLat();
                double lon = instance.getLon();
                adUpBean.setLat(lat == 0 ? "" : String.valueOf(lat)); //设置经度
                adUpBean.setLon(lon == 0 ? "" : String.valueOf(lon)); //设置纬度
            }
            //设置本地的参数(必填)
            adUpBean.setUa(DeviceUtil.getUserAgent(context));       //本地的ua的信息
            adUpBean.setVer(DeviceUtil.getVersionName());           //App的版本
            adUpBean.setSdkver(Constants.getSdkVer());              //SDK的版本号
            adUpBean.setOsv(DeviceUtil.getSdkVersion());            //设备操作系统版本
            adUpBean.setMake(DeviceUtil.getManufacturer());         //设备品牌
            adUpBean.setModel(DeviceUtil.getPhoneModel());          //设备型号
            adUpBean.setLanguage(DeviceUtil.getLanguage());         //设备语言
            adUpBean.setDensity(DeviceUtil.getDeviceDensity());     //屏幕密度
            adUpBean.setSw(DeviceUtil.getDeviceWidth());            //设备屏幕分辨率宽度
            adUpBean.setSh(DeviceUtil.getDeviceHeight());          //设备屏幕分辨率高度
            adUpBean.setIp(NetUtil.getIpAddress());                 //ip地址
            adUpBean.setVolume(getCurrentVolum(context));           //设置音量
            adUpBean.setReception(QCiVoiceSdk.get().isBackground() ? 0 : 1);//是否在前台,0不是,1是前台

            //设置本地的参数(选填)
            if (!TextUtils.isEmpty(DeviceUtil.getOperatorName())) {
                adUpBean.setCarrier(Integer.parseInt(Objects.requireNonNull(DeviceUtil.getOperatorName())));      //运营商类型
            }

            try {
                adUpBean.setConnectiontype(NetUtil.getNetworkState(context));  //设备网络类型
            } catch (Exception e) {
                e.printStackTrace();
                adUpBean.setConnectiontype(NetUtil.NETWORK_MOBILE);  //设备网络类型
            }
            //0: 未知；1: 手机外放；2: 有线连接播放设备；3: 无线连接播放设备
            int soundType = HeadsetPlugUtils.getInstance().getPlugType();
            adUpBean.setSoundtype(soundType);

            int dnf = SpUtils.getInt(Constants.SP_SDK_DNT_TAG);
            adUpBean.setDnt(dnf);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return GsonUtil.GsonString(adUpBean);
    }


    /**
     * 组装广告基础参数
     */
    private static String setBaseBean (Context context, AdidBean adsSdk, int style, String adId, boolean isAudio) {
        //初始化数据
        AdBaseBean adUpBean = new AdBaseBean();

        adUpBean.setAdid(adId);                                 //adid
        adUpBean.setBundle(DeviceUtil.getPackageName());        //开发包名
        adUpBean.setAppname(DeviceUtil.getAppName());           //App名称
        adUpBean.setStyle(style);                               //设置类型

        //设置本地的参数(必填)
        return getAdUpBeanString(context, adUpBean);
    }

    /**
     * @param context
     */
    public static int getCurrentVolum (Context context) {
        AudioManager mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int          currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int          maxVolume     = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int          volum         = currentVolume * 100 / maxVolume;
        return volum;
    }

    //接口回调
    public interface QcHttpOnListener<T> {
        void OnQcCompletionListener (T response);

        void OnQcErrorListener (String error, int code);
    }
}
