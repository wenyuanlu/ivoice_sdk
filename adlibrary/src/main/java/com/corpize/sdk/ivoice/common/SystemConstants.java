package com.corpize.sdk.ivoice.common;

/**
 * author : xpSun
 * date : 8/30/21
 * description :
 */
public class SystemConstants {

    public static final class CPU {
        //未知类型
        public static final int CPU_FRAMEWORK_UNKNOWN = 0;

        //32位 ARM CPU
        public static final int CPU_FRAMEWORK_ARM = 1;

        //64位 ARM CPU
        public static final int CPU_FRAMEWORK_ARM64 = 2;

        //32位 X86 CPU
        public static final int CPU_FRAMEWORK_X86 = 3;

        //64位 x86 cpu
        public static final int CPU_FRAMEWORK_X86_64 = 4;
    }

    public static final class DeviceType {
        //未知类型
        public static final int DEVICE_TYPE_UNKNOWN = 0;

        //移动/平板设备
        public static final int DEVICE_TYPE_DEFAULT = 1;

        //个人电脑
        public static final int DEVICE_TYPE_PC = 2;

        //智能TV
        public static final int DEVICE_TYPE_TV = 3;

        //手机
        public static final int DEVICE_TYPE_PHONE = 4;

        //平板电脑
        public static final int DEVICE_TYPE_PAD = 5;

        //连接设备
        public static final int DEVICE_TYPE_LINK = 6;

        //机顶盒
        public static final int DEVICE_TYPE_BOX = 7;

        //智能家居
        public static final int DEVICE_TYPE_HOME = 8;

        //车机设备
        public static final int DEVICE_TYPE_CAR = 9;

        //android模拟器,10/11/12为ios专用,故跳转到13
        public static final int DEVICE_TYPE_VIRTUAL = 13;
    }

    public static final class PlugType {
        //未知
        public static final int plug_type_UNKNOWN = 0;

        //设备外放
        public static final int DEVICE_EXTERNAL_RELEASE = 1;

        //设备外接式耳机,3.5mm
        public static final int DEVICE_LINK_PLUG_TYPE = 2;

        //蓝牙耳机
        public static final int BLUETOOTH_PLUG_TYPE = 3;
    }

}
