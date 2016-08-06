/*******************************************************************************
 * Copyright (c) 2015 CWI All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 * * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
 *******************************************************************************/
package io.usethesource.capsule;

public class ArrayUtilsInt {

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
