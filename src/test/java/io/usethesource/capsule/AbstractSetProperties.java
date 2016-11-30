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

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.Size;

import io.usethesource.capsule.api.deprecated.ImmutableSet;

public abstract class AbstractSetProperties<T, CT extends ImmutableSet<T>> {

  private final int DEFAULT_TRIALS = 1_000;
  private final int MAX_SIZE = 1_000;
  private final Class<?> type;

  public AbstractSetProperties(Class<?> type) {
    this.type = type;
  }

  @Property(trials = DEFAULT_TRIALS)
  public void convertToJavaSetAndCheckSize(CT input) {
    assertEquals(new HashSet<T>(input).size(), input.size());
  }

  @Property(trials = DEFAULT_TRIALS)
  public void convertToJavaSetAndCheckHashCode(CT input) {
    assertEquals(new HashSet<T>(input).hashCode(), input.hashCode());
  }

  @Property(trials = DEFAULT_TRIALS)
  public void checkSizeAfterInsertAll(@Size(min = 0, max = 0) final CT emptySet,
      java.util.HashSet<T> inputValues) {
    CT testSet = (CT) emptySet.__insertAll(inputValues);
    assertEquals(inputValues.size(), testSet.size());
  }

  /**
   * Inserted element by element, starting from an emtpy set. Keeps track of all so far inserted
   * values and checks after each insertion if all inserted elements are contained (quadratic
   * operation).
   *
   * @param emptySet
   * @param inputValues
   */
  @Property(trials = DEFAULT_TRIALS)
  public void stepwiseContainsAfterInsert(@Size(min = 0, max = 0) final CT emptySet,
      @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<T> inputValues) {

    final HashSet<T> insertedValues = new HashSet<>(inputValues.size());
    CT testSet = emptySet;

    for (T newValue : inputValues) {
      final CT tmpSet = (CT) testSet.__insert(newValue);
      insertedValues.add(newValue);

      boolean containsInsertedValues =
          insertedValues.stream().map(tmpSet::contains).reduce(true, Boolean::logicalAnd);

      assertTrue("All so far inserted values must be contained.", containsInsertedValues);
      // String.format("%s.insert(%s)", testSet, newValue);

      testSet = tmpSet;
    }
  }

  @Property(trials = DEFAULT_TRIALS)
  public void containsAfterInsert(@Size(min = 0, max = 0) final CT emptySet,
      @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<T> inputValues) {

    CT testSet = emptySet;

    for (T newValue : inputValues) {
      final CT tmpSet = (CT) testSet.__insert(newValue);
      testSet = tmpSet;
    }

    boolean containsInsertedValues =
        inputValues.stream().map(testSet::contains).reduce(true, Boolean::logicalAnd);

    assertTrue("Must contain all inserted values.", containsInsertedValues);
  }

  @Property(trials = DEFAULT_TRIALS)
  public void notContainedAfterInsertRemove(CT input, T item) {
    assertFalse(input.__insert(item).__remove(item).contains(item));
  }

}
