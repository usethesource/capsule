/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import static io.usethesource.capsule.factory.DefaultSetFactory.FACTORY;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public interface Set<K> extends java.util.Set<K> {

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

  @Override
  boolean equals(Object o);

  @Override
  int hashCode();

  interface Immutable<K> extends Set<K> {

    Set.Immutable<K> __insert(final K key);

    Set.Immutable<K> __remove(final K key);

    Set.Immutable<K> __insertAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> __removeAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> __retainAll(final java.util.Set<? extends K> set);

    default Set.Immutable<K> union(Set.Immutable<K> other) {
      return union(this, other);
    }

    default Set.Immutable<K> subtract(Set.Immutable<K> other) {
      return subtract(this, other);
    }

    default Set.Immutable<K> intersect(Set.Immutable<K> other) {
      return intersect(this, other);
    }

    boolean isTransientSupported();

    Set.Transient<K> asTransient();

    static <K> Set.Immutable<K> of() {
      return FACTORY.of();
    }

    static <K> Set.Immutable<K> of(K item) {
      return FACTORY.of(item);
    }

    static <K> Set.Immutable<K> of(K item0, K item1) {
      return FACTORY.of(item0, item1);
    }

    static <T> Set.Immutable<T> union(final Set.Immutable<T> set1, final Set.Immutable<T> set2) {

      if (set1 == null && set2 == null) {
        return Set.Immutable.of();
      }
      if (set1 == null) {
        return set2;
      }
      if (set2 == null) {
        return set1;
      }

      if (set1 == set2) {
        return set1;
      }

      final Set.Immutable<T> smaller;
      final Set.Immutable<T> bigger;

      final Set.Immutable<T> unmodified;

      if (set2.size() >= set1.size()) {
        unmodified = set2;
        smaller = set1;
        bigger = set2;
      } else {
        unmodified = set1;
        smaller = set2;
        bigger = set1;
      }

      final Set.Transient<T> tmp = bigger.asTransient();
      boolean modified = false;

      for (T key : smaller) {
        if (tmp.__insert(key)) {
          modified = true;
        }
      }

      if (modified) {
        return tmp.freeze();
      } else {
        return unmodified;
      }
    }

    static <K> Set.Immutable<K> subtract(final Set.Immutable<K> set1, final Set.Immutable<K> set2) {

      if (set1 == null && set2 == null) {
        return Set.Immutable.of();
      }
      if (set1 == set2) {
        return Set.Immutable.of();
      }
      if (set1 == null) {
        return Set.Immutable.of();
      }
      if (set2 == null) {
        return set1;
      }

      final Set.Transient<K> tmp = set1.asTransient();
      boolean modified = false;

      for (K key : set2) {
        if (tmp.__remove(key)) {
          modified = true;
        }
      }

      if (!modified) {
        return set1;
      }
      if (tmp.isEmpty()) {
        return Set.Immutable.of();
      }
      return tmp.freeze();
    }

    static <K> Set.Immutable<K> intersect(final Set.Immutable<K> set1,
        final Set.Immutable<K> set2) {

      if (set1 == null || set1.isEmpty()) {
        return Set.Immutable.of();
      }
      if (set2 == null || set2.isEmpty()) {
        return Set.Immutable.of();
      }

      if (set1 == set2) {
        return set1;
      }

      final Set.Immutable<K> smaller;
      final Set.Immutable<K> bigger;

      final Set.Immutable<K> unmodified;

      if (set2.size() >= set1.size()) {
        unmodified = set1;
        smaller = set1;
        bigger = set2;
      } else {
        unmodified = set2;
        smaller = set2;
        bigger = set1;
      }

      final Set.Transient<K> tmp = smaller.asTransient();
      boolean modified = false;

      for (Iterator<K> it = tmp.iterator(); it.hasNext(); ) {
        final K key = it.next();
        if (!bigger.contains(key)) {
          it.remove();
          modified = true;
        }
      }

      if (!modified) {
        return unmodified;
      }
      if (tmp.isEmpty()) {
        return Set.Immutable.of();
      }
      return tmp.freeze();
    }
  }

  interface Transient<K> extends Set<K> {

    boolean __insert(final K key);

    boolean __remove(final K key);

    boolean __insertAll(final java.util.Set<? extends K> set);

    boolean __removeAll(final java.util.Set<? extends K> set);

    boolean __retainAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> freeze();

    static <K> Set.Transient<K> of() {
      return FACTORY.transientOf();
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
