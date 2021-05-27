/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util.stream;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class DefaultCollector<T, A, R> implements Collector<T, A, R> {
  private final Supplier<A> supplier;
  private final BiConsumer<A, T> accumulator;
  private final BinaryOperator<A> combiner;
  private final Function<A, R> finisher;
  private final Set<Characteristics> characteristics;

  public DefaultCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator,
      BinaryOperator<A> combiner, Function<A, R> finisher, Set<Characteristics> characteristics) {
    this.supplier = supplier;
    this.accumulator = accumulator;
    this.combiner = combiner;
    this.finisher = finisher;
    this.characteristics = characteristics;
  }

  @Override
  public BiConsumer<A, T> accumulator() {
    return accumulator;
  }

  @Override
  public Supplier<A> supplier() {
    return supplier;
  }

  @Override
  public BinaryOperator<A> combiner() {
    return combiner;
  }

  @Override
  public Function<A, R> finisher() {
    return finisher;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return characteristics;
  }
}
