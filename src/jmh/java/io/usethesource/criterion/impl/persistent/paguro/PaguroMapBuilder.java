/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.paguro;

import io.usethesource.criterion.api.JmhValue;
import io.usethesource.criterion.impl.AbstractMapBuilder;
import org.organicdesign.fp.collections.PersistentHashMap;

final class PaguroMapBuilder extends
    AbstractMapBuilder<JmhValue, PersistentHashMap<JmhValue, JmhValue>> {

  PaguroMapBuilder() {
    super(PersistentHashMap.empty(), set -> set::assoc, PaguroMap::new);
  }

}