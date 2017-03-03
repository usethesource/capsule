/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util;

public class ArrayUtilsInt {

  public static final int[] arrayOfInt(int... items) {
    return items;
  }

  public static int[] arraycopyAndInsertInt(final int[] src, final int idx, final int value) {
    final int[] dst = new int[src.length + 1];

    // copy 'src' and insert 1 element(s) at position 'idx'
    System.arraycopy(src, 0, dst, 0, idx);
    dst[idx] = value;
    System.arraycopy(src, idx, dst, idx + 1, src.length - idx);

    return dst;
  }

  public static int[] arraycopyAndRemoveInt(final int[] src, final int idx) {
    final int[] dst = new int[src.length - 1];

    // copy 'src' and remove 1 element(s) at position 'idx'
    System.arraycopy(src, 0, dst, 0, idx);
    System.arraycopy(src, idx + 1, dst, idx, src.length - idx - 1);

    return dst;
  }

}
