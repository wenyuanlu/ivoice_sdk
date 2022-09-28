package com.corpize.sdk.ivoice.bean;

/**
 * @author Created by SXF on 2021/5/8 9:28 AM.
 * @description 用户播放音频信息
 */
public class UserPlayInfoBean {

    //标题
    private String  title;
    //音频地址
    private String  url;
    //音频描述
    private String  desc;
    //音频封面
    private String  image;
    //播放进度,0 - 100 ,100为完整播放
    private Integer progress;

    public UserPlayInfoBean () {

    }

    public UserPlayInfoBean (String title, String url, String desc, String image, Integer progress) {
        this.title = title;
        this.url = url;
        this.desc = desc;
        this.image = image;
        this.progress = progress;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public String getDesc () {
        return desc;
    }

    public void setDesc (String desc) {
        this.desc = desc;
    }

    public String getImage () {
        return image;
    }

    public void setImage (String image) {
        this.image = image;
    }

    public Integer getProgress () {
        return progress;
    }

    public void setProgress (Integer progress) {
        this.progress = progress;
    }
}
