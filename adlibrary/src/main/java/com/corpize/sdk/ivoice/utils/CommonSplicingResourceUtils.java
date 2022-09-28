package com.corpize.sdk.ivoice.utils;

import android.text.TextUtils;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.bean.AdMusicBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.bean.response.InteractiveBean;
import com.corpize.sdk.ivoice.bean.response.RemindBean;
import com.corpize.sdk.ivoice.common.CommonShakeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * author : xpSun
 * date : 12/24/21
 * description :
 */
public class CommonSplicingResourceUtils {

    private CommonSplicingResourceUtils () {

    }

    private static CommonSplicingResourceUtils instance;

    public static CommonSplicingResourceUtils getInstance () {
        if (null == instance) {
            instance = new CommonSplicingResourceUtils();
        }
        return instance;
    }

    public interface OnSplicingStartAdResourceListener {
        void onSplicingStartAdResource (
                List<AdMusicBean> voiceList,
                String downMusic,
                String startMusic,
                int startTime
        );
    }

    public void splicingStartAdResource (
            String adUrl,
            final AdAudioBean bean,
            OnSplicingStartAdResourceListener onSplicingStartAdResourceListener) {

        try {
            InteractiveBean interactive = bean.getInteractive();
            int             action      = bean.getAction();
            RemindBean      remind      = interactive.getRemind();
            String          startMusic  = "";
            String          downMusic   = "";
            int             startTime   = 0;

            if (remind != null
                    && remind.getDownload() != null
                    && remind.getDownload().getShakeme() != null
                    && CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction() == action) {
                downMusic = remind.getDownload().getShakeme().getStart();
            }

            if (action == CommonShakeEnum.COMMON_ENUM_WEBVIEW.getAction()) {
                //App webview 打开链接
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = remind.getOpenldp().getShakeme().getStart();
                    startTime = remind.getOpenldp().getShakeme().getStartTimer();
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_SYSTEM_WEB.getAction()) {
                //系统浏览器打开链接
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = remind.getOpenldp().getShakeme().getStart();
                    startTime = remind.getOpenldp().getShakeme().getStartTimer();
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_PHONE.getAction()) {
                //拨打电话
                if (remind != null && remind.getPhone() != null && remind.getPhone().getShakeme() != null) {
                    startMusic = remind.getPhone().getShakeme().getStart();
                    startTime = remind.getPhone().getShakeme().getStartTimer();
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction()) {
                //下载
                if (remind != null && remind.getDownload() != null && remind.getDownload().getShakeme() != null) {
                    downMusic = remind.getDownload().getShakeme().getStart();
                    startMusic = downMusic;
                    startTime = remind.getDownload().getShakeme().getStartTimer();
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_DEEPLINK.getAction()) {
                //deeplink
                if (remind != null && remind.getDeeplink() != null && remind.getDeeplink().getShakeme() != null) {
                    startMusic = remind.getDeeplink().getShakeme().getStart();
                    startTime = remind.getDeeplink().getShakeme().getStartTimer();
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_COUPON.getAction()) {
                //优惠券
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = remind.getOpenldp().getShakeme().getStart();
                    startTime = remind.getOpenldp().getShakeme().getStartTimer();
                }
            } else if(action == CommonShakeEnum.COMMON_ENUM_BRAND.getAction()){
                if (remind != null && remind.getBp() != null && remind.getBp().getCar() != null) {
                    startMusic = remind.getBp().getCar().getUrl();
                    startTime = remind.getBp().getCar().getTimes();
                }
            }

            List<AdMusicBean> voiceList = new ArrayList<>();
            voiceList.add(new AdMusicBean(adUrl));
            if (!TextUtils.isEmpty(startMusic) && !QCiVoiceSdk.get().isBackground()) {
                voiceList.add(new AdMusicBean(startMusic, downMusic));
            }

            if (onSplicingStartAdResourceListener != null) {
                onSplicingStartAdResourceListener.onSplicingStartAdResource(
                        voiceList,
                        downMusic,
                        startMusic,
                        startTime
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String managerAdWithOutMusic (AdAudioBean bean) {
        String startMusic = null;

        try {
            final int volum     = bean.getVolume();
            int       startTime = 0;
            //有互动的时候
            final InteractiveBean interactive = bean.getInteractive();
            int                   action      = bean.getAction();
            String                ldp         = bean.getLdp();
            final int             waitTime    = null == interactive ? 0 : interactive.getWait();
            String                tpnumber    = bean.getTpnumber();
            List<String>          clks        = bean.getClks();
            RemindBean            remind      = interactive.getRemind();
            startMusic = "";
            String downMusic = "";

            if (remind != null
                    && remind.getDownload() != null
                    && remind.getDownload().getShakeme() != null
                    && CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction() == action) {
                downMusic = remind.getDownload().getShakeme().getStart();
            }

            if (action == CommonShakeEnum.COMMON_ENUM_WEBVIEW.getAction()) {
                //App webview 打开链接
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = remind.getOpenldp().getShakeme().getStart();
                    startTime = remind.getOpenldp().getShakeme().getStartTimer();
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_SYSTEM_WEB.getAction()) {
                //系统浏览器打开链接
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = remind.getOpenldp().getShakeme().getStart();
                    startTime = remind.getOpenldp().getShakeme().getStartTimer();
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_PHONE.getAction()) {
                //拨打电话
                if (remind != null && remind.getPhone() != null && remind.getPhone().getShakeme() != null) {
                    startMusic = remind.getPhone().getShakeme().getStart();
                    startTime = remind.getPhone().getShakeme().getStartTimer();
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction()) {
                //下载
                if (remind != null && remind.getDownload() != null && remind.getDownload().getShakeme() != null) {
                    downMusic = remind.getDownload().getShakeme().getStart();
                    startMusic = downMusic;
                    startTime = remind.getDownload().getShakeme().getStartTimer();
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_DEEPLINK.getAction()) {
                //deeplink
                if (remind != null && remind.getDeeplink() != null && remind.getDeeplink().getShakeme() != null) {
                    startMusic = remind.getDeeplink().getShakeme().getStart();
                    startTime = remind.getDeeplink().getShakeme().getStartTimer();
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_COUPON.getAction()) {
                //优惠券
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = remind.getOpenldp().getShakeme().getStart();
                    startTime = remind.getOpenldp().getShakeme().getStartTimer();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return startMusic;
    }

    //正常情况下
    public AdMusicBean checkAdActionForMusicPath (AdAudioBean bean, int perMis) {
        if (null == bean) {
            return null;
        }
        String startMusic = null;

        try {
            //有互动的时候
            final InteractiveBean interactive = bean.getInteractive();
            int                   action      = bean.getAction();
            RemindBean            remind      = interactive.getRemind();
            startMusic = "";
            String downMusic = "";

            if (remind != null
                    && remind.getDownload() != null
                    && remind.getDownload().getShakeme() != null
                    && CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction() == action) {
                downMusic = remind.getDownload().getShakeme().getStart();
            }

            if (action == CommonShakeEnum.COMMON_ENUM_WEBVIEW.getAction()) {
                //App webview 打开链接
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = StartMusicUtils.getStartMusic(perMis, remind, action);
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_SYSTEM_WEB.getAction()) {
                //系统浏览器打开链接
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = StartMusicUtils.getStartMusic(perMis, remind, action);
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_PHONE.getAction()) {
                //拨打电话
                if (remind != null && remind.getPhone() != null && remind.getPhone().getShakeme() != null) {
                    startMusic = StartMusicUtils.getStartMusic(perMis, remind, action);
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_DOWNLOAD.getAction()) {
                //下载
                if (remind != null && remind.getDownload() != null && remind.getDownload().getShakeme() != null) {
                    startMusic = StartMusicUtils.getStartMusic(perMis, remind, action);
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_DEEPLINK.getAction()) {
                //deeplink
                if (remind != null && remind.getDeeplink() != null && remind.getDeeplink().getShakeme() != null) {
                    startMusic = StartMusicUtils.getStartMusic(perMis, remind, action);
                }
            } else if (action == CommonShakeEnum.COMMON_ENUM_COUPON.getAction()) {
                //优惠券
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = StartMusicUtils.getStartMusic(perMis, remind, action);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new AdMusicBean(startMusic);
    }

    public AdMusicBean checkAdActionForMusicPathCheckScene (AdAudioBean bean, int perMis) {
        if (null == bean) {
            return null;
        }
        String startMusic           = null;
        String backMusic            = null;
        String lockScreenFrontMusic = null;
        String lockScreenBackMusic  = null;

        try {
            //有互动的时候
            final InteractiveBean interactive = bean.getInteractive();
            int                   action      = bean.getAction();
            RemindBean            remind      = interactive.getRemind();
            startMusic = "";
            String downMusic = "";

            startMusic = StartMusicUtils.getStartMusic(perMis, remind, action);
            backMusic = StartMusicUtils.getStartMusicToBackstage(remind, action);
            lockScreenFrontMusic = StartMusicUtils.getStartMusicToFrontLockScreen(remind, action);
            lockScreenBackMusic = StartMusicUtils.getStartMusicToBackLockScreen(remind, action);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int lock_interaction_switch = bean.getLock_interaction_switch();
        SpUtils.saveInt("lock_interaction_switch",lock_interaction_switch);

        if(1 == lock_interaction_switch){
            return new AdMusicBean(
                    startMusic,
                    backMusic,
                    lockScreenFrontMusic,
                    lockScreenBackMusic
            );
        } else {
            return new AdMusicBean(
                    startMusic
            );
        }
    }
}
