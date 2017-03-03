/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util.iterator;

import java.util.NoSuchElementException;

public class ArrayKeyValueSupplierIterator<K, V> implements SupplierIterator<K, V> {

  final Object[] values;
  final int end;
  int currentIndex;
  V currentValue;

  public ArrayKeyValueSupplierIterator(final Object[] values, int start, int end) {
    assert start <= end && end <= values.length;
    assert (end - start) % 2 == 0;

    this.values = values;
    this.end = end;
    this.currentIndex = start;
  }

  @Override
  public boolean hasNext() {
    return currentIndex < end;
  }

  @Override
  public K next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    final K currentKey = (K) values[currentIndex++];
    currentValue = (V) values[currentIndex++];

    return currentKey;
  }

  @Override
  public V get() {
    return currentValue;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  public static <K, V> ArrayKeyValueSupplierIterator<K, V> of(Object[] array) {
    return new ArrayKeyValueSupplierIterator<>(array, 0, array.length);
  }

  public static <K, V> ArrayKeyValueSupplierIterator<K, V> of(Object[] array, int start,
      int length) {
    return new ArrayKeyValueSupplierIterator<>(array, start, start + length);
  }

}
