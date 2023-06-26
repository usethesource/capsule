/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import io.usethesource.criterion.PureInteger;
import io.usethesource.criterion.api.JmhValue;

public class JmhIntegerGenerator extends Generator<JmhValue> {

  public JmhIntegerGenerator() {
    super(JmhValue.class);
  }

  @Override
  public JmhValue generate(SourceOfRandomness random, GenerationStatus status) {
    return new PureInteger(random.nextInt());
  }

}
