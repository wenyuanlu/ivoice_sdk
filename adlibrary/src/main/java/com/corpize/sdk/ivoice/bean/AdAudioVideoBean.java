package com.corpize.sdk.ivoice.bean;

import com.corpize.sdk.ivoice.bean.response.EventtrackersBean;

import java.io.Serializable;
import java.util.List;

/**
 * author ：yh
 * date : 2020-11-24 17:55
 * description : 音频广告的内部的视频广告的实体类
 */
public class AdAudioVideoBean implements Serializable {

    /**
     * width : 640
     * duration : 15
     * event : {"midpoint":["http://midpoint1.tracking.com"],"close":["http://close.tracking1.com"],"mute":["http://mute.tracking1.com"],"firstQuartile":["http://firstQuartile.tracking1.com"],"complete":["http://complete1.tracking.com"],"start":["http://start.tracking1.com"],"thirdQuartile":["http://thirdQuartile.tracking1.com"]}
     * clks : ["http://adx-test.corpize.com/test_clks?p={WIN_NOTICE}&type=audio_video"]
     * imps : ["http://adx-test.corpize.com/test_imps?p={WIN_NOTICE}&type=audio_video"]
     * videourl : http://resource.corpize.com/video/shufujia.mp4
     * ldp : https://www.safeguard.com.cn/zh-cn
     * title : 舒肤佳，12小时长久呵护
     * desc
     * skip : 5
     * firstimg : http://resource.corpize.com/video/shufujia_202.jpeg
     * action : 2
     * height : 480
     * format : 2
     */

    private int               format;
    private double            duration;
    private int               width;
    private int               height;
    private int               action;
    private String            ldp;
    private String            videourl;
    private int               skip;
    private String            title;
    private String            desc;
    private String            deeplink;
    private String            icon;
    private String            firstimg;
    private String            tpnumber;
    private String            coupon;               //优惠券图片地址
    private CompanionBean     companion;
    private EventBean         event;                //播放检测
    private List<String>      imps;                 //正常展示的曝光
    private List<String>      clks;                 //点击的曝光
    private List<String>      deeplinktrackers;     //deepLink点击的曝光
    private EventtrackersBean eventtrackers;        //下载的上报

    public int getFormat () {
        return format;
    }

    public void setFormat (int format) {
        this.format = format;
    }

    public int getDuration () {
        return getCount(duration);
    }

    public static int getCount (double val) {
        if (val % 1 == 0) {//是整数
            return (int) val;
        } else {//不是整数
            return (int) (val + 1);
        }
    }

    public void setDuration (double duration) {
        this.duration = duration;
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


    public int getAction () {
        return action;
    }

    public void setAction (int action) {
        this.action = action;
    }

    public String getLdp () {
        return ldp;
    }

    public void setLdp (String ldp) {
        this.ldp = ldp;
    }

    public String getVideourl () {
        return videourl;
    }

    public void setVideourl (String videourl) {
        this.videourl = videourl;
    }

    public int getSkip () {
        return skip;
    }

    public void setSkip (int skip) {
        this.skip = skip;
    }

    public List<String> getImps () {
        return imps;
    }

    public void setImps (List<String> imps) {
        this.imps = imps;
    }

    public List<String> getClks () {
        return clks;
    }

    public void setClks (List<String> clks) {
        this.clks = clks;
    }

    public EventBean getEvent () {
        return event;
    }

    public void setEvent (EventBean event) {
        this.event = event;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getDesc () {
        return desc;
    }

    public void setDesc (String desc) {
        this.desc = desc;
    }

    public String getDeeplink () {
        return deeplink;
    }

    public void setDeeplink (String deeplink) {
        this.deeplink = deeplink;
    }

    //public IconBean getIcon () {
    //    return icon;
    //}
    //
    //public void setIcon (IconBean icon) {
    //    this.icon = icon;
    //}

    public String getIcon () {
        return icon;
    }

    public void setIcon (String icon) {
        this.icon = icon;
    }

    public String getFirstimg () {
        return firstimg;
    }

    public void setFirstimg (String firstimg) {
        this.firstimg = firstimg;
    }

    public CompanionBean getCompanion () {
        return companion;
    }

    public void setCompanion (CompanionBean companion) {
        this.companion = companion;
    }

    public List<String> getDeeplinktrackers () {
        return deeplinktrackers;
    }

    public void setDeeplinktrackers (List<String> deeplinktrackers) {
        this.deeplinktrackers = deeplinktrackers;
    }

    public EventtrackersBean getEventtrackers () {
        return eventtrackers;
    }

    public void setEventtrackers (EventtrackersBean eventtrackers) {
        this.eventtrackers = eventtrackers;
    }

    public String getTpnumber () {
        return tpnumber;
    }

    public void setTpnumber (String tpnumber) {
        this.tpnumber = tpnumber;
    }

    public String getCoupon () {
        return coupon;
    }

    public void setCoupon (String coupon) {
        this.coupon = coupon;
    }
}
