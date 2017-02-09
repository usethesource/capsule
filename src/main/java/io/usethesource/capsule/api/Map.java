/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api;

import java.util.Iterator;

public interface Map<K, V> extends java.util.Map<K, V>, MapEq<K, V> {

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

  interface Immutable<K, V> extends Map<K, V>, MapEq.Immutable<K, V> {

    Map.Immutable<K, V> __put(final K key, final V val);

    Map.Immutable<K, V> __remove(final K key);

    Map.Immutable<K, V> __putAll(final java.util.Map<? extends K, ? extends V> map);

    boolean isTransientSupported();

    Map.Transient<K, V> asTransient();

  }

  interface Transient<K, V> extends Map<K, V>, MapEq.Transient<K, V> {

    V __put(final K key, final V val);

    V __remove(final K key);

    boolean __putAll(final java.util.Map<? extends K, ? extends V> map);

    Map.Immutable<K, V> freeze();

  }
}
