package com.corpize.sdk.ivoice.utils;

import android.text.TextUtils;

import com.corpize.sdk.ivoice.bean.response.RemindBean;
import com.corpize.sdk.ivoice.common.CommonShakeEnum;

/**
 * author Created by SXF on 2021/5/25 5:04 PM.
 * description startMusic判断类
 */
public class StartMusicUtils {

    /**
     * 启动音乐
     */
    public static String getStartMusic (int perMis, RemindBean remind, int action) {
        String startMusic = "";
        switch (perMis) {
            //只有摇一摇的权限
            case PermissionUtil.PERMISSION_CODE_SHAKE:
                //摇一摇和录音权限，没有读写权限。该权限下可以摇一摇
            case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD:
                if (action == CommonShakeEnum.COMMON_ENUM_WEBVIEW.getAction()
                        || action == CommonShakeEnum.COMMON_ENUM_SYSTEM_WEB.getAction()) {
                    //1打开app网页 2打开系统浏览器网页
                    if (null != remind &&
                            null != remind.getOpenldp() &&
                            null != remind.getOpenldp().getShakeme() &&
                            !TextUtils.isEmpty(remind.getOpenldp().getShakeme().getStart())) {
                        startMusic = remind.getOpenldp().getShakeme().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_PHONE.getAction()) { //拨打电话
                    if (null != remind &&
                            null != remind.getPhone() &&
                            null != remind.getPhone().getShakeme() &&
                            !TextUtils.isEmpty(remind.getPhone().getShakeme().getStart())) {
                        startMusic = remind.getPhone().getShakeme().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction()) { //下载
                    if (null != remind &&
                            null != remind.getDownload() &&
                            null != remind.getDownload().getShakeme() &&
                            !TextUtils.isEmpty(remind.getDownload().getShakeme().getStart())) {
                        startMusic = remind.getDownload().getShakeme().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_DEEPLINK.getAction()) {//deeplink
                    if (null != remind &&
                            null != remind.getDeeplink() &&
                            null != remind.getDeeplink().getShakeme() &&
                            !TextUtils.isEmpty(remind.getDeeplink().getShakeme().getStart())) {
                        startMusic = remind.getDeeplink().getShakeme().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_COUPON.getAction()) {//优惠券
                    if (null != remind &&
                            null != remind.getOpenldp() &&
                            null != remind.getOpenldp().getShakeme() &&
                            !TextUtils.isEmpty(remind.getOpenldp().getShakeme().getStart())) {
                        startMusic = remind.getOpenldp().getShakeme().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_BRAND.getAction()) {
                    if (null != remind &&
                            null != remind.getBp() &&
                            null != remind.getBp().getCar() &&
                            !TextUtils.isEmpty(remind.getBp().getCar().getUrl())) {
                        //品牌
                        startMusic = remind.getBp().getCar().getUrl();
                    }
                }
                break;
            //摇一摇和录音还有读写的权限
            case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD_WRITE:
                if (action == CommonShakeEnum.COMMON_ENUM_WEBVIEW.getAction()
                        || action == CommonShakeEnum.COMMON_ENUM_SYSTEM_WEB.getAction()) {
                    //1打开app网页 2打开系统浏览器网页
                    if (null != remind &&
                            null != remind.getOpenldp() &&
                            null != remind.getOpenldp().getCombined() &&
                            !TextUtils.isEmpty(remind.getOpenldp().getCombined().getStart())) {
                        startMusic = remind.getOpenldp().getCombined().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_PHONE.getAction()) {
                    if (null != remind &&
                            null != remind.getPhone() &&
                            null != remind.getPhone().getCombined() &&
                            !TextUtils.isEmpty(remind.getPhone().getCombined().getStart())) {
                        //拨打电话
                        startMusic = remind.getPhone().getCombined().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction()) {
                    if (null != remind &&
                            null != remind.getDownload() &&
                            null != remind.getDownload().getCombined() &&
                            !TextUtils.isEmpty(remind.getDownload().getCombined().getStart())) {
                        //下载
                        startMusic = remind.getDownload().getCombined().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_DEEPLINK.getAction()) {
                    if (null != remind &&
                            null != remind.getDeeplink() &&
                            null != remind.getDeeplink().getCombined() &&
                            !TextUtils.isEmpty(remind.getDeeplink().getCombined().getStart())) {
                        //deeplink
                        startMusic = remind.getDeeplink().getCombined().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_COUPON.getAction()) {
                    if (null != remind &&
                            null != remind.getOpenldp() &&
                            null != remind.getOpenldp().getShakeme() &&
                            !TextUtils.isEmpty(remind.getOpenldp().getShakeme().getStart())) {
                        //优惠券
                        startMusic = remind.getOpenldp().getShakeme().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_BRAND.getAction()) {
                    if (null != remind &&
                            null != remind.getBp() &&
                            null != remind.getBp().getCar() &&
                            !TextUtils.isEmpty(remind.getBp().getCar().getUrl())) {
                        //品牌
                        startMusic = remind.getBp().getCar().getUrl();
                    }
                }
                break;
            //只有录音和读写权限
            case PermissionUtil.PERMISSION_CODE_RECORD_WRITE:
                if (action == CommonShakeEnum.COMMON_ENUM_WEBVIEW.getAction() ||
                        action == CommonShakeEnum.COMMON_ENUM_SYSTEM_WEB.getAction()) {
                    if (null != remind &&
                            null != remind.getOpenldp() &&
                            null != remind.getOpenldp().getChat() &&
                            !TextUtils.isEmpty(remind.getOpenldp().getChat().getStart())) {
                        //1打开app网页 2打开系统浏览器网页
                        startMusic = remind.getOpenldp().getChat().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_PHONE.getAction()) {
                    if (null != remind &&
                            null != remind.getPhone() &&
                            null != remind.getPhone().getChat() &&
                            !TextUtils.isEmpty(remind.getPhone().getChat().getStart())) {
                        //拨打电话
                        startMusic = remind.getPhone().getChat().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction()) {
                    if (null != remind &&
                            null != remind.getDownload() &&
                            null != remind.getDownload().getChat() &&
                            !TextUtils.isEmpty(remind.getDownload().getChat().getStart())) {
                        //下载
                        startMusic = remind.getDownload().getChat().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_DEEPLINK.getAction()) {
                    if (null != remind &&
                            null != remind.getDeeplink() &&
                            null != remind.getDeeplink().getChat() &&
                            !TextUtils.isEmpty(remind.getDeeplink().getChat().getStart())) {
                        //deeplink
                        startMusic = remind.getDeeplink().getChat().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_COUPON.getAction()) {
                    if (null != remind &&
                            null != remind.getOpenldp() &&
                            null != remind.getOpenldp().getChat() &&
                            !TextUtils.isEmpty(remind.getOpenldp().getChat().getStart())) {
                        //优惠券
                        startMusic = remind.getOpenldp().getChat().getStart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_BRAND.getAction()) {
                    if (null != remind &&
                            null != remind.getBp() &&
                            null != remind.getBp().getCar() &&
                            !TextUtils.isEmpty(remind.getBp().getCar().getUrl())) {
                        //品牌
                        startMusic = remind.getBp().getCar().getUrl();
                    }
                }
                break;
            default:
                break;
        }
        LogUtils.e("startMusic:" + startMusic);
        return startMusic;
    }

    /**
     * 启动音乐-后台播放
     */
    public static String getStartMusicToBackstage (RemindBean remind, int action) {
        String startMusic = "";
        if (action == CommonShakeEnum.COMMON_ENUM_WEBVIEW.getAction() ||
                action == CommonShakeEnum.COMMON_ENUM_SYSTEM_WEB.getAction()) {
            //1打开app网页 2打开系统浏览器网页
            if (null != remind &&
                    null != remind.getOpenldp() &&
                    null != remind.getOpenldp().getUnlock() &&
                    null != remind.getOpenldp().getUnlock().getBackend() &&
                    null != remind.getOpenldp().getUnlock().getBackend().getShakeme() &&
                    !TextUtils.isEmpty(remind.getOpenldp().getUnlock().getBackend().getShakeme().getStart())) {
                startMusic = remind.getOpenldp().getUnlock().getBackend().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_PHONE.getAction()) { //拨打电话
            if (null != remind &&
                    null != remind.getPhone() &&
                    null != remind.getPhone().getUnlock() &&
                    null != remind.getPhone().getUnlock().getBackend() &&
                    null != remind.getPhone().getUnlock().getBackend().getShakeme() &&
                    !TextUtils.isEmpty(remind.getPhone().getUnlock().getBackend().getShakeme().getStart())) {
                startMusic = remind.getPhone().getUnlock().getBackend().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction()) { //下载
            if (null != remind &&
                    null != remind.getDownload() &&
                    null != remind.getDownload().getUnlock() &&
                    null != remind.getDownload().getUnlock().getBackend() &&
                    null != remind.getDownload().getUnlock().getBackend().getShakeme() &&
                    !TextUtils.isEmpty(remind.getDownload().getUnlock().getBackend().getShakeme().getStart())) {
                startMusic = remind.getDownload().getUnlock().getBackend().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_DEEPLINK.getAction()) {//deeplink
            if (null != remind &&
                    null != remind.getDeeplink() &&
                    null != remind.getDeeplink().getUnlock() &&
                    null != remind.getDeeplink().getUnlock().getBackend() &&
                    null != remind.getDeeplink().getUnlock().getBackend().getShakeme() &&
                    !TextUtils.isEmpty(remind.getDeeplink().getUnlock().getBackend().getShakeme().getStart())) {
                startMusic = remind.getDeeplink().getUnlock().getBackend().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_COUPON.getAction()) {//优惠券
            if (null != remind &&
                    null != remind.getOpenldp() &&
                    null != remind.getOpenldp().getUnlock() &&
                    null != remind.getOpenldp().getUnlock().getBackend() &&
                    null != remind.getOpenldp().getUnlock().getBackend().getShakeme() &&
                    !TextUtils.isEmpty(remind.getOpenldp().getUnlock().getBackend().getShakeme().getStart())) {
                startMusic = remind.getOpenldp().getUnlock().getBackend().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_BRAND.getAction()) {
            if (null != remind &&
                    null != remind.getBp() &&
                    null != remind.getBp().getCar() &&
                    !TextUtils.isEmpty(remind.getBp().getCar().getUrl())) {
                //品牌
                startMusic = remind.getBp().getCar().getUrl();
            }
        }
        LogUtils.e("startMusic:" + startMusic);
        return startMusic;
    }

    //锁屏时 前台
    public static String getStartMusicToFrontLockScreen (RemindBean remind, int action) {
        String startMusic = "";
        if (action == CommonShakeEnum.COMMON_ENUM_WEBVIEW.getAction() ||
                action == CommonShakeEnum.COMMON_ENUM_SYSTEM_WEB.getAction()) {
            //1打开app网页 2打开系统浏览器网页
            if (null != remind &&
                    null != remind.getOpenldp() &&
                    null != remind.getOpenldp().getLock() &&
                    null != remind.getOpenldp().getLock().getReception() &&
                    null != remind.getOpenldp().getLock().getReception().getShakeme() &&
                    !TextUtils.isEmpty(remind.getOpenldp().getLock().getReception().getShakeme().getStart())) {
                startMusic = remind.getOpenldp().getLock().getReception().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_PHONE.getAction()) { //拨打电话
            if (null != remind &&
                    null != remind.getPhone() &&
                    null != remind.getPhone().getLock() &&
                    null != remind.getPhone().getLock().getReception() &&
                    null != remind.getPhone().getLock().getReception().getShakeme() &&
                    !TextUtils.isEmpty(remind.getPhone().getLock().getReception().getShakeme().getStart())) {
                startMusic = remind.getPhone().getLock().getReception().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction()) { //下载
            if (null != remind &&
                    null != remind.getDownload() &&
                    null != remind.getDownload().getLock() &&
                    null != remind.getDownload().getLock().getReception() &&
                    null != remind.getDownload().getLock().getReception().getShakeme() &&
                    !TextUtils.isEmpty(remind.getDownload().getLock().getReception().getShakeme().getStart())) {
                startMusic = remind.getDownload().getLock().getReception().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_DEEPLINK.getAction()) {//deeplink
            if (null != remind &&
                    null != remind.getDeeplink() &&
                    null != remind.getDeeplink().getLock() &&
                    null != remind.getDeeplink().getLock().getReception() &&
                    null != remind.getDeeplink().getLock().getReception().getShakeme() &&
                    !TextUtils.isEmpty(remind.getDeeplink().getLock().getReception().getShakeme().getStart())) {
                startMusic = remind.getDeeplink().getLock().getReception().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_COUPON.getAction()) {//优惠券
            if (null != remind &&
                    null != remind.getOpenldp() &&
                    null != remind.getOpenldp().getLock() &&
                    null != remind.getOpenldp().getLock().getReception() &&
                    null != remind.getOpenldp().getLock().getReception().getShakeme() &&
                    !TextUtils.isEmpty(remind.getOpenldp().getLock().getReception().getShakeme().getStart())) {
                startMusic = remind.getOpenldp().getLock().getReception().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_BRAND.getAction()) {
            if (null != remind &&
                    null != remind.getBp() &&
                    null != remind.getBp().getCar() &&
                    !TextUtils.isEmpty(remind.getBp().getCar().getUrl())) {
                //品牌
                startMusic = remind.getBp().getCar().getUrl();
            }
        }
        LogUtils.e("startMusic:" + startMusic);
        return startMusic;
    }

    //锁屏时 后台
    public static String getStartMusicToBackLockScreen (RemindBean remind, int action) {
        String startMusic = "";
        if (action == CommonShakeEnum.COMMON_ENUM_WEBVIEW.getAction() ||
                action == CommonShakeEnum.COMMON_ENUM_SYSTEM_WEB.getAction()) {
            //1打开app网页 2打开系统浏览器网页
            if (null != remind &&
                    null != remind.getOpenldp() &&
                    null != remind.getOpenldp().getLock() &&
                    null != remind.getOpenldp().getLock().getBackend() &&
                    null != remind.getOpenldp().getLock().getBackend().getShakeme() &&
                    !TextUtils.isEmpty(remind.getOpenldp().getLock().getBackend().getShakeme().getStart())) {
                startMusic = remind.getOpenldp().getLock().getBackend().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_PHONE.getAction()) { //拨打电话
            if (null != remind &&
                    null != remind.getPhone() &&
                    null != remind.getPhone().getLock() &&
                    null != remind.getPhone().getLock().getBackend() &&
                    null != remind.getPhone().getLock().getBackend().getShakeme() &&
                    !TextUtils.isEmpty(remind.getPhone().getLock().getBackend().getShakeme().getStart())) {
                startMusic = remind.getPhone().getLock().getBackend().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction()) { //下载
            if (null != remind &&
                    null != remind.getDownload() &&
                    null != remind.getDownload().getLock() &&
                    null != remind.getDownload().getLock().getBackend() &&
                    null != remind.getDownload().getLock().getBackend().getShakeme() &&
                    !TextUtils.isEmpty(remind.getDownload().getLock().getBackend().getShakeme().getStart())) {
                startMusic = remind.getDownload().getLock().getBackend().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_DEEPLINK.getAction()) {//deeplink
            if (null != remind &&
                    null != remind.getDeeplink() &&
                    null != remind.getDeeplink().getLock() &&
                    null != remind.getDeeplink().getLock().getBackend() &&
                    null != remind.getDeeplink().getLock().getBackend().getShakeme() &&
                    !TextUtils.isEmpty(remind.getDeeplink().getLock().getBackend().getShakeme().getStart())) {
                startMusic = remind.getDeeplink().getLock().getBackend().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_COUPON.getAction()) {//优惠券
            if (null != remind &&
                    null != remind.getOpenldp() &&
                    null != remind.getOpenldp().getLock() &&
                    null != remind.getOpenldp().getLock().getBackend() &&
                    null != remind.getOpenldp().getLock().getBackend().getShakeme() &&
                    !TextUtils.isEmpty(remind.getOpenldp().getLock().getBackend().getShakeme().getStart())) {
                startMusic = remind.getOpenldp().getLock().getBackend().getShakeme().getStart();
            }
        } else if (action == CommonShakeEnum.COMMON_ENUM_BRAND.getAction()) {
            if (null != remind &&
                    null != remind.getBp() &&
                    null != remind.getBp().getCar() &&
                    !TextUtils.isEmpty(remind.getBp().getCar().getUrl())) {
                //品牌
                startMusic = remind.getBp().getCar().getUrl();
            }
        }
        LogUtils.e("startMusic:" + startMusic);
        return startMusic;
    }

    /**
     * 重新播放音乐
     */
    public static String getReStartMusic (int perMis, RemindBean remind, int action) {
        String reStartMusic = "";
        switch (perMis) {
            //摇一摇和录音还有读写的权限
            case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD_WRITE:
                //只有录音和读写权限
            case PermissionUtil.PERMISSION_CODE_RECORD_WRITE:
                if (action == CommonShakeEnum.COMMON_ENUM_WEBVIEW.getAction() ||
                        action == CommonShakeEnum.COMMON_ENUM_SYSTEM_WEB.getAction()) {
                    if (null != remind &&
                            null != remind.getOpenldp() &&
                            null != remind.getOpenldp().getChat() &&
                            !TextUtils.isEmpty(remind.getOpenldp().getChat().getStart())) {
                        //1打开app网页 2打开系统浏览器网页
                        reStartMusic = remind.getOpenldp().getChat().getRestart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_PHONE.getAction()) {
                    if (null != remind &&
                            null != remind.getPhone() &&
                            null != remind.getPhone().getChat() &&
                            !TextUtils.isEmpty(remind.getPhone().getChat().getStart())) {
                        //拨打电话
                        reStartMusic = remind.getPhone().getChat().getRestart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction()) {
                    if (null != remind &&
                            null != remind.getDownload() &&
                            null != remind.getDownload().getChat() &&
                            !TextUtils.isEmpty(remind.getDownload().getChat().getStart())) {
                        //下载
                        reStartMusic = remind.getDownload().getChat().getRestart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_DEEPLINK.getAction()) {
                    if (null != remind &&
                            null != remind.getDeeplink() &&
                            null != remind.getDeeplink().getChat() &&
                            !TextUtils.isEmpty(remind.getDeeplink().getChat().getStart())) {
                        //deeplink
                        reStartMusic = remind.getDeeplink().getChat().getRestart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_COUPON.getAction()) {
                    if (null != remind &&
                            null != remind.getOpenldp() &&
                            null != remind.getOpenldp().getChat() &&
                            !TextUtils.isEmpty(remind.getOpenldp().getChat().getStart())) {
                        //优惠券
                        reStartMusic = remind.getOpenldp().getChat().getRestart();
                    }
                } else if (action == CommonShakeEnum.COMMON_ENUM_BRAND.getAction()) {
                    if (null != remind &&
                            null != remind.getBp() &&
                            null != remind.getBp().getCar() &&
                            !TextUtils.isEmpty(remind.getBp().getCar().getUrl())) {
                        //品牌
                        reStartMusic = remind.getBp().getCar().getUrl();
                    }
                }
                break;
            default:
                break;
        }
        LogUtils.e("startMusic,reStartMusic:" + reStartMusic);
        return reStartMusic;
    }
}
