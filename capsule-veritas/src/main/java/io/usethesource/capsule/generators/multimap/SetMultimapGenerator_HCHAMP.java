/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators.multimap;

import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HCHAMP;

@SuppressWarnings({"rawtypes"})
public class SetMultimapGenerator_HCHAMP<K>
    extends AbstractSetMultimapGenerator<TrieSetMultimap_HCHAMP> {

  public SetMultimapGenerator_HCHAMP() {
    super(TrieSetMultimap_HCHAMP.class);
  }

}
