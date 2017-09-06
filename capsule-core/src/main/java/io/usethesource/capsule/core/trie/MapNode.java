/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

import io.usethesource.capsule.util.EqualityComparator;

public interface MapNode<K, V, R extends MapNode<K, V, R>> extends Node {

  // TODO: move to {@code Node} interface
  boolean equivalent(final Object other, final EqualityComparator<Object> cmp);

}
