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

/**
 * @param <C> is a (collection) representation of one or more values
 */
public interface MultimapNode<K, V, C, R extends MultimapNode<K, V, C, R>> extends Node {

  boolean containsKey(K key, int keyHash, int shift, EqualityComparator<Object> cmp);

  boolean containsTuple(K key, V val, int keyHash, int shift, EqualityComparator<Object> cmp);

  // C -> Set.Immutable<V>
  Optional<C> findByKey(K key, int keyHash, int shift, EqualityComparator<Object> cmp);

  R inserted(AtomicReference<Thread> mutator, K key, V val, int keyHash, int shift,
      MultimapResult<K, V, C> details, EqualityComparator<Object> cmp);

  R updatedSingle(AtomicReference<Thread> mutator, K key, V val, int keyHash, int shift,
      MultimapResult<K, V, C> details, EqualityComparator<Object> cmp);

  // C -> Set<V>
  R updatedMultiple(AtomicReference<Thread> mutator, K key, C val, int keyHash, int shift,
      MultimapResult<K, V, C> details, EqualityComparator<Object> cmp);

  /**
   * Removes all values associated to {@code key}.
   */
  R removed(AtomicReference<Thread> mutator, K key, int keyHash, int shift,
      MultimapResult<K, V, C> details, EqualityComparator<Object> cmp);

  /**
   * Removes the {@code key} / {@code val} tuple.
   */
  R removed(AtomicReference<Thread> mutator, K key, V val, int keyHash, int shift,
      MultimapResult<K, V, C> details, EqualityComparator<Object> cmp);

  byte sizePredicate();

  int patternOfSingleton();

  R copyAndUpdateBitmaps(AtomicReference<Thread> mutator, final long bitmap);

  interface MultimapResult<K, V, C> {

    // update: inserted/removed single element, element count changed
    void modified();

    void updatedSingle(V replacedValue);

    void updatedMultiple(C replacedValueCollection);

    boolean isModified();

//    EitherSingletonOrCollection.Type getType();

    boolean hasReplacedValue();

    V getReplacedValue();

    C getReplacedCollection();
  }

}
