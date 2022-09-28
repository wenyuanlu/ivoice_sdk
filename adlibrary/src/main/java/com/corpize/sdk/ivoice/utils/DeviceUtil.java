package com.corpize.sdk.ivoice.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.LocaleList;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.corpize.sdk.ivoice.bean.WifiBean;
import com.corpize.sdk.ivoice.common.CommonUtils;
import com.corpize.sdk.ivoice.common.SystemConstants;
import com.corpize.sdk.ivoice.listener.SensorManagerCallback;
import com.qichuang.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.BATTERY_SERVICE;

import javax.crypto.Mac;

/**
 * author: yh
 * date: 2019-08-19 18:02
 * description: 获取设备的相关信息
 */
public class DeviceUtil {

    private static Application mApplication = CommonUtils.get();
    private static SensorEventListener sensorEventListener;

    public static final String BATTERY_CAPACITY = "battery_capacity";
    public static final String ANDROID_ID = "device_android_id";
    public static final String IMEI = "device_imei";
    public static final String MAC = "device_mac";
    public static final String USER_AGENT = "device_user_agent";
    public static final String LANGUAGE = "device_language";
    public static final String DENSITY = "device_density";
    public static final String WIDTH_PIXELS = "device_width_pixels";
    public static final String HEIGHT_PIXELS = "device_height_pixels";


    /**
     * 手机系统版本
     */
    public static Application getApplication() {
        if (mApplication == null) {
            mApplication = CommonUtils.get();
        }
        return mApplication;
    }

    /**
     * 手机系统版本
     */
    public static String getSdkVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 手机系统API level
     */
    public static int getSdkAPILevel() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 手机型号
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机语言
     */
    public static String getLanguage() {
        if (!TextUtils.isEmpty(SpUtils.getString(LANGUAGE))) {
            return SpUtils.getString(LANGUAGE);
        }
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage() + "-" + locale.getCountry();
        SpUtils.saveString(LANGUAGE, language);
        return language;
    }

