/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import io.usethesource.capsule.annotation.Experimental;
import io.usethesource.capsule.core.PersistentBidirectionalTrieSetMultimap;

@Experimental
public interface BinaryRelation<T, U> extends SetMultimap<T, U> {

  BinaryRelation<U, T> inverse();

  SetMultimap<T, U> toSetMultimap();

  @Experimental
  interface Immutable<K, V> extends BinaryRelation<K, V>, SetMultimap.Immutable<K, V> {

    @Override
    BinaryRelation.Immutable<V, K> inverse();

    @Override
    boolean isTransientSupported();

    @Override
    BinaryRelation.Transient<K, V> asTransient();

    static <K, V> BinaryRelation.Immutable<K, V> of() {
      return PersistentBidirectionalTrieSetMultimap.of();
    }

    static <K, V> BinaryRelation.Immutable<K, V> of(K key0, V value0) {
      final BinaryRelation.Transient<K, V> tmp = BinaryRelation.Transient.of();

      tmp.__insert(key0, value0);

      return tmp.freeze();
    }

    static <K, V> BinaryRelation.Immutable<K, V> of(K key0, V value0, K key1, V value1) {
      final BinaryRelation.Transient<K, V> tmp = BinaryRelation.Transient.of();

      tmp.__insert(key0, value0);
      tmp.__insert(key1, value1);

      return tmp.freeze();
    }

    static <K, V> BinaryRelation.Immutable<K, V> of(K key0, V value0, K key1, V value1, K key2,
        V value2) {
      final BinaryRelation.Transient<K, V> tmp = BinaryRelation.Transient.of();

      tmp.__insert(key0, value0);
      tmp.__insert(key1, value1);
      tmp.__insert(key2, value2);

      return tmp.freeze();
    }

    static <K, V> BinaryRelation.Immutable<K, V> of(K key0, V value0, K key1, V value1, K key2,
        V value2, K key3, V value3) {
      final BinaryRelation.Transient<K, V> tmp = BinaryRelation.Transient.of();

      tmp.__insert(key0, value0);
      tmp.__insert(key1, value1);
      tmp.__insert(key2, value2);
      tmp.__insert(key3, value3);

      return tmp.freeze();
    }

    static <K, V> BinaryRelation.Immutable<K, V> of(K key0, V value0, K key1, V value1, K key2,
        V value2, K key3, V value3, K key4, V value4) {
      final BinaryRelation.Transient<K, V> tmp = BinaryRelation.Transient.of();

      tmp.__insert(key0, value0);
      tmp.__insert(key1, value1);
      tmp.__insert(key2, value2);
      tmp.__insert(key3, value3);
      tmp.__insert(key4, value4);

      return tmp.freeze();
    }

    static <K, V> BinaryRelation.Immutable<K, V> of(K key0, V value0, K key1, V value1, K key2,
        V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
      final BinaryRelation.Transient<K, V> tmp = BinaryRelation.Transient.of();

      tmp.__insert(key0, value0);
      tmp.__insert(key1, value1);
      tmp.__insert(key2, value2);
      tmp.__insert(key3, value3);
      tmp.__insert(key4, value4);
      tmp.__insert(key5, value5);

      return tmp.freeze();
    }

  }

  @Experimental
  interface Transient<K, V> extends BinaryRelation<K, V>, SetMultimap.Transient<K, V> {

    @Override
    BinaryRelation.Transient<V, K> inverse();

    @Override
    BinaryRelation.Immutable<K, V> freeze();

    static <K, V> BinaryRelation.Transient<K, V> of() {
      return PersistentBidirectionalTrieSetMultimap.transientOf();
    }

    static <K, V> BinaryRelation.Transient<K, V> of(K key0, V value0) {
      final BinaryRelation.Transient<K, V> tmp = BinaryRelation.Transient.of();

      tmp.__insert(key0, value0);

      return tmp;
    }

    static <K, V> BinaryRelation.Transient<K, V> of(K key0, V value0, K key1, V value1) {
      final BinaryRelation.Transient<K, V> tmp = BinaryRelation.Transient.of();

      tmp.__insert(key0, value0);
      tmp.__insert(key1, value1);

      return tmp;
    }

    static <K, V> BinaryRelation.Transient<K, V> of(K key0, V value0, K key1, V value1, K key2,
        V value2) {
      final BinaryRelation.Transient<K, V> tmp = BinaryRelation.Transient.of();

      tmp.__insert(key0, value0);
      tmp.__insert(key1, value1);
      tmp.__insert(key2, value2);

      return tmp;
    }

    static <K, V> BinaryRelation.Transient<K, V> of(K key0, V value0, K key1, V value1, K key2,
        V value2, K key3, V value3) {
      final BinaryRelation.Transient<K, V> tmp = BinaryRelation.Transient.of();

      tmp.__insert(key0, value0);
      tmp.__insert(key1, value1);
      tmp.__insert(key2, value2);
      tmp.__insert(key3, value3);

      return tmp;
    }

    static <K, V> BinaryRelation.Transient<K, V> of(K key0, V value0, K key1, V value1, K key2,
        V value2, K key3, V value3, K key4, V value4) {
      final BinaryRelation.Transient<K, V> tmp = BinaryRelation.Transient.of();

      tmp.__insert(key0, value0);
      tmp.__insert(key1, value1);
      tmp.__insert(key2, value2);
      tmp.__insert(key3, value3);
      tmp.__insert(key4, value4);

      return tmp;
    }

    static <K, V> BinaryRelation.Transient<K, V> of(K key0, V value0, K key1, V value1, K key2,
        V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
      final BinaryRelation.Transient<K, V> tmp = BinaryRelation.Transient.of();

      tmp.__insert(key0, value0);
      tmp.__insert(key1, value1);
      tmp.__insert(key2, value2);
      tmp.__insert(key3, value3);
      tmp.__insert(key4, value4);
      tmp.__insert(key5, value5);

      return tmp;
    }

  }

}
