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

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

public interface Set<K> extends Iterable<K>, Function<K, Optional<K>> {

  long size();

  boolean isEmpty();
  
  boolean contains(final Object o);

  default boolean containsAll(final Set<K> set) {
    for (K item : set) {
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
   * bitwise xor operation.
   * 
   * @return xor reduction of all hashes of elements
   */
  @Override
  int hashCode();

  @Override
  boolean equals(Object other);

  Set.Immutable<K> asImmutable();
  
  public static interface Immutable<K> extends Set<K> {

    Set.Immutable<K> insert(final K key);

    Set.Immutable<K> remove(final K key);

    Set.Immutable<K> insertAll(final Set<? extends K> set);

    Set.Immutable<K> removeAll(final Set<? extends K> set);

    Set.Immutable<K> retainAll(final Set<? extends K> set);

    boolean isTransientSupported();

    Set.Transient<K> asTransient();

  }

  public static interface Transient<K> extends Set<K> {

    boolean insert(final K key);

    boolean remove(final K key);

    boolean insertAll(final Set<? extends K> set);

    boolean removeAll(final Set<? extends K> set);

    boolean retainAll(final Set<? extends K> set);

  }

}
