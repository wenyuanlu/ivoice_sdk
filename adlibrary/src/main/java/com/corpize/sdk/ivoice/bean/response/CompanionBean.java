/**
 * Copyright 2021 bejson.com
 */
package com.corpize.sdk.ivoice.bean.response;

import java.io.Serializable;

/**
 * Auto-generated: 2021-07-13 13:39:51
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class CompanionBean implements Serializable {

    private String url;
    private int    w;
    private String title;
    private String desc;
    private int       h;
    private VideoBean video;

    public void setUrl (String url) {
        this.url = url;
    }

    public String getUrl () {
        return url;
    }

    public void setW (int w) {
        this.w = w;
    }

    public int getW () {
        return w;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getTitle () {
        return title;
    }

    public void setDesc (String desc) {
        this.desc = desc;
    }

    public String getDesc () {
        return desc;
    }

    public void setH (int h) {
        this.h = h;
    }

    public int getH () {
        return h;
    }

    public void setVideo (VideoBean video) {
        this.video = video;
    }

    public VideoBean getVideo () {
        return video;
    }

}