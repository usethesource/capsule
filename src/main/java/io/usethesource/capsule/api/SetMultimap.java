/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/*
 * TODO: remove dependency of java.util.Map
 */
public interface SetMultimap<K, V>
    extends Iterable<Map.Entry<K, V>>, Function<K, Optional<Set.Immutable<V>>> {

  long size();

  // default int sizeDistinct() {
  // return (int) entrySet().stream().map(Entry::getKey).distinct().count();
  // }

  boolean isEmpty();

  boolean contains(final K key);

  boolean contains(final K key, V val);

  // boolean containsValue(final Object o);

  @Override
  Iterator<Map.Entry<K, V>> iterator();

  Iterator<Map.Entry<K, Object>> nativeEntryIterator();

  /**
   * The hash code of a mult-imap is order independent by combining the hashes of the elements (both
   * keys and values) via a bitwise XOR operation.
   * 
   * @return XOR reduction of all hashes of elements
   */
  @Override
  int hashCode();

  @Override
  boolean equals(Object other);

  SetMultimap.Immutable<K, V> asImmutable();

  interface Immutable<K, V> extends SetMultimap<K, V> {

    SetMultimap.Immutable<K, V> put(final K key, final V val);

    SetMultimap.Immutable<K, V> put(final K key, final Set<V> values);

    SetMultimap.Immutable<K, V> insert(final K key, final V val);

    SetMultimap.Immutable<K, V> insert(final K key, final Set<V> values);

    SetMultimap.Immutable<K, V> remove(final K key, final V val);

    SetMultimap.Immutable<K, V> remove(final K key);

    SetMultimap.Immutable<K, V> union(final SetMultimap<? extends K, ? extends V> setMultimap);

    SetMultimap.Immutable<K, V> intersect(final SetMultimap<? extends K, ? extends V> setMultimap);

    SetMultimap.Immutable<K, V> complement(final SetMultimap<? extends K, ? extends V> setMultimap);

    boolean isTransientSupported();

    SetMultimap.Transient<K, V> asTransient();

  }

  interface Transient<K, V> extends SetMultimap<K, V> {

    boolean put(final K key, final V val); // TODO: return Set<V> instead of boolean?

    boolean put(final K key, final Set<V> values); // TODO: return Set<V> instead of boolean?

    boolean insert(final K key, final V val);

    boolean insert(final K key, final Set<V> values);

    boolean remove(final K key, final V val);

    boolean remove(final K key);

    boolean union(final SetMultimap<? extends K, ? extends V> setMultimap);

    boolean intersect(final SetMultimap<? extends K, ? extends V> setMultimap);

    boolean complement(final SetMultimap<? extends K, ? extends V> setMultimap);

  }

}
