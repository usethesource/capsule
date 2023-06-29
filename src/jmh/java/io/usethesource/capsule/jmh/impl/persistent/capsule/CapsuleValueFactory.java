/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.capsule;

import io.usethesource.capsule.jmh.api.JmhMap;
import io.usethesource.capsule.jmh.api.JmhSet;
import io.usethesource.capsule.jmh.api.JmhSetMultimap;
import io.usethesource.capsule.jmh.api.JmhValueFactory;

public class CapsuleValueFactory implements JmhValueFactory {

  private final boolean useBinaryRelation;

  public CapsuleValueFactory(boolean useBinaryRelation) {
    this.useBinaryRelation = useBinaryRelation;
  }

  @Override
  public JmhSet.Builder setBuilder() {
    return new CapsuleSetBuilder();
  }

  @Override
  public JmhMap.Builder mapBuilder() {
    return new CapsuleMapBuilder();
  }

  @Override
  public JmhSetMultimap.Builder setMultimapBuilder() {
    return useBinaryRelation ?
            new CapsuleBidirectionalSetMultimapBuilder() :
            new CapsuleSetMultimapBuilder();
  }

  @Override
  public String toString() {
    return "VF_CAPSULE";
  }

}
