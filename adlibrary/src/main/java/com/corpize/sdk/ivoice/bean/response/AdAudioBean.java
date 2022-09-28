/**
 * Copyright 2021 bejson.com
 */
package com.corpize.sdk.ivoice.bean.response;

import java.io.Serializable;
import java.util.List;

/**
 * Auto-generated: 2021-07-13 13:39:51
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class AdAudioBean implements Serializable {

    private String format;
    private int duration;
    private int width;
    private IconBean icon;
    private List<String> clks;
    private List<String> imps;
    private EventBean event;
    private EventtrackersBean eventtrackers;
    private List<String> fallbacktrackers;
    private InteractiveBean interactive;
    private int volume;
    private String backdrop;
    private String tpnumber;
    private String desc;
    private String ldp;
    private int action;
    private String title;
    private int skip;
    private String deeplink;
    private CompanionBean companion;//背景图在对象里
    private int ignoreaudio;
    private int height;
    private String audiourl;
    private String advertiser;
    private String creativeid;
    private long start;
    private long end;
    private int ldpw;
    private int ldph;
    private RenderingConfigBean rendering_config;
    private String firstimg;//封面图
    private String logo;
    private int interactive_conf;
    private int waiting;
    private ChimeTimeBean section;//整点报时开始和结束时间

    private int direct_download;//0:不支持直接下载;1:支持直接下载;
    private int          lock_interaction_switch;//息屏互动开关#0:关;1:开;
    private DownLoadBean download;
    private List<String> coverext;
    private String qrcode;
    private int span;
    private String qr_title;

    public String getQr_title () {
        return qr_title;
    }

    public void setQr_title (String qr_title) {
        this.qr_title = qr_title;
    }

    public int getSpan () {
        return span;
    }

    public void setSpan (int span) {
        this.span = span;
    }

    public String getQrcode () {
        return qrcode;
    }

    public void setQrcode (String qrcode) {
        this.qrcode = qrcode;
    }

    public List<String> getCoverext () {
        return coverext;
    }

    public void setCoverext (List<String> coverext) {
        this.coverext = coverext;
    }

    public DownLoadBean getDownload () {
        return download;
    }

    public void setDownload (DownLoadBean download) {
        this.download = download;
    }

    public int getDirect_download () {
        return direct_download;
    }

    public void setDirect_download (int direct_download) {
        this.direct_download = direct_download;
    }

    public int getLock_interaction_switch () {
        return lock_interaction_switch;
    }

    public void setLock_interaction_switch (int lock_interaction_switch) {
        this.lock_interaction_switch = lock_interaction_switch;
    }

    public int getInteractive_conf() {
        return interactive_conf;
    }

    public void setInteractive_conf(int interactive_conf) {
        this.interactive_conf = interactive_conf;
    }

    public int getWaiting() {
        return waiting;
    }

    public void setWaiting(int waiting) {
        this.waiting = waiting;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public RenderingConfigBean getRendering_config() {
        return rendering_config;
    }

    public void setRendering_config(RenderingConfigBean rendering_config) {
        this.rendering_config = rendering_config;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setIcon(IconBean icon) {
        this.icon = icon;
    }

    public IconBean getIcon() {
        return icon;
    }

    public void setClks(List<String> clks) {
        this.clks = clks;
    }

    public List<String> getClks() {
        return clks;
    }

    public void setImps(List<String> imps) {
        this.imps = imps;
    }

    public List<String> getImps() {
        return imps;
    }

    public void setEvent(EventBean event) {
        this.event = event;
    }

    public EventBean getEvent() {
        return event;
    }

    public void setEventtrackers(EventtrackersBean eventtrackers) {
        this.eventtrackers = eventtrackers;
    }

    public EventtrackersBean getEventtrackers() {
        return eventtrackers;
    }

    public void setFallbacktrackers(List<String> fallbacktrackers) {
        this.fallbacktrackers = fallbacktrackers;
    }

    public List<String> getFallbacktrackers() {
        return fallbacktrackers;
    }

    public void setInteractive(InteractiveBean interactive) {
        this.interactive = interactive;
    }

    public InteractiveBean getInteractive() {
        return interactive;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setTpnumber(String tpnumber) {
        this.tpnumber = tpnumber;
    }

    public String getTpnumber() {
        return tpnumber;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setLdp(String ldp) {
        this.ldp = ldp;
    }

    public String getLdp() {
        return ldp;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getSkip() {
        return skip;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public void setCompanion(CompanionBean companion) {
        this.companion = companion;
    }

    public CompanionBean getCompanion() {
        return companion;
    }

    public void setIgnoreaudio(int ignoreaudio) {
        this.ignoreaudio = ignoreaudio;
    }

    public int getIgnoreaudio() {
        return ignoreaudio;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setAudiourl(String audiourl) {
        this.audiourl = audiourl;
    }

    public String getAudiourl() {
        return audiourl;
    }

    public void setAdvertiser(String advertiser) {
        this.advertiser = advertiser;
    }

    public String getAdvertiser() {
        return advertiser;
    }

    public void setCreativeid(String creativeid) {
        this.creativeid = creativeid;
    }

    public String getCreativeid() {
        return creativeid;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStart() {
        return start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getEnd() {
        return end;
    }

    public void setLdpw(int ldpw) {
        this.ldpw = ldpw;
    }

    public int getLdpw() {
        return ldpw;
    }

    public void setLdph(int ldph) {
        this.ldph = ldph;
    }

    public int getLdph() {
        return ldph;
    }

    public String getFirstimg() {
        return firstimg;
    }

    public void setFirstimg(String firstimg) {
        this.firstimg = firstimg;
    }

    public ChimeTimeBean getSection() {
        return section;
    }

    public void setSection(ChimeTimeBean section) {
        this.section = section;
    }
}