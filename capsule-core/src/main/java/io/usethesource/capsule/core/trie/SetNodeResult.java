/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

public interface SetNodeResult<K> {

  // update: neither element, nor element count changed
  static <K> SetNodeResult<K> unchanged() {
    return new SetNodeResultImpl<>();
  }

  int getDeltaSize();

  void updateDeltaSize(int deltaSize);

  int getDeltaHashCode();

  void updateDeltaHashCode(int deltaHashCode);

  // update: inserted/removed single element, element count changed
  void modified();

  void updated(K replacedValue);

  boolean isModified();

  boolean hasReplacedValue();

  K getReplacedValue();

}
