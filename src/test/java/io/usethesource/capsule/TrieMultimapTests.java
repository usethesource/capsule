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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HHAMT_Specialized;

public class TrieMultimapTests {

	final static int size = 64;

	@Test
	public void testInsertTwoTuplesThatShareSameKey() {
		ImmutableSetMultimap<Integer, String> map = TrieSetMultimap_HHAMT_Specialized
						.<Integer, String> of().__insert(1, "x").__insert(1, "y");

		assertEquals(2, map.size());
		assertTrue(map.containsKey(1));
	}

	@Test
	public void testInsertTwoTuplesWithOneRemoveThatShareSameKeyX() {
		ImmutableSetMultimap<Integer, String> map = TrieSetMultimap_HHAMT_Specialized
						.<Integer, String> of().__insert(1, "x").__insert(1, "y").__removeEntry(1, "x");

		assertEquals(1, map.size());
		assertTrue(map.containsKey(1));
	}

	@Test
	public void testInsertTwoTuplesWithOneRemoveThatShareSameKeyY() {
		ImmutableSetMultimap<Integer, String> map = TrieSetMultimap_HHAMT_Specialized
						.<Integer, String> of().__insert(1, "x").__insert(1, "y").__removeEntry(1, "y");

		assertEquals(1, map.size());
		assertTrue(map.containsKey(1));
	}

	@Test
	public void testInsertTwoTuplesWithOneRemoveThatShareSameKeyXY() {
		ImmutableSetMultimap<Integer, String> map = TrieSetMultimap_HHAMT_Specialized
						.<Integer, String> of().__insert(1, "x").__insert(1, "y").__removeEntry(1, "x")
						.__removeEntry(1, "y");

		assertEquals(0, map.size());
		assertFalse(map.containsKey(1));
	}

	@Test
	public void testInsertTwoTuplesThatShareSameKey_Iterate() {
		ImmutableSetMultimap<Integer, String> map = TrieSetMultimap_HHAMT_Specialized
						.<Integer, String> of().__insert(1, "x").__insert(1, "y");

		Collection<String> values = map.values();

		assertEquals(2, values.size());
		assertTrue(values.contains("x"));
		assertTrue(values.contains("y"));
	}

}
