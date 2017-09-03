/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util.function;

@FunctionalInterface
public interface ToBooleanBiFunction<T, U> {

  /**
   * Applies this function to the given two arguments.
   *
   * @param t the first function argument
   * @param u the second function argument
   * @return the function result
   */
  boolean applyAsBoolean(T t, U u);
}