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
public class EventBean implements Serializable {

    private List<String> midpoint;
    private List<String> fullscreen;
    private List<String> mute;
    private List<String> pause;
    private List<String> unmute;
    private List<String> close;
    private List<String> complete;
    private List<String> thirdQuartile;
    private List<String> start;
    private List<String> firstQuartile;

    public void setMidpoint (List<String> midpoint) {
        this.midpoint = midpoint;
    }

    public List<String> getMidpoint () {
        return midpoint;
    }

    public void setFullscreen (List<String> fullscreen) {
        this.fullscreen = fullscreen;
    }

    public List<String> getFullscreen () {
        return fullscreen;
    }

    public void setMute (List<String> mute) {
        this.mute = mute;
    }

    public List<String> getMute () {
        return mute;
    }

    public void setPause (List<String> pause) {
        this.pause = pause;
    }

    public List<String> getPause () {
        return pause;
    }

    public void setUnmute (List<String> unmute) {
        this.unmute = unmute;
    }

    public List<String> getUnmute () {
        return unmute;
    }

    public void setClose (List<String> close) {
        this.close = close;
    }

    public List<String> getClose () {
        return close;
    }

    public void setComplete (List<String> complete) {
        this.complete = complete;
    }

    public List<String> getComplete () {
        return complete;
    }

    public void setThirdQuartile (List<String> thirdQuartile) {
        this.thirdQuartile = thirdQuartile;
    }

    public List<String> getThirdQuartile () {
        return thirdQuartile;
    }

    public void setStart (List<String> start) {
        this.start = start;
    }

    public List<String> getStart () {
        return start;
    }

    public void setFirstQuartile (List<String> firstQuartile) {
        this.firstQuartile = firstQuartile;
    }

    public List<String> getFirstQuartile () {
        return firstQuartile;
    }

}