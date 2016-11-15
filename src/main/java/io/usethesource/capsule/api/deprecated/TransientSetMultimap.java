/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api.deprecated;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;

public interface TransientSetMultimap<K, V> extends SetMultimap<K, V> {

  ImmutableSet<V> get(final Object o);

  ImmutableSet<V> getEquivalent(final Object o, final Comparator<Object> cmp);

  boolean containsKey(final Object o);

  boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp);

  boolean containsValue(final Object o);

  boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp);

  boolean containsEntry(final Object o0, final Object o1);

  boolean containsEntryEquivalent(final Object o0, final Object o1, final Comparator<Object> cmp);

  default boolean __put(K key, ImmutableSet<V> valColl) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  boolean __insert(final K key, final V val);

  boolean __insertEquivalent(final K key, final V val, final Comparator<Object> cmp);

  boolean __insertAll(final SetMultimap<? extends K, ? extends V> setMultimap);

  boolean __insertAllEquivalent(final SetMultimap<? extends K, ? extends V> setMultimap,
      final Comparator<Object> cmp);

  boolean __removeTuple(final K key, final V val);

  boolean __removeTupleEquivalent(final K key, final V val, final Comparator<Object> cmp);

  Iterator<K> keyIterator();

  Iterator<V> valueIterator();

  Iterator<Map.Entry<K, V>> entryIterator();

  <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf);

  ImmutableSetMultimap<K, V> freeze();

}
