/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util;

import java.util.Comparator;
import java.util.Objects;

import io.usethesource.capsule.util.function.ToBooleanBiFunction;

/*
 * TODO: remove {@link java.io.Serializable} capability after removing comparator from
 * multi-map base classes.
 */
@FunctionalInterface
public interface EqualityComparator<T> extends java.io.Serializable {

  static <T> EqualityComparator<T> fromComparator(Comparator<T> comparator) {
    return (a, b) -> comparator.compare(a, b) == 0;
  }

  static <T> boolean equals(T a, T b,
      ToBooleanBiFunction<T, T> comparator) {
    return (a == b) || (a != null && comparator.applyAsBoolean(a, b));
  }

  @Deprecated // substitute with Object::equals
  EqualityComparator<Object> EQUALS = (a, b) -> Objects.equals(a, b);

  boolean equals(T o1, T o2);

  @Deprecated // limit use of Comparator interface (prefer EqualityComparator)
  default Comparator<T> toComparator() {
    return ((o1, o2) -> equals(o1, o2) == true ? 0 : -1);
  }

}
