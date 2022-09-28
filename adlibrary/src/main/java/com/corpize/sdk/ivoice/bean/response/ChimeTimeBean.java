package com.corpize.sdk.ivoice.bean.response;

import java.io.Serializable;

public class ChimeTimeBean implements Serializable {

    private long s;
    private long e;

    public long getS() {
        return s;
    }

    public void setS(long s) {
        this.s = s;
    }

    public long getE() {
        return e;
    }

    public void setE(long e) {
        this.e = e;
    }
}