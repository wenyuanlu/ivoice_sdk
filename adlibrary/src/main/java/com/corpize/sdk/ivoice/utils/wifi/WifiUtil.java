package com.corpize.sdk.ivoice.utils.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.corpize.sdk.ivoice.bean.WifiBean;
import com.corpize.sdk.ivoice.bean.WifiBean.WifiUnpairBean;
import com.corpize.sdk.ivoice.utils.GsonUtil;
import com.corpize.sdk.ivoice.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: yh
 * date: 2019-08-19 18:02
 * description: 获取wifi的相关信息
 */
public class WifiUtil {
    private static WifiUtil instance;

    private WifiUtil() { }

    public static WifiUtil getInstance() {
        if (null == instance) {
            instance = new WifiUtil();
        }
        return instance;
    }

    public String getWifiInfo(Context context) {
        WifiBean bean = getMyWifiInfo(context);
        bean.setWifiUnpairBean(getWifiList(context));
        return GsonUtil.GsonString(bean);
    }

    /**
     * 获取当前设备所连接wifi信息
     */
    public WifiBean getMyWifiInfo(Context context) {
        WifiBean bean = new WifiBean();
        try {
            WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (mWifi.isWifiEnabled()) {
                WifiInfo wifiInfo = mWifi.getConnectionInfo();
                bean.setConnectingWifiName(wifiInfo.getSSID()); //获取被连接网络的名称
                bean.setConnectingWifiAddress(wifiInfo.getBSSID()); //获取被连接网络的mac地址
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 获取当前设备附近wifi信息
     *
     * @return
     */
    public List<WifiUnpairBean> getWifiList(Context context) {
        List<ScanResult> scanResults = null;
        List<WifiUnpairBean> wifiBeans = new ArrayList<>();
        try {
            WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (mWifi.isWifiEnabled()) {
                scanResults = mWifi.getScanResults();  //getScanResults() 扫描到的当前设备的WiFi列表
            }
            for (ScanResult item : scanResults) {
                WifiUnpairBean wifiBean = new WifiUnpairBean();
                wifiBean.setWifiName(item.SSID);
                wifiBean.setWifiAddress(item.BSSID);
                wifiBean.setSignalIntensity(item.level);
                wifiBeans.add(wifiBean);
                LogUtils.e("WifiInfo:附近wifi信息" + wifiBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wifiBeans;
    }
}
