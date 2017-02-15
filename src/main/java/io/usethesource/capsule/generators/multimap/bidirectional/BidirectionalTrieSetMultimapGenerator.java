/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators.multimap.bidirectional;

import io.usethesource.capsule.experimental.relation.BidirectionalTrieSetMultimap;
import io.usethesource.capsule.generators.multimap.AbstractSetMultimapGenerator;

@SuppressWarnings({"rawtypes"})
public class BidirectionalTrieSetMultimapGenerator
    extends AbstractSetMultimapGenerator<BidirectionalTrieSetMultimap> {

  public BidirectionalTrieSetMultimapGenerator() {
    super(BidirectionalTrieSetMultimap.class);
  }

}
