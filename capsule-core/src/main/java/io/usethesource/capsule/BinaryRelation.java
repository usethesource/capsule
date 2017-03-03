/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

public interface BinaryRelation<T, U> extends SetMultimap<T, U> {

  BinaryRelation<U, T> inverse();

  SetMultimap<T, U> toSetMultimap();

  interface Immutable<K, V> extends BinaryRelation<K, V>, SetMultimap.Immutable<K, V> {

    @Override
    boolean isTransientSupported();

    @Override
    BinaryRelation.Transient<K, V> asTransient();

  }

  interface Transient<K, V> extends BinaryRelation<K, V>, SetMultimap.Transient<K, V> {

    @Override
    BinaryRelation.Immutable<K, V> freeze();

  }

}
