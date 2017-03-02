/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api.experimental;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Set<K> extends Iterable<K>, Function<K, Optional<K>> {

  long size();

  boolean isEmpty();

  boolean contains(final Object o);

  default boolean containsAll(final Set<?> set) {
    for (Object item : set) {
      if (!contains(item)) {
        return false;
      }
    }
    return true;
  }

  // K get(final Object o);

  @Override
  Iterator<K> iterator();

  /**
   * The hash code of a set is order independent by combining the hashes of the elements via a
   * bitwise XOR operation.
   * 
   * @return XOR reduction of all hashes of elements
   */
  @Override
  int hashCode();

  @Override
  boolean equals(Object other);

  default Spliterator<K> spliterator() {
    return Spliterators.spliterator(iterator(), size(), 0);
  }

  default Stream<K> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  default Stream<K> parallelStream() {
    return StreamSupport.stream(spliterator(), true);
  }

  Set.Immutable<K> asImmutable();

  interface Immutable<K> extends Set<K> {

    Set.Immutable<K> insert(final K key);

    Set.Immutable<K> remove(final K key);

    Set.Immutable<K> insertAll(final Set<? extends K> set);

    Set.Immutable<K> removeAll(final Set<? extends K> set);

    Set.Immutable<K> retainAll(final Set<? extends K> set);

    boolean isTransientSupported();

    Set.Transient<K> asTransient();

    java.util.Set<K> asJdkCollection();

  }

  interface Transient<K> extends Set<K> {

    boolean insert(final K key);

    boolean remove(final K key);

    boolean insertAll(final Set<? extends K> set);

    boolean removeAll(final Set<? extends K> set);

    boolean retainAll(final Set<? extends K> set);

  }

}
