/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Optional;

import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import io.usethesource.capsule.api.deprecated.ImmutableSet;

@RunWith(JUnitQuickcheck.class)
public class BasicSetPropertiesTest {

  private final int DEFAULT_TRIALS = 1_000;
  private final int MAX_SIZE = 1_000;

  static public <T> void assertOptionalEquals(T expected, Optional<T> actual) {
    assertTrue(actual.isPresent());
    assertEquals(null, expected, actual.get());
  }

  /**
   * TODO: replace batch construction by sequence of 'insert' operations
   */
  @Property(trials = DEFAULT_TRIALS)
  public void growingWithInsert(@Size(min = 0, max = 0) final ImmutableSet<Integer> emptySet,
      @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<Integer> inputValues) {

    ImmutableSet<Integer> growingSet = emptySet;

    for (Integer value : inputValues) {
      ImmutableSet<Integer> tmpSet = growingSet.__insert(value);

      /* inserted 'value' must be contained */
      assertTrue(tmpSet.contains(value));

      growingSet = tmpSet;
    }

    // /* all inserted 'values' must be contained */
    // assertEquals(true,
    // inputValues.stream().map(growingSet::contains).reduce(true, Boolean::logicalAnd));

    // final ImmutableSet<Integer> finalSet = growingSet;
    // inputValues.forEach(value -> assertTrue(finalSet.contains(value)));

    // assertEquals(true,
    // inputValues.stream().map(growingSet::contains).reduce(true, Boolean::logicalAnd));
  }

  @Property(trials = DEFAULT_TRIALS)
  public void growingWithInsert1(@Size(min = 0, max = 0) final ImmutableSet<Integer> emptySet,
      @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<Integer> inputValues) {
    final ImmutableSet<Integer> constructedSet = emptySet.__insertAll(inputValues);

    // /* all inserted 'values' must be contained */
    // assertEquals(true,
    // inputValues.stream().map(constructedSet::contains).reduce(true, Boolean::logicalAnd));

    // inputValues.forEach(value -> assertTrue(constructedSet.contains(value)));

    for (Integer value : inputValues) {
      if (!constructedSet.contains(value)) {
        constructedSet.contains(value);
        fail();
      }
    }
  }

  @Property(trials = DEFAULT_TRIALS)
  public void growingWithInsert2(@Size(min = 0, max = 0) final ImmutableSet<Integer> emptySet,
      @Size(min = 11, max = 11) final java.util.HashSet<Integer> inputValues) {

    final HashSet<Integer> insertedValues = new HashSet<>(inputValues.size());
    ImmutableSet<Integer> growingSet = emptySet;

    for (Integer newValue : inputValues) {
      final ImmutableSet<Integer> tmpSet = growingSet.__insert(newValue);
      insertedValues.add(newValue);

      /* 'insertedValues' must be contained */
      boolean assertionHolds =
          insertedValues.stream().map(tmpSet::contains).reduce(true, Boolean::logicalAnd);

      if (!assertionHolds) {
        growingSet.__insert(newValue);
        fail(String.format("%s.insert(%s)", growingSet, newValue));
      }

      growingSet = tmpSet;
    }

    /* all inserted 'values' must be contained */
    assertEquals(true,
        inputValues.stream().map(growingSet::contains).reduce(true, Boolean::logicalAnd));
  }

  @Property(trials = DEFAULT_TRIALS)
  public void sizeAfterBatchInsertion(java.util.Set<Integer> input) {
    ImmutableSet<Integer> set = DefaultTrieSet.<Integer>of().__insertAll(input);
    assertEquals(input.size(), set.size());
  }

  @Property(trials = DEFAULT_TRIALS)
  public void sizeEqualsJavaCollection(ImmutableSet<Integer> input) {
    assertEquals(input.size(), new HashSet<Integer>(input).size());
  }

  // @Property(trials = DEFAULT_TRIALS)
  // public <K> void sizeEqualsJavaCollection1(TrieSet_5Bits_LazyHashCode<K> input) {
  // assertEquals(input.size(), new HashSet<K>(input).size());
  // }
  //
  // @Property(trials = DEFAULT_TRIALS)
  // public <K> void sizeEqualsJavaCollection2(TrieSet_5Bits_Spec0To8<K> input) {
  // assertEquals(input.size(), new HashSet<K>(input).size());
  // }

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
