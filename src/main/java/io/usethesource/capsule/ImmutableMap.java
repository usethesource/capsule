/*******************************************************************************
 * Copyright (c) 2013-2015 CWI All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 * * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
 *******************************************************************************/
package io.usethesource.capsule;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

public interface ImmutableMap<K, V> extends Map<K, V> {

  @Override
  V get(final Object o);

  V getEquivalent(final Object o, final Comparator<Object> cmp);

  @Override
  boolean containsKey(final Object o);

  boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp);

  @Override
  boolean containsValue(final Object o);

  boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp);

  ImmutableMap<K, V> __put(final K key, final V val);

  ImmutableMap<K, V> __putEquivalent(final K key, final V val, final Comparator<Object> cmp);

  ImmutableMap<K, V> __putAll(final Map<? extends K, ? extends V> map);

  ImmutableMap<K, V> __putAllEquivalent(final Map<? extends K, ? extends V> map,
                                        final Comparator<Object> cmp);

  ImmutableMap<K, V> __remove(final K key);

  ImmutableMap<K, V> __removeEquivalent(final K key, final Comparator<Object> cmp);

  Iterator<K> keyIterator();

  Iterator<V> valueIterator();

  Iterator<Map.Entry<K, V>> entryIterator();

  boolean isTransientSupported();

  TransientMap<K, V> asTransient();

}
