/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

public interface Node {

  // <T> ArrayView<T> dataArray(int category);

  /**
   * Creates an array abstraction for a subset of data stored in a node.
   *
   * @param category the bit pattern of the (heterogeneous) data category
   * @param component the index to address into tuple
   * @param <T> dynamic cast type of projected on view
   */
  <T> ArrayView<T> dataArray(int category, int component);

  ArrayView<? extends Node> nodeArray();

}