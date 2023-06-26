/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.pcollections;

import io.usethesource.criterion.api.JmhValue;
import io.usethesource.criterion.impl.AbstractSetBuilder;
import org.pcollections.HashTreePMap;
import org.pcollections.MapPSet;

final class PcollectionsSetBuilder extends AbstractSetBuilder<JmhValue, MapPSet<JmhValue>> {

  PcollectionsSetBuilder() {
    super(MapPSet.from(HashTreePMap.empty()), set -> set::plus, PcollectionsSet::new);
  }

}
