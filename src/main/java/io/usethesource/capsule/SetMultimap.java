/*******************************************************************************
 * Copyright (c) 2015 CWI All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 * * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
 *******************************************************************************/
package io.usethesource.capsule;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface SetMultimap<K, V>
    extends Iterable<Map.Entry<K, V>>, Function<K, Optional<Set.Immutable<V>>> {

  long size();

  boolean isEmpty();

  boolean contains(final K key);

  boolean contains(final K key, V val);

  // boolean containsKey(final Object o);
  //
  // boolean containsValue(final Object o);
  //
  // boolean containsEntry(final Object o0, final Object o1);

  // Set<V> get(final java.lang.Object o);

  // Set<K> keySet();
  //
  // Collection<V> values();
  //
  // java.util.Set<Map.Entry<K, V>> entrySet();
  //
  // Iterator<K> keyIterator();
  //
  // Iterator<V> valueIterator();

  // TODO: Iterator<Map.Entry<K, Set<V>>> groupByKeyIterator();

  // Iterator<Map.Entry<K, V>> entryIterator();
  //
  // <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf);

  // default int sizeDistinct() {
  // return (int) entrySet().stream().map(Entry::getKey).distinct().count();
  // }
  //
  // boolean isEmpty();

  @Override
  Iterator<Map.Entry<K, V>> iterator();

  Iterator<Map.Entry<K, Object>> nativeEntryIterator();

  /**
   * The hash code of a multimap is order independent by combining the hashes of the elements (both
   * keys and values) via a bitwise xor operation.
   * 
   * @return xor reduction of all hashes of elements
   */
  @Override
  int hashCode();

  @Override
  boolean equals(Object other);

  SetMultimap.Immutable<K, V> asImmutable();

  public static interface Immutable<K, V> extends SetMultimap<K, V> {

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

  public static interface Transient<K, V> extends SetMultimap<K, V> {

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
