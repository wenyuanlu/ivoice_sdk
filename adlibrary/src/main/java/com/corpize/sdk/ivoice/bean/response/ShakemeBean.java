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
public class ShakemeBean implements Serializable {

    private String start;
    private int    startTimer;
    private String restart;

    public void setStart (String start) {
        this.start = start;
    }

    public String getStart () {
        return start;
    }

    public int getStartTimer () {
        return startTimer;
    }

    public void setStartTimer (int startTimer) {
        this.startTimer = startTimer;
    }

    public String getRestart () {
        return restart;
    }

    public void setRestart (String restart) {
        this.restart = restart;
    }
}