/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh;

import io.usethesource.capsule.jmh.api.JmhValue;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.CompilerControl.Mode;

@CompilerControl(Mode.DONT_INLINE)
public class PureIntegerWithCustomHashCode implements JmhValue {

  private final int value;

  PureIntegerWithCustomHashCode(int value) {
    this.value = value;
  }

  public static final PureIntegerWithCustomHashCode valueOf(int value) {
    return new PureIntegerWithCustomHashCode(value);
  }

  @Override
  public int hashCode() {
    int h = value ^ 0x85ebca6b;
    // based on the final Avalanching phase of MurmurHash2
    // providing a nice mix of bits even for small numbers.
    h ^= h >>> 13;
    h *= 0x5bd1e995;
    h ^= h >>> 15;

    return h;
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    if (other == this) {
      return true;
    }

    if (other instanceof PureIntegerWithCustomHashCode) {
      int otherValue = ((PureIntegerWithCustomHashCode) other).value;

      return value == otherValue;
    }
    return false;
  }

  @Override
  public Object unwrap() {
    return this; // cannot be unwrapped
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

}
