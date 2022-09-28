package com.corpize.sdk.ivoice.bean;

import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * author ：yh
 * date : 2020-02-11 22:18
 * description : 广告请求返回数据
 */
public class AdResponseBean implements Serializable {

    private int    status;
    private AdmBean adm;

    public int getStatus () {
        return status;
    }

    public void setStatus (int status) {
        this.status = status;
    }

    public AdmBean getAdm() {
        return adm;
    }

    public void setAdm(AdmBean adm) {
        this.adm = adm;
    }

    public static class AdmBean implements Serializable{
        private AdAudioBean normal;
        private AdAudioBean chime;

        public AdAudioBean getNormal() {
            return normal;
        }

        public void setNormal(AdAudioBean normal) {
            this.normal = normal;
        }

        public AdAudioBean getChime() {
            return chime;
        }

        public void setChime(AdAudioBean chime) {
            this.chime = chime;
        }
    }
}
