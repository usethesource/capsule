/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Comparator;

/**
 * Map extension providing methods that take a comparator. Closes over base (and not extended) map.
 */
@Deprecated
public interface MapEq<K, V> extends java.util.Map<K, V> {

  default boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Map.");
  }

  default boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Map.");
  }

  default V getEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Map.");
  }

  @Deprecated
  interface Immutable<K, V> extends MapEq<K, V> {

    default Map.Immutable<K, V> __putEquivalent(final K key, final V val,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Map.");
    }

    default Map.Immutable<K, V> __removeEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Map.");
    }

    default Map.Immutable<K, V> __putAllEquivalent(
        final java.util.Map<? extends K, ? extends V> map, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Map.");
    }

  }

  @Deprecated
  interface Transient<K, V> extends MapEq<K, V> {

    default V __putEquivalent(final K key, final V val, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Map.");
    }

    default V __removeEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Map.");
    }

    default boolean __putAllEquivalent(final java.util.Map<? extends K, ? extends V> map,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Map.");
    }

  }
}
