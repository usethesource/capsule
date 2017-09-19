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

  @Ignore
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

}
