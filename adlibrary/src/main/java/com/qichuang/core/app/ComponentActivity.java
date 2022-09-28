/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.qichuang.core.app;

import static com.qichuang.annotation.RestrictTo.Scope.LIBRARY_GROUP;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.qichuang.annotation.CallSuper;
import com.qichuang.annotation.Nullable;
import com.qichuang.annotation.RestrictTo;
import com.qichuang.core.collection.SimpleArrayMap;
import com.qichuang.core.view.KeyEventDispatcher;

/**
 * Base class for activities that enables composition of higher level components.
 * <p>
 * Rather than all functionality being built directly into this class, only the minimal set of
 * lower level building blocks are included. Higher level components can then be used as needed
 * without enforcing a deep Activity class hierarchy or strong coupling between components.
 *
 * @hide
 */
@RestrictTo(LIBRARY_GROUP)
public class ComponentActivity extends Activity
        implements KeyEventDispatcher.Component {
    /**
     * Storage for {@link ExtraData} instances.
     *
     * <p>Note that these objects are not retained across configuration changes</p>
     */
    private SimpleArrayMap<Class<? extends ExtraData>, ExtraData> mExtraDataMap =
            new SimpleArrayMap<>();

    /**
     * Store an instance of {@link ExtraData} for later retrieval by class name
     * via {@link #getExtraData}.
     *
     * <p>Note that these objects are not retained across configuration changes</p>
     *
     * @see #getExtraData
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    public void putExtraData(ExtraData extraData) {
        mExtraDataMap.put(extraData.getClass(), extraData);
    }

    @Override
    @SuppressWarnings("RestrictedApi")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @CallSuper
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Retrieves a previously set {@link ExtraData} by class name.
     *
     * @see #putExtraData
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    public <T extends ExtraData> T getExtraData(Class<T> extraDataClass) {
        return (T) mExtraDataMap.get(extraDataClass);
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    @Override
    public boolean superDispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        View decor = getWindow().getDecorView();
        if (decor != null && KeyEventDispatcher.dispatchBeforeHierarchy(decor, event)) {
            return true;
        }
        return super.dispatchKeyShortcutEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View decor = getWindow().getDecorView();
        if (decor != null && KeyEventDispatcher.dispatchBeforeHierarchy(decor, event)) {
            return true;
        }
        return KeyEventDispatcher.dispatchKeyEvent(this, decor, this, event);
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    public static class ExtraData {
    }
}
