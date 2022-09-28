/**
 * Copyright 2022 bejson.com
 */
package com.corpize.sdk.ivoice.bean.response;

import java.io.Serializable;
import java.util.Date;

/**
 * Auto-generated: 2022-01-06 17:20:31
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class DownLoadBean implements Serializable {

    private String ldp;
    private String size;
    private String bundle;
    private String name;
    private String version;

    private String permission;
    private String policy;
    private String author;
    private String logo;

    public String getLdp () {
        return ldp;
    }

    public void setLdp (String ldp) {
        this.ldp = ldp;
    }

    public String getSize () {
        return size;
    }

    public void setSize (String size) {
        this.size = size;
    }

    public String getBundle () {
        return bundle;
    }

    public void setBundle (String bundle) {
        this.bundle = bundle;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getVersion () {
        return version;
    }

    public void setVersion (String version) {
        this.version = version;
    }

    public String getPermission () {
        return permission;
    }

    public void setPermission (String permission) {
        this.permission = permission;
    }

    public String getPolicy () {
        return policy;
    }

    public void setPolicy (String policy) {
        this.policy = policy;
    }

    public String getAuthor () {
        return author;
    }

    public void setAuthor (String author) {
        this.author = author;
    }

    public String getLogo () {
        return logo;
    }

    public void setLogo (String logo) {
        this.logo = logo;
    }
}