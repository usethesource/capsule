/*******************************************************************************
 * Copyright (c) 2013-2015 CWI All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 * * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
 *******************************************************************************/
package io.usethesource.capsule;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public interface TransientSet<K> extends Set<K> {

  @Override
  boolean containsAll(final Collection<?> c);

  boolean containsAllEquivalent(final Collection<?> c, final Comparator<Object> cmp);

  K get(final Object o);

  K getEquivalent(final Object o, final Comparator<Object> cmp);

  @Override
  boolean contains(final Object o);

  boolean containsEquivalent(final Object o, final Comparator<Object> cmp);

  boolean __insert(final K key);

  boolean __insertEquivalent(final K key, final Comparator<Object> cmp);

  boolean __insertAll(final Set<? extends K> set);

  boolean __insertAllEquivalent(final Set<? extends K> set, final Comparator<Object> cmp);

  boolean __remove(final K key);

  boolean __removeEquivalent(final K key, final Comparator<Object> cmp);

  boolean __removeAll(final Set<? extends K> set);

  boolean __removeAllEquivalent(final Set<? extends K> set, final Comparator<Object> cmp);

  boolean __retainAll(final Set<? extends K> set);

  boolean __retainAllEquivalent(final TransientSet<? extends K> transientSet,
                                final Comparator<Object> cmp);

  Iterator<K> keyIterator();

  ImmutableSet<K> freeze();

}
