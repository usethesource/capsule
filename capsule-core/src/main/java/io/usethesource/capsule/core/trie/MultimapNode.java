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

  boolean containsTuple(K key, V value, int keyHash, int shift, EqualityComparator<Object> cmp);

  Optional<C> findByKey(K key, int keyHash, int shift, EqualityComparator<Object> cmp);

  R insertedSingle(AtomicReference<Thread> mutator, K key, V value, int keyHash, int shift,
      MultimapResult<K, V, C> details, EqualityComparator<Object> cmp);

  R updatedSingle(AtomicReference<Thread> mutator, K key, V value, int keyHash, int shift,
      MultimapResult<K, V, C> details, EqualityComparator<Object> cmp);

  R updatedMultiple(AtomicReference<Thread> mutator, K key, C values, int keyHash, int shift,
      MultimapResult<K, V, C> details, EqualityComparator<Object> cmp);

  /**
   * Removes the {@code key} / {@code val} tuple.
   */
  R removed(AtomicReference<Thread> mutator, K key, V value, int keyHash, int shift,
      MultimapResult<K, V, C> details, EqualityComparator<Object> cmp);

  /**
   * Removes all values associated with {@code key}.
   */
  R removed(AtomicReference<Thread> mutator, K key, int keyHash, int shift,
      MultimapResult<K, V, C> details, EqualityComparator<Object> cmp);

  /**
   * Abstract predicate over a node's size. Value can be either {@value #SIZE_EMPTY},
   * {@value #SIZE_ONE}, or {@value #SIZE_MORE_THAN_ONE}.
   *
   * @return size predicate
   */
  byte sizePredicate();

  // TODO: remove from interface
  @Deprecated
  default int patternOfSingleton() {
    throw new IllegalStateException();
  }

  // TODO: remove from interface
  @Deprecated
  default EitherSingletonOrCollection.Type typeOfSingleton() {
    throw new IllegalStateException();
  }

  // TODO: remove from interface
  @Deprecated
  default R copyAndUpdateBitmaps(AtomicReference<Thread> mutator, final long bitmap) {
    throw new IllegalStateException();
  }

//  // TODO: remove from interface
//  @Deprecated
//  default R copyAndUpdateBitmaps(AtomicReference<Thread> mutator, final int bitmap0,
//      final int bitmap1) {
//    throw new IllegalStateException();
//  }

}
