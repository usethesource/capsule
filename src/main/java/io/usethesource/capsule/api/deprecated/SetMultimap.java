/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api.deprecated;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

public interface SetMultimap<K, V> {

  Set.Immutable<V> get(final java.lang.Object o);

  boolean containsKey(final Object o);

  boolean containsValue(final Object o);

  boolean containsEntry(final Object o0, final Object o1);

  java.util.Set<K> keySet();

  java.util.Collection<V> values();

  java.util.Set<Map.Entry<K, V>> entrySet();

  int size();

  default int sizeDistinct() {
    return (int) entrySet().stream().map(Entry::getKey).distinct().count();
  }

  boolean isEmpty();

  Iterator<K> keyIterator();

  Iterator<V> valueIterator();

  // TODO: Iterator<Map.Entry<K, Set<V>>> groupByKeyIterator();

  Iterator<Entry<K, V>> entryIterator();

  /**
   * Iterates over the raw internal structure. Optional operation.
   *
   * @return native iterator, if supported
   * @throws UnsupportedOperationException, if not supported
   */
  default Iterator<Entry<K, Object>> nativeEntryIterator() throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
  }

  <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf);

  /*
   * Uses semantic of Set<Map.Entry<K, V>> instead of Map<K, Set<V>>.
   */
  @Override
  int hashCode();

  @Override
  boolean equals(Object other);

  interface Immutable<K, V> extends SetMultimap<K, V> {

    SetMultimap.Immutable<K, V> __put(final K key, final V val);

    // TODO: SetMultimap.Immutable<K, V> __insert(final K key, final Set<V> values);

    SetMultimap.Immutable<K, V> __insert(final K key, final V val);

    SetMultimap.Immutable<K, V> __insertAll(
        final SetMultimap<? extends K, ? extends V> setMultimap);

    // removes all mappings with 'key'
    SetMultimap.Immutable<K, V> __remove(final K key);

    SetMultimap.Immutable<K, V> __removeEntry(final K key, final V val);

    boolean isTransientSupported();

    SetMultimap.Transient<K, V> asTransient();

  }

  interface Transient<K, V> extends SetMultimap<K, V> {

    default boolean __put(K key, io.usethesource.capsule.api.deprecated.Set.Immutable<V> valColl) {
      throw new UnsupportedOperationException("Not yet implemented @ Transient.");
    }

    boolean __insert(final K key, final V val);

    boolean __insertAll(final SetMultimap<? extends K, ? extends V> setMultimap);

    boolean __removeTuple(final K key, final V val);

    // TODO: return Immutable<V> or boolean?
    default boolean __remove(K key) {
      throw new UnsupportedOperationException("Not yet implemented @ Transient.");
    }

    SetMultimap.Immutable<K, V> freeze();

  }
}
