/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.champ;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.jmh.api.JmhValue;
import io.usethesource.capsule.jmh.impl.AbstractMapBuilder;

final class ChampMapBuilder extends
    AbstractMapBuilder<JmhValue, Map.Immutable<JmhValue, JmhValue>> {

  ChampMapBuilder() {
    super(Map.Immutable.of(), map -> map::__put, ChampMap::new);
  }

}

//final class ChampMapBuilder extends
//    AbstractMapBuilder<JmhValue, Map.Transient<JmhValue, JmhValue>> {
//
//  ChampMapBuilder(MapFactory mapFactory) {
//    super(
//        mapFactory.transientOf(),
//        map -> (key, value) -> { map.__put(key, value); return map; },
//        map -> new ChampMap(map.freeze()));
//  }
//
//}
