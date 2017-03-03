/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util.stream;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import io.usethesource.capsule.SetMultimap;

public class CapsuleCollectors {

  public static final Set<Collector.Characteristics> UNORDERED =
      Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED));

  public static <T> Collector<T, ?, io.usethesource.capsule.Set.Immutable<T>> toSet() {
    return new DefaultCollector<>(
        (Supplier<io.usethesource.capsule.Set.Transient<T>>) io.usethesource.capsule.Set.Transient::of,
        io.usethesource.capsule.Set.Transient::__insert, (left, right) -> {
      left.__insertAll(right);
      return left;
    }, io.usethesource.capsule.Set.Transient::freeze, UNORDERED);
  }

//  public static <T, K, V> Collector<T, ?, io.usethesource.capsule.Map.Immutable<K, V>> toMap(
//      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
//
//    /** extract key/value from type {@code T} and insert into multimap */
//    final BiConsumer<io.usethesource.capsule.Map.Transient<K, V>, T> accumulator =
//        (map, element) -> map.__put(keyMapper.apply(element), valueMapper.apply(element));
//
//    return new DefaultCollector<>(
//        io.usethesource.capsule.Map::of,
//        accumulator,
//        (left, right) -> {
//          left.union(right);
//          return left;
//        }, io.usethesource.capsule.Map.Transient::freeze, UNORDERED);
//  }

  public static <T, K, V> Collector<T, ?, SetMultimap.Immutable<K, V>> toSetMultimap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {

    /** extract key/value from type {@code T} and insert into multimap */
    final BiConsumer<SetMultimap.Transient<K, V>, T> accumulator =
        (map, element) -> map.__insert(keyMapper.apply(element), valueMapper.apply(element));

    return new DefaultCollector<>(
        SetMultimap.Transient::of,
        accumulator,
        (left, right) -> {
          left.union(right);
          return left;
        }, SetMultimap.Transient::freeze, UNORDERED);
  }

}
