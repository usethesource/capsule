/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import static io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap.entryOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import io.usethesource.capsule.api.deprecated.ImmutableSetMultimap;
import io.usethesource.capsule.core.deprecated.TrieSet_5Bits;

public abstract class AbstractSetMultimapProperties {

  private final int DEFAULT_TRIALS = 1_000;

  private final SourceOfRandomness sourceOfRandomness = new SourceOfRandomness(new Random(13));

  private final static <K, V> ImmutableSetMultimap<K, V> toMultimap(Set<Map.Entry<K, V>> entrySet) {
    // TransientSetMultimap<K, V> tmpMultimap = DefaultTrieSetMultimap.<K, V>of().asTransient();
    // entrySet.forEach(entry -> tmpMultimap.__insert(entry.getKey(), entry.getValue()));
    //
    // return tmpMultimap.freeze();

    ImmutableSetMultimap<K, V> tmpMultimap = DefaultTrieSetMultimap.<K, V>of();
    for (Map.Entry<K, V> entry : entrySet) {
      tmpMultimap = tmpMultimap.__insert(entry.getKey(), entry.getValue());
    }

    return tmpMultimap;
  }

  private final static <K, V> ImmutableSetMultimap<K, V> toMultimap(K key, Set<V> values) {
    Set<Map.Entry<K, V>> entrySet =
        (Set) values.stream().map(value -> entryOf(key, value)).collect(Collectors.toSet());
    return toMultimap(entrySet);
  }

  @Property(trials = DEFAULT_TRIALS)
  public void keySetEqualsKeyIteratorElements(
      java.util.HashSet<Map.Entry<Integer, Integer>> inputValues) {
    final ImmutableSetMultimap multimap = toMultimap(inputValues);

    final java.util.Set keySet = new HashSet();
    multimap.keyIterator().forEachRemaining(keySet::add);

    assertEquals(TrieSet_5Bits.class, multimap.keySet().getClass());
    assertEquals(keySet, multimap.keySet());
  }

  @Property // (trials = DEFAULT_TRIALS)
  public void size(java.util.HashSet<Map.Entry<Integer, Integer>> input) {
    assertEquals(input.size(), toMultimap(input).size());
  }

  @Property // (trials = DEFAULT_TRIALS)
  public void containsEntry(java.util.HashSet<Map.Entry<Integer, Integer>> input) {
    input.forEach(
        entry -> assertTrue(toMultimap(input).containsEntry(entry.getKey(), entry.getValue())));
  }

  /**
   * TODO: replace batch construction by sequence of 'insert' operations
   */
  @Property // (trials = DEFAULT_TRIALS)
  public void testInsertTuplesThatShareSameKey(final Integer key,
      @Size(min = 1, max = 100) final java.util.HashSet<Integer> values) {
    assertEquals(values.size(), toMultimap(key, values).size());
    assertTrue(toMultimap(key, values).containsKey(key));
  }

  /**
   * TODO: replace batch construction by sequence of 'insert' operations followed by a 'remove'
   */
  @Property(trials = DEFAULT_TRIALS)
  public void testInsertTuplesWithOneRemoveThatShareSameKeyX(final Integer key,
      @Size(min = 2, max = 100) final java.util.HashSet<Integer> values) {

    Integer value = sourceOfRandomness.choose(values);
    ImmutableSetMultimap<Integer, Integer> multimap = toMultimap(key, values);

    if (multimap.__removeEntry(key, value).size() + 1 == multimap.size()) {
      // succeed
      multimap = multimap.__removeEntry(key, value);
    } else {
      // fail
      assertTrue(multimap.containsEntry(key, value));
      multimap = multimap.__removeEntry(key, value);
    }

    // assertEquals(values.size() - 1, multimap.size());
    // assertTrue(multimap.containsKey(key));
    // values.forEach(currentValue -> {
    // if (!currentValue.equals(value)) {
    // assertTrue(multimap.containsEntry(key, currentValue));
    // }
    // });
  }

}
