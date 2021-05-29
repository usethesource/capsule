/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import io.usethesource.capsule.core.PersistentBidirectionalTrieSetMultimap;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class SetMultimapPropertiesOfPersistentBidirectionalTrieSetMultimapTest extends
    AbstractBinaryRelationProperties<Integer, Integer, PersistentBidirectionalTrieSetMultimap<Integer, Integer>> { // TODO replace `Integer` with future `CollidableInteger` type

  public SetMultimapPropertiesOfPersistentBidirectionalTrieSetMultimapTest() {
    super(PersistentBidirectionalTrieSetMultimap.class);
  }

}
