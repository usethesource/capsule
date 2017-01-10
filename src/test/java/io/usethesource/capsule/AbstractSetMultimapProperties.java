/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.Size;
import io.usethesource.capsule.api.deprecated.SetMultimap;

import java.util.HashSet;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public abstract class AbstractSetMultimapProperties<K, V, CT extends SetMultimap.Immutable<K, V>> {

  private final int DEFAULT_TRIALS = 1_000;
  private final int MAX_SIZE = 1_000;
  private final Class<?> type;

  public AbstractSetMultimapProperties(Class<?> type) {
    this.type = type;
  }

  @Property(trials = DEFAULT_TRIALS)
  public void convertToJavaSetAndCheckSize(CT input) {
    assertEquals(new HashSet<>(input.entrySet()).size(), input.size());
  }

  @Property(trials = DEFAULT_TRIALS)
  public void keySetEqualsKeyIteratorElements(final CT multimap) {
    final java.util.Set<K> keySet = new HashSet<>();
    multimap.keyIterator().forEachRemaining(keySet::add);

    // assertEquals(TrieSet_5Bits.class, multimap.keySet().getClass());
    assertEquals(keySet, multimap.keySet());
  }

//  /**
//   * TODO: replace batch construction by sequence of 'insert' operations
//   */
//  @Property // (trials = DEFAULT_TRIALS)
//  public void testInsertTuplesThatShareSameKey(final Integer key,
//      @Size(min = 1, max = 100) final java.util.HashSet<Integer> values) {
//    assertEquals(values.size(), toMultimap(key, values).size());
//    assertTrue(toMultimap(key, values).containsKey(key));
//  }
//
//  /**
//   * TODO: replace batch construction by sequence of 'insert' operations followed by a 'remove'
//   */
//  @Property(trials = DEFAULT_TRIALS)
//  public void testInsertTuplesWithOneRemoveThatShareSameKeyX(final Integer key,
//      @Size(min = 2, max = 100) final java.util.HashSet<Integer> values) {
//
//    Integer value = sourceOfRandomness.choose(values);
//    SetMultimap.Immutable<Integer, Integer> multimap = toMultimap(key, values);
//
//    if (multimap.__removeEntry(key, value).size() + 1 == multimap.size()) {
//      // succeed
//      multimap = multimap.__removeEntry(key, value);
//    } else {
//      // fail
//      assertTrue(multimap.containsEntry(key, value));
//      multimap = multimap.__removeEntry(key, value);
//    }
//
//    // assertEquals(values.size() - 1, multimap.size());
//    // assertTrue(multimap.containsKey(key));
//    // values.forEach(currentValue -> {
//    // if (!currentValue.equals(value)) {
//    // assertTrue(multimap.containsEntry(key, currentValue));
//    // }
//    // });
//  }

  /**
   * Inserted tuple by tuple, starting from an empty multimap. Keeps track of all so far inserted
   * tuples and checks after each insertion if all inserted tuples are contained (quadratic
   * operation).
   *
   * @param emptyCollection
   * @param inputValues
   */
  @Property(trials = DEFAULT_TRIALS)
  public void stepwiseContainsAfterInsert(@Size(min = 0, max = 0) final CT emptyCollection,
      @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<Map.Entry<K, V>> inputValues) {

    final HashSet<Map.Entry<K, V>> insertedValues = new HashSet<>(inputValues.size());
    CT testCollection = emptyCollection;

    for (Map.Entry<K, V> newValueTuple : inputValues) {
      final CT tmpCollection =
          (CT) testCollection.__insert(newValueTuple.getKey(), newValueTuple.getValue());
      insertedValues.add(newValueTuple);

      boolean containsInsertedValues = insertedValues.stream()
          .allMatch(tuple -> tmpCollection.containsEntry(tuple.getKey(), tuple.getValue()));

      assertTrue("All so far inserted values must be contained.", containsInsertedValues);
      // String.format("%s.insert(%s)", testSet, newValue);

      testCollection = tmpCollection;
    }
  }

  @Property(trials = DEFAULT_TRIALS)
  public void containsAfterInsert(@Size(min = 0, max = 0) final CT emptyCollection,
      @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<Map.Entry<K, V>> inputValues) {

    CT testCollection = emptyCollection;

    for (Map.Entry<K, V> newValueTuple : inputValues) {
      final CT tmpCollection =
          (CT) testCollection.__insert(newValueTuple.getKey(), newValueTuple.getValue());
      testCollection = tmpCollection;
    }

    final CT finalCollection = testCollection;

    boolean containsInsertedValues = inputValues.stream()
        .allMatch(tuple -> finalCollection.containsEntry(tuple.getKey(), tuple.getValue()));

    assertTrue("Must contain all inserted values.", containsInsertedValues);
  }

  @Property(trials = DEFAULT_TRIALS)
  public void notContainedAfterInsertRemove(CT input, K item0, V item1) {
    assertFalse(
        input.__insert(item0, item1).__removeEntry(item0, item1).containsEntry(item0, item1));
  }

  @Property(trials = DEFAULT_TRIALS)
  public void entryIteratorAfterInsert(@Size(min = 0, max = 0) final CT emptyCollection,
      @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<Map.Entry<K, V>> inputValues) {

    CT testCollection = emptyCollection;

    for (Map.Entry<K, V> newValueTuple : inputValues) {
      final CT tmpCollection =
          (CT) testCollection.__insert(newValueTuple.getKey(), newValueTuple.getValue());
      testCollection = tmpCollection;
    }

    final CT finalCollection = testCollection;

    final Spliterator<Map.Entry> entrySpliterator = Spliterators
        .spliterator(finalCollection.entryIterator(), finalCollection.size(), Spliterator.DISTINCT);
    final Stream<Map.Entry> entryStream = StreamSupport.stream(entrySpliterator, false);

    boolean containsInsertedValues = entryStream.allMatch(inputValues::contains);

    assertTrue("Must contain all inserted values.", containsInsertedValues);
  }

}
