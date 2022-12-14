/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qichuang.core.graphics;

import static com.qichuang.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

import android.graphics.Rect;

import com.qichuang.annotation.NonNull;
import com.qichuang.annotation.RequiresApi;
import com.qichuang.annotation.RestrictTo;

/**
 * An Insets instance holds four integer offsets which describe changes to the four
 * edges of a Rectangle. By convention, positive values move edges towards the
 * centre of the rectangle.
 * <p>
 * Insets are immutable so may be treated as values.
 */
public final class Insets {
    @NonNull
    public static final Insets NONE = new Insets(0, 0, 0, 0);

    public final int left;
    public final int top;
    public final int right;
    public final int bottom;

    private Insets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    // Factory methods

    /**
     * Return an Insets instance with the appropriate values.
     *
     * @param left   the left inset
     * @param top    the top inset
     * @param right  the right inset
     * @param bottom the bottom inset
     * @return Insets instance with the appropriate values
     */
    @NonNull
    public static Insets of(int left, int top, int right, int bottom) {
        if (left == 0 && top == 0 && right == 0 && bottom == 0) {
            return NONE;
        }
        return new Insets(left, top, right, bottom);
    }

    /**
     * Return an Insets instance with the appropriate values.
     *
     * @param r the rectangle from which to take the values
     * @return an Insets instance with the appropriate values
     */
    @NonNull
    public static Insets of(@NonNull Rect r) {
        return of(r.left, r.top, r.right, r.bottom);
    }

    /**
     * Two Insets instances are equal iff they belong to the same class and their fields are
     * pairwise equal.
     *
     * @param o the object to compare this instance with.
     * @return true iff this object is equal {@code o}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Insets insets = (Insets) o;

        if (bottom != insets.bottom) return false;
        if (left != insets.left) return false;
        if (right != insets.right) return false;
        if (top != insets.top) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + top;
        result = 31 * result + right;
        result = 31 * result + bottom;
        return result;
    }

    @Override
    public String toString() {
        return "Insets{left=" + left + ", top=" + top
                + ", right=" + right + ", bottom=" + bottom + '}';
    }

    /**
     * @deprecated Use {@link #toCompatInsets(android.graphics.Insets)} instead.
     * @hide
     */
    @RequiresApi(api = 29)
    @NonNull
    @Deprecated
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    public static Insets wrap(@NonNull android.graphics.Insets insets) {
        return toCompatInsets(insets);
    }

    /**
     * Return a copy of the given {@link android.graphics.Insets} instance, converted to be an
     * {@link Insets} instance from com.qichuang.
     */
    @RequiresApi(api = 29)
    @NonNull
    public static Insets toCompatInsets(@NonNull android.graphics.Insets insets) {
        return Insets.of(insets.left, insets.top, insets.right, insets.bottom);
    }

    /**
     * Return a copy this instance, converted to be an {@link android.graphics.Insets} instance
     * from the platform.
     */
    @RequiresApi(api = 29)
    @NonNull
    public android.graphics.Insets toPlatformInsets() {
        return android.graphics.Insets.of(left, top, right, bottom);
    }
}
