/*
 * Copyright 2018 The Android Open Source Project
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

package com.qichuang.core.versionedparcelable;

import com.qichuang.annotation.RestrictTo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.qichuang.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

/**
 * Tags implementations of {@link VersionedParcelable} for code generation.
 *
 * @hide
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@RestrictTo(LIBRARY_GROUP_PREFIX)
public @interface VersionedParcelize {

    boolean allowSerialization() default false;

    boolean ignoreParcelables() default false;

    boolean isCustom() default false;

    int[] deprecatedIds() default {};

    String jetifyAs() default "";

    Class factory() default void.class;
}
