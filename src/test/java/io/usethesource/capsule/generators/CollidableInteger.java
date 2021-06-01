/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators;

import java.io.Serializable;

public final class CollidableInteger implements Comparable<CollidableInteger>, Serializable {
  public final int value;
  public final int hashCode;

  public CollidableInteger(int value, int hashCode) {
    this.value = value;
    this.hashCode = hashCode;
  }

  public int value() {
    return value;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }

    if (other == this) {
      return true;
    }

    if (other instanceof CollidableInteger) {
      int otherValue = ((CollidableInteger) other).value;
      int otherHashCode = ((CollidableInteger) other).hashCode;

      if (value == otherValue) {
        assert hashCode == otherHashCode;
        return true;
      } else {
        return false;
      }
    }

    return false;
  }

  @Override
  public int compareTo(CollidableInteger that) {
    return this.value < that.value ? -1 : (this.value == that.value ? 0 : 1);
  }

  @Override
  public String toString() {
    return String.format("%d [hashCode=%d]", value, hashCode);
  }
}
