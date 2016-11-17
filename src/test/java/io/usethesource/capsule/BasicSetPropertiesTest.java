/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import io.usethesource.capsule.api.deprecated.ImmutableSet;

@RunWith(JUnitQuickcheck.class)
public class BasicSetPropertiesTest {

  private final int DEFAULT_TRIALS = 1_000;

  @Property(trials = DEFAULT_TRIALS)
  public void sizeAfterBatchInsertion(java.util.Set<Integer> input) {
    ImmutableSet<Integer> set = DefaultTrieSet.<Integer>of().__insertAll(input);
    assertEquals(input.size(), set.size());
  }

  @Property(trials = DEFAULT_TRIALS)
  public <K> void sizeEqualsJavaCollection(ImmutableSet<K> input) {
    assertEquals(input.size(), new HashSet<K>(input).size());
  }

  // @Property(trials = DEFAULT_TRIALS)
  // public void sizeAfterInsertIncreasesByOne(ImmutableSet<Integer> input, Integer item) {
  // assertEquals(input.size() + 1, input.__insert(item).size());
  // }
  //
  // @Property(trials = DEFAULT_TRIALS)
  // public void containsAfterInsert1(ImmutableSet<Integer> input, Integer item) {
  // assumeFalse(input.contains(item));
  // assertTrue(input.__insert(item).contains(item));
  // }
  //
  // @Property(trials = DEFAULT_TRIALS)
  // public void containsAfterInsert2(ImmutableSet<Integer> input, Integer item) {
  // assumeTrue(input.contains(item));
  // assertTrue(input.__insert(item).contains(item));
  // }
  //
  // @Property(trials = DEFAULT_TRIALS)
  // public void notContainedAfterRemove(ImmutableSet<Integer> input, Integer item) {
  // assumeTrue(input.contains(item));
  // assertFalse(input.__remove(item).contains(item));
  // }

}
