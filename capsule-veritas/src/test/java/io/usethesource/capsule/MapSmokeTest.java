/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import io.usethesource.capsule.core.PersistentTrieMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MapSmokeTest {

  /*
   * UTILS
   */
  private static Class<PersistentTrieMap> targetMapClass = PersistentTrieMap.class;

  private static Method persistentMapOfEmpty;
  private static Method persistentMapOfKeyValuePairs;

  private static Method transientMapOfEmpty;
  private static Method transientMapOfKeyValuePairs;

  public static Class<PersistentTrieMap> getTargetMapClass() {
    return targetMapClass;
  }

  static {
    try {
      persistentMapOfEmpty = targetMapClass.getMethod("of");
      persistentMapOfKeyValuePairs = targetMapClass.getMethod("of", Object[].class);

      transientMapOfEmpty = targetMapClass.getMethod("of");
      transientMapOfKeyValuePairs = targetMapClass.getMethod("of", Object[].class);
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static final <K, V> Map.Immutable<K, V> mapOf() {
    try {
      return (Map.Immutable<K, V>) persistentMapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static final <K, V> Map.Immutable<K, V> mapOf(Object... keyValuePairs) {
    try {
      return (Map.Immutable<K, V>) persistentMapOfKeyValuePairs
          .invoke(null, (Object) keyValuePairs);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static final <K, V> Map.Transient<K, V> transientMapOf() {
    try {
      return (Map.Transient<K, V>) transientMapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static final <K, V> Map.Transient<K, V> transientMapOf(Object... keyValuePairs) {
    try {
      return (Map.Transient<K, V>) transientMapOfKeyValuePairs.invoke(null, (Object) keyValuePairs);
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

    Map.Immutable<Integer, Integer> map = (Map.Immutable) mapOf();

    for (int i = size; i > 0; i--) {
      Map.Immutable<Integer, Integer> res = map.__put(i, i);
      assert res.containsKey(i);
      map = res;
    }

    getTargetMapClass().cast(map).printStatistics();
  }

  @Test
  public void testPrintStatsRandom() {
    // int size = 128;

    Map.Immutable<Integer, Integer> map = (Map.Immutable) mapOf();

    Random rand = new Random(13);

    for (int i = size; i > 0; i--) {
      final int j = rand.nextInt();

      Map.Immutable<Integer, Integer> res = map.__put(j, j);
      assert res.containsKey(j);
      map = res;
    }

    getTargetMapClass().cast(map).printStatistics();
  }

  @Test
  public void testCheckPrefixConstruction() {
    // int size = 128;

    Map.Immutable<Integer, Integer> map = (Map.Immutable) mapOf();

    Map.Immutable<Integer, Integer> res1 = map.__put(63, 63).__put(64, 64).__put(32768, 32768)
        .__put(2147483647, 2147483647).__put(65536, 65536);

    assert res1.containsKey(63);
    assert res1.containsKey(64);
    assert res1.containsKey(32768);
    assert res1.containsKey(65536);
    assert res1.containsKey(2147483647);

    Map.Immutable<Integer, Integer> res2 = map.__put(2147483647, 2147483647).__put(32768, 32768)
        .__put(63, 63).__put(64, 64).__put(65536, 65536);

    assert res2.containsKey(63);
    assert res2.containsKey(64);
    assert res2.containsKey(32768);
    assert res2.containsKey(65536);
    assert res2.containsKey(2147483647);

    assert res1.equals(res2);

    getTargetMapClass().cast(map).printStatistics();
  }

  @Test
  public void testCheckCompactionFromBeginUponDelete() {

    Map.Immutable<Integer, Integer> map = (Map.Immutable) mapOf();

    Map.Immutable<Integer, Integer> res1 = map.__put(1, 1).__put(2, 2);

    Map.Immutable<Integer, Integer> res2 = res1.__put(32769, 32769).__remove(2);

    // what to test for?
    assert !res1.equals(res2);
  }

  @Test
  public void testCheckCompactionFromMiddleUponDelete() {

    Map.Immutable<Integer, Integer> map = (Map.Immutable) mapOf();

    Map.Immutable<Integer, Integer> res1 = map.__put(1, 1).__put(2, 2).__put(65, 65).__put(66, 66);

    Map.Immutable<Integer, Integer> res2 = res1.__put(32769, 32769).__remove(66);

    // what to test for?
    assert !res1.equals(res2);
  }

  public static PureSeparateHashCodeInteger p(int value, int hash) {
    return new PureSeparateHashCodeInteger(value, hash);
  }

  public static PureSeparateHashCodeInteger p(int value) {
    return new PureSeparateHashCodeInteger(value, value);
  }

  @Test
  public void testCheckCompactionFromBeginUponDelete_HashCollisionNode1() {

    Map.Immutable map = mapOf();

    Map.Immutable res1 = map.__put(p(11, 1), p(11, 1)).__put(p(12, 1), p(12, 1));
    assertTrue(res1.containsKey(p(11, 1)));
    assertTrue(res1.containsKey(p(12, 1)));

    Map.Immutable res2 = res1.__remove(p(12, 1));
    assertTrue(res2.containsKey(p(11, 1)));
    assertEquals(mapOf(p(11, 1), p(11, 1)), res2);

    Map.Immutable res3 = res1.__remove(p(11, 1));
    assertTrue(res3.containsKey(p(12, 1)));
    assertEquals(mapOf(p(12, 1), p(12, 1)), res3);

    Map.Immutable resX = res1.__put(p(32769), p(32769)).__remove(p(12, 1));
    assertTrue(resX.containsKey(p(11, 1)));
    assertTrue(resX.containsKey(p(32769)));

    // what to test for?
    assert !res1.equals(resX);
  }

  @Test
  public void testCheckCompactionFromBeginUponDelete_HashCollisionNode2() {

    Map.Immutable map = mapOf();

    Map.Immutable res1 =
        map.__put(p(32769_1, 32769), p(32769_1, 32769)).__put(p(32769_2, 32769), p(32769_2, 32769));
    assertEquals(2, res1.size());
    assertTrue(res1.containsKey(p(32769_1, 32769)));
    assertTrue(res1.containsKey(p(32769_2, 32769)));

    Map.Immutable res2 = res1.__put(p(1, 1), p(1, 1));
    assertEquals(3, res2.size());
    assertTrue(res2.containsKey(p(1, 1)));
    assertTrue(res2.containsKey(p(32769_1, 32769)));
    assertTrue(res2.containsKey(p(32769_2, 32769)));

    Map.Immutable res3 = res2.__remove(p(32769_2, 32769));
    assertEquals(2, res3.size());
    assertTrue(res3.containsKey(p(1, 1)));
    assertTrue(res3.containsKey(p(32769_1, 32769)));

    Map.Immutable expected = mapOf(p(1, 1), p(1, 1), p(32769_1, 32769), p(32769_1, 32769));
    assertEquals(expected, res3);
  }

  @Test
  public void testCheckCompactionFromBeginUponDelete_HashCollisionNode3() {

    Map.Immutable map = mapOf();

    Map.Immutable res1 =
        map.__put(p(32769_1, 32769), p(32769_1, 32769)).__put(p(32769_2, 32769), p(32769_2, 32769));
    assertEquals(2, res1.size());
    assertTrue(res1.containsKey(p(32769_1, 32769)));
    assertTrue(res1.containsKey(p(32769_2, 32769)));

    Map.Immutable res2 = res1.__put(p(1, 1), p(1, 1));
    assertEquals(3, res2.size());
    assertTrue(res2.containsKey(p(1, 1)));
    assertTrue(res2.containsKey(p(32769_1, 32769)));
    assertTrue(res2.containsKey(p(32769_2, 32769)));

    Map.Immutable res3 = res2.__remove(p(1, 1));
    assertEquals(2, res3.size());
    assertTrue(res3.containsKey(p(32769_1, 32769)));
    assertTrue(res3.containsKey(p(32769_2, 32769)));

    assertEquals(res1, res3);
  }

  @Test
  public void testCheckCompactionFromBeginUponDelete_HashCollisionNode4() {

    Map.Immutable map = mapOf();

    Map.Immutable res1 =
        map.__put(p(32769_1, 32769), p(32769_1, 32769)).__put(p(32769_2, 32769), p(32769_2, 32769));
    assertEquals(2, res1.size());
    assertTrue(res1.containsKey(p(32769_1, 32769)));
    assertTrue(res1.containsKey(p(32769_2, 32769)));

    Map.Immutable res2 = res1.__put(p(5), p(5));
    assertEquals(3, res2.size());
    assertTrue(res2.containsKey(p(5)));
    assertTrue(res2.containsKey(p(32769_1, 32769)));
    assertTrue(res2.containsKey(p(32769_2, 32769)));

    Map.Immutable res3 = res2.__remove(p(5));
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

  // @Test
  // public void testPrintStatsRandomSmallAndBigIntegers() {
  // TrieMap_Heterogeneous map = (TrieMap_Heterogeneous) TrieMap_Heterogeneous.of();
  // long smallCount = 0;
  // long bigCount = 0;
  //
  // Random rand = new Random(13);
  //
  // for (int i = size; i > 0; i--) {
  // final int j = rand.nextInt();
  // // System.out.println(j);
  //
  // final BigInteger bigJ = BigInteger.valueOf(j).multiply(BigInteger.valueOf(j));
  // // System.out.println(bigJ);
  //
  // if (i % 20 == 0) { // earlier: bigJ.bitLength() > 31
  // // System.out.println("BIG");
  // bigCount++;
  // TrieMap_Heterogeneous res = (TrieMap_Heterogeneous) map.__put(bigJ, bigJ);
  // assert res.containsKey(bigJ);
  // map = res;
  // } else {
  // // System.out.println("SMALL");
  // smallCount++;
  // TrieMap_Heterogeneous res = (TrieMap_Heterogeneous) map.__put(j, j);
  // assert res.containsKey(j);
  // map = res;
  // }
  // }
  //
  // // map.printStatistics();
  // // System.out.println(map);
  //
  // System.out.println();
  // System.out.println(String.format("PRIMITIVE: %10d (%.2f percent)", smallCount, 100.
  // * smallCount / (smallCount + bigCount)));
  // System.out.println(String.format("BIG_INTEGER: %10d (%.2f percent)", bigCount, 100.
  // * bigCount / (smallCount + bigCount)));
  // System.out.println(String.format("UNIQUE: %10d (%.2f percent)", map.size(),
  // 100. * map.size() / (smallCount + bigCount)));
  // System.out.println();
  // }

  @Test
  public void testCreateSingletonWithFactoryMethod() {
    Map.Immutable<Integer, Integer> map = mapOf(63, 65);
    assertTrue(map.containsKey(63));
    assertEquals(Integer.valueOf(65), map.get(63));
  }

  @Test
  public void testRemoveFromSingleton() {
    Map.Immutable<Integer, Integer> map = mapOf(63, 65);
    Map.Immutable<Integer, Integer> res = map.__remove(63);
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
