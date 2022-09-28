package com.corpize.sdk.ivoice.bean;

import java.util.List;

/**
 * @author Created by SXF on 2021/9/1 10:35 AM.
 * @description 蓝牙设备信息
 */
public class BluetoothBean {

    private String localBluetoothName;//本机蓝牙名称
    private String localBluetoothAddress;//本机蓝牙mac地址
    private String connectingBluetoothName;//已连接的蓝牙名称
    private String connectingBluetoothAddress;//已连接的蓝牙mac地址
    private List<BluetoothPairBean> bluetoothPairBean;//配对过的蓝牙列表
    private List<BluetoothUnpairBean> bluetoothUnpairBean;//周围在线蓝牙列表

    public static class BluetoothPairBean {
        private String bluetoothName;//蓝牙名称
        private String bluetoothAddress;//蓝牙mac地址

        public String getBluetoothName() {
            return bluetoothName;
        }

        public String getBluetoothAddress() {
            return bluetoothAddress;
        }

        public void setBluetoothName(String bluetoothName) {
            this.bluetoothName = bluetoothName;
        }

        public void setBluetoothAddress(String bluetoothAddress) {
            this.bluetoothAddress = bluetoothAddress;
        }

        @Override
        public String toString() {
            return "{" +
                    "bluetoothName=" + bluetoothName + ", bluetoothAddress=" + bluetoothAddress +
                    '}';
        }
    }

    public static class BluetoothUnpairBean {
        private String bluetoothName;//蓝牙名称
        private String bluetoothAddress;//蓝牙mac地址
        private int signalIntensity;//信号强度

        public String getBluetoothName() {
            return bluetoothName;
        }

        public String getBluetoothAddress() {
            return bluetoothAddress;
        }

        public int getSignalIntensity() {
            return signalIntensity;
        }

        public void setBluetoothName(String bluetoothName) {
            this.bluetoothName = bluetoothName;
        }

        public void setBluetoothAddress(String bluetoothAddress) {
            this.bluetoothAddress = bluetoothAddress;
        }

        public void setSignalIntensity(int signalIntensity) {
            this.signalIntensity = signalIntensity;
        }

        @Override
        public String toString() {
            return "{" +
                    "bluetoothName=" + bluetoothName +
                    ", bluetoothAddress=" + bluetoothAddress +
                    ", signalIntensity=" + signalIntensity +
                    '}';
        }
    }

    public void setLocalBluetoothName(String localBluetoothName) {
        this.localBluetoothName = localBluetoothName;
    }

    public void setLocalBluetoothAddress(String localBluetoothAddress) {
        this.localBluetoothAddress = localBluetoothAddress;
    }

    public void setConnectingBluetoothName(String connectingBluetoothName) {
        this.connectingBluetoothName = connectingBluetoothName;
    }

    public void setConnectingBluetoothAddress(String connectingBluetoothAddress) {
        this.connectingBluetoothAddress = connectingBluetoothAddress;
    }

    public void setBluetoothPairBean(List<BluetoothPairBean> bluetoothPairBean) {
        this.bluetoothPairBean = bluetoothPairBean;
    }

    public void setBluetoothUnpairBean(List<BluetoothUnpairBean> bluetoothUnpairBean) {
        this.bluetoothUnpairBean = bluetoothUnpairBean;
    }

    @Override
    public String toString() {
        return "{" +
                "localBluetoothName=" + localBluetoothName +
                ", localBluetoothAddress=" + localBluetoothAddress +
                ", connectingBluetoothName=" + connectingBluetoothName +
                ", connectingBluetoothAddress=" + connectingBluetoothAddress +
                ", bluetoothPairBean=" + bluetoothPairBean +
                ", bluetoothUnpairBean=" + bluetoothUnpairBean +
                '}';
    }

}
