/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.usethesource.capsule.jmh.api.JmhMap;
import io.usethesource.capsule.jmh.api.JmhValue;

public class AbstractMapBuilder<C extends JmhValue, CC> implements JmhMap.Builder {

  protected CC mapContent;
  protected JmhMap constructedMap;

  final Function<CC, JmhMap> functionWrap;
  final Function<CC, BiFunction<JmhValue, JmhValue, CC>> methodInsert;

  public AbstractMapBuilder(final CC empty,
      final Function<CC, BiFunction<JmhValue, JmhValue, CC>> methodInsert,
      final Function<CC, JmhMap> functionWrap) {

    this.mapContent = empty;
    this.constructedMap = null;

    this.methodInsert = methodInsert;
    this.functionWrap = functionWrap;
  }

  @Override
  public final void put(JmhValue key, JmhValue value) {
    checkMutation();
    mapContent = methodInsert.apply(mapContent).apply(key, value);
  }

  @Override
  public final void putAll(JmhMap map) {
    putAll(map.entryIterator());
  }

  @Override
  public final void putAll(java.util.Map<JmhValue, JmhValue> map) {
    putAll(map.entrySet().iterator());
  }

  private final void putAll(Iterator<Entry<JmhValue, JmhValue>> entryIterator) {
    checkMutation();

    while (entryIterator.hasNext()) {
      final Entry<JmhValue, JmhValue> entry = entryIterator.next();
      final JmhValue key = entry.getKey();
      final JmhValue value = entry.getValue();

      put(key, value);
    }
  }

  private void checkMutation() {
    if (constructedMap != null) {
      throw new UnsupportedOperationException("Mutation of a finalized map is not supported.");
    }
  }

  @Override
  public final JmhMap done() {
    if (constructedMap == null) {
      constructedMap = functionWrap.apply(mapContent);
    }

    return constructedMap;
  }

}
