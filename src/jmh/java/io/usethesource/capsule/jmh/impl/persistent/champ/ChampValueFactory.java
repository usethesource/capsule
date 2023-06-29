/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.champ;

import io.usethesource.capsule.jmh.api.JmhMap;
import io.usethesource.capsule.jmh.api.JmhSet;
import io.usethesource.capsule.jmh.api.JmhSetMultimap;
import io.usethesource.capsule.jmh.api.JmhValueFactory;

public class ChampValueFactory implements JmhValueFactory {

  private final boolean useBinaryRelation;

  public ChampValueFactory(boolean useBinaryRelation) {
    this.useBinaryRelation = useBinaryRelation;
  }

  @Override
  public JmhSet.Builder setBuilder() {
    return new ChampSetBuilder();
  }

  @Override
  public JmhMap.Builder mapBuilder() {
    return new ChampMapBuilder();
  }

  @Override
  public JmhSetMultimap.Builder setMultimapBuilder() {
    return useBinaryRelation ?
            new ChampBidirectionalSetMultimapBuilder() :
            new ChampSetMultimapBuilder();
  }

  @Override
  public String toString() {
    return "VF_CHAMP";
  }

}
