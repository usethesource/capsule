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
    extends Iterable<Map.Entry<K, V>>, Function<K, Optional<Set<V>>> {

  long size();
  
  boolean isEmpty();
  
  boolean containsKey(final Object o);

  boolean containsValue(final Object o);

  boolean containsEntry(final Object o0, final Object o1);

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
  // Iterator<Map.Entry<K, Object>> nativeEntryIterator();
  //
  // <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf);

  // default int sizeDistinct() {
  // return (int) entrySet().stream().map(Entry::getKey).distinct().count();
  // }
  //
  // boolean isEmpty();
  
  @Override
  Iterator<Map.Entry<K, V>> iterator();
  
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

  public static interface Immutable<K, V> extends SetMultimap<K, V> {

    SetMultimap.Immutable<K, V> put(final K key, final V val);

    SetMultimap.Immutable<K, V> put(final K key, final Set<V> values);

    SetMultimap.Immutable<K, V> insert(final K key, final V val);

    SetMultimap.Immutable<K, V> insert(final K key, final Set<V> values);

    SetMultimap.Immutable<K, V> remove(final K key, final V val);
    
    SetMultimap.Immutable<K, V> remove(final K key);
    
    SetMultimap.Immutable<K, V> insertAll(final SetMultimap<? extends K, ? extends V> setMultimap);

    SetMultimap.Immutable<K, V> removeAll(final SetMultimap<? extends K, ? extends V> setMultimap);

    SetMultimap.Immutable<K, V> retainAll(final SetMultimap<? extends K, ? extends V> setMultimap);

    boolean isTransientSupported();

    SetMultimap.Transient<K, V> asTransient();

  }

  public static interface Transient<K, V> extends SetMultimap<K, V> {

    Set<V> put(final K key, final V val);

    Set<V> put(final K key, final Set<V> values);

    boolean insert(final K key, final V val);

    boolean insert(final K key, final Set<V> values);

    boolean remove(final K key, final V val);

    boolean remove(final K key);

    boolean insertAll(final SetMultimap<? extends K, ? extends V> setMultimap);

    boolean removeAll(final SetMultimap<? extends K, ? extends V> setMultimap);

    boolean retainAll(final SetMultimap<? extends K, ? extends V> setMultimap);

    SetMultimap.Immutable<K, V> asImmutable();

  }

}
