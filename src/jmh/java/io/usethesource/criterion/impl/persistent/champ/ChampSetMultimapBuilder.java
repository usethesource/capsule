/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.champ;

import io.usethesource.capsule.SetMultimap;
import io.usethesource.capsule.SetMultimapFactory;
import io.usethesource.criterion.api.JmhSetMultimap;
import io.usethesource.criterion.api.JmhValue;

final class ChampSetMultimapBuilder implements JmhSetMultimap.Builder {

  protected SetMultimap.Immutable<JmhValue, JmhValue> mapContent;
  protected JmhSetMultimap constructedMap;

  ChampSetMultimapBuilder(SetMultimapFactory setMultimapFactory) {
    mapContent = setMultimapFactory.of();
    constructedMap = null;
  }

  @Override
  public void insert(JmhValue key, JmhValue value) {
    checkMutation();
    mapContent = mapContent.__insert(key, value);
  }

  protected void checkMutation() {
    if (constructedMap != null) {
      throw new UnsupportedOperationException("Mutation of a finalized map is not supported.");
    }
  }

  @Override
  public JmhSetMultimap done() {
    if (constructedMap == null) {
      constructedMap = new ChampSetMultimap(mapContent);
    }

    return constructedMap;
  }

}
