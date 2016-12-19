/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util.stream;

import io.usethesource.capsule.DefaultTrieSet;
import io.usethesource.capsule.DefaultTrieSetMultimap;
import io.usethesource.capsule.api.deprecated.ImmutableSet;
import io.usethesource.capsule.api.deprecated.ImmutableSetMultimap;
import io.usethesource.capsule.api.deprecated.TransientSet;
import io.usethesource.capsule.api.deprecated.TransientSetMultimap;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CapsuleCollectors {

  public static final Set<Collector.Characteristics> UNORDERED =
      Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED));

  public static <T> Collector<T, ?, ImmutableSet<T>> toSet() {
    return new DefaultCollector<>((Supplier<TransientSet<T>>) DefaultTrieSet::transientOf,
        TransientSet::__insert, (left, right) -> {
          left.__insertAll(right);
          return left;
        }, TransientSet::freeze, UNORDERED);
  }

  public static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> toSetMultimap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {

    /** extract key/value from type {@code T} and insert into multimap */
    final BiConsumer<TransientSetMultimap<K, V>, T> accumulator =
        (map, element) -> map.__insert(keyMapper.apply(element), valueMapper.apply(element));

    return new DefaultCollector<>(
        (Supplier<TransientSetMultimap<K, V>>) DefaultTrieSetMultimap::transientOf, accumulator,
        (left, right) -> {
          left.__insertAll(right);
          return left;
        }, TransientSetMultimap::freeze, UNORDERED);
  }

}
