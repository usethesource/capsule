/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Invariant array interface.
 *
 * @param <T> element type of array
 */
public interface ArrayView<T> extends Iterable<T> {

  int size();

  default boolean isEmpty() {
    return size() == 0;
  }

  T get(int index);

  default void set(int index, T item) {
    throw new UnsupportedOperationException();
  }

  default void set(int index, T item, AtomicReference<?> writeCapabilityToken) {
    throw new UnsupportedOperationException();
  }

  @Override
  default ListIterator<T> iterator() {
    return new ListIterator<T>() {
      private volatile int current = 0;
      private volatile int previous = -1;

      @Override
      public boolean hasNext() {
        return current < ArrayView.this.size();
      }

      @Override
      public T next() {
        return ArrayView.this.get(previous = current++);
      }

      @Override
      public boolean hasPrevious() {
        return current >= 0;
      }

      @Override
      public T previous() {
        return ArrayView.this.get(previous = current--);
      }

      @Override
      public int nextIndex() {
        return current + 1;
      }

      @Override
      public int previousIndex() {
        return current - 1;
      }

      @Override
      public void set(T t) {
        if (previous == -1) {
          throw new IllegalStateException();
        }

        ArrayView.this.set(previous, t);
      }

      @Override
      public void add(T t) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  static <T> ArrayView<T> empty() {
    return new ArrayView<T>() {
      @Override
      public int size() {
        return 0;
      }

      @Override
      public T get(int index) {
        throw new IndexOutOfBoundsException();
      }
    };
  }

}
