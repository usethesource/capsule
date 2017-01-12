/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api.deprecated;

import java.util.Collection;
import java.util.Comparator;

/**
 * Set extension providing methods that take a comparator. Closes over base (and not extended) set.
 */
@Deprecated
public interface SetEq<K> extends java.util.Set<K> {

  default boolean containsAllEquivalent(final Collection<?> c, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Set.");
  }

  default K getEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Set.");
  }

  default boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Set.");
  }

  @Deprecated
  interface Immutable<K> extends Set<K> {

    default Set.Immutable<K> __insertEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default Set.Immutable<K> __removeEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default Set.Immutable<K> __insertAllEquivalent(final java.util.Set<? extends K> set,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default Set.Immutable<K> __removeAllEquivalent(final java.util.Set<? extends K> set,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default Set.Immutable<K> __retainAllEquivalent(final Set.Transient<? extends K> transientSet,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

  }

  @Deprecated
  interface Transient<K> extends Set<K> {

    default boolean __insertEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default boolean __removeEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default boolean __insertAllEquivalent(final java.util.Set<? extends K> set,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default boolean __removeAllEquivalent(final java.util.Set<? extends K> set,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default boolean __retainAllEquivalent(final Set.Transient<? extends K> transientSet,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

  }
}
