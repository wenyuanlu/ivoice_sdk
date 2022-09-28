package com.corpize.sdk.ivoice.bean;

/**
 * @author Created by SXF on 2021/5/12 1:39 PM.
 * @description  上传音频文件返回实体类
 */
public class UpVoiceResultBean {
    private int code;// 1:肯定，0:否定，999: 无法识别
    private String id;
    public static final int SUCCESS = 1;
    public static final int FAIl    = 0;
    public static final int UN_KNOW  = 999;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
