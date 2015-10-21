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

public class ArraySupplierIterator<E> implements SupplierIterator<E, E> {

	final Object[] values;
	final int end;
	int currentIndex;
	E currentElement;

	public ArraySupplierIterator(final Object[] values, int start, int end) {
		assert start <= end && end <= values.length;

		this.values = values;
		this.end = end;
		this.currentIndex = start;
	}

	@Override
	public boolean hasNext() {
		return currentIndex < end;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next() {
		if (!hasNext())
			throw new NoSuchElementException();

		currentElement = (E) values[currentIndex++];
		return currentElement;
	}

	@Override
	public E get() {
		return currentElement;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static <E> SupplierIterator<E, E> of(Object[] array) {
		return new ArraySupplierIterator<>(array, 0, array.length);
	}

	public static <E> SupplierIterator<E, E> of(Object[] array, int start, int length) {
		return new ArraySupplierIterator<>(array, start, start + length);
	}

}
