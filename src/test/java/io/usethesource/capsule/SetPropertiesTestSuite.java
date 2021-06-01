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
import io.usethesource.capsule.generators.CollidableInteger;
import io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableSet;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Suite.SuiteClasses({
    SetPropertiesTestSuite.AbstractSpecialisedImmutableSetTest.class,
    SetPropertiesTestSuite.PersistentTrieSetTest.class})
public class SetPropertiesTestSuite {

  @RunWith(JUnitQuickcheck.class)
  public static class AbstractSpecialisedImmutableSetTest
      extends AbstractSetProperties<CollidableInteger, AbstractSpecialisedImmutableSet<CollidableInteger>> {

    public AbstractSpecialisedImmutableSetTest() {
      super(AbstractSpecialisedImmutableSet.class);
    }
  }

  @RunWith(JUnitQuickcheck.class)
  public static class PersistentTrieSetTest
      extends AbstractSetProperties<CollidableInteger, PersistentTrieSet<CollidableInteger>> {

    public PersistentTrieSetTest() {
      super(PersistentTrieSet.class);
    }
  }

}
