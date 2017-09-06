/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

public class SetNodeResultImpl<K> implements SetNodeResult<K> {

  private K replacedValue;
  private boolean isModified;
  private boolean isReplaced;

  private int deltaSize;
  private int deltaHashCode;

  public int getDeltaSize() {
    return deltaSize;
  }

  public void updateDeltaSize(int deltaSize) {
    this.deltaSize += deltaSize;
  }

  public int getDeltaHashCode() {
    return deltaHashCode;
  }

  public void updateDeltaHashCode(int deltaHashCode) {
    this.deltaHashCode += deltaHashCode;
  }

  // update: inserted/removed single element, element count changed
  public void modified() {
    this.isModified = true;
  }

  public void updated(K replacedValue) {
    this.replacedValue = replacedValue;
    this.isModified = true;
    this.isReplaced = true;
  }

  public SetNodeResultImpl() {
  }

  public boolean isModified() {
    return isModified;
  }

  public boolean hasReplacedValue() {
    return isReplaced;
  }

  public K getReplacedValue() {
    return replacedValue;
  }

}
