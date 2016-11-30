/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import io.usethesource.capsule.core.deprecated.TrieSet_5Bits;

/**
 * Static aggregation of set test. Superseeded by {@link RuntimeCodeGenerationTest}.
 */
@Deprecated
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({SetPropertiesTest.IntegerSetPropertiesTest.class,
    SetPropertiesTest.StringSetPropertiesTest.class})
public class SetPropertiesTest {

  @RunWith(JUnitQuickcheck.class)
  public static class IntegerSetPropertiesTest
      extends AbstractSetProperties<Integer, TrieSet_5Bits<Integer>> {

    public IntegerSetPropertiesTest() {
      super(TrieSet_5Bits.class);
    }
  }

  @RunWith(JUnitQuickcheck.class)
  public static class StringSetPropertiesTest
      extends AbstractSetProperties<String, TrieSet_5Bits<String>> {

    public StringSetPropertiesTest() {
      super(TrieSet_5Bits.class);
    }
  }

}
