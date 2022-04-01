/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

import java.util.Optional;

/**
 * @param <C> is a (collection) representation of one or more values
 */
public interface MultimapNode<K, V, C, R extends MultimapNode<K, V, C, R>> extends Node {

  boolean containsKey(K key, int keyHash, int shift);

  boolean containsTuple(K key, V value, int keyHash, int shift);

  Optional<C> findByKey(K key, int keyHash, int shift);

  boolean mustUnbox(C values);
  
  V unbox(C values);

  default R inserted(UniqueIdentity mutator, K key, C values, int keyHash, int shift, MultimapResult<K, V, C> details) {
    if (mustUnbox(values)) {
      return insertedSingle(mutator, key, unbox(values), keyHash, shift, details);
    } else {
      return insertedMultiple(mutator, key, values, keyHash, shift, details);
    }
  }

  R insertedSingle(UniqueIdentity mutator, K key, V value, int keyHash, int shift, MultimapResult<K, V, C> details);

  R insertedMultiple(UniqueIdentity mutator, K key, C values, int keyHash, int shift, MultimapResult<K, V, C> details);

  default R updated(UniqueIdentity mutator, K key, C values, int keyHash, int shift, MultimapResult<K, V, C> details) {
    if (mustUnbox(values)) {
      return updatedSingle(mutator, key, unbox(values), keyHash, shift, details);
    } else {
      return updatedMultiple(mutator, key, values, keyHash, shift, details);
    }
  }

  R updatedSingle(UniqueIdentity mutator, K key, V value, int keyHash, int shift, MultimapResult<K, V, C> details);

  R updatedMultiple(UniqueIdentity mutator, K key, C values, int keyHash, int shift, MultimapResult<K, V, C> details);

  /**
   * Removes the {@code key} / {@code val} tuple.
   */
  R removed(UniqueIdentity mutator, K key, V value, int keyHash, int shift, MultimapResult<K, V, C> details);

  /**
   * Removes all values associated with {@code key}.
   */
  R removed(UniqueIdentity mutator, K key, int keyHash, int shift, MultimapResult<K, V, C> details);

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
  default R copyAndUpdateBitmaps(UniqueIdentity mutator, final long bitmap) {
    throw new IllegalStateException();
  }

//  // TODO: remove from interface
//  @Deprecated
//  default R copyAndUpdateBitmaps(UniqueIdentity mutator, final int bitmap0,
//      final int bitmap1) {
//    throw new IllegalStateException();
//  }

}
