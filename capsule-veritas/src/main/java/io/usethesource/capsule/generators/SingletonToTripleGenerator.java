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
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import io.usethesource.capsule.api.Triple;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class SingletonToTripleGenerator<T extends Triple>
    extends ComponentizedGenerator<T> {

  public SingletonToTripleGenerator() {
    super((Class<T>) Triple.class);
  }

  @Override
  public int numberOfNeededComponents() {
    return 1;
  }

  @Override
  public T generate(SourceOfRandomness random, GenerationStatus status) {
    final Object item0 = componentGenerators().get(0).generate(random, status);

    return (T) Triple.of(item0, item0, item0);
  }

}
