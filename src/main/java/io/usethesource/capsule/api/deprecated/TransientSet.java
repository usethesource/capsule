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

public interface TransientSet<K> extends Set<K> {

  @Override
  boolean containsAll(final Collection<?> c);

  @Deprecated
  default boolean containsAllEquivalent(final Collection<?> c, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
  }

  K get(final Object o);

  @Deprecated
  default K getEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
  }

  @Override
  boolean contains(final Object o);

  @Deprecated
  default boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
  }

  boolean __insert(final K key);

  @Deprecated
  default boolean __insertEquivalent(final K key, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
  }

  boolean __insertAll(final Set<? extends K> set);

  @Deprecated
  default boolean __insertAllEquivalent(final Set<? extends K> set, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
  }

  boolean __remove(final K key);

  @Deprecated
  default boolean __removeEquivalent(final K key, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
  }

  boolean __removeAll(final Set<? extends K> set);

  @Deprecated
  default boolean __removeAllEquivalent(final Set<? extends K> set, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
  }

  boolean __retainAll(final Set<? extends K> set);

  @Deprecated
  default boolean __retainAllEquivalent(final TransientSet<? extends K> transientSet,
      final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
  }

  Iterator<K> keyIterator();

  ImmutableSet<K> freeze();

}
