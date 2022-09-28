package com.corpize.sdk.ivoice.bean.response;

import java.io.Serializable;

/**
 * author : xpSun
 * date : 1/5/22
 * description :
 */
public class UnLockBean implements Serializable {

    private BackendBean backend;

    public BackendBean getBackend () {
        return backend;
    }

    public void setBackend (BackendBean backend) {
        this.backend = backend;
    }
}
