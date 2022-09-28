package com.qichuang.core.view;

import android.view.View;

public interface OnApplyWindowInsetsListener {
    /**
     * When {@link ViewCompat#setOnApplyWindowInsetsListener(View, OnApplyWindowInsetsListener) set}
     * on a View, this listener method will be called instead of the view's own
     * {@code onApplyWindowInsets} method.
     *
     * @param v The view applying window insets
     * @param insets The insets to apply
     * @return The insets supplied, minus any insets that were consumed
     */
    WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets);
}