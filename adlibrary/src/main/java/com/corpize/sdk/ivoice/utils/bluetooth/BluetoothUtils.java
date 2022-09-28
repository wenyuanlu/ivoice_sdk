package com.corpize.sdk.ivoice.utils.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.bean.BluetoothBean;
import com.corpize.sdk.ivoice.bean.BluetoothBean.BluetoothPairBean;
import com.corpize.sdk.ivoice.bean.BluetoothBean.BluetoothUnpairBean;
import com.corpize.sdk.ivoice.utils.GsonUtil;
import com.corpize.sdk.ivoice.utils.LogUtils;
import com.corpize.sdk.ivoice.utils.SpUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * author : xpSun
 * date : 8/31/21
 * description :
 */
public class BluetoothUtils {

    @SuppressLint("StaticFieldLeak")
    private static BluetoothUtils instance;
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private BluetoothBroadcast bluetoothBroadcast;

    private static final String BLUETOOTH_INFO = "bluetooth_info";

    public String getBluetoothInfo() {
        return SpUtils.getString(BLUETOOTH_INFO);
    }

    private BluetoothUtils() {
    }

    public static BluetoothUtils getInstance() {
        if (null == instance) {
            instance = new BluetoothUtils();
        }
        return instance;
    }

    public void init(Context context) {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            //没有蓝牙,蓝牙不可用
            if (null != bluetoothAdapter && bluetoothAdapter.isEnabled()) {
                this.context = context;
                this.bluetoothBroadcast = new BluetoothBroadcast();

                //获取蓝牙名称
                String bluetoothName = bluetoothAdapter.getName();
                LogUtils.e("BluetoothInfo:本机蓝牙名称：" + bluetoothName);
                //蓝牙地址
                String bluetoothAddress = bluetoothAdapter.getAddress();

                BluetoothBean bluetoothBean = GsonUtil.GsonToBean(SpUtils.getString(BLUETOOTH_INFO), BluetoothBean.class);
                if (bluetoothBean == null) {
                    bluetoothBean = new BluetoothBean();
                }
                bluetoothBean.setLocalBluetoothName(bluetoothName);
                bluetoothBean.setLocalBluetoothAddress(bluetoothAddress);
                bluetoothBean.setConnectingBluetoothName(getConnectingBluetooth().getBluetoothName());
                bluetoothBean.setConnectingBluetoothAddress(getConnectingBluetooth().getBluetoothAddress());
                bluetoothBean.setBluetoothPairBean(getPairBluetoothList());
                LogUtils.e("BluetoothInfo:" + bluetoothBean);
                SpUtils.saveString(BLUETOOTH_INFO, GsonUtil.GsonString(bluetoothBean));

                registerBluetoothBroadcast();
                scanBluetooth();
            } else {
                QCiVoiceSdk.get().getSensorManagerInfo(context);//上报检测信息
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始扫描蓝牙设备
     */
    private void scanBluetooth() {
        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
        }
    }

    /**
     * 注册蓝牙监听
     */
    private void registerBluetoothBroadcast() {
        try {
            if (null != context && bluetoothBroadcast != null) {
                // 注册Receiver来获取蓝牙设备相关的结果
                IntentFilter intent = new IntentFilter();
                intent.addAction(BluetoothDevice.ACTION_FOUND); // 用BroadcastReceiver来取得搜索结果
                intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                context.registerReceiver(bluetoothBroadcast, intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注销蓝牙监听
     */
    public void unRegisterBluetoothBroadcast() {
        try {
            if (null != context && bluetoothBroadcast != null) {
                context.unregisterReceiver(bluetoothBroadcast);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取已连接蓝牙信息
     */
    private BluetoothPairBean getConnectingBluetooth() {
        BluetoothPairBean bluetoothPairBean = new BluetoothPairBean();
        try {
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            if (null != devices && !devices.isEmpty()) {
                for (BluetoothDevice item : devices) {
                    if (isConnected(item.getAddress())) {
                        bluetoothPairBean.setBluetoothName(item.getName());
                        bluetoothPairBean.setBluetoothAddress(item.getAddress());
                        LogUtils.e("BluetoothInfo:已连接蓝牙" + item.getName());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bluetoothPairBean;
    }

    /**
     * 获取已配对蓝牙列表
     */
    private List<BluetoothPairBean> getPairBluetoothList() {
        List<BluetoothPairBean> bluetoothPairBeans = new ArrayList<>();
        try {
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            if (null != devices && !devices.isEmpty()) {
                for (BluetoothDevice item : devices) {
                    BluetoothPairBean bluetoothPairBean = new BluetoothPairBean();
                    bluetoothPairBean.setBluetoothName(item.getName());
                    bluetoothPairBean.setBluetoothAddress(item.getAddress());
                    bluetoothPairBeans.add(bluetoothPairBean);
                    LogUtils.e("BluetoothInfo:已配对蓝牙：" + item.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bluetoothPairBeans;
    }

    //根据mac地址判断是否已连接(这里参数可以直接用BluetoothDevice对象)
    //但这么写其实更通用。
    @SuppressLint("PrivateApi")
    public boolean isConnected(String macAddress) {
        try {
            if (!BluetoothAdapter.checkBluetoothAddress(macAddress)) {
                return false;
            }
            final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);

            Method isConnectedMethod = null;
            boolean isConnected;
            try {
                isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                isConnectedMethod.setAccessible(true);
                isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
            } catch (NoSuchMethodException e) {
                isConnected = false;
            } catch (IllegalAccessException e) {
                isConnected = false;
            } catch (InvocationTargetException e) {
                isConnected = false;
            }
            return isConnected;
        } catch (Exception e) {
            return false;
        }
    }

    public static class BluetoothBroadcast extends BroadcastReceiver {
        //附近蓝牙信息列表
        private final List<BluetoothUnpairBean> bluetoothUnpairList = new ArrayList<>();

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    //收集蓝牙信息
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    BluetoothUnpairBean bean = new BluetoothUnpairBean();
                    bean.setBluetoothName(device.getName());
                    bean.setBluetoothAddress(device.getAddress());
                    bean.setSignalIntensity(intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI));
                    bluetoothUnpairList.add(bean);
                    LogUtils.e("BluetoothInfo:附近蓝牙信息" + bean);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    bluetoothUnpairList.clear();
                    LogUtils.e("BluetoothInfo:开始扫描");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    BluetoothBean bluetoothBean = GsonUtil.GsonToBean(SpUtils.getString(BLUETOOTH_INFO), BluetoothBean.class);
                    bluetoothBean.setBluetoothUnpairBean(bluetoothUnpairList);
                    SpUtils.saveString(BLUETOOTH_INFO, GsonUtil.GsonString(bluetoothBean));
                    QCiVoiceSdk.get().getSensorManagerInfo(context);//上报检测信息
                    LogUtils.e("BluetoothInfo:扫描完成");
                    LogUtils.e("BluetoothInfo:" + bluetoothBean);
                    break;
            }
        }
    }
}
