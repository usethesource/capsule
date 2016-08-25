/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public interface SetMultimap_Legacy<K, V> {

  V put(final K key, final V val);

  V remove(final java.lang.Object key, final java.lang.Object val);

  void putAll(final SetMultimap_Legacy<? extends K, ? extends V> multimap);

  boolean containsValue(Object value);

  Set<V> get(final java.lang.Object o);

  Set<V> getEquivalent(final java.lang.Object o, final Comparator<Object> cmp);

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

}
