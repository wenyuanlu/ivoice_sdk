package com.example.qcaudioad.utils;

import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * author : xpSun
 * date : 12/14/21
 * description :
 */
public class CommonLabelUtils {

    //按照此方式创建实体类。需填入播放过的音频信息
    // "title":"1",音频的主要信息，如歌曲名,非必填
    // "url": "a", 音频地址，如歌曲的文件地址,非必填
    // "desc": "b", 音频描述,非必填
    // "image": "c", 音频封面,非必填
    // "progress": 50,播放进度，100位完整播放,非必填
    public static List<UserPlayInfoBean> getCommonLabels () {
        List<UserPlayInfoBean> userPlayInfoBeanDataList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            UserPlayInfoBean userPlayInfoBean = new UserPlayInfoBean();
            userPlayInfoBean.setTitle(String.format("title:%s", i));
            userPlayInfoBean.setUrl(String.format("url:%s", i));
            userPlayInfoBean.setDesc(String.format("desc:%s", i));
            userPlayInfoBean.setImage(String.format("image:%s", i));
            userPlayInfoBean.setProgress(i);
            userPlayInfoBeanDataList.add(userPlayInfoBean);
        }
        return userPlayInfoBeanDataList;
    }
}
