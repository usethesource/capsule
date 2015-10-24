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

public interface ImmutableSet<K> extends Set<K> {

  @Override
  public boolean containsAll(final Collection<?> c);

  public boolean containsAllEquivalent(final Collection<?> c, final Comparator<Object> cmp);

  public K get(final Object o);

  public K getEquivalent(final Object o, final Comparator<Object> cmp);

  @Override
  public boolean contains(final Object o);

  public boolean containsEquivalent(final Object o, final Comparator<Object> cmp);

  public ImmutableSet<K> __insert(final K key);

  public ImmutableSet<K> __insertEquivalent(final K key, final Comparator<Object> cmp);

  public ImmutableSet<K> __insertAll(final Set<? extends K> set);

  public ImmutableSet<K> __insertAllEquivalent(final Set<? extends K> set,
      final Comparator<Object> cmp);

  public ImmutableSet<K> __remove(final K key);

  public ImmutableSet<K> __removeEquivalent(final K key, final Comparator<Object> cmp);

  public ImmutableSet<K> __removeAll(final Set<? extends K> set);

  public ImmutableSet<K> __removeAllEquivalent(final Set<? extends K> set,
      final Comparator<Object> cmp);

  public ImmutableSet<K> __retainAll(final Set<? extends K> set);

  public ImmutableSet<K> __retainAllEquivalent(final TransientSet<? extends K> transientSet,
      final Comparator<Object> cmp);

  public Iterator<K> keyIterator();

  public boolean isTransientSupported();

  public TransientSet<K> asTransient();

}
