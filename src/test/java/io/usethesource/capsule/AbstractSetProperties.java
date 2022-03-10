/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.Size;

/*
 * NOTE: use e.g. @When(seed = 3666151076704776907L) to fix seed for reproducing test run.
 */
public abstract class AbstractSetProperties<T, CT extends Set.Immutable<T>> {

  private final int DEFAULT_TRIALS = 1_000;
  private final int MORE_TRIALS = 10_000;
  private final int LESS_TRIALS = 100;
  private final int MAX_SIZE = 1_000;
  private final Class<?> type;

  public AbstractSetProperties(Class<?> type) {
    this.type = type;
  }

  @Property(trials = DEFAULT_TRIALS)
  public void convertToJavaSetAndCheckSize(CT input) {
    assertEquals(new HashSet<T>(input).size(), input.size());
  }

  @Property(trials = DEFAULT_TRIALS)
  public void convertToJavaSetAndCheckHashCode(CT input) {
    assertEquals(new HashSet<T>(input).hashCode(), input.hashCode());
  }

  @Property(trials = DEFAULT_TRIALS)
  public void convertToJavaSetAndCheckEquality(CT input) {
    assertEquals("input.equals(convertToJavaSet)", input, new HashSet<T>(input));
    assertEquals("convertToJavaSet.equals(input)", new HashSet<T>(input), input);
  }

  @Property(trials = DEFAULT_TRIALS)
  public void checkEquality(
      @Size(max = 0) final CT emptySet,
      @Size(min = 1, max = MAX_SIZE) final CT input) {

    final Set.Transient<T> builder = emptySet.asTransient();
    input.forEach(builder::__insert);
    final CT duplicate = (CT) builder.freeze();

    assertEquals("input.equals(duplicate)", input, duplicate);
  }

  @Property(trials = DEFAULT_TRIALS)
  public void streamYieldsSizeElements(CT input) {
    assertEquals(input.size(), input.stream().count());
  }

  @Property(trials = DEFAULT_TRIALS)
  public void checkSizeAfterInsertAll(
      @Size(max = 0) final CT emptySet,
      @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<T> inputValues) {
    CT testSet = (CT) emptySet.__insertAll(inputValues);
    assertEquals(inputValues.size(), testSet.size());
  }

  /**
   * Inserted element by element, starting from an empty set. Keeps track of all so far inserted
   * values and checks after each insertion if all inserted elements are contained (quadratic
   * operation).
   */
  @Property(trials = DEFAULT_TRIALS)
  public void stepwiseCheckSizeAfterInsertAll(@Size(max = 0) final CT emptySet,
      @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<T> inputValues) {

    int expectedSize = 0;
    int expectedHashCode = 0;

    final Set.Transient<T> builder = emptySet.asTransient();

    for (T newValue : inputValues) {
      builder.__insert(newValue);

      expectedSize += 1;
      expectedHashCode += newValue.hashCode();

      assertEquals(expectedSize, builder.size());
      assertEquals(expectedHashCode, builder.hashCode());
    }

    CT testSet = (CT) builder.freeze();
  }

  /**
   * Inserted element by element, starting from an empty set. Keeps track of all so far inserted
   * values and checks after each insertion if all inserted elements are contained (quadratic
   * operation).
   */
  @Property(trials = DEFAULT_TRIALS)
  public void stepwiseContainsAfterInsert(@Size(max = 0) final CT emptySet,
      @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<T> inputValues) {

    final HashSet<T> insertedValues = new HashSet<>(inputValues.size());
    CT testSet = emptySet;

    for (T newValue : inputValues) {
      final CT tmpSet = (CT) testSet.__insert(newValue);
      insertedValues.add(newValue);

      boolean containsInsertedValues =
          insertedValues.stream().allMatch(tmpSet::contains);

      assertTrue("All so far inserted values must be contained.", containsInsertedValues);
      // String.format("%s.insert(%s)", testSet, newValue);

      testSet = tmpSet;
    }
  }

