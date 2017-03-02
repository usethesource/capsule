/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators.multimap;

import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HHAMT_Interlinked;

@SuppressWarnings({"rawtypes"})
public class SetMultimapGenerator_HHAMT_Interlinked<K>
    extends AbstractSetMultimapGenerator<TrieSetMultimap_HHAMT_Interlinked> {

  public SetMultimapGenerator_HHAMT_Interlinked() {
    super(TrieSetMultimap_HHAMT_Interlinked.class);
  }

}
