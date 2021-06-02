/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.Size;
import io.usethesource.capsule.core.PersistentTrieSet;

import static io.usethesource.capsule.StaticConfiguration.*;
import static org.junit.Assert.*;

public abstract class AbstractSetMultimapProperties<K, V, CT extends SetMultimap.Immutable<K, V>> {

  @Property(trials = DEFAULT_TRIALS)
  public void convertToJavaSetAndCheckSize(CT input) {
    final java.util.Set<java.util.Map.Entry<K, V>> javaSet = new HashSet<>(input.entrySet());
    assertEquals(javaSet.size(), input.size());
  }

  @Property(trials = DEFAULT_TRIALS)
  public void checkSizeOfEntrySetIterator(CT input) {
    final Iterator<?> iterator = input.entrySet().iterator();

    int encounteredSize = 0;
    while (iterator.hasNext()) {
      iterator.next();
      encounteredSize++;
    }

    assertEquals(input.size(), encounteredSize);
  }

  @Property(trials = DEFAULT_TRIALS)
  public void mapEqualsOtherMap(@Size(max = 0) final CT emptyCollection,
      final SetMultimap.Immutable<K, V> thatMap) {
    final SetMultimap.Transient builder = emptyCollection.asTransient();
    thatMap.entryIterator()
        .forEachRemaining(tuple -> builder.__insert(tuple.getKey(), tuple.getValue()));
    final CT thisMap = (CT) builder.freeze();

    assertEquals(thisMap, thatMap);
    assertEquals(thatMap, thisMap);
  }

  @Property(trials = DEFAULT_TRIALS)
  public void keySetEqualsKeyIteratorElements(final CT multimap) {
    final java.util.Set<K> keySet = new HashSet<>();
    multimap.keyIterator().forEachRemaining(keySet::add);

    // assertEquals(PersistentTrieSet.class, multimap.keySet().getClass());
    assertEquals(keySet, multimap.keySet());
  }

  // /**
  // * TODO: replace batch construction by sequence of 'insert' operations
  // */
  // @Property // (trials = DEFAULT_TRIALS)
  // public void testInsertTuplesThatShareSameKey(final Integer key,
  // @Size(min = 1, max = 100) final java.util.HashSet<Integer> values) {
  // assertEquals(values.size(), toMultimap(key, values).size());
  // assertTrue(toMultimap(key, values).containsKey(key));
  // }
  //
  // /**
  // * TODO: replace batch construction by sequence of 'insert' operations followed by a 'remove'
  // */
  // @Property(trials = DEFAULT_TRIALS)
  // public void testInsertTuplesWithOneRemoveThatShareSameKeyX(final Integer key,
  // @Size(min = 2, max = 100) final java.util.HashSet<Integer> values) {
  //
  // Integer value = sourceOfRandomness.choose(values);
  // SetMultimap.Immutable<Integer, Integer> multimap = toMultimap(key, values);
  //
  // if (multimap.__remove(key, value).size() + 1 == multimap.size()) {
  // // succeed
  // multimap = multimap.__remove(key, value);
  // } else {
  // // fail
  // assertTrue(multimap.containsEntry(key, value));
  // multimap = multimap.__remove(key, value);
  // }
  //
  // // assertEquals(values.size() - 1, multimap.size());
  // // assertTrue(multimap.containsKey(key));
  // // values.forEach(currentValue -> {
  // // if (!currentValue.equals(value)) {
  // // assertTrue(multimap.containsEntry(key, currentValue));
  // // }
  // // });
  // }

  /**
   * Inserted tuple by tuple, starting from an empty multimap. Keeps track of all so far inserted
   * tuples and checks after each insertion if all inserted tuples are contained (quadratic
   * operation).
   */
  @Property(trials = SQRT_TRIALS)
  public void stepwiseContainsAfterInsert(@Size(max = 0) final CT emptyCollection,
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
  public void containsAfterInsert(@Size(max = 0) final CT emptyCollection,
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
    assertFalse(input.__insert(item0, item1).__remove(item0, item1).containsEntry(item0, item1));
  }

  @Property(trials = DEFAULT_TRIALS)
  public void entryIteratorAfterInsert(@Size(max = 0) final CT emptyCollection,
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

  @Property(trials = DEFAULT_TRIALS)
  public void sizeAfterInsertKeyValue(CT input, K key, V value) {
    int sizeDelta =
        Set.Immutable.of(value).__insertAll(input.get(key)).__removeAll(input.get(key)).size();

    assertEquals(sizeDelta, input.__insert(key, value).size() - input.size());
  }

  @Property(trials = DEFAULT_TRIALS)
  public void sizeAfterInsertKeyValues(CT input, K key, PersistentTrieSet<V> values) {
    int sizeDelta = values.__insertAll(input.get(key)).__removeAll(input.get(key)).size();

    CT updatedInput = (CT) input.__insert(key, values);
    assertEquals(sizeDelta, updatedInput.size() - input.size());

    // invoke other properties
    convertToJavaSetAndCheckSize(updatedInput);
  }

  /*
   * NOTE: tests transient insertion and variations of operations
   * TODO: make explicit sets of transient tests and tests for chained operations
   */
  @Property(trials = DEFAULT_TRIALS)
  public void sizeAfterTransientInsertKeyValues(CT input, K key, PersistentTrieSet<V> values) {
    int sizeDelta = values.__insertAll(input.get(key)).__removeAll(input.get(key)).size();

    SetMultimap.Transient<K, V> builder = input.asTransient();
    builder.__remove(key);
    builder.__put(key, values);

    builder.__remove(key);
    builder.__insert(key, values);

    builder.__remove(key);
    builder.__put(key, values);
    builder.__insert(key, values);

    builder.__remove(key);
    builder.__insert(key, values);
    builder.__put(key, values);

    CT updatedInput = (CT) builder.freeze();

    assertEquals(sizeDelta, updatedInput.size() - input.size());

    // invoke other properties
    convertToJavaSetAndCheckSize(updatedInput);
  }

  @Property(trials = DEFAULT_TRIALS)
  public void getReturnsNonNull(CT input, K key) {
    assertNotNull("Must always return a set and not null.", input.get(key));
  }

  @Property(trials = DEFAULT_TRIALS)
  public void transientGetReturnsNonNull(CT input, K key) {
    assertNotNull("Must always return a set and not null.", input.asTransient().get(key));
  }

  @Property(trials = DEFAULT_TRIALS)
  public void serializationRoundtripIfSerializable(CT input) throws Exception {
    if (input instanceof java.io.Serializable) {
      assertEquals(input, deserialize(serialize((java.io.Serializable) input), input.getClass()));
    }
  }

  private static <T extends Serializable> byte[] serialize(T item) throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(item);
      return baos.toByteArray();
    } catch (IOException e) {
      throw e;
    }
  }

  private static <T> T deserialize(byte[] bytes, Class<T> itemClass)
      throws IOException, ClassNotFoundException {
    try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais)) {
      Object item = ois.readObject();
      return itemClass.cast(item);
    } catch (IOException | ClassNotFoundException e) {
      throw e;
    }
  }

}