  @Property(trials = DEFAULT_TRIALS)
  public void containsAfterInsert(@Size(max = 0) final CT emptySet,
      @Size(min = 1, max = MAX_SIZE) final java.util.HashSet<T> inputValues) {

    CT testSet = emptySet;

    for (T newValue : inputValues) {
      final CT tmpSet = (CT) testSet.__insert(newValue);
      testSet = tmpSet;
    }

    boolean containsInsertedValues = inputValues.stream().allMatch(testSet::contains);

    assertTrue("Must contain all inserted values.", containsInsertedValues);
  }

  @Property(trials = DEFAULT_TRIALS)
  public void notContainedAfterInsertRemove(CT input, T item) {
    assertFalse(input.__insert(item).__remove(item).contains(item));
  }

  @Property(trials = LESS_TRIALS)
  public void intersectIdentityReference(@Size(max = 0) final CT emptySet,
      @Size(max = MAX_SIZE) final CT inputShared) {
    assertEquals("intersect reference equal", inputShared, inputShared.intersect(inputShared));
  }

  @Property(trials = LESS_TRIALS)
  public void intersectIdentityStructural(@Size(max = 0) final CT emptySet,
      @Size(max = MAX_SIZE) final CT inputShared) {
    final Set.Transient<T> builder = emptySet.asTransient();
    inputShared.forEach(builder::__insert);

    assertEquals("intersect copy equal", inputShared, inputShared.intersect(builder.freeze()));
  }

  @Property(trials = LESS_TRIALS)
  public void intersect(
      @Size(max = MAX_SIZE) final CT inputOne,
      @Size(max = MAX_SIZE) final CT inputTwo,
      @Size(max = MAX_SIZE) final CT inputShared) {

    CT oneWithoutShared = (CT) inputOne.__removeAll(inputShared).__removeAll(inputTwo);
    CT twoWithoutShared = (CT) inputTwo.__removeAll(inputShared).__removeAll(inputOne);

    // CT intersectedWithoutShared = (CT) oneWithoutShared.intersect(twoWithoutShared);

    CT oneWithShared = (CT) inputOne.__insertAll(inputShared);
    CT twoWithShared = (CT) inputTwo.__insertAll(inputShared);

    CT intersectedWithShared = (CT) oneWithShared.intersect(twoWithShared);

    // CT intersectedMinusShared = (CT) intersectedWithShared.__removeAll(inputShared);
    // CT sharedMinusIntersected = (CT) inputShared.__removeAll(intersectedWithShared);

    assertTrue(inputShared.size() <= intersectedWithShared.size());
    assertTrue(inputShared.stream().allMatch(intersectedWithShared::contains));
    assertTrue(intersectedWithShared.stream().noneMatch(oneWithoutShared::contains));
    assertTrue(intersectedWithShared.stream().noneMatch(twoWithoutShared::contains));
  }

  @Property(trials = LESS_TRIALS)
  public void intersectMaintainsSizeAndHashCode(
      @Size(max = MAX_SIZE) final CT inputOne,
      @Size(max = MAX_SIZE) final CT inputTwo,
      @Size(max = MAX_SIZE) final CT inputShared) {

    CT oneWithShared = (CT) inputOne.__insertAll(inputShared);
    CT twoWithShared = (CT) inputTwo.__insertAll(inputShared);
    CT intersectedWithShared = (CT) oneWithShared.intersect(twoWithShared);

    convertToJavaSetAndCheckSize(intersectedWithShared);
    convertToJavaSetAndCheckHashCode(intersectedWithShared);
  }

  @Property(trials = LESS_TRIALS)
  public void intersectEqualToDefaultImplementation(
      @Size(max = MAX_SIZE) final CT inputOne,
      @Size(max = MAX_SIZE) final CT inputTwo,
      @Size(max = MAX_SIZE) final CT inputShared) {

    CT oneWithShared = (CT) inputOne.__insertAll(inputShared);
    CT twoWithShared = (CT) inputTwo.__insertAll(inputShared);

    CT intersectNative = (CT) oneWithShared.intersect(twoWithShared);
    CT intersectDefault = (CT) Set.Immutable.intersect(oneWithShared, twoWithShared);

    assertEquals(intersectDefault, intersectNative);
  }

