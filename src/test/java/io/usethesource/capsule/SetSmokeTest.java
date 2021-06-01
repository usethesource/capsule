/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.usethesource.capsule.core.PersistentTrieSet;
import io.usethesource.capsule.generators.CollidableInteger;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SetSmokeTest {
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @Test
  public void testNodeValNode() {
    Map<Integer, Integer> input = new LinkedHashMap<>();

    input.put(1, 1);
    input.put(2, 33);
    input.put(3, 3);
    input.put(4, 4);
    input.put(5, 4);
    input.put(6, 6);
    input.put(7, 7);
    input.put(8, 7);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> set = PersistentTrieSet.of();

    for (Entry<Integer, Integer> entry : input.entrySet()) {
      set = set.__insert(new CollidableInteger(entry.getKey(), entry.getValue()));
    }

    for (Entry<Integer, Integer> entry : input.entrySet()) {
      assertTrue(set.contains(new CollidableInteger(entry.getKey(), entry.getValue())));
    }
  }

  @Test
  public void testValNodeVal() {
    Map<Integer, Integer> input = new LinkedHashMap<>();

    input.put(1, 1);
    input.put(2, 2);
    input.put(3, 2);
    input.put(4, 4);
    input.put(5, 5);
    input.put(6, 5);
    input.put(7, 7);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> set = PersistentTrieSet.of();

    for (Entry<Integer, Integer> entry : input.entrySet()) {
      set = set.__insert(new CollidableInteger(entry.getKey(), entry.getValue()));
    }

    for (Entry<Integer, Integer> entry : input.entrySet()) {
      assertTrue(set.contains(new CollidableInteger(entry.getKey(), entry.getValue())));
    }
  }

  @Test
  public void testIteration() {
    Map<Integer, Integer> input = new LinkedHashMap<>();

    input.put(1, 1);
    input.put(2, 2);
    input.put(3, 2);
    input.put(4, 4);
    input.put(5, 5);
    input.put(6, 5);
    input.put(7, 7);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> set = PersistentTrieSet.of();

    for (Entry<Integer, Integer> entry : input.entrySet()) {
      set = set.__insert(new CollidableInteger(entry.getKey(), entry.getValue()));
    }

    Set<Integer> keys = input.keySet();

    for (CollidableInteger key : set) {
      keys.remove(key.value);
    }

    assertTrue(keys.isEmpty());
  }

  // @Test
  // public void testExtendedIteration() {
  // IValueFactory valueFactory = ValueFactory.getInstance();
  // int size = 10_000;
  //
  // ISetWriter writer = valueFactory.setWriter();
  //
  // Random random = new Random();
  // for (int i = size; i > 0; i--) {
  //// writer.insert(valueFactory.integer(i));
  //
  // // Random
  // writer.insert(valueFactory.integer(random.nextInt()));
  // }
  //
  // ISet testSet = writer.done();
  // int realSize = testSet.size();
  //
  // int countedSize = 0;
  // for (Object key : testSet) {
  // countedSize++;
  // }
  //
  // System.out.println(String.format("realSize[%d] == countedSize[%d]", realSize, countedSize));
  // assertTrue (realSize == countedSize);
  // }
  //
  // @Test
  // public void testEqualityAfterInsertDelete() {
  // IValueFactory valueFactory = ValueFactory.getInstance();
  // int size = 50;
  //
  // ISetWriter writer1 = valueFactory.setWriter();
  // ISetWriter writer2 = valueFactory.setWriter();
  //
  // for (int i = size; i > 0; i--) {
  // writer1.insert(valueFactory.integer(i));
  // writer2.insert(valueFactory.integer(i));
  // }
  //
  // ISet testSet = writer1.done();
  // ISet testSetDuplicate = writer2.done();
  //
  //// IValue VALUE_EXISTING = valueFactory.integer(size - 1);
  // IValue VALUE_NOT_EXISTING = valueFactory.integer(size + 1);
  //
  // testSetDuplicate = testSet.insert(VALUE_NOT_EXISTING);
  // testSetDuplicate = testSetDuplicate.delete(VALUE_NOT_EXISTING);
  //
  // boolean equals = testSet.equals(testSetDuplicate);
  //
  // assertTrue (equals);
  // }

  @Test
  public void IterateWithLastBitsDifferent() {
    CollidableInteger hash_n2147483648_obj1 = new CollidableInteger(1, -2147483648);
    CollidableInteger hash_p1073741824_obj2 = new CollidableInteger(2, 1073741824);

    Set<CollidableInteger> todo = new HashSet<>();
    todo.add(hash_n2147483648_obj1);
    todo.add(hash_p1073741824_obj2);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs = PersistentTrieSet
        .of(hash_n2147483648_obj1, hash_p1073741824_obj2);

    for (CollidableInteger x : xs) {
      todo.remove(x);
    }

    assertEquals(Collections.EMPTY_SET, todo);
  }

  @Test
  public void TwoCollisionsEquals() {
    CollidableInteger hash98304_obj1 = new CollidableInteger(1, 98304);
    CollidableInteger hash98304_obj2 = new CollidableInteger(2, 98304);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs = PersistentTrieSet
        .of(hash98304_obj1, hash98304_obj2);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys = PersistentTrieSet
        .of(hash98304_obj2, hash98304_obj1);

    assertEquals(xs, ys);
  }

  @Test
  public void ThreeCollisionsEquals() {
    CollidableInteger hash98304_obj1 = new CollidableInteger(1, 98304);
    CollidableInteger hash98304_obj2 = new CollidableInteger(2, 98304);
    CollidableInteger hash98304_obj3 = new CollidableInteger(3, 98304);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs = PersistentTrieSet
        .of(hash98304_obj1, hash98304_obj2, hash98304_obj3);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys = PersistentTrieSet
        .of(hash98304_obj3, hash98304_obj2, hash98304_obj1);

    assertEquals(xs, ys);
  }

  @Test
  public void RemovalFromCollisonNodeEqualsSingelton() {
    CollidableInteger hash98304_obj1 = new CollidableInteger(1, 98304);
    CollidableInteger hash98304_obj2 = new CollidableInteger(2, 98304);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs = PersistentTrieSet.of(hash98304_obj1);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys =
        PersistentTrieSet.of(hash98304_obj1, hash98304_obj2).__remove(hash98304_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionIterate() {
    CollidableInteger hash98304_obj1 = new CollidableInteger(1, 98304);
    CollidableInteger hash98304_obj2 = new CollidableInteger(2, 98304);

    Set<CollidableInteger> todo = new HashSet<>();
    todo.add(hash98304_obj1);
    todo.add(hash98304_obj2);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs = PersistentTrieSet
        .of(hash98304_obj1, hash98304_obj2);

    for (CollidableInteger x : xs) {
      todo.remove(x);
    }

    assertEquals(Collections.EMPTY_SET, todo);
  }

  @Test
  public void CollisionWithMergeInlineAbove1() {
    CollidableInteger hash98304_obj1 = new CollidableInteger(1, 98304);
    CollidableInteger hash98304_obj2 = new CollidableInteger(2, 98304);

    CollidableInteger hash268435456_obj3 = new CollidableInteger(3, 268435456);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs = PersistentTrieSet
        .of(hash98304_obj1, hash98304_obj2, hash268435456_obj3).__remove(hash268435456_obj3);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys = PersistentTrieSet
        .of(hash98304_obj1, hash98304_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineAbove1_2() {
    CollidableInteger hash8_obj1 = new CollidableInteger(1, 8);
    CollidableInteger hash8_obj2 = new CollidableInteger(2, 8);

    CollidableInteger hash268435456_obj3 = new CollidableInteger(3, 268435456);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs =
        PersistentTrieSet.of(hash8_obj1, hash8_obj2, hash268435456_obj3)
            .__remove(hash268435456_obj3);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys = PersistentTrieSet
        .of(hash8_obj1, hash8_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineAbove2() {
    CollidableInteger hash98304_obj1 = new CollidableInteger(1, 98304);
    CollidableInteger hash98304_obj2 = new CollidableInteger(2, 98304);

    CollidableInteger hash268435456_obj3 = new CollidableInteger(3, 268435456);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs = PersistentTrieSet
        .of(hash98304_obj1, hash268435456_obj3, hash98304_obj2).__remove(hash268435456_obj3);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys = PersistentTrieSet
        .of(hash98304_obj1, hash98304_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineAbove2_2() {
    CollidableInteger hash8_obj1 = new CollidableInteger(1, 8);
    CollidableInteger hash8_obj2 = new CollidableInteger(2, 8);

    CollidableInteger hash268435456_obj3 = new CollidableInteger(3, 268435456);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs =
        PersistentTrieSet.of(hash8_obj1, hash268435456_obj3, hash8_obj2)
            .__remove(hash268435456_obj3);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys = PersistentTrieSet
        .of(hash8_obj1, hash8_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineAbove1RemoveOneCollisonNode() {
    CollidableInteger hash98304_obj1 = new CollidableInteger(1, 98304);
    CollidableInteger hash98304_obj2 = new CollidableInteger(2, 98304);

    CollidableInteger hash268435456_obj3 = new CollidableInteger(3, 268435456);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs = PersistentTrieSet
        .of(hash98304_obj1, hash98304_obj2, hash268435456_obj3).__remove(hash98304_obj2);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys = PersistentTrieSet
        .of(hash98304_obj1, hash268435456_obj3);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineAbove2RemoveOneCollisonNode() {
    CollidableInteger hash98304_obj1 = new CollidableInteger(1, 98304);
    CollidableInteger hash98304_obj2 = new CollidableInteger(2, 98304);

    CollidableInteger hash268435456_obj3 = new CollidableInteger(3, 268435456);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs = PersistentTrieSet
        .of(hash98304_obj1, hash268435456_obj3, hash98304_obj2).__remove(hash98304_obj2);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys = PersistentTrieSet
        .of(hash98304_obj1, hash268435456_obj3);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineBelow1() {
    CollidableInteger hash98304_obj1 = new CollidableInteger(1, 98304);
    CollidableInteger hash98304_obj2 = new CollidableInteger(2, 98304);

    CollidableInteger hash8_obj3 = new CollidableInteger(3, 8);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs =
        PersistentTrieSet.of(hash98304_obj1, hash98304_obj2, hash8_obj3).__remove(hash8_obj3);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys = PersistentTrieSet
        .of(hash98304_obj1, hash98304_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineBelow2() {
    CollidableInteger hash98304_obj1 = new CollidableInteger(1, 98304);
    CollidableInteger hash98304_obj2 = new CollidableInteger(2, 98304);

    CollidableInteger hash8_obj3 = new CollidableInteger(3, 8);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs =
        PersistentTrieSet.of(hash98304_obj1, hash8_obj3, hash98304_obj2).__remove(hash8_obj3);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys = PersistentTrieSet
        .of(hash98304_obj1, hash98304_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineBelowRemoveOneCollisonNode1() {
    CollidableInteger hash98304_obj1 = new CollidableInteger(1, 98304);
    CollidableInteger hash98304_obj2 = new CollidableInteger(2, 98304);

    CollidableInteger hash8_obj3 = new CollidableInteger(3, 8);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs =
        PersistentTrieSet.of(hash98304_obj1, hash98304_obj2, hash8_obj3).__remove(hash98304_obj2);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys = PersistentTrieSet
        .of(hash98304_obj1, hash8_obj3);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineBelowRemoveOneCollisonNode2() {
    CollidableInteger hash98304_obj1 = new CollidableInteger(1, 98304);
    CollidableInteger hash98304_obj2 = new CollidableInteger(2, 98304);

    CollidableInteger hash8_obj3 = new CollidableInteger(3, 8);

    io.usethesource.capsule.Set.Immutable<CollidableInteger> xs =
        PersistentTrieSet.of(hash98304_obj1, hash8_obj3, hash98304_obj2).__remove(hash98304_obj2);
    io.usethesource.capsule.Set.Immutable<CollidableInteger> ys = PersistentTrieSet
        .of(hash98304_obj1, hash8_obj3);

    assertEquals(xs, ys);
  }

}