    /**
     * 获取本机号码(需要“android.permission.READ_PHONE_STATE”权限)
     */
    @SuppressLint("MissingPermission")
    public static String getPhoneNumber() {
        try {
            TelephonyManager tm = (TelephonyManager) getApplication().getSystemService(Activity.TELEPHONY_SERVICE);
            if (tm != null) {
                return tm.getLine1Number();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI() {
        if (!TextUtils.isEmpty(SpUtils.getString(IMEI))) {
            return SpUtils.getString(IMEI);
        }
        String imei = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_PHONE_STATE)
                            != PackageManager.PERMISSION_GRANTED) {
                return getRandomIMEI();
            }

            TelephonyManager tm = (TelephonyManager) getApplication().getSystemService(Activity.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                imei = tm.getDeviceId();
            } else if (Build.VERSION.SDK_INT >= 29) {
                imei = "";
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelephonyManager tm2 = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
                imei = tm2.getImei();
            } else {
                ///反射获取
                Method method = tm.getClass().getMethod("getImei");
                imei = (String) method.invoke(tm);
            }

            if (TextUtils.isEmpty(imei) && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                String randomImeiSP = SpUtils.getString("RANDOM_IMEI_SP");
                if (!TextUtils.isEmpty(randomImeiSP)) {
                    imei = randomImeiSP;
                } else {
                    imei = getRandomIMEI();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                imei = getRandomIMEI();
            }
        }
        if (TextUtils.isEmpty(imei)) {
            imei = getRandomIMEI();
        }
        SpUtils.saveString(IMEI, imei);
        return imei;
    }

    public static String getRandomIMEI() {
        String randomImeiSP = null;
        try {
            Random r = new Random();
            StringBuilder rs = new StringBuilder();
            rs.append("99999");

            for (int i = 0; i < 10; i++) {
                rs.append(r.nextInt(10));
            }

            randomImeiSP = rs.toString();
            SpUtils.saveString("RANDOM_IMEI_SP", randomImeiSP);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return randomImeiSP;
    }

    /**
     * 获取手机ID(需要“android.permission.READ_PHONE_STATE”权限)
     */

    @SuppressLint("HardwareIds")
    public static String getAndroidId() {
        if (!TextUtils.isEmpty(SpUtils.getString(ANDROID_ID))) {
            return SpUtils.getString(ANDROID_ID);
        }
        String androidId = "";
        try {
            androidId = "" + Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
            SpUtils.saveString(ANDROID_ID, androidId);
            return ANDROID_ID;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取唯一设备ID
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    public static String getDeviceToken(Context context) {
        String ANDROID_ID = "";
        try {
            ANDROID_ID = "" + Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        } catch (Exception e) {

        }

        Log.e("PhoneUtils", "ANDROID_ID==" + ANDROID_ID);
        return ANDROID_ID;
    }

    /**
     * 获取手机Mac(需要“android.permission.ACCESS_WIFI_STATE”权限)
     */
    @SuppressLint("HardwareIds")
    public static String getMAC() {
        if (!TextUtils.isEmpty(SpUtils.getString(MAC))) {
            return SpUtils.getString(MAC);
        }
        try {
            Application application = getApplication();
            WifiManager wifiManager = (WifiManager) application.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            String macAddress = wInfo.getMacAddress();
            SpUtils.saveString(MAC, macAddress);
            return macAddress;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获得当前应用包名
     */
    public static String getPackageName() {
        String packageNames = "";
        try {
            PackageManager pm = getApplication().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getApplication().getPackageName(), 0);
            packageNames = packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
            return packageNames;
        }
        return packageNames;
    }

    /**
     * 获得当前应用名称
     */
    public static String getAppName() {
        try {
            PackageManager pm = getApplication().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getApplication().getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return getApplication().getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 获得当前应用版本名称
     */
    public static String getVersionName() {
        try {
            PackageManager pm = getApplication().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getApplication().getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得当前应用版本名称
     */
    public static int getVersionCode() {
        try {
            PackageManager pm = getApplication().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getApplication().getPackageName(), 0);
            //返回版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取运营商名字
     * getSimOperatorName()直接获取到运营商的名字,,getSimOperator()获取code,例如"46000"为移动
     */
    public static String getOperatorName() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                return telephonyManager.getSimOperator();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 打印不包括虚拟按键的分辨率、屏幕宽度dpi、最小宽度sw
     */
    public static int getDeviceWidth() {
        if (SpUtils.getInt(WIDTH_PIXELS) > 0) {
            return SpUtils.getInt(WIDTH_PIXELS);
        }
        DisplayMetrics metric = getApplication().getResources().getDisplayMetrics();
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        SpUtils.saveInt(WIDTH_PIXELS, width);
        return width;
    }

    /**
     * 打印不包括虚拟按键的分辨率、屏幕高度dpi、最小宽度sw
     */
    public static int getDeviceHeight() {
        if (SpUtils.getInt(HEIGHT_PIXELS) > 0) {
            return SpUtils.getInt(HEIGHT_PIXELS);
        }
        DisplayMetrics metric = getApplication().getResources().getDisplayMetrics();
        int height = metric.heightPixels;     // 屏幕高度（像素）
        SpUtils.saveInt(HEIGHT_PIXELS, height);
        return height;
    }

    /**
     * 打印不包括虚拟按键的分辨率、屏幕密度dpi、最小宽度sw
     */
    public static int getDeviceDensity() {
        if (SpUtils.getInt(DENSITY) > 0) {
            return SpUtils.getInt(DENSITY);
        }
        DisplayMetrics metric = getApplication().getResources().getDisplayMetrics();
        float density = metric.density;     // 屏幕密度（像素）dpi
        SpUtils.saveInt(DENSITY, (int) density);
        return (int) density;
    }

    /**
     * 获取屏幕的尺寸,如5.5尺寸,4.7尺寸
     */
    @SuppressLint("NewApi")
    public static String getScreenSizeOfDevice2(Activity activity) {
        try {
            Point point = new Point();
            activity.getWindowManager().getDefaultDisplay().getRealSize(point);
            DisplayMetrics dm = getApplication().getResources().getDisplayMetrics();
            double x = Math.pow(point.x / dm.xdpi, 2);
            double y = Math.pow(point.y / dm.ydpi, 2);
            double screenInches = Math.sqrt(x + y);
            //保留一位小数
            BigDecimal bg = new BigDecimal(screenInches).setScale(1, RoundingMode.UP);
            //BigDecimal bg2 = new BigDecimal(screenInches).setScale(2, RoundingMode.UP);
            //LogUtils.e("获取的原始屏幕尺寸=" + screenInches + "||计算屏幕尺寸=" + bg + "||计算屏幕尺寸=" + bg2);
            return String.valueOf(bg.doubleValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressLint("NewApi")
    public static String getScreenSizeOfDevice() {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            int dens = dm.densityDpi;
            double wi = (double) width / (double) dens;
            double hi = (double) height / (double) dens;
            double x = Math.pow(wi, 2);
            double y = Math.pow(hi, 2);
            double screenInches = Math.sqrt(x + y);
            return screenInches + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取当前进程名
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    /**
     * 获取ua信息
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static String getUserAgent(Context context) {
        if (!TextUtils.isEmpty(SpUtils.getString(USER_AGENT))) {
            return SpUtils.getString(USER_AGENT);
        }
        String ua = "";
        //api 19 之前
        if (Build.VERSION.SDK_INT < 19) {
            WebView webview = new WebView(context);

            // 得到WebSettings对象
            WebSettings settings = webview.getSettings();

            // 设置支持JavaScript
            settings.setJavaScriptEnabled(true);
            ua = settings.getUserAgentString();
        } else {
            ua = WebSettings.getDefaultUserAgent(context);
        }
        SpUtils.saveString(USER_AGENT, ua);
        return ua;

    }

    public static int getScreenWidth(Context context) {
        int screenWith = -1;
        try {
            screenWith = context.getResources().getDisplayMetrics().widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenWith;
    }

    public static int getScreenHeight(Context context) {
        int screenHeight = -1;
        try {
            screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenHeight;
    }

    /**
     * 获取真实的高度,全面屏水滴屏需要
     *
     * @param context
     * @return
     */
    public static int getRealyScreenHeight(Context context) {
        int screenHeight = -1;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //新的获取屏幕高度的方法
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Point point = new Point();
                if (wm != null) {
                    wm.getDefaultDisplay().getRealSize(point);
                    screenHeight = point.y;
                    //LogUtils.e("换一种方法获取的屏幕高度=" + screenHeight);
                } else {
                    //原来获取屏幕高度的方法
                    screenHeight = context.getResources().getDisplayMetrics().heightPixels;
                }

            } else {
                //原来获取屏幕高度的方法
                screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return screenHeight;
    }


    /**
     * dp转px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density; //屏幕密度
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density; //屏幕密度
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px转换为sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * 将sp转换为px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 陀螺仪参数获取
     */
    public static void getSensorManagerInfo(Context context, final SensorManagerCallback callBack) {
        try {
            // 取传感器
            final SensorManager sensorManager =
                    (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            final Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);//选取传感器,陀螺仪
            //设置监听器
            if (sensorEventListener == null) {
                sensorEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];
                        //LogUtils.e("陀螺仪返回X=" + x + "|y=" + y + "|z=" + z);
                        if (callBack != null) {
                            callBack.getInfo(x, y, z);
                        }
                        sensorManager.unregisterListener(this, sensor);
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
            } else {
                sensorManager.unregisterListener(sensorEventListener);
            }

            sensorManager.registerListener(sensorEventListener, sensor, sensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备支持的cpu架构
     * return String
     */
    @SuppressLint("NewApi")
    public static String getSupportFramework() {
        String supportedAbisStr = String.valueOf(SystemConstants.CPU.CPU_FRAMEWORK_UNKNOWN);
        try {
            String[] supportedAbis = Build.SUPPORTED_ABIS;
            StringBuilder sb = new StringBuilder();
            if (supportedAbis != null && supportedAbis.length > 0) {
                for (int i = 0; i < supportedAbis.length; i++) {
                    if (i < supportedAbis.length - 1) {
                        sb.append(supportedAbis[i] + ",");
                    } else {
                        sb.append(supportedAbis[i]);
                    }
                }
            }
            supportedAbisStr = sb.toString().toLowerCase();
            if (supportedAbisStr.contains("arm64-v8a")) {
                return String.valueOf(SystemConstants.CPU.CPU_FRAMEWORK_ARM64);
            }
            if (!supportedAbisStr.contains("arm64-v8a") && supportedAbisStr.contains("armeabi")) {
                return String.valueOf(SystemConstants.CPU.CPU_FRAMEWORK_ARM);
            }
            if (supportedAbisStr.contains("x86")) {
                return String.valueOf(SystemConstants.CPU.CPU_FRAMEWORK_X86);
            }
            if (supportedAbisStr.contains("x86_64")) {
                return String.valueOf(SystemConstants.CPU.CPU_FRAMEWORK_X86_64);
            }
            return supportedAbisStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return supportedAbisStr;
    }

    /**
     * 获取CPU最大频率（单位KHZ）
     * return String
     */
    public static String getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    /**
     * 获取CPU型号
     * return String
     */
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取ram大小
     * return String ，大小m
     */
    public static String getTotalRam() {
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (firstLine != null) {
            totalRam = (int) Math.ceil((new Float(Float.valueOf(firstLine) / 1024).doubleValue()));
        }

        return String.valueOf(totalRam);
    }


    /**
     * 获取rom大小，单位m
     * return String
     */
    public static long[] getRomMemroy() {
        long[] romInfo = new long[2];
        try {
            //Total rom memory
            romInfo[0] = getTotalInternalMemorySize();

            //Available rom memory
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            romInfo[1] = blockSize * availableBlocks / 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return romInfo;
    }

    /**
     * 获取内部存储总大小,单位M
     * return String
     */
    public static long getTotalInternalMemorySize() {
        long blockSize = 0;
        long totalBlocks = 0;
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (totalBlocks * blockSize) / (1024 * 1024);
    }

    /**
     * 获取sdcard大小
     * return long[]
     */
    public static long[] getSDCardMemory() {
        long[] sdCardInfo = new long[2];
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File sdcardDir = Environment.getExternalStorageDirectory();
                StatFs sf = new StatFs(sdcardDir.getPath());
                long bSize = sf.getBlockSize();
                long bCount = sf.getBlockCount();
                long availBlocks = sf.getAvailableBlocks();

                sdCardInfo[0] = bSize * bCount;//总大小
                sdCardInfo[1] = bSize * availBlocks;//可用大小
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdCardInfo;
    }

    /**
     * 系统的版本信息
     * return String[]
     */
    public static String[] getVersion() {
        String[] version = {"null", "null", "null", "null"};
        String str1 = "/proc/version";
        String str2;
        String[] arrayOfString;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            version[0] = arrayOfString[2];//KernelVersion
            localBufferedReader.close();
        } catch (IOException e) {
        }
        version[1] = Build.VERSION.RELEASE;// firmware version
        version[2] = Build.MODEL;//model
        version[3] = Build.DISPLAY;//system version
        return version;
    }

    /***
     *获取设备名称，包括自定义名称
     */
    public static String getDeviceName(Context context) {
        String name = "";
        try {
            name = Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }


    /**
     * 获取当前设备所连接wifi信息
     */
    public static String getMyWifiName(Context context) {
        String str = "";
        try {
            WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (mWifi.isWifiEnabled()) {
                WifiInfo wifiInfo = mWifi.getConnectionInfo();
                String netName = wifiInfo.getSSID(); //获取被连接网络的名称
                str = netName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.replace("\"", "");
    }

    /**
     * 获取当前设备所连接wifi信息
     *
     * @return
     */
    public static String getMyWifiList(Context context) {
        List<ScanResult> scanResults = null;
        List<WifiBean> wifiBeans = null;
        try {
            WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (mWifi.isWifiEnabled()) {
                scanResults = mWifi.getScanResults();  //getScanResults() 扫描到的当前设备的WiFi列表
            }
            wifiBeans = new ArrayList<>();
            for (ScanResult item : scanResults) {
                WifiBean wifiBean = new WifiBean();
                wifiBean.setConnectingWifiName(item.SSID);
                wifiBean.setConnectingWifiAddress(item.BSSID);
                wifiBeans.add(wifiBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.e("getMyWifiList:" + scanResults);
        LogUtils.e("getMyWifiList,wifiBeans:" + wifiBeans);
        if (null != wifiBeans && !wifiBeans.isEmpty()) {
            return wifiBeans.toString();
        } else {
            return "";
        }
    }

    /**
     * 获取电池电量
     *
     * @param context
     */
    @SuppressLint("NewApi")
    public static void getBattery(Context context) {
        try {
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            int capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            SpUtils.saveInt(BATTERY_CAPACITY, capacity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int fetchBattery() {
        return SpUtils.getInt(DeviceUtil.BATTERY_CAPACITY, 0);
    }
}
