/*******************************************************************************
 * Copyright (c) 2014 CWI
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

import java.util.NoSuchElementException;

public class EmptySupplierIterator<K, V> implements SupplierIterator<K, V> {

	@SuppressWarnings("rawtypes")
	private static final SupplierIterator EMPTY_ITERATOR = new EmptySupplierIterator();

	@SuppressWarnings("unchecked")
	public static <K, V> SupplierIterator<K, V> emptyIterator() {
		return EMPTY_ITERATOR;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public K next() {
		throw new NoSuchElementException();
	}

	@Override
	public V get() {
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}