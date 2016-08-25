/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;

public interface ImmutableSetMultimap<K, V> extends SetMultimap_Legacy<K, V> {

  ImmutableSet<V> get(final Object o);

  ImmutableSet<V> getEquivalent(final Object o, final Comparator<Object> cmp);

  boolean containsKey(final Object o);

  boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp);

  boolean containsValue(final Object o);

  boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp);

  boolean containsEntry(final Object o0, final Object o1);

  boolean containsEntryEquivalent(final Object o0, final Object o1, final Comparator<Object> cmp);

  

  
  
  ImmutableSetMultimap<K, V> __put(final K key, final V val);
  
  
  
  
  
  // TODO: ImmutableSetMultimap<K, V> __insert(final K key, final Set<V> values);

  ImmutableSetMultimap<K, V> __insert(final K key, final V val);

  ImmutableSetMultimap<K, V> __insertEquivalent(final K key, final V val,
      final Comparator<Object> cmp);

  ImmutableSetMultimap<K, V> __insertAll(final SetMultimap_Legacy<? extends K, ? extends V> setMultimap);

  ImmutableSetMultimap<K, V> __insertAllEquivalent(
      final SetMultimap_Legacy<? extends K, ? extends V> setMultimap, final Comparator<Object> cmp);

  
  
  
  
  // removes all mappings with 'key'
  ImmutableSetMultimap<K, V> __remove(final K key);

  // removes all mappings with 'key'
  ImmutableSetMultimap<K, V> __removeEquivalent(final K key, final Comparator<Object> cmp);
  
  ImmutableSetMultimap<K, V> __removeEntry(final K key, final V val);

  ImmutableSetMultimap<K, V> __removeEntryEquivalent(final K key, final V val,
      final Comparator<Object> cmp);

  
  
  
  
  Iterator<K> keyIterator();

  Iterator<V> valueIterator();

  // TODO: Iterator<Map.Entry<K, Set<V>>> groupByKeyIterator();

  Iterator<Map.Entry<K, V>> entryIterator();
  
  Iterator<Map.Entry<K, Object>> nativeEntryIterator();

  <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf);

  boolean isTransientSupported();

  TransientSetMultimap<K, V> asTransient();

}
