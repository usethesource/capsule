/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.champ;

import io.usethesource.capsule.MapFactory;
import io.usethesource.capsule.SetFactory;
import io.usethesource.capsule.SetMultimapFactory;
import io.usethesource.capsule.jmh.api.JmhMap;
import io.usethesource.capsule.jmh.api.JmhSet;
import io.usethesource.capsule.jmh.api.JmhSetMultimap;
import io.usethesource.capsule.jmh.api.JmhValueFactory;

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
