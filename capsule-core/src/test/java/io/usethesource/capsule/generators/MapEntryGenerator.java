/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators;

import java.util.Map;

import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import static io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap.entryOf;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class MapEntryGenerator<T extends Map.Entry>
    extends ComponentizedGenerator<T> {

  public MapEntryGenerator() {
    super((Class<T>) Map.Entry.class);
  }

  @Override
  public int numberOfNeededComponents() {
    return 2;
  }

  @Override
  public T generate(SourceOfRandomness random, GenerationStatus status) {
    Object item0 = componentGenerators().get(0).generate(random, status);
    Object item1 = componentGenerators().get(1).generate(random, status);

    return (T) entryOf(item0, item1);
  }

}
