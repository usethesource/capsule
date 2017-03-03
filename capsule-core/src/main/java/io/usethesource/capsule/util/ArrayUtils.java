/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util;

public class ArrayUtils {

  public static final <T> T[] arrayOf(T... items) {
    return items;
  }

  public static boolean equals(Object[] a1, Object[] a2) {
    if (null == a1 || null == a2) {
      return false;
    }
    if (a1 == a2) {
      return true;
    }

    int length = a1.length;
    if (length != a2.length) {
      return false;
    }

    for (int i = 0; i < length; i++) {
      Object o1 = a1[i];
      Object o2 = a2[i];

      boolean areEqual = (o1 == o2) || (o1 != null && o1.equals(o2));

      if (!areEqual) {
        return false;
      }
    }

    return true;
  }

  @Deprecated
  public static Object[] copyAndSet(Object[] array, int index, Object elementNew) {
    final Object[] arrayNew = new Object[array.length];
    System.arraycopy(array, 0, arrayNew, 0, array.length);
    arrayNew[index] = elementNew;
    return arrayNew;
  }

  public static Object[] copyAndMoveToBack(Object[] array, int indexOld, int indexNew,
      Object elementNew) {
    assert indexOld <= indexNew;
    if (indexNew == indexOld) {
      return copyAndSet(array, indexNew, elementNew);
    } else {
      final Object[] arrayNew = new Object[array.length];
      System.arraycopy(array, 0, arrayNew, 0, indexOld);
      System.arraycopy(array, indexOld + 1, arrayNew, indexOld, indexNew - indexOld);
      arrayNew[indexNew] = elementNew;
      System.arraycopy(array, indexNew + 1, arrayNew, indexNew + 1, array.length - indexNew - 1);
      return arrayNew;
    }
  }

  public static Object[] copyAndMoveToFront(Object[] array, int indexOld, int indexNew,
      Object elementNew) {
    assert indexOld >= indexNew;
    if (indexNew == indexOld) {
      return copyAndSet(array, indexOld, elementNew);
    } else {
      final Object[] arrayNew = new Object[array.length];
      System.arraycopy(array, 0, arrayNew, 0, indexNew);
      arrayNew[indexNew] = elementNew;
      System.arraycopy(array, indexNew, arrayNew, indexNew + 1, indexOld - indexNew);
      System.arraycopy(array, indexOld + 1, arrayNew, indexOld + 1, array.length - indexOld - 1);
      return arrayNew;
    }
  }

  @Deprecated
  public static Object[] copyAndInsert(Object[] array, int index, Object elementNew) {
    final Object[] arrayNew = new Object[array.length + 1];
    System.arraycopy(array, 0, arrayNew, 0, index);
    arrayNew[index] = elementNew;
    System.arraycopy(array, index, arrayNew, index + 1, array.length - index);
    return arrayNew;
  }

  @Deprecated
  public static Object[] copyAndRemove(Object[] array, int index) {
    final Object[] arrayNew = new Object[array.length - 1];
    System.arraycopy(array, 0, arrayNew, 0, index);
    System.arraycopy(array, index + 1, arrayNew, index, array.length - index - 1);
    return arrayNew;
  }

}
