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
public class BackendBean implements Serializable {

    private ChatBean     chat;
    private CombinedBean combined;
    private ShakemeBean  shakeme;

    public void setChat (ChatBean chat) {
        this.chat = chat;
    }

    public ChatBean getChat () {
        return chat;
    }

    public void setCombined (CombinedBean combined) {
        this.combined = combined;
    }

    public CombinedBean getCombined () {
        return combined;
    }

    public void setShakeme (ShakemeBean shakeme) {
        this.shakeme = shakeme;
    }

    public ShakemeBean getShakeme () {
        return shakeme;
    }

}