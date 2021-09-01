/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Iterator;

import io.usethesource.capsule.core.PersistentTrieMap;

public interface Map<K, V> extends java.util.Map<K, V> {

  @Override
  int size();

  @Override
  boolean isEmpty();

  @Override
  boolean containsKey(final Object o);

  @Override
  boolean containsValue(final Object o);

  @Override
  V get(final Object o);

  Iterator<K> keyIterator();

  Iterator<V> valueIterator();

  Iterator<Entry<K, V>> entryIterator();

  @Override
  boolean equals(Object o);

  @Override
  int hashCode();

  interface Immutable<K, V> extends Map<K, V> {

    Map.Immutable<K, V> __put(final K key, final V val);

    Map.Immutable<K, V> __remove(final K key);

    Map.Immutable<K, V> __putAll(final java.util.Map<? extends K, ? extends V> map);

    boolean isTransientSupported();

    Map.Transient<K, V> asTransient();

    static <K, V> Map.Immutable<K, V> of() {
      return PersistentTrieMap.of();
    }

    static <K, V> Map.Immutable<K, V> of(K key, V value) {
      return PersistentTrieMap.of(key, value);
    }

    static <K, V> Map.Immutable<K, V> of(K key0, V value0, K key1, V value1) {
      return PersistentTrieMap.of(key0, value0, key1, value1);
    }

  }

  interface Transient<K, V> extends Map<K, V> {

    V __put(final K key, final V val);

    V __remove(final K key);

    boolean __putAll(final java.util.Map<? extends K, ? extends V> map);

//    default boolean union(final Map<? extends K, ? extends V> map) {
//      boolean modified = false;
//
//      for (java.util.Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
//        // NOTE: does only work when map does not support `null` values
//        if (this.__put(entry.getKey(), entry.getValue()) != null) {
//          modified |= true;
//        }
//      }
//
//      return modified;
//    }
//
//    default boolean intersect(final Map<? extends K, ? extends V> map) {
//      throw new UnsupportedOperationException("Not yet implemented @ Map.");
//    }
//
//    default boolean complement(final Map<? extends K, ? extends V> map) {
//      throw new UnsupportedOperationException("Not yet implemented @ Map");
//    }

    Map.Immutable<K, V> freeze();

    static <K, V> Map.Transient<K, V> of() {
      return PersistentTrieMap.transientOf();
    }

    static <K, V> Map.Transient<K, V> of(K key0, V value0) {
      final Map.Transient<K, V> tmp = Map.Transient.of();

      tmp.__put(key0, value0);

      return tmp;
    }

    static <K, V> Map.Transient<K, V> of(K key0, V value0, K key1, V value1) {
      final Map.Transient<K, V> tmp = Map.Transient.of();

      tmp.__put(key0, value0);
      tmp.__put(key1, value1);

      return tmp;
    }

    static <K, V> Map.Transient<K, V> of(K key0, V value0, K key1, V value1, K key2,
        V value2) {
      final Map.Transient<K, V> tmp = Map.Transient.of();

      tmp.__put(key0, value0);
      tmp.__put(key1, value1);
      tmp.__put(key2, value2);

      return tmp;
    }

    static <K, V> Map.Transient<K, V> of(K key0, V value0, K key1, V value1, K key2,
        V value2, K key3, V value3) {
      final Map.Transient<K, V> tmp = Map.Transient.of();

      tmp.__put(key0, value0);
      tmp.__put(key1, value1);
      tmp.__put(key2, value2);
      tmp.__put(key3, value3);

      return tmp;
    }

    static <K, V> Map.Transient<K, V> of(K key0, V value0, K key1, V value1, K key2,
        V value2, K key3, V value3, K key4, V value4) {
      final Map.Transient<K, V> tmp = Map.Transient.of();

      tmp.__put(key0, value0);
      tmp.__put(key1, value1);
      tmp.__put(key2, value2);
      tmp.__put(key3, value3);
      tmp.__put(key4, value4);

      return tmp;
    }

    static <K, V> Map.Transient<K, V> of(K key0, V value0, K key1, V value1, K key2,
        V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
      final Map.Transient<K, V> tmp = Map.Transient.of();

      tmp.__put(key0, value0);
      tmp.__put(key1, value1);
      tmp.__put(key2, value2);
      tmp.__put(key3, value3);
      tmp.__put(key4, value4);
      tmp.__put(key5, value5);

      return tmp;
    }

  }

}
