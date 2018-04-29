/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators.map;

import static com.pholser.junit.quickcheck.internal.Lists.removeFrom;
import static com.pholser.junit.quickcheck.internal.Lists.shrinksOfOneItem;
import static com.pholser.junit.quickcheck.internal.Ranges.Type.INTEGRAL;
import static com.pholser.junit.quickcheck.internal.Ranges.checkRange;
import static com.pholser.junit.quickcheck.internal.Sequences.halving;
import static java.util.stream.StreamSupport.stream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Shrink;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import io.usethesource.capsule.Map;

public abstract class AbstractMapGenerator<T extends Map.Immutable>
    extends ComponentizedGenerator<T> {

  private Class<T> target;
  private Size sizeRange;

  public AbstractMapGenerator(Class<T> target) {
    super(target);
    this.target = target;
  }

  public void configure(Size size) {
    this.sizeRange = size;
    checkRange(INTEGRAL, size.min(), size.max());
  }

  protected final int size(SourceOfRandomness random, GenerationStatus status) {
    return sizeRange != null ? random.nextInt(sizeRange.min(), sizeRange.max()) : status.size();
  }

  protected T empty() {
    try {
      final Method persistentMapOfEmpty = target.getDeclaredMethod("of");
      persistentMapOfEmpty.setAccessible(true); // support AbstractSpecialisedImmutableMap
      return (T) persistentMapOfEmpty.invoke(null);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException();
    }
  }

  @Override
  public int numberOfNeededComponents() {
    return 2;
  }

  @Override
  public T generate(SourceOfRandomness random, GenerationStatus status) {
    int size = size(random, status);

    T items = empty();
    for (int i = 0; i < size; ++i) {
      Object key = componentGenerators().get(0).generate(random, status);
      Object val = componentGenerators().get(1).generate(random, status);
      items = (T) items.__put(key, val);
    }

    return items;
  }

//  @Override
//  public List<T> doShrink(SourceOfRandomness random, T larger) {
//    ...
//  }

}
