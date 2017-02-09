/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api;

import java.util.Collection;
import java.util.Iterator;

public interface Set<K> extends java.util.Set<K>, SetEq<K> {

  @Override
  int size();

  @Override
  boolean isEmpty();

  @Override
  boolean containsAll(Collection<?> c);

  K get(Object o);

  @Override
  boolean contains(Object o);

  Iterator<K> keyIterator();

  interface Immutable<K> extends Set<K>, SetEq.Immutable<K> {

    Set.Immutable<K> __insert(final K key);

    Set.Immutable<K> __remove(final K key);

    Set.Immutable<K> __insertAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> __removeAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> __retainAll(final java.util.Set<? extends K> set);

    boolean isTransientSupported();

    Set.Transient<K> asTransient();

  }

  interface Transient<K> extends Set<K>, SetEq.Transient<K> {

    boolean __insert(final K key);

    boolean __remove(final K key);

    boolean __insertAll(final java.util.Set<? extends K> set);

    boolean __removeAll(final java.util.Set<? extends K> set);

    boolean __retainAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> freeze();

  }
}
