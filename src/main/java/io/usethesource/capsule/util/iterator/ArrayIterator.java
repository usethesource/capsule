/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<E> implements Iterator<E> {

  final E[] values;
  final int end;
  int currentIndex;

  public ArrayIterator(final E[] values, int start, int end) {
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
    return values[currentIndex++];
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  @SafeVarargs
  public static <E> Iterator<E> of(E... array) {
    return new ArrayIterator<>(array, 0, array.length);
  }

  public static <E> Iterator<E> of(E[] array, int start, int length) {
    return new ArrayIterator<>(array, start, start + length);
  }

}
