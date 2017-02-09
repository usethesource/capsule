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

import java.util.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import io.usethesource.capsule.core.deprecated.TrieSet_5Bits;

public class SetSmokeTest {

  private class DummyValue {
    public final int value;
    public final int hashCode;

    DummyValue(int value, int hashCode) {
      this.value = value;
      this.hashCode = hashCode;
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      DummyValue other = (DummyValue) obj;
      if (hashCode != other.hashCode) {
        return false;
      }
      if (value != other.value) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return String.format("%d [hashCode=%d]", value, hashCode);
    }
  }

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

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

    io.usethesource.capsule.api.Set.Immutable<DummyValue> set = TrieSet_5Bits.of();

    for (Entry<Integer, Integer> entry : input.entrySet()) {
      set = set.__insert(new DummyValue(entry.getKey(), entry.getValue()));
    }

    for (Entry<Integer, Integer> entry : input.entrySet()) {
      assertTrue(set.contains(new DummyValue(entry.getKey(), entry.getValue())));
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

    io.usethesource.capsule.api.Set.Immutable<DummyValue> set = TrieSet_5Bits.of();

    for (Entry<Integer, Integer> entry : input.entrySet()) {
      set = set.__insert(new DummyValue(entry.getKey(), entry.getValue()));
    }

    for (Entry<Integer, Integer> entry : input.entrySet()) {
      assertTrue(set.contains(new DummyValue(entry.getKey(), entry.getValue())));
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

    io.usethesource.capsule.api.Set.Immutable<DummyValue> set = TrieSet_5Bits.of();

    for (Entry<Integer, Integer> entry : input.entrySet()) {
      set = set.__insert(new DummyValue(entry.getKey(), entry.getValue()));
    }

    Set<Integer> keys = input.keySet();

    for (DummyValue key : set) {
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
    DummyValue hash_n2147483648_obj1 = new DummyValue(1, -2147483648);
    DummyValue hash_p1073741824_obj2 = new DummyValue(2, 1073741824);

    Set<DummyValue> todo = new HashSet<>();
    todo.add(hash_n2147483648_obj1);
    todo.add(hash_p1073741824_obj2);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs = TrieSet_5Bits.of(hash_n2147483648_obj1, hash_p1073741824_obj2);

    for (DummyValue x : xs) {
      todo.remove(x);
    }

    assertEquals(Collections.EMPTY_SET, todo);
  }

  @Test
  public void TwoCollisionsEquals() {
    DummyValue hash98304_obj1 = new DummyValue(1, 98304);
    DummyValue hash98304_obj2 = new DummyValue(2, 98304);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs = TrieSet_5Bits.of(hash98304_obj1, hash98304_obj2);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys = TrieSet_5Bits.of(hash98304_obj2, hash98304_obj1);

    assertEquals(xs, ys);
  }

  @Test
  public void ThreeCollisionsEquals() {
    DummyValue hash98304_obj1 = new DummyValue(1, 98304);
    DummyValue hash98304_obj2 = new DummyValue(2, 98304);
    DummyValue hash98304_obj3 = new DummyValue(3, 98304);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs = TrieSet_5Bits.of(hash98304_obj1, hash98304_obj2, hash98304_obj3);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys = TrieSet_5Bits.of(hash98304_obj3, hash98304_obj2, hash98304_obj1);

    assertEquals(xs, ys);
  }

  @Test
  public void RemovalFromCollisonNodeEqualsSingelton() {
    DummyValue hash98304_obj1 = new DummyValue(1, 98304);
    DummyValue hash98304_obj2 = new DummyValue(2, 98304);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs = TrieSet_5Bits.of(hash98304_obj1);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys =
        TrieSet_5Bits.of(hash98304_obj1, hash98304_obj2).__remove(hash98304_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionIterate() {
    DummyValue hash98304_obj1 = new DummyValue(1, 98304);
    DummyValue hash98304_obj2 = new DummyValue(2, 98304);

    Set<DummyValue> todo = new HashSet<>();
    todo.add(hash98304_obj1);
    todo.add(hash98304_obj2);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs = TrieSet_5Bits.of(hash98304_obj1, hash98304_obj2);

    for (DummyValue x : xs) {
      todo.remove(x);
    }

    assertEquals(Collections.EMPTY_SET, todo);
  }

  @Test
  public void CollisionWithMergeInlineAbove1() {
    DummyValue hash98304_obj1 = new DummyValue(1, 98304);
    DummyValue hash98304_obj2 = new DummyValue(2, 98304);

    DummyValue hash268435456_obj3 = new DummyValue(3, 268435456);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs = TrieSet_5Bits
        .of(hash98304_obj1, hash98304_obj2, hash268435456_obj3).__remove(hash268435456_obj3);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys = TrieSet_5Bits.of(hash98304_obj1, hash98304_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineAbove1_2() {
    DummyValue hash8_obj1 = new DummyValue(1, 8);
    DummyValue hash8_obj2 = new DummyValue(2, 8);

    DummyValue hash268435456_obj3 = new DummyValue(3, 268435456);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs =
        TrieSet_5Bits.of(hash8_obj1, hash8_obj2, hash268435456_obj3).__remove(hash268435456_obj3);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys = TrieSet_5Bits.of(hash8_obj1, hash8_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineAbove2() {
    DummyValue hash98304_obj1 = new DummyValue(1, 98304);
    DummyValue hash98304_obj2 = new DummyValue(2, 98304);

    DummyValue hash268435456_obj3 = new DummyValue(3, 268435456);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs = TrieSet_5Bits
        .of(hash98304_obj1, hash268435456_obj3, hash98304_obj2).__remove(hash268435456_obj3);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys = TrieSet_5Bits.of(hash98304_obj1, hash98304_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineAbove2_2() {
    DummyValue hash8_obj1 = new DummyValue(1, 8);
    DummyValue hash8_obj2 = new DummyValue(2, 8);

    DummyValue hash268435456_obj3 = new DummyValue(3, 268435456);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs =
        TrieSet_5Bits.of(hash8_obj1, hash268435456_obj3, hash8_obj2).__remove(hash268435456_obj3);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys = TrieSet_5Bits.of(hash8_obj1, hash8_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineAbove1RemoveOneCollisonNode() {
    DummyValue hash98304_obj1 = new DummyValue(1, 98304);
    DummyValue hash98304_obj2 = new DummyValue(2, 98304);

    DummyValue hash268435456_obj3 = new DummyValue(3, 268435456);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs = TrieSet_5Bits
        .of(hash98304_obj1, hash98304_obj2, hash268435456_obj3).__remove(hash98304_obj2);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys = TrieSet_5Bits.of(hash98304_obj1, hash268435456_obj3);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineAbove2RemoveOneCollisonNode() {
    DummyValue hash98304_obj1 = new DummyValue(1, 98304);
    DummyValue hash98304_obj2 = new DummyValue(2, 98304);

    DummyValue hash268435456_obj3 = new DummyValue(3, 268435456);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs = TrieSet_5Bits
        .of(hash98304_obj1, hash268435456_obj3, hash98304_obj2).__remove(hash98304_obj2);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys = TrieSet_5Bits.of(hash98304_obj1, hash268435456_obj3);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineBelow1() {
    DummyValue hash98304_obj1 = new DummyValue(1, 98304);
    DummyValue hash98304_obj2 = new DummyValue(2, 98304);

    DummyValue hash8_obj3 = new DummyValue(3, 8);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs =
        TrieSet_5Bits.of(hash98304_obj1, hash98304_obj2, hash8_obj3).__remove(hash8_obj3);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys = TrieSet_5Bits.of(hash98304_obj1, hash98304_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineBelow2() {
    DummyValue hash98304_obj1 = new DummyValue(1, 98304);
    DummyValue hash98304_obj2 = new DummyValue(2, 98304);

    DummyValue hash8_obj3 = new DummyValue(3, 8);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs =
        TrieSet_5Bits.of(hash98304_obj1, hash8_obj3, hash98304_obj2).__remove(hash8_obj3);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys = TrieSet_5Bits.of(hash98304_obj1, hash98304_obj2);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineBelowRemoveOneCollisonNode1() {
    DummyValue hash98304_obj1 = new DummyValue(1, 98304);
    DummyValue hash98304_obj2 = new DummyValue(2, 98304);

    DummyValue hash8_obj3 = new DummyValue(3, 8);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs =
        TrieSet_5Bits.of(hash98304_obj1, hash98304_obj2, hash8_obj3).__remove(hash98304_obj2);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys = TrieSet_5Bits.of(hash98304_obj1, hash8_obj3);

    assertEquals(xs, ys);
  }

  @Test
  public void CollisionWithMergeInlineBelowRemoveOneCollisonNode2() {
    DummyValue hash98304_obj1 = new DummyValue(1, 98304);
    DummyValue hash98304_obj2 = new DummyValue(2, 98304);

    DummyValue hash8_obj3 = new DummyValue(3, 8);

    io.usethesource.capsule.api.Set.Immutable<DummyValue> xs =
        TrieSet_5Bits.of(hash98304_obj1, hash8_obj3, hash98304_obj2).__remove(hash98304_obj2);
    io.usethesource.capsule.api.Set.Immutable<DummyValue> ys = TrieSet_5Bits.of(hash98304_obj1, hash8_obj3);

    assertEquals(xs, ys);
  }

}
