/*******************************************************************************
 * Copyright (c) 2013-2015 CWI
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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;

public interface TransientSetMultimap<K, V> extends SetMultimap<K, V> {

	ImmutableSet<V> get(final Object o);

	ImmutableSet<V> getEquivalent(final Object o, final Comparator<Object> cmp);

	boolean containsKey(final Object o);

	boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp);

	boolean containsValue(final Object o);

	boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp);

	boolean containsEntry(final Object o0, final Object o1);

	boolean containsEntryEquivalent(final Object o0, final Object o1, final Comparator<Object> cmp);

	boolean __put(final K key, final V val);

	boolean __putEquivalent(final K key, final V val, final Comparator<Object> cmp);

	boolean __putAll(final SetMultimap<? extends K, ? extends V> setMultimap);

	boolean __putAllEquivalent(final SetMultimap<? extends K, ? extends V> setMultimap,
					final Comparator<Object> cmp);

	boolean __remove(final K key, final V val);

	boolean __removeEquivalent(final K key, final V val, final Comparator<Object> cmp);

	Iterator<K> keyIterator();

	Iterator<V> valueIterator();

	Iterator<Map.Entry<K, V>> entryIterator();

	<T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf);

	ImmutableSetMultimap<K, V> freeze();

}