package com.corpize.sdk.ivoice.bean;

import java.util.List;

/**
 * author ：yh
 * date : 2019-08-07 17:02
 */
public class AdBaseBean {

    //非必填(下发)
    private int    version;     //协议版本号
    private String adid;        //广告位ID
    private String storeurl;    //APP应用市场下载链接
    private int    pos;         //广告位在屏幕的第N屏
    private int    width;       //广告宽度
    private int    height;      //广告高度

    //必填(本地)
    private String ver;         //App的版本 7.0.1
    private String sdkver;      //Sdk的版本 7.0.1
    private String bundle;      //开发包名
    private String appname;     //App名称
    private String osv;         //设备操作系统版本
    private String make;        //设备制造商
    private String model;       //设备型号
    private String language;    //设备语言
    private String imei;        //安卓设备IMEI明文值
    private String oaid;        //安卓设备oaid值,移动联盟应对AndroidQ的广告值
    private String androidid;   //安卓设备Android ID明文值
    private int    sw;          //设备屏幕分辨率宽度，单位为像素
    private int    sh;          //设备屏幕分辨率高度，单位为像素
    private int    adtype;       //广告类型,音频301
    private String ua          = "Mozilla/5.0 (Linux; Android 7.0; Nexus 5X Build/NRD90M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/54.0.2840.85 Mobile Safari/537.36";      //APP端User Agent信息
    private String ip          = "0.0.0.0"; //用户端IP地址
    private int    dnt         = 0;         //是否允许广告追踪,0不允许,1允许
    private int    orientation = 0;         //设备屏幕方向,0竖1横

    private int    devicetype = 4;         //设备类型,手机4
    private String os         = "Android"; //操作系统
    private int    js         = 0;         //APP是否支持javascript脚本,1支持,0不支持

    //选填
    private int    carrier;          //运营商类型
    private int    connectiontype;   //设备网络类型
    private int    density;          //设备屏幕密度（像素）
    private String mac;              //设备mac地址
    private String macsha1;          //设备mac地址的SH1值(不传递)
    private String mmacmd5ac;        //设备mac地址MD5值(不传递)
    private String lat;              //设备当前所在的纬度（GPS）
    private String lon;              //设备当前所在的经度（GPS）
    //    private int    ishttps   = 0;    //是否强制HTTPS协议
    private int    style     = 1;    //1，自定义，2，弹窗
    private int    volume    = 0;    //当前系统音量
    private int    reception = 0;    //当前应用是否处在前台
    private String mid;              //媒体id
    private String hostname;         //机主名称。张XX的iphone
    private String osName;           //例如小米的miui
    private String osVersion;        //miui版本
    private String cpuModel;         //cpu型号
    private String architecture;     //处理器架构
    private String coreFrequency;    //核心频率
    private String memory;           //内存
    private String ram;              //ram
    private String rom;              //rom
    private String baseBand;         //基带
    private String battery;          //电池

    private int soundtype;//1:耳机，2:外放
    private Integer provider;

    public Integer getProvider () {
        return provider;
    }

    public void setProvider (Integer provider) {
        this.provider = provider;
    }

    public int getSoundtype () {
        return soundtype;
    }

    public void setSoundtype (int soundtype) {
        this.soundtype = soundtype;
    }


    private List<UserPlayInfoBean> label;//播放广告前播放的音频内容


    private UserBean  user;             //用户信息对象
    private VoiceBean voice;            //音频广告信息

    public int getVersion () {
        return version;
    }

    public void setVersion (int version) {
        this.version = version;
    }

    public int getDnt () {
        return dnt;
    }

    public void setDnt (int dnt) {
        this.dnt = dnt;
    }

    public String getAdid () {
        return adid;
    }

    public void setAdid (String adid) {
        this.adid = adid;
    }

    public String getVer () {
        return ver;
    }

    public void setVer (String ver) {
        this.ver = ver;
    }

    public String getSdkver () {
        return sdkver;
    }

    public void setSdkver (String sdkver) {
        this.sdkver = sdkver;
    }

    public String getStoreurl () {
        return storeurl;
    }

    public void setStoreurl (String storeurl) {
        this.storeurl = storeurl;
    }

    public int getPos () {
        return pos;
    }

    public void setPos (int pos) {
        this.pos = pos;
    }

    public int getWidth () {
        return width;
    }

    public void setWidth (int width) {
        this.width = width;
    }

    public int getHeight () {
        return height;
    }

    public void setHeight (int height) {
        this.height = height;
    }

    public String getBundle () {
        return bundle;
    }

    public void setBundle (String bundle) {
        this.bundle = bundle;
    }

    public String getAppname () {
        return appname;
    }

    public void setAppname (String appname) {
        this.appname = appname;
    }

    public String getUa () {
        return ua;
    }

    public void setUa (String ua) {
        this.ua = ua;
    }

    public int getAdtype () {
        return adtype;
    }

    public void setAdtype (int adtype) {
        this.adtype = adtype;
    }

    public int getDevicetype () {
        return devicetype;
    }

