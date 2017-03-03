/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators.relation;

import io.usethesource.capsule.core.PersistentBidirectionalTrieSetMultimap;
import io.usethesource.capsule.generators.multimap.AbstractSetMultimapGenerator;

public class BidirectionalTrieSetMultimapGenerator
    extends AbstractSetMultimapGenerator<PersistentBidirectionalTrieSetMultimap> {

  public BidirectionalTrieSetMultimapGenerator() {
    super(PersistentBidirectionalTrieSetMultimap.class);
  }

}
