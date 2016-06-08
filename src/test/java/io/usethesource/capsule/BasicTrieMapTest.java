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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import org.junit.Test;

import io.usethesource.capsule.ImmutableMap;
import io.usethesource.capsule.TransientMap;
import io.usethesource.capsule.TrieMap_5Bits;

public class BasicTrieMapTest {

	/*
	 * UTILS
	 */
	@SuppressWarnings("rawtypes")
	private static Class<TrieMap_5Bits> targetMapClass = TrieMap_5Bits.class;

	private static Method persistentMapOfEmpty;
	private static Method persistentMapOfKeyValuePairs;

	private static Method transientMapOfEmpty;
	private static Method transientMapOfKeyValuePairs;

	@SuppressWarnings("rawtypes")
	public static Class<TrieMap_5Bits> getTargetMapClass() {
		return targetMapClass;
	}

	static {
		try {
			persistentMapOfEmpty = targetMapClass.getMethod("of");
			persistentMapOfKeyValuePairs = targetMapClass.getMethod("of", Object[].class);

			transientMapOfEmpty = targetMapClass.getMethod("transientOf");
			transientMapOfKeyValuePairs = targetMapClass.getMethod("transientOf", Object[].class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static final <K, V> ImmutableMap<K, V> mapOf() {
		try {
			return (ImmutableMap<K, V>) persistentMapOfEmpty.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static final <K, V> ImmutableMap<K, V> mapOf(Object... keyValuePairs) {
		try {
			return (ImmutableMap<K, V>) persistentMapOfKeyValuePairs.invoke(null,
							(Object) keyValuePairs);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static final <K, V> TransientMap<K, V> transientMapOf() {
		try {
			return (TransientMap<K, V>) transientMapOfEmpty.invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static final <K, V> TransientMap<K, V> transientMapOf(Object... keyValuePairs) {
		try {
			return (TransientMap<K, V>) transientMapOfKeyValuePairs.invoke(null,
							(Object) keyValuePairs);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/*
	 * TESTS
	 */

	final static int size = (int) Math.pow(2, 10);

	@Test
	public void testPrintStatsSequential() {
		// int size = 128;

		ImmutableMap<Integer, Integer> map = (ImmutableMap) mapOf();

		for (int i = size; i > 0; i--) {
			ImmutableMap<Integer, Integer> res = (ImmutableMap) map.__put(i, i);
			assert res.containsKey(i);
			map = res;
		}

		getTargetMapClass().cast(map).printStatistics();
	}

	@Test
	public void testPrintStatsRandom() {
		// int size = 128;

		ImmutableMap<Integer, Integer> map = (ImmutableMap) mapOf();

		Random rand = new Random(13);

		for (int i = size; i > 0; i--) {
			final int j = rand.nextInt();

			ImmutableMap<Integer, Integer> res = (ImmutableMap) map.__put(j, j);
			assert res.containsKey(j);
			map = res;
		}

		getTargetMapClass().cast(map).printStatistics();
	}

	@Test
	public void testCheckPrefixConstruction() {
		// int size = 128;

		ImmutableMap<Integer, Integer> map = (ImmutableMap) mapOf();

		ImmutableMap<Integer, Integer> res1 = (ImmutableMap) map.__put(63, 63).__put(64, 64)
						.__put(32768, 32768).__put(2147483647, 2147483647).__put(65536, 65536);

		assert res1.containsKey(63);
		assert res1.containsKey(64);
		assert res1.containsKey(32768);
		assert res1.containsKey(65536);
		assert res1.containsKey(2147483647);

		ImmutableMap<Integer, Integer> res2 = (ImmutableMap) map.__put(2147483647, 2147483647)
						.__put(32768, 32768).__put(63, 63).__put(64, 64).__put(65536, 65536);

		assert res2.containsKey(63);
		assert res2.containsKey(64);
		assert res2.containsKey(32768);
		assert res2.containsKey(65536);
		assert res2.containsKey(2147483647);

		assert res1.equals(res2);

		getTargetMapClass().cast(map).printStatistics();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCheckCompactionFromBeginUponDelete() {

		ImmutableMap<Integer, Integer> map = (ImmutableMap) mapOf();

		ImmutableMap<Integer, Integer> res1 = (ImmutableMap) map.__put(1, 1).__put(2, 2);

		ImmutableMap<Integer, Integer> res2 = (ImmutableMap) res1.__put(32769, 32769).__remove(2);

		// what to test for?
		assert !res1.equals(res2);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCheckCompactionFromMiddleUponDelete() {

		ImmutableMap<Integer, Integer> map = (ImmutableMap) mapOf();

		ImmutableMap<Integer, Integer> res1 = (ImmutableMap) map.__put(1, 1).__put(2, 2)
						.__put(65, 65).__put(66, 66);

		ImmutableMap<Integer, Integer> res2 = (ImmutableMap) res1.__put(32769, 32769).__remove(66);

		// what to test for?
		assert !res1.equals(res2);
	}

	public static PureSeparateHashCodeInteger p(int value, int hash) {
		return new PureSeparateHashCodeInteger(value, hash);
	}

	public static PureSeparateHashCodeInteger p(int value) {
		return new PureSeparateHashCodeInteger(value, value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCheckCompactionFromBeginUponDelete_HashCollisionNode1() {

		ImmutableMap map = (ImmutableMap) mapOf();

		ImmutableMap res1 = (ImmutableMap) map.__put(p(11, 1), p(11, 1)).__put(p(12, 1), p(12, 1));
		assertTrue(res1.containsKey(p(11, 1)));
		assertTrue(res1.containsKey(p(12, 1)));

		ImmutableMap res2 = (ImmutableMap) res1.__remove(p(12, 1));
		assertTrue(res2.containsKey(p(11, 1)));
		assertEquals(mapOf(p(11, 1), p(11, 1)), res2);

		ImmutableMap res3 = (ImmutableMap) res1.__remove(p(11, 1));
		assertTrue(res3.containsKey(p(12, 1)));
		assertEquals(mapOf(p(12, 1), p(12, 1)), res3);

		ImmutableMap resX = (ImmutableMap) res1.__put(p(32769), p(32769)).__remove(p(12, 1));
		assertTrue(resX.containsKey(p(11, 1)));
		assertTrue(resX.containsKey(p(32769)));

		// what to test for?
		assert !res1.equals(resX);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCheckCompactionFromBeginUponDelete_HashCollisionNode2() {

		ImmutableMap map = (ImmutableMap) mapOf();

		ImmutableMap res1 = (ImmutableMap) map.__put(p(32769_1, 32769), p(32769_1, 32769)).__put(
						p(32769_2, 32769), p(32769_2, 32769));
		assertEquals(2, res1.size());
		assertTrue(res1.containsKey(p(32769_1, 32769)));
		assertTrue(res1.containsKey(p(32769_2, 32769)));

		ImmutableMap res2 = (ImmutableMap) res1.__put(p(1, 1), p(1, 1));
		assertEquals(3, res2.size());
		assertTrue(res2.containsKey(p(1, 1)));
		assertTrue(res2.containsKey(p(32769_1, 32769)));
		assertTrue(res2.containsKey(p(32769_2, 32769)));

		ImmutableMap res3 = (ImmutableMap) res2.__remove(p(32769_2, 32769));
		assertEquals(2, res3.size());
		assertTrue(res3.containsKey(p(1, 1)));
		assertTrue(res3.containsKey(p(32769_1, 32769)));

		ImmutableMap expected = (ImmutableMap) mapOf(p(1, 1), p(1, 1), p(32769_1, 32769),
						p(32769_1, 32769));
		assertEquals(expected, res3);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCheckCompactionFromBeginUponDelete_HashCollisionNode3() {

		ImmutableMap map = (ImmutableMap) mapOf();

		ImmutableMap res1 = (ImmutableMap) map.__put(p(32769_1, 32769), p(32769_1, 32769)).__put(
						p(32769_2, 32769), p(32769_2, 32769));
		assertEquals(2, res1.size());
		assertTrue(res1.containsKey(p(32769_1, 32769)));
		assertTrue(res1.containsKey(p(32769_2, 32769)));

		ImmutableMap res2 = (ImmutableMap) res1.__put(p(1, 1), p(1, 1));
		assertEquals(3, res2.size());
		assertTrue(res2.containsKey(p(1, 1)));
		assertTrue(res2.containsKey(p(32769_1, 32769)));
		assertTrue(res2.containsKey(p(32769_2, 32769)));

		ImmutableMap res3 = (ImmutableMap) res2.__remove(p(1, 1));
		assertEquals(2, res3.size());
		assertTrue(res3.containsKey(p(32769_1, 32769)));
		assertTrue(res3.containsKey(p(32769_2, 32769)));

		assertEquals(res1, res3);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCheckCompactionFromBeginUponDelete_HashCollisionNode4() {

		ImmutableMap map = (ImmutableMap) mapOf();

		ImmutableMap res1 = (ImmutableMap) map.__put(p(32769_1, 32769), p(32769_1, 32769)).__put(
						p(32769_2, 32769), p(32769_2, 32769));
		assertEquals(2, res1.size());
		assertTrue(res1.containsKey(p(32769_1, 32769)));
		assertTrue(res1.containsKey(p(32769_2, 32769)));

		ImmutableMap res2 = (ImmutableMap) res1.__put(p(5), p(5));
		assertEquals(3, res2.size());
		assertTrue(res2.containsKey(p(5)));
		assertTrue(res2.containsKey(p(32769_1, 32769)));
		assertTrue(res2.containsKey(p(32769_2, 32769)));

		ImmutableMap res3 = (ImmutableMap) res2.__remove(p(5));
		assertEquals(2, res3.size());
		assertTrue(res3.containsKey(p(32769_1, 32769)));
		assertTrue(res3.containsKey(p(32769_2, 32769)));

		assertEquals(res1, res3);
	}

	@Test
	public void testRecoverMask() {
		byte mask = recoverMask(-2147483648, (byte) 1);
		assertTrue(mask == 31);
	}

	static byte recoverMask(int map, byte i_th) {
		assert 1 <= i_th && i_th <= 32;

		byte cnt1 = 0;
		byte mask = 0;

		while (mask < 32) {
			if ((map & 0x01) == 0x01) {
				cnt1 += 1;

				if (cnt1 == i_th) {
					return mask;
				}
			}

			map = map >> 1;
			mask += 1;
		}

		throw new RuntimeException("Called with invalid arguments."); // cnt1 !=
																		// i_th
	}

//	@Test
//	public void testPrintStatsRandomSmallAndBigIntegers() {
//		TrieMap_Heterogeneous map = (TrieMap_Heterogeneous) TrieMap_Heterogeneous.of();
//		long smallCount = 0;
//		long bigCount = 0;
//
//		Random rand = new Random(13);
//
//		for (int i = size; i > 0; i--) {
//			final int j = rand.nextInt();
//			// System.out.println(j);
//
//			final BigInteger bigJ = BigInteger.valueOf(j).multiply(BigInteger.valueOf(j));
//			// System.out.println(bigJ);
//
//			if (i % 20 == 0) { // earlier: bigJ.bitLength() > 31
//				// System.out.println("BIG");
//				bigCount++;
//				TrieMap_Heterogeneous res = (TrieMap_Heterogeneous) map.__put(bigJ, bigJ);
//				assert res.containsKey(bigJ);
//				map = res;
//			} else {
//				// System.out.println("SMALL");
//				smallCount++;
//				TrieMap_Heterogeneous res = (TrieMap_Heterogeneous) map.__put(j, j);
//				assert res.containsKey(j);
//				map = res;
//			}
//		}
//
//		// map.printStatistics();
//		// System.out.println(map);
//
//		System.out.println();
//		System.out.println(String.format("PRIMITIVE:   %10d (%.2f percent)", smallCount, 100.
//						* smallCount / (smallCount + bigCount)));
//		System.out.println(String.format("BIG_INTEGER: %10d (%.2f percent)", bigCount, 100.
//						* bigCount / (smallCount + bigCount)));
//		System.out.println(String.format("UNIQUE:      %10d (%.2f percent)", map.size(),
//						100. * map.size() / (smallCount + bigCount)));
//		System.out.println();
//	}

	@Test
	public void testCreateSingletonWithFactoryMethod() {
		ImmutableMap<Integer, Integer> map = mapOf(63, 65);
		assertTrue(map.containsKey(63));
		assertEquals(Integer.valueOf(65), map.get(63));
	}

	@Test
	public void testRemoveFromSingleton() {
		ImmutableMap<Integer, Integer> map = mapOf(63, 65);
		ImmutableMap<Integer, Integer> res = map.__remove(63);
		assertTrue(res.isEmpty());
		assertFalse(res.containsKey(63));
		assertEquals(mapOf(), res);
	}

}

class PureSeparateHashCodeInteger {
	
	private final int value;
	private final int hash;

	PureSeparateHashCodeInteger(int value, int hash) {
		this.value = value;
		this.hash = hash;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}

		if (other instanceof PureSeparateHashCodeInteger) {
			int otherValue = ((PureSeparateHashCodeInteger) other).value;
			
			return value == otherValue;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%d [hash = %d]", value, hash);
	}

}