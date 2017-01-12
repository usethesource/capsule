/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api.deprecated;

import java.util.Comparator;

/**
 * This interface extends multi-maps for usage with custom data element comparators.
 *
 * NOTE: Currently not synchronized with {@link SetMultimap}.
 */
@Deprecated
public interface SetMultimapEq<K, V> extends SetMultimap<K, V> {

  default Set.Immutable<V> getEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
  }

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

  @Deprecated
  interface Immutable<K, V> extends SetMultimap.Transient<K, V> {

    default SetMultimap.Immutable<K, V> __insertEquivalent(final K key, final V val,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default SetMultimap.Immutable<K, V> __insertAllEquivalent(
        final SetMultimap<? extends K, ? extends V> setMultimap, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    // removes all mappings with 'key'
    default SetMultimap.Immutable<K, V> __removeEquivalent(final K key,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default SetMultimap.Immutable<K, V> __removeEntryEquivalent(final K key, final V val,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

  }

  @Deprecated
  interface Transient<K, V> extends SetMultimap.Transient<K, V> {

    default boolean __insertEquivalent(final K key, final V val, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean __insertAllEquivalent(final SetMultimap<? extends K, ? extends V> setMultimap,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean __removeTupleEquivalent(final K key, final V val,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

  }
}
