/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util.collection;

import java.util.AbstractSet;
import java.util.Collection;

import io.usethesource.capsule.Set;

@Deprecated
public abstract class AbstractImmutableSet<E> extends AbstractSet<E> implements Set.Immutable<E> {

  @Override
  public boolean add(E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isTransientSupported() {
    return false;
  }

  @Override
  public Set.Transient<E> asTransient() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    for (Object item : c) {
      if (!contains(item)) {
        return false;
      }
    }
    return true;
  }

}
