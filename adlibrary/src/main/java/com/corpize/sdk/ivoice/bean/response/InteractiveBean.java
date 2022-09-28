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
public class InteractiveBean implements Serializable {

    private RemindBean remind;
    private int        wait;
    private int    reminds;

    public void setRemind (RemindBean remind) {
        this.remind = remind;
    }

    public RemindBean getRemind () {
        return remind;
    }

    public void setWait (int wait) {
        this.wait = wait;
    }

    public int getWait () {
        return wait;
    }

    public void setReminds (int reminds) {
        this.reminds = reminds;
    }

    public int getReminds () {
        return reminds;
    }

}