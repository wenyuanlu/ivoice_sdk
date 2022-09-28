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
public class RemindBean implements Serializable {

    private CommonRemindChildBean phone;
    private CommonRemindChildBean deeplink;
    private CommonRemindChildBean download;
    private CommonRemindChildBean openldp;
    private RemindChildBPBean     bp;

    public void setPhone (CommonRemindChildBean phone) {
        this.phone = phone;
    }

    public CommonRemindChildBean getPhone () {
        return phone;
    }

    public void setDeeplink (CommonRemindChildBean deeplink) {
        this.deeplink = deeplink;
    }

    public CommonRemindChildBean getDeeplink () {
        return deeplink;
    }

    public void setDownload (CommonRemindChildBean download) {
        this.download = download;
    }

    public CommonRemindChildBean getDownload () {
        return download;
    }

    public void setOpenldp (CommonRemindChildBean openldp) {
        this.openldp = openldp;
    }

    public CommonRemindChildBean getOpenldp () {
        return openldp;
    }

    public RemindChildBPBean getBp () {
        return bp;
    }

    public void setBp (RemindChildBPBean bp) {
        this.bp = bp;
    }
}