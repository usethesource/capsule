/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HCHAMP;
import org.junit.Ignore;
import org.junit.runner.RunWith;

@Ignore("no generator available for HCHAMP")
@RunWith(JUnitQuickcheck.class)
public class SetMultimapPropertiesOfHchampTest extends
    AbstractSetMultimapProperties<Integer, Integer, TrieSetMultimap_HCHAMP<Integer, Integer>> {

  public SetMultimapPropertiesOfHchampTest() {
    super(TrieSetMultimap_HCHAMP.class);
  }

}
