/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import io.usethesource.capsule.jmh.PureIntegerWithCustomHashCode;
import io.usethesource.capsule.jmh.api.JmhValue;

public class JmhHashedIntegerGenerator extends Generator<JmhValue> {

  public JmhHashedIntegerGenerator() {
    super(JmhValue.class);
  }

  @Override
  public JmhValue generate(SourceOfRandomness random, GenerationStatus status) {
    return PureIntegerWithCustomHashCode.valueOf(random.nextInt());
  }

}