  @Property(trials = LESS_TRIALS)
  public void intersectIdentityMostlyReference(
      @Size(max = MAX_SIZE) final CT input, T key) {

    final CT inputCopy;

    if (input.contains(key)) {
      inputCopy = (CT) input.__remove(key).__insert(key);
    } else {
      inputCopy = (CT) input.__insert(key).__remove(key);
    }

    CT intersectionL = (CT) input.intersect(inputCopy);
    CT intersectionR = (CT) inputCopy.intersect(input);

    assertEquals(input, intersectionL);
    assertEquals(input, intersectionR);
  }

  @Property(trials = LESS_TRIALS)
  public void unionIdentityReference(@Size(max = 0) final CT emptySet,
      @Size(max = MAX_SIZE) final CT inputShared) {
    assertEquals("union reference equal", inputShared, inputShared.union(inputShared));
  }

  @Property(trials = LESS_TRIALS)
  public void unionIdentityMostlyReference(@Size(max = 0) final CT emptySet,
      @Size(max = MAX_SIZE) final CT input, T key) {

    final CT inputCopy;

    if (input.contains(key)) {
      inputCopy = (CT) input.__remove(key).__insert(key);
    } else {
      inputCopy = (CT) input.__insert(key).__remove(key);
    }

    CT unionL = (CT) input.union(inputCopy);
    CT unionR = (CT) inputCopy.union(input);

    assertEquals(input, unionL);
    assertEquals(input, unionR);
  }

  @Property(trials = LESS_TRIALS)
  public void unionIdentityStructural(@Size(max = 0) final CT emptySet,
      @Size(max = MAX_SIZE) final CT inputShared) {
    final Set.Transient<T> builder = emptySet.asTransient();
    inputShared.forEach(builder::__insert);

    assertEquals("union copy equal", inputShared, inputShared.union(builder.freeze()));
  }

  @Property(trials = LESS_TRIALS)
  public void union(
      @Size(max = MAX_SIZE) final CT inputOne,
      @Size(max = MAX_SIZE) final CT inputTwo) {

    CT unioned = (CT) inputOne.intersect(inputTwo);

    assertTrue(unioned.stream().allMatch(inputOne::contains));
    assertTrue(unioned.stream().allMatch(inputTwo::contains));
  }

  @Property(trials = LESS_TRIALS)
  public void unionMaintainsSizeAndHashCode(
      @Size(max = MAX_SIZE) final CT inputOne,
      @Size(max = MAX_SIZE) final CT inputTwo) {

    CT unioned = (CT) inputOne.union(inputTwo);

    convertToJavaSetAndCheckSize(unioned);
    convertToJavaSetAndCheckHashCode(unioned);
  }

  @Property(trials = LESS_TRIALS)
  public void unionEqualToDefaultImplementation(
      @Size(max = MAX_SIZE) final CT inputOne,
      @Size(max = MAX_SIZE) final CT inputTwo) {

    CT unionNative = (CT) inputOne.union(inputTwo);
    CT unionDefault = (CT) Set.Immutable.union(inputOne, inputTwo);

    assertEquals(unionDefault, unionNative);
  }

  @Property(trials = LESS_TRIALS)
  public void subtract(
      @Size(max = MAX_SIZE) final CT inputOne,
      @Size(max = MAX_SIZE) final CT inputTwo) {

    CT subtracted = (CT) inputOne.subtract(inputTwo);

    assertTrue(subtracted.stream().allMatch(inputOne::contains));
    assertTrue(subtracted.stream().noneMatch(inputTwo::contains));
  }

  @Property(trials = LESS_TRIALS)
  public void subtractMaintainsSizeAndHashCode(
      @Size(max = MAX_SIZE) final CT inputOne,
      @Size(max = MAX_SIZE) final CT inputTwo) {

    CT subtracted = (CT) inputOne.subtract(inputTwo);

    convertToJavaSetAndCheckSize(subtracted);
    convertToJavaSetAndCheckHashCode(subtracted);
  }

  @Property(trials = LESS_TRIALS)
  public void subtractIdentityReference(@Size(max = 0) final CT emptySet,
      @Size(max = MAX_SIZE) final CT inputShared) {
    assertEquals("subtract reference equal", emptySet, inputShared.subtract(inputShared));
  }

