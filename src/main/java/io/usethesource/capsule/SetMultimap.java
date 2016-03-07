/*******************************************************************************
 * Copyright (c) 2015 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *   * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI  
 *******************************************************************************/
package io.usethesource.capsule;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public interface SetMultimap<K, V> {

	V put(final K key, final V val);

	V remove(final java.lang.Object key, final java.lang.Object val);

	void putAll(final SetMultimap<? extends K, ? extends V> multimap);

	boolean containsValue(Object value);	

	Set<V> get(final java.lang.Object o);

	Set<V> getEquivalent(final java.lang.Object o, final Comparator<Object> cmp);	
	
	Set<K> keySet();
	Collection<V> values();	
	Set<Map.Entry<K, V>> entrySet();	
	
	void clear();

	int size();

	boolean isEmpty();

}
