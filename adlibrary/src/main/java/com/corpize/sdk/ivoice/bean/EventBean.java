package com.corpize.sdk.ivoice.bean;

import java.io.Serializable;
import java.util.List;

/**
 * author ：yh
 * date : 2020-02-15 22:17
 * description :
 */
public class EventBean implements Serializable {

    private List<String> start;//开始监听
    private List<String> midpoint;//中间监听
    private List<String> firstQuartile;//四分之一监听
    private List<String> thirdQuartile;//三分之一监听
    private List<String> complete;//完成监听
    private List<String> close;//关闭监听,跳过监听,未播放完成界面被销毁调用
    private List<String> unmute;//不静音监听
    private List<String> mute;//静音监听
    private List<String> pause;//暂停监听
    private List<String> fullscreen;
    private List<String> acceptInvitation;

    public List<String> getMidpoint () {
        return midpoint;
    }

    public void setMidpoint (List<String> midpoint) {
        this.midpoint = midpoint;
    }

    public List<String> getAcceptInvitation () {
        return acceptInvitation;
    }

    public void setAcceptInvitation (List<String> acceptInvitation) {
        this.acceptInvitation = acceptInvitation;
    }

    public List<String> getFullscreen () {
        return fullscreen;
    }

    public void setFullscreen (List<String> fullscreen) {
        this.fullscreen = fullscreen;
    }

    public List<String> getMute () {
        return mute;
    }

    public void setMute (List<String> mute) {
        this.mute = mute;
    }

    public List<String> getPause () {
        return pause;
    }

    public void setPause (List<String> pause) {
        this.pause = pause;
    }

    public List<String> getUnmute () {
        return unmute;
    }

    public void setUnmute (List<String> unmute) {
        this.unmute = unmute;
    }

    public List<String> getClose () {
        return close;
    }

    public void setClose (List<String> close) {
        this.close = close;
    }

    public List<String> getComplete () {
        return complete;
    }

    public void setComplete (List<String> complete) {
        this.complete = complete;
    }

    public List<String> getThirdQuartile () {
        return thirdQuartile;
    }

    public void setThirdQuartile (List<String> thirdQuartile) {
        this.thirdQuartile = thirdQuartile;
    }

    public List<String> getStart () {
        return start;
    }

    public void setStart (List<String> start) {
        this.start = start;
    }

    public List<String> getFirstQuartile () {
        return firstQuartile;
    }

    public void setFirstQuartile (List<String> firstQuartile) {
        this.firstQuartile = firstQuartile;
    }

}
