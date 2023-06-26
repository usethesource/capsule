/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

import io.usethesource.criterion.api.JmhSet;
import io.usethesource.criterion.api.JmhValue;

public class AbstractSetBuilder<C extends JmhValue, CC> implements JmhSet.Builder {

  protected CC setContent;
  protected JmhSet constructedSet;

  final Function<CC, JmhSet> functionWrap;
  final Function<CC, Function<JmhValue, CC>> methodInsert;

  public AbstractSetBuilder(final CC empty,
      final Function<CC, Function<JmhValue, CC>> methodInsert,
      final Function<CC, JmhSet> functionWrap) {

    this.setContent = empty;
    this.constructedSet = null;

    this.methodInsert = methodInsert;
    this.functionWrap = functionWrap;
  }

  @Override
  public final void insert(JmhValue... items) {
    checkMutation();
    insertAll(Arrays.asList(items));
  }

  @Override
  public void insertAll(Iterable<? extends JmhValue> collection) {
    checkMutation();

    final Iterator<? extends JmhValue> iterator = collection.iterator();

    while (iterator.hasNext()) {
      final JmhValue item = iterator.next();
      setContent = methodInsert.apply(setContent).apply(item);
    }
  }

  private void checkMutation() {
    if (constructedSet != null) {
      throw new UnsupportedOperationException("Mutation of a finalized builder is not supported.");
    }
  }

  @Override
  public final JmhSet done() {
    if (constructedSet == null) {
      constructedSet = functionWrap.apply(setContent);
    }

    return constructedSet;
  }

}
