/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.immutable.guava;

import com.google.common.collect.ImmutableSet;
import io.usethesource.criterion.api.JmhSet;
import io.usethesource.criterion.api.JmhValue;

final class ImmutableGuavaSetBuilder implements JmhSet.Builder {

  protected ImmutableSet.Builder<JmhValue> setContent;
  protected JmhSet constructedSet;

  ImmutableGuavaSetBuilder() {
    setContent = ImmutableSet.builder();
    constructedSet = null;
  }

  @Override
  public void insert(JmhValue... keys) {
    checkMutation();
    setContent.add(keys);
  }

  @Override
  public void insertAll(Iterable<? extends JmhValue> collection) {
    checkMutation();
    setContent.addAll(collection);
  }

  protected void checkMutation() {
    if (constructedSet != null) {
      throw new UnsupportedOperationException("Mutation of a finalized map is not supported.");
    }
  }

  @Override
  public JmhSet done() {
    if (constructedSet == null) {
      constructedSet = new ImmutableGuavaSet(setContent.build());
    }

    return constructedSet;
  }

}
