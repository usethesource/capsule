/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators.multimap;

import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_ChampBasedPrototype;

@SuppressWarnings({"rawtypes"})
public class SetMultimapGenerator_ChampBasedPrototype<K>
    extends AbstractSetMultimapGenerator<TrieSetMultimap_ChampBasedPrototype> {

  public SetMultimapGenerator_ChampBasedPrototype() {
    super(TrieSetMultimap_ChampBasedPrototype.class);
  }

}
