/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.immutable.guava;

import io.usethesource.capsule.jmh.api.JmhMap;
import io.usethesource.capsule.jmh.api.JmhSet;
import io.usethesource.capsule.jmh.api.JmhSetMultimap;
import io.usethesource.capsule.jmh.api.JmhValueFactory;

public class ImmutableGuavaValueFactory implements JmhValueFactory {

  public ImmutableGuavaValueFactory() {
  }

  @Override
  public JmhSet.Builder setBuilder() {
    return new ImmutableGuavaSetBuilder();
  }

  @Override
  public JmhMap.Builder mapBuilder() {
    return new ImmutableGuavaMapBuilder();
  }

  @Override
  public JmhSetMultimap.Builder setMultimapBuilder() {
    return new ImmutableGuavaSetMultimapBuilder();
  }

  @Override
  public String toString() {
    return "VF_GUAVA_IMMUTABLE";
  }

}
