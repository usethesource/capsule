/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators.relation;

import io.usethesource.capsule.experimental.relation.TernaryTrieSetMultimap;

@SuppressWarnings({"rawtypes"})
public class TernaryTrieSetMultimapGenerator
    extends AbstractTernaryRelationGenerator<TernaryTrieSetMultimap> {

  public TernaryTrieSetMultimapGenerator() {
    super(TernaryTrieSetMultimap.class);
  }

}
