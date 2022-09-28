package com.example.qcaudioad.widgets;

import android.view.View;

/**
 * author ：yh
 * date : 2020-12-18 17:53
 * description :
 */
public interface OnViewPagerListener {

    //当前展示的View
    void onPageSelected (boolean isButten, View position);

    //当前被划出的view
    void onPageRelease (boolean isUp, View position);

}
