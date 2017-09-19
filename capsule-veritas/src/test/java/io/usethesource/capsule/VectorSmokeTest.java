/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.usethesource.capsule.core.PersistentTrieSet;
import io.usethesource.capsule.core.PersistentTrieVector;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class VectorSmokeTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @Test
  public void testPushFrontAndGet() {
    final int MIN_INDEX = 0;
    final int MAX_INDEX = 1024;
    final int SIZE = MAX_INDEX - MIN_INDEX + 1;

    int[] input = IntStream.rangeClosed(MIN_INDEX, MAX_INDEX).toArray();

    io.usethesource.capsule.Vector.Immutable<Integer> vector = PersistentTrieVector.of();

    for (Integer item : input) {
      vector = vector.pushFront(item);
    }

    assert vector.size() == SIZE;

    for (int i = 0; i < input.length; i++) {
      assertEquals(Integer.valueOf(input[i]), vector.get(MAX_INDEX - i).get());
    }
  }

  @Test
  public void testPushBackAndGet() {
    final int MIN_INDEX = 0;
    final int MAX_INDEX = 1024;
    final int SIZE = MAX_INDEX - MIN_INDEX + 1;

    int[] input = IntStream.rangeClosed(MIN_INDEX, MAX_INDEX).toArray();

    io.usethesource.capsule.Vector.Immutable<Integer> vector = PersistentTrieVector.of();

    for (Integer item : input) {
      vector = vector.pushBack(item);
    }

    assert vector.size() == SIZE;

    for (int i = 0; i < input.length; i++) {
      assertEquals(Integer.valueOf(input[i]), vector.get(i).get());
    }
  }

  @Test
  public void testMixPushFrontAndPushBackAndGet() {
    final int MIN_INDEX = -1024;
    final int MID_INDEX = 0;
    final int MAX_INDEX = 1023;
    final int SIZE = MAX_INDEX - MIN_INDEX + 1;

    int[] inputPushFront = IntStream.range(MIN_INDEX, MID_INDEX).toArray();
    int[] reversedInputPushFront = reverse(inputPushFront);
    int[] inputPushBack = IntStream.rangeClosed(MID_INDEX, MAX_INDEX).toArray();

    io.usethesource.capsule.Vector.Immutable<Integer> vector = PersistentTrieVector.of();

    for (Integer item : inputPushBack) {
      vector = vector.pushBack(item);
    }

    for (Integer item : reversedInputPushFront) {
      vector = vector.pushFront(item);
    }

    assert vector.size() == SIZE;

    int[] allInput = IntStream.concat(Arrays.stream(inputPushFront), Arrays.stream(inputPushBack)).toArray();

    for (int i = 0; i < allInput.length; i++) {
      assertEquals(Integer.valueOf(allInput[i]), vector.get(i).get());
    }
  }

  private static final int[] reverse(int[] src) {
    int length = src.length;
    int[] dst = new int[length];

    for (int i = 0, j = length - 1; i < (length + 1) / 2; i++, j--) {
      dst[i] = src[j];
      dst[j] = src[i];
    }

    return dst;
  }

}
