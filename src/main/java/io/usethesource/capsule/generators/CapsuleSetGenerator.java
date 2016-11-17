/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators;

import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import io.usethesource.capsule.DefaultTrieSet;
import io.usethesource.capsule.api.deprecated.ImmutableSet;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CapsuleSetGenerator<T extends ImmutableSet> extends ComponentizedGenerator<T> {

  private Size sizeRange;

  public CapsuleSetGenerator() {
    super((Class<T>) DefaultTrieSet.getTargetClass());
  }

  public CapsuleSetGenerator(Class<T> type) {
    super(type);
  }

  private int size(SourceOfRandomness random, GenerationStatus status) {
    return sizeRange != null ? random.nextInt(sizeRange.min(), sizeRange.max()) : status.size();
  }

  protected final T empty() {
    return (T) DefaultTrieSet.of();
  }

  @Override
  public int numberOfNeededComponents() {
    return 1;
  }

  @Override
  public T generate(SourceOfRandomness random, GenerationStatus status) {
    int size = size(random, status);

    T items = empty();
    for (int i = 0; i < size; ++i) {
      Object item = componentGenerators().get(0).generate(random, status);
      if (item != null)
        items = (T) items.__insert(item);
    }

    return items;
  }

}
