/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import static com.pholser.junit.quickcheck.internal.Reflection.defaultValueOf;

public class CollidableIntegerGenerator extends Generator<CollidableInteger> {
  private int min = (Integer) defaultValueOf(InRange.class, "minInt");
  private int max = (Integer) defaultValueOf(InRange.class, "maxInt");

  private int collisionPercentage = 25;

  public CollidableIntegerGenerator() {
    super(CollidableInteger.class);
    assert 0 <= collisionPercentage && collisionPercentage <= 100;
  }

  @SuppressWarnings("unused")
  public void configure(InRange range) {
    min = range.min().isEmpty() ? range.minInt() : Integer.parseInt(range.min());
    max = range.max().isEmpty() ? range.maxInt() : Integer.parseInt(range.max());
  }

  @Override
  public CollidableInteger generate(SourceOfRandomness random, GenerationStatus status) {
    int value = random.nextInt(min, max);

    if (Math.abs(value % 100) < collisionPercentage) {
      return new CollidableInteger(value, Math.abs(value % 100));
    } else {
      return new CollidableInteger(value, value);
    }
  }
}
