/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Collection;
import java.util.Comparator;

import io.usethesource.capsule.util.EqualityComparator;

/**
 * Set extension providing methods that take a comparator. Closes over base (and not extended) set.
 */
@Deprecated
public interface SetEq<K> extends java.util.Set<K> {

  default boolean containsEquivalent(final Object o, final EqualityComparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Set.");
  }

  default boolean containsAllEquivalent(final Collection<?> c, final EqualityComparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Set.");
  }

  default K getEquivalent(final Object o, final EqualityComparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ Set.");
  }

  @Deprecated
  interface Immutable<K> extends SetEq<K> {

    default Set.Immutable<K> __insertEquivalent(final K key, final EqualityComparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default Set.Immutable<K> __removeEquivalent(final K key, final EqualityComparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default Set.Immutable<K> __insertAllEquivalent(final java.util.Set<? extends K> set,
        final EqualityComparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default Set.Immutable<K> __removeAllEquivalent(final java.util.Set<? extends K> set,
        final EqualityComparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    // TODO: unify API with insert and remove
    default Set.Immutable<K> __retainAllEquivalent(final Set.Transient<? extends K> transientSet,
        final EqualityComparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

  }

  @Deprecated
  interface Transient<K> extends SetEq<K> {

    default boolean __insertEquivalent(final K key, final EqualityComparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default boolean __removeEquivalent(final K key, final EqualityComparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default boolean __insertAllEquivalent(final java.util.Set<? extends K> set,
        final EqualityComparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    default boolean __removeAllEquivalent(final java.util.Set<? extends K> set,
        final EqualityComparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

    // TODO: unify API with insert and remove
    default boolean __retainAllEquivalent(final Set.Transient<? extends K> transientSet,
        final EqualityComparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.");
    }

  }
}
