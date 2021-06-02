/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Map;
import java.util.stream.Stream;

import com.pholser.junit.quickcheck.Property;

import static io.usethesource.capsule.StaticConfiguration.*;
import static org.junit.Assert.*;

public abstract class AbstractBinaryRelationProperties<K, V, CT extends BinaryRelation.Immutable<K, V>>
    extends AbstractSetMultimapProperties<K, V, CT> {

  @Property(trials = DEFAULT_TRIALS)
  public void inverse(CT input) {
    final BinaryRelation.Immutable<V, K> inverseInput =
        (BinaryRelation.Immutable<V, K>) input.inverse();
    final Stream<Map.Entry<K, V>> entryStream = input.entrySet().stream();

    boolean inverseContainsInversedTuples =
        entryStream.allMatch(tuple -> inverseInput.containsEntry(tuple.getValue(), tuple.getKey()));

    assertTrue(inverseContainsInversedTuples);
  }

}
