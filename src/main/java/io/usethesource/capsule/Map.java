/*******************************************************************************
 * Copyright (c) 2013-2016 CWI All rights reserved. This program and the accompanying materials are
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

public interface Map<K, V> extends Iterable<K>, Function<K, Optional<V>> {

  long size();

  boolean isEmpty();  
  
  boolean contains(final Object o);
  
  boolean containsValue(final Object o);
  
  // default boolean containsAll(final Set<K> set) {
  // for (K item : set) {
  // if (!contains(item)) {
  // return false;
  // }
  // }
  // return true;
  //
  // }

  // K get(final Object o);  
      
  @Override
  SupplierIterator<K, V> iterator();

//  public Iterator<V> valueIterator();

  // @Deprecated // TODO: replace with SupplierIterator interface
  public Iterator<java.util.Map.Entry<K, V>> entryIterator(); 

//  @Deprecated // TODO: replace with SupplierIterator interface
//  Set<java.util.Map.Entry<K, V>> entrySet();
  
  /**
   * The hash code of a map is order independent by combining the hashes of the elements (both keys
   * and values) via a bitwise XOR operation.
   * 
   * @return XOR reduction of all hashes of elements
   */  
  @Override
  int hashCode();

  @Override
  boolean equals(Object other);  

  Map.Immutable<K, V> asImmutable();
  
  public static interface Immutable<K, V> extends Map<K, V> {

    Map.Immutable<K, V> insert(final K key, final V val);

    Map.Immutable<K, V> remove(final K key);
    
    Map.Immutable<K, V> insertAll(final Map<? extends K, ? extends V> map);

    boolean isTransientSupported();

    Map.Transient<K, V> asTransient();
    
    java.util.Map<K, V> asJdkCollection();    
  }
  
  public static interface Transient<K, V> extends Map<K, V> {

    V insert(final K key, final V val);

    V remove(final K key);
    
    boolean insertAll(final Map<? extends K, ? extends V> map);
    
  }

}
