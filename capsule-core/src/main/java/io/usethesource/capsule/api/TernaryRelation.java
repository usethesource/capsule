/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api;

public interface TernaryRelation<T, U, V, R extends Triple<T, U, V>> extends Set<R> {

  // TernaryRelation<U, T> inverse();
  //
  // SetMultimap<T, U> toSetMultimap();

  interface Immutable<T, U, V, R extends Triple<T, U, V>>
      extends TernaryRelation<T, U, V, R>, Set.Immutable<R> {

    @Override
    boolean isTransientSupported();

    @Override
    TernaryRelation.Transient<T, U, V, R> asTransient();

  }

  interface Transient<T, U, V, R extends Triple<T, U, V>>
      extends TernaryRelation<T, U, V, R>, Set.Transient<R> {

    @Override
    TernaryRelation.Immutable<T, U, V, R> freeze();

  }

}
