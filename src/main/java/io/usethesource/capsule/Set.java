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
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

public interface Set<K> extends Iterable<K>, Function<K, Optional<K>> {

  boolean contains(final Object o);

  boolean containsAll(final Collection<?> c);

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

  public static interface Immutable<K> extends Set<K> {

    Set.Immutable<K> insert(final K key);

    Set.Immutable<K> remove(final K key);

    Set.Immutable<K> insertAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> removeAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> retainAll(final java.util.Set<? extends K> set);

    boolean isTransientSupported();

    Set.Transient<K> asTransient();

  }

  public static interface Transient<K> extends Set<K> {

    boolean insert(final K key);

    boolean remove(final K key);

    boolean insertAll(final java.util.Set<? extends K> set);

    boolean removeAll(final java.util.Set<? extends K> set);

    boolean retainAll(final java.util.Set<? extends K> set);

    Set.Immutable<K> asImmutable();

  }

}
