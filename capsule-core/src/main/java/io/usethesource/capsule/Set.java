/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import io.usethesource.capsule.core.PersistentTrieSet;

public interface Set<K> extends java.util.Set<K>, SetEq<K> {

  @Override
  int size();

  @Override
  boolean isEmpty();

  @Override
  boolean contains(Object o);

  @Override
  boolean containsAll(Collection<?> c);

  K get(Object o);

  default Optional<K> findFirst() {
    if (isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(iterator().next());
    }
  }

  Iterator<K> keyIterator();

  interface Immutable<K> extends Set<K>, SetEq.Immutable<K> {

    Set.Immutable<K> __insert(final K key);

    Set.Immutable<K> __remove(final K key);

    Set.Immutable<K> __insertAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> __removeAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> __retainAll(final java.util.Set<? extends K> set);

    boolean isTransientSupported();

    Set.Transient<K> asTransient();

    static <K> Set.Immutable<K> of() {
      return PersistentTrieSet.of();
    }

    static <K> Set.Immutable<K> of(K item) {
      return PersistentTrieSet.of(item);
    }

    static <K> Set.Immutable<K> of(K item0, K item1) {
      return PersistentTrieSet.of(item0, item1);
    }

  }

  interface Transient<K> extends Set<K>, SetEq.Transient<K> {

    boolean __insert(final K key);

    boolean __remove(final K key);

    boolean __insertAll(final java.util.Set<? extends K> set);

    boolean __removeAll(final java.util.Set<? extends K> set);

    boolean __retainAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> freeze();

    static <K> Set.Transient<K> of() {
      return PersistentTrieSet.transientOf();
    }

    static <K> Set.Transient<K> of(K key0) {
      final Set.Transient<K> tmp = Set.Transient.of();

      tmp.__insert(key0);

      return tmp;
    }

    static <K> Set.Transient<K> of(K key0, K key1) {
      final Set.Transient<K> tmp = Set.Transient.of();

      tmp.__insert(key0);
      tmp.__insert(key1);

      return tmp;
    }

    static <K> Set.Transient<K> of(K key0, K key1, K key2) {
      final Set.Transient<K> tmp = Set.Transient.of();

      tmp.__insert(key0);
      tmp.__insert(key1);
      tmp.__insert(key2);

      return tmp;
    }

    static <K> Set.Transient<K> of(K key0, K key1, K key2, K key3) {
      final Set.Transient<K> tmp = Set.Transient.of();

      tmp.__insert(key0);
      tmp.__insert(key1);
      tmp.__insert(key2);
      tmp.__insert(key3);

      return tmp;
    }

    static <K> Set.Transient<K> of(K key0, K key1, K key2, K key3, K key4) {
      final Set.Transient<K> tmp = Set.Transient.of();

      tmp.__insert(key0);
      tmp.__insert(key1);
      tmp.__insert(key2);
      tmp.__insert(key3);
      tmp.__insert(key4);

      return tmp;
    }

    static <K> Set.Transient<K> of(K key0, K key1, K key2, K key3, K key4, K key5) {
      final Set.Transient<K> tmp = Set.Transient.of();

      tmp.__insert(key0);
      tmp.__insert(key1);
      tmp.__insert(key2);
      tmp.__insert(key3);
      tmp.__insert(key4);
      tmp.__insert(key5);

      return tmp;
    }

  }

}
