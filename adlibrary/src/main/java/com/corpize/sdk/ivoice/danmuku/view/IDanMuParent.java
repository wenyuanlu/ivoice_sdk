package com.corpize.sdk.ivoice.danmuku.view;


import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;

import java.util.List;

/**
 * Created by android_ls on 2016/12/7.
 */
public interface IDanMuParent {
    void add (DanMuModel danMuView);

    void add (int index, DanMuModel danMuView);

    void jumpQueue (List<DanMuModel> danMuViews);

    void addAllTouchListener (List<DanMuModel> onDanMuTouchCallBackListeners);

    void clear ();

    void remove (DanMuModel danMuView);

    void lockDraw ();

    void forceSleep ();

    void forceWake ();

    boolean hasCanTouchDanMus ();

    void hideNormalDanMuView (boolean hide);

    void hideAllDanMuView (boolean hideAll);

    void pauseAllDanMuView ();

    void continueAllDanMuView ();

    void release ();

    void setChannelHeight (int avatarSize);
}
