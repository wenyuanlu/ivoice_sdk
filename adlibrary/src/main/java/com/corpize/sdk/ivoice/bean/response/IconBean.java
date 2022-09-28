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
public class IconBean implements Serializable {

    private String url;
    private String title;
    private int    w;
    private int    h;

    public void setUrl (String url) {
        this.url = url;
    }

    public String getUrl () {
        return url;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getTitle () {
        return title;
    }

    public void setW (int w) {
        this.w = w;
    }

    public int getW () {
        return w;
    }

    public void setH (int h) {
        this.h = h;
    }

    public int getH () {
        return h;
    }

}