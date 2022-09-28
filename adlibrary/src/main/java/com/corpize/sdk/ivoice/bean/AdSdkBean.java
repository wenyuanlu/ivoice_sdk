package com.corpize.sdk.ivoice.bean;

/**
 * author: yh
 * date: 2020-02-17 22:01
 * description: TODO:
 */
public class AdSdkBean {

    /**
     * oceanengine : {"appid":"5049549"}
     * qq : {"appid":"1108817031"}
     * baidu : {"appid":"e4f0333c"}
     */

    private OceanengineBean oceanengine;
    private QqBean    qq;
    private BaiduBean baidu;

    public OceanengineBean getOceanengine () {
        return oceanengine;
    }

    public void setOceanengine (OceanengineBean oceanengine) {
        this.oceanengine = oceanengine;
    }

    public QqBean getQq () {
        return qq;
    }

    public void setQq (QqBean qq) {
        this.qq = qq;
    }

    public BaiduBean getBaidu () {
        return baidu;
    }

    public void setBaidu (BaiduBean baidu) {
        this.baidu = baidu;
    }

    public static class OceanengineBean {
        /**
         * appid : 5049549
         */

        private String appid;

        public String getAppid () {
            return appid;
        }

        public void setAppid (String appid) {
            this.appid = appid;
        }
    }

    public static class QqBean {
        /**
         * appid : 1108817031
         */

        private String appid;

        public String getAppid () {
            return appid;
        }

        public void setAppid (String appid) {
            this.appid = appid;
        }
    }

    public static class BaiduBean {
        /**
         * appid : e4f0333c
         */

        private String appid;

        public String getAppid () {
            return appid;
        }

        public void setAppid (String appid) {
            this.appid = appid;
        }
    }
}
