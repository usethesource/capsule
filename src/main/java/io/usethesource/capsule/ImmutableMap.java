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

public interface ImmutableMap<K, V> extends Map<K, V> {

  @Override
  public V get(final Object o);

  public V getEquivalent(final Object o, final Comparator<Object> cmp);

  @Override
  public boolean containsKey(final Object o);

  public boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp);

  @Override
  public boolean containsValue(final Object o);

  public boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp);

  public ImmutableMap<K, V> __put(final K key, final V val);

  public ImmutableMap<K, V> __putEquivalent(final K key, final V val, final Comparator<Object> cmp);

  public ImmutableMap<K, V> __putAll(final Map<? extends K, ? extends V> map);

  public ImmutableMap<K, V> __putAllEquivalent(final Map<? extends K, ? extends V> map,
      final Comparator<Object> cmp);

  public ImmutableMap<K, V> __remove(final K key);

  public ImmutableMap<K, V> __removeEquivalent(final K key, final Comparator<Object> cmp);

  public Iterator<K> keyIterator();

  public Iterator<V> valueIterator();

  public Iterator<Map.Entry<K, V>> entryIterator();

  public boolean isTransientSupported();

  public TransientMap<K, V> asTransient();

}
