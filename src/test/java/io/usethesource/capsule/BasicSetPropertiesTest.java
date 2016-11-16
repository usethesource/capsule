/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import io.usethesource.capsule.api.deprecated.ImmutableSet;
import io.usethesource.capsule.core.deprecated.TrieSet_5Bits;

@RunWith(JUnitQuickcheck.class)
public class BasicSetPropertiesTest {

  @Property
  public <K> void sizeAfterBatchInsertion(java.util.Set<K> input) {

    if (!input.contains(null)) {
      ImmutableSet<K> set = DefaultTrieSet.<K>of().__insertAll(input);

      assertEquals(input.size(), set.size());
    }
  }

  @Property
  public <K> void sizeAfterBatchInsertionCapsule(TrieSet_5Bits<K> input) {
    assertEquals(input.size(), new HashSet<K>(input).size());
  }

  @Property
  public <K> void sizeAfterBatchInsertionJavaUtilHashSet(java.util.Set<K> input) {
    assertEquals(input.size(), new HashSet<K>(input).size());
  }

}
