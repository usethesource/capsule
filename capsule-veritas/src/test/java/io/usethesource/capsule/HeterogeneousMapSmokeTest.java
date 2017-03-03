/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.math.BigInteger;
import java.util.Random;

import io.usethesource.capsule.experimental.heterogeneous.TrieMap_Heterogeneous_BleedingEdge;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HeterogeneousMapSmokeTest {

  final static int size = 2097152;
  // final static int size = (int) Math.pow(2, 10);

  @Test
  public void testPrintStatsRandomSmallAndBigIntegers() {
    TrieMap_Heterogeneous_BleedingEdge map =
        (TrieMap_Heterogeneous_BleedingEdge) TrieMap_Heterogeneous_BleedingEdge.of();
    long smallCount = 0;
    long bigCount = 0;

    Random rand = new Random(13);

    for (int i = size; i > 0; i--) {
      final int j = rand.nextInt();
      // System.out.println(j);

      final BigInteger bigJ = BigInteger.valueOf(j).multiply(BigInteger.valueOf(j));
      // System.out.println(bigJ);

      final Integer boxedJ = Integer.valueOf(j);
      // System.out.println(boxedJ);

      if (i % 20 == 0) { // earlier: bigJ.bitLength() > 31
        // System.out.println("BIG");
        bigCount++;

        TrieMap_Heterogeneous_BleedingEdge res =
            (TrieMap_Heterogeneous_BleedingEdge) map.__put(bigJ, bigJ);
        assert res.containsKey(bigJ);

        // TrieMap_Heterogeneous_BleedingEdge res =
        // (TrieMap_Heterogeneous_BleedingEdge) map.__put(boxedJ, boxedJ);
        // assertTrue(res.containsKey(boxedJ));
        // because of non-overlap constraint
        // assertTrue(res.containsKey(j) || res.containsKey(boxedJ));

        map = res;
      } else {
        // System.out.println("SMALL");
        smallCount++;
        TrieMap_Heterogeneous_BleedingEdge res =
            (TrieMap_Heterogeneous_BleedingEdge) map.__put(j, j);
        assertTrue(res.containsKey(j));

        // assertTrue(res.containsKey(j));
        // because of non-overlap constraint
        // assertTrue(res.containsKey(j) || res.containsKey(boxedJ));

        map = res;
      }
    }

    // map.printStatistics();
    // System.out.println(map);

    System.out.println();
    System.out.println(String.format("PRIMITIVE: %10d (%.2f percent)", smallCount,
        100. * smallCount / (smallCount + bigCount)));
    System.out.println(String.format("BIG_INTEGER: %10d (%.2f percent)", bigCount,
        100. * bigCount / (smallCount + bigCount)));
    System.out.println(String.format("UNIQUE: %10d (%.2f percent)", map.size(),
        100. * map.size() / (smallCount + bigCount)));
    System.out.println();
  }

  static final int populationCountPattern01(int v) {
    int c = (v & 0x55555555) & (((v >> 1) & 0x55555555) ^ 0x55555555);
    c = (c & 0x33333333) + ((c >> 2) & 0x33333333);
    c = (c & 0x0F0F0F0F) + ((c >> 4) & 0x0F0F0F0F);
    c = (c & 0x00FF00FF) + ((c >> 8) & 0x00FF00FF);
    c = (c & 0x0000FFFF) + ((c >> 16) & 0x0000FFFF);
    return c;
  }

  @Test
  public void testPopulationCountPattern01() {
    assertEquals(2, populationCountPattern01(0b010001));
    assertEquals(3, populationCountPattern01(0b010101));
    assertEquals(2, populationCountPattern01(0b011001));
    assertEquals(2, populationCountPattern01(0b011101));
  }

  static final int populationCountPattern10(int v) {
    int c = ((v & 0x55555555) ^ 0x55555555) & ((v >> 1) & 0x55555555);
    c = (c & 0x33333333) + ((c >> 2) & 0x33333333);
    c = (c & 0x0F0F0F0F) + ((c >> 4) & 0x0F0F0F0F);
    c = (c & 0x00FF00FF) + ((c >> 8) & 0x00FF00FF);
    c = (c & 0x0000FFFF) + ((c >> 16) & 0x0000FFFF);
    return c;
  }

  @Test
  public void testPopulationCountPattern10() {
    assertEquals(2, populationCountPattern10(0b100010));
    assertEquals(3, populationCountPattern10(0b101010));
    assertEquals(2, populationCountPattern10(0b100110));
    assertEquals(2, populationCountPattern10(0b101110));
  }

  static final int populationCountPattern11(int v) {
    int c = (v & 0x55555555) & ((v >> 1) & 0x55555555);
    c = (c & 0x33333333) + ((c >> 2) & 0x33333333);
    c = (c & 0x0F0F0F0F) + ((c >> 4) & 0x0F0F0F0F);
    c = (c & 0x00FF00FF) + ((c >> 8) & 0x00FF00FF);
    c = (c & 0x0000FFFF) + ((c >> 16) & 0x0000FFFF);
    return c;
  }

  @Test
  public void testPopulationCountPattern11() {
    assertEquals(2, populationCountPattern11(0b110011));
    assertEquals(3, populationCountPattern11(0b111111));
    assertEquals(2, populationCountPattern11(0b110111));
    assertEquals(2, populationCountPattern11(0b111011));
  }

}
