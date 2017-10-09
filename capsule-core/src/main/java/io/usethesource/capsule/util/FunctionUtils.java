/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util;

import java.util.function.Function;
import java.util.function.Predicate;

public class FunctionUtils {

  @SuppressWarnings("unchecked")
  public static <T, R> Function<T, R> asInstanceOf(Class<R> resultClass) {
    return item -> (R) item;
  }

  public static <T> Predicate<T> isInstanceOf(Class<T> inputClass) {
    return item -> inputClass.isInstance(item);
  }

}
