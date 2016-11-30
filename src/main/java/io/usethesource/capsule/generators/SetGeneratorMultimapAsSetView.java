/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators;

import static io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap.entryOf;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import io.usethesource.capsule.DefaultTrieSetMultimap;
import io.usethesource.capsule.api.deprecated.ImmutableSetMultimap;
import io.usethesource.capsule.api.deprecated.ImmutableSetMultimapAsImmutableSetView;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SetGeneratorMultimapAsSetView<K, V>
    extends AbstractSetGenerator<ImmutableSetMultimapAsImmutableSetView<K, V, Map.Entry<K, V>>> {

  public SetGeneratorMultimapAsSetView() {
    super((Class) ImmutableSetMultimapAsImmutableSetView.class);
  }

  @Override
  protected final ImmutableSetMultimapAsImmutableSetView<K, V, Map.Entry<K, V>> empty() {
    final ImmutableSetMultimap<K, V> multimap = DefaultTrieSetMultimap.of();

    final BiFunction<K, V, Map.Entry<K, V>> tupleOf = (first, second) -> entryOf(first, second);

    final BiFunction<Map.Entry<K, V>, Integer, Object> tupleElementAt = (tuple, position) -> {
      switch (position) {
        case 0:
          return tuple.getKey();
        case 1:
          return tuple.getValue();
        default:
          throw new IllegalStateException();
      }
    };

    final Function<Map.Entry<Integer, Integer>, Boolean> tupleChecker = (argument) -> true;

    return new ImmutableSetMultimapAsImmutableSetView(multimap, tupleOf, tupleElementAt,
        tupleChecker);
  }

  @Override
  public int numberOfNeededComponents() {
    return 3;
  }

  // TODO: check shrinking support in hierarchy
  @Override
  public boolean canShrink(Object larger) {
    return false;
  }

  @Override
  public ImmutableSetMultimapAsImmutableSetView<K, V, Map.Entry<K, V>> generate(
      SourceOfRandomness random, GenerationStatus status) {
    int size = size(random, status);

    ImmutableSetMultimapAsImmutableSetView<K, V, Map.Entry<K, V>> items = empty();
    for (int i = 0; i < size; ++i) {
      Map.Entry<K, V> item =
          (Map.Entry<K, V>) componentGenerators().get(2).generate(random, status);
      items = (ImmutableSetMultimapAsImmutableSetView) items.__insert(item);
    }

    return items;
  }

}
