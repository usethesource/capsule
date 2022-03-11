/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Indicates an experimental API that change at any time, without any stability or backwards compatibility guarantees.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(value = {ANNOTATION_TYPE, CONSTRUCTOR, FIELD, METHOD, PACKAGE, TYPE})
@Documented
public @interface Experimental {
}
