/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util;

import java.util.Objects;

@FunctionalInterface
public interface EqualityComparator<T> {

  boolean equals(T o1, T o2);

  final EqualityComparator<Object> EQUALS = (a, b) -> Objects.equals(a, b);

}
