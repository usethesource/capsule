/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion;

import io.usethesource.criterion.api.JmhValue;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.CompilerControl.Mode;

@CompilerControl(Mode.DONT_INLINE)
public class SleepingInteger implements JmhValue {

  public static boolean IS_SLEEP_ENABLED_IN_HASHCODE = true;
  public static boolean IS_SLEEP_ENABLED_IN_EQUALS = true;

  private static final int MAX_SLEEP_IN_MILLISECONDS = 0;
  private static final int MAX_SLEEP_IN_NANOSECONDS = 10;

  private int value;

  public SleepingInteger(int value) {
    this.value = value;
  }

  protected void sleep(int base) {
    try {
      int timeMillis =
          MAX_SLEEP_IN_MILLISECONDS == 0 ? 0 : Math.abs(base) % MAX_SLEEP_IN_MILLISECONDS;

      int timeNanos = MAX_SLEEP_IN_NANOSECONDS == 0 ? 0 : Math.abs(base) % MAX_SLEEP_IN_NANOSECONDS;

      // System.out.printf("Sleeping %dms\n", time);

      Thread.sleep(timeMillis, timeNanos);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  // TODO: have configurable WEIGHTS for hashCode() and equals() penalties.
  // TODO: add caching to option to CHART (either keys, or keys + vals; in
  // extra int[] array)
  @Override
  public int hashCode() {
    if (IS_SLEEP_ENABLED_IN_HASHCODE) {
      sleep(value);
    }
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

    if (other instanceof SleepingInteger) {
      int otherValue = ((SleepingInteger) other).value;

      if (IS_SLEEP_ENABLED_IN_EQUALS) {
        sleep(value + otherValue);
      }

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
