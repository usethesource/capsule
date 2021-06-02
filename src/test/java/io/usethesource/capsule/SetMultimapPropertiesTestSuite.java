/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 * <p>
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import io.usethesource.capsule.core.PersistentBidirectionalTrieSetMultimap;
import io.usethesource.capsule.core.PersistentTrieSetMultimap;
import io.usethesource.capsule.generators.CollidableInteger;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Suite.SuiteClasses({
    SetMultimapPropertiesTestSuite.PersistentBidirectionalTrieSetMultimapTest.class,
    SetMultimapPropertiesTestSuite.PersistentTrieSetMultimapTest.class})
public class SetMultimapPropertiesTestSuite {

  @RunWith(JUnitQuickcheck.class)
  public static class PersistentBidirectionalTrieSetMultimapTest extends
      AbstractBinaryRelationProperties<CollidableInteger, CollidableInteger, PersistentBidirectionalTrieSetMultimap<CollidableInteger, CollidableInteger>> {
  }

  @RunWith(JUnitQuickcheck.class)
  public static class PersistentTrieSetMultimapTest extends
      AbstractSetMultimapProperties<CollidableInteger, CollidableInteger, PersistentTrieSetMultimap<CollidableInteger, CollidableInteger>> {
  }

}
