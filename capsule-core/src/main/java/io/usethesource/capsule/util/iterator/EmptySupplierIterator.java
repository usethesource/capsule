/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util.iterator;

import java.util.NoSuchElementException;

public class EmptySupplierIterator<K, V> implements SupplierIterator<K, V> {

  private static final SupplierIterator EMPTY_ITERATOR = new EmptySupplierIterator();

  public static <K, V> SupplierIterator<K, V> emptyIterator() {
    return EMPTY_ITERATOR;
  }

  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public K next() {
    throw new NoSuchElementException();
  }

  @Override
  public V get() {
    throw new NoSuchElementException();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}
