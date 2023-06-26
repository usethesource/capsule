/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.pcollections;

import io.usethesource.criterion.api.JmhValue;
import io.usethesource.criterion.impl.AbstractMapBuilder;
import org.pcollections.HashPMap;
import org.pcollections.HashTreePMap;

final class PcollectionsMapBuilder extends
    AbstractMapBuilder<JmhValue, HashPMap<JmhValue, JmhValue>> {

  PcollectionsMapBuilder() {
    super(HashTreePMap.empty(), map -> map::plus, PcollectionsMap::new);
  }

}
