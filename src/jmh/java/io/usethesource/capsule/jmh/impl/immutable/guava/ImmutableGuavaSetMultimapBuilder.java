/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.immutable.guava;

import com.google.common.collect.ImmutableSetMultimap;
import io.usethesource.capsule.jmh.api.JmhSetMultimap;
import io.usethesource.capsule.jmh.api.JmhValue;

final class ImmutableGuavaSetMultimapBuilder implements JmhSetMultimap.Builder {

  private ImmutableSetMultimap.Builder<JmhValue, JmhValue> mapContent;
  private JmhSetMultimap constructedMap;

  ImmutableGuavaSetMultimapBuilder() {
    mapContent = ImmutableSetMultimap.builder();
    constructedMap = null;
  }

  @Override
  public void insert(JmhValue key, JmhValue value) {
    checkMutation();
    mapContent.put(key, value);
  }

  private void checkMutation() {
    if (constructedMap != null) {
      throw new UnsupportedOperationException("Mutation of a finalized map is not supported.");
    }
  }

  @Override
  public JmhSetMultimap done() {
    if (constructedMap == null) {
      constructedMap = new ImmutableGuavaSetMultimap(mapContent.build());
    }

    return constructedMap;
  }

}
