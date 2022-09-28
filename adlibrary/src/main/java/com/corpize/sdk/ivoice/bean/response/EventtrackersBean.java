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
public class EventtrackersBean implements Serializable {

    private List<String> startdownload;
    private List<String> completedownload;
    private List<String> startinstall;
    private List<String> completeinstall;

    public void setStartdownload (List<String> startdownload) {
        this.startdownload = startdownload;
    }

    public List<String> getStartdownload () {
        return startdownload;
    }

    public void setCompletedownload (List<String> completedownload) {
        this.completedownload = completedownload;
    }

    public List<String> getCompletedownload () {
        return completedownload;
    }

    public void setStartinstall (List<String> startinstall) {
        this.startinstall = startinstall;
    }

    public List<String> getStartinstall () {
        return startinstall;
    }

    public void setCompleteinstall (List<String> completeinstall) {
        this.completeinstall = completeinstall;
    }

    public List<String> getCompleteinstall () {
        return completeinstall;
    }

}