  @Property(trials = LESS_TRIALS)
  public void subtractIdentityMostlyReference(@Size(max = 0) final CT emptySet,
      @Size(max = MAX_SIZE) final CT input, T key) {

    final CT inputCopy;

    if (input.contains(key)) {
      inputCopy = (CT) input.__remove(key).__insert(key);
    } else {
      inputCopy = (CT) input.__insert(key).__remove(key);
    }

    CT subtractionL = (CT) input.subtract(inputCopy);
    CT subtractionR = (CT) inputCopy.subtract(input);

    assertEquals(emptySet, subtractionL);
    assertEquals(emptySet, subtractionR);
  }

  @Property(trials = LESS_TRIALS)
  public void subtractIdentityStructural(@Size(max = 0) final CT emptySet,
      @Size(max = MAX_SIZE) final CT inputShared) {
    final Set.Transient<T> builder = emptySet.asTransient();
    inputShared.forEach(builder::__insert);

    assertEquals("subtract copy equal", emptySet, inputShared.subtract(builder.freeze()));
  }

  @Property(trials = LESS_TRIALS)
  public void subtract(
      @Size(max = MAX_SIZE) final CT inputOne,
      @Size(max = MAX_SIZE) final CT inputTwo,
      @Size(max = MAX_SIZE) final CT inputShared) {

    CT oneWithoutShared = (CT) inputOne.__removeAll(inputShared).__removeAll(inputTwo);
    CT twoWithoutShared = (CT) inputTwo.__removeAll(inputShared).__removeAll(inputOne);

    // CT subtractedWithoutShared = (CT) oneWithoutShared.subtracted(twoWithoutShared);

    CT oneWithShared = (CT) inputOne.__insertAll(inputShared);
    CT twoWithShared = (CT) inputTwo.__insertAll(inputShared);

    CT subtractedWithShared = (CT) oneWithShared.subtract(twoWithShared);

    // CT intersectedMinusShared = (CT) intersectedWithShared.__removeAll(inputShared);
    // CT sharedMinusIntersected = (CT) inputShared.__removeAll(intersectedWithShared);

    // assertTrue(inputShared.size() <= intersectedWithShared.size());
    assertTrue(inputShared.stream().noneMatch(subtractedWithShared::contains));
    assertTrue(subtractedWithShared.stream().allMatch(oneWithoutShared::contains));
    assertTrue(subtractedWithShared.stream().noneMatch(twoWithoutShared::contains));
  }

  @Property(trials = LESS_TRIALS)
  public void subtractMaintainsSizeAndHashCode(
      @Size(max = MAX_SIZE) final CT inputOne,
      @Size(max = MAX_SIZE) final CT inputTwo,
      @Size(max = MAX_SIZE) final CT inputShared) {

    CT oneWithShared = (CT) inputOne.__insertAll(inputShared);
    CT twoWithShared = (CT) inputTwo.__insertAll(inputShared);
    CT subtractedWithShared = (CT) oneWithShared.subtract(twoWithShared);

    convertToJavaSetAndCheckSize(subtractedWithShared);
    convertToJavaSetAndCheckHashCode(subtractedWithShared);
  }

  @Property(trials = LESS_TRIALS)
  public void subtractEqualToDefaultImplementation(
      @Size(max = MAX_SIZE) final CT inputOne,
      @Size(max = MAX_SIZE) final CT inputTwo,
      @Size(max = MAX_SIZE) final CT inputShared) {

    CT oneWithShared = (CT) inputOne.__insertAll(inputShared);
    CT twoWithShared = (CT) inputTwo.__insertAll(inputShared);

    CT subtractNative = (CT) oneWithShared.subtract(twoWithShared);
    CT subtractDefault = (CT) Set.Immutable.subtract(oneWithShared, twoWithShared);

    assertEquals(subtractDefault, subtractNative);
  }

  @Property(trials = DEFAULT_TRIALS)
  public void serializationRoundtrip(CT input) throws Exception {
    assertEquals(input, deserialize(serialize((java.io.Serializable) input), input.getClass()));
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
