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
import java.util.Iterator;
import java.util.Set;

public interface TransientSet<K> extends Set<K> {

  @Override
  public boolean containsAll(final Collection<?> c);

  public boolean containsAllEquivalent(final Collection<?> c, final Comparator<Object> cmp);

  public K get(final Object o);

  public K getEquivalent(final Object o, final Comparator<Object> cmp);

  @Override
  public boolean contains(final Object o);

  public boolean containsEquivalent(final Object o, final Comparator<Object> cmp);

  public boolean __insert(final K key);

  public boolean __insertEquivalent(final K key, final Comparator<Object> cmp);

  public boolean __insertAll(final Set<? extends K> set);

  public boolean __insertAllEquivalent(final Set<? extends K> set, final Comparator<Object> cmp);

  public boolean __remove(final K key);

  public boolean __removeEquivalent(final K key, final Comparator<Object> cmp);

  public boolean __removeAll(final Set<? extends K> set);

  public boolean __removeAllEquivalent(final Set<? extends K> set, final Comparator<Object> cmp);

  public boolean __retainAll(final Set<? extends K> set);

  public boolean __retainAllEquivalent(final TransientSet<? extends K> transientSet,
      final Comparator<Object> cmp);

  public Iterator<K> keyIterator();

  public ImmutableSet<K> freeze();

}
