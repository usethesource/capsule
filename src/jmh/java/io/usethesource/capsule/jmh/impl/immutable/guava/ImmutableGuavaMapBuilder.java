/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.immutable.guava;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import io.usethesource.capsule.jmh.api.JmhMap;
import io.usethesource.capsule.jmh.api.JmhValue;

final class ImmutableGuavaMapBuilder implements JmhMap.Builder {

  // NOTE: ImmutableMap.Builder cannot handle duplicate arguments
  // protected ImmutableMap.Builder<JmhValue, JmhValue> mapContent;
  protected HashMap<JmhValue, JmhValue> mapContent;
  protected JmhMap constructedMap;

  ImmutableGuavaMapBuilder() {
    // mapContent = ImmutableMap.builder();
    mapContent = new HashMap<>();
    constructedMap = null;
  }

  @Override
  public void put(JmhValue key, JmhValue value) {
    checkMutation();
    mapContent.put(key, value);
  }

  @Override
  public void putAll(JmhMap map) {
    putAll(map.entryIterator());
  }

  @Override
  public void putAll(java.util.Map<JmhValue, JmhValue> map) {
    putAll(map.entrySet().iterator());
  }

  private void putAll(Iterator<Entry<JmhValue, JmhValue>> entryIterator) {
    checkMutation();

    while (entryIterator.hasNext()) {
      final Entry<JmhValue, JmhValue> entry = entryIterator.next();
      final JmhValue key = entry.getKey();
      final JmhValue value = entry.getValue();

      mapContent.put(key, value);
    }
  }

  protected void checkMutation() {
    if (constructedMap != null) {
      throw new UnsupportedOperationException("Mutation of a finalized map is not supported.");
    }
  }

  @Override
  public JmhMap done() {
    if (constructedMap == null) {
      // constructedMap = new ImmutableGuavaMap(mapContent.build());
      constructedMap = new ImmutableGuavaMap(ImmutableMap.copyOf(mapContent));
    }

    return constructedMap;
  }

}
