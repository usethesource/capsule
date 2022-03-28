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

public interface MapNode<K, V, R extends MapNode<K, V, R>> extends Node {

  boolean containsKey(final K key, final int keyHash, final int shift);

  Optional<V> findByKey(final K key, final int keyHash, final int shift);

  R updated(final AtomicReference<Thread> mutator, final K key,
            final V val, final int keyHash, final int shift, final MapNodeResult<K, V> details);

  R removed(final AtomicReference<Thread> mutator, final K key,
            final int keyHash, final int shift, final MapNodeResult<K, V> details);

}
