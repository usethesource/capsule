/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import io.usethesource.capsule.core.PersistentTrieSet;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Static aggregation of set tests. Superseded by {@link RuntimeCodeGenerationTest}.
 */
@Deprecated
@RunWith(Suite.class)
@Suite.SuiteClasses({SetPropertiesTest.IntegerSetPropertiesTest.class,
    SetPropertiesTest.StringSetPropertiesTest.class})
public class SetPropertiesTest {

  @RunWith(JUnitQuickcheck.class)
  public static class IntegerSetPropertiesTest
      extends AbstractSetProperties<Integer, PersistentTrieSet<Integer>> { // TODO replace `Integer` with future `CollidableInteger` type

    public IntegerSetPropertiesTest() {
      super(PersistentTrieSet.class);
    }
  }

  @Ignore
  @RunWith(JUnitQuickcheck.class)
  public static class StringSetPropertiesTest
      extends AbstractSetProperties<String, PersistentTrieSet<String>> {

    public StringSetPropertiesTest() {
      super(PersistentTrieSet.class);
    }
  }

}
