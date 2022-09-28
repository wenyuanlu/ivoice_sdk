package com.corpize.sdk.ivoice.bean;

import java.util.List;

/**
 * @author Created by SXF on 2021/9/1 10:24 AM.
 * @description wifi信息实体类
 */
public class WifiBean {
    private String connectingWifiName;//已连接的wifi名称
    private String connectingWifiAddress;//已连接的wifi mac地址
    private List<WifiUnpairBean> wifiUnpairBean;//周围在线wifi列表

    public static class WifiUnpairBean {
        private String wifiName;//蓝牙名称
        private String wifiAddress;//蓝牙mac地址
        private int signalIntensity;//信号强度

        public String getWifiName() {
            return wifiName;
        }

        public void setWifiName(String wifiName) {
            this.wifiName = wifiName;
        }

        public String getWifiAddress() {
            return wifiAddress;
        }

        public void setWifiAddress(String wifiAddress) {
            this.wifiAddress = wifiAddress;
        }

        public int getSignalIntensity() {
            return signalIntensity;
        }

        public void setSignalIntensity(int signalIntensity) {
            this.signalIntensity = signalIntensity;
        }

        @Override
        public String toString() {
            return "{" +
                    "wifiName='" + wifiName + '\'' +
                    ", wifiAddress='" + wifiAddress + '\'' +
                    ", signalIntensity=" + signalIntensity +
                    '}';
        }
    }

    public String getConnectingWifiName() {
        return connectingWifiName;
    }

    public void setConnectingWifiName(String connectingWifiName) {
        this.connectingWifiName = connectingWifiName;
    }

    public String getConnectingWifiAddress() {
        return connectingWifiAddress;
    }

    public void setConnectingWifiAddress(String connectingWifiAddress) {
        this.connectingWifiAddress = connectingWifiAddress;
    }

    public List<WifiUnpairBean> getWifiUnpairBean() {
        return wifiUnpairBean;
    }

    public void setWifiUnpairBean(List<WifiUnpairBean> wifiUnpairBean) {
        this.wifiUnpairBean = wifiUnpairBean;
    }

    @Override
    public String toString() {
        return "{" +
                "connectingWifiName='" + connectingWifiName + '\'' +
                ", connectingWifiAddress='" + connectingWifiAddress + '\'' +
                ", wifiUnpairBean=" + wifiUnpairBean +
                '}';
    }
}
