/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import io.usethesource.capsule.util.EqualityComparator;

public interface MapNode<K, V, R extends MapNode<K, V, R>> extends Node {

  default boolean containsKey(final K key, final int keyHash, final int shift) {
    return containsKey(key, keyHash, shift, Object::equals);
  }

  boolean containsKey(final K key, final int keyHash, final int shift,
      final EqualityComparator<Object> cmp);

  default Optional<V> findByKey(final K key, final int keyHash, final int shift) {
    return findByKey(key, keyHash, shift, Object::equals);
  }

  Optional<V> findByKey(final K key, final int keyHash, final int shift,
      final EqualityComparator<Object> cmp);

  default R updated(final AtomicReference<Thread> mutator, final K key,
      final V val, final int keyHash, final int shift, final MapNodeResult<K, V> details) {
    return updated(mutator, key, val, keyHash, shift, details, Object::equals);
  }

  R updated(final AtomicReference<Thread> mutator, final K key,
      final V val, final int keyHash, final int shift, final MapNodeResult<K, V> details,
      final EqualityComparator<Object> cmp);

  default R removed(final AtomicReference<Thread> mutator, final K key,
      final int keyHash, final int shift, final MapNodeResult<K, V> details) {
    return removed(mutator, key, keyHash, shift, details, Object::equals);
  }

  R removed(final AtomicReference<Thread> mutator, final K key,
      final int keyHash, final int shift, final MapNodeResult<K, V> details,
      final EqualityComparator<Object> cmp);
  
  // TODO: move to {@code Node} interface
  boolean equivalent(final Object other, final EqualityComparator<Object> cmp);

}
