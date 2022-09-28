package com.corpize.sdk.ivoice.danmuku.control.dispatcher;

import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;
import com.corpize.sdk.ivoice.danmuku.model.channel.DanMuChannel;

/**
 * Created by android_ls on 2016/12/7.
 */
public interface IDanMuDispatcher {

    void dispatch (DanMuModel iDanMuView, DanMuChannel[] danMuChannels);

}
