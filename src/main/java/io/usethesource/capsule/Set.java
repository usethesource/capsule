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

public interface Set<K> {

  boolean containsAll(final Collection<?> c);

  boolean containsAllEquivalent(final Collection<?> c, final Comparator<Object> cmp);

  K get(final Object o);

  K getEquivalent(final Object o, final Comparator<Object> cmp);

  boolean contains(final Object o);

  boolean containsEquivalent(final Object o, final Comparator<Object> cmp);

  Iterator<K> keyIterator();

  public static interface Immutable<K> extends Set<K> {

    boolean isTransientSupported();

    Set.Transient<K> asTransient();

    Set.Immutable<K> __insert(final K key);

    Set.Immutable<K> __insertEquivalent(final K key, final Comparator<Object> cmp);

    Set.Immutable<K> __insertAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> __insertAllEquivalent(final java.util.Set<? extends K> set,
        final Comparator<Object> cmp);

    Set.Immutable<K> __remove(final K key);

    Set.Immutable<K> __removeEquivalent(final K key, final Comparator<Object> cmp);

    Set.Immutable<K> __removeAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> __removeAllEquivalent(final java.util.Set<? extends K> set,
        final Comparator<Object> cmp);

    Set.Immutable<K> __retainAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> __retainAllEquivalent(
        final Set.Transient<? extends K> transientSet, final Comparator<Object> cmp);

  }

  public static interface Transient<K> extends Set<K> {

    Set.Immutable<K> freeze();

    boolean __insert(final K key);

    boolean __insertEquivalent(final K key, final Comparator<Object> cmp);

    boolean __insertAll(final java.util.Set<? extends K> set);

    boolean __insertAllEquivalent(final java.util.Set<? extends K> set,
        final Comparator<Object> cmp);

    boolean __remove(final K key);

    boolean __removeEquivalent(final K key, final Comparator<Object> cmp);

    boolean __removeAll(final java.util.Set<? extends K> set);

    boolean __removeAllEquivalent(final java.util.Set<? extends K> set,
        final Comparator<Object> cmp);

    boolean __retainAll(final java.util.Set<? extends K> set);

    boolean __retainAllEquivalent(final Set.Transient<? extends K> transientSet,
        final Comparator<Object> cmp);

  }

}
