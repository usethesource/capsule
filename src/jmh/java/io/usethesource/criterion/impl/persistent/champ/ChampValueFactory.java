/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.champ;

import io.usethesource.capsule.MapFactory;
import io.usethesource.capsule.SetFactory;
import io.usethesource.capsule.SetMultimapFactory;
import io.usethesource.criterion.api.JmhMap;
import io.usethesource.criterion.api.JmhSet;
import io.usethesource.criterion.api.JmhSetMultimap;
import io.usethesource.criterion.api.JmhValueFactory;

public class ChampValueFactory implements JmhValueFactory {

  private final SetFactory setFactory;
  private final MapFactory mapFactory;
  private final SetMultimapFactory setMultimapFactory;

  public ChampValueFactory(final Class<?> targetSetClass, final Class<?> targetMapClass,
      final Class<?> targetSetMultimapClass) {
    setFactory = targetSetClass == null ? null : new SetFactory(targetSetClass);
    mapFactory = targetMapClass == null ? null : new MapFactory(targetMapClass);
    setMultimapFactory = targetSetMultimapClass == null ? null : new SetMultimapFactory(targetSetMultimapClass);
  }

  @Override
  public JmhSet.Builder setBuilder() {
    return new ChampSetBuilder(setFactory);
  }

  @Override
  public JmhMap.Builder mapBuilder() {
    return new ChampMapBuilder(mapFactory);
  }

  @Override
  public JmhSetMultimap.Builder setMultimapBuilder() {
    return new ChampSetMultimapBuilder(setMultimapFactory);
  }

  @Override
  public String toString() {
    return "VF_CHAMP";
  }

}
