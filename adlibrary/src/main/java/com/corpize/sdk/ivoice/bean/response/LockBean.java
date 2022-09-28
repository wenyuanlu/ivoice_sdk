package com.corpize.sdk.ivoice.bean.response;

import java.io.Serializable;

/**
 * author : xpSun
 * date : 1/5/22
 * description :
 */
public class LockBean implements Serializable {

    private BackendBean backend;
    private ReceptionBean reception;

    public BackendBean getBackend () {
        return backend;
    }

    public void setBackend (BackendBean backend) {
        this.backend = backend;
    }

    public ReceptionBean getReception () {
        return reception;
    }

    public void setReception (ReceptionBean reception) {
        this.reception = reception;
    }
}
