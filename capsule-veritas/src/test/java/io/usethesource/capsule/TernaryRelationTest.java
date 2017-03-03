/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import io.usethesource.capsule.api.Triple;
import io.usethesource.capsule.experimental.relation.TernaryTrieSetMultimap;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class TernaryRelationTest extends AbstractSetProperties<Triple<Integer, Integer, Integer>, TernaryTrieSetMultimap<Integer, Integer, Integer, Triple<Integer, Integer, Integer>>> {

  public TernaryRelationTest() {
    super(TernaryTrieSetMultimap.class);
  }

}
