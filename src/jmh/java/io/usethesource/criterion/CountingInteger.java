/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion;

import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.CompilerControl.Mode;

import io.usethesource.criterion.api.JmhValue;

@CompilerControl(Mode.DONT_INLINE)
public class CountingInteger implements JmhValue {

  private static long HASHCODE_COUNTER = 0;
  private static long EQUALS_COUNTER = 0;

  public static void resetCounters() {
    HASHCODE_COUNTER = 0;
    EQUALS_COUNTER = 0;
  }

  public static long getHashcodeCounter() {
    return HASHCODE_COUNTER;
  }

  public static long getEqualsCounter() {
    return EQUALS_COUNTER;
  }

  private int value;

  CountingInteger(int value) {
    this.value = value;
  }

  @Override
  public int hashCode() {
    HASHCODE_COUNTER++;
    return value;
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    if (other == this) {
      return true;
    }

    if (other instanceof CountingInteger) {
      int otherValue = ((CountingInteger) other).value;

      EQUALS_COUNTER++;

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
