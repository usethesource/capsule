/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api.deprecated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

public interface SetMultimap<K, V> {

  V put(final K key, final V val);

  V remove(final java.lang.Object key, final java.lang.Object val);

  void putAll(final SetMultimap<? extends K, ? extends V> multimap);

  boolean containsValue(Object value);

  Set<V> get(final java.lang.Object o);

  // Set<V> getEquivalent(final java.lang.Object o, final Comparator<Object> cmp);

  Set<K> keySet();

  Collection<V> values();

  Set<Map.Entry<K, V>> entrySet();

  void clear();

  int size();

  default int sizeDistinct() {
    return (int) entrySet().stream().map(Entry::getKey).distinct().count();
  }

  boolean isEmpty();

  /*
   * Uses semantic of Set<Map.Entry<K, V>> instead of Map<K, Set<V>>.
   */
  @Override
  int hashCode();

  @Override
  boolean equals(Object other);

  interface Immutable<K, V> extends SetMultimap<K, V> {

    @Override
    io.usethesource.capsule.api.deprecated.Set.Immutable<V> get(final Object o);

    // Immutable<V> getEquivalent(final Object o, final Comparator<Object> cmp);

    boolean containsKey(final Object o);

    // boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp);

    @Override
    boolean containsValue(final Object o);

    // boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp);

    boolean containsEntry(final Object o0, final Object o1);

    // boolean containsEntryEquivalent(final Object o0, final Object o1, final Comparator<Object>
    // cmp);

    Immutable __put(final K key, final V val);

    // TODO: SetMultimap.Immutable<K, V> __insert(final K key, final Set<V> values);

    Immutable __insert(final K key, final V val);

    // SetMultimap.Immutable<K, V> __insertEquivalent(final K key, final V val,
    // final Comparator<Object> cmp);

    Immutable __insertAll(final SetMultimap<? extends K, ? extends V> setMultimap);

    // SetMultimap.Immutable<K, V> __insertAllEquivalent(
    // final SetMultimap<? extends K, ? extends V> setMultimap, final Comparator<Object> cmp);

    // removes all mappings with 'key'
    Immutable __remove(final K key);

    // // removes all mappings with 'key'
    // SetMultimap.Immutable<K, V> __removeEquivalent(final K key, final Comparator<Object> cmp);

    Immutable __removeEntry(final K key, final V val);

    // SetMultimap.Immutable<K, V> __removeEntryEquivalent(final K key, final V val,
    // final Comparator<Object> cmp);

    Iterator<K> keyIterator();

    Iterator<V> valueIterator();

    // TODO: Iterator<Map.Entry<K, Set<V>>> groupByKeyIterator();

    Iterator<Entry<K, V>> entryIterator();

    Iterator<Entry<K, Object>> nativeEntryIterator();

    <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf);

    boolean isTransientSupported();

    SetMultimap.Transient<K, V> asTransient();

  }

  interface Transient<K, V> extends SetMultimap<K, V> {

    @Override
    io.usethesource.capsule.api.deprecated.Set.Immutable<V> get(final Object o);

    // Immutable<V> getEquivalent(final Object o, final Comparator<Object> cmp);

    boolean containsKey(final Object o);

    // boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp);

    @Override
    boolean containsValue(final Object o);

    // boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp);

    boolean containsEntry(final Object o0, final Object o1);

    // boolean containsEntryEquivalent(final Object o0, final Object o1, final Comparator<Object>
    // cmp);

    default boolean __put(K key, io.usethesource.capsule.api.deprecated.Set.Immutable<V> valColl) {
      throw new UnsupportedOperationException("Not yet implemented @ Transient.");
    }

    boolean __insert(final K key, final V val);

    // boolean __insertEquivalent(final K key, final V val, final Comparator<Object> cmp);

    boolean __insertAll(final SetMultimap<? extends K, ? extends V> setMultimap);

    // boolean __insertAllEquivalent(final SetMultimap<? extends K, ? extends V> setMultimap,
    // final Comparator<Object> cmp);

    boolean __removeTuple(final K key, final V val);

    // boolean __removeTupleEquivalent(final K key, final V val, final Comparator<Object> cmp);

    // TODO: return Immutable<V> or boolean?
    default boolean __remove(K key) {
      throw new UnsupportedOperationException("Not yet implemented @ Transient.");
    }

    Iterator<K> keyIterator();

    Iterator<V> valueIterator();

    Iterator<Entry<K, V>> entryIterator();

    <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf);

    Immutable freeze();

  }
}
