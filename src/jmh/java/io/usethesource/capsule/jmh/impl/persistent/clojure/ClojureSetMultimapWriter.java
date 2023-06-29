/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.clojure;

import java.util.HashMap;
import java.util.Map;

import clojure.lang.ITransientMap;
import clojure.lang.ITransientSet;
import clojure.lang.PersistentHashMap;
import clojure.lang.PersistentHashSet;
import io.usethesource.capsule.jmh.api.JmhSetMultimap;
import io.usethesource.capsule.jmh.api.JmhValue;

class ClojureSetMultimapWriter implements JmhSetMultimap.Builder {

  Map<JmhValue, Object> builderMap = new HashMap<>();

  protected ClojureSetMultimapWriter() {
    super();
  }

  @Override
  public void insert(JmhValue key, JmhValue value) {
    Object singletonOrSet = builderMap.get(key);

    if (singletonOrSet == null) {
      builderMap.put(key, value);
    } else if (singletonOrSet instanceof ITransientSet) {
      ITransientSet set = (ITransientSet) singletonOrSet;
      set.conj(value);
    } else if (singletonOrSet.equals(value)) {
      // NOTHING
    } else {
      ITransientSet set =
          (ITransientSet) PersistentHashSet.create(singletonOrSet, value).asTransient();
      builderMap.put(key, set);
    }
  }

  @Override
  public JmhSetMultimap done() {
    ITransientMap xs = PersistentHashMap.EMPTY.asTransient();

    for (Map.Entry<JmhValue, Object> entry : builderMap.entrySet()) {
      Object key = entry.getKey();
      Object valueOrSet = entry.getValue();

      if (valueOrSet instanceof ITransientSet) {
        ITransientSet set = (ITransientSet) valueOrSet;
        xs.assoc(key, set.persistent());
      } else {
        xs.assoc(key, valueOrSet);
      }
    }

    return new ClojureSetMultimap(xs.persistent());
  }

}
