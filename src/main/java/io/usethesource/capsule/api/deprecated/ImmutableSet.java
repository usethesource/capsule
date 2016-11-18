/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api.deprecated;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public interface ImmutableSet<K> extends Set<K> {

  @Override
  boolean containsAll(final Collection<?> c);

  @Deprecated
  default boolean containsAllEquivalent(final Collection<?> c, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
  }

  K get(final Object o);

  @Deprecated
  default K getEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
  }

  @Override
  boolean contains(final Object o);

  @Deprecated
  default boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
  }

  ImmutableSet<K> __insert(final K key);

  @Deprecated
  default ImmutableSet<K> __insertEquivalent(final K key, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
  }

  ImmutableSet<K> __insertAll(final Set<? extends K> set);

  @Deprecated
  default ImmutableSet<K> __insertAllEquivalent(final Set<? extends K> set,
      final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
  }

  ImmutableSet<K> __remove(final K key);

  @Deprecated
  default ImmutableSet<K> __removeEquivalent(final K key, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
  }

  ImmutableSet<K> __removeAll(final Set<? extends K> set);

  @Deprecated
  default ImmutableSet<K> __removeAllEquivalent(final Set<? extends K> set,
      final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
  }

  ImmutableSet<K> __retainAll(final Set<? extends K> set);

  @Deprecated
  default ImmutableSet<K> __retainAllEquivalent(final TransientSet<? extends K> transientSet,
      final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
  }

  Iterator<K> keyIterator();

  boolean isTransientSupported();

  TransientSet<K> asTransient();

}
