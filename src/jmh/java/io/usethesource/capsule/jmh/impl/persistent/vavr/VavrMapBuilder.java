/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.vavr;

import io.usethesource.capsule.jmh.api.JmhValue;
import io.usethesource.capsule.jmh.impl.AbstractMapBuilder;
import io.vavr.collection.HashMap;

final class VavrMapBuilder extends AbstractMapBuilder<JmhValue, HashMap<JmhValue, JmhValue>> {

  VavrMapBuilder() {
    super(HashMap.empty(), map -> map::put, VavrMap::new);
  }

}
