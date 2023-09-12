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
import java.util.Objects;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.Size;

import static io.usethesource.capsule.StaticConfiguration.*;
import static org.junit.Assert.*;

/*
 * NOTE: use e.g. @When(seed = 3666151076704776907L) to fix seed for reproducing test run.
 */
public abstract class AbstractMapProperties<T, CT extends Map.Immutable<T, T>> {

  @Property(trials = DEFAULT_TRIALS)
  public void serializationRoundtrip(CT input) throws Exception {
    assertEquals(input, deserialize(serialize((Serializable) input), input.getClass()));
  }

  @Property(trials = DEFAULT_TRIALS)
  public void serializationRoundtripIfSerializable(CT input) throws Exception {
    if (input instanceof Serializable) {
      assertEquals(input, deserialize(serialize((Serializable) input), input.getClass()));
    }
  }

  /**
   * Inserted element by element, starting from an empty map. Keeps track of all so far inserted
   * values and checks after each insertion if all inserted elements are contained (quadratic
   * operation).
   */
  @Property(trials = SQRT_TRIALS)
  public void stepwiseContainsKeyAfterInsert(@Size(max = 0) final CT emptyMap,
                                     @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<T> inputValues) {

    final HashSet<T> insertedValues = new HashSet<>(inputValues.size());
    CT testMap = emptyMap;

    for (T newValue : inputValues) {
      final CT tmpMap = (CT) testMap.__put(newValue, newValue);
      insertedValues.add(newValue);

      boolean containsInsertedValues =
              insertedValues.stream().allMatch(tmpMap::containsKey);

      assertTrue("All so far inserted values must be contained.", containsInsertedValues);
      // String.format("%s.insert(%s)", testMap, newValue);

      testMap = tmpMap;
    }
  }

  @Property(trials = DEFAULT_TRIALS)
  public void containsKeyAfterInsert(@Size(max = 0) final CT emptyMap,
                             @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<T> inputValues) {

    CT testMap = emptyMap;

    for (T newValue : inputValues) {
      final CT tmpMap = (CT) testMap.__put(newValue, newValue);
      testMap = tmpMap;
    }

    boolean containsInsertedValues = inputValues.stream().allMatch(testMap::containsKey);

    assertTrue("Must contain all inserted values.", containsInsertedValues);
  }

  /**
   * Inserted element by element, starting from an empty map. Keeps track of all so far inserted
   * values and checks after each insertion if all inserted elements are contained (quadratic
   * operation).
   */
  @Property(trials = SQRT_TRIALS)
  public void stepwiseGetAfterInsert(@Size(max = 0) final CT emptyMap,
                                          @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<T> inputValues) {

    final HashSet<T> insertedValues = new HashSet<>(inputValues.size());
    CT testMap = emptyMap;

    for (T newValue : inputValues) {
      final CT tmpMap = (CT) testMap.__put(newValue, newValue);
      insertedValues.add(newValue);

      boolean containsInsertedValues =
              insertedValues.stream().allMatch(value -> Objects.equals(tmpMap.get(value), value));

      assertTrue("All so far inserted values must be contained.", containsInsertedValues);
      // String.format("%s.insert(%s)", testMap, newValue);

      testMap = tmpMap;
    }
  }

  @Property(trials = DEFAULT_TRIALS)
  public void getAfterInsert(@Size(max = 0) final CT emptyMap,
                                  @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<T> inputValues) {

    CT testMap = emptyMap;

    for (T newValue : inputValues) {
      final CT tmpMap = (CT) testMap.__put(newValue, newValue);
      testMap = tmpMap;
    }

    final CT constructedMap = testMap;
    boolean containsInsertedValues = inputValues.stream().allMatch(value -> Objects.equals(constructedMap.get(value), value));

    assertTrue("Must contain all inserted values.", containsInsertedValues);
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
