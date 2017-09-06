/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

public final class MapNodeResult<K, V> {

  private V replacedValue;
  private boolean isModified;
  private boolean isReplaced;

  // update: inserted/removed single element, element count changed
  public void modified() {
    this.isModified = true;
  }

  public void updated(V replacedValue) {
    this.replacedValue = replacedValue;
    this.isModified = true;
    this.isReplaced = true;
  }

  // update: neither element, nor element count changed
  public static <K, V> MapNodeResult<K, V> unchanged() {
    return new MapNodeResult<>();
  }

  private MapNodeResult() {
  }

  public boolean isModified() {
    return isModified;
  }

  public boolean hasReplacedValue() {
    return isReplaced;
  }

  public V getReplacedValue() {
    return replacedValue;
  }
}
