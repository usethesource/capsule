/*******************************************************************************
 * Copyright (c) 2013-2014 CWI All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 * * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
 *******************************************************************************/
package io.usethesource.capsule;

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
    if (!hasNext())
      throw new NoSuchElementException();
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
