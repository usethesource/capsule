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
 * This interface extends multi-maps for usage with custom data element comparators.
 */
@Deprecated
public interface SetMultimapEq<K, V> extends SetMultimap<K, V> {

  default boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
  }

  default boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
  }

  default boolean containsEntryEquivalent(final Object o0, final Object o1,
      final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
  }

  default Set.Immutable<V> getEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
  }

  @Deprecated
  interface Immutable<K, V> extends SetMultimapEq<K, V> {

    default SetMultimap.Immutable<K, V> __putEquivalent(final K key, final V value,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default SetMultimap.Immutable<K, V> __putEquivalent(final K key, final Set.Immutable<V> values,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default SetMultimap.Immutable<K, V> __insertEquivalent(final K key, final V value,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default SetMultimap.Immutable<K, V> __insertEquivalent(final K key,
        final Set.Immutable<V> values, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default SetMultimap.Immutable<K, V> __removeEquivalent(final K key,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default SetMultimap.Immutable<K, V> __removeEquivalent(final K key, final V val,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default SetMultimap.Immutable<K, V> unionEquivalent(
        final SetMultimap<? extends K, ? extends V> setMultimap, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default SetMultimap.Immutable<K, V> intersectEquivalent(
        final SetMultimap<? extends K, ? extends V> setMultimap, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default SetMultimap.Immutable<K, V> complementEquivalent(
        final SetMultimap<? extends K, ? extends V> setMultimap, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

  }

  @Deprecated
  interface Transient<K, V> extends SetMultimapEq<K, V> {

    default boolean __putEquivalent(final K key, final V value, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean __putEquivalent(final K key, final Set.Immutable<V> values,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean __insertEquivalent(final K key, final V value, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean __insertEquivalent(final K key, final Set.Immutable<V> values,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean __removeEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean __removeEquivalent(final K key, final V val, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean unionEquivalent(final SetMultimap<? extends K, ? extends V> setMultimap,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean intersectEquivalent(final SetMultimap<? extends K, ? extends V> setMultimap,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean complementEquivalent(final SetMultimap<? extends K, ? extends V> setMultimap,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

  }
}