    public void setDevicetype (int devicetype) {
        this.devicetype = devicetype;
    }

    public String getOs () {
        return os;
    }

    public void setOs (String os) {
        this.os = os;
    }

    public String getIp () {
        return ip;
    }

    public void setIp (String ip) {
        this.ip = ip;
    }

    public int getJs () {
        return js;
    }

    public void setJs (int js) {
        this.js = js;
    }

    public String getOsv () {
        return osv;
    }

    public void setOsv (String osv) {
        this.osv = osv;
    }

    public String getMake () {
        return make;
    }

    public void setMake (String make) {
        this.make = make;
    }

    public String getModel () {
        return model;
    }

    public void setModel (String model) {
        this.model = model;
    }

    public String getLanguage () {
        return language;
    }

    public void setLanguage (String language) {
        this.language = language;
    }

    public String getImei () {
        return imei;
    }

    public void setImei (String imei) {
        this.imei = imei;
    }

    public String getOaid () {
        return oaid;
    }

    public void setOaid (String oaid) {
        this.oaid = oaid;
    }

    public String getAndroidid () {
        return androidid;
    }

    public void setAndroidid (String androidid) {
        this.androidid = androidid;
    }

    public int getOrientation () {
        return orientation;
    }

    public void setOrientation (int orientation) {
        this.orientation = orientation;
    }

    public int getSw () {
        return sw;
    }

    public void setSw (int sw) {
        this.sw = sw;
    }

    public int getSh () {
        return sh;
    }

    public void setSh (int sh) {
        this.sh = sh;
    }

    public int getCarrier () {
        return carrier;
    }

    public void setCarrier (int carrier) {
        this.carrier = carrier;
    }

    public int getConnectiontype () {
        return connectiontype;
    }

    public void setConnectiontype (int connectiontype) {
        this.connectiontype = connectiontype;
    }

    public int getDensity () {
        return density;
    }

    public void setDensity (int density) {
        this.density = density;
    }

    public String getMac () {
        return mac;
    }

    public void setMac (String mac) {
        this.mac = mac;
    }

    public String getMacsha1 () {
        return macsha1;
    }

    public void setMacsha1 (String macsha1) {
        this.macsha1 = macsha1;
    }

    public String getMmacmd5ac () {
        return mmacmd5ac;
    }

    public void setMmacmd5ac (String mmacmd5ac) {
        this.mmacmd5ac = mmacmd5ac;
    }

    public String getLat () {
        return lat;
    }

    public void setLat (String lat) {
        this.lat = lat;
    }

    public String getLon () {
        return lon;
    }

    public void setLon (String lon) {
        this.lon = lon;
    }

//    public int getIshttps () {
//        return ishttps;
//    }
//
//    public void setIshttps (int ishttps) {
//        this.ishttps = ishttps;
//    }

    public UserBean getUser () {
        return user;
    }

    public void setUser (UserBean user) {
        this.user = user;
    }

    public VoiceBean getVoice () {
        return voice;
    }

    public void setVoice (VoiceBean voice) {
        this.voice = voice;
    }

    public int getStyle () {
        return style;
    }

    public void setStyle (int style) {
        this.style = style;
    }

    public int getVolume () {
        return volume;
    }

    public void setVolume (int volume) {
        this.volume = volume;
    }

    public int getReception () {
        return reception;
    }

    public void setReception (int reception) {
        this.reception = reception;
    }

    public String getMid () {
        return mid;
    }

    public void setMid (String mid) {
        this.mid = mid;
    }

    public String getHostname () {
        return hostname;
    }

    public void setHostname (String hostname) {
        this.hostname = hostname;
    }

    public String getOsName () {
        return osName;
    }

    public void setOsName (String osName) {
        this.osName = osName;
    }

    public String getOsVersion () {
        return osVersion;
    }

    public void setOsVersion (String osVersion) {
        this.osVersion = osVersion;
    }

    public String getCpuModel () {
        return cpuModel;
    }

    public void setCpuModel (String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public String getArchitecture () {
        return architecture;
    }

    public void setArchitecture (String architecture) {
        this.architecture = architecture;
    }

    public String getCoreFrequency () {
        return coreFrequency;
    }

    public void setCoreFrequency (String coreFrequency) {
        this.coreFrequency = coreFrequency;
    }

    public String getMemory () {
        return memory;
    }

    public void setMemory (String memory) {
        this.memory = memory;
    }

    public String getRam () {
        return ram;
    }

    public void setRam (String ram) {
        this.ram = ram;
    }

    public String getRom () {
        return rom;
    }

    public void setRom (String rom) {
        this.rom = rom;
    }

    public String getBaseBand () {
        return baseBand;
    }

    public void setBaseBand (String baseBand) {
        this.baseBand = baseBand;
    }

    public String getBattery () {
        return battery;
    }

    public void setBattery (String battery) {
        this.battery = battery;
    }


    public List<UserPlayInfoBean> getLabel () {
        return label;
    }

    public void setLabel (List<UserPlayInfoBean> label) {
        this.label = label;
    }


}
