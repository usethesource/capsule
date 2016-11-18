/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import io.usethesource.capsule.api.deprecated.ImmutableSet;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class CapsuleSetGenerator<T extends ImmutableSet>
    extends ComponentizedGenerator<T> {

  private Class<T> target;
  private Size sizeRange;

  public CapsuleSetGenerator(Class<T> target) {
    super(target);
    this.target = target;
  }

  private int size(SourceOfRandomness random, GenerationStatus status) {
    return sizeRange != null ? random.nextInt(sizeRange.min(), sizeRange.max()) : status.size();
  }

  protected final T empty() {
    try {
      final Method persistentSetOfEmpty = target.getMethod("of");
      return (T) persistentSetOfEmpty.invoke(null);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException();
    }
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
