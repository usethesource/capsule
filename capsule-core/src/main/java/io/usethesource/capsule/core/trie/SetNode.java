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

public interface SetNode<K, R extends SetNode<K, R>> extends Node {

  boolean contains(final K key, final int keyHash, final int shift,
      final EqualityComparator<Object> cmp);

  Optional<K> findByKey(final K key, final int keyHash, final int shift,
      final EqualityComparator<Object> cmp);

  R updated(final AtomicReference<Thread> mutator, final K key, final int keyHash, final int shift,
      final SetNodeResult<K> details, final EqualityComparator<Object> cmp);

  R removed(final AtomicReference<Thread> mutator, final K key, final int keyHash, final int shift,
      final SetNodeResult<K> details, final EqualityComparator<Object> cmp);

  // TODO: move to {@code Node} interface
  boolean equivalent(final Object other, final EqualityComparator<Object> cmp);

  boolean hasPayload();

  int payloadArity();

  K getKey(final int index);

  int getKeyHash(final int index);

  default ImmutablePayloadTuple<K> getPayload(final int index) {
    return ImmutablePayloadTuple.of(getKeyHash(index), getKey(index));
  }

  default Optional<K> findFirst() {
    final ArrayView<K> elementArray = dataArray(0, 0);

    if (elementArray.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(elementArray.get(0));
    }
  }

  int size();

  int recursivePayloadHashCode();

  default R union(final AtomicReference<Thread> mutator, R that, final int shift,
      final EqualityComparator<Object> cmp) {
    throw new UnsupportedOperationException();
  }

  default R intersect(final AtomicReference<Thread> mutator, R that, final int shift,
      final EqualityComparator<Object> cmp) {
    throw new UnsupportedOperationException();
  }

  default R subtract(final AtomicReference<Thread> mutator, R that, final int shift,
      final EqualityComparator<Object> cmp) {
    throw new UnsupportedOperationException();
  }

}
