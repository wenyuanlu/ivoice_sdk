package com.corpize.sdk.ivoice.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.corpize.sdk.ivoice.listener.IADViewLayoutInterfaces;

/**
 * author : xpSun
 * date : 2022/2/25
 * description :
 */
public abstract class CustomAdBaseView extends FrameLayout implements IADViewLayoutInterfaces {

    public CustomAdBaseView (@NonNull Context context) {
        super(context);
    }

    public CustomAdBaseView (@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomAdBaseView (@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
