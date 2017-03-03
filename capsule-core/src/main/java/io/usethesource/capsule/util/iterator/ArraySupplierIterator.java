/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util.iterator;

import java.util.NoSuchElementException;

public class ArraySupplierIterator<E> implements SupplierIterator<E, E> {

  final Object[] values;
  final int end;
  int currentIndex;
  E currentElement;

  public ArraySupplierIterator(final Object[] values, int start, int end) {
    assert start <= end && end <= values.length;

    this.values = values;
    this.end = end;
    this.currentIndex = start;
  }

  @Override
  public boolean hasNext() {
    return currentIndex < end;
  }

  @Override
  public E next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    currentElement = (E) values[currentIndex++];
    return currentElement;
  }

  @Override
  public E get() {
    return currentElement;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  public static <E> SupplierIterator<E, E> of(Object[] array) {
    return new ArraySupplierIterator<>(array, 0, array.length);
  }

  public static <E> SupplierIterator<E, E> of(Object[] array, int start, int length) {
    return new ArraySupplierIterator<>(array, start, start + length);
  }

}
