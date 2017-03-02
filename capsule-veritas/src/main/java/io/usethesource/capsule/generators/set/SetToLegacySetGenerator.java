/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators.set;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import io.usethesource.capsule.core.experimental.TrieSet;
import io.usethesource.capsule.core.converter.SetToLegacySetConverter;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SetToLegacySetGenerator<K> extends AbstractSetGenerator<SetToLegacySetConverter<K>> {

  public SetToLegacySetGenerator() {
    super((Class) SetToLegacySetConverter.class);
  }

  @Override
  protected final SetToLegacySetConverter<K> empty() {
    return (SetToLegacySetConverter<K>) SetToLegacySetConverter.adapt(TrieSet.of());
  }

  @Override
  public int numberOfNeededComponents() {
    return 1;
  }

  // TODO: check shrinking support in hierarchy
  @Override
  public boolean canShrink(Object larger) {
    return false;
  }

  @Override
  public SetToLegacySetConverter<K> generate(SourceOfRandomness random, GenerationStatus status) {
    int size = size(random, status);

    SetToLegacySetConverter<K> items = empty();
    for (int i = 0; i < size; ++i) {
      K item = (K) componentGenerators().get(0).generate(random, status);
      items = (SetToLegacySetConverter<K>) items.__insert(item);
    }

    return items;
  }

}
